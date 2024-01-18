
$(document).ready(function () {
	
	var clickMenuItemCount = false;
				
    $("a").click(function () { //.m-menu__link
		clickMenuItemCount = true;
        var hrefValue = $(this).attr("href");

		if(clickMenuItemCount == true){
			localStorage.setItem("selectMenuItem", hrefValue);
		}else{
			localStorage.setItem("selectMenuItem", null);
			
		}
        
    });
	var selectMenuItem = localStorage.getItem("selectMenuItem");

	$(".m-menu__nav li").each(function (i){

		var hrefUrl = $(this).find("a").attr('href');	
		
		if(window.location.pathname  == selectMenuItem)	{
			
			if( hrefUrl == selectMenuItem){
				$('a[href="'+selectMenuItem+'"] ').css("background-color","#161A27")
				
				$(this).addClass("m-menu__item--open")
				$(this>"#openMenuDivItem").css("display", "block")
				
			}
		}else{
			$('a[href="'+window.location.pathname+'"] ').css("background-color","#161A27")
			
			if(window.location.pathname == "/home"){				
				localStorage.setItem("openMenuItem", null);
			}						
		}
		
	});
	
	$(".m-menu__item--submenu").on("click", function(){
		localStorage.setItem("openMenuItem", $(this).attr("id"));		
		
	})

	$('#sidebarMenu li').each(function(i)
		{
			if(localStorage.getItem("openMenuItem") ==  $(this).attr('id') && window.location.pathname !="/"){
				$(this).addClass("m-menu__item--open")
				$(this>"#openMenuDivItem").css("display", "block")
			}
	});
});
