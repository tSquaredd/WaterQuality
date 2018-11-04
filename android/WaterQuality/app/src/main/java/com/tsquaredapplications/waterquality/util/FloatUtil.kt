package com.tsquaredapplications.waterquality.util

import java.math.BigDecimal

class FloatUtil {
    companion object {
        fun round(num: Float, decimalPlace: Int): Float {
            var bd = BigDecimal(num.toString())
             bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
            return bd.toFloat()

        }
    }
}