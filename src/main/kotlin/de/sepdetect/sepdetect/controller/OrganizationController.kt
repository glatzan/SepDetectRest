package de.sepdetect.sepdetect.controller

import de.sepdetect.sepdetect.model.Organization
import org.springframework.web.bind.annotation.*

@RestController
class OrganizationController {

    @PostMapping("organization/add")
    fun addOrganization(@RequestBody organization: Organization) {

    }

    @DeleteMapping("organization/delete/{id}")
    fun deleteOrganization(@PathVariable id: Long){

    }
}
