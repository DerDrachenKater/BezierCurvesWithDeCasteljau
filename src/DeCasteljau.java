import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class DeCasteljau
{
	private static DrawPanel dp = new DrawPanel();
	static Controls theControls = new Controls(dp);
	
	public DeCasteljau()
	{
		JFrame f = new JFrame("DeCasteljau");
		
		f.setLayout(new BorderLayout());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		f.add("Center", new FramedArea(dp));
		f.add("South", theControls);
		
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setVisible(true);
	}
	
	public static void main(String args[])
	{
		DeCasteljau drawBez = new DeCasteljau();
	}
}

class Geometry
{
	static Point interpolate(Point p0, Point p1, double t)
	{
		double x = t * p1.x + (1 - t) * p0.x;
		double y = t * p1.y + (1 - t) * p0.y;
		return new Point((int) (x + 0.5), (int) (y + 0.5));
	}
	
	static Point calcBezier(Point arr[], double t)
	{
		for (int iter = arr.length; iter > 0; iter--)
		{
			for (int i = 1; i < iter; i++)
			{
				arr[i - 1] = interpolate(arr[i - 1], arr[i], t);
			}
		}
		return arr[0];
	}
}

class FramedArea extends Panel
{
	FramedArea(DrawPanel target)
	{
		super();
		
		setLayout(new GridLayout(1, 0));
		
		add(target);
		validate();
	}
	
	public Insets getInsets()
	{
		return new Insets(4, 4, 5, 5);
	}
	
	public void paint(Graphics g)
	{
		Dimension d = this.getSize();
		Color bg = getBackground();
		
		g.setColor(bg);
		g.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
		g.draw3DRect(3, 3, d.width - 7, d.height - 7, false);
	}
}

class DrawPanel extends Panel implements MouseListener
{
	
	private static final int EXTEND = 0;
	private int mode = EXTEND;
	private boolean bShow = false;
	int step = 0;
	private Point dePnts[][];
	double tVal = 0.5;
	
	private Vector points = new Vector();
	
	private Point mPoint;
	private int index;
	
	DrawPanel()
	{
		setBackground(Color.white);
		mPoint = new Point(-1, -1);
		points.addElement(new Point(800, 100));
		points.addElement(new Point(200, 300));
		points.addElement(new Point(100, 600));
		points.addElement(new Point(900, 800));
		
		addMouseListener(this);
		
	}
	
	void initShow()
	{
		bShow = true;
		step = 0;
		// allocate space for points
		int i, j;
		int np = points.size();
		if (np <= 0)
			return;
		dePnts = new Point[np][];
		for (i = 0; i < np; i++)
			dePnts[i] = new Point[np - i];
		// copy original polygon
		points.copyInto(dePnts[0]);
		// fill the points of all algorithm steps
		for (i = 1; i < np; i++)
			for (j = 0; j < np - i; j++)
				dePnts[i][j] = Geometry.interpolate(dePnts[i - 1][j], dePnts[i - 1][j + 1], tVal);
		repaint();
	}
	
	void endShow()
	{
		bShow = false;
		step = 0;
		repaint();
	}
	
	void clearAll()
	{
		points.removeAllElements();
		repaint();
	}
	
	private void drawDeCasteljau(Graphics g)
	{
		step = points.size() - 1;
		int np = points.size();
		if (np <= 0)
			return;
		Point p0, p1;
		for (int i = 0; i <= step; i++)
		{
			// draw first point
			p0 = dePnts[i][0];
			g.setColor(Color.red);
			g.fillRect(p0.x - 2, p0.y - 2, 5, 5);
			// draw polygon & other points
			for (int j = 1; j < np - i; j++)
			{
				p1 = dePnts[i][j];
				g.setColor(Color.blue);
				g.drawLine(p0.x, p0.y, p1.x, p1.y);
				g.setColor(Color.red);
				g.fillRect(p1.x - 2, p1.y - 2, 5, 5);
				p0 = p1;
			}
		}
		if (step == np - 1)
		{
			drawBezier(g);
			// show last point
			g.setColor(Color.black);
			p0 = dePnts[step][0];
			g.fillRect(p0.x - 2, p0.y - 2, 5, 5);
		}
	}
	
	private void drawBezier(Graphics g)
	{
		int np = points.size();
		if (np < 3)
			return;
		
		Point ptArray[] = new Point[np];
		
		Point p0, p1;
		p0 = (Point) points.elementAt(0);
		for (int i = 1; i <= 50; i++)
		{
			double t = (double) i / 50.0;
			points.copyInto(ptArray);
			p1 = Geometry.calcBezier(ptArray, t);
			g.setColor(Color.DARK_GRAY);
			g.drawLine(p0.x, p0.y, p1.x, p1.y);
			p0 = p1;
		}
	}
	
	public void paint(Graphics g)
	{
		
		if (bShow)
		{
			drawDeCasteljau(g);
			return;
		}
		
		// draw the bezier spline + polygon
		int np = points.size();
		if (np == 0)
			// draw nothing
			return;
		
		// draw polygon
		Point p0, p1;
		p0 = (Point) points.elementAt(0);
		g.setColor(Color.red);
		g.fillRect(p0.x - 2, p0.y - 2, 4, 4);
		
		for (int i = 1; i < np; i++)
		{
			p0 = (Point) points.elementAt(i - 1);
			p1 = (Point) points.elementAt(i);
			g.setColor(Color.red);
			g.fillRect(p1.x - 2, p1.y - 2, 4, 4);
			g.setColor(Color.blue);
			g.drawLine(p0.x, p0.y, p1.x, p1.y);
		}
		
		// draw bezier
		drawBezier(g);
	}
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent)
	{
		
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		mPoint.x = e.getX();
		mPoint.y = e.getY();
		switch (mode)
		{
			case EXTEND:
				points.addElement(new Point(e.getX(), e.getY()));
				index = points.size() - 1;
				break;
			
		}
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent mouseEvent)
	{
		
	}
	
	@Override
	public void mouseEntered(MouseEvent mouseEvent)
	{
		
	}
	
	@Override
	public void mouseExited(MouseEvent mouseEvent)
	{
		
	}
}

class Controls extends Panel
{
	
	Controls(DrawPanel target)
	{
		setLayout(new CardLayout());
		add("Draw", new DrawControls(target));
		add("View", new ViewControls(target));
		
	}
}

class ViewControls extends Panel implements AdjustmentListener, ActionListener
{
	
	private DrawPanel target;
	private Label tValLabel;
	private final int INTERVAL = 100;
	private final int INITIAL_VAL = INTERVAL / 2;
	
	ViewControls(DrawPanel target)
	{
		this.target = target;
		setLayout(new FlowLayout());
		setBackground(Color.lightGray);
		
		tValLabel = new Label("t=" + target.tVal + " ");
		add(tValLabel);
		
		Scrollbar sb = new Scrollbar(Scrollbar.HORIZONTAL, INITIAL_VAL, INTERVAL / 100, 0, INTERVAL + 1);
		add(sb);
		sb.addAdjustmentListener(this);
		
		Button okB = new Button("OK");
		add(okB);
		okB.addActionListener(this);
		
	}
	
	public void paint(Graphics g)
	{
		Rectangle r = this.getBounds();
		g.setColor(Color.lightGray);
		g.draw3DRect(0, 0, r.width, r.height, false);
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if (e.getSource() instanceof Scrollbar)
		{
			int val = ((Scrollbar) e.getSource()).getValue();
			target.tVal = (double) val / (double) INTERVAL;
			tValLabel.setText("t=" + target.tVal + " ");
			target.initShow();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof Button)
		{
			String choice = ((Button) e.getSource()).getLabel();
			
			if (choice.equals("OK"))
			{
				((CardLayout) DeCasteljau.theControls.getLayout()).first(DeCasteljau.theControls);
				target.endShow();
			}
		}
	}
}

class DrawControls extends Panel implements ActionListener
{
	
	private DrawPanel target;
	
	DrawControls(DrawPanel target)
	{
		this.target = target;
		setLayout(new FlowLayout());
		setBackground(Color.lightGray);
		
		Button b1 = new Button("Clear");
		add(b1);
		b1.addActionListener(this);
		Button b2 = new Button("Show De-Casteljau");
		add(b2);
		b2.addActionListener(this);
		
	}
	
	public void paint(Graphics g)
	{
		Rectangle r = this.getBounds();
		g.setColor(Color.lightGray);
		g.draw3DRect(0, 0, r.width, r.height, false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof Button)
		{
			String choice = ((Button) e.getSource()).getLabel();
			
			if (choice.equals("Clear"))
			{
				target.clearAll();
			} else if (choice.equals("Show De-Casteljau"))
			{
				((CardLayout) DeCasteljau.theControls.getLayout()).last(DeCasteljau.theControls);
				target.initShow();
			}
		}
	}
}