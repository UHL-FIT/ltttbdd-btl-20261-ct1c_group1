package com.example.calpro.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberFormatterTest {

    @Test
    fun testIntegerWithThousandsSeparator() {
        // 433160505 → "433,160,505"
        assertEquals("433,160,505", formatNumber(433160505.0))
    }

    @Test
    fun testSmallInteger() {
        // 123 → "123"
        assertEquals("123", formatNumber(123.0))
    }

    @Test
    fun testZero() {
        // 0 → "0"
        assertEquals("0", formatNumber(0.0))
    }

    @Test
    fun testNegativeInteger() {
        assertEquals("-1,234", formatNumber(-1234.0))
    }

    @Test
    fun testDecimalNumber() {
        // 3.141590 → "3.14159"
        assertEquals("3.14159", formatNumber(3.141590))
    }

    @Test
    fun testDecimalWithThousandsSeparator() {
        assertEquals("1,234.56", formatNumber(1234.56))
    }

    @Test
    fun testVeryLargeNumber() {
        //>=10^10-> ký hiệu khoa học
        val result = formatNumber(1.5e-15)
        assertEquals("1.5E-15", result)
    }

    @Test
    fun testVerySmallNumber() {
        // <= 10^-10 → ký hiệu khoa học
        val result = formatNumber(1.5e-15)
        assertEquals("1.5E-15", result)
    }

    @Test
    fun testNaN() {
        assertEquals("NaN", formatNumber(Double.NaN))
    }

    @Test
    fun testInfinity() {
        assertEquals("Tràn số", formatNumber(Double.POSITIVE_INFINITY))
        assertEquals("Tràn số", formatNumber(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun testLargeIntegerBelowThreshold(){
        // dưới 10^10, vẫn hiển thị dạng số nguyên với dấu phẩy
        assertEquals("9,999,999,999", formatNumber(9999999999.0))
    }

    @Test
    fun testLargeIntegerAboveThreshold(){
        //trên 10^10, hiển thị dạng khoa học
        val result = formatNumber(10000000000.0)
        assertEquals("1E10", result)
    }
}