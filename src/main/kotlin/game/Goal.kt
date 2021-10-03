package game

import com.balduvian.cnge.core.Timer
import com.balduvian.cnge.graphics.Camera3D
import com.balduvian.cnge.graphics.Timing
import org.joml.Math.cos
import org.joml.Math.sin
import kotlin.math.PI

class Goal(val type: Int) {
	val timer = Timer()
	var incoming = ArrayList<Crate>()
	var acceping = 0
	var satisfied = false

	fun matches(crate: Crate): Boolean {
		return crate.type == type || crate.type == 4
	}

	fun incomingCrate(crate: Crate) {
		incoming.add(crate)
		satisfied = incoming.any { it.type != 4 }

		++acceping
	}

	fun update(timing: Timing): Boolean {
		return if (acceping == 0)
			timer.update(timing.time)
		else false
	}

	fun render(camera: Camera3D, x: Int, z: Int, y: Float) {
		val color = Util.colors[type]

		val timeRemaining = timer.timeRemaining()
		val size = if (acceping > 0) {
			2.0f
		} else if (timeRemaining < 5.0) {
			val panicAlong = 1.0f - (timeRemaining / 5.0).toFloat()

			(cos(10 * PI.toFloat() * (panicAlong - 0.0392f)) / 2 + 0.5f) * 1.5f + 0.5f
		} else {
			1.5f
		}

		GameResources.ringShader.get().enable(camera.projView, Camera3D.transform(x + 0.5f, y, z + 0.5f, size, 1f, size))
		GameResources.ringShader.get().uniformVector4(0, color.first, color.second, color.third, 0.5f)
		GameResources.ringShader.get().uniformFloat(1, if (acceping == 0) timer.along() else 0.0f)

		GameResources.centerPlane.get().render()
	}
}
