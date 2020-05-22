package de.sepdetect.sepdetect.controller

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.model.Score
import de.sepdetect.sepdetect.model.ScoreValue
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.repository.ScoreRepository
import de.sepdetect.sepdetect.repository.ScoreValueRepository
import de.sepdetect.sepdetect.util.HttpResponseStatus
import de.sepdetect.sepdetect.util.JsonViews
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
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

    @JsonView(JsonViews.ScoreList::class)
    @GetMapping("score/value/get/{scoreValueId}")
    fun getScore(@PathVariable scoreValueId: Long): ScoreValue {
        return scoreValueRepository.findById(scoreValueId).orElseThrow { throw EntityNotFoundException("Score Value not found! ($scoreValueId)") }
    }

    @JsonView(JsonViews.ScoreList::class)
    @GetMapping("score/end/{scoreId}")
    fun endScore(@PathVariable scoreId: Long): Score {
        val score = scoreRepository.findById(scoreId).orElseThrow { throw EntityNotFoundException("Score not found!") }

        if (score.completed)
            throw IllegalStateException("Score already completed")

        score.endDate = LocalDate.now()
        score.completed = true;

        return scoreRepository.save(score)
    }

    @JsonView(JsonViews.ScoreList::class)
    @GetMapping("score/end/last/{patientId}")
    fun endLastScore(@PathVariable patientId: Long): Score {
        val patient = patientRepository.findPatientByPersonId(patientId).orElseThrow { throw EntityNotFoundException("Score not found!") }

        if (patient.scores.isEmpty() || patient.scores.last().completed)
            throw IllegalStateException("Score already completed")

        var score = patient.scores.last()

        score.endDate = LocalDate.now()
        score.completed = true;
        score = scoreRepository.save(score)
        return score
    }

    @JsonView(JsonViews.ScoreList::class)
    @PostMapping("score/value/add/{patientID}")
    fun addScoreValue(@PathVariable patientID: Long, @RequestBody scoreValue: ScoreValue, @RequestParam("newScore") newScore: Optional<Boolean>): ScoreValue {
        val patient = patientRepository.findPatientByPersonId(patientID).orElseThrow { throw EntityNotFoundException("Patient not found!") }

        var score: Score? = null
        if ((newScore.isPresent && newScore.get()) || patient.scores.isEmpty() || patient.scores.last().completed) {
            score = Score()
            score.startDate = LocalDate.now()
            score.patient = patient
            patient.scores.add(score)
        } else {
            score = patient.scores.last()
        }

        scoreValue.score = score
        score.values.add(scoreValueRepository.save(scoreValue))
        return patientRepository.save(patient).scores.last().values.last();
    }

    @JsonView(JsonViews.ScoreList::class)
    @PutMapping("score/value/edit")
    fun editScoreValue(@RequestBody scoreValue: ScoreValue): ScoreValue {
        val dbScoreValue = scoreValueRepository.findById(scoreValue.id).orElseThrow { throw EntityNotFoundException("Score Value not found!") }

        dbScoreValue.pao = scoreValue.pao
        dbScoreValue.gcs = scoreValue.gcs
        dbScoreValue.map = scoreValue.map
        dbScoreValue.liver = scoreValue.liver
        dbScoreValue.coagulation = scoreValue.coagulation
        dbScoreValue.krea = scoreValue.krea
        dbScoreValue.total = scoreValue.total

        return scoreValueRepository.save(dbScoreValue);
    }

    @DeleteMapping("score/value/delete/{scoreValueId}")
    fun deleteScore(@PathVariable scoreValueId: Long): HttpResponseStatus {
        val scoreValue = scoreValueRepository.findById(scoreValueId).orElseThrow { throw EntityNotFoundException("Score not found!") }
        val score = scoreRepository.findById(scoreValue.score.id).orElseThrow { throw EntityNotFoundException("Score not found!") }

        score.values.removeIf { it.id == scoreValueId }

        scoreRepository.save(score)
        scoreValueRepository.deleteById(scoreValueId)

        return HttpResponseStatus("OK", "ScoreValue deleted (id $scoreValueId)")
    }

}
