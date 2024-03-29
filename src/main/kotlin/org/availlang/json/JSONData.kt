/*
 * JSONData.kt
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
import kotlin.jvm.Throws

/**
 * `JSONData` is the superclass of [JSONValue], [JSONNumber], [JSONArray], and
 * [JSONObject]. A [JSONReader] [reads][JSONReader.read] a single `JSONData`.
 *
 * @author Todd L Smith &lt;todd@availlang.org&gt;
 */
sealed class JSONData : JSONFriendly
{
	/**
	 * Is the [receiver][JSONData] a JSON null?
	 *
	 * @return
	 *   `true` if the receiver is a JSON null, `false` otherwise.
	 */
	open val isNull: Boolean
		get() = false

	/**
	 * Is the [receiver][JSONData] a JSON boolean?
	 *
	 * @return
	 *   `true` if the receiver is a JSON boolean, `false otherwise.
	 */
	open val isBoolean: Boolean
		get() = false

	/**
	 * Is the [receiver][JSONData] a [JSON][JSONNumber]?
	 *
	 * @return
	 *   `true` if the receiver is a JSON number, `false` otherwise.
	 */
	open val isNumber: Boolean
		get() = false

	/**
	 * Is the [receiver][JSONData] a JSON string?
	 *
	 * @return
	 *   `true` if the receiver is a JSON string, `false` otherwise.
	 */
	open val isString: Boolean
		get() = false

	/**
	 * Is the [receiver][JSONData] a [JSON][JSONArray]?
	 *
	 * @return
	 *   `true` if the receiver is a JSON array, `false` otherwise.
	 */
	open val isArray: Boolean
		get() = false

	/**
	 * Is the [receiver][JSONData] a [JSON][JSONObject]?
	 *
	 * @return
	 *   `true` if the receiver is a JSON object, `false` otherwise.
	 */
	open val isObject: Boolean
		get() = false

	/**
	 * If this is a [JSONNumber], extract its value as a [BigDecimal], otherwise
	 * throw a [ClassCastException].
	 */
	open val bigDecimal: BigDecimal get() = throw ClassCastException()

	/**
	 * If this is a [JSONNumber], extract its value as a [BigInteger], otherwise
	 * throw a [ClassCastException].  If the value has a non-zero fractional
	 * part, throw an [ArithmeticException].
	 */
	open val bigInteger: BigInteger
		@Throws(ClassCastException::class, ArithmeticException::class)
		get() = throw ClassCastException()

	/**
	 * If this is a [JSONNumber], extract its value as an [Int], otherwise
	 * throw a [ClassCastException].  If the value has a non-zero fractional
	 * part or is out of range, throw an [ArithmeticException].
	 */
	open val int: Int
		@Throws(ClassCastException::class, ArithmeticException::class)
		get() = throw ClassCastException()

	/**
	 * If this is a [JSONNumber], extract its value as a [Long], otherwise
	 * throw a [ClassCastException].  If the value has a non-zero fractional
	 * part or is out of range, throw an [ArithmeticException].
	 */
	open val long: Long
		@Throws(ClassCastException::class, ArithmeticException::class)
		get() = throw ClassCastException()

	/**
	 * If this is a [JSONNumber], extract its value as a [Float], otherwise
	 * throw a [ClassCastException].
	 */
	open val float: Float
		@Throws(ClassCastException::class)
		get() = throw ClassCastException()

	/**
	 * If this is a [JSONNumber], extract its value as a [Double], otherwise
	 * throw a [ClassCastException].
	 */
	open val double: Double
		@Throws(ClassCastException::class)
		get() = throw ClassCastException()

	/**
	 * Extract a [Boolean], or throw a [ClassCastException] if the value is not
	 * a boolean.
	 */
	open val boolean: Boolean
		@Throws(ClassCastException::class)
		get() = throw ClassCastException()

	/**
	 * Extract a [String], or throw a [ClassCastException] if the value is not
	 * a string.
	 */
	open val string: String
		@Throws(ClassCastException::class)
		get() = throw ClassCastException()
}

/**
 * The sole JSON null value.
 */
object JSONNull: JSONData()
{
	override val isNull: Boolean
		get() = true

	override fun writeTo(writer: JSONWriter)
	{
		writer.writeNull()
	}
}
