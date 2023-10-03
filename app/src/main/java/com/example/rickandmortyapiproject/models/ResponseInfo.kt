package com.example.rickandmortyapiproject.models

data class ResponseInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?,
)
