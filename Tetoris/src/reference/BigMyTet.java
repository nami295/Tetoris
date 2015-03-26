package reference;

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

/*
<applet code="BigMyTet.class" width="400" height="600">
</applet>
*/



public class BigMyTet extends Applet implements Runnable, KeyListener {

	private int block=30;
    private int x=block*4,y=block;
    private int[] blockKinds={1,2,3,5,10,14,16};
    private int LOWSPEED=1000,MIDIUMSPEED=700,EXTRASPEED=400,GODSPEED=150,HIGHSPEED=100;
    private int SPEED;
    private int width=100*3,height=200*3;
    private int subWidth=width/block,subHeight=height/block;
    private	int score,plessEnter,nextAreaX,nextAreaY,stock,stockAreaX,stockAreaY,gameOverJudge;
    private int nextNextPattern,nextPattern,pattern;

    private boolean[][] status=new boolean[subWidth][subHeight];
    private String[][] colorStatus=new String[subWidth][subHeight];

    private	String StringScore;

    private	boolean loopJudge,opLoopJudge,end,op,gameOver;
	private boolean game,gameOverLoop,restartFlag;

	private Thread t;

	File file;

	private Color tempColor;
	private boolean fileCre=false;

	private ArrayList<Integer> arr;


    public void init() {
    	setSize(200*3,270*3);
        addKeyListener(this);
        //requestFocus();
        game=true;
        t=new Thread(this);
		t.start();
		System.out.println("\t\t\tBigMyTet ver1.2 start!");
    }

    public void start() {
        if(t==null) {
            t = new Thread(this);
            t.start();
        }
    }

    public void destroy(){
	}

    public void paint(Graphics g) {
    	requestFocus();
    	if(op==true){
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
                	g.fillRect(j*block+1,i*block+1,block,block);
            	}
            }
	    	g.setColor(Color.white);
	    	g.setFont(new Font(null,Font.BOLD,40));
    		g.drawString("TETRIS",width/2/2, block*4);
	    	if(plessEnter%2==0){
	    		g.setColor(Color.white);
	    		g.setFont(new Font(null,Font.PLAIN,20));
	    		g.drawString("Pless Enter",width/2/2+block/2, height/2+(block*6));
	    	}else{
	    		g.setColor(new Color(5,5,255));
				g.fillRect(width/2/2+block/2, height/2-10+(block*6), 70, 20);
	    	}
	    	//スタート画面デザイン（ランキング表示）
	    	//g.setColor(Color.white);
    		//g.setFont(new Font(null,Font.ITALIC,20));
    		//if(arr.get(0)==null){
    		//	g.drawString("ファイルが読み込めません",block, (height/2-block));
    		//}else{
		    //	for(int i=0;i<3;i++){
		    //		g.drawString(1+i+"位："+String.valueOf(arr.get(i)),width/4+block, (height/2-block)+(i*block));
		    //	}
    		//}
	    	//スタート画面デザイン（右の余白塗りつぶし）
	    	g.setColor(Color.white);
			g.fillRect(width+10, 0, width, height);
			//スタート画面デザイン（新規ランキングファイル作成報告）
			if(fileCre){
	    		g.setColor(Color.red);
	    		g.setFont(new Font(null,Font.PLAIN,15));
				g.drawString("※Cドライブ直下にファイルを作成しました",0, (height/2-block*3));
			}
    	}else if(op==false){
	    	//テトリス画面、ゲームオーバー画面（スコア表示）
	    	g.setColor(new Color(255,250-19*10,255-19*10));
	    	g.setFont(new Font(null,Font.PLAIN,20));	//※スコア表示まで一括設定
			g.drawString("score.",width+20, 40);
			StringScore=""+score;
			g.setColor(Color.white);
			g.fillRect(width+80, 20, 60, block);
			g.setColor(new Color(255,250-19*10,255-19*10));
			g.drawString(StringScore,width+80, 40);
			//テトリス画面、ゲームオーバー画面（レベル表示）
	    	g.setColor(new Color(255,250-19*10,255-19*10));
			g.drawString("Lv.",width+20, 80);
			String Lv="";
			int LvColor=0;
			if(score>=40)	 	{Lv="godfield";	LvColor=5;}
			else if(score>=20)	{Lv="fantastic";LvColor=8;}
			else if(score>=10)	{Lv="expart";	LvColor=10;}
			else				{Lv="normal";	LvColor=19;}
			g.setColor(Color.white);
			g.fillRect(width+50, 60, 80, block);
			g.setColor(new Color(255,250-LvColor*10,255-LvColor*10));
			g.drawString(Lv,width+50, 80);
			//テトリス画面、ゲームオーバー画面（ストックエリア）
			patternArea(g,"stock",stockAreaX,stockAreaY);
        	if(stock!=99){
        		writeBlock(g,stock,stockAreaX,stockAreaY);
        		writeLine(g,stockAreaX,stockAreaY);
        	}
			//テトリス画面、ゲームオーバー画面（ネクストエリア）
        	patternArea(g,"next",nextAreaX,nextAreaY);
        	writeBlock(g,nextPattern,nextAreaX,nextAreaY);
        	writeLine(g,nextAreaX,nextAreaY);
	    	//ゲームオーバー画面（ゲームオーバーアニメーション）
	    	if(end==true && !gameOver){
	    		for(int i=0;i<subHeight-1;i++){
	    			for(int j=0;j<subWidth;j++){
	            		if(!status[j][i]){
	            			g.setColor(new Color(255,250-i*10,255-i*10,200));
	                    	g.fillRect(j*block+1,i*block+1,29,29);
	                    	try {
	                            Thread.sleep(20);
	                        } catch (InterruptedException e) {
	                            e.printStackTrace();
	                        }
	            		}else{
	            			g.setColor(Color.white);
	                    	g.fillRect(j*block+1,i*block+1,29,29);
	                    	try {
	                            Thread.sleep(20);
	                        } catch (InterruptedException e) {
	                            e.printStackTrace();
	                        }
	            		}
	            	}
	            }
	    		g.setColor(new Color(255,250-19*10,255-19*10));
	    		g.setFont(new Font(null,Font.ITALIC,40));
	    		g.drawString("Game Over", width/2/2/2, 100);
	    		g.setFont(new Font(null,Font.ITALIC,20));
	    		g.drawString("pless Enter one more Challenge!!", 2, 150);
	    		System.out.println("game over");
	    		gameOver=true;
	    	}
	    	//テトリス画面（たまったブロックの色設定）
	    	else if(!end){
		        g.setColor(Color.gray);
		    	g.fillRect(0,0,100*3,200*3-30);
		    	for(int i=0;i<subWidth;i++){
		    		for(int j=0;j<subHeight;j++){
		    			if(status[i][j]==false){
		    				String myColor=colorStatus[i][j];
		    				if(myColor.equals("yellow")){
		    					g.setColor(Color.yellow);
		    				}else if(myColor.equals("white")){
		    					g.setColor(Color.white);
		    				}else if(myColor.equals("red")){
		    					g.setColor(Color.red);
		    				}else if(myColor.equals("magenta")){
		    					g.setColor(Color.magenta);
		    				}else if(myColor.equals("cyan")){
		    					g.setColor(Color.cyan);
		    				}else if(myColor.equals("pink")){
		    					g.setColor(Color.pink);
		    				}else if(myColor.equals("green")){
		    					g.setColor(Color.green);
		    				}else{
		    					g.setColor(Color.orange);
		    				}
		    				oneBlock(g,i*block, j*block);
		    				//g.fillRect(i*block, j*block, block, block);
		    			}
		    		}
		    	}
		    	//テトリス画面（ブロック表示）
		        writeBlock(g,pattern,x,y);
		        //テトリス画面（罫線表示）
		        g.setColor(Color.white);
		        for(int i=0;i<subWidth+1;i++){
		        	g.drawLine(i*block, 0, i*block, height);
		        }
		        for(int i=0;i<subHeight;i++){
		        	g.drawLine(0, i*block, width, i*block);
		        }
	    	}
    	}
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void run() {
		while (game) {
			syokika();				//初期化
			while (opLoopJudge) {	//opループ
				speedAndRepaint();	//処理停止間隔と再描画
				plessEnter += 1;
			}
			while (loopJudge) {		//テトリスループ処理
				speedAndRepaint();	//処理停止間隔と再描画
				kakutei();			//ブロックの形色確定処理
				deleteBlock();		//ブロックが消える判定
				speedChange();		//スピード変化
				y += block;			//ブロックを一マス進める
				gameOver();			//ゲームオーバー判定
			}
			//endRank();			//ゲーム終了時のランキング処理
			repaint();			//ゲームオーバー画面描画
			MyTetRestart();		//リスタート処理
		}

    }


    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode()==KeyEvent.VK_LEFT) {
        	try {
				if(pattern==0){
				    if(x>=block && status[x/block-1][y/block+1] && status[x/block-1][y/block] && status[x/block-1][y/block-1] && status[x/block-1][y/block-2]) {
				        x-=block;
				    }
				}
				else if(pattern==1) {
					if(x>=block*2 && status[x/block-2][y/block]) {
						x-=block;
					}
				}
				else if(pattern==2){
					if(x>=block && status[x/block-1][y/block] && status[x/block-1][y/block-1]) {
						x-=block;
					}
				}
				else if(pattern==3){
					if(x>=block*2 && status[x/block-1][y/block] && status[x/block-2][y/block-1]) {
						x-=block;
					}
				}
				else if(pattern==4){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-2][y/block+1] && status[x/block-1][y/block-1]) {
						x-=block;
					}
				}
				else if(pattern==5){
					if(x>=block && status[x/block-1][y/block] && status[x/block-1][y/block+1] && status[x/block-1][y/block-1]) {
						x-=block;
					}
				}
				else if(pattern==6){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-2][y/block+1] ) {
						x-=block;
					}
				}
				else if(pattern==7){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-2][y/block+1] && status[x/block-2][y/block-1] ) {
						x-=block;
					}
				}
				else if(pattern==8){
					if(x>=block*2 && status[x/block-1][y/block] && status[x/block-1][y/block+1] && status[x/block-2][y/block-1]) {
						x-=block;
					}
				}
				else if(pattern==9){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block][y/block-1] ) {
						x-=block;
					}
				}
				else if(pattern==10){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-1][y/block-1] ) {
						x-=block;
					}
				}
				else if(pattern==11){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-1][y/block-1] && status[x/block-1][y/block+1] ) {
						x-=block;
					}
				}
				else if(pattern==12){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-1][y/block+1] ) {
						x-=block;
					}
				}
				else if(pattern==13){
					if(x>=block   && status[x/block-1][y/block] && status[x/block-1][y/block-1] && status[x/block-1][y/block+1]) {
						x-=block;
					}
				}

				else if(pattern==14){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-1][y/block-1] ) {
						x-=block;
					}
				}
				else if(pattern==15){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-2][y/block-1] && status[x/block-1][y/block+1]) {
						x-=block;
					}
				}
				else if(pattern==16){
					if(x>=block*2 && status[x/block-1][y/block] && status[x/block-1][y/block-1] && status[x/block-2][y/block+1]) {
						x-=block;
					}
				}
				else if(pattern==17){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block-2][y/block-1] ) {
						x-=block;
					}
				}
				else if(pattern==18){
					if(x>=block*1 && status[x/block-1][y/block] && status[x/block-1][y/block-1] && status[x/block-1][y/block+1]) {
						x-=block;
					}
				}
				else if(pattern==19){
					if(x>=block*2 && status[x/block-2][y/block] && status[x/block][y/block+1] ) {
						x-=block;
					}
				}
			} catch (Exception e1) {
				x=width/2;
				y=block;
				System.out.println("左にはみ出すエラー");
			}
        }

        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
        	try {
				if(pattern==0){
					if(x<width-block && status[x/block+1][y/block+1] && status[x/block+1][y/block] && status[x/block+1][y/block-1] && status[x/block+1][y/block-2]){
					    x+=block;
					}
				}
				else if(pattern==1) {
					if(x<width-block*3 && status[x/block+3][y/block]) {
						x+=block;
					}
				}
				else if(pattern==2){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+2][y/block-1]) {
						x+=block;
					}
				}
				else if(pattern==3){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+1][y/block-1]) {
						x+=block;
					}
				}
				else if(pattern==4){
					if(x<width-block && status[x/block+1][y/block] && status[x/block+1][y/block-1] && status[x/block][y/block+1]) {
						x+=block;
					}
				}
				else if(pattern==5){
					if(x<width-block*2 && status[x/block+1][y/block] && status[x/block+1][y/block-1] && status[x/block+2][y/block+1]) {
						x+=block;
					}
				}
				else if(pattern==6){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block][y/block+1] ) {
						x+=block;
					}
				}
				else if(pattern==7){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+2][y/block+1] && status[x/block+2][y/block-1] ) {
						x+=block;
					}
				}
				else if(pattern==8){
					if(x<width-block && status[x/block+1][y/block] && status[x/block+1][y/block-1] && status[x/block+1][y/block+1]) {
						x+=block;
					}
				}
				else if(pattern==9){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+2][y/block-1] ) {
						x+=block;
					}
				}
				else if(pattern==10){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+1][y/block-1] ) {
						x+=block;
					}
				}
				else if(pattern==11){
					if(x<width-block   && status[x/block+1][y/block] && status[x/block+1][y/block-1] && status[x/block+1][y/block+1] ) {
						x+=block;
					}
				}
				else if(pattern==12){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+1][y/block+1] ) {
						x+=block;
					}
				}
				else if(pattern==13){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+1][y/block-1] && status[x/block+1][y/block+1] ) {
						x+=block;
					}
				}

				else if(pattern==14){
					if(x<width-block*2 && status[x/block+1][y/block] && status[x/block+2][y/block-1] ) {
						x+=block;
					}
				}
				else if(pattern==15){
					if(x<width-block   && status[x/block+1][y/block] && status[x/block+1][y/block+1] && status[x/block][y/block-1] ) {
						x+=block;
					}
				}
				else if(pattern==16){
					if(x<width-block   && status[x/block+1][y/block] && status[x/block+1][y/block+1] && status[x/block+1][y/block-1] ) {
						x+=block;
					}
				}
				else if(pattern==17){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block][y/block-1]  ) {
						x+=block;
					}
				}
				else if(pattern==18){
					if(x<width-block*2 && status[x/block+1][y/block] && status[x/block+1][y/block+1] && status[x/block+2][y/block-1] ) {
						x+=block;
					}
				}
				else if(pattern==19){
					if(x<width-block*2 && status[x/block+2][y/block] && status[x/block+2][y/block+1] ) {
						x+=block;
					}
				}
			}catch (Exception e1) {
				x=width/2;
				y=block;
				System.out.println("右にはみ出すエラー");
			}
        }

        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            SPEED = HIGHSPEED;
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (restartFlag) {
            	gameOverLoop=false;
            }else{
				if (op == true) {
					op = false;
				}
				if (opLoopJudge == true) {
					opLoopJudge = false;
				}
			}
        }

        //ストック処理
        if(e.getKeyCode() == KeyEvent.VK_P) {
        	if (loopJudge && x >= block * 2
						&& x <= width - block * 3 && y <= height - block * 2
						&& y >= block * 2 && status[x / block][y / block]
						&& status[x / block + 1][y / block - 1]
						&& status[x / block][y / block - 1]
						&& status[x / block - 1][y / block - 1]
						&& status[x / block + 1][y / block + 1]
						&& status[x / block][y / block + 1]
						&& status[x / block - 1][y / block + 1]
						&& status[x / block - 1][y / block]
						&& status[x / block + 1][y / block]
						&& status[x / block][y / block - 2]
						&& status[x / block + 2][y / block]) {
				//ストック処理（ストックが存在しない場合）
				if (stock == 99) {
					if (pattern == 0 || pattern == 1) {
						stock = blockKinds[0];
					} else if (pattern == 2) {
						stock = blockKinds[1];
					} else if (pattern == 3 || pattern == 4) {
						stock = blockKinds[2];
					} else if (pattern == 5 || pattern == 6 || pattern == 8
							|| pattern == 9) {
						stock = blockKinds[3];
					} else if (pattern == 10 || pattern == 11 || pattern == 12
							|| pattern == 13) {
						stock = blockKinds[4];
					} else if (pattern == 14 || pattern == 15) {
						stock = blockKinds[5];
					} else if (pattern == 16 || pattern == 17 || pattern == 18
							|| pattern == 19) {
						stock = blockKinds[6];
					}
					Ran();
				}
				//ストック処理（ストックが存在する場合）
				else if (stock != 99) {
					nextNextPattern = nextPattern;
					if (pattern == 0 || pattern == 1) {
						nextPattern = blockKinds[0];
					} else if (pattern == 2) {
						nextPattern = blockKinds[1];
					} else if (pattern == 3 || pattern == 4) {
						nextPattern = blockKinds[2];
					} else if (pattern == 5 || pattern == 6 || pattern == 8
							|| pattern == 9) {
						nextPattern = blockKinds[3];
					} else if (pattern == 10 || pattern == 11 || pattern == 12
							|| pattern == 13) {
						nextPattern = blockKinds[4];
					} else if (pattern == 14 || pattern == 15) {
						nextPattern = blockKinds[5];
					} else if (pattern == 16 || pattern == 17 || pattern == 18
							|| pattern == 19) {
						nextPattern = blockKinds[6];
					}
					pattern = stock;
					stock = 99;
				}
			}
        }

        //開発用コマンド
        if(e.getKeyCode() == KeyEvent.VK_N) {
            score+=1;
        }
        if(e.getKeyCode() == KeyEvent.VK_Q) {
            try {
				if(status[x/block][y/block-2]==true){y-=block;}
			} catch (ArrayIndexOutOfBoundsException e1) {
				y+=0;
			}
        }

    }

    public void keyReleased(KeyEvent e) {

    	if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            SPEED = LOWSPEED;
        }

    	if(e.getKeyCode() == KeyEvent.VK_SPACE) {
    		 try {
    			 if(pattern==0){
 					try {
						if (x >= block && x <= width - block * 3
								&& status[x / block - 1][y / block]
								&& status[x / block - 1][y / block + 1]
								&& status[x / block - 1][y / block - 1]
								&& status[x / block - 1][y / block - 2]
								&& status[x / block + 1][y / block]
								&& status[x / block + 1][y / block - 1]
								&& status[x / block + 1][y / block - 2]
								&& status[x / block + 1][y / block + 1]
								&& status[x / block + 2][y / block]
								&& status[x / block + 2][y / block - 1]
								&& status[x / block + 2][y / block - 2]
								&& status[x / block + 2][y / block + 1]) {
							pattern = 1;
						}
 					} catch (ArrayIndexOutOfBoundsException e1) {
 						y+=block;
 						System.out.println("対処済みエラー");
 					}
 				}
 				else if(pattern==1){
 					 if(y<=height-block*4){
 			             if(!status[x/block][y/block+2]){
 			            	 y-=block;
 			             }
 			             pattern = 0;
 					 }
 				}
 				else if(pattern==2){
					 pattern=2;
				}
				else if(pattern==3){
					 if(!status[x/block-1][y/block+1]){
						 y-=block;
					 }
					 if(status[x/block][y/block+1] && status[x/block-1][y/block+1] && status[x/block+1][y/block+1] ){
				         pattern = 4;
					 }
				}
				else if(pattern==4){
					 if(x<=width-block*2 && status[x/block+1][y/block]){
			             pattern = 3;
					 }
					 else if(x==width-block && status[x/block-1][y/block-1] && status[x/block-2][y/block-1] ){
						 x-=block;
			             pattern = 3;
					 }
				}
				else if(pattern==5){
					 if(x>=block && status[x/block-1][y/block] && status[x/block-1][y/block+1] && status[x/block-1][y/block-1]){
						 pattern=6;
					 }else if(x==0 && status[x/block+2][y/block] && status[x/block+2][y/block+1] && status[x/block+2][y/block-1]){
						 x+=block;
						 pattern=6;
					 }
				}
				else if(pattern==6){
					 pattern=8;
				}
				else if(pattern==8){
					 if(x<width-block*1){
						 if(!status[x/block-1][y/block+1]){
							 y-=block;
						 }
						 pattern=9;
					 }else if(x==width-block && status[x/block-2][y/block] && status[x/block-1][y/block] ){
						 x-=block;
			             pattern = 9;
					 }
				}
				else if(pattern==9){
					 if(y<=height-block*2 && status[x/block-1][y/block+1] && status[x/block][y/block+1] && status[x/block+1][y/block+1]){
						 pattern=5;
						 if(!status[x/block][y/block+2]){
							 y-=block;
						 }
					 }
				}
				else if(pattern==10){
					if(y<=height-block*3){
						 if(!status[x/block][y/block+2]){
							 y-=block;
						 }
						 pattern=11;
					}
				}
				else if(pattern==11){
					 if(x<width-block && status[x/block+1][y/block] && status[x/block+1][y/block+1] && status[x/block+1][y/block-1]){
						 pattern=12;
					 }else if(x==width-block && status[x/block-2][y/block] && status[x/block-1][y/block+1] ){
						 x-=block;
			             pattern = 12;
					 }
				}
				else if(pattern==12){
					 pattern=13;
				}
				else if(pattern==13){
					 if(x>=block && status[x/block-1][y/block] && status[x/block-1][y/block+1] ){
						 pattern=10;
					 }else if(x==0 && status[x/block+1][y/block-1] && status[x/block+2][y/block] ){
						 x+=block;
						 pattern=10;
					 }
				}
				else if(pattern==14){
					 if (status[x/block][y/block+1] && status[x/block-1][y/block+1] && status[x/block+1][y/block+1] ) {
						 if(!status[x/block][y/block+1]){
							 y-=block;
						 }
						pattern = 15;
					}
				}
				else if(pattern==15){
					 if(x<=width-block*2 && status[x/block+1][y/block] && status[x/block+1][y/block-1] && status[x/block+1][y/block+1]){
						 pattern=14;
					 }else if(x==width-block && status[x/block-2][y/block] && status[x/block][y/block-1] ){
						 x-=block;
			             pattern = 14;
					 }
				}
				else if(pattern==16 ){
					 if (x<=width-block*2 && status[x/block+1][y/block] && status[x/block+1][y/block-1] && status[x/block+1][y/block+1]) {
						pattern = 17;
					 }else if(x==width-block && status[x/block-2][y/block-1] && status[x/block-2][y/block] && status[x/block-1][y/block]){
						 x-=block;
			             pattern = 17;
					 }
				}
				else if(pattern==17 ){
					 pattern=18;
				}
				else if(pattern==18 ){
					 if (x>=block && status[x/block-1][y/block] && status[x/block-1][y/block-1] && status[x/block-1][y/block+1]) {
						pattern = 19;
					}else if(x==0 && status[x/block+1][y/block] && status[x/block+2][y/block] && status[x/block+2][y/block+1]){
						 x+=block;
						 pattern=19;
					 }
				}
				else if(pattern==19 ){
					 pattern=16;
				}
				if (y>block && loopJudge) {
					repaint();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("スペースキーエラー");
				y+=block;
			}

         }

    }

    public void keyTyped(KeyEvent e) {
    }
    //初期位置メソッド
    public void first(){
        x=block*4;
        y=0;
    }
    //パターン決定メソッド
    public void Ran(){
        pattern=nextPattern;
        nextPattern=nextNextPattern;
    	nextNextPattern=blockKinds[(int)(Math.random()*blockKinds.length)];
    }
    //next,stock表示エリア表示メソッド
    public void patternArea(Graphics g,String name,int x,int y){
    	g.setColor(Color.lightGray);
    	g.drawString(name,x-block*2-2,y-block*2-3);
    	g.fillRect(x-block*2-2,y-block*2-2,block*6+4,block*5+4);
    	g.setColor(Color.white);
    	g.fillRect(x-block*2,y-block*2,block*6,block*5);
    }
    //ブロック描画メソッド
    public void writeBlock(Graphics g,int number,int x,int y){
    	if(number==0){
        	g.setColor(Color.orange);
        	blockShape(g,x,y+block,x,y,x,y-block,x,y-block*2);
        }
        else if(number==1){
        	g.setColor(Color.orange);
        	blockShape(g,x-block,y,x,y,x+block,y,x+block*2,y);
        }
        else if(number==2){
        	g.setColor(Color.yellow);
        	blockShape(g,x,y,x+block,y,x,y-block,x+block,y-block);
        }
        else if(number==3){
        	g.setColor(Color.red);
        	blockShape(g,x-block,y-block,x,y-block,x,y,x+block,y);
        }
        else if(number==4){
        	g.setColor(Color.red);
        	blockShape(g,x,y-block,x,y,x-block,y,x-block,y+block);
        }
        else if(number==5){
        	g.setColor(Color.magenta);
        	blockShape(g,x,y-block,x,y,x,y+block,x+block,y+block);
        }
        else if(number==6){
        	g.setColor(Color.magenta);
        	blockShape(g,x+block,y,x,y,x-block,y,x-block,y+block);
        }
        else if(number==7){
        	g.setColor(Color.cyan);
        	g.fillRect(x+block,y,block,block);
            g.fillRect(x,y,block,block);
            g.fillRect(x+block,y+block,block,block);
            g.fillRect(x,y+block,block,block);
            g.fillRect(x-block,y+block,block,block);
            g.fillRect(x-block,y,block,block);
            g.fillRect(x-block,y-block,block,block);
            g.fillRect(x,y-block,block,block);
            g.fillRect(x+block,y-block,block,block);
        }
        else if(number==8){
        	g.setColor(Color.magenta);
        	blockShape(g,x,y-block,x,y,x-block,y-block,x,y+block);
        }
        else if(number==9){
        	g.setColor(Color.magenta);
        	blockShape(g,x+block,y,x,y,x-block,y,x+block,y-block);
        }
        else if(number==10){
        	g.setColor(Color.pink);
        	blockShape(g,x+block,y,x,y,x-block,y,x,y-block);
        }
        else if(number==11){
        	g.setColor(Color.pink);
        	blockShape(g,x,y+block,x,y,x-block,y,x,y-block);
        }
        else if(number==12){
        	g.setColor(Color.pink);
        	blockShape(g,x+block,y,x,y,x-block,y,x,y+block);
        }
        else if(number==13){
        	g.setColor(Color.pink);
        	blockShape(g,x+block,y,x,y,x,y+block,x,y-block);
        }
        else if(number==14){
        	g.setColor(Color.green);
        	blockShape(g,x-block,y,x,y,x,y-block,x+block,y-block);
        }
        else if(number==15){
        	g.setColor(Color.green);
        	blockShape(g,x-block,y,x,y,x-block,y-block,x,y+block);
        }
        else if(number==16){
        	g.setColor(Color.cyan);
        	blockShape(g,x,y-block,x,y,x,y+block,x-block,y+block);
        }
        else if(number==17){
        	g.setColor(Color.cyan);
        	blockShape(g,x-block,y,x,y,x+block,y,x-block,y-block);
        }
        else if(number==18){
        	g.setColor(Color.cyan);
        	blockShape(g,x,y-block,x,y,x,y+block,x+block,y-block);
        }
        else if(number==19){
        	g.setColor(Color.cyan);
        	blockShape(g,x-block,y,x,y,x+block,y,x+block,y+block);
        }
    }
    //next,stock表示エリアの罫線表示メソッド
    public void writeLine(Graphics g,int x,int y){
    	g.setColor(Color.white);
        for(int i=0;i<4;i++){
        	g.drawLine(x-block, y-block+(block*i), x+block*3, y-block+(block*i));
        }
        for(int i=0;i<5;i++){
        	g.drawLine(x-block+(block*i), y-block, x-block+(block*i), y+block*2);
        }
    }
    //ブロックの形メソッド
    public void blockShape(Graphics g,int x1,int y1,int x2,int y2,int x3,int y3,int x4,int y4){
    	oneBlock(g,x1,y1);
    	oneBlock(g,x2,y2);
    	oneBlock(g,x3,y3);
    	oneBlock(g,x4,y4);
    }
    //１ブロック表示メソッド
    public void oneBlock(Graphics g,int x,int y){
    	tempColor=g.getColor();
    	g.fillRect(x,y,block,block);
    	g.setColor(Color.white);
    	g.fillRect(x+block/5, y+block/4, block/7, block/7);
    	g.fillRect(x+block/5*2, y+block/4, block/2, block/7);
    	g.setColor(tempColor);
    }
    //ブロック確定メソッド
    public void kakutei(){
    	try {
    		if (pattern == 0) {
    			if (!status[x / block][y / block + 2]) {
    				falseAndCol("orange",0,1,0,0,0,-1,0,-2);//pattern==0
    			}
    		} else if (pattern == 1) {
    			if (!status[x / block + 2][y / block + 1]
    					|| !status[x / block + 1][y / block + 1]
    					|| !status[x / block][y / block + 1]
    					|| !status[x / block - 1][y / block + 1]) {
    				falseAndCol("orange",2,0,1,0,0,0,-1,0);//pattern==1
    			}
    		} else if (pattern == 2) {
    			if (!status[x / block][y / block + 1]
    					|| !status[x / block + 1][y / block + 1]) {
    				falseAndCol("yellow",0,0,1,0,0,-1,1,-1);//pattern==2
    			}
    		} else if (pattern == 3) {
    			if (!status[x / block][y / block + 1]
    					|| !status[x / block + 1][y / block + 1]
    					|| !status[x / block - 1][y / block]) {
    				falseAndCol("red",-1,-1,0,-1,0,0,1,0);//pattern==3
    			}
    		} else if (pattern == 4) {
    			if (!status[x / block][y / block + 1]
    					|| !status[x / block - 1][y / block + 2]) {
    				falseAndCol("red",0,-1,0,0,-1,0,-1,1);//pattern==4
    			}
    		} else if (pattern == 5) {
    			if (!status[x / block][y / block + 2]
    					|| !status[x / block + 1][y / block + 2]) {
    				falseAndCol("magenta",0,-1,0,0,0,1,1,1);//pattern==5
    			}
    		} else if (pattern == 6) {
    			if (!status[x / block - 1][y / block + 2]
    					|| !status[x / block][y / block + 1]
    					|| !status[x / block + 1][y / block + 1]) {
    				falseAndCol("magenta",1,0,0,0,-1,0,-1,1);//pattern==6
    			}
    		} else if (pattern == 7) {
    			if (!status[x / block - 1][y / block + 2]
    					|| !status[x / block][y / block + 2]
    					|| !status[x / block + 1][y / block + 2]) {
    				status[x / block + 1][y / block] = false;
    				status[x / block][y / block] = false;
    				status[x / block + 1][y / block + 1] = false;
    				status[x / block][y / block + 1] = false;
    				status[x / block - 1][y / block + 1] = false;
    				status[x / block - 1][y / block] = false;
    				status[x / block - 1][y / block - 1] = false;
    				status[x / block][y / block - 1] = false;
    				status[x / block + 1][y / block - 1] = false;
    				colorStatus[x / block + 1][y / block] = "cyan";
    				colorStatus[x / block][y / block] = "cyan";
    				colorStatus[x / block + 1][y / block + 1] = "cyan";
    				colorStatus[x / block][y / block + 1] = "cyan";
    				colorStatus[x / block - 1][y / block + 1] = "cyan";
    				colorStatus[x / block - 1][y / block] = "cyan";
    				colorStatus[x / block - 1][y / block - 1] = "cyan";
    				colorStatus[x / block][y / block - 1] = "cyan";
    				colorStatus[x / block + 1][y / block - 1] = "cyan";
    				first();
    				Ran();
    			}
    		} else if (pattern == 8) {
    			if (!status[x / block - 1][y / block]
    					|| !status[x / block][y / block + 2]) {
    				falseAndCol("magenta",-1,-1,0,0,0,-1,0,1);//pattern==8
    			}
    		} else if (pattern == 9) {
    			if (!status[x / block - 1][y / block + 1]
    					|| !status[x / block][y / block + 1]
    					|| !status[x / block + 1][y / block + 1]) {
    				falseAndCol("magenta",1,0,0,0,-1,0,1,-1);//pattern==9
    			}
    		} else if (pattern == 10) {
    			if (!status[x / block - 1][y / block + 1]
    					|| !status[x / block][y / block + 1]
    					|| !status[x / block + 1][y / block + 1]) {
    				falseAndCol("pink",1,0,0,0,-1,0,0,-1);//pattern==10
    			}
    		} else if (pattern == 11) {
    			if (!status[x / block - 1][y / block + 1]
    					|| !status[x / block][y / block + 2]) {
    				falseAndCol("pink",0,1,0,0,-1,0,0,-1);//pattern==11
    			}
    		} else if (pattern == 12) {
    			if (!status[x / block - 1][y / block + 1]
    					|| !status[x / block][y / block + 2]
    					|| !status[x / block + 1][y / block + 1]) {
    				falseAndCol("pink",1,0,0,0,-1,0,0,1);//pattern==12
    			}
    		} else if (pattern == 13) {
    			if (!status[x / block][y / block + 2]
    					|| !status[x / block + 1][y / block + 1]) {
    				falseAndCol("pink",1,0,0,0,0,1,0,-1);//pattern==13
    			}
    		} else if (pattern == 14) {
    			if (!status[x / block - 1][y / block + 1]
    					|| !status[x / block][y / block + 1]
    					|| !status[x / block + 1][y / block]) {
    				falseAndCol("green",-1,0,0,0,0,-1,1,-1);//pattern==14
    			}
    		} else if (pattern == 15) {
    			if (!status[x / block][y / block + 2]
    					|| !status[x / block - 1][y / block + 1]) {
    				falseAndCol("green",-1,0,0,0,-1,-1,0,1);//pattern==15
    			}
    		} else if (pattern == 16) {
    			if (!status[x / block][y / block + 2]
    					|| !status[x / block - 1][y / block + 2]) {
    				falseAndCol("cyan",0,1,0,0,0,-1,-1,1);//pattern==16
    			}
    		} else if (pattern == 17) {
    			if (!status[x / block][y / block + 1]
    					|| !status[x / block - 1][y / block + 1]
    					|| !status[x / block + 1][y / block + 1]) {
    				falseAndCol("cyan",-1,0,0,0,1,0,-1,-1);//pattern==17
    			}
    		} else if (pattern == 18) {
    			if (!status[x / block][y / block + 2]
    					|| !status[x / block + 1][y / block]) {
    				falseAndCol("cyan",0,1,0,0,0,-1,1,-1);//pattern==18
    			}
    		} else if (pattern == 19) {
    			if (!status[x / block][y / block + 1]
    					|| !status[x / block - 1][y / block + 1]
    					|| !status[x / block + 1][y / block + 2]) {
    				falseAndCol("cyan",-1,0,0,0,1,0,1,1);
    			}
    		}
    	} catch (ArrayIndexOutOfBoundsException e) {
    		//e.printStackTrace();
    		y += block;
    		System.out.println("ブロック確定の際のエラー（対処済）");
    	}
    }
    //ブロック形色確定、xy軸初期化処理メソッド
    public void falseAndCol(String col,int xnum1,int ynum1,int xnum2,int ynum2,int xnum3,int ynum3,int xnum4,int ynum4){
    	status[x / block +xnum1][y / block +ynum1] = false;
    	status[x / block +xnum2][y / block +ynum2] = false;
    	status[x / block +xnum3][y / block +ynum3] = false;
    	status[x / block +xnum4][y / block +ynum4] = false;
    	colorStatus[x / block +xnum1][y / block +ynum1] = col;
    	colorStatus[x / block +xnum2][y / block +ynum2] = col;
    	colorStatus[x / block +xnum3][y / block +ynum3] = col;
    	colorStatus[x / block +xnum4][y / block +ynum4] = col;
    	first();
    	Ran();
    }
    //初期化メソッド
    public void syokika(){
    	SPEED=LOWSPEED;
    	loopJudge=true;
    	opLoopJudge=true;
    	end=false;
    	score=0;
    	StringScore="";
    	op=true;
    	plessEnter=0;
    	nextAreaX=width/4*5;
    	nextAreaY=height/4*3;
    	gameOver=false;
    	stock=99;
    	stockAreaX=width/4*5;
    	stockAreaY=height/4*2-block;
    	gameOverJudge=0;
    	gameOverLoop=true;
    	nextNextPattern=blockKinds[(int)(Math.random()*blockKinds.length)];
    	nextPattern=blockKinds[(int)(Math.random()*blockKinds.length)];
    	pattern=blockKinds[(int)(Math.random()*blockKinds.length)];
    	restartFlag=false;
    	fileCre=false;
    	//fileSyokika();
    	masuSyokika();
    	//arr=new ArrayList<Integer>();
    	//rankSyokika();
    }
    //ファイルの初期化メソッド
    public void fileSyokika(){
    	file = new File("C:\\workspace\\myTet\\src\\myTet\\rank2");
    	//ファイルが存在しない場合の処理
    	if(!file.exists()){
    		file = new File("C:\\tetris_tempfile(deleteOK!)\\tempRank.txt");
    		if(!file.exists()){
    			try {
    				fileCre=true;
    			    File directory1 = new File("C:", "tetris_tempfile(deleteOK!)");
    			    File file1 = new File(directory1, "tempRank.txt");
    			    directory1.mkdir();
    			    file1.createNewFile();
    				file=new File(file1.getAbsolutePath());
    				FileWriter fw = new FileWriter(file);
    				BufferedWriter bw = new BufferedWriter(fw);
    				for (int i = 0; i < 3; i++) {
    					bw.write(0 + "\r\n");
    				}
    				bw.close();
    			} catch (IOException e) {
    				// TODO 自動生成された catch ブロック
    				e.printStackTrace();
    			}
    		}
    	}
    }
    //マスのパラメータ初期化メソッド
    public void masuSyokika(){
    	for(int i=0;i<subWidth;i++){
    		for(int j=0;j<subHeight;j++){
    			if(j==19){
    				status[i][j]=false;
    			}else{
    				status[i][j]=true;
    			}
    			if(j==19){
    				colorStatus[i][j]="white";
    			}else{
    				colorStatus[i][j]="orange";
    			}
    		}
    	}
    }
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
    		System.out.println(e);
    	}
    	arrSort();
    }
    //横一列そろったらブロックを消すメソッド
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
    //スピード変化メソッド
    public void speedChange(){
    	if (score >= 40) {
    		if (SPEED != HIGHSPEED && SPEED != GODSPEED) {
    			SPEED = GODSPEED;
    		}
    	} else if (score >= 20) {
    		if (SPEED != HIGHSPEED && SPEED != EXTRASPEED) {
    			SPEED = EXTRASPEED;
    		}
    	} else if (score >= 10) {
    		if (SPEED != HIGHSPEED && SPEED != MIDIUMSPEED) {
    			SPEED = MIDIUMSPEED;
    		}
    	}
    }
    //ゲームオーバー判定メソッド
    public void gameOver(){
    	for (int i = 0; i < subWidth; i++) {
    		if (!status[i][0]) {
    			gameOverJudge += 1;
    		}
    	}
    	if (gameOverJudge >= 1) {
    		end = true;
    		loopJudge = false;
    	}
    }
    //ランキング処理
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
			System.out.println(e);
		}
    }
    //リスタート処理メソッド
    public void MyTetRestart(){
    	//リスタート待受けの待ち
    	try {
    		Thread.sleep(subWidth*subHeight*20);
    	} catch (InterruptedException e) {
    		// TODO 自動生成された catch ブロック
    		e.printStackTrace();
    	}
    	restartFlag=true;
    	//リスタート待受け
    	while (gameOverLoop) {
    		try {
    			Thread.sleep(SPEED);
    		} catch (InterruptedException e) {
    			// TODO 自動生成された catch ブロック
    			e.printStackTrace();
    		}
    	}
    }
    //処理停止間隔と再描画
    public void speedAndRepaint(){
    	try {
    		Thread.sleep(SPEED);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    	repaint();
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
