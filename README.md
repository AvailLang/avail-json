Avail JSON
================================================================================

API for procedural reading and writing of JSON data, decoupled from 
source-level object representations to maximize developer freedom. 

JSON construction is driven by the [builder pattern](https://en.wikipedia.org/wiki/Builder_pattern), 
rather than an object mapping supplied through, e.g., annotations. Freeform 
writing support is provided via [JSONWriter](#JSONWriter), which provides 
precise errors and fast failures. Serialization support is provided via the
[JSONFriendly](#JSONFriendly) interface; deserialization support is provided
via the `JSONData` hierarchy, and easily integrated through implementation of 
a constructor accepting a `JSONData` instance. 

Supports [ECMA 404: "The JSON Data Interchange Format"](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf).
Supports Kotlin/JVM on JVM ≥11, using interoperability features, e.g., `@Throws`
annotations, for clean integration with Java. Does not support Kotlin/JS or
Kotlin/Native.

# Key Types

## JSONWriter
A `JSONWriter` builds JSON using an underlying `java.io.Writer`; unless a custom
`Writer` is passed, it will use a `java.io.StringWriter`. The current state of
the writer is managed using `JSONState`s, and the current state controls which
subsequent operations are legal. If an illegal operation is attempted, an
`IllegalStateException` is thrown.

Pretty printing is available, but must be requested (via a constructor
parameter).

## JSONReader
A `JSONReader` parses a JSON string into a `JSONData` instance; this object can
be queried for values using string keys.

## JSONData
`JSONData` represents a single JSON datum, and provides _queries_ for
determining type compliance, e.g., `isInt`, `isString`, and _extractors_ for
obtaining demarshaled Kotlin values, e.g., `int`, `string`. Subclasses
differentiate by type and role — `JSONNull` for the null value, `JSONValue` for
booleans and strings, `JSONNumber` for numbers, `JSONArray` for arrays, and
`JSONObject` for objects — and may provide additional behaviors. `JSONData` is a
sealed hierarchy, for convenient use within `when` expressions.

## JSONFriendly
Classes that implement the `JSONFriendly` interface override the `writeTo`
method to control their serialization to JSON, via calls to the supplied
[JSONWriter](#JSONWriter):

```kotlin
/**
 * Emit a JSON representation of the JSONFriendly receiver onto the
 * specified JSONWriter.
 *
 * @param writer
 *   A JSONWriter.
 */
fun writeTo(writer: JSONWriter)
```

# Examples

## Freeform JSON Writing

The following example shows freeform writing, using application configuration
as the theme:

```kotlin
import org.availlang.json.JSONWriter

// Fictional configuration state.
var verbose = false
var logLevel = 0

fun writeConfiguration(writer: JSONWriter) =
	writer.run {
		// Write a complete JSON object, whose properties are defined by the
		// block. `writer` is an implied receiver within the block.
		writeObject {
			// Write a string value at the key "verbose".
			at("verbose") { write(verbose) }
			// Write an int value at the key "logLevel".
			at("logLevel") { write(logLevel) }
		}
		toString()
	}

// Write configuration data.
val writer = JSONWriter()
val document = writeConfiguration(writer)
assert(document == """{"verbose":false,"logLevel":0}""")
```

## Freeform JSON Reading

The following example shows freeform reading, using application configuration
as the theme:

```kotlin
import org.availlang.json.JSONData
import org.availlang.json.JSONObject
import org.availlang.json.JSONReader

// Fictional configuration state.
var logLevel = 5
var verbose = true

fun readConfiguration(document: JSONData)
{
    // The document is really a JSONObject. Read the fields, in any order you
    // like; here, it just so happens to be the opposite order from which they
    // were written.
	document as JSONObject
	logLevel = document["logLevel"].int
	verbose = document["verbose"].boolean
}

// Read configuration data from the JSON string.
val json = """{"verbose":false,"logLevel":0}"""
val document = JSONReader(json.reader()).read()
readConfiguration(document)
assert(loglLevel == 0)
assert(verbose)
```

## Serialization
The following example shows serialization, using a simple user model as the
theme:

```kotlin
import org.availlang.json.JSONArray
import org.availlang.json.JSONFriendly
import org.availlang.json.JSONObject
import org.availlang.json.JSONReader
import org.availlang.json.JSONWriter
import java.util.UUID
import java.util.UUID.fromString as uuid

@Suppress("MemberVisibilityCanBePrivate")
class User(
	val id: UUID,
	val fullName: String,
	val preferredName: String,
	val isActive: Boolean,
	val ageAtSignup: Int,
	val securityRules: List<String>
): JSONFriendly
{
	// Deserialize a User from the supplied JSONObject.
	//
	// Note that not all keys match field names. This is perfectly okay,
	// as this is a freeform JSON library! You just need to use the
	// same keys when writing and reading, of course!
	constructor(data: JSONObject): this(
		id = uuid(data["id"].string),
		fullName = data["fullName"].string,
		preferredName = data["preferredName"].string,
		isActive = data["active"].boolean,
		ageAtSignup = data["age"].int,
		securityRules = (data["rules"] as JSONArray).strings
	)

	// Serialize the receiver to the specified JSONWriter. Overridden from
	// JSONFriendly.
	override fun writeTo(writer: JSONWriter) = writer.run {
		// Pack all data into a JSON object.
		writeObject {
			// Write the fields. You can write them out in any order you like,
			// however the order used in this function implementation is the
			// order in which fields will appear in the final JSON string.
			//
			// Again, note that not all keys match field names, but they do
			// (and must) align with the keys used by the constructor.
			at("id") { write(id.toString()) }
			at("fullName") { write(fullName) }
			at("preferredName") { write(preferredName) }
			at("active") { write(isActive) }
			at("age") { write(ageAtSignup) }
			at("rules") { writeStrings(securityRules) }
		}
	}

	companion object
	{
		// Deserialize a JSON document comprising a single User.
		fun deserialize(json: String): User
		{
			// Obtain a StringReader over the supplied JSON, then extract a
			// JSONData that represents the whole JSON document. Cast it to
			// JSONObject before the constructor call.
			val data = JSONReader(json.reader()).read()
			return User(data as JSONObject)
		}
	}
}
```
