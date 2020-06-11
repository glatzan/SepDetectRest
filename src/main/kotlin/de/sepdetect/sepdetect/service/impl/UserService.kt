package de.sepdetect.sepdetect.service.impl

import de.sepdetect.sepdetect.model.User
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream


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

    /**
     * Erzeugt ein zufalls pw für neue Benutzter
     */
    companion object {
        @JvmStatic
        fun generateSecureRandomPassword(length: Int): String {
            val possibleCharacters: CharArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?".toCharArray()
            val randomStr: String = RandomStringUtils.random(length, 0, possibleCharacters.size - 1, false, false, possibleCharacters, SecureRandom())
            return randomStr
        }
    }

}
