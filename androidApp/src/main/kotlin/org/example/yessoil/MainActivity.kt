package org.example.yessoil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize the YSC2027 Supabase client before rendering the app.
        SupabaseProvider.client

        setContent {
            App(
                sensorReadings = SupabaseSensorRepository.readings(),
                pointLoader = { SupabaseFieldPointRepository.loadPoints() },
                pointCreator = { name, readings -> SupabaseFieldPointRepository.addPoint(name, readings) },
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
