package org.example.yessoil

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.yessoil.ui.SensorReadingUpdate
import org.example.yessoil.ui.SoilReadings

private const val DEVICE_ID = "farm01"
private const val POLL_INTERVAL_MS = 2_000L

@Serializable
private data class SensorReadingRecord(
    val ph: Double,
    val ec: Double,
    val moisture: Double,
    val temperature: Double,
    val nitrogen: Double,
    val phosphorus: Double,
    val potassium: Double,
    @SerialName("recorded_at") val recordedAt: String = "",
)

object SupabaseSensorRepository {
    fun readings(): Flow<SensorReadingUpdate> = flow {
        var loadedInitialReading = false
        var lastRecordedAt: String? = null

        while (true) {
            try {
                val latest = SupabaseProvider.client.postgrest["sensor_readings"]
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
                // The ViewModel marks the device offline when no new row arrives.
            }
            delay(POLL_INTERVAL_MS)
        }
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
}
