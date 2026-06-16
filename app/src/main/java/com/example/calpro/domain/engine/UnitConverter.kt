package com.example.calpro.domain.engine

class UnitConverter {

    fun convert(value: Double, fromUnit: String, toUnit: String, category: String): Double {
        if (fromUnit == toUnit) return value

        return when (category) {
            "Length" -> convertLength(value, fromUnit, toUnit)
            "Weight" -> convertWeight(value, fromUnit, toUnit)
            "Temp" -> convertTemperature(value, fromUnit, toUnit)
            else -> value
        }
    }

    private fun convertLength(v: Double, from: String, to: String): Double {
        val inMeters = when (from) {
            "nm" -> v / 1e9
            "µm" -> v / 1e6
            "mm" -> v / 1000
            "cm" -> v / 100
            "dm" -> v / 10
            "km" -> v * 1000
            "inch" -> v * 0.0254
            "ft" -> v * 0.3048
            "yd" -> v * 0.9144
            "mi" -> v * 1609.344
            "nmi" -> v * 1852.0
            else -> v // from m
        }
        return when (to) {
            "nm" -> inMeters * 1e9
            "µm" -> inMeters * 1e6
            "mm" -> inMeters * 1000
            "cm" -> inMeters * 100
            "dm" -> inMeters * 10
            "km" -> inMeters / 1000
            "inch" -> inMeters / 0.0254
            "ft" -> inMeters / 0.3048
            "yd" -> inMeters / 0.9144
            "mi" -> inMeters / 1609.344
            "nmi" -> inMeters / 1852.0
            else -> inMeters
        }
    }

    private fun convertWeight(v: Double, from: String, to: String): Double {
        val inKg = when (from) {
            "µg" -> v / 1e9
            "mg" -> v / 1e6
            "g" -> v / 1000
            "t" -> v * 1000
            "oz" -> v * 0.0283495
            "lb" -> v * 0.453592
            "st" -> v * 6.35029
            else -> v // from kg
        }
        return when (to) {
            "µg" -> inKg * 1e9
            "mg" -> inKg * 1e6
            "g" -> inKg * 1000
            "t" -> inKg / 1000
            "oz" -> inKg / 0.0283495
            "lb" -> inKg / 0.453592
            "st" -> inKg / 6.35029
            else -> inKg
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val inCelsius = when (from) {
            "°F" -> (value - 32) * 5 / 9
            "K" -> value - 273.15
            else -> value
        }
        return when (to) {
            "°F" -> (inCelsius * 9 / 5) + 32
            "K" -> inCelsius + 273.15
            else -> inCelsius
        }
    }
}