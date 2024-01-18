let editUrl = contextPath + "admin/map/blacklist/edit";
let blackListdeleteUrl = contextPath + "admin/map/blacklist/delete";
let g_tableId = "#blackListTableId";


function createDateFormatter(data, type, full) {
	return full.createDateStr;
}
function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}

$("#layerId").on("change", function (e) {
	$("#search-button").click();
});

$("#eventGroupId").on("change", function (e) {
	$("#search-button").click();
});

$("#eventTypeId").on("change", function (e) {
	$("#search-button").click();
});

$('select[name="state"]').on("change", function (e) {
	$("#search-button").click();
});

var options = $('#eventGroupId option');
$("#layerId").on("change", function(){	

	var layerId = $(this).val();	
	var optionsList = $('#eventGroupId option');
	
	if(layerId == ""){
		reloadEventGroup()	
	}
	
	let eventGroupFilterUrl = "admin/map/blacklist/eventGroupFilter/"
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

var options = $('#eventGroupId option');
$("#modalLayerId").on("change", function(){	

	var layerId = $(this).val();	
	var optionsList = $('#modalEventGroupId option');
	
	if(layerId == ""){
		reloadModalEventGroup()
	}
	
	let eventGroupFilterUrl = "admin/map/blacklist/eventGroupFilter/"
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
			$('#modalEventGroupId').append('<option value='+unremoveList[j].id+' >'+unremoveList[j].name+'</option>')			
		}		
		
		if(eventGroupId != ""){
			
				$editModal.find('select#modalEventGroupId').val(eventGroupId).trigger('change');
		}
		else {
				
		    	$editModal.find('select#modalEventGroupId').val("").trigger('change');
		}
	});
});

function reloadModalEventGroup(){

   for(var i = 1; i<options.length; i++){			
	   $('#modalEventGroupId').append('<option value='+options[i].value+' >'+options[i].text+'</option>')			
    }
}

let $editModal = $('#editModal');
let $saveBtn = $("#editModal #layer-save-btn");
$saveBtn.on("click", function (e) {
	
	let modalData = $('#editModal *').serializeArray();
	
	if (modalData.find(f=>f.name=="id").value=="") {
                   
         modalData.find(f=>f.name=="inputTag").value="";
         modalData[3].name="tag";
     }

    else {
           
         modalData.find(f=>f.name=="textareaTag").value=="";
         modalData[2].name="tag";
     }
     
	var message = lang.get("label.are.you.sure.want.to.save");      	
    if( $("input#state").is(':checked') ){
		message = lang.get("label.are.you.sure.want.to.save.the.blacklist");      		
    }

	custombox.confirm(message, function (result) {
		if (result) {

			$.post(contextPath + "admin/map/blacklist/save", modalData)
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
	
	$.post(editUrl, {blackListId: dataId})
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
		
			//field kapatıldı.
		$("#generalTagDiv").css("display", "none");
		$("#generalLayerDiv").css("display", "none");
		$("#generalEventGroupDiv").css("display", "none");
		$("#generalEventTypeDiv").css("display", "none");
		
		
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
	
	$.post(blackListdeleteUrl, {blackListId: dataId})
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

var eventGroupId = "";
$editModal.on('show.bs.modal', function (event) {

 	eventGroupId = "";

    var options = $('#modalEventGroupId option');
	
	let button = $(event.relatedTarget);
	let type = button.data("type");
	let modal = $(this);
	modal.clearForm(true);
	
	$("#generalTagDiv").css("display", "block");
	$("#generalLayerDiv").css("display", "block");
	$("#generalEventGroupDiv").css("display", "block");
	$("#generalEventTypeDiv").css("display", "block");
	
	
	modal.find('select#modalLayerId').val(null).trigger('change');
	modal.find('select#modalEventGroupId').val(null).trigger('change');
	modal.find('select#eventTypeId').val(null).trigger('change');
	
	let title;
	
	if (type === "add") {
	
	    $("#inputTag").css("display", "none");
        $("#textareaTag").css("display", "block");
	
		title = lang.get("label.blackList.add");//TODO:lang
	} else {
		
		$("#textareaTag").css("display", "none");
        $("#inputTag").css("display", "block");

		let data = jQuery.data(button[0], "response-data");
		
		if (!data || !(data.id && Number.isInteger(data.id))) {
			custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
			modal.data("will-hide", true);
			return;
		  }

        eventGroupId = data.eventGroupId;
		
		modal.find('input#id').val(data.id);
		modal.find('input#name').val(data.name);
		modal.find('input#inputTag').val(data.tag);	
		modal.find('select#modalLayerId').val(data.layerId).trigger('change');
		modal.find('select#modalEventGroupId').val(data.eventGroupId).trigger('change');
		modal.find('select#eventTypeId').val(data.eventTypeId).trigger('change');
		modal.find('input#state').prop("checked", data.state);		
		title = lang.get("label.blackList.edit");//TODO:lang
	}
	
	modal.find('.modal-title').text(title);
	
});

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
