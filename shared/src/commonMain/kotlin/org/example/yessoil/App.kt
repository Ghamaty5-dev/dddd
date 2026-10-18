package org.example.yessoil

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import org.example.yessoil.ui.DashboardScreen
import org.example.yessoil.ui.FieldScreen
import org.example.yessoil.ui.RecommendationScreen
import org.example.yessoil.ui.SoilReadings
import org.example.yessoil.ui.SensorReadingUpdate
import org.example.yessoil.ui.SoilViewModel
import org.example.yessoil.ui.theme.SoilTheme

private data class AppTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun App(
    sensorReadings: Flow<SensorReadingUpdate>? = null,
    pointLoader: (suspend () -> List<org.example.yessoil.ui.FieldPoint>)? = null,
    pointCreator: (suspend (String, SoilReadings) -> org.example.yessoil.ui.FieldPoint)? = null,
) {
    SoilTheme {
        val vm: SoilViewModel = viewModel { SoilViewModel(sensorReadings, pointLoader, pointCreator) }
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = remember {
            listOf(
                AppTab("ภาพรวม", Icons.Default.Home),
                AppTab("คำแนะนำ", Icons.Default.Agriculture),
                AppTab("แปลงของฉัน", Icons.Default.Map),
            )
        }
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        tabs.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                },
            ) { paddingValues ->
                AnimatedContent(targetState = selectedTab, label = "page_transition") { page ->
                    when (page) {
                        0 -> DashboardScreen(vm, paddingValues)
                        1 -> RecommendationScreen(vm, paddingValues)
                        else -> FieldScreen(vm, paddingValues)
                    }
                }
            }
        }
    }
}
