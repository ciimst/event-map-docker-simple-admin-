LangLocale = function () {
	this.props = undefined;
};

LangLocale.prototype.load = function (props) {
	
	this.props = props
};

LangLocale.prototype.get = function (key) {
	
	if (typeof this.props === "undefined") {
		return "??{0}??".f(key);
	}
	
	if(this.props[key]) {
		return this.props[key];
	}
	else {
		return "??{0}??".f(key);
	}
};

let lang = new LangLocale();

	

 	