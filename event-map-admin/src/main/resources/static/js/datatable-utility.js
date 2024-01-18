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
		else if (btnType === "blackListdelete") {
			
			//TODO:lang
			custombox.confirm("BlackListi silerseniz olayların durum bilgisi aktif olarak değiştirilecektir.", function (result) {
				if (result) {
					if (typeof deleteItem !== "undefined") {
						deleteItem(self);
					} else {
						console.error("deleteUrl available but deleteItem function not exist.")
					}
				}
			});
		}
		
		else if (btnType === "dumpRestore") {
			
			//TODO:lang
			custombox.confirm("UYARI! Bu işlem mevcut veritabanını silecek ve yedekteki ile değiştirecektir. Devam etmek istediğinize emin misiniz?", function (result) {
				if (result) {
					if (typeof deleteItem !== "undefined") {
						restoreItem(self);
					} else {
						console.error("restoreUrl available but restoreItem function not exist.")
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
		
		if(btnType === "fakeLayerAdd"){
			
			if (typeof openfakeLayerAddModal !== "undefined") {
				openfakeLayerAddModal(self);
			} else {
				console.error("fakeLayerAdd available but openfakeLayerAddModal function not exist.")
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
	
	actionStateTypeFormatter : function (data, type, full) {
		
		var color="";
		let actionStateType = data;
		
		if(actionStateType == "finished"){
			
			color="green";
		}
		else if(actionStateType == "running"){
			
			color="red";
		}
		else {
			
			color="yellow";
		}
		
		return "<span style='color:{1}'>{0}</span>".format(lang.get( "label." +data), color);	
	
	},	
	
	stateFormatterStateChange : function (data, type, full) {
		
		$(function() {
			    $('.toggle-one').bootstrapToggle({
					size: "small",
					offstyle: "danger",
					onstyle: "success",
					on: lang.get("label.active"),
			      	off: lang.get("label.passive" )
					
			    });
		  })

		let isChecked = data ? "checked" : "";
		
		var html = '<input data-width="40" data-height="20" data-toggle="toggle" onChange="StateChanged('+full.id+')" class="toggle-one" '+isChecked+' type="checkbox">';
		
		if(full.stateId == 3){//balckList ise
			html = '<div class="">BlackList</div>'
		}
		return html;
		
	},
	
	profileFormatterIsDefault : function (data, type, full) {
		let state = data ? "yes" : "no";			
		
		let color = data ? "green" : "red";			
		return '<span style="color: '+color+';">'+lang.get("label." + state)+'</span>';		
	},
	stateFormatterIsDbUser : function (data, type, full) {
		let state = data ? "yes" : "no";			
		
		let color = data ? "green" : "red";			
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
				"btn-outline-primary mr-2",
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
		
		if (typeof blackListdeleteUrl !== "undefined" && blackListdeleteUrl) {
			result += actionButtonTemp.f(data,
				"btn-outline-dangery", "la-trash",
				lang.get("label.delete"),
				"blackListdelete");
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
		
		if (typeof fakeLayerAddUrl !== "undefined" && fakeLayerAddUrl) {
			result += actionButtonTemp.f(data,
				"btn-outline-primary",
				"la-life-saver",
				lang.get("label.fake.layer.add"),
				"fakeLayerAdd");					
		}
		
		if (typeof restoreUrl !== "undefined" && restoreUrl) {
			result += actionButtonTemp.f(data,
				"btn-outline-primary",
				"la-cloud-download",
				"Veritabanı Geri Yükleme",
				"dumpRestore");					
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
		
	},
	

	
};

let dataTableUtility = new DataTableUtility();

