package com.example.shortnews.model

// 카테고리 선택 뉴스 response
data class NewsResponse (
    val news_list:MutableList<NewsItem>,
    val status:String
)

data class NewsItem (
    val news_id:String,
    val title:String,
    val views:Int,
    val like:Int,
    val dislike:Int,
    val reply:Int,
    var bookmark:Int,
    val imgs:String,
    val report: Int
)

// 뉴스 클릭 response
data class SelectNewsResponse(
    val news:SelectNewsItem,
    val status: String
)

data class SelectNewsItem (
    val title:String,
    val content:String,
    val imgs:MutableList<String>,
    val views:Int,
    var like:Int,
    var dislike:Int,
    var reply: Int,
    var type:Int,
    val bookmark:Int
)


// 추천 뉴스 response
data class RecommendResponse(
    val selectedList:MutableList<SelectedListItem>,
    val nonselectedList:MutableList<NonSelectedItem>,
    val subList:MutableList<SubListItem>,
    val status:String
)

data class SelectedListItem(
    override val news_id: String,
    override val title: String,
    override val views: Int,
    override val like: Int,
    override val dislike: Int,
    override val reply: Int,
    override val cate_id: String,
    override val imgs: String
) : RecommendItem

data class NonSelectedItem(
    override val news_id: String,
    override val title: String,
    override val views: Int,
    override val like: Int,
    override val dislike: Int,
    override val reply: Int,
    override val cate_id: String,
    override val imgs: String
) : RecommendItem

data class SubListItem(
    override val news_id: String,
    override val title: String,
    override val views: Int,
    override val like: Int,
    override val dislike: Int,
    override val reply: Int,
    override val cate_id: String,
    override val imgs: String
) : RecommendItem

interface  RecommendItem {
    val news_id: String
    val title: String
    val views: Int
    val like: Int
    val dislike: Int
    val reply: Int
    val cate_id: String
    val imgs: String
}


// 좋아요, 싫어요 request
data class LikeRequest (
    val like:Int,   // 1 = 좋아요, 0 = 선택 안함, -1 = 싫어요
    val news_id:String,
    val reply_id:String?
)

// 좋아요, 싫어요 response
data class LikeReponse (
    val status:String
)

// 뉴스 검색 request
data class SearchRequest (
    val st:Int,
    val ed:Int
)

// 뉴스 검색 reponse
data class SearchResponse (
    val news_list:MutableList<Search_NewsItem>,
    val status:String
)

data class Search_NewsItem (
    val news_id:String,
    val title:String,
    val cate_id:String,
    val imgs:String,
    val views:Int,
    val like:Int,
    val dislike:Int,
    val reply:Int,
    var bookmark:Int
)

// 뉴스 댓글
data class NewsReply(
    val reply_id: String,
    val id: String,
    val nickname: String,
    var content: String,
    var like: Int,
    var hate: Int,
    val news_id: String,
    var type: Int,
    val report: Int,
    val edited: Int,
    val lower: List<NewsReply>,
    var isOpenRereply: Boolean?
)

data class NewsReplyResponse (
    val status: String,
    val replies: List<NewsReply>,
)

data class NewsReplyRequest (
    val news_id: String,
    val reply: String,
    val upper_id: String?,
    val upper_user: String?
)

data class NewsReplyDeleteRequest (
    val news_id: String,
    val reply_id: String
)

data class NewsReplyUpdateRequest (
    val news_id: String,
    val reply_id: String,
    val text: String
)


// 북마크 Request
data class BookmarkRequest (
    val news_id:String,
    val type:Boolean
)

// 북마크 Response
data class BookmarkResponse (
    val status:String
)

// 신고
data class ReportRequest (
    val content: String,
    val type: String,
    val reply_id: String?,
    val news_id: String
)

data class ReportResponse (
    val report: Boolean,
    val status: String
)

// 출처 Response
data class SourceResponse (
    val link:String,
    val status:String
)

// 키워드 Response
data class KeywordResponse(
    val wordlist: List<Category>,
    val status: String
)

data class Category(
    val text: String,
    val cate: String,
    val value: Int
)