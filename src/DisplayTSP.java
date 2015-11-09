import javax.swing.*;
import java.awt.*;

/**
 * Created by andreahe on 4/29/15.
 */
public class DisplayTSP extends JPanel{
    Point[] points;
    MyStack hull;
    Edge[] tour;
    Point[] optimal;
    public DisplayTSP() {
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,700);
        setVisible(true);
        setBackground(Color.white);
    }

    public void paint(Graphics g) {
        //Plot cities as closed circles and label with their names.
        if (points != null) {
            for (int i = 0; i < points.length; i++) {
                g.fillOval(points[i].xcoord-3, points[i].ycoord-3, 6, 6);
            }
        }
        g.setColor(Color.red);
        //Draw lines between points on hull
        if (hull != null) {
            Node current = hull.header;
            //int i = 1;
            while (current.next != null) {
                g.drawLine(current.data.xcoord, current.data.ycoord, current.next.data.xcoord, current.next.data.ycoord);
                //g.drawString(Integer.toString(i), current.data.xcoord, current.data.ycoord);
                //i++;
                current = current.next;
            }
        }
        //g.drawLine(current.data.xcoord, current.data.ycoord, hull.header.data.xcoord, hull.header.data.ycoord);
        //Draw edges of tour
        if (tour != null) {
            g.setColor(Color.blue);
            for (int j = 0; j < tour.length; j++) {
                g.drawLine(tour[j].x.xcoord, tour[j].x.ycoord, tour[j].y.xcoord, tour[j].y.ycoord);
            }
        }
        //repaint();
        if (optimal != null) {
            g.setColor(Color.black);
            for (int j = 0; j < optimal.length - 1; j++) {
                g.drawLine(optimal[j].xcoord, optimal[j].ycoord, optimal[j + 1].xcoord, optimal[j + 1].ycoord);
            }
            g.drawLine(optimal[0].xcoord, optimal[0].ycoord, optimal[tour.length - 1].xcoord, optimal[tour.length - 1].ycoord);
        }
    }
}
