package de.sepdetect.sepdetect.model

import javax.persistence.*

@Entity
class Organization {

    @Id
    @GeneratedValue(generator = "organization_sequencegenerator")
    @SequenceGenerator(name = "organization_sequencegenerator", sequenceName = "organization_sequence")
    @Column(unique = true, nullable = false)
    var id : Long = 0

    var name : String = ""
}
