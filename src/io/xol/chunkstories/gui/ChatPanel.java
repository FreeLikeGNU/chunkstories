package io.xol.chunkstories.gui;

//(c) 2015-2016 XolioWare Interactive
// http://chunkstories.xyz
// http://xol.io

import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.TrueTypeFont;
import io.xol.engine.gui.InputText;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector4f;

import io.xol.chunkstories.client.Client;

public class ChatPanel
{
	int chatHistorySize = 150;
	//String[] chatHistory = new String[chatHistorySize];
	InputText inputBox = new InputText(0, 0, 500, 32, BitmapFont.SMALLFONTS);
	public boolean chatting = false;

	Deque<ChatLine> chat = new ArrayDeque<ChatLine>();

	class ChatLine
	{
		public ChatLine(String text)
		{
			this.text = text;
			time = System.currentTimeMillis();
		}

		public long time;
		public String text;

		public void clickRelative(int x, int y)
		{

		}
	}

	public void key(int k)
	{
		if (k == 28)
		{
			chatting = false;
			if (inputBox.text.equals("/clear"))
			{
				//java.util.Arrays.fill(chatHistory, "");
				chat.clear();
				return;
			}
			if (inputBox.text.startsWith("/loctime"))
			{
				try
				{
					int time = Integer.parseInt(inputBox.text.split(" ")[1]);
					Client.world.worldTime = time;
				}
				catch (Exception e)
				{

				}
				return;
			}
			if (Client.connection != null)
				Client.connection.sendTextMessage("chat/" + inputBox.text);
			else
				insert("#00CC22" + Client.username + "#FFFFFF > " + inputBox.text);
			inputBox.text = "";

		}
		else if (k == 1)
			chatting = false;
		else
			inputBox.input(k);

	}

	public void openChatbox()
	{
		inputBox.text = "";
		chatting = true;
	}

	public void update()
	{
		String m;
		if (Client.connection != null)
			while ((m = Client.connection.getLastChatMessage()) != null)
				insert(m);
		if (!chatting)
			inputBox.text = "<Press T to chat>";
		inputBox.focus = true;
	}

	public void draw()
	{
		while (chat.size() > chatHistorySize)
			chat.removeLast();
		int linesDrew = 0;
		int maxLines = 14;
		Iterator<ChatLine> i = chat.iterator();
		while (linesDrew < maxLines && i.hasNext())
		{
			//if (a >= chatHistorySize - lines)
			ChatLine line = i.next();
			//System.out.println("added" +line.text);
			int actualLines = TrueTypeFont.arial12.getLinesHeight(line.text, 250);
			linesDrew += actualLines;
			float a = (line.time + 10000L - System.currentTimeMillis()) / 1000f;
			if (a < 0)
				a = 0;
			if (a > 1 || chatting)
				a = 1;
			//FontRenderer2.drawTextUsingSpecificFont(9, (linesDrew + 0 * maxLines - 1) * 24 + 100 + (chatting ? 50 : 0), 0, 32, line.text, BitmapFont.SMALLFONTS, a);
			//TrueTypeFont.arial12.drawString(9, (-linesDrew + 1) * 24 + 100 + (chatting ? 50 : 0), line.text, 2, 2, 500, new Vector4f(1,1,1,a));
			TrueTypeFont.arial12.drawStringWithShadow(9, (linesDrew - 1) * 26 + 100 + (chatting ? 50 : 0), line.text, 2, 2, 500, new Vector4f(1,1,1,a));
		}
		inputBox.setPos(12, 112);
		if (chatting)
			inputBox.drawWithBackGroundTransparent();

	}

	public void insert(String t)
	{
		chat.addFirst(new ChatLine(t));
	}
}
