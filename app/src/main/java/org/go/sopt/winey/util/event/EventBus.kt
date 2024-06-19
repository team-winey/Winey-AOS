package org.go.sopt.winey.util.event

sealed interface EventBus {
    object Empty : EventBus
    object ShowSnackBar: EventBus
}
