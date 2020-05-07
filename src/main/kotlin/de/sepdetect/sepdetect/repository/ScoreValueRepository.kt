package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.ScoreValue
import org.springframework.data.jpa.repository.JpaRepository

interface ScoreValueRepository : JpaRepository<ScoreValue, Long> {
}
