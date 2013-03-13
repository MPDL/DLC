/*
CDDL HEADER START
The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.

You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
See the License for the specific language governing permissions and limitations under the License.

When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
CDDL HEADER END

Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
All rights reserved. Use is subject to license terms.
*/
//global function to combine all functions to stop immediately default actions
function eg3_stopDefaultAction(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
}

/* this function search for the parent node */
function eg3_searchParentTag(source_obj, searchTagString) {
	for (var i=0; i < $(source_obj).parents().length; i++) {
		if ($($(source_obj).parents()[i]).hasClass(searchTagString)) {
			return $($(source_obj).parents()[i]);
			break;
		}
	}
	return false;
}

/* this function returns an jQuery object, if the given obj (e.g. a class-string as '.eg3_id_wrapper')  is no object */
function eg3_returnJQueryObject(obj) {
	return (typeof(obj) == "object") ? obj : $(obj);
}

/*
 * these function delete the searched value in an attribute
 */
function eg3_removeAttributeValue(reference, attribute, value) {
	var refObj = eg3_returnJQueryObject(reference);
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
function eg3_updateCustomSelectBox(obj) {
	if (!obj) {
		$("select").each(function(i){
			var parent = null;
			if (parent = eg3_searchParentTag(this, "eg3_dynamicSelectBox_js")) {
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
		var sbPadR = Math.round(Number(dynSB.css("padding-right").replace("px", "")));
		
		if (typeof(selCont = dynSB.find(".eg3_selectionContent")) != "undefined") {
			//save all margin and padding definitions of the selectionContent class
			var padL = Number(selCont.css("padding-left").replace("px", ""));
			var padR = Number(selCont.css("padding-right").replace("px", ""));
			
			slctTag = dynSB.find('select'); //save the select tag in the object variable
			
			//if a ajax reload given or the browser had been resized
			slctTag.css("width", "auto"); //the select tag width must be reset to auto for the further right width
			eg3_removeAttributeValue(dynSB, "style", "width"); //delete the earlier definition of width
			eg3_removeAttributeValue(selCont, "style", "width"); //delete the earlier definition of width
			
			//calculate the new width values
			var newDynSBWidth = 0;
			
			if (dynSB.attr("class").match(/eg3_container_/)) { //at first: check if the dynamicSelectBox_js has a given width
				newDynSBWidth = Math.floor( (dynSB.width()) ) - sbPadR; //calculate the new width for the dynamicSelectBox_js
			} else if (slctTag.width() > dynSB.parent().width()) { //at second: check if the select tag larger than the parent container - e.g. used by ingest process - volume
				newDynSBWidth = Math.floor( (dynSB.parent().width()) - sbPadR); //calculate the new width for the dynamicSelectBox_js with base of the parent container width
			} else { //otherwise 
				newDynSBWidth = Math.floor( (slctTag.width()) - sbPadR); //calculate the new width for the dynamicSelectBox_js
			}
			if (dynSB.hasClass("eg3_border_1")) {
				newDynSBWidth -= 2;
			} 
			var newSelContWidth = Math.floor(newDynSBWidth - padL - padR) - 1; //calculate the new width for the selectContent container with 1px less for a better optical border
			
			//declare the new values to the objects
			dynSB.css("width", newDynSBWidth);
			selCont.css("width", newSelContWidth);
			
			if (slctTag.width() < (newDynSBWidth + sbPadR)) { // if the select tag smaller than the given width for the container
				slctTag.css("width", newDynSBWidth + sbPadR); //resize the select tag
			}
			
			/* add behaviour for change and focus */
			//slctTag.bind("focus", function(evt) { eg3_updateCustomSelectBox(this); } );
			slctTag.bind("change", function(evt) { eg3_updateCustomSelectBox(this); } );
		}
	}); //end of each
	eg3_updateCustomSelectBox();
	
	eg3_initWindowResizeListener();
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


function eg3_addShowHideAction() {
	/*append listener to the .showHideAll_js-Tag for opening and closing all details in the current list*/
	$('.eg3_showHideAll_js').click(function(e){
		$(this).toggleClass("eg3_icon_collapse_16_16 eg3_icon_expand_16_16");
		eg3_stopDefaultAction(e);
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


function eg3_addDisplayControl(target) {
	switch (target) {
		case ".eg3_listItemMediaAcc":
		case ".eg3_listItem":
			$(target+" .eg3_itemDetailAction_js").click(function(e){
				eg3_stopDefaultAction(e);
				
				$(this).find("div").toggleClass("eg3_icon_expand_16_16, eg3_icon_collapse_16_16");
					
				$(this).find("div").toggleClass("eg3_icon_collapse_16_16, eg3_icon_expand_16_16");
				
				var allDetails = null;
				//read the parents array to itemContent and stop, take the itemContent as parent and starting point for selection
				if (allDetails = eg3_searchParentTag(this, 'eg3_itemContent').find('.eg3_mediumView_js')) {
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
				
				var volume = eg3_searchParentTag(this, "eg3_listItemMultiVolume").next();
				
				if (volume.hasClass("eg3_listItemVolume")) {
					while (volume.hasClass("eg3_listItemVolume")) {
						volume.toggle();
						volume = volume.next();
					}
				}
				
				var volumeList = $('.eg3_listItemVolume');
				var volumeListHidden = $('.eg3_listItemVolume:hidden');
				if (volumeListeHidden && volumeList && (volumeList.length == volumeListHidden.length)) {
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
function eg3_addShowHideAll(element, child_elements, component) {
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
			if (curTabPanelContent && curTabPanelContent.length > 0) {
				curTabPanelContent.addClass("eg3_expand");
				curTabPanelContent.addClass("eg3_widthAuto");
				if (curTabPanelContent.width() < sidebarLeft.width()) {
					eg3_closeOverlay(curControlPanel.find(".eg3_collapseOverlay"));
					eg3_disableExpand(curControlPanel.find(".eg3_expandOverlay"), "true");
					eg3_disableExpand(curControlPanel.find(".eg3_collapseOverlay"), "true");
				};
				curTabPanelContent.find(".rf-trn-cnt .rf-trn-lbl").addClass("eg3_noWrap");
			}
			break;
		case 'thumbs':
			var thumbs = $('.eg3_thumbnail');
			if (thumbs && thumbs.length > 0) {
				thumbs.addClass("eg3_container_1_6");
				thumbs.removeClass("eg3_container_1_2");
			}
			if (curTabPanelContent && curTabPanelContent.length > 0) {
				curTabPanelContent.addClass("eg3_expand");
				curTabPanelContent.css("width", Math.floor($(window).width() * 0.8));
			}
			$('.eg3_thumbnail.eg3_floatNone').attr("data-static", "eg3_floatNone").removeClass("eg3_floatNone");
			break;
		default:
			if (curTabPanelContent && curTabPanelContent.length > 0) {
				curTabPanelContent.addClass("eg3_expand");
				curTabPanelContent.css("width", Math.floor($(window).width() * 0.8));
			}
			break;
	}
	//global settings for all modus
	if (curTabPanel && curTabPanel.length > 0) {
		curTabPanel.addClass("eg3_noWrapTrn");
		curTabPanel.addClass("eg3_widthAuto");
	}
	if (curControlPanel && curControlPanel.length > 0) {
		curControlPanel.addClass("eg3_expand");
	}
	if (($(window).width() * 0.8) < curTabPanelContent.width()) {
		curTabPanelContent.removeClass("eg3_widthAuto");
		curTabPanelContent.css("width", Math.round($(window).width() * 0.8));
	}
	sidebarLeft.addClass("eg3_expand");
	/*$(listButton).attr('disabled', 'disabled');
	curControlPanel.find('.eg3_collapseOverlay').removeAttr('disabled');*/
	$(listButton).hide();
	curControlPanel.find('.eg3_collapseOverlay').show();
}
/**
 * function eg3_openOverlay
 * close the current content to see the content in a minimized container
 * listButton: object whose be clicked
 * @used: in viewPages for sidebar
 */
function eg3_closeOverlay(listButton, cnt) {
	var curTabPanel = $(listButton).parents(".rf-tbp");
	var curTabPanelContent = curTabPanel.find(".rf-tab-cnt");
	var curControlPanel = $(listButton).parents('.eg3_iconBar');
	var sidebarLeft = $('.eg3_id_sidebarLeft');
	
	eg3_removeAttributeValue(curTabPanelContent, "style", "width");
	switch (cnt) {
		case 'thumbs':
			var thumbs = $('.eg3_thumbnail');
			if (thumbs && thumbs.length > 0) {
				thumbs.addClass("eg3_container_1_2");
				thumbs.removeClass("eg3_container_1_6");
			}
			$('.eg3_thumbnail[data-static="eg3_floatNone"]').removeAttr("data-static").addClass("eg3_floatNone");
			break;
	}
	//global settings for all modus
	if (curTabPanel) {
		curTabPanel.removeClass("eg3_noWrapTrn");
		curTabPanel.removeClass("eg3_widthAuto");
	}
	if (curTabPanelContent) {
		curTabPanelContent.removeClass("eg3_expand");
		curTabPanelContent.removeClass("eg3_widthAuto");
		curTabPanelContent.find(".rf-trn-cnt .rf-trn-lbl").removeClass("eg3_noWrap");
		//finaly delete all inline width values in every rf-tab-cnt
		for (var ctpc = 0; ctpc < curTabPanelContent.length; ctpc++) {
			eg3_removeAttributeValue($(curTabPanelContent.get(ctpc)), "style", "width");
		}
	}
	if (curControlPanel) {
		curControlPanel.removeClass("eg3_expand");
	}
	sidebarLeft.removeClass("eg3_expand");
	/*$(listButton).attr('disabled', 'disabled');
	curControlPanel.find('.eg3_expandOverlay').removeAttr('disabled');*/
	$(listButton).hide();
	curControlPanel.find('.eg3_expandOverlay').show();
}

function eg3_disableExpand(button, status) {
	var handle = eg3_returnJQueryObject(button);
	if (button) {
		switch(status) {
			case 'true':
			case true:
			case 'on':
				handle.attr("disabled", "disabled");
				break;
			case 'false':
			case false:
			case 'off':
				handle.removeAttr("disabled");
				break;
		}
	}
}

/**
 * switchInputType is a function to change to container, whose controls input type formats
 * @param hide_element:String - used in case of jQuery selection
 * @param show_element:String - used in case of jQuery selection
 * @param escListener:Boolean - if set, the escape listener will change the input formats back
 */
function eg3_switchInputType(hide_element, show_element, escListener, callbackFunction) {
	$(hide_element).hide();
	$(show_element).show();
	if (escListener && escListener === true) {
		$(document).keyup(function(e) {
			switch (e.which) {
				case 0: //esc button in firefox
				case 27: //esc button in chrome and IE >= 8
					eg3_switchInputType(show_element, hide_element);
					$(document).unbind("keypress");
					break;
			}
		});
	}
	if (callbackFunction) {
		setTimeout(callbackFunction, 10);
	}
}


/* Workaround: Make rerendered forms work. See http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-790 */
function eg3_rerenderJSFForms() {
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


var EG3_PAGE = null;
var EG3_PAGE_IMG_OBJ = null;
var EG3_CALLBACK = new Array(); //an variable for global callback functions as array, e.g. used for sidebar resize and scrolling into sidebar

/**
 * sidebar functions
 * @used by viewPages.xhtml & co.
 */
function eg3_definePageObjects() {
	if ($('#viewPage').length > 0) {
		EG3_PAGE = '#viewPage';
		EG3_PAGE_IMG_OBJ = $('#viewPage img');
	} else {
		EG3_PAGE = '#editPage';
		EG3_PAGE_IMG_OBJ = $('#editPage img');
	}
}

function eg3_initSidebar(evt) {
	eg3_definePageObjects();
	if (!evt) { //if the function was called without an event, e.g. a tab was changed in viewPages
		EG3_PAGE_IMG_OBJ.load(eg3_initSidebar); //add the img load event to the current image container
	} else { // if the load event is given, call the resize function
		eg3_resizeSidebar();
	}
}

/* 
 * these function will be called with every complete loading event of an image 
 * @reference by eg3_initSidebar
 */
function eg3_resizeSidebar() {
	var maxHeight = 0; //init a param for the greates height value of all available images
	var sidebarHeight, contentDetailsHeight = 0;
	
	if (EG3_PAGE_IMG_OBJ == 'undefined' || EG3_PAGE_IMG_OBJ == undefined || EG3_PAGE_IMG_OBJ == null) {
		eg3_definePageObjects();
	}
	
	if (EG3_PAGE_IMG_OBJ) {
		//check every image of them height and safe the greates value
		for (var i = 0; i < EG3_PAGE_IMG_OBJ.length; i++) {
			var imgHeight = $(EG3_PAGE_IMG_OBJ.get(i)).height(); // height of the image
			var imgContHeight = $(EG3_PAGE_IMG_OBJ.get(i)).parent().height(); // height of image container
			tmpHeight = (imgHeight < imgContHeight) ? imgContHeight : imgHeight; //check which height is greater
			//check if the new height is the maximum value for sidebar height
			if (tmpHeight > maxHeight) {
				maxHeight = tmpHeight;
			}
		}
		
		//safe the sidebar as jQuery object
		var sdb = $(".eg3_id_sidebarLeft");
		if (sdb.length > 0) {
			//eg3_iconBar
			var icoBar = $('.eg3_id_sidebarLeft .eg3_iconBar:visible');
			var icbHeight = (icoBar.length > 0) ? icoBar.height() : 0;
			
			/* set the tabpanel on the same height as the pageMenu, for a better look */
			var scanMenu = $('.eg3_viewPageMenu');
			$('.rf-tab-hdr-tabline-top').css({
				"height": scanMenu.height(),
				"padding-top": scanMenu.css("padding-top"),
				"padding-bottom": scanMenu.css("padding-bottom")
			});
			
			//sidebar padding bottom
			var sdbPadBot = sdb.css("padding-bottom");
			while (!Number(sdbPadBot.substr(sdbPadBot.length-1,1))) {
				sdbPadBot = sdbPadBot.substr(0, sdbPadBot.length-1);
			}
			sdbPadBot = Math.round(Number(sdbPadBot));
			
			sidebarHeight = (maxHeight - sdbPadBot)+2;	//use the maxHeight variable for the new height of the sidebar
			
			var curTabCnt = sdb.find(".rf-tab-cnt:visible");
			var cntDtlMinHeight = (curTabCnt.length > 0) ? Number(curTabCnt.find(".eg3_contentDetails").css("min-height").replace("px", "")) : 0;
			
			
			
			
			contentDetailsHeight = sidebarHeight - icbHeight;
			
			if (sidebarHeight < cntDtlMinHeight) {
				sidebarHeight = cntDtlMinHeight+(25); //sidebar height is the min height of the content detailed container plus 25 pixel for the scrolling buttons
				contentDetailsHeight = sidebarHeight - icbHeight;
			}
			
			
			
			switch (EG3_PAGE) {
				case '#editPage':
					sdb.find('.eg3_editSidebarContent').css({
						"height": sidebarHeight,
						"max-height": sidebarHeight
					});
					break;
				case '#viewPage':
				default:
					curTabCnt.css({
						"height":sidebarHeight, 
						"max-height":sidebarHeight
						}); 
					curTabCnt.find(".eg3_contentDetails").css({
						"height": contentDetailsHeight,
						"max-height": contentDetailsHeight
					});
					break;
			}
		}
		
		if (EG3_CALLBACK.length > 0) {
			for (var func in EG3_CALLBACK) {
				setTimeout(EG3_CALLBACK[func], 20);
			}
		}
	} else {
		// if EG3_PAGE_IMG_OBJ missing, start a refresh for the function
		eg3_definePageObjects();
		setTimeout(eg3_resizeSidebar, 50);
	}
}


function eg3_copyToClipboard(infoText, obj) {
	//infoText: e.g. "Copy to clipboard: Ctrl+C, Enter"
	//the value of object is the text to copy
	window.prompt (infoText, $(obj).val());
}

//used by messaging, in template_v3.xhtml
function eg3_checkMessageContent() {
	var msgCo = $('.eg3_id_contentDescription .eg3_messageArea');
	if (msgCo && msgCo.length > 0) {
		msgCo.draggable();
		var msgSuc = msgCo.find('.eg3_messageSuccess');
		var msgErr = msgCo.find('.eg3_messageError, .eg3_messageFatal, .eg3_messageWarning');
		
		if ((msgSuc && msgSuc.length > 0) && (!msgErr || msgErr.length == 0)) {
			setTimeout("$('.eg3_id_contentDescription').hide(1000);", 3500);
		}
	}
}


/**
 * function to resize all dynamic selectboxes and specific page moduls on the window resize event
 */
function eg3_initWindowResizeListener() {
	$(window).unbind("resize"); //if the browser will be resize, unbind at first before a new bind will be done
	$(window).bind("resize", function() { 
		resizeSelectBox();
		if ($('.eg3_id_sidebarLeft').length > 0) {
			eg3_resizeSidebar();
		}
		eg3_checkLogoDimension();
	});
}

var EG3_MODALPOPUP;
/* used for multiVolumes */
function eg3_loadPopup(listItem) {
	var popContent;
	if (listItem) {
		popContent = jQuery(listItem).parents(".eg3_itemContent").find('.modalDialog').html();
	} else {
		//these condition will be use in viewPages, for loading the content via ajax and show them in the popup
		//in these case it's important, that only one element with class "modalDialog" exists on the page
		popContent = $('.modalDialog').html();
	}
	
	EG3_MODALPOPUP = jQuery("#EG3_MODALPOPUP");
	var wrapper = jQuery(".eg3_id_wrapper");
	if (!EG3_MODALPOPUP || EG3_MODALPOPUP.length === 0 || typeof EG3_MODALPOPUP == undefined) {
		EG3_MODALPOPUP = jQuery('<div id="EG3_MODALPOPUP" class="modalDialog"></div>').insertBefore(wrapper);
	}
	EG3_MODALPOPUP.html(popContent);
	EG3_MODALPOPUP.show();
}
/* used for multiVolumes */
function eg3_unloadPopup() {
	EG3_MODALPOPUP.html("");
	EG3_MODALPOPUP.remove();
}
function eg3_listenMultiVolume() {
	var multiVolumeLink = jQuery('.eg3_itemContent .modalDialog').parent().find(".eg3_itemHeadline .eg3_itemTitle");
	multiVolumeLink.click(function(e) {
		eg3_stopDefaultAction(e);
		eg3_loadPopup(this);
	});
}

/*
 * the function checks the solution logo in the header
 * if the width is greater than the container, the logo must be fit to container 
 */
function eg3_checkLogoDimension() {
	var logo = $('.eg3_solutionLogo img');
//	If logo exists
	if(logo.length)
	{
		var logoContainer = logo.parents('.eg3_solutionLogo');
		var logoContainerWidth = logoContainer.width() - 10;
		var tmpImg = new Image();
		tmpImg.src = logo.attr("src");
		
		//to resize the logo on every resolution, it's necessary to remove old settings of this function
		eg3_removeAttributeValue(logoContainer, "style", "padding-top");
		eg3_removeAttributeValue(logo, "style", "height");
		
		//if the image was loaded, chech the new dimensions and resize if necessary
		$(tmpImg).load(function(e) {
			var cssHeight, factor = 0;
			if (logo.css("height") != "none" || logo.css("max-height") != "none") {
				cssHeight = (logo.css("height") != "none") ? Number(logo.css("height").replace("px", "")) : Number(logo.css("max-height").replace("px", ""));
				factor = tmpImg.height / cssHeight;
				if ((tmpImg.width / factor) > logoContainerWidth) {
					var resizeFactor = logoContainerWidth / tmpImg.width;
					var newHeight = Math.floor(tmpImg.height * resizeFactor);
					var paddingForMiddle = Math.round((cssHeight - newHeight) / 2);
					logo.css("height", newHeight);
					logoContainer.css("padding-top", paddingForMiddle);
				}
			}
		});
		
	}
	
}



$(document).ready(function(e) {
	/*
	 * use the setTimeout method only if you load the content via ajax 
	 * alternative: create an ajax handler to refer the function after successed loading with dynamic time
	 */
	eg3_addDisplayControl('.eg3_listItem');
	eg3_addDisplayControl('.eg3_listItemMediaAcc');
	eg3_addDisplayControl('.eg3_listItemMultiVolume');
	
	resizeSelectBox();
	
	eg3_addShowHideAll('.eg3_toggleListItemVolume_js', '.eg3_listItemVolume', '.eg3_bibList');
	
	eg3_rerenderJSFForms();
	
	eg3_checkLogoDimension();
	
	if (Number($.browser.version) < 9) {
		eg3_ie8_addSearchSubmitOnEnter();
	}
	
	if ($.browser.msie && (Number($.browser.version) === 9 || Number($.browser.version) < 9)) {
		eg3_ie9_addHoverColor();
	}
});



/**
 * following functions are only for IE8
 */
function eg3_ie8_addSearchSubmitOnEnter() {
	$('.eg3_icon_quickSearch_16_16').parents("form").keypress(function(e){
		if (e.which == 13) {
			eg3_stopDefaultAction(e);
			$(this).find("[type=submit]").trigger("click");
		}
	});
}


/**
 * following functions are only for IE9
 */
/*
 * because of a bug in IE9, all input elements must get a js-listener for hover events/handlings
 */
function eg3_ie9_addHoverColor() {
	$('input[type="submit"], input[type="button"], input[type="reset"]').mouseover(function(e){
		$(this).css({"color": "#EA7125", "border-color": "#EA7125"});
	}).mouseout(function(e){
		eg3_removeAttributeValue($(this), "style", "color");
		eg3_removeAttributeValue($(this), "style", "border-color");
	});
} 
