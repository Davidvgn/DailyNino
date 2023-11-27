package fr.delcey.dailynino.domain.paging_video

import fr.delcey.dailynino.TestCoroutineRule
import fr.delcey.dailynino.domain.video.VideoRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ResetVideoPageUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val videoRepository: VideoRepository = mockk()
    private val pagingVideoRepository: PagingVideoRepository = mockk()

    private lateinit var resetVideoPageUseCase: ResetVideoPageUseCase

    @Before
    fun setUp() {
        coJustRun { videoRepository.resetPagedVideosCache() }
        justRun { pagingVideoRepository.resetPage() }

        resetVideoPageUseCase = ResetVideoPageUseCase(
            videoRepository,
            pagingVideoRepository,
        )
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        resetVideoPageUseCase.invoke()
        runCurrent()

        // Then
        coVerify(exactly = 1) {
            videoRepository.resetPagedVideosCache()
            pagingVideoRepository.resetPage()
        }
        confirmVerified(videoRepository, pagingVideoRepository)
    }
}