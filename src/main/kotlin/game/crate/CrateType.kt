package game.crate

import com.balduvian.cnge.graphics.Camera3D
import org.joml.Vector3f

enum class CrateType(val colored: Boolean, val render: (camera: Camera3D, x: Float, z: Float, y: Float, color: Int, colorSet: CrateColorSet, lightAngle: Vector3f, time: Double) -> Unit) {
	NORMAL(true, { camera, x, z, y, color, colorSet, lightAngle, time ->
		//val (r0, g0, b0) = colorSet.colors[color]
		//val (r1, g1, b1) = colorSet.colors[color][1]
		//val (r2, g2, b2) = colorSet.colors[color][2]
//
		//game.GameResources.skyShader.get().enable(camera.projView, com.balduvian.cnge.graphics.Camera3D.transform(x + 0.5f, y + offY + 0.45f, z + 0.5f, 0.45f, 0.45f, 0.45f))
		//game.GameResources.skyShader.get().uniformVector3(0, r0, g0, b0)
		//game.GameResources.skyShader.get().uniformVector3(1, r1, g1, b1)
		//game.GameResources.skyShader.get().uniformVector3(2, r2, g2, b2)
		//game.GameResources.centerCube.get().render()
	})
}
