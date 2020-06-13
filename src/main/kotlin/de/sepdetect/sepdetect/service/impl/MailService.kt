package de.sepdetect.sepdetect.service.impl

import com.sun.xml.fastinfoset.util.StringArray
import de.sepdetect.sepdetect.model.Patient
import de.sepdetect.sepdetect.model.User
import de.sepdetect.sepdetect.repository.OrganizationRepository
import de.sepdetect.sepdetect.repository.UserRepository
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

/**
 * Dieser Service ermöglicht es emails an Benutzer zu versenden.
 */
@Service
class MailService constructor(
        private val emailSender: JavaMailSender,
        private val userRepository: UserRepository) {

    /**
     * Sendet eine Email an einen Benutzter
     */
    fun sendMail(to: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject(subject)
        message.setText(text)
        emailSender.send(message)
    }

    /**
     * Sucht alle User heraus, die in der gleichen Organisation wie der Patient sind und sendet diesen eine Warnung.
     */
    fun sendWarMailToUsers(patient: Patient) {
        val users = userRepository.findAllByOrganization(patient.organization!!)

        val score = patient.scores.last().values[patient.scores.last().values.size - 1].total - patient.scores.last().values[patient.scores.last().values.size - 2].total

        val subject = "SOFA-Score Warnung für ${patient.person.lastName}, ${patient.person.surname}, Geb: ${patient.person.birthday}, Piz: ${patient.piz}"
        val mail = "Achtung. Der SOFA-Score ist um $score Punkte angestiegen! Im Falle einer bestehenden Infektion bzw. wahrscheinlichen Infektion hat der Patient eine Sepsis. Bitte kontaktieren Sie umgehend die Intensivstation zur intensivmedizinischen Beurteilung des Patienten. (${patient.scores.last().values.last().date})"

        users.forEach {
            sendMail(it.email, subject, mail)
        }
    }

    /**
     * Sendet das UserPasswort an eine List von Emails
     */
    fun sendPasswordToUser(user: User, pw: String, mails: List<String>) {

        println("Sending mail with password to: ${mails.first()}")

        val subject = "Neues Passwort für Sepdetec"
        val mail = "Das Passwort für den Account ${user.name} wurde auf $pw geändert"

        mails.forEach {
            sendMail(it, subject, mail)
        }
    }
}