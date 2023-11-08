package model

import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * The header of an ADB message.
 *
 * @author Klaus Reimer (k@ailis.de)
 */
class MessageHeader {
	/**
	 * Returns the command.
	 *
	 * @return The command.
	 */
	/** The ADB message command.  */
	val command: Int
	/**
	 * Returns the first argument.
	 *
	 * @return The first argument.
	 */
	/** First argument.  */
	val arg0: Int
	/**
	 * Returns the second argument.
	 *
	 * @return The second argument.
	 */
	/** Second argument.  */
	val arg1: Int
	/**
	 * Returns the data length.
	 *
	 * @return The data length.
	 */
	/** Length of payload (0 is allowed).  */
	val dataLength: Int
	/**
	 * Returns the data checksum.
	 *
	 * @return The data checksum.
	 */
	/** Checksum of data payload (Sum of all bytes).  */
	val dataChecksum: Int
	/**
	 * Returns the inverted command.
	 *
	 * @return The inverted command.
	 */
	/** Inverted command.  */
	private val magic: Int

	/**
	 * Constructs a new ADB message.
	 *
	 * @param command
	 * The command.
	 * @param arg0
	 * The first argument.
	 * @param arg1
	 * The second argument.
	 * @param dataLength
	 * The data length in bytes.
	 * @param dataChecksum
	 * The data checksum. According to the documentation this is a
	 * CRC32 checksum but in reality it is just the sum of all data
	 * bytes.
	 * @param magic
	 * The inverted command. Can be used for validating the message
	 * header.
	 */
	constructor(
		command: Int, arg0: Int, arg1: Int, dataLength: Int,
		dataChecksum: Int, magic: Int
	) {
		this.command = command
		this.arg0 = arg0
		this.arg1 = arg1
		this.dataLength = dataLength
		this.dataChecksum = dataChecksum
		this.magic = command xor -0x1
	}

	/**
	 * Constructs a new ADB message header from the specified byte array.
	 *
	 * @param bytes
	 * The ADB message header as bytes.
	 */
	constructor(bytes: ByteArray) {
		require(bytes.size == SIZE) {
			"ADB message header must be $SIZE bytes large, not  ${bytes.size} bytes"
		}
		val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
		command = buffer.getInt()
		arg0 = buffer.getInt()
		arg1 = buffer.getInt()
		dataLength = buffer.getInt()
		dataChecksum = buffer.getInt()
		magic = buffer.getInt()
	}

	val isValid: Boolean
		/**
		 * Check if this message header is valid. This simply checks if the header
		 * magic value is the inverted value of the command value.
		 *
		 * @return True if message header is valid, false if not.
		 */
		get() = magic == command xor -0x1
	val bytes: ByteArray
		/**
		 * Returns the message header as a byte array.
		 *
		 * @return The message header as a byte array.
		 */
		get() {
			val buffer = ByteBuffer.allocate(SIZE)
			buffer.order(ByteOrder.LITTLE_ENDIAN)
			buffer.putInt(command)
			buffer.putInt(arg0)
			buffer.putInt(arg1)
			buffer.putInt(dataLength)
			buffer.putInt(dataChecksum)
			buffer.putInt(magic)
			return buffer.array()
		}

	companion object {
		/** SYNC 消息命令。  */
		const val CMD_SYNC = 0x434e5953

		/** CONNECT 消息的命令。  */
		const val CMD_CNXN = 0x4e584e43

		/** AUTH 消息的命令。  */
		const val CMD_AUTH = 0x48545541

		/** COPEN 消息的命令。  */
		const val CMD_OPEN = 0x4e45504f

		/** OKAY 消息的命令。 */
		const val CMD_OKAY = 0x59414b4f

		/** CLOSE 消息命令。  */
		const val CMD_CLSE = 0x45534c43

		/**WRITE 消息命令.  */
		const val CMD_WRTE = 0x45545257

		/** 消息标头大小（以字节为单位）。 */
		const val SIZE = 24
	}
}

