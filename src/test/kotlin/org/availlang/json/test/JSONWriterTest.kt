/*
 * JSONWriterTest.kt
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

package org.availlang.json.test

import org.availlang.json.EndOfDocumentException
import org.availlang.json.ExpectingObjectKeyOrObjectEndException
import org.availlang.json.ExpectingObjectValueException
import org.availlang.json.ExpectingSingleArbitraryValueException
import org.availlang.json.ExpectingValueOrArrayEndException
import org.availlang.json.JSONException
import org.availlang.json.JSONObject
import org.availlang.json.JSONReader
import org.availlang.json.JSONWriter
import org.availlang.json.JSONWriter.Companion.jsonString
import org.availlang.json.json
import org.availlang.json.test.TestJSONKeyValue.Companion.addObjectToWriter
import org.availlang.json.test.TestJSONKeyValue.Companion.addToWriter
import org.availlang.json.test.TestJSONKeyValue.Companion.test
import org.availlang.json.test.TestJSONKeyValue.IMACOMPACTARRAY
import org.availlang.json.test.TestJSONKeyValue.IMAFALSE
import org.availlang.json.test.TestJSONKeyValue.IMAFLOAT
import org.availlang.json.test.TestJSONKeyValue.IMALONG
import org.availlang.json.test.TestJSONKeyValue.IMANINT
import org.availlang.json.test.TestJSONKeyValue.IMANOBJECT
import org.availlang.json.test.TestJSONKeyValue.IMANULL
import org.availlang.json.test.TestJSONKeyValue.IMASTRING
import org.availlang.json.test.TestJSONKeyValue.IMATERMINATEDSTRING
import org.availlang.json.test.TestJSONKeyValue.IMATRUE
import org.availlang.json.test.TestJSONKeyValue.OBJINT
import org.availlang.json.test.TestJSONKeyValue.OBJSTRING
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.StringReader

/**
 * A `JSONWriterTest` contains unit tests for the [JSONWriter].
 *
 * @author Richard Arriaga &lt;rich@availlang.org&gt;
 */
class JSONWriterTest
{
	/**
	 * Answer a [JSONObject] from the provided [StringBuilder].
	 *
	 * @param writer
	 *   The `StringBuilder` that contains the JSON payload.
	 * @return
	 *   A `JSONObject`.
	 */
	private fun getJsonData(writer: JSONWriter): JSONObject?
	{
		try
		{
			JSONReader(
				StringReader(writer.toString())).use { reader ->
				return reader.read() as JSONObject?
					?: error("The payload should not be empty!")
			}
		}
		catch (e: JSONException)
		{
			fail<Any>(
				"The following test JSON could not be " +
					"parsed:\n${writer.contents()}")
		}
		catch (e: IllegalStateException)
		{
			fail<Any>(
				"The following test JSON could not be created due to an " +
					"exception:\n${writer.contents()}")
		}
		catch (e: IOException)
		{
			fail<Any>(
				"The following test JSON could not be created due to an " +
					"exception:\n${writer.contents()}")
		}

		return null // Shouldn't get here
	}

	/**
	 * Display the test payload to screen.
	 *
	 * @param writer
	 *   The [JSONWriter] with the test JSON.
	 * @param printTestPayload
	 *   `true` indicates the `sb` should be printed to standard out; `false`
	 *   otherwise.
	 */
	private fun displayTestPayload(
		writer: JSONWriter,
		printTestPayload: Boolean)
	{
		//noinspection ConstantConditions,ConstantIfStatement
		if (printTestPayload)
		{
			println("Test Payload\n============\n$writer\n")
		}
	}

	@Test
	@DisplayName("Correctly built JSON")
	internal fun correctlyBuiltJSONTest()
	{
		val writer = JSONWriter()
		writer.startObject()
		addToWriter(
			writer, IMASTRING, IMANINT, IMALONG, IMAFLOAT, IMATRUE,
			IMAFALSE, IMANULL, IMACOMPACTARRAY, IMATERMINATEDSTRING)
		IMASTRING.addValueToWriter(writer)
		addObjectToWriter(IMANOBJECT.key, writer, OBJSTRING, OBJINT)
		writer.endObject()
		val content = getJsonData(writer)
		test(
			content!!, IMASTRING, IMANINT, IMALONG, IMAFLOAT, IMATRUE,
			IMAFALSE, IMANULL, IMACOMPACTARRAY, IMANOBJECT, IMATERMINATEDSTRING)
		val objContent = content.getObject(IMANOBJECT.key)
		test(objContent, OBJSTRING, OBJINT)
		displayTestPayload(writer, false)
	}

	@Test
	@DisplayName("Test Failure: Close an array when not expected")
	internal fun inappropriateCloseArray()
	{
		val writer = JSONWriter()
		writer.startObject()
		assertThrows(ExpectingObjectKeyOrObjectEndException::class.java)
			{ writer.endArray() }
		displayTestPayload(writer, false)
	}

	@Test
	@DisplayName("Test Failure: Close an object when not expected")
	internal fun inappropriateCloseObject()
	{
		val writer = JSONWriter()
		writer.startArray()
		assertThrows(ExpectingValueOrArrayEndException::class.java)
		{ writer.endObject() }
		displayTestPayload(writer, false)
	}

	@Test
	@DisplayName("Test Failure: Write value when not expected")
	internal fun inappropriateObjectValue()
	{
		val writer = JSONWriter()
		writer.startObject()
		assertThrows(ExpectingObjectKeyOrObjectEndException::class.java)
		{ writer.write(5) }
		displayTestPayload(writer, false)
	}

	@Test
	@DisplayName("Test Failure: Close an object when object value expected")
	internal fun inappropriateObjectClose()
	{
		val writer = JSONWriter()
		writer.startObject()
		writer.write("foo")
		assertThrows(ExpectingObjectValueException::class.java)
		{ writer.endObject() }
		displayTestPayload(writer, false)
	}

	@Test
	@DisplayName("Test Failure: Started a document without a valid JSON type")
	internal fun inappropriateDocumentStart()
	{
		val writer = JSONWriter()
		assertThrows(ExpectingSingleArbitraryValueException::class.java)
			{ writer.endObject() }
	}

	@Test
	@DisplayName("Test Failure: Wrote to document after document end")
	internal fun inappropriateWriteAfterEnd()
	{
		val writer = JSONWriter()
		writer.writeObject {  }
		assertThrows(EndOfDocumentException::class.java)
		{ writer.write(5) }
	}

	@Test
	@DisplayName("Test Map Utilities")
	internal fun testUtilities()
	{
		val writer = JSONWriter()
		val m1 = mapOf("a" to 5, "b" to 10)
		val m2 = mapOf("a" to listOf(5, 2), "b" to listOf(25, 22))
		val m3 = mapOf("a" to true, "b" to false)
		val m4 = mapOf("a" to listOf(false, true), "b" to listOf(false, true))
		val i1: Iterator<Pair<String, Long>> =
			listOf("a" to 99L, "b" to 88L).iterator()
		writer.writeArray {
			writeMapInt(m1)
			writeMapInts(m2)
			writeMapBoolean(m3)
			writeMapBooleans(m4)
			write("foo")
			write("foo")
			writeInts(listOf(11,12,15))
			writePairsAsArrayOfPairs(i1) { f, s -> f.json to s.json }
		}
		val expected = """[{"a":5,"b":10},{"a":[5,2],"b":[25,22]},{"a":true,"b":false},{"a":[false,true],"b":[false,true]},"foo","foo",[11,12,15],[["a",99],["b",88]]]"""
		assertEquals(expected, writer.toString())
	}

	/**
	 * Test the jsonString() static function.
	 */
	@Test
	internal fun testJsonString()
	{
		val actual = jsonString {
			writeMapInt(mapOf("a" to 5, "b" to 10, "c" to 20))
		}
		val expected = """{"a":5,"b":10,"c":20}"""
		assertEquals(expected, actual)
	}

	/**
	 * Test the jsonString() static function with pretty-printing.
 	 */
	@Test
	internal fun testPrettyJsonString()
	{
		val actual = jsonString(true) {
			writeMapInt(mapOf("a" to 5, "b" to 10, "c" to 20))
		}
		val expected = """
			{
				"a": 5,
				"b": 10,
				"c": 20
			}
			""".trimIndent()
		assertEquals(expected, actual)
	}
}
