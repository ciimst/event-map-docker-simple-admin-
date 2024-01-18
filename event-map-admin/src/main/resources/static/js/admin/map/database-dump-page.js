let restoreUrl = contextPath + "admin/map/dumps/restore";
let deleteUrl = contextPath + "admin/map/dumps/delete";
let backupUrl = contextPath + "admin/map/dumps/backup";
let databaseDumpsTableId = "#databaseDumpsTableId";

function createDateFormatter(data, type, full) {
	return full.createDateStr;
}

function restoreItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(layerTableId);
		return;
	}
	
	$.post(restoreUrl, {dumpId: dataId})
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
		
		dataTableUtility.tableStandingRedraw(databaseDumpsTableId);
	});
}

function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(layerTableId);
		return;
	}
	
	$.post(deleteUrl, {dumpId: dataId})
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
		
		dataTableUtility.tableStandingRedraw(databaseDumpsTableId);
	});
}

let $saveBtn = $("#saveDumpButton");
$saveBtn.on("click", function (e) {	
	
	custombox.confirm("Manuel olarak yedek alınmasını istiyor musunuz?", function (result) {
		if (result) {
			
			$.post(backupUrl)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (response) {
				
				if (!response.state) {
					
					custombox.alertError(response.description ? response.description : lang.get("label.unknown.error"));
					
				} else {
					
					custombox.alertSuccess(response.description);

				}
				
				dataTableUtility.tableStandingRedraw(databaseDumpsTableId);
				
			});
		}
	});
	
});
