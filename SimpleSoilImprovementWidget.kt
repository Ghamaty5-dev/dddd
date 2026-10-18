// Standalone Jetpack Compose component. Move this file into the app's source set
// and add the package declaration used by the surrounding feature module.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** A practical, farmer-facing soil action shown as an independent UI item. */
data class SoilImprovementAction(
    val title: String,
    val instruction: String,
    val icon: ImageVector,
)

/**
 * Drop-in soil improvement widget.
 *
 * The component owns no dashboard, risk, or navigation state. Pass measured
 * conditions and actions from any screen, or use the default local mock data.
 */
@Composable
fun SimpleSoilImprovementWidget(
    modifier: Modifier = Modifier,
    soilCondition: String = "Based on the current soil readings",
    actions: List<SoilImprovementAction> = defaultSoilImprovementActions(),
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Improve your soil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = soilCondition,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))

            actions.forEach { action ->
                SoilImprovementActionRow(action)
            }
        }
    }
}

@Composable
private fun SoilImprovementActionRow(action: SoilImprovementAction) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = action.instruction,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun defaultSoilImprovementActions() = listOf(
    SoilImprovementAction(
        title = "Apply balanced fertilizer",
        instruction = "Apply N-P-K 15-15-15 at 25–30 kg/rai. Place it 5–10 cm from the stem and water it in.",
        icon = Icons.Default.Eco,
    ),
    SoilImprovementAction(
        title = "Water slowly and deeply",
        instruction = "Irrigate at the base in the early morning until the top 15–20 cm of soil is moist. Avoid standing water.",
        icon = Icons.Default.Opacity,
    ),
    SoilImprovementAction(
        title = "Add organic compost",
        instruction = "Mix 2–3 kg of mature compost per square metre into the top 10–15 cm before planting.",
        icon = Icons.Default.Spa,
    ),
)

@Preview(showBackground = true)
@Composable
private fun SimpleSoilImprovementWidgetPreview() {
    MaterialTheme {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item { SimpleSoilImprovementWidget() }
        }
    }
}
