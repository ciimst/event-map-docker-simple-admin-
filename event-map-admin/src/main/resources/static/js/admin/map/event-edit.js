const ADD_LINK = contextPath + "admin/map/event-basic/add";
const SAVE_LINK = contextPath + "admin/map/event-basic/save";

if($(".col-lg-4>.row>.col-12>#userId").val() == ""){	
	$(".col-lg-4>.row>.col-12>#userId").val("0");
}
if($(".col-lg-4>.row>.col-12>#groupId").val() == ""){	
	$(".col-lg-4>.row>.col-12>#groupId").val("0");
	$("input[name='state']").prop( "checked" ,true)
}

var beforeTagSelectedList = $('#tagId').val(); 
var afterTagSelectedList = [];
var tagDeleteControl = false;
$( "#tagId" ).change(function() {	
	afterTagSelectedList = $('#tagId').val()
	tagDeleteControl=true;	
});




/*-----------------------------------Yeni--------------------------------------------------- */
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
/*-----------------------------------Yeni--------------------------------------------------- */


EventIcon = L.Icon.extend({
		options: {
		iconSize:     [39, 39], // size of the icon
		iconAnchor:   [19.5, 19.5], // point of the icon which will correspond to marker's location
		popupAnchor:  [0, -19.5], // point from which the popup should open relative to the iconAnchor
		id: 0
	}
});

var addMarkerCount = 0;
 
class MarkerMiniMap extends MiniMap {
	
	/*----------------Overridden methods-------------------------------------------------------*/
	
	constructor(mapDomId) {
		super(mapDomId);
	}
	
	preInit() {
		this.marker = undefined;
		this.layerItemDom = undefined;
		this.coordinates = {lat: eventItem.latitude, lng: eventItem.longitude};
		this.setEventTypeById(eventItem.eventTypeId);
		this.setEventGroupById(eventItem.eventGroupId);
		// super.preInit();
	}
	
	loadItems() {
		// super.loadItems();
		if (!this.eventGroup || !this.eventType) {
			return;
		}
		
		
		this.removeLayer(this.marker);
		
		if (!this.coordinates) {
			return;
		}
		
		if(this.eventType.image != null &&  this.eventGroup.color!= null && this.coordinates.lat != null && this.coordinates.lng != null){
			
			eventItem.eventTypeImage = this.eventType.image;
			eventItem.eventGroupColor = this.eventGroup.color;
			
			var base64Icon = getOrGenerateBase64Icon(eventItem);
			this.marker = L.marker([this.coordinates.lat, this.coordinates.lng], {
			icon:  new EventIcon({iconUrl: base64Icon}), riseOnHover: true
			}).bindPopup(eventItem.description);
						
		
		}
		
	
		
		
		this.addLayer(this.marker);
				
		if(addMarkerCount == 1){
			
			this.setView(this.marker.getLatLng(), 4);
		}
		
		addMarkerCount++;
		
		$(".leaflet-marker-icon.leaflet-interactive").on("click", function(){
			$(".leaflet-marker-icon.leaflet-interactive").addClass("i-circle-marker");
			
		})
		
	}
	
	onCreated(e) {
		
		addMarkerCount++;
		let layer = e.layer;
		
		if(addMarkerCount > 1){
			if (this.marker) {
			custombox.alertError(lang.get("label.only.add.one.marker"));//TODO:lang
			return;
			}
		}
		
		
		this.marker = layer;
		this.reloadCoordinates();
		
		//bu yoksa addLayer ile eklemek lazım
		super.onCreated(e);
	}
	
	onEdited(e) {
		let self = this;
		let layers = e.layers;
		layers.eachLayer(function (layer) {
			self.marker = layer;
			self.reloadCoordinates();
		});
		// super.onEdited(e);
	}
	
	onDeleted(e) {
		addMarkerCount = 0;
		this.marker = undefined;
		// super.onDeleted(e);
	}
	
	initUiTriggers() {
		// super.initUiTriggers();
		let self = this;
		
		$("#longitude").on("change", function () {
			
			let $coords = $(this);
			clearTimeout(self.coordinateTimeout);
			self.coordinateTimeout = setTimeout(function () {
				
				self.coordinates["lng"] = $coords.val();
				self.loadItems();
			}, 300);
		});
		
		$("#latitude").on("change", function () {
			
			let $coords = $(this);
			clearTimeout(self.coordinateTimeout);
			self.coordinateTimeout = setTimeout(function () {
				
				self.coordinates["lat"] = $coords.val();
				self.loadItems();
			}, 300);
		});
		
		$("#eventTypeId").on("change", function () {
			
			self.setEventTypeById($(this).val());
			// self.updateLayerItem();
			self.loadItems();
		});
		
		$("#eventGroupId").on("change", function () {
			
			self.setEventGroupById($(this).val());
			// self.updateLayerItem();
			self.loadItems();
		});
		
		this.addLayerItem();
	}
	
	getDrawOptions() {
		let drawOptions = super.getDrawOptions();
		drawOptions.draw.marker = true;
		drawOptions.edit.remove = true;
		return drawOptions;
	}
	
	/*-------------------------------------------------------------------------------------------*/
	
	//------------------------------------custom functions------------------------------------
	
	setEventTypeById(eventTypeId) {
		eventTypeId = parseInt(eventTypeId);
		this.eventType = eventTypes.find(eventItemType => eventItemType.id === eventTypeId);
	}
	
	setEventGroupById(eventGroupId) {
		eventGroupId = parseInt(eventGroupId);
		this.eventGroup = eventGroups.find(eventGroup => eventGroup.id === eventGroupId);
	}
	
	reloadCoordinates() {
		let latLng = this.marker.getLatLng();
		this.coordinates["lat"] = latLng.lat;
		this.coordinates["lng"] = latLng.lng;
		$("#longitude").val(latLng.lng);
		$("#latitude").val(latLng.lat);
	}
	
	getCreateMarkerIconByEventType(eventType, color){
		let iconUrl = this.getSvgFromEventType(eventType, color);
		let icon = L.icon({
			iconUrl: iconUrl,
			iconSize: 30,
		});
		return icon;
	}
	getSvgFromEventType(eventType, color) {
		
		let pathDom = document.domFromString(eventType.image);
		pathDom.setAttribute("fill", color);
		pathDom.setAttribute("stroke", "#6d6f71");
		pathDom.setAttribute("stroke-width", '1');
		let path = document.domToString(pathDom);
		
		let svg = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1'  viewBox='0 0 100 100'> " +
			path +
			"</svg>";
		let iconUrl = 'data:image/svg+xml;base64,' + btoa(svg);
		return iconUrl
	}
	
	updateLayerItem() {
		
		this.layerItemDom.innerHTML = this.getCurrentMapAreaGroup().layerName;
	}
	
	addLayerItem() {
		let self = this;
		L.Control.LayerItem = L.Control.extend({
			onAdd: function(map) {
				let layerItem = L.DomUtil.create('div');
				let styles = {
					backgroundColor : "#fff",
					fontWeight : "bold",
					padding : "5px 10px 5px 10px"
				};
				
				Object.assign(layerItem.style, styles);
				layerItem.innerHTML = self.eventGroup ? self.eventGroup.layerName : "";
				
				self.layerItemDom = layerItem;
				return self.layerItemDom;
			},
			
			onRemove: function(map) {
				// Nothing to do here
			}
		});
		
		L.control.layerItem = function(opts) {
			return new L.Control.LayerItem(opts);
		};
		
		L.control.layerItem({ position: 'topright' }).addTo(this.getMap());
	}
}

let markerMiniMap = new MarkerMiniMap("mapid");


$(document).ready(function () {
	
	
	$("#m_aside_left_minimize_toggle").on("click", function () {
		//soldaki menü açılıp kapanırken harita boyutunu yeniliyoruz, bozulmasın diye.
		setTimeout(function () {
			markerMiniMap.getMap().invalidateSize();
		}, 300);
	});
	
	if (eventNotFound) {
		custombox.alertError(lang.get("label.event.not.found"), function () {
			
			window.location.href = ADD_LINK;
		});
	}
	
	$("#save-button").on("click", function () {

		if (!validate()) {
			return;
		}
		
		let formData = $("form#event-form").serializeArray();
		
		custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
			
			if (result) {	
								
				saveEvent();
			}
		});
	});	
			
	
	let idVal = $("form#event-form").find("#id").val();
	if (idVal) {
		$(".edit-page-title").html(lang.get("label.event.edit"));
	} else {
		$(".edit-page-title").html(lang.get("label.event.add"));
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
	
	$("div#m-dropzone-one").dropzone({
		url: contextPath + "admin/upload/single",
		paramName:"file",
		maxFiles: 1,
		maxFilesize: 5,
		headers: {
			'X-CSRF-TOKEN': csrf_token
		},
		success: function (e, o) {
			console.log("e", e, "o", o);
		}
	});
	
	// Dropzone.options.mDropzoneOne = {
	// 	paramName: "filefs",
	// 	parallelUploads: 1,
	// 	maxFiles: 1,
	// 	maxFilesize: 5,
	// 	accept: function(e, o) {
	// 		"justinbieber.jpg" == e.name ? o("Naha, you don't.") : o()
	// 	}
	// };
	//
	// Dropzone.options.mDropzoneTwo = {
	// 	paramName: "file",
	// 	maxFiles: 10,
	// 	maxFilesize: 10,
	// 	accept: function(e, o) {
	// 		"justinbieber.jpg" == e.name ? o("Naha, you don't.") : o()
	// 	}
	// };
	// Dropzone.options.mDropzoneThree = {
	// 	paramName: "file",
	// 	maxFiles: 10,
	// 	maxFilesize: 10,
	// 	acceptedFiles: "image/*,application/pdf,.psd",
	// 	accept: function(e, o) {
	// 		"justinbieber.jpg" == e.name ? o("Naha, you don't.") : o()
	// 	}
	// };
	
	
	
});

function validate() {
	
	let latitude = $("#latitude").val();
	if (!latitude) {
		custombox.alertError(lang.get("label.latitude.not.null"));
		return false;
	}
	
	let longitude = $("#longitude").val();
	if (!longitude) {
		custombox.alertError(lang.get("label.longitude.not.null"));
		return false;
	}
	let eventGroupId = $("#eventGroupId").val();
	if (!eventGroupId) {
		custombox.alertError(lang.get("label.eventGroup.not.found"));
		return false;
	}
	
	let eventTypeId = $("#eventTypeId").val();
	if (!eventTypeId) {
		custombox.alertError(lang.get("label.eventType.not.found"));
		return false;
	}
	
	return true;
}


var mediaPathList = [];
var coverImagePathLİst = [];
Dropzone.autoDiscover = false;
$("#my-dropzone").dropzone({
        success : function(file, response) {	
			mediaPathList.push(response);	
								
			$("form#event-form").append('<input type ="hidden" id="mediaPath" name="mediaPath" value='+response+'/>')
	
        }
    });

function saveEvent() {
	
	tagDelete();
	let formData = $("form#event-form").serializeArray();
	
	if($("#eventDate").val() == '' || $("#eventDate").val() == undefined){
		$("#eventDate").css("border-color","red")
		return;
	}
	
	$.post(SAVE_LINK, formData)
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (data) {
		
		if (data.state) {

			custombox.alertEventSuccess(data.description, function () {
				
				$("#mediaPath").val("");
				
				if (data.redirectUrl) {
					window.location.href = contextPath + data.redirectUrl
				}
			});
			
		} else {
			
			custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
		}
	});
}

function getOrGenerateBase64Icon(eventItem){
    	
     var base64Icon = SvgIcon.GenerateBase64Icon(eventItem.eventTypeImage, eventItem.eventGroupColor);
     return base64Icon;
  }

var deleteImageList = [];
function imageDelete(imageId){
	
	$("#"+imageId).remove();
	$("form#event-form").append('<input type ="hidden" id="deleteImageId" name="deleteImageId" value='+imageId+'>')
	deleteImageList.push(imageId)
	
}

let controlTag = true;	
function tagDelete(){
	$.each(beforeTagSelectedList, function(key, beforeTagId){	
		
		if(tagDeleteControl == true){			
			$.each(afterTagSelectedList, function(key1, afterTagId){
				if(afterTagId == beforeTagId){
					controlTag = false;					
				}
		})
			if(controlTag == true){
				$("form#event-form").append('<input type ="hidden" id="deleteTagId" name="deleteTagId" value='+beforeTagId+'>')
			}
			controlTag = true;
		}		
	});
}

