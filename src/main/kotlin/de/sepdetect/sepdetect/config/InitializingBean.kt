package de.sepdetect.sepdetect.config

import de.sepdetect.sepdetect.model.*
import de.sepdetect.sepdetect.repository.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class InitializingBean constructor(
        private val personRepository: PersonRepository,
        private val scoreValueRepository: ScoreValueRepository,
        private val scoreRepository: ScoreRepository,
        private val userRepository: UserRepository,
        private val organizationRepository: OrganizationRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val patientRepository: PatientRepository) : InitializingBean {

    @Value("\${sepdetect.debugdata}")
    var debugdata: Boolean = false

    override fun afterPropertiesSet() {
        println("Starting........")
        val user = userRepository.findUserByName("admin")
        if (user.isEmpty) {
            println("################# First start #################")
            println("Creating user:")
            println("Name: admin")
            println("password admin")

            val person = Person()
            person.lastName = "Admin"
            person.surname = "Admin"
            person.gender = 'D'

            val newUser = User()
            newUser.name = "admin"
            newUser.pw = bCryptPasswordEncoder.encode("admin")
            newUser.person = person

            var organization = Organization()
            organization.name = "admin"
            organization = organizationRepository.save(organization)

            newUser.organization.add(organization)
            userRepository.save(newUser)

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

                var score = Score()
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
        }
    }
}
