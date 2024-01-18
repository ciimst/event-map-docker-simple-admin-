MiniMap = function () {
	this.map = undefined;
	let southWest = L.latLng(-90, -180),
		northEast = L.latLng(90, 180);
	this.bounds = new L.LatLngBounds(southWest, northEast);
	this.defaultTileUrl = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
	this.tiles = {};
	this.originalCoordinates = null;
	
	this.init.apply(this, arguments);
};

MiniMap.prototype = {
	
	init: async function (mapid) {
		
		this.preInit();
		
		let selectedTile = this.tiles["Open Real"] ? this.tiles["Open Real"] : L.tileLayer(this.defaultTileUrl);
		
		this.editableLayers = new L.FeatureGroup();
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
			layers: [selectedTile],//TODO: tiles (selected or something)
			maxBounds: this.bounds,
			maxBoundsViscosity: 1.0
		})
		.setView([38.6, 33.7], 4);
		
		this.trailInit();
		
		this.drawLocale = await this._loadDrawLocales();
		L.drawLocal = this.drawLocale;
		
		this._addButtons();
		this.loadItems();
		this.addExpandBtn();
		this.initUiTriggers();
		this.initFinished();
	},
	
	getMap: function () {
		return this.map;
	},
	
	addExpandBtn : function () {
		let $mapDom = $("#" + this.mapId);
		let self = this;
		let $parent = $mapDom.closest(".map-container-parent");
		let originalPosition = {position: "inherit",
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
					
					$parent.css({position: "absolute",
						height: originalPosition.height,
						width: originalPosition.width});
					$parent.animate({
						right: "0px",
						height: "455px",
						width: "625px"
					}, function () {
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
						self.getMap().invalidateSize()
					});
				}
			}]
		});
		stateChangingButton.addTo(this.map);
	},
	
	async _addButtons() {
		let self = this;
		// FeatureGroup is to store editable layers
		this.map.addLayer(self.editableLayers);
		
		let drawOptions = this.getDrawOptions(self.editableLayers);
		let drawControl = new L.Control.Draw(drawOptions);
		this.map.addControl(drawControl);
		
		this.map.on(L.Draw.Event.CREATED, function(e) {
			let allowFurther = self.onCreated(e);
			if (!allowFurther) {
				return;
			}
			
			let layer = e.layer;
			self.editableLayers.addLayer(layer);
		});
		
		this.map.on(L.Draw.Event.EDITED, function (e) {
			let allowFurther = self.onEdited(e);
			if (!allowFurther) {
				return;
			}
		});
		
		this.map.on(L.Draw.Event.DELETED, function (e) {
			let allowFurther = self.onDeleted(e);
			if (!allowFurther) {
				return;
			}
		});
	},
	
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
	},
	
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
				featureGroup: self.editableLayers, //REQUIRED!!
				remove: false
			}
		};
	},
	
	preInit() {
	
	},
	
	trailInit() {
	
	},
	
	initFinished() {
	
	},
	
	loadItems() {
		
	},
	
	onCreated(e) {
	
		return true;
	},
	onEdited(e) {
		
		return true;
	},
	onDeleted(e) {
		
		return true;
	},
	
	initUiTriggers() {
	
	}
};
