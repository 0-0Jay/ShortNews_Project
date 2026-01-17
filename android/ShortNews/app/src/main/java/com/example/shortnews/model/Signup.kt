package com.example.shortnews.model

// 중복 체크 request
data class IdCheckRequest (
    val id:String
)

// 중복 체크 response
data class IdCheckResponse (
    val flag:Boolean,
    val status:String
)

// 전화번호 코드 전송 request
data class PhoneCheckRequest (
    val phone:String
)

// 전화번호 코드 전송 response
data class PhoneCheckResponse (
    val flag:Boolean,
    val code:String,
    val status:String
)

// 회원 가입 request
data class SubmitRequest (
    val id:String,
    val phone:String,
    val nickname:String,
    val pw:String
)

// 회원 가입 response
data class SubmitResponse (
    val flag:Boolean,
    val message:String,
    val status:String
)

// 카테고리 선택 request
data class SelectCategoryRequest (
    val id:String,
    val phone:String,
    val nickname:String,
    val pw:String,
    val platform:String,
    val cate:List<Boolean>
)

// 카테고리 선택 response
data class SelectCategoryResponse(
    val dto: SelectCategoryDto,
    val access_token: String,
    val refresh_token: String,
    val status: String
)

// 카테고리 선택 dto
data class SelectCategoryDto(
    val nickname: String,
    val phone: String,
    val id: String,
    val category: List<String>,
    val model: String,
    val speed: String,
    val platform: String
)