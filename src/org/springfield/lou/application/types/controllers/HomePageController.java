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

	public void attach(String sel) {
		System.out.println("Attach Homepagecontroller!");
		selector = sel;
		screen.get("#homepage").setControllerProperty("FsListController", "nodepath",
				"/domain/senso/user/rbb/collection/homepage/");

		FsListController lc = new FsListController();
		screen.get("#homepage").attach(lc);

		screen.bind("#homepage", "itemselected", "itemselected", this);
		VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
		app.onPathUpdate("/videostate/", "onVideoUpdate", this);

	}

	public void onVideoUpdate(String path, FsNode node) {
		System.out.println("Video-Update!");
		String[] params = ((String) node.getProperty("action")).split(",");
		String action = params[0];
		if (action == null)
			return;
		if (action.equals("startvideo")) {
			String itemid = params[1];
			VideoController vc = new VideoController();

			screen.setProperty("requestedvideo", itemid);
			screen.get("#screen").append("video", "video1", vc);
			screen.get("#video1").show();
		}
		if (action.equals("closevideo")) {
			screen.removeContent("video1");
		}
	}

	public void itemselected(Screen s, JSONObject data) {
		System.out.println("WE WANT VIDEO !! =" + data.toJSONString());
		VideoController vc = new VideoController();

		screen.setProperty("requestedvideo", data.get("itemid"));

		System.out.println("ItemID: " + data.get("itemid"));


		screen.get("#screen").append("video", "video1", vc);
		screen.get("#video1").show();

		screen.get("#homepage").hide();
	}

}
