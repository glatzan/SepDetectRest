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
import javax.persistence.EntityNotFoundException


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
        return patientRepository.findPatientByPersonId(id).orElseThrow { throw EntityNotFoundException("Patient not found!") }
    }

    @JsonView(JsonViews.FullPatient::class)
    @PostMapping("patient/add")
    fun addPatient(@RequestBody patient: Patient): Patient {
        patient.personId = 0
        patient.person.id = 0

        if(patient.organization == null)
            throw IllegalStateException("Patient needs an associated organization!")

        return patientRepository.save(patient)
    }

    @JsonView(JsonViews.FullPatient::class)
    @PutMapping("patient/edit")
    fun edit(@RequestBody patient: Patient): Patient {
        if(patient.personId == 0L)
            throw IllegalStateException("Provide patient id!")

        // setzte person id wenn diese nicht gesetzt wurde
        if (patient.person.id == 0L) {
            patient.person.id = patient.personId
        }

        if(patient.organization == null)
            throw IllegalStateException("Patient needs an associated organization!")

        return patientRepository.save(patient)
    }

    @DeleteMapping("patient/delete/{id}")
    fun deletePatient(@PathVariable id: Long): String {
        patientRepository.deleteById(id)
        return "OK: Patient deleted (id: $id)"
    }

    @GetMapping("patient/active/{id}")
    fun togglePatient(@PathVariable id: Long, @RequestParam("active") active: Optional<Boolean>): String {
        if (!active.isPresent)
            return "Error"
        patientService.togglePatient(id, active.get())
        // TODO set score to completed

        return "OK: ${if (active.get()) "active" else "inactive"}"
    }

    @JsonView(JsonViews.PatientsOnly::class)
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
