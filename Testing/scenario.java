import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import customExceptions.*;

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

    static public scenario readFromFile(String filename) throws FileNotFoundException, InvalidDescriptionException, InvalidValueException {
        byte d;
        int m;
        int t;
        int u;
        File file = new File("medialab\\"+filename);
        Scanner sc = new Scanner(file);
        try {
            d = sc.nextByte();
            m = sc.nextInt();
            t = sc.nextInt();
            u = sc.nextInt();
        } catch (InputMismatchException e){
            throw new InvalidDescriptionException("Invalid scenario description in file \""+filename+"\"",e);
        } catch (NoSuchElementException e){
            throw new InvalidDescriptionException("Invalid scenario description in file \""+filename+"\"",e);
        }
        if (d!=1 && d!=2){
            throw new InvalidValueException(filename+": invalid difficulty.");
        } else if (d==1) {
            if (m>11 || m<9) throw new InvalidValueException(filename+": invalid mine amount.");

            if (t<120 || t>180) throw new InvalidValueException(filename+": invalid time.");

            if (u!=0) throw new InvalidValueException(filename+": invalid uber-mine flag.");
        } else { //d==2
            if (m>45 || m<35) throw new InvalidValueException(filename+": invalid mine amount.");

            if (t<240 || t>360) throw new InvalidValueException(filename+": invalid time.");

            if (u!=1) throw new InvalidValueException(filename+": invalid mine amount.");
    }
        System.out.println("Difficulty: "+d);
        System.out.println("Mines:      "+m);
        System.out.println("Time:       "+t);
        System.out.println("Uber-mine:  "+(u==1));
        return new scenario(d,m,t,u==1);
    }
    public static void main(String[] args){
        if (args.length!=1){
            System.out.println("Usage: scenario <filename>");
            return;
        }
        String filename=args[0];
        try {
            scenario.readFromFile(filename);
        }catch (FileNotFoundException fnf){
            System.err.println("File Not Found: "+filename);
        }catch (InvalidDescriptionException e){
            System.err.print(e.getMessage());
        } catch (InvalidValueException e){
            System.err.println(e.getMessage());
        }
    }
}
