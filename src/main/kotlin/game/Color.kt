package game

class Color(val rgb: Int) {
	fun r(): Float {
		return rgb.ushr(16) / 255.0f
	}

	fun g(): Float {
		return rgb.ushr(8).and(0xff) / 255.0f
	}

	fun b(): Float {
		return rgb.and(0xff) / 255.0f
	}

	operator fun component1() = r()
	operator fun component2() = g()
	operator fun component3() = b()
}
