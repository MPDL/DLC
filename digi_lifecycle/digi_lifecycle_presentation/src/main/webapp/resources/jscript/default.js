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

/* this function updates the selectText container with the selected item of selectbox */
function updateCustomSelectBox(obj) {
	if (!obj) {
		$("select").each(function(i){
			var parent = null;
			if (parent = searchParentTag(this, "mpdl_dynamicSelectBox_js")) {
				var val = $(this).val();
				$(this).find("option").each(function(i){
					if ($(this).val() == val) {
						val = $(this).text();
					}
				});
				$(parent).find(".mpdl_selectionText").html(val);
				console.log(val);
			}
		});
	} else {
		$(obj).prev().find('.mpdl_selectionText').text(obj.options[obj.selectedIndex].text);
	}
}


/* 
 * resize all dynamic selectBox container to the correct size of invisible selectbox 
 */
function resizeSelectBox() {
	$(".mpdl_dynamicSelectBox_js").each(function(ind){
		var selCont = null;
		if (typeof(selCont = $(this).find(".mpdl_selectionContent")) != "undefined") {
			var selectTag = $(this).find('select');
			var padL = Number(selCont.css("padding-left").replace("px", ""));
			var padR = Number(selCont.css("padding-right").replace("px", ""));
			var marL = Number(selCont.css("margin-left").replace("px", ""));
			var marR = Number(selCont.css("margin-right").replace("px", ""));
			var newInfoWidth = Math.floor( (selectTag.width() - marR) );
			var newContainerWidth = Math.floor( (newInfoWidth + marR + marL + padR + padL) )
			selCont.css("width", newInfoWidth);
			$(this).css("width", newContainerWidth);
			selectTag.css("width", newContainerWidth);
			/* add behaviour for change and focus */
			selectTag.bind("focus", function(evt) { updateCustomSelectBox(this); } );
			selectTag.bind("change", function(evt) { updateCustomSelectBox(this); } );
		}
	});
	
	updateCustomSelectBox();
}


function addShowHideAction() {
	/*append listener to the .showHideAll_js-Tag for opening and closing all details in the current list*/
	$('.mpdl_showHideAll_js').click(function(e){
		$(this).toggleClass("mpdl_icon_collapse_16_16 mpdl_icon_expand_16_16");
		stopDefaultAction(e);
		var listBody = null;
		
		for (var i=0; i < $(this).parents().length; i++) {
			//read the parents array to listHeader and stop, take the listHeader as parent and starting point for selection
			if ($($(this).parents()[i]).hasClass('mpdl_listHeader')) {
				listHead = $($(this).parents()[i]);
				listBody = $($(this).parents()[i]).siblings();
				break;
			}
		}
		if (listBody) {
			var allItemDetailActions = $(listBody.find(".mpdl_itemDetailAction_js"));
			if ( !$(this).attr("mpdl_detailStatus") || $(this).attr("mpdl_detailStatus") == 'undefined' || $(this).attr("mpdl_detailStatus") == undefined ) {
				$(this).attr("mpdl_detailStatus", "open");
				$(this).html($.trim($(this).html()).replace("Open ", "Close "));
				listBody.find('.mpdl_mediumView_js').show();
				allItemDetailActions.find("div").removeClass("mpdl_icon_collapse_16_16").addClass("mpdl_icon_expand_16_16");
				allItemDetailActions.find(".mpdl_itemActionLabel").text("Less");
			} else {
				$(this).attr("mpdl_detailStatus", "");
				$(this).html($.trim($(this).html()).replace("Close ", "Open "));
				listBody.find('.mpdl_mediumView_js').hide();
				allItemDetailActions.find("div").removeClass("mpdl_icon_expand_16_16").addClass("mpdl_icon_collapse_16_16");
				allItemDetailActions.find(".mpdl_itemActionLabel").text("More");
			}
		}
	});
	
	
}


function addDisplayControl(target) {
	switch (target) {
		case ".mpdl_listItemMediaAcc":
		case ".mpdl_listItem":
			$(target+" .mpdl_itemDetailAction_js").click(function(e){
				stopDefaultAction(e);
				
				$(this).find("div").toggleClass("mpdl_icon_expand_16_16, mpdl_icon_collapse_16_16");
					
				$(this).find("div").toggleClass("mpdl_icon_collapse_16_16, mpdl_icon_expand_16_16");
				
				var allDetails = null;
				//read the parents array to itemContent and stop, take the itemContent as parent and starting point for selection
				if (allDetails = searchParentTag(this, 'mpdl_itemContent').find('.mpdl_mediumView_js')) {
					allDetails.toggle();
				};
				/*
				 * following code is deprecated and should be rebuild with new function
				//from here: comfort function to handle the showHideAll_js button
				var list = searchParentTag(this, 'bibList');
				var showHideAll_jsBtn = list.find('.showHideAll_js');
				if (showHideAll_jsBtn) {
					if ($(list).find('.mediumView_js:hidden').length == 0) { //if no child element invisible, all details are open
						showHideAll_jsBtn.attr("detailStatus", "open");	//switch the status of showHideAll_js to open
						$(showHideAll_jsBtn).html($.trim($(showHideAll_jsBtn).html()).replace("Open ", "Close "));
						list.find(".showHideAll_js").removeClass("icon_collapse_16_16").addClass("icon_expand_16_16");
					} else if ($(showHideAll_jsBtn).html().match(/Close all/)) {
						showHideAll_jsBtn.attr("detailStatus", "");
						$(showHideAll_jsBtn).html($.trim($(showHideAll_jsBtn).html()).replace("Close ", "Open "));
						list.find(".showHideAll_js").removeClass("icon_expand_16_16").addClass("icon_collapse_16_16");
					}
				}
				*/
			});
			break;
		case '.mpdl_listItemMultiVolume':
			$(target + " .mpdl_itemDetailAction_js").click(function(e) {
				var volume = searchParentTag(this, "mpdl_listItemMultiVolume").next();
				
				if (volume.hasClass("mpdl_listItemVolume")) {
					while (volume.hasClass("mpdl_listItemVolume")) {
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


$(document).ready(function(e) {
	/*
	 * use the setTimeout method only if you load the content via ajax 
	 * alternative: create an ajax handler to refer the function after successed loading with dynamic time
	 */
	setTimeout("addDisplayControl('.mpdl_listItem')", 290);
	setTimeout("addDisplayControl('.mpdl_listItemMediaAcc')", 290);
	setTimeout("addDisplayControl('.mpdl_listItemMultiVolume')", 290);
	
//	use it on every page, because of language selectbox in metamenu
	// setTimeout("resizeSelectBox()", 290);
	resizeSelectBox();
	
	setTimeout("addShowHideAll('.mpdl_showHideAll_js', '.mpdl_listItem .mpdl_mediumView_js', '.mpdl_bibList')", 290);
	setTimeout("addShowHideAll('.mpdl_showHideAll_js', '.mpdl_listItemMediaAcc .mpdl_mediumView_js', '.mpdl_bibList')", 290);
	setTimeout("addShowHideAll('.mpdl_toggleListItemVolume_js', '.mpdl_listItemVolume', '.mpdl_bibList')", 290);
	
});
