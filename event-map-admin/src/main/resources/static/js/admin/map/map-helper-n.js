MapHelper = function () {
	this.map = undefined;
	let southWest = L.latLng(-90, -180),
		northEast = L.latLng(90, 180);
	this.bounds = new L.LatLngBounds(southWest, northEast);
	this.defaultTileUrl = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
	this.tiles = {};
	this.groupedEventMarkers = {};
	this.eventMarkerGroups = {};
	this.overlayMapsByName = {};
	this.groupedOverlayMaps = {};
	this.layerss = undefined;
	
	this.eventMarkerLayerGroupById = {};
	//TODO: 0. argüman hangisi olduğunu belirleyebilir
	if (this.init && arguments[0] === "marker") {
		
		this.init.apply(this, arguments);
		
	} else if (this.initMapArea && arguments[0] === "map-area") {
		
		this.initMapArea.apply(this, arguments);
	}
};

MapHelper.prototype = {

	init : function (type, mapid, tileServers, eventItems, allLayers) {
		
		this.mapId = mapid;
		this._loadTiles(tileServers);//this.tiles
		this._loadMarkers(eventItems);//this.groupedEventMarkers
		//TODO: tiles (selected or something)
		let selectedTile = this.tiles["Open Real"] ? this.tiles["Open Real"] : L.tileLayer(this.defaultTileUrl);
		this.map = L.map(mapid, {
			center : this.bounds.getCenter(),
			zoom: 5,
			minZoom: 2,
//			maxZoom: 12,
			noWrap: true,
			// worldCopyJump: true,
			// continousWorld : true,
			continuousWorld : true,
			layers: [selectedTile],//TODO: tiles (selected or something)
			maxBounds: this.bounds,
			maxBoundsViscosity: 1.0
		})
		.setView([38.6, 33.7], 4);
		L.control.scale().addTo(this.map);
		
		this._loadGroupedMarkersIntoLayersx(allLayers);//this.overlayMapsByName
		
		this.setControlLayers(this.tiles, this.groupedOverlayMaps);
		
		this._addMarkerButton();
	},
	
	initMapArea: async function (type, mapid, tileServers, allLayers, mapAreaItems, mapAreaGroupItems) {
		
		this.drawLocale = await this._loadDrawLocales();
		L.drawLocal = this.drawLocale;
		
		this.mapId = mapid;
		this._loadTiles(tileServers);//this.tiles
		// TODO: loadAreas
		// this._loadMarkers(eventItems);//this.groupedEventMarkers
		
		let selectedTile = this.tiles["Open Real"] ? this.tiles["Open Real"] : L.tileLayer(this.defaultTileUrl);
		this.map = L.map(mapid, {
			center : this.bounds.getCenter(),
			zoom: 5,
			minZoom: 3,
			maxZoom: 12,
			noWrap: true,
			// worldCopyJump: true,
			// continousWorld : true,
			continuousWorld : true,
			layers: [selectedTile],//TODO: tiles (selected or something)
			maxBounds: this.bounds,
			maxBoundsViscosity: 1.0
		})
		.setView([38.6, 33.7], 4);
		L.control.scale().addTo(this.map);
		
		// this._loadGroupedMarkersIntoLayers(allLayers);//this.overlayMapsByName
		// TODO: areas into layers
		// this._loadGroupedMarkersIntoLayersx(allLayers);//this.overlayMapsByName
		
		this.setControlLayers(this.tiles, this.groupedOverlayMaps);
		
		this._addAreaButton(mapAreaItems, mapAreaGroupItems);
		
		
	},
	
	getMap: function () {
		return this.map;
	},
	addLayer: function (layer) {
		this.map.addLayer(layer);
	},
	addLayers: function (layers) {
		this.map._addLayers(layers);
	},
	setControlLayers : function (baseLayers, overlays) {
		this.layerss = L.control.customLayers(baseLayers, overlays).addTo(this.map);
	},
	
	_loadTiles : function (tileServers) {
		
		for (let i = 0; i < tileServers.length; i++) {
			
			this.tiles[tileServers[i].name] = L.tileLayer(tileServers[i].url, {
				attribution: '<a href="https://www.openstreetmap.org/">OpenStreetMap</a>',
			});
		}
	},
	_loadMarkers : function (eventItems) {
		// let eventMarkers = [];
		
		for (let i = 0; i < eventItems.length; i++) {
			let eventItem = eventItems[i];
			
			let icon = this.getCreateMarkerIconByEventTypeId(eventItem.eventTypeId, eventItem.eventGroupColor);
			
			let marker = L.marker([eventItem.latitude, eventItem.longitude], {
				icon: icon
			}).bindPopup(eventItem.description);
			
			marker.data = eventItem;
			// eventMarkers.push(marker);
			if (!this.groupedEventMarkers[eventItem.layerId]) {
				this.groupedEventMarkers[eventItem.layerId] = [];
			}
			this.groupedEventMarkers[eventItem.layerId].push(marker);
			
			if (!this.eventMarkerGroups[eventItem.eventGroupId]) {
				this.eventMarkerGroups[eventItem.eventGroupId] = [];
			}
			this.eventMarkerGroups[eventItem.eventGroupId].push(marker);
		}
	},
	removeMarker: function (eventGroup, marker) {
		let index = this.eventMarkerGroups[eventGroup.id].indexOf(marker);
		this.eventMarkerGroups[eventGroup.id].splice(index, 1);
		
		let layerGroup = this.groupedOverlayMaps[eventGroup.layerName + " - " + eventGroup.name];
		layerGroup.removeLayer(marker);
		marker.remove();
		
		if (layerGroup.getLayers().length < 1) {
			layerGroup.remove();
		}
	},
	updateMarker: function (oldEventGroup, eventGroup, marker) {
		this.removeMarker(oldEventGroup, marker);
		this.addMarker(eventGroup, marker);
	},
	addMarker: function (eventGroup, marker) {
		if (!this.eventMarkerGroups[eventGroup.id]) {
			this.eventMarkerGroups[eventGroup.id] = [];
		}
		this.eventMarkerGroups[eventGroup.id].push(marker);
		let layerGroup = this.groupedOverlayMaps[eventGroup.layerName + " - " + eventGroup.name];
		layerGroup.addLayer(marker);
		
		if (!this.map.hasLayer(layerGroup)) {
			layerGroup.addTo(this.map);
		}
		
	},
	_loadGroupedMarkersIntoLayers : function (allLayers) {
		
		for (const layerItem of allLayers) {
			
			let layerGroup;
			
			let groupedEventMarker = this.groupedEventMarkers[layerItem.id];
			if (groupedEventMarker) {
				layerGroup = new L.LayerGroup(groupedEventMarker);
				layerGroup.addTo(this.map);
			} else {
				layerGroup = new L.LayerGroup();
			}
			
			this.eventMarkerLayerGroupById[layerItem.id] = layerGroup;
			this.overlayMapsByName[layerItem.name] = layerGroup;
			
		}
	},
	_loadGroupedMarkersIntoLayersx : function (allLayers) {
		
		for (const layerItem of allLayers) {
			
			let layerGroup;
			
			layerGroup = new L.LayerGroup();
			layerGroup.isGroupName = true;
			layerGroup.name = layerItem.name;
			this.groupedOverlayMaps[layerItem.name] = layerGroup;
			
			for (const eventGroup of layerItem.eventGroups) {
				
				let groupedMarkers = this.eventMarkerGroups[eventGroup.id];
				if (groupedMarkers) {
					
					layerGroup = new L.LayerGroup(groupedMarkers);
					layerGroup.name = eventGroup.name;
					layerGroup.addTo(this.map);
					this.groupedOverlayMaps[layerItem.name + " - " + eventGroup.name] = layerGroup;
					
				} else {//normalde var ama markerı yok
					
					layerGroup = new L.LayerGroup();
					layerGroup.name = eventGroup.name;
					this.groupedOverlayMaps[layerItem.name + " - " + eventGroup.name] = layerGroup;
				}
			}
		}
	},
	_addMarkerButton : function () {
		let $mapDom = $("#" + this.mapId);
		let self = this;
		let stateChangingButton = L.easyButton({
			states: [{
				stateName: 'add',        // name the state
				icon: 'fa-crosshairs',               // and define its properties
				title: lang.get("label.event_add"),
				onClick: function(btn, map) {
					// and its callback
					btn.state('cancel');    // change state on click!
					$mapDom.awesomeCursor("crosshairs");
					
					map.on('click', async function(e){
						// var marker = new L.marker(e.latlng).addTo(map);
						map.off("click");
						btn.state('add');
						
						$mapDom.css('cursor', '');
						// let marker = L.marker(e.latlng);//todo referansını tut
						// let markerGeoInfo = await self.getReverse(e.latlng);
						
						//daha sonra ekliyecez gruba: layer seçip kaydedince.
						// eventMarkerLayerGroup.addLayer(marker);
						L.control.markerAddScreen(e).addTo(map);
						console.log("TODO: screen için control yazılacak.");
					});
				}
			}, {
				stateName: 'cancel',
				icon:      'fa-times',
				title:    lang.get("label.cancel"),
				onClick: function(btn, map) {
					
					btn.state('add');
					$mapDom.css('cursor', '');
				}
			}]
		});
		stateChangingButton.addTo(this.map);
	},
	
	_loadMapAreas: function (editableLayers, mapAreaItems, mapAreaGroupItems) {
		
		for (const mapAreaItem of mapAreaItems) {
			
			let mapAGroups = mapAreaGroupItems.filter(function (item) {
				return item.id === mapAreaItem.mapAreaGroupId;
			});
			
			if (mapAGroups.length > 0) {
				
				let mapAGroup = mapAGroups[0];
				let state = {
					properties: "test",
					"popupContent": "This is where the Rockies play!",
					color: mapAGroup.color
				};
				
				let polygon = L.polygon(
					[JSON.parse(mapAreaItem.coordinateInfo)],
					state
					);
				polygon.bindPopup(mapAreaItem.title + " (" + mapAGroup.name + ")");
				editableLayers.addLayer(polygon);
			}
			
		}
	},
	_loadDrawLocales: async function () {
		
		return await new Promise(function (resolve, reject) {
			
			try {
				$.getJSON(contextPath + "js/i18n/leaflet-draw-" + lang_iso + ".json?v=" + app_version, function(json) {
					resolve(json);
				});
			} catch (e) {
				
				reject(e);
			}
		});
	},
	_addAreaButton : async function (mapAreaItems, mapAreaGroupItems) {
		let $mapDom = $("#" + this.mapId);
		let self = this;
		// FeatureGroup is to store editable layers
		let editableLayers = new L.FeatureGroup();
		this.map.addLayer(editableLayers);
		
		let drawOptions = this.getDrawOptions(editableLayers);
		let drawControl = new L.Control.Draw(drawOptions);
		this.map.addControl(drawControl);
		
		this.map.on(L.Draw.Event.CREATED, function(e) {
			let type = e.layerType,
				layer = e.layer;
			
			switch (type) {
				case "marker":
					// L.control.markerAddScreen(e).addTo(self.map);
					break;
				case "polygon":
					break;
				case "circle":
					break;
			}
			
			editableLayers.addLayer(layer);
		});
		
		this.map.on(L.Draw.Event.EDITED, function (e) {
			var layers = e.layers;
			layers.eachLayer(function (layer) {
				//do whatever you want; most likely save back to db
			});
		});
		
		this._loadMapAreas(editableLayers, mapAreaItems, mapAreaGroupItems);
		
	},
	getDrawOptions: function (editableLayers) {
		
		return {
			position: 'topleft',
			draw: {
				polygon: {
					allowIntersection: false, // Restricts shapes to simple polygons
					// drawError: {
					// 	color: '#e1e100', // Color the shape will turn when intersects
					// 	message: '<strong>Oh snap!<strong> you can\'t draw that!' // Message that will show when intersect
					// },
					// shapeOptions: {
					// 	color: '#97009c'
					// }
				},
				// disable toolbar item by setting it to false
				polyline: false,
				circle: false, // Turns off this drawing tool
				rectangle: false,
				circlemarker:false,
				// marker: false,
			},
			edit: {
				featureGroup: editableLayers, //REQUIRED!!
				remove: false
			}
		};
	},
	
	getCreateMarkerIconByEventTypeId : function(eventTypeId, color){
		let found = mapItemUtility.getEventTypeItemById(eventTypeId);
		return this.getCreateMarkerIconByEventType(found, color);
	},
	getCreateMarkerIconByEventType : function(eventType, color){
		let iconUrl = this.getSvgFromEventType(eventType, color);
		let icon = L.icon({
			iconUrl: iconUrl,
			iconSize: 30,
		});
		return icon;
	},
	getSvgFromEventTypeId: function (eventTypeId, color) {
		let found = mapItemUtility.getEventTypeItemById(eventTypeId);
		return this.getSvgFromEventType(found, color)
	},
	getSvgFromEventType: function (eventType, color) {
		
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
};