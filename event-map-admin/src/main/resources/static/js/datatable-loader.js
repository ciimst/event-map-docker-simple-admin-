var namespace = "dt";
var ns_separator= "dt:";
var DESC = "desc";
var table_prefix = "oTable_";


var dataTablesOnScreen = {};

var dataTableNameMap = {
	"serverSide": "serverSide",
	"createdRow": "createdRow",
	"rowCallback": "rowCallback",
	"drawCallback": "drawCallback",
	"pageLength": "pageLength",
	"autoWidth": "autoWidth",
	"lengthChange": "lengthChange",
	"processing": "processing",
	"initComplete": "initComplete",
	"serverParams": "serverParams",
	"reloadSelector": "reloadSelector",
	"clearSelector" : "clearSelector",
	"sortInitDirection": "sortInitDirection",
	"renderFunction": "render",
	"property": "data",
	"sortable": "orderable",
	"ajaxInitial": "ajaxInitial",
	"ajaxData": "ajaxData",
	"ajaxType": "ajaxType",
	"deferLoading": "deferLoading"
};

var datatableDefaultParams = {
	"serverSide": true,
	"pageLength": (typeof pageLength === "undefined") ? 15 : pageLength,
	"createdRow": "",
	"searching": false,
	"autoWidth": false,
	"processing": true,
	"lengthChange": false,
	"language": {
		"url": "\/js\/i18n/datatables-" + lang_iso + ".json"
	},
	"pagingType": "full_numbers",
	"dom": '<"top">rt<"bottom"pi><"clear">'
};

$(document).ready(function() {
	loadDatatable();
});

function loadDatatable() {
	
	var $tables = $("table.dataTable");	
	$tables.each(function (i, table) {
		var datatableParams = parseTableAttributes(table);
		dataTablesOnScreen[table.id] = $(table).DataTable(datatableParams);
	});
	
}
function getParameterCI(object, key) {
	var realKey = Object.keys(object).filter(function(k) {
		return k.toLowerCase() === key.toLowerCase();
	})[0];
	if (realKey === undefined) {
		return key;
	}
	return object[realKey];
}


function parseTableAttributes(table) {
	
	var datatableParamInstance = $.extend({}, datatableDefaultParams);
	
	//An attribute to remove; as of version 1.7, it can be a space-separated list of attributes.
	var attributeRemoval = "";
	
	$.each(table.attributes, function (a, attribute) {
		
		var name = attribute.name;
		var value = attribute.value;
		
		if (name.indexOf(ns_separator) === 0) {
			
			var paramName = name.replace(ns_separator, "");
			
			paramName = getParameterCI(dataTableNameMap, paramName);
			
			var isAdvancedParam = setAdvancedTableParam(table, datatableParamInstance, paramName, value);
			if (!isAdvancedParam) {
				datatableParamInstance[ paramName ] = fixValue(value);
			}
			attributeRemoval += name + " ";
		}
	});
	
	$(table).removeAttr(attributeRemoval);
	
	parseColumnAttributes(table, datatableParamInstance);

	return datatableParamInstance;
}

function parseColumnAttributes(table, datatableInstance) {
	
	var order = [];
	var columns = [];
	$(table).find("th").each(function (columnIndex, th) {
		
		var column = {"defaultContent": "", "orderable": true};
		
		//An attribute to remove; as of version 1.7, it can be a space-separated list of attributes.
		var attributeRemoval = "";
		
		var firstSortable;
		
		$.each(th.attributes, function (a, attribute) {
			
			var name = attribute.name;
			var value = attribute.value;
			
			if (name.indexOf(ns_separator) === 0) {
				
				var paramName = name.replace(ns_separator, "");
				paramName = getParameterCI(dataTableNameMap, paramName);
				
				switch (paramName) {
					case "sortInitDirection":
						order.push([columnIndex, value]);//TODO: multiple sort maybe?
						break;
					case "render":
						column[paramName] = customEval(value);
						break;
					case "orderable":
						column[paramName] = value === "true";
						break;
					default:
						column[paramName] = value;
				}
				
				columns[columnIndex] = column;
				
				attributeRemoval += name + " ";
			}
			
		});
		
		$(th).removeAttr(attributeRemoval);
	});
	
	datatableInstance["columns"] = columns;
	//empty array basarsak default seçimi bozuyor (yani initial order hiç bişey yazılmadığında da olsun diye)
	datatableInstance["order"] = order.length === 0 ? undefined : order;
}




function fixValue(val) {
	val = val.trim();
	if ($.isNumeric(val)) {
		return parseInt(val);
	} else if (isBoolean(val)) {
		return parseBoolean(val);
	} else {
		return val;
	}
}

function isBoolean(stringVal) {
	stringVal = stringVal.trim();
	return stringVal.toLowerCase() === "true" || stringVal.toLowerCase() === "false";
}

function parseBoolean(stringVal){
	return stringVal.toLowerCase() === "true";
}

function setAdvancedTableParam(table, datatableParamInstance, paramName, value) {
	
	var newValue;
	var oldValue;
	
	switch (paramName) {
		
		case "createdRow":
		case "initComplete":
		case "serverParams":
		case "rowCallback":
		case "drawCallback":
			newValue = customEval(value);
			break;
		case "url":
			paramName = "ajax";
			oldValue = datatableParamInstance[paramName];
			oldValue = oldValue === undefined ? {} : oldValue;
			oldValue.url = value;
			oldValue.dataSrc = "data";
			datatableParamInstance[paramName] = oldValue;
			return true;
		// case "ajaxData":
		// 	paramName = "ajax";
		// 	oldValue = datatableParamInstance[paramName];
		// 	oldValue = oldValue === undefined ? {} : oldValue;
		// 	oldValue.data = customEval(value);
		// 	datatableParamInstance[paramName] = oldValue;
		// 	return true;
		// case "ajaxType":
		// 	paramName = "ajax";
		// 	oldValue = datatableParamInstance[paramName];
		// 	oldValue = oldValue === undefined ? {} : oldValue;
		// 	oldValue.type = customEval(value);
		// 	datatableParamInstance[paramName] = oldValue;
		// 	return true;
		case "ajaxInitial":
			paramName = "ajax";
			oldValue = datatableParamInstance[paramName];
			oldValue = oldValue === undefined ? {} : oldValue;
			var initialValue = window[value];
			oldValue.type = initialValue.type;
			oldValue.data = initialValue.data;
			datatableParamInstance[paramName] = oldValue;
			return true;
		case "reloadSelector":
		case "clearSelector":
			$(value).bind('click', function() {
				dataTablesOnScreen[table.id].ajax.reload();
			});
			break;
		default:
			return false;
	}
	
	datatableParamInstance[paramName] = newValue;
	
	return true;
}

// function dt_customInitComplete(settings, json) {
// 	dataTablesOnScreen[table.id].columns.adjust().draw();
// }


function customEval(name) {
	return new Function('"use strict";return ' + name)();
}


/*datatable additions*/

// Prevent alert message from being displayed
//bkz. datatable.error.mode in application-x.properties
$.fn.dataTable.ext.errMode = dt_error_mode;

jQuery.fn.dataTableExt.oApi.fnStandingRedraw = function(oSettings) {
	if(!oSettings.oFeatures.bServerSide){
		var before = oSettings._iDisplayStart;
		
		oSettings.oApi._fnReDraw(oSettings);
		
		// iDisplayStart has been reset to zero - so lets change it back
		oSettings._iDisplayStart = before;
		oSettings.oApi._fnCalculateEnd(oSettings);
	}
	
	// draw the 'current' page
	oSettings.oApi._fnDraw(oSettings);
};
