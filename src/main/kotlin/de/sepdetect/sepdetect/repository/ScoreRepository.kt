package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Score
import org.springframework.data.jpa.repository.JpaRepository

interface ScoreRepository : JpaRepository<Score,Long> {

    fun findAllByPatientPersonIdOrderByListOrder(id : Long) : List<Score>
}
