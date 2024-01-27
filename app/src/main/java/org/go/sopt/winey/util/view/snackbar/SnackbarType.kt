package org.go.sopt.winey.util.view.snackbar

sealed class SnackbarType {
    data class WineyFeedResult(
        val isSuccess: Boolean
    ) : SnackbarType()

    data class NotiPermission(
        val onActionClicked: () -> Unit
    ) : SnackbarType()
}
