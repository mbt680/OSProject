package nachos.threads;

import nachos.machine.*;

public class ReactWater {

	private static int hydroCount;
	private static int oxyCount;
	private Lock singlePass;
	private Condition2 condHFull;
	private Condition2 condOFull;
	private Condition2 condReady;

	// constructor
	public ReactWater() {
		hydroCount = 0;
		oxyCount = 0;
		singlePass = new Lock();
		condHFull = new Condition2(singlePass);
		condOFull = new Condition2(singlePass);
		condReady = new Condition2(singlePass);
	}

	// hydrogen thread is formed and waits to react with another hydrogen and
	// oxygen. If already 2 hydrogen in wait,
	// it will go in hydrofull condition to wait for a reaction
	public void hReady() {
		singlePass.acquire();

		while (hydroCount >= 2) {
			condHFull.sleep();
		}
		if (oxyCount == 1 && hydroCount == 1) {
			condReady.wakeAll();
			oxyCount--;
			hydroCount--;
			makeWater();
		} else {
			hydroCount++;
			condReady.sleep();
			condHFull.wake();
		}

		singlePass.release();

	}

	// oxygen thread getting ready to react with 2 hydrogen, if already oxy
	// waiting it goes to sleep in condition
	public void oReady() {
		singlePass.acquire();

		while (oxyCount >= 1) {
			condOFull.sleep();
		}

		if (hydroCount == 2) {

			condReady.wakeAll();
			hydroCount = hydroCount - 2;
			makeWater();

		} else {

			oxyCount++;
			condReady.sleep();
			condOFull.wake();
		}

		singlePass.release();

	}

	private void makeWater() {
		System.out.println("Water was made.");
	}

	// testing
	public static void selfTest() {
		System.out.println("Water Test Start");
		final ReactWater MotherNature = new ReactWater();

		KThread hydro1 = new KThread(new Runnable() {
			public void run() {
				MotherNature.hReady();
			}

		});
		KThread hydro2 = new KThread(new Runnable() {
			public void run() {
				MotherNature.hReady();
			}

		});
		KThread oxy = new KThread(new Runnable() {
			public void run() {
				MotherNature.oReady();

			}

		});
		KThread oxy2 = new KThread(new Runnable() {
			public void run() {
				MotherNature.oReady();

			}

		});
		KThread oxy3 = new KThread(new Runnable() {
			public void run() {
				MotherNature.oReady();

			}

		});
		KThread hydro3 = new KThread(new Runnable() {
			public void run() {
				MotherNature.hReady();
			}

		});
		KThread hydro4 = new KThread(new Runnable() {
			public void run() {
				MotherNature.hReady();
			}

		});
		KThread hydro5 = new KThread(new Runnable() {
			public void run() {
				MotherNature.hReady();
			}

		});
		KThread hydro6 = new KThread(new Runnable() {
			public void run() {
				MotherNature.hReady();
			}

		});

		hydro1.fork();
		hydro2.fork();
		oxy.fork();
		oxy2.fork();
		oxy3.fork();
		hydro3.fork();
		hydro4.fork();
		hydro5.fork();
		hydro6.fork();

		oxy3.join();

		System.out.println("Water Test End");
	}

}