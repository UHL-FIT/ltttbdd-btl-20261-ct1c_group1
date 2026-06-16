package com.example.calpro.domain.engine

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.tan

class ExpressionEngine {
    fun evaluate(expression: String, useDegrees: Boolean = false): Double {
        val sanitized = expression
            .replace(",", "")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("%", "*(1/100.0)")
            .replace("π", PI.toString())
            .replace("ln(", "log(") // exp4j uses log() for natural logarithm (ln)
            .replace(Regex("(?<![a-zA-Z])e(?![a-zA-Z])"), E.toString())

        return ExpressionBuilder(sanitized)
            .functions(
                trig("sin", useDegrees) { sin(it) },
                trig("cos", useDegrees) { cos(it) },
                trig("tan", useDegrees) {
                    if (abs(cos(it)) < 1e-15) throw ArithmeticException("Lỗi: Tan không xác định")
                    tan(it)
                }
            )
            .build()
            .evaluate()
    }

    private fun trig(name: String, useDegrees: Boolean, op: (Double) -> Double) =
        object : Function(name, 1) {
            override fun apply(vararg args: Double): Double {
                val radians = if (useDegrees) Math.toRadians(args[0]) else args[0]
                val result = op(radians)
                // Correct floating point errors near zero (e.g., sin(180) or cos(90))
                return if (abs(result) < 1e-15) 0.0 else roundToPrecision(result)
            }
        }

    private fun roundToPrecision(value: Double): Double {
        if (!value.isFinite() || abs(value) > 1e14) return value
        val scale = 1e14
        return round(value * scale) / scale
    }
}
