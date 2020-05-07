package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Person
import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository : JpaRepository<Person,Long> {
}
