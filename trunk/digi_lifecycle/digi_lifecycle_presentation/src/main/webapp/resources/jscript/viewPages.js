/**
 * function to resize the text-area box on the right side of the image
 * @returns {Boolean}
 */
function resizeFulltext() {
	var fullText = $('.eg3_viewPage_fulltext .eg3_containerCenter');
	if (fullText && fullText.length > 0) {
		fullText.css('height', Math.ceil($('.eg3_viewPage_imgContainer').height() - $('.eg3_permalinkBox').height() - 2));
		return false;
	}
}
 
jsf.ajax.addOnEvent(function(e)
{	
	//notice the container height for setting a min-height while ajax reloading
	//without a height value the page jump to top because of to slowly image loading
	if (e.status == "begin") {
		$('.eg3_viewPageContainer').css("min-height", $('.eg3_viewPageContainer').height());
		eg3_closeOverlay('.eg3_collapseOverlay');
	}
	//if the ajax request is success
	//scroll to the right position in sidebar and delete the min-height value of the global parent container for dynamical browser behaviour
	if(e.status=="success")
	{
		//scrollTree();
		//scrollThumbs();
		checkSidebarHeight('.eg3_viewPage', "ajax");
		setTimeout("resizeFulltext()", 200);
	}
});

function scrollTree()
{
	var selectedDivId= '#{viewPages.selectedDiv.id}';
	var containerId = "#{rich:clientId('tocTreeContainer')}";
	
	scrollDiv(containerId, 'struct_' + selectedDivId);
	
	resetViewPageContainer();
}

function scrollThumbs()
{
	var selectedPageId= "#{viewPages.selectedPage.id}";
	if(!selectedPageId)
	{
		selectedPageId = "#{viewPages.selectedRightPage.id}"
	}
	
	var containerId = "#{rich:clientId('thumbnailContainer')}";
	scrollDiv(containerId, selectedPageId);
}

function scrollDiv(containerId, scrollToClassId)
{

	if(containerId && scrollToClassId)
	{
		//add an backslahs before all colons					
		containerId = containerId.replace(/:/g, "\\:");
		var containerDiv = $('#' + containerId);
		var scrollToElement = $('.' + scrollToClassId);

		var pos = scrollToElement.offset();
		if(pos!=null)
		{
			var containerPos = containerDiv.offset();				
			var currentScroll = containerDiv.scrollTop();				
			var newScrollPos = pos.top - containerPos.top + currentScroll - 50;		
			containerDiv.scrollTop(newScrollPos);
		}		
	}

}

/*
 * function to remove the style value min-height in case of ajax functions, e.g. for pagination
 */
function resetViewPageContainer() 
{
	var reloadDone = false;
	
	$('.eg3_viewPageContainer .eg3_viewPage_imgContainer').each(function(i) 
	{
		if ($(this).height() > 0) 
		{
			reloadDone = true;
		}
	});
	(reloadDone) ? removeAttributeValue('.eg3_viewPageContainer', "style", "min-height") : setTimeout("resetViewPageContainer()", 25);
}


$(document).ready(function(e)
{
	scrollTree();
	scrollThumbs();
	
	checkSidebarHeight('.eg3_viewPage', "init");
	
	resizeFulltext();
});
		