import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class WelcomeScreen extends Screen
{
    public static Image img = ImageLoader.loadCompatibleImage("mario.png");
	private int x, y;
	
	public WelcomeScreen(GameState s, int w, int h) {
		super(s, w, h);
	}
	
	public void render(Graphics g) {
		
		g.setFont(new Font("Geneva", Font.BOLD, 42));
		g.setColor(Color.BLUE);
        
        g.drawImage(img, 0, 0, null);
        
		g.drawString("Press Enter to Play", x, y);
	}
	
	public void update() {
				
		x++;
		y++;
		
		if(x > width)
			x = 0;
		
		if(y > height)
			y = 0;
	}
	
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_ENTER)
			state.switchToGameScreen();
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