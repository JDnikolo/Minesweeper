import customExceptions.InvalidDescriptionException;
import customExceptions.InvalidValueException;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Game {
    static public scenario script;
    public int[][] board;
    public char[][] revealed;
    static public int minesTotal;
    int minesLeft;
    public Mine[] mines;
    int timeLeft;
    int flagsLeft;

    Game(scenario sc){
        int size;
        script=new scenario(sc);
        if (script.diff==1) size=9;
        else size=16;
        minesTotal=minesLeft=flagsLeft=script.mines;
        timeLeft=sc.time;
        board=new int[size][size];
        revealed = new char[size][size];
        mines=new Mine[minesTotal];
        initBoard();
        StringBuilder mineOutput= new StringBuilder();
        for (Mine mine:mines){
            mineOutput.append(mine.printMine()).append('\n');
        }
        try{
            FileWriter out= new FileWriter("medialab\\mines.txt",false);
            out.write(mineOutput.toString());
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void reveal(int x, int y,boolean safe){
        if (x<0 || x>board.length-1) return;
        if (y<0 || y>board.length-1) return;
        //System.out.println("Revealing: "+x+" "+y);
        if (board[x][y]==1) {
            if (!safe){
                System.out.println("BOOM!");
            }
            return;
        }
        if (revealed[x][y]!='\u0000'){
            //System.out.println("Already Revealed,moving on");
            return;
        }
        int nearMines=0;
        for (int i=x-1;i<x+2;i++){
            for (int j=y-1;j<y+2;j++){
                if(i==x && j==y) continue;
                //System.out.println("---Checking: "+i+" "+j);
                if(i>-1 && i<board.length-1 && j>-1 && j< board.length-1){
                    nearMines+=board[i][j];
                }
            }
        }
        if (nearMines==0) {
            revealed[x][y]='E';
            for (int i = x - 1; i < x + 2; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    if (i == x && j == y) continue;
                    this.reveal(i, j,true);
                }
            }
        }else{
            revealed[x][y]= (char) (nearMines+'0');
        }
    }

    public void flag(int x, int y){
        if (x<0 || x>board.length-1) return;
        if (y<0 || y>board.length-1) return;
        if (revealed[x][y]=='F') {
            revealed[x][y] = '\u0000';
            flagsLeft+=1;
        }
        if (revealed[x][y]!='\u0000') return; //nothing to reveal
        revealed[x][y]='F';
        flagsLeft=(flagsLeft-1);
        if (board[x][y]==1){
            minesLeft-=1;
            for (Mine m:mines){
                if(x==m.x && y==m.x && m.isUber){
                    for (int i=0;i<x;i++) {
                        reveal(i, y, true);
                        reveal(x, i, true);
                    }
                }
            }
        }
    }
    public void initBoard(){
        int size;
        Mine m;
        List l;
        if (script.diff==1) size=9;
        else size=16;
        for(int i=0;i<minesTotal;i++){
            l=Arrays.asList(mines);
            do {
                m= Mine.randomMine(size, false);
            }while(l.contains(m));
            mines[i]=m;
            board[m.x][m.y]=1;
        }
        if (script.uber){
            int pick=new Random().nextInt(0, mines.length);
            mines[pick].makeUber();
        }
    }

    String printMines(){
        StringBuilder output= new StringBuilder();
        StringBuilder sep;
        for (int[] ints : board) {
            sep = new StringBuilder();
            for (int j = 0; j < board.length; j++) {
                output.append("| ").append(ints[j]).append(" |");
                sep.append("-----");
            }
            output.append('\n').append(sep).append('\n');
        }
        return output.toString();
    }

    String printBoard(){
        StringBuilder output= new StringBuilder();
        StringBuilder sep;
        char out;
        for (int i=0;i<board.length;i++){
            sep = new StringBuilder();
            for (int j=0;j<board.length;j++){
                if (revealed[i][j]=='\u0000') out=' ';
                else out=revealed[i][j];
                output.append("| ").append(out).append(" |");
                sep.append("-----");
            }
            output.append('\n').append(sep).append('\n');
        }
        return output.toString();
    }

    public static void main(String[] args) {
        String filename=args[0];
        scenario sc=new scenario();
        try {
            sc=scenario.readFromFile(filename);
        }catch (FileNotFoundException fnf){
            System.err.println("File Not Found: "+filename);
        }catch (InvalidDescriptionException e){
            System.err.print(e.getMessage());
        } catch (InvalidValueException e){
            System.err.println(e.getMessage());
        }
        Game g=new Game(sc);
        System.out.println(g.printMines());
        g.reveal(1,1,false);
        g.reveal(5,5,false);
        System.out.println(g.printBoard());
        Scanner in=new Scanner(System.in);
        while(g.minesLeft>0) {
            System.out.println("Enter move and coord:");
            char move = in.next().charAt(0);
            int x=in.nextInt();
            int y=in.nextInt();
            switch (move){
                case 'r': g.reveal(x,y,false);
                case 'f': g.flag(x,y);
            }
            System.out.println(g.printBoard());
        }
    }
}
class Mine{
    public int x;
    public int y;
    public boolean isUber;
    Mine(){
        x=y=0;
        isUber=false;
    }
    String printMine(){
        char u;
        if (isUber) u='1';
        else u='0';
        return(x+","+y+","+u);
    }
    void makeUber(){
        isUber=true;
    }
    static Mine randomMine(int tableSize, boolean uber){
        Mine m=randomMine(tableSize);
        m.isUber=uber;
        return m;
    }
    static Mine randomMine(int tableSize){
       Mine m = new Mine();
       Random gen = new Random();
       m.x=gen.nextInt(0,tableSize-1);
       m.y=gen.nextInt(0,tableSize-1);
       m.isUber=gen.nextBoolean();
       return m;
    }
}
