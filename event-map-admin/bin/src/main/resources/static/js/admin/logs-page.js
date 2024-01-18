
function createDateFormatter(data, type, full) {
	return full.createDateStr;
}

function logTypeFormatter(data, type, full) {
	return lang.get("label." + data);
}

function descFormatter(data, type, full) {
	
	//TODO: burada düzenlemeyse aradaki farkı bulup gösterebiliriz sadece.
	let result = "<div class='log-desc' onclick='descPrettify(this);' style='cursor: pointer'>{0}</div>"
	.f(data);
	return result;
}

function descPrettify(elem) {
	
	if (!$(elem).hasClass("prettified")) {
		let jsonObj = JSON.parse($(elem).html());
		let jsonPretty = JSON.stringify(jsonObj, null, '\t');
		let escaped = new Option(jsonPretty).innerHTML;
		$(elem).html("<pre><code> {0} </code></pre>".f(escaped));
		$(elem).addClass("prettified");
	} else {
		
		
		let jsonObj = JSON.parse($(elem).find("pre>code").html());
		let jsonPretty = JSON.stringify(jsonObj);
		$(elem).html(jsonPretty);
		$(elem).removeClass("prettified");
	}
	
}
