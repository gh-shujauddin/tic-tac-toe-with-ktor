package com.qadri.tic_tac_toe.data

import kotlinx.serialization.Serializable

@Serializable
data class MakeTurn(val x: Int)

@Serializable
data class PlayAgain(val play: Boolean)
