/**
 * 数据为三字节，第一字节为通道，第二第三字节为通道值
 */
data class PortData(val port: Byte = 0x01, val data: Short = 0x1024)
