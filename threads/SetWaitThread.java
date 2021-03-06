package nachos.threads;

public class SetWaitThread implements Runnable {
	public void run() {
		System.out.println("Calling -1");
        ThreadedKernel.alarm.waitUntil(-1);
        System.out.println("Returned");
        
        System.out.println("Calling 0");
        ThreadedKernel.alarm.waitUntil(0);
        System.out.println("Returned");
        
        System.out.println("Calling 5000");
        ThreadedKernel.alarm.waitUntil(5000);
        System.out.println("Returned");
	}
}