package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.ComponentMapper

class Mapper {
//	static final ComponentMapper<AnimationComponent> animCom = ComponentMapper.getFor(AnimationComponent.class)
//	static final ComponentMapper<WaterFloorComponent> waterFlCom = ComponentMapper.getFor(WaterFloorComponent.class)
//	static final ComponentMapper<ParticleEffectComponent> peCom = ComponentMapper.getFor(ParticleEffectComponent.class)
// 	static final ComponentMapper<WallComponent> wallCom = ComponentMapper.getFor(WallComponent.class)

	static final ComponentMapper<SdBodyComponent> bCom = ComponentMapper.getFor(SdBodyComponent.class)
	static final ComponentMapper<BulletComponent> bulletCom = ComponentMapper.getFor(BulletComponent.class)
	static final ComponentMapper<CollisionComponent> collisionCom = ComponentMapper.getFor(CollisionComponent.class)
	static final ComponentMapper<EnemyComponent> enemyCom = ComponentMapper.getFor(EnemyComponent.class)
	static final ComponentMapper<PlayerComponent> playerCom = ComponentMapper.getFor(PlayerComponent.class)
	static final ComponentMapper<StateComponent> stateCom = ComponentMapper.getFor(StateComponent.class)
	static final ComponentMapper<TextureComponent> texCom = ComponentMapper.getFor(TextureComponent.class)
	static final ComponentMapper<TransformComponent> transCom = ComponentMapper.getFor(TransformComponent.class)
	static final ComponentMapper<TypeComponent> typeCom = ComponentMapper.getFor(TypeComponent.class)
	static final ComponentMapper<SteeringComponent> sCom = ComponentMapper.getFor(SteeringComponent.class)
	static final ComponentMapper<ParticleEffectComponent> peCom = ComponentMapper.getFor(ParticleEffectComponent.class)
	static final ComponentMapper<ScoreComponent> scoreCom = ComponentMapper.getFor(ScoreComponent.class)
	static final ComponentMapper<SoundEffectComponent> soundCom = ComponentMapper.getFor(SoundEffectComponent.class)
}
