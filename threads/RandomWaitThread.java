package nachos.threads;

public class RandomWaitThread implements Runnable {
	public void run() {
		ThreadedKernel.alarm.waitUntil(5000);
	}
}