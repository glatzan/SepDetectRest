package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.loader.JoinWalker
import javax.persistence.*


/**
 * POJO für eine Patienten. Stellt den entity Graph "graph.patient.scores.values", welcher alle SOFA-Values lädt.
 */
@Entity
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.patient.scores.values",
            attributeNodes = [NamedAttributeNode("scores", subgraph = "scores.values")],
            subgraphs = [NamedSubgraph(name = "scores.values",
                    attributeNodes = [NamedAttributeNode("values")])])
])
class Patient {

    /**
     * Einzigartige ID, Referenz zum Patienten Objekt
     */
    @Id
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var personId: Long = 0

    /**
     * Piz
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var piz: String = ""

    /**
     * Raum des Patienten
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var room: String = ""

    /**
     * Patient aktiv/inaktiv
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    var active: Boolean = false

    /**
     * Person Objekt
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    var person: Person = Person()

    /**
     *Organisations Objekt
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class)
    @ManyToOne
    var organization: Organization? = null

    /**
     * Array mit SOFA-Score Verläufen
     */
    @JsonView(JsonViews.FullPatient::class)
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderColumn(name = "listOrder")
    var scores: MutableList<Score> = ArrayList<Score>()
}
