
const editUrl = contextPath + "admin/map/event-type/edit/";
const deleteUrl = contextPath + "admin/map/event-type/delete";
const currentTableId = "#eventTypeTableId";
const ADD_LINK = contextPath + "admin/map/event-type/add";
const SAVE_LINK = contextPath + "admin/map/event-type/save";


$(document).ready(function () {
	
	let $imageIcon = $("#image");
	$imageIcon.on("change paste keyup", function (e) {
		
		let imgPath = $(this).val();
		if (imgPath) {
			loadIcon(imgPath);
		}
		
	});
	
	let iconPath = $imageIcon.val();
	if (iconPath) {
		loadIcon(iconPath);
		
		$("svg").append(''+iconPath+'')
		$("#iconSvg").html($("#iconSvg").html());
	}
	
	$("#save-button").on("click", function () {
		
		if (!validate()) {
			return;
		}
		custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
			
			if (result) {
				
				saveEventType();
			}
		});
	});
	
	let idVal = $("form.event-type-form").find("#id").val();
	if (idVal) {
		$(".edit-page-title").html(lang.get("label.eventType.edit"));
	} else {
		$(".edit-page-title").html(lang.get("label.eventType.add"));
	}
	
});

function validate() {
	
	let name = $("#name").val();
	if (!name) {
		custombox.alertError(lang.get("label.name.not.null"));
		return false;
	}
	
	let imagePath = $("#image").val();
	if (!imagePath) {
		custombox.alertError(lang.get("label.icon.not.null"));
		return false;
	}
	
	return true;
}

function saveEventType() {
	
	let formData = $("form.event-type-form").serializeArray();
	
	$.post(SAVE_LINK, formData)
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (data) {
		
		if (data.state) {
			
			custombox.alertSuccess(data.description, function () {
				
				if (data.redirectUrl) {
					window.location.href = contextPath + data.redirectUrl
				}
			});
		} else {
			
			custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
		}
	});
}

function loadIcon(value) {
	
	let url = getSvgFromEventType(value);
	let img = '<img src="{0}" >'.f(url);
	$(".icon-preview").html(img);
}

function getSvgFromEventType(image) {
	
	let pathDom = document.domFromString(image);
	pathDom.setAttribute("fill", "#000");
	pathDom.setAttribute("stroke", "#6d6f71");
	pathDom.setAttribute("stroke-width", '1');
	let path = document.domToString(pathDom);
	
	let svg = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1'  viewBox='0 0 100 100'> " +
		path +
		"</svg>";
	let iconUrl = 'data:image/svg+xml;base64,' + btoa(svg);
	return iconUrl
}