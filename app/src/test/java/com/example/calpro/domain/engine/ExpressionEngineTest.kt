package com.example.calpro.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExpressionEngineTest {
    private lateinit var engine: ExpressionEngine

    @Before
    fun setup() {
        engine = ExpressionEngine()
    }

    @Test
    fun testBasicAddition() {
        val result = engine.evaluate("2+3")
        assertEquals(5.0, result, 0.001)
    }

    @Test
    fun testBasicSubtraction() {
        val result = engine.evaluate("10-4")
        assertEquals(6.0, result, 0.001)
    }

    @Test
    fun testBasicMultiplication() {
        val result = engine.evaluate("5×6")
        assertEquals(30.0, result, 0.001)
    }

    @Test
    fun testBasicDivision() {
        val result = engine.evaluate("15÷3")
        assertEquals(5.0, result, 0.001)
    }

    @Test
    fun testComplexExpression() {
        val result = engine.evaluate("2+3×4")
        assertEquals(14.0, result, 0.001)
    }

    @Test(expected = ArithmeticException::class)
    fun testDivisionByZero() {
        engine.evaluate("5÷0")
    }

    //RAD mode(default)
    @Test
    fun testScientificFuntionRad() {
        val result = engine.evaluate("sin(0)")
        assertEquals(0.0, result, 0.001)

        val resultCos = engine.evaluate("cos(0)")
        assertEquals(1.0, resultCos, 0.001)
    }

    //DEG mode
    @Test
    fun testScientificFunctionDeg() {
        val resultSin = engine.evaluate("sin(90)", useDegrees = true)
        assertEquals(1.0, resultSin, 0.001)

        val resultCos = engine.evaluate("cos(180)", useDegrees = true)
        assertEquals(-1.0, resultCos, 0.001)

        val resultTan = engine.evaluate("tan(45)", useDegrees = true)
        assertEquals(1.0, resultTan, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidExpression() {
        engine.evaluate("2*+")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEmptyExpression() {
        engine.evaluate("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMismatchedParentheses() {
        engine.evaluate("(2+3")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDanglingOperator() {
        engine.evaluate("5+")
    }

    @Test(expected = ArithmeticException::class)
    fun testZeroDividedByZero() {
        engine.evaluate("0÷0")
    }

    @Test
    fun testPercentage() {
        assertEquals(1.5, engine.evaluate("50%*3"), 0.001)
        assertEquals(100.05, engine.evaluate("100+5%"), 0.001)
        assertEquals(0.0005, engine.evaluate("5%%"), 0.00001)
    }

    @Test
    fun testSpecialDecimal() {
        assertEquals(0.3, engine.evaluate("0.1+0.2"), 0.001)
    }

    //chế độ RAD:các góc đặc biệt

    @Test
    fun testSinRAD() {
        assertEquals(0.0, engine.evaluate("sin(0)"), 1e-10)
        assertEquals(1.0, engine.evaluate("sin(${Math.PI / 2})"), 1e-10)
        assertEquals(0.0, engine.evaluate("sin(${Math.PI})"), 1e-10)
    }

    @Test
    fun testCosRad() {
        assertEquals(1.0, engine.evaluate("cos(0)"), 1e-10)
        assertEquals(0.0, engine.evaluate("cos(${Math.PI / 2})"), 1e-10)
        assertEquals(-1.0, engine.evaluate("cos(${Math.PI})"), 1e-10)
    }

    @Test
    fun testTanRad() {
        assertEquals(0.0, engine.evaluate("tan(0)"), 1e-10)
        assertEquals(1.0, engine.evaluate("tan(${Math.PI / 4})"), 1e-10)
    }

//chế độ DEG:các góc đặc biệt

    @Test
    fun testSinDeg_specialAngles() {
        assertEquals(0.0, engine.evaluate("sin(0)", useDegrees = true), 1e-10)
        assertEquals(0.5, engine.evaluate("sin(30)", useDegrees = true), 1e-10)
        assertEquals(1.0, engine.evaluate("sin(90)", useDegrees = true), 1e-10)
        assertEquals(0.0, engine.evaluate("sin(180)", useDegrees = true), 1e-10)
        assertEquals(-1.0, engine.evaluate("sin(270)", useDegrees = true), 1e-10)
        assertEquals(0.0, engine.evaluate("sin(360)", useDegrees = true), 1e-10)
    }

    @Test
    fun testCosDeg_specialAngles() {
        assertEquals(1.0, engine.evaluate("cos(0)", useDegrees = true), 1e-10)
        assertEquals(0.0, engine.evaluate("cos(90)", useDegrees = true), 1e-10)
        assertEquals(-1.0, engine.evaluate("cos(180)", useDegrees = true), 1e-10)
        assertEquals(0.0, engine.evaluate("cos(270)", useDegrees = true), 1e-10)
        assertEquals(1.0, engine.evaluate("cos(360)", useDegrees = true), 1e-10)
    }

    @Test
    fun testTanDeg_specialAngles() {
        assertEquals(0.0, engine.evaluate("tan(0)", useDegrees = true), 1e-10)
        assertEquals(1.0, engine.evaluate("tan(45)", useDegrees = true), 1e-10)
        assertEquals(0.0, engine.evaluate("tan(180)", useDegrees = true), 1e-10)
        assertEquals(0.0, engine.evaluate("tan(360)", useDegrees = true), 1e-10)
    }

    @Test(expected = ArithmeticException::class)
    fun testTanDeg_90_undefined() {
        engine.evaluate("tan(90)", useDegrees = true)
    }

    @Test(expected = ArithmeticException::class)
    fun testTanDeg_270_undefined() {
        engine.evaluate("tan(270)", useDegrees = true)
    }

    //góc âm

    @Test
    fun testSinDeg_negativeAngles() {
        assertEquals(-0.5, engine.evaluate("sin(-30)", useDegrees = true), 1e-10)
        assertEquals(-1.0, engine.evaluate("sin(-90)", useDegrees = true), 1e-10)
    }

    @Test
    fun testCosDeg_negativeAngles() {
        assertEquals(0.5, engine.evaluate("cos(60)", useDegrees = true), 1e-10)
        assertEquals(0.5, engine.evaluate("cos(-60)", useDegrees = true), 1e-10)
    }

    //biểu thức lượng giác

    @Test
    fun testTrigInExpression() {
        // sin(30)^2 + cos(30)^2 = 1 (đẳng thức lượng giác cơ bản)
        assertEquals(1.0, engine.evaluate("sin(30)^2+cos(30)^2", useDegrees = true), 1e-10)
    }

    @Test
    fun testTrigMultiply() {
        // 2 * sin(30) = 2 * 0.5 = 1.0
        assertEquals(1.0, engine.evaluate("2×sin(30)", useDegrees = true), 1e-10)
    }

}
