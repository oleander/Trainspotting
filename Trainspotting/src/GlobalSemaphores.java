
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class GlobalSemaphores {

    private static HashMap<Point, Semaphore> semaphores = new HashMap<Point, Semaphore>();

    // must be symchronized so not 2 sempahores get created at same time.
    public static synchronized Semaphore findOrCreate(Point p) {
        if (semaphores.containsKey(p)) {
            Semaphore s = semaphores.get(p);
//            System.err.println("Retrieving existing semaphore " + p + ":" + s);
            return s;
        } else {
            Semaphore s = new Semaphore(1, true);
            semaphores.put(p, s);
//            System.err.println("Created new semaphore at " + p + ":" + s);
            System.err.println("Num semaphores: " + semaphores.size());
            return s;
        }
    }

    static Semaphore findOrCreate2(Point p1, Point p2) {
        return findOrCreate(sortHackMerge(p1, p2));
    }

    static Point sortHackMerge(Point p1, Point p2){
        if(1000*p1.x+p1.y > 1000*p2.x+p2.y){
            return sortHackMerge(p2, p1);
        }
        return new Point(p1.x*1000+p1.y, p2.x*1000+p2.y);
    }
}
