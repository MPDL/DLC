/**
 * @author Marco Schlender, MPDL User Interface Engineer
 * @version 0.5
 */

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
 * these function delete the searched value in an attribute
 */
function removeAttributeValue(reference, attribute, value) {
	var refObj = (typeof(reference) == "object") ? reference : $(reference);
	var attr = $.trim(refObj.attr(attribute));
	
	if (attr) {
		var posFound = false;
		var output = '';
		attr = attr.split(';');
		for (var ai = 0; ai < attr.length; ai++) {
			if ($.trim(attr[ai])) {
				var attrVal = attr[ai].split(':');
				if ($.trim(attrVal[0]) == value) {
					posFound = true;
				} else {
					output += attr[ai]+'; ';
				}
			}
		}
		if (posFound) {
			output = $.trim(output);
			if (!output || output == '') {
				refObj.removeAttr(attribute);
			} else {
				refObj.attr(attribute, $.trim(output));
			}
		}
	}
}

/* this function updates the selectText container with the selected item of selectbox */
function updateCustomSelectBox(obj) {
	if (!obj) {
		$("select").each(function(i){
			var parent = null;
			if (parent = searchParentTag(this, "eg3_dynamicSelectBox_js")) {
				var val = $(this).val();
				$(this).find("option").each(function(i){
					if ($(this).val() == val) {
						val = $(this).text();
						$(this).parent().attr("title", val);
					}
				});
				$(parent).find(".eg3_selectionText").html(val).attr("title", val);
			}
		});
	} else {
		$(obj).prev().find('.eg3_selectionText').text(obj.options[obj.selectedIndex].text);
	}
}

function extractNumberFromString(str, delimiter, noPos) {
	var output = false;
	output = Number(str.split(delimiter)[noPos]);
	if (output == "NaN") { output = false; }
	return output;
}

function checkForMaxWidth(obj, widthAttr) {
	if (obj.attr(widthAttr).match(/eg3_maxWidth_/)) {
		//newInfoWidth = Number(maxWidth) - marR;
		var tmpAr = obj.attr(widthAttr).split(" ");
		
		for (pos in tmpAr) {
			if (tmpAr[pos].match(/eg3_maxWidth_/)) {
				var tmpVal = extractNumberFromString(tmpAr[pos], "_", 2);
				console.log("hallo new Width: "+tmpVal);
				return tmpVal;
				break;
			}
		}
	}
	return false;
}

/* 
 * resize all dynamic selectBox container to the correct size of invisible selectbox 
 */
function resizeSelectBox() {
	$(".eg3_dynamicSelectBox_js").each(function(ind, dynBox){
		var selCont = null;
		if (typeof(selCont = $(dynBox).find(".eg3_selectionContent")) != "undefined") {
			var selectTag = $(dynBox).find('select');
			var padL = Number(selCont.css("padding-left").replace("px", ""));
			var padR = Number(selCont.css("padding-right").replace("px", ""));
			var marL = Number(selCont.css("margin-left").replace("px", ""));
			var marR = Number(selCont.css("margin-right").replace("px", ""));
			var newInfoWidth = Math.floor( (selectTag.width() - marR) );
			
			var newContainerWidth = Math.floor( (newInfoWidth + marR + marL + padR + padL) );
			var maxWidth = checkForMaxWidth($(dynBox), "class");
			console.log(newContainerWidth + ' maxW: ' + maxWidth);
			if (maxWidth && newContainerWidth > maxWidth) {
				newContainerWidth = maxWidth;
				newInfoWidth = maxWidth - 2*marR;
			}
			
			selCont.css("width", newInfoWidth);
			$(dynBox).css("width", newContainerWidth);
			selectTag.css("width", newContainerWidth);
			/* add behaviour for change and focus */
			//selectTag.bind("focus", function(evt) { updateCustomSelectBox(this); } );
			selectTag.bind("change", function(evt) { updateCustomSelectBox(this); } );
		}
	});
	
	updateCustomSelectBox();
}


function addShowHideAction() {
	/*append listener to the .showHideAll_js-Tag for opening and closing all details in the current list*/
	$('.eg3_showHideAll_js').click(function(e){
		$(this).toggleClass("eg3_icon_collapse_16_16 eg3_icon_expand_16_16");
		stopDefaultAction(e);
		var listBody = null;
		
		for (var i=0; i < $(this).parents().length; i++) {
			//read the parents array to listHeader and stop, take the listHeader as parent and starting point for selection
			if ($($(this).parents()[i]).hasClass('eg3_listHeader')) {
				listHead = $($(this).parents()[i]);
				listBody = $($(this).parents()[i]).siblings();
				break;
			}
		}
		if (listBody) {
			var allItemDetailActions = $(listBody.find(".eg3_itemDetailAction_js"));
			if ( !$(this).attr("eg3_detailStatus") || $(this).attr("eg3_detailStatus") == 'undefined' || $(this).attr("eg3_detailStatus") == undefined ) {
				$(this).attr("eg3_detailStatus", "open");
				$(this).html($.trim($(this).html()).replace("Open ", "Close "));
				listBody.find('.eg3_mediumView_js').show();
				allItemDetailActions.find("div").removeClass("eg3_icon_collapse_16_16").addClass("eg3_icon_expand_16_16");
				allItemDetailActions.find(".eg3_itemActionLabel").text("Less");
			} else {
				$(this).attr("eg3_detailStatus", "");
				$(this).html($.trim($(this).html()).replace("Close ", "Open "));
				listBody.find('.eg3_mediumView_js').hide();
				allItemDetailActions.find("div").removeClass("eg3_icon_expand_16_16").addClass("eg3_icon_collapse_16_16");
				allItemDetailActions.find(".eg3_itemActionLabel").text("More");
			}
		}
	});
	
	
}


function addDisplayControl(target) {
	switch (target) {
		case ".eg3_listItemMediaAcc":
		case ".eg3_listItem":
			$(target+" .eg3_itemDetailAction_js").click(function(e){
				stopDefaultAction(e);
				
				$(this).find("div").toggleClass("eg3_icon_expand_16_16, eg3_icon_collapse_16_16");
					
				$(this).find("div").toggleClass("eg3_icon_collapse_16_16, eg3_icon_expand_16_16");
				
				var allDetails = null;
				//read the parents array to itemContent and stop, take the itemContent as parent and starting point for selection
				if (allDetails = searchParentTag(this, 'eg3_itemContent').find('.eg3_mediumView_js')) {
					allDetails.toggle();
				};
			});
			break;
		case '.eg3_listItemMultiVolume':
			$(target + " .eg3_itemDetailAction_js").click(function(e) {
				var volume = searchParentTag(this, "eg3_listItemMultiVolume").next();
				
				if (volume.hasClass("eg3_listItemVolume")) {
					while (volume.hasClass("eg3_listItemVolume")) {
						volume.toggle();
						volume = volume.next();
					}
				}
			});
			break;
	}
}



/*
 * function to add function for show or hide all wished elements (e.g. child-elements)
 */
function addShowHideAll(element, child_elements, component) {
	var showHideAllBtn = null;
	
	//if the caller has a component, component will be formatted to an jquery object and the showHideAll button will be search into component
	if (component) {
		component = $(component);
		showHideAllBtn = component.find(element);
	} else { //else the showHideAll button will be search in document
		showHideAllBtn = $(element);
	}
	
	showHideAllBtn.click(function(e) {
		if ($(child_elements+':hidden').length > 0) {
			$(child_elements).show();
		} else {
			$(child_elements).hide();
		}
	});
}


/************************************************
 * open / close overlay menu
 * e.g. sidebar in pageView
 */
/**
 * function eg3_openOverlay
 * open the current content to see the content in a maximum container
 * listButton: object whose be clicked
 */
function eg3_openOverlay(listButton, cnt) {
	
	var curTabPanel = $(listButton).parents(".rf-tbp");
	var curTabPanelContent = curTabPanel.find(".rf-tab-cnt:visible");
	var curControlPanel = $(listButton).parents('.eg3_iconBar');
	console.log(cnt);
	switch (cnt) {
		case 'toc':
			if (curTabPanel && curTabPanel.length > 0) {
				curTabPanel.addClass("eg3_noWrapTrn");
				curTabPanel.addClass("eg3_widthAuto");
			}
			if (curTabPanelContent && curTabPanelContent.length > 0) {
				curTabPanelContent.addClass("eg3_expand");
				curTabPanelContent.addClass("eg3_widthAuto");
			}
			if (curControlPanel && curControlPanel.length > 0) {
				curControlPanel.addClass("eg3_expand");
			}
			break;
		case 'thumbs':
			var thumbs = $('.eg3_thumbnail');
			if (thumbs && thumbs.length > 0) {
				thumbs.addClass("eg3_container_1_6");
				thumbs.removeClass("eg3_container_1_2");
			}
			if (curTabPanel && curTabPanel.length > 0) {
				curTabPanel.addClass("eg3_noWrapTrn");
				curTabPanel.addClass("eg3_widthAuto");
			}
			if (curTabPanelContent && curTabPanelContent.length > 0) {
				curTabPanelContent.addClass("eg3_expand");
				curTabPanelContent.css("width", ($(window).width() * 0.8));
			}
			if (curControlPanel && curControlPanel.length > 0) {
				curControlPanel.addClass("eg3_expand");
			}
			break;
	}
	
	$('.eg3_id_sidebarLeft').addClass("eg3_expand");
	$(listButton).attr('disabled', 'disabled');
	curControlPanel.find('.eg3_collapseOverlay').removeAttr('disabled');
}
/**
 * function eg3_openOverlay
 * close the current content to see the content in a minimized container
 * listButton: object whose be clicked
 */
function eg3_closeOverlay(listButton, cnt) {
	var curTabPanel = $(listButton).parents(".rf-tbp");
	var curTabPanelContent = curTabPanel.find(".rf-tab-cnt:visible");
	var curControlPanel = $(listButton).parents('.eg3_iconBar');
	
	switch (cnt) {
		case 'toc':
			if (curTabPanel) {
				curTabPanel.removeClass("eg3_noWrapTrn");
				curTabPanel.removeClass("eg3_widthAuto");
			}
			if (curTabPanelContent) {
				curTabPanelContent.removeClass("eg3_expand");
				curTabPanelContent.removeClass("eg3_widthAuto");
			}
			if (curControlPanel) {
				curControlPanel.removeClass("eg3_expand");
			}
			break;
		case 'thumbs':
			var thumbs = $('.eg3_thumbnail');
			if (thumbs && thumbs.length > 0) {
				thumbs.addClass("eg3_container_1_2");
				thumbs.removeClass("eg3_container_1_6");
			}
			if (curTabPanel) {
				curTabPanel.removeClass("eg3_noWrapTrn");
				curTabPanel.removeClass("eg3_widthAuto");
			}
			if (curTabPanelContent) {
				curTabPanelContent.removeClass("eg3_expand");
				curTabPanelContent.css("width", "100%");
			}
			if (curControlPanel) {
				curControlPanel.removeClass("eg3_expand");
			}
			break;
	}
	
	$('.eg3_id_sidebarLeft').removeClass("eg3_expand");
	$(listButton).attr('disabled', 'disabled');
	curControlPanel.find('.eg3_expandOverlay').removeAttr('disabled');
}

/**
 * switchInputType is a function to change to container, whose controls input type formats
 * @param hide_element:String - used in case of jQuery selection
 * @param show_element:String - used in case of jQuery selection
 * @param escListener:Boolean - if set, the escape listener will change the input formats back
 */
function eg3_switchInputType(hide_element, show_element, escListener) {
	$(hide_element).hide();
	$(show_element).show();
	if (escListener && escListener === true) {
		$(document).keyup(function(e) {
			console.log(e.which);
			switch (e.which) {
				case 0: //esc button in firefox
				case 27: //esc button in chrome and IE >= 8
					eg3_switchInputType(show_element, hide_element);
					$(document).unbind("keypress");
					break;
			}
		});
	}
}


/* Workaround: Make rerendered forms work. See http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-790 */
function rerenderJSFForms() {
	jsf.ajax.addOnEvent(function (e) {
		if (e.status === 'success') 
		{
			$("partial-response:first changes:first update[id='javax.faces.ViewState']", e.responseXML).each(function (i, u) 
			{
				// update all forms
		        $(document.forms).each(function (i, f)
		        {
		        	var field = $("input[name='javax.faces.ViewState']", f);
		        	if (field.length == 0)
		        	{
		        		field = $("<input type=\"hidden\" name=\"javax.faces.ViewState\" />").appendTo(f);
		        	}
		        	field.val(u.firstChild.data);
		        });
			});
		}
	});
}


/**
 * sidebar functions
 * @used by viewPages.xhtml & co.
 */

function resizeSidebar(reference) {
	var sdbHeight = $(reference).height();
	
	switch (reference) {
		case '.eg3_viewPage':
			//eg3_iconBar
			var icoBar = $('.eg3_id_sidebarLeft .eg3_iconBar:visible');
			//sidebar left
			var sdbLeft = $('.eg3_id_sidebarLeft');
			//sidebar padding bottom
			var sdbPadBot = sdbLeft.css("padding-bottom");
			while (!Number(sdbPadBot.substr(sdbPadBot.length-1,1))) {
				sdbPadBot = sdbPadBot.substr(0, sdbPadBot.length-1);
			}
			sdbPadBot = Number(sdbPadBot) + 1;
			sdbHeight = Math.ceil(sdbHeight - sdbPadBot);
			
			//iconBar height
			var ibHeight = (icoBar.length > 0) ? icoBar.height() : 0;
			//tab header height
			var tabHdrHeight = $('.rf-tab-hdr-tabline-top').height();
			
			//set height for tab content details
			$('.eg3_contentDetails').css('height', Math.ceil(sdbHeight - ibHeight - tabHdrHeight - sdbPadBot));
			//set padding bottom in tab content
			$('.rf-tab-cnt').css('padding-bottom', sdbLeft.css('padding-bottom'));
			
			//sidebar left gets the height of calculated height
			sdbLeft.css('height', sdbHeight);
			break;
	}
}

/*
 * function to check if the reference has a defined height
 */
function checkSidebarHeight(reference) {
	
	switch (reference) {
		case '.eg3_viewPage': //the defined height is given by one of the contained images
			//if minimum one image ready get the height of viewPage container and define the height of sidebar
			//otherwise setTimeout for checkSidebarHeight
			var reloadDone = false;
			var imgContainer = $('.eg3_viewPageContainer .eg3_viewPage_imgContainer');
			
			imgContainer.each(function(i) 
			{
				if ($(this).height() > 0) //if the first image has finish loading, set reloadDone on true
				{
					reloadDone = true;
				}
			});
			(reloadDone) ? resizeSidebar(reference) : setTimeout("checkSidebarHeight('"+reference+"')", 25); 
			break;
	}
}
/*
 * this function is used by viewPages.xhtml
 * checks the width of sidebar left
 * if it's expanded it must be less than 80% of the screen width
 */
function checkSidebarWidth(reference) {
	var sidebar = ($('.eg3_id_sidebarLeft').length > 0) ? $('.eg3_id_sidebarLeft') : null;
	/*
	if (sidebar) {
		var documentWidth = $(document).width();
		var curWidthObj = (sidebar.hasClass('eg3_widthAuto')) ? sidebar : $(sidebar.find('.eg3_widthAuto').get(0));
		var sdbWidth = curWidthObj.width();
		
		if (sidebar.hasClass("eg3_expand")) {
			removeAttributeValue(curWidthObj, 'style', 'width');
			removeAttributeValue(curWidthObj, 'style', 'max-width');
			switch (reference) {
				default:
					if (sdbWidth > documentWidth * 0.9) //check if the sidebar width is larger than 90 percent of the document width
					{
						curWidthObj.css("width", (documentWidth * 0.8)); //resize the sidebar to 80% of document width
						curWidthObj.css("max-width", (documentWidth * 0.8));
					} else 
					{ //check if the sidebar is smaller as the layout container
						var classes = sidebar.attr("class"); //read all classes from the sidebar to get the layout container
						classes = classes.split(" "); //separate the classes
						for (var ci = 0; ci < classes.length; ci++) 
						{ 
							if (classes[ci].substr(4, 9) == "container") //search for the layout container class 
							{ 
								var classParts = classes[ci].split("_");
								var minWidth = documentWidth / Number(classParts[classParts.length - 1]); //the last value of class name is the factor for the layout width
								if (sdbWidth < minWidth) //check if the sidebar width is smaller than the minimum layout width 
								{
									eg3_closeOverlay('.eg3_collapseOverlay');
								}
							}
						}
					}
					break;
			}
		}
	}
	*/
}

function eg3_copyToClipboard(infoText, obj) {
	//infoText: e.g. "Copy to clipboard: Ctrl+C, Enter"
	//the value of object is the text to copy
	window.prompt (infoText, $(obj).val());
}

//used by messaging, in template_v3.xhtml
function checkMessageContent() {
	var msgCo = $('.eg3_id_contentDescription .eg3_messageArea');
	if (msgCo && msgCo.length > 0) {
		msgCo.draggable();
		var msgSuc = msgCo.find('.eg3_messageSuccess');
		var msgErr = msgCo.find('.eg3_messageError, .eg3_messageFatal, .eg3_messageWarning');
		
		if ((msgSuc && msgSuc.length > 0) && (!msgErr || msgErr.length == 0)) {
			setTimeout("$('.eg3_id_contentDescription').hide(1000);", 2000);
		}
	}
}

$(document).ready(function(e) {
	/*
	 * use the setTimeout method only if you load the content via ajax 
	 * alternative: create an ajax handler to refer the function after successed loading with dynamic time
	 */
	addDisplayControl('.eg3_listItem');
	addDisplayControl('.eg3_listItemMediaAcc');
	addDisplayControl('.eg3_listItemMultiVolume');
	
	resizeSelectBox();
	
	addShowHideAll('.eg3_showHideAll_js', '.eg3_listItem .eg3_mediumView_js', '.eg3_bibList');
	addShowHideAll('.eg3_showHideAll_js', '.eg3_listItemMediaAcc .eg3_mediumView_js', '.eg3_bibList');
	addShowHideAll('.eg3_toggleListItemVolume_js', '.eg3_listItemVolume', '.eg3_bibList');
	
	rerenderJSFForms();
});
