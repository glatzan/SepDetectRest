package de.sepdetect.sepdetect.controller

import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.model.User
import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UserController constructor(
        private val userRepository: UserRepository) {

    @PostMapping("user/add")
    fun addPatient(@RequestBody user: User): User {
        return userRepository.save(user)
    }

    @PutMapping("user/add")
    fun eidtPatient(@RequestBody user: User): User {
        return userRepository.save(user)
    }

    @DeleteMapping("user/delete/{id}")
    fun deleteUser(@PathVariable id: Long) {
        return userRepository.deleteById(id)
    }

    @GetMapping("user/organization/add/{id}")
    fun addOrganization(@PathVariable id: Long, @RequestParam("organizationID") organizationID: Optional<Long>){

    }

    @GetMapping("user/organization/remove")
    fun removeOrganization(@PathVariable id: Long, @RequestParam("organizationID") organizationID: Optional<Long>){

    }
}
