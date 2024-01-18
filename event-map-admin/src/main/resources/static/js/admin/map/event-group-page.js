let editUrl = contextPath + "admin/map/eventgroup/edit";
let deleteUrl = contextPath + "admin/map/eventgroup/delete";
let g_tableId = "#eventGroupTableId";

$(".color-picker").on("click", function(){
	$("#colorPicker")[0].click();
	
})

$("#layerId").on("change", function (e) {
	$("#search-button").click();
});

$("#parentId").on("change", function (e) {
	$("#search-button").click();
});

function createDateFormatter(data, type, full) {
	return full.createDateStr;
}
function expireDateFormatter(data, type, full) {
	return full.expireDateStr;
}

var options = $('#parentId option');
$("#layerId").on("change", function(){	

	var layerId = $(this).val();	
	var optionsList = $('#parentId option');
	

	if(layerId == ""){
		reloadEventGroup()
	}
	
	let eventGroupFilterUrl = "admin/map/eventgroup/parentFilter/"
	$.post(eventGroupFilterUrl, {layerId: layerId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
		
		let unremoveList = [];
		$.each(response.data, function(key, data){
			
			unremoveList.push(data);
		});
		
		for(var i = 1; i<optionsList.length; i++){												
			optionsList[i].remove();
		}
		
		for(var j = 0; j<unremoveList.length; j++){			
			$('#parentId').append('<option value='+unremoveList[j].id+' >'+unremoveList[j].name+'</option>')			
		}		
	});
}); 

 function reloadEventGroup(){
	
	for(var i = 1; i<options.length; i++){			
		$('#parentId').append('<option value='+options[i].value+' >'+options[i].text+'</option>')			
	}
 }
let $editModal = $('#editModal');
let $saveBtn = $("#editModal #layer-save-btn");
$saveBtn.on("click", function (e) {
	
	let modalData = $('#editModal *').serializeArray().map(m=>m.name==="description" ? {name: "description", value: window.editor.getData()} : m)
	custombox.confirm(lang.get("label.are.you.sure.want.to.save"), function (result) {
		if (result) {
			
			$.post(contextPath + "admin/map/eventgroup/save", modalData)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (data) {
				
				if (data.state) {
					
					$editModal.modal("hide");
					custombox.alertSuccess(data.description);
					
				} else {
					
					custombox.alertError(data.description ? data.description : lang.get("label.unknown.error"));
				}
				
			});
		}
	});
	
});

function openEditModal(elem) {	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	
	$.post(editUrl, {eventGroupId: dataId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
		
		if (!response.state) {
			custombox.alertError(response.description ? response.description : lang.get("label.unknown.error"));
			return;
		}
		
		//relatedTarget
		jQuery.data(elem, "response-data", response.data);
		$editModal.modal("show", $this);
	});
}

function deleteItem(elem) {
	
	let $this = $(elem);
	let dataId = $this.data("id");
	if (!(dataId && Number.isInteger(dataId))) {
		custombox.alertError(lang.get("label.ThisRecordCannotBeDeleted.PleaseTryAgain"));//TODO:lang
		dataTableUtility.tableStandingRedraw(g_tableId);
		return;
	}
	
	$.post(deleteUrl, {eventGroupId: dataId})
	.fail(function (xhr) {
		xhr.state = false;
		console.error(xhr);
		xhr.description = lang.get("label.unknown.error");
		xhr.redirectUrl = null;
	})
	.always(function (response) {
		
		if (!response.state) {
			
			custombox.alertError(response.description ? response.description : lang.get("label.unknown.error"));
		}
		
		dataTableUtility.tableStandingRedraw(g_tableId);
	});
}
var eventGroupParentId = null;
var openModalDefaultLayerId = 0;
$editModal.on('show.bs.modal', function (event) {
	openModalDefaultLayerId = 0;
	window.editor.setData("")
	let button = $(event.relatedTarget);
	let type = button.data("type");
	let modal = $(this);
	modal.clearForm(true);
	
	let title;
	
	if (type === "add") {
		
		title = lang.get("label.eventGroup.add");//TODO:lang
	
		modal.find('span#colorText').text("#000000");	
		modal.find('input#colorPicker').val("#000000");	
		
		
	} else {
		
		let data = jQuery.data(button[0], "response-data");
		if (!data || !(data.id && Number.isInteger(data.id))) {
			custombox.alertError(lang.get("label.thisRecordCannotBeUpdated.PleaseTryAgain"));//TODO:lang
			modal.data("will-hide", true);
			return;
		}
		
		eventGroupParentId = data.parentId;
		
		modal.find('input#id').val(data.id);
		modal.find('input#name').val(data.name);
		modal.find('input#color').val(data.color);		
		modal.find('span#colorText').text(data.color);	
		modal.find('input#colorPicker').val(data.color);
		data.description ? editor.data.set(data.description) : editor.data.set("");
		modal.find('select#modalLayerId').val(data.layerId).trigger('change');
		title = lang.get("label.eventGroup.edit");
		
		openModalDefaultLayerId = data.layerId;
	}
	
	modal.find('.modal-title').text(title);

});

function randomColor(colorList){
	var color = '#'+ ('000000' + Math.floor(Math.random()*16777215).toString(16)).slice(-6);
	
	var found = colorList.find(element => element == color);
	if(found != undefined){
		randomColor(colorList)
	}else{
		return color;
	}
}

$editModal.on("hidden.bs.modal", function () {
	//clears datas
	$("#modalLayerId").val(null).trigger('change');;
	let modal = $(this);
	modal.clearForm(true);//includeHidden = true
	dataTableUtility.tableStandingRedraw(g_tableId);
});

$editModal.on('shown.bs.modal', function (event) {
	//açıldıktan sonra kapatabiliyoruz sadece
	if ($(this).data("will-hide")) {
		$(this).modal("hide");
	}
});


document.querySelectorAll('input[type=color]').forEach(function(picker) {
	

  picker.addEventListener('change', function() {
	
	$("span#colorText").text(picker.value)
  
  });
});


window.addEventListener("load", (e) => {

    CKEDITOR.ClassicEditor.create(document.getElementById("description"), {

        height: '500',
        toolbar: {
            items: [
                // 'exportPDF','exportWord', '|',
                'findAndReplace', 'selectAll', '|',
                'heading', '|',
                'bold', 'italic', 'strikethrough', 'underline', 'code', 'subscript', 'superscript', 'removeFormat', '|',
                'bulletedList', 'numberedList', /*'todoList',*/ '|',
                'outdent', 'indent', '|',
                'undo', 'redo',
                '-',
                'fontSize', 'fontFamily', 'fontColor', 'fontBackgroundColor', 'highlight', '|',
                'alignment', '|',
                // 'link', 
                'insertImage', 'blockQuote', 'insertTable', 'mediaEmbed', 'codeBlock', 'htmlEmbed', '|',
                'specialCharacters', 'horizontalLine',
                //'pageBreak', '|',
                // 'textPartLanguage', '|',
                'sourceEditing'
            ],
            shouldNotGroupWhenFull: true
        },
        // Changing the language of the interface requires loading the language file using the <script> tag.
        // language: 'es',
        list: {
            properties: {
                styles: true,
                startIndex: true,
                reversed: true
            }
        },

        heading: {
            options: [{
                    model: 'paragraph',
                    title: 'Paragraph',
                    class: 'ck-heading_paragraph'
                },
                {
                    model: 'heading1',
                    view: 'h1',
                    title: 'Heading 1',
                    class: 'ck-heading_heading1'
                },
                {
                    model: 'heading2',
                    view: 'h2',
                    title: 'Heading 2',
                    class: 'ck-heading_heading2'
                },
                {
                    model: 'heading3',
                    view: 'h3',
                    title: 'Heading 3',
                    class: 'ck-heading_heading3'
                },
                {
                    model: 'heading4',
                    view: 'h4',
                    title: 'Heading 4',
                    class: 'ck-heading_heading4'
                },
                {
                    model: 'heading5',
                    view: 'h5',
                    title: 'Heading 5',
                    class: 'ck-heading_heading5'
                },
                {
                    model: 'heading6',
                    view: 'h6',
                    title: 'Heading 6',
                    class: 'ck-heading_heading6'
                }
            ]
        },

        placeholder: lang.get("label.description"),

        fontFamily: {
            options: [
                'default',
                'Arial, Helvetica, sans-serif',
                'Courier New, Courier, monospace',
                'Georgia, serif',
                'Lucida Sans Unicode, Lucida Grande, sans-serif',
                'Tahoma, Geneva, sans-serif',
                'Times New Roman, Times, serif',
                'Trebuchet MS, Helvetica, sans-serif',
                'Verdana, Geneva, sans-serif'
            ],
            supportAllValues: true
        },

        fontSize: {
            options: [10, 12, 14, 'default', 18, 20, 22],
            supportAllValues: true
        },

        htmlSupport: {
            allow: [{
                name: /.*/,
                attributes: true,
                classes: true,
                styles: true
            }]
        },

        htmlEmbed: {
            showPreviews: true
        },

        link: {
            decorators: {
                addTargetToExternalLinks: true,
                defaultProtocol: 'https://',
                toggleDownloadable: {
                    mode: 'manual',
                    label: 'Downloadable',
                    attributes: {
                        download: 'file'
                    }
                }
            }
        },

        mention: {
            feeds: [{
                marker: '@',
                feed: [
                    '@apple', '@bears', '@brownie', '@cake', '@cake', '@candy', '@canes', '@chocolate', '@cookie', '@cotton', '@cream',
                    '@cupcake', '@danish', '@donut', '@dragée', '@fruitcake', '@gingerbread', '@gummi', '@ice', '@jelly-o',
                    '@liquorice', '@macaroon', '@marzipan', '@oat', '@pie', '@plum', '@pudding', '@sesame', '@snaps', '@soufflé',
                    '@sugar', '@sweet', '@topping', '@wafer'
                ],
                minimumCharacters: 1
            }]
        },

        removePlugins: [
            // These two are commercial, but you can try them out without registering to a trial.
            // 'ExportPdf',
            // 'ExportWord',
            'CKBox',
            'CKFinder',
            'EasyImage',
            // This sample uses the Base64UploadAdapter to handle image uploads as it requires no configuration.
            // https://ckeditor.com/docs/ckeditor5/latest/features/images/image-upload/base64-upload-adapter.html
            // Storing images as Base64 is usually a very bad idea.
            // Replace it on production website with other solutions:
            // https://ckeditor.com/docs/ckeditor5/latest/features/images/image-upload/image-upload.html
            // 'Base64UploadAdapter',
            'RealTimeCollaborativeComments',
            'RealTimeCollaborativeTrackChanges',
            'RealTimeCollaborativeRevisionHistory',
            'PresenceList',
            'Comments',
            'TrackChanges',
            'TrackChangesData',
            'RevisionHistory',
            'Pagination',
            'WProofreader',
            // Careful, with the Mathtype plugin CKEditor will not load when loading this sample
            // from a local file system (file://) - load this site via HTTP server if you enable MathType
            'MathType'
        ]
    }).then(editor => {
        editor.editing.view.change(writer => {
            writer.setStyle('min-height', '300px', editor.editing.view.document.getRoot());
        });
        window.editor = editor;
        document.querySelector('.document-editor__toolbar').appendChild(editor.ui.view.toolbar.element);
        document.querySelector('.ck-toolbar').classList.add('ck-reset_all');
    }).catch(error => {
        console.error(error);
    });
});

$(".modal-dialog").addClass("modal-lg");


$( document ).ready(function() {
	
	//Katman bazın daha önceden olay gruplarına verilmemiş random bir rengin önerilmesi.
		$("#modalLayerId").on("change", function(){	
			var layerId = $(this).val();	
			
			var dataItemId = $("#id").val();
				
			var options = $('#modalParentId option');
		
			/*var layerId = $(this).val();	*/
			var optionsList = $('#modalParentId option');
			
			if(layerId == ""){
				reloadModalEventGroup()
			}
			
			let eventGroupFilterUrl = "admin/map/eventgroup/parentFilter/"
			$.post(eventGroupFilterUrl, {layerId: layerId, currentEventGroupId: dataItemId})
				.fail(function (xhr) {
					xhr.state = false;
					console.error(xhr);
					xhr.description = lang.get("label.unknown.error");
					xhr.redirectUrl = null;
				})
				.always(function (response) {
					
					let unremoveList = [];
					$.each(response.data, function(key, data){
						
						unremoveList.push(data);
					});
					
					for(var i = 1; i<optionsList.length; i++){												
						optionsList[i].remove();
					}
					
					for(var j = 0; j<unremoveList.length; j++){			
						$('#modalParentId').append('<option value='+unremoveList[j].id+' >'+unremoveList[j].name+'</option>')			
					}		
					
					if(eventGroupParentId != null){
					
						$editModal.find('select#modalParentId').val(eventGroupParentId).trigger('change');
					}
				});
				
				
			if((layerId != "" && dataItemId == "" ) || (dataItemId != "" && layerId != "" && openModalDefaultLayerId != layerId && openModalDefaultLayerId != 0)){
				
				let eventGroupFilterUrl = "admin/map/eventgroup/eventGroupFilter/"
				$.post(eventGroupFilterUrl, {layerId: layerId})
				.fail(function (xhr) {
					xhr.state = false;
					console.error(xhr);
					xhr.description = lang.get("label.unknown.error");
					xhr.redirectUrl = null;
				})
				.always(function (response) {
					
					if(response.state){
						var color = randomColor(response.data);
						$editModal.find('input#colorPicker').val(color);
						$editModal.find('span#colorText').text(color);
					}
						
				});
		     }
		
	});
	
	function reloadModalEventGroup(){

	   for(var i = 1; i<options.length; i++){			
		   $('#modalParentId').append('<option value='+options[i].value+' >'+options[i].text+'</option>')			
	    }
     }
	
});

