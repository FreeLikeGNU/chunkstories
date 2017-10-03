package io.xol.chunkstories.content;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import io.xol.chunkstories.api.exceptions.content.mods.NotAllModsLoadedException;
import io.xol.chunkstories.api.mods.ModsManager;
import io.xol.chunkstories.content.ModsManagerImplementation.NonExistentCoreContent;

//(c) 2015-2017 XolioWare Interactive
//http://chunkstories.xyz
//http://xol.io

public class ModsManagerTest {
	
	@Test public void testModsManager()
	{
		try {
			ModsManager modsManager = new ModsManagerImplementation(new File("res/"));
			modsManager.loadEnabledMods();
		} catch (NonExistentCoreContent e) {
			e.printStackTrace();
			fail();
		} catch (NotAllModsLoadedException e) {
			e.printStackTrace();
		}
		
		
	}
}