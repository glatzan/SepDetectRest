package de.sepdetect.sepdetect.repository.custom

import de.sepdetect.sepdetect.model.Patient
import java.time.LocalDate

/**
 * PatientRepositoryCustom ist eine eigene Erweiterung des JPA-Repositories um eine dynamische Patientensuche zu ermöglichen.
 */
interface PatientRepositoryCustom {
    /**
     * Findet einen Patienten mittel der übergebenen Argumente. Wird null übergeben, wird das Argument ignoriert.
     */
    fun findPatientByArguments(lastname: String?, surname: String?, birthday: LocalDate?, gender: Char?): List<Patient>
}