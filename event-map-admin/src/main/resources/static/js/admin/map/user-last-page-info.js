
var dataList = null;

var DatatableDataLocalDemo = function() {
    var e = function() {
    var e =   dataList,     
	 a = $(".m_datatable").mDatatable({
                data: {
                    type: "local",
                    source: e,
                    pageSize: 10
                },
                layout: {
                    theme: "default",
                    class: "",
                    scroll: !1,
                    height: 450,
                    footer: !1
                },
				translate: {
					records: {noRecords: lang.props["label.no.record.found"]},
					toolbar: {
						pagination: {
							items: {

								info: "  {{start}} - {{end}} "+lang.props["label.displaying.between"]+"   ("+lang.props["label.total"]+" {{total}} "+lang.props["label.record"]+")"
							}
						}
					}
				},
                sortable: !0,
                filterable: !1,
                pagination: !0,
                columns: [

				 {
                    field: "username",
                    title: lang.props["label.username"],
                    filterable: !1,
                  //  width: 150,
                }, {
                    field: "url",
                    title: "Url",
                    //width: 150,

                },
				{
                    field: "updateDate",
                    title: lang.props["label.update.date"],
                   // width: 150,

                }
                
]
            }),
            i = a.getDataSourceQuery();
        $("#m_form_search").on("keyup", function(e) {
            a.search($(this).val().toLowerCase())
        })
		
		.val(i.generalSearch), $("#m_form_status").on("change", function() {
	
            a.search($(this).val(), "Status")
        })

		.val(i.generalSearch), $("#userId").on("change", function() {
	
            a.search($(this).val(), "userId")
        })



		.val(void 0 !== i.Status ? i.Status : ""), $("#m_form_type").on("change", function() {
            a.search($(this).val(), "Type")
        })
		
		.val(void 0 !== i.Type ? i.Type : ""), $("#m_form_status, #m_form_type, #userId").selectpicker()
    };
    return {
        init: function() {
            e()
        }
    }
}();
jQuery(document).ready(function() {
	$.get("/admin/map/userLastPage/list/data")
		.fail(function (xhr) {
			xhr.state = false;
			console.error(xhr);
			xhr.description = lang.get("label.unknown.error");
			xhr.redirectUrl = null;
		})
		.always(function (data) {
			
			dataList = data;
			DatatableDataLocalDemo.init()
			
		})

});
