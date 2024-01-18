String.prototype.format = String.prototype.f = function() {
	let s = this, i = arguments.length;
	while (i--) {
		s = s.replace(new RegExp('\\{' + i + '\\}', 'gm'), arguments[i]);
	}
	return s;
};

$.ajaxPrefilter(function(options, originalOptions, jqXHR){
	
	if (options.type.toLowerCase() === "post") {
		
		options.headers = {'X-CSRF-TOKEN': csrf_token};
	}
});

$(document).ajaxSend(function(event, xhr, options) {
	
	if (isBlocked(options)) {
		
		$.blockUI();
	}
	
	options.url = contextPathFix(options.url);
	
}).ajaxComplete(function (event, xhr, options) {
	if (isBlocked(options)) {
		
		$.unblockUI();
	}
})
.ajaxError(function(event, xhr, options, thrownError){
	
	if (xhr.status === 403) {
		redirectToTimeout();
	}
});

let redirectToTimeout = function () {
	window.location.href = contextPath + "/logout?timeout=timeout";
};

let contextPathFix = function (url) {
	url = url ? url : "";
	if (!url.startsWith(contextPath) && !url.startsWith("http")) {
		url = url.startsWith("/") ? url.substring(1, url.length) : url;
		url = contextPath + url;
	}
	return url;
};

function isBlocked(options) {
	//blocklanması istenmeyen pageler
	var isPageUIBlocked = true;
		// options.url.indexOf("que/list/data") < 0;
	// isPageUIBlocked &= options.url.indexOf("systemSettings/all-state") < 0;
	// isPageUIBlocked &= options.url.indexOf("updateSoftware/edit") < 0;
	return isPageUIBlocked;
}

Document.prototype.createElementFromString = Document.prototype.domFromString  = function (elem) {
	let element = document.createElement("div");
	element.innerHTML = elem.trim();
	return element.firstElementChild;
};

Document.prototype.getStringFromDom = Document.prototype.domToString  = function (elem) {
	let element = document.createElement("div");
	element.appendChild(elem);
	return element.innerHTML;
};