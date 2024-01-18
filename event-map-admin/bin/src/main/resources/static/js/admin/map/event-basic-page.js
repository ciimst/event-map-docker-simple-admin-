let editUrl = contextPath + "admin/map/event-basic/edit/";
let deleteUrl = contextPath + "admin/map/event-basic/deleted";
let g_tableId = "#eventBasicTableId";


function createDateFormatter(data, type, full) {
	return full.eventDateStr;
}
function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}

var options = $('#eventGroupId option');
$("#layerId").on("change", function(){	

	var layerId = $(this).val();	
	var optionsList = $('#eventGroupId option');
	
	if(layerId == ""){
		reloadEventGroup()
	}
	let eventGroupFilterUrl = "admin/map/event-basic/eventGroupFilter/"
	$.post(eventGroupFilterUrl, {layerId: layerId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
		
		let unremoveList = [];
		$.each(response.data, function(key, data){
			
			unremoveList.push(data);
		});
		
		for(var i = 1; i<optionsList.length; i++){												
			optionsList[i].remove();
		}
		
		for(var j = 0; j<unremoveList.length; j++){			
			$('#eventGroupId').append('<option value='+unremoveList[j].id+' >'+unremoveList[j].name+'</option>')			
		}		
	});
});

function reloadEventGroup(){
	
	for(var i = 1; i<options.length; i++){			
		$('#eventGroupId').append('<option value='+options[i].value+' >'+options[i].text+'</option>')			
	}
}

function openEditModal(elem) {	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	
	window.open(editUrl + dataId, '_blank');
}

function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
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
		
		
		dataTableUtility.tableStandingRedraw(g_tableId);
	});
}
/*
$(function () {
    $("input[name='eventDateStr']").datetimepicker({
    	format: "dd.mm.yyyy HH:ii:ss",
    	todayBtn: "linked",
    	pickTime: true,
        useSeconds: false,
        showOn: "button",
        closeOnDateSelect: true
    });
});*/
