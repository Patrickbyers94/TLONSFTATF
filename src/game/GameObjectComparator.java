package game;

import java.util.Comparator;

public class GameObjectComparator implements Comparator<GameObject> {

	/**
	 * Compares GameObjects by their Y positions so we can draw them nicely behind or infront of each other in a faux pseudo ersatz Z Ordering System
	 */
	public GameObjectComparator(){

	}

	@Override
	public int compare(GameObject o1, GameObject o2) {
		if(o1.getPosition().getRow()< o2.getPosition().getRow()){
			return -1;
		}else if(o1.getPosition().getRow() > o2.getPosition().getRow()){
			return 1;
		}else{
			return 0;
		}
	}

}
