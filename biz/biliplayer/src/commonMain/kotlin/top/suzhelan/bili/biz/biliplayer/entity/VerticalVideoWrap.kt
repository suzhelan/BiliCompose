package top.suzhelan.bili.biz.biliplayer.entity

data class VerticalVideoWrap(
    val id : String,
    val detailsInfo: VideoInfo,
    val onlineCountText: String,
    val videoTags: List<VideoTag>,
    val isLike: Boolean,
    val isFavorite: Boolean,
    val coinQuotationCount: Int,
    val isLoading : Boolean,
) {
    companion object {
        fun empty(
            id : String = ""
        ) = VerticalVideoWrap(
            id = id,
            detailsInfo = VideoInfo(),
            onlineCountText = "",
            videoTags = emptyList(),
            isLike = false,
            isFavorite = false,
            coinQuotationCount = 0,
            isLoading = true
        )
    }
}
