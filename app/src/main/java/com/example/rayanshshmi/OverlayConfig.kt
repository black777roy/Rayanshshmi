package com.example.rayanshshmi

import java.io.Serializable

data class OverlayConfig(
    val boxWidth: Int,
    val boxHeight: Int,
    val ballSize: Int,
    val borderType: Int // 0=red, 1=blue, 2=black
) : Serializable