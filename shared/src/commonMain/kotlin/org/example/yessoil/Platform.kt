package org.example.yessoil

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform