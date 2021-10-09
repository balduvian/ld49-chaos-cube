package game

import com.balduvian.cnge.core.Timer
import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.Camera3D
import com.balduvian.cnge.graphics.Input
import com.balduvian.cnge.graphics.Timing
import game.crate.Crate
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

class Board(val width: Int, val height: Int) {
	companion object {
		val CREATE_GOAL_TIME = 6.0
		val FALL_TIME = 3.0

		val COLLISION_VOID = 0
		val COLLISION_EMPTY = 1
	}

	var lastColorDropped = 0

	val level = Array(width * height) { 1 }
	val crates = Array<Crate?>(width * height) { null }
	val impending = Array<Crate?>(width * height) { null }
	val goals = Array<Goal?>(width * height) { null }
	val fallingBlocks = ArrayList<Triple<Crate, Int, Int>>()

	val dropTimer = Timer()
	val createGoalTimer = Timer()

	var gameTimer = 0.0

	var stageNo = 0
	var stage: Stage = getStage(stageNo)
	val stageTimer = Timer()

	data class CrateMovement(val timer: Timer, val crate: Crate, val startX: Int, val startZ: Int, val endX: Int, val endZ: Int)
	val ongoingMoves = ArrayList<CrateMovement>()

	fun start(player: Player) {
		dropTimer.start(stage.dropTime)
		createGoalTimer.start(CREATE_GOAL_TIME)
		stageTimer.start(stage.length)

		/* populate board */
		val numCrates = round(width * height * 0.125).toInt()
		for (i in 0 until numCrates) {
			var index = Random.nextInt(0, width * height)

			while (level[index] == COLLISION_VOID || crates[index] != null || index == xzToIndex(player.x, player.z)) {
				index = (index + 1) % (width * height)
			}

			crates[index] = Crate(Random.nextInt(0, 4))
		}
	}

	fun update(input: Input, timing: Timing, player: Player, controllable: Boolean): Int {
		if (controllable) {
			gameTimer += timing.time
		}

		if (controllable && dropTimer.updateContinual(timing.time) > 0) {
			var hasChaos = false
			val numCrates = crates.fold(0) { acc, crate ->
				if (crate?.type == 4) hasChaos = true
				acc + if (crate == null) 0 else 1
			}
			iter(impending) { crate, _, _, _ -> if (crate.type == 4) hasChaos = true }

			if (numCrates < stage.idealNumCrates(width, height)) {
				val spot = findEmptySpot()
				if (spot != null) {
					val colorOptions = (0..3 union
						(if (hasChaos) IntRange.EMPTY else 4..4) union
						(if (stage.allowBad) 5..5 else IntRange.EMPTY)).toList()

					val color = colorOptions[Random.nextInt(0, colorOptions.size)]
					val crate = Crate(color)
					crate.fall()

					if (color < 4) lastColorDropped = color
					impending[spot] = crate
				}
			}
		}

		if (controllable && createGoalTimer.updateContinual(timing.time) > 0) {
			val spot = findEmptySpot()
			if (spot != null) {
				val goal = Goal(lastColorDropped)
				goal.timer.start(stage.goalCompleteTime)

				goals[spot] = goal
			}
		}

		var lifeChange = 0

		iter(impending) { crate, _, _, i ->
			if (crates[i] != null) {
				impending[i] = null

			} else if (crate.update(timing)) {
				impending[i] = null

				if (xzToIndex(player.x, player.z) == i) {
					lifeChange -= 1
				} else {
					crates[i] = crate
				}
			}
		}

		ongoingMoves.removeIf { (timer, crate, _, _, destX, destZ) ->
			if (timer.update(timing.time)) {
				val (level, _, _, goal) = accessBlock(destX, destZ)

				/* crates die if they land here */
				if (level == COLLISION_VOID) {
					crate.fallOffEdge()
					fallingBlocks.add(Triple(crate, destX, destZ))

					if (crate.type == 4) {
						shuffle(xzToIndex(player.x, player.z))
					}

				} else if (goal?.matches(crate) == true) {
					crate.putInHole()
					fallingBlocks.add(Triple(crate, destX, destZ))

					val numCrate = goal.incoming.size - goal.acceping
					Sounds.goalComplete.set_pitch(1.0f + numCrate * 0.25f)
					Sounds.goalComplete.play(false)

					if (--goal.acceping == 0) {
						if (goal.satisfied) {
							lifeChange += goal.incoming.size - 1
							val spot = xzToIndex(destX, destZ)
							goals[spot] = null

							if (goals.filterNotNull().isEmpty()) {
								createGoalTimer.changeTime(100000.0)
							}

						} else {
							goal.timer.restart()
						}
					}

				/* add crates back to the board */
				} else {
					val spot = xzToIndex(destX, destZ)
					crates[spot] = crate
				}

				true

			} else {
				false
			}
		}

		iter(goals) { goal, x, z, i ->
			if (goal.update(timing)) {
				lifeChange -= 2
				goals[i] = null
			}
		}

		fallingBlocks.removeIf { (crate, _, _) ->
			crate.update(timing)
		}

		if ((stageTimer.update(timing.time) || input.keyPressed(GLFW_KEY_5)) && controllable) {
			stage = getStage(++stageNo)
			stageTimer.start(stage.length)
			dropTimer.start(stage.dropTime)
		}

		return lifeChange
	}

	data class Stage(val length: Double, val dropTime: Double, val goalCompleteTime: Double, val numCratesPercentage: Float, val allowBad: Boolean, val color0: Color, val color1: Color, val color2: Color) {
		fun idealNumCrates(width: Int, height: Int): Int {
			return round(width * height * numCratesPercentage).toInt()
		}
	}

	fun getStage(stageNo: Int): Stage {
		return when (stageNo) {
			0 -> Stage( 60.0, 2.50, 20.0, 0.25f, false, Color(0x010a29), Color(0x000000), Color(0x1a0436))
			1 -> Stage( 90.0, 2.25, 18.0, 0.30f, false, Color(0xf03a47), Color(0xaf5b5b), Color(0xf87060))
			2 -> Stage(120.0, 2.00, 16.0, 0.35f, false, Color(0x8db580), Color(0x4cb5ae), Color(0x065143))
			3 -> Stage(150.0, 1.75, 14.0, 0.40f, true, Color(0xce1483), Color(0x9e1946), Color(0xde0d92))
			4 -> Stage(180.0, 1.50, 12.0, 0.45f, true, Color(0xf7d002), Color(0xd7af70), Color(0xfea82f))
			5 -> Stage(210.0, 1.25, 10.0, 0.50f, true, Color(0x423e3b), Color(0xaea4bf), Color(0x393e41))
			else -> Stage(240.0, 1.00, 08.0, 0.55f, true, Color(Random.nextInt(0x1000000)), Color(Random.nextInt(0x1000000)), Color(Random.nextInt(0x1000000)))
		}
	}

	/**
	 * a move is created
	 * and the crates involved are removed from the main crates list
	 * they will be returned upon colliding, unless they fall off
	 */
	fun createMove(from: Int, direction: Util.Direction, magnitude: Int, kick: Boolean) {
		val crate = crates[from]
		if (crate != null) {
			crates[from] = null

			val startX = indexToX(from)
			val startZ = indexToZ(from)

			val timer = Timer()
			timer.start(if (kick) {
				Player.KICK_TIME_PER_BLOCK * magnitude
			} else {
				Player.PUSH_TIME
			})

			ongoingMoves.add(CrateMovement(
				timer,
				crate,
				startX,
				startZ,
				startX + direction.offX * magnitude,
				startZ + direction.offZ * magnitude,
			))
		}
	}

	fun shuffle(reserveIndex: Int) {
		crates.shuffle()
		impending.shuffle()
		goals.shuffle()

		fun <T> moveAway(array: Array<T?>, index: Int) {
			val original = array[index]
			if (original != null) {
				array[index] = null

				val newSpot = findEmptySpot()
				if (newSpot != null) {
					array[newSpot] = original
				}
			}
		}

		moveAway(crates, reserveIndex)
		moveAway(impending, reserveIndex)

		for (i in 0 until width * height) {
			if (crates[i] != null && impending[i] != null) {
				moveAway(impending, i)
			}
			if (crates[i] != null && goals[i] != null) {
				moveAway(goals, i)
			}
			if (impending[i] != null && goals[i] != null) {
				moveAway(impending, i)
			}
		}

		val leastGoal = goals.filterNotNull().minByOrNull { it.timer.timeRemaining() }
		leastGoal?.timer?.changeTime(-5.0)

		Sounds.shuffle.play(false)
	}

	private fun findEmptySpot(): Int? {
		val startIndex = Random.nextInt(width * height)
		var index = startIndex

		while (level[index] == COLLISION_VOID || crates[index] != null || impending[index] != null || goals[index] != null) {
			index = (index + 1) % (width * height)
			if (index == startIndex) return null
		}

		return index
	}

	fun <T> iter(array: Array<T?>, onCrate: (T, Int, Int, Int) -> Unit) {
		for (i in array.indices) {
			val element = array[i]
			if (element != null) {
				onCrate(element, indexToX(i), indexToZ(i), i)
			}
		}
	}

	data class Collision(val level: Int, val crate: Crate?, val impending: Crate?, val goal: Goal?) {
		fun withCrate(newCrate: Crate?): Collision {
			return Collision(level, newCrate, impending, goal)
		}
	}

	fun accessBlock(x: Int, z: Int): Collision {
		return if (x in 0 until width && z in 0 until height) {
			val index = xzToIndex(x, z)

			/* the end position of currently moving blocks counts as collidable */
			val moveCrate = ongoingMoves.find { (_, _, _, _, crateX, crateZ) ->
				x == crateX && z == crateZ
			}?.crate

			Collision(level[index], crates[index] ?: moveCrate, impending[index], goals[index])

		} else {
			Collision(COLLISION_VOID, null, null, null)
		}
	}

	fun indexToX(index: Int): Int {
		return index % width
	}

	fun indexToZ(index: Int): Int {
		return index / width
	}

	fun xzToIndex(x: Int, z: Int): Int {
		return z * width + x
	}

	fun render(camera: Camera3D, lightAngle: Vector3f, globalTimer: Double) {
		/* render floor */
		val random = Random(32432)

		val (r0, g0, b0) = Color(0x2b06d1)
		val (r1, g1, b1) = Color(0xde00d3)

		val edgeWidth = 0.1f

		glDisable(GL_DEPTH_TEST)

		for (i in 0 until width) {
			for (j in 0 until height) {
				GameResources.gridFuzzyShader.get().enable(
					camera.projView,
					Camera3D.transform(
						i - edgeWidth * 2, 0f, j - edgeWidth * 2, 1f + edgeWidth * 4, 1f, 1f + edgeWidth * 4
					)
				)

				GameResources.gridFuzzyShader.get().uniformFloat(0, edgeWidth * 4)
				GameResources.gridFuzzyShader.get().uniformVector4(1, r0, g0, b0, 1f)
				GameResources.gridFuzzyShader.get().uniformVector4(2, r1, g1, b1, 1f)
				GameResources.gridFuzzyShader.get().uniformFloat(3, globalTimer.toFloat() * 0.125f)
				GameResources.plane.get().render()
			}
		}

		for (i in 0 until width) {
			for (j in 0 until height) {
				val index = xzToIndex(i, j)

				if (level[index] != 0) {
					/* grid face */
					//GameResources.stage3DShader.get().enable(camera.projView, Camera3D.transform(i.toFloat(), 0f, j.toFloat(), 1f, 1f, 1f))
//
					//val goal = goals[index]
					//if (goal != null) {
					//	val color = Util.colors[goal.type]
					//	GameResources.stage3DShader.get().uniformVector3(0, color.first, color.second, color.third)
					//} else {
					//	GameResources.stage3DShader.get().uniformVector3(0, lightness, lightness, lightness)
					//}
//
					//GameResources.stage3DShader.get().uniformVector3(1, 0.25f, 0.25f, 0.25f)
					//GameResources.stage3DShader.get().uniformVector3(2, lightAngle.x, lightAngle.y, lightAngle.z)
//
					//val impendingCrate = impending[index]
					//if (impendingCrate == null) {
					//	GameResources.stage3DShader.get().uniformFloat(3, 0f)
					//} else {
					//	GameResources.stage3DShader.get().uniformFloat(3, impendingCrate.timer.along())
					//}
//
					//GameResources.plane.get().render()

					GameResources.gridShader.get().enable(
						camera.projView,
						Camera3D.transform(
							i - edgeWidth / 2f, 0f, j - edgeWidth / 2f, 1f + edgeWidth, 1f, 1f + edgeWidth
						)
					)

					GameResources.gridShader.get().uniformFloat(0, edgeWidth)
					GameResources.gridShader.get().uniformVector4(1, r0, g0, b0, 1f)
					GameResources.gridShader.get().uniformVector4(2, r1, g1, b1, 1f)

					val goal = goals[index]
					if (goal != null) {
						val color = Util.colors[goal.type]
						GameResources.gridShader.get().uniformVector4(3, color.first, color.second, color.third, 1f)
					} else {
						GameResources.gridShader.get().uniformVector4(3, 0f, 0f, 0f, 0f)
					}

					val impendingCrate = impending[index]
					if (impendingCrate != null) {
						GameResources.gridShader.get()
							.uniformVector4(3, 0f, 0f, 0f, impendingCrate.timer.along() * 0.75f)
					}

					GameResources.gridShader.get().uniformFloat(4, globalTimer.toFloat() * 0.125f)

					GameResources.plane.get().render()

					/* walls */
					//GameResources.color3DShader.get().enable(camera.projView, Camera3D.transform(i.toFloat(), -3f, j.toFloat(), 1f, 3f, 1f))
					//GameResources.color3DShader.get().uniformVector3(0, 0.75f, 0.75f, 0.75f)
					//GameResources.color3DShader.get().uniformVector3(1, 0.25f, 0.25f, 0.25f)
					//GameResources.color3DShader.get().uniformVector3(2, lightAngle.x, lightAngle.y, lightAngle.z)
					//GameResources.sleeve.get().render()
				}
			}
		}

		glEnable(GL_DEPTH_TEST)

		/* render crates */
		iter(impending) { crate, x, z, _ ->
			crate.render(camera, x.toFloat(), z.toFloat(), Util.interp(10f, 0f, crate.timer.along()), lightAngle, globalTimer)
		}

		iter(crates) { crate, x, z, _ ->
			crate.render(camera, x.toFloat(), z.toFloat(), 0f, lightAngle, globalTimer)
		}

		fallingBlocks.forEach { (crate, x, z) ->
			crate.render(camera, x.toFloat(), z.toFloat(), 0f, lightAngle, globalTimer)
		}

		ongoingMoves.forEach { (timer, crate, startX, startZ, endX, endZ) ->
			val along = timer.along().pow(2)

			crate.render(
				camera,
				Util.interp(startX.toFloat(), endX.toFloat(), along),
				Util.interp(startZ.toFloat(), endZ.toFloat(), along),
				0f,
				lightAngle, gameTimer
			)
		}
	}

	fun renderGoals(camera: Camera3D) {
		iter(goals) { goal, x, z, i ->
			goal.render(camera, x, z, 1f + 0.001f * i)
		}
	}

	fun renderTimer(camera: Camera) {
		val millis = ""
		val seconds = "${gameTimer.toInt() % 60}"

		fun fixedWidth(int: Int): String {
			val newInt = int % 100
			return if (newInt < 10) {
				"0$newInt"
			} else {
				"$newInt"
			}
		}

		val str = "${fixedWidth((gameTimer.toInt() / 60) % 60)}:${fixedWidth(gameTimer.toInt() % 60)}:${fixedWidth((gameTimer * 100).toInt() % 100)}"

		Font.renderString(str, camera, GameResources.fontTiles.get(), 2.5f, 2.5f, 0.5f, 5f, Vector4f(1.0f, 1.0f, 1.0f, 1.0f), false)
	}
}
