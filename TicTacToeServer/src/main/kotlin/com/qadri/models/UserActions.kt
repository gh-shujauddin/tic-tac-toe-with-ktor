package com.qadri.models

import kotlinx.serialization.Serializable

@Serializable
sealed class UserActions{
    object PlayAgainButtonClicked: UserActions()
    data class BoardTapped(val cellNumber: Int): UserActions()
}
