package model.message

import model.MessageHeader
import java.nio.charset.Charset

/**
 * ADB connect message.
 *
 * @author Klaus Reimer (k@ailis.de)
 */
class ConnectMessage : Message {
	/**
	 * Constructs a new connect message.
	 *
	 * @param header
	 * The ADB message header.
	 * @param data
	 * The ADB message data.
	 */
	constructor(header: MessageHeader?, data: ByteArray?) : super(header!!, data!!)

	/**
	 * Constructs a new connect message.
	 *
	 * @param version
	 * The protocol version.
	 * @param maxData
	 * The maximum message body size.
	 * @param systemType
	 * The system type. Must be either
	 * [.SYSTEM_TYPE_BOOTLOADER], [.SYSTEM_TYPE_DEVICE]
	 * or [.SYSTEM_TYPE_HOST].
	 * @param serialNo
	 * The serial number. A unique ID or empty.
	 * @param banner
	 * The banner. A human-readable version or identifier string.
	 */
	constructor(
		version: Int, maxData: Int, systemType: String?,
		serialNo: String?, banner: String?
	) : this(version, maxData, buildIdentity(systemType, serialNo, banner))

	/**
	 * Constructs a new connect message.
	 *
	 * @param version
	 * The protocol version.
	 * @param maxData
	 * The maximum message body size.
	 * @param identity
	 * The identity as a UTF-8 encoded character array.
	 */
	constructor(version: Int, maxData: Int, identity: ByteArray?) : super(
		MessageHeader.CMD_CNXN,
		version,
		maxData,
		identity!!
	)

	/**
	 * Constructs a new connect message.
	 *
	 * @param systemType
	 * The system type. Must be either
	 * [.SYSTEM_TYPE_BOOTLOADER], [.SYSTEM_TYPE_DEVICE]
	 * or [.SYSTEM_TYPE_HOST].
	 * @param serialNo
	 * The serial number. A unique ID or empty.
	 * @param banner
	 * The banner. A human-readable version or identifier string.
	 */
	constructor(systemType: String?, serialNo: String?, banner: String?) : this(
		DEFAULT_PROTOCOL_VERSION, DEFAULT_MAX_DATA, systemType, serialNo,
		banner
	)

	val version: Int
		/**
		 * Returns the protocol version.
		 *
		 * @return The protocol version.
		 */
		get() = header.arg0
	val maxData: Int
		/**
		 * Returns the maximum message body size the remote is willing to accept.
		 *
		 * @return The maximum message body size.
		 */
		get() = header.arg1
	val identity: String
		/**
		 * Returns the system identity string.
		 *
		 * @return The system identity string.
		 */
		get() {
			var len = data.size
			while (len > 0 && data[len - 1].toInt() == 0) len--
			return String(data, 0, len, Charset.forName("UTF-8"))
		}
	val systemType: String
		/**
		 * Returns the system type.
		 *
		 * @return The system type.
		 */
		get() = identity.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
	val serialNo: String
		/**
		 * Returns the serial number.
		 *
		 * @return The serial number.
		 */
		get() = identity.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
	val banner: String
		/**
		 * Returns the banner.
		 *
		 * @return The banner.
		 */
		get() = identity.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2]

	override fun toString(): String {
		return String.format(
			"CONNECT(0x%08x, %d, \"%s\")",
			version, maxData, identity
		)
	}

	companion object {
		/** Constant for default protocol version.  */
		const val DEFAULT_PROTOCOL_VERSION = 0x01000000

		/** Constant for default maximum message body size.  */
		const val DEFAULT_MAX_DATA = 4096

		/** Constant for system type "bootloader".  */
		const val SYSTEM_TYPE_BOOTLOADER = "bootloader"

		/** Constant for system type "device".  */
		const val SYSTEM_TYPE_DEVICE = "device"

		/** Constant for system type "host".  */
		const val SYSTEM_TYPE_HOST = "host"

		/**
		 * Builds and returns the identity payload.
		 *
		 * @param systemType
		 * The system type.
		 * @param serialNo
		 * The serial number. A unique ID or empty.
		 * @param banner
		 * The banner. A human-readable version or identifier string.
		 * @return The identity payload.
		 */
		private fun buildIdentity(
			systemType: String?, serialNo: String?,
			banner: String?
		): ByteArray {
			requireNotNull(systemType) { "systemType must be set" }
			requireNotNull(serialNo) { "serialNo must be set" }
			requireNotNull(banner) { "banner must be set" }
			return "$systemType:$serialNo:$banner\u0000"
				.toByteArray(Charset.forName("UTF-8"))
		}
	}
}
