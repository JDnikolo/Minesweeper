import customExceptions.InvalidDescriptionException;
import customExceptions.InvalidValueException;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
    static public scenario script;
    public int[][] board;
    public char[][] revealed;
    static public int minesTotal;
    public int minesLeft;
    public Mine[] mines;
    int timeLeft;

    Game(scenario sc){
        int size;
        script=new scenario(sc);
        if (script.diff==1) size=9;
        else size=16;
        minesTotal=minesLeft=script.mines;
        timeLeft=sc.time;
        board=new int[size][size];
        revealed = new char[size][size];
        mines=new Mine[minesTotal];
        initBoard();
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
            revealed[x][y]='R';
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
        String output="";
        String sep;
        for (int i=0;i<board.length;i++){
            sep="";
            for (int j=0;j<board.length;j++){
                output+="| "+board[i][j]+" |";
                sep+="-----";
            }
            output+='\n'+sep+'\n';
        }
        return output;
    }

    String printBoard(){
        String output="";
        String sep;
        char out;
        for (int i=0;i<board.length;i++){
            sep="";
            for (int j=0;j<board.length;j++){
                if (revealed[i][j]=='\u0000') out=' ';
                else out=revealed[i][j];
                output+="| "+out+" |";
                sep+="-----";
            }
            output+='\n'+sep+'\n';
        }
        return output;
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
