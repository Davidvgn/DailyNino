package fr.delcey.dailynino.ui.home

import fr.delcey.dailynino.R
import fr.delcey.dailynino.stubs.getDefaultVideoEntity
import fr.delcey.dailynino.ui.utils.EquatableCallback
import fr.delcey.dailynino.ui.utils.NativeText

fun getDefaultHomeViewStateList(): List<HomeViewState> = List(3) { index ->
    getDefaultHomeViewState(index)
} + HomeViewState.LoadingFooter

fun getDefaultHomeViewState(index: Int) = HomeViewState.Video(
    id = getDefaultVideoEntity(index).id,
    title = NativeText.Simple(getDefaultVideoEntity(index).title),
    description = NativeText.Html(getDefaultVideoEntity(index).description),
    duration = NativeText.Argument(R.string.video_duration_seconds, index.toLong()),
    thumbnailUrl = getDefaultVideoEntity(index).thumbnailUrl,
    createdAt = NativeText.Plural(
        id = R.plurals.delta_seconds,
        number = index,
        args = listOf(index),
    ),
    onClicked = EquatableCallback { },
)