package org.example.yessoil

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.yessoil.ui.FieldPoint
import org.example.yessoil.ui.SensorReadingUpdate
import org.example.yessoil.ui.SoilReadings

private const val SUPABASE_URL = "https://ilwlyywnmqpgxljcjgfm.supabase.co"
private const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_7j_H91COaBOICgf1jmTS2g_SUzEoeBz"
private const val DEVICE_ID = "farm01"

private val supabaseClient = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_PUBLISHABLE_KEY,
) {
    install(Postgrest)
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
                val latest = supabaseClient.postgrest["sensor_readings"]
                    .select {
                        eq("device_id", DEVICE_ID)
                        order(column = "recorded_at", order = Order.DESCENDING)
                        limit(count = 1)
                    }
                    .decodeList<SensorReadingRecord>()
                    .firstOrNull()

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

    suspend fun loadPoints(): List<FieldPoint> =
        supabaseClient.postgrest["field_points"]
            .select { order(column = "created_at", order = Order.DESCENDING) }
            .decodeList<FieldPointRecord>()
            .map { it.toFieldPoint() }

    suspend fun addPoint(name: String, readings: SoilReadings): FieldPoint =
        supabaseClient.postgrest["field_points"]
            .insert(
                FieldPointInsert(
                    name = name,
                    moisture = readings.moisture.toDouble(),
                    ph = readings.ph.toDouble(),
                ),
            )
            .decodeSingle<FieldPointRecord>()
            .toFieldPoint()

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
