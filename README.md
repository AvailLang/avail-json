Avail JSON
===============================================================================

API for procedural reading and writing of JSON data, decoupled from 
source-level object representations to maximize developer freedom. 

JSON construction is driven by the [builder pattern](https://en.wikipedia.org/wiki/Builder_pattern), 
rather than an object mapping supplied through, e.g., annotations. Freeform 
writing support is provided via [JSONWriter](#JSONWriter), which provides 
precise errors and fast failures. Serialization support is provided via the
[JSONFriendly](#JSONFriendly) interface; deserialization support is provided
via the `JSONData` hierarchy, and easily integrated through implementation of 
a constructor accepting a `JSONData` object. 

Supports [ECMA 404: "The JSON Data Interchange Format"](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf).

## JSONFriendly
Objects that implement the `JSONFriendly` interface specify how they are
written to a [JSONWriter](#JSONWriter) by overriding the `writeTo` function:

```kotlin
/**
 * Emit a JSON representation of the [receiver][JSONFriendly] onto the
 * specified [writer][JSONWriter].
 *
 * @param writer
 *   A [JSONWriter].
 */
fun writeTo(writer: JSONWriter)
```

## JSONWriter
A `JSONWriter` builds JSON using a `java.io.Writer`; unless a custom `Writer`
is passed, it will use a `java.io.StringWriter`. The current state of the
writer is managed using `JSONState`s, and the current state controls which
subsequent operations are legal. If an illegal operation is attempted, an 
`IllegalStateException` is thrown.

## JSONReader
A `JSONReader` parses a JSON string into a `JSONData` object; this object can
be queried for values using string keys.

## Example
The following example classes, which implement `JSONFriendly`, can be written to and read from JSON.

```kotlin
import java.io.StringReader
import java.util.UUID

/**
 * A simple [JSONFriendly] object.
 */
class Baz constructor(
	val name: String,
	val id: Int
): JSONFriendly
{
	override fun writeTo(writer: JSONWriter)
	{
		// Start a new JSON Object.
		writer.startObject()
		
		// Write a String value.
		writer.at("name") { write(name) }
		
		// Write an Int value.
		writer.at("id") { write(id) }
		
		// End the JSON Object opened at the start of this function.
		writer.endObject()
	}

	/**
	 * Constructor that takes a [JSONObject]. The incoming [JSONObject] MUST be
	 * identical in content to the output of the [writeTo] implementation.
	 */
	constructor(jsonObj: JSONObject)
	{
		this.name = jsonObj.getString("name")
		this.id = jsonObj.getNumber("id").int
	}

	/**
	 * Constructor that takes a raw JSON string. The string is first passed to 
	 * a [StringReader], which then is used to construct a [JSONReader]. The
	 * `read` function produces a [JSONData] object. Because we know the JSON
	 * must represent not just free-form data but an object, we cast to 
	 * [JSONObject], and then funnel to the constructor defined above.
	 */
	constructor(rawJSONString: String): this(
		JSONReader(StringReader(rawJSONString)).read() as JSONObject?
			?: error("Not a proper JSON Object!")
	)
}

/**
 * A more complex [JSONFriendly] object.
 */
class Foo constructor(
	val myDouble: Double,
	val myString: String?,
	val myUUID: UUID,
	val myBoolean: Boolean,
	val myBaz: Baz,
	val myLongs: List<Long>
): JSONFriendly
{
	override fun writeTo(writer: JSONWriter)
	{
		writer.apply {
			// Start a new JSON Object.
			startObject() 
			// Write the fields. You can write them out in any order you like,
			// however the order used in this function implementation is the 
			// order in which fields will appear in the final JSON string.
			at("myDouble") { write(myDouble) }
			at("myString") {
				if (myString == null) writeNull()
				else write(myString)
			}
			at("myUUID") { write(myUUID.toString()) }
			at("myBoolean") { write(myBoolean) }
			at("myBaz") { myBaz.writeTo(writer) }
			at("myLongs") {
				// Start an array.
				startArray()
				myLongs.forEach { write(it) }
				// End the array.
				endArray()
			}
			// End the JSON Object opened at the start of this function.
			endObject()
		}
	}

	/**
	 * Constructor that takes a [JSONObject].
	 */
	constructor(jsonObj: JSONObject)
	{
		// Fields are read here in the same order as the main constructor for
		// ease of reading, however this will not make any difference in the 
		// object or any subsequent serialization.
		this.myDouble = jsonObj.getNumber("myDouble").double
		this.myString =
			jsonObj["myString"].let { if (it.isNull) null else it.toString() }
		this.myUUID = UUID.fromString(jsonObj.getString("myUUID"))
		this.myBoolean = jsonObj.getBoolean("myBoolean")
		this.myBaz = Baz(jsonObj)
		this.myLongs =
			jsonObj.getArray("myLongs").map { (it as JSONNumber).long }
	}

	/**
	 * Constructor that takes a raw JSON string.
	 */
	constructor(rawJSONString: String): this(
		JSONReader(StringReader(rawJSONString)).read() as JSONObject?
			?: error("Not a proper JSON Object!")
	)
}
```
