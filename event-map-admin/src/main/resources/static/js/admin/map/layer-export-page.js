let deleteUrl = contextPath + "admin/map/layer-export/delete";
let zoomUrl = contextPath + "admin/map/layer-export/tileExport/"
let layerExportTableId = "#layerExportTableId";

function createDateFormatter(data, type, full) {
	return full.createDateStr;
}
function startDateFormatter(data, type, full) {
	return full.startDateStr;
}
function finishDateFormatter(data, type, full) {
	return full.finishDateStr;
}
function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}

$('select[name="layerId"]').on("change", function (e) {
	$("#search-button").click();
});

function createButton(){
	let dataId = 0;
	localStorage.setItem("save", 0);
	window.open(contextPath + 'admin/map/layer-export/zoom?id='+dataId, '_blank');
}



function openEditModalZoom(elem) {	
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError("Lütfen tekrar deneyiniz");//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	localStorage.setItem("save", 0);
	window.open(contextPath + 'admin/map/layer-export/zoom?id='+dataId, '_blank');
	
}

function tileExportCreate(){
	
	var minZoom = $('#minZoom').val();
	var maxZoom = $('#maxZoom').val();
	var startDate = $('#startDate').val();
	var finishDate = $('#finishDate').val();
	var layerExportId = $("#layerExportId").val();
	var layerId = $( "#layerSelectBox option:selected" ).val();
	var tileServerId = $( "#tileServerSelectBox option:selected" ).val();
	var name = $("#name").val();
	
	$.post(zoomUrl, {layerId: layerId, minZoom: minZoom, maxZoom: maxZoom, startDate: startDate, finishDate: finishDate, layerExportId: layerExportId, name: name, tileServerId: tileServerId})
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
		}else{
			
			custombox.alertSuccess(response.description);			
			setTimeout(function(){window.location=contextPath + 'admin/map/layer-export/zoom?id='+layerExportId;}, 1000);
		}
											
	});
	
}

function eventExportCreate(){
	
	var startDate = $('#startDate').val();
	var finishDate = $('#finishDate').val();
	var minZoom = $('#minZoom').val();
	var maxZoom = $('#maxZoom').val();
	var layerId = $( "#layerSelectBox option:selected" ).val();
	var tileServerId = $( "#tileServerSelectBox option:selected" ).val();
	var layerExportId = $("#layerExportId").val();
	var name = $("#name").val();
	
	var url = "admin/map/layer-export/eventExport"
	$.post(url, {layerId: layerId, minZoom: minZoom, maxZoom: maxZoom, startDate: startDate, finishDate: finishDate, layerExportId: layerExportId, name: name, tileServerId: tileServerId})
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
		}else{
			
			custombox.alertSuccess(response.description);
			setTimeout(function(){window.location=contextPath + 'admin/map/layer-export/zoom?id='+layerExportId;}, 1000);			
		}
											
	});
	
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
};

function layerExportSave(){
	if($("#editAndSaveButton").find("#layerExportSaveText").text() == lang.get("label.save")){
		
		if(getUrlParameter("id") == 0){
			
			save();
			
		}else{
			custombox.confirm(lang.get("label.export.delete") , function (result) {
				if (result == true) {								
					save();
				}else {						
					location.reload();
				}
			});
		}	
		
	}else if($("#editAndSaveButton").find("#layerExportSaveText").text() == lang.get("label.edit")){
		
		$("#editAndSaveButton").find("#layerExportSaveText").text(lang.props["label.save"]);
		$('input[type="text"]').prop("disabled", false);
		$("#layerSelectBox").prop("disabled", false);
		$("#tileServerSelectBox").prop("disabled", false);
		$("#createButton").prop("disabled", true);
		$("#tileCreateButton").prop("disabled", true);
		
	}
}

function save(){
	
	var minZoom = $('#minZoom').val();
	var maxZoom = $('#maxZoom').val();	
	var startDate = $('#startDate').val();
	var finishDate = $('#finishDate').val();
	var layerExportId = $("#layerExportId").val();
	var layerId = $( "#layerSelectBox option:selected" ).val();
	var tileServerId = $( "#tileServerSelectBox option:selected" ).val();
	var name = $("#name").val();
	let saveUrl = "/admin/map/layer-export/save"
		
		
	$.post(saveUrl, {id: layerId, minZoom: minZoom, maxZoom: maxZoom, startDate: startDate, finishDate: finishDate, layerExportId: layerExportId, name: name, tileServerId: tileServerId})
	.fail(function (xhr) {
		xhr.state = false;
		onsole.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
						
		if (!response.state) {
			custombox.alertError(response.description ? response.description : lang.get("label.unknown.error"));
			return;
		}else{
			custombox.alertSuccess(response.description);
			setTimeout(function(){window.location=contextPath+'admin/map/layer-export/zoom?id='+response.data;}, 1000);			
		}
															
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
	
	$.post(deleteUrl, {layerExportId: dataId})
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
		}else{
			custombox.alertSuccess(response.description);
		}
		
		dataTableUtility.tableStandingRedraw(layerExportTableId);
	});
}


$(document).ready(function (){
	
	if(getUrlParameter("id") == 0){
		
		$("#editAndSaveButton").find("#layerExportSaveText").text(lang.props["label.save"]);
		$('input[type="text"]').prop("disabled", false);
		$("#layerSelectBox").prop("disabled", false);
		$("#tileServerSelectBox").prop("disabled", false);
		$("#createButton").prop("disabled", true);
		$("#tileCreateButton").prop("disabled", true);
			
	}else{
		
		$('input[type="text"]').prop("disabled", true);
		$("#layerSelectBox").prop("disabled", true);
		$("#tileServerSelectBox").prop("disabled", true);
		$("#createButton").prop("disabled", false);
		$("#tileCreateButton").prop("disabled", false);
	}
	
	$("input[name='eventDateStr']").datetimepicker({
		format: "dd.mm.yyyy HH:ii:ss",
		todayBtn: "linked",
		pickTime: true,
		useSeconds: false,
		showOn: "button",
		language: "tr",
		closeOnDateSelect: true
	});
});