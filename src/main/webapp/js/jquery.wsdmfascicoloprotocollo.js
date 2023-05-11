/*
 * Variabili globali
 */
var _tableDocumenti = null;
var _tableMittenti = null;
var _tableDestinatari = null;
var _tableAllegati = null;
var _tableDocumentiFascicolo = null;
var isWSDocumentoPopolato = false;
var _tableDatiPersonalizzati = null;
var _strutturaJiride = null;
var _strutturaDaServizio = null;
var _erroreLetturaStruttura = false;
/*
 * Legge i dati di riferimento del fascicolo contenuto nella tabella WSFASCICOLO.
 * I dati di riferimento servono per la successiva interrogazione dei servizi remoti.
 */
function _getWSFascicolo(sistemaRemoto) {
		
	_wait();
	
	var entita = $("#entita").val();
	var key1 = $("#key1").val();
	var key2 = $("#key2").val();
	var key3 = $("#key3").val();
	var key4 = $("#key4").val();
	var idconfi = $("#idconfi").val();

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSFascicolo.do",
		data: {
			entita:entita,
			key1:key1,
			key2:key2,
			key3:key3,
			key4:key4,
			idconfi:idconfi,
			tipo:"chiave"
		},
		success: function(json){
			$.map( json, function( item ) {
				$("#codicefascicolo").val(item[0]);
				$("#annofascicolo").val(item[1]);
				$("#numerofascicolo").val(item[2]);
				//$("#codiceaoonuovo").val(item[3]);
				//Si sbiancano i valori
				$('#codiceaoonuovo').find('option').not('[value=123]').remove();
				$("#codiceaoonuovo").append($("<option/>", {value: item[3], text: "" }));
				$("#codiceaoonuovo option").eq(0).prop('selected', true);
				$("#codiceaoodes").val(item[9]);
				
				$('#codiceufficionuovo').find('option').not('[value=123]').remove();
				$("#codiceufficionuovo").append($("<option/>", {value: item[4], text: "" }));
				$("#codiceufficionuovo option").eq(0).prop('selected', true);
				$("#codiceufficiodes").val(item[10]);
				
				$('#strutturaonuovo').find('option').not('[value=123]').remove();
				$("#strutturaonuovo").append($("<option/>", {value: item[5], text: "" }));
				$("#strutturaonuovo option").eq(0).prop('selected', true);
				if(sistemaRemoto=="ARCHIFLOWFA" || sistemaRemoto=="PRISMA" || sistemaRemoto=="FOLIUM" || sistemaRemoto=="JPROTOCOL" || sistemaRemoto=="INFOR" || sistemaRemoto=="TITULUS" || sistemaRemoto=="DOCER"
					|| sistemaRemoto=="ITALPROT" || sistemaRemoto=="LAPISOPERA"){
					$('#classificafascicolonuovo').find('option').not('[value=123]').remove();
					$("#classificafascicolonuovo").append($("<option/>", {value: item[6], text: "" }));
					$("#classificafascicolonuovo option").eq(0).prop('selected', true);
					if(sistemaRemoto=="FOLIUM" || sistemaRemoto=="JPROTOCOL" || sistemaRemoto=="TITULUS" || sistemaRemoto=="DOCER" || sistemaRemoto=="ITALPROT" ){
						if(sistemaRemoto=="TITULUS"){
							var codice = item[6];
							var descrizione = item[7];
							var voce = item[8];
							$("#classificadescrizione").val(descrizione);
							$("#voce").val(voce);
							if(codice==null)
								codice =" ";
							if(descrizione==null){
								descrizione =" ";
							}else{
								descrizione =" - " + descrizione;
							}
							if(voce==null){
								voce =" ";
							}else{
								voce =" (" + voce + ")";
							}
							$("#classificafascicolodescrizione").text(codice + descrizione + voce);
						}else if(sistemaRemoto!="ITALPROT"){
							$("#classificafascicolodescrizione").text(item[6]);
						}
						$('#classificadocumento').find('option').not('[value=123]').remove();
						$("#classificadocumento").append($("<option/>", {value: item[6], text: "" }));
						$("#classificadocumento option").eq(0).prop('selected', true);
					}else if(sistemaRemoto=="LAPISOPERA"){
						$("#classificadocumento").val(item[6]);
						$("#classificafascicolodescrizione").text(item[6]);
					}
				}else if(sistemaRemoto=="ENGINEERINGDOC"){
					$("#uocompetenza").val(item[4]);
					$("#uocompetenzadescrizione").val(item[10]);
					var testoUoCompetenza="";
					if(item[4]!=null)
						testoUoCompetenza=item[4];
					if(testoUoCompetenza!="" && item[10]!=null)
						testoUoCompetenza+= " - "; 
					if(item[10]!=null)
						testoUoCompetenza+=item[10];
					$("#uocompetenzaTxt").val(testoUoCompetenza);
				}
				
				
			});
		},
		error: function(e){
			var messaggio = "Errore durante la lettura dei parametri di ricerca del fascicolo";
			$('#documentifascicolomessaggio').text(messaggio);
			$('#documentifascicolomessaggio').show(tempo);
		},
		complete: function() {
			_nowait();
		}
	});
}

/*
 * Legge i dati di riferimento del fascicolo contenuto nella tabella WSFASCICOLO.
 * La lettura del fascicolo avviene tramite codice, anno e numero.
 */
function _getWSFascicoloByCodice(codice, anno, numero) {
		
	_wait();
	
	var idconfi = $("#idconfi").val();

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSFascicolo.do",
		data: {
			codice: codice,
			anno: anno,
			numero: numero,
			tipo: "codice"
		},
		success: function(json){
			$.map( json, function( item ) {
				_strutturaJiride = item[5];
			});
		},
		error: function(e){
			var messaggio = "Errore durante la lettura dei parametri di ricerca del fascicolo";
			$('#documentifascicolomessaggio').text(messaggio);
			$('#documentifascicolomessaggio').show(tempo);
		},
		complete: function() {
			_nowait();
		}
	});
}


/*
 * Gestione dei dati di riferimento del fascicolo in relazione
 * alla modalita' di apertura della maschera.
 * Se in visualizzazione e' necessario impostare alcuni 
 * campi in modalita' di sola lettura.
 */	
function _gestioneWSFascicolo() {
	var modoapertura = $("#modoapertura").val();
	var tiposistemaremoto = $("#tiposistemaremoto").val();
	
	if (tiposistemaremoto == "PALEO") {
		$("#annofascicolo").hide();
		$("#annofascicolo").closest('tr').hide();
		$("#numerofascicolo").hide();
		$("#numerofascicolo").closest('tr').hide();
		$("#oggettofascicolo").hide();
		$("#oggettofascicolo").closest('tr').hide();
		$("#classificafascicolodescrizione").hide();
		$("#classificafascicolodescrizione").closest('tr').hide();
		$("#descrizionefascicolo").hide();
		$("#descrizionefascicolo").closest('tr').hide();
	}else if (tiposistemaremoto == "PRISMA"){
		$("#codicefascicolo").hide();
		$("#codicefascicolo").closest('tr').hide();
		$("#oggettofascicolo").hide();
		$("#oggettofascicolo").closest('tr').hide();
	}
	
	if (modoapertura == "VISUALIZZA" || modoapertura == "MODIFICA_LOGIN") {
		$("#annofascicolo").prop("readonly", true);
		$("#annofascicolo").addClass("readonly");
		$("#numerofascicolo").prop("readonly", true);
		$("#numerofascicolo").addClass("readonly");
		$("#codicefascicolo").prop("readonly", true);
		$("#codicefascicolo").addClass("readonly");

	} else {
		$("#annofascicolo").prop("readonly", false);
		$("#annofascicolo").removeClass("readonly");
		$("#numerofascicolo").prop("readonly", false);
		$("#numerofascicolo").removeClass("readonly");
		$("#codicefascicolo").prop("readonly", false);
		$("#codicefascicolo").removeClass("readonly");
	}
}


/*
 * Memorizzazione dei dati di riferimento del fascicolo.
 * L'utente puo' modificare i dati di riferimento del fascicolo.
 */
function _setWSFascicolo() {
	
	var entita = $("#entita").val();
	var key1 = $("#key1").val();
	var key2 = $("#key2").val();
	var key3 = $("#key3").val();
	var key4 = $("#key4").val();
	
	var codice = $("#codicefascicolo").val();
	var anno = $("#annofascicolo").val();
	var numero = $("#numerofascicolo").val();
	var classifica = $("#classificafascicolonuovo").val();
	var codiceAOO = $("#codiceaoonuovo").val();
	var codiceUfficio= $("#codiceufficionuovo").val();
	var struttura= $("#strutturaonuovo").val();
	var isRiservatezzaAttiva= $("#isRiservatezzaAttiva").val();
	var classificadescrizione= $("#classificadescrizione").val();
	var voce= $("#voce").val();
	var desaoo= $("#codiceaoonuovo").text();
	var desuff= $("#codiceufficionuovo").text();
	$.ajax({
		type: "GET",
		async: false,
		url: "pg/SetWSFascicolo.do",
		data: "entita=" + entita + "&key1=" + key1 + "&key2=" + key2 + "&key3=" + key3 + "&key4=" + key4 + "&codice=" + codice + "&anno=" + anno + "&numero=" + numero + "&classifica=" 
		 + classifica + "&codiceAOO=" + codiceAOO + "&codiceUfficio=" + codiceUfficio + "&struttura=" + struttura + "&isRiservatezzaAttiva="+isRiservatezzaAttiva
		 + "&classificadescrizione="+classificadescrizione + "&voce="+voce + "&desaoo=" + desaoo + "&desuff="+desuff
	});
}


/*
 * Legge i dati del fascicolo e la lista degli elementi documentali
 * del fascicolo dal servizio remoto.
 * Questa funzione ricava la lista degli elementi documentali
 * (protocollati e non protocollati) inseriti nel fascicolo.
 */
function _getWSDMFascicolo(popolaDocumentiFascicolo, ritardo, chiamataCompleta) {
	//Si e inserito un ritardo per il richiamo della funzione, perchè in alcuni browser altrimenti
	//la maschera di wait non comparirebbe
	if(ritardo>0){
		setTimeout(function(){
			_wait();
		}, ritardo);
	}else{
		_wait();
	}
	var action = "pg/GetWSDMFascicoloMetadati.do";
	if(chiamataCompleta){action = "pg/GetWSDMFascicolo.do";}
	var username = $("#username").val();
	var password = $("#password").val();
	var ruolo =  $("#ruolo option:selected").val();
	var nome =  $("#nome").val();
	var cognome =  $("#cognome").val();
	var codiceuo =  $("#codiceuo option:selected").val();
	var idutente = $("#idutente").val();
	var idutenteunop = $("#idutenteunop").val();
	var codice = $("#codicefascicolo").val();
	var anno = $("#annofascicolo").val();
	var numero = $("#numerofascicolo").val();
	var entita = $("#entita").val();
	var key1 = $("#key1").val(); 
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	if(servizio==null || servizio == "")
		servizio = "FASCICOLOPROTOCOLLO";
	var classifica = $("#classificafascicolonuovo option:selected").val();
	if ($("#tiposistemaremoto").val() == "PRISMA" && $("#classificafascicolonuovoPrisma").is(':visible')){
		classifica = $("#classificafascicolonuovoPrisma").val();
	}
		
	$("#oggettofascicolo").text("");
	if ($("#tiposistemaremoto").val() != "DOCER" && $("#tiposistemaremoto").val() != "LAPISOPERA")
		$("#classificafascicolodescrizione").text("");
	$("#descrizionefascicolo").text("");
	$('#documentifascicolomessaggio').text("");
	$('#documentifascicolomessaggio').hide(tempo);
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: action,
		//data: "username=" + username + "&password=" + password + "&ruolo=" + ruolo + "&nome=" + nome + "&cognome=" + cognome + "&codiceuo=" + codiceuo + "&idutente=" + idutente + "&idutenteunop=" + idutenteunop + "&codice=" + codice + "&anno=" + anno + "&numero=" + numero+ "&entita=" + entita + "&key1=" + key1 + "&servizio=" + servizio,
		data:{
			username: username,
			password: password,
			ruolo: ruolo,
			nome: nome,
			cognome: cognome,
			codiceuo: codiceuo,
			idutente: idutente,
			idutenteunop: idutenteunop,
			codice: codice,
			anno: anno,
			numero: numero,
			entita: entita, 
			key1: key1, 
			servizio: servizio,
			classifica:classifica,
			idconfi:idconfi
		},
		success: function(json){
			if (json.esito == true) {
				if(!($("#tiposistemaremoto").val() == "LAPISOPERA" && popolaDocumentiFascicolo && chiamataCompleta)){
					if ($("#tiposistemaremoto").val() == "IRIDE" || $("#tiposistemaremoto").val() == "JIRIDE" || $("#tiposistemaremoto").val() == "ENGINEERING" 
						|| $("#tiposistemaremoto").val() == "ENGINEERINGDOC" || $("#tiposistemaremoto").val() == "ARCHIFLOW" || $("#tiposistemaremoto").val() == "PRISMA"
						|| $("#tiposistemaremoto").val() == "LAPISOPERA") {
						$("#codicefascicolo").val(json.codicefascicolo);
					}
					if($("#tiposistemaremoto").val() == "ARCHIFLOW" || $("#tiposistemaremoto").val() == "PRISMA" || $("#tiposistemaremoto").val() == "JIRIDE" || $("#tiposistemaremoto").val() == "LAPISOPERA"){
						if(json.annofascicolo!=null){
							$("#annofascicolo").val(json.annofascicolo);
							$("#spanannofascicolo").text(json.annofascicolo);
						}
						if(json.numerofascicolo!=null){
							$("#numerofascicolo").val(json.numerofascicolo);
							$("#spannumerofascicolo").text(json.numerofascicolo);
						}
					}
					
					$("#oggettofascicolo").text(json.oggettofascicolo);
					if($("#tiposistemaremoto").val() == "ARCHIFLOWFA" || $("#tiposistemaremoto").val() == "PRISMA" || $("#tiposistemaremoto").val() == "LAPISOPERA")
						$("#oggettofascicolonuovo").val(json.oggettofascicolo);
					
					if($("#tiposistemaremoto").val() != "ARCHIFLOWFA" && $("#tiposistemaremoto").val() != "JPROTOCOL" && $("#tiposistemaremoto").val() != "LAPISOPERA"){
						$("#classificafascicolodescrizione").text(json.classificafascicolodescrizione);
						//Nel caso il valore della classifica del fascicolo restituita dal servizio non sia presente fra i valori di classificadocumento, allora va inserito
						//altrimenti non si riesce a procedere con la protocollazione per JIRIDE
						if(json.classificafascicolo !=null && json.classificafascicolo!="" && $("#classificadocumento option[value='" + json.classificafascicolo + "']").length == 0)
						{
							$("#classificadocumento").append($("<option/>", {value: json.classificafascicolo, text: json.classificafascicolo }));
						}
						$("#classificadocumento option[value='" + json.classificafascicolo + "']").attr("selected", true);
						$('#idtitolazione').val(json.classificafascicolo);
						$("#descrizionefascicolo").text(json.descrizionefascicolo);
						if($("#tiposistemaremoto").val() == "ENGINEERINGDOC" && servizio == "DOCUMENTALE"){
							//Se l'oggetto del fascicolo è valorizzato, si visualizza il campo 
							if(json.oggettofascicolo!=null && json.oggettofascicolo!="")
								$("#oggettofascicolo").parent().parent().show();
							//Se la descrizione del fascicolo è valorizzata, si visualizza il campo 
							if(json.descrizionefascicolo!=null && json.descrizionefascicolo!="")
								$("#descrizionefascicolo").parent().parent().show();
						}
					}
					if($("#tiposistemaremoto").val() == "PRISMA"){
						$('#classificafascicolonuovoPrisma').val(json.classificafascicolo);
						$("#classificafascicolodescrizione").text(json.classificafascicolo);
						$('#classificafascicolonuovo').find('option').remove().end().append($("<option/>", {value: json.classificafascicolo, text: json.classificafascicolo })).val(json.classificafascicolo);
					}
					$('#classificafascicolonuovo').val(json.classificafascicolo);
					if($("#tiposistemaremoto").val() == "JIRIDE"){
						if(json.tipofascicolo!=null && json.tipofascicolo!=""){
							$("#tipofascicolo").text(json.tipofascicolo);
						}
						//Per la funzione di fascicolo documentale si ha la necessità di leggere la struttura dal servizio
						if(json.struttura!=null && json.struttura!=""){
							$("#strutturaonuovo").val(json.struttura);
						}
					}
					if($("#tiposistemaremoto").val() == "LAPISOPERA"){
						$('#classificafascicolonuovo').find('option').remove().end().append($("<option/>", {value: json.classificafascicolo, text: json.classificafascicolo })).val(json.classificafascicolo);
						if(json.struttura!=null && json.struttura!=""){
							$('#strutturaonuovo').find('option').remove().end().append($("<option/>", {value: json.struttura, text: json.struttura })).val(json.struttura);
						}
						$("#struttura").text(json.struttura);
						$("#classificadescrizione").val(json.classificafascicolodescrizione);
						$("#classificafascicolodescrizione").text(json.classificafascicolo);
					}
				}
			} else {
				var messaggio = "La richiesta di lettura del fascicolo segnala il seguente errore: " + json.messaggio;
				$('#documentifascicolomessaggio').text(messaggio);
				$('#documentifascicolomessaggio').show(tempo);
			}
			if (popolaDocumentiFascicolo == true) {
				_popolaDocumentiFascicolo(json.iTotalRecords, json.data);
			}
		},
		error: function(e){
			var messaggio = "Errore durante la lettura del fascicolo";
			$('#documentifascicolomessaggio').text(messaggio);
			$('#documentifascicolomessaggio').show(tempo);
		},
		complete: function(e){
			/*
			 * Salvataggio, in base dati, del codice fascicolo.
			 * Il salvataggio e' richiesto in quei casi in cui l'interrogazione
			 * del fascicolo avviene inizialmente per anno e numero (IRIDE e JIRIDE)
			 * ed il codice viene ricavato da ws.
			 * Per le richieste successive (per esempio aggiunta di un elemento
			 * documentale ad un fascicolo esistente) e' richiesto il codice fascicolo.
			 * 
			 * Nel caso PALEO non e' necessario salvare il codice poiche' 
			 * anche le prima interrogazione del fascicolo avviene solo
			 * ed esclusivamente per codice.
			 */
			if ($("#tiposistemaremoto").val() == "IRIDE" || $("#tiposistemaremoto").val() == "JIRIDE" || $("#tiposistemaremoto").val() == "ENGINEERING" || $("#tiposistemaremoto").val() == "ARCHIFLOW") {
				if($("#tiposistemaremoto").val() == "JIRIDE"){
					if (_controlloGestioneStrutturaCompetente() != 1){
						$("#strutturaonuovo").val('');
					}
					
				}
				_setWSFascicolo();
			}
			//Per evitare problemi di sincronizzazione con la chiamata alla funzione _wait, si deve introdurre un ritardo analogo nella chiamata di nowait
			setTimeout(function(){
				_nowait();
			}, 600);
			
		}
	});
}


/*
 * Popola la lista degli elementi documentali del fascicolo
 */
function _popolaDocumentiFascicolo(numeroTotale, documentifascicolo) {
	
	if (_tableDocumentiFascicolo != null) {
		_tableDocumentiFascicolo.destroy(true);
	}
	
	var _table = $('<table/>', {"id": "documentifascicolo", "class": "elementi", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	var _tfoot = $('<tfoot/>');
	_tfoot.append(_tr);
	_table.append(_tfoot);
	$("#documentifascicolocontainer").append(_table);

	_tableDocumentiFascicolo = $('#documentifascicolo').DataTable( {
		"data": documentifascicolo,
		"columnDefs": [
   			{
				"data": "oggetto",
				"visible": true,
				"targets": [ 0 ],
				"sTitle": "Oggetto"
			},
			{
				"data": "numerodocumento",
				"visible": true,
				"sTitle": "Numero documento",
				"sWidth": "100px",
				"sClass": "aligncenter",
				"targets": [ 1 ]
			},
			{
				"data": "annoprotocollo",
				"visible": true,
				"targets": [ 2 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Anno protocollo"
			},
			{
				"data": "numeroprotocollo",
				"visible": true,
				"targets": [ 3 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Numero protocollo"
			},
			{
				"data": "inout",
				"visible": true,
				"targets": [ 4 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Ingresso / uscita"
			},
			{
				"data": "segnaturaprotocollo",
				"visible": false,
				"targets": [ 5 ],
				"sClass": "aligncenter",
				"sTitle": "Segnatura protocollo"
			},
			{
				"data": "codiceUO",
				"visible": false,
				"targets": [ 6 ],
				"sClass": "aligncenter",
				"sTitle": "Codice UO"
			}
			
        ],
        "language": {
			"sEmptyTable":     "Nessun elemento documentale trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ elementi documentali",
			"sInfoEmpty":      "Nessun elemento documentale trovato",
			"sInfoFiltered":   "(su _MAX_ elementi documentali totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ elementi documentali",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca elementi documentali",
			"sZeroRecords":    "Nessun elemento documentale trovato",
			"oPaginate": {
				"sFirst":      "Prima",
				"sPrevious":   "Precedente",
				"sNext":       "Successiva",
				"sLast":       "Ultima"
			}
		},
		initComplete: function () {
			var api = this.api();
 			api.columns().indexes().flatten().each( function ( i ) {
				if (i == 2 || i == 4) {
					var column = api.column( i );
					var select = $('<select><option value=""></option></select>').appendTo( $(column.footer()).empty() ).on( 'change', function () {
						var val = $(this).val();
 						column.search( val ? '^'+val+'$' : '', true, false ).draw();
					});
 					column.data().unique().sort().each( function ( d, j ) {
 						if (d != null) {
 							select.append( '<option value="'+d+'">'+d+'</option>' )
 						}
					});
				}
			});
		},
		"lengthMenu": [[10, 50, 100, 200], ["10", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
        "order": [[ 1, "asc" ]],
        "aoColumns": [
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true }
		   ]
    });
	
	
	$('#documentifascicolo tfoot th').eq(0).html( '<input class="search" size="56" type="text" />' );
	$('#documentifascicolo tfoot th').eq(0).css( "text-align", "left" );
	$('#documentifascicolo tfoot th').eq(0).css( "padding-left", "5px" );
	$('#documentifascicolo tfoot th').eq(1).html( '<input class="search" size="8" type="text" />' );
	$('#documentifascicolo tfoot th').eq(3).html( '<input class="search" size="8" type="text" />' );

	_tableDocumentiFascicolo.columns().eq( 0 ).each( function ( colIdx ) {
		$( 'input', _tableDocumentiFascicolo.column(colIdx).footer()).on( 'keyup change', function () {
			_tableDocumentiFascicolo.column(colIdx).search(this.value).draw();
		} );
	} );
	
	if (numeroTotale == 0) {
		$("#documentifascicolo tfoot").hide();
		$("#documentifascicolo_info").hide();
		$("#documentifascicolo_paginate").hide();
	}
	
	$("body").delegate("#documentifascicolo tbody tr", "click", function() {
	    if ($(this).hasClass("selected")) {
	        $(this).removeClass("selected");
	        $("#tabs").fadeOut(tempo);
	        $("#tabs").easytabs("select", "#tabs-datigenerali");
	    }
	    else {
	    	$("#tabs").fadeIn(tempo);
	    	$("#tabs").easytabs("select", "#tabs-datigenerali");
	    	_tableDocumentiFascicolo.$("tr.selected").removeClass("selected");
	        $(this).addClass("selected");
	        var r = _tableDocumentiFascicolo.row(this).data();
	        var numerodocumento = r.numerodocumento;
	        var annoprotocollo = r.annoprotocollo;
	        var numeroprotocollo = r.numeroprotocollo;
	        var codiceUo=r.codiceUO;
	        
	        if ($("#tiposistemaremoto").val() == "IRIDE" || $("#tiposistemaremoto").val() == "JIRIDE") {
		        if (annoprotocollo != null && annoprotocollo != "" && numeroprotocollo != null && numeroprotocollo != "") {
		        	_getWSDMProtocollo(annoprotocollo,numeroprotocollo);
		        } else {	        
		        	_getWSDMDocumento(r.numerodocumento);
		        }
	        }
	        
	        if ($("#tiposistemaremoto").val() == "PALEO" || $("#tiposistemaremoto").val() == "ARCHIFLOW" || $("#tiposistemaremoto").val() == "PRISMA" || $("#tiposistemaremoto").val() == "ENGINEERINGDOC" || $("#tiposistemaremoto").val() == "LAPISOPERA") {
	        	_getWSDMDocumento(r.numerodocumento);
	        }
	        
	        if ($("#tiposistemaremoto").val() == "ENGINEERING" || $("#tiposistemaremoto").val() == "JPROTOCOL") {
	        	if($("#tiposistemaremoto").val() == "JPROTOCOL"){
					$('#codiceuo option').remove();
					$("#codiceuo").append($("<option/>", {value: codiceUo ,text: codiceUo }));
					$("#codiceuo option").eq(1).prop('selected', true);
	        	}
	        	_getWSDMProtocollo(annoprotocollo,numeroprotocollo);
	        }
	        
    		var w = $(window);
 		    $('html,body').animate({scrollTop: $("#tabs").offset().top - (w.height()/2)}, 1000 );
	    }
   	});
	
}


/*
 * Legge i dati di riferimento dei documenti inviati al sistema remoto di
 * gestione documentale. I dati di riferimento sono contenuti
 * nella tabella WSDOCUMENTO.
 */
function _getWSDocumento() {
	
	_wait();
	
	var entita = $("#entita").val();
	var key1 = $("#key1").val();
	var key2 = $("#key2").val();
	var key3 = $("#key3").val();
	var key4 = $("#key4").val();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDocumento.do",
		data: "entita=" + entita + "&key1=" + key1 + "&key2=" + key2 + "&key3=" + key3 + "&key4=" + key4 +"&letturaComunicazioni=1", 
		success: function(json){
			_popolaDocumenti(json.iTotalRecords, json.data);
		},
		error: function(e){
			var messaggio = "Errore durante la lettura dei documenti";
			$('#documentimessaggio').text(messaggio);
			$('#documentimessaggio').show(tempo);
		},
		complete: function() {
			_nowait();
		}
	});
}


/*
 * La funzione controlla se vi sono occorrenze in WSDOCUMENTO.
 */
function _isWSDocumentoPopolato() {
	
	_wait();
	
	var entita = $("#entita").val();
	var key1 = $("#key1").val();
	var key2 = $("#key2").val();
	var key3 = $("#key3").val();
	var key4 = $("#key4").val();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDocumento.do",
		data: "entita=" + entita + "&key1=" + key1 + "&key2=" + key2 + "&key3=" + key3 + "&key4=" + key4 +"&letturaComunicazioni=0", 
		success: function(json){
			var numOccorrenze= json.iTotaltWsdocumento
			if(numOccorrenze>0)
				isWSDocumentoPopolato=true;
			
		},
		error: function(e){
			var messaggio = "Errore durante la lettura dei documenti";
			$('#documentimessaggio').text(messaggio);
			$('#documentimessaggio').show(tempo);
		},
		complete: function() {
			_nowait();
		}
	});
}


/*
 * Popola la lista degli elementi documentali
 */
function _popolaDocumenti(numeroTotale, documentifascicolo) {
	
	if (_tableDocumenti != null) {
		_tableDocumenti.destroy(true);
	}
	
	var _table = $('<table/>', {"id": "documenti", "class": "elementi", "cellspacing": "0", "width" : "100%"});
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
	var _tfoot = $('<tfoot/>');
	_tfoot.append(_tr);
	_table.append(_tfoot);
	$("#documenticontainer").append(_table);
	
	_tableDocumenti = $('#documenti').DataTable( {
		"data": documentifascicolo,
		"columnDefs": [
			{
				"data": "oggetto",
				"visible": true,
				"targets": [ 0 ],
				"sTitle": "Oggetto"
			},
			{
				"data": "numerodoc",
				"visible": true,
				"sTitle": "Numero documento",
				"sWidth": "100px",
				"sClass": "aligncenter",
				"targets": [ 1 ]
			},
			{
				"data": "annoprot",
				"visible": true,
				"targets": [ 2 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Anno protocollo"
			},
			{
				"data": "numeroprot",
				"visible": true,
				"targets": [ 3 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Numero protocollo"
			},
			{
				"data": "inout",
				"visible": true,
				"targets": [ 4 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Ingresso / uscita",
				"render": function ( data, type, full, meta ) {
					var INOUTDesc = "";
					if (full.inout == 'IN') {
						INOUTDesc = "Ingresso";
					} else {
						INOUTDesc = "Uscita";
					}
					return INOUTDesc;
				}
			}
        ],
        "language": {
			"sEmptyTable":     "Nessun elemento documentale trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ elementi documentali",
			"sInfoEmpty":      "Nessun elemento documentale trovato",
			"sInfoFiltered":   "(su _MAX_ elementi documentali totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ elementi documentali",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca elementi documentali",
			"sZeroRecords":    "Nessun elemento documentale trovato",
			"oPaginate": {
				"sFirst":      "Prima",
				"sPrevious":   "Precedente",
				"sNext":       "Successiva",
				"sLast":       "Ultima"
			}
		},
		initComplete: function () {
			var api = this.api();
 			api.columns().indexes().flatten().each( function ( i ) {
				if (i == 2) {
					var column = api.column( i );
					var select = $('<select><option value=""></option></select>').appendTo( $(column.footer()).empty() ).on( 'change', function () {
						var val = $(this).val();
 						column.search( val ? '^'+val+'$' : '', true, false ).draw();
					});
 					column.data().unique().sort().each( function ( d, j ) {
 						if (d != null) {
 							select.append( '<option value="'+ d +'">'+d+'</option>' )
 						}
					});
				}
				
				if (i == 4) {
					var column = api.column( i );
					var select = $('<select><option value=""></option></select>').appendTo( $(column.footer()).empty() ).on( 'change', function () {
						var val = $(this).val();
 						column.search( val ? '^'+val+'$' : '', true, false ).draw();
					});
 					column.data().unique().sort().each( function ( d, j ) {
 						var d_desc = "";
 						if (d=='IN') {
 							d_desc = "Ingresso";
 						} else {
 							d_desc = "Uscita";
 						}
						select.append( '<option value="'+ d_desc +'">'+d_desc+'</option>' )
					});
				}
				
			});
		},
		"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
        "order": [[ 0, "asc" ]],
        "aoColumns": [
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true }
		   ]
    });
	
	
	$('#documenti tfoot th').eq(0).html( '<input class="search" size="56" type="text" />' );
	$('#documenti tfoot th').eq(0).css( "text-align", "left" );
	$('#documenti tfoot th').eq(0).css( "padding-left", "5px" );
	$('#documenti tfoot th').eq(1).html( '<input class="search" size="8" type="text" />' );
	$('#documenti tfoot th').eq(3).html( '<input class="search" size="8" type="text" />' );

	_tableDocumenti.columns().eq( 0 ).each( function ( colIdx ) {
		$( 'input', _tableDocumenti.column(colIdx).footer()).on( 'keyup change', function () {
			_tableDocumenti.column(colIdx).search(this.value).draw();
		} );
	} );
	
	if (numeroTotale == 0) {
		$("#documenti tfoot").hide();
		$("#documenti_info").hide();
		$("#documenti_paginate").hide();
	}
	
}


/*
 * Legge l'elemento documentale dal sistema remoto in
 * funzione del numero documento.
 */
function _getWSDMDocumento(numerodocumento,callback) {
	
	var callbackFunction = function(json){
		if (json.esito == true) {
			$("#oggettodocumento").text(json.oggetto);
			$("#numerodocumento").text(json.numerodocumento);
			$("#annoprotocollo").text(json.annoprotocollo);
			$("#numeroprotocollo").text(json.numeroprotocollo);
			$("#inout").text(json.inout);
			$("#tipodocumentodescrizione").text(json.tipodocumentodescrizione);
			$("#annofascicolo").text(json.annofascicolo);
			$("#numerofascicolo").text(json.numerofascicolo);
			$("#tipoDatiPersonalizzati").val(json.tipoDatiPersonalizzati);
			
			_popolaTabellaMittenti(json.iTotalRecordsMITTENTI, json.dataMITTENTI);
			_popolaTabellaDestinatari(json.iTotalRecordsDESTINATARI, json.dataDESTINATARI);
			_popolaTabellaAllegati(json.iTotalRecordsALLEGATI, json.dataALLEGATI);
			_popolaTabellaDatiPersonalizzati(json.iTotalRecordsDATIMERSONALIZZATI, json.dataDATIPERSONALIZZATI);

		} else {
			_removeDettaglioDocumento();
			var messaggio = "La richiesta di lettura del documento segnala il seguente errore: " + json.messaggio;
			$('#wsdmdocumentomessaggio').text(messaggio);
			$('#wsdmdocumentomessaggio').show(tempo);
		}
		if (callback && typeof callback === 'function'){
			callback(json.esito);
		}
	};
	
	_wait();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMDocumento.do",
		data : {
			username: $("#username").val(),
			password: $("#password").val(),
			ruolo: $("#ruolo option:selected").val(),
			nome : $("#nome").val(),
			cognome : $("#cognome").val(),
			codiceuo : $("#codiceuo option:selected").val(),
			idutente : $("#idutente").val(),
			idutenteunop : $("#idutenteunop").val(),
			numerodocumento : numerodocumento,
			servizio:$("#servizio").val(),
			idconfi:$("#idconfi").val()
		},
		success: callbackFunction,
		error: function(e){
			_removeDettaglioDocumento();
			var messaggio = "Errore durante la lettura del documento";
			$('#wsdmdocumentomessaggio').text(messaggio);
			$('#wsdmdocumentomessaggio').show(tempo);
		},
		complete: function() {
			_nowait();
	    }
	});
}

/*
 * Legge il protocollo
 */
function _getWSDMProtocollo(annoprotocollo, numeroprotocollo) {
	
	_wait();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMProtocollo.do",
		data : {
			username: $("#username").val(),
			password: $("#password").val(),
			ruolo: $("#ruolo option:selected").val(),
			nome : $("#nome").val(),
			cognome : $("#cognome").val(),
			codiceuo : $("#codiceuo option:selected").val(),
			idutente : $("#idutente").val(),
			idutenteunop : $("#idutenteunop").val(),
			annoprotocollo : annoprotocollo,
			numeroprotocollo: numeroprotocollo,
			servizio:$("#servizio").val(),
			idconfi:$("#idconfi").val()
		},
		success: function(json){
			if (json.esito == true) {
				$("#oggettodocumento").text(json.oggetto);
				$("#numerodocumento").text(json.numerodocumento);
				$("#annoprotocollo").text(json.annoprotocollo);
				$("#numeroprotocollo").text(json.numeroprotocollo);
				$("#inout").text(json.inout);
				_popolaTabellaMittenti(json.iTotalRecordsMITTENTI, json.dataMITTENTI);
				_popolaTabellaDestinatari(json.iTotalRecordsDESTINATARI, json.dataDESTINATARI);
				_popolaTabellaAllegati(json.iTotalRecordsALLEGATI, json.dataALLEGATI);
			} else {
				_removeDettaglioDocumento();
				var messaggio = "La richiesta di lettura del documento segnala il seguente errore: " + json.messaggio;
				$('#wsdmdocumentomessaggio').text(messaggio);
				$('#wsdmdocumentomessaggio').show(tempo);
			}
		},
		error: function(e){
			_removeDettaglioDocumento();
			var messaggio = "Errore durante la lettura del documento";
			$('#wsdmdocumentomessaggio').text(messaggio);
			$('#wsdmdocumentomessaggio').show(tempo);
		},
		complete: function() {
			_nowait();
        }
	});
}


/*
 * Popola la lista dei mittenti dell'elemento documentale
 */
function _popolaTabellaMittenti(numeroTotale, mittenti) {
	
	if (_tableMittenti != null) {
		_tableMittenti.destroy(true);
	}
	
	if (numeroTotale == 0) {
		$("#li-mittenti").hide();
	} else {
		$("#li-mittenti").show();
	}
	
	var _table = $('<table/>', {"id": "mittenti", "class": "elementi", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#mittenticontainer").append(_table);
	
	_tableMittenti = $('#mittenti').DataTable( {
		"data": mittenti,
		"columnDefs": [
			{
				"data": "cognomeointestazione",
				"visible": true,
				"sWidth": "400px",
				"sTitle": "Cognome o intestazione",
				"targets": [ 0 ]
			},
			{
				"data": "codicefiscale",
				"visible": true,
				"sTitle": "Codice fiscale",
				"targets": [ 1 ]
			}
        ],
        "language": {
			"sEmptyTable":     "Nessun mittente trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ mittenti",
			"sInfoEmpty":      "Nessun mittente trovato",
			"sInfoFiltered":   "(su _MAX_ mittenti totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ mittenti",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca mittenti",
			"sZeroRecords":    "Nessun mittente trovato",
			"oPaginate": {
				"sFirst":      "Prima",
				"sPrevious":   "Precedente",
				"sNext":       "Successiva",
				"sLast":       "Ultima"
			}
		},
		"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
        "order": [[ 0, "desc" ]],
        "aoColumns": [
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true }
		   ]
    });

	if (numeroTotale == 0) {
		$("#mittenti tfoot").hide();
		$("#mittenti_info").hide();
		$("#mittenti_paginate").hide();
	}
}


/*
 * Popola la lista dei destinatari dell'elemento documentale
 */
function _popolaTabellaDestinatari(numeroTotale, destinatari) {
	
	if (_tableDestinatari != null) {
		_tableDestinatari.destroy(true);
	}
	
	if (numeroTotale == 0) {
		$("#li-destinatari").hide();
	} else {
		$("#li-destinatari").show();
	}
	
	var _table = $('<table/>', {"id": "destinatari", "class": "elementi", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#destinataricontainer").append(_table);
	
	_tableDestinatari = $('#destinatari').DataTable( {
		"data": destinatari,
		"columnDefs": [
			{
				"data": "cognomeointestazione",
				"visible": true,
				"sWidth": "400px",
				"sTitle": "Cognome o intestazione",
				"targets": [ 0 ]
			},
			{
				"data": "codicefiscale",
				"visible": true,
				"sTitle": "Codice fiscale",
				"targets": [ 1 ]
			}
        ],
        "language": {
			"sEmptyTable":     "Nessun destinatario trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ destinatari",
			"sInfoEmpty":      "Nessun destinatario trovato",
			"sInfoFiltered":   "(su _MAX_ destinatari totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ destinatari",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca destinatari",
			"sZeroRecords":    "Nessun destinatario trovato",
			"oPaginate": {
				"sFirst":      "Prima",
				"sPrevious":   "Precedente",
				"sNext":       "Successiva",
				"sLast":       "Ultima"
			}
		},
		"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
        "order": [[ 0, "desc" ]],
        "aoColumns": [
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true }
		   ]
    });

	if (numeroTotale == 0) {
		$("#destinatari tfoot").hide();
		$("#destinatari_info").hide();
		$("#destinatari_paginate").hide();
	}
		
}


/*
 * Popola la lista degli allegati dell'elemento documentale
 */
function _popolaTabellaAllegati(numeroTotale, allegati) {
	
	if (_tableAllegati != null) {
		_tableAllegati.destroy(true);
	}
	
	if (numeroTotale == 0) {
		$("#li-allegati").hide();
	} else {
		$("#li-allegati").show();
	}
	
	var _table = $('<table/>', {"id": "allegati", "class": "elementi", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#allegaticontainer").append(_table);
	
	_tableAllegati = $('#allegati').DataTable( {
		"data": allegati,
		"columnDefs": [
			{
				"data": "titoloallegato",
				"visible": true,
				"sWidth": "400px",
				"sTitle": "Titolo",
				"targets": [ 0 ]
			},
			{
				"data": "nomeallegato",
				"visible": true,
				"sTitle": "Nome documento",
				"targets": [ 1 ],
				"render": function ( data, type, full, meta ) {
					if($("#tiposistemaremoto").val() == "TITULUS"){
						return full.nomeallegato;
					}else{
						var _span = $("<span/>");
						var _numerodocumento = $("#numerodocumento").text();
						var _annoprotocollo = $("#annoprotocollo").text();
						var _numeroprotocollo = $("#numeroprotocollo").text();
						var href;
						if ($("#tiposistemaremoto").val() == "IRIDE" || $("#tiposistemaremoto").val() == "JIRIDE") {
							if (_annoprotocollo != null && _annoprotocollo != "" && _annoprotocollo != "0" && _numeroprotocollo != null && _numeroprotocollo != "" && _numeroprotocollo != "0") {
								_href = "javascript:_getProtocolloAllegato('" + _annoprotocollo + "','" + _numeroprotocollo + "','" + full.nomeallegato + "','" + full.tipoallegato + "');";	
							} else {
								_href = "javascript:_getDocumentoAllegato('" + _numerodocumento + "','" + full.nomeallegato + "','" + full.tipoallegato + "');";
							}
						} else if ($("#tiposistemaremoto").val() == "JPROTOCOL") {
							_href = "javascript:scaricaAllegato('" +  full.urlallegato + "');";
						} else {
							_href = "javascript:_getDocumentoAllegato('" + _numerodocumento + "','" + full.nomeallegato + "','" + full.tipoallegato + "');";
						}
						var _a = $("<a/>",{href: _href, "text": full.nomeallegato, "class": "link-generico"});
						var _img = $("<img/>",{title: "Scarica l'allegato", alt: "Scarica l'allegato", "class": "img_allegato", "src": "img/documentazione_elenco.png"});
						_a.append(_img);
						_span.append(_a);
						return _span.html();
					}
				}
			},
			{
				"data": "tipoallegato",
				"visible": false,
				"sTitle": "Tipo",
				"targets": [ 2 ]
			},
			{
				"data": "urlallegato",
				"visible": false,
				"sTitle": "Url",
				"targets": [ 3 ]
			}
        ],
        "language": {
			"sEmptyTable":     "Nessun allegato trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ allegati",
			"sInfoEmpty":      "Nessun allegato trovato",
			"sInfoFiltered":   "(su _MAX_ allegati totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ allegati",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca allegati",
			"sZeroRecords":    "Nessun allegato trovato",
			"oPaginate": {
				"sFirst":      "Prima",
				"sPrevious":   "Precedente",
				"sNext":       "Successiva",
				"sLast":       "Ultima"
			}
		},
		"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
        "order": [[ 0, "desc" ]],
        "aoColumns": [
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true }
		   ]
    });

	if (numeroTotale == 0) {
		$("#allegati tfoot").hide();
		$("#allegati_info").hide();
		$("#allegati_paginate").hide();
	}

}


/*
 * Popola la lista dei dati personalizzati dell'elemento documentale
 */
function _popolaTabellaDatiPersonalizzati(numeroTotale, datiPersonalizzati) {
	
	if (_tableDatiPersonalizzati != null) {
		_tableDatiPersonalizzati.destroy(true);
	}
	
	if (numeroTotale == 0) {
		$("#li-datipers").hide();
	} else {
		$("#li-datipers").show();
	}
	
	var _table = $('<table/>', {"id": "datipers", "class": "elementi", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#datiperscontainer").append(_table);

	
	_tableDatiPersonalizzati = $('#datipers').DataTable( {
		"data": datiPersonalizzati,
		"columnDefs": [
			{
				"data": "nomeMetadato",
				"visible": true,
				"sWidth": "400px",
				"sTitle": "Nome attributo",
				"targets": [ 0 ]
			},
			{
				"data": "valoreMetadato",
				"visible": true,
				"sTitle": "Valore attributo",
				"targets": [ 1 ]
			}
        ],
        "language": {
			"sEmptyTable":     "Nessun metadato trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ metadati",
			"sInfoEmpty":      "Nessun metadato trovato",
			"sInfoFiltered":   "(su _MAX_ metadati totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ metadati",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca metadati",
			"sZeroRecords":    "Nessun metadato trovato",
			"oPaginate": {
				"sFirst":      "Prima",
				"sPrevious":   "Precedente",
				"sNext":       "Successiva",
				"sLast":       "Ultima"
			}
		},
		"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
        "order": [[ 0, "asc" ]],
        "aoColumns": [
		     { "bSortable": true, "bSearchable": true },
		     { "bSortable": true, "bSearchable": true }
		   ]
    });

	if (numeroTotale == 0) {
		$("#datipers tfoot").hide();
		$("#datipers_info").hide();
		$("#datipers_paginate").hide();
	}
}



/*
 * Scarica l'allegato selezionato dalla lista degli allegati
 * dell'elemento documentale.
 */
function _getDocumentoAllegato(numerodocumento, nomeallegato, tipoallegato) {

	$("#getdocumentoallegato_username").val($("#username").val());
	$("#getdocumentoallegato_password").val($("#password").val());
	$("#getdocumentoallegato_ruolo").val($("#ruolo option:selected").val());
	$("#getdocumentoallegato_nome").val($("#nome").val());
	$("#getdocumentoallegato_cognome").val($("#cognome").val());
	$("#getdocumentoallegato_codiceuo").val($("#codiceuo option:selected").val());
	$("#getdocumentoallegato_idutente").val($("#idutente").val());
	$("#getdocumentoallegato_idutenteunop").val($("#idutenteunop").val());
	$("#getdocumentoallegato_numerodocumento").val(numerodocumento);
	$("#getdocumentoallegato_nomeallegato").val(nomeallegato);
	$("#getdocumentoallegato_tipoallegato").val(tipoallegato);
	document.formgetdocumentoallegato.submit();
	
}


/*
 * Scarica l'allegato selezionato dalla lista degli allegati
 * del protocollo
 */
function _getProtocolloAllegato(annoprotocollo, numeroprotocollo, nomeallegato, tipoallegato) {
	$("#getprotocolloallegato_username").val($("#username").val());
	$("#getprotocolloallegato_password").val($("#password").val());
	$("#getprotocolloallegato_ruolo").val($("#ruolo option:selected").val());
	$("#getprotocolloallegato_nome").val($("#nome").val());
	$("#getprotocolloallegato_cognome").val($("#cognome").val());
	$("#getprotocolloallegato_codiceuo").val($("#codiceuo option:selected").val());
	$("#getprotocolloallegato_idutente").val($("#idutente").val());
	$("#getprotocolloallegato_idutenteunop").val($("#idutenteunop").val());
	$("#getprotocolloallegato_annoprotocollo").val(annoprotocollo);
	$("#getprotocolloallegato_numeroprotocollo").val(numeroprotocollo);
	$("#getprotocolloallegato_nomeallegato").val(nomeallegato);
	$("#getprotocolloallegato_tipoallegato").val(tipoallegato);
	document.formgetprotocolloallegato.submit();
}


/*
 * Rimuove la scheda a tabs con i dati di dettaglio
 * dell'elemento documentale.
 */	
function _removeTabsDettaglioDocumento() {
	$("#tabs").fadeOut(tempo);
	_removeDettaglioDocumento();
}


/*
 * Rimuove i dati di dettaglio dell'elemento documentale
 */
function _removeDettaglioDocumento() {
	$('#wsdmdocumentomessaggio').text("");
	$('#wsdmdocumentomessaggio').hide();
	$("#oggettodocumento").text("");
	$("#numerodocumento").text("");
	$("#annoprotocollo").text("");
	$("#numeroprotocollo").text("");
	$("#inout").text("");

	if (_tableMittenti != null) {
		_tableMittenti.destroy(true);
	}
	
	if (_tableDestinatari != null) {
		_tableDestinatari.destroy(true);
	}
	
	if (_tableAllegati != null) {
		_tableAllegati.destroy(true);
	}
}

/*
 * Verifica se ci sono fascicoli associati all'entit�
 */
function _controlloFascicoliAssociati() {
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetFascicoloAssociato.do",
		data : {
			entita : $("#entita").val(),
			chiave1 : $("#key1").val(),
			chiave2 : $("#key2").val(),
			chiave3 : $("#key3").val(),
			chiave4 : $("#key4").val()
		}, 
		success: function(data){
			if (data) {
				_fascicoliPresenti=data.fascicoliAssociati;
        	}
		},
		error: function(e) {
			alert("Errore durante il controllo dell'esistenza di fascicoli associati");
		}
	});
}

/*
 * Viene prelevata dal servizio la descrizione della classifica.
 * Funzionalità valida solo per PRISMA.
 */
function _getWSDMDescrizioneTabellatoPrisma() {
	
	
		_wait();

	
	var username = $("#username").val();
	var password = $("#password").val();
	var ruolo =  $("#ruolo option:selected").val();
	var nome =  $("#nome").val();
	var cognome =  $("#cognome").val();
	var codiceuo =  $("#codiceuo option:selected").val();
	var idutente = $("#idutente").val();
	var idutenteunop = $("#idutenteunop").val();
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	if(servizio==null || servizio == "")
		servizio = "FASCICOLOPROTOCOLLO";
	var classifica = $("#classificafascicolonuovo option:selected").val();
		
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMDescrizioneTabellatoPrisma.do",
		data:{
			username: username,
			password: password,
			ruolo: ruolo,
			nome: nome,
			cognome: cognome,
			codiceuo: codiceuo,
			idutente: idutente,
			idutenteunop: idutenteunop,
			servizio: servizio,
			classifica:classifica,
			idconfi:idconfi
		},
		success: function(json){
			if (json.classificafascicolodescrizione != null) {
				$("#classificafascicolodescrizione").text(json.classificafascicolodescrizione);
				
			} 
			
		},
		error: function(e){
			var messaggio = "Errore durante la lettura del fascicolo";
			$('#documentifascicolomessaggio').text(messaggio);
			$('#documentifascicolomessaggio').show(tempo);
			alert("ccc");
		},
		complete: function(e){
			_nowait();
			
		}
	});
	
	
}

function scaricaAllegato(url){
	var w = 700;
	var h = 500;
	var l = Math.floor((screen.width-w)/2);
	var t = Math.floor((screen.height-h)/2);
	window.open(url, "scaricaAllegato", "toolbar=no,menubar=no,width=" + w + ",height=" + h + ",top=" + t + ",left=" + l + ",resizable=yes,scrollbars=yes");
}

/* funzione adoperata da JIRIDE per la lettura della struttura di un fascicolo tramite servizio. Siccome non esiste un servizio dedicato, si deve effettura 
*  la lettura di un fascicolo. Viene effettuata la chiamata del servizio GetWSDMFascicoloMetadati.
*/
function _getWSDMStruttura() {
	_wait();
		
	$('#documentifascicolomessaggio').text("");
	$('#documentifascicolomessaggio').hide();
	
	var username = $("#username").val();
	var password = $("#password").val();
	var ruolo =  $("#ruolo option:selected").val();
	var nome =  $("#nome").val();
	var cognome =  $("#cognome").val();
	var codiceuo =  $("#codiceuo option:selected").val();
	var idutente = $("#idutente").val();
	var idutenteunop = $("#idutenteunop").val();
	var codice = $("#codicefascicolo").val();
	var anno = $("#annofascicolo").val();
	var numero = $("#numerofascicolo").val();
	var entita = $("#entita").val();
	var key1 = $("#key1").val(); 
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	if(servizio==null || servizio == "")
		servizio = "FASCICOLOPROTOCOLLO";
	var classifica = $("#classificafascicolonuovo option:selected").val();
	_strutturaDaServizio = null;	
	_erroreLetturaStruttura = false;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMFascicoloMetadati.do",
		data:{
			username: username,
			password: password,
			ruolo: ruolo,
			nome: nome,
			cognome: cognome,
			codiceuo: codiceuo,
			idutente: idutente,
			idutenteunop: idutenteunop,
			codice: codice,
			anno: anno,
			numero: numero,
			entita: entita, 
			key1: key1, 
			servizio: servizio,
			classifica:classifica,
			idconfi:idconfi
		},
		success: function(json){
			if (json.esito == true) {
				if(json.struttura!=null){
					_strutturaDaServizio = json.struttura;
				}
			} else{
				var messaggio = "La richiesta di lettura del fascicolo segnala il seguente errore: " + json.messaggio;
				$('#documentifascicolomessaggio').text(messaggio);
				$('#documentifascicolomessaggio').show();
				_erroreLetturaStruttura = true;
			}		
		},
		error: function(e){
			//var messaggio = "Errore durante la lettura del fascicolo";
			//$('#documentifascicolomessaggio').text(messaggio);
			//$('#documentifascicolomessaggio').show(tempo);
			 
		},
		complete: function(e){
			_nowait();
		}
	});
}




