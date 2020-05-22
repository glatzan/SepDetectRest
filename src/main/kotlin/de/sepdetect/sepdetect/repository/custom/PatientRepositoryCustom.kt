package de.sepdetect.sepdetect.repository.custom

import de.sepdetect.sepdetect.model.Patient
import java.time.LocalDate

interface PatientRepositoryCustom {
    fun findPatientByArguments(lastname: String?, surname: String?, birthday: LocalDate?, gender: Char?): List<Patient>
}