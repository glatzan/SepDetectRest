package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.envers.Audited
import java.time.LocalDate
import javax.persistence.*

/**
 * POJO für eine SOFA-Score Verlauf. Stellt den entity Graph "graph.scores.values", welcher alle SOFA-Values lädt.
 */
@Entity
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.scores.values",
            attributeNodes = [NamedAttributeNode("values")])
])
@Audited
class Score {

    /**
     * Einzigartige ID
     */
    @Id
    @GeneratedValue(generator = "score_sequencegenerator")
    @SequenceGenerator(name = "score_sequencegenerator", sequenceName = "score_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var id: Long = 0

    /**
     * Patient Parent Objekt
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_person_id")
    var patient: Patient = Patient()

    /**
     * Start Datum des Verlaufes
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var startDate: LocalDate = LocalDate.now()

    /**
     * End Datum des Verlaufes
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var endDate: LocalDate = LocalDate.now()

    /**
     * Verlauf abgeschlossen
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var completed = false

    /**
     * Order im Parent-Objekt
     */
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    @Column(insertable = false, updatable = false)
    var listOrder: Int = 0

    /**
     * SOFA-Score Values
     */
    @OneToMany(mappedBy = "score", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    @OrderColumn(name = "listOrder")
    var values: MutableList<ScoreValue> = ArrayList<ScoreValue>()
}
