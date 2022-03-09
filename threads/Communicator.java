package nachos.threads;

import java.util.Random;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {

	private boolean messagewaiting;
	private int message;
	private int listners;
	private Lock lock;

	private Condition2 waitspeak;
	private Condition2 waitlisten;

	/**
	 * Allocate a new communicator.
	 */
	public Communicator() {
		messagewaiting = false;
		message = 0;
		listners = 0;
		lock = new Lock();

		waitspeak = new Condition2(lock);
		waitlisten = new Condition2(lock);
	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 * 
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 * 
	 * @param word
	 *            the integer to transfer.
	 */
	public void speak(int word) {
		System.out.println("Getting" + word);
		
		lock.acquire();

		if (listners == 0)
			waitspeak.sleep();

		while(messagewaiting)
			waitspeak.sleep();
		
		System.out.println("Giving" + word);
		
		messagewaiting = true;
		message = word;
		waitlisten.wake();

		lock.release();
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	public int listen() {
		lock.acquire();

        System.out.println("listening");
		
		++listners;
		
		waitspeak.wake();

		while (!messagewaiting)
			waitlisten.sleep();


		System.out.println("listend");
		
		--listners;

		messagewaiting = false;

		int temp = message;

		if(listners > 0)
			waitspeak.wake();
		
		lock.release();
		return temp;
	}

	public static void selfTest() {
		System.out.println("Com Test Start");

		final Communicator com = new Communicator();

		KThread test1A = new KThread(new Runnable() {
			public void run() {
				com.speak(5);
			}

		});

		KThread test1B = new KThread(new Runnable() {
			public void run() {
				System.out.println("Must print 5");
				System.out.println(com.listen());
			}

		});

		KThread test2A = new KThread(new Runnable() {
			public void run() {
				System.out.println("Must print 7");
				System.out.println(com.listen());
			}

		});

		KThread test2B = new KThread(new Runnable() {
			public void run() {
				com.speak(7);
			}

		});
		
		KThread test3A = new KThread(new Runnable() {
			public void run() {
				System.out.println("Must print 9");
				System.out.println(com.listen());
			}

		});

		KThread test3B = new KThread(new Runnable() {
			public void run() {
				System.out.println("Must print 3");
				System.out.println(com.listen());
			}

		});
		
		KThread test3C = new KThread(new Runnable() {
			public void run() {
				com.speak(9);
			}

		});

		KThread test3D = new KThread(new Runnable() {
			public void run() {
				com.speak(3);
			}

		});
		
		KThread test4A = new KThread(new Runnable() {
			public void run() {
				com.speak(2);
			}

		});

		KThread test4B = new KThread(new Runnable() {
			public void run() {
				com.speak(4);
			}

		});
		
		KThread test4C = new KThread(new Runnable() {
			public void run() {
				System.out.println("Must print 2");
				System.out.println(com.listen());
			}

		});

		KThread test4D = new KThread(new Runnable() {
			public void run() {
				System.out.println("Must print 4");
				System.out.println(com.listen());
			}

		});
		
		

		test1A.fork();
		test1B.fork();
		test1A.join();
		test1B.join();
		
		test2A.fork();
		test2B.fork();
		test2A.join();
		test2B.join();

		test3A.fork();
		test3B.fork();
		test3C.fork();
		test3D.fork();
		test3A.join();
		test3B.join();
		test3C.join();
		test3D.join();
		
		test4A.fork();
		test4B.fork();
		test4C.fork();
		test4D.fork();
		test4A.join();
		test4B.join();
		test4C.join();
		test4D.join();
		
		System.out.println("Com Test End");
	}
}
