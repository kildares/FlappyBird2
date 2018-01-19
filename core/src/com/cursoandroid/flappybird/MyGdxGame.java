package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.css.Rect;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	private Circle passaroCirculo;
	private Rectangle canoTopoRect;
	private Rectangle canoBaixoRect;
	private ShapeRenderer shape;

	private SpriteBatch batch;
	private Texture[] passaro;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
    private Texture gameOver;

	private BitmapFont font;

	private int gameState; //0: jogo nao iniciado. 1: jogo iniciado.
	private int pontuacao;

	private float larguraTela;
	private float alturaTela;
	private float spritePassaro;
	private int velocidadeQueda;
	private int posicaoVertical;
	private int posCanoHorizontal;
	private int espacoEntreCanos;
	private float deltaTime;
	private Random numRandom;
	private float alturaRandom;
	private boolean pointScored;

	private static final int GAME_NOT_STARTED = 0;
	private static final int GAME_STARTED = 1;
    private static final int GAME_OVER = 2;
	private static final int POS_PASSARO = 80;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;
	@Override
	public void create () {

		gameState=GAME_NOT_STARTED;

		font = new BitmapFont();
		fundo = new Texture("fundo.png");
		batch = new SpriteBatch();
		passaro = new Texture[3];
		passaro[0] = new Texture("passaro1.png");
		passaro[1] = new Texture("passaro2.png");
		passaro[2] = new Texture("passaro3.png");
        gameOver = new Texture("game_over.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

		passaroCirculo = new Circle();
		canoBaixoRect = new Rectangle();
		canoTopoRect = new Rectangle();
		shape = new ShapeRenderer();

		font.setColor(Color.WHITE);
		font.getData().setScale(6);

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,camera);
        larguraTela=VIRTUAL_WIDTH;
        alturaTela=VIRTUAL_HEIGHT;

		spritePassaro=0;
		velocidadeQueda=0;
		posicaoVertical= (int) (alturaTela/2);
		posCanoHorizontal= (int) (larguraTela-100);
		espacoEntreCanos=300;
		numRandom = new Random();
		pontuacao=0;
		pointScored=false;


	}

	@Override
	public void render () {

        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT  );

		deltaTime = Gdx.graphics.getDeltaTime() * 10;
		spritePassaro += deltaTime;
		if(spritePassaro>2.0)
			spritePassaro= (float) 0.0;

        batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//shape.begin(ShapeRenderer.ShapeType.Filled);
		//shape.circle(passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius);
		//shape.setColor(Color.RED);

		if(gameState == GAME_STARTED){
			runGame();
		}
		else if(gameState == GAME_OVER){

            if(Gdx.input.justTouched()) {
                restartGame();
            }
            quedaPassaro(false);
            batch.draw(canoTopo,posCanoHorizontal,alturaTela/2+espacoEntreCanos/2 + alturaRandom);
            batch.draw(canoBaixo,posCanoHorizontal,alturaTela/2 - canoBaixo.getHeight()-espacoEntreCanos/2 + alturaRandom);
            batch.draw(gameOver,larguraTela/2 - gameOver.getWidth()/2,alturaTela/2- gameOver.getHeight());
        }
		else if(Gdx.input.justTouched()){
				gameState = GAME_STARTED;
		}
		else{
            batch.draw(fundo,0,0,larguraTela,alturaTela);
            batch.draw(passaro[(int)spritePassaro],POS_PASSARO,posicaoVertical);
            font.draw(batch,Integer.toString(pontuacao),larguraTela/2,alturaTela-50);
        }

		batch.end();

	}
	
	@Override
	public void dispose (){
		//batch.dispose();
//		img.dispose();
	}

    public void restartGame() {
        spritePassaro=0;
        velocidadeQueda=0;
        posicaoVertical= (int) (alturaTela/2);
        posCanoHorizontal= (int) (larguraTela-100);
        espacoEntreCanos=300;
        numRandom = new Random();
        pontuacao=0;
        gameState= GAME_STARTED;
    }


    public void runGame(){

		posCanoHorizontal -= (int)(deltaTime * 50);
		Gdx.app.log("variacao","variacao:" + Gdx.graphics.getDeltaTime());

        quedaPassaro(true);

		if(posCanoHorizontal < -canoTopo.getWidth()){
			posCanoHorizontal = (int) larguraTela;
			alturaRandom = numRandom.nextInt(400)-200;
			pointScored=false;
		}

		if(posCanoHorizontal < POS_PASSARO){
			if(!pointScored){
				pontuacao++;
				pointScored=true;
			}

		}
        passaroCirculo.set(POS_PASSARO+(passaro[0].getWidth()/2),posicaoVertical + (passaro[0].getWidth()/2),30);
        canoTopoRect.set(posCanoHorizontal,alturaTela/2+espacoEntreCanos/2 + alturaRandom,canoTopo.getWidth(),canoTopo.getHeight());
		canoBaixoRect.set(posCanoHorizontal,alturaTela/2 - canoBaixo.getHeight()-espacoEntreCanos/2 + alturaRandom, canoBaixo.getWidth(),canoBaixo.getHeight());

        //shape.rect(canoTopoRect.x,canoTopoRect.y,canoTopoRect.width,canoTopoRect.height);
        //shape.rect(canoBaixoRect.x,canoBaixoRect.y,canoBaixoRect.width,canoBaixoRect.height);

		batch.draw(canoTopo,posCanoHorizontal,alturaTela/2+espacoEntreCanos/2 + alturaRandom);
		batch.draw(canoBaixo,posCanoHorizontal,alturaTela/2 - canoBaixo.getHeight()-espacoEntreCanos/2 + alturaRandom);

        if(Intersector.overlaps(passaroCirculo,canoBaixoRect)||Intersector.overlaps(passaroCirculo,canoTopoRect)){
            gameState = GAME_OVER;
        }

	}

	public void quedaPassaro(boolean checkClick){

        velocidadeQueda=velocidadeQueda++;
        if(checkClick){
            if(Gdx.input.justTouched()){
                Gdx.app.log("Toque","Toque na tela");
                velocidadeQueda=-20;
            }
        }

        if(posicaoVertical > 0 || velocidadeQueda<0)
            posicaoVertical -= velocidadeQueda;

        if(gameState==GAME_STARTED && posicaoVertical<=0){
            gameState=GAME_OVER;
        }

        batch.draw(fundo,0,0,larguraTela,alturaTela);
        batch.draw(passaro[(int)spritePassaro],POS_PASSARO,posicaoVertical);
        font.draw(batch,Integer.toString(pontuacao),larguraTela/2,alturaTela-50);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}
