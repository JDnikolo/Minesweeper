package gameLogic;

import customExceptions.InvalidDescriptionException;
import customExceptions.InvalidValueException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Game {
    static public scenario script;
    public Board gameBoard;
    int timeLeft;
    int flagsLeft;
    int tries;
    Instant gameStartTime;
    boolean hasEnded;
    boolean wasWon;

    Game(scenario sc){
        script=new scenario(sc);
        flagsLeft=script.mines;
        timeLeft=sc.time;
        gameBoard = new Board(script);
        tries=0;
        hasEnded=false;
        wasWon=false;
        StringBuilder mineOutput= new StringBuilder();
        for (Mine mine: gameBoard.mines){
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

    public void start(){
        this.gameStartTime=Instant.now();
    }
    public void reveal(int x, int y){
        Instant currentTime = Instant.now();
        if (Duration.between(gameStartTime,currentTime).toSeconds()>=timeLeft){
            this.hasEnded=true;
            return;
        }
        boolean noBoom=gameBoard.reveal(x,y,false);
        if (noBoom) {
            tries += 1;
            if(gameBoard.revealedTiles == gameBoard.totalTiles-script.mines){
                hasEnded=true;
                wasWon=true;
            }
        }else{
            hasEnded=true;
        }
    }
    public void flag(int x, int y){
        gameBoard.flag(x,y,this.tries);
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
        g.start();
        System.out.println(g.gameBoard.printMines());
        g.reveal(1,1);
        g.reveal(5,5);
        System.out.println(g.gameBoard.printBoard());
        Scanner in=new Scanner(System.in);
        while(!g.hasEnded) {
            System.out.println("Enter move and coord:");
            char move = in.next().charAt(0);
            int x=in.nextInt();
            int y=in.nextInt();
            switch (move){
                case 'r': g.reveal(x-1,y-1);
                case 'f': g.flag(x-1,y-1);
            }
            System.out.println(g.gameBoard.printBoard());
        }
    }
}
class Board{
    int[][] mineBoard;
    char[][] revealedBoard;
    final int boardSize;
    int revealedTiles;
    final int totalTiles;
    Mine[] mines;
    public Board(scenario script){
        Mine m;
        List l;
        if (script.diff==1) boardSize=9;
        else boardSize=16;
        mineBoard=new int[boardSize][boardSize];
        revealedBoard = new char[boardSize][boardSize];
        revealedTiles = 0;
        totalTiles=boardSize*boardSize;
        mines = new Mine[script.mines];
        for(int i=0;i<script.mines;i++){
            l=Arrays.asList(mines);
            do {
                m= Mine.randomMine(boardSize);
            }while(l.contains(m));
            mines[i]=m;
            mineBoard[m.x][m.y]=1;
        }
        if (script.uber){
            int pick=new Random().nextInt(0, mines.length);
            mines[pick].makeUber();
        }
    }

    //Returns false on mine reveal, true otherwise.
    public boolean reveal(int x, int y,boolean safe){
        if (x<0 || x>boardSize-1) return true;
        if (y<0 || y>boardSize-1) return true;
        //System.out.println("Revealing: "+x+" "+y);
        if (mineBoard[x][y]==1) {
            if (!safe){
                System.out.println("BOOM!");
            }
            return false;
        }
        if (revealedBoard[x][y]!='\u0000'){
            //System.out.println("Already revealed,moving on");
            return true;
        }
        int nearMines=0;
        for (int i=x-1;i<x+2;i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (i == x && j == y) continue;
                //System.out.println("---Checking: "+i+" "+j);
                if (i > -1 && i < boardSize && j > -1 && j < boardSize) {
                    nearMines += mineBoard[i][j];
                }
            }
        }
        if (nearMines==0) {
            revealedBoard[x][y]='E';
            for (int i = x - 1; i < x + 2; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    if (i == x && j == y) continue;
                    this.reveal(i, j,true);
                }
            }
        }else{
            revealedBoard[x][y]= (char) (nearMines+'0');
        }
        revealedTiles+=1;
        return true;
    }
    public void flag(int x, int y,int tries){
        if (x<0 || x>boardSize-1) return;
        if (y<0 || y>boardSize-1) return;
        if (revealedBoard[x][y]=='F') {
            revealedBoard[x][y] = '\u0000';
        }
        if (revealedBoard[x][y]!='\u0000') return; //nothing to do
        revealedBoard[x][y]='F';
        if (mineBoard[x][y]==1){
            for (Mine m:mines){
                if(x==m.x && y==m.x && m.isUber && tries<=4){
                    for (int i=0;i<x;i++) {
                        uberReveal(i, y);
                        uberReveal(x, i);
                    }
                }
            }
        }
    }
    private void uberReveal(int x, int y) {
    //TODO implement this
        if (mineBoard[x][y]==1){
            revealedBoard[x][y]='N'; //neutralized mine
            return;
        }
        if (revealedBoard[x][y]=='\u0000'){
            int nearMines=0;
            for (int i=x-1;i<x+2;i++){
                for (int j=y-1;j<y+2;j++){
                    if(i==x && j==y) continue;
                    //System.out.println("---Checking: "+i+" "+j);
                    if(i>-1 && i<boardSize-1 && j>-1 && j< boardSize-1){
                        nearMines+=mineBoard[i][j];
                    }
                }
            }
            if (nearMines==0) {
                revealedBoard[x][y]='E';
            }else{
                revealedBoard[x][y]= (char) (nearMines+'0');
            }
            revealedTiles+=1;
        }
    }
    String printBoard(){
        StringBuilder output= new StringBuilder();
        StringBuilder sep,start;
        sep=new StringBuilder();
        start=new StringBuilder("     ");
        char out;
        for (int i=0;i<boardSize;i++){
            start.append("  ").append(i+1).append("  ");
            sep = new StringBuilder("     ");
            output.append("| ").append(i+1).append("->");
            for (int j=0;j<boardSize;j++){
                if (revealedBoard[i][j]=='\u0000') out=' ';
                else out=revealedBoard[i][j];
                output.append("| ").append(out).append(" |");
                sep.append("-----");
            }
            output.append('\n').append(sep).append('\n');
        }
        start.append('\n').append(sep).append('\n');
        return start.append(output).toString();
    }
    String printMines(){
        StringBuilder output= new StringBuilder();
        StringBuilder sep;
        for (int[] ints : mineBoard) {
            sep = new StringBuilder();
            for (int j = 0; j < boardSize; j++) {
                output.append("| ").append(ints[j]).append(" |");
                sep.append("-----");
            }
            output.append('\n').append(sep).append('\n');
        }
        return output.toString();
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
    static Mine randomMine(int tableSize){
       Mine m = new Mine();
       Random gen = new Random();
       m.x=gen.nextInt(0, tableSize);
       m.y=gen.nextInt(0, tableSize);
       m.isUber=false;
       return m;
    }
}
