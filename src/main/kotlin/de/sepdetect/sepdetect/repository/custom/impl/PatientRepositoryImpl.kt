package de.sepdetect.sepdetect.repository.custom.impl

import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.model.Person
import de.sepdetect.sepdetect.repository.custom.PatientRepositoryCustom
import org.hibernate.Session
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*
import javax.persistence.metamodel.SingularAttribute

@Service
class PatientRepositoryImpl : PatientRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    private fun getSession(): Session {
        return em.unwrap(Session::class.java)
    }

    private fun getCriteriaBuilder(): CriteriaBuilder? {
        return getSession().criteriaBuilder
    }

    override fun findPatientByArguments(lastname: String?, surname: String?, birthday: LocalDate?, gender: Char?): List<Patient> {
        val builder = getCriteriaBuilder()
        val criteria: CriteriaQuery<Patient> = builder!!.createQuery(Patient::class.java)
        val root: Root<Patient> = criteria.from(Patient::class.java)

        val personQuery: Join<Patient, Person> = root.join<Patient, Person>("person", JoinType.LEFT)

        val predicates: MutableList<Predicate> = ArrayList()

        if (!lastname.isNullOrEmpty()) {
            predicates.add(builder.like(builder.lower(personQuery.get("lastName")), "%$lastname%"))
        }

        if (!surname.isNullOrEmpty()) {
            predicates.add(builder.like(builder.lower(personQuery.get("surname")), "%$surname%"))
        }

        if (gender != null) {
            predicates.add(builder.equal(personQuery.get<Char>("gender"), gender))
        }

        if (birthday != null) {
            predicates.add(builder.equal(personQuery.get<LocalDate>("birthday"), birthday))
        }

        criteria.where(builder.and(*predicates.toTypedArray()))
        criteria.distinct(true)

        return getSession().createQuery(criteria).resultList
    }


}