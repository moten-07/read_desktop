package model.message

import model.MessageHeader
import toHexString
import java.nio.charset.Charset

/**
 * ADB WRITE message.
 *
 * @author Klaus Reimer (k@ailis.de)
 */
class WriteMessage : Message {
	/**
	 * Constructs a new WRITE message.
	 *
	 * @param header
	 * The ADB message header.
	 * @param data
	 * The ADB message data.
	 */
	constructor(header: MessageHeader, data: ByteArray) : super(header, data)

	/**
	 * Constructs a new WRITE message.
	 *
	 * @param remoteId
	 * The remote ID.
	 * @param data
	 * The destination.
	 */
	constructor(remoteId: Int, data: ByteArray?) : super(MessageHeader.CMD_WRTE, remoteId, 0, data!!)

	/**
	 * Constructs a new WRITE message.
	 *
	 * @param remoteId
	 * The local ID.
	 * @param data
	 * The data.
	 */
	constructor(remoteId: Int, data: String) : this(remoteId, (data + '\u0000').toByteArray(Charset.forName("UTF-8")))

	private val remoteId: Int
		/**
		 * Returns the remote ID.
		 *
		 * @return The remote ID.
		 */
		get() = header.arg1
	val dataAsString: String
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
		return java.lang.String.format(
			"WRITE(%d, %s)", remoteId,
			data.toHexString()
		)
	}
}
