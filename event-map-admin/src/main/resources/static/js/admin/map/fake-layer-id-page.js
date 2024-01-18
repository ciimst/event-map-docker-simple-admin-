//let editUrl = contextPath + "admin/map/layer/edit";
let deleteUrl = contextPath + "admin/map/fake-layer-id/delete";
//let layerExportUrl = contextPath + "admin/map/layer/mobileTileData/"
let fakeLayerIdUrl = contextPath + "admin/map/fake-layer-id/fakeLayerIdAdd";
let layerTableId = "#fakeLayerIdTableId";

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

	var layerId = $("#modalLayerId").val();
	var roleId = $("#roleId").val();
	

	$.post(fakeLayerIdUrl, {layerId: layerId, roleId: roleId})
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
	
	$.post(deleteUrl, {id: dataId})
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
		
		dataTableUtility.tableStandingRedraw(layerTableId);
	});
}

