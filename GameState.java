import java.util.*;

public class GameState
{	
	private ArrayList<Screen> screens = new ArrayList<Screen>();
	private int indexOfCurrentScreen = 0;
	
	public GameState(int width, int height) {
		
		//create all the screens
		//Ex:
		//screens.add(new WelcomeScreen(this, width, height));
		//screens.add(new GameScreen(this, width, height));
		//screens.add(new GameOverScreen(this, width, height));
		
		screens.add(new WelcomeScreen(this, width, height));
		screens.add(new GameScreen(this, width, height));
	}
	
	public Screen currentActiveScreen() {
		return screens.get(indexOfCurrentScreen);
	}
	
	//methods that change which screen is currently showing
	//public void switchTo*()...
	//public void is*()...
	
	public void switchToWelcomeScreen() {
		indexOfCurrentScreen = 0;
		screens.get(indexOfCurrentScreen).start();
	}
	
	public void switchToGameScreen() {
		indexOfCurrentScreen = 1;
		screens.get(indexOfCurrentScreen).start();
	}
	
	public boolean isWelcomeScreen() {
		return indexOfCurrentScreen == 0;
	}
	
	public boolean isGameScreen() {
		return indexOfCurrentScreen == 1;
	}
	
}