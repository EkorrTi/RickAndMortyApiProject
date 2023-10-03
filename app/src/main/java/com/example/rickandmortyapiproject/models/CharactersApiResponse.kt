package com.example.rickandmortyapiproject.models

data class CharactersApiResponse(
    val info: CharactersResponseInfo,
    val results: List<Character>,
)

data class CharactersResponseInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?,
)
