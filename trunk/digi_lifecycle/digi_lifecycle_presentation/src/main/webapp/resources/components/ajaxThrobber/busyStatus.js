if (!window["busystatus"]) {
			var busystatus = {};
		}
		 
		busystatus.onStatusChange = function onStatusChange(data) {
			var status = data.status;
		 
			//alert("ajax event triggered")
			if (status === "begin") { // turn on busy indicator
				document.body.style.cursor = 'wait';
			} else { // turn off busy indicator, on either "complete" or "success"
				document.body.style.cursor = 'auto';
			}
		};
		 
		jsf.ajax.addOnEvent(busystatus.onStatusChange)