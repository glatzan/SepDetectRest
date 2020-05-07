package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.loader.JoinWalker
import javax.persistence.*

@Entity
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.patient.scores.values",
            attributeNodes = [NamedAttributeNode("scores", subgraph = "scores.values")],
            subgraphs = [NamedSubgraph(name = "scores.values",
                    attributeNodes = [NamedAttributeNode("values")])])
])
class Patient {

    @Id
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var personId: Long = 0

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var piz: String = ""

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var room: String = ""

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var active: Boolean = false

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    var person: Person = Person()

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    @OneToOne
    var organization: Organization? = null

    @JsonView(JsonViews.FullPatient::class)
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    @OrderColumn(name = "listOrder")
    var scores: MutableList<Score> = ArrayList<Score>()
}
