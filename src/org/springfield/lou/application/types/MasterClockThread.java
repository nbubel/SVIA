package org.springfield.lou.application.types;

import java.util.Date;

import org.springfield.fs.Fs;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

public class MasterClockThread extends Thread {
	private boolean running = false;
	private long streamtime = 0;
	private String name;
	private Screen master;
	private VideoremoteApplication app;
	private boolean paused = false;
	
    public MasterClockThread(VideoremoteApplication a,Screen s,String n) {
		super("masterclockthread "+n);
		master = s;
		app = a;
		name = n;
	}
    
	public void run() {
		while (running) {
			try {
				System.out.println("MasterclockThread is running!");
				long realtime = new Date().getTime()+5000; // 5 seconds from now I want you to be at xxxxx
				if (!paused) streamtime += 5000;
				//if (streamtime>(120*1000)) streamtime = 5000; // loop our test song ! (should be less because loop time)
				app.setProperty("/masterclock/"+name+"/wantedtime", ""+realtime+","+streamtime);
				sleep(5000);
				} catch(InterruptedException i) {
					System.out.println("INTERRUPT THREAD");
					break;
				} catch(Exception e) {
					System.out.println("ERROR MasterClockThread "+name);
					e.printStackTrace();
				}
		}
	}
	
	public void seek(long newtime) {
		System.out.println("MasterClockThread - seeking to: " + newtime);
		streamtime = newtime;
		running = true;
		paused= false;
		System.out.println("Masterclock is running: " + running);
		this.interrupt(); // interupt to take new times
		this.run();
		start();
	}
	
	public void pause() {
		paused=true;
		running =false;
		//this.interrupt(); // interupt to take new times
	}
	
	public void reset(Screen s) {
		// new master
		master = s;
	}
	
	public String getClockName() {
		return name;
	}
	
	public boolean running() {
		return running;
	}
	
	public void start(long time) {
		running = true;
		paused=false;
		start();
	}
    
    /**
     * Shutdown
     */
	public void destroy() {
		running = false;
		this.interrupt(); // signal we should stop;
		System.out.println("Destroyed Thread: " + name);
	}
}