package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.repository.custom.PatientRepositoryCustom
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.*

/**
 * PatientRepository zur Verwaltung von Patienten in der Datenbank.
 */
interface PatientRepository : JpaRepository<Patient, Long>, PatientRepositoryCustom {
    /**
     * Findet alle aktiven Patienten in der Datenbank
     */
    fun findAllByActive(active: Boolean): List<Patient>

    /**
     * Findet alle aktiven Patienten die in einer der übergebene Organisationen sind.
     */
    fun findAllByActiveAndOrganizationIn(active: Boolean, organizations: List<Organization>): List<Patient>

    /**
     * Findet einen Patienten mittels seiner ID und lädt alle Scores/ScoreValues
     */
    @EntityGraph(value = "graph.patient.scores.values")
    fun findPatientByPersonId(id: Long): Optional<Patient>

}
