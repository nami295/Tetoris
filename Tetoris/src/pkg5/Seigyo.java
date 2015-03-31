//転載元　http://www.wacharo.net/Tetris/
package pkg5;

public class Seigyo {
    private static boolean bool;

    public static boolean downCheck(int xx,int yy,int block,boolean[][] Status,int[][] colorStatus,int pattern,int ran) {

        bool=false;

        switch(ran) {
            case 0:
                if(pattern==0) {
                    if(Status[xx/block][yy/block+2]==false) {
                        Status[xx/block][yy/block+1]=false;
                        Status[xx/block][yy/block]=false;
                        Status[xx/block][yy/block-1]=false;
                        Status[xx/block][yy/block-2]=false;
                        bool=true;
                    }
                }

                if(pattern==1) {
                    if(!Status[xx/block+2][yy/block+1] || !Status[xx/block+1][yy/block+1] || !Status[xx/block][yy/block+1] || !Status[xx/block-1][yy/block+1]) {
                        Status[xx/block+2][yy/block]=false;
                        Status[xx/block+1][yy/block]=false;
                        Status[xx/block][yy/block]=false;
                        Status[xx/block-1][yy/block]=false;
                        bool=true;
                    }
                }

                if(pattern==2) {
                    if(Status[xx/block][yy/block+3]==false) {
                        Status[xx/block][yy/block-1]=false;
                        Status[xx/block][yy/block]=false;
                        Status[xx/block][yy/block+1]=false;
                        Status[xx/block][yy/block+2]=false;
                        bool=true;
                    }
                }

                if(pattern==3) {
                    if(!Status[xx/block-2][yy/block+1] || !Status[xx/block-1][yy/block+1] || !Status[xx/block][yy/block+1] || !Status[xx/block+1][yy/block+1]) {
                        Status[xx/block-2][yy/block]=false;
                        Status[xx/block-1][yy/block]=false;
                        Status[xx/block][yy/block]=false;
                        Status[xx/block+1][yy/block]=false;
                        bool=true;
                    }
                }
                break;
        }

        return bool;
    }

    public static boolean leftCheck(int xx,int yy,int width,int block,boolean[][] Status,int pattern,int ran) {

        bool=false;

        switch(ran) {
            case 0:
                if(pattern==0) {
                    if(xx>=block && Status[xx/block-1][yy/block+1] && Status[xx/block-1][yy/block] && Status[xx/block-1][yy/block-1] && Status[xx/block-1][yy/block-2]) {
                        bool=true;
                    }
                }

                if(pattern==1) {
                    if(xx>=block*2 && Status[xx/block-2][yy/block]) {
                        bool=true;
                    }
                }

                if(pattern==2) {
                    if(xx>=block && Status[xx/block-1][yy/block-1] && Status[xx/block-1][yy/block] && Status[xx/block-1][yy/block+1] && Status[xx/block-1][yy/block+2]) {
                        bool=true;
                    }
                }

                if(pattern==3) {
                    if(xx>=block*3 && Status[xx/block-3][yy/block]) {
                        bool=true;
                    }
                }
            break;
        }

        return bool;
    }

    public static boolean rightCheck(int xx,int yy,int width,int block,boolean[][] Status,int pattern,int ran) {
        bool=false;

        switch(ran) {
            case 0:
                if(pattern==0) {
                    if(xx<width-block && Status[xx/block+1][yy/block+1] && Status[xx/block+1][yy/block] && Status[xx/block+1][yy/block-1] && Status[xx/block+1][yy/block-2]) {
                        bool=true;
                    }
                }

                if(pattern==1) {
                    if(xx<width-block*3 && Status[xx/block+3][yy/block]) {
                        bool=true;
                    }
                }

                if(pattern==2) {
                    if(xx<width-block && Status[xx/block+1][yy/block-1] && Status[xx/block+1][yy/block] && Status[xx/block+1][yy/block+1] && Status[xx/block+1][yy/block+2]) {
                        bool=true;
                    }
                }

                if(pattern==3) {
                    if(xx<width-block*2 && Status[xx/block+2][yy/block]) {
                        bool=true;
                    }
                }
                break;
        }
        return bool;
    }
}
