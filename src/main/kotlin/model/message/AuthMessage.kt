package model.message

import model.MessageHeader
import toHexString

/**
 * AUTH 消息。
 *
 * @author Klaus Reimer (k@ailis.de)
 */
class AuthMessage : Message {

	constructor(header: MessageHeader, data: ByteArray) : super(header, data)

	constructor(type: Int, data: ByteArray) : super(MessageHeader.CMD_AUTH, type, 0, data)

	private val type: Int
		get() = header.arg0

	override fun toString(): String {
		return java.lang.String.format(
			"AUTH(%d, 0x%s)", type,
			data.toHexString()
		)
	}

	companion object {
		/**
		 * The auth message transmits a authentification token.
		 */
		const val TYPE_TOKEN = 1

		/**
		 * The auth message transmits a signature.
		 */
		const val TYPE_SIGNATURE = 2

		/**
		 * The auth message transmits a public key.
		 */
		const val TYPE_RSAPUBLICKEY = 3
	}
}
