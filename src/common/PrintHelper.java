package common;

public class PrintHelper {
	public static void printInterval(int current, int total, int printCount, String text) {
		if (Math.floorMod(current, total / printCount) == 0) {
			System.out.println(text);
		}
	}
}
