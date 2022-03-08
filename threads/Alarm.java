package nachos.threads;

import java.util.PriorityQueue;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {

	private class WaitKThread {
		public long wake;
		public KThread thread;

		public WaitKThread(long l, KThread k) {
			wake = l;
			thread = k;
		}

		public int compareTo(WaitKThread other) {
			return (int) (this.wake - other.wake);
		}
	}

	private PriorityQueue<WaitKThread> queue;
	private Lock alarmLock;

	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		queue = new PriorityQueue<WaitKThread>();
		alarmLock = new Lock();

		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		boolean intStatus = Machine.interrupt().disable();
		
		while(queue.peek()!= null && queue.peek().wake <= Machine.timer().getTime())
		{
			WaitKThread nextthread = queue.poll();
			nextthread.thread.ready();
		}
		
		Machine.interrupt().restore(intStatus);
		KThread.currentThread().yield();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x
	 *            the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		boolean intStatus = Machine.interrupt().disable();

		if (x < 0) {
			Machine.interrupt().restore(intStatus);
			return;
		} else if (x == 0) {
			KThread.yield();
			Machine.interrupt().restore(intStatus);
			return;
		} else {
			alarmLock.acquire();
			long wakeTime = Machine.timer().getTime() + x;
			WaitKThread currentthread = new WaitKThread(wakeTime,
					KThread.currentThread());
			queue.add(currentthread);
			alarmLock.release();

			KThread.sleep();

			Machine.interrupt().restore(intStatus);
			return;
		}
	}
}
