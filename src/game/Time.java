package game;

public class Time {

	private int hour;
	private int minute;
	private int second;
	private long start;

	public Time() {
		start = System.currentTimeMillis();
	}

	public void update() {
		second = (int) ((System.currentTimeMillis() - start) / 1000);
		if (second >= 60) {
			start = System.currentTimeMillis();
			minute++;
			if (minute >= 60) {
				hour++;
				if (hour >= 24) {
					hour = 0;
				}
				minute = 0;
			}
			second = 0;
		}
		// System.out.println(hour + ":" + minute + ":" + second);
		//System.out.printf("%02d:%02d:%02d\n", hour, minute, second);
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public static void main(String[] args) {
		new Time();
	}

}
