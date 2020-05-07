package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import javax.persistence.*

@Entity
@Table(name = "user_table")
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.user.organization",
            attributeNodes = [NamedAttributeNode("organization")])])
class User {

    @Id
    var personId: Long = 0

    var name: String = ""

    var pw: String = ""

    var lastLogin: Long = 0

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    var person: Person = Person()

    @JsonView(JsonViews.PatientsOnly::class)
    @ManyToMany
    var organization: MutableList<Organization> = mutableListOf<Organization>()
}
