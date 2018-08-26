package utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Collection that holds a number of objects for a set period of time. The time is specified in the constructor.
 * Client code must run oneTick() for a set amount of times, in order for the counter to increment. Once the counter
 * exceeds or equals the waiting time, the objects can be returned by using pollFinished(). 
 * To look at the objects WITHOUT removing them, use peekFinished().
 * 
 * @author Alex Luckett
 * @version 10/04/2014
 */
public class WaitingList<T> {
	private ArrayList<WaitingItem> waitingItems;
	private final int timeToWait;
	
	/**
	 * Constructs a new WaitingList. 
	 * 
	 * @param timeToWait The time to wait before returning objects
	 */
	public WaitingList(int timeToWait) {
		waitingItems = new ArrayList<WaitingItem>();
		this.timeToWait = timeToWait;
	}
	
	/**
	 * Adds an object into the collection
	 * @param object The object being added to the collection
	 */
	public void add(T object) {
		waitingItems.add(new WaitingItem(object)); // create a new waiting item from the parameter, add to arraylist
	}
	
	public void remove(T object) { } // do not need to remove in this collection
	
	/**
	 * Runs WaitingList for one tick. Increments the waiting time of each object.
	 */
	public void oneTick() {
		for (WaitingItem currentItem : waitingItems) {
			currentItem.incrementWaitTime();
		}
	}
	
	/**
	 * Returns all the finished objects (ones that have been waiting for timeToWait)
	 * does NOT remove them from the WaitingList
	 * 
	 * @return ArrayList<T> containing the finished objects
	 */
	public ArrayList<T> peek() {
		ArrayList<T> finishedObjects = new ArrayList<T>();
		
		for(WaitingItem currentItem : waitingItems) {
			if(currentItem.getWaitTime() >= timeToWait) {
				finishedObjects.add((T) currentItem.getObject());
			}
		}
		
		return finishedObjects;
	}
	
	/**
	 * Returns all the finished objects (ones that have been waiting for timeToWait) 
	 * and removes them from the WaitingList
	 * 
	 * @return ArrayList<T> containing the finished objects
	 */
	public ArrayList<T> poll() {
		ArrayList<T> finishedObjects = peek();
		
		/*
		 * Iterate over the items in the waiting collection.
		 * If object is contained within the original, remove from collection
		 */
		Iterator<WaitingItem> iter = waitingItems.iterator();
		while (iter.hasNext()) {
			WaitingItem currentWaitItem = iter.next();
			Object currentObject = currentWaitItem.getObject();
		    if (finishedObjects.contains(currentObject)) {
		        iter.remove();
		    }
		}
		
		return finishedObjects;
	}

	/**
	 * Returns the number of items stored within the collection.
	 * @return size of the collection
	 */
	public int size() { 
		return waitingItems.size();
	}
	
	/**
	 * Returns the amount of ticks to wait before returning objects
	 * @return timeToWait
	 */
	public int getWaitingTime() {
		return timeToWait;
	}
	
	/**
	 * Returns the current waiting items that have not been pulled out (finished).
	 * Will not remove from waiting queue. Use pollFinished() for that.
	 *  
	 * @return ArrayList<T> contains the current waiting items
	 */
	public ArrayList<T> peekWaiting() {
		ArrayList<T> finishedObjects = new ArrayList<T>();
		
		for(WaitingItem currentItem : waitingItems) {
			finishedObjects.add((T) currentItem.getObject());
		}
		
		return finishedObjects;
	}
	
	
	/**
	 * An item to be used within the WaitingList collection. Intended use: stores an object
	 * and the amount of time it has been waiting in a certain place.
	 * 
	 * @author Alex Luckett
	 * @version 08/04/2014
	 */
	private class WaitingItem {
		private T object; // object to be contained within the WaitingItem
		private int waitTime; // amount of time it has been waiting in the WaitingList

		public WaitingItem(T object) {
			this.object = object;
			this.waitTime = 0;
		}

		public void incrementWaitTime() {
			waitTime++;
		}

		public T getObject() {
			return object;
		}

		public int getWaitTime() {
			return waitTime;
		}
	}
}