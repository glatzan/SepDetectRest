package de.sepdetect.sepdetect.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AccountStatusException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter zum authentifizieren des Benutzers. Verlangt einen JW-Token. Wenn dieser gültig ist darf die Anfrage ausgeführt werden.
 */
class JWTAuthorizationFilter constructor(
        private val securitySettings: SecuritySettings,
        authenticationManager: AuthenticationManager,
        private val userRepository: UserRepository) : BasicAuthenticationFilter(authenticationManager) {

    /**
     * Überprüft ob an Authentication-Header gesetzt wurde.
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(securitySettings.header_string)
        if (header == null || !header.startsWith(securitySettings.token_prefix)) {
            chain.doFilter(req, response)
            return
        }
        val authentication = getAuthentication(req)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, response)
    }

    /**
     * Überprüft den Authentication-Header und vergleicht den Session-Token. Wenn dieser nicht übereinstimmt, wird ein
     * Fehler zurückgegeben.
     */
    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(securitySettings.header_string)
        if (token != null) { // parse the token.
            val user = JWT.require(Algorithm.HMAC512(securitySettings.secret.toByteArray()))
                    .build()
                    .verify(token.replace(securitySettings.token_prefix, ""))

            val userName = user.subject ?: return null

            val uniqueToken = user.claims["token"]?.asString() ?: return null

            val dbUser = userRepository.findUserByName(userName).orElseThrow { UsernameNotFoundException("User not found!") }

            // vergleiche tokens, wenn sie nicht übereinstimmen wird ein Fehler geworfen
            if (uniqueToken != dbUser.userToken)
                throw AccountExpiredException("Token does not match!")

            return UsernamePasswordAuthenticationToken(dbUser, null, ArrayList())
        }
        return null
    }
}
