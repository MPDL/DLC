/**
digilib plugin stub
 */

(function($) {

	//input field with url to update
	var citationLinkFields = [];
	
	var queryParamKey = 'dl';
	
    // affine geometry
    var geom;
    // plugin object with digilib data
    var digilib;
    
    // the functions made available by digilib
    var fn;

    var FULL_AREA;

    var buttons = {
            /*
    		stub : {
                onclick : ["doStub", 1],
                tooltip : "what does this button do?",
                icon : "stub.png"
                }
                */
    };

    var defaults = {
            // is stub active?
            'isStubActive' : true
    };

    var actions = {
            // action code goes here 
            /*
    		doStub : function (data, param) {
                var settings = data.settings;
                console.log('isStubActive', settings.isStubActive);
                // do some useful stuff ...
            }
            */
    };

    // plugin installation called by digilib on plugin object.
    var install = function(plugin) {
        digilib = plugin;
        console.debug('installing dlc plugin. digilib:', digilib);
        // import geometry classes
        geom = digilib.fn.geometry;
        fn = digilib.fn;
        FULL_AREA = geom.rectangle(0,0,1,1);
        // add defaults, actins, buttons
        $.extend(digilib.defaults, defaults);
        $.extend(digilib.actions, actions);
        $.extend(digilib.buttons, buttons);
    };

    // plugin initialization
    var init = function (data) {
        console.debug('initialising dlc plugin. data:', data);
        var $data = $(data);
        
        if(data.settings.citationLinkFields!=null)
        {
        	citationLinkFields = data.settings.citationLinkFields;
        	
        }
        else
        {
        	console.error("no citationLinkid set!");
        }
        
        if(data.settings.queryParamKey!=null)
        {
        	queryParamKey = data.settings.queryParamKey;
        }
        
        
        // install event handler
        $data.bind('setup', handleSetup);
        $data.bind('update', handleUpdate);
        $data.bind('redisplay', handleRedisplay);
        $data.bind('dragZoom', handleDragZoom);
    };


    var handleSetup = function (evt) {
        console.debug("stub: handleSetup");
        var data = this;
    };

    var handleUpdate = function (evt) {
    	
    	console.debug("dlcplugin: handleUpdate");
        var data = this;
    	var urlParts = citationLinkFields[0].val().split("?");
    	var urlWithoutQueryString = urlParts[0];
    	var queryParameters = {};
    	
    	//Get the query string
    	if(urlParts[1])
    	{
    		var queryString = urlParts[1];
    		var re = /([^&=]+)=([^&]*)/g;
    		var  m;
    		
    		while (m = re.exec(queryString)) {
    	        queryParameters[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
    	    }
    	}
    	
    	//var digilibParamNames =  $(data.settings.digilibParamNames).clone();
    	//delete digilibParamNames['fn'];
    	
    	
    	//Set digilib query string value
    	queryParameters['dl'] = fn.getParamString(data.settings, data.settings.digilibParamNames, digilib.defaults);
    	
    	//Remove if empty
    	if(!queryParameters['dl'])
    	{
    		delete queryParameters['dl'];
    	}
		
    	
		var newUrl = urlWithoutQueryString;
		if(!$.isEmptyObject(queryParameters))
		{
			newUrl = newUrl + '?' + $.param(queryParameters);
		}

		
		var len=citationLinkFields.length;
		for(var i=0; i<len; i++) {
			citationLinkFields[i].val(newUrl);
		}
		
		console.debug("New DLC Url: " + newUrl);

    };

    var handleRedisplay = function (evt) {
        console.debug("stub: handleRedisplay");
        var data = this;
    };

    var handleDragZoom = function (evt, zoomArea) {
        var data = this;
    };

    // plugin object with name and init
    // shared objects filled by digilib on registration
    var plugin = {
            name : 'dlcplugin',
            install : install,
            init : init,
            buttons : {},
            actions : {},
            fn : {},
            plugins : {}
    };

    if ($.fn.digilib == null) {
        $.error("jquery.digilib.pluginstub must be loaded after jquery.digilib!");
    } else {
        $.fn.digilib('plugin', plugin);
    }
})(jQuery);
