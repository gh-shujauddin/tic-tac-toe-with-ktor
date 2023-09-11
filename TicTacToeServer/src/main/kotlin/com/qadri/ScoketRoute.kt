package com.qadri

import com.qadri.models.MakeTurn
import com.qadri.models.PlayAgain
import com.qadri.models.TicTacToeGame
import com.qadri.models.UserActions
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

fun Route.socket(game: TicTacToeGame) {
    route("/play") {
        webSocket {
            val player = game.connectPlayer(this)

            if (player == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "2 players already connected"))
                return@webSocket
            }

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val type = frame.readText().substringBefore("#")
                        if (type == "make_turn") {
                            val action: MakeTurn = extractActionMakeTurn(frame.readText())
                            game.addValueToBoard(player = player, action.x)
                        }
                        if (type == "play_again") {
                            game.gameReset()
                        }
//                        game.finishTurn(player, action.x, action.y)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                game.disconnectPlayer(player)
            }
        }
    }
}

private fun extractActionMakeTurn(message: String): MakeTurn {
    //make_turn#{....}
    val body = message.substringAfter("#")
    return  Json.decodeFromString(body)
}