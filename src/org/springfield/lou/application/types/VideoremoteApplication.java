package org.springfield.lou.application.types;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.application.types.controllers.AnnotationController;
import org.springfield.lou.application.types.controllers.AudioController;
import org.springfield.lou.application.types.controllers.HomePageController;
import org.springfield.lou.application.types.controllers.MainscreenController;
import org.springfield.lou.application.types.controllers.RelatedController;
import org.springfield.lou.application.types.controllers.VideoController;
import org.springfield.lou.application.types.controllers.VideoRemoteController;
import org.springfield.lou.controllers.*;
import org.springfield.lou.screen.*;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

public class VideoremoteApplication extends Html5Application {
	double videolength = ((4 * 60) + 22) * 1000;
	public String masterclock;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Map<String, ArrayList<PathBindObject>> pathbindobjects = new HashMap<String, ArrayList<PathBindObject>>();

	public VideoremoteApplication(String id) {
		super(id);
		MasterClockManager.setApp(this); // tmp needed until moved to mojo
											// shared memory
	}

	public void onNewScreen(Screen s) {
		s.setLanguageCode("en");
		Capabilities cap = s.getCapabilities();
		String mode = s.getParameter("mode");
		if (mode != null && mode.equals("controller")) {
			loadStyleSheet(s, "ipad");
			s.get("#screen").append("div", "mainscreencontroller", new MainscreenController());
		} else if (mode != null && mode.equals("audio")) {
			/*
			 * (cap.getDeviceMode()==cap.MODE_IPAD_LANDSCAPE ||
			 * cap.getDeviceMode()==cap.MODE_APHONE_PORTRAIT ||
			 * cap.getDeviceMode()==cap.MODE_IPHONE_PORTRAIT ||
			 * cap.getDeviceMode()==cap.MODE_APHONE_PORTRAIT)
			 */
			loadStyleSheet(s, "phone");
			// load the base html but also parse it by mustache
			s.get("#screen").attach(new ScreenController());
			s.get("#screen").append("div", "related", new RelatedController());
			AudioController ac = new AudioController();
			s.get("#screen").append("div", "audio1", ac);

			masterclock = s.getParameter("masterclock");
			String master = s.getParameter("master");
			if (masterclock != null && !masterclock.equals("")) {
				if (master != null && master.equals("true")) {
					System.out.println("WE HAVE A MASTERCLOCK " + masterclock + " and we are the master");
					ac.becomeMasterClock(masterclock);
				} else {
					System.out.println("WE HAVE A MASTERCLOCK " + masterclock + " and we are a slave");
					ac.followMasterClock(masterclock);
				}

			}
			s.get("#related").draggable();
			s.bind("#annotations", "valueChange", "newSeekWanted", this);

		} else {
			System.out.println("VideoRemoteApplication: Load HomepageController!");
			loadStyleSheet(s, "mainscreen-fonts");
			loadStyleSheet(s, "materialize");
			loadStyleSheet(s, "mainscreen");

			// load the base html but also parse it by mustache
			s.get("#screen").attach(new ScreenController());
			s.get("#screen").append("div", "homepage", new HomePageController());

			VideoController vc = new VideoController();
			s.get("#screen").append("video", "video1", vc);
			s.removeContent("video1");

			s.get("#screen").append("div", "annotations", new AnnotationController());
			s.get("#screen").append("div", "related", new RelatedController());
			s.get("#related").draggable();
			s.bind("#annotations", "valueChange", "newSeekWanted", this);

			masterclock = s.getParameter("masterclock");
			String master = s.getParameter("master");
			if (masterclock != null && !masterclock.equals("")) {
				if (master != null && master.equals("true")) {
					System.out.println("WE HAVE A MASTERCLOCK " + masterclock + " and we are the master");
					vc.becomeMasterClock(masterclock);
				} else {
					System.out.println("WE HAVE A MASTERCLOCK " + masterclock + " and we are a slave");
					vc.followMasterClock(masterclock);
				}
			}
		}
	}

	public void newSeekWanted(Screen s, JSONObject data) {
		try {
			FsNode videonode = Fs.getNode("/domain/senso/tmp/videocontrollerapp/video/1");
			videolength = videonode.getDuration();

			String[] posxy = ((String) data.get("clientXY")).split(",");
			float rx = Float.parseFloat(posxy[0]);
			long width = (Long) data.get("width");
			float px = ((float) rx / width);

			double newtime = px * videolength;
			// System.out.println("NEW SEEK="+newtime);

			setProperty("/videostate/" + masterclock + "/newtime", "" + newtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onPathUpdate(String paths, String methodname, Object callbackobject) {
		System.out.println("PATHUPDATE with: " + paths + ", " + methodname + ", " + callbackobject);
		String[] vars = paths.split(",");
		for (int i = 0; i < vars.length; i++) {
			System.out.println("BINDPATH=" + vars[i]);
			ArrayList<PathBindObject> list = pathbindobjects.get(vars[i]);
			if (list != null) {
				list.add(new PathBindObject(methodname, callbackobject));
			} else {
				list = new ArrayList<PathBindObject>();
				list.add(new PathBindObject(methodname, callbackobject));
				pathbindobjects.put(vars[i], list);
			}
		}
	}

	public void setProperty(String path, String value) {
		properties.put(path, value);

		String[] parts = path.split("/");
		String key = parts[1];
		String nodeid = parts[2];
		String propertyname = parts[3];

		FsNode node = new FsNode(key, nodeid);
		node.setProperty(propertyname, value);

		key = "/" + key + "/";
		ArrayList<PathBindObject> binds = pathbindobjects.get(key);
		if (binds != null) {
			for (int i = 0; i < binds.size(); i++) {
				PathBindObject bind = binds.get(i);
				String methodname = bind.method;
				Object object = bind.object;
				try {
					Method method = object.getClass().getMethod(methodname, String.class, FsNode.class);
					if (method != null) {
						method.invoke(object, key, node);
					} else {
						System.out.println("MISSING METHOD IN APP =" + method);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Object getProperty(String path) {
		return properties.get(path);
	}

}