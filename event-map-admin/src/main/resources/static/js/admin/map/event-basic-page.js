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



function getExcelParams(params) {
		
		
	var url = "";
	
	var eventItem = {
	
		title: params.title,
	  	spot: params.spot,
	  	description: params.description,
	  	city: params.city,
	  	country: params.country,
	  	layerId: params.layerId,
	  	eventGroupId: params.eventGroupId,
	  	eventTypeId: params.eventTypeId,
	  	state: params.state,	
		startDateStr: params.startDateStr,
		endDateStr: params.endDateStr,
		blackListTag: params.blackListTag	
	}
	  
	url= contextPath + "admin/map/event-basic/export/" + encodeURIComponent(JSON.stringify(eventItem));

	return url;
}


let $exportExcelModal = $('#exportExcelModal');
let $saveExcelBtn = $("#exportExcelModal #excel-save-btn");
$saveExcelBtn.on("click", function () {
  
  	custombox.confirm(lang.get("label.are.you.sure.you.want.to.export.to.excel"), function (result) {
		
		if (result) {	
		
			var params = [];
			dataTableUtility.getExtraParams(params);
			var url = getExcelParams(params);

			$.blockUI({ message: $.blockUI.defaults.message }); 
			window.location.href = url
			excelStateInformationControl();
			$exportExcelModal.modal("hide");
		}
		else {
          
			$exportExcelModal.modal("hide");

        }
		
	});		
});	
		

            

function excelStateInformationControl() {        

	var excelStateInformation = "started";
  	setTimeout(function() { 
      	
		$.get("/admin/map/event-basic/excelStateInformation")
		.fail(function (xhr) {
			xhr.state = false;
			console.error(xhr);
			xhr.description = lang.get("label.unknown.error");
			xhr.redirectUrl = null;
		})
		.always(function (data) {
			
			excelStateInformation = data;
			
			if(excelStateInformation == "finished"){
			
				$.unblockUI();
			}else{
				excelStateInformationControl(); 
			}		
			
		})
                  
  }, 1000)
}


$editModal.on('show.bs.modal', function () {
	let modal = $(this);
	modal.clearForm(true);	
	modal.find("#eventBatchOperationsInfo").text("");
	modal.find('.modal-title').text(lang.get("label.batch.operations"));
	
	let modalData = $('#editModal *').serializeArray();
	let batchState = modalData[1] != undefined ? modalData[1].value : false;
	
	var str = $("#eventBasicTableId_info").text();
	var mySubString = str.substring(
	    str.indexOf("(") + 1, 
	    str.lastIndexOf(")")
	);
		
	var eventCount = mySubString.split(" ")[1];
	eventCount = eventCount != undefined ? eventCount : 0;
		
	var stateColor = batchState ? "green" : "red";
	let batchStateText = batchState ? "Aktif" : "Pasif"
	var html = '<p>Toplam <span style="font-weight: bold">'+ eventCount +'</span> kayıt <span style="color: '+ stateColor +'">'+ batchStateText +'</span> durumuna çekilmektedir.</p>'
	
	modal.find("#eventBatchOperationsInfo").append(html);
	
	$("#eventBatchOperationsState").on("change", function(){
		modal.find("#eventBatchOperationsInfo").text("");
		let modalData = $('#editModal *').serializeArray();
		let batchState = modalData[1] != undefined ? modalData[1].value : false;
		let batchStateText = batchState ? "Aktif" : "Pasif"
		
		var stateColor = batchState ? "green" : "red";
		var html = '<p>Toplam <span style="font-weight: bold">'+ eventCount +'</span> kayıt <span style="color: '+ stateColor +'">'+ batchStateText +'</span> durumuna çekilmektedir.</p>'
	
		modal.find("#eventBatchOperationsInfo").append(html);
	});
	
});


$exportExcelModal.on('show.bs.modal', function () {
		
		let modal = $(this);
		modal.clearForm(true);	
		modal.find("#exportExcelInfo").text("");
		modal.find('.modal-title').text(lang.get("label.export.to.excel"));
	
	    var str = $("#eventBasicTableId_info").text();
		var mySubString = str.substring(
		    str.indexOf("(") + 1, 
		    str.lastIndexOf(")")
		);
	   
	    var eventCount = mySubString.split(" ")[1];
		eventCount = eventCount != undefined ? eventCount : 0;
		
		var html = "";
		if(eventCount != 0){
			
			var eventCount = eventCount.split(",");	
			var resultCount = "";	
			if(eventCount.length > 1){
				$.each(eventCount, function(key, value){
					resultCount += value;
				});
			}else{
				resultCount = eventCount[0];
			}
					
			eventCount = parseInt(resultCount);	
					
			
			var newEventCount = $("#excelDownloadEventCount").val();
			newEventCount = newEventCount.toLocaleString();
			newEventCount = parseInt(newEventCount);
			
			html = '<p> '+lang.get('label.export.excel.limited.event.download').format(newEventCount.toLocaleString()); +'</p>'
		
			if(newEventCount > eventCount){
				newEventCount = eventCount;
				
				html = '<p><span style="font-weight: bold">'+ newEventCount.toLocaleString() +'</span> kayıt <span style="color: '+'">'+'</span> Excel e aktarılacaktır.</p>'
		
			}
		}else{
			html = "<p>"+lang.get('label.record.not.found')+"</p>"
		}
		
		
	   
	    modal.find("#exportExcelInfo").append(html);
	
	});
		

function StateChanged(dataId){	
	
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	
	var url = contextPath + "admin/map/event-basic/stateChange";
	
	$.post(url, {id: dataId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
		
		dataTableUtility.tableStandingRedraw(g_tableId);
	})
	.always(function (response) {
		
		if(!response.state){
			dataTableUtility.tableStandingRedraw(g_tableId);
			custombox.alertError(response.description);
		}
	});

}

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
