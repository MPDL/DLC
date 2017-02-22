/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/

// Custom example logic
function initUploader(clientId, rerender, viewState, sessionId, url, flashUrl, silverlightUrl, maxFileSize) {
	
	//alert('Start');
	var styledUploader = $("#pluploader").pluploadQueue({
		// General settings
		preinit: attachCallbacks,
		runtimes : 'html5,flash,html4',
		url : url + ';jsessionid=' + sessionId,
		max_file_size : '200mb',
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


