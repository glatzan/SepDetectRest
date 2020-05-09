package de.sepdetect.sepdetect.service.impl

import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserServiceImpl constructor(
        private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails {
        val user = userRepository.findUserByName(userName).orElseThrow { throw UsernameNotFoundException(userName) }
        return User(userName, user.pw, listOf())
    }
}
