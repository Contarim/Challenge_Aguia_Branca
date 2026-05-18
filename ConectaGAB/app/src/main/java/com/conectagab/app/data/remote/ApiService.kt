package com.conectagab.app.data.remote

import com.conectagab.app.data.model.*
import retrofit2.http.*

interface ApiService {

    // ── Users ────────────────────────────────────────────────────────────────
    @GET("users")
    suspend fun getUsers(): List<User>

    // ── Guidelines ───────────────────────────────────────────────────────────
    @GET("guidelines")
    suspend fun getGuidelines(): List<StrategicGuideline>

    @POST("guidelines")
    suspend fun createGuideline(@Body guideline: StrategicGuideline): StrategicGuideline

    @PUT("guidelines/{id}")
    suspend fun updateGuideline(
        @Path("id") id: String,
        @Body guideline: StrategicGuideline
    ): StrategicGuideline

    @DELETE("guidelines/{id}")
    suspend fun deleteGuideline(@Path("id") id: String): StrategicGuideline

    // ── Ideas ────────────────────────────────────────────────────────────────
    @GET("ideas")
    suspend fun getIdeas(): List<Idea>

    @POST("ideas")
    suspend fun createIdea(@Body idea: Idea): Idea

    @PUT("ideas/{id}")
    suspend fun updateIdea(@Path("id") id: String, @Body idea: Idea): Idea

    // ── Projects ─────────────────────────────────────────────────────────────
    @GET("projects")
    suspend fun getProjects(): List<Project>

    @POST("projects")
    suspend fun createProject(@Body project: Project): Project

    @PUT("projects/{id}")
    suspend fun updateProject(@Path("id") id: String, @Body project: Project): Project
}
