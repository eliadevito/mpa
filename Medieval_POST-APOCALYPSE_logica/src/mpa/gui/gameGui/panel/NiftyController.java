package mpa.gui.gameGui.panel;

import mpa.gui.gameGui.GameGui;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class NiftyController implements ScreenController
{

	private Nifty nifty;
	private GameGui gameGui;

	public NiftyController(GameGui gameGui)
	{
		this.gameGui = gameGui;

	}

	public void onClickButtonOccupy()
	{

		gameGui.occupy();

	}

	public void onStartScreen()
	{
	}

	public void onEndScreen()
	{
	}

	@Override
	public void bind(Nifty arg0, Screen arg1)
	{

	}
}