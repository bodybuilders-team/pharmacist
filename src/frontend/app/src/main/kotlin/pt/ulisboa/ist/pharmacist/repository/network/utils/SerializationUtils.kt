package pt.ulisboa.ist.pharmacist.repository.network.utils

import com.google.gson.Gson
import com.google.gson.stream.JsonReader

object SerializationUtils {
    val gson = Gson()
}

/**
 * Parses an object of type [T] from the [json] stream.
 *
 * @receiver the Gson instance
 * @param json the json stream
 *
 * @return the parsed object
 */
inline fun <reified T> Gson.fromJson(json: JsonReader): T = fromJson(json, T::class.java)

/**
 * Parses an object of type [T] from the [json] stream.
 *
 * @receiver the Gson instance
 * @param json the json String
 *
 * @return the parsed object
 */
inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, T::class.java)
