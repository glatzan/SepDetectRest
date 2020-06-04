package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import java.time.LocalDate
import javax.persistence.*

/**
 * POJO für eine Person, wird von Patient und User referenziert.
 */
@Entity
class Person {

    /**
     * Einzigartige ID
     */
    @Id
    @GeneratedValue(generator = "patient_sequencegenerator")
    @SequenceGenerator(name = "patient_sequencegenerator", sequenceName = "patient_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var id: Long = 0

    /**
     * Nachname
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var lastName: String = ""

    /**
     * Vorname
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var surname: String = ""

    /**
     * Geschlecht
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var gender: Char = '?'

    /**
     * Geburtstag
     */
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var birthday: LocalDate = LocalDate.now()
}
