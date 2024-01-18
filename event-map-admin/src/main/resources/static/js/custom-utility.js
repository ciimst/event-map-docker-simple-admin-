$.fn.password = function () {

	this.each(function () {
		
		const self = this;
		const $input = $(this);
		
		let $toggler = $input.parent().find("[data-target-toggle]");
		$toggler.css("cursor", "pointer");
		
		let target = $toggler.data("target-toggle");
		let $targetElem = $toggler.find(target);
		let toggleClassPair = $toggler.data("pair-toggle").split(",");
		if (typeof toggleClassPair === "undefined" || toggleClassPair.length < 1) {
			throw new Error("Unexpected pair-toggle classes, please add data-pair-toggle with 2 classes separated by comma.");
		}
		let classOne = toggleClassPair[0];
		let classTwo = toggleClassPair[1];
		
		$toggler.click(function () {
			
			if (self.type === "password") {
				self.type = "text";
			} else {
				self.type = "password";
			}
			
			if ($targetElem.hasClass(classOne)) {
				$targetElem.removeClass(classOne).addClass(classTwo);
			} else {
				$targetElem.removeClass(classTwo).addClass(classOne);
			}
		
		});
	
	});
};

$(() => {
	$('[data-toggle="password"]').password();
});



function generatePassword() {
	return (Math.random().toString(36)+Math.random().toString(36).toUpperCase())
	.split('')
	.sort(function(){return 0.5-Math.random()}).join('').substr(0,8);
}

function groupBy(xs, key) {
	return xs.reduce(function (rv, x) {
		let v = key instanceof Function ? key(x) : x[key];
		if (rv[v]) {
			rv[v].push(x);
		} else {
			rv[v] = [];
			rv[v].push(x);
		}
		return rv;
	}, []);
}