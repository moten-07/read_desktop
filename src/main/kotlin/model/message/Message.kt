package model.message

import model.MessageHeader


/**
 * ADB Message. The abstract base class for all commands.
 *
 * @author Klaus Reimer (k@ailis.de)
 */
abstract class Message {
	/**
	 * Returns the message header.
	 *
	 * @return The message header.
	 */
	/** The ADB message header.  */
	val header: MessageHeader
	/**
	 * Returns the payload data.
	 *
	 * @return The payload data.
	 */
	/** The data payload.  */
	val data: ByteArray

	/**
	 * Constructs a new ADB message.
	 *
	 * @param command
	 * The command.
	 * @param arg0
	 * The first argument.
	 * @param arg1
	 * The second argument.
	 * @param data
	 * The data payload.
	 */
	protected constructor(command: Int, arg0: Int, arg1: Int, data: ByteArray) {
		this.data = data
		var checksum = 0
		for (b in data) checksum += b.toInt() and 0xff
		header = MessageHeader(
			command, arg0, arg1,
			data.size, checksum, command xor -0x1
		)
	}

	/**
	 * Constructs a new ADB message.
	 *
	 * @param header
	 * The ADB message header.
	 * @param data
	 * The ADB message data.
	 */
	constructor(header: MessageHeader, data: ByteArray) {
		this.header = header
		this.data = data
	}

	val isValid: Boolean
		/**
		 * Checks if this ADB message is valid. First it checks the validity of the
		 * header and then it checks the data checksum.
		 *
		 * @return True if ADB message is valid, false if not.
		 */
		get() {
			if (!header.isValid) return false
			var checksum = 0
			for (b in data) checksum += b.toInt() and 0xff
			return checksum == header.dataChecksum
		}

	companion object {
		/**
		 * Creates an ADB message.
		 *
		 * @param header
		 * The ADB message header.
		 * @param data
		 * The ADB message data.
		 * @return The parsed ADB message.
		 */
		fun create(header: MessageHeader, data: ByteArray): Message {
			return when (val command: Int = header.command) {
				MessageHeader.CMD_CNXN -> ConnectMessage(header, data)
				MessageHeader.CMD_AUTH -> AuthMessage(header, data)
				MessageHeader.CMD_OPEN -> OpenMessage(header, data)
				MessageHeader.CMD_CLSE -> CloseMessage(header, data)
				MessageHeader.CMD_OKAY -> OkayMessage(header, data)
				MessageHeader.CMD_WRTE -> WriteMessage(header, data)
				else -> throw UnsupportedOperationException(String.format("命令 0x%08x 的解析尚未实现", command))
			}
		}
	}
}

