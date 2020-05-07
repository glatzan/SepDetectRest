package de.sepdetect.sepdetect.service.impl

import de.sepdetect.sepdetect.repository.PatientRepository
import org.springframework.stereotype.Service

@Service
class PatientService constructor(
        private val patientRepository: PatientRepository) {

    fun togglePatient(patientID: Long, active: Boolean) {
        val patient = patientRepository.findById(patientID)
        if (patient.isPresent) {
            patient.get().active = active
            patientRepository.save(patient.get())
        }
    }
}
