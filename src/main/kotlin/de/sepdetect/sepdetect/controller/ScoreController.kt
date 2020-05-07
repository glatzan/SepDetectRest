package de.sepdetect.sepdetect.controller

import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.model.Score
import de.sepdetect.sepdetect.repository.ScoreRepository
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ScoreController constructor(
        private val scoreRepository: ScoreRepository) {

    @GetMapping("scores/{patientID}")
    fun getScores(@PathVariable patientID: Long) : List<Score> {
        return scoreRepository.findAllByPatientPersonIdOrderByListOrder(patientID)
    }

    @PostMapping("score/add/{patienID}")
    fun addScore(@PathVariable patientID: Long, @RequestBody patient: Patient, @RequestParam("new") active: Optional<Boolean>) {

    }

    @DeleteMapping("score/delete/{scoreID}")
    fun deleteScore(@PathVariable scoreID: Long) {

    }
}
