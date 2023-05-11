
/*
 * Gestione dei permessi
 */

var _integrazioneCOS;
var _integrazioneWSDM;
var _isStipula=false;

$(window).on("load", function (){
	
	if($("#entita").val() == "G1STIPULA")
		_isStipula=true;
		
	if($("#genere").val()=='1' || $("#genere").val()=='3'){
		var _lottoVisibile = true;
	}else{
		var _lottoVisibile = false;
	}
	
	_getTrasferisciDocumentaleWSDM();
	
	var _tableDocumenti = null;
	$(".contenitore-dettaglio").width("980");

	trasmissioneAbilitata = $("#trasmissioneAbilitata").val() == "true";
	
	_popolaDocumenti();
	_wait();
	
	_getTrasferisciDocumentaleWSDM();
	if(_integrazioneWSDM == '1'){
		/*
	     * Gestione utente ed attributi per il collegamento remoto
	     */
		
		_getWSTipoSistemaRemoto();
		_popolaTabellato("ruolo","ruolo");
		_popolaTabellato("codiceuo","codiceuo");
		_getWSLogin();
		_gestioneWSLogin();
		
		
		/*
		 * Gestione tabellati per richiesta protocollazione
		 */
		_popolaTabellato("classifica","classificadocumento");
		_popolaTabellato("codiceregistro","codiceregistrodocumento");
		_popolaTabellato("tipodocumento","tipodocumento");
		_popolaTabellato("mittenteinterno","mittenteinterno");
		_popolaTabellato("classificafascicolo","classificafascicolonuovo");
		_popolaTabellato("idtitolazione","idtitolazione");
		_popolaTabellato("mezzo","mezzo");
		_popolaTabellato("supporto","supporto");
		_popolaTabellato("idunitaoperativamittente","idunitaoperativamittente");
		_popolaTabellato("sottotipo","sottotipo");
		_popolaTabellato("tipofirma","tipofirma");
		$('#idunitaoperativadestinataria').val($('#idunitaoperativamittente').val());
		_controlloPresenzaFascicolazione();
		_controlloFascicoliAssociati();
		_inizializzazioni();
		
	}
	
				
		/*
		 * Definizioni per il metodo validate()
		 */
		function _validazioneRichiestaInserimentoProtocollo() {
			$("#richiestainserimentoprotocollo").validate({
				rules: {
					classificadocumento: "required",
					classdoc_filtro: "required",
					codiceregistrodocumento: "required",
					tipodocumento: "required",
					mittenteinterno: "required",
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
					tipofascicolonuovo: "required",
					descrizionefascicolonuovo: "required",
					numeroallegatinodescr: {
						required: true,
						range: [0, 0]
					},
					idtitolazione: "required",
					mezzo: "required",
					supporto: "required",
					codiceaoonuovo: "required",
					codiceaoo_filtro: "required",
					idunitaoperativamittente: "required",
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
					tipofascicolonuovo:"specificare il tipo del fascicolo",
					descrizionefascicolonuovo: "Specificare la descrizione del fascicolo",
					numeroallegatinodescr: {
						required: "Per tutti gli allegati deve essere valorizzata la descrizione",
						range: "Per tutti gli allegati deve essere valorizzata la descrizione"
					},
					idtitolazione: "Specificare la classifica",
					mezzo: "Specificare il mezzo",
					supporto: "Specificare il supporto",
					codiceaoonuovo:"Specificare il codice AOO",
					codiceaoo_filtro:"Specificare il codice AOO",
					idunitaoperativamittente: "Specificare l'unit&agrave; operativa mittente",
					strutturaonuovo:"Specificare la struttura",
					codiceufficio_filtro:"Specificare il codice ufficio",
					sottotipo:"Specificare il sottotipo",
					tipofirma: "Specificare il tipo firma",
					listafascicoli: "Specificare il fascicolo",
					uocompetenzaTxt: "Specificare l'unit&agrave; operativa di competenza"
				},
				errorPlacement: function (error, element) {
					error.insertAfter($(element));
					error.css("margin-right","5px");
					error.css("float", "right");
					error.css("vertical-align", "top");
				}
			});
			_getTipoWSDM();
				if (_tipoWSDM != "IRIDE" && _tipoWSDM != "JIRIDE") {
					$( "#mittenteinterno" ).rules( "remove" );
					
				}
				if (_tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "JDOC" && _tipoWSDM != "NUMIX") {
					$( "#mezzo" ).rules( "remove" );
					
				}
				if (_tipoWSDM == "JIRIDE") {
					$( "#annofascicolo" ).rules( "remove" );
					
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
				if (_tipoWSDM == "TITULUS") {
					if(!_classificaObbligatoria)
						$( "#classificafascicolonuovo" ).rules( "remove" );
				}
				
				if (_tipoWSDM == "JDOC") {
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


	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};
	
	function _waitgperm() {
		document.getElementById('bloccaScreen').style.visibility='visible';
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}
	
	function _nowaitgperm() {
		var timeout = null;
		timeout = setInterval(function() {
			document.getElementById('bloccaScreen').style.visibility='hidden';
			document.getElementById('wait').style.visibility='hidden';
			clearTimeout(timeout);	
	     }, 500);
	}
	
	function _creaTabellaDocumenti() {
		
		var _table = $('<table/>', {"id": "documenti", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
		
		var _tr = $('<tr/>', {"class": "intestazione"});
		_tr.append('<th/>', {"class": "codice"});
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		var _thck = $('<th/>', {"id": "arch","class": "ck"}); //12
		_tr.append(_thck);
		var _thck_cos = $('<th/>', {"id": "arch_cos","class": "ck"});
		_tr.append(_thck_cos);
								
		var _thead = $('<thead/>');
		_thead.append(_tr);

		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
		_tr2.append('<td/>', {"class": "codice"});
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
			
		var _tfoot = $('<tfoot/>');
		_tfoot.append(_tr2);

		_table.append(_thead);
		_table.append(_tbody);
		_table.append(_tfoot);
		
		$("#documentiContainer").append(_table);	
		
	}

	/*
	 * Popola la tabella con la lista dei documenti
	 */
	function _popolaDocumenti() {
		_waitgperm();
		var _visStatoSel = true;
		var _visStatoSel_cos = true;
		if(_integrazioneWSDM != '1'){
			_visStatoSel = false;
			$("#menuarchprot").hide();
			$("#pulsantearchprot").hide();
            _visStatoSel = false;
        }
        
        if(_integrazioneCOS != '1')
        {
            $("#menuexpcos").hide();
            $("#pulsanteexpCOS").hide();
            _visStatoSel_cos = false;
            
            
        }

		

		_creaTabellaDocumenti();
		
		var stipula=false;
		if($("#entita").val() == "G1STIPULA")
			stipula=true;
		
		_tableDocumenti = $('#documenti').removeAttr('width').DataTable( {
			"ajax": {
				"url": "pg/GetListaDocumenti.do",
				"data" : function (n) { 
					return {
						operation: $("#operation").val(),
						codgar: $("#codgar").val(),
						genere: $("#genere").val(),
						stipula: stipula,
						key1: $("#key1").val(),
						chiaveOriginale: $("#chiaveOriginale").val()
					};	
				},
				"complete": function() {
	            	_nowaitgperm();
	            }
			},
			
			"bAutoWidth": false,
			
			"columnDefs": [
				{	
					"data": "ARGOMENTO.value",
					"visible": false,
					"sTitle": "Argomento",
					"searchable": false,
					"targets": [ 0 ]
				},
				{	
					"data": "GRUPPO.value",
					"visible": true,
					"sTitle": "Tipologia",
					"sWidth": "100px",
					"sClass": "aligncenter",
					"searchable": false,
					"targets": [ 1 ],
				"render": function ( data, type, full, meta ) {
					var TipoDocDesc = "";
					
					switch(full.GRUPPO.value) {
					case 1: TipoDocDesc = "Documento del bando/avviso"; break;
					case 2:
						if($("#genere").val()=='10' || $("#genere").val()=='20'){
							TipoDocDesc = "Requisiti degli operatori"; break;
						}else{
							TipoDocDesc = "Requisiti dei concorrenti"; break;
						}
					case 3: 
						if($("#genere").val()=='10' || $("#genere").val()=='20'){
							TipoDocDesc = "Fac-simile documento richiesto agli operatori"; break;
						}else{
							TipoDocDesc = "Fac-simile documento richiesto ai concorrenti"; break;
						}
					case 4: TipoDocDesc = "Documento dell'esito"; break;
					case 5: TipoDocDesc = "Documento per la trasparenza"; break;
					case 6 : TipoDocDesc = "Documento dell'invito a presentare offerta"; break;
					case 10: TipoDocDesc = "Atto o documento art.29 c.1 DLgs.50/2016"; break;
					case 11: TipoDocDesc = "Documento allegato all'ordine di acquisto"; break;
					case 12: TipoDocDesc = "Documento dell'invito all'asta elettronica"; break;
					case 15: TipoDocDesc = "Delibera a contrarre o atto equivalente"; break;
					default:
						TipoDocDesc = full.GRUPPO.value; break;
					}
					
					if ( full.ARCHIVIATO.value == "1" ){
						TipoDocDesc = TipoDocDesc + "\r\n(ARCHIVIATO)";
					}else if ( full.ARCHIVIATO.value == "3" ){
						TipoDocDesc = TipoDocDesc + "\r\n(ANNULLATO DA OPERATORE)";
					}

					return TipoDocDesc;
				}

				},
				{	
					"data": "LOTTO.value",
					"visible": _lottoVisibile,
					"sTitle": "Lotto",
					"searchable": false,
					"sWidth": "100px",
					"targets": [ 2 ]
				},
				{
					"data": "CFDITTA.value",
					"visible": true,
					"sTitle": "Codice Fiscale Ditta",
					"targets": [ 3 ],
					"sWidth": "50px",
					"class" : "codfisc"
				},
				{
					"data": "DITTA.value",
					"visible": true,
					"sTitle": "Ditta",
					"targets": [ 4 ],
					"sWidth": "100px",
					"class" : "descr"
				},
				{
					"data": "DESCRIZIONE.value",
					"visible": true,
					"sTitle": "Descrizione",
					"targets": [ 5 ],
					"sWidth": "100px",
					"class" : "descr"
				},
				{
					"data": "IDPRG.value",
					"visible": false,
					"searchable": false,
					"targets": [ 6 ],
				},
				{
					"data": "IDDOCDIG.value",
					"visible": false,
					"searchable": false,
					"targets": [ 7 ],
				},
				{
					"data": "DIGNOMDOC.value",
					"visible": true,
					"sTitle": "Nome documento",
					"targets": [ 8 ],
					"sWidth": "100px",
					"class" : "descr",
					"render": function ( data, type, full, meta ) {
						var _span = $("<span/>");
						var _a = $("<a/>",{id: "nomedoc_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value ,href: "#", "text": full.DIGNOMDOC.value, "class": "link-generico"});
						_span.append(_a);
						return _span.html();
					}
				},
				{
					"data": "DATA.value",
					"visible": true,
					"sTitle": "Data",
					"targets": [ 9 ],
					"sWidth": "100px",
					"class" : "datadescr"
				},
				{
					"data": "STATO.value",
					"visible": _visStatoSel,
					"sTitle": "Stato",
					"targets": [ 10 ],
					"sWidth": "100px",
					"class" : "stato",
					"render": function ( data, type, full, meta ) {
						var statoDesc = "";
						if(full.IDWSDOC.value != null && full.STATO.value != 20 && full.STATO.value !=21 && full.STATO.value !=22 && full.STATO.value !=23 && full.STATO.value!=1){
							full.STATO.value = 3;
						}
							
						var _span = $("<span/>");
						switch(full.STATO.value) {
						case 1:
							statoDesc = "In archiviazione";
							_span.append(statoDesc);
							break;
						case 2:
						case 23:
							statoDesc = "Errore";
							var _a = $("<a/>",{id: "detterrore_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value ,href: "#", "text": "...", "class": "link-generico"});
							var _div = $("<div/>", {"text":full.ESITO.value});
							_div.css("display","none");
							_span.append(_div).append(statoDesc + "&nbsp;").append(_a);
							
						break;
						case 3:
							statoDesc = "Archiviato";
							_span.append(statoDesc);
							break;
						case 20:
							statoDesc = "Archiviato e associato a protocollo busta";
							_span.append(statoDesc);
							break;
						case 21:
							statoDesc = "Archiviato, associazione a protocollo busta fallita";
							var _a = $("<a/>",{id: "detterrore_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value ,href: "#", "text": "...", "class": "link-generico"});
							var _div = $("<div/>", {"text":full.ESITO.value});
							_div.css("display","none");
							_span.append(_div).append(statoDesc + "&nbsp;").append(_a);
							break;
						case 22:
							statoDesc = "Aggiunto in protocollo busta";
							_span.append(statoDesc);
							break;
						default:
							statoDesc = "Da archiviare";
							_span.append(statoDesc);
							break;
						}
							return _span.html();
					}

				},
                {
					"data": "STATO_COS.value",
					"visible": _visStatoSel_cos,
					"sTitle": "Stato",
					"targets": [ 11 ],
					"sWidth": "100px",
					"class" : "stato",
					"render": function ( data, type, full, meta ) {
						var statoDesc = "";
//						if(full.IDWSDOC.value != null){
//							full.STATO_COS.value = 3;
//						}
							
						var _span = $("<span/>");
						switch(full.STATO_COS.value) {
                                                case -1:
                                                case -2:
                                                    break;
						case 1:
							statoDesc = "In archiviazione";
							_span.append(statoDesc);
							break;
						case 2:
							statoDesc = "Errore";
							var _a = $("<a/>",{id: "detterrore_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value ,href: "#", "text": "...", "class": "link-generico"});
							var _div = $("<div/>", {"text":full.ESITO.value});
							_div.css("display","none");
							_span.append(_div).append(statoDesc + "&nbsp;").append(_a);
							
						break;
						case 3:
							statoDesc = "Archiviato";
							_span.append(statoDesc);
							break;
                        case 4:////trasferito in area FTP
                        	statoDesc = "Trasferito in area FTP";
                            _span.append(statoDesc);
                            break;
                        case 5://trasferito in area FTP con file di indice
                        	statoDesc = "Trasferito in area FTP, con indice";
                        	_span.append(statoDesc);
                            break;
                        case 6://trasferimento COS completato
                            statoDesc = "Archiviato";
                            _span.append(statoDesc);
                            break;
                        case 7://trasferimento COS fallito
                            statoDesc = "Errore in trasferimento in conservazione";
							var _a = $("<a/>",{id: "detterrore_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value ,href: "#", "text": "...", "class": "link-generico"});
							if(full.ESITO.value != null){ 
								var _div = $("<div/>", {"text":full.ESITO.value});
								_div.css("display","none");
								_span.append(_div).append(statoDesc + "&nbsp;").append(_a);
							} else{
								_span.append(statoDesc);
							}
                            break;
                        case 8://errore in fase di creazione dell'indice (doc. mancante)'
                            statoDesc = "Errore in creazione file di indice";
							var _a = $("<a/>",{id: "detterrore_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value ,href: "#", "text": "...", "class": "link-generico"});
							var _div = $("<div/>", {"text":full.ESITO.value});
							_div.css("display","none");
							_span.append(_div).append(statoDesc + "&nbsp;").append(_a);
                            break;
						default:
							statoDesc = "Da archiviare";
							_span.append(statoDesc);
							break;
						}
							return _span.html();
					}

				},
				{
					"data": "IDWSDOC.value",
					"visible": _visStatoSel,
					"targets": [ 12 ],
					"class" : "ck",
					"sWidth" : "70px",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "usr_ck_x_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value});
						//Si introduce il campo check nascoto per potere tenere traccia delle righe già processate per l'archiviazione.
						var _check1 = $("<input/>",{"type":"checkbox", "id": "usr_ck_processata_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value, "style":"display:none"});
						var _stato = $("<input/>",{"type":"hidden", "id": "stato_" + full.IDPRG.value + "_" + full.IDDOCDIG.value  , "value": + full.STATO.value });
						switch(full.STATO.value) {
						case 1:
							_check.prop("checked","checked");
							_check.prop("disabled","disabled");
							_div.append(_check);
							_check1.prop("checked","checked");
							_div.append(_check1);
							break;
						case 2:
						case 21:
						case 23:
							_div.append(_check);
							_div.append(_check1);
							break;
						case 3:
						case 20:
						case 22:
							break;
						default:
							_div.append(_check);
							_div.append(_check1);
							break;
						}
						if(trasmissioneAbilitata && data!=null){
							var _hid=$("<input/>",{"type":"hidden", "id": "IDWSDOC_hid_"+ data}).val(data);
							_div.append(_hid);
						}
						_div.append(_stato);
						return _div.html();
					}
				},
				
                {
					"data": "IDWSDOC.value",
					"visible": _visStatoSel_cos,
					"targets": [ 13 ],
					"class" : "ck",
					"sWidth" : "70px",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "cos_usr_ck_x_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value});
						//Si introduce il campo check nascoto per potere tenere traccia delle righe gia' processate per l'archiviazione.
						var _check1 = $("<input/>",{"type":"checkbox", "id": "cos_usr_ck_processata_" + full.PROVENIENZA.value + "_" + full.IDPRG.value + "_" + full.IDDOCDIG.value, "style":"display:none"});
						switch(full.STATO_COS.value) {
						case -1:
                            _div.append("<div><img title='Il file non \u00e8 trasferibile in conservazione: la data di pubblicazione non \u00e8 valorizzata' height='15' width='15' src='" + _contextPath + "/img/isquantimod.png'></div>");
                            break;
                        case -2:
                            _div.append("<div><img title='Il file non \u00e8 trasferibile in conservazione: non ha un formato valido' height='15' width='15' src='" + _contextPath + "/img/isquantimod.png'></div>");
                            break;	
						case 1:
							_check.prop("checked","checked");
							_check.prop("disabled","disabled");
							_div.append(_check);
							_check1.prop("checked","checked");
							_div.append(_check1);
							break;
						case 2:
						case 7:
						case 8:
							_div.append(_check);
							_div.append(_check1);
							break;
						case 3:
						case 4://caricato su sftp cos, ma in attesa della generazione del file
                        case 5: //caricato su sftp cos e file di indice generato
						case 6:
							break;
						default:
							_div.append(_check);
							_div.append(_check1);
							break;
						}

						return _div.html();
					}
				},
				
					{
					"data": "PROVENIENZA.value",
					"visible": false,
					"searchable": false,
					"targets": [ 14 ],
					"class" : "descr",
				}

	        ],
	        "language": {
				"sEmptyTable":     "Non ci sono documenti da archiviare",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ documenti",
				"sInfoEmpty":      "Non ci sono documenti da archiviare",
				"sInfoFiltered":   "(su _MAX_ documenti totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca documenti",
				"sZeroRecords":    "Non ci sono documenti da archiviare",
				"oPaginate": {
					"sFirst":      "<<",
					"sPrevious":   "<",
					"sNext":       ">",
					"sLast":       ">>"
				}
			},
			
			
			"initComplete": function (oSettings, jso) {
	 			var _iTotalRecords = oSettings.fnRecordsTotal();
				if (_iTotalRecords == 0) {
					$("#documenti tfoot").hide();
					$("#documenti_info").hide();
					$("#documenti_paginate").hide();
				}
			},
			
	        "pagingType": "full_numbers",
	        "lengthMenu": [[50, 70, 100], ["50 documenti", "70 documenti", "100 documenti"]],
	        "ordering": false,
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": false, "bSearchable": true },
			     { "bSortable": false, "bSearchable": true },
			     { "bSortable": false, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
				 { "bSortable": true, "bSearchable": true },
				 { "bSortable": true, "bSearchable": true },
				 { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": false, "bSearchable": true },
				 { "bSortable": false, "bSearchable": false }
			   ]
	    });
	
			$('#documenti tfoot td').eq(0).html( '<input class="search" size="10" type="text" placeholder="Ricerca tipologia "/>' );
			if($("#genere").val()=='3'){
				$('#documenti tfoot td').eq(1).html( '<input class="search" size="10" type="text" placeholder="Ricerca lotto "/>' );
				$('#documenti tfoot td').eq(2).html( '<input class="search" size="10" type="text" placeholder="Ricerca C.F. "/>' );
				$('#documenti tfoot td').eq(3).html( '<input class="search" size="10" type="text" placeholder="Ricerca ditta "/>' );
				$('#documenti tfoot td').eq(4).html( '<input class="search" size="10" type="text" placeholder="Ricerca descrizione "/>' );
				$('#documenti tfoot td').eq(5).html( '<input class="search" size="10" type="text" placeholder="Ricerca nome"/>' );
				$('#documenti tfoot td').eq(6).html( '<input class="search" size="10" type="text" placeholder="Ricerca data"/>' );
				$('#documenti tfoot td').eq(7).html( '<input class="search" size="10" type="text" placeholder="Ricerca stato"/>' );

			}else{
				$('#documenti tfoot td').eq(1).html( '<input class="search" size="10" type="text" placeholder="Ricerca C.F. "/>' );
				$('#documenti tfoot td').eq(2).html( '<input class="search" size="10" type="text" placeholder="Ricerca ditta "/>' );
				$('#documenti tfoot td').eq(3).html( '<input class="search" size="10" type="text" placeholder="Ricerca descrizione "/>' );
				$('#documenti tfoot td').eq(4).html( '<input class="search" size="10" type="text" placeholder="Ricerca nome"/>' );
				$('#documenti tfoot td').eq(5).html( '<input class="search" size="10" type="text" placeholder="Ricerca data"/>' );
				$('#documenti tfoot td').eq(6).html( '<input class="search" size="10" type="text" placeholder="Ricerca stato"/>' );
			}
			
			
			
			_tableDocumenti.columns().eq(0).each( function (colIdx) {
				$('input', _tableDocumenti.column(colIdx).footer()).on( 'keyup change', function () {
					_tableDocumenti.column(colIdx).search(this.value).draw();
				});
		    });

		
		var _center = $("<center/>");
		var _href = "<a href='javascript:_selezionaTutti();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
		var _href = _href + "&nbsp;";
		var _href = _href + "<a href='javascript:_deselezionaTutti();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
		_center.append(_href);
		_center.appendTo($("#arch"));

		var _center_cos = $("<center/>");
		var _href_cos = "<a href='javascript:_selezionaTutti_cos();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
		var _href_cos = _href_cos + "&nbsp;";
		var _href_cos = _href_cos + "<a href='javascript:_deselezionaTutti_cos();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
		_center_cos.append(_href_cos);
		_center_cos.appendTo($("#arch_cos"));
		
		$('#documenti thead th').eq(9).prop("title","Data");
		
		$("#documenti_filter").hide();

		$("body").on("click",'[id^="detterrore_"]', function(event ) {
			$("#DIV_DESCR_ERRORE").css('top',event.pageY);
			$("#DIV_DESCR_ERRORE").css('left',event.pageX);
			$("#DIV_DESCRIZIONE").css('width',$("#DIV_DESCR_ERRORE").width()- 20);
			$("#DIV_DESCRIZIONE").css('height',$("#DIV_DESCR_ERRORE").height() - 50);
			$("#DIV_DESCRIZIONE").css("background","#FFFFFF");
			$("#DIV_DESCR_ERRORE").show(200);
			$("#DIV_DESCRIZIONE").focus();
			var div = $(this).parent().find("div");
			$("#DIV_DESCRIZIONE").text(div.text()); 
		});
		
		$("body").on( "click", '[id^="nomedoc_"]', function() {
			var _id = 0;
			var _provenienza = 0;
			var _k1 = 0;
			var _k2 = 0;
			_id = $(this).prop("id");
			_provenienza = _id.substring(8,9);
			_k1 = _id.substring(10,12);
			_k2 = _id.substring(13);
			_text = $(this).prop("text");
			visualizzaFileAllegato(_provenienza,_k1,_k2,_text);
		});
		

		
	}
	

    function sendRequestCos(){
		$.ajax({
			type: "GET",
			dataType: "json",
			async: false,
			url: "pg/SetRichiestaArchiviazioneCOS.do",
			data : {
				codgar: $("#codgar").val()
			},
			
			complete: function() {
				_nowaitgperm();
				historyReload();
			},
			
			success: function(data){
				if (data) {
					_idRichiesta=data.idRichiesta;
			_tableDocumenti.$("tr").each(function () {
			var _ick = $(this).find('input:checked');
			if (_ick.size() > 0) {
				var _id = 0;
				var _x = 0;
				var _k1 = '';
				var _k2 = 0;
				var _provenienza = '';
				var _idwsdoc = '';
				
				_ick.each(function () {
					_id = $(this).prop("id");
					if (_id.substring(0,13) == 'cos_usr_ck_x_'){
						_x = 1;
						_provenienza = _id.substring(13,14);
						_k1 = _id.substring(15,17);
						_k2 = _id.substring(18);
					} 
				});
				
				if (_x == 1) {
					_idwsdoc = '1';
				} else {
					_idwsdoc = '2';
				}
									
				//si devono processare solo le richieste che non risultano gi� procesate
				if(!$('#cos_usr_ck_processata_' + _provenienza + "_" + _k1 + "_" + _k2).prop( "checked")){
					_waitgperm();
					$.ajax({
						type: "GET",
						dataType: "json",
						async: false,
						url: "pg/SetDocumentoArchiviato.do",
						data: "operation=INSERT&id=" + _id + "&k1=" + _k1+ "&k2=" + _k2 + "&provenienza=" + _provenienza  + "&idwsdoc=" + _idwsdoc + "&idRichiesta=" + _idRichiesta + "&cos=true",
						success: function() {
							$('#cos_usr_ck_processata_' + _provenienza + "_" + _k1 + "_" + _k2).prop("checked","checked");
						},
	
						error: function(e){
							alert("Errore nella richiesta di archiviazione del documento");
						},
						complete: function() {
							_nowaitgperm();
						}
					});
				}

			}
		});
				}
			},

			error: function(e){
				alert("Errore nella richiesta di archiviazione");
			},
		});
	}
	
	
	$('#menuarchprot, #pulsantearchprot').on("click",function() {
	
		var nSelected = 0;
		$( "input[id^='usr_ck_x_']" ).each( function( index ) {
			if($( this ).prop( "checked") && !$( this ).prop( "disabled")){
				nSelected= nSelected + 1;
			}
		});

		if(nSelected > 0){
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
			height:650,
			title: 'Trasferisci al documentale',
			buttons: {
			"Conferma": {
				text: "Conferma",
				id: "archiviaWsdmButton",
				click: _archInProt 
			},
			"Annulla": function() {
					$( this ).dialog( "close" );
				}
			 }
			};
		
			$("#mascheraParametriWSDM").dialog(opt).dialog("open");
			
			/*
			 * Avvio all'apertura della maschera del popolamento 
			 * della lista degli elementi documentali del fascicolo
			 */
			setTimeout(function(){
				_validazioneWSLogin();
				_validazioneRichiestaInserimentoProtocollo();
				_getRichiesta();
				_controlloPresenzaFascicolazione();
				
				if(_tipoWSDM != "SMAT" )
					_controlloFascicoliAssociati();
				_inizializzazioni();
				 if(_fascicolazioneAbilitata==1 && _tipoWSDM == "JPROTOCOL"){
					 $("#sezionedatidocumentale").hide();
				 }
				
				if(_tipoWSDM == "ENGINEERINGDOC")
					$("#sezionedatidocumentale").hide();	
				
				if (_tipoWSDM == "JIRIDE"){
					_popolaTabellato("tipocollegamento","tipocollegamento");
					_controlloAssociaDocumentiProtocollo();
				}
				if (_tipoWSDM == "LAPISOPERA"){
					$("#classificadocumento").show();
					$("#classificadocumento").closest('tr').show();
				}
			}, 800);
		
		
		}else{
			alert("Selezionare almeno un documento");
		}
		
    });
		
	$('#menuassociadoc').on("click",function() {
		var comando = "href=gare/commons/popup-associa-documentiDitta-protocollo.jsp?codgar=" + $("#codgar").val() + "&genere=" + $("#genere").val();
		if($("#idconfi").val()){
			comando=comando+ "&idconfi=" + $("#idconfi").val();
		}
		openPopUpCustom(comando, "exportDocumenti", 700, 350, "yes", "yes");
	});
	
	$('#menuexpcos, #pulsantearchcos').on("click",function() {
	
		var nSelected = 0;
		$( "input[id^='cos_usr_ck_x_']" ).each( function( index ) {
			if($( this ).prop( "checked") && !$( this ).prop( "disabled")){
				nSelected= nSelected + 1;
			}
		});
		var controlloGaraOk = true;
		var confermaButton = true;
		var controlloConfigOk = true;
		var codgar = $("#codgar").val();
		$.ajax({
				type: "GET",
				dataType: "json",
				async: false,
				url: "pg/CheckConfigurazioneArchiviazioneCOS.do",
	    		data: "codgar=" + codgar,
				complete: function() {
	            	_nowaitgperm();
	            },
				
				success: function(data){
					if (data) {
						
						result=data.result;
						if(result != true){
							confermaButton = false;
							$("#elencoErroriGara").empty();
							$("#elencoErroriConfig").empty();
							var errorsconfig = data.errorsconfig;
							var errorsgara = data.errorsgara;
							if(errorsconfig.length > 0){
								controlloConfigOk = false;
								$.each (errorsconfig, function (index) {
									$("#elencoErroriConfig").append("<li>" + errorsconfig[index] + "</li>");
								});
							}
							if(errorsgara.length > 0){
								controlloGaraOk = false;
								$.each (errorsgara, function (index) {
									$("#elencoErroriGara").append("<li>" + errorsgara[index] + "</li>");
								});
							}
						}
		        	}
				},
	
				error: function(e){
					alert("Errore nella richiesta di archiviazione");
				},
			});
		
		if(nSelected > 0){
			if(confermaButton){
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
					$("#messaggioSuccess").show();
					$("#messaggioErrorGara").hide();
					$("#messaggioErrorConfig").hide();
				},
				autoOpen: false,
				modal: true,
				width: 550,
				height:250,
				title: 'Trasferimento a conservazione digitale',
				buttons: {
					"Conferma" : sendRequestCos,
					"Annulla": function() {
						$( this ).dialog( "close" );
					}
				 }
				};
				$("#mascheraConfermaCOS").dialog(opt).dialog("open");
			}else{
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
					$("#messaggioSuccess").hide();
					if(!controlloConfigOk){
						$("#messaggioErrorConfig").show();
						$("#elencoErroriConfig").show();
					}else{
						$("#messaggioErrorConfig").hide();
						$("#elencoErroriConfig").hide();
					}
					if(!controlloGaraOk){
						$("#messaggioErrorGara").show();
						$("#elencoErroriGara").show();
					}else{
						$("#messaggioErrorGara").hide();
						$("#elencoErroriGara").hide();
					}
				},
				autoOpen: false,
				modal: true,
				width: 550,
				height:350,
				title: 'Trasferimento a conservazione digitale',
				buttons: {
					"Annulla": function() {
						$( this ).dialog( "close" );
					}
				 }
				};
				$("#mascheraConfermaCOS").dialog(opt).dialog("open");
			}
		}else{
			alert("Selezionare almeno un documento");
		}
		
    });
		
		
		
	$('#menuexpdoc, #pulsanteexpdoc').on("click", function() {
		var comando = "href=gare/commons/popup-richiesta-export-documenti.jsp?codgar=" + $("#codgar").val() + "&genere=" + $("#genere").val() + "&codice=" + $("#codice").val();
		comando += "&entita=" + $('#entita').val();
		if(_isStipula) 
			comando+="&idstipula=" + $('#key1').val();
		openPopUpCustom(comando, "exportDocumenti", 700, 350, "yes", "yes");
	});
	
	function _archInProt() {
		
		var _idRichiesta="";
		_setWSLogin();
		if((_tipoWSDM=="ARCHIFLOW" || _tipoWSDM=="NUMIX") && (_ufficioIntestatario==null || _ufficioIntestatario=="") ){
			alert("Non e' possibile procedere poiche' non e' valorizzato il codice della stazione appaltante");
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
		
		if(_tipoWSDM=="JIRIDE" && _associazioneDocumentiProtocolloAbilitata == 1 && ($("#tipocollegamento option:selected").val()==null || $("#tipocollegamento option:selected").val()=="")){
			alert("Non e' possibile procedere poiche' non e' configurato il tabellato 'Tipo collegamento' dell'integrazione WSDM");
			return;
		}
		
		if ($("#richiestawslogin").validate().form() && $("#richiestainserimentoprotocollo").validate().form()) {
			_waitgperm();
	        var classificadocumento=$("#classificadocumento").val();
	        if(_tipoWSDM=="INFOR")
	        	classificadocumento="";
			$.ajax({
				type: "GET",
				dataType: "json",
				async: false,
				url: "pg/SetRichiestaArchiviazione.do",
	    		data : {
	    			codgar: $("#codgar").val(),
	    			codice: $("#codice").val(),
					username: $("#username").val(),
					password: $("#password").val(),
					ruolo: $("#ruolo option:selected").val(),
					nome : $("#nome").val(),
					cognome : $("#cognome").val(),
					codiceuo : $("#codiceuo option:selected").val(),
					idutente : $("#idutente").val(),
					idutenteunop : $("#idutenteunop").val(),
					classificadocumento : classificadocumento,
					tipodocumento : $("#tipodocumento").val(),
					descrizionedocumento : $("#descrizionedocumento").val(),
					oggettodocumento : $("#oggettodocumento").val(),
					mittenteinterno : $("#mittenteinterno").val(),
					inout : $("#inout").val(),
					inserimentoinfascicolo : $("#inserimentoinfascicolo option:selected").val(),
					codicefascicolo : $("#codicefascicolo").val(),
					annofascicolo : $("#annofascicolo").val(),
					numerofascicolo : $("#numerofascicolo").val(),
					oggettofascicolo : $("#oggettofascicolonuovo").val(),
					classificafascicolo : $("#classificafascicolonuovo").val(),
					descrizionefascicolo : $("#descrizionefascicolonuovo").val(),
					codiceregistrodocumento : $("#codiceregistrodocumento").val(),
					idtitolazione : $("#idtitolazione option:selected").val(),
					idunitaoperativadestinataria : $("#idunitaoperativadestinataria").val(),
					tipowsdm : _tipoWSDM,
					delegainviomail : _delegaInvioMailDocumentaleAbilitata,
					servizio: $("#servizio").val(),
					genere: $("#genere").val(),
					mezzo: $("#mezzo").val(),
					codiceaoonuovo: $("#codiceaoonuovo option:selected").val(),
					codiceufficionuovo: $("#codiceufficionuovo option:selected").val(),
					struttura: $("#strutturaonuovo option:selected").val(),
					isRiservatezzaAttiva: $("#isRiservatezzaAttiva").val(),
					tipofascicolo: $("#tipofascicolonuovo option:selected").val(),
					supporto: $("#supporto option:selected").val(),
					classificadescrizione: $("#classificadescrizione").val(),
					voce: $("#voce").val(),
					codiceaoodes : $("#codiceaoonuovo option:selected").text(),
					codiceufficiodes : $("#codiceufficionuovo option:selected").text(),
					idconfi: $("#idconfi").val(),
					RUP:$("#RUP").val(),
					nomeRup: $("#nomeRup").text(),
					acronimoRup: $("#acronimoRup").text(),
					sottotipo: $("#sottotipo option:selected").val(),
					idunitaoperativamittente : $("#idunitaoperativamittente option:selected").val(),
					idunitaoperativamittenteDesc: $( "#idunitaoperativamittente option:selected" ).text(),
					tipofirma:$("#tipofirma option:selected").val(),
					key1:$("#key1").val(),
					entita: $("#entita").val(),
					uocompetenza:$("#uocompetenza").val(),
					uocompetenzadescrizione:$("#uocompetenzadescrizione").val()
	    		},
	    		
				complete: function() {
	            	_nowaitgperm();
	            },
				
				success: function(data){
					if (data) {
						_idRichiesta=data.idRichiesta;
		        	}
				},
	
				error: function(e){
					alert("Errore nella richiesta di archiviazione");
				},
			});
	
	
			_tableDocumenti.$("tr").each(function () {
				var _ick = $(this).find('input:checked');
				if (_ick.size() > 0) {
					var _id = 0;
					var _x = 0;
					var _k1 = '';
					var _k2 = 0;
					var _provenienza = '';
					var _idwsdoc = '';
					
					_ick.each(function () {
						_id = $(this).prop("id");
						if (_id.substring(0,9) == 'usr_ck_x_'){
							_x = 1;
							_provenienza = _id.substring(9,10);
							_k1 = _id.substring(11,13);
							_k2 = _id.substring(14);
						} 
					});
					
					if (_x == 1) {
						_idwsdoc = '1';
					} else {
						_idwsdoc = '2';
					}
					//si devono processare solo le richieste che non risultano già procesate
					if(!$('#usr_ck_processata_' + _provenienza + "_" + _k1 + "_" + _k2).prop( "checked")){
						var _stato = $('#stato_' + _k1 + "_" + _k2).val();
						_waitgperm();
				        $.ajax({
							type: "GET",
							dataType: "json",
							async: false,
							url: "pg/SetDocumentoArchiviato.do",
							data: "operation=INSERT&id=" + _id + "&k1=" + _k1+ "&k2=" + _k2 + "&provenienza=" + _provenienza  + "&idwsdoc=" + _idwsdoc + "&idRichiesta=" + _idRichiesta + "&stato=" + _stato,
							success: function() {
								$('#usr_ck_processata_' + _provenienza + "_" + _k1 + "_" + _k2).prop("checked","checked");
				            },
		
							error: function(e){
								alert("Errore nella richiesta di archiviazione del documento");
							},
							complete: function() {
				            	_nowaitgperm();
				            }
						});
					}
				
					
	
				}
			});
			
			$("#mascheraParametriWSDM").dialog( "close" );
			
			_tableDocumenti.destroy(true);
			_popolaDocumenti();
		}

    };	
	
  //Per TITULUS alla variazione della login si deve caricare il tabellato del codice AOO
	$('#username').on("change", function() {
		if (_tipoWSDM == "TITULUS"){
			caricamentoCodiceAooTITULUS();
			_popolaTabellatoClassificaTitulus();
		}else  if (_tipoWSDM == "JIRIDE"){
			caricamentoStrutturaJIRIDE();
		}else if (_tipoWSDM == "NUMIX"){
			caricamentoClassificaNumix();
		}
    });
	
	$('#password').on("change", function() {
		if (_tipoWSDM == "TITULUS"){
			caricamentoCodiceAooTITULUS();
			_popolaTabellatoClassificaTitulus();
		}else if (_tipoWSDM == "NUMIX"){
			caricamentoClassificaNumix();
		}
    });
	
	 $('#codiceaoonuovo').on("change", function() {
			if (_tipoWSDM == "TITULUS"){
				caricamentoUfficioTITULUS();
			}
	    });
	 
	$('#idunitaoperativamittente').on("change", function() {
		if (_tipoWSDM == "PRISMA" || _tipoWSDM == "INFOR"){
			$('#idunitaoperativadestinataria').val($('#idunitaoperativamittente').val());
		}
    });
	
	$('#ruolo').on("change", function() {
		if (_tipoWSDM == "JIRIDE"){
			caricamentoStrutturaJIRIDE();
		}
    });
    
	$('#codicefascicolo').on("change", function() {
		if (_tipoWSDM == "ARCHIFLOWFA"){
			gestionemodificacampofascicolo();
		}
	});
	
	$('#annofascicolo').change(function() {
				if (_tipoWSDM == "ITALPROT"){
					$('#listafascicoli').empty();
					$('#codicefascicolo').val(); 
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


	/*
	 * Lettura dei parametri per una singola richiesta.
	 * Utilizzata per popolare i dati generali della richiesta,
	 */
	function _getRichiesta() {
				
		var servizio = $("#servizio").val();
		
		_getTipoWSDM();
		if (_tipoWSDM == "IRIDE" ||_tipoWSDM == "JIRIDE") {
			$("#codiceregistrodocumento").hide();
			$("#codiceregistrodocumento").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
			//$("#idunitaoperativadestinataria").hide();
			//$("#idunitaoperativadestinataria").closest('tr').hide();
		}
		
		_controlloDelegaInvioMailAlDocumentale();
		
		if (_tipoWSDM == "PALEO") {
			$("#classificadocumento").hide();
			$("#classificadocumento").closest('tr').hide();
			$("#tipodocumento").hide();
			$("#tipodocumento").closest('tr').hide();
			$("#mittenteinterno").hide();
			$("#mittenteinterno").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
			//$("#idunitaoperativadestinataria").hide();
			//$("#idunitaoperativadestinataria").closest('tr').hide();
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
		}
		
		if (_tipoWSDM == "ENGINEERINGDOC") {
			$("#classificadocumento").hide();
			$("#classificadocumento").closest('tr').hide();
			$("#codiceregistrodocumento").hide();
			$("#codiceregistrodocumento").closest('tr').hide();
			$("#tipodocumento").hide();
			$("#tipodocumento").closest('tr').hide();
			$("#mittenteinterno").hide();
			$("#mittenteinterno").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
		}
		
		
		if ((_tipoWSDM != "JIRIDE"  && _tipoWSDM != "ARCHIFLOW") || (_tipoWSDM == "JIRIDE" && _delegaInvioMailDocumentaleAbilitata != 1) || (_tipoWSDM == "ARCHIFLOW" && _delegaInvioMailDocumentaleAbilitata != 1)) {
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
			$('#indirizzomittente option').eq(0).prop('selected', true);
		}
		
		if (_tipoWSDM == "TITULUS" || _tipoWSDM == "SMAT")
			$("#sezionedatidocumentale").hide();
		
		if(_tipoWSDM == "PRISMA" || _tipoWSDM == "INFOR" || _tipoWSDM == "DOCER"){
			//$("#idunitaoperativamittente").hide();
			$("#idunitaoperativamittente").closest('tr').show();
		}
		
		
		$('#inserimentoinfascicolo').on("change", function() {
			_gestioneInserimentoInFascicolo();
		});
		
		$('#classificafascicolonuovo').on("change", function() {
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
		
	}
	
	function _getTrasferisciDocumentaleWSDM(){
		var codgar = $("#codgar").val();
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
			url: "pg/GetTrasferisciDocumentaleWSDM.do",
			data: {
				codgar:codgar,
				idconfi:idconfi
			},
			success: function(data){
				if (data) {
					$.map( data, function( item ) {
						_integrazioneWSDM = item[0];
						_integrazioneCOS = item[1];
					});
	        	}
			},
			error: function(e){
				_integrazioneWSDM;
				_integrazioneCOS;
			}
		});
	}

	
  function _selezionaTutti() {
   $("input[id^='usr_ck_x_']").prop("checked","checked");
  }

  function _deselezionaTutti() {
	$( "input[id^='usr_ck_x_']" ).each( function( index ) {
			if ($( this ).prop( "disabled")) {
				;
			}else{
				$( this ).prop( "checked",false)
			}
	});
  }
  
  function _selezionaTutti_cos() {
	   $("input[id^='cos_usr_ck_x_']").prop("checked","checked");
	  }

	  function _deselezionaTutti_cos() {
		$( "input[id^='cos_usr_ck_x_']" ).each( function( index ) {
				if ($( this ).prop( "disabled")) {
					;
				}else{
					$( this ).prop( "checked",false)
				}
		});
	  }
  
  	  
	  function _apriDescrErrore(provenienza) {
		
		$("#DIV_DESCR_ERRORE").show(200);
		$("#DIV_DESCRIZIONE").focus();
		$("#DIV_DESCRIZIONE").prop("text","");
		$("#DIV_DESCRIZIONE").prop("text",provenienza);

		
  }

  function visualizzaFileAllegato(provenienza,idprg,iddocdig,dignomdoc) {
		var contextPath = $("#contextPath").val();
		if(provenienza == '4' && !($("#documentiAssociatiDB").val() == '1')){
				var href = contextPath + "/DocumentoAssociato.do?"+csrfToken+"&metodo=download";
				document.location.href=href+"&id=" + iddocdig;
		}else{
			digitalSignatureWsCheck: $("#digitalSignatureWsCheck").val();
			
			switch(digitalSignatureWsCheck.value)
			{
				case "0":
					var vet = dignomdoc.split(".");
					var ext = vet[vet.length-1];
					ext = ext.toUpperCase();
					if(ext=='P7M' || ext=='TSD'){
						var href = "href=gene/system/firmadigitale/verifica-firmadigitale-popUp.jsp";
						href += "&idprg=" + idprg;
						href += "&iddocdig=" + iddocdig;
							
						openPopUpCustom(href, "DownloadP7M", 900, 550, "yes", "yes");
						
					}else{
						var href = contextPath + "/pg/VisualizzaFileAllegato.do";
						document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
					}
					break;
				default:
					var vet = dignomdoc.split(".");
					var ext = vet[vet.length-1];
					ext = ext.toUpperCase();
					if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
						var href = "href=gene/system/firmadigitale/verifica-firmadigitale-popUp.jsp";
						href += "&idprg=" + idprg;
						href += "&iddocdig=" + iddocdig;
							
						openPopUpCustom(href, "DownloadP7M", 900, 550, "yes", "yes");
					}else{
						if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verrà apportata alla copia locale ma non all\'originale. Continuare?"))
						{
							var href = contextPath+"/VisualizzaFileDIGOGG.do";
							document.location.href = href+"?"+csrfToken+"&c0acod=" + c0acod + "&dignomdoc=" + encodeURIComponent(dignomdoc);
						}
					}
					break;
			}
				
			
		}
		
 }
  
  

