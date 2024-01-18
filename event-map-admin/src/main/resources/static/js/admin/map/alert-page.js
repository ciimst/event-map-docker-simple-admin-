let deleteUrl = contextPath + "admin/map/alert/deleted";
let g_tableId = "#alertTableId";


function createDateFormatter(data, type, full) {
	return full.eventDateStr;
}
function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}

var options = $('#eventGroupId option');
$("#layerId").on("change", function(){	
	
	$.unblockUI();
	
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

$("#layerId").on("change", function (e) {
	$("#search-button").click();
});

$("#eventGroupId").on("change", function (e) {
	$("#search-button").click();
});

$("#eventTypeId").on("change", function (e) {
	$("#search-button").click();
});

$('select[name="stateId"]').on("change", function (e) {
	$("#search-button").click();
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


let $editModal = $('#editModal');
let $saveBtn = $("#editModal #layer-save-btn");
$saveBtn.on("click", function () {
	
	let modalData = $('#editModal *').serializeArray();
	let batchState = modalData[1] != undefined ? modalData[1].value : false;
	let formData = $("form").serializeArray();
	
	var url = contextPath + "admin/map/event-basic/batchOperations/"+ batchState;
	
	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$("#editModal *").prop('disabled',true);
			$.post(url, formData)
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
					
					$("#search-button").click();
					
				} else {
					
					custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
				}
				
				$("#editModal *").prop('disabled',false);
				
			});
		}
	});
	
});


$(function () {
   $("input[name='startDateStr']").datetimepicker({
		format: "dd.mm.yyyy HH:ii:ss",
		todayBtn: "linked",
		pickTime: true,
		useSeconds: false,
		showOn: "button",
		language: "tr",
		closeOnDateSelect: true
	});

	$("input[name='endDateStr']").datetimepicker({
		format: "dd.mm.yyyy HH:ii:ss",
		todayBtn: "linked",
		pickTime: true,
		useSeconds: false,
		showOn: "button",
		language: "tr",
		closeOnDateSelect: true
	});

});

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
