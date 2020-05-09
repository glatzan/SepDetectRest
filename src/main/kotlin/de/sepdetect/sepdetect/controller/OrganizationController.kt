package de.sepdetect.sepdetect.controller

import de.sepdetect.sepdetect.model.Organization
import de.sepdetect.sepdetect.repository.OrganizationRepository
import org.springframework.web.bind.annotation.*

@RestController
class OrganizationController constructor(
        private var organizationRepository: OrganizationRepository) {

    @PostMapping("organization/add")
    fun addOrganization(@RequestBody organization: Organization): Organization {
        return organizationRepository.save(organization)
    }

    @DeleteMapping("organization/delete/{id}")
    fun deleteOrganization(@PathVariable id: Long) : String {
        organizationRepository.deleteById(id)
        return "OK: Organization deleted (id: $id)"
    }
}
