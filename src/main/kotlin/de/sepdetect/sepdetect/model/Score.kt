package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate
import javax.persistence.*

@Entity
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.scores.values",
            attributeNodes = [NamedAttributeNode("values")])
])
class Score {
    @Id
    @GeneratedValue(generator = "score_sequencegenerator")
    @SequenceGenerator(name = "score_sequencegenerator", sequenceName = "score_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_person_id")
    var patient: Patient = Patient()

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var startDate: LocalDate = LocalDate.now()

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var endDate: LocalDate = LocalDate.now()

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var completed = false

    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    var listOrder: Int = 0

    @OneToMany(mappedBy = "score", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonView(JsonViews.FullPatient::class, JsonViews.ScoreList::class)
    @OrderColumn(name = "listOrder")
    var values: MutableList<ScoreValue> = ArrayList<ScoreValue>()
}
