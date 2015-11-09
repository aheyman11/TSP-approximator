/**
 * Created by andreahe on 4/28/15.
 */
public class Point implements Comparable{
    public int xcoord;
    public int ycoord;
    public double angle; //stores the angle between this point and P0
    public boolean visited = false;
    public Point(int x, int y) {
        xcoord = x;
        ycoord = y;
    }
    //compare based on angle from P0
    public int compareTo(Object other) {
        Point test = (Point) other;
        if (angle < test.angle) {
            return -1;
        }
        if (angle > test.angle) {
            return 1;
        }
        if (angle == test.angle && angle != Math.PI) {
            if (ycoord < test.ycoord) {
                return -1;
            }
            if (ycoord > test.ycoord) {
                return 1;
            }
            if (ycoord == test.ycoord) {
                return 0;
            }
        }
        if (angle == test.angle && angle == Math.PI) {
            if (xcoord > test.xcoord) {
                return -1;
            }
            if (xcoord < test.xcoord) {
                return 1;
            }
            if (xcoord == test.xcoord) {
                return 0;
            }
        }
        return 0;
    }
}
