package de.sepdetect.sepdetect.controller

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.model.User
import de.sepdetect.sepdetect.model.UserRole
import de.sepdetect.sepdetect.repository.OrganizationRepository
import de.sepdetect.sepdetect.repository.UserRepository
import de.sepdetect.sepdetect.service.impl.UserService
import de.sepdetect.sepdetect.util.HttpResponseStatus
import de.sepdetect.sepdetect.util.JsonViews
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.EntityNotFoundException

/**
 * Kontroller stellt Rest-Endpoints für die Manipulation von Benutzern zur Verfügung.
 */
@RestController
class UserController constructor(
        private val userRepository: UserRepository,
        private val organizationRepository: OrganizationRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val userService: UserService) {

    /**
     * Ermöglicht das Erstellen von neuen Benutzern. Muss von einem Administrator ausgeführt werden.
     */
    @PostMapping("user/add")
    @JsonView(JsonViews.UserView::class)
    fun addUser(@RequestBody user: User): User {
        if (userService.getCurrentUser().role != UserRole.ADMIN)
            throw InsufficientAuthenticationException("Not Permitted!")
        user.personId = 0
        user.person.id = 0
        user.pw = bCryptPasswordEncoder.encode(user.pw)
        return userRepository.save(user)
    }

    /**
     * Schnittstelle zum Bearbeiten eines Benutzers. Muss von einem Administrator oder dem betreffenden Benutzer ausgeführt werden. Hiermit kann auch ein
     * neues Password gesetzt werden.
     */
    @PutMapping("user/edit")
    @JsonView(JsonViews.UserView::class)
    fun editUser(@RequestBody user: User): User {
        if (user.personId == 0L)
            throw IllegalStateException("User patient id!")

        // setzte person id wenn diese nicht gesetzt wurde
        if (user.person.id == 0L) {
            user.person.id = user.personId
        }

        if (userService.getCurrentUser().role != UserRole.ADMIN || user.personId != userService.getCurrentUser().personId)
            throw InsufficientAuthenticationException("Not Permitted!")

        // wenn passwort feld leer ist, wird das pw aus der db übernommen
        if (user.pw.isEmpty()) {
            val dbUser = userRepository.findByPersonId(user.personId).orElseThrow { throw EntityNotFoundException("User not found!") }
            user.pw = dbUser.pw
        } else if (!user.pw.startsWith("\$2a")) {
            user.pw = bCryptPasswordEncoder.encode(user.pw)
        }

        return userRepository.save(user)
    }

    /**
     * Gibt den User mit der angegeben ID zurück
     */
    @GetMapping("user/get/{id}")
    @JsonView(JsonViews.UserView::class)
    fun getUser(@PathVariable id: Long): User {
        return userRepository.findByPersonId(id).orElseThrow { throw EntityNotFoundException("User not found") }
    }

    /**
     * Ermöglicht das Löschen von Benutzern. Muss von einem Administrator ausgeführt werden.
     */
    @DeleteMapping("user/delete/{id}")
    fun deleteUser(@PathVariable id: Long): HttpResponseStatus {
        if (userService.getCurrentUser().role != UserRole.ADMIN)
            throw InsufficientAuthenticationException("Not Permitted!")
        userRepository.deleteById(id)
        return HttpResponseStatus("OK", "User deleted (id: $id)")
    }

    /**
     * Ermöglicht es einem Benutzer eine Organisation hinzuzufügen. Muss von einem Administrator ausgeführt werden.
     */
    @GetMapping("user/organization/add/{id}")
    @JsonView(JsonViews.UserView::class)
    fun addOrganization(@PathVariable id: Long, @RequestParam("organizationID") organizationID: Optional<Long>): User {
        if (userService.getCurrentUser().role != UserRole.ADMIN)
            throw InsufficientAuthenticationException("Not Permitted!")

        if (!organizationID.isPresent)
            throw IllegalArgumentException("Error: No organization id provided")

        val organization = organizationRepository.findById(organizationID.get()).orElseThrow { throw EntityNotFoundException("Organization not found") }
        val user = userRepository.findByPersonId(id).orElseThrow { throw EntityNotFoundException("User not found") }

        if (user.organization.any { it.id == organization.id })
            throw IllegalArgumentException("User is already added to organization")

        user.organization.add(organization)

        return userRepository.save(user)
    }

    /**
     * Dieser Endpunkt ermöglicht es einen Benutzer aus einer Organisation zu entfernen. Der Aufrufende Benutzer muss die Rolle Admin innehaben.
     */
    @GetMapping("user/organization/remove/{id}")
    @JsonView(JsonViews.UserView::class)
    fun removeOrganization(@PathVariable id: Long, @RequestParam("organizationID") organizationID: Optional<Long>): User {
        if (userService.getCurrentUser().role != UserRole.ADMIN)
            throw InsufficientAuthenticationException("Not Permitted!")

        if (!organizationID.isPresent)
            throw IllegalArgumentException("Error: No organization id provided")

        val user = userRepository.findByPersonId(id).orElseThrow { throw EntityNotFoundException("User not found") }

        val organization = user.organization.firstOrNull { it.id == organizationID.get() }
                ?: throw IllegalArgumentException("User can not be removed, is not known in organization")

        user.organization.remove(organization)

        return userRepository.save(user)
    }
}
