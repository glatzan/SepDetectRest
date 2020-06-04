package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.model.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

/**
 * UserRepository zur Verwaltung der Benutzer in der Datenbank.
 */
interface UserRepository : JpaRepository<User, Long> {

    /**
     * Findet einen Benutzer mittels Nachahmen und lädt alle Organisationen.
     */
    @EntityGraph(value = "graph.user.organization")
    fun findUserByName(name: String): Optional<User>

    /**
     * Findet einen Benutzer mittels seiner ID und lädt alle Organisationen.
     */
    @EntityGraph(value = "graph.user.organization")
    fun findByPersonId(id: Long): Optional<User>

    /**
     * Gibt eine Liste von Benutzer, die der gegeben Organisation angehören, zurück.
     */
    fun findAllByOrganization(organization: Organization): List<User>
}
