package fr.delcey.dailynino.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fr.delcey.dailynino.R
import fr.delcey.dailynino.TestCoroutineRule
import fr.delcey.dailynino.domain.paging_video.IncreaseCurrentVideoPageUseCase
import fr.delcey.dailynino.domain.paging_video.ResetVideoPageUseCase
import fr.delcey.dailynino.domain.video.GetPagedVideosUseCase
import fr.delcey.dailynino.observeForTesting
import fr.delcey.dailynino.stubs.getDefaultClock
import fr.delcey.dailynino.stubs.getDefaultPagedVideosEntitySuccess
import fr.delcey.dailynino.stubs.getDefaultVideoEntity
import fr.delcey.dailynino.ui.utils.NativeText
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@RunWith(Parameterized::class)
class HomeViewModelDurationParameterizedTest(
    private val givenVideoDurationInSeconds: Long?, // Can't use Duration because it's from Kotlin stdlib...
    private val expectedVideoDurationNativeText: NativeText?,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(
            name = "Given PagedVideosEntity.VideoEntity.duration [{0}s], " +
                "When observing ViewState, " +
                "Then HomeViewState.Video.duration should be [{1}]"
        )
        fun getValue() = listOf(
            arrayOf(null as Duration?, null),
            arrayOf(
                (0.seconds).inWholeSeconds,
                NativeText.Argument(
                    id = R.string.video_duration_seconds,
                    arg = 0L
                )
            ),
            arrayOf(
                (9.seconds).inWholeSeconds,
                NativeText.Argument(
                    id = R.string.video_duration_seconds,
                    arg = 9L
                )
            ),
            arrayOf(
                (10.seconds).inWholeSeconds,
                NativeText.Argument(
                    id = R.string.video_duration_seconds,
                    arg = 10L
                )
            ),
            arrayOf(
                (24.seconds).inWholeSeconds,
                NativeText.Argument(
                    id = R.string.video_duration_seconds,
                    arg = 24L
                )
            ),
            arrayOf(
                (59.seconds).inWholeSeconds,
                NativeText.Argument(
                    id = R.string.video_duration_seconds,
                    arg = 59L
                )
            ),

            arrayOf(
                (1.minutes).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_minutes,
                    args = listOf<Any>(1L, 0)
                )
            ),
            arrayOf(
                (1.minutes + 1.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_minutes,
                    args = listOf<Any>(1L, 1)
                )
            ),
            arrayOf(
                (1.minutes + 59.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_minutes,
                    args = listOf<Any>(1L, 59)
                )
            ),
            arrayOf(
                (4.minutes + 7.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_minutes,
                    args = listOf<Any>(4L, 7)
                )
            ),
            arrayOf(
                (27.minutes + 3.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_minutes,
                    args = listOf<Any>(27L, 3)
                )
            ),
            arrayOf(
                (43.minutes + 39.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_minutes,
                    args = listOf<Any>(43L, 39)
                )
            ),
            arrayOf(
                (59.minutes + 59.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_minutes,
                    args = listOf<Any>(59L, 59)
                )
            ),

            arrayOf(
                (1.hours).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(1L, 0, 0)
                )
            ),
            arrayOf(
                (1.hours + 1.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(1L, 0, 1)
                )
            ),
            arrayOf(
                (1.hours + 10.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(1L, 0, 10)
                )
            ),
            arrayOf(
                (1.hours + 49.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(1L, 0, 49)
                )
            ),
            arrayOf(
                (1.hours + 1.minutes + 1.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(1L, 1, 1)
                )
            ),
            arrayOf(
                (1.hours + 33.minutes + 4.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(1L, 33, 4)
                )
            ),
            arrayOf(
                (3.hours + 47.minutes + 56.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(3L, 47, 56)
                )
            ),
            arrayOf(
                (10.hours + 48.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(10L, 0, 48)
                )
            ),
            arrayOf(
                (1.hours + 59.minutes + 59.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(1L, 59, 59)
                )
            ),
            arrayOf(
                (10.hours + 10.minutes + 10.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(10L, 10, 10)
                )
            ),
            arrayOf(
                (28.hours + 28.minutes + 28.seconds).inWholeSeconds,
                NativeText.Arguments(
                    id = R.string.video_duration_hours,
                    args = listOf<Any>(28L, 28, 28)
                )
            ),
        )
    }


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

        homeViewModel = HomeViewModel(
            getPagedVideosUseCase = getPagedVideosUseCase,
            increaseCurrentVideoPageUseCase = increaseCurrentVideoPageUseCase,
            resetVideoPageUseCase = resetVideoPageUseCase,
            clock = getDefaultClock(),
        )
    }

    @Test
    fun test() = testCoroutineRule.runTest {
        // Given
        every { getPagedVideosUseCase.invoke() } returns flowOf(
            getDefaultPagedVideosEntitySuccess().copy(
                videos = listOf(
                    getDefaultVideoEntity().copy(duration = givenVideoDurationInSeconds?.seconds)
                )
            )
        )

        // When
        homeViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertEquals(
                expectedVideoDurationNativeText,
                (it.value!!.first() as HomeViewState.Video).duration
            )
        }
    }
}