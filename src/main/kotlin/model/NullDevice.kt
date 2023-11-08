package model

import javax.usb.*
import javax.usb.event.UsbDeviceListener

/**
 * 空实现，无任何作用,最多用于标记无设备
 */
class NullDevice : UsbDevice {
	override fun getParentUsbPort(): UsbPort? = null

	override fun isUsbHub(): Boolean = false

	override fun getManufacturerString(): String? = null

	override fun getSerialNumberString(): String? = null

	override fun getProductString(): String? = null

	override fun getSpeed(): Any = Unit

	override fun getUsbConfigurations(): MutableList<Any?> = mutableListOf()

	override fun getUsbConfiguration(number: Byte): UsbConfiguration? = null

	override fun containsUsbConfiguration(number: Byte): Boolean = false

	override fun getActiveUsbConfigurationNumber(): Byte = 0

	override fun getActiveUsbConfiguration(): UsbConfiguration? = null

	override fun isConfigured(): Boolean = false

	override fun getUsbDeviceDescriptor(): UsbDeviceDescriptor? = null

	override fun getUsbStringDescriptor(index: Byte): UsbStringDescriptor? = null

	override fun getString(index: Byte): String? = null

	override fun syncSubmit(irp: UsbControlIrp?) = Unit

	override fun syncSubmit(list: MutableList<Any?>?) = Unit

	override fun asyncSubmit(irp: UsbControlIrp?) = Unit

	override fun asyncSubmit(list: MutableList<Any?>?) = Unit

	override fun createUsbControlIrp(
		bmRequestType: Byte,
		bRequest: Byte,
		wValue: Short,
		wIndex: Short
	): UsbControlIrp? = null

	override fun addUsbDeviceListener(listener: UsbDeviceListener?) = Unit

	override fun removeUsbDeviceListener(listener: UsbDeviceListener?) = Unit
}