
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class GlobalSemaphores {

    private static HashMap<Point, Semaphore> semaphores = new HashMap<Point, Semaphore>();

    // must be symchronized so not 2 sempahores get created at same time.
    public static synchronized Semaphore findOrCreate(Point p) {
        if (semaphores.containsKey(p)) {
            Semaphore s = semaphores.get(p);
            System.err.println("Retrieving existing semaphore " + s);
            return s;
        } else {
            Semaphore s = new Semaphore(1, true);
            semaphores.put(p, s);
            System.err.println("Created new semaphore at " + p + ":" + s);
            return s;
        }
    }
}
