




// Custom example logic
function initUploader(clientId, rerender, viewState, sessionId, url, flashUrl, maxFileSize) {
	
	//alert('Start');
	var styledUploader = $("#pluploader").pluploadQueue({
		// General settings
		preinit: attachCallbacks,
		runtimes : 'html5',
		url : url + ';jsessionid=' + sessionId,
		max_file_size : '10mb',
		flash_swf_url : flashUrl,
		multi_selection : true,
		multipart : true,
		multiple_queues : true,
		filters : [
			{title : "Image files", extensions : "jpg,gif,png,tif"},
			{title : "Xml Files", extensions : "xml"},
			{title : "Mab Files", extensions : "mab"}
		],
		file_data_name : clientId,
		multipart_params : {
			//'form1' : 'form1',
			//'form1:offset' : '',
			//'form1:Header:selSelectLocale' : 'de',
			'javax.faces.ViewState' : viewState,
			'javax.faces.partial.ajax' : '1',
			'javax.faces.partial.execute' : clientId,
			'javax.faces.source' : clientId,
			//'javax.faces.partial.render' : rerender
			//clientId.toString() : ''
			
			
		},
		headers : {
			'Cookie' : 'JSESSIONID=' + sessionId,
			'X-Requested-With' : 'XmlHttpRequest',
			'Faces-Request' : 'partial/ajax'
		}
	
		
	});

	
	//After file upload is complete, send an JSF ajax request in order to rerender fiven elements
	function attachCallbacks(uploader) {
	
		uploader.bind('FileUploaded', function(up, file, response) {
			//If last file was uploaded
			if ((uploader.total.uploaded + 1) == uploader.files.length)
			{
				//$('#'+clientId).find('.hiddenUploadCompleteButton').click();
				var element = document.getElementById(clientId);
				jsf.ajax.request(element, null, {render: rerender});
			}
			
		});
	}
	

	
};


