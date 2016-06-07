package org.springfield.lou.application.types.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.application.types.VideoremoteApplication;
import org.springfield.lou.controllers.FsListController;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class MainscreenController extends Html5Controller {

	FSList fsPointer;
	List<FsNode> nodesPointer;
	private int selectedItemID = -1;
	FSList fsStart = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false);
	List<FsNode> nodesStart = fsStart.getNodes();
	int sizeStart = fsStart.size();

	public void attach(String sel) {
		selector = sel;
		JSONObject data = new JSONObject();
		screen.get(selector).parsehtml(data);
		screen.get("#mainscreencontroller_play").on("mouseup", "startVideo", this);
		screen.get("#mainscreencontroller_close").on("mouseup", "closeVideo", this);
		screen.get("#mainscreencontroller_import").on("mouseup", "importXML", this);
		screen.get("#mainscreencontroller_clear").on("mouseup", "removeAllVideos", this);
		screen.get("#mainscreencontroller_pointer").on("mouseup", "setPointer", this);
		screen.get("#mainscreencontroller_left").on("mouseup", "goPrev", this);
		screen.get("#mainscreencontroller_right").on("mouseup", "goNext", this);
		screen.get("#mainscreencontroller_up").on("mouseup", "goUp", this);
		screen.get("#mainscreencontroller_down").on("mouseup", "goDown", this);
		System.out.println("Pointer: " + selectedItemID);
		screen.setProperty("requestedvideo", selectedItemID);
		System.out.println("Set Property Requested Video: " + screen.getProperty("requestedvideo"));

	}

	public void removeAllVideos(Screen s, JSONObject data) {

		System.out.println("Remove all videos!");

		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false);

		List<FsNode> nodes = fslist.getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println("Node out of DB " + i + ": " + nodes.get(i).asXML());
			boolean deleted = org.springfield.fs.Fs
					.deleteNode("/domain/senso/user/rbb/collection/homepage/video/" + nodes.get(i).getId());
			System.out.println("Deleted succsessfull?: " + deleted);
		}

	}

	public void importXML(Screen s, JSONObject data) {
		System.out.println("Update - Liste!");

		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false);

		List<FsNode> nodes = fslist.getNodes();
		List<FsNode> nodes2;
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println("Node " + i + ": " + nodes.get(i).asXML());
		}

		// Version1: without mp3 -
		// webapps/ROOT/eddie/apps/videoremote/img/20160229-wall-videos-german.xml
		// webapps/ROOT/eddie/apps/videoremote/img/201600318-wall-videos-german.xml
		File file = new File("webapps/ROOT/eddie/apps/videoremote/img/20160408-wall-videos2.xml");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			try {
				doc = dbFactory.newDocumentBuilder().parse(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SAXException e) {
			e.printStackTrace();
		}

		if (doc != null) {
			System.out.println("Test:");
			String titel = doc.getElementsByTagName("titel").item(0).getTextContent();
			System.out.println("Titel: " + titel);
		} else {
			System.out.println("Document wurde nicht korrekt eingelesen 2!");
		}

		for (int i = 0; i < doc.getElementsByTagName("titel").getLength(); i++) {
			FsNode node = new FsNode("video");
			//german version
			node.setProperty("title", doc.getElementsByTagName("titel").item(i).getTextContent());
			node.setProperty("date", doc.getElementsByTagName("datum").item(i).getTextContent());
			node.setProperty("img", doc.getElementsByTagName("img").item(i).getTextContent());
			node.setProperty("mp4", doc.getElementsByTagName("mp4").item(i).getTextContent());
			node.setProperty("mp3", doc.getElementsByTagName("mp3").item(i).getTextContent());
			node.setProperty("duration", doc.getElementsByTagName("dauer").item(i).getTextContent());
			node.setProperty("categories", doc.getElementsByTagName("kategorien").item(i).getTextContent());
			node.setProperty("description", doc.getElementsByTagName("beschreibung").item(i).getTextContent());
			node.setProperty("location", doc.getElementsByTagName("ort").item(i).getTextContent());
			node.setProperty("year", doc.getElementsByTagName("jahr").item(i).getTextContent());
			// english version
			node.setProperty("title-en", doc.getElementsByTagName("titel-en").item(i).getTextContent());
			node.setProperty("date-en", doc.getElementsByTagName("datum-en").item(i).getTextContent());
			node.setProperty("mp3-en", doc.getElementsByTagName("mp3-en").item(i).getTextContent());
			node.setProperty("categories-en", doc.getElementsByTagName("kategorien-en").item(i).getTextContent());
			node.setProperty("description-en", doc.getElementsByTagName("beschreibung-en").item(i).getTextContent());
			node.setProperty("location-en", doc.getElementsByTagName("ort-en").item(i).getTextContent());
			node.setProperty("year-en", doc.getElementsByTagName("jahr-en").item(i).getTextContent());
			
			node.setId("" + (nodes.size() + 1));
			System.out.println("created new node: " + node.asXML());

			fslist.addNode(node);
		}

		nodes = fslist.getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println("Node to DB " + i + ": " + nodes.get(i).asXML());
			boolean inserted = org.springfield.fs.Fs.insertNode(nodes.get(i),
					"/domain/senso/user/rbb/collection/homepage");
			System.out.println("Insertion succsessfull?: " + inserted);
		}

		FSList fslist2 = new FSList();

		FsNode node2 = new FsNode("pointer");
		node2.setProperty("selectedID", "-1");
		node2.setId("1");
		System.out.println("created new pointer-node: " + node2.asXML());

		fslist2.addNode(node2);
		nodes2 = fslist2.getNodes();

		boolean insertedID = org.springfield.fs.Fs.insertNode(nodes2.get(0),
				"/domain/senso/user/rbb/collection/pointer");
		System.out.println("Insertion Pointer: " + insertedID);

		/*
		 * System.out.println("Put fslist to DB");
		 * FSListManager.put("/domain/senso/user/rbb/collection/homepage",
		 * fslist);
		 */

		// String bordercolor = node.getProperty("bordercolor");
		// if (bordercolor!=null && bordercolor.equals("ffff00")) {
		// VideoremoteApplication app =
		// (VideoremoteApplication)screen.getApplication();
		// app.setProperty("/videostate/"+app.masterclock +"/action",
		// "startvideo,"+(i+1));
		// }
	}

	public void startVideo(Screen s, JSONObject data) {
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
					VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
					app.setProperty("/videostate/" + app.masterclock + "/action", "startvideo," + (i + 1));
					app.setProperty("/videostate/" + app.masterclock + "/newtime", "0");
				}
			}
		}
	}

	public void closeVideo(Screen s, JSONObject data) {
		VideoremoteApplication app = (VideoremoteApplication) screen.getApplication();
		app.setProperty("/videostate/" + app.masterclock + "/action", "closevideo");
	}

	public void setPointer(Screen s, JSONObject data) {
		System.out.println("Set pointer to the first video");
		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false); // get
																								// the
																								// results
																								// from
																								// the
																								// database
		List<FsNode> nodes = fslist.getNodes();
		fsStart = fslist;
		nodesStart = nodes;
		sizeStart = nodes.size();
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				if (i == 0) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/1", "bordercolor", "ff5722");
					selectedItemID = 0;
					screen.setProperty("requestedvideo", selectedItemID);
					Fs.setProperty("/domain/senso/tmp/videocontrollerapp/video/1", "mp3", "" + selectedItemID);
					System.out.println("Set Property Requested Video: " + screen.getProperty("requestedvideo"));
					Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID",
							selectedItemID + "");
					System.out.println("SelectedVideo: " + selectedItemID);
				} else {
					// System.out.println("Set Bordercolor: at Node: "+ i);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (i + 1), "bordercolor", "ffffff");
				}
			}
		}
		blockView9(selectedItemID - 1, nodes.size(), false);
	}

	public void goPrev(Screen s, JSONObject data) {
		System.out.println("PREV!!");
		// System.out.println("PREV!! ="+data.toJSONString());
		fsPointer = FSListManager.get("/domain/senso/user/rbb/collection/pointer", false);
		nodesPointer = fsPointer.getNodes();
		selectedItemID = Integer.parseInt(nodesPointer.get(0).getProperty("selectedID"));
		if (selectedItemID != -1) {
			System.out.println("Selected Item: " + selectedItemID);
			Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID + 1), "bordercolor",
					"ffffff");
			if (selectedItemID == 0) {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + sizeStart, "bordercolor",
						"ff5722");
				selectedItemID = sizeStart;
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID", sizeStart + "");
				System.out.println("Selected Item: " + selectedItemID);
				screen.setProperty("requestedvideo", selectedItemID);
				System.out.println("Set Property Requested Video: " + screen.getProperty("requestedvideo"));
			} else {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID), "bordercolor",
						"ff5722");
				selectedItemID--;
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID",
						(selectedItemID) + "");
				System.out.println("Selected Item: " + selectedItemID);
				screen.setProperty("requestedvideo", selectedItemID);
				System.out.println("Set Property Requested Video: " + screen.getProperty("requestedvideo"));
			}
		}
		blockView9(selectedItemID + 1, sizeStart, false);
	}

	public void goNext(Screen s, JSONObject data) {
		System.out.println("NEXT !!");
		// System.out.println("NEXT !! ="+data.toJSONString());
		fsPointer = FSListManager.get("/domain/senso/user/rbb/collection/pointer", false);
		nodesPointer = fsPointer.getNodes();
		selectedItemID = Integer.parseInt(nodesPointer.get(0).getProperty("selectedID"));
		if (selectedItemID != -1) {
			System.out.println("Selected Item: " + selectedItemID);
			Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID + 1), "bordercolor",
					"ffffff");
			if ((selectedItemID + 2) > sizeStart) {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/1", "bordercolor", "ff5722");
				selectedItemID = 0;
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID", "0");
				System.out.println("Selected Item: " + selectedItemID);
				screen.setProperty("requestedvideo", selectedItemID);
				System.out.println("Set Property Requested Video: " + screen.getProperty("requestedvideo"));
			} else {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID + 2),
						"bordercolor", "ff5722");
				selectedItemID++;
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID",
						selectedItemID + "");
				System.out.println("Selected Item: " + selectedItemID);
				screen.setProperty("requestedvideo", selectedItemID);
				System.out.println("Set Property Requested Video: " + screen.getProperty("requestedvideo"));
			}
		}

		blockView9(selectedItemID + 1, sizeStart, true);
	}

	public void goDown(Screen s, JSONObject data) {
		System.out.println("DOWN !!");
		// System.out.println("DOWN !! ="+data.toJSONString());
		fsPointer = FSListManager.get("/domain/senso/user/rbb/collection/pointer", false);
		nodesPointer = fsPointer.getNodes();
		selectedItemID = Integer.parseInt(nodesPointer.get(0).getProperty("selectedID"));

		if (selectedItemID != -1) {
			System.out.println("Selected Item: " + selectedItemID);
			Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID + 1), "bordercolor",
					"ffffff");
			// System.out.println("node-size:" + nodes.size());
			if ((selectedItemID + 4) > sizeStart) {
				// System.out.println("jump to start");
				Fs.setProperty(
						"/domain/senso/user/rbb/collection/homepage/video/" + (2 - ((sizeStart - 2) - selectedItemID)),
						"bordercolor", "ff5722");
				selectedItemID = 1 - ((sizeStart - 2) - selectedItemID);
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID",
						selectedItemID + "");
				System.out.println("Selected Item: " + selectedItemID);
				// System.out.println("Go to video:" +
				// (2-((nodes.size()-2)-i)));
			} else {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID + 4),
						"bordercolor", "ff5722");
				selectedItemID = selectedItemID + 3;
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID",
						selectedItemID + "");
				System.out.println("Selected Item: " + selectedItemID);
			}
		}
		blockView9(selectedItemID + 1, sizeStart, true);
	}

	public void goUp(Screen s, JSONObject data) {
		System.out.println("UP !!");
		// System.out.println("DOWN !! ="+data.toJSONString());
		fsPointer = FSListManager.get("/domain/senso/user/rbb/collection/pointer", false);
		nodesPointer = fsPointer.getNodes();
		selectedItemID = Integer.parseInt(nodesPointer.get(0).getProperty("selectedID"));
		if (selectedItemID != -1) {
			Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID + 1), "bordercolor",
					"ffffff");
			if ((selectedItemID - 4) < 0) {
				Fs.setProperty(
						"/domain/senso/user/rbb/collection/homepage/video/" + ((sizeStart + (-3 + selectedItemID) + 1)),
						"bordercolor", "ff5722");
				selectedItemID = sizeStart + (-3 + selectedItemID);
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID",
						selectedItemID + "");
				System.out.println("Selected Item: " + selectedItemID);
			} else {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (selectedItemID - 2),
						"bordercolor", "ff5722");
				selectedItemID = selectedItemID - 3;
				Fs.setProperty("/domain/senso/user/rbb/collection/pointer/pointer/1", "selectedID",
						selectedItemID + "");
				System.out.println("Selected Item: " + selectedItemID);
			}
		}
		blockView9(selectedItemID + 1, sizeStart, false);
	}

	public static void blockView9(int selectedItem, int itemNumbers, boolean down) {

		// Grid-Part-View of 9 items ------ Start
		// FSList fslist =
		// FSListManager.get("/domain/senso/user/rbb/collection/homepage",
		// false);
		// List<FsNode> nodes = fslist.getNodes();
		// FsNode node = null;
		int selectedItemID = selectedItem;
		int size = itemNumbers;

		/*
		 * for (int i = 0; i < nodes.size(); i++) { node = nodes.get(i); String
		 * bordercolor = node.getProperty("bordercolor"); if
		 * (bordercolor.equals("ffff00")) { selectedItemID =
		 * Integer.parseInt(node.getId()); System.out.println(
		 * "Found selected item with ID: " + selectedItemID); } }
		 */

		if (selectedItemID == -1) {
			for (int i = 0; i < 9; i++) {
				System.out.println("Display node: " + i);
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (i + 1), "display", "true");
			}
			for (int i = 9; i < size; i++) {
				System.out.println("Hide node: " + i);
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (i + 1), "display", "");
			}
			return;

		}

		if (selectedItemID >= 0 && selectedItemID <= 6) {
			System.out.println("First two Rows, Item: " + selectedItemID);
			for (int j = selectedItemID - 7; j < selectedItemID + 10; j++) {
				if (selectedItemID > -1 && j > 8) {
					System.out.println("Hide node: " + j);
					// org.springfield.fs.Fs.insertNode(nodes.get(j),
					// "/domain/senso/user/rbb/collection/homepage");
					// org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+(j+1)+"/properties/hidden");
					// Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
			for (int k = size - 9; k < size; k++) {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (k + 1), "display", "");
			}

			return;
		}

		if (selectedItemID <= size + 1 && selectedItemID >= size - 9) {
			System.out.println("Last two Rows, Item: " + selectedItemID);
			for (int j = size - 9; j < size; j++) {
				if (selectedItemID > -1 && j < (size - 9)) {
					System.out.println("Hide node: " + j);
					// org.springfield.fs.Fs.insertNode(nodes.get(j),
					// "/domain/senso/user/rbb/collection/homepage");
					// org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+(j+1)+"/properties/hidden");
					// Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
			for (int k = 0; k < 9; k++) {
				Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (k + 1), "display", "");
			}
			return;
		}

		if (selectedItemID % 3 == 1 && down) {
			System.out.println("Left column, down: " + selectedItemID % 3);
			for (int j = selectedItemID - 10; j < selectedItemID + 10; j++) {
				if (selectedItemID > -1 && selectedItemID < size && (j < (selectedItemID - 7))
						|| (j > (selectedItemID + 1))) {
					System.out.println("Hide node: " + j);
					// org.springfield.fs.Fs.insertNode(nodes.get(j),
					// "/domain/senso/user/rbb/collection/homepage");
					// org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+(j+1)+"/properties/hidden");
					// Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
		} else if (selectedItemID % 3 == 2 && down) {
			System.out.println("Center column, down: " + selectedItemID % 3);
			for (int j = selectedItemID - 12; j < selectedItemID + 10; j++) {
				if (selectedItemID > -1 && selectedItemID < size && (j < (selectedItemID - 8))
						|| (j > (selectedItemID + 0))) {
					System.out.println("Hide node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
		} else if (selectedItemID % 3 == 0 && down) {
			System.out.println("right column, down: " + selectedItemID % 3);
			for (int j = selectedItemID - 12; j < selectedItemID + 10; j++) {
				if (selectedItemID > -1 && selectedItemID < size && (j < (selectedItemID - 9))
						|| (j > (selectedItemID - 1))) {
					System.out.println("Hide node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
		} else if (selectedItemID % 3 == 1 && !down) {
			System.out.println("Left column, up: " + selectedItemID % 3);
			for (int j = selectedItemID - 10; j < selectedItemID + 15; j++) {
				if (selectedItemID > -1 && selectedItemID < size + 1 && (j < (selectedItemID - 1))
						|| (j > (selectedItemID + 7))) {
					System.out.println("Hide node: " + j);
					// org.springfield.fs.Fs.insertNode(nodes.get(j),
					// "/domain/senso/user/rbb/collection/homepage");
					// org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+(j+1)+"/properties/hidden");
					// Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
		} else if (selectedItemID % 3 == 2 && !down) {
			System.out.println("Center column, up: " + selectedItemID % 3);
			for (int j = selectedItemID - 10; j < selectedItemID + 15; j++) {
				if (selectedItemID > -1 && selectedItemID < size + 1 && (j < (selectedItemID - 2))
						|| (j > (selectedItemID + 6))) {
					System.out.println("Hide node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
		} else if (selectedItemID % 3 == 0 && !down) {
			System.out.println("right up: " + selectedItemID % 3);
			for (int j = selectedItemID - 10; j < selectedItemID + 15; j++) {
				if (selectedItemID > -1 && selectedItemID < size + 1 && (j < (selectedItemID - 3))
						|| (j > (selectedItemID + 5))) {
					System.out.println("Hide node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "");
				} else {
					System.out.println("Display node: " + j);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/" + (j + 1), "display", "true");
				}
			}
		}

		else {
			System.out.println("No matching: selectedtItem % 3: " + (selectedItemID % 3));
		}

		// Grid-Part-View of 9 items ------- End

	}

}
