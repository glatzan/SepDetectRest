package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    @EntityGraph(value = "graph.user.organization")
    fun findUserByName(name: String): User?
}
