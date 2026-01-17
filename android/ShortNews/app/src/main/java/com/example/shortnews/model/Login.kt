package com.example.shortnews.model

// 아이디 체크 request
data class LoginIdCheckRequest (
    val id:String
)

// 아이디 체크 response
data class LoginIdCheckResponse (
    val id:String,
    val flag:Boolean,
    val status:String
)

//----------------------------------------

// 로그인 체크 request
data class LoginRequest (
    val id:String,
    val pw:String
)

// 로그인 체크 response
data class LoginResponse (
    val dto:LoginCheckDto,
    val access_token:String,
    val refresh_token:String,
    val status:String
)

// 로그인 체크 dto
data class LoginCheckDto (
    val nickname:String,
    val phone:String,
    val id:String,
    val category:List<String>,
    val model:String,
    val speed:String,
    val platform:String,
    val alarm : Int
)

// 로그인 체크 alarm
data class LoginCheckAlarm (
    val time:String,
    val link:String,
    val status:String,
    val type:String,
    val target_id:String
)

//----------------------------------------

// 비밀번호 찾기 request
data class FindPwRequest (
    val id:String,
    val phone:String
)

// 비밀번호 찾기 response
data class FindPwResponse (
    val flag:Boolean,
    val status:String,
    val code:String
)

//----------------------------------------

// 아이디 찾기 request
data class FindIdRequest (
    val phone:String
)

// 아이디 찾기 response
data class FindIdResponse (
    val flag:Boolean,
    val status:String,
    val code:String,
    val id:String
)

//----------------------------------------

// 비밀번호 변경 request
data class UpdatePwRequest (
    val id:String,
    val pw:String
)

// 비밀번호 변경 response
data class UpdatePwResponse (
    val status:String
)

//----------------------------------------

// 네이버 로그인 request
data class NaverLoginRequest(
    val id:String,
    val email:String
)

// 네이버 로그인 response
data class NaverLoginResponse (
    val dto:LoginCheckDto,
    val id:String,
    val phone:String,
    val nickname:String,
    val access_token:String,
    val refresh_token:String,
    val status:String,
    val message:Boolean
)

//----------------------------------------

// 카카오 로그인 request
data class KakaoLoginRequest (
    val id:String,
    val email:String
)

// 카카오 로그인 response
data class KakaoLoginResponse (
    val dto:LoginCheckDto,
    val id:String,
    val phone:String,
    val nickname:String,
    val access_token:String,
    val refresh_token:String,
    val status:String,
    val message:Boolean
)

//----------------------------------------

















