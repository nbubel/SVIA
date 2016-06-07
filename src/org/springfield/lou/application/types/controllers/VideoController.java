package org.springfield.lou.application.types.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.MasterClockManager;
import org.springfield.lou.application.types.MasterClockThread;
import org.springfield.lou.application.types.VideoremoteApplication;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class VideoController extends Html5Controller {

	String selector;
	String nodepath;
	FsNode videonode;
	JSONArray annotations = new JSONArray();
	MasterClockThread clock;
	Boolean master = false;
	String ClockName = "";
	String requestedtVideo;
	String firstVideo;
	Boolean ClockUpdate = false;

	public VideoController() {

	}

	public void becomeMasterClock(String name) {
		System.out.println("VIDEO BECOME MASTER");
		master = true;
		ClockName = name;
		clock = MasterClockManager.addMasterClock(screen, name);
		VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
		app.onPathUpdate("/masterclock/", "onClockUpdate", this);

	}

	public void followMasterClock(String name) {
		master = false;
		ClockName = name;
		clock = MasterClockManager.getMasterClock(name);
		System.out.println("FOLLOW CLOCK=" + clock);
		VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
		app.onPathUpdate("/masterclock/", "onClockUpdate", this);

	}

	public void attach(String sel) {
		System.out.println("Attach Videocontroller!");
		System.out.println("sel: " + sel);
		requestedtVideo = "" + screen.getProperty("requestedvideo");
		firstVideo = "" + screen.getProperty("firstVideo");
		System.out.println("Requested Video: " + requestedtVideo);
		System.out.println("First Video: " + firstVideo);
		selector = sel;

		screen.get(selector).loadScript(this);

		FsNode node = getControllerNode(selector);
		System.out.println("Node: " + node.asXML());
		if (node != null) {
			String videopath = node.getProperty("videonode");
			videonode = Fs.getNode(videopath);
			System.out.println("Videopath-Node: " + videopath);

			if (videonode != null) {
				// FsNode snode =
				// Fs.getNode("/domain/senso/user/rbb/collection/homepage/video/142");
				System.out.println("Requestedvideo: " + screen.getProperty("requestedvideo"));
				FsNode snode = Fs.getNode("/domain/senso/user/rbb/collection/homepage/video/" + requestedtVideo);
				// FsNode snode =
				// Fs.getNode("/domain/senso/user/rbb/collection/homepage/video/1");

				if (!firstVideo.contains("1")) {
					System.out.println("Loading Video-Data:");

					videonode.setProperty("mp4", snode.getProperty("mp4"));
					
					// German version
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3", snode.getProperty("mp3"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "description",
							snode.getProperty("description"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "year",
							snode.getProperty("year"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "date",
							snode.getProperty("date"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "title",
							snode.getProperty("title"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "location",
							snode.getProperty("location"));
					System.out.println("Set Requestet Audiofile to TMP-Folder: " + snode.getProperty("mp3"));
					System.out.println("Set Requestet description to TMP-Folder: " + snode.getProperty("description"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("year"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("date"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("title"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("location"));
					
					// englisch version
					
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3-en",
							snode.getProperty("mp3-en"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "description-en",
							snode.getProperty("description-en"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "year-en",
							snode.getProperty("year-en"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "date-en",
							snode.getProperty("date-en"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "title-en",
							snode.getProperty("title-en"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "location-en",
							snode.getProperty("location-en"));
					System.out.println("Set Requestet Audiofile to TMP-Folder: " + snode.getProperty("mp3-en"));
					System.out.println("Set Requestet description to TMP-Folder: " + snode.getProperty("description-en"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("year-en"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("date-en"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("title-en"));
					System.out.println("Set Requestet year to TMP-Folder: " + snode.getProperty("location-en"));

				} else {
					System.out.println("Loading Dummy:");

					videonode.setProperty("mp4", "");
					videonode.setProperty("autoplay", "false");
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3", snode.getProperty("mp3"));
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "description",
							snode.getProperty("description"));
					System.out.println("Set Requestet Audiofile to TMP-Folder: " + snode.getProperty("mp3"));
					System.out.println("Set Requestet description to TMP-Folder: " + snode.getProperty("description"));
					screen.setProperty(firstVideo, 0);
					System.out.println("Set firstVideo to: " + screen.getProperty("firstVideo"));
				}

				JSONObject videonodeobject = videonode.toJSONObject("en",
						"mp4,mp3,autoplay,controls,videolist,selected,wantedtime");
				System.out.println("VideoNodeObject: " + videonodeobject.toJSONString());

				screen.get(selector).parsehtml(videonodeobject);
				screen.get(selector).update(videonodeobject);
				screen.get(selector).track("currentTime", "currentTime", this); // track
																				// the
																				// currentTime
				VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
				app.onPathUpdate("/videostate/", "onVideoUpdate", this);
			} else {
				System.out.println("No Requested Video to play!");
				screen.get(selector).on("mouseup", "closeVideo", this);
			}
		}

	}

	public void closeVideo(Screen s, JSONObject data) {
		System.out.println("CLOSE VIDEO");
		screen.removeContent(selector.substring(1));
	}

	public void currentTime(Screen s, JSONObject data) {
		if (master) { // so i am the master need to keep master clock correct
			VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
			app.setProperty("/videostate/" + clock.getName() + "/currentTime", "" + data.get("currentTime"));
			if (!clock.running()) {
				// clock.start(0);
				System.out.println("Master-Clock isn't running");
			} else {
				System.out.println("Master clock is running!");
			}
		}
	}

	public void onClockUpdate(String path, FsNode node) {
		String id = node.getId();
		if (id.equals(clock.getClockName())) { // extra check since mem track
												// model not 100% done.
			JSONObject nd = node.toJSONObject("en", "wantedtime");
			nd.put("action", "wantedtime");
			System.out.println("clock change=" + node.getProperty("wantedtime") + " for " + node.getId());
			screen.get(selector).update(nd);
		}
	}

	public void onVideoUpdate(String path, FsNode node) {
		// System.out.println("Videocontroller: onVideoUpdate with node: " +
		// node.asXML());
		String newtime = node.getProperty("newtime");
		if (newtime != null && !newtime.equals("")) {
			node.setProperty("seekingvalue", newtime);
			node.setProperty("action", "seek");
			System.out.println("Set new time to: " + newtime);
			ClockUpdate = false;
			try {
				clock.seek((long) Float.parseFloat(newtime));
			} catch (Exception e) {
			}
			node.setProperty("newtime", "");

		}
		String volume = node.getProperty("volume");
		if (volume != null && !volume.equals("")) {
			node.setProperty("volume", volume);
			node.setProperty("action", "volumechange");
		}

		if ((String) node.getProperty("action") != null) {
			String[] params = ((String) node.getProperty("action")).split(",");
			String action = params[0];
			if (action.equals("closevideo")) {
				System.out.println("Stop Masterclock - Videocontroller");
				System.out.println("Clockupdate: " + ClockUpdate);
				if (!ClockUpdate) {
					System.out.println("Run Clockupdate!");
					// System.out.println("Masterclock-Reset!");
					// MasterClockManager.resetMasterClock(ClockName);
					clock = MasterClockManager.addMasterClock(screen, ClockName);
					System.out.println("Clock is paused: " + clock.running());
					ClockUpdate = true;
					System.out.println("Clockupdate: " + ClockUpdate);
					MasterClockManager.threadControl();
				}

			}
		} else {
			System.out.println("No action");
		}

		JSONObject nd = node.toJSONObject("en", "mp4,mp3,autoplay,controls,action,volume,seekingvalue,videolist");
		// nd.put("annotation", annotations);
		screen.get(selector).update(nd);
	}

}
