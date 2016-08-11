package io.xol.chunkstories.world.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import io.xol.chunkstories.api.csf.OfflineSerializedData;
import io.xol.chunkstories.api.entity.Entity;
import io.xol.chunkstories.api.entity.interfaces.EntityControllable;
import io.xol.chunkstories.api.entity.interfaces.EntityUnsaveable;
import io.xol.chunkstories.entity.EntitySerializer;
import io.xol.chunkstories.world.chunk.RegionImplementation;

//(c) 2015-2016 XolioWare Interactive
//http://chunkstories.xyz
//http://xol.io

public class CSFRegionFile implements OfflineSerializedData
{
	private RegionImplementation holder;
	private File file;

	//Locks modifications to the region untils it finishes saving.
	public AtomicInteger savingOperations = new AtomicInteger();

	public CSFRegionFile(RegionImplementation holder)
	{
		this.holder = holder;

		this.file = new File(holder.world.getFolderPath() + "/regions/" + holder.regionX + "." + holder.regionY + "." + holder.regionZ + ".csf");
	}

	public boolean exists()
	{
		return file.exists();
	}

	public void load() throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		int[] chunksSizes = new int[8 * 8 * 8];
		// First load the index
		for (int a = 0; a < 8 * 8 * 8; a++)
		{
			int size = in.read() << 24;
			size += in.read() << 16;
			size += in.read() << 8;
			size += in.read();
			chunksSizes[a] = size;
		}
		
		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++)
				for (int c = 0; c < 8; c++)
				{
					int size = chunksSizes[a * 8 * 8 + b * 8 + c];
					// if chunk present then create it's byte array
					// and
					// fill it
					if (size > 0)
					{
						byte[] buffer  = new byte[size];
						in.read(buffer, 0, size);
						holder.getChunkHolder(a, b, c).setCompressedData(buffer);
						// i++;
					}
				}
		
		/*
		//Lock the holder compressed chunks array !
		holder.compressedChunksLock.beginWrite();
		// Then load the chunks
		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++)
				for (int c = 0; c < 8; c++)
				{
					int size = chunksSizes[a * 8 * 8 + b * 8 + c];
					// if chunk present then create it's byte array
					// and
					// fill it
					if (size > 0)
					{
						holder.compressedChunks[a][b][c] = new byte[size];
						in.read(holder.compressedChunks[a][b][c], 0, size);
						// i++;
					}
				}
		//Unlock it immediatly afterwards
		holder.compressedChunksLock.endWrite();*/
		
		//We pretend it's loaded sooner so we can add the entities and they will load their chunks data if needed
		holder.setDiskDataLoaded(true);

		//don't tick the world entities until we get this straight
		holder.world.entitiesLock.lock();

		if (in.available() <= 0)
		{
			System.out.println("Old version file, no entities to be found anyway");
			in.close();

			holder.world.entitiesLock.unlock();
			return;
		}

		DataInputStream dis = new DataInputStream(in);

		//Read entities until we hit -1
		Entity entity = null;
		do
		{
			entity = EntitySerializer.readEntityFromStream(dis, this, holder.world);
			if (entity != null)
				holder.world.addEntity(entity);
		}
		while (entity != null);

		holder.world.entitiesLock.unlock();

		// System.out.println("read "+i+" compressed chunks");
		in.close();
	}

	public void save() throws IOException
	{
		file.getParentFile().mkdirs();
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		// int[] chunksSizes = new int[8*8*8];
		// First write the index
		
		byte[][][][] compressedVersions = new byte[8][8][8][];
		
		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++)
				for (int c = 0; c < 8; c++)
				{
					int chunkSize = 0;
					
					byte[] chunkCompressedVersion = holder.getChunkHolder(a, b, c).getCompressedData();
					if (chunkCompressedVersion != null)
					{
						//Save the reference to ensure coherence with later part
						compressedVersions[a][b][c] = chunkCompressedVersion;
						chunkSize = chunkCompressedVersion.length;
					}
					out.write((chunkSize >>> 24) & 0xFF);
					out.write((chunkSize >>> 16) & 0xFF);
					out.write((chunkSize >>> 8) & 0xFF);
					out.write((chunkSize >>> 0) & 0xFF);
				}
		// Then write said chunks
		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++)
				for (int c = 0; c < 8; c++)
				{
					if (compressedVersions[a][b][c] != null)
					{
						out.write(compressedVersions[a][b][c]);
					}
				}

		//don't tick the world entities until we get this straight
		holder.world.entitiesLock.lock();

		//System.out.println("writing region file of " + holder);

		DataOutputStream dos = new DataOutputStream(out);

		Iterator<Entity> holderEntities = holder.getEntitiesWithinRegion();
		while (holderEntities.hasNext())
		{
			Entity entity = holderEntities.next();
			//Don't save controllable entities
			if (entity.exists() && !(entity instanceof EntityUnsaveable && !((EntityUnsaveable)entity).shouldSaveIntoRegion()))
			{
				EntitySerializer.writeEntityToStream(dos, this, entity);
				//System.out.println("wrote " + entity);
			}
		}
		dos.writeLong(-1);

		//System.out.println("done");

		holder.world.entitiesLock.unlock();

		out.close();
	}

	public void finishSavingOperations()
	{
		//Waits out saving operations.
		while (savingOperations.get() > 0)
			//System.out.println(savingOperations.get());
			synchronized (this)
			{
				try
				{
					wait(20L);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
	}

}
