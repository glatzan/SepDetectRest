package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.model.Patient
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface PatientRepository : JpaRepository<Patient, Long> {
    fun findAllByActive(active: Boolean): List<Patient>

    fun findAllByActiveAndOrganizationIn(active: Boolean, organizations: List<Organization>): List<Patient>
}
