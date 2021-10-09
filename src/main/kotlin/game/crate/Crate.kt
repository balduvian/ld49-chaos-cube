package game.crate

import com.balduvian.cnge.core.Timer
import com.balduvian.cnge.graphics.Camera3D
import com.balduvian.cnge.graphics.Timing
import game.Board
import game.GameResources
import game.Util
import org.joml.Vector3f
import kotlin.random.Random

class Crate(val type: Int) {
	val timer = Timer()

	var holeState: Int = ON_GROUND
	val id = Random.nextInt(-1000, 1000)

	companion object {
		const val HOLE_TIME = 0.25

		const val ON_GROUND = 0
		const val GOOD_HOLE = 1
		const val  OFF_EDGE = 2
	}

	/**
	 * @return if the timer finished
	 */
	fun update(timing: Timing): Boolean {
		return timer.update(timing.time)
	}

	fun fall() {
		timer.start(Board.FALL_TIME)
	}

	fun putInHole() {
		holeState = GOOD_HOLE
		timer.start(HOLE_TIME)
	}

	fun fallOffEdge() {
		holeState = OFF_EDGE
		timer.start(Board.FALL_TIME)
	}

	fun render(camera: Camera3D, x: Float, z: Float, y: Float, lightAngle: Vector3f, time: Double) {
		val offY = if (holeState == ON_GROUND) 0f else Util.interp(
			0f,
			if (holeState == GOOD_HOLE) -1f else -10f,
			timer.along()
		)

		val model = Camera3D.transform(x + 0.05f, y + offY, z + 0.05f, 0.9f, 0.9f, 0.9f)

		if (type == 4) {
			GameResources.shinyShader.get().enable(camera.projView, model)
			GameResources.shinyShader.get().uniformVector3(0, lightAngle.x, lightAngle.y, lightAngle.z)
			GameResources.shinyShader.get().uniformFloat(1, (time + id).toFloat())
			GameResources.cube.get().render()

		} else {
			val (r0, g0, b0) = Util.crateColors[type][0]
			val (r1, g1, b1) = Util.crateColors[type][1]
			val (r2, g2, b2) = Util.crateColors[type][2]

			GameResources.smoothColor3DShader.get().enable(camera.projView, Camera3D.transform(x + 0.5f, y + offY + 0.45f, z + 0.5f, 0.9f, 0.9f, 0.9f))
			GameResources.smoothColor3DShader.get().uniformVector3(0, r1, g1, b1)
			GameResources.smoothColor3DShader.get().uniformVector3(1, r0, g0, b0)
			GameResources.smoothColor3DShader.get().uniformVector3(2, lightAngle.x, lightAngle.y, lightAngle.z)
			GameResources.roundedCube.get().render()

			//GameResources.skyShader.get().enable(camera.projView, Camera3D.transform(x + 0.5f, y + offY + 0.45f, z + 0.5f, 0.45f, 0.45f, 0.45f))
			//GameResources.skyShader.get().uniformVector3(0, r0, g0, b0)
			//GameResources.skyShader.get().uniformVector3(1, r1, g1, b1)
			//GameResources.skyShader.get().uniformVector3(2, r2, g2, b2)
			//GameResources.centerCube.get().render()
		}
	}
}
