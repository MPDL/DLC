/*
CDDL HEADER START
The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.

You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
See the License for the specific language governing permissions and limitations under the License.

When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
CDDL HEADER END

Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
All rights reserved. Use is subject to license terms.
*/
function load(pageNr)		
		{
			$('#container').find("img").lazyload({
					placeholder: "http://www.appelsiini.net/projects/lazyload/img/grey.gif",
					container: $("#container"),
					//event: "click",
					effect: "fadeIn"
					});
			var obj = document.getElementById("container");
			var imgCount = obj.getElementsByTagName("a").length;
//			var url = window.location.pathname;
//			var reg=/escidoc:.+\/(.+)/;
//			var e = reg.exec(url);
//			obj.scrollTop=obj.scrollHeight*((e[1]-1)/imgCount);
			obj.scrollTop=obj.scrollHeight*((pageNr-1)/imgCount);
		};
		

