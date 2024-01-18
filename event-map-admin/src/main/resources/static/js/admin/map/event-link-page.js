let editUrl = contextPath + "admin/map/event-link/edit";
let deleteUrl = contextPath + "admin/map/event-link/delete";
let g_tableId = "#eventLinkTableId";

var eventColumnId ="";
function createDateFormatter(data, type, full) {
	return full.createDateStr;
}
function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}


let $editModal = $('#editModal');
let $saveBtn = $("#editModal #layer-save-btn");
$saveBtn.on("click", function (e) {
	
	let modalData = $('#editModal *').serializeArray();
	
	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$.post(contextPath + "admin/map/event-link/save", modalData)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (data) {
				
				if (data.state) {
					
					$editModal.modal("hide");
					custombox.alertSuccess(data.description);
					
				} else {
					
					custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
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
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	
	$.post(editUrl, {eventLinkId: dataId})
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
		$editModal.modal("show", $this);
	});
}

function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	
	$.post(deleteUrl, {eventLinkId: dataId})
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
		
		dataTableUtility.tableStandingRedraw(g_tableId);
	});
}


$editModal.on('show.bs.modal', function (event) {
	
	let button = $(event.relatedTarget);
	let type = button.data("type");
	let modal = $(this);
	modal.clearForm(true);
	
	let title;
	eventColumnId = "";
	if (type === "add") {
		
		title = lang.get("label.event.link.add");//TODO:lang
		modal.find('span#colorText').text("#000000");	
		modal.find('input#colorPicker').val("#000000");	
	} else {
		
		let data = jQuery.data(button[0], "response-data");
		
		if (!data || !(data.id && Number.isInteger(data.id))) {
			custombox.alertError("Bu kayıt güncellenemiyor. Lütfen tekrar deneyiniz");//TODO:lang
			modal.data("will-hide", true);
			return;
		}
		
		eventColumnId = data.eventColumnId;
		modal.find('input#id').val(data.id);
		modal.find('input#link').val(data.link);
//		modal.find('select#modalEventColumnId').val(data.eventColumnId).trigger('change');		
		modal.find('select#modalEventColumnId').val(eventColumnId).trigger('change');		
		title = lang.get("label.event.link.edit");//TODO:lang
		
		modal.find('input#displayName').val(data.displayName);
		modal.find('input#color').val(data.color);		
		modal.find('span#colorText').text(data.color);	
		modal.find('input#colorPicker').val(data.color);	
	}
	

	modal.find('.modal-title').text(title);
	
});

//var options = $('#modalEventColumnId option');




$editModal.on("hidden.bs.modal", function () {	
	//clears datas
	let modal = $(this);
	modal.clearForm(true);//includeHidden = true
	dataTableUtility.tableStandingRedraw(g_tableId);
});

$editModal.on('shown.bs.modal', function (event) {
	//açıldıktan sonra kapatabiliyoruz sadece
	if ($(this).data("will-hide")) {
		$(this).modal("hide");
	}
});

$("#eventColumnId").on("change", function (e) {
	$("#search-button").click();
});

document.querySelectorAll('input[type=color]').forEach(function(picker) {
	
  picker.addEventListener('change', function() {
	
	$("span#colorText").text(picker.value)
  
  });
});


