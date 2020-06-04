package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.ScoreValue
import org.springframework.data.jpa.repository.JpaRepository

/**
 * ScoreValueRepository zur Verwaltung von ScoreValues in der Datenbank.
 */
interface ScoreValueRepository : JpaRepository<ScoreValue, Long> {
}
