package com.example.calpro.data.repository

import com.example.calpro.data.local.dao.CalculationDao
import com.example.calpro.data.local.model.CalculationModel
import com.example.calpro.data.local.model.toEntity
import com.example.calpro.data.local.model.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CalculationRepository(
    private val dao: CalculationDao
) {
    fun getHistory(): Flow<List<CalculationModel>> =
        dao.getAllHistory().map { list -> list.map { it.toModel() } }

    suspend fun insertCalculation(model: CalculationModel) {
        dao.insert(model.toEntity())
        dao.keepOnlyLatest50()
    }

    suspend fun deleteCalculation(model: CalculationModel) {
        dao.delete(model.toEntity())
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
