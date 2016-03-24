import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class GameScreen extends Screen
{
		
	public GameScreen(GameState s, int w, int h) {
		super(s, w, h);
		
	}
	
	public void render(Graphics g) {
		
	}
	
	public void update() {

	}
	
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_Q)
			state.switchToWelcomeScreen();
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void mousePressed(Point2D p)
	{
	}
	public void mouseReleased(Point2D p)
	{
	}
	public void mouseMoved(Point2D p)
	{
	}
	public void mouseDragged(Point2D p)
	{
	}
}