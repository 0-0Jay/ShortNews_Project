package com.example.shortnews.model
// AlarmResponse
data class AlarmStatus(
    val status : String
)

data class AlarmChild(
    var time : String,
    val link : String,
    val status : Int,
    val type : Int,
    val target_id : String,
    val nickname : String
)
data class Alarms (
    val alarm : List<AlarmChild>,
    val status : String
)

data class AlarmSwitchRequest(
    val alarm : Int
)

data class AlarmBodyRequest(
    val time : String,
    val news_id : String
)


// 추천 뉴스 response
data class AlarmRealtimeData(
    val nickname : String,
    val type : Int,
    val news_id : String
)
