/*
 * JSONWriter.kt
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

import org.availlang.json.JSONWriter.JSONState.EXPECTING_FIRST_OBJECT_KEY_OR_OBJECT_END
import org.availlang.json.JSONWriter.JSONState.EXPECTING_FIRST_VALUE_OR_ARRAY_END
import org.availlang.json.JSONWriter.JSONState.EXPECTING_SINGLE_VALUE
import org.availlang.json.JSONWriter.JSONState.EXPECTING_VALUE_OR_ARRAY_END
import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Deque
import java.util.Formatter
import java.util.LinkedList

/**
 * A `JSONWriter` produces ASCII-only documents that adhere strictly to
 * ECMA 404: "The JSON Data Interchange Format".
 *
 * @author Todd L Smith &lt;todd@availlang.org&gt;
 * @author Richard Arriaga
 * @see [ECMA&#32;404:&#32;"The&#32;JSON&#32;Data&#32;Interchange&#32;Format"](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf)
 *
 * @property writer
 *   The [target][Writer] for the raw JSON document.
 * @property prettyPrint
 *   `true` indicates the JSON should be pretty-printed; `false` indicates the
 *   JSON should be minified.
 *
 * @constructor
 * Construct a new [JSONWriter].
 *
 * @param writer
 *   The [target][Writer] for the raw JSON document.
 * @param prettyPrint
 *   `true` indicates the JSON should be pretty-printed; `false` indicates the
 *   JSON should be minified.
 */
@Suppress("unused")
open class JSONWriter constructor(
	private val writer: Writer = StringWriter(),
	private val prettyPrint: Boolean = false
) : AutoCloseable
{
	/**
	 * The number of indentations there should be on each new line. Each
	 * indentation is represented by a tab `\t` character.
	 */
	private var currentIndentationLevel = 0

	/**
	 * The prefix indentation each time a new line is to be inserted.
	 */
	private var newLineAndIndentation: String = ""

	/** `true` indicates in an array; `false` otherwise. */
	private var inArray = false
	private var addedArrayObjectValue = false

	/**
	 * Update the [newLineAndIndentation] String according to the
	 * [currentIndentationLevel].
	 */
	private fun updateIndentation()
	{
		if (prettyPrint)
		{
			newLineAndIndentation =
				"\n${String(CharArray(currentIndentationLevel) { '\t' })}"
		}
	}

	/**
	 * Reduce the [currentIndentationLevel] by 1.
	 */
	private fun reduceIndentation()
	{
		if (prettyPrint)
		{
			currentIndentationLevel =
				0.coerceAtLeast(currentIndentationLevel - 1)
			updateIndentation()
		}
	}

	/**
	 * Increase the currentIndentationLevel by 1.
	 */
	private fun increaseIndentation()
	{
		currentIndentationLevel =
			0.coerceAtLeast(currentIndentationLevel + 1)
		updateIndentation()
	}

	/**
	 * If [prettyPrint] is `true`, [write][privateWrite] a
	 * [new line and indentation][newLineAndIndentation].
	 */
	private fun insertNewLine()
	{
		privateWrite(newLineAndIndentation)
	}

	/**
	 * The [stack][Deque] of [states][JSONState], to ensure correct usage of the
	 * [writer][JSONWriter].
	 */
	private val stack = LinkedList<JSONState>().apply {
		addFirst(EXPECTING_SINGLE_VALUE)
	}

	/**
	 * Answer the accumulated String contents of the [JSONWriter].
	 *
	 * @return
	 *   A String.
	 */
	fun contents(): String = writer.toString()

	/**
	 * Write the specified character to the underlying document
	 * [writer][Writer].
	 *
	 * @param c
	 *   The character to write. Only the 16 low order bits will be
	 *   written. (If assertions are enabled, then attempting to write
	 *   values that would be truncated causes an [AssertionError].)
	 * @throws JSONIOException
	 *   If the operation fails.
	 */
	@Throws(JSONIOException::class)
	internal fun privateWrite(c: Int)
	{
		assert(c and 0xFFFF == c)
		try
		{
			writer.write(c)
		}
		catch (e: IOException)
		{
			throw JSONIOException(e)
		}

	}

	/**
	 * Write the specified character to the underlying document
	 * [writer][Writer].
	 *
	 * @param c
	 *   The [Char] to write. Only the 16 low order bits will be written.
	 *   (If assertions are enabled, then attempting to write values that
	 *   would be truncated causes an [AssertionError].)
	 * @throws JSONIOException
	 *   If the operation fails.
	 */
	@Throws(JSONIOException::class)
	internal fun privateWrite(c: Char)
	{
		try
		{
			writer.write(c.toString())
		}
		catch (e: IOException)
		{
			throw JSONIOException(e)
		}

	}

	/**
	 * Write the specified text to the underlying document [writer][Writer].
	 *
	 * @param text
	 *   The text that should be written.
	 * @throws JSONIOException
	 *   If the operation fails.
	 */
	@Throws(JSONIOException::class)
	internal fun privateWrite(text: String)
	{
		try
		{
			writer.write(text)
		}
		catch (e: IOException)
		{
			throw JSONIOException(e)
		}

	}

	/**
	 * A `JSONState` represents the [writer][JSONWriter]'s view of what
	 * operations are legal based on what operations have become before.
	 */
	internal enum class JSONState
	{
		/** The [writer][JSONWriter] is expecting a single arbitrary value. */
		EXPECTING_SINGLE_VALUE
		{
			override fun nextStateAfterValue(): JSONState =
				EXPECTING_END_OF_DOCUMENT

			@Throws(IllegalStateException::class)
			override fun checkCanEndDocument()
			{
				// Do nothing.
			}
		},

		/** The [writer][JSONWriter] is expecting the end of the document. */
		EXPECTING_END_OF_DOCUMENT
		{
			override fun nextStateAfterValue(): JSONState =
				throw IllegalStateException()

			@Throws(IllegalStateException::class)
			override fun checkCanEndDocument()
			{
				// Do nothing.
			}

			@Throws(IllegalStateException::class)
			override fun checkCanWriteAnyValue() =
				throw IllegalStateException()

			@Throws(IllegalStateException::class)
			override fun checkCanWriteStringValue() =
				throw IllegalStateException()

			@Throws(IllegalStateException::class)
			override fun checkCanWriteObjectStart() =
				throw IllegalStateException()

			@Throws(IllegalStateException::class)
			override fun checkCanWriteArrayStart() =
				throw IllegalStateException()

			override val newlineBeforeWrite: Boolean = true
		},

		/**
		 * The [writer][JSONWriter] is expecting the first object key or the end
		 * of an object.
		 */
		EXPECTING_FIRST_OBJECT_KEY_OR_OBJECT_END
		{
			override fun nextStateAfterValue(): JSONState =
				EXPECTING_OBJECT_VALUE

			@Throws(IllegalStateException::class)
			override fun checkCanWriteAnyValue() =
				throw IllegalStateException()

			@Throws(IllegalStateException::class)
			override fun checkCanWriteObjectStart() =
				throw IllegalStateException()

			override fun checkCanWriteObjectEnd()
			{
				// Do nothing.
			}

			@Throws(IllegalStateException::class)
			override fun checkCanWriteArrayStart() =
				throw IllegalStateException()

			override val newlineBeforeWrite: Boolean = true
		},

		/**
		 * The [writer][JSONWriter] is expecting an object key or the end of an
		 * object.
		 */
		EXPECTING_OBJECT_KEY_OR_OBJECT_END
		{
			override fun nextStateAfterValue(): JSONState =
				EXPECTING_OBJECT_VALUE

			@Throws(IllegalStateException::class)
			override fun checkCanWriteAnyValue() =
				throw IllegalStateException()

			@Throws(IllegalStateException::class)
			override fun checkCanWriteObjectStart() =
				throw IllegalStateException()

			override fun checkCanWriteObjectEnd()
			{
				// Do nothing.
			}

			@Throws(IllegalStateException::class)
			override fun checkCanWriteArrayStart() =
				throw IllegalStateException()

			@Throws(JSONIOException::class)
			override fun writePrologueTo(writer: JSONWriter) =
				if (writer.prettyPrint)
				{
					writer.privateWrite(", ")
				}
				else
				{
					writer.privateWrite(',')
				}

			override val newlineBeforeWrite: Boolean = true
		},

		/**
		 * The [writer][JSONWriter] is expecting an object value.
		 */
		EXPECTING_OBJECT_VALUE
		{
			override fun nextStateAfterValue(): JSONState =
				EXPECTING_OBJECT_KEY_OR_OBJECT_END

			@Throws(JSONIOException::class)
			override fun writePrologueTo(writer: JSONWriter) =
				if (writer.prettyPrint)
				{
					writer.privateWrite(": ")
				}
				else
				{
					writer.privateWrite(':')
				}
		},

		/**
		 * The [writer][JSONWriter] is expecting the first value of an array or
		 * the end of an array.
		 */
		EXPECTING_FIRST_VALUE_OR_ARRAY_END
		{
			override fun nextStateAfterValue(): JSONState =
				EXPECTING_VALUE_OR_ARRAY_END

			override fun checkCanWriteArrayEnd()
			{
				// Do nothing.
			}
		},

		/**
		 * The [writer][JSONWriter] is expecting an arbitrary value or the end
		 * of a JSON array.
		 */
		EXPECTING_VALUE_OR_ARRAY_END
		{
			override fun nextStateAfterValue(): JSONState =
				EXPECTING_VALUE_OR_ARRAY_END

			override fun checkCanWriteArrayEnd()
			{
				// Do nothing.
			}

			@Throws(JSONIOException::class)
			override fun writePrologueTo(writer: JSONWriter) =
				writer.privateWrite(',')
		};

		/**
		 * Check that the receiver permits the [writer][JSONWriter] to be
		 * [closed][JSONWriter.close].
		 *
		 * @throws IllegalStateException
		 *   If the document cannot be closed yet.
		 */
		@Throws(IllegalStateException::class)
		internal open fun checkCanEndDocument(): Unit =
			throw IllegalStateException()

		/**
		 * Check that the receiver permits the [writer][JSONWriter] to emit an
		 * arbitrary value.
		 *
		 * @throws IllegalStateException
		 *   If an arbitrary value cannot be written.
		 */
		@Throws(IllegalStateException::class)
		internal open fun checkCanWriteAnyValue()
		{
			// Do nothing.
		}

		/**
		 * Check that the receiver permits the [writer][JSONWriter] to emit a
		 * [value][String].
		 *
		 * @throws IllegalStateException
		 *   If an arbitrary value cannot be written.
		 */
		@Throws(IllegalStateException::class)
		internal open fun checkCanWriteStringValue()
		{
			// Do nothing.
		}

		/**
		 * Check that the receiver permits the [writer][JSONWriter] to emit an
		 * object beginning.
		 *
		 * @throws IllegalStateException
		 *   If an object beginning cannot be written.
		 */
		@Throws(IllegalStateException::class)
		internal open fun checkCanWriteObjectStart()
		{
			// Do nothing.
		}

		/**
		 * Check that the receiver permits the [writer][JSONWriter] to emit an
		 * object ending.
		 *
		 * @throws IllegalStateException
		 *   If an object ending cannot be written.
		 */
		@Throws(IllegalStateException::class)
		internal open fun checkCanWriteObjectEnd()
		{
			throw IllegalStateException()
		}

		/**
		 * Check that the receiver permits the [writer][JSONWriter] to emit an
		 * array beginning.
		 *
		 * @throws IllegalStateException
		 *   If an array beginning cannot be written.
		 */
		@Throws(IllegalStateException::class)
		internal open fun checkCanWriteArrayStart()
		{
			// Do nothing.
		}

		/**
		 * Check that the receiver permits the [writer][JSONWriter] to emit an
		 * array ending.
		 *
		 * @throws IllegalStateException
		 *   If an array ending cannot be written.
		 */
		@Throws(IllegalStateException::class)
		internal open fun checkCanWriteArrayEnd(): Unit =
			throw IllegalStateException()

		/**
		 * Answer the next [state][JSONState] following the writing of a value.
		 *
		 * @return
		 *   The next state.
		 */
		internal abstract fun nextStateAfterValue(): JSONState

		/**
		 * Write any prologue required before a value has been written, given
		 * that the [receiver][JSONState] is the current state.
		 *
		 * @param writer
		 *   A writer.
		 * @throws JSONIOException
		 *   If an I/O exception occurs.
		 */
		@Throws(JSONIOException::class)
		internal open fun writePrologueTo(writer: JSONWriter)
		{
			// Do nothing.
		}

		/**
		 * When pretty-printing, can this element be started on the next line?
		 *
		 * @return
		 *   `true` indicates yes; `false` indicates must be on same line.
		 */
		open val newlineBeforeWrite: Boolean = false
	}

	/** The current [JSONState] of this [JSONWriter]. */
	private val currentState: JSONState get() = stack.peekFirst()

	/**
	 * Output an element that has already been rendered as a String.
	 * Respect any necessary prologue and pretty-printing.
	 *
	 * @param text
	 *   The pre-rendered text to output.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 */
	@Throws(JSONIOException::class)
	private fun privateWriteItem(text: String)
	{
		val state = stack.removeFirst()
		state.checkCanWriteAnyValue()
		state.writePrologueTo(this)
		if (prettyPrint
			&& state.newlineBeforeWrite
			&& state != EXPECTING_VALUE_OR_ARRAY_END)
		{
			insertNewLine()
		}
		privateWrite(text)
		stack.addFirst(state.nextStateAfterValue())
	}

	/**
	 * Write a JSON `null` to the underlying document [writer][Writer].
	 *
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	open fun writeNull() = privateWriteItem((null as Any?).toString())

	/**
	 * Write the specified `Boolean` to the underlying document [writer][Writer]
	 * as a JSON boolean.
	 *
	 * @param value
	 *   A `Boolean` value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	open fun write(value: Boolean) =
		privateWriteItem(java.lang.Boolean.toString(value))

	/**
	 * Write the specified [BigDecimal] to the underlying document
	 * [writer][Writer] as a JSON number.
	 *
	 * @param value
	 *   A `BigDecimal` value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	open fun write(value: BigDecimal) = privateWriteItem(value.toString())

	/**
	 * Write the specified [BigInteger] to the underlying document
	 * [writer][Writer] as a JSON number.
	 *
	 * @param value
	 *   A `BigInteger` value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun write(value: BigInteger) = privateWriteItem(value.toString())

	/**
	 * Write the specified [Int] to the underlying document [writer][Writer] as
	 * a JSON number.
	 *
	 * @param value
	 *   A [Int] value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun write(value: Int) = privateWriteItem(value.toString())

	/**
	 * Write the specified `Long` to the underlying document [writer][Writer]
	 * as a JSON number.
	 *
	 * @param value
	 *   A `Long` value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun write(value: Long) = privateWriteItem(value.toString())

	/**
	 * Write the specified `Float` to the underlying document [writer][Writer]
	 * as a JSON number.
	 *
	 * @param value
	 *   A `Float` value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun write(value: Float) =
		privateWriteItem(
			when
			{
				value == java.lang.Float.POSITIVE_INFINITY -> "Infinity"
				value == java.lang.Float.NEGATIVE_INFINITY -> "-Infinity"
				java.lang.Float.isNaN(value) -> "NaN"
				else -> value.toString()
			})

	/**
	 * Write the specified `Double` to the underlying document [writer][Writer]
	 * as a JSON number. Use JSON 5 extensions (and an additional NaN
	 * extension).
	 *
	 * **NOTE:** The number is only written to 10^-6 precision.
	 *
	 * @param value
	 *   A `Double` value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun write(value: Double) =
		privateWriteItem(
			when
			{
				value == java.lang.Double.POSITIVE_INFINITY -> "Infinity"
				value == java.lang.Double.NEGATIVE_INFINITY -> "-Infinity"
				java.lang.Double.isNaN(value) -> "NaN"
				else -> value.toString()
			})

	/**
	 * Write the specified [String] to the underlying document [writer][Writer]
	 * as a JSON string. All non-ASCII characters are encoded as Unicode escape
	 * sequences, so only ASCII characters will be written.
	 *
	 * @param pattern
	 *   A [pattern][Formatter] `String` (may be `null`).
	 * @param args
	 *   The arguments to the pattern.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun format(pattern: String?, vararg args: Any)
	{
		if (pattern === null)
		{
			writeNull()
		}
		else
		{
			val state = stack.removeFirst()
			state.checkCanWriteStringValue()
			state.writePrologueTo(this)
			if (prettyPrint
				&& state.newlineBeforeWrite
				&& state != EXPECTING_VALUE_OR_ARRAY_END)
			{
				insertNewLine()
			}
			val value = String.format(pattern, *args)
			privateWrite('"')
			var codePoint: Int
			var i = 0
			val size = value.length
			while (i < size)
			{
				codePoint = value.codePointAt(i)
				when
				{
					Character.isISOControl(codePoint) ->
					{
						when (codePoint)
						{
							// Backspace.
							'\b'.code -> privateWrite("\\b")
							// Character tabulation.
							'\t'.code -> privateWrite("\\t")
							// Line feed.
							'\n'.code -> privateWrite("\\n")
							// Form feed.
							'\u000C'.code -> privateWrite("\\f")
							// Carriage return.
							'\r'.code -> privateWrite("\\r")
							else -> privateWrite(
								String.format(
									"\\u%04X",
									codePoint))
						}
					}
					codePoint == '\\'.code -> privateWrite("\\\\")
					codePoint == '"'.code -> privateWrite("\\\"")
					codePoint < 128 ->
						// Even though Writer doesn't work on general code
						// points, we have just proven that the code point is
						// ASCII, so this is fine.
						privateWrite(codePoint)
					Character.isSupplementaryCodePoint(codePoint) ->
						// Supplementary code points need to be written as two
						// Unicode escapes.
						privateWrite(
							String.format(
								"\\u%04X\\u%04X",
								Character.highSurrogate(codePoint).code,
								Character.lowSurrogate(codePoint).code))
					else ->
						// Force all non-ASCII characters to Unicode escape
						// sequences.
						privateWrite(String.format("\\u%04X", codePoint))
				}
				i += Character.charCount(codePoint)
			}
			privateWrite('"')
			stack.addFirst(state.nextStateAfterValue())
		}
	}

	/**
	 * Write the specified [String] to the underlying document [writer][Writer]
	 * as a JSON string. All non-ASCII characters are encoded as Unicode escape
	 * sequences, so only ASCII characters will be written.
	 *
	 * @param value
	 *   A `String` (may be `null`).
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	fun write(value: String?)
	{
		if (value === null)
		{
			writeNull()
		}
		else
		{
			format("%s", value)
		}
	}

	/**
	 * Write the contents of the specified [JSONWriter] to the underlying
	 * document as a JSON value. This is only permitted whenever an arbitrary
	 * JSON value would be permitted.
	 *
	 * @param other
	 *   Another `JSONWriter`.
	 */
	fun write(other: JSONWriter)
	{
		val state = stack.removeFirst()
		state.checkCanWriteAnyValue()
		state.writePrologueTo(this)
		privateWrite(other.toString())
		stack.addFirst(state.nextStateAfterValue())
	}

	/**
	 * Write the specified [JSON-friendly][JSONFriendly] value to the underlying
	 * document [writer][Writer].
	 *
	 * @param friendly
	 *   A [JSONFriendly] value.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an arbitrary value cannot be written.
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun write(friendly: JSONFriendly)
	{
		friendly.writeTo(this)
	}

	/**
	 * Write an object beginning to the underlying document [writer][Writer].
	 *
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an object beginning cannot be written.
	 */
	@Throws(JSONIOException::class)
	fun startObject()
	{
		val state = currentState
		state.checkCanWriteObjectStart()
		state.writePrologueTo(this)
		if (prettyPrint && inArray)
		{
			insertNewLine()
			addedArrayObjectValue = true
		}
		privateWrite('{')
		if (prettyPrint)
		{
			increaseIndentation()
		}
		stack.addFirst(EXPECTING_FIRST_OBJECT_KEY_OR_OBJECT_END)
	}

	/**
	 * Write an object ending to the underlying document [writer][Writer].
	 *
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an object ending cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun endObject()
	{
		val state = stack.removeFirst()
		state.checkCanWriteObjectEnd()
		if (prettyPrint)
		{
			reduceIndentation()
			insertNewLine()
		}
		privateWrite('}')
		stack.addFirst(stack.removeFirst().nextStateAfterValue())
	}

	/**
	 * Write an object, using an action to supply the contents.
	 *
	 * @param R
	 *   The type of value, if any, returned by the action.
	 * @param action
	 *   An action that writes the contents of an object.
	 * @return
	 *   The value, if any, returned by the action.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an object cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	inline fun <R> writeObject(action: JSONWriter.()->R): R
	{
		startObject()
		try
		{
			return this.action()
		}
		finally
		{
			endObject()
		}
	}

	/**
	 * Write an array beginning to the underlying document [writer][Writer].
	 *
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array beginning cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun startArray()
	{
		val state = currentState
		state.checkCanWriteArrayStart()
		state.writePrologueTo(this)
		privateWrite('[')
		if (prettyPrint)
		{
			increaseIndentation()
			inArray = true
			addedArrayObjectValue = false
		}
		stack.addFirst(EXPECTING_FIRST_VALUE_OR_ARRAY_END)
	}

	/**
	 * Write an array ending to the underlying document [writer][Writer].
	 *
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array ending cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun endArray()
	{
		val state = stack.removeFirst()
		state.checkCanWriteArrayEnd()
		if (prettyPrint)
		{
			reduceIndentation()
			if (addedArrayObjectValue) { insertNewLine() }
		}
		privateWrite(']')
		stack.addFirst(stack.removeFirst().nextStateAfterValue())
		if (prettyPrint)
		{
			addedArrayObjectValue = false
			inArray = false
		}
	}

	/**
	 * Write an array, using an action to supply the contents.
	 *
	 * @param action
	 *   An action that writes the contents of an array.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	inline fun <R> writeArray(action: JSONWriter.()->R): R
	{
		startArray()
		try
		{
			return this.action()
		}
		finally
		{
			endArray()
		}
	}

	/**
	 * Write an array of integer values.
	 *
	 * @param values
	 *   An [Iterator] of [Int] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeInts(values: Iterator<Int>) = writeArray {
		values.forEach(::write)
	}

	/**
	 * Write an array of long values.
	 *
	 * @param values
	 *   An [Iterator] of [Long] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeLongs(values: Iterator<Long>) = writeArray {
		values.forEach(::write)
	}

	/**
	 * Write an array of float values.
	 *
	 * @param values
	 *   An [Iterator] of [Float] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeFloats(values: Iterator<Float>) = writeArray {
		values.forEach(::write)
	}

	/**
	 * Write an array of double values.
	 *
	 * @param values
	 *   An [Iterator] of [Double] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeDoubles(values: Iterator<Double>) = writeArray {
		values.forEach(::write)
	}

	/**
	 * Write an array of boolean values.
	 *
	 * @param values
	 *   An [Iterator] of [Boolean] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeBooleans(values: Iterator<Boolean>) = writeArray {
		values.forEach(::write)
	}

	/**
	 * Write an array of string values.
	 *
	 * @param values
	 *   An [Iterator] of [String] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeStrings(values: Iterator<String>) = writeArray {
		values.forEach(::write)
	}

	/**
	 * Write an array of [JSON-friendly][JSONFriendly] values.
	 *
	 * @param values
	 *   An [Iterator] of [JSONFriendly] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeArray(values: Iterator<JSONFriendly>) = writeArray {
		values.forEach(::write)
	}

	/**
	 * Write an array of integer values.
	 *
	 * @param values
	 *   An [Iterable] of [Int] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeInts(values: Iterable<Int>) = writeInts(values.iterator())

	/**
	 * Write an array of long values.
	 *
	 * @param values
	 *   An [Iterable] of [Long] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeLongs(values: Iterable<Long>) = writeLongs(values.iterator())

	/**
	 * Write an array of float values.
	 *
	 * @param values
	 *   An [Iterable] of [Float] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeFloats(values: Iterable<Float>) = writeFloats(values.iterator())

	/**
	 * Write an array of double values.
	 *
	 * @param values
	 *   An [Iterable] of [Double] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeDoubles(values: Iterable<Double>) = writeDoubles(values.iterator())

	/**
	 * Write an array of boolean values.
	 *
	 * @param values
	 *   An [Iterable] of [Boolean] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeBooleans(values: Iterable<Boolean>) =
		writeBooleans(values.iterator())

	/**
	 * Write an array of string values.
	 *
	 * @param values
	 *   An [Iterable] of [String] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeStrings(values: Iterable<String>) = writeStrings(values.iterator())

	/**
	 * Write an array of [JSON-friendly][JSONFriendly] values.
	 *
	 * @param values
	 *   An [Iterable] of [JSONFriendly] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeArray(values: Iterable<JSONFriendly>) =
		writeArray(values.iterator())

	/**
	 * Write an array of integer values.
	 *
	 * @param values
	 *   An [Sequence] of [Int] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeInts(values: Sequence<Int>) = writeInts(values.iterator())

	/**
	 * Write an array of long values.
	 *
	 * @param values
	 *   An [Sequence] of [Long] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeLongs(values: Sequence<Long>) = writeLongs(values.iterator())

	/**
	 * Write an array of float values.
	 *
	 * @param values
	 *   An [Sequence] of [Float] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeFloats(values: Sequence<Float>) = writeFloats(values.iterator())

	/**
	 * Write an array of double values.
	 *
	 * @param values
	 *   An [Sequence] of [Double] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeDoubles(values: Sequence<Double>) = writeDoubles(values.iterator())

	/**
	 * Write an array of boolean values.
	 *
	 * @param values
	 *   An [Sequence] of [Boolean] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeBooleans(values: Sequence<Boolean>) =
		writeBooleans(values.iterator())

	/**
	 * Write an array of string values.
	 *
	 * @param values
	 *   An [Sequence] of [String] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeStrings(values: Sequence<String>) = writeStrings(values.iterator())

	/**
	 * Write an array of [JSON-friendly][JSONFriendly] values.
	 *
	 * @param values
	 *   An [Sequence] of [JSONFriendly] values.
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 * @throws IllegalStateException
	 *   If an array cannot be written.
	 */
	@Throws(JSONIOException::class, IllegalStateException::class)
	fun writeArray(values: Sequence<JSONFriendly>) =
		writeArray(values.iterator())

	/**
	 * Write the given non-null [String] as the name of an entity, then
	 * evaluate the action, generally to write the associated value.
	 *
	 * @param R
	 *   The type of value, if any, returned by the action.
	 * @param key
	 *   The key [String] to write first.
	 * @param action
	 *   The action to invoke to do additional writing.
	 * @return
	 *   The value, if any, returned by the action.
	 */
	inline fun <reified R> at(key: String, action: JSONWriter.()->R): R
	{
		write(key)
		return this.action()
	}

	/**
	 * Flush any buffered data to the underlying document [writer][Writer].
	 *
	 * @throws JSONIOException
	 *   If an I/O exception occurs.
	 */
	@Throws(JSONIOException::class)
	fun flush()
	{
		try
		{
			writer.flush()
		}
		catch (e: IOException)
		{
			throw JSONIOException(e)
		}

	}

	@Throws(JSONIOException::class, IllegalStateException::class)
	override fun close()
	{
		// Don't actually remove the remaining state; we want any errant
		// subsequent operations to fail appropriately, not because the stack is
		// empty.
		val state = currentState
		state.checkCanEndDocument()
		assert(stack.size == 1)
		flush()
		try
		{
			writer.close()
		}
		catch (e: IOException)
		{
			throw JSONIOException(e)
		}

	}

	@Throws(IllegalStateException::class)
	override fun toString(): String
	{
		try
		{
			val state = currentState
			state.checkCanEndDocument()
			return writer.toString()
		}
		catch (e: Throwable)
		{
			// Do not allow an exception to derail the stringification
			// operation.
			return String.format(
				"BROKEN: %s <%s>: %s",
				e.javaClass.name,
				e.message,
				writer.toString())
		}
	}

	companion object
	{
		/**
		 * Answer a [JSONWriter] that targets an internal [StringWriter].
		 *
		 * @return
		 *   A `JSONWriter`.
		 */
		fun newWriter(): JSONWriter = JSONWriter(StringWriter())

		/**
		 * Answer a [JSONWriter] that targets an internal [StringWriter] and
		 * pretty-prints the content.
		 *
		 * @return
		 *   A `JSONWriter`.
		 */
		fun newPrettyPrinterWriter(): JSONWriter =
			JSONWriter(prettyPrint = true)
	}
}
