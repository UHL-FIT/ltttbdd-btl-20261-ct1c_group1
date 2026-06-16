package com.example.calpro.domain.engine

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

fun formatNumber(value: Double): String {
    if (value.isNaN()) return "NaN"
    if (value.isInfinite()) return "Tràn số"

    val absValue = Math.abs(value)

    if (absValue == 0.0) return "0"
//hiển thị khoa học cho số rất lớn hoặc rất nhỏ
    if (absValue >= 1e10 || absValue < 1e-10) {
        return formatScientific(value)
    }

    //kiểm tra xem có phải số nguyên không
    val isInteger = abs(value - value.roundToLong()) < 1e-10
    if (isInteger && absValue < Long.MAX_VALUE.toDouble()) {
        return formatInteger(Math.round(value))
    }

    return formatDecimal(value)
}

//phân tách chữ số nguyên cách hàng nghìn
private fun formatInteger(value: Long): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#,###", symbols)
    return df.format(value)
}

//định dạng số thực 10 chữ số thập phân
//phần nguyên có dấu phẩy
private fun formatDecimal(value: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#,##0.##########", symbols)
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(value)
}

//định dạng khoa học
//vd 1.23456E12
private fun formatScientific(value: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("0.######E0", symbols)
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(value)
}

//định dạng hiển thị biểu thức cho đẹp (có dấu phẩy phần nghìn)
fun formatExpressionForDisplay(expr: String): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("#,###", symbols)
    
    val regex = Regex("\\d+(\\.\\d*)?")
    return regex.replace(expr) { matchResult ->
        val numberString = matchResult.value
        if (numberString.contains(".")) {
            val parts = numberString.split(".")
            val intPart = parts[0].toLongOrNull()?.let { formatter.format(it) } ?: parts[0]
            "$intPart.${parts[1]}"
        } else {
            val num = numberString.toLongOrNull()
            if (num != null) formatter.format(num) else numberString
        }
    }
}