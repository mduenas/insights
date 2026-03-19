package com.markduenas.insights.domain.usecase

import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.repository.InsightRepository
import kotlinx.coroutines.flow.firstOrNull

private val STOPWORDS = setOf(
    "a", "an", "the", "and", "or", "but", "in", "on", "at", "to", "for",
    "of", "with", "by", "is", "are", "was", "be", "it", "this", "that", "i"
)

class FindMatchingInsightUseCase(
    private val insightRepository: InsightRepository
) {
    /**
     * Returns up to [maxResults] common insights that potentially match the given
     * personal insight based on keyword overlap in title and body.
     */
    suspend operator fun invoke(insight: Insight, maxResults: Int = 5): List<Insight> {
        val keywords = tokenize("${insight.title} ${insight.body}")
        if (keywords.isEmpty()) return emptyList()

        // Use the top keyword as the search query against Firestore
        val topKeyword = keywords.first()
        val candidates = insightRepository.searchInsights(topKeyword).firstOrNull() ?: emptyList()

        return candidates
            .filter { it.id != insight.id }
            .map { candidate ->
                val candidateKeywords = tokenize("${candidate.title} ${candidate.body}")
                val overlap = keywords.intersect(candidateKeywords).size
                candidate to overlap
            }
            .filter { (_, score) -> score > 0 }
            .sortedByDescending { (_, score) -> score }
            .take(maxResults)
            .map { (insight, _) -> insight }
    }

    private fun tokenize(text: String): Set<String> =
        text.lowercase()
            .split(Regex("[^a-z0-9]+"))
            .filter { it.length > 2 && it !in STOPWORDS }
            .toSet()
}
