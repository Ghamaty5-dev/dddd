package org.example.yessoil

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    App(
        sensorReadings = SupabaseIosRepository.readings(),
        pointLoader = { SupabaseIosRepository.loadPoints() },
        pointCreator = { name, readings -> SupabaseIosRepository.addPoint(name, readings) },
    )
}
