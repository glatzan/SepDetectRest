package de.sepdetect.sepdetect.util

/**
 * Interfaces definiert alle JSON Views. Wichtig, damit in der Restschnitstelle nur bestimme Felder zurückgegeben werden.
 * Alle Werte die zurück gegeben werden sollen sind mittels des Enum-Wertes markiert
 */
object JsonViews {
    interface PatientsOnly {}
    interface FullPatient {}
    interface ScoreList {}
    interface UserView {}
}
