package de.sepdetect.sepdetect.controller

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.model.UserRole
import de.sepdetect.sepdetect.repository.OrganizationRepository
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.service.impl.UserService
import de.sepdetect.sepdetect.util.HttpResponseStatus
import de.sepdetect.sepdetect.util.JsonViews
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.persistence.EntityNotFoundException

/**
 * Kontroller zum erstellen, manipulieren und löschen von Patienten.
 */
@CrossOrigin
@RestController
class PatientController constructor(
        private val patientRepository: PatientRepository,
        private val organizationRepository: OrganizationRepository,
        private val userService: UserService) {

    /**
     * Gibt eine Liste mit allen aktiven Patienten zurück, auf die der Benutzter zugreifen darf. Ist der Benutzer ein Admin
     * werden alle aktiven Patienten zurückgegeben.
     */
    @JsonView(JsonViews.PatientsOnly::class)
    @GetMapping("patients")
    fun getPatients(): List<Patient> {
        val user = userService.getCurrentUser()
        return if (user.role == UserRole.ADMIN) patientRepository.findAllByActive(true) else patientRepository.findAllByActiveAndOrganizationIn(true, userService.getCurrentUser().organization)
    }

    /**
     * Endpunkt gibt den Patienten und alle Score-Werte mit der entsprechenden Patienten-ID als JSON-Objekt zurück.
     * Wirt einen Fehler wenn der Benutzer nicht auf den Patienten zugreifen darf.
     */
    @JsonView(JsonViews.FullPatient::class)
    @GetMapping("patient/get/{id}")
    fun getPatient(@PathVariable id: Long): Patient {
        val pat = patientRepository.findPatientByPersonId(id).orElseThrow { throw EntityNotFoundException("Patient not found!") }
        val user = userService.getCurrentUser()
        if (user.role != UserRole.ADMIN && !user.organization.any { it.id == pat.organization?.id })
            throw EntityNotFoundException("Access denied!")

        return patientRepository.findPatientByPersonId(id).orElseThrow { throw EntityNotFoundException("Patient not found!") }
    }

    /**
     * Ermöglicht es einen neuen Patienten zu erstellen. Zum Erstellen eines neuen Patienten muss dem Endpunkt ein Patienten-Objekt übergeben werden.
     * Der Patient muss zwingend einer Organisation zugeordnet sein, sonst wird ein Fehler geworfen.
     */
    @JsonView(JsonViews.FullPatient::class)
    @PostMapping("patient/add")
    fun addPatient(@RequestBody patient: Patient): Patient {
        patient.personId = 0
        patient.person.id = 0
        patient.active = true

        if (patient.organization == null)
            throw IllegalStateException("Patient needs an associated organization!")

        return patientRepository.save(patient)
    }

    /**
     *  Ermöglicht des bearbeiten von Patienten.
     */
    @JsonView(JsonViews.FullPatient::class)
    @PutMapping("patient/edit")
    fun edit(@RequestBody patient: Patient): Patient {
        if (patient.personId == 0L)
            throw IllegalStateException("Provide patient id!")

        val dbPatient = patientRepository.findPatientByPersonId(patient.personId).orElseThrow { throw EntityNotFoundException("Patient not found!") }
        dbPatient.piz = patient.piz
        dbPatient.room = patient.room
        dbPatient.person.gender = patient.person.gender
        dbPatient.person.birthday = patient.person.birthday
        dbPatient.person.surname = patient.person.surname
        dbPatient.person.lastName = patient.person.lastName

        if (patient.organization != null && dbPatient.organization!!.id != patient.organization!!.id) {
            val newOrg = organizationRepository.findById(patient.organization!!.id).orElseThrow { throw IllegalStateException("Patient needs an associated an existing organization!") }
            dbPatient.organization = newOrg
        }

        return patientRepository.save(dbPatient)
    }

    /**
     *  Löscht den angebenden Patienten aus der Datenbank. Alle SOFA-Verläufe werden gemeinsam mit dem Patienten gelöscht.
     *  Der ausführende Benutzter muss die Rolle Admin inne haben.
     */
    @DeleteMapping("patient/delete/{id}")
    fun deletePatient(@PathVariable id: Long): HttpResponseStatus {
        if (userService.getCurrentUser().role != UserRole.ADMIN)
            throw InsufficientAuthenticationException("Not Permitted!")

        val pat = patientRepository.findPatientByPersonId(id).orElseThrow { throw EntityNotFoundException("Patient not found!") }
        patientRepository.delete(pat)
        return HttpResponseStatus("OK", "Patient deleted (id: $id)")
    }

    /**
     * Aktiviert oder deaktiviert einen Patienten.
     */
    @GetMapping("patient/active/{patientID}")
    fun togglePatient(@PathVariable patientID: Long, @RequestParam("active") active: Optional<Boolean>): HttpResponseStatus {
        if (!active.isPresent)
            throw IllegalArgumentException("Provide active argument")

        val patient = patientRepository.findPatientByPersonId(patientID).orElseThrow { throw EntityNotFoundException("Patient not found!") }

        if (patient.scores.any { !it.completed })
            throw java.lang.IllegalStateException("Scores have to be completed")

        patient.active = active.get()

        patientRepository.save(patient)
        return HttpResponseStatus("OK", "Set Patient to ${if (active.get()) "active" else "inactive"}")
    }

    /**
     * Sucht Patienten anhand von bestimmten Kriterien. Alle Parameter sind optional. Wird kein Parameter angeben wird ein
     * Fehler ausgegeben.
     */
    @JsonView(JsonViews.PatientsOnly::class)
    @GetMapping("patient/search")
    fun searchPatients(@RequestParam("lastname") lastname: Optional<String>, @RequestParam("surname") surname: Optional<String>, @RequestParam("birthday") birthday: Optional<String>, @RequestParam("gender") gender: Optional<Char>): List<Patient> {
        if (!lastname.isPresent && !surname.isPresent && !birthday.isPresent && !gender.isPresent)
            return listOf<Patient>()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val lastName = if (lastname.isPresent && lastname.get().isNotEmpty()) String(Base64.getDecoder().decode(lastname.get())).toLowerCase() else null
        val surName = if (surname.isPresent && surname.get().isNotEmpty()) String(Base64.getDecoder().decode(surname.get())).toLowerCase() else null
        val birthday = if (birthday.isPresent && birthday.get().isNotEmpty()) LocalDate.parse(String(Base64.getDecoder().decode(birthday.get())), formatter) else null
        val gender = if (gender.isPresent) gender.get() else null

        return patientRepository.findPatientByArguments(lastName, surName, birthday, gender);
    }
}
