package org.example.yessoil

import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.yessoil.ui.FieldPoint
import org.example.yessoil.ui.SoilReadings

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
    @SerialName("device_id") val deviceId: String = "hardware-1",
    val moisture: Double,
    val ph: Double,
)

object SupabaseFieldPointRepository {
    suspend fun loadPoints(): List<FieldPoint> =
        SupabaseProvider.client.postgrest["field_points"]
            .select { order(column = "created_at", order = Order.DESCENDING) }
            .decodeList<FieldPointRecord>()
            .map { it.toFieldPoint() }

    suspend fun addPoint(name: String, readings: SoilReadings): FieldPoint =
        SupabaseProvider.client.postgrest["field_points"]
            .insert(FieldPointInsert(name = name, moisture = readings.moisture.toDouble(), ph = readings.ph.toDouble()))
            .decodeSingle<FieldPointRecord>()
            .toFieldPoint()

    private fun FieldPointRecord.toFieldPoint() = FieldPoint(
        id = id.toInt(),
        name = name,
        moisture = moisture?.toInt() ?: 0,
        ph = ph?.toFloat() ?: 0f,
        createdAt = createdAt,
    )
}
