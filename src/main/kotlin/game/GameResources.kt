package game

import com.balduvian.cnge.core.resource.ShaderResource
import com.balduvian.cnge.core.resource.TileTextureResource
import com.balduvian.cnge.core.resource.VAOResource
import com.balduvian.cnge.graphics.TextureParams
import com.balduvian.cnge.graphics.VAO
import org.joml.Math.*
import org.lwjgl.opengl.GL46.*

object GameResources {
	val colorShader = ShaderResource(
		"/shaders/color/vert.glsl",
		"/shaders/color/frag.glsl",
		"color",
	)
	val textureShader = ShaderResource(
		"/shaders/texture/vert.glsl",
		"/shaders/texture/frag.glsl",
		"color",
	)
	val tileShader = ShaderResource(
		"/shaders/tile/vert.glsl",
		"/shaders/tile/frag.glsl",
		"color",
		"tile",
	)
	val color3DShader = ShaderResource(
		"/shaders/color3D/vert.glsl",
		"/shaders/color3D/frag.glsl",
		"light",
		"ambient",
		"lightAngle"
	)
	val stage3DShader = ShaderResource(
		"/shaders/stage3D/vert.glsl",
		"/shaders/stage3D/frag.glsl",
		"light",
		"ambient",
		"lightAngle",
		"shadow",
	)
	val vhsShader = ShaderResource(
		"/shaders/vhs/vert.glsl",
		"/shaders/vhs/frag.glsl",
		"ratio",
		"redOffset",
		"greOffset",
		"bluOffset",
		"scanLines",
		"scanOffset",
		"seed"
	)
	val gradientShader = ShaderResource(
		"/shaders/gradient/vert.glsl",
		"/shaders/gradient/frag.glsl",
		"color0",
		"color1",
		"color2",
	)
	val ringShader = ShaderResource(
		"/shaders/ring/vert.glsl",
		"/shaders/ring/frag.glsl",
		"ringColor",
		"along",
	)
	val shinyShader = ShaderResource(
		"/shaders/shiny3D/vert.glsl",
		"/shaders/shiny3D/frag.glsl",
		"lightAngle",
		"time",
	)
	val starShader = ShaderResource(
		"/shaders/star/vert.glsl",
		"/shaders/star/frag.glsl",
		"color",
	)
	val tileShinyShader = ShaderResource(
		"/shaders/tileShiny/vert.glsl",
		"/shaders/tileShiny/frag.glsl",
		"color",
		"tile",
		"time",
	)
	val rect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 2, 3, 0),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 0f,
				1f, 0f,
				1f, 1f,
				0f, 1f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 1f,
				1f, 1f,
				1f, 0f,
				0f, 0f,
			))
		)
	)
	val frameRect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 0, 2, 3),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 0f,
				1f, 0f,
				1f, 1f,
				0f, 1f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 0f,
				1f, 0f,
				1f, 1f,
				0f, 1f,
			))
		)
	)
	val lineRect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 0, 2, 3),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0.0f, -0.5f,
				1.0f, -0.5f,
				1.0f,  0.5f,
				0.0f,  0.5f,
			)),
		)
	)
	val cube = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			 0,  1,  2,  2,  3,  0,
			 4,  5,  6,  6,  7,  4,
			 8,  9, 10, 10, 11,  8,
			12, 13, 14, 14, 15, 12,
			16, 17, 18, 18, 19, 16,
			20, 21, 22, 22, 23, 20,
		),
		arrayOf(
			VAO.StaticAttribute(3, floatArrayOf(
				/* front */
				0f, 0f, 1f, /**/ 1f, 0f, 1f, /**/ 1f, 1f, 1f, /**/ 0f, 1f, 1f,
				/* right */
				1f, 0f, 1f, /**/ 1f, 0f, 0f, /**/ 1f, 1f, 0f, /**/ 1f, 1f, 1f,
				/* back */
				1f, 0f, 0f, /**/ 0f, 0f, 0f, /**/ 0f, 1f, 0f, /**/ 1f, 1f, 0f,
				/* left */
				0f, 0f, 0f, /**/ 0f, 0f, 1f, /**/ 0f, 1f, 1f, /**/ 0f, 1f, 0f,
				/* up */
				0f, 1f, 1f, /**/ 1f, 1f, 1f, /**/ 1f, 1f, 0f, /**/ 0f, 1f, 0f,
				/* down */
				0f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 1f, /**/ 0f, 0f, 1f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
			)),
			VAO.StaticAttribute(3, floatArrayOf(
				/* front */
				0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f,
				/* right */
				1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f,
				/* back */
				0f, 0f,-1f, /**/ 0f, 0f,-1f, /**/ 0f, 0f,-1f, /**/ 0f, 0f,-1f,
				/* left */
				-1f,0f, 0f, /**/ -1f,0f, 0f, /**/ -1f,0f, 0f, /**/ -1f,0f, 0f,
				/* up */
				0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f,
				/* down */
				0f,-1f, 0f, /**/ 0f,-1f, 0f, /**/ 0f,-1f, 0f, /**/ 0f,-1f, 0f,
			)),
		)
	)
	val sleeve = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			0,  1,  2,  2,  3,  0,
			4,  5,  6,  6,  7,  4,
			8,  9, 10, 10, 11,  8,
			12, 13, 14, 14, 15, 12,
		),
		arrayOf(
			VAO.StaticAttribute(3, floatArrayOf(
				/* front */
				0f, 0f, 1f, /**/ 1f, 0f, 1f, /**/ 1f, 1f, 1f, /**/ 0f, 1f, 1f,
				/* right */
				1f, 0f, 1f, /**/ 1f, 0f, 0f, /**/ 1f, 1f, 0f, /**/ 1f, 1f, 1f,
				/* back */
				1f, 0f, 0f, /**/ 0f, 0f, 0f, /**/ 0f, 1f, 0f, /**/ 1f, 1f, 0f,
				/* left */
				0f, 0f, 0f, /**/ 0f, 0f, 1f, /**/ 0f, 1f, 1f, /**/ 0f, 1f, 0f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
			)),
			VAO.StaticAttribute(3, floatArrayOf(
				/* front */
				0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f,
				/* right */
				1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f,
				/* back */
				0f, 0f,-1f, /**/ 0f, 0f,-1f, /**/ 0f, 0f,-1f, /**/ 0f, 0f,-1f,
				/* left */
				-1f,0f, 0f, /**/ -1f,0f, 0f, /**/ -1f,0f, 0f, /**/ -1f,0f, 0f,
			)),
		)
	)
	val plane = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			0,  1,  2,  2,  3,  0,
		),
		arrayOf(
			VAO.StaticAttribute(3, floatArrayOf(
				/* up */
				0f, 0f, 1f, /**/ 1f, 0f, 1f, /**/ 1f, 0f, 0f, /**/ 0f, 0f, 0f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
			)),
			VAO.StaticAttribute(3, floatArrayOf(
				/* up */
				0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f,
			)),
		)
	)
	val centerPlane = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			0,  1,  2,  2,  3,  0,
		),
		arrayOf(
			VAO.StaticAttribute(3, floatArrayOf(
				-0.5f, 0f, 0.5f, /**/ 0.5f, 0f, 0.5f, /**/ 0.5f, 0f, -0.5f, /**/ -0.5f, 0f, -0.5f,
			)),
		)
	)
	val starPiece: VAOResource
	init {
		val halfStep = PI / 5

		val angleLeft = PI / 2 + halfStep
		val angleRight = PI / 2 - halfStep

		val inside = 0.381966011251f

		starPiece = VAOResource(
			GL_TRIANGLES,
			intArrayOf(
				0, 1, 2,
				3, 4, 5
			),
			arrayOf(
				VAO.StaticAttribute(2, floatArrayOf(
					0f, 0f,
					0f, 1f,
					cos(angleLeft).toFloat() * inside, sin(angleLeft).toFloat() * inside,
					0f, 0f,
					cos(angleRight).toFloat() * inside, sin(angleRight).toFloat() * inside,
					0f, 1f,
				)),
				VAO.StaticAttribute(1, floatArrayOf(
					1f, 1f, 1f,
					0.75f, 0.75f, 0.75f
				))
			)
		)
	}
	val dynTri = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2),
		arrayOf(VAO.DynamicAttribute(2, 3))
	)
	val fontTiles = TileTextureResource(
		"/textures/font.png",
		TextureParams().filter(GL_NEAREST).wrap(GL_CLAMP_TO_BORDER),
		16,
		8
	)
}
