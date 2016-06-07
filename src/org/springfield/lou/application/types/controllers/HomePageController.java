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

/**
 * @author Niels Bubel, Rundfunk Berlin-Brandenburg (RBB), Innovationsprojekte
 * @version 7.2 - final version, 31.05.2016
 *
 * Controller for showing the video items on the mainscreen in a grid
 *
 */
public class HomePageController extends Html5Controller {

	public String masterclock;

	/** 
	 * Brings the video items from the database to the screen
	 */
	public void attach(String sel) {
		System.out.println("Attach Homepagecontroller!");
		selector = sel;
		screen.get("#homepage").setControllerProperty("FsListController", "nodepath",
				"/domain/senso/user/rbb/collection/homepage/");

		FsListController lc = new FsListController();
		screen.get("#homepage").attach(lc);
		
		// get the results from the database
		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false); 
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

		// Starting point for the additional feature to filter video categories
		
		// FsListController lc = new FsListController();
		// lc.addFilter(this,"extendNodes");
		// screen.get("#itemlist").attach(lc);
		// public FSList extendNodes(FSList incomming) {
		// FSList outgoing = new FSList();
		// List<FsNode> nodes = incomming.getNodesSorted("created","UP");
		// for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
		// FsNode n = (FsNode)iter.next();
		
		// registry the method onVideoUpdate for the path Update process of the folder /videostate/ (Play and stop)
		screen.bind("#homepage", "itemselected", "itemselected", this);
		VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
		app.onPathUpdate("/videostate/", "onVideoUpdate", this);
	}

	/**
	 * Starting- and stop-Process of a video
	 * 
	 * @param path path to the node
	 * @param node videoinformations
	 */
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

	/**
	 * Communicates the video that is choosed by the user on the mainscreen
	 * 
	 * @param s Mainscreen
	 * @param data JSONObject
	 */
	public void itemselected(Screen s, JSONObject data) {
		System.out.println("WE WANT VIDEO !! =" + data.toJSONString());

		screen.setProperty("requestedvideo", data.get("itemid"));
		System.out.println("ItemID: " + data.get("itemid"));
		screen.get("#video1").show();
		screen.get("#homepage").hide();

	}
}
