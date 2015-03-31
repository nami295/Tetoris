//参照元　http://www.wacharo.net/Tetris/
package pkg5;

import java.awt.Graphics;

public class Block {
    public static void polygon(int xx,int yy,int block,Graphics offG,int pattern,int ran,boolean[][] Status,int[][] colorStatus) {
    	int shadowYY = yy;
        for(;Seigyo.downCheck(xx, shadowYY, block, Status, colorStatus, pattern, ran,false) == false;shadowYY += block){

        }
    	_polygon(xx, shadowYY, block, offG, pattern, ran,true);
    	_polygon(xx, yy, block, offG, pattern, ran,false);
    }
    private static void _polygon(int xx,int yy,int block,Graphics offG,int pattern,int ran,boolean shadow) {
        switch(ran) {
            case 0:
                if(pattern==0) {
                    if(shadow)offG.setColor(Clr.shadow);
                    else offG.setColor(Clr.clr[ran]);
                    offG.fillRect(xx,yy+block,block,block);
                    offG.fillRect(xx,yy,block,block);
                    offG.fillRect(xx,yy-block,block,block);
                    offG.fillRect(xx,yy-block*2,block,block);

                    offG.setColor(Clr.clr[7]);
                    offG.drawRect(xx,yy+block,block,block);
                    offG.drawRect(xx,yy,block,block);
                    offG.drawRect(xx,yy-block,block,block);
                    offG.drawRect(xx,yy-block*2,block,block);
                }

                if(pattern==1) {
                    if(shadow)offG.setColor(Clr.shadow);
                    else offG.setColor(Clr.clr[ran]);
                    offG.fillRect(xx-block,yy,block,block);
                    offG.fillRect(xx,yy,block,block);
                    offG.fillRect(xx+block,yy,block,block);
                    offG.fillRect(xx+block*2,yy,block,block);

                    offG.setColor(Clr.clr[7]);
                    offG.drawRect(xx-block,yy,block,block);
                    offG.drawRect(xx,yy,block,block);
                    offG.drawRect(xx+block,yy,block,block);
                    offG.drawRect(xx+block*2,yy,block,block);
                }

                if(pattern==2) {
                    if(shadow)offG.setColor(Clr.shadow);
                    else offG.setColor(Clr.clr[ran]);
                    offG.fillRect(xx,yy-block,block,block);
                    offG.fillRect(xx,yy,block,block);
                    offG.fillRect(xx,yy+block,block,block);
                    offG.fillRect(xx,yy+block*2,block,block);

                    offG.setColor(Clr.clr[7]);
                    offG.drawRect(xx,yy-block,block,block);
                    offG.drawRect(xx,yy,block,block);
                    offG.drawRect(xx,yy+block,block,block);
                    offG.drawRect(xx,yy+block*2,block,block);
                }

                if(pattern==3) {
                    if(shadow)offG.setColor(Clr.shadow);
                    else offG.setColor(Clr.clr[ran]);
                    offG.fillRect(xx+block,yy,block,block);
                    offG.fillRect(xx,yy,block,block);
                    offG.fillRect(xx-block,yy,block,block);
                    offG.fillRect(xx-block*2,yy,block,block);

                    offG.setColor(Clr.clr[7]);
                    offG.drawRect(xx+block,yy,block,block);
                    offG.drawRect(xx,yy,block,block);
                    offG.drawRect(xx-block,yy,block,block);
                    offG.drawRect(xx-block*2,yy,block,block);
                }
                break;
        }
    }
}
