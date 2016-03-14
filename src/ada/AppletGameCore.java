package ada;
import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;

/**
 *AppletGameCore.java
 *@author David Graham
 */

public abstract class AppletGameCore extends Applet implements Runnable
{
	private BufferStrategy bufferStrategy;
	protected Canvas drawArea;/*Drawing Canvas*/
	private boolean stopped = false;/*True if the applet has been destroyed*/
	private int x = 0;

	public void init()
	{
		Thread t = new Thread(this);
		drawArea = new Canvas();
		setIgnoreRepaint(true);
		t.start();
	}

	public void destroy()
	{
		stopped = true;

		/*Allow Applet to destroy any resources used by this applet*/
		super.destroy();
	}

	public void update()
	{
		if(!bufferStrategy.contentsLost())
		{
			//Show bufferStrategy
			bufferStrategy.show();
		}
	}

	//Return drawArea's BufferStrategy
	public BufferStrategy getBufferStrategy()
	{
		return bufferStrategy;
	}

	//Create drawArea's BufferStrategies
	public void createBufferStrategy(int numBuffers)
	{
		drawArea.createBufferStrategy(numBuffers);
	}

	//Subclasses should override this method to do any drawing
	public abstract void draw(Graphics2D g);

	public void update(Graphics2D g)
	{
		g.setColor(g.getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
	}

	//Update any sprites, images, or primitives
	public abstract void update(long time);

	public Graphics2D getGraphics()
	{
		return (Graphics2D)bufferStrategy.getDrawGraphics();
	}

	//Do not override this method      
	public void run()
	{
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(ComponentEvent e) {
				drawArea.setSize(new Dimension(getWidth(),getHeight()));
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});

		drawArea.setSize(new Dimension(getWidth(),getHeight()));
		add(drawArea);
		createBufferStrategy(2);
		bufferStrategy = drawArea.getBufferStrategy();

		long startTime = System.currentTimeMillis();
		long currTime = startTime;

		//animation loop
		while(!stopped)
		{
			//Get time past
			long elapsedTime = System.currentTimeMillis()-currTime;
			currTime += elapsedTime;

			//Flip or show the back buffer
			update();

			//Update any sprites or other graphical objects
			update(elapsedTime);

			//Handle Drawing
			Graphics2D g = getGraphics();
			update(g);
			draw(g);

			//Dispose of graphics context
			g.dispose();

			if (!stopped) {
				try {
					Thread.sleep(1000/30);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
				}
			}
		}

	}
}
