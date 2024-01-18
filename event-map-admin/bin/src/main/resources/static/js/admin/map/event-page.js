
MapItemUtility = function () {
};

MapItemUtility.prototype = {
	
	getCityByName: function (cityName) {
		
		return cityItems.find(cityItem => cityItem.name === cityName);
	},
	getCityCodeByName: function (cityName) {
		let found = cityItems.find(cityItem => cityItem.name === cityName);
		return found ? found.code || "" : "";
	},
	getEventGroupItemById : function (eventGroupId) {
		return eventGroupItems.find(eventGroupItem => eventGroupItem.id === eventGroupId);
	},
	getEventTypeItemById: function (eventTypeId) {
		return eventTypeItems.find(eventItemType => eventItemType.id === eventTypeId);
	}
};
let mapItemUtility = new MapItemUtility();

// https://leafletjs.com/reference-1.6.0.html#tilelayer-url-template
// 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
let mapHelper = new MapHelper("mapid", tileServers, eventItems, layerItems);


$(document).ready(function () {
	$("#m_aside_left_minimize_toggle").on("click", function () {
		//soldaki menü açılıp kapanırken harita boyutunu yeniliyoruz, bozulmasın diye.
		setTimeout(function () {
			mapHelper.getMap().invalidateSize();
		}, 300);
	});
});
function saveMarker(container) {
	console.log("saveMarker", container);
	console.log($(container).find("form").serializeArray());
	let formData = $(container).find("form").serializeArray();
	
	$.post(contextPath + "admin/map/event/save", formData)
		.fail(function (xhr) {
			xhr.state = false;
			console.error(xhr);
			xhr.description = lang.get("label.unknown.error");
			xhr.redirectUrl = null;
		})
		.always(function (data) {
			
			console.log("always", data);
		});
}

