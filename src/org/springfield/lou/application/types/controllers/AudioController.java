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
	
	
	public AudioController() {

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
		System.out.println("Audiocontroller - Video-Update!");
		System.out.println("node: " + node.asXML());
		if (node!=null){
		System.out.println("node: " + node.asXML() + "action-Prop: " + node.getProperty("action"));
		if ((String) node.getProperty("action")!=null){
		String[] params = ((String) node.getProperty("action")).split(",");
		String action = params[0];
		if (action == null)
			return;
		if (action.equals("startvideo")) {
			String itemid = params[1];
			//VideoController vc = new VideoController();

			////screen.setProperty("requestedvideo", itemid);
			//screen.get("#screen").append("video", "video1", vc);
			//screen.get("#video1").show();
			//screen.get("#homepage").hide();
			
			System.out.println("Start Audio");
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
					audionode.setProperty("mp3",snode.getProperty("mp3"));
					System.out.println("Audionode-MP3: " + audionode.getProperty("mp3"));
					audionode.setProperty("description",snode.getProperty("description"));
					System.out.println("Audionode-description: " + audionode.getProperty("description"));
					
					JSONObject audionodeobject = audionode.toJSONObject("en","mp3,description,mp4,autoplay,controls,videolist,selected,wantedtime");
					System.out.println("VideoNodeObject: "+audionodeobject.toJSONString());
					
					screen.get(selector).parsehtml(audionodeobject);
					screen.get(selector).update(audionodeobject);
				}
			}
			
			System.out.println("Audiocontoller: Startvideo");
		}
		if (action.equals("closevideo")) {
			//screen.removeContent("video1");
			//screen.get("#homepage").show();
			System.out.println("CloseAudio!");
			Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3", "");
			//screen.get("#audio1").hide();
			//screen.get("#source").setVariable("src", "");
			
			
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
					audionode.setProperty("mp3",snode.getProperty("mp3"));
					System.out.println("Audionode-MP3: " + audionode.getProperty("mp3"));
					audionode.setProperty("description", "Es läuft zur Zeit kein Video auf dem Mainscreen!");
					System.out.println("Audionode-description: " + audionode.getProperty("description"));
					
					JSONObject audionodeobject = audionode.toJSONObject("en","mp3,description,mp4,autoplay,controls,videolist,selected,wantedtime");
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