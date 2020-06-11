package de.sepdetect.sepdetect.config

import de.sepdetect.sepdetect.model.*
import de.sepdetect.sepdetect.repository.*
import de.sepdetect.sepdetect.service.impl.MailService
import de.sepdetect.sepdetect.service.impl.UserService
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * afterPropertiesSet() wird nach dem Programmstart ausgeführt. Es wird überprüft ob der Benutzer
 * admin angelegt wurde. Existiert dieser nicht, wird er angelegt.
 */
@Component
@ConfigurationProperties(prefix = "sepdetect.default")
class InitializingBean constructor(
        private val personRepository: PersonRepository,
        private val mailService: MailService,
        private val scoreValueRepository: ScoreValueRepository,
        private val scoreRepository: ScoreRepository,
        private val userRepository: UserRepository,
        private val organizationRepository: OrganizationRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val patientRepository: PatientRepository) : InitializingBean {

    /**
     * Wenn wahr, werden Dummybenutzer angelegt. Kann in der application.yml gesetzt werden.
     */
    var debugdata: Boolean = false

    var adminMails = mutableListOf<String>()

    /**
     * Wird nach dem Programmstart ausgeführt. Erstellt den Benutzer Admin, wenn dieser nicht exisitiert.
     */
    override fun afterPropertiesSet() {
        println("Starting........")
        val user = userRepository.findUserByName("admin")
        if (!user.isPresent) {
            val pw = UserService.generateSecureRandomPassword(15)
            println("################# First start #################")
            println("Creating user:")
            println("Name: admin")
            println("password $pw")

            // erstelle neue Person
            val person = Person()
            person.lastName = "Admin"
            person.surname = "Admin"
            person.gender = 'D'

            // erstelle neuen User Admin
            val newUser = User()
            newUser.name = "admin"
            newUser.pw = bCryptPasswordEncoder.encode(pw)
            newUser.person = person
            newUser.role = UserRole.ADMIN

            // organisation admin
            var organization = Organization()
            organization.name = "admin"
            organization = organizationRepository.save(organization)

            newUser.organization.add(organization)
            userRepository.save(newUser)

            // wenn debugdata true, werden zwei Dummybenutzer angelegt
            if (debugdata) {
                println("")
                println("Creating dummy Patient: Max Mustermann")
                var p = Patient()
                p.person = Person()
                p.person.lastName = "Mustermann"
                p.person.surname = "Max"
                p.person.birthday = LocalDate.of(1988, 12, 3)
                p.person.gender = 'M'
                p.piz = "12345678"
                p.active = true
                p.room = "Zimmer 1"
                p.organization = organization

                patientRepository.save(p)

                println("Creating dummy Patient: Elise Mustermann")
                p = Patient()
                p.person = Person()
                p.person.lastName = "Mustermann"
                p.person.surname = "Elise"
                p.person.birthday = LocalDate.of(1989, 3, 20)
                p.person.gender = 'W'
                p.piz = "12345679"
                p.active = true
                p.room = "Zimmer 2"
                p.organization = organization

                p = patientRepository.save(p)

                val score = Score()
                score.patient = p
                score.startDate = LocalDate.now()
                p.scores.add(score)

                var scoreValue = ScoreValue()
                scoreValue.pao = 1
                scoreValue.krea = 2
                scoreValue.coagulation = 3
                scoreValue.liver = 0
                scoreValue.map = 2
                scoreValue.gcs = 2
                scoreValue.total = 10
                scoreValue.date = LocalDate.now()
                scoreValue.score = score
                score.values.add(scoreValue)

                scoreValue = ScoreValue()
                scoreValue.pao = 2
                scoreValue.krea = 2
                scoreValue.coagulation = 3
                scoreValue.liver = 0
                scoreValue.map = 0
                scoreValue.gcs = 1
                scoreValue.total = 8
                scoreValue.date = LocalDate.now()
                scoreValue.score = score
                score.values.add(scoreValue)

                scoreValue = ScoreValue()
                scoreValue.pao = 1
                scoreValue.krea = 2
                scoreValue.coagulation = 3
                scoreValue.liver = 0
                scoreValue.map = 2
                scoreValue.gcs = 2
                scoreValue.total = 10
                scoreValue.date = LocalDate.now()
                scoreValue.score = score
                score.values.add(scoreValue)

                p = patientRepository.save(p)

            }

            println("###############################################")

            mailService.sendPasswordToUser(newUser, pw, this.adminMails)
        }

    }
}
