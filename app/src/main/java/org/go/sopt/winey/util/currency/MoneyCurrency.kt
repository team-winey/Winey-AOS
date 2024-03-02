package org.go.sopt.winey.util.currency

import java.text.NumberFormat

object MoneyCurrency {
    fun convertToKoreanCurrencyFormat(amount: Int): String {
        return when (amount) {
            in 0..99 -> "${amount}원"
            in 100..999 -> "${amount / 100}백원"
            in 1000..9999 -> "${amount / 1000}천원"
            in 10000..9999999 -> "${amount / 10000}만원"
            else -> "해당 범위를 초과하는 금액입니다."
        }
    }

    fun formatWithCommaForMoney(number: Int): String {
        val numberFormat = NumberFormat.getNumberInstance()
        return numberFormat.format(number)
    }
}
