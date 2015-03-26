package pkg;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
<applet code="BigMyTet.class" width="400" height="600">
</applet>
*/
public class Tetoris extends Applet implements Runnable, KeyListener{

	private final int BLOCK=30;
    private int x=BLOCK*4;
    private int y=BLOCK;
    private final int[] BLOCKKINDS={1,2,3,5,10,14,16};

    private final int SPEED_LOW=1000;
    private final int SPEED_MIDIUM=700;
    private final int SPEED_EXTRA=400;
    private final int SPEED_GOD=150;
    private final int SPEED_HIGH=100;

    private int speed = SPEED_LOW;
    private int width=100*3;
    private int height=200*3;

    private int subWidth=width/BLOCK;
    private int subHeight=height/BLOCK;
    private int score = 0;
    private int plessEnter = 0;
    private int nextAreaX = width/4*5;
    private int nextAreaY = height/4*3;
    private int stock = 99;
    private int stockAreaX = width/4*5;
    private int stockAreaY = height/4*2-BLOCK;

    private int patternNextNext = BLOCKKINDS[(int)(Math.random()*BLOCKKINDS.length)];
    private int patternNext = BLOCKKINDS[(int)(Math.random()*BLOCKKINDS.length)];
    private int pattern = BLOCKKINDS[(int)(Math.random()*BLOCKKINDS.length)];

    private boolean[][] status=new boolean[subWidth][subHeight];
    private Color[][] colorStatus=new Color[subWidth][subHeight];

    private String StringScore = "";

	private boolean loopJudge = true;
	private boolean opLoopJudge = true;
	private boolean end = false;
	private boolean opening = true;
	private boolean gameOver = false;
	private boolean game = true;
	private boolean gameOverLoop = true;
	private boolean restartFlag = false;

	private Thread thread;

	private File file;

//	private boolean fileCre=false;

	private ArrayList<Integer> arr;
	private final List<Level> LEVEL_LIST = new ArrayList<Level>(){
		{
			add(new Level(40,new Color(255,250-5*10,255-5*10),"godfield",SPEED_GOD));
			add(new Level(20,new Color(255,250-8*10,255-8*10),"fantastic",SPEED_EXTRA));
			add(new Level(10,new Color(255,250-10*10,255-10*10),"expart",SPEED_MIDIUM));
			add(new Level(0,new Color(255,250-19*10,255-19*10),"normal",SPEED_LOW));
		}
	};

	private final Font DEFAULT_FONT = new Font(null,Font.PLAIN,20);
	private final Color DEFAULT_COLOR = Color.WHITE;

	private final String KEY_TITLE = "title";
	private final String KEY_TITLE_UNDER = "title_under";
	private final String KEY_SCORE_LABEL = "score_label";
	private final String KEY_SCORE = "score";
	private final String KEY_LEVEL = "level";
	private final String KEY_GAME_OVER = "game_over";
	private final String KEY_GAME_OVER_UNDER = "game_over_under";
	private final Map<String,ScreenString> SCREENSTRING = new HashMap<String,ScreenString>(){
		{
			put(KEY_TITLE,new ScreenString("TETRIS",Color.white,new Font(null,Font.BOLD,40),width/2/2,BLOCK*4));
			put(KEY_TITLE_UNDER,new ScreenString("Pless Enter",Color.white,new Font(null,Font.PLAIN,20),width/2/2+BLOCK/2,height/2+(BLOCK*6)));
			put(KEY_SCORE_LABEL,new ScreenString("score.",new Color(255,250-19*10,255-19*10),new Font(null,Font.PLAIN,20),width+20,40));
			put(KEY_SCORE,new ScreenString(null,new Color(255,250-19*10,255-19*10),new Font(null,Font.PLAIN,20),width+80,40));
			put(KEY_LEVEL,new ScreenString("Lv.",new Color(255,250-19*10,255-19*10),new Font(null,Font.PLAIN,20),width+20,80));
			put(KEY_GAME_OVER,new ScreenString("Game Over",new Color(255,250-19*10,255-19*10),new Font(null,Font.ITALIC,40),width/2/2/2,100));
			put(KEY_GAME_OVER_UNDER,new ScreenString("pless Enter one more Challenge!!",new Color(255,250-19*10,255-19*10),new Font(null,Font.ITALIC,20),2,150));
		}
	};
    @Override
    public void init() {
    	setSize(200*3,270*3);
        addKeyListener(this);
        thread=new Thread(this);
		thread.start();
    }

    @Override
    public void start() {
        if(thread==null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void paint(Graphics g) {
    	requestFocus();
    	if(opening){
    		//スタート画面デザイン
	    	for(int i=0;i<subHeight-1;i++){
    			for(int j=0;j<subWidth;j++){
    				int RectColor=0;
    				if(i*20<255){
    					RectColor=i*20;
    				}else{
    					RectColor=250;
    				}
        			g.setColor(new Color(255-RectColor,250-RectColor,255));
                	g.fillRect(j*BLOCK+1,i*BLOCK+1,BLOCK,BLOCK);
            	}
            }
	    	printLabel(g,SCREENSTRING.get(KEY_TITLE));
	    	if(plessEnter%2==0){
		    	printLabel(g,SCREENSTRING.get(KEY_TITLE_UNDER));
	    	}else{
	    		g.setColor(new Color(5,5,255));
				g.fillRect(width/2/2+BLOCK/2, height/2-10+(BLOCK*6), 70, 20);
	    	}
	    	//スタート画面デザイン（右の余白塗りつぶし）
	    	g.setColor(Color.white);
			g.fillRect(width+10, 0, width, height);
    	} else {
	    	//テトリス画面、ゲームオーバー画面（スコア表示）
	    	printLabel(g,SCREENSTRING.get(KEY_SCORE_LABEL));

	    	//スコアの表示
			StringScore=""+score;
			g.setColor(Color.white);
			g.fillRect(width+80, 20, 60, BLOCK);

			printLabel(g,new ScreenString(
							StringScore,
							SCREENSTRING.get(KEY_SCORE).getColor(),
							SCREENSTRING.get(KEY_SCORE).getFont(),
							SCREENSTRING.get(KEY_SCORE).getX(),
							SCREENSTRING.get(KEY_SCORE).getY()));

			//テトリス画面、ゲームオーバー画面（レベル表示）
	    	printLabel(g,SCREENSTRING.get(KEY_LEVEL));

			g.setColor(Color.white);
			g.fillRect(width+50, 60, 80, BLOCK);

			for(int i=0;i<LEVEL_LIST.size();i++){
				Level level = LEVEL_LIST.get(i);
				if(score >= level.getLower()){
					printLabel(g,new ScreenString(
									level.getStr(),
									level.getColor(),
									null,
									width+50,
									80));
					break;
				}
			}

			//テトリス画面、ゲームオーバー画面（ストックエリア）
			patternArea(g,"stock",stockAreaX,stockAreaY);
        	if(stock!=99){
        		System.out.print("paint  stock!=99[");
        		writeBlock(g,stock,stockAreaX,stockAreaY);
        		System.out.println("]");
        		writeLine(g,stockAreaX,stockAreaY);
        	}
			//テトリス画面、ゲームオーバー画面（ネクストエリア）
        	patternArea(g,"next",nextAreaX,nextAreaY);
    		System.out.print("paint  ネクストエリア[");
        	writeBlock(g,patternNext,nextAreaX,nextAreaY);
    		System.out.println("]");
        	writeLine(g,nextAreaX,nextAreaY);
	    	//ゲームオーバー画面（ゲームオーバーアニメーション）
	    	if(end && !gameOver){
	    		for(int i=0;i<subHeight-1;i++){
	    			for(int j=0;j<subWidth;j++){
            			g.setColor(status[j][i]?Color.WHITE:new Color(255,250-i*10,255-i*10,200));
                    	g.fillRect(j*BLOCK+1,i*BLOCK+1,29,29);
                    	try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
	            	}
	            }
		    	printLabel(g,SCREENSTRING.get(KEY_GAME_OVER));
		    	printLabel(g,SCREENSTRING.get(KEY_GAME_OVER_UNDER));
	    		gameOver=true;
	    	}
	    	//テトリス画面（たまったブロックの色設定）
	    	else if(!end){
		        g.setColor(Color.gray);
		    	g.fillRect(0,0,100*3,200*3-30);
		    	for(int i=0;i<subWidth;i++){
		    		for(int j=0;j<subHeight;j++){
		    			if(!status[i][j]){
//	    					g.setColor(Color.orange);//default
//	    					g.setColor(colorStatus[i][j]);
		    				oneBlock(g,i*BLOCK, j*BLOCK,colorStatus[i][j]);
		    			}
		    		}
		    	}
	    		System.out.print("paint  たまったブロックの色設定[");
		    	//テトリス画面（ブロック表示）
		        writeBlock(g,pattern,x,y);
	    		System.out.println("]");
		        //テトリス画面（罫線表示）
		        g.setColor(Color.white);
		        for(int i=0;i<subWidth+1;i++){
		        	g.drawLine(i*BLOCK, 0, i*BLOCK, height);
		        }
		        for(int i=0;i<subHeight;i++){
		        	g.drawLine(0, i*BLOCK, width, i*BLOCK);
		        }
	    	}
    	}
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private void printLabel(Graphics g,ScreenString screenStr){
    	if(screenStr.getColor() == null){
    		screenStr.setColor(DEFAULT_COLOR);
    	}
    	if(screenStr.getFont() == null){
    		screenStr.setFont(DEFAULT_FONT);
    	}
    	g.setColor(screenStr.getColor());
    	g.setFont(screenStr.getFont());
		g.drawString(screenStr.getStr(),screenStr.getX(), screenStr.getY());
    }
    /**
     * グローバル変数の初期化
     */
    public void syokika(){
    	speed=SPEED_LOW;
    	loopJudge=true;
    	opLoopJudge=true;
    	end=false;
    	score=0;
    	StringScore="";
    	opening=true;
    	plessEnter=0;
    	nextAreaX=width/4*5;
    	nextAreaY=height/4*3;
    	gameOver=false;
    	stock=99;
    	stockAreaX=width/4*5;
    	stockAreaY=height/4*2-BLOCK;
    	gameOverLoop=true;
    	patternNextNext=BLOCKKINDS[(int)(Math.random()*BLOCKKINDS.length)];
    	patternNext=BLOCKKINDS[(int)(Math.random()*BLOCKKINDS.length)];
    	pattern=BLOCKKINDS[(int)(Math.random()*BLOCKKINDS.length)];
    	restartFlag=false;
    }
    public void run() {
		while (game) {
			syokika();
//			System.out.println("①マスの初期化を行います");
	    	//マスの初期化
	    	for(int i=0;i<subWidth;i++){
	    		for(int j=0;j<subHeight;j++){
	    			if(j==subHeight - 1){
	    				status[i][j]=false;
	    			}else{
	    				status[i][j]=true;
	    			}
	    			if(j==subHeight - 1){
	    				colorStatus[i][j]=Color.WHITE;
	    			}else{
	    				colorStatus[i][j]=Color.ORANGE;
	    			}
	    		}
	    	}
//			System.out.println("①マスの初期化が終了しました");

//			System.out.println("②" + (opLoopJudge?"トップ画面のループに入ります":"トップ画面のループをスキップします"));
			while (opLoopJudge) {	//opループ
		    	try {
		    		Thread.sleep(speed);
		    	} catch (InterruptedException e) {
		    		e.printStackTrace();
		    	}
		    	repaint();
				plessEnter += 1;
			}
//			System.out.println("②トップ画面のループが終了しました");
//			System.out.println("③" + (loopJudge?"テトリスのループに入ります":"テトリスのループをスキップします"));
			//テトリスループ処理
			while (loopJudge) {
		    	try {
		    		Thread.sleep(speed);
		    	} catch (InterruptedException e) {
		    		e.printStackTrace();
		    	}
		    	repaint();
				kakutei();
				deleteBlock();
				speedChange();

				//ブロックを一マス進める
				y += BLOCK;
				gameOver();
			}
//			System.out.println("③テトリスのループが終了しました");
			//ゲームオーバー画面描画
			repaint();


			//リスタート処理
	    	//リスタート待受けの待ち
	    	try {
	    		Thread.sleep(subWidth*subHeight*20);
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    	restartFlag=true;
	    	//リスタート待受け
//			System.out.println("④" + (gameOverLoop?"リスタート待受けを開始します":"リスタート待受けをスキップします"));
	    	for(int i = 0;gameOverLoop; i++){
//	    		System.out.println("リスタート待受け:[" + i + "]");
	    		try {
	    			Thread.sleep(speed);
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
	    	}
//			System.out.println("④リスタート待受けを終了します");
		}

    }


    public void keyPressed(KeyEvent keyEvent) {

        if(keyEvent.getKeyCode()==KeyEvent.VK_LEFT) {
        	try {
				if(pattern==0){
				    if(x>=BLOCK && status[x/BLOCK-1][y/BLOCK+1] && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] && status[x/BLOCK-1][y/BLOCK-2]) {
				        x-=BLOCK;
				    }
				}
				else if(pattern==1) {
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK]) {
						x-=BLOCK;
					}
				}
				else if(pattern==2){
					if(x>=BLOCK && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==3){
					if(x>=BLOCK*2 && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-2][y/BLOCK-1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==4){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-2][y/BLOCK+1] && status[x/BLOCK-1][y/BLOCK-1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==5){
					if(x>=BLOCK && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK+1] && status[x/BLOCK-1][y/BLOCK-1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==6){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-2][y/BLOCK+1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==7){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-2][y/BLOCK+1] && status[x/BLOCK-2][y/BLOCK-1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==8){
					if(x>=BLOCK*2 && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK+1] && status[x/BLOCK-2][y/BLOCK-1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==9){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK][y/BLOCK-1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==10){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==11){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] && status[x/BLOCK-1][y/BLOCK+1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==12){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-1][y/BLOCK+1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==13){
					if(x>=BLOCK   && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] && status[x/BLOCK-1][y/BLOCK+1]) {
						x-=BLOCK;
					}
				}

				else if(pattern==14){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==15){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-2][y/BLOCK-1] && status[x/BLOCK-1][y/BLOCK+1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==16){
					if(x>=BLOCK*2 && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] && status[x/BLOCK-2][y/BLOCK+1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==17){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-2][y/BLOCK-1] ) {
						x-=BLOCK;
					}
				}
				else if(pattern==18){
					if(x>=BLOCK*1 && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] && status[x/BLOCK-1][y/BLOCK+1]) {
						x-=BLOCK;
					}
				}
				else if(pattern==19){
					if(x>=BLOCK*2 && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK][y/BLOCK+1] ) {
						x-=BLOCK;
					}
				}
			} catch (Exception e) {
				x=width/2;
				y=BLOCK;
				e.printStackTrace();
			}
        }

        if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
        	try {
				if(pattern==0){
					if(x<width-BLOCK && status[x/BLOCK+1][y/BLOCK+1] && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+1][y/BLOCK-2]){
					    x+=BLOCK;
					}
				}
				else if(pattern==1) {
					if(x<width-BLOCK*3 && status[x/BLOCK+3][y/BLOCK]) {
						x+=BLOCK;
					}
				}
				else if(pattern==2){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+2][y/BLOCK-1]) {
						x+=BLOCK;
					}
				}
				else if(pattern==3){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1]) {
						x+=BLOCK;
					}
				}
				else if(pattern==4){
					if(x<width-BLOCK && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK][y/BLOCK+1]) {
						x+=BLOCK;
					}
				}
				else if(pattern==5){
					if(x<width-BLOCK*2 && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+2][y/BLOCK+1]) {
						x+=BLOCK;
					}
				}
				else if(pattern==6){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK][y/BLOCK+1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==7){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+2][y/BLOCK+1] && status[x/BLOCK+2][y/BLOCK-1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==8){
					if(x<width-BLOCK && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+1][y/BLOCK+1]) {
						x+=BLOCK;
					}
				}
				else if(pattern==9){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+2][y/BLOCK-1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==10){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==11){
					if(x<width-BLOCK   && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+1][y/BLOCK+1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==12){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+1][y/BLOCK+1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==13){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+1][y/BLOCK+1] ) {
						x+=BLOCK;
					}
				}

				else if(pattern==14){
					if(x<width-BLOCK*2 && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+2][y/BLOCK-1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==15){
					if(x<width-BLOCK   && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK+1] && status[x/BLOCK][y/BLOCK-1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==16){
					if(x<width-BLOCK   && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK+1] && status[x/BLOCK+1][y/BLOCK-1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==17){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK][y/BLOCK-1]  ) {
						x+=BLOCK;
					}
				}
				else if(pattern==18){
					if(x<width-BLOCK*2 && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK+1] && status[x/BLOCK+2][y/BLOCK-1] ) {
						x+=BLOCK;
					}
				}
				else if(pattern==19){
					if(x<width-BLOCK*2 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+2][y/BLOCK+1] ) {
						x+=BLOCK;
					}
				}
			}catch (Exception e) {
				x=width/2;
				y=BLOCK;
				e.printStackTrace();
			}
        }

        if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            speed = SPEED_HIGH;
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
//    		System.out.println("Enterキー restartFlag:[" + restartFlag + "]");
            if (restartFlag) {
            	gameOverLoop=false;
            }else{
				if (opening) {
					opening = false;
				}
				if (opLoopJudge) {
					opLoopJudge = false;
				}
			}
        }

        //ストック処理
        if(keyEvent.getKeyCode() == KeyEvent.VK_P) {
        	try{
            	if (loopJudge && x >= BLOCK * 2
    						&& x <= width - BLOCK * 3 && y <= height - BLOCK * 2
    						&& y >= BLOCK * 2 && status[x / BLOCK][y / BLOCK]
    						&& status[x / BLOCK + 1][y / BLOCK - 1]
    						&& status[x / BLOCK][y / BLOCK - 1]
    						&& status[x / BLOCK - 1][y / BLOCK - 1]
    						&& status[x / BLOCK + 1][y / BLOCK + 1]
    						&& status[x / BLOCK][y / BLOCK + 1]
    						&& status[x / BLOCK - 1][y / BLOCK + 1]
    						&& status[x / BLOCK - 1][y / BLOCK]
    						&& status[x / BLOCK + 1][y / BLOCK]
    						&& status[x / BLOCK][y / BLOCK - 2]
    						&& status[x / BLOCK + 2][y / BLOCK]) {

    				//ストック処理（ストックが存在しない場合）
    				if (stock == 99) {
    					if (pattern == 0 || pattern == 1) {
    						stock = BLOCKKINDS[0];
    					} else if (pattern == 2) {
    						stock = BLOCKKINDS[1];
    					} else if (pattern == 3 || pattern == 4) {
    						stock = BLOCKKINDS[2];
    					} else if (pattern == 5 || pattern == 6 || pattern == 8 || pattern == 9) {
    						stock = BLOCKKINDS[3];
    					} else if (pattern == 10 || pattern == 11 || pattern == 12 || pattern == 13) {
    						stock = BLOCKKINDS[4];
    					} else if (pattern == 14 || pattern == 15) {
    						stock = BLOCKKINDS[5];
    					} else if (pattern == 16 || pattern == 17 || pattern == 18 || pattern == 19) {
    						stock = BLOCKKINDS[6];
    					}
    					patternDecide();
    				}
    				//ストック処理（ストックが存在する場合）
    				else if (stock != 99) {
    					patternNextNext = patternNext;
    					if (pattern == 0 || pattern == 1) {
    						patternNext = BLOCKKINDS[0];
    					} else if (pattern == 2) {
    						patternNext = BLOCKKINDS[1];
    					} else if (pattern == 3 || pattern == 4) {
    						patternNext = BLOCKKINDS[2];
    					} else if (pattern == 5 || pattern == 6 || pattern == 8 || pattern == 9) {
    						patternNext = BLOCKKINDS[3];
    					} else if (pattern == 10 || pattern == 11 || pattern == 12 || pattern == 13) {
    						patternNext = BLOCKKINDS[4];
    					} else if (pattern == 14 || pattern == 15) {
    						patternNext = BLOCKKINDS[5];
    					} else if (pattern == 16 || pattern == 17 || pattern == 18 || pattern == 19) {
    						patternNext = BLOCKKINDS[6];
    					}
    					pattern = stock;
    					stock = 99;
    				}
    			}
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
        }

        //開発用コマンド
        if(keyEvent.getKeyCode() == KeyEvent.VK_N) {
            score+=1;
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_Q) {
            try {
				if(status[x/BLOCK][y/BLOCK-2]){
					y -= BLOCK;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				y+=0;
				e.printStackTrace();
			}
        }

    }

    public void keyReleased(KeyEvent keyEvent) {

    	if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            speed = SPEED_LOW;
        }

    	if(keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
    		 try {
    			 if(pattern==0){
 					try {
						if (x >= BLOCK && x <= width - BLOCK * 3
								&& status[x / BLOCK - 1][y / BLOCK]
								&& status[x / BLOCK - 1][y / BLOCK + 1]
								&& status[x / BLOCK - 1][y / BLOCK - 1]
								&& status[x / BLOCK - 1][y / BLOCK - 2]
								&& status[x / BLOCK + 1][y / BLOCK]
								&& status[x / BLOCK + 1][y / BLOCK - 1]
								&& status[x / BLOCK + 1][y / BLOCK - 2]
								&& status[x / BLOCK + 1][y / BLOCK + 1]
								&& status[x / BLOCK + 2][y / BLOCK]
								&& status[x / BLOCK + 2][y / BLOCK - 1]
								&& status[x / BLOCK + 2][y / BLOCK - 2]
								&& status[x / BLOCK + 2][y / BLOCK + 1]) {
							pattern = 1;
						}
 					} catch (ArrayIndexOutOfBoundsException e) {
 						y+=BLOCK;
 						e.printStackTrace();
 					}
 				}
 				else if(pattern==1){
 					 if(y<=height-BLOCK*4){
 			             if(!status[x/BLOCK][y/BLOCK+2]){
 			            	 y-=BLOCK;
 			             }
 			             pattern = 0;
 					 }
 				}
 				else if(pattern==2){
					 pattern=2;
				}
				else if(pattern==3){
					 if(!status[x/BLOCK-1][y/BLOCK+1]){
						 y-=BLOCK;
					 }
					 if(status[x/BLOCK][y/BLOCK+1] && status[x/BLOCK-1][y/BLOCK+1] && status[x/BLOCK+1][y/BLOCK+1] ){
				         pattern = 4;
					 }
				}
				else if(pattern==4){
					 if(x<=width-BLOCK*2 && status[x/BLOCK+1][y/BLOCK]){
			             pattern = 3;
					 }
					 else if(x==width-BLOCK && status[x/BLOCK-1][y/BLOCK-1] && status[x/BLOCK-2][y/BLOCK-1] ){
						 x-=BLOCK;
			             pattern = 3;
					 }
				}
				else if(pattern==5){
					 if(x>=BLOCK && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK+1] && status[x/BLOCK-1][y/BLOCK-1]){
						 pattern=6;
					 }else if(x==0 && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+2][y/BLOCK+1] && status[x/BLOCK+2][y/BLOCK-1]){
						 x+=BLOCK;
						 pattern=6;
					 }
				}
				else if(pattern==6){
					 pattern=8;
				}
				else if(pattern==8){
					 if(x<width-BLOCK*1){
						 if(!status[x/BLOCK-1][y/BLOCK+1]){
							 y-=BLOCK;
						 }
						 pattern=9;
					 }else if(x==width-BLOCK && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-1][y/BLOCK] ){
						 x-=BLOCK;
			             pattern = 9;
					 }
				}
				else if(pattern==9){
					 if(y<=height-BLOCK*2 && status[x/BLOCK-1][y/BLOCK+1] && status[x/BLOCK][y/BLOCK+1] && status[x/BLOCK+1][y/BLOCK+1]){
						 pattern=5;
						 if(!status[x/BLOCK][y/BLOCK+2]){
							 y-=BLOCK;
						 }
					 }
				}
				else if(pattern==10){
					if(y<=height-BLOCK*3){
						 if(!status[x/BLOCK][y/BLOCK+2]){
							 y-=BLOCK;
						 }
						 pattern=11;
					}
				}
				else if(pattern==11){
					 if(x<width-BLOCK && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK+1] && status[x/BLOCK+1][y/BLOCK-1]){
						 pattern=12;
					 }else if(x==width-BLOCK && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-1][y/BLOCK+1] ){
						 x-=BLOCK;
			             pattern = 12;
					 }
				}
				else if(pattern==12){
					 pattern=13;
				}
				else if(pattern==13){
					 if(x>=BLOCK && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK+1] ){
						 pattern=10;
					 }else if(x==0 && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+2][y/BLOCK] ){
						 x+=BLOCK;
						 pattern=10;
					 }
				}
				else if(pattern==14){
					 if (status[x/BLOCK][y/BLOCK+1] && status[x/BLOCK-1][y/BLOCK+1] && status[x/BLOCK+1][y/BLOCK+1] ) {
						 if(!status[x/BLOCK][y/BLOCK+1]){
							 y-=BLOCK;
						 }
						pattern = 15;
					}
				}
				else if(pattern==15){
					 if(x<=width-BLOCK*2 && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+1][y/BLOCK+1]){
						 pattern=14;
					 }else if(x==width-BLOCK && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK][y/BLOCK-1] ){
						 x-=BLOCK;
			             pattern = 14;
					 }
				}
				else if(pattern==16 ){
					 if (x<=width-BLOCK*2 && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+1][y/BLOCK-1] && status[x/BLOCK+1][y/BLOCK+1]) {
						pattern = 17;
					 }else if(x==width-BLOCK && status[x/BLOCK-2][y/BLOCK-1] && status[x/BLOCK-2][y/BLOCK] && status[x/BLOCK-1][y/BLOCK]){
						 x-=BLOCK;
			             pattern = 17;
					 }
				}
				else if(pattern==17 ){
					 pattern=18;
				}
				else if(pattern==18 ){
					 if (x>=BLOCK && status[x/BLOCK-1][y/BLOCK] && status[x/BLOCK-1][y/BLOCK-1] && status[x/BLOCK-1][y/BLOCK+1]) {
						pattern = 19;
					}else if(x==0 && status[x/BLOCK+1][y/BLOCK] && status[x/BLOCK+2][y/BLOCK] && status[x/BLOCK+2][y/BLOCK+1]){
						 x+=BLOCK;
						 pattern=19;
					 }
				}
				else if(pattern==19 ){
					 pattern=16;
				}
				if (y>BLOCK && loopJudge) {
					repaint();
				}
			} catch (Exception e) {
				e.printStackTrace();
				y+=BLOCK;
			}
         }
    }

    public void keyTyped(KeyEvent e) {
    }
    /**
     * x軸y軸を初期位置に設定する
     */
    public void XYReset(){
        x=BLOCK*4;
        y=0;
    }
    /**
     * パターンの決定
     */
    public void patternDecide(){
        pattern = patternNext;
        patternNext = patternNextNext;
    	patternNextNext = BLOCKKINDS[(int)(Math.random()*BLOCKKINDS.length)];
    }
    /**
     * nextエリア、stockエリアの表示
     */
    public void patternArea(Graphics g,String name,int x,int y){
    	g.setColor(Color.lightGray);
    	g.drawString(name,x-BLOCK*2-2,y-BLOCK*2-3);
    	g.fillRect(x-BLOCK*2-2,y-BLOCK*2-2,BLOCK*6+4,BLOCK*5+4);
    	g.setColor(Color.white);
    	g.fillRect(x-BLOCK*2,y-BLOCK*2,BLOCK*6,BLOCK*5);
    }
    /**
     * ブロック描画を行う
     * @param g
     * 				グラフィックオブジェクト
     * @param number
     * 				ブロックナンバー
     * @param x
     * 				x軸
     * @param y
     * 				y軸
     * */
    public void writeBlock(Graphics g,int number,int x,int y){
    	System.out.print("writeBlock  number:[" + number + "]");
    	switch(number){
    	case 0:
        	blockShape(g,x,y+BLOCK,x,y,x,y-BLOCK,x,y-BLOCK*2,Color.orange);
    		break;
    	case 1:
        	blockShape(g,x-BLOCK,y,x,y,x+BLOCK,y,x+BLOCK*2,y,Color.orange);
    		break;
    	case 2:
        	blockShape(g,x,y,x+BLOCK,y,x,y-BLOCK,x+BLOCK,y-BLOCK,Color.yellow);
    		break;
    	case 3:
        	blockShape(g,x-BLOCK,y-BLOCK,x,y-BLOCK,x,y,x+BLOCK,y,Color.red);
    		break;
    	case 4:
        	blockShape(g,x,y-BLOCK,x,y,x-BLOCK,y,x-BLOCK,y+BLOCK,Color.red);
    		break;
    	case 5:
        	blockShape(g,x,y-BLOCK,x,y,x,y+BLOCK,x+BLOCK,y+BLOCK,Color.magenta);
    		break;
    	case 6:
        	blockShape(g,x+BLOCK,y,x,y,x-BLOCK,y,x-BLOCK,y+BLOCK,Color.magenta);
    		break;
    	case 8:
        	blockShape(g,x,y-BLOCK,x,y,x-BLOCK,y-BLOCK,x,y+BLOCK,Color.magenta);
    		break;
    	case 9:
        	blockShape(g,x+BLOCK,y,x,y,x-BLOCK,y,x+BLOCK,y-BLOCK,Color.magenta);
    		break;
    	case 10:
    		//□□□□□□□
    		//□□□■□□□
    		//□□■■■□□
    		//□□□□□□□
        	blockShape(g,x+BLOCK,y,x,y,x-BLOCK,y,x,y-BLOCK,Color.pink);
    		break;
    	case 11:
        	blockShape(g,x,y+BLOCK,x,y,x-BLOCK,y,x,y-BLOCK,Color.pink);
    		break;
    	case 12:
        	blockShape(g,x+BLOCK,y,x,y,x-BLOCK,y,x,y+BLOCK,Color.pink);
    		break;
    	case 13:
        	blockShape(g,x+BLOCK,y,x,y,x,y+BLOCK,x,y-BLOCK,Color.pink);
    		break;
    	case 14:
        	blockShape(g,x-BLOCK,y,x,y,x,y-BLOCK,x+BLOCK,y-BLOCK,Color.green);
    		break;
    	case 15:
        	blockShape(g,x-BLOCK,y,x,y,x-BLOCK,y-BLOCK,x,y+BLOCK,Color.green);
    		break;
    	case 16:
        	blockShape(g,x,y-BLOCK,x,y,x,y+BLOCK,x-BLOCK,y+BLOCK,Color.cyan);
    		break;
    	case 17:
        	blockShape(g,x-BLOCK,y,x,y,x+BLOCK,y,x-BLOCK,y-BLOCK,Color.cyan);
    		break;
    	case 18:
        	blockShape(g,x,y-BLOCK,x,y,x,y+BLOCK,x+BLOCK,y-BLOCK,Color.cyan);
    		break;
    	case 19:
        	blockShape(g,x-BLOCK,y,x,y,x+BLOCK,y,x+BLOCK,y+BLOCK,Color.cyan);
    		break;
    	default:
    		break;
    	}
    }
    /**
     * nextエリア、stockエリアの罫線表示
     */
    public void writeLine(Graphics g,int x,int y){
    	g.setColor(Color.white);
        for(int i=0;i<4;i++){
        	g.drawLine(x-BLOCK, y-BLOCK+(BLOCK*i), x+BLOCK*3, y-BLOCK+(BLOCK*i));
        }
        for(int i=0;i<5;i++){
        	g.drawLine(x-BLOCK+(BLOCK*i), y-BLOCK, x-BLOCK+(BLOCK*i), y+BLOCK*2);
        }
    }
    /**
     * ブロックを表示する
     * @param g
     * 				グラフィックオブジェクト
     * @param x1
     * 				ブロック1x軸
     * @param y1
     * 				ブロック1y軸
     * @param x2
     * 				ブロック2x軸
     * @param y2
     * 				ブロック2y軸
     * @param x3
     * 				ブロック3x軸
     * @param y3
     * 				ブロック3y軸
     * @param x4
     * 				ブロック4x軸
     * @param y4
     * 				ブロック4y軸
     * @param color
     * 				色
     */
    public void blockShape(Graphics g,int x1,int y1,int x2,int y2,int x3,int y3,int x4,int y4,Color color){
    	oneBlock(g,x1,y1,color);
    	oneBlock(g,x2,y2,color);
    	oneBlock(g,x3,y3,color);
    	oneBlock(g,x4,y4,color);
    }
    /**
     * 指定された位置にブロックを表示する
     * @param g
     * 				グラフィックオブジェクト
     * @param x
     * 				x軸
     * @param y
     * 				y軸
     * @param color
     * 				色
     */
    public void oneBlock(Graphics g,int x,int y, Color color){
    	g.setColor(color);
    	g.fillRect(x,y,BLOCK,BLOCK);
    	Color tempColor=g.getColor();
    	g.setColor(Color.white);
    	g.fillRect(x+BLOCK/5, y+BLOCK/4, BLOCK/7, BLOCK/7);
    	g.fillRect(x+BLOCK/5*2, y+BLOCK/4, BLOCK/2, BLOCK/7);
    	g.setColor(tempColor);
    }
    /**
     * ブロックの形色確定処理
     */
    public void kakutei(){
    	try {
    		switch(pattern){
    		case 0:
    			if (!status[x / BLOCK][y / BLOCK + 2]) {
    				updateStatus(Color.ORANGE,false,0,1,0,0,0,-1,0,-2);
    			}
    			break;
    		case 1:
    			if (!status[x / BLOCK + 2][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]
    					|| !status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK - 1][y / BLOCK + 1]) {
    				updateStatus(Color.ORANGE,false,2,0,1,0,0,0,-1,0);
    			}
    			break;
    		case 2:
    			if (!status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]) {
    				updateStatus(Color.YELLOW,false,0,0,1,0,0,-1,1,-1);
    			}
    			break;
    		case 3:
    			if (!status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]
    					|| !status[x / BLOCK - 1][y / BLOCK]) {
    				updateStatus(Color.RED,false,-1,-1,0,-1,0,0,1,0);
    			}
    			break;
    		case 4:
    			if (!status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK - 1][y / BLOCK + 2]) {
    				updateStatus(Color.RED,false,0,-1,0,0,-1,0,-1,1);
    			}
    			break;
    		case 5:
    			if (!status[x / BLOCK][y / BLOCK + 2]
    					|| !status[x / BLOCK + 1][y / BLOCK + 2]) {
    				updateStatus(Color.MAGENTA,false,0,-1,0,0,0,1,1,1);
    			}
    			break;
    		case 6:
    			if (!status[x / BLOCK - 1][y / BLOCK + 2]
    					|| !status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]) {
    				updateStatus(Color.MAGENTA,false,1,0,0,0,-1,0,-1,1);
    			}
    			break;
    		case 8:
    			if (!status[x / BLOCK - 1][y / BLOCK]
    					|| !status[x / BLOCK][y / BLOCK + 2]) {
    				updateStatus(Color.MAGENTA,false,-1,-1,0,0,0,-1,0,1);
    			}
    			break;
    		case 9:
    			if (!status[x / BLOCK - 1][y / BLOCK + 1]
    					|| !status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]) {
    				updateStatus(Color.MAGENTA,false,1,0,0,0,-1,0,1,-1);
    			}
    			break;
    		case 10:
    			if (!status[x / BLOCK - 1][y / BLOCK + 1]
    					|| !status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]) {
    				updateStatus(Color.PINK,false,1,0,0,0,-1,0,0,-1);
    			}
    			break;
    		case 11:
    			if (!status[x / BLOCK - 1][y / BLOCK + 1]
    					|| !status[x / BLOCK][y / BLOCK + 2]) {
    				updateStatus(Color.PINK,false,0,1,0,0,-1,0,0,-1);
    			}
    			break;
    		case 12:
    			if (!status[x / BLOCK - 1][y / BLOCK + 1]
    					|| !status[x / BLOCK][y / BLOCK + 2]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]) {
    				updateStatus(Color.PINK,false,1,0,0,0,-1,0,0,1);
    			}
    			break;
    		case 13:
    			if (!status[x / BLOCK][y / BLOCK + 2]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]) {
    				updateStatus(Color.PINK,false,1,0,0,0,0,1,0,-1);
    			}
    			break;
    		case 14:
    			if (!status[x / BLOCK - 1][y / BLOCK + 1]
    					|| !status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK]) {
    				updateStatus(Color.GREEN,false,-1,0,0,0,0,-1,1,-1);
    			}
    			break;
    		case 15:
    			if (!status[x / BLOCK][y / BLOCK + 2]
    					|| !status[x / BLOCK - 1][y / BLOCK + 1]) {
    				updateStatus(Color.GREEN,false,-1,0,0,0,-1,-1,0,1);
    			}
    			break;
    		case 16:
    			if (!status[x / BLOCK][y / BLOCK + 2]
    					|| !status[x / BLOCK - 1][y / BLOCK + 2]) {
    				updateStatus(Color.CYAN,false,0,1,0,0,0,-1,-1,1);
    			}
    			break;
    		case 17:
    			if (!status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK - 1][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 1]) {
    				updateStatus(Color.CYAN,false,-1,0,0,0,1,0,-1,-1);
    			}
    			break;
    		case 18:
    			if (!status[x / BLOCK][y / BLOCK + 2]
    					|| !status[x / BLOCK + 1][y / BLOCK]) {
    				updateStatus(Color.CYAN,false,0,1,0,0,0,-1,1,-1);
    			}
    			break;
    		case 19:
    			if (!status[x / BLOCK][y / BLOCK + 1]
    					|| !status[x / BLOCK - 1][y / BLOCK + 1]
    					|| !status[x / BLOCK + 1][y / BLOCK + 2]) {
    				updateStatus(Color.CYAN,false,-1,0,0,0,1,0,1,1);
    			}
    			break;
    		default:
    			break;
    		}
    	} catch (ArrayIndexOutOfBoundsException e) {
    		e.printStackTrace();
    		y += BLOCK;
    	}
    }
    /**
     * ブロック形色確定、xy軸初期化処理メソッド
     * @param col
     * 				色
     * @param status_
     * 				ステータス
     * @param xnum1
     * 				ブロック1のx軸
     * @param ynum1
     * 				ブロック1のy軸
     * @param xnum2
     * 				ブロック2のx軸
     * @param ynum2
     * 				ブロック2のy軸
     * @param xnum3
     * 				ブロック3のx軸
     * @param ynum3
     * 				ブロック3のy軸
     * @param xnum4
     * 				ブロック4のx軸
     * @param ynum4
     * 				ブロック4のy軸
     */
    public void updateStatus(Color col,boolean status_, int xnum1,int ynum1,int xnum2,int ynum2,int xnum3,int ynum3,int xnum4,int ynum4){
    	status[x / BLOCK +xnum1][y / BLOCK +ynum1] = status_;
    	status[x / BLOCK +xnum2][y / BLOCK +ynum2] = status_;
    	status[x / BLOCK +xnum3][y / BLOCK +ynum3] = status_;
    	status[x / BLOCK +xnum4][y / BLOCK +ynum4] = status_;
    	colorStatus[x / BLOCK +xnum1][y / BLOCK +ynum1] = col;
    	colorStatus[x / BLOCK +xnum2][y / BLOCK +ynum2] = col;
    	colorStatus[x / BLOCK +xnum3][y / BLOCK +ynum3] = col;
    	colorStatus[x / BLOCK +xnum4][y / BLOCK +ynum4] = col;
    	XYReset();
    	patternDecide();
    }
    //ファイルの初期化メソッド
//    public void fileSyokika(){
//    	file = new File("C:\\workspace\\myTet\\src\\myTet\\rank2");
//    	//ファイルが存在しない場合の処理
//    	if(!file.exists()){
//    		file = new File("C:\\tetris_tempfile(deleteOK!)\\tempRank.txt");
//    		if(!file.exists()){
//    			try {
//    				fileCre=true;
//    			    File directory1 = new File("C:", "tetris_tempfile(deleteOK!)");
//    			    File file1 = new File(directory1, "tempRank.txt");
//    			    directory1.mkdir();
//    			    file1.createNewFile();
//    				file=new File(file1.getAbsolutePath());
//    				FileWriter fw = new FileWriter(file);
//    				BufferedWriter bw = new BufferedWriter(fw);
//    				for (int i = 0; i < 3; i++) {
//    					bw.write(0 + "\r\n");
//    				}
//    				bw.close();
//    			} catch (IOException e) {
//    				// TODO 自動生成された catch ブロック
//    				e.printStackTrace();
//    			}
//    		}
//    	}
//    }
    //ランキング配列初期化メソッド
    public void rankSyokika(){
    	try{
    		FileReader fr=new FileReader(file);
    		BufferedReader br=new BufferedReader(fr);
    		String s;
    		int rankIndex=0;
    		while((s=br.readLine())!=null){
    			if(rankIndex==3){
    				break;
    			}
    			try {
					arr.add(Integer.valueOf(s));
				} catch (NumberFormatException e) {
					arr.add(0);
				}
    			rankIndex+=1;
    		}
    		br.close();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	arrSort();
    }
    /**
     * ブロックを一列削除する
     */
    public void deleteBlock(){
    	int judgeCount = 0;
    	for (int i = 0; i < subHeight - 1; i++) {
    		for (int j = 0; j < subWidth; j++) {
    			if (status[j][i]) {
    				judgeCount += 1;
    			}
    		}
    		if (judgeCount == 0) {
    			score += 1;
    			StringScore = "";
    			for (int k = 0; k < subWidth; k++) {
    				status[k][i] = true;
    			}
    			for (int n = 0; n < subWidth; n++) {
    				for (int m = i; m > 0; m--) {
    					status[n][m] = status[n][m - 1];
    					colorStatus[n][m] = colorStatus[n][m - 1];
    				}
    			}
    		}
    		judgeCount = 0;
    	}
    }
    /**
     * スピード変化
     */
    public void speedChange(){
    	if (score >= 40) {
    		if (speed != SPEED_HIGH && speed != SPEED_GOD) {
    			speed = SPEED_GOD;
    		}
    	} else if (score >= 20) {
    		if (speed != SPEED_HIGH && speed != SPEED_EXTRA) {
    			speed = SPEED_EXTRA;
    		}
    	} else if (score >= 10) {
    		if (speed != SPEED_HIGH && speed != SPEED_MIDIUM) {
    			speed = SPEED_MIDIUM;
    		}
    	}
    	//描画速度に影響が出るため停止
//    	for(int i=0;i<LEVEL_LIST.size();i++){
//    		if(score >= LEVEL_LIST.get(i).getLower() && SPEED != LEVEL_LIST.get(i).getSpeed()){
//    			SPEED = LEVEL_LIST.get(i).getSpeed();
//    			break;
//    		}
//    	}
    }
    /**
     * ゲームオーバー判定
     */
    public void gameOver(){
    	for (int i = 0; i < subWidth; i++) {
    		if (!status[i][0]) {
        		end = true;
        		loopJudge = false;
    		}
    	}
    }
    /**
     * ランキング処理
     */
    public void endRank(){
		//配列の値を変更
		arr.add(score);
		arrSort();
		//ファイルの書き直し
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < 3; i++) {
				bw.write(String.valueOf(arr.get(i)) + "\r\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void arrSort(){
    	int temp=0;
    	for(int i=0;i<arr.size();i++){
    		for(int j=i;j<arr.size();j++){
    			if(arr.get(i)<arr.get(j)){
    				temp=arr.get(i);
    				arr.set(i, arr.get(j));
    				arr.set(j,temp);
    			}
    		}
    	}
    }
}
