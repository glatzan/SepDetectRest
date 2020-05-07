package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import javax.persistence.*

@Entity
class Patient {

    @Id
    @JsonView(JsonViews.PatientsOnly::class)
    var person_id: Long = 0

    @JsonView(JsonViews.PatientsOnly::class)
    var piz: String = ""

    @JsonView(JsonViews.PatientsOnly::class)
    var room: String = ""

    @JsonView(JsonViews.PatientsOnly::class)
    var active: Boolean = false

    @JsonView(JsonViews.PatientsOnly::class)
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    var person: Person = Person()

    @JsonView(JsonViews.PatientsOnly::class)
    @OneToOne
    var organization: Organization? = null

    @OneToMany(mappedBy = "patient")
    var scores: MutableList<Score> = ArrayList<Score>()
}
