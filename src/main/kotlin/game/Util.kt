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

	val crateColors = arrayOf(
		arrayOf(Color(0x4f0505), Color(0x991717), Color(0xde2828)),
		arrayOf(Color(0x05630b), Color(0x14c932), Color(0x91e3ae)),
		arrayOf(Color(0x110673), Color(0x3b1cd6), Color(0xbba8f0)),
		arrayOf(Color(0xcc6516), Color(0xf5cb0f), Color(0xebe4a0)),

		arrayOf(Color(0xcc6516), Color(0xf5cb0f), Color(0xebe4a0)),
		arrayOf(Color(0x010f0d), Color(0x172120), Color(0x333333))
	)
}
