package de.sepdetect.sepdetect.model

import java.time.LocalDate
import javax.persistence.*

@Entity
class Person {

    @Id
    @GeneratedValue(generator = "patient_sequencegenerator")
    @SequenceGenerator(name = "patient_sequencegenerator", sequenceName = "patient_sequence")
    @Column(unique = true, nullable = false)
    var id: Long = 0

    var lastName: String = ""

    var surname: String = ""

    var gender: Char = '?'

    var birthday: LocalDate = LocalDate.now()
}
