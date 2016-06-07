var MainscreenController = function(options) {}; // needed for detection

var status = "pause";

function togglePlay() {
	console.log("status: "+ status);
	if (status == "pause"){
		console.log("change status to play");
		status = "play";
		document.getElementById("mainscreencontroller_play").parentElement.style.display = "none";
		document.getElementById("mainscreencontroller_close").parentElement.style.display = "";
	} else {
		console.log("change status to pause");
		
		document.getElementById("mainscreencontroller_play").parentElement.style.display = "";
		document.getElementById("mainscreencontroller_close").parentElement.style.display = "none";
		
		status = "pause";
	}
	
	
}