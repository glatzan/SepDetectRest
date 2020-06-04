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
import org.springframework.security.core.userdetails.UsernameNotFoundException
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
        private val securitySettings: SecuritySettings,
        private val authManager: AuthenticationManager,
        private val userRepository: UserRepository) : UsernamePasswordAuthenticationFilter() {

    /**
     * Setzte Adresse für Login
     */
    init {
        this.setFilterProcessesUrl(securitySettings.sign_in)
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
    override fun successfulAuthentication(req: HttpServletRequest,
                                          res: HttpServletResponse,
                                          chain: FilterChain?,
                                          auth: Authentication) {
        val userName = (auth.principal as org.springframework.security.core.userdetails.User).username;

        // setzte Session-UUID, falls sich der gleiche Account doppelt einloggt, wir der erste User ausgeloggt.
        var user = userRepository.findUserByName(userName).orElseThrow { UsernameNotFoundException("User not found!") }
        user.userToken = UUID.randomUUID().toString()
        user = userRepository.save(user)

        val token: String = JWT.create()
                .withSubject((auth.principal as org.springframework.security.core.userdetails.User).username)
                .withClaim("token", user.userToken)
                .withExpiresAt(Date(System.currentTimeMillis() + securitySettings.expiration_time))
                .sign(Algorithm.HMAC512(securitySettings.secret.toByteArray()))

        res.status = HttpServletResponse.SC_OK
        res.writer.write("{\"expires\" : \"${securitySettings.expiration_time}\", \"token\" : \"${securitySettings.token_prefix.toString() + token}\", \"user\" : ${Gson().toJson(user)}}")
    }

    /**
     * Gibt Fehlermeldung zurück, wenn die Authentifizierung nicht erfolgreich war
     */
    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException) {
        response.writer.write("Fehler: Benutzername oder Passwort stimmen nicht.")
    }
}
