package com.stardecimal.game.util

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

class MyOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {
	MyOrthogonalTiledMapRenderer(TiledMap map) {
		super(map)
	}

	MyOrthogonalTiledMapRenderer(TiledMap map, Batch batch) {
		super(map, batch)
	}

	MyOrthogonalTiledMapRenderer(TiledMap map, float unitScale) {
		super(map, unitScale)
	}

	MyOrthogonalTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
		super(map, unitScale, batch)
	}

	@Override
	void renderObject(MapObject object) {
		if (object instanceof TextureMapObject) {
			TextureMapObject textureObject = (TextureMapObject) object

			batch.draw(
					textureObject.textureRegion,
					textureObject.x * unitScale as float,
					textureObject.y * unitScale as float,
					textureObject.originX,
					textureObject.originY,
					textureObject.textureRegion.regionWidth * unitScale as float,
					textureObject.textureRegion.regionHeight * unitScale as float,
					textureObject.scaleX,
					textureObject.scaleY,
					textureObject.rotation
			)
		}
	}
}
