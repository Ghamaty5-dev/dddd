package org.example.yessoil.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(vm: SoilViewModel, padding: PaddingValues) {
    val r = vm.readings
    val pulse by rememberInfiniteTransition(label = "device_pulse").animateFloat(0.45f, 1f, infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse), label = "pulse")
    LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ScreenHeader("สวัสดี, เกษตรกร 👋", "ภาพรวมสุขภาพดินแบบเรียลไทม์") }
        item {
            ConnectionCard(isConnected = vm.isDeviceOnline)
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                val statusColor = if (vm.isDeviceOnline) Color(0xFF2E7D5B) else Color(0xFFB3261E)
                Icon(if (vm.isDeviceOnline) Icons.Default.CheckCircle else Icons.Default.Warning, "สถานะอุปกรณ์", tint = statusColor, modifier = Modifier.alpha(pulse))
                Text(if (vm.isDeviceOnline) "  กำลังรับข้อมูลจาก Sensor" else "  ไม่พบข้อมูลใหม่จาก Sensor ภายใน 15 วินาที", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Text("ค่าดินล่าสุด", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricCard("pH", "%.1f".format(r.ph), "", Icons.Default.Science, Color(0xFF6E56B5), Modifier.weight(1f))
                MetricCard("EC", "%.2f".format(r.ec), "dS/m", Icons.Default.Eco, Color(0xFF2C8A72), Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricCard("ความชื้น", "${r.moisture.toInt()}", "%", Icons.Default.WaterDrop, Color(0xFF368CC1), Modifier.weight(1f))
                MetricCard("อุณหภูมิ", "%.1f".format(r.temperature), "°C", Icons.Default.Thermostat, Color(0xFFE4773F), Modifier.weight(1f))
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text("ธาตุอาหารหลัก N-P-K", fontWeight = FontWeight.Bold)
                    Text("ระดับปัจจุบันในแปลงของคุณ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    NutrientRow("N", r.nitrogen.toString(), Color(0xFF2E7D5B)); NutrientRow("P", r.phosphorus.toString(), Color(0xFFD18B27)); NutrientRow("K", r.potassium.toString(), Color(0xFF6E56B5))
                }
            }
        }
        item {
            Button(onClick = { if (!vm.isMeasuring) vm.startMeasurement() }, enabled = !vm.isMeasuring, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                if (vm.isMeasuring) { LinearProgressIndicator(Modifier.fillMaxWidth(.32f), color = Color.White); Text("  กำลังรอค่าล่าสุดจาก Sensor") } else { Icon(Icons.Default.PlayArrow, "เริ่มวัด"); Text("  เริ่มวัดค่าจากอุปกรณ์") }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(vm: SoilViewModel, padding: PaddingValues) {
    val selected = mockRecommendations[vm.selectedCropIndex]
    var expanded by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ScreenHeader("คำแนะนำการจัดการดิน", "วางแผนใส่ปุ๋ยให้เหมาะกับพืชและแปลงของคุณ") }
        item {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = selected.crop, onValueChange = {}, readOnly = true, label = { Text("ชนิดพืชเศรษฐกิจ") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    mockRecommendations.forEachIndexed { index, crop -> DropdownMenuItem(text = { Text(crop.crop) }, onClick = { vm.selectCrop(index); expanded = false }) }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(18.dp)) {
                    Text("แผนการบำรุงสำหรับ${selected.crop}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(selected.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(14.dp))
                    RecommendationLine("ไนโตรเจน (N)", selected.nitrogen, Color(0xFF2E7D5B)); RecommendationLine("ฟอสฟอรัส (P)", selected.phosphorus, Color(0xFFD18B27)); RecommendationLine("โพแทสเซียม (K)", selected.potassium, Color(0xFF6E56B5))
                }
            }
        }
        item { Text("ประเมินความเสี่ยงหลายปัจจัย", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(selected.risks) { risk -> Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3D6))) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Warning, "คำเตือน", tint = Color(0xFFD98A20)); Text(risk, Modifier.padding(start = 12.dp), fontWeight = FontWeight.Medium) } } }
    }
}

@Composable private fun RecommendationLine(label: String, value: String, color: Color) { Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text(value, fontWeight = FontWeight.Bold, color = color) } }

@Composable
fun FieldScreen(vm: SoilViewModel, padding: PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }
    var newPointName by remember { mutableStateOf("") }
    val currentMoisture = vm.readings.moisture
    val currentPh = vm.readings.ph
    LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ScreenHeader("แปลงเกษตรของฉัน", "ติดตามข้อมูลจากทุกจุดวัดในแปลง") }
        item { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF244B3D))) { Column(Modifier.padding(18.dp)) { Text("ค่าปัจจุบันจากฐานข้อมูล", color = Color.White); Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("ความชื้น  ${currentMoisture.toInt()}%", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("pH  %.1f".format(currentPh), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) } } } }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("จุดวัดในแปลง (${vm.points.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); IconButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, "เพิ่มจุดวัด", tint = MaterialTheme.colorScheme.primary) } } }
        items(vm.points, key = { it.id }) { point -> Card { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, "ตำแหน่ง", tint = MaterialTheme.colorScheme.primary); Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(point.name, fontWeight = FontWeight.SemiBold); Text("pH ${point.ph}  •  ความชื้น ${point.moisture}%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); if (point.createdAt.isNotBlank()) Text("บันทึกเมื่อ ${point.createdAt.replace('T', ' ').take(16)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }; Icon(Icons.Default.CheckCircle, "พร้อมใช้งาน", tint = Color(0xFF2E9A67)) } } }
        item { OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Icon(Icons.Default.Add, "เพิ่ม"); Text("  เพิ่มจุดวัดใหม่") } }
    }
    if (showDialog) AlertDialog(onDismissRequest = { showDialog = false }, title = { Text("เพิ่มจุดวัดใหม่") }, text = { OutlinedTextField(value = newPointName, onValueChange = { newPointName = it }, label = { Text("ชื่อจุดวัด") }, placeholder = { Text("เช่น แปลงทิศเหนือ") }) }, confirmButton = { TextButton(onClick = { vm.addPoint(newPointName); newPointName = ""; showDialog = false }) { Text("เพิ่ม") } }, dismissButton = { TextButton(onClick = { showDialog = false }) { Text("ยกเลิก") } })
}
