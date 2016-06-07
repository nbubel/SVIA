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

public class MainscreenController extends Html5Controller{
	
	
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
	 
	}
	
	public void removeAllVideos(Screen s, JSONObject data) {
		
		System.out.println("Remove all videos!");
		
		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false);
		
		List<FsNode> nodes = fslist.getNodes();
		for(int i=0;i<nodes.size();i++ ) {
			System.out.println("Node out of DB " + i + ": " + nodes.get(i).asXML());
			boolean deleted = org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+nodes.get(i).getId());
			System.out.println("Deleted succsessfull?: " + deleted);	
		}
		
	}
	
	public void importXML(Screen s, JSONObject data) {
		System.out.println("Update - Liste!");

		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false); 
		
		List<FsNode> nodes = fslist.getNodes();
		for(int i=0;i<nodes.size();i++ ) {
			System.out.println("Node " + i + ": " + nodes.get(i).asXML());
		}
		
		File file = new File("webapps/ROOT/eddie/apps/videoremote/img/20160229-wall-videos-german.xml");

		
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
		
		for(int i=0;i<doc.getElementsByTagName("titel").getLength();i++ ) {
			FsNode node = new FsNode("video");
			node.setProperty("title", doc.getElementsByTagName("titel").item(i).getTextContent());
			node.setProperty("date", doc.getElementsByTagName("datum").item(i).getTextContent());
			node.setProperty("img", doc.getElementsByTagName("img").item(i).getTextContent());
			node.setProperty("mp4", doc.getElementsByTagName("mp4").item(i).getTextContent());
			node.setId("" + (nodes.size()+1));
			System.out.println("created new node: " + node.asXML());
			
			fslist.addNode(node);		
		}
		
		
		nodes = fslist.getNodes();
		for(int i=0;i<nodes.size();i++ ) {
			System.out.println("Node to DB " + i + ": " + nodes.get(i).asXML());
			boolean inserted = org.springfield.fs.Fs.insertNode(nodes.get(i), "/domain/senso/user/rbb/collection/homepage");
			System.out.println("Insertion succsessfull?: " + inserted);	
		}
		
		/*
		System.out.println("Put fslist to DB");
		FSListManager.put("/domain/senso/user/rbb/collection/homepage", fslist);
		*/
		


		// String bordercolor = node.getProperty("bordercolor");
		// if (bordercolor!=null && bordercolor.equals("ffff00")) {
		// VideoremoteApplication app =
		// (VideoremoteApplication)screen.getApplication();
		// app.setProperty("/videostate/"+app.masterclock +"/action",
		// "startvideo,"+(i+1));
		// }
	}
    	
    
	
    public void startVideo(Screen s,JSONObject data) {
		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage",false); // get the results from the database
		List<FsNode> nodes = fslist.getNodes();
		if (nodes!=null) { 
			for(int i=0;i<nodes.size();i++ ) {
				FsNode node = nodes.get(i);
				String bordercolor = node.getProperty("bordercolor");
				if (bordercolor!=null && bordercolor.equals("ffff00")) {
					VideoremoteApplication app = (VideoremoteApplication)screen.getApplication();
					app.setProperty("/videostate/"+app.masterclock +"/action", "startvideo,"+(i+1));
				}
			}
		}
    }
    
    public void closeVideo(Screen s,JSONObject data) {
		VideoremoteApplication app = (VideoremoteApplication)screen.getApplication();
		app.setProperty("/videostate/"+app.masterclock +"/action", "closevideo");
    }
    
    public void setPointer(Screen s,JSONObject data) {
    	System.out.println("Set pointer to the first video");
    	FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage",false); // get the results from the database
		List<FsNode> nodes = fslist.getNodes();
		int selectedItemID = -1;
		if (nodes!=null) { 
			for(int i=0;i<nodes.size();i++ ) {
				if (i == 0) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/1","bordercolor","ffff00");
				} else {
					// System.out.println("Set Bordercolor:  at Node: "+ i);
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+1),"bordercolor","");
				}
			}
		}
		blockView9(selectedItemID, nodes.size());
    } 
    
    

	
    public void goPrev(Screen s,JSONObject data) {
    	System.out.println("PREV!!");
    	// System.out.println("PREV!! ="+data.toJSONString());
    	FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage",false); // get the results from the database
		List<FsNode> nodes = fslist.getNodes();
		int selectedItemID = -1;
		if (nodes!=null) { 
			for(int i=0;i<nodes.size();i++ ) {
				FsNode node = nodes.get(i);
				// System.out.println("NODE="+node.asXML());
				String bordercolor = node.getProperty("bordercolor");
				if (bordercolor!=null && bordercolor.equals("ffff00")) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+1),"bordercolor","");
					if (i==0) {
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+nodes.size(),"bordercolor","ffff00");
						selectedItemID = nodes.size();
					} else {
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i),"bordercolor","ffff00");
						selectedItemID = i;
					}
				} 
			}
			blockView9(selectedItemID, nodes.size());
		}
    }
    
    public void goNext(Screen s,JSONObject data) {
    	System.out.println("NEXT !!");
    	// System.out.println("NEXT !! ="+data.toJSONString());
		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage",false); // get the results from the database
		List<FsNode> nodes = fslist.getNodes();
		int selectedItemID = -1;
		if (nodes!=null) { 
			for(int i=0;i<nodes.size();i++ ) {
				FsNode node = nodes.get(i);
				// System.out.println("NODE="+node.asXML());
				String bordercolor = node.getProperty("bordercolor");
				if (bordercolor!=null && bordercolor.equals("ffff00")) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+1),"bordercolor","");
					if ((i+2)>nodes.size()) {
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/1","bordercolor","ffff00");
						selectedItemID = 1;
					} else {
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+2),"bordercolor","ffff00");
						selectedItemID = i+2;
					}
				}
			}
			blockView9(selectedItemID, nodes.size());
		}
    }
    
    
    public void goDown(Screen s,JSONObject data) {
    	System.out.println("DOWN !!");
    	// System.out.println("DOWN !! ="+data.toJSONString());
		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage",false); // get the results from the database
		List<FsNode> nodes = fslist.getNodes();
		int selectedItemID = -1;
		if (nodes!=null) { 
			for(int i=0;i<nodes.size();i++ ) {
				FsNode node = nodes.get(i);
				// System.out.println("NODE="+node.asXML());
				String bordercolor = node.getProperty("bordercolor");
				if (bordercolor!=null && bordercolor.equals("ffff00")) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+1),"bordercolor","");
					// System.out.println("node-size:" + nodes.size());
					if ((i+4)>nodes.size()) {
						//System.out.println("jump to start");
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(2-((nodes.size()-2)-i)),"bordercolor","ffff00");
						selectedItemID = 2-((nodes.size()-2)-i);
						//System.out.println("Go to video:" + (2-((nodes.size()-2)-i)));
					} else {
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+4),"bordercolor","ffff00");
						selectedItemID = i+4;
					}
				}
			}
			blockView9(selectedItemID, nodes.size());
		}
    }
    
    public void goUp (Screen s,JSONObject data) {
    	System.out.println("UP !!");
    	// System.out.println("DOWN !! ="+data.toJSONString());
		FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage",false); // get the results from the database
		List<FsNode> nodes = fslist.getNodes();
		int selectedItemID = -1;
		if (nodes!=null) { 
			for(int i=0;i<nodes.size();i++ ) {
				FsNode node = nodes.get(i);
				// System.out.println("NODE="+node.asXML());
				String bordercolor = node.getProperty("bordercolor");
				if (bordercolor!=null && bordercolor.equals("ffff00")) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+1),"bordercolor","");
					if ((i-3)<0) {
						if (i == 0){
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-2),"bordercolor","ffff00");
							selectedItemID = nodes.size()-2;
						} else {
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
							selectedItemID = nodes.size()-(2-i);
						}
					} else {
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i-2),"bordercolor","ffff00");
						selectedItemID = i-2;
					}
				}
			}
			blockView9(selectedItemID, nodes.size());
		}
    }
    
public static void blockView9(int selectedItem, int itemNumbers){
		
    	
    	// Grid-Part-View of 9 items ------ Start
				//FSList fslist = FSListManager.get("/domain/senso/user/rbb/collection/homepage", false);
				//List<FsNode> nodes = fslist.getNodes();
				//FsNode node = null;
				int selectedItemID = selectedItem;
				int size = itemNumbers;

				/*
				for (int i = 0; i < nodes.size(); i++) {
					node = nodes.get(i);
					String bordercolor = node.getProperty("bordercolor");
					if (bordercolor.equals("ffff00")) {
						selectedItemID = Integer.parseInt(node.getId());
						System.out.println("Found selected item with ID: " + selectedItemID);
					}
				}
				*/
				
				if (selectedItemID == -1){
					for (int i = 0; i < 9; i++) {
						System.out.println("Display node: " + i);
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+1),"display","true");	
					}
					for (int i = 9; i < size; i++) {
						System.out.println("Hide node: " + i);
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(i+1),"display","");
					}
					return;
					
				}
				
				if (selectedItemID == 0 || selectedItemID == 1 || selectedItemID == 2 || selectedItemID == 3){
					System.out.println("First Row, Item: " + selectedItemID);
				for (int j = selectedItemID-3; j < selectedItemID+10; j++) {
					if (selectedItemID > -1 && j > 8) {
						System.out.println("Hide node: " + j);
						// org.springfield.fs.Fs.insertNode(nodes.get(j), "/domain/senso/user/rbb/collection/homepage");
						//org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+(j+1)+"/properties/hidden");
						//Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","");
					} else {
						System.out.println("Display node: " + j);
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","true");						
					}
				} 
				for (int k = size-9; k < size; k++) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(k+1),"display","");	
				}
				
				return;
				}
				
				if (selectedItemID == size || selectedItemID == size-1  || selectedItemID == size-2){
					System.out.println("Last Row, Item: " + selectedItemID);
				for (int j = size-9; j < size; j++) {
					if (selectedItemID > -1 && j < (size-9)) {
						System.out.println("Hide node: " + j);
						// org.springfield.fs.Fs.insertNode(nodes.get(j), "/domain/senso/user/rbb/collection/homepage");
						//org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+(j+1)+"/properties/hidden");
						//Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","");
					} else {
						System.out.println("Display node: " + j);
						Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","true");						
					}
				} 
				for (int k = 0; k < 9; k++) {
					Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(k+1),"display","");	
				}
				return;
				}
					

				if (selectedItemID % 3 == 1) {
					System.out.println("Left column: " + selectedItemID % 3);
					for (int j = selectedItemID-10; j < selectedItemID+10; j++) {
						if (selectedItemID > -1 && selectedItemID < size && (j < (selectedItemID - 4)) || (j > (selectedItemID + 4))) {
							System.out.println("Hide node: " + j);
							// org.springfield.fs.Fs.insertNode(nodes.get(j), "/domain/senso/user/rbb/collection/homepage");
							//org.springfield.fs.Fs.deleteNode("/domain/senso/user/rbb/collection/homepage/video/"+(j+1)+"/properties/hidden");
							//Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(nodes.size()-(2-i)),"bordercolor","ffff00");
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","");
						} else {
							System.out.println("Display node: " + j);
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","true");						
						}
					}
				} else if (selectedItemID % 3 == 2) {
					System.out.println("Center column: " + selectedItemID % 3);
					for (int j = selectedItemID-10; j < selectedItemID+10; j++) {
						if (selectedItemID > -1 && selectedItemID < size && (j < (selectedItemID - 5)) || (j > (selectedItemID + 3))) {
							System.out.println("Hide node: " + j);
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","");
						} else {
							System.out.println("Display node: " + j);
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","true");						
						}
					}
				} else if (selectedItemID % 3 == 0) {
					System.out.println("right column: " + selectedItemID % 3);
					for (int j = selectedItemID-10; j < selectedItemID+10; j++) {
						if (selectedItemID > -1 && selectedItemID < size && (j < (selectedItemID - 6)) || (j > (selectedItemID + 2))) {
							System.out.println("Hide node: " + j);
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","");
						} else {
							System.out.println("Display node: " + j);
							Fs.setProperty("/domain/senso/user/rbb/collection/homepage/video/"+(j+1),"display","true");						
						}
					}
				} else {
					System.out.println("No matching: selectedtItem % 3: " + (selectedItemID % 3));
				}

				// Grid-Part-View of 9 items ------- End

    }
    
	
	}
