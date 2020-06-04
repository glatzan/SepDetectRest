package de.sepdetect.sepdetect.config

import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*


/**
 * Security Config
 * Beispiel: https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/
 */
@EnableWebSecurity
class SecurityConfig @Autowired constructor(
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val userRepository: UserRepository,
        private val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {

    var securitySettings: SecuritySettings = SecuritySettings()

    /**
     * Setzte HTTP Rechte
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable().authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilter(JWTAuthenticationFilter(securitySettings, authenticationManager(), userRepository))
                .addFilter(JWTAuthorizationFilter(securitySettings, authenticationManager(), userRepository)) // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        println(securitySettings.expiration_time)
    }

    /**
     * Setzte UserDetailsService
     */
    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder)
    }

    /**
     * Setzte Cors
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = Arrays.asList("*")
        configuration.allowedMethods = Arrays.asList("*")
        configuration.allowedHeaders = Arrays.asList("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}
