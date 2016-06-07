/* 
 * 
 * Niels Bubel, Rundfunk Berlin-Brandenburg (RBB), Innovationsprojekte
 * 7.2 - final version, 31.05.2016
 *
 * UI of the mainscreen controller, starts the SVIA functions (start, stop, navigate...)
 * 
 */

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


function toStart() {
	console.log("Reset-Position");
	document.getElementById("mainscreencontroller_pointerConfirm").style.display = "block";
}

function importVideos() {
	console.log("Import Videos");
	document.getElementById("mainscreencontroller_importConfirm").style.display = "block";
}

function deleteVideos() {
	console.log("Delete-Videos");
	document.getElementById("mainscreencontroller_clearConfirm").style.display = "block";
}

function hidePointerConfirm() {
	document.getElementById("messagePointer").innerHTML = "Bitte warten!";
	setTimeout(function(){
		document.getElementById("mainscreencontroller_pointerConfirm").style.display = "none";
		document.getElementById("messagePointer").innerHTML = "Möchten Sie zum ersten Video springen?";
	}, 10000);
}

function hideImportConfirm() {
	document.getElementById("messageImport").innerHTML = "Bitte warten!";
	setTimeout(function(){
		document.getElementById("mainscreencontroller_importConfirm").style.display = "none";
		document.getElementById("messageImport").innerHTML = "Möchten Sie alle 244 verfügbaren Mauervideos importieren?";
	}, 10000);
}


function hideClearConfirm() {
	document.getElementById("messageClear").innerHTML = "Bitte warten!";
	setTimeout(function(){
		document.getElementById("mainscreencontroller_clearConfirm").style.display = "none";
		document.getElementById("messageClear").innerHTML = "Möchten Sie alle Videos in der Anwendung löschen?";
	}, 10000);
}

function hidePointer() {
	document.getElementById("mainscreencontroller_pointerConfirm").style.display = "none";
}

function hideImport() {
	document.getElementById("mainscreencontroller_importConfirm").style.display = "none";
}

function hideClear() {
	document.getElementById("mainscreencontroller_clearConfirm").style.display = "none";
}

