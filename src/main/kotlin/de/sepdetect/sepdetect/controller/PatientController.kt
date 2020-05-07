package de.sepdetect.sepdetect.controller

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.model.UserRole
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.service.impl.UserService
import de.sepdetect.sepdetect.util.JsonViews
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class PatientController constructor(
        private val patientRepository: PatientRepository,
        private val userService: UserService) {

    @JsonView(JsonViews.PatientsOnly::class)
    @GetMapping("patients")
    fun getPatients(): List<Patient> {
        val user = userService.getCurrentUser()
        return if (user.role == UserRole.ADMIN) patientRepository.findAllByActive(true) else patientRepository.findAllByActiveAndOrganizationIn(true, userService.getCurrentUser().organization)
    }
}
