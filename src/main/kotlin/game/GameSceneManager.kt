package game

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.SceneManager
import com.balduvian.cnge.graphics.Window

class GameSceneManager : SceneManager(SCENE_GAME) {
	companion object {
		const val SCENE_MENU = 0
		const val SCENE_GAME = 1
	}
	override fun getResources(sceneId: Int): Array<Resource<*>> {
		return arrayOf(
			GameResources.color3DShader,
			GameResources.stage3DShader,
			GameResources.shinyShader,
			GameResources.ringShader,
			GameResources.cube,
			GameResources.sleeve,
			GameResources.plane,
			GameResources.centerPlane,
			GameResources.gradientShader,
			GameResources.rect,
			GameResources.starShader,
			GameResources.starPiece,
			GameResources.tileShader,
			GameResources.tileShinyShader,
			GameResources.textureShader,
			GameResources.fontTiles,
			GameResources.skyShader,
			GameResources.sphere,
			GameResources.centerCube,
			GameResources.twinkleShader,
			GameResources.starTri,
			GameResources.smoothColor3DShader,
			GameResources.roundedCube,
			GameResources.gridShader,
			GameResources.gridFuzzyShader,
		)
	}

	override fun createScene(window: Window, sceneId: Int): Scene {
		return GameScene(window)
	}
}
