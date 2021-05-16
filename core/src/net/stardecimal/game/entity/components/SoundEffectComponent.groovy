package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Pool

class SoundEffectComponent implements Component, Pool.Poolable {
	Sound soundEffect
	boolean looping = false
	long soundId = -1

	@Override
	void reset() {
		soundEffect?.stop(soundId)
		soundEffect = null
		looping = false
		soundId = -1
	}

	void play() {
		if(soundEffect) {
			soundId = soundEffect.play()
			soundEffect.setLooping(soundId, looping)
		}
	}

	void stop() {
		soundEffect?.stop(soundId)
	}
}
