package de.sepdetect.sepdetect.model

import com.fasterxml.jackson.annotation.JsonView
import de.sepdetect.sepdetect.util.JsonViews
import java.time.LocalDate
import javax.persistence.*

@Entity
class Person {

    @Id
    @GeneratedValue(generator = "patient_sequencegenerator")
    @SequenceGenerator(name = "patient_sequencegenerator", sequenceName = "patient_sequence")
    @Column(unique = true, nullable = false)
    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var id: Long = 0

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var lastName: String = ""

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var surname: String = ""

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var gender: Char = '?'

    @JsonView(JsonViews.PatientsOnly::class, JsonViews.FullPatient::class, JsonViews.UserView::class)
    var birthday: LocalDate = LocalDate.now()
}
