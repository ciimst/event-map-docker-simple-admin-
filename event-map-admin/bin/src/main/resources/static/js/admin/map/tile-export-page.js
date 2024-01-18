let deleteUrl = contextPath + "admin/map/tile-export/delete";
let editUrl = contextPath + "admin/map/tile-export/save/"
let tileExportTableId = "#tileExportTableId";


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


var center = [38.6, 33.7];
var map = L.map('mapid').setView(center, 4);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {maxZoom: 18}).addTo(map);

var drawnItems = new L.FeatureGroup();
map.addLayer(drawnItems);

var rectangleCount = 0;
let data = {};

var tileExportId = $("#tileExportId").val();
if(tileExportId != null && tileExportId != ""){
		
	$.post("/admin/map/tile-export/getCoordinate", {tileExportId: tileExportId})
	.fail(function (xhr) {
			xhr.state = false;
			console.error(xhr);
			xhr.description = lang.get("label.unknown.error");
			xhr.redirectUrl = null;
	})
	.always(function (response) {
												
		var bounds = [[response.minLat, response.maxLong], [response.maxLat, response.minLong]];
		var rect = L.rectangle(bounds, {color: 'blue', weight: 1}).on('click', function (e) {}).addTo(map);
		drawnItems.addLayer(rect);
		rectangleCount = 1;
			
		data.lat1 = response.maxLat;
		data.long1 = response.minLong;
		data.lat2 = response.minLat;
		data.long2 = response.maxLong;
		data.tileServerId = response.tileServerId;
		data.tileServerUrl = response.tileServerUrl;
		data.tileServerName = response.tileServerName;
		
		map.setView(rect._latlngs[0][0]);	
		map.fitBounds(rect._bounds);	
	});	
}

var drawControl = new L.Control.Draw({
	position: 'topleft',
	draw: {
		polyline: false,
		polygon: false,
		circle: false,
		marker: false,
		circlemarker: false,
        rectangle: editAndRemove()
	},
	edit: {
		edit: editAndRemove(),
		featureGroup: drawnItems,
		remove: editAndRemove()
	}
});

map.addControl(drawControl);

function editAndRemove(){
	
	if($("#editAndSaveButton").find("#tileExportSaveText").text() != lang.get("label.save") && getUrlParameter("id") != 0){
		return false;
	}else{
		return true;
	}
	
}

map.on('draw:created', function (e) {
	var type = e.layerType,
	layer = e.layer;
	drawnItems.addLayer(layer);
		
	data.lat1 = layer._bounds.getSouthWest().lat;
	data.long1 = layer._bounds.getSouthWest().lng;
	data.lat2 = layer._bounds.getNorthEast().lat;
	data.long2 = layer._bounds.getNorthEast().lng;
		
	rectangleCount++;
	if(rectangleCount > 1){		
		custombox.alertError(lang.get("label.only.rectangle.create"));
		layer.remove()		
	}
});

map.on('draw:edited', function (e) {
	var layers = e.layers;
	var countOfEditedLayers = 0;
	layers.eachLayer(function(layer) {
		countOfEditedLayers++;
	});
});
		
map.on('draw:deleted', function (e) {
	var layers = e.layers;
	rectangleCount = 0;
});


let tileServerName = null;
map.on('baselayerchange', function(tileServer) {
  tileServerName = tileServer.name
});


let tileLayerList = null;
let tileList = null;
let eventLayerGroupList = [];
tileServer();
function tileServer(){
	
	$.ajax({ type: "POST",   
		     url: "/admin/map/tile-export/tileServer",   
		     async: true,
		     contentType: "application/json",
		     success : function(tileServers)
		     {	
				
					tileList = tileServers;
					tileLayerList = addTileServers(tileList);					
					
					if(getUrlParameter("id") == 0){
			    		AddMapControlBoxWhenReady();
					}
	 				
					
				
		     }
	});
}


function addTileServers (tileServers) {
    	
    this.tileLayerMap = []; 
    for (let i = 0; i < tileServers.length; i++) {
    		
    	var tileLayer = L.tileLayer(tileServers[i].url, {
    		attribution: '<a href="https://www.openstreetmap.org/">OpenStreetMap</a>',
    		maxZoom: 18,
    	});
    		
    	this.tileLayerMap[tileServers[i].name] = tileLayer;
    }
    
	if(data.tileServerName != "" && data.tileServerName != undefined){
		this.tileLayerMap[data.tileServerName].addTo(this.map); 
	}else{
		this.tileLayerMap[tileServers[0].name].addTo(this.map); 
	}
       	
    	
    return this.tileLayerMap;
}

function AddMapControlBoxWhenReady () {
    	
	addMapControlBox(tileLayerList, eventLayerGroupList);	
}

function addMapControlBox (tileLayerList, overlaysMap){
    	
   L.control.layers(tileLayerList, overlaysMap, {"collapsed" : false}).addTo(this.map);

}


function createTile(){
	
	let dataId = 0;
	window.open(contextPath + 'admin/map/tile-export/tile?id='+dataId, '_blank');

}

function openEditModal(elem) {	
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.try.again"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	window.open(contextPath + 'admin/map/tile-export/tile?id='+dataId, '_blank');
	
}

function tileExportSave(){

	if($("#editAndSaveButton").find("#tileExportSaveText").text() == lang.get("label.save")){
		
		if(getUrlParameter("id") == 0){
			
			save();
			
		}else{
			custombox.confirm(lang.get("label.export.delete"), function (result) {
				if (result == true) {								
					save();
				}else {						
					location.reload();
				}
			});
		}	
		
	}else if($("#editAndSaveButton").find("#tileExportSaveText").text() == lang.get("label.edit")){
		
		 drawControl = new L.Control.Draw({
			position: 'topleft',
			draw: {
				polyline: false,
				polygon: false,
				circle: false,
				marker: false,
				circlemarker: false,
		        rectangle: true
			},
			edit: {
				edit: true,
				featureGroup: drawnItems,
				remove: true
			}
		});
		map.addControl(drawControl);
		
		
		$("#editAndSaveButton").find("#tileExportSaveText").text(lang.props["label.save"]);
		$('input[type="text"]').prop("disabled", false);
		$("#tileCreateButton").prop("disabled", true);
			
		AddMapControlBoxWhenReady();
		
	}
}

function save(){
	
	var maxLat = 0;
	var minLat = 0;
	var maxLong = 0;
	var minLong = 0;
	var tileServerId = 0;	
	if(rectangleCount == 0){		
		custombox.alertError(lang.get("label.create.field"));
			
	}else{

		maxLat = data.lat1;
		minLat = data.lat2;
		maxLong = data.long1;
		minLong = data.long2;
			
		if(getUrlParameter("id") == 0){
			tileServerId = tileList[0].id;
		}
			
		$.each(tileList, function(key, value){ //farklı bir seçim yaptığında
			if(tileServerName == value.name){
				tileServerId = value.id;
			}
		})
		
		if(tileServerId == 0 && data.tileServerId != undefined){
			
			tileServerId = data.tileServerId;
		}
		
				
		var minZoom = $('#minZoom').val();
		var maxZoom = $('#maxZoom').val();
		var name = $("#name").val();
		var tileExportId = $("#tileExportId").val();
		
		let saveUrl = "admin/map/tile-export/save";
		$.post(saveUrl, {lat1: maxLat, long1: maxLong, lat2: minLat, long2: minLong, minZoom: minZoom, maxZoom: maxZoom, name: name, tileExportId: tileExportId, tileServerId: tileServerId})
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
				setTimeout(function(){window.location=contextPath + 'admin/map/tile-export/tile?id='+response.data;}, 1000);
			}
												
		});
	}
}

function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(tileExportTableId);
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
		}else{
			custombox.alertSuccess(response.description);
		}
		
		dataTableUtility.tableStandingRedraw(tileExportTableId);
	});
}

function tileExportCreate(){

	if(rectangleCount == 0){		
		
		custombox.alertError(lang.get("label.create.field"));
				
	}else{
		
		var minZoom = $('#minZoom').val();
		var maxZoom = $('#maxZoom').val();
		var name = $("#name").val();
			
		if($("#tileServerId").val() != ""){
			
			tileServerUrl = $("#tileServerUrl").val();
			tileServerId = $("#tileServerId").val();
		}
		
		let url = "/admin/map/tile-export/tileExport";
		$.post(url, {lat1: data.lat1, long1: data.long1, lat2: data.lat2, long2: data.long2, minZoom: minZoom, maxZoom: maxZoom, tileExportId: tileExportId, tileServerUrl: tileServerUrl, tileServerId: tileServerId})
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
				setTimeout(function(){window.location=contextPath + 'admin/map/tile-export/tile?id='+tileExportId;}, 1000);				
			}
													
		});
	}					
}


function createDateFormatter(data, type, full) {
	return full.createDateStr;
}



$(document).ready(function (){
	
	
	if(getUrlParameter("id") == 0){
		
		$("#editAndSaveButton").find("#tileExportSaveText").text(lang.props["label.save"]);
		$('input[type="text"]').prop("disabled", false);

		$("#tileCreateButton").prop("disabled", true);
			
	}else{
		
		$('input[type="text"]').prop("disabled", true);
		$("#tileCreateButton").prop("disabled", false);
		
	}
	
	
});


