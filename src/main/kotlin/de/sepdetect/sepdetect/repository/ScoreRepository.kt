package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Score
import de.sepdetect.sepdetect.model.ScoreValue
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ScoreRepository : JpaRepository<Score, Long> {

    fun findAllByPatientPersonIdOrderByListOrder(id: Long): List<Score>

    @EntityGraph(value = "graph.scores.values")
    override fun findById(id: Long): Optional<Score>
}
