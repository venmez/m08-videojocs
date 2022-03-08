package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class Enemic {

	float velocitat;
	Rectangle rectangle;
	Texture texture;
	
	public Enemic(float velocitat, Rectangle rectangle, Texture texture) {
		super();
		this.velocitat = velocitat;
		this.rectangle = rectangle;
		this.texture = texture;
	}

	public void moure(float delta) {
		
		rectangle.y -= velocitat * delta;
	}
	
	public void dibuixa(Batch batch) {
		
		batch.draw(texture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		
	}
	
	public boolean colisionar(Rectangle rectangle) {
		
		return this.rectangle.overlaps(rectangle);
	}
	
}
