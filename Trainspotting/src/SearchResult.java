public class SearchResult {
    public Point pos;
    public int direction;
    public int distance;

    public SearchResult(Point pos, int direction, int distance) {
        this.pos = pos;
        this.direction = direction;
        this.distance = distance;
    }
}
