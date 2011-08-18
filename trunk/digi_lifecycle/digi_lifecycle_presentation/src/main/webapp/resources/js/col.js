jQuery(document).ready(function(){
	 $("#showPanel").click(function(){
		 $("#showPanel").animate({width:"0px", opacity:0}, 200).hide("slow");
		 $("#colleft").animate({width:"220px", opacity:1}, 200);
		 $("#colright").show("normal").animate({width:"100px", opacity:1}, 200);

	 });
	 
	 
	 $("#hidePanel").click(function(){
		 $("#colright").hide();
		  $("#colleft").animate({width:"300px", opacity:1}, 200);
		 $("#showPanel").show("normal").animate({width:"20px", opacity:1}, 200);
 });
 	 
	 
});