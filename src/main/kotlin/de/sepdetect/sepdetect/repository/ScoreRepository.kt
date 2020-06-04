package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Score
import de.sepdetect.sepdetect.model.ScoreValue
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

/**
 * ScoreRepository zur Verwaltung eines Scores in der Datenbank.
 */
interface ScoreRepository : JpaRepository<Score, Long> {

    /**
     * Findet alle Scores eines Patienten sortiert nach deren ID
     */
    fun findAllByPatientPersonIdOrderByListOrder(id: Long): List<Score>

    /**
     * Findet einen Score und lädt alle Values
     */
    @EntityGraph(value = "graph.scores.values")
    override fun findById(id: Long): Optional<Score>
}
