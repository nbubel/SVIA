package org.springfield.lou.application.types;

import java.util.Date;

import org.springfield.fs.Fs;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

/**
 * @author Niels Bubel, Rundfunk Berlin-Brandenburg (RBB), Innovationsprojekte
 * @version 7.2 - final version, 31.05.2016
 *
 *          The main function set for the SVIA Synchronisation by a thread
 */
public class MasterClockThread extends Thread {
	private boolean running = false;
	private long streamtime = 0;
	private String name;
	private Screen master;
	private VideoremoteApplication app;
	private boolean paused = false;

	public MasterClockThread(VideoremoteApplication a, Screen s, String n) {
		super("masterclockthread " + n);
		master = s;
		app = a;
		name = n;
	}

	/**
	 * the beating heart, the thread is sending out an update of the masterclock
	 * (realtime+streamtime) every 5 seconds to the video-mainscreen and the
	 * audio-client-devices
	 */
	public void run() {
		while (running) {
			try {
				System.out.println("MasterclockThread is running!");
				// 5 seconds from now I want you to be at xxxxx
				long realtime = new Date().getTime() + 5000;
				if (!paused)
					streamtime += 5000;
				// loop the test video ! (should be less because loop time)
				// if (streamtime>(120*1000)) streamtime = 5000;
				app.setProperty("/masterclock/" + name + "/wantedtime", "" + realtime + "," + streamtime);
				sleep(5000);
			} catch (InterruptedException i) {
				System.out.println("INTERRUPT THREAD");
				break;
			} catch (Exception e) {
				System.out.println("ERROR MasterClockThread " + name);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Seeks to the newtime
	 * 
	 * @param newtime point to seeking
	 */
	public void seek(long newtime) {
		System.out.println("MasterClockThread - seeking to: " + newtime);
		streamtime = newtime;
		running = true;
		paused = false;
		System.out.println("Masterclock is running: " + running);
		this.interrupt(); // interupt to take new times
		this.run();
		start();
	}

	/**
	 * Pausing the masterclock thread
	 */
	public void pause() {
		paused = true;
		running = false;
	}

	/**
	 * Resets the mainscreen-masterclock
	 * @param s mainscreen
	 */
	public void reset(Screen s) {
		// new master
		master = s;
	}

	/**
	 * Get the Masterclockname
	 * @return Masterclockname
	 */
	public String getClockName() {
		return name;
	}

	/**
	 * Boolean, if the masterclock is running
	 * 
	 * @return Boolean, if running
	 */
	public boolean running() {
		return running;
	}

	/**
	 * Starts the masterclock at timepoint x
	 * 
	 * @param time timepoint x
	 */
	public void start(long time) {
		running = true;
		paused = false;
		start();
	}

	/**
	 * Shutdown the masterclock thread
	 */
	public void destroy() {
		running = false;
		this.interrupt();
		System.out.println("Destroyed Thread: " + name);
	}
}