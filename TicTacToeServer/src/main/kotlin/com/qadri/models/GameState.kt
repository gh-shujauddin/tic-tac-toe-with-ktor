package com.qadri.models

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val playerCircleCount: Int = 0,
    val playerCrossCount: Int = 0,
    val drawCount: Int = 0,
    val hintText: String = "Player 'O's turn",
    val currentTurn: BoardCellValue = BoardCellValue.CIRCLE,
    val victoryType: VictoryType = VictoryType.NONE,
    val hasWon: Boolean = false,
    val boardItem: MutableMap<Int, BoardCellValue> = boardItems(),
    val connectedPlayer: List<BoardCellValue> = emptyList(),
    val isPlayAgainClicked: Boolean = false
) {
    companion object {
        fun boardItems(): MutableMap<Int, BoardCellValue> {
            return mutableMapOf(
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
//
//@Serializable
//data class GameState(
//    val playerAtTurn: Char? = 'X',
//    val field: Array<Array<Char?>> = emptyField(),
//    val winningPlayer: Char? = null,
//    val isBoardFull: Boolean = false,
//    val connectedPlayer: List<Char> = emptyList()
//) {
//    companion object{
//        fun emptyField(): Array<Array<Char?>> {
//            return arrayOf(
//                arrayOf(null, null, null),
//                arrayOf(null, null, null),
//                arrayOf(null, null, null)
//            )
//        }
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as GameState
//
//        if (playerAtTurn != other.playerAtTurn) return false
//        if (!field.contentDeepEquals(other.field)) return false
//        if (winningPlayer != other.winningPlayer) return false
//        if (isBoardFull != other.isBoardFull) return false
//        if (connectedPlayer != other.connectedPlayer) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = playerAtTurn?.hashCode() ?: 0
//        result = 31 * result + field.contentDeepHashCode()
//        result = 31 * result + (winningPlayer?.hashCode() ?: 0)
//        result = 31 * result + isBoardFull.hashCode()
//        result = 31 * result + connectedPlayer.hashCode()
//        return result
//    }
//}
