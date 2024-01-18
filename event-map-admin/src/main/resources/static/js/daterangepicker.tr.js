$(function() {

    function cb(start, end) {
        $('#reportrange span').html(start.format('MMMM D') + ' - ' + end.format('MMMM D'));
    }
    cb(moment().subtract(29, 'days'), moment());

    $('input[name="daterange"]').daterangepicker({
    	"alwaysShowCalendars": true,
        ranges: {
           'Bugün': [moment(), moment()],
           'Dün': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
           'Son 7 gün': [moment().subtract(6, 'days'), moment()],
           'Son 30 gün': [moment().subtract(29, 'days'), moment()],
           'Bu ay': [moment().startOf('month'), moment().endOf('month')],
           'Geçen ay': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        },
		"locale": {
        "format": "DD.MM.YYYY",
        "separator": " - ",
        "applyLabel": "Uygula",
        "cancelLabel": "Vazgeç",
        "fromLabel": "Dan",
        "toLabel": "a",
        "customRangeLabel": "Tarih Aralığı",
        "daysOfWeek": [
            "Pt",
            "Sl",
            "Çr",
            "Pr",
            "Cm",
            "Ct",
            "Pz"
        ],
        "monthNames": [
            "Ocak",
            "Şubat",
            "Mart",
            "Nisan",
            "Mayıs",
            "Haziran",
            "Temmuz",
            "Ağustos",
            "Eylül",
            "Ekim",
            "Kasım",
            "Aralık"
        ],
        "firstDay": 1
    }
    }, cb);

});