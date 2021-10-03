package game

import com.balduvian.cnge.core.Timer
import com.balduvian.cnge.graphics.Camera3D
import com.balduvian.cnge.graphics.Input
import com.balduvian.cnge.graphics.Timing
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.pow

class Player(var x: Int, var z: Int) {
	companion object {
		val MOVE_TIME = 0.20
		val REPEAT_MOVE_TIME = 0.10
		val PUSH_TIME = 0.30
		val KICK_TIME_PER_BLOCK = 0.10
	}

	val moveTimer = Timer()

	var moveDirection: Util.Direction = Util.Direction.DOWN
	var moveMagnitude: Int = 0
	var facing: Util.Direction = Util.Direction.DOWN
	var alreadyMoving: Boolean = false

	val bufferedMovements = ArrayList<Pair<Util.Direction, Boolean>>()

	fun update(input: Input, timing: Timing, board: Board, controllable: Boolean) {
		val trySprint = input.keyHeld(GLFW_KEY_LEFT_SHIFT) || input.keyHeld(GLFW_KEY_RIGHT_SHIFT)

		val tryMoveDirection = when {
			input.keyPressed(GLFW_KEY_D) -> Util.Direction.RIGHT
			input.keyPressed(GLFW_KEY_W) -> Util.Direction.UP
			input.keyPressed(GLFW_KEY_S) -> Util.Direction.DOWN
			input.keyPressed(GLFW_KEY_A) -> Util.Direction.LEFT
			else -> null
		}

		if (tryMoveDirection != null && bufferedMovements.size < 5) {
			bufferedMovements.add(Pair(tryMoveDirection, trySprint))
		}

		val tryHeldDirection = when {
			input.keyHeld(GLFW_KEY_D) -> Util.Direction.RIGHT
			input.keyHeld(GLFW_KEY_W) -> Util.Direction.UP
			input.keyHeld(GLFW_KEY_S) -> Util.Direction.DOWN
			input.keyHeld(GLFW_KEY_A) -> Util.Direction.LEFT
			else -> null
		}

		fun unbufferMove(repeat: Boolean) {
			if (!controllable) return

			if (tryHeldDirection != null) {
				bufferedMovements.clear()
				startMoving(tryHeldDirection, trySprint, board, repeat)

			} else {
				if (bufferedMovements.isNotEmpty()) {
					val (direction, sprint) = bufferedMovements.removeFirst()
					startMoving(direction, sprint, board, repeat)
				}
			}
		}

		if (moveTimer.going) {
			if (moveTimer.update(timing.time)) {
				x += moveDirection.offX * moveMagnitude
				z += moveDirection.offZ * moveMagnitude

				unbufferMove(true)
			}
		} else {
			unbufferMove(false)
		}
	}

	private fun inLine(startX: Int, startZ: Int, direction: Util.Direction, board: Board, onLine: (Int, Int, Int, Board.Collision) -> Boolean): Pair<Int,  Board.Collision> {
		var distance = 1

		while (true) {
			val x = startX + distance * direction.offX
			val z = startZ + distance * direction.offZ

			val collision = board.accessBlock(x, z)
			if (onLine(distance, x, z, collision)) return Pair(distance, collision)

			++distance
		}
	}

	private fun startMoving(direction: Util.Direction, sprint: Boolean, board: Board, repeat: Boolean) {
		val endX = x + direction.offX
		val endZ = z + direction.offZ

		val (level, crate, _) = board.accessBlock(endX, endZ)

		when {
			level == Board.COLLISION_EMPTY && crate == null -> {
				alreadyMoving = repeat && direction == moveDirection

				facing = direction
				moveDirection = direction

				moveMagnitude = if (sprint) {
					inLine(x, z, direction, board) { _, x, z, collision ->
						collision.level == Board.COLLISION_VOID || collision.crate != null
					}.first - 1

				} else {
					1
				}

				moveTimer.start(if (alreadyMoving) REPEAT_MOVE_TIME else MOVE_TIME)
			}
			crate != null && !crate.timer.going -> {
				/* represents a line of collisions for the crates */
				/* scanned from the map */
				val scanLine = ArrayList<Board.Collision>()
				val crateSpots = ArrayList<Int>()

				/* when the search forward still is on the inital line of contiguous crates */
				var contiguous = true
				var numBlocks = 0

				/* place the line in the map, until the edge, into scanline */
				inLine(x, z, direction, board) { scanDistance, crateX, crateZ, collision ->
					scanLine.add(collision)

					if (contiguous) {
						if (collision.crate == null) {
							contiguous = false
						} else {
							crateSpots.add(board.xzToIndex(crateX, crateZ))
							++numBlocks
						}
					}

					collision.level == 0
				}

				var foundAGoal = false

				/* see where the blocks move, starting with the last block to move */
				for (block in numBlocks - 1 downTo 0) {
					/* preemtively remove the block, it will be moved */
					val thisCrate = scanLine[block].crate!!
					scanLine[block] = scanLine[block].withCrate(null)

					/* look ahead to where the block will move to */
					for (distance in 1 until Int.MAX_VALUE) {
						val collision = scanLine[block + distance]

						/* limit movement to only one block artifically */
						if (!sprint) {
							if (collision.goal?.matches(thisCrate) == true) {
								collision.goal.incomingCrate(thisCrate)
							}

							board.createMove(crateSpots[block], direction, 1, false)
							break

						/* the block gets placed back down one before the crate it hits */
						} else if (collision.crate != null) {
							board.createMove(crateSpots[block], direction, distance - 1, true)
							scanLine[block + distance - 1] = scanLine[block + distance - 1].withCrate(thisCrate)
							break

						} else if (collision.goal?.matches(thisCrate) == true) {
							foundAGoal = true
							collision.goal.incomingCrate(thisCrate)
							board.createMove(crateSpots[block], direction, distance, true)
							break

						/* the block never gets placed back down, it moves all the way */
						} else if (collision.level == Board.COLLISION_VOID) {
							/* + block so that the chain all falls on different spaces */
							board.createMove(crateSpots[block], direction, distance + block, true)
							break
						}
					}
				}

				/* kick instead of push, player does not move */
				if (sprint) {
					facing = direction
					moveDirection = direction
					moveMagnitude = 0
					moveTimer.start(PUSH_TIME)
					Sounds.push.set_pitch(if (foundAGoal) 1.25f else 1.0f)
					Sounds.push.play(false)

				} else {
					facing = direction
					moveDirection = direction
					moveMagnitude = 1
					moveTimer.start(PUSH_TIME)
				}
			}
		}
	}

	fun render(camera: Camera3D, lightAngle: Vector3f) {
		val (offsetX, offsetZ) = if (moveTimer.going) {
			val along = if (alreadyMoving) {
				moveTimer.along()
			} else {
				moveTimer.along().pow(2)
			}

			Pair(
				Util.interp(0f, moveDirection.offX.toFloat() * moveMagnitude, along),
				Util.interp(0f, moveDirection.offZ.toFloat() * moveMagnitude, along),
			)
		} else {
			Pair(0f, 0f)
		}

		GameResources.color3DShader.get().enable(camera.projView, Camera3D.transform(x + 0.25f + offsetX, 0f, z + 0.25f + offsetZ, 0.5f, 0.5f, 0.5f))
		GameResources.color3DShader.get().uniformVector3(0, 1f, 1f, 1f)
		GameResources.color3DShader.get().uniformVector3(1, 0.25f, 0.25f, 0.25f)
		GameResources.color3DShader.get().uniformVector3(2, lightAngle.x, lightAngle.y, lightAngle.z)

		GameResources.cube.get().render()
	}
}
