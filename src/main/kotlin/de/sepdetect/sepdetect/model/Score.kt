package de.sepdetect.sepdetect.model

import java.time.LocalDate
import javax.persistence.*

@Entity
class Score {

    @Id
    @GeneratedValue(generator = "score_sequencegenerator")
    @SequenceGenerator(name = "score_sequencegenerator", sequenceName = "score_sequence")
    @Column(unique = true, nullable = false)
    var id : Long = 0

    @ManyToOne
    @JoinColumn(name = "patient_person_id")
    var patient : Patient = Patient()

    var date : LocalDate = LocalDate.now()

    @OneToMany(mappedBy = "score")
    var values : MutableList<ScoreValue> = ArrayList<ScoreValue>()
}
