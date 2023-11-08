package model.message

import model.MessageHeader
import java.nio.charset.Charset

/**
 * ADB OPEN message.
 *
 * @author Klaus Reimer (k@ailis.de)
 */
class OpenMessage : Message {
	/**
	 * Constructs a new OPEN message.
	 *
	 * @param header
	 * The ADB message header.
	 * @param data
	 * The ADB message data.
	 */
	constructor(header: MessageHeader, data: ByteArray) : super(header, data)

	/**
	 * Constructs a new OPEN message.
	 *
	 * @param localId
	 * The local ID.
	 * @param destination
	 * The destination.
	 */
	constructor(localId: Int, destination: ByteArray?) : super(MessageHeader.CMD_OPEN, localId, 0, destination!!)

	/**
	 * Constructs a new OPEN message.
	 *
	 * @param localId
	 * The local ID.
	 * @param destination
	 * The destination.
	 */
	constructor(localId: Int, destination: String) : this(
		localId,
		(destination + '\u0000').toByteArray(Charset.forName("UTF-8"))
	)

	private val localId: Int
		/**
		 * Returns the local ID.
		 *
		 * @return The local ID.
		 */
		get() = header.arg0
	private val destination: String
		/**
		 * Returns the destination.
		 *
		 * @return The destination.
		 */
		get() {
			var len = data.size
			while (len > 0 && data[len - 1].toInt() == 0) len--
			return String(data, 0, len, Charset.forName("UTF-8"))
		}

	override fun toString(): String {
		return String.format(
			"OPEN(%d, \"%s\")", localId,
			destination
		)
	}
}
