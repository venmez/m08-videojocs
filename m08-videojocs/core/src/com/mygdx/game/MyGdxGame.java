package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.SortedIntList.Iterator;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private Rectangle player;
	private OrthographicCamera camera;
	private Array<Enemic> raindrops;
	private long lastDropTime;
	private Texture dropImage;
	private Texture playerLaserImage;
	private Array<Rectangle> playerLasers;
	private Texture enemyLaserImage;
	private Array<Rectangle> enemyLasers;
	private Rectangle enemy;
	private Texture enemyImage;
	private static final int VELOCITAT = 250;
	private float velFinal = 0;
	BitmapFont playerScoreFont;
	int puntuacioPlayer = 0;
	BitmapFont playerLives;
	int playerRemainingLives = 5;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		playerScoreFont = new BitmapFont();
		playerScoreFont.getData().setScale(1.2f, 1.2f);
		playerLives = new BitmapFont();
		playerLives.getData().setScale(1.2f, 1.2f);
		img = new Texture("img/PNG/playerShip2_blue.png");
		enemyImage = new Texture("img/PNG/Enemies/enemyBlack3.png");
		dropImage = new Texture("img/PNG/Meteors/meteorGrey_med1.png");
		playerLaserImage = new Texture("img/PNG/Lasers/laserBlue01.png");
		player = new Rectangle(0, 0, 80, 80);
		enemy = new Rectangle(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 85, 85, 85);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		raindrops = new Array<Enemic>();
		playerLasers = new Array<Rectangle>();
		enemyLaserImage = new Texture("img/PNG/Lasers/laserRed01.png");
		enemyLasers = new Array<Rectangle>();
		
		spawnRaindrop();
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		playerScoreFont.draw(batch, "Player: "+ puntuacioPlayer, 30, Gdx.graphics.getHeight() - 30);
		playerScoreFont.draw(batch, "Lives: "+ playerRemainingLives, 30, Gdx.graphics.getHeight() - 60);
		//Dibujar nave aliada
		batch.draw(img, player.getX(), player.getY(), player.getWidth(), player.getHeight());
		//Dibujar nave enemiga
		batch.draw(enemyImage, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
		// Posicion de la nave jugador.
		playerMovement();
		// Movimiento enemigo
		enemyMovement();
		//Ataque de la nave
		 if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			 spawnPlayerLaser();
		 }
		//Ataque del enemigo
		 if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			 spawnEnemyLaser();
		 }
		// Bounds pantalla
		 setBounds();
		// Raindrops down
		 raindropMovement();
		// Disparos jugador up
		 playerShootMovement();
		//Disparos enemy down
		 enemyShootMovement();
			

		 //End game
		 if (playerRemainingLives == 0) {
			 Gdx.app.exit();
		}
		batch.end();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	private void spawnRaindrop() {
		Rectangle rain = new Rectangle();
		
		rain.x = MathUtils.random(0, 800 - 64);
		rain.y = 480;
		rain.width = 40;
		rain.height = 40;
		Enemic raindrop = new Enemic(MathUtils.random(200, 600),rain, dropImage);
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void raindropMovement() {
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRaindrop();
		for (ArrayIterator<Enemic> iter = raindrops.iterator(); iter.hasNext();) {
			Enemic raindrop = iter.next();
			raindrop.moure(Gdx.graphics.getDeltaTime());
			if (raindrop.rectangle.y + 64 < 0)
				iter.remove();
			if (raindrop.colisionar(player)) {
				iter.remove();
				playerRemainingLives--;
			}
		}
		for (Enemic raindrop : raindrops) {
			raindrop.dibuixa(batch);
		}
	}

	private void spawnPlayerLaser() {
		Rectangle playerLaser = new Rectangle();
		playerLaser.x = player.x + 35;
		playerLaser.y = player.y + 80;
		playerLaser.width = 64;
		playerLaser.height = 64;
		playerLasers.add(playerLaser);
	}
	
	private void spawnEnemyLaser() {
		Rectangle enemyLaser = new Rectangle();
		enemyLaser.x = enemy.x + 40;
		enemyLaser.y = enemy.y - 50;
		enemyLaser.width = 64;
		enemyLaser.height = 64;
		enemyLasers.add(enemyLaser);
	}


	private void playerMovement() {
		velFinal = VELOCITAT * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			player.x -= velFinal;
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			player.x += velFinal;
		if (Gdx.input.isKeyPressed(Input.Keys.UP))
			player.y += velFinal;
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
			player.y -= velFinal;
	}
	
	private void enemyMovement() {
		velFinal = VELOCITAT * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.A))
			enemy.x -= velFinal;
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			enemy.x += velFinal;
		if (Gdx.input.isKeyPressed(Input.Keys.W))
			enemy.y += velFinal;
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			enemy.y -= velFinal;
	}

	private void playerShootMovement() {

		for (ArrayIterator<Rectangle> iter = playerLasers.iterator(); iter.hasNext();) {
			Rectangle playerLaser = iter.next();
			playerLaser.y += 200 * Gdx.graphics.getDeltaTime();
			if (playerLaser.y + 64 < 0)
				iter.remove();
			if (playerLaser.overlaps(enemy)) {
				iter.remove();
				puntuacioPlayer += 1;
			}
		}
		for (Rectangle playerLaser : playerLasers) {
			batch.draw(playerLaserImage, playerLaser.x, playerLaser.y);
		}
	}
	
	private void enemyShootMovement() {

		for (ArrayIterator<Rectangle> iter = enemyLasers.iterator(); iter.hasNext();) {
			Rectangle enemyLaser = iter.next();
			enemyLaser.y -= 200 * Gdx.graphics.getDeltaTime();
			if (enemyLaser.y + 64 < 0)
				iter.remove();
			if (enemyLaser.overlaps(player)) {
				iter.remove();
				playerRemainingLives--;
			}
		}
		for (Rectangle enemyLaser : enemyLasers) {
			batch.draw(enemyLaserImage, enemyLaser.x, enemyLaser.y);
		}
	}

	private void setBounds() {
		if (player.x < 0)
			player.x = 0;
		
		if (player.x > Gdx.graphics.getWidth())
			player.x = Gdx.graphics.getWidth();
		
		if (player.y < 0)
			player.y = 0;
		
		if (player.y > Gdx.graphics.getHeight() - player.height)
			player.y = Gdx.graphics.getHeight() - player.height;
		
		if (enemy.x < 0)
			enemy.x = 0;
		
		if (enemy.x > Gdx.graphics.getWidth())
			enemy.x = Gdx.graphics.getWidth();
		
		if (enemy.y < 0)
			enemy.y = 0;
		
		if (enemy.y > Gdx.graphics.getHeight() - enemy.height)
			enemy.y = Gdx.graphics.getHeight() - enemy.height;
		
	}

}
