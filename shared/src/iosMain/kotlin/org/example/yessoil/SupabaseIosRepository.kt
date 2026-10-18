package org.example.yessoil

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import org.example.yessoil.ui.FieldPoint
import org.example.yessoil.ui.SensorReadingUpdate
import org.example.yessoil.ui.SoilReadings

private const val SUPABASE_URL = "https://ilwlyywnmqpgxljcjgfm.supabase.co"
private const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_7j_H91COaBOICgf1jmTS2g_SUzEoeBz"
private const val DEVICE_ID = "farm01"

private val json = Json { ignoreUnknownKeys = true }

private val httpClient = HttpClient()

private fun HttpRequestBuilder.supabaseHeaders() {
    headers {
        append(HttpHeaders.Authorization, "Bearer $SUPABASE_PUBLISHABLE_KEY")
        append("apikey", SUPABASE_PUBLISHABLE_KEY)
        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        append("Prefer", "return=representation")
    }
}

@Serializable
private data class SensorReadingRecord(
    val ph: Double,
    val ec: Double,
    val moisture: Double,
    val temperature: Double,
    val nitrogen: Double,
    val phosphorus: Double,
    val potassium: Double,
    @SerialName("recorded_at") val recordedAt: String,
)

@Serializable
private data class FieldPointRecord(
    val id: Long,
    val name: String,
    val moisture: Double? = null,
    val ph: Double? = null,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
private data class FieldPointInsert(
    val name: String,
    @SerialName("device_id") val deviceId: String = DEVICE_ID,
    val moisture: Double,
    val ph: Double,
)

/** iOS implementation for the same Supabase tables used by Android. */
object SupabaseIosRepository {
    fun readings(): Flow<SensorReadingUpdate> = flow {
        var loadedInitialReading = false
        var lastRecordedAt: String? = null
        while (true) {
            try {
                val url = "$SUPABASE_URL/rest/v1/sensor_readings?device_id=eq.$DEVICE_ID&order=recorded_at.desc&limit=1"
                val response: HttpResponse = httpClient.get(url) { supabaseHeaders() }
                val body = response.bodyAsText()
                val records = json.decodeFromString<List<SensorReadingRecord>>(body)
                val latest = records.firstOrNull()

                latest?.let { record ->
                    if (!loadedInitialReading || record.recordedAt != lastRecordedAt) {
                        emit(
                            SensorReadingUpdate(
                                readings = record.toSoilReadings(),
                                isNewFromDevice = loadedInitialReading,
                            ),
                        )
                        loadedInitialReading = true
                        lastRecordedAt = record.recordedAt
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // Keep polling after temporary Wi-Fi or server errors.
            }
            delay(2_000)
        }
    }

    suspend fun loadPoints(): List<FieldPoint> {
        val url = "$SUPABASE_URL/rest/v1/field_points?order=created_at.desc"
        val response: HttpResponse = httpClient.get(url) { supabaseHeaders() }
        val body = response.bodyAsText()
        val records = json.decodeFromString<List<FieldPointRecord>>(body)
        return records.map { it.toFieldPoint() }
    }

    suspend fun addPoint(name: String, readings: SoilReadings): FieldPoint {
        val insert = FieldPointInsert(
            name = name,
            moisture = readings.moisture.toDouble(),
            ph = readings.ph.toDouble(),
        )
        val url = "$SUPABASE_URL/rest/v1/field_points"
        val response: HttpResponse = httpClient.post(url) {
            supabaseHeaders()
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(insert))
        }
        val body = response.bodyAsText()
        val record = json.decodeFromString<FieldPointRecord>(body)
        return record.toFieldPoint()
    }

    private fun SensorReadingRecord.toSoilReadings() = SoilReadings(
        ph = ph.toFloat(),
        ec = ec.toFloat(),
        moisture = moisture.toFloat(),
        temperature = temperature.toFloat(),
        nitrogen = nitrogen.toInt(),
        phosphorus = phosphorus.toInt(),
        potassium = potassium.toInt(),
    )

    private fun FieldPointRecord.toFieldPoint() = FieldPoint(
        id = id.toInt(),
        name = name,
        moisture = moisture?.toInt() ?: 0,
        ph = ph?.toFloat() ?: 0f,
        createdAt = createdAt,
    )
}