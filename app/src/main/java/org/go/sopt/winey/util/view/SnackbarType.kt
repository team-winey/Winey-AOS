package org.go.sopt.winey.util.view

sealed class SnackbarType {
    data class WineyFeedResult(
        val isSuccess: Boolean
    ): SnackbarType()

    data class NotiPermission(
        val onActionClicked: () -> Unit
    ): SnackbarType()
}
