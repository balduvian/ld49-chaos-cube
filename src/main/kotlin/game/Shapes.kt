package game

import com.balduvian.cnge.core.resource.VAOResource
import com.balduvian.cnge.graphics.VAO
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import kotlin.math.*

object Shapes {
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

	fun createRoundedCube(): VAOResource {
		val faceVertices = arrayOf(
			Vector4f(-0.5f, -0.5f, 0.5f, 1f),
			Vector4f( 0.5f, -0.5f, 0.5f, 1f),
			Vector4f( 0.5f,  0.5f, 0.5f, 1f),
			Vector4f(-0.5f,  0.5f, 0.5f, 1f),
		)

		val faceNormals = arrayOf(
			Vector4f(-1f, -1f, 1f, 1f).normalize3(),
			Vector4f( 1f, -1f, 1f, 1f).normalize3(),
			Vector4f( 1f,  1f, 1f, 1f).normalize3(),
			Vector4f(-1f,  1f, 1f, 1f).normalize3(),
		)

		val faceIndices = intArrayOf(
			0, 1, 2,
			2, 3, 0
		)

		val vertices = FloatArray(faceVertices.size * 3 * 6)
		val normals = FloatArray(faceNormals.size * 3 * 6)

		val indices = IntArray(faceIndices.size * 6) { i ->
			faceIndices[i % faceIndices.size] + (i / faceIndices.size) * 4
		}

		fun insertFace(face: Int, rotationY: Float, rotationX: Float) {
			val transform = Vector4f()

			for (v in faceVertices.indices) {
				faceVertices[v].rotateX(rotationX, transform).rotateY(rotationY)

				vertices[face * (4 * 3) + v * 3 + 0] = transform.x
				vertices[face * (4 * 3) + v * 3 + 1] = transform.y
				vertices[face * (4 * 3) + v * 3 + 2] = transform.z

				faceNormals[v].rotateX(rotationX, transform).rotateY(rotationY)

				normals[face * (4 * 3) + v * 3 + 0] = transform.x
				normals[face * (4 * 3) + v * 3 + 1] = transform.y
				normals[face * (4 * 3) + v * 3 + 2] = transform.z
			}
		}

		insertFace(0, 0f, 0f)
		insertFace(1, PI.toFloat() / 2f, 0f)
		insertFace(2, PI.toFloat(), 0f)
		insertFace(3, -PI.toFloat() / 2f, 0f)
		insertFace(4, 0f, PI.toFloat() / 2f)
		insertFace(5, 0f, -PI.toFloat() / 2f)

		return VAOResource(
			GL_TRIANGLES,
			indices,
			arrayOf(
				VAO.StaticAttribute(3, vertices),
				VAO.StaticAttribute(3, normals),
			)
		)
	}
}
