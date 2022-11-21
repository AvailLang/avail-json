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
}
