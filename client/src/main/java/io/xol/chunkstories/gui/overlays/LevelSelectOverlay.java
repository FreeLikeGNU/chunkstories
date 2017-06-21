package io.xol.chunkstories.gui.overlays;

//(c) 2015-2017 XolioWare Interactive
// http://chunkstories.xyz
// http://xol.io

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.xol.chunkstories.client.Client;
import io.xol.chunkstories.content.GameDirectory;
import io.xol.chunkstories.gui.OverlayableScene;
import io.xol.chunkstories.world.WorldInfoImplementation;
import io.xol.chunkstories.world.WorldClientLocal;
import io.xol.engine.graphics.RenderingContext;
import io.xol.engine.graphics.fonts.BitmapFont;
import io.xol.engine.graphics.fonts.FontRenderer2;
import io.xol.engine.gui.LocalWorldButton;
import io.xol.engine.gui.Overlay;
import io.xol.engine.gui.elements.Button;

public class LevelSelectOverlay extends Overlay
{
	//GuiElementsHandler guiHandler = new GuiElementsHandler();
	Button backOption = new Button(0, 0, 300, 32, ("Back"), BitmapFont.SMALLFONTS, 1);
	Button newWorldOption = new Button(0, 0, 300, 32, ("New..."), BitmapFont.SMALLFONTS, 1);
	List<WorldInfoImplementation> localWorlds = new ArrayList<WorldInfoImplementation>();
	List<LocalWorldButton> worldsButtons = new ArrayList<LocalWorldButton>();

	public LevelSelectOverlay(OverlayableScene scene, Overlay parent)
	{
		super(scene, parent);
		// Gui buttons
		guiHandler.add(backOption);
		guiHandler.add(newWorldOption);
		File worldsFolder = new File(GameDirectory.getGameFolderPath() + "/worlds");
		if(!worldsFolder.exists())
			worldsFolder.mkdir();
		for (File f : worldsFolder.listFiles())
		{
			File infoTxt = new File(f.getAbsolutePath() + "/info.txt");
			if (infoTxt.exists())
			{
				localWorlds.add(new WorldInfoImplementation(infoTxt, f.getName()));
			}
		}
		for (WorldInfoImplementation wi : localWorlds)
		{
			LocalWorldButton worldButton = new LocalWorldButton(0, 0, wi);
			// System.out.println(worldButton.toString());
			worldButton.height = 64 + 8;
			guiHandler.add(worldButton);
			worldsButtons.add(worldButton);
		}
	}

	int scroll = 0;

	@Override
	public void drawToScreen(RenderingContext renderingContext, int x, int y, int w, int h)
	{
		if (scroll < 0)
			scroll = 0;

		int posY = renderingContext.getWindow().getHeight() - 128;
		FontRenderer2.drawTextUsingSpecificFont(64, posY + 64, 0, 48, "Select a level ...", BitmapFont.SMALLFONTS);
		int remainingSpace = (int)Math.floor(renderingContext.getWindow().getHeight()/96 - 2);
		
		while(scroll + remainingSpace > worldsButtons.size())
			scroll--;
		
		int skip = scroll;
		for (LocalWorldButton worldButton : worldsButtons)
		{
			if(skip-- > 0)
				continue;
			if(remainingSpace-- <= 0)
				break;
			if (worldButton.clicked())
			{
				Client.getInstance().changeWorld(new WorldClientLocal(Client.getInstance(), worldButton.info));
				//Client.world.startLogic();
				//this.mainScene.eng.changeScene(new GameplayScene(mainScene.eng, false));
			}
			int maxWidth = renderingContext.getWindow().getWidth() - 64 * 2;
			worldButton.width = maxWidth;
			worldButton.setPosition(64 + worldButton.width / 2, posY);
			worldButton.draw();
			posY -= 96;
		}

		backOption.setPosition(x + 192, 48);
		backOption.draw();
		
		newWorldOption.setPosition(renderingContext.getWindow().getWidth() - 192, 48);
		newWorldOption.draw();

		if (backOption.clicked())
		{
			this.mainScene.changeOverlay(this.parent);
		}
		if (newWorldOption.clicked())
		{
			this.mainScene.changeOverlay(new LevelCreateOverlay(this.mainScene, this));
		}
	}

	@Override
	public boolean handleKeypress(int k)
	{
		return false;
	}
	
	@Override
	public boolean onScroll(int dx)
	{
		if(dx < 0)
			scroll++;
		else
			scroll--;
		return true;
	}

	@Override
	public boolean onClick(int posx, int posy, int button)
	{
		if (button == 0)
			guiHandler.handleClick(posx, posy);
		return true;
	}
}