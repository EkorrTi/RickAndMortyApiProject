package com.example.rickandmortyapiproject.models

data class CharactersApiResponse(
    val info: ResponseInfo,
    val results: List<Character>,
)

data class LocationsApiResponse(
    val info: ResponseInfo,
    val results: List<Location>
)

data class EpisodesApiResponse(
    val info: ResponseInfo,
    val results: List<Episode>
)