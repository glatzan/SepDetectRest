package de.sepdetect.sepdetect.controller

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.model.UserRole
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.service.impl.PatientService
import de.sepdetect.sepdetect.service.impl.UserService
import de.sepdetect.sepdetect.util.JsonViews
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@CrossOrigin
@RestController
class PatientController constructor(
        private val patientRepository: PatientRepository,
        private val patientService: PatientService,
        private val userService: UserService) {

    @JsonView(JsonViews.PatientsOnly::class)
    @GetMapping("patients")
    fun getPatients(): List<Patient> {
        val user = userService.getCurrentUser()
        return if (user.role == UserRole.ADMIN) patientRepository.findAllByActive(true) else patientRepository.findAllByActiveAndOrganizationIn(true, userService.getCurrentUser().organization)
    }

    @JsonView(JsonViews.FullPatient::class)
    @GetMapping("patient/get/{id}")
    fun getPatient(@PathVariable id: Long): Patient {
        return patientRepository.findPatientByPersonId(id)
    }

    @PostMapping("patient/add")
    fun addPatient(@RequestBody patient: Patient): Patient {
        return patientRepository.save(patient)
    }

    @DeleteMapping("patient/delete/{id}")
    fun deletePatient(@PathVariable id: Long) {
        return patientRepository.deleteById(id)
    }

    @GetMapping("patient/active/{id}")
    fun togglePatient(@PathVariable id: Long, @RequestParam("active") active: Optional<Boolean>) {
        if (!active.isPresent)
            return
        patientService.togglePatient(id, active.get())
        // TODO set score to completed
    }

    @GetMapping("patient/search")
    fun searchPatients(@RequestParam("lastname") lastname: Optional<String>, @RequestParam("surname") surname: Optional<String>, @RequestParam("birthday") birthday: Optional<String>, @RequestParam("gender") gender: Optional<Char>): List<Patient> {
        if (!lastname.isPresent && !surname.isPresent && !birthday.isPresent && !gender.isPresent)
            return listOf<Patient>()

        var birthDayDate: LocalDate? = null
        if (birthday.isPresent) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            birthDayDate = LocalDate.parse(birthday.get(), formatter)
        }

        when {
            lastname.isPresent && surname.isPresent && gender.isPresent && birthDayDate != null -> {
                return patientRepository.findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCaseAndPersonGenderAndPersonBirthday(lastname.get(), surname.get(), gender.get(), birthDayDate)
            }
            lastname.isPresent && surname.isPresent && gender.isPresent -> {
                return patientRepository.findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCaseAndPersonGender(lastname.get(), surname.get(), gender.get())
            }
            lastname.isPresent && surname.isPresent && birthDayDate != null -> {
                return patientRepository.findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCaseAndPersonBirthday(lastname.get(), surname.get(), birthDayDate)
            }
            lastname.isPresent && surname.isPresent -> {
                return patientRepository.findPatientByPersonLastNameContainingIgnoreCaseAndPersonSurnameContainingIgnoreCase(lastname.get(), surname.get())
            }
            lastname.isPresent && birthDayDate != null -> {
                return patientRepository.findPatientByPersonLastNameContainingIgnoreCaseAndPersonBirthday(lastname.get(), birthDayDate)
            }
            lastname.isPresent && gender.isPresent -> {
                return patientRepository.findPatientByPersonLastNameContainingIgnoreCaseAndPersonGender(lastname.get(), gender.get())
            }
            surname.isPresent && birthDayDate != null -> {
                return patientRepository.findPatientByPersonSurnameContainingIgnoreCaseAndPersonBirthday(surname.get(), birthDayDate)
            }
            surname.isPresent && gender.isPresent -> {
                return patientRepository.findPatientByPersonSurnameContainingIgnoreCaseAndPersonGender(surname.get(), gender.get())
            }
            gender.isPresent && birthDayDate != null -> {
                return patientRepository.findPatientByPersonGenderAndPersonBirthday(gender.get(), birthDayDate)
            }
            else -> {
                return patientRepository.findPatientByPersonLastNameContainingIgnoreCaseOrPersonSurnameContainingIgnoreCaseOrPersonGenderOrPersonBirthday(lastname.orElse("-"), surname.orElse("-"), gender.orElse('X'), birthDayDate)
            }
        }
    }
}
