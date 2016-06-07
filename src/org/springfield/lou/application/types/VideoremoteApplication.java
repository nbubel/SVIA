package org.springfield.lou.application.types;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.application.types.controllers.AudioController;
import org.springfield.lou.application.types.controllers.HomePageController;
import org.springfield.lou.application.types.controllers.MainscreenController;
import org.springfield.lou.application.types.controllers.VideoController;
import org.springfield.lou.controllers.*;
import org.springfield.lou.screen.*;

/**
 * The main class of the SVIA - VideoremoteApplication to demonstrate the
 * video/audio synchronisation of broadcast footage
 * 
 * it is implemented to work on the base of the Noteriks Springfield-Framework
 * visit for more information: http://www.noterik.nl/products/webtv_framework/
 * 
 * @author Niels Bubel, Rundfunk Berlin-Brandenburg (RBB), Innovationsprojekte
 * @version 7.2 - final version, 31.05.2016
 * 
 *
 */
public class VideoremoteApplication extends Html5Application {
	double videolength;
	public String masterclock;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Map<String, ArrayList<PathBindObject>> pathbindobjects = new HashMap<String, ArrayList<PathBindObject>>();

	/**
	 * Constructor for the class object
	 * 
	 * @param id
	 *            the user/screen id
	 */
	public VideoremoteApplication(String id) {
		super(id);
		// tmp needed until moved to mojo shared memory
		MasterClockManager.setApp(this); 
	}

	/**
	 * Creates new screen by starting the application
	 * 
	 * there are three device-possibilities a) the remote control device for an
	 * ipad by choosing "controller" as url-parameter "mode" b) the audio device
	 * for an smartphone by choosing "audio" as url-parameter "mode" c) the
	 * video device for an mainscreen as default option
	 */
	public void onNewScreen(Screen s) {
		s.setLanguageCode("de");
		Capabilities cap = s.getCapabilities();
		String mode = s.getParameter("mode");
		// option a) loads the the remote control device on the screen
		if (mode != null && mode.equals("controller")) {
			// css-loading
			loadStyleSheet(s, "controller-fonts");
			loadStyleSheet(s, "materialize");
			loadStyleSheet(s, "ipad");
			s.get("#screen").append("div", "mainscreencontroller", new MainscreenController());

			// option b) loads the audio device for an smartphone
		} else if (mode != null && mode.equals("audio")) {

			// css-loading
			loadStyleSheet(s, "mainscreen-fonts");
			loadStyleSheet(s, "materialize");
			loadStyleSheet(s, "phone");

			// load the base html but also parse it by mustache-templates
			s.get("#screen").attach(new ScreenController());
			AudioController ac = new AudioController();
			s.get("#screen").append("div", "audio1", ac);

			// init the masterclock for the synchronisation of video and audio,
			// if the screen is the audio device to play the audio
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

			// option c) loads the videoitems for playing videos on the
			// mainscreen
		} else {
			System.out.println("VideoRemoteApplication: Load HomepageController!");
			// css-loading
			loadStyleSheet(s, "mainscreen-fonts");
			loadStyleSheet(s, "materialize");
			loadStyleSheet(s, "mainscreen");

			// load the base html but also parse it by mustache
			s.get("#screen").attach(new ScreenController());
			s.get("#screen").append("div", "homepage", new HomePageController());
			// load the video-grid with the video items of the db
			VideoController vc = new VideoController();
			s.get("#screen").append("video", "video1", vc);
			s.removeContent("video1");

			// init the masterclock for the synchronisation of video and audio,
			// if the screen is the mainscreen to play the video
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

	/**
	 * Jumps to another point at the video on the mainscreen
	 * 
	 * @param s
	 *            screen
	 * @param data
	 *            the JSONObject with the update data
	 */
	public void newSeekWanted(Screen s, JSONObject data) {
		try {
			FsNode videonode = Fs.getNode("/domain/senso/tmp/videocontrollerapp/video/1");
			videolength = videonode.getDuration();
			String[] posxy = ((String) data.get("clientXY")).split(",");
			float rx = Float.parseFloat(posxy[0]);
			long width = (Long) data.get("width");
			float px = ((float) rx / width);
			double newtime = px * videolength;
			;
			setProperty("/videostate/" + masterclock + "/newtime", "" + newtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Listed the screens to inform if there is an update(object)
	 * 
	 * @param paths
	 *            location that should be observed
	 * @param methodname
	 *            name of the method
	 * @param callbackobject
	 *            object/function of the action that follows the update
	 */
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

	/**
	 * Sets an value at a path
	 * 
	 * @param path,
	 *            the location path
	 * @param value,
	 *            the value
	 */
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

	/**
	 * Returns the saved value of some path
	 * 
	 * @param path
	 *            of the value
	 * @return value
	 */
	public Object getProperty(String path) {
		return properties.get(path);
	}
}