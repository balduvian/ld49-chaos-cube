package game

import com.balduvian.cnge.sound.AL_Management
import com.balduvian.cnge.sound.Sound

object Sounds {
	val management = AL_Management()

	val push = Sound("/sounds/push.wav")
	val goalComplete = Sound("/sounds/goalComplete.wav")
	val goalFail = Sound("/sounds/goalFail.wav")
	val shuffle = Sound("/sounds/shuffle.wav")
}
