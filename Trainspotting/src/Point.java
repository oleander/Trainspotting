public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    

    @Override
    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.x;
        hash = 41 * hash + this.y;
        return hash;
    }

    void moveInDirection(int dir) {
        x += DirectionArrays.xDirs[dir];
        y += DirectionArrays.yDirs[dir];
    }

    public static Point createNewAndMove(Point p, int dir){
        Point newP = new Point(p.x, p.y);
        newP.moveInDirection(dir);
        return newP;
    }
}
