var AudioController = function(options) {}; // needed for detection
var language = null;

AudioController.update = function(vars, data){
	// get out targetid from our local vars
	//var targetid = data['targetid'];
	//$("#audiop").attr('autoplay', true);
	//$("#audiop2").attr('autoplay', false);
	//$("#audiop").attr('controls',data['controls']);
	//$("#audiop2").attr('controls',data['controls']);
	//$("#" + targetid).parent().append("<div id=\"notificationbox\" style=\"position: absolute; top: 0;\"></div>");
	
	console.log("action: " + data['action']);
	console.log("data: " + JSON.stringify(data));
	console.log("id: " + data['targetid']);
	console.log("autoplay: " + data['autoplay']);	
	console.log("controls: " + data['controls']);	
	
	console.log(language);
	console.log(document.getElementById("version1"));
	
	if (language == null && ((document.getElementById("version1")) != null) && (data['autoplay'] == "true")){
	language = "reset";
	changeToGerman();
	} else if (language == "english"){
		language = "reload";
		changeToEnglish();
	} else if (language == "german"){
		language = "reload";
		changeToGerman();
	}
	
	var action = data['action'];	
	switch (action) {
	case "pause":
		$("#"+targetid)[0].pause();
		break;
	case "play":
		$("#"+targetid)[0].play();
		break;
	case "wantedtime":
	    var wt = data['wantedtime'].split(',');
	    var realtime = wt[0];
	    var streamtime = wt[1];
		var audiotime = $("#audiop")[0].currentTime*1000;
		var audiotime2 = $("#audiop2")[0].currentTime*1000;
		var curtime = new Date().getTime()-window.timeoffset; 
	
		// so in realtime-curtime we want to be at streamtime !
		var timegap = realtime-curtime;
		
		// where will we really be ?
		var expectedtime = audiotime + timegap;
		var expectedtime2 = audiotime2 + timegap;
		
		// so how far are we off ?
		var delta = streamtime - expectedtime;
		var delta2 = streamtime - expectedtime2;
		
		console.log('RT='+realtime+' ST='+streamtime+' CT='+curtime+' AT='+audiotime+' AT2='+audiotime2+' TG='+timegap+' ET='+expectedtime+' ET2='+expectedtime2+" DELTA="+delta+" DELTA2="+delta2);
		
		// lets act on it part 1
		if (delta<-1000 || delta>1000) {
			var newtime = ((audiotime+delta)+200)/1000;
			console.log('seekto='+newtime);
			$("#audiop")[0].currentTime = newtime;
		} else {
		    var speedup = 1;
		    if (delta<0) {
		    	console.log('neg='+delta);
		    	if (delta<-200) {
					speedup = 0.97;
				} else if (delta<-100) {
					speedup = 0.98;
				} else if (delta<-50) {
					speedup = 0.99;
				} else {
					speedup = 1;
				}
		    } else {    
		    	console.log('pos='+delta);
				if (delta<50) {
					speedup = 1.00;
				} else if (delta<200) {
					speedup = 1.01;
				} else if (delta<400) {
					speedup = 1.02;
				} else {
					speedup = 1.03;
				}
			}
			
			console.log('speedup='+speedup);
		   // so if are within 500ms lets speedup and see if we can catch it
		   $("#audiop")[0].playbackRate = speedup;
		   
		}
		
		// lets act on it part 2
		if (delta2<-1000 || delta2>1000) {
			var newtime2 = ((audiotime2+delta2)+200)/1000;
			console.log('seekto='+newtime2);
			$("#audiop2")[0].currentTime = newtime2;
		} else {
		    var speedup2 = 1;
		    if (delta2<0) {
		    	console.log('neg2='+delta2);
		    	if (delta2<-200) {
					speedup2 = 0.97;
				} else if (delta2<-100) {
					speedup2 = 0.98;
				} else if (delta2<-50) {
					speedup2 = 0.99;
				} else {
					speedup2 = 1;
				}
		    } else {    
		    	console.log('pos2='+delta2);
				if (delta2<50) {
					speedup2 = 1.00;
				} else if (delta2<200) {
					speedup2 = 1.01;
				} else if (delta2<400) {
					speedup2 = 1.02;
				} else {
					speedup2 = 1.03;
				}
			}
			
			console.log('speedup2='+speedup2);
		   // so if are within 500ms lets speedup and see if we can catch it
		   $("#audiop2")[0].playbackRate = speedup2;
		   
		}
		break;
	case "seek":
		console.log('s='+data['seekingvalue'] / 1000);
		$("#"+targetid)[0].currentTime = data['seekingvalue'] / 1000;
		break;
	case "autoplay":
		$("#"+targetid)[0].autoplay = true;
		break;
	case "volumechange":
		$("#"+targetid)[0].volume  = data['volume'] / 100;
		break;
	case "newvideo":
		$("#videosrc").attr("src","http://images1.noterik.com/videoremote/"+data['mp4']+".mp4");
		$("#"+targetid)[0].load();
		console.log('mp4='+data['mp4']);
		break;
	default:
		break;
	}
};

function changeToGerman() {
	console.log("language: "+ language);
	if (language == "german" || language == null){
		console.log("Language is allready german!");
		language = "german";
		console.log("language: "+ language);
	} else {
		console.log("Change Language to german!");
		document.getElementById("german").setAttribute("class", "disabled pressed waves-effect waves-light btn");
		document.getElementById("english").setAttribute("class", "unpressed waves-effect waves-light btn");
		document.getElementById("pic1").setAttribute("src", "http://rbb.noterik.com/eddie/apps/videoremote/img/ger_grey.png");
		document.getElementById("pic2").setAttribute("src", "http://rbb.noterik.com/eddie/apps/videoremote/img/gb.png");
		document.getElementById("version1").style.display = "block";
		document.getElementById("version2").style.display = "none";
		$("#audiop")[0].autoplay = true;
		$("#audiop2")[0].autoplay = false;
		$("#audiop")[0].play();
		$("#audiop2")[0].pause();
		language = "german";
		console.log("language: "+ language);
	}
	
	
}

function changeToEnglish() {
	console.log("language: "+ language);
	if (language == "english"){
		console.log("Language is allready english!");
	} else {
		console.log("Change Language to english!");
		document.getElementById("english").setAttribute("class", "disabled pressed waves-effect waves-light btn");
		document.getElementById("german").setAttribute("class", "unpressed waves-effect waves-light btn");
		document.getElementById("pic1").setAttribute("src", "http://rbb.noterik.com/eddie/apps/videoremote/img/ger.png");
		document.getElementById("pic2").setAttribute("src", "http://rbb.noterik.com/eddie/apps/videoremote/img/gb_grey.png");
		document.getElementById("version1").style.display = "none";
		document.getElementById("version2").style.display = "block";
		$("#audiop")[0].autoplay = false;
		$("#audiop2")[0].autoplay = true;
		$("#audiop")[0].pause();
		$("#audiop2")[0].play();
		language = "english";
		console.log("language: "+ language);
	}
	
	
}