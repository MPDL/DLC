




// Custom example logic
$(function() {
	
	
	var styledUploader = $("#pluploader").pluploadQueue({
		// General settings
		runtimes : 'gears,html5,flash,silverlight,browserplus,html4',
		url : document.URL,
		max_file_size : '10mb',
		chunk_size : '1mb',
		multipart : true,
		unique_names : true,
		resize : {width : 320, height : 240, quality : 90},
		filters : [
			{title : "Image files", extensions : "jpg,gif,png"},
			{title : "Zip files", extensions : "zip"}
		],
		multipart_params : {
			'javax.faces.ViewState' : getViewState()
		}
		
	});
	
	styledUploader.bind('UploadComplete', function(up, file) {
		$('#submit-form').find('.hiddenUploadCompleteButton').click();
	});
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	var uploader = new plupload.Uploader({
		runtimes : 'gears,html5,flash,silverlight,browserplus,html4',
		browse_button : 'pickfiles',
		drop_element : 'dropArea',
		url : document.URL,
		
		max_file_size : '50mb',
		resize : {width : 320, height : 240, quality : 90},
		flash_swf_url : '../js/plupload.flash.swf',
		filters : [
			{title : "Image files", extensions : "jpg,gif,png,tif"},
			{title : "Zip files", extensions : "zip"}
		],
		multipart_params : {
			'javax.faces.ViewState' : getViewState()
		}
	});

	uploader.bind('Init', function(up, params) {
		$('#filelist').html("<div>Current runtime: " + params.runtime + "</div>");
	});

	uploader.bind('FilesAdded', function(up, files) {
		$.each(files, function(i, file) {
			$('#filelist').append(
				'<div id="' + file.id + '">' +
				file.name + ' (' + plupload.formatSize(file.size) + ') <b></b>' +
			'</div>');
		});
	});
	
	uploader.bind('UploadFile', function(up, file) {
		$('<input type="hidden" name="file-' + file.id + '" value="' + file.name + '" />')
			.appendTo('#submit-form');
	});

	uploader.bind('UploadProgress', function(up, file) {
		$('#' + file.id + " b").html(file.percent + "%");
	});
	
	uploader.bind('UploadComplete', function(up, file) {
		$('#submit-form').find('.hiddenUploadCompleteButton').click();
	});

	$('#uploadfiles').click(function(e) {
		uploader.start();
		e.preventDefault();
	});

	
	uploader.init();
	
	
});

function getViewState()
{
	var viewStateValue = encodeURI(document.getElementsByName('javax.faces.ViewState')[0].value);
	var regExp = new RegExp('\\+','g');
	var encodedViewState = viewStateValue.replace(regExp, '\%2B');
	return encodedViewState;
	}
