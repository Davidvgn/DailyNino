package fr.delcey.dailynino.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fr.delcey.dailynino.TestCoroutineRule
import fr.delcey.dailynino.domain.paging_video.IncreaseCurrentVideoPageUseCase
import fr.delcey.dailynino.domain.paging_video.ResetVideoPageUseCase
import fr.delcey.dailynino.domain.video.GetPagedVideosUseCase
import fr.delcey.dailynino.domain.video.model.PagedVideosEntity
import fr.delcey.dailynino.observeForTesting
import fr.delcey.dailynino.stubs.getDefaultClock
import fr.delcey.dailynino.stubs.getDefaultPagedVideosEntitySuccess
import fr.delcey.dailynino.ui.utils.Event
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getPagedVideosUseCase: GetPagedVideosUseCase = mockk()
    private val increaseCurrentVideoPageUseCase: IncreaseCurrentVideoPageUseCase = mockk()
    private val resetVideoPageUseCase: ResetVideoPageUseCase = mockk()

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        every { getPagedVideosUseCase.invoke() } returns flowOf(getDefaultPagedVideosEntitySuccess())
        justRun { increaseCurrentVideoPageUseCase.invoke() }
        coJustRun { resetVideoPageUseCase.invoke() }

        homeViewModel = HomeViewModel(
            getPagedVideosUseCase = getPagedVideosUseCase,
            increaseCurrentVideoPageUseCase = increaseCurrentVideoPageUseCase,
            resetVideoPageUseCase = resetVideoPageUseCase,
            clock = getDefaultClock(),
        )
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        homeViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertEquals(getDefaultHomeViewStateList(), it.value)
        }
    }

    @Test
    fun `edge case - user clicks on a video`() = testCoroutineRule.runTest {
        // Given
        homeViewModel.viewStateLiveData.observeForTesting(this) { liveData ->
            homeViewModel.viewEventLiveData.observeForTesting(this) { event ->

                // When
                (liveData.value!!.first() as HomeViewState.Video).onClicked.invoke()
                runCurrent()

                // Then
                assertEquals(
                    Event(HomeViewEvent.PlayVideo("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")),
                    event.value
                )
            }
        }
    }

    @Test
    fun `edge case - end of pagination`() = testCoroutineRule.runTest {
        // Given
        every { getPagedVideosUseCase.invoke() } returns flowOf(
            getDefaultPagedVideosEntitySuccess().copy(
                hasMore = false,
            )
        )

        // When
        homeViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertEquals(
                getDefaultHomeViewStateList() - HomeViewState.LoadingFooter,
                it.value
            )
        }
    }

    @Test
    fun `error case`() = testCoroutineRule.runTest {
        // Given
        every { getPagedVideosUseCase.invoke() } returns flowOf(PagedVideosEntity.Failure)

        // When
        homeViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertEquals(listOf(HomeViewState.Error), it.value)
        }
    }

    @Test
    fun `error case - null video list`() = testCoroutineRule.runTest {
        // Given
        every { getPagedVideosUseCase.invoke() } returns flowOf(
            getDefaultPagedVideosEntitySuccess().copy(videos = null)
        )

        // When
        homeViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertEquals(listOf(HomeViewState.Error), it.value)
        }
    }

    @Test
    fun `error case - empty video list`() = testCoroutineRule.runTest {
        // Given
        every { getPagedVideosUseCase.invoke() } returns flowOf(
            getDefaultPagedVideosEntitySuccess().copy(videos = emptyList())
        )

        // When
        homeViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertEquals(listOf(HomeViewState.Error), it.value)
        }
    }

    @Test
    fun `verify onFooterReached`() = testCoroutineRule.runTest {
        // When
        homeViewModel.onFooterReached()

        // Then
        verify(exactly = 1) {
            increaseCurrentVideoPageUseCase.invoke()
        }
    }

    @Test
    fun `verify onPullToRefresh`() = testCoroutineRule.runTest {
        // When
        homeViewModel.onPullToRefresh()
        runCurrent()

        // Then
        coVerify(exactly = 1) {
            resetVideoPageUseCase.invoke()
        }
    }
}