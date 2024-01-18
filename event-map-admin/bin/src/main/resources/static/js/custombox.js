var CustomBox = function() {
	bootbox.setDefaults({centerVertical : true});
	return {
		"alert": function(message, alertFunction) {
            return bootbox.dialog({
		                message:message,
		                buttons: {
		                    confirm: {
		                        label: '<i class="la la-fw la-check"></i> <span>{0}</span>'.f(lang.get("label.ok")),
		                        className: 'btn-primary',
		                        callback: function(){
		                            if (typeof alertFunction !== "undefined") {
		                               return alertFunction();
		                            }
		                        }
		                    }
		                },
		                callback: function(){
		                   if (typeof alertFunction !== "undefined") {
		                       return alertFunction();
		                   }
		                },
		                onEscape: function(e) {
		                  // you can do anything here you want when the user dismisses dialog
		                  if (e.type === "click"){
								if (typeof alertFunction !== "undefined") {
								    alertFunction();
								}
								return false;
		                  }
		                }
		            });
        },
		"alertError": function(message, alertFunction) {
			return bootbox.dialog({
				message:'<p class="text-danger text-center"><i class="la la-fw la-5x la-exclamation-triangle"></i></p><p class="text-center">{0}</p>'.f(message),
				buttons: {
					confirm: {
						label: '<i class="la la-fw la-check"></i> <span>{0}</span>'.f(lang.get("label.ok")),
						className: 'btn-primary',
						callback: function(){
							if (typeof alertFunction !== "undefined") {
								return alertFunction();
							}
						}
					}
				},
				callback: function(){
					if (typeof alertFunction !== "undefined") {
						return alertFunction();
					}
				},
				onEscape: function(e) {
					// you can do anything here you want when the user dismisses dialog
					if (e.type === "click"){
						if (typeof alertFunction !== "undefined") {
							alertFunction();
						}
						return false;
					}
				}
			});
		},
		"alertSuccess": function(message, alertFunction) {
			return bootbox.dialog({
				message: '<p class="text-success text-center"><i class="la la-fw la-5x la-check-circle"></i></p><p class="text-center">{0}</p>'.f(message) ,
				onEscape: function(e) {
					// you can do anything here you want when the user dismisses dialog
					if (e.type === "click"){
						return false;
					}
				},
				closeButton: false
			}).on('shown.bs.modal', function (e) {
				setTimeout(closeTimeout, 500, $(this), alertFunction);
			});
		},
		"listDialog": function(message) {
			return bootbox.dialog({
					    message:message
					});
		},
		"detailDialog": function(message) {
			return bootbox.dialog({
				size:'large',
				backdrop:true,
				message:message,
				onEscape: function(e) {
					// you can do anything here you want when the user dismisses dialog
					if (e.type === "click"){
						return false;
					}
				}
			});
		},
		"confirm" : function(message, confirmFunction, cancelBtnName, confirmBtnName, title) {
            return bootbox.dialog({
                        title:title,
		                message:message,
		                buttons: {
		                    cancel: {
		                        label: typeof cancelBtnName !== "undefined" ?
			                        '<i class="la la-fw la-times"></i> <span>{0}</span>'.f(cancelBtnName) :
			                        '<i class="la la-fw la-times"></i> <span>{0}</span>'.f(lang.get("label.cancel")),
		                        className: 'btn-danger',
		                        callback: function(){
		                            if (typeof confirmFunction !== "undefined") {
		                                return confirmFunction(false);
		                            }
		                        }
		                    },
		                    confirm: {
		                        label: typeof confirmBtnName !== "undefined" ?
			                        '<i class="la la-fw la-check"></i> <span>{0}</span>'.f(confirmBtnName) :
			                        '<i class="la la-fw la-check"></i> <span>{0}</span>'.f(lang.get("label.ok")),
		                        className: 'btn-primary',
		                        callback: function(){
		                            if (typeof confirmFunction !== "undefined") {
		                                return confirmFunction(true);
		                            }
		                        }
		                    }
		                },
		                onEscape: function(e) {//modal transitioning fix
		                  // you can do anything here you want when the user dismisses dialog
		                  if (e.type === "click"){
		                        if (typeof confirmFunction !== "undefined") {
		                            confirmFunction(false);
		                        }
		                        // return false;//buna gerek var mı?
		                  }
		                }
		            });
        }
	}
};
var custombox = new CustomBox();



function closeTimeout(dialog, alertFunction){
	dialog.modal("hide");
	
	if (typeof alertFunction !== "undefined") {
		return alertFunction();
	}
}