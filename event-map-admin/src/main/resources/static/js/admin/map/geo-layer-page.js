let editUrl = contextPath + "admin/map/geolayer/edit";
let deleteUrl = contextPath + "admin/map/geolayer/delete";
let g_tableId = "#geoLayerTableId";


function createDateFormatter(data, type, full) {
	return full.createDateStr;
}
function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}

function descFormatter(data, type, full) {
	
	//TODO: burada düzenlemeyse aradaki farkı bulup gösterebiliriz sadece.
	let result = "<div class='geoLayer-desc' onclick='descPrettify(this);' style='cursor: pointer'>{0}</div>"
	.f(data);
	return result;
}

function descPrettify(elem) {
	
	if (!$(elem).hasClass("prettified")) {
		let jsonObj = JSON.parse($(elem).html());
		let jsonPretty = JSON.stringify(jsonObj, null, '\t');
		let escaped = new Option(jsonPretty).innerHTML;
		$(elem).html("<pre><code> {0} </code></pre>".f(escaped));
		$(elem).addClass("prettified");
	} else {
		
		
		let jsonObj = JSON.parse($(elem).find("pre>code").html());
		let jsonPretty = JSON.stringify(jsonObj);
		$(elem).html(jsonPretty);
		$(elem).removeClass("prettified");
	}
	
}

let $editModal = $('#editModal');
let $saveBtn = $("#editModal #layer-save-btn");
$saveBtn.on("click", function (e) {
	
	let modalData = $('#editModal *').serializeArray();
	
	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$.post(contextPath + "admin/map/geolayer/save", modalData)
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
	
	$.post(editUrl, {geoLayerId: dataId})
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
	
	$.post(deleteUrl, {geoLayerId: dataId})
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
		
		dataTableUtility.tableStandingRedraw(g_tableId);
	});
}


$editModal.on('show.bs.modal', function (event) {
	
	let button = $(event.relatedTarget);
	let type = button.data("type");
	let modal = $(this);
	modal.clearForm(true);
	
	$('#layerId').val(null).trigger('change');
	
	let title;
	
	if (type === "add") {
		
		title = lang.get("label.geoLayer.add");//TODO:lang
	} else {
		
		let data = jQuery.data(button[0], "response-data");
		
		if (!data || !(data.id && Number.isInteger(data.id))) {
			custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
			modal.data("will-hide", true);
			return;
		}
		
		modal.find('input#id').val(data.id);
		modal.find('input#name').val(data.name);
		modal.find('input#data').val(data.data);
		modal.find('input#state').prop("checked", data.state);		
		modal.find('select#layerId').val(data.layerId).trigger('change');
		title = lang.get("label.geoLayer.edit");//TODO:lang
	}
	
	modal.find('.modal-title').text(title);
	
});

$('select[name="state"]').on("change", function (e) {
	$("#search-button").click();
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
