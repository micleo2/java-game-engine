import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public abstract class Screen
{
	protected GameState state;
	protected int width, height;
	
	public Screen(GameState s, int w, int h) {
		state = s;
		width = w;
		height = h;
	}
	public void start(){}
	public abstract void render(Graphics g);
	public abstract void update();
	
	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
	
	public abstract void mousePressed(Point2D p);
	public abstract void mouseReleased(Point2D p);
	public abstract void mouseMoved(Point2D p);
	public abstract void mouseDragged(Point2D p);
}