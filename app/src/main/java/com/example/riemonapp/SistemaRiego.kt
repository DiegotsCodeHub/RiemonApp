package com.example.riemonapp

data class SistemaRiegoValores(
    val bomba_activa: Boolean = false,
    val comando: String = "",
    val estado_sistema: String = "",
    val humedad_suelo: Int = 0,
    val nivel_agua: Int = 0,
    val nivel_luz: Int = 0
)