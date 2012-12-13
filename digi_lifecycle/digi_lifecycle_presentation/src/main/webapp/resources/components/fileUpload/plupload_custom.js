// Custom example logic
function initUploader(clientId, rerender, viewState, sessionId, url, flashUrl, silverlightUrl, maxFileSize) {
	
	//alert('Start');
	var styledUploader = $("#pluploader").pluploadQueue({
		// General settings
		preinit: attachCallbacks,
		runtimes : 'html5,flash,html4',
		url : url + ';jsessionid=' + sessionId,
		max_file_size : '20mb',
		flash_swf_url : flashUrl,
		silverlight_xap_url : silverlightUrl,
		multi_selection : true,
		multipart : true,
		multiple_queues : true,
		filters : [
			{title : "Image files", extensions : "jpg,jpeg,JPG,JPEG,tif,tiff,TIFF,png,PNG"},
			{title : "Xml Files", extensions : "xml"},
			{title : "Mab Files", extensions : "mab.xml"}
		],
		
		file_data_name : clientId,
		
		multipart_params : {
			
			'javax.faces.ViewState' : viewState,
			'javax.faces.partial.ajax' : 'true',
			'javax.faces.partial.execute' : clientId,
			'javax.faces.source' : clientId
			
			
			
		},
		
		headers : {
			//'Cookie' : 'JSESSIONID=' + sessionId,
			'X-Requested-With' : 'XmlHttpRequest',
			'Faces-Request' : 'partial/ajax'
		}
	
		
	});

	
	//After file upload is complete, send an JSF ajax request in order to rerender given elements
	function attachCallbacks(uploader) {
	
		uploader.bind('FileUploaded', function(up, file, response) {
			//If last file was uploaded
			/*
			if ((uploader.total.uploaded + 1) == uploader.files.length)
			{

				$('[id="'+clientId+'"]').find('.hiddenUploadCompleteButton').click();
			}
			*/
		});
		
		
		uploader.bind('UploadProgress', function(up, file, response) {
			//If last file was uploaded
			$( "#progressBar" ).progressbar({
	            value: up.total.percent 
	        });
			
			
			/*
			if ((uploader.total.uploaded + 1) == uploader.files.length)
			{

				$('[id="'+clientId+'"]').find('.hiddenUploadCompleteButton').click();
			}
			*/
		});
		
		uploader.bind('BeforeUpload', function(up, file, response) {
			
			var inputId = "#" + clientId + ":uploadedFilesByPlupload";
			inputId = inputId.replace(/:/g, "\\:");
			var input = $(inputId);
			input.val(input.val() + "||" + file.name)
			//input.val(input.val() + "||yy" + file.name)
			
			/*
			if ((uploader.total.uploaded + 1) == uploader.files.length)
			{

				$('[id="'+clientId+'"]').find('.hiddenUploadCompleteButton').click();
			}
			*/
		});
		
		
		
		
		
		
		uploader.bind('StateChanged', function(up) {
			
			//console.log('Statechanged: ' + up.state);
			if (up.state=plupload.STARTED) {
				//console.log('StartUpload');
				$("#modalUploadDialog").dialog("open");   
			}
		});
		
		uploader.bind('UploadComplete', function(up, file, response) {
			
			//console.log('UploadComplete');
			$("#modalUploadDialog").dialog("close");
			
			$('[id="'+clientId+'"]').find('.hiddenUploadCompleteButton').click();
			
			
		});
		
		
		
	}
	

	
};


