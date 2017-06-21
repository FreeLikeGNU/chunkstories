package io.xol.chunkstories.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.xol.chunkstories.api.client.ClientContent;
import io.xol.chunkstories.api.client.ClientInputsManager;
import io.xol.chunkstories.api.client.ClientInterface;
import io.xol.chunkstories.api.client.ClientRenderingConfig;
import io.xol.chunkstories.api.client.ClientSoundManager;
import io.xol.chunkstories.api.item.inventory.Inventory;
import io.xol.chunkstories.api.particles.ParticlesManager;
import io.xol.chunkstories.api.player.Player;
import io.xol.chunkstories.api.player.PlayerClient;
import io.xol.chunkstories.api.plugin.PluginManager;
import io.xol.chunkstories.api.rendering.GameWindow;
import io.xol.chunkstories.api.rendering.effects.DecalsManager;
import io.xol.chunkstories.api.server.PermissionsManager;
import io.xol.chunkstories.api.server.ServerInterface;
import io.xol.chunkstories.api.util.ChunkStoriesLogger;
import io.xol.chunkstories.api.util.ConfigDeprecated;
import io.xol.chunkstories.api.util.IterableIterator;
import io.xol.chunkstories.api.world.WorldClient;
import io.xol.chunkstories.world.WorldClientLocal;

/**
 * Bridges over Client and Server
 * TODO: Make this share behaviour with an actual Server
 */
public class LocalServerContext implements ClientInterface, ServerInterface
{
	private final Client client;
	private final WorldClientLocal world;
	
	private PermissionsManager permissionsManager;
	
	public LocalServerContext(Client client)
	{
		this.client = client;
		this.world = (WorldClientLocal) client.getWorld();
		
		this.permissionsManager = new PermissionsManager() {

			@Override
			public boolean hasPermission(Player player, String permissionNode)
			{
				//if (UsersPrivileges.isUserAdmin(player.getName()))
				//	return true;
				return true;
			}
			
		};
	}
	
	@Override
	public ClientContent getContent()
	{
		return client.getContent();
	}

	@Override
	public PluginManager getPluginManager()
	{
		return client.getPluginManager();
	}

	@Override
	public void print(String message)
	{
		client.print(message);
	}

	@Override
	public IterableIterator<Player> getConnectedPlayers()
	{
		Set<Player> players = new HashSet<Player>();
		players.add(Client.getInstance().getPlayer());
			
		return new IterableIterator<Player>()
				{
					Iterator<Player> i = players.iterator();
					@Override
					public boolean hasNext()
					{
						return i.hasNext();
					}
					@Override
					public Player next()
					{
						return i.next();
					}
					@Override
					public Iterator<Player> iterator()
					{
						return this;
					}
			
				};
	}

	@Override
	public Player getPlayerByName(String string)
	{
		return world.getPlayerByName(string);
	}

	@Override
	public Player getPlayerByUUID(long UUID)
	{
		if((long)Client.username.hashCode() == UUID)
			return Client.getInstance().getPlayer();
		
		System.out.println("player by uuid not found"+UUID);
		return null;
	}

	@Override
	public void broadcastMessage(String message)
	{
		printChat(message);
	}

	@Override
	public WorldClientLocal getWorld()
	{
		return (WorldClientLocal) Client.getInstance().getWorld();
	}

	@Override
	public PlayerClient getPlayer()
	{
		return Client.getInstance().getPlayer();
	}

	@Override
	public void printChat(String textToPrint)
	{
		client.printChat(textToPrint);
	}

	@Override
	public void changeWorld(WorldClient world)
	{
		client.changeWorld(world);
	}

	@Override
	public void exitToMainMenu()
	{
		client.exitToMainMenu();
	}

	@Override
	public void exitToMainMenu(String errorMessage)
	{
		client.exitToMainMenu(errorMessage);
	}

	@Override
	public void openInventories(Inventory... otherInventory)
	{
		client.openInventories(otherInventory);
	}

	@Override
	public boolean hasFocus()
	{
		return client.hasFocus();
	}

	@Override
	public void reloadAssets()
	{
		client.reloadAssets();
	}

	@Override
	public ClientSoundManager getSoundManager()
	{
		return client.getSoundManager();
	}

	@Override
	public ClientInputsManager getInputsManager()
	{
		return client.getInputsManager();
	}

	@Override
	public ParticlesManager getParticlesManager()
	{
		return client.getParticlesManager();
	}

	@Override
	public DecalsManager getDecalsManager()
	{
		return client.getDecalsManager();
	}

	@Override
	public GameWindow getGameWindow()
	{
		return client.getGameWindow();
	}

	@Override
	public PermissionsManager getPermissionsManager()
	{
		// TODO Auto-generated method stub
		// TODO Grant-all permissions system ?
		return null;
	}

	@Override
	public void installPermissionsManager(PermissionsManager permissionsManager)
	{
		
	}

	@Override
	public ChunkStoriesLogger logger() {
		return client.logger();
	}

	@Override
	public String username() {
		return client.username();
	}

	@Override
	public ConfigDeprecated configDeprecated() {
		return client.configDeprecated();
	}

	@Override
	public ClientRenderingConfig renderingConfig() {
		return client.renderingConfig();
	}

}