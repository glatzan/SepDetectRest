package de.sepdetect.sepdetect

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Spring Bott Main Class
 */
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
@EnableConfigurationProperties
class SepdetectApplication {

    /**
     * Password encrypter
     */
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}

fun main(args: Array<String>) {
    runApplication<SepdetectApplication>(*args)
}
