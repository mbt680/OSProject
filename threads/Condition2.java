package nachos.threads;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 * 
 * <p>
 * You must implement this.
 * 
 * @see nachos.threads.Condition
 */
public class Condition2 {

	private Lock conditionLock;
	private ThreadQueue waitQueue = null;

	/**
	 * Allocate a new condition variable.
	 * 
	 * @param conditionLock
	 *            the lock associated with this condition variable. The current
	 *            thread must hold this lock whenever it uses <tt>sleep()</tt>,
	 *            <tt>wake()</tt>, or <tt>wakeAll()</tt>.
	 */
	public Condition2(Lock conditionLock) {
		this.conditionLock = conditionLock;
		this.waitQueue = ThreadedKernel.scheduler.newThreadQueue(true);
	}

	/**
	 * Atomically release the associated lock and go to sleep on this condition
	 * variable until another thread wakes it using <tt>wake()</tt>. The current
	 * thread must hold the associated lock. The thread will automatically
	 * reacquire the lock before <tt>sleep()</tt> returns.
	 */
	public void sleep() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		boolean restoreStatus = Machine.interrupt().disable();
		waitQueue.waitForAccess(KThread.currentThread());

		conditionLock.release();

		KThread.sleep();

		conditionLock.acquire();
		Machine.interrupt().restore(restoreStatus);
	}

	/**
	 * Wake up at most one thread sleeping on this condition variable. The
	 * current thread must hold the associated lock.
	 */
	public void wake() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		boolean restoreStatus = Machine.interrupt().disable();
		// The next thread to wake up from the waitQueue
		KThread wakeThread = waitQueue.nextThread();
		// if the queue is not empty, wake up the retrieved thread
		if (wakeThread != null) {
			wakeThread.ready();
		}
		Machine.interrupt().restore(restoreStatus);
	}

	/**
	 * Wake up all threads sleeping on this condition variable. The current
	 * thread must hold the associated lock.
	 */
	public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		boolean restoreStatus = Machine.interrupt().disable();
		// The next thread to wake up from the waitQueue
		KThread wakeThread;
		do {
			wakeThread = waitQueue.nextThread();
			// if the queue is not empty, wake up the retrieved thread
			if (wakeThread != null) {
				wakeThread.ready();
			}
		} while (wakeThread != null);
		Machine.interrupt().restore(restoreStatus);

	}

	public static void selfTest() {
		System.out.println("Condtion Test Start");

		final Lock testLock = new Lock();
		final Condition2 testCon = new Condition2(testLock);

		KThread car1 = new KThread(new Runnable() {
			public void run() {
				testLock.acquire();
				System.out.println("going to sleep on condition");
				testCon.sleep();
				System.out.println("woke up");
				testLock.release();
			}

		});
		KThread car2 = new KThread(new Runnable() {
			public void run() {
				testLock.acquire();
				System.out.println("going to sleep on condition");
				testCon.sleep();
				System.out.println("woke up");
				testLock.release();

			}

		});
		KThread car3 = new KThread(new Runnable() {
			public void run() {
				testLock.acquire();
				System.out.println("going to sleep on condition");
				testCon.sleep();
				System.out.println("woke up");
				testLock.release();

			}

		});
		KThread car4 = new KThread(new Runnable() {
			public void run() {
				testLock.acquire();
				System.out.println("gonna wake up 1");
				testCon.wake();

				testLock.release();

			}

		});
		KThread car5 = new KThread(new Runnable() {
			public void run() {
				testLock.acquire();
				System.out.println("gonna wake up the rest");
				testCon.wakeAll();

				testLock.release();

			}

		});
		car1.fork();
		car2.fork();
		car3.fork();
		car4.fork();

		// System.out.println("join stuck");
		car1.join();
		car5.fork();
		car3.join();
		System.out.println("Condtion Test End");
	}

}
