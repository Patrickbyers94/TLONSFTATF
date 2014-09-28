package game;

import org.jdom2.Element;
import java.util.*;

/**
 * Class used to represent the player's chosen job. Each job
 * (Archer/Soldier/...) determines how the user attacks and partly what damage
 * is dealt/taken.
 */

public class Job {

	private String name;
	private int attack;
	private int range;
	private int health;

	public Job() {

	}

	public Job(String name, int attack, int range, int health) {
		this.name = name;
		this.attack = attack;
		this.range = range;
		this.health = health;
	}

	public String getName() {
		return name;
	}

	public int getAttack() {
		return attack;
	}

	public int getRange() {
		return range;
	}

	public int getHealth() {
		return health;
	}

	public static Job Archer() {
		return new Job("Archer", 2, 4, 40);
	}

	public static Job Soldier() {
		return new Job("Soldier", 4, 1, 60);
	}

	public static List<Job> getJobs() {
		List<Job> j = new ArrayList<Job>();
		j.add(Job.Soldier());
		j.add(Job.Archer());
		return j;
	}

	public String toString() {
		return this.name;
	}

	/**
	 * Create an XML Element that represents this job
	 * 
	 * @return This Job as an Element
	 */
	public Element toElement() {
		Element me = new Element("job").setText(name);
		return me;
	}

	/**
	 * Create a Job from an XML Element
	 * 
	 * @param e
	 *            The Element to make a Job from
	 * @return The Job from Element e
	 */
	public static Job fromElement(Element e) {
		if (e.getText().equals("Archer"))
			return Archer();
		if (e.getText().equals("Soldier"))
			return Soldier();

		return null; // Shouldn't get here
	}

}
