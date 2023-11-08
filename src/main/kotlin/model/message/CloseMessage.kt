package model.message

import model.MessageHeader

/**
 * ADB CLOSE message.
 *
 * @author Klaus Reimer (k@ailis.de)
 */
class CloseMessage : Message {
	/**
	 * Constructs a new CLOSE message.
	 *
	 * @param header
	 * The ADB message header.
	 * @param data
	 * The ADB message data.
	 */
	constructor(header: MessageHeader?, data: ByteArray?) : super(header!!, data!!)

	/**
	 * Constructs a new CLOSE message.
	 *
	 * @param localId
	 * The local ID.
	 * @param remoteId
	 * The remote ID.
	 */
	constructor(localId: Int, remoteId: Int) : super(MessageHeader.CMD_CLSE, localId, remoteId, ByteArray(0))

	val localId: Int
		/**
		 * Returns the local ID.
		 *
		 * @return The local ID.
		 */
		get() = header.arg0
	val remoteId: Int
		/**
		 * Returns the remote ID.
		 *
		 * @return The remote ID.
		 */
		get() = header.arg1

	override fun toString(): String {
		return String.format("CLOSE(%d, %d)", localId, remoteId)
	}
}
