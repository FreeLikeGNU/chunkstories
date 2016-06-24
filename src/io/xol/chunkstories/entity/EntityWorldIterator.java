package io.xol.chunkstories.entity;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import io.xol.chunkstories.api.entity.Entity;

public class EntityWorldIterator implements Iterator<Entity>
{
	BlockingQueue<Entity> entities;
	Iterator<Entity> ie;
	Entity currentEntity;

	public EntityWorldIterator(BlockingQueue<Entity> entities)
	{
		this.entities = entities;
		ie = entities.iterator();
	}

	@Override
	public boolean hasNext()
	{
		return ie.hasNext();
	}

	@Override
	public Entity next()
	{
		currentEntity = ie.next();
		return currentEntity;
	}

	@Override
	public void remove()
	{
		//Remove it from the world set
		ie.remove();
	}
}