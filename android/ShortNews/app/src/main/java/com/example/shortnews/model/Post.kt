package com.example.shortnews.model

data class Post (
    val title : String,
    val content : String,
    val imgs : String,
    val views : Int,
    val like : Int,
    val dislike : Int,
    val type : Int,
    val bookmark : Boolean
)