package com.example.calpro.data.local.model

import com.example.calpro.data.local.entity.CalculationEntity

data class CalculationModel(
    val id: Int = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun CalculationEntity.toModel() = CalculationModel(
    id = id,
    expression = expression,
    result = result,
    timestamp = timestamp
)

fun CalculationModel.toEntity() = CalculationEntity(
    id = id,
    expression = expression,
    result = result,
    timestamp = timestamp
)


