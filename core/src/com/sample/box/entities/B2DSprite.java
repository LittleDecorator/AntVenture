package com.sample.box.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.sample.box.handlers.Animation;

import java.util.HashMap;
import java.util.Map;

import static  com.sample.box.handlers.B2DVars.PPM;
import static com.sample.box.utils.Console.log;

public class B2DSprite {

    protected Body body;
    protected Animation animation;
    protected float width;
    protected float height;
    protected Map<String,PositionedFixture> fixtureMap = new HashMap<String, PositionedFixture>();

    public B2DSprite(Body body) {
        this.body = body;
        animation = new Animation();
    }

    public void setAnimation(TextureRegion reg, float delay) {
        setAnimation(new TextureRegion[] { reg }, delay);
    }

    public void setAnimation(TextureRegion[] reg, float delay){
        animation.setFrames(reg,delay);
        width = reg[0].getRegionWidth();
        height = reg[0].getRegionHeight();
    }

    public void update(float dt){
        animation.update(dt);
    }

    public void render(SpriteBatch sb){
        sb.begin();
        sb.draw(animation.getFrame(), body.getPosition().x * PPM - width/2, body.getPosition().y * PPM - height/2);
        sb.end();
    }

    public Body getBody(){ return body; }

    public void setBody(Body body) {
        this.body = body;
    }

    public Vector2 getPosition(){ return body.getPosition(); }
    public float getWidth(){ return width; }
    public float getHeight(){ return height; }
}
