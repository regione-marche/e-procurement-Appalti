$(window).ready(function (){

		
	pagina = $('#pagina').val();
	
	//Variabile che conterrà l'elenco dei valori di idwsdoc
	vettIdWsdoc="";
	_tableOperatori = null;
	
});

/**
 * Inizializzazione della popup modale
 */

function apriTrasmettiAOperatoriInterni(){
	
	if(pagina=='ARC'){
		//Nella colonna IDWSDOC della tabella della pagina di archiviazione è presente un campo hidden, 
		//avente id del tipo "IDWSDOC_hid_" + IDWSDOC, e contenente il valore IDWSDOC
		var nSelected = 0;
		$( "input[id^='IDWSDOC_hid_']" ).each( function( index ) {
			nSelected= nSelected + 1;
			if(vettIdWsdoc!="")
				vettIdWsdoc+=",";
			vettIdWsdoc+= $(this).val();
		});
		if(nSelected == 0){
			alert("Non vi sono documenti archiviati da trasmettere");
			return;
		}
	}else{
		_getIDWSDOC();
	}

	$('#filtroCognome').val('');
	$('#operatorimessaggio').hide();
	_getTabellatiInDB();
	_popolaTabellato("ruolo","ruolo");
	_popolaTabellato("codiceuo","codiceuo");
	_popolaTabellato("tipiTrasmissione","tipiTrasmissione");
	_getWSLogin();
	_gestioneWSLogin();
		
	if(pagina=='ARC'){
		$('#richiestainserimentoprotocollo').hide();
		$('#formTrasmissione').show();
		$('#messagiTrasmissione').html("Mediante questa funzione e' possibile rendere accessibili sul documentale, a un operatore interno opportunamente specificato, tutti i documenti in stato 'archiviato' elencati nella lista.<br>Per procedere specificare l'operatore<br><br>");
	}else{
		$('#messagiTrasmissione').html("Mediante questa funzione e' possibile rendere accessibile sul documentale, a un operatore interno opportunamente specificato, la comunicazione protocollata.<br>Per procedere specificare l'operatore<br><br>");
	}
	$('#sezioneMessaggi').show();
	
	
	
	
	var opt = {
		open: function(event, ui) { 
			$(this).parent().children().children('.ui-dialog-titlebar-close').hide();
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
		autoOpen: false,
		modal: true,
		width: 700,
		height:730,
		title: 'Trasmetti su documentale a operatori interni',
		buttons: {
		"Conferma": trasmettiAOperatori,
		"Annulla": function() {
				$( this ).dialog( "close" );
				$('#sezioneMessaggi').hide();
				if(pagina=='ARC'){
					$('#richiestainserimentoprotocollo').show();
					$('#formTrasmissione').hide();
				}
				$('#sezioneErrori').hide();
				$('#operatori').empty();
				$('#operatori_filtro').val("");
				$('#operatori_filtro').attr( "title", "" );
				$("label[for='operatori_filtro']").hide();
				
			}
		 }
		};
		
		$("#mascheraParametriWSDM").dialog(opt).dialog("open");
		
		/*
		 * Inizializzazioni della pagina 
		 * 
		 */
		setTimeout(function(){
			_validazioneWSLogin();
			_validazioneFormTrasmissione();
			_popolaTabellaOperatori(0,null);
					
			
		}, 800);
		
		function trasmettiAOperatori(){
			_setWSLogin();
			if($("#formTrasmissione").validate().form()){
				
				var operatore = null;
				var valoriSelezionati = 0;
				//Si deve controllare se se è selezionato un solo operatore
				_tableOperatori.$('input[type="radio"]').each(function(){
			     if(this.checked){
		            operatore = this.value;
		            valoriSelezionati ++;
		            
		         }
			      
				});
				if(valoriSelezionati == 0){
					alert("Selezionare un operatore nella lista");
					return;
				}
			
				var datiOperatore = operatore.split("||");
				if(datiOperatore.length>0){
					var nomeOp = datiOperatore[0];
					var cognomeOp = datiOperatore[1];
					var ruoloOp = datiOperatore[2];
					var codiceuoOp = datiOperatore[3];
				}
				var username = $("#username").val();
				var password = $("#password").val();
				var nome = $("#nome").val();
				var cognome = $("#cognome").val();
				var ruolo =$("#ruolo option:selected").val();
				var codiceuo = $("#codiceuo option:selected").val();
				var tipoTrasmissione =  $("#tipiTrasmissione option:selected").val();
				var ngara = $("#key1").val();
				var idconfi = $("#idconfi").val();
				
				$.ajax({
					type: "POST",
					async: false,
					dataType: "json",
					url: "pg/WSDMTrasmissioneOperatori.do",
					data : {
						username: username,
						password: password,
						nome: nome,
						cognome: cognome,
						ruolo: ruolo,
						codiceuo: codiceuo,
						nomeOp: nomeOp,
						cognomeOp: cognomeOp,
						ruoloOp: ruoloOp,
						codiceuoOp: codiceuoOp,
						tipoTrasmissione: tipoTrasmissione,
						vettIdWsdoc : vettIdWsdoc,
						servizio: $("#servizio").val(),
						ngara: ngara,
						idconfi: idconfi
					},
					success: function(json) {
						if (json) {
							if (json.esito == true) {
								_nowait();
								$("#mascheraParametriWSDM").dialog("close");
								
							} else {
								var messaggio = json.messaggio;
								$('#erroriTrasmissione').text(messaggio);
								$('#sezioneErrori').show(tempo);	
								_nowait();
								
							}
						}
					},
					error: function(e) {
						var messaggio = "Si e' presentato un errore nella chiamata al servizio di Trasmissione";
						$('#erroriTrasmissione').text(messaggio);
						$('#sezioneErrori').show(tempo);	
						_nowait();
						
					}
				});
			
			}
		}
}

function _validazioneFormTrasmissione(){
	$("#formTrasmissione").validate({
		rules: {
			tipiTrasmissione: "required",
			filtroCognome : "required"
		},
		messages: {
			tipiTrasmissione: "Specificare il tipo trasmissione",
			filtroCognome : "Specificare il filtro"
		},
		errorPlacement: function (error, element) {
			error.insertAfter($(element));
			if($(element).attr('id') == 'operatori_filtro' ){
				   error.css("margin-left","25px");
			}  
			error.css("margin-right","5px");
			error.css("float", "right");
			error.css("vertical-align", "top");
		}
	});
}


/**
 * Perchè si possano caricare i tabellati è necessario che siano valorizzati, username, password, nome, cognome,ruolo e codiceuo
 */
function caricamentoTabellatoOperatori(){
	var password = $("#password").val();
	var username = $("#username").val();
	var nome = $("#nome").val();
	var cognome = $("#cognome").val();
	var ruolo = $("#ruolo option:selected").val();
	var codiceuo = $("#codiceuo option:selected").val();
	var filtro = $("#filtroCognome").val();
	if(username!=null && password!=null && codiceuo!=null && nome!=null && cognome!=null && ruolo!=null && 
		username!="" && password!="" && nome!="" && cognome!="" && ruolo!="" && codiceuo!="" && $('#tipiTrasmissione').is(':visible')){
		if(filtro == null || filtro == ""){
			alert("E' necessario impostare un filtro sul cognome operatore");
		}else {
			_getDatiOperatori();
		}
		//_popolaTabellatoListaTipiTrasmissione();
	}else{
		$('#operatori').empty();
		$('#operatori_filtro').val('');
		$('#operatori_filtro').attr('title','');
		//$('#tipiTrasmissione').empty();
		$('#operatorimessaggio').hide();
		//$('#tipitrasmissionemessaggio').hide();
		alert("E' necessario impostare tutti i dati della sezione 'Parametri utente per l'inoltro delle richieste al servizio remoto'");
	}
	
}


/**
 * Viene letto l'elenco degli operatori col filtro impostato
 */
function _getDatiOperatori() {
	
	_wait();
	$('#operatorimessaggio').hide();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMListaOperatori.do",
		data : {
			username: $("#username").val(),
			password: $("#password").val(),
			idconfi: $("#idconfi").val(),
			ruolo: $("#ruolo option:selected").val(),
			nome: $("#nome").val(),
			cognome: $("#cognome").val(),
			codiceuo: $("#codiceuo option:selected").val(),
			codicefiscale: null,
			servizio: $("#servizio").val(),
			filtro: $("#filtroCognome").val(),
			
		},
		success: function(json){
			if (json.esito == true) {
				_popolaTabellaOperatori(json.iTotalRecords, json.data);
			}else {
				var messaggio = json.messaggio;
				$('#operatorimessaggio').text(messaggio);
				$('#operatorimessaggio').show(tempo);	
				
			}
			_nowait();
		},
		error: function(e){
			var messaggio = "Non e' stato possibile caricare i valori degli operatori dal servizio";
			$('#operatorimessaggio').text(messaggio);
			$('#operatorimessaggio').show(tempo);	
			_nowait();
			
		},
		complete: function() {
			_nowait();
        }
	});
}


/*
 * Popola la lista degli operatori
 */
function _popolaTabellaOperatori(numOperatori, operatori) {
	
	if (_tableOperatori != null) {
		_tableOperatori.destroy(true);
	}
	
		
	var _table = $('<table/>', {"id": "operatori", "class": "operatori", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#operatoricontainer").append(_table);
	
	_tableOperatori = $('#operatori').DataTable( {
		"data": operatori,
		paging: false,
		"searching": false,
		"scrollY": "200px",
		//"scrollCollapse": true,
		"columnDefs": [
			{
				"data": null,
				"visible": true,
				"sWidth": "10px",
				"targets": [ 0 ],
				'render': function (data, type, row) {
		            // value: item.nome + "||" + item.cognome + "||"  + item.ruolo + "||" + item.codiceuo
					var nome = row.nome;
					var cognome = row.cognome; 
					var ruolo = row.ruolo; 
					var codiceuo = row.codiceuo; 
					var valore = nome + "||" + cognome + "||"  + ruolo + "||" + codiceuo; 
					return '<input type="radio" name="idRadio" value="' + valore + '">';
		         }
			},
			{
				"data": "cognome",
				"visible": true,
				"sTitle": "Cognome",
				"sWidth": "100px",
				"targets": [ 1 ]
			},
			{
				"data": "nome",
				"visible": true,
				"targets": [ 2 ],
				"sWidth": "100px",
				"sTitle": "Nome"
			},
			{
				"data": "ruolo",
				"visible": true,
				"targets": [ 3 ],
				"sWidth": "100px",
				"sTitle": "Ruolo"
			},
			{
				"data": "codiceuo",
				"visible": true,
				"targets": [ 4 ],
				"sWidth": "60px",
				"sTitle": "Codice uo"
			}
		],
        "language": {
			"sEmptyTable":     "Nessun operatore trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ operatori",
			"sInfoEmpty":      "Nessun elemento trovato",
			"sInfoFiltered":   "(su _MAX_ elementi documentali totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ operatori",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sZeroRecords":    "Nessun elemento trovato"
		},
		complete: function() {
			_nowait();
		},
		"order": [[ 1 , "asc" ]]
    });
	
	    	
	
	
}


/*
 * Legge IDWSDOC da WSALLEGATI
 */
function _getIDWSDOC() {
	
	_wait();
	
	var entita = "W_INVCOM";
	var key1 = $("#W_INVCOM_IDPRG").val();
	var key2 = $("#W_INVCOM_IDCOM").val();
	
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetIDWSDOC.do",
		data: "entita=" + entita + "&key1=" + key1 + "&key2=" + key2, 
		success: function(json){
			if (json) {
				vettIdWsdoc = json.idwsdoc;
			}else{
				var messaggio = json.messaggio;
				$('#erroriTrasmissione').text(messaggio);
				$('#sezioneErrori').show(tempo);
			}
		},
		error: function(e){
			var messaggio = "Errore durante la lettura del numero documento";
			$('#erroriTrasmissione').text(messaggio);
			$('#sezioneErrori').show(tempo);
		},
		complete: function() {
			_nowait();
		}
	});
}


