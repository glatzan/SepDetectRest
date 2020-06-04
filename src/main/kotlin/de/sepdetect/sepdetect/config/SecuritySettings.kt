package de.sepdetect.sepdetect.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Security Settings, können in der application.yml gesetzt werden (sepdetect.securitySettings).
 */
@ConfigurationProperties(prefix = "sepdetect.securitySettings")
class SecuritySettings {
    /**
     * Secret for das JW-Token
     */
    var secret = "SecretKeyToGenJWTs"

    /**
     * Expiration Time
     */
    var expiration_time: Long = 864000000

    /**
     * Token prefix
     */
    var token_prefix = "Bearer "

    /**
     * Header String
     */
    var header_string = "Authorization"

    /**
     * Login URL
     */
    var sign_in = "/login"
}