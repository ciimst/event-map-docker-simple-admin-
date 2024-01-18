
const editUrl = contextPath + "admin/map/event-type/edit/";
const deleteUrl = contextPath + "admin/map/event-type/delete";
const currentTableId = "#eventTypeTableId";


$(document).ready(function () {


});


function openEditModal(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(currentTableId);
		return;
	}
	
	window.open(editUrl + dataId, '_self');
}


function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(currentTableId);
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
			return;
		}
		
		custombox.alertSuccess(response.description);
		dataTableUtility.tableStandingRedraw(currentTableId);
	});
	
}
