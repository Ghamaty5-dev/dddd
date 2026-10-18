package org.example.yessoil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ScreenHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ConnectionCard(isConnected: Boolean) {
    val statusColor = if (isConnected) Color(0xFF237A52) else Color(0xFFB3261E)
    val dotColor = if (isConnected) Color(0xFF25A66A) else Color(0xFFD32F2F)
    val background = if (isConnected) MaterialTheme.colorScheme.primaryContainer else Color(0xFFFFEBEE)
    Card(colors = CardDefaults.cardColors(containerColor = background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bluetooth, "Bluetooth", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("อุปกรณ์วัดดิน", fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(dotColor, RoundedCornerShape(8.dp)))
                    Spacer(Modifier.width(6.dp)); Text(if (isConnected) "เชื่อมต่อ Wi-Fi แล้ว" else "ยังไม่ได้เชื่อมต่อ Wi-Fi", color = statusColor)
                }
            }
            Icon(Icons.Default.BatteryFull, "แบตเตอรี่", tint = statusColor)
            Text("82%", fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, unit: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(14.dp)) {
            Icon(icon, label, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(10.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(3.dp)); Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun NutrientRow(label: String, value: String, color: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.width(32.dp), fontWeight = FontWeight.Bold, color = color)
        LinearProgressIndicator(progress = { (value.toFloatOrNull() ?: 20f) / 60f }, modifier = Modifier.weight(1f).height(7.dp), color = color, trackColor = color.copy(alpha = .15f))
        Text(" $value mg/kg", modifier = Modifier.width(82.dp), style = MaterialTheme.typography.labelMedium)
    }
}
