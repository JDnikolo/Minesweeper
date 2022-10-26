import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class scenario {
    private byte diff;
    private int mines;
    private int time;
    private boolean uber;

    public scenario(){
        this.diff=1;
        this.mines=9;
        this.time = 180;
        this.uber=false;
    }
    public scenario(byte d, int m, int t,boolean u){
        diff=d;
        mines=m;
        time=t;
        uber=u;
    }

    static public scenario readFromFile(String filename) throws FileNotFoundException {
      //TODO read from file
        File file = new File("C:\\Code\\Minesweeper\\Testing\\medialab\\"+filename);
        Scanner sc = new Scanner(file);
        byte d = sc.nextByte();
        int m = sc.nextInt();
        int t=sc.nextInt();
        boolean u = 0==sc.nextInt();
        System.out.println(d);
        System.out.println(m);
        System.out.println(t);
        System.out.println(u);
        return new scenario(d,m,t,u);
    }
    public static void main(String[] args){
        try {
            scenario.readFromFile("SCENARIO-01.txt");
        }catch (FileNotFoundException fnf){
            System.out.println("File Not Found.");
        }
    }
}
