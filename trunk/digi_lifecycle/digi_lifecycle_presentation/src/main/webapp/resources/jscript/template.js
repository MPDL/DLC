/**
 * @author Marco Schlender, MPDL User Interface Engineer
 * @version 0.5
 */

/*
 * global function
 * to locate and select all GET parameter
 * - saving into HTTP_GET array
 * - used on index.html 
 */
var HTTP_GET = new Array();
function READ_HTTP_GET(variabl) {
	if(!location.search){
		return 'undefined';
	} else {
		var vars = new Array();
		vars = location.search.substring(1, location.search.length).split("&");
		
		for (var cc = 0; cc < vars.length; cc++) {
			var tmpVar = vars[cc].split("=");
			HTTP_GET[tmpVar[0]] = tmpVar[1];
		}
	}
}



/* 
 * this function uses ajax to load content to the wished section 
 * at first: load the content via ajax to the right area
 * at second: switch the source to save the source as VAR & VALUE in HTTP_GET array
 * @param source:String - with path and file (e.g. sample/sample.html)
 * @param target:String - object reference (e.g. an ID -> ".id_id")
 * @param callbackfunc:Function - call as String (e.g. "sample('param1')")
 */
function loadAjaxContent(loadURL, target, callbackfunc, scrollToX, scrollToY) {
	$.ajax({
		type: "GET",
		url: loadURL,
		cache: false,
		success: function(returndata){
			switch (target) {
				case ".id_content":
					$(target).append(returndata);
					break;
				default:
					$(target).html(returndata);
					break;
			}
			
			if (callbackfunc) {
				setTimeout(callbackfunc, 20);
			} 
			if (typeof(scrollToX) === "number" && typeof(scrollToY) === "number" && Number(scrollToX) >= 0 && Number(scrollToY) >= 0) { 
				window.scrollTo(scrollToX, scrollToY);
			}
		},
		error: function() {
			$(target).html("content not available");
		}
	});
	
	switch (target) {
		case ".id_content":
			if (!loadURL.match(/paginator/)) {
				HTTP_GET["page"] = loadURL;
			}
			break;
		case ".id_header":
			HTTP_GET["header"] = loadURL;
			break;
		case ".id_mainMenu":
			HTTP_GET["menu"] = loadURL;
			break;
	}
}

/*
 * primary use for directly page call
 * use as callback function
 * @param source:String - with path and file (e.g. sample/sample.html)
 */
function updatePageTitle(source) {
	var linkTag = $("[onclick*='"+source+"']");
	$(".id_siteTitle").text(linkTag.attr("title"));
/*	
	var mainMenu = $(".mainMenu");
	var level1_Ar = mainMenu.find(".level1");
	console.log(level1_Ar);
	for (var i = 0; i < level1_Ar.length; i++) {
		var ind = $(level1_Ar.get(i));
		console.log(ind.next());
		if (ind.next().children().find(linkTag)) {
			console.log('found: '+ind.contents().length);
		}
	} */
	loadAjaxContent("components/paginator/paginator_wave.html", ".id_content", "updateDynamicElements()", 0, 0);
}

function updateDynamicElements() {
	resizeSelectBox();
	addShowHideAction();
	loadAjaxDocu(".id_content");
	setTimeout("window.scrollTo(0,0)", 90);
}

/* 
 * this function load the wished content to the docu-field !as text! 
 * @param source:String - with path and file (e.g. sample/sample.html)
 * @param callbackfunc:Function - call as String (e.g. "sample('param1')")
 */
function loadAjaxDocu(source, callbackfunc) {
	$(".id_templateDocu").text($(source).html());
	if (callbackfunc) {
		setTimeout(callbackfunc, 12);
	} 
//	showDocuFieldDialog()
}

/* show the templateDocuField as dialog */
function showDocuFieldDialog() {
	var windowWidth = null;
	var windowHeight = null;
	var yOffset = null;
	
	// browser switch to get the right values
	if (window.innerWidth) {
		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;
		yOffset = window.pageYOffset;
	} else {
		windowWidth = document.body.clientWidth;
		windowHeight = document.body.clientHeight;
		yOffset = document.body.scrollTop;
	}
	
	$(".id_templateDocu").css("height", (windowHeight * 0.7));
	$(".id_templateDocuField").css("z-index", 5000);
	$(".id_templateDocuField").css("position", "absolute");
	$(".id_templateDocuField").css("background-color", "#777777");
	$(".id_templateDocuField").css("padding-left", 10);
	$(".id_templateDocuField").css("padding-right", 10);
	$(".id_templateDocuField").css("cursor", "move");
	$(".id_templateDocuField").css("left", (windowWidth - $(".id_templateDocuField").width())/2);
	$(".id_templateDocuField").css("top", ((windowHeight - $(".id_templateDocuField").height())/2) + yOffset);
	$(".id_templateDocuField").draggable();
	$(".id_templateDocuField").show();
}


/* default ready function to start the js-action on page */
$(document).ready(function(ev) {
	READ_HTTP_GET();
//	loadAjaxContent("components/header/header_standard.html", ".id_header");
//	loadAjaxContent("components/menu/menu_horz_level3.html", ".id_mainMenu");
	
	var folder = HTTP_GET["page"].split("/")[1];
	
	if (HTTP_GET["page"] != "undefined" && HTTP_GET["page"] != undefined) {
		$(".id_content").html("");
		
		var loading = HTTP_GET["page"];
		if (loading.substr(loading.length - 4) == "html") {
			console.log("html found");
		} else {
			loading += ".html";
		}
		
		switch (folder) {
			case 'list':
				/* loadAjaxContent(loadURL, target, callbackfunc, scrollToX, scrollToY) */
				loadAjaxContent("components/menu/optionMenu_horz_all_visible.html", ".id_optionBox", "", 0, 0);
				loadAjaxContent("components/paginator/paginator_wave.html", ".id_content", "", 0, 0);
				loadAjaxContent(loading, ".id_content", "updatePageTitle('"+loading+"')", 0, 0);
				break;
			default:
				$(".id_contentDescription").hide();
				/* loadAjaxContent(loadURL, target, callbackfunc, scrollToX, scrollToY) */
				loadAjaxContent(loading, ".id_content", "updatePageTitle('"+loading+"')", 0, 0);
				setTimeout("$('.paginatorWave').hide()", 800);
				break;
		}
	} 
});