package fr.delcey.dailynino.domain.video

import app.cash.turbine.test
import fr.delcey.dailynino.TestCoroutineRule
import fr.delcey.dailynino.domain.paging_video.PagingVideoRepository
import fr.delcey.dailynino.domain.video.model.PagedVideosEntity
import fr.delcey.dailynino.stubs.getDefaultPagedVideosEntitySuccess
import fr.delcey.dailynino.stubs.getDefaultVideoEntities
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class GetPagedVideosUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val videoRepository: VideoRepository = mockk()
    private val pagingVideoRepository: PagingVideoRepository = mockk()

    private val currentPageMutableStateFlow = MutableStateFlow(1)

    private lateinit var getPagedVideosUseCase: GetPagedVideosUseCase

    @Before
    fun setUp() {
        every { pagingVideoRepository.getCurrentPageFlow() } returns currentPageMutableStateFlow
        coEvery { videoRepository.getPagedVideos(1) } returns getDefaultPagedVideosEntitySuccess()

        getPagedVideosUseCase = GetPagedVideosUseCase(
            videoRepository,
            pagingVideoRepository,
        )
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        getPagedVideosUseCase.invoke().test {
            val result = awaitItem()

            // Then
            assertEquals(
                getDefaultPagedVideosEntitySuccess(),
                result
            )
            coVerify(exactly = 1) {
                pagingVideoRepository.getCurrentPageFlow()
                videoRepository.getPagedVideos(1)
            }
            confirmVerified(pagingVideoRepository, videoRepository)
        }
    }

    @Test
    fun `edge case - page 2`() = testCoroutineRule.runTest {
        // Given
        coEvery { videoRepository.getPagedVideos(2) } returns getDefaultPagedVideosEntitySuccess(indexOffset = 3)

        getPagedVideosUseCase.invoke().test {
            awaitItem() // Page 1

            // When
            currentPageMutableStateFlow.value = 2
            val result = awaitItem()

            // Then
            assertEquals(
                getDefaultPagedVideosEntitySuccess().copy(
                    videos = getDefaultVideoEntities()
                        + getDefaultVideoEntities(indexOffset = 3)
                ),
                result
            )
            coVerify {
                pagingVideoRepository.getCurrentPageFlow()
                videoRepository.getPagedVideos(1)
                videoRepository.getPagedVideos(2)
            }
            confirmVerified(pagingVideoRepository, videoRepository)
        }
    }

    @Test
    fun `edge case - success for first page, error for second page, success for retry`() = testCoroutineRule.runTest {
        // Given
        coEvery { videoRepository.getPagedVideos(2) } returns
            PagedVideosEntity.Failure andThen
            getDefaultPagedVideosEntitySuccess(indexOffset = 3)

        getPagedVideosUseCase.invoke().test {
            awaitItem() // Page 1

            currentPageMutableStateFlow.value = 2 // Page 2 fails at first

            // When
            advanceTimeBy(5.seconds)
            val result = awaitItem()

            // Then
            assertEquals(
                getDefaultPagedVideosEntitySuccess().copy(
                    videos = getDefaultVideoEntities()
                        + getDefaultVideoEntities(indexOffset = 3)
                ),
                result
            )
            coVerify(exactly = 1) {
                videoRepository.getPagedVideos(1)
            }
            coVerify(exactly = 2) {
                videoRepository.getPagedVideos(2)
            }
            confirmVerified(videoRepository)
        }
    }

    @Test
    fun `edge case - page 2 - success but no video`() = testCoroutineRule.runTest {
        // Given
        coEvery { videoRepository.getPagedVideos(2) } returns getDefaultPagedVideosEntitySuccess().copy(
            videos = null,
        )

        getPagedVideosUseCase.invoke().test {
            awaitItem() // Page 1

            // When
            currentPageMutableStateFlow.value = 2
            val result = awaitItem()

            // Then
            assertEquals(
                getDefaultPagedVideosEntitySuccess(),
                result
            )
            coVerify {
                pagingVideoRepository.getCurrentPageFlow()
                videoRepository.getPagedVideos(1)
                videoRepository.getPagedVideos(2)
            }
            confirmVerified(pagingVideoRepository, videoRepository)
        }
    }

    @Test
    fun `error case - if page 1, error is emitted downstream`() = testCoroutineRule.runTest {
        // Given
        coEvery { videoRepository.getPagedVideos(1) } returns PagedVideosEntity.Failure

        // When
        getPagedVideosUseCase.invoke().test {
            val result = awaitItem()

            // Then
            assertEquals(
                PagedVideosEntity.Failure,
                result
            )
            coVerify {
                pagingVideoRepository.getCurrentPageFlow()
                videoRepository.getPagedVideos(1)
            }
            confirmVerified(pagingVideoRepository, videoRepository)
        }
    }

    @Test
    fun `error case - must retry after 5 seconds`() = testCoroutineRule.runTest {
        // Given
        coEvery { videoRepository.getPagedVideos(1) } returns PagedVideosEntity.Failure

        getPagedVideosUseCase.invoke().test {
            awaitItem()

            // When
            advanceTimeBy(5.seconds)
            val result = awaitItem()

            // Then
            assertEquals(
                PagedVideosEntity.Failure,
                result
            )
            coVerify(exactly = 2) {
                videoRepository.getPagedVideos(1)
            }
            confirmVerified(videoRepository)
        }
    }

    @Test
    fun `edge case - reset paging - shouldn't contain previous data`() = testCoroutineRule.runTest {
        // Given
        coEvery { videoRepository.getPagedVideos(2) } returns getDefaultPagedVideosEntitySuccess(indexOffset = 3)

        getPagedVideosUseCase.invoke().test {
            awaitItem() // Page 1
            currentPageMutableStateFlow.value = 2
            awaitItem() // Page 2

            // When
            currentPageMutableStateFlow.value = 1
            val result = awaitItem()

            // Then
            assertEquals(
                getDefaultPagedVideosEntitySuccess(),
                result
            )
            coVerify(ordering = Ordering.SEQUENCE) {
                pagingVideoRepository.getCurrentPageFlow()
                videoRepository.getPagedVideos(1)
                videoRepository.getPagedVideos(2)
                videoRepository.getPagedVideos(1)
            }
            confirmVerified(pagingVideoRepository, videoRepository)
        }
    }
}