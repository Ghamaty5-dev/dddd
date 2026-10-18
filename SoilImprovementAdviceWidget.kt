package com.yessoil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Current readings used to tailor soil-management recommendations. */
data class SoilConditionMetrics(
    val ph: Float? = null,
    val moisturePercent: Int? = null,
    val organicMatterPercent: Float? = null,
    val nitrogenStatus: String? = null,
)

/** A single, actionable recommendation grouped by its soil-management category. */
data class SoilImprovementAdvice(
    val title: String,
    val instruction: String,
    val category: SoilAdviceCategory,
)

enum class SoilAdviceCategory(
    val label: String,
    val icon: ImageVector,
    val accent: Color,
) {
    FERTILIZER("Fertilizer", Icons.Default.Science, Color(0xFF557A57)),
    WATERING("Water management", Icons.Default.Opacity, Color(0xFF397C9B)),
    ORGANIC_MATTER("Organic matter", Icons.Default.Eco, Color(0xFF8A6941)),
}

/**
 * Standalone soil-improvement widget. It intentionally has no dashboard or risk-assessment
 * dependencies, so it can be embedded in any screen that already provides a Material theme.
 */
@Composable
fun SoilImprovementAdviceWidget(
    metrics: SoilConditionMetrics,
    modifier: Modifier = Modifier,
    advice: List<SoilImprovementAdvice> = defaultSoilImprovementAdvice(metrics),
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Soil improvement plan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Practical steps based on your latest soil readings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            SoilMetricSummary(metrics)
            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(advice) { recommendation ->
                    AdviceRow(recommendation)
                }
            }
        }
    }
}

@Composable
private fun SoilMetricSummary(metrics: SoilConditionMetrics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Metric("pH", metrics.ph?.toString() ?: "—", Modifier.weight(1f))
        Metric("Moisture", metrics.moisturePercent?.let { "$it%" } ?: "—", Modifier.weight(1f))
        Metric("Organic matter", metrics.organicMatterPercent?.let { "$it%" } ?: "—", Modifier.weight(1f))
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AdviceRow(advice: SoilImprovementAdvice) {
    val category = advice.category
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = category.accent.copy(alpha = 0.10f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(category.icon, contentDescription = category.label, tint = category.accent)
            Column {
                Text(category.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = category.accent)
                Text(advice.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text(advice.instruction, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

fun defaultSoilImprovementAdvice(metrics: SoilConditionMetrics): List<SoilImprovementAdvice> = buildList {
    add(
        SoilImprovementAdvice(
            "Balance nutrients",
            if (metrics.nitrogenStatus?.equals("low", ignoreCase = true) == true) {
                "Apply chemical fertilizer สูตร 16-20-0 at 30–50 kg/rai. Split the application and place it beside the planting row."
            } else {
                "Use a soil-test-based fertilizer plan. Apply สูตร 16-20-0 at 30–50 kg/rai only when phosphorus and nitrogen are needed."
            },
            SoilAdviceCategory.FERTILIZER,
        ),
    )
    add(
        SoilImprovementAdvice(
            "Water at the root zone",
            if (metrics.moisturePercent != null && metrics.moisturePercent < 30) {
                "Moisture is low: irrigate slowly at the base of each plant until the root zone is evenly moist, then add mulch."
            } else {
                "Water early in the morning at the root zone. Let the topsoil dry slightly between watering to avoid waterlogging."
            },
            SoilAdviceCategory.WATERING,
        ),
    )
    add(
        SoilImprovementAdvice(
            "Build organic matter",
            if (metrics.organicMatterPercent != null && metrics.organicMatterPercent < 2f) {
                "Organic matter is low: incorporate well-matured compost or manure before planting and keep crop residues on the soil."
            } else {
                "Add mature compost after harvest, keep crop residues as mulch, and avoid burning them to maintain soil structure."
            },
            SoilAdviceCategory.ORGANIC_MATTER,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SoilImprovementAdviceWidgetPreview() {
    MaterialTheme {
        SoilImprovementAdviceWidget(
            metrics = SoilConditionMetrics(ph = 5.8f, moisturePercent = 24, organicMatterPercent = 1.4f, nitrogenStatus = "low"),
            modifier = Modifier.padding(16.dp),
        )
    }
}
