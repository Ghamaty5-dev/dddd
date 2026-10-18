package org.example.yessoil.ui

data class SoilReadings(
    val ph: Float = 0f, val ec: Float = 0f, val moisture: Float = 0f,
    val temperature: Float = 0f, val nitrogen: Int = 0,
    val phosphorus: Int = 0, val potassium: Int = 0,
)

/** A reading loaded from Supabase. [isNewFromDevice] is true only for a newly inserted sensor row. */
data class SensorReadingUpdate(
    val readings: SoilReadings,
    val isNewFromDevice: Boolean,
)

data class FieldPoint(
    val id: Int,
    val name: String,
    val moisture: Int,
    val ph: Float,
    val createdAt: String = "",
)

data class CropRecommendation(
    val crop: String, val subtitle: String, val nitrogen: String,
    val phosphorus: String, val potassium: String, val risks: List<String>,
)

val mockRecommendations = listOf(
    CropRecommendation("ข้าว", "นาข้าวฤดูฝน", "15–20 กก./ไร่", "8–12 กก./ไร่", "5–10 กก./ไร่", listOf("ความชื้นสูงกว่าค่าที่เหมาะสม", "ควรเฝ้าระวังโรคจากเชื้อรา")),
    CropRecommendation("ข้าวโพด", "ข้าวโพดเลี้ยงสัตว์", "18–25 กก./ไร่", "10–15 กก./ไร่", "8–12 กก./ไร่", listOf("ไนโตรเจนในดินอยู่ในระดับต่ำ", "ควรเพิ่มอินทรียวัตถุในดิน")),
    CropRecommendation("มันสำปะหลัง", "แปลงปลูกเชิงพาณิชย์", "12–18 กก./ไร่", "6–10 กก./ไร่", "10–16 กก./ไร่", listOf("ค่า pH ค่อนข้างเป็นกรด", "โพแทสเซียมมีแนวโน้มไม่เพียงพอ")),
    CropRecommendation("อ้อย", "อ้อยปลูกใหม่", "20–28 กก./ไร่", "10–14 กก./ไร่", "12–18 กก./ไร่", listOf("อุณหภูมิผิวดินสูง", "แนะนำคลุมดินเพื่อรักษาความชื้น")),
)
