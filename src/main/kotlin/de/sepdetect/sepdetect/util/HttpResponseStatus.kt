package de.sepdetect.sepdetect.util

/**
 * Return Objekt für Rest-Kommunikation, die normalerweise kein JSON Response bietet.
 */
class HttpResponseStatus(var status: String, var message: String) {
}