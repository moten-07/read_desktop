package model.message

import model.MessageHeader

/**
 * ADB OKAY message.
 *
 * @author Klaus Reimer (k@ailis.de)
 */
class OkayMessage : Message {
	/**
	 * Constructs a new OKAY message.
	 *
	 * @param header
	 * The ADB message header.
	 * @param data
	 * The ADB message data.
	 */
	constructor(header: MessageHeader, data: ByteArray) : super(header, data)

	/**
	 * Constructs a new OKAY message.
	 *
	 * @param localId
	 * The local ID.
	 * @param remoteId
	 * The remote ID.
	 */
	constructor(remoteId: Int, localId: Int) : super(MessageHeader.CMD_OKAY, remoteId, localId, ByteArray(0))

	private val localId: Int
		/**
		 * Returns the local ID.
		 *
		 * @return The local ID.
		 */
		get() = header.arg1
	private val remoteId: Int
		/**
		 * Returns the remote ID.
		 *
		 * @return The remote ID.
		 */
		get() = header.arg0

	override fun toString(): String {
		return String.format("OKAY(%d, %d)", remoteId, localId)
	}
}
