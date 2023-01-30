/*
 * utility.kt
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

import java.io.Reader
import java.io.StringReader

/**
 * Answer a [JSONWriter] that has had the provided [writerAction] applied to it.
 *
 * @param writerAction
 *   A lambda that accepts the created and returned [JSONWriter].
 * @return
 *   A [JSONWriter].
 */
fun jsonWriter (
	writerAction: JSONWriter.() -> Unit = {}
): JSONWriter = JSONWriter.newWriter().apply(writerAction)

/**
 * Answer a pretty-print [JSONWriter] that has had the provided [writerAction]
 * applied to it.
 *
 * @param writerAction
 *   A lambda that accepts the created and returned [JSONWriter].
 * @return
 *   A [JSONWriter].
 */
fun jsonPrettyPrintWriter (
	writerAction: JSONWriter.() -> Unit = {}
): JSONWriter = JSONWriter.newPrettyPrinterWriter().apply(writerAction)

/**
 * Answer a [JSONData] that has had the provided [dataAction] applied to it.
 *
 * @param reader
 *   The [Reader] that contains the JSON content.
 * @param dataAction
 *   A lambda that accepts the created and returned [JSONData].
 * @return
 *   A [JSONData].
 */
@Suppress("unused")
fun jsonData (
	reader: Reader,
	dataAction: JSONData.() -> Unit ={}
): JSONData = JSONReader(reader).read().apply(dataAction)

/**
 * Answer a [JSONObject] that has had the provided [objectAction] applied to it.
 *
 * @param reader
 *   The [Reader] that contains the JSON content.
 * @param objectAction
 *   A lambda that accepts the created and returned [JSONObject].
 * @return
 *   A [JSONObject].
 */
@Suppress("unused")
fun jsonObject (
	reader: Reader,
	objectAction: JSONObject.() -> Unit = {}
): JSONObject = (JSONReader(reader).read() as JSONObject).apply(objectAction)


/**
 * Answer a [JSONObject] that has had the provided [objectAction] applied to it.
 *
 * @param raw
 *   The raw JSON as a string.
 * @param objectAction
 *   A lambda that accepts the created and returned [JSONObject].
 * @return
 *   A [JSONObject].
 */
@Suppress("unused")
fun jsonObject (
	raw: String,
	objectAction: JSONObject.() -> Unit = {}
): JSONObject =
	(JSONReader(StringReader(raw)).read() as JSONObject).apply(objectAction)

/**
 * Answer a [JSONArray] that has had the provided [arrayAction] applied to it.
 *
 * @param raw
 *   The raw JSON as a string.
 * @param arrayAction
 *   A lambda that accepts the created and returned [JSONArray].
 * @return
 *   A [JSONArray].
 */
@Suppress("unused")
fun jsonArray (
	raw: String,
	arrayAction: JSONArray.() -> Unit = {}
): JSONArray =
	(JSONReader(StringReader(raw)).read() as JSONArray).apply(arrayAction)

/**
 * Answer a [JSONArray] that has had the provided [arrayAction] applied to it.
 *
 * @param reader
 *   The [Reader] that contains the JSON content.
 * @param arrayAction
 *   A lambda that accepts the created and returned [JSONArray].
 * @return
 *   A [JSONArray].
 */
@Suppress("unused")
fun jsonArray (
	reader: Reader,
	arrayAction: JSONArray.() -> Unit = {}
): JSONArray = (JSONReader(reader).read() as JSONArray).apply(arrayAction)

/**
 * Answer a [JSONReader] that has had the provided [readerAction] applied to it.
 *
 * @param raw
 *   The raw JSON as a string.
 * @param readerAction
 *   A lambda that accepts the created and returned [JSONReader].
 * @return
 *   A [JSONReader].
 */
@Suppress("unused")
fun jsonReader (
	raw: String,
	readerAction: JSONReader.() -> Unit = {}
): JSONReader = JSONReader(StringReader(raw)).apply(readerAction)

/**
 * Answer a [JSONReader] that has had the provided [readerAction] applied to it.
 *
 * @param reader
 *   The [Reader] that contains the JSON content.
 * @param readerAction
 *   A lambda that accepts the created and returned [JSONReader].
 * @return
 *   A [JSONReader].
 */
@Suppress("unused")
fun jsonReader (
	reader: Reader,
	readerAction: JSONReader.() -> Unit = {}
): JSONReader = JSONReader(reader).apply(readerAction)

////////////////////////////////////////////////////////////////////////////////
//                          Type Extensions/Functions                         //
////////////////////////////////////////////////////////////////////////////////

/** [Float] wrapped in a [JSONData]. */
val Float.json: JSONData get() = JSONNumber(this)

/** Nullable [Float] wrapped in a [JSONData]. */
val Float?.json: JSONData get() = this?.let { JSONNumber(this) }?: JSONNull

/** 
 * Wrap an [Iterator] of [Float] wrapped in a [JSONArray].
 * 
 * @param iterator
 *   The [Iterator] to wrap.
 * @return
 *   The [JSONArray].
 */
@Suppress("unused")
fun floatJSONArray (iterator: Iterator<Float>): JSONArray =
	JSONArray(mutableListOf<JSONData>().let {
		iterator.forEach { v -> it.add(v.json) }
		it
	}.toTypedArray())

/** [Double] wrapped in a [JSONData]. */
val Double.json: JSONData get() = JSONNumber(this)

/** Nullable [Double] wrapped in a [JSONData]. */
val Double?.json: JSONData get() = this?.let { JSONNumber(this) }?: JSONNull

/**
 * Wrap an [Iterator] of [Double] wrapped in a [JSONArray].
 *
 * @param iterator
 *   The [Iterator] to wrap.
 * @return
 *   The [JSONArray].
 */
@Suppress("unused")
fun doubleJSONArray (iterator: Iterator<Double>): JSONArray =
	JSONArray(mutableListOf<JSONData>().let {
		iterator.forEach { v -> it.add(v.json) }
		it
	}.toTypedArray())

/** [Int] wrapped in a [JSONData]. */
val Int.json: JSONData get() = JSONNumber(this)

/** Nullable [Int] wrapped in a [JSONData]. */
val Int?.json: JSONData get() = this?.let { JSONNumber(this) }?: JSONNull

/**
 * Wrap an [Iterator] of [Int] wrapped in a [JSONArray].
 *
 * @param iterator
 *   The [Iterator] to wrap.
 * @return
 *   The [JSONArray].
 */
@Suppress("unused")
fun intJSONArray (iterator: Iterator<Int>): JSONArray =
	JSONArray(mutableListOf<JSONData>().let {
		iterator.forEach { v -> it.add(v.json) }
		it
	}.toTypedArray())

/** [Long] wrapped in a [JSONData]. */
val Long.json: JSONData get() = JSONNumber(this)

/** Nullable [Long] wrapped in a [JSONData]. */
val Long?.json: JSONData get() = this?.let { JSONNumber(this) }?: JSONNull

/**
 * Wrap an [Iterator] of [Long] wrapped in a [JSONArray].
 *
 * @param iterator
 *   The [Iterator] to wrap.
 * @return
 *   The [JSONArray].
 */
@Suppress("unused")
fun longJSONArray (iterator: Iterator<Long>): JSONArray =
	JSONArray(mutableListOf<JSONData>().let {
		iterator.forEach { v -> it.add(v.json) }
		it
	}.toTypedArray())

/** [String] wrapped in a [JSONData]. */
val String.json: JSONData get() = JSONValue(this)

/** Nullable [String] wrapped in a [JSONData]. */
@Suppress("unused")
val String?.jsonNullable: JSONData get() =
	this?.let { JSONValue(this) }?: JSONNull

/**
 * Wrap an [Iterator] of [String] wrapped in a [JSONArray].
 *
 * @param iterator
 *   The [Iterator] to wrap.
 * @return
 *   The [JSONArray].
 */
@Suppress("unused")
fun stringJSONArray (iterator: Iterator<String>): JSONArray =
	JSONArray(mutableListOf<JSONData>().let {
		iterator.forEach { v -> it.add(v.json) }
		it
	}.toTypedArray())

/** [Boolean] wrapped in a [JSONData]. */
val Boolean.json: JSONData get() = JSONValue(this)

/** Nullable [Boolean] wrapped in a [JSONData]. */
val Boolean?.json: JSONData get() =
	this?.let { JSONValue(this) }?: JSONNull

/**
 * Wrap an [Iterator] of [Boolean] wrapped in a [JSONArray].
 *
 * @param iterator
 *   The [Iterator] to wrap.
 * @return
 *   The [JSONArray].
 */
@Suppress("unused")
fun booleanJSONArray (iterator: Iterator<Boolean>): JSONArray =
	JSONArray(mutableListOf<JSONData>().let {
		iterator.forEach { v -> it.add(v.json) }
		it
	}.toTypedArray())


