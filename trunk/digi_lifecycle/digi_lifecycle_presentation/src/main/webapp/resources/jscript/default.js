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
			if (parent = searchParentTag(this, "dynamicSelectBox_js")) {
				var val = $(this).val();
				$(this).find("option").each(function(i){
					if ($(this).val() == val) {
						val = $(this).text();
					}
				});
				$(parent).find(".selectionText").html(val);
			}
		});
	} else {
		$(obj).prev().find('.selectionText').text(obj.options[obj.selectedIndex].text);
	}
}


/* 
 * resize all dynamic selectBox container to the correct size of invisible selectbox 
 */
function resizeSelectBox() {
	$(".dynamicSelectBox_js").each(function(ind){
		var selCont = null;
		if (typeof(selCont = $(this).find(".selectionContent")) != "undefined") {
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
	$('.showHideAll_js').click(function(e){
		$(this).toggleClass("icon_collapse_16_16 icon_expand_16_16");
		stopDefaultAction(e);
		var listBody = null;
		
		for (var i=0; i < $(this).parents().length; i++) {
			//read the parents array to listHeader and stop, take the listHeader as parent and starting point for selection
			if ($($(this).parents()[i]).hasClass('listHeader')) {
				listHead = $($(this).parents()[i]);
				listBody = $($(this).parents()[i]).siblings();
				break;
			}
		}
		if (listBody) {
			var allItemDetailActions = $(listBody.find(".itemDetailAction_js"));
			if ( !$(this).attr("detailStatus") || $(this).attr("detailStatus") == 'undefined' || $(this).attr("detailStatus") == undefined ) {
				$(this).attr("detailStatus", "open");
				$(this).html($.trim($(this).html()).replace("Open ", "Close "));
				listBody.find('.mediumView_js').show();
				allItemDetailActions.find("div").removeClass("icon_collapse_16_16").addClass("icon_expand_16_16");
				allItemDetailActions.find(".itemActionLabel").text("Less");
			} else {
				$(this).attr("detailStatus", "");
				$(this).html($.trim($(this).html()).replace("Close ", "Open "));
				listBody.find('.mediumView_js').hide();
				allItemDetailActions.find("div").removeClass("icon_expand_16_16").addClass("icon_collapse_16_16");
				allItemDetailActions.find(".itemActionLabel").text("More");
			}
		}
	});
	
	
}


function addDisplayControl(target) {
	switch (target) {
		case ".listItemMediaAcc":
		case ".listItem":
			$(target+" .itemDetailAction_js").click(function(e){
				stopDefaultAction(e);
				
				$(this).find("div").toggleClass("icon_expand_16_16, icon_collapse_16_16");
					
				$(this).find("div").toggleClass("icon_collapse_16_16, icon_expand_16_16");
				
				var allDetails = null;
				//read the parents array to itemContent and stop, take the itemContent as parent and starting point for selection
				if (allDetails = searchParentTag(this, 'itemContent').find('.mediumView_js')) {
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
		case '.listItemMultiVolume':
			$(target + " .itemDetailAction_js").click(function(e) {
				var volume = searchParentTag(this, "listItemMultiVolume").next();
				
				if (volume.hasClass("listItemVolume")) {
					while (volume.hasClass("listItemVolume")) {
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
	setTimeout("addDisplayControl('.listItem')", 290);
	setTimeout("addDisplayControl('.listItemMediaAcc')", 290);
	setTimeout("addDisplayControl('.listItemMultiVolume')", 290);
	
//	use it on every page, because of language selectbox in metamenu
	// setTimeout("resizeSelectBox()", 290);
	resizeSelectBox();
	
	setTimeout("addShowHideAll('.showHideAll_js', '.listItem .mediumView_js', '.bibList')", 290);
	setTimeout("addShowHideAll('.showHideAll_js', '.listItemMediaAcc .mediumView_js', '.bibList')", 290);
	setTimeout("addShowHideAll('.toggleListItemVolume_js', '.listItemVolume', '.bibList')", 290);
	
});
