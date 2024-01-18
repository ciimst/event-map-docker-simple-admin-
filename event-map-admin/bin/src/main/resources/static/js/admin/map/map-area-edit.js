const ADD_LINK = contextPath + "admin/map/map-area/add";
const SAVE_LINK = contextPath + "admin/map/map-area/save";

class MapAreaMiniMap extends MiniMap {
	
	/*----------------Overridden methods-------------------------------------------------------*/
	
	constructor(mapDomId) {
		super(mapDomId);
	}
	
	preInit() {
		this.polygon = undefined;
		this.layerItemDom = undefined;
		try {
			let coordinateInfos = JSON.parse(mapAreaItem.coordinateInfo);
			if (coordinateInfos) {
				this.coordinates = [coordinateInfos];
			} else {
				this.coordinates = [];
			}
		} catch (e) {
			this.coordinates = [];
		}
		
		try {
			this.mapAreaGroup = this.getMapAreaGroupById(mapAreaItem.mapAreaGroupId);
		} catch (e) {
			this.mapAreaGroup = null
		}
		
		// super.preInit();
	}
	
	loadItems() {
		// super.loadItems();
		
		let mapAGroup = this.getCurrentMapAreaGroup();
		if (!mapAGroup) {
			return;
		}
		let state = {
			color: mapAGroup.color
		};
		
		
		if (this.polygon) {
			this.removeLayer(this.polygon);
		}
		
		if (!this.coordinates || this.coordinates.length === 0) {
			return;
		}
		
		this.polygon = L.polygon(
			this.coordinates,
			state
		);
		
		this.polygon.bindPopup(mapAreaItem.title + " (" + mapAGroup.name + ")");
		this.addLayer(this.polygon);
		
		this.map.setView(this.polygon.getBounds().getCenter(), 4);
	}
	
	onCreated(e) {
		
		let layer = e.layer;
		
		if (this.polygon) {
			custombox.alertError(lang.get("label.only.add.polygon.one"));//TODO:lang
			return;
		}
		
		this.polygon = layer;
		this.reloadCoordinates();
		
		//bu yoksa addLayer ile eklemek lazım
		super.onCreated(e);
	}
	
	onEdited(e) {
		let self = this;
		let layers = e.layers;
		layers.eachLayer(function (layer) {
			self.polygon = layer;
			self.reloadCoordinates();
		});
		// super.onEdited(e);
	}
	
	onDeleted(e) {
		
		this.polygon = undefined;
		this.reloadCoordinates();
		// super.onDeleted(e);
	}
	
	initUiTriggers() {
		// super.initUiTriggers();
		let self = this;
		
		$("#coordinateInfo").on("change", function () {
			
			let $coords = $(this);
			clearTimeout(self.coordinateTimeout);
			self.coordinateTimeout = setTimeout(function () {
				
				let coordsVal = $coords.val();
				try {
					
					let coords = JSON.parse(coordsVal);
					if (coords) {
						self.coordinates = [coords];
					} else {
						self.coordinates = null;
					}
				} catch (e) {
					self.coordinates = null;
				}
				self.loadItems();
			}, 300);
		});
		
		$("#mapAreaGroupId").on("change", function () {
			
			self.setMapAreaGroupById($(this).val());
			self.updateLayerItem();
			self.loadItems();
		});
		this.addLayerItem();
	}
	
	getExpandStyles() {
		return {
			top: "-170px",
			height: "455px",
			width: "200%"
		};
	}
	
	getDrawOptions() {
		let drawOptions = super.getDrawOptions();
		drawOptions.draw.polygon = {
			allowIntersection: false, // Restricts shapes to simple polygons
			// drawError: {
			// 	color: '#e1e100', // Color the shape will turn when intersects
			// 	message: '<strong>Oh snap!<strong> you can\'t draw that!' // Message that will show when intersect
			// },
			// shapeOptions: {
			// 	color: '#97009c'
			// }
		};
		drawOptions.draw.marker = false;
		drawOptions.edit.remove = true;
		return drawOptions;
	}
	
	/*-------------------------------------------------------------------------------------------*/
	
	//------------------------------------custom functions------------------------------------
	
	reloadCoordinates() {
		
		if (this.polygon) {
			
			let lls = this.polygon.getLatLngs()[0].map(function (point) {
				// return [parseFloat(point.lat.toFixed(5)), parseFloat(point.lng.toFixed(5))];
				return [point.lat, point.lng];
			});
			this.coordinates = lls;
			
		} else {
			
			this.coordinates = [];
		}
		
		this.invalidateCoordinateInfo();
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
				layerItem.innerHTML = self.getCurrentMapAreaGroup().layerName;
				
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
	
	updateLayerItem() {
		
		this.layerItemDom.innerHTML = this.getCurrentMapAreaGroup().layerName;
	}
	
	invalidateCoordinateInfo() {
		
		let infoValue = JSON.stringify(this.coordinates)
		.replace(/[0-9](],)/g, "],\n\t")
		.replace(/(],\[)/g, "],\n [")
		.replace(/(]])/g, "]\n]")
		.replace(/(\[\[\[)/g, "[\n [\n\t[")
		.replace(/(\[\[)/g, "[\n\t[")
		.replace(/(\[\[)/g, "[\n\t[")
		.replace(/(]])/g, " ]\n]");
		
		$("#coordinateInfo").val(infoValue);
	}
	
	setMapAreaGroupById(mapAreaGroupId) {
		this.mapAreaGroup = this.getMapAreaGroupById(mapAreaGroupId);
	}
	
	getMapAreaGroupById(mapAreaGroupId) {
		mapAreaGroupId = parseInt(mapAreaGroupId);
		return  mapAreaGroupItems.filter(function (item) {
			return item.id === mapAreaGroupId;
		})[0];
	}
	
	getCurrentMapAreaGroup() {
		return this.mapAreaGroup;
	}
	
}

let mapAreaMiniMap = new MapAreaMiniMap("mapid");


$(document).ready(function () {
	
	
	$("#m_aside_left_minimize_toggle").on("click", function () {
		//soldaki menü açılıp kapanırken harita boyutunu yeniliyoruz, bozulmasın diye.
		setTimeout(function () {
			mapAreaMiniMap.getMap().invalidateSize();
		}, 300);
	});
	
	if (mapAreaNotFound) {
		custombox.alertError(lang.get("label.mapArea.not.found"), function () {
			
			window.location.href = ADD_LINK;
		});
	}
	
	$("#save-button").on("click", function () {
	
		if (!validate()) {
			return;
		}
		let formData = $("form.map-area-form").serializeArray();
		let coordinates = $("#coordinateInfo").val();
		if (!coordinates) {
			
			custombox.confirm(lang.get("label.location.null.continue"), function (result) {
				if (result) {
					saveMapArea();
				}
			});
		} else {
			
			custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
				
				if (result) {
					
					saveMapArea();
				}
			});
		}
	});
	
	let idVal = $("form.map-area-form").find("#id").val();
	if (idVal) {
		$(".edit-page-title").html(lang.get("label.map_area_edit"));
	} else {
		$(".edit-page-title").html(lang.get("label.map_area_add"));
	}
});


function validate() {
	
	let title = $("#title").val();
	if (!title) {
		custombox.alertError(lang.get("label.title.not.null"));
		return false;
	}
	
	let mapAreaGroupId = $("#mapAreaGroupId").val();
	if (!mapAreaGroupId) {
		custombox.alertError(lang.get("label.mapAreaGroup.not.found"));
		return false;
	}
	
	
	return true;
}

function saveMapArea() {
	
	let formData = $("form.map-area-form").serializeArray();
	
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

