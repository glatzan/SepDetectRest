package de.sepdetect.sepdetect.service.impl

import de.sepdetect.sepdetect.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class UserService {
    fun getCurrentUser(): User {
        val user = SecurityContextHolder.getContext().authentication
        return (user.principal as User)
    }
}
