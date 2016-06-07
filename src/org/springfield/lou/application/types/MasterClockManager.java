package org.springfield.lou.application.types;

import java.util.HashMap;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;



public class MasterClockManager {
	
	private static VideoremoteApplication app; // temp needed until moved to global memory in mojo
	
	private static HashMap<String,MasterClockThread> clocks = new HashMap<String,MasterClockThread>();
	
	public static Boolean createdClock = false;
	
	public static void setApp(VideoremoteApplication a) {
		app = a;
	}
	
	
	
	public static MasterClockThread addMasterClock(Screen s,String name) {
		System.out.println("AddMasterCLock - createdClock: " + createdClock);
		MasterClockThread clock  = clocks.get(name);
		if (clock!=null) {
			for (int i = 0; i < clocks.size(); i++) {
				System.out.println("reset nr.: " + i);
				System.out.println("reset masterclock cause size is: " + clocks.size());
				resetMasterClock(name);
			}
			System.out.println("We have " + clocks.size() + " Threads");
			clock = new MasterClockThread(app,s,name);
			clock.pause();
			clocks.put(name, clock);
			System.out.println("MASTERCLOCKMANAGER: MASTERCLock ist reseted and paused");
			System.out.println(clock.running());
		} else if (!createdClock) {
			System.out.println("Create new Clock!");
			clock = new MasterClockThread(app,s,name);
			clock.pause();
			System.out.println("MASTERCLOCKMANAGER: MASTERCLock ist created and paused");
			clocks.put(name, clock);
			createdClock = true;
		} else {
			System.out.println("Clock is already created!");
			clock  = clocks.get("test");
			System.out.println("Return Clock: " + clock.getClockName());
		}
		return clock;
	}

	public static MasterClockThread getMasterClock(String name) {
		MasterClockThread clock  = clocks.get(name);
		if (clock!=null) {
			return clock;
		}
		return null;
	}
	public static void resetMasterClock (String name){
		System.out.println("reset clock with name: " + name);
		MasterClockThread clock  = clocks.get(name);
		System.out.println("get clock for delete: " + clock);
		if (clock!=null) {
			System.out.println("destroy clock: " + clock);
			clock.destroy();
		}
		clocks.remove(name);
		System.out.println("Laufende Threads: " + clocks.size());
	}
	
	public static void threadControl (){
		System.out.println("Laufende Threads: " + clocks.size());
	}

}
