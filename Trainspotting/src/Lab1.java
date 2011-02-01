
import TSim.*;
import java.io.File;

public final class Lab1 {

    private final RailMap railMap;

    public static void main(String[] args) {
        String fileName = args[0];
        int[] trainSpeeds = new int[1000];
        for (int i = 0; i < 1000; i++) {
            trainSpeeds[i] = 10; //10 is trainspeed if not specified
            if (i < args.length - 1) {
                trainSpeeds[i] = Integer.parseInt(args[i + 1]);
            }
        }
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("Didn't find given file: " + fileName);
        }
        new Lab1(file, trainSpeeds);
    }

    public Lab1(File file, int[] trainSpeeds) {
        railMap = new RailMap(file);
        TSimInterface.getInstance().setDebug(false);
        startTrains(trainSpeeds);
    }

    private void startTrains(int[] trainSpeeds) {
//        railMap.printAsciiMap();

        for (int tid = 1; tid <= railMap.getNumTrains(); tid++) {
            Train t = new Train(railMap, trainSpeeds[tid - 1], tid);
            t.start();
        }
    }
}
