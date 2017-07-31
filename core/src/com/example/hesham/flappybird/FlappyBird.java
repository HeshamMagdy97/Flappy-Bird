package com.example.hesham.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

import java.util.Random;

import sun.java2d.pipe.SpanShapeRenderer;

public class FlappyBird extends ApplicationAdapter {
    int game_state = 0;
    SpriteBatch batch;
    Texture background;
    Texture gameover;
    Rectangle[] toprec;
    Rectangle[] bottomrec;
    int score=0;
    int scoreing_tube=0;
    int best;

    private Preferences preferences;
    BitmapFont font;

    Texture[] birds;
    Circle birdcircle;
    int flappy_state;
    float birdY = 0;
    float velocity=0;
    float gravity=2;

    Texture tobtube;
    Texture bottomtube;
    float gap=400;
    float maxtubeoffset;
    float tubevelocity=4;
    int numberoftubes=4;
    float[] tubeX = new float[numberoftubes];
    float[] tubeoffset = new float[numberoftubes];


    float destancebetweentubes;
    Random random ;

    public FlappyBird() {
    }

    @Override
	public void create () {
		batch = new SpriteBatch();
        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        birdcircle =new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        preferences=Gdx.app.getPreferences("My Preferences");
        try {
            best =preferences.getInteger("Best");
        }catch (Exception e) {
            best =0;
        }

        tobtube = new Texture("toptube.png");
        bottomtube = new Texture("bottomtube.png");
        maxtubeoffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100 ;
        destancebetweentubes =Gdx.graphics.getWidth() * 3 / 4;
        toprec = new Rectangle[numberoftubes];
        bottomrec = new Rectangle[numberoftubes];
        random = new Random();


        startgame();
    }

    public void startgame(){
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
        for(int i=0;i<numberoftubes;i++){

            tubeoffset[i] =(random.nextFloat() -0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] =Gdx.graphics.getWidth()/2 - tobtube.getWidth()/2 + Gdx.graphics.getWidth() + i*destancebetweentubes;

            toprec[i]=new Rectangle();
            bottomrec[i]= new Rectangle();
        }

        scoreing_tube=0;
        score=0;
        velocity=0;
    }
	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (game_state == 1) {

            if(tubeX[scoreing_tube] < Gdx.graphics.getWidth() / 2){

                score++;
               // scoreing_tube = scoreing_tube < numberoftubes - 1 ? scoreing_tube++ : 0 ;
                if(scoreing_tube < numberoftubes - 1){
                    scoreing_tube++;
                }else{
                    scoreing_tube=0;
                }
            }
            if (Gdx.input.isTouched()) {

                velocity = -10;


            }

            for(int i=0;i<numberoftubes;i++) {

                if(tubeX[i] < -tobtube.getWidth() ){
                    tubeX[i]=numberoftubes * destancebetweentubes;
                    tubeoffset[i] =(random.nextFloat() -0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                }else {
                    tubeX[i] = tubeX[i] - tubevelocity;
                }
                batch.draw(tobtube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeoffset[i]);
                batch.draw(bottomtube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i]);

                toprec[i]= new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeoffset[i],tobtube.getWidth(),tobtube.getHeight());
                bottomrec[i]=new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i],bottomtube.getWidth(),bottomtube.getHeight());
            }

            if(birdY > 0 ) {
                velocity += gravity;
                birdY -= velocity;
            }else {
                game_state=2;
            }

        }else if (game_state==0) {
            if (Gdx.input.isTouched()) {
                game_state=1;
            }
        }
        else if (game_state==2){
            batch.draw(gameover,Gdx.graphics.getWidth() / 2 - gameover.getWidth() /2,Gdx.graphics.getHeight() / 2 - gameover.getHeight() /2 );
            if (best <= score) {
                preferences.putInteger("Best", score);
                preferences.flush();
            }

            if (Gdx.input.isTouched()) {
                game_state=1;
                startgame();
            }
        }

        flappy_state = flappy_state == 0 ? 1 : 0;
        if (best < score) {
            best=score;
        }
        font.draw(batch,String.valueOf(score),100,200);
        font.draw(batch, String.valueOf(best), Gdx.graphics.getWidth() - 100, 200);


        batch.draw(birds[flappy_state], Gdx.graphics.getWidth() / 2 - birds[flappy_state].getWidth() / 2, birdY);
        batch.end();

        birdcircle.set(Gdx.graphics.getWidth() / 2 ,birdY + birds[flappy_state].getHeight() / 2,birds[0].getWidth() / 2);

        for(int i=0;i<numberoftubes;i++) {
            if(Intersector.overlaps(birdcircle,bottomrec[i]) || Intersector.overlaps(birdcircle,toprec[i])){
                game_state=2;
            }
        }

    }
}
