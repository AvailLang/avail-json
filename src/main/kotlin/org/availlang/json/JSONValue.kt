/*
 * JSONValue.kt
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

/**
 * A `JSONValue` is an arbitrary JSON value. `JSONValue`s are produced by a
 * [JSONReader].
 *
 * @author Todd L Smith &lt;todd@availlang.org&gt;
 */
class JSONValue : JSONData
{
	/** The [Object]. */
	private val value: Any

	override val isBoolean: Boolean
		get() = value is Boolean

	/**
	 * Extract a `Boolean`.
	 *
	 * @return
	 *   A `Boolean`.
	 * @throws ClassCastException
	 *   If the value is not a `Boolean`.
	 */
	override val boolean: Boolean
		@Throws(ClassCastException::class)
		get() = value as Boolean

	override val isString: Boolean
		get() = value is String

	/**
	 * Extract a [String].
	 *
	 * @return
	 *   A `String`.
	 * @throws ClassCastException
	 *   If the value is not a `String`.
	 */
	override val string: String
		@Throws(ClassCastException::class)
		get() = value as String

	/**
	 * Construct a new [JSONValue].
	 *
	 * @param value
	 *   The value.
	 */
	constructor(value: Boolean)
	{
		this.value = value
	}

	/**
	 * Construct a new [JSONValue].
	 *
	 * @param value
	 *   The value.
	 */
	constructor(value: String)
	{
		this.value = value
	}

	override fun writeTo(writer: JSONWriter)
	{
		if (value is Boolean)
		{
			writer.write(boolean)
		}
		else
		{
			assert(value is String)
			writer.write(string)
		}
	}

	companion object
	{
		/** The sole JSON `false`. */
		val jsonFalse = JSONValue(false)

		/** The sole JSON `true`. */
		val jsonTrue = JSONValue(true)
	}
}
