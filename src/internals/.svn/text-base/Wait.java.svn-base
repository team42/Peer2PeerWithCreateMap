package internals;

public class Wait {
	
	public static void milliSec(double d) {
		try {
			Thread.currentThread();
			Thread.sleep((long) d);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void forever() {
		try {
			Thread.currentThread();
			while(true){
				Thread.sleep(Long.MAX_VALUE);	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}