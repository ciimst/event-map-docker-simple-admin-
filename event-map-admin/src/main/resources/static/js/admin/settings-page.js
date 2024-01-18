
$("#settings-save-button").click(function () {
	
	let formData = $('#settingsForm').serializeArray();
	
	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$.post(contextPath + "admin/settings/save", formData)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (data) {

				if (data.state) {

					custombox.alertSuccess(data.description);

				} else {

					custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
					// toastr.error(data.description ? data.description : lang.get("label.unknown.error"), 5000);
				}

			});
		}
	});
});