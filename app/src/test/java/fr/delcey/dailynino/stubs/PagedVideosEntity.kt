package fr.delcey.dailynino.stubs

import fr.delcey.dailynino.domain.video.model.PagedVideosEntity

fun getDefaultPagedVideosEntitySuccess(indexOffset: Int = 0) = PagedVideosEntity.Success(
    videos = getDefaultVideoEntities(indexOffset = indexOffset),
    currentPage = 1,
    hasMore = true,
)
