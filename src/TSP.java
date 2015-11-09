import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by andreahe on 4/28/15.
 */
public class TSP implements ActionListener {
    private static final int RANGE = 500;
    private static int N = 0;
    private JFrame frame= new JFrame();
    private JPanel panel = new JPanel(new FlowLayout());
    private DisplayTSP display = new DisplayTSP();
    JTextField input = new JTextField(30);
    private JPanel buttons = new JPanel(new GridLayout(0,2));
    JLabel cheapestInsertionLength = new JLabel("Cheapest insertion tour length");
    JLabel optimalTourLength = new JLabel("Optimal tour length");

    public TSP() {
        display.setPreferredSize(new Dimension(800,800));
        panel.add(input);
        input.setText("Enter an integer number of points (greater than 2).");
        addButton(panel, "Generate points");
        addButton(panel, "Display convex hull");
        addButton(buttons, "Generate cheapest insertion tour");
        buttons.add(cheapestInsertionLength);
        addButton(buttons, "Generate optimal TSP tour");
        buttons.add(optimalTourLength);
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(display, BorderLayout.CENTER);
        frame.getContentPane().add(buttons, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();  // cleans up the window panel
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Generate points")) {
            try {
                if (Integer.parseInt(input.getText()) > 2) {
                    N = Integer.parseInt(input.getText());
                    display.points = generatePoints();
                    display.hull = null;
                    display.tour = null;
                    display.optimal = null;
                }
                else {
                    input.setText("Not valid: try again.");
                }
            }
            catch (Exception ex) {
                input.setText("Not valid: try again.");
            }
            panel.repaint();
            frame.repaint();
            return;
        }
        if (cmd.equals("Display convex hull")) {
            if (N > 0) {
                MyStack convexHullStack = convexHullFinder(display.points);
                display.hull = convexHullStack;
                panel.repaint();
                frame.repaint();
            }
            return;
        }
        if (cmd.equals("Generate cheapest insertion tour")) {
            if (N > 0) {
                Edge[] edges = cheapestInsertion(display.hull, display.points);
                display.tour = edges;
                panel.repaint();
                frame.repaint();
            }
            return;
        }
        if (cmd.equals("Generate optimal TSP tour")) {
            Point[] tour = optimalTourFinder(display.points);
            display.optimal = tour;
            panel.repaint();
            frame.repaint();
            return;
        }
        else {
            throw new RuntimeException("No such button: " + cmd);}
    }

    //Helper method for adding buttons
    private JButton addButton(JPanel panel, String name){
        JButton button = new JButton(name);
        button.addActionListener(this);
        panel.add(button);
        return button;
    }

    public TSP(int n) {
        N = n;
    }

    public Point[] generatePoints() {
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            Random random = new Random();
            points[i] = new Point(random.nextInt(RANGE+1)+100, random.nextInt(RANGE+1)+100); //shift coordinates so appear   in frame
        }
        return points;
    }

    //Returns the lowest, rightmost point from an array of Points
    public Point findBottomRight(Point[] points) {
        Point current = points[0];
        for (int i = 1; i < points.length; i++) {
            //If we find a lower point, replace it.
            if (points[i].ycoord > current.ycoord) {
                current = points[i];
            }
            //If we find a point that's equally as low and further right, replace it.
            else if (points[i].ycoord == current.ycoord && points[i].xcoord > current.xcoord) {
                current = points[i];
            }
        }
        return current;
    }

    //Sets the angle of each point to the angle that the point makes with a horizontal line through P0
    public void setAngles(Point[] points, Point P0) {
        for (int i = 0; i < points.length; i++) {
            points[i].angle = Math.atan2(points[i].ycoord-P0.ycoord, points[i].xcoord-P0.xcoord);
        }
    }

    //Uses quicksort to sort points by angle
    public void sortByAngle(Point[] points) {
        QuickSort quick = new QuickSort();
        quick.quicksort(points);
    }

    //Returns a stack of consecutive points on the convex hull of the points in the array, implementing the Graham scan.
    public MyStack convexHullFinder(Point[] points) {
        MyStack stack = new MyStack();
        Point P0 = findBottomRight(points);
        setAngles(points, P0);
        sortByAngle(points);
        //System.out.println("Sorted: ");
        //printPoints(points);
        stack.push(points[points.length-1]);
        stack.push(points[0]);
        int i = 1;
        while (i < N) {
            //System.out.println(i);
            if (!isRight(points[i], stack.header.data, stack.header.next.data)) {
                stack.push(points[i]);
                i++;
            }
            else {
                stack.pop();
            }
        }
        return stack;
    }

    //Returns true if P is to the right of the line formed by Ptop and Ptop1
    public boolean isRight(Point P, Point Ptop, Point Ptop1) {
        return ((Ptop.xcoord-Ptop1.xcoord)*(P.ycoord-Ptop1.ycoord) - (Ptop.ycoord-Ptop1.ycoord)*(P.xcoord-Ptop1.xcoord) <= 0);
    }

    //Returns an array of edges in a TSP tour of the points in the array points, starting from the convex hull points
    public Edge[] cheapestInsertion(MyStack convexHullPoints, Point[] points) {
        Edge[] TSPEdges = new Edge[points.length];
        //Transfer convex hull edges into TSPEdges
        Node current = convexHullPoints.header;
        int i = 0;
        while (current.next != null) {
            TSPEdges[i] = new Edge(current.data, current.next.data);
            current.data.visited = true;
            current.next.data.visited = true;
            current = current.next;
            i++;
        }
        //Find TSP tour using cheapest insertion metric
        while (i < points.length) {
            //int numberOfImprovements = 0;
            Edge e = TSPEdges[0];
            int replace = 0;
            Point p;
            int a = 0;
            while (points[a].visited) {
                a++;
            }
            p = points[a];
            double dist = distance(e.x, p) + distance(e.y, p) - distance(e.x, e.y);
            for (int j = 0; j < points.length; j++) {
                if (!points[j].visited) {
                    for (int k = 0; k < i; k++) {
                        if (distance(TSPEdges[k].x, points[j]) + distance(TSPEdges[k].y, points[j]) - distance(TSPEdges[k].x, TSPEdges[k].y) < dist) {
                            dist = distance(TSPEdges[k].x, points[j]) + distance(TSPEdges[k].y, points[j]) - distance(TSPEdges[k].x, TSPEdges[k].y);
                            e = TSPEdges[k];
                            p = points[j];
                            replace = k;
                            //numberOfImprovements++;
                        }
                    }
                }
            }
            TSPEdges[replace] = new Edge(e.x, p);
            TSPEdges[i] = new Edge(e.y, p);
            p.visited = true;
            i++;
            //System.out.println("Had " + numberOfImprovements + " on iteration " + i + "; added pt " + p.xcoord + " " + p.ycoord);
        }
        double totalDist = 0;
        for (int j = 0; j < N; j++) {
            totalDist += distance(TSPEdges[j].x, TSPEdges[j].y);
        }
        //System.out.println("Cheapest insertion length: " + totalDist);
        cheapestInsertionLength.setText(Double.toString(totalDist));
        return TSPEdges;
    }

    //Finds optimal TSP tour by testing all permutations. Returns tour as an ordered array of points visited.
    public Point[] optimalTourFinder(Point[] points) {
        Point[] currentPermutation = points;
        double dist = lengthOfTour(points);
        int[] intArray = new int[N];
        for (int i = 0; i < N; i++) {
            intArray[i] = i;
        }
        int[][] allPermutations = permute(intArray);
        for (int i = 0; i < allPermutations.length; i++) {
            //System.out.println("Permutation: " + i);
            //printArray(allPermutations[i]);
            Point[] pointArray = new Point[N];
            for (int j = 0; j < N; j++) {
                pointArray[j] = points[allPermutations[i][j]];
            }
            if (lengthOfTour(pointArray) < dist) {
                dist = lengthOfTour(pointArray);
                currentPermutation = pointArray;
            }
        }
        //System.out.println("Optimal tour length: " + dist);
        optimalTourLength.setText(Double.toString(dist));
        return currentPermutation;
    }

    public static double lengthOfTour(Point[] points) {
        double dist = 0;
        for (int i = 0; i < points.length-1; i++) {
            dist += distance(points[i], points[i+1]);
        }
        dist += distance(points[0], points[points.length-1]);
        return dist;
    }

    //Returns a 2D array containing all permutations of the array
    public static int[][] permute(int[] array) {
       if (array.length == 1) {
           int[][] permutations = new int[1][1];
           permutations[0] = array;
           return permutations;
       }
        int j = 0;
        int[][] permutations = new int[factorial(array.length)][array.length];
        for (int i = 0; i < array.length; i++) {
            int[] newArray = new int[array.length-1];
            for (int k = 0; k < array.length-1; k++) {
                if (k < i) {
                    newArray[k] = array[k];
                }
                if (k >= i) {
                    newArray[k] = array[k+1];
                }
            }
            int[][] previousPerms = permute(newArray);
            for (int a = 0; a < previousPerms.length; a++) {
                int[] permutation = new int[array.length];
                for (int b = 0; b < array.length-1; b++) {
                    permutation[b] = previousPerms[a][b];
                }
                permutation[array.length-1] = array[i];
                permutations[j] = permutation;
                j++;
            }
        }
        return permutations;
    }

    //For debugging: prints an integer array
    public static void printArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

    //Returns n!
    public static int factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n*factorial(n-1);
    }

    //Returns the distance between the points a and b
    public static double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.xcoord - b.xcoord, 2) + Math.pow(a.ycoord - b.ycoord, 2));
    }

    public static void main(String[] args) {
        TSP tsp = new TSP();
/*        TSP tsp = new TSP(Integer.parseInt(args[0]));
        Point[] points = tsp.generatePoints();
        printPoints(points);
        MyStack convexHullStack = tsp.convexHullFinder(points);
        DisplayTSP f = new DisplayTSP(points,convexHullStack);
        f.setVisible(true);
        f.tour = cheapestInsertion(convexHullStack, points);
        f.repaint();
        f.optimal = optimalTourFinder(points);
        f.repaint();*/
    }

    //For debugging
    public static void printPoints(Point[] points) {
        for (int i = 0; i < points.length; i++) {
            System.out.println("(" + points[i].xcoord + ", " + points[i].ycoord + ")" + points[i].angle);
        }
    }
}
