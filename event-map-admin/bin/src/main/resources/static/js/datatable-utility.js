DataTableUtility = function () {
	//init
	let self = this;
	$(".advanced-search").submit(function (e) {
		e.preventDefault();
		let tableId = $("table.dataTable").attr("id");
		self.tableStandingRedraw("#" + tableId);
	});
	$("body").on("click", "button.list-action-btn", function () {
		
		let btnType = $(this).data("btn-type");
		let self = this;
		if (btnType === "edit") {
			
			if (typeof openEditModal !== "undefined") {
				openEditModal(self);
			} else {
				console.error("editUrl available but openEditModal function not exist.")
			}
			
		} else if (btnType === "delete") {
			
			//TODO:lang
			custombox.confirm(lang.get("label.AreYouSureYouWantToDeleteTheRecord"), function (result) {
				if (result) {
					if (typeof deleteItem !== "undefined") {
						deleteItem(self);
					} else {
						console.error("deleteUrl available but deleteItem function not exist.")
					}
				}
			});
		}
		if(btnType === "zoom"){
			
			if (typeof openEditModalZoom !== "undefined") {
				openEditModalZoom(self);
			} else {
				console.error("zoomUrl available but openEditModal function not exist.")
			}
		}
		
		if(btnType === "layerExport"){
			
			if (typeof openEditModalZoom !== "undefined") {
				openEditModalZoom(self);
			} else {
				console.error("zoomUrl available but openEditModal function not exist.")
			}
		}
	});
};

DataTableUtility.prototype = {
	
	tableClearRedraw : function (tableId) {
		$(tableId).dataTable().fnClearTable();
	},
	
	tableStandingRedraw : function (tableId) {
		$(tableId).dataTable().fnStandingRedraw();
	},
	
	drawCallback: function (settings) {

	},
	
	rowCallback: function (nrow, data, index) {

	},

	stateFormatter : function (data, type, full) {
		let state = data ? "active" : "passive";			
		
		let color = data ? "green" : "red";			
		return '<span style="color: '+color+';">'+lang.get("label." + state)+'</span>';		
	},	
	stateFormatterIsDbUser : function (data, type, full) {
		let state = data ? "no" : "yes";			
		
		let color = data ? "red" : "green";			
		return '<span style="color: '+color+';">'+lang.get("label." + state)+'</span>';		
	},
	isTempFormatter : function (data, type, full) {
		let state = data ? "temp" : "notTemp";			
		
		let color = data ? "gray" : "black";			
		return '<span style="color: '+color+';">'+lang.get("label.layer." + state)+'</span>';		
	},	
	layerUrlFormatter : function (data, type, full) {
		
		let result = "/region/" + data;			
		return result;		
	},	
	
	stateFormatterColor : function (data, type, full) {
				
		let color = data;			
		return '<span style="color: '+color+';">'+ data +'</span>';		
	},
	
	actionFormatter : function (data, type, full) {
		
		let result = "";
		if (typeof editUrl !== "undefined" && editUrl) {
			result = actionButtonTemp.f(data,
				"btn-outline-primary",
				"la-edit",
				lang.get("label.edit"),
				"edit");
								
		}
		
		if (typeof deleteUrl !== "undefined" && deleteUrl) {
			result += actionButtonTemp.f(data,
				"btn-outline-dangery", "la-trash",
				lang.get("label.delete"),
				"delete");
		}
		
		if (typeof zoomUrl !== "undefined" && zoomUrl) {
			result += actionButtonTemp.f(data,
				"btn-outline-primary",
				"la-edit",
				lang.get("label.zoom.create"),
				"zoom");					
		}
		if (typeof layerExportUrl !== "undefined" && layerExportUrl) {
			result += actionButtonTemp.f(data,
				"btn-outline-primary",
				"flaticon-layers",
				lang.get("label.zoom.create"),
				"layerExport");					
		}
			
		return result;
	},
	
	getExtraParams : function (params) {
		
		$(".advanced-search").find("input, textarea, select").each(function(){
			
			if($(this).attr("name") === undefined) return;
			if($(this).is(":checkbox")){
				
				if($(this).is(":checked")){
					params.push({name: $(this).attr("name"), value: $(this).val()});					
				}
				return;
			}
			params[$(this).attr("name")] = $(this).val();
		});
	}
	
};

let dataTableUtility = new DataTableUtility();

