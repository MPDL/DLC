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
			if (parent = searchParentTag(this, "eg3_dynamicSelectBox_js")) {
				var val = $(this).val();
				$(this).find("option").each(function(i){
					if ($(this).val() == val) {
						val = $(this).text();
					}
				});
				$(parent).find(".eg3_selectionText").html(val);
//				console.log(val);
			}
		});
	} else {
		$(obj).prev().find('.eg3_selectionText').text(obj.options[obj.selectedIndex].text);
	}
}


/* 
 * resize all dynamic selectBox container to the correct size of invisible selectbox 
 */
function resizeSelectBox() {
	$(".eg3_dynamicSelectBox_js").each(function(ind){
		var selCont = null;
		if (typeof(selCont = $(this).find(".eg3_selectionContent")) != "undefined") {
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
function eg3_openOverlay(listButton) {
	
	var curIconPanel = $(listButton).parent();
	var curContentPanel = curIconPanel;
	
	for (var ctbp = 0; ctbp < 15; ctbp++) {
		if (curContentPanel.hasClass("rf-tbp")) {
			curIconPanel.addClass("eg3_expand");
			
			curContentPanel.addClass("eg3_noWrapTrn");
			curContentPanel.addClass("eg3_widthAuto");
			curContentPanel.addClass("eg3_overflow");
			curContentPanel.find(".rf-tab-cnt").addClass("eg3_expand");
			/*
			//check the width of the tree
			var objWidth = curContentPanel.find(".rf-tr").width();
			var docWidth = $(document).width();
			//compare the width of tree and document
			if (objWidth > (docWidth/2)) { //if the tree width larger than the half of document width
				if (docWidth >= 500) { //and the document width is larger than 500px 
					curContentPanel.addClass("eg3_expandMax80"); //reduce the current content container to a width of 80 percent
				}
			}
			*/
			$(listButton).attr('disabled', 'disabled');
			curIconPanel.find('.eg3_collapseOverlay').removeAttr('disabled');
			
			break;
		} else {
			curContentPanel = curContentPanel.parent();
		}
	}
}
/**
 * function eg3_openOverlay
 * close the current content to see the content in a minimized container
 * listButton: object whose be clicked
 */
function eg3_closeOverlay(listButton) {
	var curIconPanel = $(listButton).parent();
	var curContentPanel = curIconPanel;
	
	for (var ctbp = 0; ctbp < 15; ctbp++) {
		if (curContentPanel.hasClass("rf-tbp")) {
			curIconPanel.removeClass("eg3_expand");
			
			curContentPanel.removeClass("eg3_noWrapTrn");
			curContentPanel.removeClass("eg3_widthAuto");
			curContentPanel.removeClass("eg3_overflow");
			curContentPanel.find(".rf-tab-cnt").removeClass("eg3_expand");
			/*
			//check the width of the tree
			var objWidth = curContentPanel.find(".rf-tr").width();
			var docWidth = $(document).width();
			//compare the width of tree and document
			if (objWidth > (docWidth/2)) { //if the tree width larger than the half of document width
				if (docWidth >= 500) { //and the document width is larger than 500px 
					curContentPanel.removeClass("eg3_expandMax80"); //reduce the current content container to a width of 80 percent
				}
			}
			*/
			$(listButton).attr('disabled', 'disabled');
			curIconPanel.find('.eg3_expandOverlay').removeAttr('disabled');
			
			break;
		} else {
			curContentPanel = curContentPanel.parent();
		}
	}
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
		console.log('escListener found');
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
	
	setTimeout("addShowHideAll('.eg3_showHideAll_js', '.eg3_listItem .eg3_mediumView_js', '.eg3_bibList')", 290);
	setTimeout("addShowHideAll('.eg3_showHideAll_js', '.eg3_listItemMediaAcc .eg3_mediumView_js', '.eg3_bibList')", 290);
	setTimeout("addShowHideAll('.eg3_toggleListItemVolume_js', '.eg3_listItemVolume', '.eg3_bibList')", 290);
	
});
