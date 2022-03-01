package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.SortedIntList.Iterator;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Rectangle player;
	private OrthographicCamera camera;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private Texture dropImage;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("img/spaceship.png");
		dropImage = new Texture("img/laserGreen15.png");
		player = new Rectangle(0, 0, 150, 150);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		raindrops = new Array<Rectangle>();
		 spawnRaindrop();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.draw(img, player.getX(), player.getY(), player.getWidth(), player.getHeight());
		for(Rectangle raindrop: raindrops) {
		      batch.draw(dropImage, raindrop.x, raindrop.y);
		   }
		 if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.x -= 5;
		 if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.x += 5;
		 if(Gdx.input.isKeyPressed(Input.Keys.UP)) player.y += 5;
		 if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) player.y -= 5;
		 if(player.x < 0) player.x = 0;
		 if(player.x > Gdx.graphics.getWidth()) player.x = Gdx.graphics.getWidth();
		 if(player.y < 0) player.y = 0;
		 if(player.y > Gdx.graphics.getHeight() - player.height) player.y = Gdx.graphics.getHeight() - player.height;
		 if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
			for (ArrayIterator<Rectangle> iter = raindrops.iterator(); iter.hasNext();) {
				Rectangle raindrop = iter.next();
				raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
				if (raindrop.y + 64 < 0)
					iter.remove();
				if(raindrop.overlaps(player)) {
		            iter.remove();
		         }
			}
			
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
	
	private void spawnRaindrop() {
	      Rectangle raindrop = new Rectangle();
	      raindrop.x = MathUtils.random(0, 800-64);
	      raindrop.y = 480;
	      raindrop.width = 64;
	      raindrop.height = 64;
	      raindrops.add(raindrop);
	      lastDropTime = TimeUtils.nanoTime();
	   }
	
	
}
