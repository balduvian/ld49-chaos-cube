package com.balduvian.cnge.graphics

import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL46.*

class Shader(
	val program: Int,
	val projViewLocation: Int,
	val modelLocation: Int,
	val normalMatrixLocation: Int,
	val pvmLocation: Int,
	val locations: Array<Int>
) : GraphicsObject() {
	companion object {
		const val MODEL_NAME = "model"
		const val PROJVIEW_NAME = "projView"
		const val PVM_NAME = "pvm"
		const val NORMALMATRIX_NAME = "normalMatrix"

		val matrixValues = FloatArray(16)
		val vm = Matrix4f()
		val pvm = Matrix4f()
		val normalMatrix = Matrix3f()

		fun create(
			vertexData: String,
			fragmentData: String,
			vararg uniforms: String,
		): Option<Shader> {
			val program = glCreateProgram()

			val fragmentShader = when (
				val option = createShader(fragmentData, GL_FRAGMENT_SHADER)
			) {
				is Good -> option.value
				is Bad -> return option.forward()
			}

			val vertexShader = when (
				val option = createShader(vertexData, GL_VERTEX_SHADER)
			) {
				is Good -> option.value
				is Bad -> return option.forward()
			}

			glAttachShader(program, vertexShader)
			glAttachShader(program, fragmentShader)

			glLinkProgram(program)
			if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
				return Bad(glGetProgramInfoLog(program))
			}

			glDetachShader(program, vertexShader)
			glDetachShader(program, fragmentShader)

			glDeleteShader(vertexShader)
			glDeleteShader(fragmentShader)

			val modelLocation = glGetUniformLocation(program, MODEL_NAME)
			val projViewLocation = glGetUniformLocation(program, PROJVIEW_NAME)
			val normalMatrixLocation = glGetUniformLocation(program, NORMALMATRIX_NAME)

			val pvmLocation = glGetUniformLocation(program, PVM_NAME)
			if (pvmLocation == -1) return Bad("No uniform for \"${PVM_NAME}\" was found")

			val locations = Array(uniforms.size) {
				glGetUniformLocation(program, uniforms[it])
			}
			for (i in locations.indices) {
				if (locations[i] == -1) return Bad("Uniform ${uniforms[i]} was not found")
			}

			return Good(Shader(program, projViewLocation, modelLocation, normalMatrixLocation, pvmLocation, locations))
		}

		private fun createShader(data: String, type: Int): Option<Int> {
			val shader = glCreateShader(type)

			glShaderSource(shader, data)
			glCompileShader(shader)

			val shaderError = glGetShaderi(shader, GL_COMPILE_STATUS)

			if (shaderError != 1) {
				return Bad(glGetShaderInfoLog(shader))
			}

			return Good(shader)
		}
	}

	fun enable(projectionView: Matrix4f) {
		glUseProgram(program)

		projectionView.get(matrixValues)

		if (projViewLocation != -1) {
			glUniformMatrix4fv(projViewLocation, false, matrixValues)
		}

		glUniformMatrix4fv(pvmLocation, false, matrixValues)
	}

	fun enable(projectionView: Matrix4f, model: Matrix4f) {
		glUseProgram(program)

		if (projViewLocation != -1) {
			glUniformMatrix4fv(projViewLocation, false, projectionView.get(matrixValues))
		}

		if (modelLocation != -1) {
			glUniformMatrix4fv(modelLocation, false, model.get(matrixValues))
		}

		projectionView.mul(model, pvm).get(matrixValues)

		if (normalMatrixLocation != -1) {
			glUniformMatrix3fv(normalMatrixLocation, false, model.normal(normalMatrix).get(matrixValues))
		}

		glUniformMatrix4fv(pvmLocation, false, pvm.get(matrixValues))
	}

	override fun destroy() {
		glDeleteProgram(program)
	}

	/* uniform helpers */

	fun uniformFloat(index: Int, value: Float) {
		glUniform1f(locations[index], value)
	}

	fun uniformVector2(index: Int, x: Float, y: Float) {
		glUniform2f(locations[index], x, y)
	}

	fun uniformVector3(index: Int, x: Float, y: Float, z: Float) {
		glUniform3f(locations[index], x, y, z)
	}

	fun uniformVector4(index: Int, x: Float, y: Float, z: Float, w: Float) {
		glUniform4f(locations[index], x, y, z, w)
	}

	fun uniformVector4(index: Int, values: Array<Float>) {
		glUniform4f(locations[index], values[0], values[1], values[2], values[3])
	}

	fun uniformVector2Array(index: Int, values: FloatArray) {
		glUniform2fv(locations[index], values)
	}
}
