package com.codepath.caltraindating.models;

import java.util.ArrayList;
import com.codepath.caltraindating.models.Callback;

public class MultiAsyncExecutor {
	private int taskCount = 0;
	private ArrayList<Task>  tasks = new ArrayList<Task>();
	private OnAllComplete complete = null;

	
	public interface OnAllComplete{
		void complete();
	}
	
	public abstract class Task{

		public abstract void start();

		public void complete() {
			completeItem();
		}
		
	}

	public MultiAsyncExecutor(OnAllComplete complete){
		this.complete = complete;
	}
	
	public void completeItem(){
		taskCount--;
		if(taskCount <= 0 && complete != null){
			complete.complete();
		}
	}
	
	
	public void addTask(Task task){
		tasks.add(task);
	}
	
	public void executeAll(){
		for(Task t: tasks){
			taskCount++;
			t.start();
		}
	}
}
