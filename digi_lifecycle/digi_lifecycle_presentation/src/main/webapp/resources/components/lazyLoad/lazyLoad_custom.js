		$(function(){
			$('#container').find("img").lazyload({
					placeholder: "http://www.appelsiini.net/projects/lazyload/img/grey.gif",
					container: $("#container"),
					//event: "click",
					effect: "fadeIn"
					});
			var obj = document.getElementById("container");
			var imgCount = obj.getElementsByTagName("a").length;
			var url = window.location.pathname;
			var reg=/escidoc:.+\/(.+)/;
			var e = reg.exec(url);
			obj.scrollTop=obj.scrollHeight*((e[1]-1)/imgCount);
			});

		

