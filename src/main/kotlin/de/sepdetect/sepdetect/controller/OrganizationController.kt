package de.sepdetect.sepdetect.controller

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.model.UserRole
import de.sepdetect.sepdetect.repository.OrganizationRepository
import de.sepdetect.sepdetect.service.impl.UserService
import de.sepdetect.sepdetect.util.HttpResponseStatus
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

/**
 * Kontroller für Organisationen.
 */
@RestController
class OrganizationController constructor(
        private val organizationRepository: OrganizationRepository,
        private val userService: UserService) {

    /**
     * Der Rest-Endpoint ermöglicht es eine Organisation zu erstellen. Der ausführende Benutzer muss die Rolle Admin inne haben.
     */
    @PostMapping("organization/add")
    fun addOrganization(@RequestBody organization: Organization): Organization {
        if (userService.getCurrentUser().role != UserRole.ADMIN)
            throw InsufficientAuthenticationException("Not Permitted!")
        return organizationRepository.save(organization)
    }

    /**
     * Endpunkt zum Löschen einer Organisation. Alle Benutzer die dieser Organisation zugeteilt sind werden zuerst aus der Organisation entfernt.
     * Der Aufrufende Benutzer muss die Rolle Admin innehaben.
     * TODO Personen entfernen
     */
    @DeleteMapping("organization/delete/{id}")
    fun deleteOrganization(@PathVariable id: Long): HttpResponseStatus {
        if (userService.getCurrentUser().role != UserRole.ADMIN)
            throw InsufficientAuthenticationException("Not Permitted!")
        organizationRepository.deleteById(id)
        return HttpResponseStatus("Ok", "Organization deleted (id: $id)")
    }
}
