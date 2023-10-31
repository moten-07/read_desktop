import gnu.io.NRSerialPort
import java.io.DataInputStream

fun main() {

	for (s in NRSerialPort.getAvailableSerialPorts()) {
		println("Availible port: $s")
	}

	val serial = NRSerialPort("COM3")

	try {
		serial.connect()
		val ins = DataInputStream(serial.inputStream)
		while (!Thread.interrupted()) {
			if (ins.available() > 0) {
				val b = ins.read().toChar()
				print(b)
			}
			Thread.sleep(5)
		}
	} catch (ex: Exception) {
		ex.printStackTrace()
	} finally {
//        serial.disconnect()
	}

}