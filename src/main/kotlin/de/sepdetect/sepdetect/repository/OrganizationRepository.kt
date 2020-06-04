package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Organization
import org.springframework.data.jpa.repository.JpaRepository

/**
 * OrganizationRepository zur Verwaltung von Organisationen in der Datenbank.
 */
interface OrganizationRepository : JpaRepository<Organization,Long> {
}
