import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Local, replaceable sample source for the advice screen. */
data class SoilReading(
    val pH: Double,
    val nitrogen: Int,
    val phosphorus: Int,
    val potassium: Int,
    val moisturePercent: Int,
    val ec: Double,
)

enum class AdvicePriority { LOW, MEDIUM, HIGH }

data class SoilAdvice(
    val title: String,
    val summary: String,
    val steps: List<String>,
    val priority: AdvicePriority,
)

object MockSoilAdviceData {
    val reading = SoilReading(
        pH = 5.4,
        nitrogen = 18,
        phosphorus = 11,
        potassium = 76,
        moisturePercent = 28,
        ec = 1.9,
    )

    val advice = listOf(
        SoilAdvice(
            title = "Correct acidic soil",
            summary = "pH 5.4 is acidic. Apply agricultural lime before planting to improve nutrient availability.",
            steps = listOf(
                "Broadcast 80–120 kg/rai of finely ground agricultural lime and incorporate into the top 15–20 cm.",
                "Apply at least 2–4 weeks before planting; re-test pH after 8–12 weeks.",
                "Use gypsum only when a soil test confirms sodicity or calcium deficiency; it does not raise pH.",
            ),
            priority = AdvicePriority.HIGH,
        ),
        SoilAdvice(
            title = "Build nitrogen and phosphorus",
            summary = "N and P are low for active crop growth; split fertilizer to reduce losses.",
            steps = listOf(
                "Apply 8–12 kg N/rai in 2–3 split applications, starting at planting and top-dressing during vegetative growth.",
                "Apply 4–6 kg P₂O₅/rai in the planting band, keeping fertilizer away from direct seed contact.",
                "Potassium is adequate at 76 mg/kg; maintain with 2–4 kg K₂O/rai only if the crop removes a high K load.",
            ),
            priority = AdvicePriority.MEDIUM,
        ),
        SoilAdvice(
            title = "Manage low moisture",
            summary = "Moisture is 28%, so the root zone is drying. Irrigate deeply before plants show wilting.",
            steps = listOf(
                "Apply 12–18 mm of water in the early morning, then check moisture again after 24 hours.",
                "Mulch around the crop and maintain residue cover to reduce evaporation; avoid shallow daily watering.",
                "If water pools after irrigation, improve drainage because low pH plus saturated roots increases stress.",
            ),
            priority = AdvicePriority.HIGH,
        ),
        SoilAdvice(
            title = "Watch root disease and pests",
            summary = "Acidic, dry soil can weaken roots and increase susceptibility to opportunistic pests.",
            steps = listOf(
                "Scout twice weekly for yellowing, root browning, cutworms, and uneven patches.",
                "Keep irrigation uniform and remove severely affected plants; do not reuse contaminated soil.",
                "Use crop rotation and an approved biological or chemical treatment only after local diagnosis.",
            ),
            priority = AdvicePriority.MEDIUM,
        ),
    )
}

/** Standalone farmer-facing screen. Pass a real reading/advice list when connecting a sensor or API. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilAdviceScreen(
    reading: SoilReading = MockSoilAdviceData.reading,
    advice: List<SoilAdvice> = MockSoilAdviceData.advice,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Soil action plan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { SoilHealthSummary(reading) }
            item {
                Text("Recommended actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(advice, key = { it.title }) { item -> AdviceCard(item) }
        }
    }
}

@Composable
private fun SoilHealthSummary(reading: SoilReading) {
    val score = 62
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Soil health", style = MaterialTheme.typography.labelLarge)
                    Text("Needs attention", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.tertiaryContainer) {
                    Text("$score / 100", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
                }
            }
            Text("Prioritize pH correction, split nutrient applications, and restore root-zone moisture.", style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Metric("pH", "%.1f".format(reading.pH))
                Metric("N · P · K", "${reading.nitrogen} · ${reading.phosphorus} · ${reading.potassium}")
                Metric("Moisture", "${reading.moisturePercent}%")
                Metric("EC", "${reading.ec} dS/m")
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun AdviceCard(advice: SoilAdvice) {
    var expanded by remember { mutableStateOf(false) }
    val priorityColor = when (advice.priority) {
        AdvicePriority.LOW -> Color(0xFF2E7D32)
        AdvicePriority.MEDIUM -> Color(0xFFB26A00)
        AdvicePriority.HIGH -> MaterialTheme.colorScheme.error
    }
    Card(onClick = { expanded = !expanded }) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(advice.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(advice.summary, style = MaterialTheme.typography.bodyMedium)
                }
                Text(if (expanded) "−" else "+", style = MaterialTheme.typography.headlineSmall)
            }
            AssistChip(onClick = { expanded = !expanded }, label = { Text("${advice.priority.name.lowercase().replaceFirstChar { it.uppercase() }} priority") })
            if (expanded) {
                HorizontalDivider()
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    advice.steps.forEachIndexed { index, step ->
                        Text("${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
