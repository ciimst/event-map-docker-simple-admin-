L.Control.MarkerAddScreen = L.Control.extend({
	options: {
		
		position: "bottomleft",
		reverseGeoUrl: "https://nominatim.openstreetmap.org/reverse?format={0}&lat={1}&lon={2}&zoom={3}",
		geoReturnFormat: "jsonv2",
		zoom: 10,
	},
	
	initialize: function (clickEvent) {
		
		let self = this;

		this.latLng = clickEvent.latlng;
		this.selectedEventGroup = mapItemUtility.getEventGroupItemById(1);
		this.selectedEventType = mapItemUtility.getEventTypeItemById(3);
		
		let iconUrl = mapHelper.getSvgFromEventType(this.selectedEventType, this.selectedEventGroup.color);
		let icon = L.icon({
			iconUrl: iconUrl,
			iconSize: 30,
		});
		
		this._marker = L.marker([this.latLng.lat, this.latLng.lng], {
			icon: icon,
			draggable: true
		}).bindPopup("");
		this._marker.on('dragend', function(event){
			let marker = event.target;
			let position = marker.getLatLng();
			marker.setLatLng(new L.LatLng(position.lat, position.lng),{draggable:'true'});
			self._update();
		});
		
		mapHelper.addMarker(this.selectedEventGroup, this._marker);
	},
	_initLayout: async function () {
		let className = 'leaflet-control-layers',
			container = this._container = L.DomUtil.create('div', className);
		
		// makes this work on IE touch devices by stopping it from firing a mouseout event when the touch is released
		container.setAttribute('aria-haspopup', true);
		L.DomEvent.disableClickPropagation(container);
		L.DomEvent.disableScrollPropagation(container);
		let form = L.DomUtil.create("form", " m-form");
		let card = L.DomUtil.create('div', "marker-add-container card", form);
		let cardBody = L.DomUtil.create('div', "card-body", card);
		
		let closeBtn =  L.DomUtil.create("a", "marker-add-close-btn", cardBody);
		closeBtn.href = 'javascript:void(0);';
		closeBtn.appendChild(L.DomUtil.create("i", "la la-times"));
		let self = this;
		L.DomEvent.on(closeBtn, 'click', function () {
			self._close();
		});
		let cardTitle = L.DomUtil.create("h4", "card-title marker-add-header mb-4", cardBody);
		cardTitle.innerHTML = "Olay Ekleme";//TODO:lang
		
		// child.innerHTML = "TEST TEST TEST TEST";form-group m-form__group row
		
		//-----
		let row0 = L.DomUtil.create("div", "row form-group", cardBody);
		
		
		
		let row6 = L.DomUtil.create("div", "row form-group", cardBody);
		let latitudeInputContainer = L.DomUtil.create("div", "col-sm-6", row6);
		this.latitudeInput = L.DomUtil.create("input", "latitude-input form-control", latitudeInputContainer);
		this.latitudeInput.placeholder = "Enlem";//TODO:lang
		this.latitudeInput.setAttribute("name", "latitude");
		
		let longitudeInputContainer = L.DomUtil.create("div", "col-sm-6", row6);
		this.longitudeInput = L.DomUtil.create("input", "longitude-input form-control", longitudeInputContainer);
		this.longitudeInput.placeholder = "Boylam";//TODO:lang
		this.longitudeInput.setAttribute("name", "longitude");
		
		
//		this.latitudeInput = L.DomUtil.create("input", "", row0);
//		this.latitudeInput.type = "text";
//		this.latitudeInput.setAttribute("name", "latitude");
//		this.longitudeInput = L.DomUtil.create("input", "", row0);
//		this.longitudeInput.type = "text";
//		this.longitudeInput.setAttribute("name", "longitude");
		
		/*event types*/
		let eventTypeSelectContainer = L.DomUtil.create("div", "col-sm-6", row0);
		let eventTypeSelect = L.DomUtil.create("select", "form-control m-select2 m_select2_12_4", eventTypeSelectContainer);
		eventTypeSelect.setAttribute("name", "eventTypeId");
		eventTypeSelect.setAttribute("data-placeholder", "Olay Grubu");//TODO:lang
		L.DomUtil.create("option", "", eventTypeSelect);
		
		for (const eventTypeItem of eventTypeItems) {
			let option = L.DomUtil.create("option", "", eventTypeSelect);
			option.value = eventTypeItem.id;
			option.innerHTML = eventTypeItem.name;
		}
		
		eventTypeSelectContainer.appendChild(eventTypeSelect);
		let $eventTypeSelect = $(eventTypeSelect);
		$eventTypeSelect.select2({
			allowClear: true,
			placeholder: "Select an option"//TODO:lang
		});
		
		$eventTypeSelect.on("select2:select", function (e) {

			try {
				self.selectedEventType = mapItemUtility.getEventTypeItemById(parseInt(e.params.data.id));
				self._updateMarker();
			} catch (e) {
				console.error(e);
			}
			
		});
		
		$eventTypeSelect.val(this.selectedEventType.id);
		$eventTypeSelect.trigger('change');
		
		/*event groups*/
		let eventGroupSelectContainer = L.DomUtil.create("div", "col-sm-6", row0);
		let eventGroupSelect = L.DomUtil.create("select", "form-control m-select2 m_select2_12_4", eventGroupSelectContainer);
		eventGroupSelect.setAttribute("name", "eventGroupId");
		eventGroupSelect.setAttribute("data-placeholder", "Olay Grubu");//TODO:lang
		L.DomUtil.create("option", "", eventGroupSelect);
		
		for (const eventGroupItem of eventGroupItems) {
			let option = L.DomUtil.create("option", "", eventGroupSelect);
			option.value = eventGroupItem.id;
			option.innerHTML = eventGroupItem.name;
		}
		eventGroupSelectContainer.appendChild(eventGroupSelect);
		let $eventGroupSelect = $(eventGroupSelect);
		$eventGroupSelect.select2({
			allowClear: true,
			placeholder: "Select an option"//TODO:lang
		});
		
		$eventGroupSelect.on("select2:select", function (e) {

			try {
				let oldSelectedEventGroup = self.selectedEventGroup;
				self.selectedEventGroup = mapItemUtility.getEventGroupItemById(parseInt(e.params.data.id));
				self._updateMarker(oldSelectedEventGroup);
			} catch (e) {
				console.error(e);
			}
			
		});
		$eventGroupSelect.val(this.selectedEventGroup.id);
		$eventGroupSelect.trigger('change');
		
		//-----
		let row1 = L.DomUtil.create("div", "row form-group", cardBody);
		let addressInputContainer = L.DomUtil.create("div", "col-sm-12", row1);
		this.addressInput = L.DomUtil.create("input", "address-input form-control", addressInputContainer);
		this.addressInput.placeholder = "Adres";//TODO:lang
		
		//-----
		let row4 = L.DomUtil.create("div", "row form-group", cardBody);
		let cityInputContainer = L.DomUtil.create("div", "col-sm-6", row4);
		this.cityInput = L.DomUtil.create("input", "city-input form-control", cityInputContainer);
		this.cityInput.placeholder = "Şehir";//TODO:lang
		this.cityInput.setAttribute("name", "city");
		
		/*cities*///
		/*
		let citySelectContainer = L.DomUtil.create("div", "col-sm-6", row4);
		let citySelect = L.DomUtil.create("select", "form-control m-select2 m_select2_12_4", citySelectContainer);
		citySelect.setAttribute("name", "cityCode");
		citySelect.setAttribute("data-placeholder", "Şehir");//TODO:lang
		L.DomUtil.create("option", "", citySelect);
		
		for (const cityItem of cityItems) {
			let option = L.DomUtil.create("option", "", citySelect);
			option.value = cityItem.code;
			option.innerHTML = cityItem.name;
		}
		citySelectContainer.appendChild(citySelect);
		this.$citySelect = $(citySelect);
		this.$citySelect.select2({
			allowClear: true,
			placeholder: "Select an option"//TODO:lang
		});*/
		
		let countryInputContainer = L.DomUtil.create("div", "col-sm-6", row4);
		this.countryInput = L.DomUtil.create("input", "country-input form-control", countryInputContainer);
		this.countryInput.placeholder = "Ülke";//TODO:lang
		this.countryInput.setAttribute("name", "country");
		
		/*country*/
		/*
		let countrySelectContainer = L.DomUtil.create("div", "col-sm-6", row4);
		let countrySelect = L.DomUtil.create("select", "form-control m-select2 m_select2_12_4", countrySelectContainer);
		countrySelect.setAttribute("name", "countryCode");
		countrySelect.setAttribute("data-placeholder", "Ülke");//TODO:lang
		L.DomUtil.create("option", "", countrySelect);
		
		for (const countryItem of countryItems) {
			let option = L.DomUtil.create("option", "", countrySelect);
			option.value = countryItem.code;
			option.innerHTML = countryItem.name;
		}
		countrySelectContainer.appendChild(countrySelect);
		this.$countrySelect = $(countrySelect);
		this.$countrySelect.select2({
			allowClear: true,
			placeholder: "Select an option"
		});*/
		
		//-----
		let row5 = L.DomUtil.create("div", "row form-group", cardBody);
		let titleInputContainer = L.DomUtil.create("div", "col-sm-12", row5);
		let title = L.DomUtil.create("input", "title-input form-control", titleInputContainer);
		title.placeholder = "Başlık";//TODO:lang
		title.setAttribute("name", "title");
		
		//-----
		let row2 = L.DomUtil.create("div", "row form-group", cardBody);
		let spotInputContainer = L.DomUtil.create("div", "col-sm-12", row2);
		let spot = L.DomUtil.create("input", "spot-input form-control", spotInputContainer);
		spot.placeholder = "Spot";//TODO:lang
		spot.setAttribute("name", "spot");
		
		//-----
		let row3 = L.DomUtil.create("div", "row form-group", cardBody);
		let descriptionInputContainer = L.DomUtil.create("div", "col-sm-12", row3);
		let description = L.DomUtil.create("input", "description-input form-control", descriptionInputContainer);
		description.placeholder = lang.get("label.description");
		description.setAttribute("name", "description");
		
		let saveButton = L.DomUtil.create("button", "btn btn-primary btn-sm marker-add-save-btn pull-right mt-2", cardBody);
		saveButton.type = "button";
		saveButton.innerHTML = lang.get("label.save");
		L.DomEvent.on(saveButton, 'click', function (e) {
			
			saveMarker(self._container);
			self.remove();
		});
		container.appendChild(form);
		return true;
	},
	onAdd: function (map) {
		this._map = map;
		this._initLayout();
		this._update();
		return this._container;
	},
	_update : function() {
		let self = this;
		
		let position = this._marker.getLatLng();
		self._map.panTo(new L.LatLng(position.lat, position.lng));
		this.latitudeInput.value = position.lat;
		this.longitudeInput.value = position.lng;
		
		this._getReverseIntoFieldsAsync(this.addressInput, this.cityInput, this.countryInput, this._getQuery(position));
	},
	_updateMarker: function (oldSelectedGroup) {
		
		let icon = mapHelper.getCreateMarkerIconByEventType(this.selectedEventType, this.selectedEventGroup.color);
		this._marker.setIcon(icon);
		mapHelper.updateMarker(oldSelectedGroup, this.selectedEventGroup, this._marker);
	},
	// onRemove: function (map) {
	//
	// },
	_close: function () {
		this._marker.off('dragend');
		mapHelper.removeMarker(this.selectedEventGroup, this._marker);
		this.remove();
	},
	_getQuery: function (latLng) {
		
		return this.options.reverseGeoUrl.f(this.options.geoReturnFormat, latLng.lat, latLng.lng, this.options.zoom);
	},
	
	_getReverseIntoFieldsAsync: async function (input, cityInput, countryInput, query) {
		
		let result = await new Promise(function (resolve, reject) {
			
			try {
				$.get(query, function (result) {

					resolve(result);
				});
			} catch (e) {
				
				reject(e);
			}
		});
		
		let addressStr = "";
		if (result) {
			
			let address = result.address;
			let county = address.county;
			let city = address.province || address.city;
			//let cityCode = mapItemUtility.getCityCodeByName(city);
			//city = city ? " - " + city : "";
			
			county = county || address.state_district;
			county = county ? county : address.city;
			
			addressStr = county + " " + city + " / " + address.country;
			
			cityInput.value = city ? city : (county ? county : "");
//			$citySelect.val(cityCode);
//			$citySelect.trigger('change');
			
			countryInput.value = address.country ? address.country : "";
//			$countrySelect.val(address.country_code);
//			$countrySelect.trigger('change');
		}
		input.value = addressStr;
	}
	
});

L.control.markerAddScreen = function (options) {
	return new L.Control.MarkerAddScreen(options);
};
