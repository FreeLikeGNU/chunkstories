//
// This file is a part of the Chunk Stories Implementation codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package io.xol.chunkstories.workers;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.xol.chunkstories.api.workers.Task;
import io.xol.chunkstories.api.workers.TaskExecutor;
import io.xol.chunkstories.api.workers.Tasks;

public class WorkerThreadPool extends TasksPool<Task> implements Tasks
{
	protected int threadsCount;
	protected WorkerThread[] workers;
	
	public WorkerThreadPool(int threadsCount)
	{
		this.threadsCount = threadsCount;
	}
	
	public void start() {
		workers = new WorkerThread[threadsCount];
		for(int id = 0; id < threadsCount; id++)
			workers[id] = spawnWorkerThread(id);
	}
	
	protected WorkerThread spawnWorkerThread(int id) {
		return new WorkerThread(this, id);
	}
	
	//Virtual task the reference is used to signal threads to end.
	protected Task DIE = new Task() {

		@Override
		protected boolean task(TaskExecutor whoCares)
		{
			return true;
		}
		
	};
	
	long tasksRan = 0;
	long tasksRescheduled = 0;
	
	void rescheduleTask(Task task)
	{
		tasksQueue.addLast(task);
		tasksCounter.release();
		
		tasksRescheduled++;
	}
	
	public String toString() {
		return "[WorkerThreadPool threadCount="+this.threadsCount+", tasksRan="+tasksRan+", tasksRescheduled="+tasksRescheduled+"]";
	}
	
	public String toShortString() {
		return "workers tc: "+this.threadsCount+", todo: "+submittedTasks()+"";
	}
	
	public void destroy()
	{
		//Send threadsCount DIE orders
		for(int i = 0; i < threadsCount; i++)
			this.scheduleTask(DIE);
	}

	@Override
	public int submittedTasks() {
		return this.tasksQueueSize.get();
	}

	public void dumpTasks() {
		System.out.println("dumping tasks");
		
		//Hardcoding a security because you can fill the queue faster than you can iterate it
		int hardLimit = 500;
		Iterator<Task> i = this.tasksQueue.iterator();
		while (i.hasNext())
		{
			Task task = i.next();
			hardLimit--;
			if(hardLimit < 0)
				return;
			System.out.println(task);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger("workers");
	public Logger logger() {
		return logger;
	}
}
