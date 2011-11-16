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

//global function to combine all functions to stop immediately default actions
function stopDefaultAction(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
}

/* this function search for the parent node */
function searchParentTag(source_obj, searchTagString) {
	for (var i=0; i < $(source_obj).parents().length; i++) {
		if ($($(source_obj).parents()[i]).hasClass(searchTagString)) {
			return $($(source_obj).parents()[i]);
			break;
		}
	}
	return false;
}

/* 
 * this function uses ajax to load content to the wished section 
 * at first: load the content via ajax to the right area
 * at second: switch the source to save the source as VAR & VALUE in HTTP_GET array
 * @param source:String - with path and file (e.g. sample/sample.html)
 * @param target:String - object reference (e.g. an ID -> ".id_id")
 * @param callbackfunc:Function - call as String (e.g. "sample('param1')")
 */
function loadAjaxContent(loadURL, target, callbackfunc) {
	$.ajax({
		type: "GET",
		url: loadURL,
		cache: false,
		success: function(returndata){
			$(target).html(returndata);
			if (callbackfunc) {
				setTimeout(callbackfunc, 20);
			} 
		},
		error: function() {
			$(target).html("content not available");
		}
	});
	
	switch (target) {
		case '.id_content':
			HTTP_GET["page"] = loadURL;
			loadAjaxDocu('.id_siteContent');
			break;
		case '.id_header':
			HTTP_GET["header"] = loadURL;
			break;
		case '.id_mainMenu':
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
	
	
	
	var mainMenu = $(".mainMenu");
	var level1_Ar = mainMenu.find(".level1");
	console.log(level1_Ar);
	for (var i = 0; i < level1_Ar.length; i++) {
		var ind = $(level1_Ar.get(i));
		console.log(ind.next());
		if (ind.next().children().find(linkTag)) {
			console.log('found: '+ind.contents().length);
		}
	}
}

/* 
 * this function uses ajax to load content to the wished section !as text! 
 * at first: switch the content to read the right var from HTTP_GET array
 * at second: load the content to the docu textarea
 * at last: set position for the docu dialog and show it
 * @param source:String - with path and file (e.g. sample/sample.html)
 * @param callbackfunc:Function - call as String (e.g. "sample('param1')")
 */
function loadAjaxDocu(source, callbackfunc) {
	
	var loadURL = '';
	switch (source) {
		case '.id_siteHeader':
			loadURL = HTTP_GET["header"];
			break;
		case '.id_siteContent':
			loadURL = HTTP_GET["page"];
			break;
		default:
			loadURL = source;
			break;
	}
	if (loadURL != '') {
		$.ajax({
			type: "GET",
			url: loadURL,
			cache: false,
			success: function(returndata){
				$(".id_templateDoc.id_").text(returndata);
				if (callbackfunc) {
					setTimeout(callbackfunc, 12);
				} 
			},
			error: function() {
				$(".id_templateDocu").text("content not available");
			}
		});
		/*
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
		$(".id_templateDocuField").css("left", (windowWidth - $(".id_templateDocuField").width())/2);
		$(".id_templateDocuField").css("top", ((windowHeight - $(".id_templateDocuField").height())/2) + yOffset);
		*/
		$(".id_templateDocuField").show();
	}
}

/* default ready function to start the js-action on page */
$(document).ready(function(ev){
//	loadAjaxContent("components/menu/templateMenu_accordion_01.html", ".id_mainMenu");
//	loadAjaxContent("components/header/standard_header.html", ".id_header");
	
	READ_HTTP_GET();
	if (HTTP_GET["page"] != "undefined" && HTTP_GET["page"] != undefined) {
		loadAjaxContent(HTTP_GET["page"]+".html", ".id_content", "updatePageTitle('"+HTTP_GET["page"]+".html')");
	} 
	
	$(".id_templateDocuField").draggable();
});