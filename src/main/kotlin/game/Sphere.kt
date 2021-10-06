package game

import com.balduvian.cnge.core.resource.VAOResource
import com.balduvian.cnge.graphics.VAO
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import kotlin.math.*

object Sphere {
	fun createSphere(slices: Int): VAOResource {
		val vertices = FloatArray(slices * slices * 3)

		for (i in 0 until slices) {
			val along = i.toFloat() / (slices - 1)
			val y = Util.interp(-1f, 1f, along)
			val radius = sqrt(1f - (2f * along - 1f).pow(2))

			for (j in 0 until slices) {
				val angle = (j.toFloat() / slices) * 2 * PI.toFloat()

				vertices[(i * slices * 3) + (j * 3) + 0] = cos(angle) * radius
				vertices[(i * slices * 3) + (j * 3) + 1] = y
				vertices[(i * slices * 3) + (j * 3) + 2] = sin(angle) * radius
			}
		}

		val indices = IntArray((slices - 1) * slices * 6)

		for (i in 0 until (slices - 1)) {
			for (j in 0 until slices) {
				indices[(i * slices * 6) + (j * 6) + 0] = i       * slices + j
				indices[(i * slices * 6) + (j * 6) + 1] = i       * slices + ((j + 1) % slices)
				indices[(i * slices * 6) + (j * 6) + 2] = (i + 1) * slices + ((j + 1) % slices)

				indices[(i * slices * 6) + (j * 6) + 3] = (i + 1) * slices + ((j + 1) % slices)
				indices[(i * slices * 6) + (j * 6) + 4] = (i + 1) * slices + j
				indices[(i * slices * 6) + (j * 6) + 5] = i       * slices + j
			}
		}

		return VAOResource(
			GL_TRIANGLES,
			indices,
			arrayOf(VAO.StaticAttribute(3, vertices))
		)
	}
}
