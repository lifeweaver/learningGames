package net.stardecimal.game

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect
import com.badlogic.gdx.utils.IntMap

/**
 *  A pooled particle effect manager to store particle effect pools
 *  https://www.gamedevelopment.blog/full-libgdx-game-tutorial-particle-effects/
 */
class ParticleEffectManager {
	// DEFINE constants for particleEffects
	static final int SMOKE = 0
	static final int WATER = 1
	static final int FIRE = 2
	static final int CONTRAIL = 3
	static final int EXPLOSION = 4
	static final int FLAMES = 5

	// create intmaps for effects and pools
	private IntMap<ParticleEffect> partyEffects
	private IntMap<ParticleEffectPool> partyEffectPool

	/**
	 *  Particle Effect Manager for controlling creating pools and dispensing particle effects
	 */
	ParticleEffectManager(){
		partyEffects = new IntMap<ParticleEffect>()
		partyEffectPool = new IntMap<ParticleEffectPool>()
	}

	/** Create a particle effect pool for type  with default values (scale 1, pool init capacity 5, max capacity 20)
	 * @param type int id of particle effect
	 * @param party the particle effect
	 */
	void addParticleEffect(int type, ParticleEffect party){
		addParticleEffect(type,party,1)
	}

	/** Create a particle effect pool for type with scale and default pool sizes
	 * @param type int id of particle effect
	 * @param party the particle effect
	 * @param scale The particle effect scale
	 */
	void addParticleEffect(int type, ParticleEffect party, float scale ){
		addParticleEffect(type,party,scale,5,20)

	}

	/** Create a particle effect pool for type
	 * @param type int id of particle effect
	 * @param party the particle effect
	 * @param scale The particle effect scale
	 * @param startCapacity pool initial capacity
	 * @param maxCapacity pool max capacity
	 */
	void addParticleEffect(int type, ParticleEffect party, float scale, int startCapacity, int maxCapacity){
		party.scaleEffect(scale)
		partyEffects.put(type, party)
		partyEffectPool.put(type,new ParticleEffectPool(party,startCapacity,maxCapacity))

	}

	/**
	 *  Get a particle effect of type type
	 * @param type the type to get
	 * @return The pooled particle effect
	 */
	PooledEffect getPooledParticleEffect(int type){
		return partyEffectPool.get(type).obtain()
	}
}