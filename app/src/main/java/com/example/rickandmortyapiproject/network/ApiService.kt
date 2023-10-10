package com.example.rickandmortyapiproject.network

import com.example.rickandmortyapiproject.models.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

private const val BASE_URL = "https://rickandmortyapi.com/api/"

val gson: Gson = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int?,
        @Query("name") name: String?,
        @Query("status") status: String?,
        @Query("species") species: String?,
        @Query("type") type: String?,
        @Query("gender") gender: String?,
    ): CharactersApiResponse

    @GET
    suspend fun getCharacters(@Url url: String): CharactersApiResponse

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character

    @GET("character/{ids}")
    suspend fun getCharactersList(@Path("ids") ids: List<Int>): List<Character>

    @GET("location")
    suspend fun getLocations(
        @Query("page") page: Int? = null,
        @Query("name") name: String?,
        @Query("type") type: String?,
        @Query("dimension") dimension: String?,
    ): LocationsApiResponse

    @GET
    suspend fun getLocations(@Url url: String): LocationsApiResponse

    @GET("location/{id}")
    suspend fun getLocation(@Path("id") id: Int): Location

    @GET("episode")
    suspend fun getEpisodes(
        @Query("page") page: Int? = null,
        @Query("name") name: String?,
        @Query("episode") episode: String?,
    ): EpisodesApiResponse

    @GET
    suspend fun getEpisodes(@Url url: String): EpisodesApiResponse

    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): Episode

    @GET("episode/{ids}")
    suspend fun getEpisodesList(@Path("ids") ids: List<Int>): List<Episode>
}

object RNMApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}