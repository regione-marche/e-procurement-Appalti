var _esitoLetturaProtocollo = null;
var _fascicoloGaraAssociato = false;
var _codicefascicoloGara = null;
var _annofascicoloGara = null; 
var _numerofascicoloGara = null;
var _fascicoliUguali = null;
var _codiceProfilo = null;
var _codiceAooIniz = null;
var _codiceUfficioIniz = null;
var sso = "false";

$(window).ready(function (){
	
	//$( "#annoprotocolloRicerca" ).datepicker($.datepicker.regional[ "it" ]);
	
	idconfi=$('#idconfi').val();
	sso=$('#sso').val();
	
	_controlloObbligatorietaClassifica();
	_controlloObbligatorietaUfficio();
	inizializzazione();
	_inizializzaProfiloFiltrato();
	
	$('#username').change(function() {
		caricamentoProfili();
    });
	
	$('#password').change(function() {
		caricamentoProfili();
    });
	
	$('#associaFascicoloSi').click(function() {
			$('#sezioneamministrazioneorganizzativa').show();
			$('#rigaCodiceAoo').show();
			$('#rigaCodiceUfficio').show();
    });
	
	$('#associaFascicoloNo').click(function() {
			$('#sezioneamministrazioneorganizzativa').hide();
			$('#rigaCodiceAoo').hide();
			$('#rigaCodiceUfficio').hide();
			//$('#codiceaoonuovo').val('');
			//$('#codiceaoonuovo_filtro').val('');
			//$('#codiceaoo_filtro').val('');
			//$('#codiceaoo_filtro').attr('title','');
			
			//$('#codiceufficionuovo').val('');
			//$('#codiceufficionuovo_filtro').val('');
			//$('#codiceufficio_filtro').val('');
			//$('#codiceufficio_filtro').attr('title','');
    });
	
	

	$('#codiceaoonuovo').change(function() {
		caricamentoUfficioTITULUS();
		
    });
	
	_getWSLogin();
	if(_logincomune=="1"){
		bloccaCampiLoginComune();
	}
		
	caricamentoProfili();
	
	/*
	 * Inizializzazioni della pagina 
	 * 
	 */
	setTimeout(function(){
		_validazioneFormDatiProtocollo();
		
			
	}, 800);
});


function _validazioneFormDatiProtocollo(){
	
	$("#parametririchiestafascicolo").validate({
		rules: {
			username: "required",
			password: "required",
			profilo_filtro_input: "required",
			profilo: "required",
			numeroprotocolloRicerca: "required",
			annoprotocolloRicerca: "required",
			codiceaoo_filtro: "required",
			dataprov: "required",
			numeroprov: "required",
			codiceufficio_filtro: "required"
		},
		messages: {
			username: "Specificare l'utente",
			password: "Specificare la password",
			profilo_filtro_input: "Specificare il profilo",
			profilo: "Specificare il profilo",
			numeroprotocolloRicerca : "Specificare il numero protocollo",
			annoprotocolloRicerca: "Specificare l'anno del protocollo",
			codiceaoo_filtro:"Specificare il codice AOO",
			dataprov: "Specificare la data del provvedimento",
			numeroprov: "Specificare il numero del provvedimento",
			codiceufficio_filtro:"Specificare il codice ufficio"
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

function caricamentoProfili(){
	if(sso!="true" && ($("#username").val() == null || $("#username").val() == "" || $("#password").val() == null || $("#password").val() == "")){
		$('#profilo').empty();
		$('#profilo_filtro_input').val('');
		$('#profilo_filtro_input').attr('title','');
		return;
	}
	
	_wait();
			
	$.ajax({
		type: "POST",
		async: true,
		dataType: "json",
		url: "pg/GetWSDMListaProfili.do",
		data : {
			username: $("#username").val(),
			password: $("#password").val(),
			idconfi: idconfi,
			servizio: "FASCICOLOPROTOCOLLO",
			sso : sso,
			utenteSso : $("#utenteSso").val()
			
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					if (json.data != null) {
						$("#profilo").append($("<option/>", {value: "" ,text: "" }));
						$.map( json.data, function( item ) {
							//var valore = item.numero + "�" + item.codiceaoo + "�" + item.codiceufficio;
							var valore = item.numero + "�";
							if(item.codiceaoo!=null && item.codiceaoo !='')
								valore += item.codiceaoo;
							else
								valore += " ";
							valore += "�";
							if(item.codiceufficio!=null && item.codiceufficio !='')
								valore += item.codiceufficio;
							else
								valore += " ";
							$("#profilo").append($("<option/>", {value: valore, text: item.nome  }));
							
						});
						
						//Se � presente un solo valore nel tabellato allora lo si seleziona
						if($("#profilo option").length == 2 ){
							$("#profilo option").eq(1).prop('selected', true);
							$("#profilo_filtro_input").val($("#profilo option").eq(1).text());
						}
						$('#sezioneErrori').hide();
					}
				} else {
					var messaggio = json.messaggio;
					$('#sezioneErrori').text(messaggio);
					$('#sezioneErrori').show(tempo);	
					$('#profilo').empty();
					$('#profilo_filtro_input').val('');
					$('#profilo_filtro_input').attr('title','');
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori dei profili dal servizio";
			$('#sezioneErrori').text(messaggio);
			$('#sezioneErrori').show(tempo);	
			$('#profilo').empty();
			$('#profilo_filtro_input').val('');
			$('#profilo_filtro_input').attr('title','');
		},
		complete: function() {
			_nowait();
			
        }
	});
}

function confermaStep1(){
	if($("#parametririchiestafascicolo").validate().form()){
		_setWSLogin();
		_getWSFascicolo(function foo(){
			var valoreTemp = $('#profilo option:selected').val(); // struttura profilo�codiceaoo�codiceufficio
			var vetValori = valoreTemp.split('�');
			_codiceProfilo = vetValori[0];
			if(vetValori.length>0){
				_codiceAooIniz = vetValori[1];
				if(vetValori.length>1)
					_codiceUfficioIniz = vetValori[2];
			}
			_getWSDMProtocollo($('#annoprotocolloRicerca').val(),$('#numeroprotocolloRicerca').val(),_codiceProfilo, function(){
				if(_esitoLetturaProtocollo){
					$('#step').val(2);
					inizializzazione();
					inizializzazioneDatiFascicolo();
				}
				_nowait();
			});
			
		});	

	}
}

function inizializzazione(){
	var step = $('#step').val();
	if(step == 1){
		$('#titolo1').show();
		$('#titolo2').hide();
		$('#datiLogin').show();
		$('#fascicolo').hide();
		$('#elementiDocumentali').hide();
		$('#wsdmConfermaStep1').show();
		$('#wsdmAnnullaStep1').show();
		$('#wsdmConfermaStep2').hide();
		$('#wsdmAnnullaStep2').hide();
	}else{
		$('#titolo1').hide();
		$('#titolo2').show();
		$('#datiLogin').hide();
		$('#fascicolo').show();
		$('#elementiDocumentali').show();
		$('#wsdmConfermaStep1').hide();
		$('#wsdmAnnullaStep1').hide();
		$('#wsdmConfermaStep2').show();
		$('#wsdmConfermaStep2').focus();
		$('#wsdmAnnullaStep2').show();
		$('#tabs-datigenerali').hide();
		$('#li-datigenerali').hide();
		$('#tabs-mittenti').hide();
		$('#li-mittenti').hide();
		$('#li-destinatari').hide();
		$('#tabs-destinatari').hide();
		var gruppo = $('#gruppo').val();
		if(gruppo == 15){
			$('#provvedimento').show();
			$('#rigaDataProv').show();
			$('#rigaNumeroProv').show();
		}
	}
	$('#sezioneErrori').hide();
	$('#wsdmdocumentomessaggio').hide();
	$('#notaselezione').hide();
}

/*
 * Legge il protocollo
 */
function _getWSDMProtocollo(annoprotocollo, numeroprotocollo, profilo, callback) {
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
			ruolo: null,
			nome : null,
			cognome : null,
			codiceuo : null,
			idutente : null,
			idutenteunop : null,
			annoprotocollo : annoprotocollo,
			numeroprotocollo: numeroprotocollo,
			servizio:$("#servizio").val(),
			idconfi:$("#idconfi").val(),
			profilo: profilo,
			utenteSso : $("#utenteSso").val(),
			sso : sso,
			leggerefascicolo : true
		},
		success: function(json){
			if (json.esito == true) {
				$("#codicefascicolotesto").text(json.codiceFascicolo);
				$("#codicefascicolo").val(json.codiceFascicolo);
				$("#numerofascicolo").val(json.numeroFascicolo);
				$("#numerofascicolotesto").text(json.numeroFascicolo);
				$("#annofascicolo").val(json.annoFascicolo);
				$("#annofascicolotesto").text(json.annoFascicolo);
				$("#oggettofascicolotesto").text(json.oggettoFascicolo);
				$("#oggettofascicolo").val(json.oggettoFascicolo);
				$("#classificafascicolodescrizione").text(json.classificaDescrizioneFascicolo);
				$("#classificafascicolo").val(json.classificaFascicolo);
				$("#inout").text(json.inout);
				$("#INOUT").val(json.INOUT);
				$("#voce").val(json.voce);
				$("#classifica").val(json.classifica);
				$("#classificadescrizione").val(json.classificadescrizione);
				
				
				var documentifascicolo = { 
						data : [] 
					};
				var jsonData = {};
				jsonData["numerodocumento"] = json.numerodocumento;
				jsonData["oggetto"] = json.oggetto;
				jsonData["annoprotocollo"] = annoprotocollo;
				jsonData["numeroprotocollo"] = numeroprotocollo;
				jsonData["inout"] = json.inout;
				documentifascicolo.data.push(jsonData);
				
				$("#oggettodocumento").text(json.oggetto);
				$("#numerodocumento").text(json.numerodocumento);
				$("#annoprotocollo").text(annoprotocollo);
				$("#numeroprotocollo").text(numeroprotocollo);
				
				var numeroDoc=1;
				if(json.numerodocumento==null || json.numerodocumento == "")
					numeroDoc=0;
				
				_popolaDocumentiFascicolo(numeroDoc,documentifascicolo.data);
				$("#tabs").fadeIn(tempo);
				/*
				if(json.iTotalRecordsMITTENTI >0 )
					$("#tabs").easytabs("select", "#tabs-mittenti");
				else if(json.iTotalRecordsDESTINATARI >0 )
					$("#tabs").easytabs("select", "#tabs-destinatari");
				else
					$("#tabs").easytabs("select", "#tabs-allegati");
				*/
				$("#tabs").easytabs("select", "#tabs-allegati");
				
				_popolaTabellaMittenti(json.iTotalRecordsMITTENTI, json.dataMITTENTI);
				_popolaTabellaDestinatari(json.iTotalRecordsDESTINATARI, json.dataDESTINATARI);
				_popolaTabellaAllegati(json.iTotalRecordsALLEGATI, json.dataALLEGATI);
				_esitoLetturaProtocollo = true;
			} else {
				_removeDettaglioDocumento();
				var messaggio = "La richiesta di lettura del documento segnala il seguente errore: " + json.messaggio;
				$('#sezioneErrori').text(messaggio);
				$('#sezioneErrori').show(tempo);
				_esitoLetturaProtocollo = false;
			}
		},
		error: function(e){
			_removeDettaglioDocumento();
			var messaggio = "Errore durante la lettura del documento";
			$('#sezioneErrori').text(messaggio);
			$('#sezioneErrori').show(tempo);
			_esitoLetturaProtocollo = false;
		},
		complete: function() {
			callback();
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
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	
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
		"lengthMenu": [[10, 50, 100, 200], ["10", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
        "order": [[ 1, "asc" ]]
    });
	
			
	
	_tableDocumentiFascicolo.$("tr.selected").removeClass("selected");
    $(this).addClass("selected");
    	        
	var w = $(window);
	    $('html,body').animate({scrollTop: $("#tabs").offset().top - (w.height()/2)}, 1000 );
	
}

function _getWSFascicolo(callback) {
	_wait();
	
	var entita = $("#entita").val();
	var key1 = $("#key1").val();
	var idconfi = $("#idconfi").val();

	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSFascicolo.do",
		data: {
			entita:entita,
			key1:key1,
			key2:null,
			key3:null,
			key4:null,
			idconfi:idconfi
		},
		success: function(json){
			$.map( json, function( item ) {
				_codicefascicoloGara = item[0];
				_annofascicoloGara = item[1];
				_numerofascicoloGara = item[2];
												
				var riepilogo = "Cod.: " + item[0];
				riepilogo += " - Anno:" + item[1];
				riepilogo += " - Num.:" + item[2];
				riepilogo += " - Classifica:" + item[6];
				if(item[3]!=null && item[3]!=''){
					riepilogo += " - AOO:" + item[3];
					if(item[9]!=null && item[9]!='')
						riepilogo += " " + item[9];
				}
				if(item[4]!=null && item[4]!=''){
					riepilogo += " - Ufficio:" + item[4];
					if(item[10]!=null && item[10]!='')
						riepilogo += " " + item[10];
				}
				$("#fascicoloGara").text(riepilogo);
				_fascicoloGaraAssociato = true;
				
			});
		},
		error: function(e){
			var messaggio = "Errore durante la lettura dei dati del fascicolo della gara";
			$('#wsdmdocumentomessaggio').text(messaggio);
			$('#wsdmdocumentomessaggio').show(tempo);
		},
		complete: function() {
			callback();
		}
	});
}

function inizializzazioneDatiFascicolo(){
	if(_fascicoloGaraAssociato){
		var codiceFascicoloProtocollo = $("#codicefascicolo").val();
		var annofascicoloProtocollo = $("#annofascicolo").val();
		var numerofascicoloProtocollo = $("#numerofascicolo").val();
		if(codiceFascicoloProtocollo == _codicefascicoloGara && annofascicoloProtocollo == _annofascicoloGara && _numerofascicoloGara == _numerofascicoloGara) {
			_fascicoliUguali = true;
			$("#FascicoliUguali").show();
		}else{
			$("#FascicoliDiversi").show();
			$("#rifFascicoloGara").show();
			_fascicoliUguali = false;
		}
	}else{
		$("#associazioneFascicolo").show();
		$('#sezioneamministrazioneorganizzativa').show();
		$("#rigaCodiceAoo").show();
		$("#rigaCodiceUfficio").show();
		_inizializzaCodiceAooFiltrato();
		_inizializzaCodiceUfficioFiltrato();
		_popolaTabellatoCodiceAoo();
		if(_codiceAooIniz!=null && _codiceAooIniz!= ''){
			$('#codiceaoonuovo option[value="' + _codiceAooIniz + '"]').prop('selected', true);
			$('#codiceaoonuovo_filtro option[value="' + _codiceAooIniz + '"]').prop('selected', true);
            $("#codiceaoo_filtro").val($("#codiceaoonuovo option:selected").text());
		}
		if(_codiceUfficioIniz!=null && _codiceUfficioIniz!= ''){
			caricamentoUfficioTITULUS();
			$('#codiceufficionuovo option[value="' + _codiceUfficioIniz + '"]').prop('selected', true);
			$('#codiceufficionuovo_filtro option[value="' + _codiceUfficioIniz + '"]').prop('selected', true);
            $("#codiceufficio_filtro").val($("#codiceufficionuovo option:selected").text());
		}
	}
}

function confermaStep2(){
	if($("#parametririchiestafascicolo").validate().form()){
		_wait();
		
		var entita = $("#entita").val();
		var key1 = $("#key1").val();
		var idconfi = $("#idconfi").val();
		var associarefascicolo=false;
		if(!_fascicoloGaraAssociato && $('#associaFascicoloSi').attr('checked')){
			associarefascicolo = true; 
		}
		var valoreTemp = $('#profilo option:selected').val(); // struttura profilo�codiceaoo�codiceufficio
		var vetValori = valoreTemp.split('�');
		_codiceProfilo = vetValori[0];
		
		$.ajax({
			type: "POST",
			dataType: "json",
			async: true,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pg/InserimentoDocumentiDaPRotocollo.do",
			data: {
				entita:entita,
				key1:key1,
				ngara : $("#ngara").val(),
				codiceGara : $("#codiceGara").val(),
				idconfi:idconfi,
				genereGara : $("#genereGara").val(),
				username: $("#username").val(),
				password: $("#password").val(),
				servizio:$("#servizio").val(),
				profilo: _codiceProfilo,
				gruppo: $('#gruppo').val(),
				tipologiaDoc: $('#tipologiaDoc').val(),
				codicefascicolo : $("#codicefascicolo").val(),
				annofascicolo : $("#annofascicolo").val(),
				numerofascicolo : $("#numerofascicolo").val(),
				classifica : $("#classificafascicolo").val(),
				classificadescrizione : $("#classificafascicolodescrizione").text(),
				voce : $("#voce").val(),
				codiceaoo : $("#codiceaoonuovo option:selected").val(),
				codiceufficio : $("#codiceufficionuovo option:selected").val(),
				codiceaoodes : $("#codiceaoonuovo option:selected").text(),
				codiceufficiodes : $("#codiceufficionuovo option:selected").text(),
				associarefascicolo : associarefascicolo,
				fascicoliUguali: _fascicoliUguali,
				numerodocumento: $("#numerodocumento").text(),
				annoprotocollo: $("#annoprotocollo").text(),
				numeroprotocollo: $("#numeroprotocollo").text(),
				oggettodocumento: $("#oggettodocumento").text(),
				inout: $("#INOUT").val(),
				dataprov: $("#dataprov").val(),
				numprov: $("#numeroprov").val(),
				sso: sso,
				utenteSso : $("#utenteSso").val()
				
				
			},
			success: function(json){
				$('#titolo3').show(tempo);
				$('#successo').show(tempo);
				$('#titolo0').hide();
				$('#titolo1').hide();
				$('#titolo2').hide();
				$('#FascicoliDiversi').hide();
				$('#FascicoliUguali').hide();
				$('#datiLogin').hide();
				$('#fascicolo').hide();
				$('#elementiDocumentali').hide();
			},
			error: function(e){
				var messaggio = "Errore durante l'inserimento dei documenti";
				$('#wsdmdocumentomessaggio').text(messaggio);
				$('#wsdmdocumentomessaggio').show(tempo);
			},
			complete: function() {
				_nowait();
			}
		});
	}
}

/**
 * Inizializzazione del widget per la gestione del filtro nella lista del codice AOO per TITULUS
 * viene sfruttato il plugin autocomplete
 */
function _inizializzaProfiloFiltrato() {
    $.widget( "custom.comboboxProfilo", {
      _create: function() {
        this.wrapper = $( "<span>" )
          .addClass( "custom-combobox" )
          .insertAfter( this.element );
 
        this.element.hide();
        this._createAutocomplete();
        this._createShowAllButton();
      },
 
      _createAutocomplete: function() {
        var selected = this.element.children( ":selected" ),
          value = selected.val() ? selected.text() : "";
 
        this.input = $( "<input>" )
          .appendTo( this.wrapper )
          .val( value )
          .attr( {id: "profilo_filtro_input" , name:  "profilo_filtro_input", size: "45"})
          .addClass( "custom-combobox-input ui-widget ui-widget-content  ui-corner-left" )
          .autocomplete({
            delay: 0,
            minLength: 0,
            source: $.proxy( this, "_source" )
          })
          .tooltip({
            classes: {
              "ui-tooltip": "ui-state-highlight"
            }
          });
 
        this._on( this.input, {
          autocompleteselect: function( event, ui ) {
            ui.item.option.selected = true;
            this._trigger( "select", event, {
              item: ui.item.option
            });
            $('#profilo').val(ui.item.option.value);
            $('#profilo').trigger("change");
            $('#profilo_filtro_input').attr( "title", ui.item.option.text );
          },
 
          autocompletechange: "_removeIfInvalid"
        });
      },
 
      _createShowAllButton: function() {
        var input = this.input,
          wasOpen = false;
 
        $( "<a>" )
          .attr( "tabIndex", -1 )
          .attr( "title", "Visualizza tutto" )
          .tooltip()
          .appendTo( this.wrapper )
          .button({
            icons: {
              primary: "ui-icon-triangle-1-s"
            },
            text: false
          })
          .removeClass( "ui-corner-all" )
          .addClass( "custom-combobox-toggle ui-corner-right" )
          .on( "mousedown", function() {
            wasOpen = input.autocomplete( "widget" ).is( ":visible" );
          })
          .on( "click", function() {
            input.trigger( "focus" );
 
            // Close if already visible
            if ( wasOpen ) {
              return;
            }
 
            // Pass empty string as value to search for, displaying all results
            input.autocomplete( "search", "" );
          });
      },
 
      _source: function( request, response ) {
        var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
        response( this.element.children( "option" ).map(function() {
          var text = $( this ).text();
          if ( this.value && ( !request.term || matcher.test(text) ) )
            return {
              label: text,
              value: text,
              option: this
            };
        }) );
      },
 
      _removeIfInvalid: function( event, ui ) {
 
        // Selected an item, nothing to do
        if ( ui.item ) {
        	return;
        }else{
        	$('#profilo').val('');
        	$('#profilo_filtro_input').attr( "title", "" );
            
        }
 
        // Search for a match (case-insensitive)
        var value = this.input.val(),
          valueLowerCase = value.toLowerCase(),
          valid = false;
        this.element.children( "option" ).each(function() {
          if ( $( this ).text().toLowerCase() === valueLowerCase ) {
            this.selected = valid = true;
            return false;
          }
        });
 
        // Found a match, nothing to do
        if ( valid ) {
        	return;
        }
 
        // Remove invalid value
        this.input
          .val( "" )
          .attr( "title", "Codice " + value + " non trovato nell'elenco" )
          .tooltip( "open" );
        this.element.val( "" );
        this._delay(function() {
          this.input.tooltip( "close" ).attr( "title", "" );
        }, 2500 );
        this.input.autocomplete( "instance" ).term = "";
        
        //Si deve sbiancare se valorizzato codice codiceaoonuovo
        $('#profilo').val("");
        $('#profilo_filtro_input').attr( "title", "" );
      },
 
      _destroy: function() {
        this.wrapper.remove();
        this.element.show();
      }
    });
 
    $( "#profilo" ).comboboxProfilo();
    
    $('.profilo_filtro_input').tooltip().click(function() {
        $('.profilo_filtro_input').tooltip( "close");
    });

  }