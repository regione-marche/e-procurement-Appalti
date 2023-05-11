var nso_dialog_send_confirm = null;
var validation_dialog = null;
var nso_dialog_revocation = null;
$( function() {
	$( "#nso-dialog-revocation" ).hide();

	nso_dialog_revocation = $( "#nso-dialog-revocation" ).dialog({
		closeOnEscape: false,
		autoOpen: false,
		resizable: false,
		draggable: false,
		height: "auto",
		width: 400,
		modal: true,
		open: function(event, ui) {
			$(".ui-dialog-titlebar-close").hide();
			$(this).parent().css("border-color","#C0C0C0");
			var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
			_divtitlebar.css("border","0px");
			_divtitlebar.css("background","#FFFFFF");
			var _dialog_title = $(this).parent().find("span.ui-dialog-title");
			_dialog_title.css("font-size","13px");
			_dialog_title.css("font-weight","bold");
			_dialog_title.css("color","#002856");
			$(this).parent().find("div.ui-dialog-buttonpane").css("background","#FFFFFF");
	    },
	    close: function(event, ui) {
			$(".ui-dialog-titlebar-close").show(); //prevent erroneous setups
	    },
		buttons: {
			Conferma: {
				id:"nso-dialog-revoke",
				text:"Conferma",
				click: function(event) {
					revokeOrder();
					$(event.target).hide();
//					$( this ).dialog( "close" );
				}
			},
			Annulla:{
				id:"nso-dialog-revoke-annulla",
				text:"Annulla",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		}
    });
	
	$( "#nso-dialog-send-confirm" ).hide();
	
	nso_dialog_send_confirm = $( "#nso-dialog-send-confirm" ).dialog({
		closeOnEscape: false,
		autoOpen: false,
		resizable: false,
		draggable: false,
		height: "auto",
		width: 400,
		modal: true,
		open: function(event, ui) {
			$(".ui-dialog-titlebar-close").hide();
			$(this).parent().css("border-color","#C0C0C0");
			var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
			_divtitlebar.css("border","0px");
			_divtitlebar.css("background","#FFFFFF");
			var _dialog_title = $(this).parent().find("span.ui-dialog-title");
			_dialog_title.css("font-size","13px");
			_dialog_title.css("font-weight","bold");
			_dialog_title.css("color","#002856");
			$(this).parent().find("div.ui-dialog-buttonpane").css("background","#FFFFFF");
	    },
	    close: function(event, ui) {
			$(".ui-dialog-titlebar-close").show(); //prevent erroneous setups
	    },
		buttons: {
			Conferma: function(event) {
				doPost();
				$(event.target).hide();
			},
			Annulla:{
				id:"nso-dialog-send-annulla",
				text:"Annulla",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		}
    });
	
	function revokeOrder(){
		$.ajax({
			  type: "POST",
			  url: "pg/NsoIntegration.do",
			  data: {"orderId": $('#NSO_ORDINI_ID').val(),
				  "method":"revokeOrder"},
			  beforeSend: function(x) {
				  $('#nso-dialog-revocation-content').css('text-align','center').html('<img src="img/wait.gif" />');
				  $("#nso-dialog-revoke-annulla").off('click');
			  },
			  success: function(data){
				  $('#nso-dialog-revoke-annulla').text("Chiudi");
				  $('#nso-dialog-revoke-annulla').click(function() {
					  $('form[name^="formScheda"] :input[name=jspPathTo]').val($('form[name^="formScheda"] :input[name=jspPath]').val());
					  bloccaRichiesteServer();
					  $('form[name^="formScheda"]').submit();
					  $( this ).dialog( "close" );
					});
				  var htmlOutput="<b>Errore nell'invio dell'ordine.</b>";
				  if(data.result=="OK"){
					  htmlOutput="<b>Ordine Revocato con Successo</b>";
					  //modify on click
				  } else if(data.entity!=null){
					  htmlOutput+="<br><ul>";
					  for(k in data.entity.details){
						  htmlOutput+="<li>"+data.entity.details[k]+"</li>";
					  }
					  htmlOutput+="</ul>";
				  }
				  
				  $('#nso-dialog-revocation-content').css('text-align','left').html(htmlOutput);
			  },
			  dataType: "json",
			  error: function(e){
				  console.log("Error "+e);
				  $('#nso-dialog-send-confirm-content').css('text-align','left').html("<b>Errore generico.<br>Ricaricare la pagina e riprovare.</b>");
				  $('#nso-dialog-send-annulla').text("Chiudi");
				  $('#nso-dialog-send-annulla').on('click',function() {
				  	$( this ).dialog( "close" );
				  });
	            }
			});
	}
	
	function doPost(){
		$.ajax({
			  type: "POST",
			  url: "pg/NsoIntegration.do",
			  data: {"orderId": $('#NSO_ORDINI_ID').val()},
			  beforeSend: function(x) {
				  $('#nso-dialog-send-confirm-content').css('text-align','center').html('<img src="img/wait.gif" />');
				  $("#nso-dialog-send-annulla").off('click');
			  },
			  success: function(data){
				  $('#nso-dialog-send-annulla').text("Chiudi");
				  $('#nso-dialog-send-annulla').click(function() {
					  $('form[name="formScheda7"] :input[name=jspPathTo]').val($('form[name="formScheda7"] :input[name=jspPath]').val());
					  bloccaRichiesteServer();
					  $('form[name="formScheda7"]').submit();
					  $( this ).dialog( "close" );
					});
				  var htmlOutput="<b>Errore nell'invio dell'ordine.</b>";
				  if(data.result=="OK"){
					  htmlOutput="<b>Ordine Inviato con Successo</b>";
					  //modify on click
				  } else if(data.entity!=null){
					  htmlOutput+="<br><ul>";
					  for(k in data.entity.details){
						  htmlOutput+="<li>"+data.entity.details[k]+"</li>";
					  }
					  htmlOutput+="</ul>";
				  }
				  
				  $('#nso-dialog-send-confirm-content').css('text-align','left').html(htmlOutput);
			  },
			  dataType: "json",
			  error: function(e){
				  console.log("Error");
				  $('#nso-dialog-send-confirm-content').css('text-align','left').html("<b>Errore generico.<br>Ricaricare la pagina e riprovare.</b>");
				  $('#nso-dialog-send-annulla').text("Chiudi");
				  $('#nso-dialog-send-annulla').on('click',function() {
				  	$( this ).dialog( "close" );
				  });
	            }
			});
	}
	
	//add functions for validation
	validation_dialog = $("#nso-dialog-verification").dialog({
		closeOnEscape: false,
		autoOpen: false,
		resizable: false,
		draggable: false,
		height: "auto",
		width:480,
		modal: true,
		open: function(event, ui) {
			$(".ui-dialog-titlebar-close").hide();
			$(this).parent().css("border-color","#C0C0C0");
			var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
			_divtitlebar.css("border","0px");
			_divtitlebar.css("background","#FFFFFF");
			var _dialog_title = $(this).parent().find("span.ui-dialog-title");
			_dialog_title.css("font-size","13px");
			_dialog_title.css("font-weight","bold");
			_dialog_title.css("color","#002856");
			$(this).parent().find("div.ui-dialog-buttonpane").css("background","#FFFFFF");
	    },
	    close: function(event, ui) {
			$(".ui-dialog-titlebar-close").show(); //prevent erroneous setups
	    },
		buttons: {
			Conferma:{
				id:"nso-dialog-verification-conferma",
				text:"Conferma",
				click: function() {
//					$( this ).dialog( "close" );
				}
			},
			Chiudi:{
				id:"nso-dialog-verification-annulla",
				text:"Chiudi",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		}
    });
	
	
  } );

function sendValidationRequest(msg){
	$("#nso-dialog-verification-conferma").hide();
	$.ajax({
		type: "GET",
		url: "pg/NsoIntegration.do",
		data: {"method":"validateAction","orderId": $('#NSO_ORDINI_ID').val()},
		cache: false,
		async: false,
		beforeSend: function(x) {
			$("#nso-dialog-verification-content").css('text-align','center').html('<img src="img/wait.gif" />');
			$("#nso-dialog-verification-annulla").off('click');
		},
		success: function(data){
			console.log(data);
			$("#nso-dialog-verification-annulla").on('click',function() {
				validation_dialog.dialog( "close" );
			});
			var htmlOutput="<b>Errore nella validazione dell'ordine.</b>";
			if(data.result=="OK"){
				htmlOutput="<b>Ordine Validato con Successo</b>";
				if(msg!=null){
					htmlOutput+="<br>Questa funzione permette di aggiornare lo stato dell'ordine a Completato.<br>L'ordine in stato Completato risultera' pronto per l'invio a NSO.";
					$("#nso-dialog-verification-conferma").on('click',function(){
						var html = '<form action="pg/SetNsoStatoOrdine.do?'+csrfToken+'" method="post"><input type="hidden" name="idOrdine" value="'+$('#NSO_ORDINI_ID').val()+'" /><input type="hidden" name="statoOrdine" value="2" /></form>';
						console.log(html);
						$(html).appendTo('body').submit();
						validation_dialog.dialog( "close" );
						bloccaRichiesteServer();
					});
					$("#nso-dialog-verification-conferma").show();
				}
				//modify on click
			} else if(data.entity!=null){
				htmlOutput+="<br><ul>";
				for(k in data.entity.details){
					htmlOutput+="<li>"+data.entity.details[k]+"</li>";
				}
				htmlOutput+="</ul>";
			}
			$('#nso-dialog-verification-content').css('text-align','left').html(htmlOutput);
		},
		dataType: "json",
		error: function(e){
			console.log(e);
			$("#nso-dialog-verification").css('text-align','left').html("<b>Errore generico.<br>Ricaricare la pagina e riprovare.</b>");
			$("#nso-dialog-verification-annulla").on('click',function() {
				validation_dialog.dialog( "close" );
			});
		}
	});
}

function inviaOrdine(){
	nso_dialog_send_confirm.dialog( "open" );
}

function validaOrdine(msg){
	//react to functions
	validation_dialog.dialog("open");
	sendValidationRequest(msg);
}

function confermaOrdineCompletato() {
	var idOrdine = $('#NSO_ORDINI_ID').val();
	var statoOrdine = "2";
	var comando = "href=gare/nso_ordini/popup-aggiorna-stato-ordine.jsp&idOrdine=" + idOrdine + "&statoOrdine=" +statoOrdine;
	openPopUpCustom(comando, "aggiornaStatoOrdine", 550, 300, "yes", "yes");
}

function revocaOrdineNso(){
	nso_dialog_revocation.dialog( "open" );
}