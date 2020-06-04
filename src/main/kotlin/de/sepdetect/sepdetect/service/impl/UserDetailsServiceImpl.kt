package de.sepdetect.sepdetect.service.impl

import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Service erlaubt das laden eines Benutzers aus der Datenbank. Wird zur Authentifizierung benötigt.
 */
@Service
class UserServiceImpl constructor(
        private val userRepository: UserRepository) : UserDetailsService {

    /**
     * Lädt einen user aus der Datenbank und gibt ein UserDetails Objekt zurück. Wenn der Benutzer nicht gefunden wird, wird eine
     * UsernameNotFoundException geworfen.
     */
    override fun loadUserByUsername(userName: String): UserDetails {
        val user = userRepository.findUserByName(userName).orElseThrow { throw UsernameNotFoundException(userName) }
        return User(userName, user.pw, listOf())
    }
}
