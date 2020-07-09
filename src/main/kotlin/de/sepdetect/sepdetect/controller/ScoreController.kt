package de.sepdetect.sepdetect.controller

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.model.Score
import de.sepdetect.sepdetect.model.ScoreValue
import de.sepdetect.sepdetect.repository.PatientRepository
import de.sepdetect.sepdetect.repository.ScoreRepository
import de.sepdetect.sepdetect.repository.ScoreValueRepository
import de.sepdetect.sepdetect.service.impl.MailService
import de.sepdetect.sepdetect.util.HttpResponseStatus
import de.sepdetect.sepdetect.util.JsonViews
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityNotFoundException

@CrossOrigin
@RestController
class ScoreController constructor(
        private val scoreRepository: ScoreRepository,
        private val scoreValueRepository: ScoreValueRepository,
        private val patientRepository: PatientRepository,
        private val mailService: MailService) {

    /**
     * Gibt den Score mit der passenden ID zurück. Aufrufer muss die passenden Rechte haben.
     * TODO: Rechte Check
     */
    @JsonView(JsonViews.ScoreList::class)
    @GetMapping("score/value/get/{scoreValueId}")
    fun getScore(@PathVariable scoreValueId: Long): ScoreValue {
        return scoreValueRepository.findById(scoreValueId).orElseThrow { throw EntityNotFoundException("Score Value not found! ($scoreValueId)") }
    }

    /**
     * Markiert einen Verlauf als beendet. Aufrufer muss die passenden Rechte haben.
     * TODO: Rechte Check
     */
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

    /**
     * Beendet den letzten SOFA-Verlauf des Patienten. Wenn er schon beendet wurde, wird ein Fehler geschmissen.
     */
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

    /**
     * Erlaubt das hinzufügen eines neuen Scores zu dem aktuellen Verlauf. Existiert kein Verlauf wird ein neuer erstellt. Aufrufer muss die passenden Rechte haben.
     * TODO: Rechte Check
     */
    @JsonView(JsonViews.ScoreList::class)
    @PostMapping("score/value/add/{patientID}")
    fun addScoreValue(@PathVariable patientID: Long, @RequestBody scoreValue: ScoreValue, @RequestParam("newScore") newScore: Optional<Boolean>): ScoreValue {
        var patient = patientRepository.findPatientByPersonId(patientID).orElseThrow { throw EntityNotFoundException("Patient not found!") }

        var score: Score? = null
        // erstellt neuen Verlauf wenn nötig
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

        patient = patientRepository.save(patient)

        if (patient.scores.last().values.size > 1) {
            val lastValue = patient.scores.last().values.last()
            val beforeLastValue = patient.scores.last().values[patient.scores.last().values.size - 2]
            // send warning mail to users
            if (lastValue.total - beforeLastValue.total >= 2) {
                mailService.sendWarMailToUsers(patient)
            }
        }

        return patient.scores.last().values.last();
    }

    /**
     * Erlaubt das bearbeiten des Scores mit der passenden ID. Aufrufer muss die passenden Rechte haben.
     * TODO: Rechte Check
     */
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

    /**
     * Erlaubt das Löschen eines SOFA-Scores. Aufrufer muss die passenden Rechte haben.
     */
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
