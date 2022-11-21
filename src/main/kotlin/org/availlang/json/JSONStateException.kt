package org.availlang.json

/**
 * An [IllegalStateException] thrown by a [JSONWriter] if an attempt is made to
 * write content that doesn't align with the currently expected
 * [JSONWriter.JSONState].
 *
 * @author Richard Arriaga
 *
 * @constructor
 * Construct a [JSONStateException].
 *
 * @param message
 *   The description of the exception.
 */
abstract class JSONStateException internal constructor(message: String) :
	IllegalStateException(message)

/**
 * A [JSONStateException] that is thrown if an attempt is made to write to the
 * [JSONWriter] after the JSON document has been completed: The top level
 * [JSONObject] or the top level [JSONArray] has been closed.
 *
 * @author Richard Arriaga
 *
 * @constructor
 * Construct a [EndOfDocumentException].
 *
 * @param message
 *   The description of the exception.
 */
internal class EndOfDocumentException internal constructor(message: String)
	: JSONStateException(message)

/**
 * A [JSONStateException] that is thrown if an attempt is made to write anything
 * other than a JSON object key or the JSON object closing character (`}`) to
 * the [JSONWriter].
 *
 * @author Richard Arriaga
 *
 * @constructor
 * Construct a [ExpectingObjectKeyOrObjectEndException].
 *
 * @param message
 *   The description of the exception.
 */
internal class ExpectingObjectKeyOrObjectEndException
internal constructor(message: String) : JSONStateException(message)

/**
 * A [JSONStateException] that is thrown if an attempt is made to write anything
 * other than a JSON value or the JSON array closing character (`]`) to the
 * [JSONWriter].
 *
 * @author Richard Arriaga
 *
 * @constructor
 * Construct a [ExpectingValueOrArrayEndException].
 *
 * @param message
 *   The description of the exception.
 */
internal class ExpectingValueOrArrayEndException
internal constructor(message: String) : JSONStateException(message)

/**
 * A [JSONStateException] that is thrown if an attempt is made to write anything
 * other than a JSON object value to the [JSONWriter].
 *
 * @author Richard Arriaga
 *
 * @constructor
 * Construct a [ExpectingObjectValueException].
 *
 * @param message
 *   The description of the exception.
 */
internal class ExpectingObjectValueException
internal constructor(message: String) : JSONStateException(message)

/**
 * A [JSONStateException] that is thrown if an attempt is made to write anything
 * other than a JSON value to the [JSONWriter].
 *
 * @author Richard Arriaga
 *
 * @constructor
 * Construct a [ExpectingSingleArbitraryValueException].
 *
 * @param message
 *   The description of the exception.
 */
internal class ExpectingSingleArbitraryValueException
internal constructor(message: String) : JSONStateException(message)
