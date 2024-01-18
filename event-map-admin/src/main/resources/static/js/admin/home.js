var Home ={
	
	 Init: function (){
		
		Home.UserSize();
		Home.SessionCount();
		Home.LayerSize();
		Home.EventSize();
		Home.AlarmSize();
		Home.EventAlarmSize();
		Home.AlarmUnReadSize();
		Home.LayerAlertInfo();
		Home.SharedAlarmSize();
		Home.EventType();
	},
	
	InitializeEvents: function () {

	},
	SessionCount: function(){
	
		$.ajax({ type: "POST",   
			     url: "/home/sessionCount",   
			     async: true,
			     success : function(sessionCount)
			     {
	
					$("#sessionCount").text(sessionCount)
	
			     }
			});
	},
	
	UserSize: function(){
	
		$.ajax({ type: "POST",   
			     url: "/home/userCount",   
			     async: true,
			     success : function(userSize)
			     {
	
					$("#userSize").text(userSize)
	
			     }
			});
	},

	LayerSize: function(){
		
		$.ajax({ type: "POST",   
			     url: "/home/layerCount",   
			     async: true,
			     success : function(layerSize)
			     {		    	
					$("#layerSize").text(layerSize)
	
			     }
		});
		
		
	},
	
	EventSize: function(){
		
		$.ajax({ type: "POST",   
			     url: "/home/eventCount",   
			     async: true,
			     success : function(eventSize)
			     {
	
					$("#eventSize").text(eventSize)
	
			     }
		});
	},
	
	EventType: function() {
						
		$.ajax({ type: "POST",   
			     url: "/home/eventTypeInfo",   
			     async: true,
			     success : function(layerEvent)
			     {
					
					$.each( layerEvent, function( eventTypeName, eventCount ){
																	
						if(lang.props["icons."+eventTypeName] != undefined){
							eventTypeName = lang.props["icons."+eventTypeName]
						}
						
						var rowItem = '<div class="m-widget4__item"><div class="m-widget4__info"> <span class="m-widget4__title layerName"> ' + eventTypeName + '</span></div>'+
						'<span class="m-widget4__ext"><span class="m-widget4__number m--font-danger">'+eventCount+'</span></span></div>';
						
						$("#eventTypeList").append(rowItem);
											
					});
					
					
	
			     }
		});
	},
	
	AlarmSize : function(){
		
		$.ajax({ type: "POST",   
			     url: "/home/alarmCount",   
			     async: true,
			     success : function(alarmSize)
			     {
	
					$("#alarmSize").text(alarmSize)
	
			     }
		});
	},
	
	EventAlarmSize : function(){
		
		$.ajax({ type: "POST",   
			     url: "/home/eventAlarmCount",   
			     async: true,
			     success : function(eventAlarmSize)
			     {
	
					$("#eventAlarmSizex").text(eventAlarmSize)
	
			     }
		});
	},
	SharedAlarmSize : function(){
		
		$.ajax({ type: "POST",   
			     url: "/home/sharedAlertCount",   
			     async: true,
			     success : function(sharedAlarmSize)
			     {
	
					$("#sharedAlarmSize").text(sharedAlarmSize)
	
			     }
		});
	},
	AlarmUnReadSize : function(){
		
		$.ajax({ type: "POST",   
			     url: "/home/unReadAlarmCount",   
			     async: true,
			     success : function(readAlarmSize)
			     {
	
					$("#readAlarmSize").text(readAlarmSize)
	
			     }
		});
	},
	LayerAlertInfo : function(){
						
		$.ajax({ type: "POST",   
			     url: "/home/layerAlarmInfo",   
			     async: true,
			     success : function(layerAlert)
			     {					
			    	
					$.each( layerAlert, function( index, data ){				
						var rowItem = '<div class="m-widget4__item"><div class="m-widget4__info"> <span class="m-widget4__title layerName"> '+data.layerName+'</span></div>'+
						'<span class="m-widget4__ext"><span class="m-widget4__number m--font-danger">'+data.alertCount+'</span></span></div>';
						
						$("#layerList").append(rowItem);
											
					});											
			     }
		});
	},

}


$(document).ready(function () {
    
	Home.InitializeEvents();
	Home.Init();
	
});