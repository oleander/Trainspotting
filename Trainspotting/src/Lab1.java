
import TSim.*;
import java.io.File;

public final class Lab1 {

    public static void main(String[] a) {
        new Lab1();
    }

    public Lab1() {
        TSimInterface.getInstance().setDebug(false);
        startTrains();

    }

    private void startTrains() {
//        final RailMap rm = new RailMap(new File("build/classes/bana"));
        final RailMap rm = new RailMap(new File("bana"));
        rm.printAsciiMap();
        if(rm.isEnd(new Point(3, 7))){
            System.err.println("isEnd");
        }
        if(rm.isSwitch(new Point(7, 7))){
            System.err.println("isSwitch");
        }

        for(int tid = 1; tid <= rm.getNumTrains(); tid++) {
            Train t = new Train(rm, tid == 2 ? 15 : 15, tid);
            t.start();
        }

        new Thread(new Runnable() {

            public void run() {
                //startControlTrainForever(2);
            }
        }).start();
    }

}
