package com.example.shortnews.model

// 닉네임 변경
data class NicknameRequest (
    val nickname:String
)

data class NicknameResponse (
    val status:String,
    val message:String
)

// 전화번호 변경
data class CheckPhoneRequest (
    val phone:String
)

data class CheckPhoneResponse (
    val status:String,
    val code:String
)

data class PhoneRequest (
    val phone:String
)

data class PhoneResponse (
    val status:String
)

// 비밀번호 변경
data class PwRequest (
    val origin_pw:String,
    val new_pw:String
)

data class PwResponse (
    val status:String,
    val flag:Boolean
)

// 카테고리 변경
data class CategoryRequest (
    val cate:List<Boolean>
)

data class CategoryResponse (
    val status:String
)

// TTS 변경
data class TTSRequest (
    val model:String, // _male, _female
    val speed:String   // 0.75, 1, 1.25
)

data class TTSResponse (
    val status:String
)

// 북마크
data class BookmarkNewsResponse (
    val bookmark:MutableList<BookmarkNewsItem>,
    val status:String
)
data class BookmarkNewsItem (
    val news_id:String,
    val title:String,
    val views:Int,
    val like:Int,
    val dislike:Int,
    val reply:Int,
    var bookmark:Int,
    val cate_id:Int,
    val imgs:String
)

// 회원 탈퇴
data class DeleteMemberRequest (
    val content:String
)

data class DeleteMemberResponse (
    val status:String
)

// 나의 활동 (좋아요)
data class LikeActivityResponse (
    val newsLike:MutableList<ActivityNewsItem>,
    val status:String
)

// 나의 활동 (싫어요)
data class DisLikeActivityResponse (
    val newsDislike:MutableList<ActivityNewsItem>,
    val status:String
)

data class ActivityNewsItem (
    val news_id:String,
    val title:String,
    val views:Int,
    val like:Int,
    val dislike:Int,
    val reply:Int,
    var bookmark:Int,
    val cate_id:String,
    val imgs:String
)

// 나의 활동 (댓글)
data class ReplyActivityResponse (
    val result:MutableList<ActivityReplyItem>,
    val status:String
)

data class ActivityReplyItem (
    val news_id:String,
    val title:String,
    val imgs:String,
    val reply_id:String,
    var id:String,
    val content:String,
    val nickname:String,
    val low_rid:String,
    val low_uid:String,
    val low_content:String,
    val low_nickname:String
)