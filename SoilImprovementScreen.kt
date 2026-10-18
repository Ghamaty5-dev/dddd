import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class SoilImprovementCategory {
    PH_CORRECTION,
    ORGANIC_MATTER,
    MOISTURE_RETENTION,
    SOIL_STRUCTURE,
    BIOLOGICAL_ACTIVITY,
}

data class SoilImprovementItem(
    val title: String,
    val category: SoilImprovementCategory,
    val shortDescription: String,
    val measurement: String,
    val steps: List<String>,
)

/** Replace this local mock source with data from a farm plan or soil test when available. */
object MockSoilImprovementData {
    val items = listOf(
        SoilImprovementItem(
            title = "Apply agricultural lime",
            category = SoilImprovementCategory.PH_CORRECTION,
            shortDescription = "Raise acidic soil pH gradually and improve nutrient availability.",
            measurement = "50–100 kg/rai",
            steps = listOf(
                "Spread evenly over dry soil after harvest.",
                "Mix into the top 15–20 cm with a light tillage pass.",
                "Apply 2–4 weeks before planting and retest pH after 8–12 weeks.",
            ),
        ),
        SoilImprovementItem(
            title = "Add mature compost",
            category = SoilImprovementCategory.ORGANIC_MATTER,
            shortDescription = "Feed the soil and improve its structure, nutrient holding, and water storage.",
            measurement = "500–1,000 kg/rai",
            steps = listOf(
                "Use dark, crumbly compost with no strong ammonia smell.",
                "Spread before planting and incorporate into the top 10–15 cm.",
                "Repeat once per season, reducing the rate if soil organic matter improves.",
            ),
        ),
        SoilImprovementItem(
            title = "Plant green manure or cover crops",
            category = SoilImprovementCategory.BIOLOGICAL_ACTIVITY,
            shortDescription = "Protect the soil surface and return plant nutrients after cutting.",
            measurement = "1 cover crop between main crops",
            steps = listOf(
                "Choose legumes such as sunn hemp or mung bean where suitable locally.",
                "Cut before seed formation and leave the residue as surface mulch.",
                "Allow 2–3 weeks for residue to soften before planting the next crop.",
            ),
        ),
        SoilImprovementItem(
            title = "Mulch the soil surface",
            category = SoilImprovementCategory.MOISTURE_RETENTION,
            shortDescription = "Reduce evaporation, moderate soil temperature, and protect soil from rain impact.",
            measurement = "5–10 cm mulch layer",
            steps = listOf(
                "Use clean rice straw, chopped crop residue, or dry leaves.",
                "Keep mulch 5 cm away from the plant stem to prevent rot.",
                "Top up when the layer becomes thinner than 5 cm.",
            ),
        ),
        SoilImprovementItem(
            title = "Use gentle, timely tillage",
            category = SoilImprovementCategory.SOIL_STRUCTURE,
            shortDescription = "Keep soil pores open and avoid compaction from working wet soil.",
            measurement = "Till only when soil crumbles in hand",
            steps = listOf(
                "Avoid heavy machinery when soil sticks to tools or forms a shiny surface.",
                "Use shallow tillage to incorporate compost without breaking all soil aggregates.",
                "Keep permanent traffic lanes where possible to limit compaction.",
            ),
        ),
    )
}

/**
 * Dedicated soil-improvement screen. It intentionally contains no risk dashboard or alert logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilImprovementScreen(
    items: List<SoilImprovementItem> = MockSoilImprovementData.items,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Improve your soil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Small improvements, repeated each season, make soil easier to work and more productive.",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            items(items, key = { it.title }) { item -> ImprovementCard(item) }
        }
    }
}

@Composable
private fun ImprovementCard(item: SoilImprovementItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("•", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.category.displayName(), style = MaterialTheme.typography.labelMedium)
                    Text(item.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Text(if (expanded) "−" else "+", style = MaterialTheme.typography.headlineSmall)
            }
            Text(item.shortDescription, style = MaterialTheme.typography.bodyLarge)
            Text("Suggested amount: ${item.measurement}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)

            if (expanded) {
                HorizontalDivider()
                Text("How to do it", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                item.steps.forEachIndexed { index, step ->
                    Text("${index + 1}. $step", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

private fun SoilImprovementCategory.displayName(): String = when (this) {
    SoilImprovementCategory.PH_CORRECTION -> "pH Correction"
    SoilImprovementCategory.ORGANIC_MATTER -> "Organic Matter Boost"
    SoilImprovementCategory.MOISTURE_RETENTION -> "Moisture Retention"
    SoilImprovementCategory.SOIL_STRUCTURE -> "Soil Structure"
    SoilImprovementCategory.BIOLOGICAL_ACTIVITY -> "Biological Activity"
}
