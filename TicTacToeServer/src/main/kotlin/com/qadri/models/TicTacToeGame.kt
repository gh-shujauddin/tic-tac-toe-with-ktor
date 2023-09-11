package com.qadri.models

import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.encodeToString
import java.util.concurrent.ConcurrentHashMap

class TicTacToeGame {

    private val state = MutableStateFlow(GameState())

    private val playerSockets = ConcurrentHashMap<BoardCellValue, WebSocketSession>()

    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var delayGameJob: Job? = null

    init {
        state.onEach(::broadcast)
            .launchIn(gameScope) // On each emission we call broadcast with corresponding state
    }

    fun connectPlayer(session: WebSocketSession): BoardCellValue? {
        var isPlayerX = state.value.connectedPlayer.any { it == BoardCellValue.CROSS }
        val player = if (isPlayerX) BoardCellValue.CIRCLE else BoardCellValue.CROSS

        state.update {
            if (state.value.connectedPlayer.contains(player)) {
                return null
            }

            if (!playerSockets.containsKey(player)) { // If required player is not in Socket
                playerSockets[player] = session
            }

            it.copy(
                connectedPlayer = it.connectedPlayer + player
            )
        }
        return player
    }

    fun disconnectPlayer(player: BoardCellValue) {
        playerSockets.remove(player)
        state.update {
            it.copy(
                connectedPlayer = it.connectedPlayer - player
            )
        }
    }

    suspend fun broadcast(state: GameState) {
        playerSockets.values.forEach { socket ->
            socket.send(
                Json.encodeToString(state)
            )
        }
    }

    fun addValueToBoard(player: BoardCellValue, cellNumber: Int) {
        if (state.value.boardItem[cellNumber] != BoardCellValue.NONE) {
            return
        }
        if (state.value.currentTurn != player) {
            return
        }
        if (state.value.currentTurn == BoardCellValue.CIRCLE) {
            state.update {
                val newBoardItem = it.boardItem.also { board ->
                    board[cellNumber] = player
                }
                it.copy(
                    boardItem = newBoardItem
                )
            }
            if (checkForVictory(BoardCellValue.CIRCLE)) {
                state.update {
                    it.copy(
                        hintText = "Player O Won",
                        hasWon = true,
                        playerCircleCount = state.value.playerCircleCount + 1,
                        currentTurn = BoardCellValue.NONE,
                        isPlayAgainClicked = true
                    )
                }
            } else if (hasBoardFull()) {
                state.update {
                    it.copy(
                        hintText = "Game Draw",
                        drawCount = state.value.drawCount + 1,
                        isPlayAgainClicked = true
                    )
                }
            } else {
                state.update {
                    it.copy(
                        hintText = "Player X's Turn",
                        currentTurn = BoardCellValue.CROSS
                    )
                }
            }
        } else if (state.value.currentTurn == BoardCellValue.CROSS) {
            state.update {
                val newBoardItem = it.boardItem.also { board ->
                    board[cellNumber] = BoardCellValue.CROSS
                }
                it.copy(
                    boardItem = newBoardItem
                )
            }
            state.value.boardItem[cellNumber] = BoardCellValue.CROSS
            if (checkForVictory(BoardCellValue.CROSS)) {
                state.update {
                    it.copy(
                        hintText = "Player X Won",
                        hasWon = true,
                        playerCrossCount = state.value.playerCrossCount + 1,
                        currentTurn = BoardCellValue.NONE,
                        isPlayAgainClicked = true
                    )
                }
            } else if (hasBoardFull()) {
                state.update {
                    it.copy(
                        hintText = "Game Draw",
                        drawCount = state.value.drawCount + 1,
                        isPlayAgainClicked = true
                    )
                }
            } else {
                state.update {
                    it.copy(
                        hintText = "Player O's Turn",
                        currentTurn = BoardCellValue.CIRCLE
                    )
                }
            }
        }
    }


    private fun hasBoardFull(): Boolean {
        if (state.value.boardItem.containsValue(BoardCellValue.NONE)) return false
        return true
    }

    private fun checkForVictory(boardValue: BoardCellValue): Boolean {
        when {
            state.value.boardItem[1] == boardValue && state.value.boardItem[2] == boardValue && state.value.boardItem[3] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.HORIZONTAL1
                    )
                }
                return true
            }

            state.value.boardItem[4] == boardValue && state.value.boardItem[5] == boardValue && state.value.boardItem[6] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.HORIZONTAL2
                    )
                }
                return true
            }

            state.value.boardItem[7] == boardValue && state.value.boardItem[8] == boardValue && state.value.boardItem[9] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.HORIZONTAL3
                    )
                }
                return true
            }

            state.value.boardItem[1] == boardValue && state.value.boardItem[4] == boardValue && state.value.boardItem[7] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.VERTICAL1
                    )
                }
                return true
            }

            state.value.boardItem[2] == boardValue && state.value.boardItem[5] == boardValue && state.value.boardItem[8] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.VERTICAL2
                    )
                }
                return true
            }

            state.value.boardItem[3] == boardValue && state.value.boardItem[6] == boardValue && state.value.boardItem[9] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.VERTICAL3
                    )
                }
                return true
            }

            state.value.boardItem[1] == boardValue && state.value.boardItem[5] == boardValue && state.value.boardItem[9] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.DIAGONAL1
                    )
                }
                return true
            }

            state.value.boardItem[3] == boardValue && state.value.boardItem[5] == boardValue && state.value.boardItem[7] == boardValue -> {
                state.update {
                    it.copy(
                        victoryType = VictoryType.DIAGONAL2
                    )
                }
                return true
            }

            else -> {
                return false
            }
        }
    }

    fun gameReset() {
        state.value.boardItem.forEach { (i, _) ->
            state.value.boardItem[i] = BoardCellValue.NONE
        }
        state.update {
            it.copy(
                hintText = "Player O's turn",
                currentTurn = BoardCellValue.CIRCLE,
                victoryType = VictoryType.NONE,
                hasWon = false,
                isPlayAgainClicked = false
            )
        }
    }

    private fun startNewRoundDelayed() {
        delayGameJob?.cancel()
        delayGameJob = gameScope.launch {
            state.value.boardItem.forEach { (i, _) ->
                state.value.boardItem[i] = BoardCellValue.NONE
            }
            delay(5000L)
            state.update {
                it.copy(
                    hintText = "Player O's turn",
                    currentTurn = BoardCellValue.CIRCLE,
                    victoryType = VictoryType.NONE,
                    hasWon = false,
                    isPlayAgainClicked = false
                )
            }
        }
    }
}
