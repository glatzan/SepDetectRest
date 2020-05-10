package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import javax.persistence.*

@Entity
class ScoreValue {

    @Id
    @GeneratedValue(generator = "score_value_sequencegenerator")
    @SequenceGenerator(name = "score_value_sequencegenerator", sequenceName = "score_value_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var id : Long = 0

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    var score : Score = Score()

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var pao : Int = 0

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var gcs : Int = 0

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var map : Int = 0

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var liver : Int = 0

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var coagulation : Int = 0

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var krea : Int = 0

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var total : Int = 0

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var listOrder : Int = 0
}
