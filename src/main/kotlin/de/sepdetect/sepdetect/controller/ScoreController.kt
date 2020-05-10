package de.sepdetect.sepdetect.controller

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.model.Score
import de.sepdetect.sepdetect.model.ScoreValue
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.repository.ScoreRepository
import de.sepdetect.sepdetect.repository.ScoreValueRepository
import de.sepdetect.sepdetect.util.JsonViews
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityNotFoundException

@RestController
class ScoreController constructor(
        private val scoreRepository: ScoreRepository,
        private val soreValueRepository: ScoreValueRepository,
        private val patientRepository: PatientRepository) {

    @JsonView(JsonViews.ScoreList::class)
    @GetMapping("scores/{patientID}")
    fun getScores(@PathVariable patientID: Long): List<Score> {
        return scoreRepository.findAllByPatientPersonIdOrderByListOrder(patientID)
    }

    @JsonView(JsonViews.ScoreList::class)
    @PostMapping("score/add/{patientID}")
    fun addScore(@PathVariable patientID: Long, @RequestBody scoreValue: ScoreValue, @RequestParam("newScore") newScore: Optional<Boolean>): Score {
        val patient = patientRepository.findPatientByPersonId(patientID).orElseThrow { throw EntityNotFoundException("Patient not found!") }

        var score: Score? = null
        if ((newScore.isPresent && newScore.get()) || patient.scores.isEmpty()) {
            score = Score()
            score.date = LocalDate.now()
            score.patient = patient
            patient.scores.add(score)
            score = scoreRepository.save(score)
        } else {
            score = patient.scores.last()
        }

        scoreValue.score = score
        score.values.add(soreValueRepository.save(scoreValue))
        return scoreRepository.save(score)
    }

    @DeleteMapping("score/delete/{scoreID}")
    fun deleteScore(@PathVariable scoreID: Long) {

    }
}
