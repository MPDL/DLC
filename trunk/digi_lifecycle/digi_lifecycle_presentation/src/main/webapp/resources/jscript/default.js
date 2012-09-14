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
				$(this).find("option").each(function(i, opt){
					if ($(opt).val() == val) {
						val = $(opt).text();
						$(opt).parent().attr("title", val);
					}
				});
				$(parent).find(".eg3_selectionText").html(val).attr("title", val);
			}
		});
	} else {
		$(obj).prev().find('.eg3_selectionText').text(obj.options[obj.selectedIndex].text);
		$(obj).attr("title", obj.options[obj.selectedIndex].text);
	}
}


/* 
 * resize all dynamic selectBox container to the correct size of invisible selectbox 
 */
function resizeSelectBox() {
	
	$(".eg3_dynamicSelectBox_js").each(function(ind, dynBox) {
		var selCont = null; //variable for the selectionContent div
		var dynSB = $(dynBox); // object of the dynamicSelectBox_js
		var slctTag = null; //object of the selectOneMenu or select tag
		var drpDwnIconWidth = 16; //pixel value of the icon with for the drop down arrow
		
		if (typeof(selCont = dynSB.find(".eg3_selectionContent")) != "undefined") {
			//save all margin and padding definitions of the selectionContent class
			var padL = Number(selCont.css("padding-left").replace("px", ""));
			var padR = Number(selCont.css("padding-right").replace("px", ""));
			var marL = Number(selCont.css("margin-left").replace("px", ""));
			var marR = Number(selCont.css("margin-right").replace("px", ""));
			//console.log("undefined");
			slctTag = dynSB.find('select'); //save the select tag in the object variable
			
			//if a ajax reload given or the browser had been resized
			slctTag.css("width", "auto"); //the select tag width must be reset to auto for the further right width
			removeAttributeValue(dynSB, "style", "width"); //delete the earlier definition of width
			removeAttributeValue(selCont, "style", "width"); //delete the earlier definition of width
			
			//calculate the new width values
			var newDynSBWidth = 0;
			
			if (dynSB.attr("class").match(/eg3_container_/)) { //at first: check if the dynamicSelectBox_js has a given width
				newDynSBWidth = Math.floor( (dynSB.width()) ); //calculate the new width for the dynamicSelectBox_js
			} else if (slctTag.width() > dynSB.parent().width()) { //at second: check if the select tag larger than the parent container - e.g. used by ingest process - volume
				newDynSBWidth = Math.floor( (dynSB.parent().width()) ); //calculate the new width for the dynamicSelectBox_js with base of the parent container width
			} else { //otherwise 
				newDynSBWidth = Math.floor( (slctTag.width()) ); //calculate the new width for the dynamicSelectBox_js
			}
			
			var newSelContWidth = Math.floor(newDynSBWidth - drpDwnIconWidth - padL - padR) - 1; //calculate the new width for the selectContent container with 1px less for a better optical border
			
			//declare the new values to the objects
			dynSB.css("width", newDynSBWidth);
			selCont.css("width", newSelContWidth);
			
			if (slctTag.width() < newDynSBWidth) { // if the select tag smaller than the given width for the container
				slctTag.css("width", newDynSBWidth); //resize the select tag
			}
			
			/* add behaviour for change and focus */
			//slctTag.bind("focus", function(evt) { updateCustomSelectBox(this); } );
			slctTag.bind("change", function(evt) { updateCustomSelectBox(this); } );
		}
	});
	updateCustomSelectBox();
	initWindowResizeListener();
}


/*
 * following functions are for show or hide medium-/short-view in the bibliographic lists
 */
function eg3_bibListOpenAllMediumView() {
	var bibList = $('.eg3_bibList');
	var listItems = bibList.find(".eg3_listBody .eg3_itemContent");
	
	listItems.each(function(ind) {
		var forwards = $(this).find(".eg3_showHideMediumView_js .eg3_icon_forwardi_16_16");
		if (forwards.length > 0) {
			listItems.find(".eg3_showHideMediumView_js .eg3_iconActionLabel").text(showShortViewText);
		}
		forwards.removeClass("eg3_icon_forwardi_16_16").addClass("eg3_icon_backwardi_16_16");
	});
	listItems.find(".eg3_mediumView_js").show();
}

function eg3_bibListHideAllMediumView() {
	var bibList = $('.eg3_bibList');
	var listItems = bibList.find(".eg3_listBody .eg3_itemContent");
	
	listItems.each(function(ind) {
		var backwards = $(this).find(".eg3_showHideMediumView_js .eg3_icon_backwardi_16_16");
		if (backwards.length > 0) {
			listItems.find(".eg3_showHideMediumView_js .eg3_iconActionLabel").text(showMediumViewText);
		}
		backwards.removeClass("eg3_icon_backwardi_16_16").addClass("eg3_icon_forwardi_16_16");
	});
	listItems.find(".eg3_mediumView_js").hide();
}

function eg3_bibListToggleAllMediumView() {
	var bibList = $('.eg3_bibList');
	var showAllBtn = bibList.find(".eg3_listHeader .eg3_showHideAll_js");
	if (showAllBtn.hasClass("eg3_icon_collapse_16_16")) {
		eg3_bibListOpenAllMediumView();
		eg3_bibListSetAllMediumViewBtn(showAllBtn, "open");
	} else {
		eg3_bibListHideAllMediumView();
		eg3_bibListSetAllMediumViewBtn(showAllBtn, "close");
	}
}


function eg3_bibListSetAllMediumViewBtn(btn, status) {
	switch (status) {
		case 'open':
			btn.removeClass("eg3_icon_collapse_16_16").addClass("eg3_icon_expand_16_16").find(".eg3_iconActionLabel").text(hideMediumViewText);
			break;
		case 'close':
		default:
			btn.removeClass("eg3_icon_expand_16_16").addClass("eg3_icon_collapse_16_16").find(".eg3_iconActionLabel").text(showMediumViewText);
			break;
	}
}

function eg3_bibListCheckAllMediumViewStatus(itemContent) {
	var curList = itemContent.parents(".eg3_bibList");
	var mediumList = curList.find(".eg3_mediumView_js");
	var openList = curList.find(".eg3_mediumView_js:visible");
	var toggleAllBtn = curList.find(".eg3_listHeader .eg3_showHideAll_js");
	if (openList.length == mediumList.length) {
		eg3_bibListSetAllMediumViewBtn(toggleAllBtn, "open");
	} else {
		eg3_bibListSetAllMediumViewBtn(toggleAllBtn, "close");
	}
}

function eg3_bibListOpenItemMediumView(itemContent) {
	var forwards = itemContent.find(".eg3_showHideMediumView_js .eg3_icon_forwardi_16_16, .eg3_itemDetailAction_js");
	if (forwards.length > 0) {
		if (forwards.hasClass("eg3_itemDetailAction_js")) {
			forwards.find(".eg3_iconActionLabel").text(hideRelatedPublications);
		} else {
			itemContent.find(".eg3_showHideMediumView_js .eg3_iconActionLabel").text(showShortViewText);
			forwards.removeClass("eg3_icon_forwardi_16_16").addClass("eg3_icon_backwardi_16_16");
		}
	}
	
	itemContent.find(".eg3_mediumView_js").show();
	eg3_bibListCheckAllMediumViewStatus(itemContent);
}
function eg3_bibListHideItemMediumView(itemContent) {
	var backwards = itemContent.find(".eg3_showHideMediumView_js .eg3_icon_backwardi_16_16, .eg3_itemDetailAction_js");
	if (backwards.length > 0) {
		if (backwards.hasClass("eg3_itemDetailAction_js")) {
			backwards.find(".eg3_iconActionLabel").text(showRelatedPublications);
		} else {
			itemContent.find(".eg3_showHideMediumView_js .eg3_iconActionLabel").text(showMediumViewText);
			backwards.removeClass("eg3_icon_backwardi_16_16").addClass("eg3_icon_forwardi_16_16");
		}
	}
	
	itemContent.find(".eg3_mediumView_js").hide();
	eg3_bibListCheckAllMediumViewStatus(itemContent);
}
function eg3_bibListToggleItemMediumView(obj) {
	var curItemContent = $(obj).parents(".eg3_itemContent");
	
	if (curItemContent.find(".eg3_mediumView_js").is(":visible")) {
		eg3_bibListHideItemMediumView(curItemContent);
	} else {
		eg3_bibListOpenItemMediumView(curItemContent);
	}
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
				var actionArea = $(this);
				if (actionArea.children().hasClass("eg3_icon_collapse_16_16")) {
					eg3_toggleVolumeMediabarActionLabel(actionArea.parents(".eg3_listItemMultiVolume"), "visible");
				} else {
					eg3_toggleVolumeMediabarActionLabel(actionArea.parents(".eg3_listItemMultiVolume"), "hidden");
				}
				
				var volume = searchParentTag(this, "eg3_listItemMultiVolume").next();
				
				if (volume.hasClass("eg3_listItemVolume")) {
					while (volume.hasClass("eg3_listItemVolume")) {
						volume.toggle();
						volume = volume.next();
					}
				}
				
				var volumeList = $('.eg3_listItemVolume');
				var volumeListHidden = $('.eg3_listItemVolume:hidden');
				if (volumeList.length == volumeListHidden.length) {
					$('.eg3_listHeader .eg3_toggleListItemVolume_js').removeClass("eg3_icon_expand_16_16").addClass("eg3_icon_collapse_16_16").find(".eg3_iconActionLabel").text(showAllText);
				} else {
					$('.eg3_listHeader .eg3_toggleListItemVolume_js').removeClass("eg3_icon_collapse_16_16").addClass("eg3_icon_expand_16_16").find(".eg3_iconActionLabel").text(hideAllText);
				}
			});
			break;
	}
}

function eg3_toggleVolumeMediabarActionLabel(volume, to_status) {
	var items = null;
	switch (to_status) {
		case 'visible':
			items = volume.find(".eg3_itemMediabar .eg3_icon_collapse_16_16");
			items.toggleClass("eg3_icon_collapse_16_16 eg3_icon_expand_16_16").find(".eg3_iconActionLabel").text(hideItemText);
			break;
		default:
			items = volume.find(".eg3_itemMediabar .eg3_icon_expand_16_16");
			items.toggleClass("eg3_icon_expand_16_16 eg3_icon_collapse_16_16").find(".eg3_iconActionLabel").text(showItemText);
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
		var btn = $(this);
		var lbl = btn.find(".eg3_iconActionLabel");
		if (btn.hasClass("eg3_icon_collapse_16_16")) {
			btn.toggleClass("eg3_icon_collapse_16_16 eg3_icon_expand_16_16");
			lbl.text(hideAllText); //the text variable is defined in the current xhtml file
		} else {
			btn.toggleClass("eg3_icon_expand_16_16 eg3_icon_collapse_16_16");
			lbl.text(showAllText); //the text variable is defined in the current xhtml file
		}
		if ($(child_elements+':hidden').length > 0) {
			$(child_elements).show();
		//	eg3_toggleVolumeMediabarActionLabel($(child_elements), "visible");
			if (child_elements == '.eg3_listItemVolume') {
				eg3_toggleVolumeMediabarActionLabel($('.eg3_listItemMultiVolume'), "visible");
			}
		} else {
			$(child_elements).hide();
//			eg3_toggleVolumeMediabarActionLabel($(child_elements), "hidden");
			if (child_elements == '.eg3_listItemVolume') {
				eg3_toggleVolumeMediabarActionLabel($('.eg3_listItemMultiVolume'), "hidden");
			}
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
	var sidebarLeft = $('.eg3_id_sidebarLeft');
	
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
			
			var staticThumbs = $('.eg3_thumbnail.eg3_floatNone').attr("static", "eg3_floatNone").removeClass("eg3_floatNone");
			break;
	}
	if (($(window).width() * 0.8) < curTabPanelContent.width()) {
		curTabPanelContent.removeClass("eg3_widthAuto");
		curTabPanelContent.css("width", Math.round($(window).width() * 0.8));
	}
	sidebarLeft.addClass("eg3_expand");
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
	var sidebarLeft = $('.eg3_id_sidebarLeft');
	
	removeAttributeValue(curTabPanelContent, "style", "width");
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
			var staticThumbs = $('.eg3_thumbnail[static="eg3_floatNone"]').removeAttr("static").addClass("eg3_floatNone");
			break;
	}
	
	sidebarLeft.removeClass("eg3_expand");
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
			//console.log(e.which);
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



function checkIconbar(icoBar) {
	var activeTab = $('.eg3_id_sidebarLeft .rf-tab:visible');
	
	if (activeTab.length > 0 && activeTab.attr("id")) {
		activeTab = activeTab.attr("id").split(":")[1];
	} else {
		return false;
	}
	//console.log(activeTab);
	switch(activeTab) {
		case 'toc':
			if ($('.eg3_contentDetails .rf-tr').width() < $('.eg3_contentDetails').width()) {
				$('.eg3_collapseOverlay, .eg3_expandOverlay').attr('disabled', 'disabled');
			}
			break;
	}
}

/**
 * sidebar functions
 * @used by viewPages.xhtml & co.
 */

function resizeSidebar(reference, availableHeight) {
//	console.log("reference: "+reference+' / refHeight: '+availableHeight);
	var refHeight = $(reference).height();
	var sdbHeight = 0;
	switch (reference) {
		case '.eg3_viewPage':
			//eg3_iconBar
			var icoBar = $('.eg3_id_sidebarLeft .eg3_iconBar:visible');
			checkIconbar(icoBar);
			//sidebar left
			var sdbLeft = $('.eg3_id_sidebarLeft');
			//sidebar padding bottom
			var sdbPadBot = sdbLeft.css("padding-bottom");
			
			while (!Number(sdbPadBot.substr(sdbPadBot.length-1,1))) {
				sdbPadBot = sdbPadBot.substr(0, sdbPadBot.length-1);
			}
			
			sdbPadBot = Math.round(Number(sdbPadBot));
			sdbHeight = Math.ceil(refHeight - sdbPadBot);
			
			
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
		case '.eg3_editPage':
			console.log("eg3_editPage: "+availableHeight);
			if (availableHeight && availableHeight > 0) {
				console.log('found editPage with refHeight');
				$('.eg3_id_sidebarLeft .eg3_editSidebarContent').css("height", availableHeight);
			} else {
				//eg3_iconBar
				var icoBar = $('.eg3_id_sidebarLeft .eg3_iconBar:visible');
				checkIconbar(icoBar);
				//sidebar left
				var sdbLeft = $('.eg3_id_sidebarLeft');
				//sidebar padding bottom
				var sdbPadBot = sdbLeft.css("padding-bottom");
				
				while (!Number(sdbPadBot.substr(sdbPadBot.length-1,1))) {
					sdbPadBot = sdbPadBot.substr(0, sdbPadBot.length-1);
				}
				
				sdbPadBot = Math.round(Number(sdbPadBot));
				sdbHeight = Math.ceil(refHeight - sdbPadBot);
				
				
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
			}
			break;
	}
}

/*
 * function to check if the reference has a defined height
 */
function checkSidebarHeight(reference, call) {
	if (call) {
		console.log("reference: "+reference+" / call: "+call);
	}
	
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
		case '.eg3_editPage': //the defined height is given by one of the contained images
			//if minimum one image ready get the height of viewPage container and define the height of sidebar
			//otherwise setTimeout for checkSidebarHeight
			var reloadDone = false;
			var image = $('.eg3_viewPageContainer .eg3_editPage_imgContainer img');
			
			
			var height = image.css("height");
			height = Number(height.substr(0, height.length - 2));
			console.log(height);
			
			
			if (height > 0) //if the first image has finish loading, set reloadDone on true
			{
				reloadDone = true;
			}
			(reloadDone) ? resizeSidebar(reference, height) : setTimeout("checkSidebarHeight('"+reference+"')", 25); 
			break;
	}
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


/**
 * function to resize all dynamic selectboxes and specific page moduls on the window resize event
 */
function initWindowResizeListener(page) {
	$(window).unbind("resize"); //if the browser will be resize, unbind at first before a new bind will be done
	$(window).bind("resize", function() 
		{ 
			resizeSelectBox();
			if ($('.eg3_id_sidebarLeft').length > 0) {
				checkSidebarHeight(page, "window");
			}
		} 
	);
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
	
	addShowHideAll('.eg3_toggleListItemVolume_js', '.eg3_listItemVolume', '.eg3_bibList');
	
	rerenderJSFForms();
});
