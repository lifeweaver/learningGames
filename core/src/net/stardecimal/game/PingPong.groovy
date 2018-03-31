package net.stardecimal.game

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class PingPong extends Rectangle  {
    final int BOUNCE_INCREASE = 25
    final int MAX_SPEED = 2100
    final float ELAPSED = 0.01f
    final int INITIAL_SPEED = 350
    Vector2 location = new Vector2()
    Vector2 direction = new Vector2(INITIAL_SPEED, 0)

    PingPong() {
        width = 10
        height = 10
    }

    @Override
    PingPong setX(float newX) {
        super.setX(newX)
        location.x = x
        return this
    }

    @Override
    PingPong setY(float newY) {
        super.setY(newY)
        location.y = y
        return this
    }

    void applyMovement() {
        if (direction.x > 0 && direction.x > MAX_SPEED) direction.x = MAX_SPEED
        if (direction.x < 0 && direction.x < -MAX_SPEED) direction.x = -MAX_SPEED

        if (direction.x > 0) {
            x += (float) direction.x * ELAPSED
        } else {
            x -= (float) -direction.x * ELAPSED
        }

        if (direction.y > 0) {
            y += (float) direction.y * ELAPSED
        } else {
            y -= (float) -direction.y * ELAPSED
        }
    }
}
