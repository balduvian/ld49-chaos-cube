package game

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.Timer
import com.balduvian.cnge.core.frame.AspectFrame
import com.balduvian.cnge.graphics.*
import com.balduvian.cnge.sound.Sound
import org.joml.Math.sin
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL46.*
import kotlin.math.PI
import kotlin.random.Random
import kotlin.system.exitProcess

class GameScene(window: Window) : Scene(window) {
	val aspect = AspectFrame(16.0f / 9.0f)

	val hudCamera = Camera().setOrtho(160f, 90f)
	val camera = Camera3D().setPerspective(PI.toFloat() / 2.0f, 16.0f / 9.0f)

	var board = Board(13, 9)
	var player = Player(6, 4)

	companion object {
		const val MAX_HEALTH = 5
		const val DEATH_TIME = 5.0
	}

	val deathTimer = Timer()

	var controllable = false
	var health = MAX_HEALTH

	var globalTimer: Double = 0.0

	init {
		Sounds
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glEnable(GL_CULL_FACE)
		glCullFace(GL_BACK)
		glFrontFace(GL_CCW)
		deathTimer.start(DEATH_TIME)
		deathTimer.stop()
		deathTimer.changeTime(10000.0)
	}

	var cameraX = 0.0f
	var cameraY = 0.0f
	var cameraZ = 0.0f

	override fun update(input: Input, timing: Timing) {
		globalTimer += timing.time

		player.update(input, timing, board, controllable)

		var healthChange = board.update(input, timing, player, controllable)
		if (input.keyPressed(GLFW_KEY_7)) {
			healthChange = -200000
		}
		health += healthChange

		if (health > MAX_HEALTH) {
			health = MAX_HEALTH
		} else if (healthChange < 0 && controllable) {
			if (health <= 0) {
				controllable = false
				health = 0
				deathTimer.start(DEATH_TIME)
				Sounds.goalFail.set_pitch(0.5f)

			} else {
				Sounds.goalFail.set_pitch(1.0f)
			}

			Sounds.goalFail.play(false)
		}

		if (!controllable && deathTimer.along() >= 0.5f &&
			(
				input.keyPressed(GLFW_KEY_W) ||
				input.keyPressed(GLFW_KEY_A) ||
				input.keyPressed(GLFW_KEY_S) ||
				input.keyPressed(GLFW_KEY_D)
			)
		) {
			/* reset board */
			globalTimer = 0.0
			board = Board(13, 9)
			player = Player(6, 4)
			board.start(player)
			health = MAX_HEALTH
			controllable = true
		}

		deathTimer.update(timing.time)

		cameraX = board.width * 0.5f
		cameraY = board.height * 0.75f
		cameraZ = board.height * 1.15f

		if (controllable) {
			camera.updateLookAt(cameraX, cameraY, cameraZ, board.width * 0.5f, 0f, board.height * 0.5f, 0f, 1f, 0f)

		} else {
			val lookY = Util.interp(0f, 10f, deathTimer.along())
			camera.updateLookAt(cameraX, cameraY, cameraZ, board.width * 0.5f, lookY, board.height * 0.5f, 0f, 1f, 0f)
		}

		hudCamera.update()

		if (input.keyPressed(GLFW_KEY_ESCAPE)) {
			exitProcess(0)
		}

		if (input.keyPressed(GLFW_KEY_F11)) {
			window.setFullScreen(!window.full)
		}
	}

	override fun render() {
		glClearColor(0f, 0f, 0f, 1f)
		glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))

		/* render background */
		glEnable(GL_DEPTH_TEST)

		val (r0, g0, b0) = board.stage.color0
		val (r1, g1, b1) = board.stage.color1
		val (r2, g2, b2) = board.stage.color2

		glCullFace(GL_FRONT)
		GameResources.skyShader.get().enable(camera.projView, Camera3D.transform(cameraX, cameraY, cameraZ, 20f, 20f, 20f))
		GameResources.skyShader.get().uniformVector3(0, r0, g0, b0)
		GameResources.skyShader.get().uniformVector3(1, r1, g1, b1)
		GameResources.skyShader.get().uniformVector3(2, r2, g2, b2)
		GameResources.centerCube.get().render()
		glCullFace(GL_BACK)

		/* render stars */
		glDisable(GL_CULL_FACE)

		val random = Random(238728L)
		for (i in 0 until 128) {
			val xa = random.nextFloat() * 2 * PI.toFloat()
			val ya = random.nextFloat() * 2 * PI.toFloat()
			val za = Util.interp(-PI.toFloat() / 2, PI.toFloat() / 2, random.nextFloat())
			val scale = Util.interp(0.05f, 0.25f, random.nextFloat())

			GameResources.twinkleShader.get().enable(
				camera.projView,
				Camera.transform
					.translation(cameraX, cameraY, cameraZ)
					.rotateZ(za + (globalTimer.toFloat() * 2 * PI.toFloat() / 8))
					.rotateY(ya)
					.translate(19.5f, 0f, 0f)
					.rotateX(xa)
					.scale(1f, scale, scale)
			)
			GameResources.twinkleShader.get().uniformFloat(0, i.toFloat())
			GameResources.twinkleShader.get().uniformFloat(1, globalTimer.toFloat())
			GameResources.twinkleShader.get().uniformVector4(2, 1.0f, 1.0f, 1.0f, 1.0f)
			GameResources.starTri.get().render()
		}

		glEnable(GL_CULL_FACE)

		/* render game */
		val lightAngle = Vector3f(-0.5f, -2f, -1f).normalize()

		board.render(camera, lightAngle, globalTimer)

		player.render(camera, lightAngle)

		board.renderGoals(camera)

		/* render HUD */
		glDisable(GL_DEPTH_TEST)

		val starX = 80f
		val starY = 12.5f
		val starSize = 10f

		for (i in 0 until 5) {
			GameResources.starShader.get().enable(hudCamera.projection, Camera.transform(starX, starY, starSize + 2.5f, starSize + 2.5f, i * (2.0 * PI / 5.0).toFloat()))
			GameResources.starShader.get().uniformVector3(0, 0.1f, 0.1f, 0.1f)
			GameResources.starPiece.get().render()
		}

		for (i in 0 until 5) {
			GameResources.starShader.get().enable(hudCamera.projection, Camera.transform(starX, starY, starSize, starSize, i * (2.0 * PI / 5.0).toFloat()))
			GameResources.starShader.get().uniformVector3(0, 0.3f, 0.3f, 0.3f)
			GameResources.starPiece.get().render()
		}

		for (i in 0 until health) {
			GameResources.starShader.get().enable(hudCamera.projection, Camera.transform(starX, starY, starSize, starSize, i * (2.0 * PI / 5.0).toFloat()))
			GameResources.starShader.get().uniformVector3(0, 1.0f, 0.9f, 0.0f)
			GameResources.starPiece.get().render()
		}

		board.renderTimer(hudCamera)

		if (!controllable) {
			Font.renderString("chaos", hudCamera, GameResources.fontTiles.get(), 70f, 75.0f, 0.5f, 10f, Vector4f(1f, 1f, 1f, deathTimer.along()), true, true, globalTimer)
			Font.renderString("cube", hudCamera, GameResources.fontTiles.get(),  90f, 60.0f, 0.5f, 10f, Vector4f(1f, 1f, 1f, deathTimer.along()), true)

			val fontYOffset = sin(globalTimer).toFloat() * 2.5f

			Font.renderString("press wasd to start", hudCamera, GameResources.fontTiles.get(), 80f, 42.5f + fontYOffset, 0.5f, 5f, Vector4f(1f, 1f, 1f, deathTimer.along()), true)
		}
	}

	override fun onResize(width: Int, height: Int) {
		val (x, y, w, h) = aspect.getBounds(width, height)
		glViewport(x, y, w, h)
	}

	override fun switchScene(): Int? {
		return null
	}
}

private operator fun Vector3f.component1(): Float {
	return this.x
}
private operator fun Vector3f.component2(): Float {
	return this.y
}
private operator fun Vector3f.component3(): Float {
	return this.z
}