fun ByteArray.toHexString() = StringBuffer()
	.apply {
		forEachIndexed { index, byte ->
			val upper = byte.toInt() shr 4 and 0x0F
			val lower = byte.toInt() and 0x0F
			val s = String.format("0x%s", upper.toString(16) + lower.toString(16))
			append(s)
			if (index != size - 1) {
				append(",")
			}
		}
	}.toString()
