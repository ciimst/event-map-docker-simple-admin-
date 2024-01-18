class MiniMap {

	constructor(mapid) {
		this.map = undefined;
		let southWest = L.latLng(-90, -180),
			northEast = L.latLng(90, 180);
		this.bounds = new L.LatLngBounds(southWest, northEast);
		this.defaultTileUrl = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
		this.defaultTileName = "";
		this.tiles = {};
		this.coordinates = null;
		this.init(mapid);
	}
	
	async init(mapid) {
		
		this.preInit();
		this.loadTiles();
		
		let selectedTile = this.tiles[this.defaultTileName] ? this.tiles[this.defaultTileName] : L.tileLayer(this.defaultTileUrl);
		
		this._editableLayers = new L.FeatureGroup();
		this.mapId = mapid;
		this.map = L.map(mapid, {
			center: this.bounds.getCenter(),
			zoom: 5,
			minZoom: 2,
//			maxZoom: 12,
			noWrap: true,
			// worldCopyJump: true,
			// continousWorld : true,
			continuousWorld: true,
			layers: [selectedTile],
			maxBounds: this.bounds,
			maxBoundsViscosity: 1.0
		})
		.setView([38.6, 33.7], 4);
		
		// L.tileLayer(mapboxUrl, {id: 'MapID', tileSize: 512, zoomOffset: -1, attribution: mapboxAttribution});
		// L.control.customLayers(this.tiles, null).addTo(this.map);
		if (Object.keys(this.tiles).length > 0) {
			L.control.layers(this.tiles, null).addTo(this.map);
		}
		
		this.trailInit();
		
		this.drawLocale = await this._loadDrawLocales();
		L.drawLocal = this.drawLocale;
		
		this._addButtons();
		this.loadItems();
		this.addExpandBtn();
		this.initUiTriggers();
		this.initFinished();
	}
	
	getMap() {
		return this.map;
	}
	
	setView(center, zoom, options) {
		this.map.setView(center, zoom, options);
	}
	
	addExpandBtn() {
		let $mapDom = $("#" + this.mapId);
		let self = this;
		let $parent = $mapDom.closest(".map-container-parent");
		let originalPosition = {
			position: "inherit",
			top: "0",
			height: $parent.height() + "px",
			width: $parent.width() + "px"};
		
		let stateChangingButton = L.easyButton({
			position:  'bottomright',
			states: [{
				stateName: 'expand',        // name the state
				icon: 'fa-expand',               // and define its properties
				title: "Genişlet",
				onClick: function(btn, map) {
					// and its callback
					btn.state('compress');    // change state on click!
					
					$parent.css({
						position: "absolute",
						height: originalPosition.height,
						width: originalPosition.width});
					$parent.animate(self.getExpandStyles(), function () {
						self.getMap().invalidateSize()
					});
					
				}
			}, {
				stateName: 'compress',
				icon:      'fa-compress',
				title:    "Daralt",
				onClick: function(btn, map) {
					
					btn.state('expand');
					
					
					$parent.animate(originalPosition, function () {
						$parent.css({position: originalPosition.position});
						self.getMap().invalidateSize()
					});
				}
			}]
		});
		stateChangingButton.addTo(this.map);
	}
	
	async _addButtons() {
		
		let self = this;
		// FeatureGroup is to store editable layers
		this.map.addLayer(self._editableLayers);
		
		let drawOptions = this.getDrawOptions(self._editableLayers);
		let drawControl = new L.Control.Draw(drawOptions);
		this.map.addControl(drawControl);
		
		this.map.on(L.Draw.Event.CREATED, function(e) {
			
			self.onCreated(e);
		});
		
		this.map.on(L.Draw.Event.EDITED, function (e) {
			self.onEdited(e);
			
		});
		
		this.map.on(L.Draw.Event.DELETED, function (e) {
			self.onDeleted(e);
		});
	}
	
	async _loadDrawLocales () {
		
		return await new Promise(function (resolve, reject) {
			
			try {
				$.getJSON(contextPath + "js/i18n/leaflet-draw-" + lang_iso + ".json?v=" + app_version, function(json) {
					resolve(json);
				});
			} catch (e) {
				
				reject(e);
			}
		});
	}
	
	getExpandStyles() {
		return {
			right: "0px",
			height: "455px",
			width: "200%"
		};
	}
	
	getDrawOptions () {
		let self = this;
		return {
			position: 'topleft',
			draw: {
				polygon: false,
				// disable toolbar item by setting it to false
				polyline: false,
				circle: false, // Turns off this drawing tool
				rectangle: false,
				circlemarker:false,
				marker: true,
			},
			edit: {
				featureGroup: self._editableLayers, //REQUIRED!!
				remove: false
			}
		};
	}
	
	addLayer(layer) {
		if (!layer) {
			return;
		}
		this._editableLayers.addLayer(layer);
	}
	
	removeLayer(layer) {
		if (!layer) {
			return;
		}
		this._editableLayers.removeLayer(layer);
	}
	
	preInit() {
	
	}
	
	loadTiles() {
		//tiles from server
		if (typeof tileServers === "undefined" || !tileServers) {
			return;
		}
		for (const tileServer of tileServers) {
			
			this.tiles[tileServer.name] = L.tileLayer(tileServer.url, {
				// attribution: '<a href="https://www.openstreetmap.org/">OpenStreetMap</a>',
			});
			if (tileServer.sortOrder === 1) {//orderı 1. olanı seç
				this.defaultTileName = tileServer.name;
			}
		}
		if (!this.defaultTileName || this.defaultTileName === "") {
			this.defaultTileName = tileServers[0].name;
		}
	}
	
	trailInit() {
	
	}
	
	initFinished() {
	
	}
	
	loadItems() {
	
	}
	
	onCreated(e) {
		
		this.addLayer(e.layer);
	}
	onEdited(e) {
		
	}
	onDeleted(e) {
		
	}
	
	initUiTriggers() {
	
	}
	
}

