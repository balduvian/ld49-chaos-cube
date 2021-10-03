package game

import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.TileTexture
import org.joml.Vector4f

object Font {
	fun renderString(string: String, camera: Camera, tileTexture: TileTexture, x: Float, y: Float, charWidth: Float, scale: Float, color: Vector4f, centered: Boolean, chaos: Boolean = false, time: Double = 0.0) {
		val boxSize = scale / charWidth
		val offX = if (centered) -(string.length * scale / 2.0f) else 0f

		val shader = if (chaos) GameResources.tileShinyShader.get() else GameResources.tileShader.get()

		tileTexture.bind()

		string.forEachIndexed { i, c ->
			shader.enable(camera.projection, Camera.transform(x + (i * scale) + offX, y, boxSize, boxSize))
			shader.uniformVector4(0, color.x, color.y, color.z, color.w)
			shader.uniformVector4(1, tileTexture.tile(c.code % 16, c.code / 16))
			if (chaos) shader.uniformFloat(2, time.toFloat())

			GameResources.rect.get().render()
		}
	}
}
