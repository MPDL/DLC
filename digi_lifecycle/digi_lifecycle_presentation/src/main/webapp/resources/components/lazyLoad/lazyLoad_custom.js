		$(function(){

			$('#container').find("img").lazyload({
					placeholder: "http://www.appelsiini.net/projects/lazyload/img/grey.gif",
					container: $("#container"),
					//event: "click",
					effect: "fadeIn"
					});

			});