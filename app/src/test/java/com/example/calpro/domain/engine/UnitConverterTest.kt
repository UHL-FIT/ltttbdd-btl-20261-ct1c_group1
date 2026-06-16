package com.example.calpro.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UnitConverterTest {
    private lateinit var converter: UnitConverter

    @Before
    fun setup() {
        converter = UnitConverter()
    }

    @Test
    fun testLengthConversion() {
        val result = converter.convert(1.0, "km", "m", "Length")
        assertEquals(1000.0, result, 0.001)

        val result2 = converter.convert(100.0, "cm", "m", "Length")
        assertEquals(1.0, result2, 0.001)

    }

    @Test
    fun testExtendedLengthConversion() {
        val result1 = converter.convert(1e9, "nm", "m", "Length")
        assertEquals(1.0, result1, 0.001)

        val result2 = converter.convert(1.0, "mi", "m", "Length")
        assertEquals(1609.344, result2, 0.001)

        //ft->inch(1 ft = 12 inches)
        val result3 = converter.convert(1.0, "ft", "inch", "Length")
        assertEquals(12.0, result3, 0.001)

        // nmi -> km (1 nmi = 1.852 km)
        val result4 = converter.convert(1.0, "nmi", "km", "Length")
        assertEquals(1.852, result4, 0.001)
    }

    @Test
    fun testWeightConversion() {
        val result = converter.convert(1.0, "kg", "g", "Weight")
        assertEquals(1000.0, result, 0.001)
    }

    @Test
    fun testExtendedWeightConversion() {
        //t->kg
        val result1 = converter.convert(1.0, "t", "kg", "Weight")
        assertEquals(1000.0, result1, 0.001)

        //st->kg
        val result2 = converter.convert(1.0, "st", "kg", "Weight")
        assertEquals(6.35029, result2, 0.001)

        //mg->g
        val result3 = converter.convert(1000.0, "mg", "g", "Weight")
        assertEquals(1.0, result3, 0.001)

        // oz -> g (1 oz ~ 28.3495 g)
        val result4 = converter.convert(1.0, "oz", "g", "Weight")
        assertEquals(28.3495, result4, 0.001)

    }

    @Test
    fun testTemperatureConversion() {
        val result = converter.convert(0.0, "°C", "°F", "Temp")
        assertEquals(32.0, result, 0.001)

        val result2 = converter.convert(32.0, "°F", "°C", "Temp")
        assertEquals(0.0, result2, 0.001)
    }

    @Test
    fun testSameUnitConversion() {
        val result = converter.convert(1.0, "m", "m", "Length")
        assertEquals(1.0, result, 0.001)

        val result2 = converter.convert(1.0, "kg", "kg", "weight")
        assertEquals(1.0, result2, 0.001)

        val result3 = converter.convert(0.0, "°C", "°C", "Temp")
        assertEquals(0.0, result3, 0.001)
    }

    @Test
    fun testInvalidCategory() {
        val result = converter.convert(1.0, "m", "cm", "InvalidCategory")
        assertEquals(1.0, result, 0.001)

    }

    @Test
    fun testInvalidUnit() {
        val result = converter.convert(1.0, "invalid_from", "invalid_to", "Length")
        assertEquals(1.0, result, 0.001)
    }
}