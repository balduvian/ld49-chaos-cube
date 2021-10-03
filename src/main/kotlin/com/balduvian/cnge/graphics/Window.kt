package com.balduvian.cnge.graphics

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL.createCapabilities

class Window(
	var videoMode: GLFWVidMode,
	val window: Long,
	val vsync: Boolean,
	var width: Int,
	var height: Int,
	var full: Boolean,
) {
	companion object {
		fun init(onError: (String) -> Unit): Boolean {
			glfwSetErrorCallback { code, description ->
				onError("GLFW ERROR CODE $code | ${GLFWErrorCallback.getDescription(description)}")
			}

			return glfwInit()
		}

		fun create(
			majorVersion: Int,
			minorVersion: Int,
			resizable: Boolean,
			decorated: Boolean,
			title: String,
			full: Boolean,
			vsync: Boolean
		): Window? {
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, majorVersion)
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, minorVersion)
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
			glfwWindowHint(GLFW_RESIZABLE, if (resizable) 1 else 0)
			glfwWindowHint(GLFW_DECORATED, if (decorated) 1 else 0)

			val monitor = glfwGetPrimaryMonitor()
			if (monitor == 0L) return null

			val videoMode = glfwGetVideoMode(monitor)
				?: return null

			val width = videoMode.width() / 2
			val height = videoMode.height() / 2

			val window = glfwCreateWindow(
				width,
				height,
				title,
				if (full) monitor else 0L,
				0L
			)
			if (window == 0L) return null

			glfwFocusWindow(window)

			glfwMakeContextCurrent(window)
			createCapabilities()

			glfwSwapInterval(if (vsync) 1 else 0)

			return Window(videoMode, window, vsync, width, height, full)
		}
	}

	val input = Input()

	fun sizeCallback(window: Long, newWidth: Int, newHeight: Int) {
		this.width = newWidth
		this.height = newHeight
		input.didResize = true
	}

	init {
		glfwSetWindowSizeCallback(window, ::sizeCallback)

		glfwSetKeyCallback(window) { _, key, scanCode, action, _ ->
			if (action == GLFW_PRESS) {
				input.keys[key] = Input.PRESSED
			} else if (action == GLFW_RELEASE) {
				input.keys[key] = Input.RELEASED
			}
		}

		glfwSetMouseButtonCallback(window) { _, button, action, _ ->
			if (action == GLFW_PRESS) {
				input.buttons[button] = Input.PRESSED
			} else if (action == GLFW_RELEASE) {
				input.buttons[button] = Input.RELEASED
			}
		}

		glfwSetCursorPosCallback(window) { _, x, y ->
			input.mouseX = x.toFloat()
			input.mouseY = y.toFloat()
		}
	}

	fun setFullScreen(full: Boolean) {
		this.full = full

		val newMonitor = glfwGetPrimaryMonitor()
		val newVideoMode = glfwGetVideoMode(newMonitor) ?: return

		val newWidth = if (full) newVideoMode.width() else newVideoMode.width() / 2
		val newHeight = if (full) newVideoMode.height() else newVideoMode.height() / 2

		if (full) {
			glfwSetWindowMonitor(window, newMonitor, 0, 0, newWidth, newHeight, GLFW_DONT_CARE)
		} else {
			glfwSetWindowMonitor(window, 0L, newVideoMode.width() / 4, newVideoMode.height() / 4, newWidth, newHeight, GLFW_DONT_CARE)
		}

		glfwSwapInterval(if (vsync) 1 else 0)

		this.videoMode = newVideoMode
		sizeCallback(window, newWidth, newHeight)
	}

	fun poll() {
		glfwPollEvents()
	}

	fun postFrame() {
		input.keys.indices.forEach { i ->
			if (input.keys[i] == Input.PRESSED) input.keys[i] = Input.HELD
		}
		input.buttons.indices.forEach { i ->
			if (input.buttons[i] == Input.PRESSED) input.buttons[i] = Input.HELD
		}
		input.didResize = false
	}

	fun swap() {
		glfwSwapBuffers(window)
	}

	fun close() {
		glfwTerminate()
	}

	fun shouldClose(): Boolean {
		return glfwWindowShouldClose(window)
	}

	fun refreshRate(): Int {
		return videoMode.refreshRate()
	}
}
