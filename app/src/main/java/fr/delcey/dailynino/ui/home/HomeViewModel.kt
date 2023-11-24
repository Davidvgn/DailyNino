package fr.delcey.dailynino.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.dailynino.R
import fr.delcey.dailynino.domain.video.GetVideosUseCase
import fr.delcey.dailynino.domain.video.model.VideoEntity
import fr.delcey.dailynino.ui.utils.NativeText
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
    private val getVideosUseCase: GetVideosUseCase,
) : ViewModel() {

    val viewStateLiveData: LiveData<List<HomeViewState>> = liveData {
        emit(
            map(getVideosUseCase.invoke())
        )
    }

    private fun map(videos: List<VideoEntity>?): List<HomeViewState> =
        if (videos.isNullOrEmpty()) {
            listOf(HomeViewState.Error)
        } else {
            videos.map { video ->
                HomeViewState.Video(
                    id = video.id,
                    title = NativeText.Simple(video.title),
                    description = NativeText.Simple(video.description),
                    duration = mapDuration(video.duration),
                    thumbnailUrl = video.thumbnailUrl,
                    createdAt = mapCreatedAt(video.createdAt),
                )
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
        val delta = ChronoUnit.SECONDS.between(videoCreatedAt, ZonedDateTime.now()).seconds

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