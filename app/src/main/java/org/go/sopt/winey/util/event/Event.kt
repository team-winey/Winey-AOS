package org.go.sopt.winey.util.event

sealed interface Event {
    object Empty : Event
    object ShowSnackBar: Event
}
