package de.sepdetect.sepdetect.repository

import de.sepdetect.sepdetect.model.Organization
import org.springframework.data.jpa.repository.JpaRepository

interface OrganizationRepository : JpaRepository<Organization,Long> {
}
