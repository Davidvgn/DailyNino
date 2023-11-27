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
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

@RunWith(Parameterized::class)
class HomeViewModelCreatedAtParameterizedTest(
    private val givenVideoCreatedAt: ZonedDateTime,
    private val expectedVideoCreatedAtNativeText: NativeText,
    private val clock: Clock,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(
            name = "Given PagedVideosEntity.VideoEntity.createdAt [{0}s], " +
                "When observing ViewState, " +
                "Then HomeViewState.Video.createdAt should be [{1}]"
        )
        fun getValue() = listOf(
            arrayOf(
                ZonedDateTime.now(getDefaultClock()),
                NativeText.Plural(
                    id = R.plurals.delta_seconds,
                    number = 0,
                    args = listOf(0),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusSeconds(1),
                NativeText.Plural(
                    id = R.plurals.delta_seconds,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusSeconds(9),
                NativeText.Plural(
                    id = R.plurals.delta_seconds,
                    number = 9,
                    args = listOf(9),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusSeconds(10),
                NativeText.Plural(
                    id = R.plurals.delta_seconds,
                    number = 10,
                    args = listOf(10),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusSeconds(29),
                NativeText.Plural(
                    id = R.plurals.delta_seconds,
                    number = 29,
                    args = listOf(29),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusSeconds(59),
                NativeText.Plural(
                    id = R.plurals.delta_seconds,
                    number = 59,
                    args = listOf(59),
                ),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusMinutes(1),
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusMinutes(1).minusSeconds(1),
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusMinutes(1).minusSeconds(59),
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusMinutes(2),
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = 2,
                    args = listOf(2),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusMinutes(10),
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = 10,
                    args = listOf(10),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusMinutes(59),
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = 59,
                    args = listOf(59),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusMinutes(59).minusSeconds(59),
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = 59,
                    args = listOf(59),
                ),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusHours(1),
                NativeText.Plural(
                    id = R.plurals.delta_hours,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusHours(1).minusSeconds(1),
                NativeText.Plural(
                    id = R.plurals.delta_hours,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusHours(1).minusMinutes(1).minusSeconds(1),
                NativeText.Plural(
                    id = R.plurals.delta_hours,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusHours(1).minusMinutes(59).minusSeconds(59),
                NativeText.Plural(
                    id = R.plurals.delta_hours,
                    number = 1,
                    args = listOf(1),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusHours(2),
                NativeText.Plural(
                    id = R.plurals.delta_hours,
                    number = 2,
                    args = listOf(2),
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusHours(23).minusMinutes(59).minusSeconds(59),
                NativeText.Plural(
                    id = R.plurals.delta_hours,
                    number = 23,
                    args = listOf(23),
                ),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(1),
                NativeText.Resource(id = R.string.yesterday),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(1).minusHours(23).minusMinutes(59).minusSeconds(59),
                NativeText.Resource(id = R.string.yesterday),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(2),
                NativeText.Resource(id = R.string.the_day_before_yesterday),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(2).minusHours(23).minusMinutes(59).minusSeconds(59),
                NativeText.Resource(id = R.string.the_day_before_yesterday),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(3),
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = NativeText.Resource(R.string.thursday),
                ),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(4),
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = NativeText.Resource(R.string.wednesday),
                ),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(5),
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = NativeText.Resource(R.string.tuesday),
                ),
                getDefaultClock(),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(6),
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = NativeText.Resource(R.string.monday),
                ),
                getDefaultClock(),
            ),

            arrayOf(
                // Sunday 26/11/2023 - 22:56:23
                ZonedDateTime.now(getDefaultClock()),
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = NativeText.Resource(R.string.sunday),
                ),
                // Thursday 30/11/2023 - 22:56:23
                Clock.fixed(
                    Instant.ofEpochSecond(1701381383),
                    ZoneOffset.UTC,
                ),
            ),

            arrayOf(
                // Saturday 25/11/2023 - 22:56:23
                ZonedDateTime.now(getDefaultClock()).minusDays(1),
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = NativeText.Resource(R.string.saturday),
                ),
                // Thursday 30/11/2023 - 22:56:23
                Clock.fixed(
                    Instant.ofEpochSecond(1701381383),
                    ZoneOffset.UTC,
                ),
            ),

            arrayOf(
                // Friday 24/11/2023 - 22:56:23
                ZonedDateTime.now(getDefaultClock()).minusDays(2),
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = NativeText.Resource(R.string.friday),
                ),
                // Thursday 30/11/2023 - 22:56:23
                Clock.fixed(
                    Instant.ofEpochSecond(1701381383),
                    ZoneOffset.UTC,
                ),
            ),

            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(7),
                NativeText.Date(
                    temporal = ZonedDateTime.now(getDefaultClock()).minusDays(7),
                    temporalFormatterPatternStringRes = R.string.video_created_at_date_time_formatter,
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(28),
                NativeText.Date(
                    temporal = ZonedDateTime.now(getDefaultClock()).minusDays(28),
                    temporalFormatterPatternStringRes = R.string.video_created_at_date_time_formatter,
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(32),
                NativeText.Date(
                    temporal = ZonedDateTime.now(getDefaultClock()).minusDays(32),
                    temporalFormatterPatternStringRes = R.string.video_created_at_date_time_formatter,
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(364),
                NativeText.Date(
                    temporal = ZonedDateTime.now(getDefaultClock()).minusDays(364),
                    temporalFormatterPatternStringRes = R.string.video_created_at_date_time_formatter,
                ),
                getDefaultClock(),
            ),
            arrayOf(
                ZonedDateTime.now(getDefaultClock()).minusDays(367),
                NativeText.Date(
                    temporal = ZonedDateTime.now(getDefaultClock()).minusDays(367),
                    temporalFormatterPatternStringRes = R.string.video_created_at_date_time_formatter,
                ),
                getDefaultClock(),
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
            clock = clock,
        )
    }

    @Test
    fun test() = testCoroutineRule.runTest {
        // Given
        every { getPagedVideosUseCase.invoke() } returns flowOf(
            getDefaultPagedVideosEntitySuccess().copy(
                videos = listOf(
                    getDefaultVideoEntity().copy(createdAt = givenVideoCreatedAt)
                )
            )
        )

        // When
        homeViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertEquals(
                expectedVideoCreatedAtNativeText,
                (it.value!!.first() as HomeViewState.Video).createdAt
            )
        }
    }
}