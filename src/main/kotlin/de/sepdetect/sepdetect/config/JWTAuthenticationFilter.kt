package de.sepdetect.sepdetect.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import de.sepdetect.sepdetect.model.User
import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.lang.Error
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter für die Authentifizierung. Nach erfolgreicher Authentifizierung wird ein JW-Token zurückgeben.
 */
class JWTAuthenticationFilter constructor(
        private val authManager: AuthenticationManager,
        private val userRepository: UserRepository) : UsernamePasswordAuthenticationFilter() {

    init {
        this.setFilterProcessesUrl(SecurityConstants.SIGN_IN)
    }

    /**
     * Parst die User-Credentials and gibt ein User-Objekt an den AuthManager weiter.
     */
    @Throws(AuthenticationServiceException::class)
    override fun attemptAuthentication(req: HttpServletRequest,
                                       res: HttpServletResponse): Authentication? {
        return try {
            val creds: User = ObjectMapper().readValue(req.inputStream, User::class.java)
            authManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            creds.name,
                            creds.pw,
                            ArrayList())
            )
        } catch (e: AuthenticationException) {
            unsuccessfulAuthentication(req, res, e)
            return null
        }
    }

    /**
     * Generiert einen JW-Token wenn der Benutzer sich erfolgreich authenifizieren konnte.
     */
    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(req: HttpServletRequest?,
                                          res: HttpServletResponse,
                                          chain: FilterChain?,
                                          auth: Authentication) {
        val userName = (auth.principal as org.springframework.security.core.userdetails.User).username;
        val token: String = JWT.create()
                .withSubject((auth.principal as org.springframework.security.core.userdetails.User).username)
                //.withClaim("token", (auth.principal as User).valToken)
                .withExpiresAt(Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.toByteArray()))
        val user = userRepository.findUserByName(userName)
        res.status = HttpServletResponse.SC_OK
        res.writer.write("{\"expires\" : \"${SecurityConstants.EXPIRATION_TIME}\", \"token\" : \"${SecurityConstants.TOKEN_PREFIX.toString() + token}\", \"user\" : ${Gson().toJson(user.get())}}")
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException) {
        response.writer.write("Fehler: Benutzername oder Passwort stimmen nicht.")
    }
}
