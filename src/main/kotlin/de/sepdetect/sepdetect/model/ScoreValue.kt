package de.sepdetect.sepdetect.model

import javax.persistence.*

@Entity
class ScoreValue {

    @Id
    @GeneratedValue(generator = "score_value_sequencegenerator")
    @SequenceGenerator(name = "score_value_sequencegenerator", sequenceName = "score_value_sequence")
    @Column(unique = true, nullable = false)
    var id : Long = 0

    @ManyToOne
    var score : Score = Score()

    var pao : Int = 0

    var gcs : Int = 0

    var map : Int = 0

    var liver : Int = 0

    var coagulation : Int = 0

    var krea : Int = 0

    var total : Int = 0
}
