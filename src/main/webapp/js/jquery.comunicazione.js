/*
*	Gestione del fascicolo e dei documenti protocollati e non
*
*/


$(window).ready(function (){
	
	_wait();
	
	var _tableDestinatariComunicazione = null;
	var tuttiAllegatiFormatoValido = null;
	var _commodello = null;
	var _comdatsca = null;
	var _comorasca = null;
	var _commsgtes = null;
	var primoAllegatoFirmato = null;
		
    /*
     * Gestione utente ed attributi per il collegamento remoto
     */
	_getWSTipoSistemaRemoto();
	_popolaTabellato("ruolo","ruolo");
	_popolaTabellato("codiceuo","codiceuo");
	_getWSLogin();
	_gestioneWSLogin();
	_controlloObbligatorietaClassifica();
	_controlloObbligatorietaUfficio();
	
	/*
	 * Gestione tabellati per richiesta protocollazione
	 */
	_popolaTabellato("classifica","classificadocumento");
	_popolaTabellato("codiceregistro","codiceregistrodocumento");
	_popolaTabellato("tipodocumento","tipodocumento");
	_popolaTabellato("mittenteinterno","mittenteinterno");
	_popolaTabellato("livelloriservatezza","livelloriservatezza");
	_popolaTabellato("indirizzomittente","indirizzomittente");
	_popolaTabellato("mezzo","mezzoinvio");
	_popolaTabellato("mezzo","mezzo");
	_popolaTabellato("supporto","supporto");
	_popolaTabellato("classificafascicolo","classificafascicolonuovo");
	_popolaTabellato("idindice","idindice");
	_popolaTabellato("idtitolazione","idtitolazione");
	_popolaTabellato("idunitaoperativamittente","idunitaoperativamittente");
	_controlloPresenzaFascicolazione();
	_controlloFascicoliAssociati();
	_popolaTabellato("sottotipo","sottotipo");
	_popolaTabellato("tipofirma","tipofirma");
	//_inizializzazioni();
		
	
	/*
	 * Avvio all'apertura della maschera del popolamento 
	 * della lista degli elementi documentali del fascicolo
	 */
	setTimeout(function(){
		
		_validazioneWSLogin();
		_validazioneRichiestaInserimentoProtocollo();
		_getComunicazione();
		_controlloPresenzaFascicolazione();
		_controlloFascicoliAssociati();
		_inizializzazioni();
	}, 800);
	
	/*
	 * Definizioni per il metodo validate()
	 */
	function _validazioneRichiestaInserimentoProtocollo() {
		
		var maxDescrizioneFascicoloNuovo= 
		$("#richiestainserimentoprotocollo").validate({
			rules: {
				classificadocumento: "required",
				classdoc_filtro: "required",
				codiceregistrodocumento: "required",
				tipodocumento: "required",
				mittenteinterno: "required",
				indirizzomittente: "required",
				mezzoinvio: "required",
				mezzo: "required",
				supporto: "required",
				oggettodocumento: "required",
				inout: "required",
				inserimentoinfascicolo: "required",
				numerodestinatari: {
					required: true,
					min: 1
				},
				numerodestinatarinocf: {
					required: true,
					range: [0, 0]
				},
				numerodestinatarinopec: {
					required: true,
					range: [0, 0]
				},
				codicefascicolo: "required",	
			    annofascicolo: "required",	
			    numerofascicolo: "required",	
				oggettofascicolonuovo: "required",
				classificafascicolonuovo: "required",
				descrizionefascicolonuovo: "required",
				tipofascicolonuovo: "required",
				numeroallegatinodescr: {
					required: true,
					range: [0, 0]
				},
				numeroallegatiattesafirma: {
					required: true,
					range: [0, 0]
				},
				idindice: "required",
				idtitolazione: "required",
				idunitaoperativamittente: "required",
				codiceaoonuovo: "required",
				codiceaoo_filtro: "required",
				strutturaonuovo: "required",
				codiceufficio_filtro: "required",
				sottotipo: "required",
				tipofirma: "required",
				listafascicoli: "required",
				uocompetenzaTxt: "required"
			},
			messages: {
				classificadocumento: "Specificare la classifica",
				classdoc_filtro: "Specificare la classifica",
				codiceregistrodocumento: "Specificare il codice registro",
				tipodocumento: "Specificare il tipo documento",
				mittenteinterno: "Specificare il mittente interno",
				indirizzomittente: "Specificare l'indirizzo mittente",
				mezzoinvio: "Specificare il mezzo invio",
				mezzo: "Specificare il mezzo",
				supporto: "Specificare il supporto",
				oggettodocumento: "Specificare l'oggetto",
				inout: "Specificare il verso di protocollazione",
				inserimentoinfascicolo: "Specificare la modalit&agrave; di associazione",
				numerodestinatari: {
					required: "Deve essere indicato almeno un destinatario",
					min: "Deve essere indicato almeno un destinatario"
				},
				numerodestinatarinocf: {
					required: "Per tutti i destinatari deve essere valorizzato il codice fiscale",
					range: "Per tutti i destinatari deve essere valorizzato il codice fiscale"
				},
				numerodestinatarinopec: {
					required: "Per tutti i destinatari deve essere specificata la pec",
					range: "Per tutti i destinatari deve essere specificata la pec"
				},
				codicefascicolo: "Specificare il codice del fascicolo",
				annofascicolo: "Specificare l'anno del fascicolo",
				numerofascicolo: "Specificare il numero del fascicolo",
				oggettofascicolonuovo: "Specificare l'oggetto del fascicolo",
				classificafascicolonuovo: "Specificare la classifica del fascicolo",
				descrizionefascicolonuovo: "Specificare la descrizione del fascicolo",
				tipofascicolonuovo:"specificare il tipo del fascicolo",
				numeroallegatinodescr: {
					required: "Per tutti gli allegati deve essere valorizzata la descrizione",
					range: "Per tutti gli allegati deve essere valorizzata la descrizione"
				},
				numeroallegatiattesafirma: {
					required: "Non ci devono essere allegati in attesa di firma",
					range: "Non ci devono essere allegati in attesa di firma"
				},
				idindice: "Specificare l'indice",
				idtitolazione: "Specificare la classifica",
				idunitaoperativamittente: "Specificare l'unit&agrave; operativa mittente",
				codiceaoonuovo:"Specificare il codice AOO",
				codiceaoo_filtro:"Specificare il codice AOO",
				strutturaonuovo:"Specificare la struttura",
				codiceufficio_filtro:"Specificare il codice ufficio",
				sottotipo:"Specificare il sottotipo",
				tipofirma: "Specificare il tipo firma",
				listafascicoli: "Specificare il fascicolo",
				uocompetenzaTxt: "Specificare l'unit&agrave; operativa di competenza"
			},
			errorPlacement: function (error, element) {
				error.insertAfter($(element));
				//Nel caso di TITULUS, il campo codiceaoo_filtro è seguito da un elemento <a>, quindi per il messaggio di errore serve del margine  
				if($(element).attr('id') == 'codiceaoo_filtro'  || $(element).attr('id') == 'codiceufficio_filtro' || $(element).attr('id') == 'classdoc_filtro'){
					   error.css("margin-left","25px");
				}                    
				error.css("margin-right","5px");
				error.css("float", "right");
				error.css("vertical-align", "top");
			}
		});
		_getTipoWSDM();
		if (_tipoWSDM != "IRIDE" && _tipoWSDM != "JIRIDE") {
			$( "#mittenteinterno" ).rules( "remove" );
			
		}
		if (_tipoWSDM != "JIRIDE" && _tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA") {
			$( "#indirizzomittente" ).rules( "remove" );
		}
		
		if(_tipoWSDM != "JIRIDE" && _tipoWSDM != "IRIDE"){
			$( "#mezzoinvio" ).rules( "remove" );
		}
		if(_tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "JDOC" && _tipoWSDM != "NUMIX"){
			$( "#mezzo" ).rules( "remove" );
		}
		if(_tipoWSDM != "ARCHIFLOWFA" && _tipoWSDM != "PRISMA" && _tipoWSDM != "JIRIDE" && _tipoWSDM != "INFOR"){
			$( "#supporto" ).rules( "remove" );
			$( "#strutturaonuovo" ).rules( "remove" );
		}
		if (_tipoWSDM == "ENGINEERING") {
			$( "#descrizionefascicolonuovo" ).rules( "add", {
				  required: true,
				  maxlength: 75,
				  messages: {
				    maxlength: jQuery.validator.format("La lunghezza massima deve essere di {0} caratteri")
				  }
				});

		}
		if (_tipoWSDM == "TITULUS") {
			$( "#oggettodocumento" ).rules( "add", {
				  required: true,
				  minlength: 30,
				  messages: {
					  minlength: jQuery.validator.format("La lunghezza minima deve essere di {0} caratteri")
				  }
				});
			if(!_classificaObbligatoria)
				$( "#classificafascicolonuovo" ).rules( "remove" );
			
			$( "#posAllegato" ).rules( "add", {
				  required: true,
				  messages: {
					  required: "Specificare Testo della comunicazione come allegato principale?"
				  }
				});
				
		}
		if(_tipoWSDM == "ARCHIFLOWFA"){
			$( "#oggettodocumento" ).rules( "remove" );
			$( "#oggettodocumento" ).rules( "add", {
				  required: true,
				  maxlength: 4000,
				  messages: {
					  required: "Specificare l'oggetto",
				    maxlength: jQuery.validator.format("La lunghezza massima deve essere di {0} caratteri")
				  }
				});
			$( "#oggettofascicolonuovo" ).rules( "remove" );
			$( "#oggettofascicolonuovo" ).rules( "add", {
				  required: true,
				  messages: {
					  required: "Leggere il fasciciolo per caricare l'oggetto"
				  }
			});
			$( "#classificafascicolonuovo" ).rules( "remove" );
			$( "#classificafascicolonuovo" ).rules( "add", {
				  required: true,
				  messages: {
					  required: "Leggere il fasciciolo per caricare la classifica"
				  }
			});
		}
		if(_tipoWSDM == "FOLIUM"){
			$( "#categoria" ).rules( "add", {
				required: true,
				  messages: {
					  required: "Specificare la classifica del fascicolo"
				  }
			});
		}
		if(_tipoWSDM == "JIRIDE"){
			$( "#annofascicolo" ).rules( "remove" );
			if ($('#livelloriservatezza').length > 0) {
				$( "#livelloriservatezza" ).rules( "add", {
					  required: true,
					  messages: {
						  required: "Specificare il livello di riservatezza"
					  }
				});
			}
		}
		if(_tipoWSDM == "PRISMA"){
			$( "#oggettofascicolonuovo" ).rules( "remove" );
			$( "#oggettofascicolonuovo" ).rules( "add", {
				  required: true,
				  messages: {
					  required: "Leggere il fasciciolo per caricare l'oggetto"
				  }
			});
			$( "#classificafascicolonuovoPrisma" ).rules( "add", {
				required: true,
				  messages: {
					  required: "Specificare la classifica del fascicolo"
				  }
			});
		}
		
		if (_tipoWSDM == "INFOR" || _tipoWSDM == "JPROTOCOL") {
			var numCaratteri=255;
			if (_tipoWSDM == "JPROTOCOL")
				numCaratteri=160;
			$( "#oggettofascicolonuovo" ).rules( "remove" );
			$( "#oggettofascicolonuovo" ).rules( "add", {
				  required: true,
				  maxlength: numCaratteri,
				  messages: {
					  required: "Specificare l'oggetto del fascicolo",
					  maxlength: jQuery.validator.format("La lunghezza massima deve essere di {0} caratteri")
				  }
				});

		}
		
		if (_tipoWSDM == "JDOC") {
			$( "#oggettodocumento" ).rules( "remove" );
			$( "#oggettodocumento" ).rules( "add", {
				  required: true,
				  maxlength: 500,
				  messages: {
					  required: "Specificare l'oggetto",
				    maxlength: jQuery.validator.format("La lunghezza massima deve essere di {0} caratteri")
				  }
				});
			$( "#oggettofascicolonuovo" ).rules( "remove" );
			$( "#oggettofascicolonuovo" ).rules( "add", {
				  required: true,
				  maxlength: 500,
				  messages: {
					  required: "Specificare l'oggetto del fascicolo",
					  maxlength: jQuery.validator.format("La lunghezza massima deve essere di {0} caratteri")
				  }
				});

		}
		
		if(_tipoWSDM == "ITALPROT"){
			$( "#classificafascicolonuovoItalprot" ).rules( "add", {
				required: true,
				  messages: {
					  required: "Specificare la classifica del fascicolo"
				  }
			});
		}
		
		if(_tipoWSDM == "ENGINEERINGDOC" && _fascicoliPresenti >0){
			$( "#uocompetenzaTxt" ).rules( "remove" );
			$( "#uocompetenzaTxt" ).rules( "add", {
				  required: true,
				  messages: {
					  required: "Specificare l'unit&agrave; operativa di competenza.<br>Utilizzare la funzione 'Modifica U.O. di competenza' disponibile nella scheda della procedura",
				  }
				});
		}
		
	}
	
	
	
	/*
	 * Lettura della singola comunicazione.
	 * Utilizzata per popolare i dati generali della comunicazione,
	 * la lista dei destinatari e la lista degli allegati.
	 */
	function _getComunicazione() {
				
		var servizio = $("#servizio").val();
		
		/*
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pg/GetWSDMRemote.do",
			data: "servizio=" + servizio,
			success: function(data){
				if (data) {
					$.map( data, function( item ) {
						_tipoWSDM = item[0];
						if (item[0] == "IRIDE" || item[0] == "JIRIDE") {
							$("#codiceregistrodocumento").hide();
							$("#codiceregistrodocumento").closest('tr').hide();
						}
						
						if (item[0] == "PALEO") {
							$("#classificadocumento").hide();
							$("#classificadocumento").closest('tr').hide();
							$("#tipodocumento").hide();
							$("#tipodocumento").closest('tr').hide();
							$("#mittenteinterno").hide();
							$("#mittenteinterno").closest('tr').hide();
						}
					});
	        	}
			}
		});
		*/
		_getTipoWSDM();
		if (_tipoWSDM == "IRIDE" ||_tipoWSDM == "JIRIDE") {
			$("#codiceregistrodocumento").hide();
			$("#codiceregistrodocumento").closest('tr').hide();
			$("#idindice").hide();
			$("#idindice").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
			$("#idunitaoperativamittente").hide();
			$("#idunitaoperativamittente").closest('tr').hide();
		}
		
		_controlloDelegaInvioMailAlDocumentale();
		
		if (_tipoWSDM == "PALEO") {
			$("#classificadocumento").hide();
			$("#classificadocumento").closest('tr').hide();
			$("#tipodocumento").hide();
			$("#tipodocumento").closest('tr').hide();
			$("#mittenteinterno").hide();
			$("#mittenteinterno").closest('tr').hide();
			$("#idindice").hide();
			$("#idindice").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
			$("#idunitaoperativamittente").hide();
			$("#idunitaoperativamittente").closest('tr').hide();
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
		}
		
		if (_tipoWSDM == "ENGINEERING") {
			$("#classificadocumento").hide();
			$("#classificadocumento").closest('tr').hide();
			$("#codiceregistrodocumento").hide();
			$("#codiceregistrodocumento").closest('tr').hide();
			$("#tipodocumento").hide();
			$("#tipodocumento").closest('tr').hide();
			$("#mittenteinterno").hide();
			$("#mittenteinterno").closest('tr').hide();
			$("#mezzo").hide();
			$("#mezzo").closest('tr').hide();
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
		}
		
		
		if ((_tipoWSDM != "JIRIDE"  && _tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA") || ((_tipoWSDM == "JIRIDE" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "ARCHIFLOWFA") && _delegaInvioMailDocumentaleAbilitata != 1)) {
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
			$('#indirizzomittente option').eq(0).prop('selected', true);
		}
		if (_tipoWSDM != "JIRIDE" && _tipoWSDM != "IRIDE" && _tipoWSDM != "TITULUS") {
			$("#mezzoinvio").hide();
			$("#mezzoinvio").closest('tr').hide();
			//$('#mezzoinvio option').eq(0).prop('selected', true);
		}
		if (_tipoWSDM != "JIRIDE") {
			$("#livelloriservatezza").hide();
			$("#livelloriservatezza").closest('tr').hide();
		}
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pg/GetW_INVCOM.do",
			data: "idprg=" + $("#idprg").val() + "&idcom=" + $("#idcom").val(), 
			success: function(json){
				$.map( json.dataW_INVCOM, function( item ) {
					$("#oggettodocumento").val(item.commsgogg);
					$('#inout').val("OUT").attr("selected", "selected");
					_commodello = item.commodello;
					_comdatsca = item.comdatsca;
					_comorasca = item.comorasca;
					_commsgtes = item.commsgtes;
				});
				$("#numerodestinatari").val(json.iTotalRecordsW_INVCOMDES);
				$("#numerodestinatari").prop("readonly", true);
				$("#numerodestinatari").addClass("readonly");
				if (json.iTotalRecordsW_INVCOMDES > 0) {
					if (json.iTotalRecordsW_INVCOMDES_NOCF > 0) {
						$("#sezionenumerodestinatarinocf").show(tempo);
						$("#numerodestinatarinocf").val(json.iTotalRecordsW_INVCOMDES_NOCF);
						$("#numerodestinatarinocf").prop("readonly", true);
						$("#numerodestinatarinocf").addClass("readonly");
					}
					if (json.iTotalRecordsW_INVCOMDES_NOPEC > 0 && (_tipoWSDM == "PALEO" || _tipoWSDM == "JIRIDE" || _tipoWSDM == "ENGINEERING") && _delegaInvioMailDocumentaleAbilitata == 1) {
						$("#sezionenumerodestinatarinopec").show(tempo);
						$("#numerodestinatarinopec").val(json.iTotalRecordsW_INVCOMDES_NOPEC);
						$("#numerodestinatarinopec").prop("readonly", true);
						$("#numerodestinatarinopec").addClass("readonly");
					}
					$("#sezionedestinataricomunicazione").show(tempo);
					_popolaTabellaDestinatariComunicazione(json.iTotalRecordsW_INVCOMDES, json.dataW_INVCOMDES)
				}
				$("#numeroallegati").val(json.iTotalRecordsW_DOCDIG);
				$("#numeroallegati").prop("readonly", true);
				$("#numeroallegati").addClass("readonly");
				if (json.iTotalRecordsW_DOCDIG_NODESCRIZIONE > 0) {
					$("#sezionenumeroallegatinodescr").show(tempo);
					$("#numeroallegatinodescr").val(json.iTotalRecordsW_DOCDIG_NODESCRIZIONE);
					$("#numeroallegatinodescr").prop("readonly", true);
					$("#numeroallegatinodescr").addClass("readonly");
				}
				if (json.iTotalRecordsW_DOCDIG_ATTESAFIRMA > 0) {
					$("#sezionenumeroallegatiattesafirma").show(tempo);
					$("#numeroallegatiattesafirma").val(json.iTotalRecordsW_DOCDIG_ATTESAFIRMA);
					$("#numeroallegatiattesafirma").prop("readonly", true);
					$("#numeroallegatiattesafirma").addClass("readonly");
				}
				tuttiAllegatiFormatoValido=json.tuttiAllegatiFormatoValido;
				primoAllegatoFirmato = json.primoAllegatoFirmato;
				
			},
			error: function(e){
				var messaggio = "Errore durante la lettura della comunicazione";
				$('#comunicazionimessaggio').text(messaggio);
				$('#comunicazionimessaggio').show(tempo);
			},
			complete: function() {
				_nowait();
			}
		});
	}
	
	
	/*
	 * Popola la lista dei destinatari della comunicazione selezionata
	 */
	function _popolaTabellaDestinatariComunicazione(numeroTotale, destinatari) {
		
		if (_tableDestinatariComunicazione != null) {
			_tableDestinatariComunicazione.destroy(true);
		}
		
		var _table = $('<table/>', {"id": "destinataricomunicazione", "class": "elementi", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr = $('<tr/>');
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_thead.append(_tr);
		_table.append(_thead);
		var _tbody = $('<tbody/>');
		_table.append(_tbody);
		$("#destinataricomunicazionecontainer").append(_table);
		
		_tableDestinatari = $('#destinataricomunicazione').DataTable( {
			"data": destinatari,
			"columnDefs": [
				{
					"data": "desintest",
					"visible": true,
					"sWidth": "400px",
					"sTitle": "Intestazione",
					"targets": [ 0 ]
				},
				{
					"data": "desmail",
					"visible": true,
					"sTitle": "Indirizzo email",
					"targets": [ 1 ]
				},
				{
					"data": "codicefiscale",
					"visible": true,
					"sTitle": "Codice fiscale",
					"targets": [ 2 ]
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
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });

		if (numeroTotale == 0) {
			$("#destinataricomunicazione tfoot").hide();
			$("#destinataricomunicazione_info").hide();
			$("#destinataricomunicazione_paginate").hide();
		}
	}

	
	

	/*
	 * Protocolla ed invia la comunicazione
	 */
	function _protocollaComunicazione() {
						
		_wait();
		$.ajax({
    		type: "POST",
    		async: true,
    		dataType: "json",
    		url: "pg/ProtocollaComunicazione.do",
    		data : {
				username: $("#username").val(),
				password: $("#password").val(),
				ruolo: $("#ruolo option:selected").val(),
				nome : $("#nome").val(),
				cognome : $("#cognome").val(),
				codiceuo : $("#codiceuo option:selected").val(),
				idutente : $("#idutente").val(),
				idutenteunop : $("#idutenteunop").val(),
				classificadocumento : $("#classificadocumento").val(),
				tipodocumento : $("#tipodocumento").val(),
				oggettodocumento : $("#oggettodocumento").val(),
				descrizionedocumento : $("#descrizionedocumento").val(),
				mittenteinterno : $("#mittenteinterno").val(),
				indirizzomittente : $("#indirizzomittente").val(),
				mezzoinvio : $("#mezzoinvio").val(),
				mezzo : $("#mezzo").val(),
				inout : $("#inout").val(),
				inserimentoinfascicolo : $("#inserimentoinfascicolo option:selected").val(),
				codicefascicolo : $("#codicefascicolo").val(),
				annofascicolo : $("#annofascicolo").val(),
				numerofascicolo : $("#numerofascicolo").val(),
				oggettofascicolo : $("#oggettofascicolonuovo").val(),
				classificafascicolo : $("#classificafascicolonuovo").val(),
				tipofascicolo : $("#tipofascicolonuovo").val(),
				descrizionefascicolo : $("#descrizionefascicolonuovo").val(),
				codiceregistrodocumento : $("#codiceregistrodocumento").val(),
				idprg : $("#idprg").val(),
				idcom : $("#idcom").val(),
				idcfg : $("#idcfg").val(),
				entita : $("#entita").val(),
				key1 : $("#key1").val(),
				key2 : $("#key2").val(),
				key3 : $("#key3").val(),
				key4 : $("#key4").val(),
				idindice : $("#idindice option:selected").val(),
				idtitolazione : $("#idtitolazione option:selected").val(),
				idunitaoperativamittente : $("#idunitaoperativamittente option:selected").val(),
				tipowsdm : _tipoWSDM,
				delegainviomail : _delegaInvioMailDocumentaleAbilitata,
				codiceaoo : $("#codiceaoonuovo option:selected").val(),
				codiceufficio : $("#codiceufficionuovo option:selected").val(),
				societa : $("#societa").val(),
				codicegaralotto : $("#codicegaralotto").val(),
				cig : $("#cig").val(),
				generegara: _genereGara,
				numeroallegati: $("#numeroallegati").val(),
				livelloriservatezza: $("#livelloriservatezza").val(),
				isRiservatezzaAttiva: $("#isRiservatezzaAttiva").val(),
				struttura: $("#strutturaonuovo option:selected").val(),
				supporto: $("#supporto option:selected").val(),
				idconfi: $("#idconfi").val(),
				classificadescrizione: $("#classificadescrizione").val(),
				voce: $("#voce").val(),
				codiceaoodes : $("#codiceaoonuovo option:selected").text(),
				codiceufficiodes : $("#codiceufficionuovo option:selected").text(),
				RUP:$("#RUP").val(),
				nomeRup: $("#nomeRup").text(),
				acronimoRup: $("#acronimoRup").text(),
				sottotipo: $("#sottotipo option:selected").val(),
				posAllegato: $("#posAllegato option:selected").val(),
				idunitaoperativamittenteDesc: $( "#idunitaoperativamittente option:selected" ).text(),
				tipofirma:$("#tipofirma option:selected").val(),
				uocompetenza:$("#uocompetenza").val(),
				uocompetenzadescrizione:$("#uocompetenzadescrizione").val()
			},
    		success: function(json) {
    			if (json) {
					if (json.esito == true) {
						_nowait();
						var messaggio = "La comunicazione e' stata protocollata con il numero " + json.numeroprotocollo ; 
						if(json.annoprotocollo!=null && json.annoprotocollo != "")
							messaggio += " del " + json.annoprotocollo; 
						
						if (json.inserimentoinfascicolo == 'SI_FASCICOLO_ESISTENTE') {
							messaggio = messaggio + " ed inserita nel fascicolo indicato.";
						}
						
						if (json.inserimentoinfascicolo == 'SI_FASCICOLO_NUOVO') {
							if (_tipoWSDM != "ENGINEERING" && _tipoWSDM != "PRISMA" && _tipoWSDM != "INFOR" && _tipoWSDM != "JPROTOCOL") {
								var fascicolo = json.codicefascicolo;
							}else{
								var fascicolo = json.numerofascicolo;
							}
							
							var msgFascicoloJIRIDE="";
							if(_tipoWSDM == "JIRIDE" && (fascicolo==null || fascicolo =="")){
								messaggio += ".";
								msgFascicoloJIRIDE="<font color='red'>Il codice del fascicolo restituito dal servizio di protocollazione risulta nullo!</font><br>";
							}else{
								messaggio = messaggio + " ed inserita nel nuovo fascicolo " + fascicolo;
								if (_tipoWSDM != "PALEO") {
									messaggio += " del " + json.annofascicolo + ".";
								} else {
									messaggio += ".";
								}
							}
							if(_tipoWSDM == "JIRIDE" && (json.numerofascicolo==null || json.numerofascicolo =="")){
								msgFascicoloJIRIDE+="<font color='red'>Il numero del fascicolo restituito dal servizio di protocollazione risulta nullo!</font>"; 
							}
							if(_tipoWSDM == "JIRIDE" && msgFascicoloJIRIDE!=""){
								$("#messageinfo").after(msgFascicoloJIRIDE); 
							}
						}
						
						$("#messageinfo").text(messaggio);
						if (json.esitoInviaMail == false) {
							$("#messageinviomailinfo").text("Il tentativo di invio della comunicazione segnala il seguente errore: " + json.messaggioInviaMail);
							$("#messageinviomailinfo").show();
						}
				    	$("#info").show(tempo);
				    	if(_delegaInvioMailDocumentaleAbilitata == 1 && _tipoWSDM != "IRIDE"){
				    		$("#messageinfoMailStandard").hide(tempo);
				    		if(json.statoComunicazione == 11)
				    			$("#messageinfoMailDocumentaleErr").show(tempo);
				    		else
				    			$("#messageinfoMailDocumentaleErr").hide(tempo);
				    	}else{
				    		$("#messageinfoMailDocumentale").hide(tempo);
				    		
				    	}
				    	$("#richiestawslogin").hide(tempo);
				    	$("#richiestainserimentoprotocollo").hide(tempo);
				    	var w = $(window);
			 		    $('html,body').animate({scrollTop: $("#info").offset().top - (w.height()/2)}, 1000 );
				    	
					} else {
						var messaggio = "Il tentativo di invio al protocollo segnala il seguente errore: " + json.messaggio;
						$('#protocollacomunicazionemessaggio').text(messaggio);
						$('#protocollacomunicazionemessaggio').show(tempo);	
						_nowait();
					}
    			}
    		},
    		error: function(e) {
    			var messaggio = "Errore durante l'invio dei dati al protocollo";
				$('#protocollacomunicazionemessaggio').text(messaggio);
				$('#protocollacomunicazionemessaggio').show(tempo);
				_nowait();
    		}
    	});
	}
	
	
	/*
	 * Invio della comunicazione selezionata al sistema di 
	 * protocollazione
	 */
	$('#wsdmprotocollapulsante').click(function() {
		_setWSLogin();
		$('#protocollacomunicazionemessaggio').text("");
		$('#protocollacomunicazionemessaggio').hide();
		//Per Archiflow deve essere valorizzato il codice dell'ufficio intestatario
		if(_tipoWSDM == "ARCHIFLOW" && (_ufficioIntestatario==null || _ufficioIntestatario=="")){
			var msg="Non e' possibile procedere poiche' non e' valorizzato il codice della stazione appaltante";
			if(_genereGara==10)
				msg+=" dell'elenco";
			else if(_genereGara==20)
				msg+=" del catalogo";
			else if(_genereGara==11)
				msg+=" dell'avviso";
			else
				msg+=" della gara";
			alert(msg);
			return;
		}
		if(_tipoWSDM == "JDOC"){
			var nomeRUP = $("#nomeRup").text();
			var acronimo = $("#acronimoRup").text(acronimo);
			if(nomeRUP == null || nomeRUP == ''){
				var msg="Non e' possibile procedere poiche' non sono valorizzati nome e cognome del RUP";
				alert(msg);
				return;
			}
			
		}
		if(_commsgtes==null || _commsgtes==""){
			alert("Non e' possibile procedere perche' il testo della comunicazione e' vuoto.");
			return;
		}
		if(!tuttiAllegatiFormatoValido){
			alert("Non e' possibile procedere perche' ci sono degli allegati con formato non valido.");
			return;
		}
		if(_tipoWSDM == "LAPISOPERA" && !primoAllegatoFirmato){
			alert(messaggioLapisoperaAllegati);
			return;
		}
		if(_commodello == '1'){
			if(_comdatsca == null && _comorasca == null){
				alert("Non e' possibile procedere perche' non e' stata specificata la data e l'ora di termine presentazione documentazione.");
				return;
			}else{
				var oggi = new Date();
				var temp = _comdatsca.split("/");
				var giorno = temp[0];
				var mese = temp[1];
				var anno = temp[2]; 
				temp = _comorasca.split(":");
				var ore = temp[0];
				var minuti = temp[1];
				var comdatsca = new Date(anno, mese-1, giorno, ore, minuti, 0, 0);
				if(comdatsca < oggi){
					alert("Non e' possibile procedere perche' la data termine presentazione documentazione e' precedente alla data corrente");
					return;
				}
			}
		}
		if(_tipoWSDM == "FOLIUM" && $("#inserimentoinfascicolo").val()=="SI_FASCICOLO_NUOVO"){
			var categoria=$("#categoria").val();
			var classe=$("#classe").val();
			var sottoclasse=$("#sottoclasse").val();
			var sottosottoclasse=$("#sotto-sottoclasse").val();
			var fascicoloFolium=$("#fascicoloFolium").val();
			var titolare=$("#titolare").val();
			if(categoria==null)
				categoria="";
			if(classe==null)
				classe="";
			if(sottoclasse==null)
				sottoclasse="";
			if(sottosottoclasse==null)
				sottosottoclasse="";
			if(fascicoloFolium==null)
				fascicoloFolium="";
			if(titolare==null)
				titolare="";
			popolaClassificaFoliumDaCampi(categoria,classe,sottoclasse,sottosottoclasse,fascicoloFolium,titolare);
		}
		
		if ($("#richiestawslogin").validate().form() && $("#richiestainserimentoprotocollo").validate().form()) {
			_protocollaComunicazione();
		}
    });

	$('#wsdmritornapulsante').click(function() {
		historyVaiIndietroDi(1);
    });
	
	
	$('#inserimentoinfascicolo').change(function() {
		_gestioneInserimentoInFascicolo();
    });
    
	$('#classificafascicolonuovo').change(function() {
		if (_tipoWSDM == "TITULUS"){
			var valore=$( "#classificafascicolonuovo option:selected" ).val();
			var codice = null;
			var voce = null;
			var descrizione =null;
			if(valore != null && valore != ""){
				codice = valore.split(" .-+-. ")[0];
				descrizione = valore.split(" .-+-. ")[1];
				voce = valore.split(" .-+-. ")[2];
			}
			$('#classificadocumento').val(codice);
			$("#classificadescrizione").val(descrizione);
			$("#voce").val(voce);
		}else{
			$('#classificadocumento').val($('#classificafascicolonuovo').val());
		}
		$('#idtitolazione').val($('#classificafascicolonuovo').val());
		
	});
	
	
	//Per TITULUS alla variazione della login si deve caricare il tabellato del codice AOO
	$('#username').change(function() {
		if (_tipoWSDM == "TITULUS"){
			caricamentoCodiceAooTITULUS();
			_popolaTabellatoClassificaTitulus();
		}
		if (_tipoWSDM == "JIRIDE"){
			if(_letturaMittenteDaServzio){
				$('#mittenteinterno').empty();
				_popolaTabellatoJirideMittente("mittenteinterno");
				if(_delegaInvioMailDocumentaleAbilitata == 1){
					$('#indirizzomittente').empty();
					_popolaTabellatoJirideMittente("indirizzomittente");
				}
			}
			caricamentoStrutturaJIRIDE();
		}
		if (_tipoWSDM == "NUMIX"){
			caricamentoClassificaNumix();
		}
    });
	
	$('#password').change(function() {
		if (_tipoWSDM == "TITULUS"){
			caricamentoCodiceAooTITULUS();
			_popolaTabellatoClassificaTitulus();
		}
		if (_tipoWSDM == "NUMIX"){
			caricamentoClassificaNumix();
		}
    });
	
	$('#codiceaoonuovo').change(function() {
		if (_tipoWSDM == "TITULUS"){
			caricamentoUfficioTITULUS();
		}
    });
	
			
	$('#codicefascicolo').change(function() {
		if (_tipoWSDM == "ARCHIFLOWFA"){
			gestionemodificacampofascicolo();
		}
	});
	
	$('#annofascicolo').change(function() {
		if (_tipoWSDM == "PRISMA"){
			gestionemodificacampoannofascicolo();
		}
		if (_tipoWSDM == "ITALPROT"){
			$('#listafascicoli').empty();
			$('#codicefascicolo').val(); 
		}
	});
	
	$('#numerofascicolo').change(function() {
		if (_tipoWSDM == "PRISMA"){
			gestionemodificacamponumerofascicolo();
		}
	});
	
	$('#classificafascicolonuovoPrisma').change(function() {
		gestionemodificacampoclassificafascicolo();
	});
	
	$('#ruolo').change(function() {
		if (_tipoWSDM == "JIRIDE"){
			caricamentoStrutturaJIRIDE();
		}
    });
    
    $('#classificafascicolonuovoItalprot').change(function() {
		$('#listafascicoli').empty();
		$('#codicefascicolo').val(); 
		
	});
    
	$('#listafascicoli').on('change',  function () {
		var str = this.value;
		gestioneSelezioneFascicolo(str);
		
	});
		
});


