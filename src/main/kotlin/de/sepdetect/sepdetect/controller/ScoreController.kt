package de.sepdetect.sepdetect.controller

import de.sepdetect.sepdetect.model.Score
import de.sepdetect.sepdetect.model.ScoreValue
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.repository.ScoreRepository
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityNotFoundException

@RestController
class ScoreController constructor(
        private val scoreRepository: ScoreRepository,
        private val patientRepository: PatientRepository) {

    @GetMapping("scores/{patientID}")
    fun getScores(@PathVariable patientID: Long): List<Score> {
        return scoreRepository.findAllByPatientPersonIdOrderByListOrder(patientID)
    }

    @PostMapping("score/add/{patienID}")
    fun addScore(@PathVariable patientID: Long, @RequestBody scoreValue: ScoreValue, @RequestParam("newScore") newScore: Optional<Boolean>) {
        val patient = patientRepository.findPatientByPersonId(patientID).orElseThrow { throw EntityNotFoundException("Patient not found!") }

        var score: Score? = null
        if ((newScore.isPresent && newScore.get()) || patient.scores.isEmpty()) {
            score = Score()
            score.date = LocalDate.now()
            score.patient = patient

            patient.scores.add(score)
        } else {
            score = patient.scores.last()
        }

        score.values.add(scoreValue)

    }

    @DeleteMapping("score/delete/{scoreID}")
    fun deleteScore(@PathVariable scoreID: Long) {

    }
}
