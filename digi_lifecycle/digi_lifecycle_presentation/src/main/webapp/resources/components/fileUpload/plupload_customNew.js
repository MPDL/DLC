$(function() {
	// Setup html5 version
	$("#uploader").pluploadQueue({
		// General settings
		runtimes : 'gears,html5,silverlight,browserplus,html4',
		url : location.pathname + "?start=1",
		max_file_size : $('div.setMaxFileSize').text(),
		chunk_size : '1mb',
		unique_names : true,
		filters : [
			{title : "files", extensions : $('div.setFileExtension').text()}
		],

	});
});

