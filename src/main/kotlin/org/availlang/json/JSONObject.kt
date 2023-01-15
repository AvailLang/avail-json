/*
 * JSONObject.kt
 * Copyright © 1993-2022, The Avail Foundation, LLC.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the copyright holder nor the names of the contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

@file:Suppress("unused")

package org.availlang.json

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.collections.Map.Entry

/**
 * A [JSONObject] is produced by a [JSONReader] when an object is
 * read. Each key of the object is a [String] and each value is a [JSONData].
 *
 * @author Todd L Smith &lt;todd@availlang.org&gt;
 *
 * @property map
 *   The field assignments of the [JSONObject] as a [map][Map] from
 *   [key][String]s to [value][JSONData]s.
 *
 * @constructor
 *
 * Construct a new [JSONObject].
 *
 * @param map
 *   The field assignments of the [JSONObject] as a [Map] from
 *   [key][String]s to [value][JSONData]s. This must not be modified by the
 *   caller afterward; this call transfers ownership of the reference.
 */
class JSONObject internal constructor(
	private val map: Map<String, JSONData>
) : JSONData(), Iterable<Entry<String, JSONData>>
{
	override val isObject: Boolean
		get() = true

	/**
	 * Does the [JSONObject] include a binding for the specified key?
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   `true` if the [JSONObject] includes a binding for the key, `false`
	 *   otherwise.
	 */
	fun containsKey(k: String): Boolean = map.containsKey(k)

	/**
	 * Get a [JSONData] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [JSONData] associated with requested key.
	 * @throws NullPointerException
	 *   If the requested key is not present.
	 */
	operator fun get(k: String): JSONData =
		map[k] ?: throw NullPointerException("Key, $k, not found.")

	/**
	 * Get a [Boolean] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [Boolean] associated with requested key, or the result of applying
	 *   [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONValue].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getBoolean(
		k: String,
		orElse: ()->Boolean = {
			throw NullPointerException("Key, $k, not found.")
		}
	): Boolean = (map[k] as? JSONValue)?.boolean ?: run(orElse)

	/**
	 * Get a [Boolean] associated with requested key, or `null` if no such key
	 * exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [Boolean] associated with requested key, or `null` if no such key
	 *   exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONValue].
	 */
	@Throws(ClassCastException::class)
	fun getBooleanOrNull(
		k: String
	): Boolean? = (map[k] as? JSONValue)?.boolean
	
	/**
	 * Get a [JSONNumber] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [JSONNumber] associated with requested key, or the result of
	 *   applying [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONNumber].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getNumber(
		k: String,
		orElse: ()->JSONNumber = {
			throw NullPointerException("Key, $k, not found.")
		}
	): JSONNumber = (map[k] as? JSONNumber) ?: run(orElse)

	/**
	 * Get a [JSONNumber] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [JSONNumber] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONNumber].
	 */
	@Throws(ClassCastException::class)
	fun getNumberOrNull(k: String): JSONNumber? = map[k] as? JSONNumber

	/**
	 * Get a [BigInteger] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [BigInteger] associated with requested key, or the result of
	 *   applying [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [BigInteger].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getBigInteger(
		k: String,
		orElse: ()->BigInteger = {
			throw NullPointerException("Key, $k, not found.")
		}
	): BigInteger = (map[k] as? JSONNumber)?.bigInteger ?: run(orElse)

	/**
	 * Get a [BigInteger] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [BigInteger] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [BigInteger].
	 */
	@Throws(ClassCastException::class)
	fun getBigIntegerOrNull(k: String): BigInteger? =
		(map[k] as? JSONNumber)?.bigInteger

	/**
	 * Get a [Long] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [Long] associated with requested key, or the result of applying
	 *   [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Long].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getLong(
		k: String,
		orElse: ()->Long = {
			throw NullPointerException("Key, $k, not found.")
		}
	): Long = (map[k] as? JSONNumber)?.long ?: run(orElse)

	/**
	 * Get a [Long] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [Long] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Long].
	 */
	@Throws(ClassCastException::class)
	fun getLongOrNull(k: String): Long? = (map[k] as? JSONNumber)?.long

	/**
	 * Get a [Int] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [Int] associated with requested key, or the result of applying
	 *   [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Int].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getInt(
		k: String,
		orElse: ()->Int = {
			throw NullPointerException("Key, $k, not found.")
		}
	): Int = (map[k] as? JSONNumber)?.int ?: run(orElse)

	/**
	 * Get a [Int] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [Int] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Int].
	 */
	@Throws(ClassCastException::class)
	fun getIntOrNull(k: String): Int? = (map[k] as? JSONNumber)?.int

	/**
	 * Get a [BigDecimal] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [BigDecimal] associated with requested key, or the result of
	 *   applying [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [BigDecimal].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getBigDecimal(
		k: String,
		orElse: ()->BigDecimal = {
			throw NullPointerException("Key, $k, not found.")
		}
	): BigDecimal = (map[k] as? JSONNumber)?.bigDecimal ?: run(orElse)

	/**
	 * Get a [BigDecimal] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [BigDecimal] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [BigDecimal].
	 */
	@Throws(ClassCastException::class)
	fun getBigDecimalOrNull(k: String): BigDecimal? =
		(map[k] as? JSONNumber)?.bigDecimal

	/**
	 * Get a [Double] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [Double] associated with requested key, or the result of applying
	 *   [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Double].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getDouble(
		k: String,
		orElse: ()->Double = {
			throw NullPointerException("Key, $k, not found.")
		}
	): Double = (map[k] as? JSONNumber)?.double ?: run(orElse)

	/**
	 * Get a [Double] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [Double] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Double].
	 */
	@Throws(ClassCastException::class)
	fun getDoubleOrNull(k: String): Double? = (map[k] as? JSONNumber)?.double

	/**
	 * Get a [Float] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [Float] associated with requested key, or the result of applying
	 *   [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Float].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getFloat(
		k: String,
		orElse: ()->Float = {
			throw NullPointerException("Key, $k, not found.")
		}
	): Float = (map[k] as? JSONNumber)?.float ?: run(orElse)

	/**
	 * Get a [Float] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [Float] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [Float].
	 */
	@Throws(ClassCastException::class)
	fun getFloatOrNull(k: String): Float? = (map[k] as? JSONNumber)?.float
	
	/**
	 * Get a [String] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [String] associated with requested key, or the result of applying
	 *   [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONValue].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getString(
		k: String,
		orElse: ()->String = {
			throw NullPointerException("Key, $k, not found.")
		}
	): String = (map[k] as? JSONValue)?.string ?: run(orElse)

	/**
	 * Get a [String] associated with requested key, or `null` if no such key
	 * exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [String] associated with requested key, or `null` if no such key
	 *   exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONValue].
	 */
	@Throws(ClassCastException::class)
	fun getStringOrNull(k: String): String? = (map[k] as? JSONValue)?.string
	
	/**
	 * Get a [JSONArray] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [JSONArray] associated with requested key, or the result of
	 *   applying [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONArray].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getArray(
		k: String,
		orElse: () -> JSONArray = {
			throw NullPointerException("Key, $k, not found.")
		}
	): JSONArray = (map[k] as? JSONArray) ?: run(orElse)

	/**
	 * Get a [JSONArray] associated with requested key, or `null` if no such key
	 * exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [JSONArray] associated with requested key, or `null` if no such key
	 *   exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONArray].
	 */
	@Throws(ClassCastException::class)
	fun getArrayOrNull(k: String): JSONArray? = map[k] as? JSONArray
	
	/**
	 * Get a [JSONObject] associated with requested key.
	 *
	 * @param k
	 *   The key.
	 * @param orElse
	 *   An optional function to run if [k] was not found.  If not present, a
	 *   [NullPointerException] is thrown if [k] was not found.
	 * @return
	 *   The [JSONObject] associated with requested key, or the result of
	 *   applying [orElse] if [k] was not found.
	 * @throws NullPointerException
	 *   If the requested key is not present and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONObject].
	 */
	@Throws(NullPointerException::class, ClassCastException::class)
	fun getObject(
		k: String,
		orElse: () -> JSONObject = {
			throw NullPointerException("Key, $k, not found.")
		}
	): JSONObject = (map[k] as? JSONObject) ?: run(orElse)

	/**
	 * Get a [JSONObject] associated with requested key, or `null` if no such
	 * key exists.
	 *
	 * @param k
	 *   The key.
	 * @return
	 *   The [JSONObject] associated with requested key, or `null` if no such
	 *   key exists.
	 * @throws ClassCastException
	 *   If the value associated with the requested key is not a [JSONObject].
	 */
	@Throws(ClassCastException::class)
	fun getObjectOrNull(k: String): JSONObject? = map[k] as? JSONObject

	override fun iterator(): Iterator<Entry<String, JSONData>> =
		map.entries.iterator()

	/**
	 * Transform the fields of this [JSONObject] into a [Map] with the keys
	 * of the map being the transformed keys of this [JSONObject] and the values
	 * of the map being the corresponding transformed values of this
	 * [JSONObject].
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun <K, V> map (
		transform: (String, JSONData) -> Pair<K, V>
	): Map<K, V> = this.associate { (s, v) -> transform(s, v) }

		override fun writeTo(writer: JSONWriter) =
		writer.writeObject {
			map.forEach { (key, value) ->
				at(key) { value.writeTo(writer) }
			}
		}

	companion object
	{
		/** The sole empty [JSONObject]. */
		val empty = JSONObject(emptyMap())
	}
}
