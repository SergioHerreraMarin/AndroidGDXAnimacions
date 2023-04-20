package com.animacionsgdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	// Constant rows and columns of the sprite sheet
	private static final int FRAME_COLS = 2, FRAME_ROWS = 2;

	// Objects used
	Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
	Texture walkSheet;
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;

	// A variable for tracking elapsed time for the animation
	float stateTime;
	float scaleFactor = 0.3f;

	//Player movement
	private float playerPosX;
	private float playerPosY;
	private Rectangle reclangleUp, rectangleDown, rectangleLeft, rectangleRight;
	private float playerSpeed = 220;
	private Vector3 playerPos;
	private Vector3 playerDirectionLeft, playerDirectionRight, playerDirectionUp, playerDirectionDown, playerIdle;
	private int playerDirection;

	@Override
	public void create () {


		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// Load the sprite sheet as a Texture
		walkSheet = new Texture(Gdx.files.internal("AmongSpritesheet1024x1024_02.png"));

		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);

		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}

		// Initialize the Animation with the frame interval and array of frames
		walkAnimation = new Animation<TextureRegion>(0.15f, walkFrames);

		// Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;


		playerPos = new Vector3((512 - (walkSheet.getWidth() / 2)) + 300,50,0);

		reclangleUp = new Rectangle(0, 480 * 2/3, 800, 480 /3);
		rectangleDown = new Rectangle(0, 0, 800, 480/3);
		rectangleLeft = new Rectangle(0, 0, 800/3, 480);
		rectangleRight = new Rectangle(800 *2/3, 0, 800/3, 480);

		playerDirectionUp = new Vector3(0,1,0);
		playerDirectionDown = new Vector3(0,-1,0);
		playerDirectionRight = new Vector3(1,0,0);
		playerDirectionLeft = new Vector3(-1,0,0);
		playerIdle = new Vector3(0,0,0);
		playerDirection = 1;

	}


	@Override
	public void render () {

		ScreenUtils.clear(0.447f, 0.533f, 0.569f, 1);
		camera.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		// Get current frame of animation for the current stateTime
		TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

		if(playerDirection == -1){
			currentFrame.flip(true, false);
		}else{
			currentFrame.flip(false, false);
		}

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(currentFrame,  playerPos.x += ((getPlayerDirection().x * playerSpeed) * Gdx.graphics.getDeltaTime()) , playerPos.y += ((getPlayerDirection().y * playerSpeed) * Gdx.graphics.getDeltaTime()), scaleFactor * currentFrame.getRegionWidth(), scaleFactor * currentFrame.getRegionHeight());
		spriteBatch.end();

		currentFrame.flip(currentFrame.isFlipX(), currentFrame.isFlipY());
	}


	private Vector3 getPlayerDirection() {
		// iterar per multitouch
		// cada "i" és un possible "touch" d'un dit a la pantalla
		for(int i=0;i<10;i++)
			if (Gdx.input.isTouched(i)) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				// traducció de coordenades reals (depen del dispositiu) a 800x480
				camera.unproject(touchPos);
				if (reclangleUp.contains(touchPos.x, touchPos.y)) {
					return playerDirectionUp;
				} else if (rectangleDown.contains(touchPos.x, touchPos.y)) {
					return playerDirectionDown;
				} else if (rectangleLeft.contains(touchPos.x, touchPos.y)) {
					playerDirection = -1;
					return playerDirectionLeft;
				} else if (rectangleRight.contains(touchPos.x, touchPos.y)) {
					playerDirection = 1;
					return playerDirectionRight;
				}
			}
		return playerIdle;
	}


	@Override
	public void dispose () { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
		walkSheet.dispose();
	}
}
