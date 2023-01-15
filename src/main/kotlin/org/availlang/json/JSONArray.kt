/*
 * JSONArray.kt
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

package org.availlang.json

import java.math.BigDecimal
import java.math.BigInteger

/**
 * A `JSONArray` is produced by a [JSONReader] when an array is
 * read. Each element is a [JSONData].
 *
 * @property array
 *   The array of [JSONData].
 * @author Todd L Smith &lt;todd@availlang.org&gt;
 *
 * @constructor
 *
 * Construct a new [JSONArray].
 *
 * @param array
 *   The array of [JSONData]s. This must not be modified by the caller
 *   afterward; this call transfers ownership of the reference.
 */
@Suppress("unused")
class JSONArray internal constructor(
	private val array: Array<JSONData>
) : JSONData(), Iterable<JSONData>
{
	override val isArray: Boolean get() = true

	/**
	 * Answer the length of the [receiver][JSONArray].
	 *
	 * @return
	 *   The length of the receiver.
	 */
	fun size(): Int = array.size

	/**
	 * @return
	 *   `true` if [size] is 0; `false` otherwise.
	 */
	fun isEmpty(): Boolean = array.isEmpty()

	/**
	 * @return
	 *   `true` if [size] is greater than 0; `false` otherwise.
	 */
	fun isNotEmpty(): Boolean = array.isNotEmpty()

	/**
	 * Get a [JSONData] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The `JSONData` at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 */
	@Throws(ArrayIndexOutOfBoundsException::class)
	operator fun get(index: Int): JSONData = array[index]

	/**
	 * Get a [Boolean] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [Boolean] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getBoolean(
		index: Int,
		orElse: ()->Boolean = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read a " +
					"boolean.")
		}
	): Boolean = (array[index] as? JSONValue)?.boolean ?: run(orElse)

	/**
	 * Get a [Boolean] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [Boolean] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getBooleanOrNull(index: Int): Boolean? =
		(array[index] as? JSONValue)?.boolean

	/**
	 * Get a [JSONNumber] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [JSONNumber] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONNumber].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getNumber(
		index: Int,
		orElse: ()->JSONNumber = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read a " +
					"number.")
		}
	): JSONNumber = array[index] as? JSONNumber ?: run(orElse)

	/**
	 * Get a [JSONNumber] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [JSONNumber] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONNumber].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getNumberOrNull(index: Int): JSONNumber? = array[index] as? JSONNumber

	/**
	 * Get a [BigInteger] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [BigInteger] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getBigInteger(
		index: Int,
		orElse: ()->BigInteger = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read a " +
					"BigInteger.")
		}
	): BigInteger = (array[index] as? JSONNumber)?.bigInteger ?: run(orElse)

	/**
	 * Get a [BigInteger] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [BigInteger] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getBigIntegerOrNull(index: Int): BigInteger? =
		(array[index] as? JSONNumber)?.bigInteger

	/**
	 * Get a [BigDecimal] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [BigDecimal] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getBigDecimal(
		index: Int,
		orElse: ()->BigDecimal = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read a " +
					"BigDecimal.")
		}
	): BigDecimal = (array[index] as? JSONNumber)?.bigDecimal ?: run(orElse)

	/**
	 * Get a [BigDecimal] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [BigDecimal] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getBigDecimalOrNull(index: Int): BigDecimal? =
		(array[index] as? JSONNumber)?.bigDecimal

	/**
	 * Get a [Long] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [Long] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getLong(
		index: Int,
		orElse: ()->Long = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read a " +
					"Long.")
		}
	): Long = (array[index] as? JSONNumber)?.long ?: run(orElse)

	/**
	 * Get a [Long] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [Long] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getLongOrNull(index: Int): Long? = (array[index] as? JSONNumber)?.long

	/**
	 * Get a [Int] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [Int] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getInt(
		index: Int,
		orElse: ()->Int = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read an " +
					"Int.")
		}
	): Int = (array[index] as? JSONNumber)?.int ?: run(orElse)

	/**
	 * Get a [Int] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [Int] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getIntOrNull(index: Int): Int? = (array[index] as? JSONNumber)?.int

	/**
	 * Get a [String] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [String] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getString(
		index: Int,
		orElse: ()->String = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read a " +
					"String.")
		}
	): String = (array[index] as? JSONValue)?.string ?: run(orElse)

	/**
	 * Get a [String] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [String] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getStringOrNull(index: Int): String? =
		(array[index] as? JSONValue)?.string

	/**
	 * Get a [JSONArray] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [JSONArray] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getArray(
		index: Int,
		orElse: ()->JSONArray = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read an " +
					"Array.")
		}
	): JSONArray = array[index] as? JSONArray ?: run(orElse)

	/**
	 * Get a [JSONArray] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [JSONArray] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getArrayOrNull(index: Int): JSONArray? = array[index] as? JSONArray

	/**
	 * Get a [JSONObject] at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @param orElse
	 *   An optional function to run if the element was `null`. If not present,
	 *   a [NullPointerException] is thrown if the element was `null`.
	 * @return
	 *   The [JSONObject] at the requested subscript, or the result of applying
	 *   [orElse] if the element was `null`.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws NullPointerException
	 *   If the requested element is `null` and [orElse] was omitted.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(
		ArrayIndexOutOfBoundsException::class,
		NullPointerException::class,
		ClassCastException::class)
	fun getObject(
		index: Int,
		orElse: ()->JSONObject = {
			throw NullPointerException(
				"Value at index, $index, was `null` when trying to read an " +
					"Object.")
		}
	): JSONObject = array[index] as? JSONObject ?: run(orElse)

	/**
	 * Get a [JSONObject] or `null` at the requested subscript.
	 *
	 * @param index
	 *   The array subscript.
	 * @return
	 *   The [JSONObject] at the requested subscript.
	 * @throws ArrayIndexOutOfBoundsException
	 *   If the subscript is out of bounds.
	 * @throws ClassCastException
	 *   If the element at the requested subscript is not a [JSONValue].
	 */
	@Throws(ArrayIndexOutOfBoundsException::class, ClassCastException::class)
	fun getObjectOrNull(index: Int): JSONObject? = array[index] as? JSONObject

	/**
	 * Extract a [List] of [BigDecimal], throwing a [ClassCastException] if any
	 * elements are non-numeric.
	 */
	val bigDecimals
		@Throws(ClassCastException::class)
		get() = array.map(JSONData::bigDecimal)

	/**
	 * Extract a [List] of [BigInteger], throwing a [ClassCastException] if any
	 * elements are non-numeric, or an [ArithmeticException] if any elements
	 * are non-integral.  The first element to fail determines which exception
	 * is thrown.
	 */
	val bigIntegers
		@Throws(ClassCastException::class, ArithmeticException::class)
		get() = array.map(JSONData::bigInteger)

	/**
	 * Extract a [List] of [Int]s, throwing a [ClassCastException] if any
	 * elements are non-numeric, or an [ArithmeticException] if any elements are
	 * non-integral or overflow an [Int].  The first element to fail determines
	 * which exception is thrown.
	 */
	val ints
		@Throws(ClassCastException::class, ArithmeticException::class)
		get() = array.map(JSONData::int)

	/**
	 * Extract a [List] of [Long]s, throwing a [ClassCastException] if any
	 * elements are non-numeric, or an [ArithmeticException] if any elements are
	 * non-integral or overflow a [Long].  The first element to fail determines
	 * which exception is thrown.
	 */
	val longs
		@Throws(ClassCastException::class, ArithmeticException::class)
		get() = array.map(JSONData::long)

	/**
	 * Extract a [List] of [Float]s, throwing a [ClassCastException] if any
	 * elements are non-numeric.
	 */
	val floats
		@Throws(ClassCastException::class)
		get() = array.map(JSONData::float)

	/**
	 * Extract a [List] of [Double]s, throwing a [ClassCastException] if any
	 * elements are non-numeric.
	 */
	val doubles
		@Throws(ClassCastException::class)
		get() = array.map(JSONData::double)

	/**
	 * Extract a [List] of [String]s, throwing a [ClassCastException] if any
	 * element is not a string.
	 */
	val strings
		@Throws(ClassCastException::class)
		get() = array.map(JSONData::string)

	/**
	 * Extract a [List] of [Boolean]s, throwing a [ClassCastException] if any
	 * element is not a boolean.
	 */
	val booleans get() = array.map(JSONData::boolean)

	override fun iterator(): ListIterator<JSONData> =
		listOf(*array).listIterator()

	override fun writeTo(writer: JSONWriter)
	{
		writer.startArray()
		for (value in array)
		{
			value.writeTo(writer)
		}
		writer.endArray()
	}

	companion object
	{
		/** The canonical [emptySet][empty] [JSONArray]. */
		private val empty = JSONArray(arrayOf())

		/**
		 * Answer an emptySet [JSONArray].
		 *
		 * @return
		 *   The `JSONArray`.
		 */
		internal fun empty(): JSONArray
		{
			return empty
		}

		/**
		 * Answer a singleton [JSONArray].
		 *
		 * @param value
		 *   The sole [element][JSONData] of the `JSONArray`.
		 * @return
		 *   The `JSONArray`.
		 */
		internal fun singleton(value: JSONData): JSONArray =
			JSONArray(arrayOf(value))
	}
}
