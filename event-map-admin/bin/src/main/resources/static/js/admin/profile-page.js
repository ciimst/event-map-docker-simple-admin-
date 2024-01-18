
let editUrl = contextPath + "admin/profile/permission/data";
let deleteUrl = contextPath + "admin/profile/delete";
let profileTableId = "#profileTableId";

function userDrawCallback(settings) {
	
	$("button.list-action-btn").each(function (e) {
		
		
		let $btn = $(this);
		
		$btn.click(function () {
			
			let self = this;
			if ($btn.data("btn-type") === "edit") {
				
				openProfileEditModal(this);
				
			} else if ($btn.data("btn-type") === "delete") {
				
				//TODO:lang
				/*custombox.confirm("Profili silmek istediğinize emin misiniz?", function (result) {
					if (result) {
						deleteProfile(self);
					}
				});*/
				
			}
		});
	});
}

function userRowCallback(nrow, data, index) {
	// $(nrow).css("background-color", "#F9DAD7");
}

function getExtraParams(params) {
	//örnek
	// $(".advanced-search").find("input, textarea, select").each(function(){
	//
	//     if($(this).attr("name") === undefined) return;
	//     if($(this).is(":checkbox")){
	//
	//         if($(this).is(":checked")){
	//             array.push({name: $(this).attr("name"), value: $(this).val()});
	//         }
	//         return;
	//     }
	//     params[$(this).attr("name")] = $(this).val();
	// });
}


let switchTemp =
	'<div class="row permissionName">' +
		'<span class="col-9" data-permissionId="{0}">{1}</span>' +
		'<span class="col-3 m-switch m-switch--sm m-switch--primary">' +
			'<label>' +
				'<input type="checkbox" {2} id="permission-{0}" name="permissionItemList[$].id" value="{0}">' +
				'<span></span>' +
			'</label>' +
		'</span>' +
	'</div>';
	

let permGroupTemp = '<div class="perm-group"><div class="group-title"><span>{0}</span></div>';

var buttonTemp = '<button type="button" data-id="{0}" data-btn-type="{4}"' +
	' class="btn btn-md m-btn--icon m-btn--icon-only m-btn--pill {1} list-action-btn"' +
	' aria-label="{3}" title="{3}">' +
	'<i class="la {2}"></i></button>';

function actionFormatter(data, type, full) {
	
	let result = "";
	
	if (typeof editUrl !== "undefined" && editUrl) {
		result = buttonTemp.f(data,
			"btn-outline-primary",
			"la-edit",
			lang.get("label.edit"),
			"edit");
	}
	
	if (typeof deleteUrl !== "undefined" && deleteUrl) {
		result += buttonTemp.f(data,
			"btn-outline-dangery", "la-trash",
			lang.get("label.delete"),
			"delete");
	}
	return result;
}

let $profileEditModal = $('#profileEditModal');
let $profileSaveBtn = $("#profileEditModal #profile-save-btn");
$profileSaveBtn.on("click", function (e) {
	
	let modalData = $('#profileEditModal *').serializeArray();
	
	let index = 0;
	$.each(modalData, function(idx, el) {//listenin içine yazmak için
		if (el.name.indexOf("$") !== -1) {
			el.name = el.name.replace("$", index++);
		}
	});
	
	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$.post(contextPath + "admin/profile/save", modalData)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (data) {

				if (data.state) {
					
					$profileEditModal.modal("hide");
					custombox.alertSuccess(data.description);

				} else {

					custombox.alertError(data.description ? data.description : lang.get("label.profile.user.have"));
				}
				dataTableUtility.tableStandingRedraw(profileTableId);

			});
		}
	});
});

$("#profile-add-btn").on("click", function () {
	
	openProfileAddModal(this);
});

function openProfileAddModal(elem) {
	openProfileEditModal(elem, true);
}

function openProfileEditModal(elem, isAdd) {
	
	let $this = $(elem);
	
	let dataId = $this.data("id");
	
	if (!(dataId && Number.isInteger(dataId)) && !isAdd) {
		custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(profileTableId);
		return;
	}
	
	$.post(editUrl, {profileId: dataId})
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
		
		
		let groupedPermissions = groupBy(response.data.permissionItemList, "groupName");
		let result = "";
		
		for (const groupKey in groupedPermissions) {
			
			if (groupedPermissions.hasOwnProperty(groupKey)) {
			
			
				result += permGroupTemp.f(groupKey);
				let permissions = groupedPermissions[groupKey];
			
				
				for (const permission of permissions) {
				
					result += switchTemp.f(permission.id, permission.description, permission.selected ? "checked" : "");
				}
				
				result +="</div>";//permGroupTemp kapanıyor burda
			}
		}
		
		//relatedTarget
		jQuery.data(elem, "response-result", result);
		jQuery.data(elem, "response-data", response.data);
		$profileEditModal.modal("show", $this);
		
		$(".group-title").each(function(){
			var groupName = $(this).children("span").text();				
			$(this).children("span").html(lang.get("label."+groupName));
		})
		
		$(".permissionName").each(function(){
			var a = $(this).children(".col-9").text()
			var id = $(this).children(".col-9").attr("data-permissionId")
						
			for (const groupKey in groupedPermissions) {
			
				if (groupedPermissions.hasOwnProperty(groupKey)) {
							
					let permissions = groupedPermissions[groupKey];			
					
					for (const permission of permissions) {
					
						if(permission.id == id){
							$(this).children(".col-9").html(lang.get("label."+permission.name));
						}
					}							
				}
			}
			
		});
	});
	
	
	
}


function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(profileTableId);
		return;
	}
	
	$.post(deleteUrl, {profileId: dataId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
		
		if (!response.state) {
			
			custombox.alertError(response.description ? response.description : lang.get("label.unknown.error"));
		}else{
				custombox.alertSuccess(response.description);
		}
		dataTableUtility.tableStandingRedraw(profileTableId);
	});
}

$profileEditModal.on('show.bs.modal', function (event) {
	var button = $(event.relatedTarget); // Button that triggered the modal
	var dataId = button.data('id'); // Extract info from data-* attributes
	let type = button.data("type");
	
	let modal = $(this);
	modal.clearForm(true);
	
	let title;
	
	if (type === "add") {
		
		title = lang.get("label.add_profile");//TODO:lang
	
	} else {
		
		let data = jQuery.data(button[0], "response-data");
		
		title = lang.get("label.profile.edit");//TODO:lang 
		
		modal.find('input#id').val(data.id);
		modal.find('input#name').val(data.name);
		modal.find('input#description').val(data.description);
	}
	
	let result = jQuery.data(button[0], "response-result");
	
	modal.find('.perm-container').html(result);
	
	
	modal.find('.modal-title').text(title);
	
});


