package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import java.time.LocalDate
import javax.persistence.*

@Entity
class Score {
    @Id
    @GeneratedValue(generator = "score_sequencegenerator")
    @SequenceGenerator(name = "score_sequencegenerator", sequenceName = "score_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.FullPatient::class)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_person_id")
    var patient: Patient = Patient()

    @JsonView(JsonViews.FullPatient::class)
    var date: LocalDate = LocalDate.now()

    var completed = false

    var listOrder : Int = 0

    @OneToMany(mappedBy = "score", fetch = FetchType.LAZY)
    @JsonView(JsonViews.FullPatient::class)
    @OrderColumn(name = "listOrder")
    var values: MutableList<ScoreValue> = ArrayList<ScoreValue>()
}
