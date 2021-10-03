package game

object Util {
	fun interp(start: Float, end: Float, along: Float): Float {
		return (end - start) * along + start
	}

	enum class Direction(val offX: Int, val offZ: Int) {
		RIGHT( 1,  0),
		UP   ( 0, -1),
		LEFT (-1,  0),
		DOWN ( 0,  1),
		NONE ( 0,  0);
	}

	val colors = arrayOf(
		Triple(0.9f, 0.0f, 0.0f),
		Triple(0.0f, 0.9f, 0.0f),
		Triple(0.0f, 0.0f, 0.9f),
		Triple(0.9f, 0.9f, 0.0f),

		Triple(0.0f, 0.0f, 0.0f),

		Triple(0.2f, 0.2f, 0.2f),
	)
	val shadowColors = arrayOf(
		Triple(0.25f, 0.00f, 0.00f),
		Triple(0.00f, 0.25f, 0.00f),
		Triple(0.00f, 0.00f, 0.25f),
		Triple(0.25f, 0.25f, 0.00f),

		Triple(0.0f, 0.0f, 0.0f),

		Triple(0.1f, 0.1f, 0.1f),
	)
}
