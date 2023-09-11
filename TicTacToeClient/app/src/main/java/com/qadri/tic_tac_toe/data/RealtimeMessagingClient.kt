package com.qadri.tic_tac_toe.data

import kotlinx.coroutines.flow.Flow

interface RealtimeMessagingClient {
    fun getGameStateStream(): Flow<GameState>
    suspend fun sendAction(action: MakeTurn)

    suspend fun playAgain(action: PlayAgain)
    suspend fun close()
}