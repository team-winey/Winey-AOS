package org.go.sopt.winey.util.number

import java.text.DecimalFormat

fun Int.formatAmountNumber(): String {
    return DecimalFormat("#,###").format(this)
}
