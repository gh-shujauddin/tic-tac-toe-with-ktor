package com.qadri.models

import kotlinx.serialization.Serializable

@Serializable
data class MakeTurn(val x: Int)

@Serializable
data class PlayAgain(val playAgain: Boolean)
