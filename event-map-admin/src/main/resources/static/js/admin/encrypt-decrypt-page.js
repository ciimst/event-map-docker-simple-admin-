
function encrypt() {
	
	   let formData = $('#password-1').serialize();
		   			
			   $.post(contextPath + "admin/encrypt-decrypt/encrypt", formData)
			   .fail(function (xhr) {
				   xhr.state = false;
				   console.error(xhr);
				   xhr.description = lang.get("label.unknown.error");
				   xhr.redirectUrl = null;
			   })
			   .always(function (data) {

				   if (data.state) {

					   custombox.alertSuccess(data.description);
                       $("#password-2").val(data.data);
				   } else {

					   custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
				   }

			   });
		
  }
  

function decrypt() {

	   let formData = $('#password-1').serialize();
				
			   $.post(contextPath + "admin/encrypt-decrypt/decrypt", formData)
			   .fail(function (xhr) {
				   xhr.state = false;
				   console.error(xhr);
				   xhr.description = lang.get("label.unknown.error");
				   xhr.redirectUrl = null;
			   })
			   .always(function (data) {

				   if (data.state) {

					   custombox.alertSuccess(data.description);
                       $("#password-2").val(data.data);
				   } else {

					   custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
				     	
			     	}

			   });

   } 
 