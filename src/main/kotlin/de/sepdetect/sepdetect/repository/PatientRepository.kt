package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.model.Patient
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface PatientRepository : JpaRepository<Patient, Long> {
    fun findAllByActive(active: Boolean): List<Patient>

    fun findAllByActiveAndOrganizationIn(active: Boolean, organizations: List<Organization>): List<Patient>

    @EntityGraph(value = "graph.patient.scores.values")
    fun findPatientByPersonId(id: Long): Patient

    fun findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCase(lastName: String, surname: String): List<Patient>

    fun findPatientByPersonLastNameContainingIgnoreCaseAndPersonBirthday(lastName: String, birthday: LocalDate): List<Patient>

    fun findPatientByPersonLastNameContainingIgnoreCaseAndPersonGender(lastName: String, gender: Char): List<Patient>

    fun findPatientByPersonSurnameContainingIgnoreCaseAndPersonBirthday(surname: String, birthday: LocalDate): List<Patient>

    fun findPatientByPersonSurnameContainingIgnoreCaseAndPersonGender(surname: String, gender: Char): List<Patient>

    fun findPatientByPersonGenderAndPersonBirthday(gender: Char, birthday: LocalDate): List<Patient>

    fun findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCaseAndPersonBirthday(lastName: String, surname: String, birthday: LocalDate): List<Patient>

    fun findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCaseAndPersonGender(lastName: String, surname: String, gender: Char): List<Patient>

    fun findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCaseAndPersonGenderAndPersonBirthday(lastName: String, surname: String, gender: Char, birthday: LocalDate?): List<Patient>

    fun findPatientByPersonLastNameContainingIgnoreCaseOrPersonSurnameContainingIgnoreCaseOrPersonGenderOrPersonBirthday(lastName: String?, surname: String?, gender: Char?, birthday: LocalDate?): List<Patient>
}
