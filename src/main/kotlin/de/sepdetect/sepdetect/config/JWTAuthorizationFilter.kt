package de.sepdetect.sepdetect.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter zum authenifizieren des Benutzers. Verlangt einen JWT. Wenn dieser gültig ist darf die Anfrage ausgeführt werden.
 */
class JWTAuthorizationFilter constructor(
        authenticationManager: AuthenticationManager,
        private val userRepository: UserRepository) : BasicAuthenticationFilter(authenticationManager) {


    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(SecurityConstants.HEADER_STRING)
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, response)
            return
        }
        val authentication = getAuthentication(req)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(SecurityConstants.HEADER_STRING)
        if (token != null) { // parse the token.
            val user = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.toByteArray()))
                    .build()
                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))

            val userName = user.subject ?: return null

            val dbUser = userRepository.findUserByName(userName)

            if (!dbUser.isPresent)
                return null

            return UsernamePasswordAuthenticationToken(dbUser.get(), null, ArrayList())
        }
        return null
    }
}
