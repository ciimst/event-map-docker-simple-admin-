$(function() {
	$('#example').DataTable( {
	"pagingType": "full_numbers",
    "language": {
    	"sProcessing":   "İşleniyor...",
    	"sLengthMenu":   "_MENU_",
    	"sZeroRecords":  "Eşleşen Kayıt Bulunmadı",
    	"sInfo":         "  _TOTAL_ Kayıttan _START_ - _END_ Arası Kayıtlar",
    	"sInfoEmpty":    "Kayıt Yok",
    	"sInfoFiltered": "( _MAX_ Kayıt İçerisinden Bulunan)",
    	"sInfoPostFix":  "",
    	"sSearch":       "Bul:",
    	"sUrl":          "",
    	"oPaginate": {
    		"sFirst":    "<<",
    		"sPrevious": "<",
    		"sNext":     ">",
    		"sLast":     ">>",
    	} 
	},
	"dom": '<"top">rt<"bottom"pil><"clear">'
    });    
});