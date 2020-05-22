package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.repository.custom.PatientRepositoryCustom
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.*

interface PatientRepository : JpaRepository<Patient, Long>, PatientRepositoryCustom {
    fun findAllByActive(active: Boolean): List<Patient>

    fun findAllByActiveAndOrganizationIn(active: Boolean, organizations: List<Organization>): List<Patient>

    @EntityGraph(value = "graph.patient.scores.values")
    fun findPatientByPersonId(id: Long): Optional<Patient>

}
