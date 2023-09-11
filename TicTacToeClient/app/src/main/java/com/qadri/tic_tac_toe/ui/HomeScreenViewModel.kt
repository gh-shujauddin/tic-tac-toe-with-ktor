package com.qadri.tic_tac_toe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qadri.tic_tac_toe.data.GameState
import com.qadri.tic_tac_toe.data.RealtimeMessagingClient
import com.qadri.tic_tac_toe.data.MakeTurn
import com.qadri.tic_tac_toe.data.PlayAgain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val client: RealtimeMessagingClient
): ViewModel() {

    val state = client
        .getGameStateStream()
        .onStart { _isConnection.value = true }
        .onEach { _isConnection.value = false }
        .catch { t -> _showConnectionError.value = t is ConnectException }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            GameState()
        )

    private val _isConnection = MutableStateFlow(false)
    val isConnection = _isConnection.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()

    fun onAction(action: MakeTurn) {

        viewModelScope.launch {
            client.sendAction(action)
        }
    }

    fun onPlayAgain(action: PlayAgain) {
        viewModelScope.launch {
            client.playAgain(action)
        }
    }
}