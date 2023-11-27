package fr.delcey.dailynino.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.dailynino.R
import fr.delcey.dailynino.domain.paging_video.IncreaseCurrentVideoPageUseCase
import fr.delcey.dailynino.domain.paging_video.ResetVideoPageUseCase
import fr.delcey.dailynino.domain.video.GetPagedVideosUseCase
import fr.delcey.dailynino.domain.video.model.PagedVideosEntity
import fr.delcey.dailynino.ui.utils.EquatableCallback
import fr.delcey.dailynino.ui.utils.Event
import fr.delcey.dailynino.ui.utils.NativeText
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPagedVideosUseCase: GetPagedVideosUseCase,
    private val increaseCurrentVideoPageUseCase: IncreaseCurrentVideoPageUseCase,
    private val resetVideoPageUseCase: ResetVideoPageUseCase,
    private val clock: Clock,
) : ViewModel() {

    // The wrapped String (videoId or whatever else) could be used if needed
    private val clickedVideoIdMutableSharedFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val viewStateLiveData: LiveData<List<HomeViewState>> = liveData {
        getPagedVideosUseCase.invoke().collect { pagedVideos ->
            emit(
                map(pagedVideos)
            )
        }
    }

    val viewEventLiveData: LiveData<Event<HomeViewEvent>> = liveData {
        clickedVideoIdMutableSharedFlow.collect {
            emit(Event(HomeViewEvent.PlayVideo("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")))
        }
    }

    fun onFooterReached() {
        increaseCurrentVideoPageUseCase.invoke()
    }

    fun onPullToRefresh() {
        viewModelScope.launch {
            resetVideoPageUseCase.invoke()
        }
    }

    private fun map(pagedVideosEntity: PagedVideosEntity): List<HomeViewState> = when (pagedVideosEntity) {
        is PagedVideosEntity.Failure -> listOf(HomeViewState.Error)
        is PagedVideosEntity.Success -> if (pagedVideosEntity.videos.isNullOrEmpty()) {
            listOf(HomeViewState.Error)
        } else {
            buildList {
                pagedVideosEntity.videos.forEach { video ->
                    add(
                        HomeViewState.Video(
                            id = video.id,
                            title = NativeText.Simple(video.title),
                            description = NativeText.Html(video.description),
                            duration = mapDuration(video.duration),
                            thumbnailUrl = video.thumbnailUrl,
                            createdAt = mapCreatedAt(video.createdAt),
                            onClicked = EquatableCallback {
                                clickedVideoIdMutableSharedFlow.tryEmit(video.id)
                            },
                        )
                    )
                }

                if (pagedVideosEntity.hasMore) {
                    add(HomeViewState.LoadingFooter)
                }
            }.toList()
        }
    }

    private fun mapDuration(duration: Duration?): NativeText? = when {
        duration == null -> null
        duration < 1.minutes -> NativeText.Argument(R.string.video_duration_seconds, duration.inWholeSeconds)
        duration < 1.hours -> duration.toComponents { minutes, seconds, _ ->
            NativeText.Arguments(
                id = R.string.video_duration_minutes,
                args = listOf(minutes, seconds)
            )
        }
        else -> duration.toComponents { hours, minutes, seconds, _ ->
            NativeText.Arguments(
                id = R.string.video_duration_hours,
                args = listOf(hours, minutes, seconds)
            )
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    private fun mapCreatedAt(videoCreatedAt: ZonedDateTime): NativeText {
        val delta = ChronoUnit.SECONDS.between(videoCreatedAt, ZonedDateTime.now(clock)).seconds

        return when {
            delta < 1.minutes -> {
                val seconds = delta.inWholeSeconds.toInt()
                NativeText.Plural(
                    id = R.plurals.delta_seconds,
                    number = seconds,
                    args = listOf(seconds),
                )
            }
            delta < 1.hours -> {
                val minutes = delta.inWholeMinutes.toInt()
                NativeText.Plural(
                    id = R.plurals.delta_minutes,
                    number = minutes,
                    args = listOf(minutes),
                )
            }
            delta < 1.days -> {
                val hours = delta.inWholeHours.toInt()
                NativeText.Plural(
                    id = R.plurals.delta_hours,
                    number = hours,
                    args = listOf(hours),
                )
            }
            delta < 2.days -> NativeText.Resource(id = R.string.yesterday)
            delta < 3.days -> NativeText.Resource(id = R.string.the_day_before_yesterday)
            delta < 7.days -> {
                NativeText.Argument(
                    id = R.string.last_weekday,
                    arg = when (videoCreatedAt.dayOfWeek) {
                        DayOfWeek.MONDAY -> NativeText.Resource(R.string.monday)
                        DayOfWeek.TUESDAY -> NativeText.Resource(R.string.tuesday)
                        DayOfWeek.WEDNESDAY -> NativeText.Resource(R.string.wednesday)
                        DayOfWeek.THURSDAY -> NativeText.Resource(R.string.thursday)
                        DayOfWeek.FRIDAY -> NativeText.Resource(R.string.friday)
                        DayOfWeek.SATURDAY -> NativeText.Resource(R.string.saturday)
                        DayOfWeek.SUNDAY -> NativeText.Resource(R.string.sunday)
                    }
                )
            }
            else -> NativeText.Date(
                temporal = videoCreatedAt,
                temporalFormatterPatternStringRes = R.string.video_created_at_date_time_formatter,
            )
        }
    }
}