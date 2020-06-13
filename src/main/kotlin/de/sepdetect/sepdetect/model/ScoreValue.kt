package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import org.hibernate.envers.Audited
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.persistence.*

/**
 * POJO für eine SOFA Score.  Stellt den entity Graph "graph.score", welcher das parent Objekt lädt
 */
@Entity
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.score",
            attributeNodes = [NamedAttributeNode("score")])
])
@Audited
class ScoreValue {

    /**
     * Einzigartige ID
     */
    @Id
    @GeneratedValue(generator = "score_value_sequencegenerator")
    @SequenceGenerator(name = "score_value_sequencegenerator", sequenceName = "score_value_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var id: Long = 0

    /**
     * Parent Objekt
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    var score: Score = Score()

    /**
     * POA Value
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var pao: Int = 0

    /**
     * GCS Value
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var gcs: Int = 0

    /**
     * Map Value
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var map: Int = 0

    /**
     * Liver Value
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var liver: Int = 0

    /**
     * Coagulation Value
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var coagulation: Int = 0

    /**
     * Krea Value
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var krea: Int = 0

    /**
     * Total Score
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var total: Int = 0

    /**
     * Datum des Scores
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    var date: LocalDate = LocalDate.now()

    /**
     * Order im Vater-Kontainer
     */
    @Column(insertable = false, updatable = false)
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var listOrder: Int = 0
}
