package com.markduenas.insights.presentation.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.repository.InsightRepository
import kotlinx.coroutines.flow.*

class InsightDetailScreenModel(
    private val insightRepository: InsightRepository,
    val insightId: String
) : ScreenModel {

    val insight: StateFlow<Insight?> = insightRepository
        .getInsightById(insightId)
        .catch { emit(null) }
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)
}
