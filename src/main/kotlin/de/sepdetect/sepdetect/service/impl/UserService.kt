package de.sepdetect.sepdetect.service.impl

import de.sepdetect.sepdetect.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


/**
 * User Service, stellt für andere Services den aktuellen Benutzer zur Verfügung.
 */
@Service
class UserService {
    /**
     * Lädt den aktuellen Benutzer als dem Security Context des Requests
     */
    fun getCurrentUser(): User {
        val user = SecurityContextHolder.getContext().authentication
        return (user.principal as User)
    }
}
