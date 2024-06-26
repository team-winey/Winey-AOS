package org.go.sopt.winey.util.state

sealed interface InputError {
    enum class Nickname: InputError {
        BLANK_INPUT,
        INVALID_CHAR,
        UNCHECKED_DUPLICATION,
        DUPLICATED
    }

    object Upload: InputError
}
