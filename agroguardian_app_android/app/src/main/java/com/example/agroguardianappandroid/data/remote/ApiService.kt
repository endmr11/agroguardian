package com.example.agroguardianappandroid.data.remote

import com.example.agroguardianappandroid.data.models.Command
import com.example.agroguardianappandroid.data.models.CommandData
import com.example.agroguardianappandroid.data.models.TelemetryData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/devices/{deviceName}/telemetry")
    suspend fun fetchTelemetry(@Path("deviceName") deviceName: String): List<TelemetryData>

    @GET("api/devices/{deviceName}/commands")
    suspend fun fetchLastCommands(@Path("deviceName") deviceName: String): List<CommandData>

    @POST("api/devices/{deviceName}/commands")
    suspend fun sendCommand(
        @Path("deviceName") deviceName: String,
        @Body command: Command
    )

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}