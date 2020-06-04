package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import org.hibernate.annotations.SelectBeforeUpdate
import javax.persistence.*

/**
 * POJO für einen Benutzer. Stellt den entity Graph "graph.user.organization", welcher alle Organisationen des Benutzers lädt.
 */
@Entity
@Table(name = "user_table")
@NamedEntityGraphs(*[
    NamedEntityGraph(name = "graph.user.organization",
            attributeNodes = [NamedAttributeNode("organization")])])
class User {

    /**
     * Einzigartige ID, Referenz zum Patienten Objekt
     */
    @JsonView(JsonViews.UserView::class)
    @Id
    var personId: Long = 0

    /**
     * Name
     */
    @JsonView(JsonViews.UserView::class)
    @Column(unique = true)
    var name: String = ""

    /**
     * Passwort mit AES 256 gehasht
     */
    var pw: String = ""

    /**
     * Email
     */
    @JsonView(JsonViews.UserView::class)
    var email: String = ""

    /**
     * Timestamp des letzten Login
     */
    @JsonView(JsonViews.UserView::class)
    var lastLogin: Long = 0

    /**
     * Einzigartiger Session Token, verhindert doppelt Login
     */
    var userToken: String = ""

    /**
     * Rolle @see UserRole
     */
    @JsonView(JsonViews.UserView::class)
    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER

    /**
     * Personen Objekt
     */
    @JsonView(JsonViews.UserView::class)
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    var person: Person = Person()

    /**
     * Organisationen Objekte
     */
    @JsonView(JsonViews.UserView::class)
    @ManyToMany
    var organization: MutableList<Organization> = mutableListOf<Organization>()
}
