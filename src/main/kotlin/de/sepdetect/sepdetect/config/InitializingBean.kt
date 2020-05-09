package de.sepdetect.sepdetect.config

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.model.Person
import de.sepdetect.sepdetect.model.User
import de.sepdetect.sepdetect.repository.OrganizationRepository
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.repository.PersonRepository
import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class InitializingBean constructor(
        private val personRepository: PersonRepository,
        private val userRepository: UserRepository,
        private val organizationRepository: OrganizationRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val patientRepository: PatientRepository) : InitializingBean {


    override fun afterPropertiesSet() {
        println("Starting..")
        val user = userRepository.findUserByName("admin1")
        if (user == null) {
            println("creating new user")
            val person = Person()
            person.lastName = "Admin"
            person.surname = "Admin"
            person.gender = 'D'

            val newUser = User()
            newUser.name = "admin1"
            newUser.pw = bCryptPasswordEncoder.encode("admin1")
            newUser.person = person

            var organization = Organization()
            organization.name = "Test"
            organization = organizationRepository.save(organization)

            newUser.organization.add(organization)

            userRepository.save(newUser)
        }
    }
}
