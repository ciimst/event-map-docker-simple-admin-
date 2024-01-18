let ldapUrl = contextPath + "admin/user/ldapInfo";
let getFullNameUrl = contextPath + "admin/user/getFullName?user=";
let editUrl = contextPath + "admin/user/edit";
let deleteUrl = contextPath + "admin/user/delete";
let userTableId = "#userTableId";
let $userEditModal = $('#userEditModal');
let $userSaveBtn = $("#userEditModal #user-save-btn");

	
jQuery(document).ready(function() {
	$("#ldapname").select2({
                placeholder: "LDAP Kullanıcısı Ara",
               // initSelection: true,
                allowClear: !0,
                ajax: {
                    url: ldapUrl,
                    dataType: "json",
                    type: "GET",
                    delay: 250,
                    data: function(params) {
                        return {
                            user: params.term,
                            
                        };
                    },
                    processResults: function (data, params) {
            			return {
                			results: $.map(data, function (item) {                			    
                 			    return {
                   	     			text: item,
                     			    id: item,
                    			    data: item
                   				 };
          				      })
        			    };
      			  },
                    cache: false
                },
                escapeMarkup: function(e) {
                    return e
                },
                minimumInputLength: 2,
//               	templateResult: function(e) {
//	
//                    return e;
//                },
            });
        });

$('select[name="profileId"]').on("change", function (e) {
	$("#search-button").click();
});

$('select[name="state"]').on("change", function (e) {
	$("#search-button").click();
});

$userSaveBtn.on("click", function (e) {
	
	let modal = $(userEditModal);
	modal.find("input#isDbUser").prop("disabled", false);
	let modalData = $('#userEditModal *').serializeArray();

	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$.post(contextPath + "admin/user/save", modalData)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (data) {
				
				if (data.state) {
					
					$userEditModal.modal("hide");
					custombox.alertSuccess(data.description);
					
				} else {
					
					custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
					// toastr.error(data.description ? data.description : lang.get("label.unknown.error"), 5000);
				}
				
			});
		}
	});
	
});

function openEditModal(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(userTableId);
		return;
	}
	
	$.post(editUrl, {userId: dataId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
		
		if (!response.state) {
			custombox.alertError(response.description ? response.description : lang.get("label.unknown.error"));
			return;
		}
		
		//relatedTarget
		jQuery.data(elem, "response-data", response.data);
		$userEditModal.modal("show", $this);
	});
}

function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(userTableId);
		return;
	}
	
	$.post(deleteUrl, {userId: dataId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
		
		if (!response.state) {
			
			custombox.alertError(response.description ? response.description : lang.get("label.unknown.error"));
		}
		
		dataTableUtility.tableStandingRedraw(userTableId);
	});
}

$userEditModal.find('input#isDbUser').click(function(){
	let modal = $(userEditModal);
	if (!(modal.find('input#isDbUser').prop("checked"))){
//		modal.find("div#ldapname-div").hide();
//		modal.find("div#username-div").show();
		modal.find("div#password-div").hide();
	}
	else{
//		modal.find("div#ldapname-div").show();
//		modal.find("div#username-div").hide();
		modal.find("div#password-div").show();
	}
});

$userEditModal.find("select#ldapname").change(function(){
	let modal = $(userEditModal);
	let ldapname = modal.find("select#ldapname").val();
	$.get(getFullNameUrl + ldapname, function(data){
      
      modal.find('input#name').val(data);
    });
    modal.find("input#username").val(ldapname);
});

$userEditModal.on('show.bs.modal', function (event) {
	
	let button = $(event.relatedTarget);
	let type = button.data("type");
	let modal = $(this);
	modal.clearForm(true);
	
	let $passField = modal.find(".password-container .input-group");
	let $passHelp = modal.find("#passwordHelp");
	let $passLabel = modal.find("#password-label");
	$passLabel.off("click");
	$passLabel.removeClass("cancel");
	let title;
	
	if (type === "add") {
		
		title = lang.get("label.user_add");//TODO:lang
		
		modal.find("#passwordHelp").show();
		modal.find("#password").val(generatePassword());
		modal.find('select#profileId').trigger("change");
		
		modal.find("div#ldapname-div").hide();
		modal.find("div#username-div").show();
		modal.find("div#password-div").hide();
		modal.find("input#state").prop("checked", true);
		modal.find("input#isDbUser").prop("disabled", false);
		
		$passField.show();
		$passLabel.removeClass("change").html(lang.get("label.password"));
	} else {
		
		let data = jQuery.data(button[0], "response-data");
		
		if (!data || !(data.id && Number.isInteger(data.id))) {
			custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
			modal.data("will-hide", true);
			return;
		}
		$passField.hide();
		$passHelp.hide();
		
		$passLabel.addClass("change").html(lang.get("label.password.change"));
		$passLabel.on("click", function () {
			if ($passField.is(":visible")) {
				$passField.hide();
				$passLabel.removeClass("cancel").html(lang.get("label.password.change"));
			} else {
				$passLabel.addClass("cancel")
				.html(lang.get("label.password.change") + " -" + lang.get("label.cancel"));
				$passField.show();
			}
		});
		
		modal.find('input#id').val(data.id);
		modal.find('input#name').val(data.name);
		
		modal.find('input#username').val(data.username);
		modal.find("input#providerUserId").val(data.providerUserId);
		
		modal.find('select#profileId').val(data.profileId).trigger('change');
		modal.find('input#isDbUser').prop("checked", data.isDbUser);	
		modal.find('input#state').prop("checked", data.state);
		modal.find("div#ldapname-div").toggle(!data.isDbUser);
		modal.find("div#username-div").toggle(data.isDbUser);
		modal.find("div#password-div").toggle(data.isDbUser);
		//modal.find('select#ldapname').select2();
		
		modal.find("input#isDbUser").prop("disabled", true);
		if(!data.isDbUser){
			modal.find('select#ldapname').empty() //empty select
        	.append($("<option/>") //add option tag in select
            	.val(data.username) //set value for option to post it
            	.text(data.username)) //set a text for show in select
        	.val(data.username) //select option of select2
        	.trigger("change"); //apply to select2
			
		}
		
		title = lang.get("label.user.edit");//TODO:lang
	}
	
	modal.find('.modal-title').text(title);
	
});

$userEditModal.on("hidden.bs.modal", function () {
	//clears datas
	let modal = $(this);
	modal.clearForm(true);
	dataTableUtility.tableStandingRedraw(userTableId);
});

$userEditModal.on('shown.bs.modal', function (event) {
	//açıldıktan sonra kapatabiliyoruz sadece
	if ($(this).data("will-hide")) {
		$(this).modal("hide");
	}
});
