//
// This file is a part of the Chunk Stories Implementation codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package io.xol.engine.concurrency;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A queue that garantees uniqueness AND order while still being concurrent access
 * @author Gobrosse
 *
 * @param <T>
 */
public class UniqueQueue<T> implements Queue<T>
{
	Queue<T> internalQueue = new ConcurrentLinkedQueue<T>();
	Set<T> internalSet = ConcurrentHashMap.newKeySet();

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		for(T e : c)
			add(e);
		return true;
	}

	@Override
	public void clear()
	{
		internalQueue.clear();
		internalSet.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return internalSet.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		for(Object e : c)
			if(!internalSet.contains(e))
				return false;
		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return internalSet.size() == 0;
	}

	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
				{
					Iterator<T> i = internalSet.iterator();
				
					T o;
					
					@Override
					public boolean hasNext()
					{
						return i.hasNext();
					}

					@Override
					public T next()
					{
						o = i.next();
						return o;
					}
					
					@Override
					public void remove()
					{
						i.remove();
						internalQueue.remove(o);
					}
				};
	}

	@Override
	public boolean remove(Object o)
	{
		if(internalSet.remove(o))
			return internalQueue.remove(o); //True
		//System.out.println("Did not contain "+o);
		//Thread.dumpStack();
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		for(Object e : c)
		{
			if(internalSet.remove(e))
				internalQueue.remove(e);
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		//TODO implement ( but osef )
		throw new UnsupportedOperationException();
		//return false;
	}

	@Override
	public int size()
	{
		/*if(internalSet.size() != internalQueue.size())
		{
			System.out.println("Critical error : UniqueQueue incoherence ( size differs between backing set and queue : "+internalSet.size()+" != "+ internalQueue.size()+")");
			Runtime.getRuntime().exit(-4);
		}*/
		return internalSet.size();
	}

	@Override
	public Object[] toArray()
	{
		return internalQueue.toArray();
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] a)
	{
		return (T[]) internalQueue.toArray();
	}

	@Override
	public boolean add(T e)
	{
		if(internalSet.add(e))
			return internalQueue.add(e);
		return false;
	}

	@Override
	public T element()
	{
		return internalQueue.element();
	}

	@Override
	public boolean offer(T e)
	{
		boolean rslt = internalSet.add(e);
		if(rslt)
			return internalQueue.offer(e);
		return false;
	}

	@Override
	public T peek()
	{
		return internalQueue.peek();
	}

	@Override
	public T poll()
	{
		T o = internalQueue.poll();
		if(o != null)
			internalSet.remove(o);
		return o;
	}

	@Override
	public T remove()
	{
		T o = internalQueue.remove();
		if(o != null)
			internalSet.remove(o);
		return o;
	}

}
