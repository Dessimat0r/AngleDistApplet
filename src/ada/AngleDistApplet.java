/**
 * 
 */
package ada;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.NumberFormat;
import java.util.Random;

/**
 * AngleDistApplet - AngleDistApplet
 * @version 1.0
 * @author <a href="mailto:Dessimat0r@ntlworld.com">Chris Dennett</a>
 */
public class AngleDistApplet extends AppletGameCore implements MouseListener, KeyListener, MouseWheelListener {
	public static final double DEG_TO_RAD = Math.PI / 180f;
	
	public static final Font FONT_1 = new Font("Helvetica", Font.PLAIN,  22);
	public static final Font FONT_2 = new Font("TimesRoman", Font.PLAIN,  20);
	public static final Font FONT_3 = new Font("Courier", Font.PLAIN,  18);

	public static final Font FONT_4 = new Font("Helvetica", Font.BOLD,  16);
	public static final Font FONT_5 = new Font("Helvetica", Font.ITALIC,  16);
	public static final Font FONT_6 = new Font("Helvetica", Font.BOLD + Font.ITALIC,  16);
	
	public static final Font FONT_7 = new Font("Helvetica", Font.PLAIN,  12);
	
	public static final Random r = new Random();

	private NumberFormat formatter = NumberFormat.getInstance();
	{
		formatter.setMinimumFractionDigits(0);
		formatter.setMaximumFractionDigits(1);
	}


	double currAngle = 0d;
	double toAngle = 0d;
	
	double acwdist = 0d;
	double cwdist = 0d;
	
	double delta = 0d;
	double increment = 1d;

	private long lastMovementTime = 0;
	
	double blockedAngleStart = Double.NaN;
	double blockedAngleEnd   = Double.NaN;
	
	/* (non-Javadoc)
	 * @see ada.AppletGameCore#init()
	 */
	@Override
	public void init() {
		super.init();
		drawArea.addKeyListener(this);
		drawArea.addMouseListener(this);
		drawArea.addMouseWheelListener(this);
	}

	/* (non-Javadoc)
	 * @see ada.AppletGameCore#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		int min = Math.min(getWidth() - 40, getHeight() - 40);
		int minh = min / 2;
		
		g.setColor(Color.WHITE);
		g.drawOval(20, 20, min, min);
		
		int lineEndX = (int)(minh - (Math.cos((-Math.PI / 2d) - (toAngle * DEG_TO_RAD)) * minh));
		int lineEndY = (int)(minh + (Math.sin((-Math.PI / 2d) - (toAngle * DEG_TO_RAD)) * minh));
		
		g.setColor(Color.GREEN);
		g.drawLine(minh + 20, minh + 20, 20 + lineEndX, 20 + lineEndY);
		
		lineEndX = (int)(minh - (Math.cos((-Math.PI / 2d) - (currAngle * DEG_TO_RAD)) * minh));
		lineEndY = (int)(minh + (Math.sin((-Math.PI / 2d) - (currAngle * DEG_TO_RAD)) * minh));
		
		g.setColor(Color.BLUE);
		g.drawLine(minh + 20, minh + 20, 20 + lineEndX, 20 + lineEndY);
		
		if (!Double.isNaN(blockedAngleStart)) {
			lineEndX = (int)(minh - (Math.cos((-Math.PI / 2d) - (blockedAngleStart * DEG_TO_RAD)) * minh));
			lineEndY = (int)(minh + (Math.sin((-Math.PI / 2d) - (blockedAngleStart * DEG_TO_RAD)) * minh));
			
			g.setColor(Color.RED);
			g.drawLine(minh + 20, minh + 20, 20 + lineEndX, 20 + lineEndY);
			
			if (!Double.isNaN(blockedAngleEnd)) {
				lineEndX = (int)(minh - (Math.cos((-Math.PI / 2d) - (blockedAngleEnd * DEG_TO_RAD)) * minh));
				lineEndY = (int)(minh + (Math.sin((-Math.PI / 2d) - (blockedAngleEnd * DEG_TO_RAD)) * minh));
				
				g.setColor(Color.RED);
				g.drawLine(minh + 20, minh + 20, 20 + lineEndX, 20 + lineEndY);				
			}
		}
		
		drawShadowedString(
			g,
			FONT_7,
			"inc: " + formatter.format(increment),
			getWidth() - 150,
			getHeight() - (15 * 8),
			Color.RED,
			Color.DARK_GRAY
		);
		
		drawShadowedString(
			g,
			FONT_7,
			"curr: " + formatter.format(currAngle) + "°",
			getWidth() - 150,
			getHeight() - (15 * 7),
			Color.RED,
			Color.DARK_GRAY
		);
		
		drawShadowedString(
			g,
			FONT_7,
			"to: " + formatter.format(toAngle) + "°",
			getWidth() - 150,
			getHeight() - (15 * 6),
			Color.BLUE,
			Color.DARK_GRAY
		);
		
		drawShadowedString(
			g,
			FONT_7,
			"cw_dist: " + formatter.format(cwdist) + "°",
			getWidth() - 150,
			getHeight() - (15 * 5),
			Color.GREEN,
			Color.DARK_GRAY
		);
		drawShadowedString(
			g,
			FONT_7,
			"acw_dist: " + formatter.format(acwdist) + "°",
			getWidth() - 150,
			getHeight() - (15 * 4),
			Color.YELLOW,
			Color.DARK_GRAY
		);		
		drawShadowedString(
			g,
			FONT_7,
			"delta: " + formatter.format(delta) + "°",
			getWidth() - 150,
			getHeight() - (15 * 3),
			Color.ORANGE,
			Color.DARK_GRAY
		);		
		drawShadowedString(
			g,
			FONT_7,
			"blocked_start: " + (Double.isNaN(blockedAngleStart) ? "N/A" : formatter.format(blockedAngleStart) + "°"),
			getWidth() - 150,
			getHeight() - (15 * 2),
			Color.RED,
			Color.DARK_GRAY
		);
		drawShadowedString(
			g,
			FONT_7,
			"blocked_end: " + ((Double.isNaN(blockedAngleStart) || Double.isNaN(blockedAngleEnd)) ? "N/A" : formatter.format(blockedAngleEnd) + "°"),
			getWidth() - 150,
			getHeight() - (15 * 1),
			Color.RED,
			Color.DARK_GRAY
		);
	}

	/* (non-Javadoc)
	 * @see ada.AppletGameCore#update(java.awt.Graphics2D)
	 */
	@Override
	public void update(Graphics2D g) {
		g.setBackground(Color.BLACK);
		super.update(g);
	}
	
	/* (non-Javadoc)
	 * @see ada.AppletGameCore#update(long)
	 */
	@Override
	public void update(long time) {
		distUpdate();
	}
	
	
	public static double normalAbsoluteAngleDegrees(double angle) {
		return (angle %= 360) >= 0 ? angle : (angle + 360);
	}
	
	public static void drawShadowedString(Graphics2D g2, Font f, String someText, int x, int y, Color forColor, Color offsetColor) {
		Font orig = g2.getFont();
		if (f != null) g2.setFont(f);
		
		float offset = 1;
		// set up x and y to be the coordinates for where you want the string
		g2.setColor(offsetColor); // usually a darker color
		g2.drawString(someText, x, y + offset);
		g2.drawString(someText, x + offset, y);
		g2.setColor(forColor);
		g2.drawString(someText, x, y);
		
		if (f != null) g2.setFont(orig);
	}
	
	public void distUpdate() {
		if ((lastMovementTime + (1000 / 60)) < System.currentTimeMillis()) {
			move(increment);
			lastMovementTime = System.currentTimeMillis();
		}
	}
	
	protected final double[] distancesTemp = new double[2];
	
	public double bestAngleDelta(double currAngle, double toAngle, double blockedAngleStart, double blockedAngleEnd, double[] distances) {
		if (currAngle == toAngle) return 0d;
		
		double cwdist  = Math.abs(
			currAngle < toAngle ? currAngle - toAngle : currAngle - (360d + toAngle)
		);
		double acwdist = Math.abs(
			currAngle < toAngle ? (360d + currAngle) - toAngle : toAngle - currAngle
		);
		if (distances != null) {
			distances[0] = cwdist;
			distances[1] = acwdist;
		}
		currAngle = normalAbsoluteAngleDegrees(currAngle);
		
		if (Double.isNaN(blockedAngleStart) || Double.isNaN(blockedAngleEnd)) {
			double delta = cwdist < acwdist ? cwdist : -acwdist;
			return delta;
		}
		
		// looking for b-start
		double tobstartd  = bestAngleDelta(
			currAngle, blockedAngleStart, Double.NaN, Double.NaN, __distancesTemp2
		);
		double bstartdcl  = __distancesTemp2[0];
		double bstartdacl = __distancesTemp2[1];
		
		// looking for b-end
		double tobendd = bestAngleDelta(
			currAngle, blockedAngleEnd, Double.NaN, Double.NaN, __distancesTemp2
		);
		double benddcl    = __distancesTemp2[0];
		double benddacl   = __distancesTemp2[1];		
		
		if (toAngle >= blockedAngleStart && toAngle <= blockedAngleEnd) {
			//System.out.println("hi");
			double delta = Math.abs(tobstartd) < Math.abs(tobendd) ? bstartdcl : -benddacl;
			return delta;
		}
		
		if (cwdist < bstartdcl) {
			System.out.println("hmm1");
			return tobstartd;
		}
		System.out.println("hmm2");
		return tobendd;
	}
	
	// this is used internally!
	protected final double[] __distancesTemp2 = new double[2];
	
	public void move(double increment) {
		delta   = bestAngleDelta(currAngle, toAngle, blockedAngleStart, blockedAngleEnd, distancesTemp);
		cwdist  = distancesTemp[0];
		acwdist = distancesTemp[1];
		if (Math.abs(delta) <= increment) {
			currAngle = toAngle;
			return;
		}
		currAngle += Math.signum(delta) * increment;
		currAngle = normalAbsoluteAngleDegrees(currAngle);
	}
	
	public void change() {
		toAngle = r.nextInt(360);
		System.out.println("changed to: " + toAngle);
	}
	
	public static void drawShadowedString(Graphics2D g2,String someText, int x, int y, Color forColor, Color offsetColor) {
		drawShadowedString(g2, null, someText, x, y, forColor, offsetColor);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		change();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		//change();
		
		int min = Math.min(getWidth(), getHeight());
		int minh = min / 2;
		
		//determine angle
		int x1 = minh;
		int y1 = minh;
		int x2 = e.getX();
		int y2 = e.getY();
		int dx = x2 - x1;
		int dy = y2 - y1;
		
		double angleInDegrees = (Math.atan2(dy, dx) * 180d / Math.PI) + 90d;
		double angle = normalAbsoluteAngleDegrees(angleInDegrees);
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			toAngle = angle;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			if (Double.isNaN(blockedAngleStart) || !Double.isNaN(blockedAngleEnd)) {
				blockedAngleStart = angle;
				blockedAngleEnd = Double.NaN;
			} else {
				blockedAngleEnd = angle;
			}
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			blockedAngleStart = Double.NaN;
			blockedAngleEnd   = Double.NaN;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    	increment += -(e.getWheelRotation() / 10d);
    	increment = Math.max(increment, 0d);
    }

}
