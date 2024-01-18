let editUrl = contextPath + "admin/map/layer/edit";
let deleteUrl = contextPath + "admin/map/layer/delete";
let layerExportUrl = contextPath + "admin/map/layer/mobileTileData/"
let fakeLayerAddUrl = contextPath + "admin/map/layer/fakeLayerIdAdd";
let layerTableId = "#layerTableId";

function createDateFormatter(data, type, full) {
	return full.createDateStr;
}

function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}

$('select[name="state"]').on("change", function (e) {
	$("#search-button").click();
});

$('select[name="isTemp"]').on("change", function (e) {
	$("#search-button").click();
});

let $fakeLayerAddModal = $('#fakeLayerAddFields');
let $layerEditModal = $('#editModal');
let $layerSaveBtn = $("#editModal #layer-save-btn");
$layerSaveBtn.on("click", function (e) {
	
	let modalData = $('#editModal *').serializeArray();
	
	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$.post(contextPath + "admin/map/layer/save", modalData)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (data) {
				
				if (data.state) {
					
					$layerEditModal.modal("hide");
					custombox.alertSuccess(data.description);
					
				} else {
					
					custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
				}
				
			});
		}
	});
	
});


function openEditModalZoom(elem) {	
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.try.again"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	window.open(contextPath + 'admin/map/layer-export?id='+dataId, '_self');
}


function openEditModal(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(layerTableId);
		return;
	}
	
	$.post(editUrl, {layerId: dataId})
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
		$layerEditModal.modal("show", $this);
	});
}

let $fakeLayerIdModal = $('#fakeLayerIdModal');
let $saveFakeLayerIdBtn = $("#fakeLayerIdModal #fake-layer-id-save-btn");
function openfakeLayerAddModal(elem){
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError("Lütfen tekrar deneyiniz");//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}

	$fakeLayerIdModal.clearForm(true);
	$fakeLayerIdModal.modal("show");	
	
	$("#fakeLayerId").val(dataId);

}


$saveFakeLayerIdBtn.on("click", function () {

	var layerId = $("#fakeLayerId").val();
	var roleId = $("#roleId").val();
	

	$.post(fakeLayerAddUrl, {layerId: layerId, roleId: roleId})
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
				$fakeLayerIdModal.modal("hide");
				custombox.alertSuccess(response.description);
		}
		
		dataTableUtility.tableStandingRedraw(layerTableId);
	});
	
});	

function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(layerTableId);
		return;
	}
	
	$.post(deleteUrl, {layerId: dataId})
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
		
		dataTableUtility.tableStandingRedraw(layerTableId);
	});
}


$layerEditModal.on('show.bs.modal', function (event) {
	
	let button = $(event.relatedTarget);
	let type = button.data("type");
	let modal = $(this);
	modal.clearForm(true);
	
	let title;
	
	if (type === "add") {
		
		title = lang.get("label.layer.add");//TODO:lang
	} else {
		
		let data = jQuery.data(button[0], "response-data");
		
		if (!data || !(data.id && Number.isInteger(data.id))) {
			custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
			modal.data("will-hide", true);
			return;
		}
		
		modal.find('input#id').val(data.id);
		modal.find('input#name').val(data.name);
		modal.find('input#state').prop("checked", data.state);
		
		title = lang.get("label.layer.edit");//TODO:lang
	}
	
	modal.find('.modal-title').text(title);
	
});

$layerEditModal.on("hidden.bs.modal", function () {
	//clears datas
	let modal = $(this);
	modal.clearForm(true);//includeHidden = true
	dataTableUtility.tableStandingRedraw(layerTableId);
});

$layerEditModal.on('shown.bs.modal', function (event) {
	//açıldıktan sonra kapatabiliyoruz sadece
	if ($(this).data("will-hide")) {
		$(this).modal("hide");
	}
});
