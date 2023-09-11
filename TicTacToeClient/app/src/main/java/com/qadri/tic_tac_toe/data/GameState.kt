package com.qadri.tic_tac_toe.data

import kotlinx.serialization.Serializable

@Serializable
data class GameState (
    val playerCircleCount: Int = 0,
    val playerCrossCount: Int = 0,
    val drawCount: Int = 0,
    val hintText: String = "Player 'O's turn",
    val currentTurn: BoardCellValue = BoardCellValue.CIRCLE,
    val victoryType: VictoryType = VictoryType.NONE,
    val hasWon: Boolean = false,
    val boardItem: MutableMap<Int, BoardCellValue> = boardItems,
    val connectedPlayer: List<BoardCellValue> = emptyList(),
    val isPlayAgainClicked: Boolean = false
) {
    companion object{
        val boardItems: MutableMap<Int, BoardCellValue> = mutableMapOf(
            1 to BoardCellValue.NONE,
            2 to BoardCellValue.NONE,
            3 to BoardCellValue.NONE,
            4 to BoardCellValue.NONE,
            5 to BoardCellValue.NONE,
            6 to BoardCellValue.NONE,
            7 to BoardCellValue.NONE,
            8 to BoardCellValue.NONE,
            9 to BoardCellValue.NONE
        )
    }
}

enum class VictoryType {
    HORIZONTAL1,
    HORIZONTAL2,
    HORIZONTAL3,
    VERTICAL1,
    VERTICAL2,
    VERTICAL3,
    DIAGONAL1,
    DIAGONAL2,
    NONE
}

enum class BoardCellValue {
    CIRCLE,
    CROSS,
    NONE
}