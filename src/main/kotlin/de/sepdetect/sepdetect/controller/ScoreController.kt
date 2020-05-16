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
        private val scoreValueRepository: ScoreValueRepository,
        private val patientRepository: PatientRepository) {

    @JsonView(JsonViews.ScoreList::class)
    @GetMapping("scores/{patientID}")
    fun getScores(@PathVariable patientID: Long): List<Score> {
        return scoreRepository.findAllByPatientPersonIdOrderByListOrder(patientID)
    }

    @GetMapping("score/get/{scoreValueId}")
    @JsonView(JsonViews.ScoreList::class)
    fun getScore(@PathVariable scoreValueId: Long): ScoreValue {
        return scoreValueRepository.findById(scoreValueId).orElseThrow { throw EntityNotFoundException("Score Value not found! ($scoreValueId)") }
    }


    @JsonView(JsonViews.ScoreList::class)
    @PostMapping("score/add/{patientID}")
    fun addScoreValue(@PathVariable patientID: Long, @RequestBody scoreValue: ScoreValue, @RequestParam("newScore") newScore: Optional<Boolean>): Score {
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
        score.values.add(scoreValueRepository.save(scoreValue))
        return scoreRepository.save(score)
    }

    @DeleteMapping("score/deleteValue/{scoreID}")
    @JsonView(JsonViews.ScoreList::class)
    @PutMapping("score/edit")
    fun editScoreValue(@RequestBody scoreValue: ScoreValue): ScoreValue {
        return scoreValueRepository.save(scoreValue);
    }

    @DeleteMapping("score/delete/{scoreID}")
    fun deleteScore(@PathVariable scoreID: Long) {
        scoreValueRepository.deleteById(scoreID)
    }

    @GetMapping("scores/merge}")
    fun mergeScores(@RequestParam("score1ID") score1ID: Optional<Long>, @RequestParam("score2ID") score2ID: Optional<Long>) {
        if (!score1ID.isPresent || !score2ID.isPresent)
            throw IllegalArgumentException("Error: Provide two score ids!")

        val score1 = scoreRepository.findById(score1ID.get()).orElseThrow { throw EntityNotFoundException("Error: Score not found (id: ${score1ID.get()})") }
        val score2 = scoreRepository.findById(score2ID.get()).orElseThrow { throw EntityNotFoundException("Error: Score not found (id: ${score2ID.get()})") }

        if (score1.listOrder > score2.listOrder) {

        }else{

        }
    }
}
