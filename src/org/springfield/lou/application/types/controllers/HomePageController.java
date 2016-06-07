package org.springfield.lou.application.types.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.application.types.VideoremoteApplication;
import org.springfield.lou.controllers.FsListController;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Html5Element;
import org.springfield.lou.screen.Screen;

public class HomePageController extends Html5Controller {

	public String masterclock;

	public void attach(String sel) {
		System.out.println("Attach Homepagecontroller!");
		selector = sel;
		screen.get("#homepage").setControllerProperty("FsListController", "nodepath",
				"/domain/senso/user/rbb/collection/homepage/");

		FsListController lc = new FsListController();
		screen.get("#homepage").attach(lc);

		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false); // get
																								// the
																								// results
																								// from
																								// the
																								// database
		List<FsNode> nodes = fslist.getNodes();
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				FsNode node = nodes.get(i);
				String bordercolor = node.getProperty("bordercolor");
				if (bordercolor != null && bordercolor.equals("ff5722")) {
					screen.setProperty("requestedvideo", i + 1);
					screen.setProperty("firstVideo", 1);
				}
			}
		}

		// FsListController lc = new FsListController();
		// lc.addFilter(this,"extendNodes");
		// screen.get("#itemlist").attach(lc);

		// public FSList extendNodes(FSList incomming) {

		// FSList outgoing = new FSList();

		// List<FsNode> nodes = incomming.getNodesSorted("created","UP");

		// for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
		// FsNode n = (FsNode)iter.next();

		screen.bind("#homepage", "itemselected", "itemselected", this);
		VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
		app.onPathUpdate("/videostate/", "onVideoUpdate", this);

		// screen.get("#screen").append("div","audio1",new AudioController());
		// VideoController vc = new VideoController();
		// screen.get("#screen").append("video","video1",vc);

	}

	public void onVideoUpdate(String path, FsNode node) {
		System.out.println("Video-Update!");
		System.out.println("node: " + node.asXML());
		if (node != null) {
			System.out.println("node: " + node.asXML() + "action-Prop: " + node.getProperty("action"));
			if ((String) node.getProperty("action") != null) {
				String[] params = ((String) node.getProperty("action")).split(",");
				String action = params[0];
				if (action == null)
					return;
				if (action.equals("startvideo")) {
					String itemid = params[1];
					screen.setProperty("firstVideo", 0);
					VideoController vc = new VideoController();

					screen.setProperty("requestedvideo", itemid);
					screen.get("#screen").append("video", "video1", vc);
					screen.get("#video1").show();
					screen.get("#homepage").hide();

				}
				if (action.equals("closevideo")) {
					screen.removeContent("video1");
					screen.get("#homepage").show();
				}
			} else {
				System.out.println("No action!");
			}
		}
	}

	public void itemselected(Screen s, JSONObject data) {
		System.out.println("WE WANT VIDEO !! =" + data.toJSONString());

		// VideoController vc = new VideoController();

		screen.setProperty("requestedvideo", data.get("itemid"));

		System.out.println("ItemID: " + data.get("itemid"));

		// screen.get("#screen").append("video", "video1", vc);
		screen.get("#video1").show();

		screen.get("#homepage").hide();

	}

}
