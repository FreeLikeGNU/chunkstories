//
// This file is a part of the Chunk Stories Implementation codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package io.xol.chunkstories.content.mods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.xol.chunkstories.api.exceptions.content.mods.MalformedModTxtException;
import io.xol.chunkstories.api.content.mods.Mod;
import io.xol.chunkstories.api.content.mods.ModInfo;
import io.xol.chunkstories.materials.GenericConfigurable;

public class ModInfoImplementation extends GenericConfigurable implements ModInfo
{
	private Mod mod;
	
	private final String internalName;
	private String name;
	private String version;// = "undefined";
	private String description;// = "No description given";
	
	public Mod getMod()
	{
		return mod;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
	
	public ModInfoImplementation(Mod mod, InputStream inputStream) throws MalformedModTxtException
	{
		super();
		
		if(inputStream == null)
			throw new MalformedModTxtException(this);
		
		try {
			load(new BufferedReader(new InputStreamReader(inputStream)));
		}
		catch(IOException e) {
			throw new MalformedModTxtException(this);
		}
		
		this.internalName = this.resolveProperty("internalName", null);
		this.name = this.resolveProperty("name", "<internalName>");
		this.version = this.resolveProperty("version", "1.0");
		this.description = this.resolveProperty("description", "Please provide a description in your mod.txt");

		//Requires a name to be set, at least
		if(this.internalName == null)
			throw new MalformedModTxtException(this);
	}

	@Override
	public String getInternalName() {
		return internalName;
	}
}
