package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {

    @EntityGraph(value = "graph.user.organization")
    fun findUserByName(name: String): Optional<User>

    @EntityGraph(value = "graph.user.organization")
    fun findByPersonId(id :Long) : Optional<User>
}
