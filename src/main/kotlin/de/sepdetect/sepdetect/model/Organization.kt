package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import javax.persistence.*

/**
 * POGO für eine Organisation
 */
@Entity
class Organization {

    /**
     * Einzigartige ID
     */
    @Id
    @GeneratedValue(generator = "organization_sequencegenerator")
    @SequenceGenerator(name = "organization_sequencegenerator", sequenceName = "organization_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var id : Long = 0

    /**
     * Name der Organisation
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var name : String = ""
}
