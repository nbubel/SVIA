package org.springfield.lou.application.types.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.MasterClockManager;
import org.springfield.lou.application.types.MasterClockThread;
import org.springfield.lou.application.types.VideoremoteApplication;
import org.springfield.lou.controllers.Html5Controller;

public class AudioController extends Html5Controller{
	
	String selector;
	String nodepath;
	JSONArray annotations = new JSONArray();
	MasterClockThread clock;
	Boolean master = false;
	String requestedtVideo;
	FsNode audionode;
	String ClockName = "";
	
	
	public AudioController() {

	}
	
	public void becomeMasterClock(String name) {
		master = true;
		clock = MasterClockManager.addMasterClock(screen,name);
		VideoremoteApplication app = (VideoremoteApplication)screen.getApplication();
		app.onPathUpdate("/masterclock/","onClockUpdate",this);
	}
	
	public void followMasterClock(String name) {
		System.out.println("AUDIO FOLLOWS MASTERCLOCK");
		master = false;
		ClockName = name;
		clock = MasterClockManager.getMasterClock(name);
		System.out.println("FOLLOW CLOCK="+clock);
		VideoremoteApplication app = (VideoremoteApplication)screen.getApplication();
		app.onPathUpdate("/masterclock/","onClockUpdate",this);
		
		System.out.println("CLock: " + clock.getClockName());
		System.out.println("CLock: " + clock);
		
		if (clock.running()){
			System.out.println("Masterclock is running!");
			if (audionode!=null) {
				FsNode snode = Fs.getNode("/domain/senso/tmp/videocontrollerapp/video/1");
				
				//german-version
				audionode.setProperty("mp3",snode.getProperty("mp3"));
				audionode.setProperty("description", snode.getProperty("description"));
				audionode.setProperty("date",snode.getProperty("date"));
				audionode.setProperty("title", snode.getProperty("title"));
				audionode.setProperty("location",snode.getProperty("location"));
				audionode.setProperty("year", snode.getProperty("year"));
				System.out.println("Audionode-MP3: " + audionode.getProperty("mp3"));
				System.out.println("Audionode-description: " + audionode.getProperty("description"));
				System.out.println("Audionode-date: " + audionode.getProperty("date"));
				System.out.println("Audionode-title: " + audionode.getProperty("title"));
				System.out.println("Audionode-location: " + audionode.getProperty("location"));
				System.out.println("Audionode-year: " + audionode.getProperty("year"));
				
				// english version
				
				audionode.setProperty("mp3-en",snode.getProperty("mp3-en"));
				audionode.setProperty("description-en", snode.getProperty("description-en"));
				audionode.setProperty("date-en",snode.getProperty("date-en"));
				audionode.setProperty("title-en", snode.getProperty("title-en"));
				audionode.setProperty("location-en",snode.getProperty("location-en"));
				audionode.setProperty("year-en", snode.getProperty("year-en"));
				System.out.println("Audionode-MP3: " + audionode.getProperty("mp3-en"));
				System.out.println("Audionode-description: " + audionode.getProperty("description-en"));
				System.out.println("Audionode-date: " + audionode.getProperty("date-en"));
				System.out.println("Audionode-title: " + audionode.getProperty("title-en"));
				System.out.println("Audionode-location: " + audionode.getProperty("location-en"));
				System.out.println("Audionode-year: " + audionode.getProperty("year-en"));
				
				JSONObject audionodeobject = audionode.toJSONObject("en","title,location,date,year,mp3,description,title-en,location-en,date-en,year-en,mp3-en,description-en,mp4,autoplay,controls,videolist,selected,wantedtime");
				System.out.println("VideoNodeObject: "+audionodeobject.toJSONString());
				
				screen.get(selector).parsehtml(audionodeobject);
				screen.get(selector).update(audionodeobject);
			}
		} else {
			System.out.println("Masterclock isn't running!");
			if (audionode!=null) {
				Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3", "");
				Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3-en", "");
				FsNode snode = Fs.getNode("/domain/senso/tmp/videocontrollerapp/video/1");
				
				// german version
				audionode.setProperty("mp3",snode.getProperty("mp3"));
				audionode.setProperty("description", "Es läuft zur Zeit kein Video auf dem Mainscreen!");
				audionode.setProperty("date","XX");
				audionode.setProperty("title", "XX");
				audionode.setProperty("location","XX");
				audionode.setProperty("year", "XX");
				System.out.println("Audionode-MP3: " + audionode.getProperty("mp3"));
				System.out.println("Audionode-description: " + audionode.getProperty("description"));
				System.out.println("Audionode-date: " + audionode.getProperty("date"));
				System.out.println("Audionode-title: " + audionode.getProperty("title"));
				System.out.println("Audionode-location: " + audionode.getProperty("location"));
				System.out.println("Audionode-year: " + audionode.getProperty("year"));
				
				// englisch version
				
				audionode.setProperty("mp3-en",snode.getProperty("mp3-en"));
				audionode.setProperty("description-en", "There is no video running at the mainscreen!");
				audionode.setProperty("date-en", "XX");
				audionode.setProperty("title-en", "XX");
				audionode.setProperty("location-en", "XX");
				audionode.setProperty("year-en", "XX");
				System.out.println("Audionode-MP3: " + audionode.getProperty("mp3-en"));
				System.out.println("Audionode-description: " + audionode.getProperty("description-en"));
				System.out.println("Audionode-date: " + audionode.getProperty("date-en"));
				System.out.println("Audionode-title: " + audionode.getProperty("title-en"));
				System.out.println("Audionode-location: " + audionode.getProperty("location-en"));
				System.out.println("Audionode-year: " + audionode.getProperty("year-en"));
				
				
				
				JSONObject audionodeobject = audionode.toJSONObject("en","title,location,date,year,mp3,description,title-en,location-en,date-en,year-en,mp3-en,description-en,mp4,autoplay,controls,videolist,selected,wantedtime");
				System.out.println("VideoNodeObject: "+audionodeobject.toJSONString());
				
				screen.get(selector).parsehtml(audionodeobject);
				screen.get(selector).update(audionodeobject);
			}
		}

	}
	
	public void attach(String sel) {
		
		
		//http://research.rbb-online.de/media/mauer/audio/101audio_deutsch_192k.m4a
		
		System.out.println("ATTACHING AUDIO TAG");
		selector = sel;
		
		JSONObject data = new JSONObject();
		screen.get(selector).parsehtml(data);		
		
		screen.get(selector).loadScript(this);
		FsNode node = getControllerNode(selector);
		System.out.println("AudioControllerNode: "+node.asXML());
		
		if (node!=null) {
			String audiopath = node.getProperty("audionode");
			audionode = Fs.getNode(audiopath);
			System.out.println("Videopath-Node: " + audiopath);
			System.out.println(audionode.asXML());
			
			
			
			String url = node.getProperty("mp3url");
			String masterclockpath = node.getProperty("masterclock");
			requestedtVideo = "" + screen.getProperty("requestedvideo");
			System.out.println("Requestedvideo: "+ requestedtVideo);
			System.out.println("MP3 URL="+url);
			System.out.println("CLOCK PATH="+masterclockpath);
			
			FsNode masternode = Fs.getNode(masterclockpath);
			System.out.println("MasterNode: "+masternode.asXML());
			if (masternode!=null) {
				JSONObject nd = masternode.toJSONObject("en","wantedtime");
				System.out.println("JSONObject: "+nd);
				screen.get(selector).update(nd);	
			}
			model.observeNode(this,masterclockpath);
		}
		
		VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
		app.onPathUpdate("/videostate/", "onVideoUpdate", this);
		
	}
	
	public void onClockUpdate(String path,FsNode node) {
		clock = MasterClockManager.getMasterClock(ClockName);
		System.out.println("AudioController: onClockUpdate with node: " + node.asXML());
		String id = node.getId();
		if (clock != null){
			if (id.equals(clock.getClockName())) { // extra check since mem track model not 100% done.
				JSONObject nd = node.toJSONObject("en","wantedtime");
				nd.put("action","wantedtime");
				System.out.println("audio clock change="+node.getProperty("wantedtime")+" for "+node.getId());
				screen.get(selector).update(nd);
			}
		} else {
			System.out.println("there is no master clock!");
		}
	}
	
	public void onVideoUpdate(String path, FsNode node) {
		//System.out.println("Audiocontroller - Video-Update!");
		//System.out.println("node: " + node.asXML());
		if (node!=null){
		//System.out.println("node: " + node.asXML() + "action-Prop: " + node.getProperty("action"));
		if ((String) node.getProperty("action")!=null){
		String[] params = ((String) node.getProperty("action")).split(",");
		String action = params[0];
		if (action == null)
			return;
		if (action.equals("startvideo")) {
			String itemid = params[1];
			
			System.out.println("Start Audio");
			node = getControllerNode(selector);
	
			
			if (node!=null) {
				String audiopath = node.getProperty("audionode");
				audionode = Fs.getNode(audiopath);
				
				
				if (audionode!=null) {
					//FsNode snode = Fs.getNode("/domain/senso/user/rbb/collection/homepage/video/142");
					System.out.println("Requestedvideo: "+ screen.getProperty("requestedvideo"));
					//FsNode snode = Fs.getNode("/domain/senso/user/rbb/collection/homepage/video/"+requestedtVideo);
					FsNode snode = Fs.getNode("/domain/senso/tmp/videocontrollerapp/video/1");
					
					// german version
					
					audionode.setProperty("mp3",snode.getProperty("mp3"));
					audionode.setProperty("description",snode.getProperty("description"));
					audionode.setProperty("date",snode.getProperty("date"));
					audionode.setProperty("title", snode.getProperty("title"));
					audionode.setProperty("location",snode.getProperty("location"));
					audionode.setProperty("year", snode.getProperty("year"));
					
					System.out.println("Audionode-MP3: " + audionode.getProperty("mp3"));
					System.out.println("Audionode-description: " + audionode.getProperty("description"));
					System.out.println("Audionode-date: " + audionode.getProperty("date"));
					System.out.println("Audionode-title: " + audionode.getProperty("title"));
					System.out.println("Audionode-location: " + audionode.getProperty("location"));
					System.out.println("Audionode-year: " + audionode.getProperty("year"));
					
					// english version
					
					audionode.setProperty("mp3-en",snode.getProperty("mp3-en"));
					audionode.setProperty("description-en", snode.getProperty("description-en"));
					audionode.setProperty("date-en",snode.getProperty("date-en"));
					audionode.setProperty("title-en", snode.getProperty("title-en"));
					audionode.setProperty("location-en",snode.getProperty("location-en"));
					audionode.setProperty("year-en", snode.getProperty("year-en"));
					System.out.println("Audionode-MP3: " + audionode.getProperty("mp3-en"));
					System.out.println("Audionode-description: " + audionode.getProperty("description-en"));
					System.out.println("Audionode-date: " + audionode.getProperty("date-en"));
					System.out.println("Audionode-title: " + audionode.getProperty("title-en"));
					System.out.println("Audionode-location: " + audionode.getProperty("location-en"));
					System.out.println("Audionode-year: " + audionode.getProperty("year-en"));
					

					JSONObject audionodeobject = audionode.toJSONObject("en","title,location,date,year,mp3,description,title-en,location-en,date-en,year-en,mp3-en,description-en,mp4,autoplay,controls,videolist,selected,wantedtime");
					System.out.println("VideoNodeObject: "+audionodeobject.toJSONString());
					
					screen.get(selector).parsehtml(audionodeobject);
					screen.get(selector).update(audionodeobject);
				}
			}
			
			System.out.println("Audiocontoller: Startvideo");
		}
		if (action.equals("closevideo")) {
			System.out.println("CloseAudio!");
			Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3", "");
			Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3-en", "");
			
			
			System.out.println("Audio remove src");
			node = getControllerNode(selector);
			System.out.println("AudioControllerNode: "+node.asXML());
			
			
			if (node!=null) {
				String audiopath = node.getProperty("audionode");
				audionode = Fs.getNode(audiopath);
				System.out.println("Videopath-Node: " + audiopath);
				
				
				if (audionode!=null) {
					//FsNode snode = Fs.getNode("/domain/senso/user/rbb/collection/homepage/video/142");
					System.out.println("Requestedvideo: "+ screen.getProperty("requestedvideo"));
					//FsNode snode = Fs.getNode("/domain/senso/user/rbb/collection/homepage/video/"+requestedtVideo);
					FsNode snode = Fs.getNode("/domain/senso/tmp/videocontrollerapp/video/1");
					
					// german version
					audionode.setProperty("mp3",snode.getProperty("mp3"));
					audionode.setProperty("description", "Es läuft zur Zeit kein Video auf dem Mainscreen!");
					audionode.setProperty("date","XX");
					audionode.setProperty("title", "XX");
					audionode.setProperty("location","XX");
					audionode.setProperty("year", "XX");
					System.out.println("Audionode-MP3: " + audionode.getProperty("mp3"));
					System.out.println("Audionode-description: " + audionode.getProperty("description"));
					System.out.println("Audionode-date: " + audionode.getProperty("date"));
					System.out.println("Audionode-title: " + audionode.getProperty("title"));
					System.out.println("Audionode-location: " + audionode.getProperty("location"));
					System.out.println("Audionode-year: " + audionode.getProperty("year"));
					
					// englisch version
					
					audionode.setProperty("mp3-en",snode.getProperty("mp3-en"));
					audionode.setProperty("description-en", "There is no video running at the mainscreen!");
					audionode.setProperty("date-en", "XX");
					audionode.setProperty("title-en", "XX");
					audionode.setProperty("location-en", "XX");
					audionode.setProperty("year-en", "XX");
					System.out.println("Audionode-MP3: " + audionode.getProperty("mp3-en"));
					System.out.println("Audionode-description: " + audionode.getProperty("description-en"));
					System.out.println("Audionode-date: " + audionode.getProperty("date-en"));
					System.out.println("Audionode-title: " + audionode.getProperty("title-en"));
					System.out.println("Audionode-location: " + audionode.getProperty("location-en"));
					System.out.println("Audionode-year: " + audionode.getProperty("year-en"));
					
					JSONObject audionodeobject = audionode.toJSONObject("en","title,location,date,year,mp3,description,title-en,location-en,date-en,year-en,mp3-en,description-en,mp4,autoplay,controls,videolist,selected,wantedtime");
					System.out.println("VideoNodeObject: "+audionodeobject.toJSONString());
					
					screen.get(selector).parsehtml(audionodeobject);
					screen.get(selector).update(audionodeobject);
				}
			}
			
		}
		} else {
			System.out.println("Audio-Controller: No action!");
		}
		}

	}
	              
}