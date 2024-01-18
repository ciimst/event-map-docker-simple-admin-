let editUrl = contextPath + "admin/map/map-area/edit/";
let deleteUrl = contextPath + "admin/map/map-area/delete";
let mapAreaTableId = "#mapAreaTableId";

$(document).ready(function () {
});

$("#mapAreaGroupId").on("change", function (e) {
	$("#search-button").click();
});
        
function openEditModal(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(mapAreaTableId);
		return;
	}
	
	window.open(editUrl + dataId, '_blank');
}


function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(mapAreaTableId);
		return;
	}
	
	$.post(deleteUrl, {mapAreaId: dataId})
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
		
		custombox.alertSuccess(response.description);
		dataTableUtility.tableStandingRedraw(mapAreaTableId);
	});
	
}