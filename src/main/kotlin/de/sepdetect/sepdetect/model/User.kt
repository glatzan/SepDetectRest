package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import org.hibernate.annotations.SelectBeforeUpdate
import javax.persistence.*

@Entity
@Table(name = "user_table")
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.user.organization",
            attributeNodes = [NamedAttributeNode("organization")])])
class User {

    @JsonView(JsonViews.UserView::class)
    @Id
    var personId: Long = 0

    @JsonView(JsonViews.UserView::class)
    @Column(unique = true)
    var name: String = ""

    var pw: String = ""

    @JsonView(JsonViews.UserView::class)
    var lastLogin: Long = 0

    var userToken : String = ""

    @JsonView(JsonViews.UserView::class)
    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER

    @JsonView(JsonViews.UserView::class)
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    var person: Person = Person()

    @JsonView(JsonViews.UserView::class)
    @ManyToMany
    var organization: MutableList<Organization> = mutableListOf<Organization>()
}
