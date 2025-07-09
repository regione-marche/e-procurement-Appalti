/*
*	Gestione del fascicolo e dei documenti protocollati e non
*
*/
var fascicoloAssociato = false;
var sbiancaDatiFascicolo = false;
var _skipGetWSDMFascicolo = false;

$(window).ready(function (){
	/*
	 * Gestione dell'attesa
	 */
	_wait();

	/*
	 * Istanzia tabs
	 */
	$('#tabs').easytabs();
	
	
    /*
     * Gestione utente ed attributi per il collegamento remoto
     */
	_getWSTipoSistemaRemoto();
	_popolaTabellato("ruolo","ruolo");
	_popolaTabellato("codiceuo","codiceuo");
	_getWSLogin();
	_gestioneWSLogin();
	
	
	/*
	 * Applica le regole di controllo/validazione ed
	 * avvia la lettura del fascicolo
	 */
	setTimeout(function(){
		_validateParametriRichiestaFascicolo();
		_caricaWSDM();
	}, 800);
	
	
	
	
	/*
	 * Definizione regole di controllo sul form
	 * di richiesta dei dati del fascicolo
	 */
	function _validateParametriRichiestaFascicolo() {
		$("#parametririchiestafascicolo").validate({
			rules: {
				username: "required",
				password: "required",
				ruolo: "required",
				ruolovisualizza: "required",
				nome: "required",
				cognome: "required",
				codiceuo: "required",
				codiceuovisualizza: "required",
				idutente: "required",
				idutenteunop: "required",
				codicefascicolo: {
					"required": function() {
						return $('#tiposistemaremoto').val() == 'PALEO' || $('#tiposistemaremoto').val() == 'ARCHIFLOW';
			        }
			    },	
			    annofascicolo: {
					"required": function() {
						return $('#tiposistemaremoto').val() == 'IRIDE' || $('#tiposistemaremoto').val() == 'ENGINEERING' || $('#tiposistemaremoto').val() == 'HALLEY';
			        }
			    },	
			    numerofascicolo: {
					"required": function() {
						return $('#tiposistemaremoto').val() == 'IRIDE' || $('#tiposistemaremoto').val() == 'JIRIDE' || $('#tiposistemaremoto').val() == 'ENGINEERING' || $('#tiposistemaremoto').val() == 'HALLEY';
			        }
			    }	
			},
			messages: {
				username: "Specificare l'utente",
				password: "Specificare la password",
				ruolo: "Specificare il ruolo",
				ruolovisualizza: "Specificare il ruolo",
				nome: "Specificare il nome",
				cognome: "Specificare il cognome",
				codiceuo: "Specificare il codice dell'unit&agrave organizzativa",
				codiceuovisualizza: "Specificare il codice dell'unit&agrave organizzativa",
				idutente: "Specificare l'identificativo dell'utente",
				idutenteunop: "Specificare l'identificativo dell'unit&agrave; operativa",
				codicefascicolo: "Specificare il codice del fascicolo",
				annofascicolo: "Specificare l'anno",
				numerofascicolo: "Specificare il numero"	
			},
			errorPlacement: function (error, element) {
				error.insertAfter($(element));
				error.css("margin-right","5px");
				error.css("float", "right");
				error.css("vertical-align", "top");
			}
		});
	}
    
	

	
	
	/*
	 * Avvia il caricamento dei dati del fascicolo
	 */
	function _caricaWSDM() {
		_tipoWSDM = $('#tiposistemaremoto').val();
		
		_getWSFascicolo(_tipoWSDM);
		_gestioneWSFascicolo();		
		
		_isWSDocumentoPopolato();
		if (_tableDocumentiFascicolo != null) {
			_tableDocumentiFascicolo.destroy(true);
		}

		$("#wsdmChiudiFascicolo").hide();
				
				
		$("#oggettofascicolo").text("");
		if($('#tiposistemaremoto').val()!='DOCER' && $('#tiposistemaremoto').val()!='LAPISOPERA' && $('#tiposistemaremoto').val()!='HALLEY')
			$("#classificafascicolodescrizione").text("");
		$("#descrizionefascicolo").text("");
		
		var codice = $("#codicefascicolo").val();
		if(codice!=null && codice!="")
			fascicoloAssociato = true;
		
		if($('#tiposistemaremoto').val()=='ARCHIFLOW'){
			$("#classificafascicolodescrizione").hide();
			$("#classificafascicolodescrizione").closest('tr').hide();
			$("#annofascicolo").hide();
			$("#numerofascicolo").hide();
		}else if($('#tiposistemaremoto').val()=='PRISMA'){
			$("#descrizionefascicolo").hide();
			$("#descrizionefascicolo").closest('tr').hide();
		}else if($('#tiposistemaremoto').val()=='JPROTOCOL'){
			$("#codicefascicolo").closest('tr').hide();
			$("#codicefascicolo").hide();
			$("#oggettofascicolo").closest('tr').hide();
			$("#oggettofascicolo").hide();
			$("#classificafascicolodescrizione").closest('tr').hide();
			$("#classificafascicolodescrizione").hide();
			$("#descrizionefascicolo").closest('tr').hide();
			$("#descrizionefascicolo").hide();
		}else if($('#tiposistemaremoto').val()=='DOCER'){
			$("#codicefascicolo").closest('tr').hide();
			$("#codicefascicolo").hide();
			$("#oggettofascicolo").closest('tr').hide();
			$("#oggettofascicolo").hide();
			$("#descrizionefascicolo").closest('tr').hide();
			$("#descrizionefascicolo").hide();
			
		}else if($('#tiposistemaremoto').val()=='LAPISOPERA'){
			$("#linkleggiDatiFascicolo").show();
			$("#descrizionefascicolo").closest('tr').hide();
			$("#descrizionefascicolo").hide();
			$("#oggettofascicolo").closest('tr').hide();
			$("#oggettofascicolo").hide();
		}else if($('#tiposistemaremoto').val()=='HALLEY'){
			_controlloGestioneStrutturaCompetente();
			$("#sezioneclassificafascicolo").show();
			$("#classificafascicolonuovoselect").empty();
			_popolaTabellato("classificafascicolo", "classificafascicolonuovoselect");
			var classifica = $("#classificafascicolodescrizione").text();
			$("#classificafascicolonuovoselect option[value='"+classifica+"']").attr("selected", "selected");
			_setDescrizioneCodiceTabellato("classificafascicolo", classifica, "classificafascicolo", 2);
			$("#classificafascicolodescrizione").closest('tr').hide();
		}

		if($('#tiposistemaremoto').val()=='HALLEY' && (_gestioneStrutturaCompetente == 1)){
			$("#sezionestruttura").show();
			$("#strutturaonuovoselect").empty();
			_popolaTabellato("struttura", "strutturaonuovoselect");
			var struttura = $("#strutturaonuovo option").val()
			$("#strutturaonuovoselect option[value="+struttura+"]").attr("selected", "selected");
			_setDescrizioneCodiceTabellato("struttura", struttura, "struttura", 2);
		} else {
			$("#sezionestruttura").remove();
		}

		if(fascicoloAssociato && isWSDocumentoPopolato && $('#tiposistemaremoto').val()=='PALEO'){
			$("#wsdmChiudiFascicolo").show();
		}
		
		//Se non c'è il fascicolo o il fasciolo è presente ma non ci sono elementi documentali
		//è presente la funzione "Associa fascicolo esistente", altrimenti la funzione
		//"Imposta credenziali"
		if(!fascicoloAssociato || (fascicoloAssociato && !isWSDocumentoPopolato)){
			if($("#autorizzatoModifiche").val() != "2" && $("#autorizzatoAssociaFascicolo").val() == "true" && $('#tiposistemaremoto').val()!='ENGINEERINGDOC' && $('#tiposistemaremoto').val()!='JPROTOCOL' 
				&& $('#tiposistemaremoto').val()!='DOCER' && $('#tiposistemaremoto').val()!='LAPISOPERA'){
				$("#wsdmModificaPulsante").show();
				$("#wsdmModificaMenu").show();
			}else{
				$("#wsdmModificaPulsante").hide();
				$("#wsdmModificaMenu").hide();
			}
			$("#wsdmImpostaCredenziali").hide();
		}else{
			$("#wsdmModificaPulsante").hide();
			$("#wsdmModificaMenu").hide();
			if(_logincomune!=1)
				$("#wsdmImpostaCredenziali").show();
			else
				$("#wsdmImpostaCredenziali").hide();
		}
		
		if ($("#parametririchiestafascicolo").validate().form()) {
			var anno = $("#annofascicolo").val();
			var numero = $("#numerofascicolo").val();
			
			if (codice != null || (anno != null && numero != null)) {
				if(!_skipGetWSDMFascicolo) {
					_getWSDMFascicolo(true, 600, true);
				}
				if($('#tiposistemaremoto').val()=='ARCHIFLOW'){
					$("#spanannofascicolo").show();
					$("#spannumerofascicolo").show();
				}
			} else {
				_nowait();
			}
		} else {
			_nowait();
			alert("Non esiste alcun fascicolo associato. I dati del fascicolo non sono valorizzati, di conseguenza non e' possibile ottenere la lista dei suoi elementi documentali.")
		}
		
		if($('#tiposistemaremoto').val()=='JIRIDE'){
			_controlloGestioneStrutturaCompetente();
		}
	}
	
	
	/*
	 * Eventi
	 */
	$('#wsdmSalvaPulsante, #wsdmSalvaMenu').click(function() {
		$('#documentifascicolomessaggio').text("");
		$('#documentifascicolomessaggio').hide();
		_erroreLetturaFascicolo=false;
		if($('#tiposistemaremoto').val()=="JIRIDE" && _gestioneStrutturaCompetente == 1){
			_getWSDMStruttura();
			if(_erroreLetturaStruttura){
				sbiancaDatiFascicolo = true;
				return;
			}
			if(_strutturaDaServizio == null || _strutturaDaServizio == ""){
				alert("Non e' possibile procedere con l'associazione. Il fascicolo selezionato non ha la struttura competente valorizzata.");
				sbiancaDatiFascicolo = true;
				return;
			}
			
		}
		if($('#tiposistemaremoto').val()=="HALLEY" && _gestioneStrutturaCompetente == 1){
			$("#strutturaonuovo").val($("#strutturaonuovoselect").val())
			if($("#strutturaonuovo").val() == null || $("#strutturaonuovo").val() == ""){
				alert("Non e' possibile procedere con l'associazione. Valorizzare la struttura competente.");
				sbiancaDatiFascicolo = true;
				return;
			}
		}

		sbiancaDatiFascicolo = false;
		if($("#parametririchiestafascicolo").validate().form()){
			_wait();
			_getWSDMFascicolo(false,600,true);//verifico se esiste il fascicolo!
			if(_erroreLetturaFascicolo) {
				sbiancaDatiFascicolo = true;
				return;
			}
			$("body").off("click", "#documentifascicolo tbody tr");
			_removeTabsDettaglioDocumento();
			$("#wsdmSalvaPulsante").hide();
			$("#wsdmAnnullaPulsante").hide();
			if($('#tiposistemaremoto').val()!="ENGINEERING" && $('#tiposistemaremoto').val()!="JPROTOCOL"){
				$("#wsdmModificaPulsante").show();
				$("#wsdmModificaMenu").show();
			}
			$("#wsdmSalvaMenu").hide();
			$("#wsdmAnnullaMenu").hide();
			$("#alinkHelpPagina").show();
			$("#alinkIndietro").show();
			_setWSLogin();
			_setWSFascicolo();
			$("#modoapertura").val("VISUALIZZA");
			_getWSLogin();
			_gestioneWSLogin();
			
			_caricaWSDM();
		}
    });
	
	$('#wsdmAnnullaPulsante, #wsdmAnnullaMenu').click(function() {
		_wait();
		$("body").off("click", "#documentifascicolo tbody tr");
		_removeTabsDettaglioDocumento();
		$("#wsdmSalvaPulsante").hide();
		$("#wsdmAnnullaPulsante").hide();
		if($('#tiposistemaremoto').val()!="ENGINEERINGDOC" && $('#tiposistemaremoto').val()!="JPROTOCOL"){
			$("#wsdmModificaPulsante").show();
			$("#wsdmModificaMenu").show();
		}
		$("#wsdmSalvaMenu").hide();
		$("#wsdmAnnullaMenu").hide();
		$("#alinkHelpPagina").show();
		$("#alinkIndietro").show();
		$("#modoapertura").val("VISUALIZZA");
		if(sbiancaDatiFascicolo){
			$("#codicefascicolo").val("");
			$("#annofascicolo").val("");
			$("#numerofascicolo").val("");
		}
		_getWSLogin();
		_gestioneWSLogin();
		_caricaWSDM();
    });
	
	$('#wsdmModificaPulsante, #wsdmModificaMenu').click(function() {
		if ($("#tiposistemaremoto").val() == "IRIDE" || $("#tiposistemaremoto").val() == "JIRIDE") {
			$("#codicefascicolo").val("");
		}
		_wait();
		$("body").off("click", "#documentifascicolo tbody tr");
		_removeTabsDettaglioDocumento();
		$("#wsdmSalvaPulsante").show();
		$("#wsdmAnnullaPulsante").show();
		$("#wsdmModificaPulsante").hide();
		$("#wsdmSalvaMenu").show();
		$("#wsdmAnnullaMenu").show();
		$("#wsdmModificaMenu").hide();	
		$("#alinkHelpPagina").hide();
		$("#alinkIndietro").hide();
		$("#modoapertura").val("MODIFICA");
		_gestioneWSLogin();
		_gestioneWSFascicolo();
		_nowait();
    });
	
	$('#wsdmImpostaCredenziali').click(function() {
		_wait();
		$("body").off("click", "#documentifascicolo tbody tr");
		_removeTabsDettaglioDocumento();
		$("#wsdmSalvaPulsante").show();
		$("#wsdmAnnullaPulsante").show();
		$("#wsdmImpostaCredenziali").hide();
		$("#wsdmSalvaMenu").show();
		$("#wsdmAnnullaMenu").show();
		$("#alinkHelpPagina").hide();
		$("#alinkIndietro").hide();
		$("#modoapertura").val("MODIFICA_LOGIN");
		_gestioneWSLogin();
		_gestioneWSFascicolo();
		_nowait();
    });

	$('#wsdmChiudiFascicolo').on("click",function() {
		$("#sezioneErrori").hide();
		var opt = {
			open: function (event, ui) {
				$(this).parent().children().children('.ui-dialog-titlebar-close').hide();
				$(this).parent().css("border-color", "#C0C0C0");
				var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
				_divtitlebar.css("border", "0px");
				_divtitlebar.css("background", "#FFFFFF");
				var _dialog_title = $(this).parent().find("span.ui-dialog-title");
				_dialog_title.css("font-size", "13px");
				_dialog_title.css("font-weight", "bold");
				_dialog_title.css("color", "#002856");
				$(this).parent().find("div.ui-dialog-buttonpane").css("background", "#FFFFFF");
				$("#chiudiFascicoloWsdmButton").focus();
			},
			position: {
				using: function (pos) {
					$(this).css({
						top: '50%',
						left: '50%',
						marginRight: '50%',
						transform: 'translate(-50%, -50%)'

					});
				}
			},
			autoOpen: false,
			modal: true,
			width: 700,
			height: 250,
			title: 'Chiudi fascicolo',
			buttons: {
				"Conferma": {
					text: "Conferma",
					id: "chiudiFascicoloWsdmButton",
					click: _chiudiFascicolo,
					style: "display: none;"
				},
				"Annulla": {
					text: "Annulla",
					click: function () {
						$(this).dialog("close");
					},
					id: "annullaFascicoloWsdmButton",
				},
				"Chiudi": {
					text: "Chiudi",
					id: "refreshWsdmButton",
					click: function() {
						historyReload();
					},
					style: "display: none;"
				}
			}
		};

		$.ajax({
			type: "GET",
			dataType: "json",
			async: false,
			url: "pg/WSDMChiudiFascicolo.do",
			data : {
				codicefascicolo : $("#codicefascicolo").val(),
				metodo: "check"
			},

			complete: function() {
				_nowait();
			},

			success: function(data){
				if(data.esito === 'KO') {
					$("#erroriTrasmissione").html(data.errors);
					$("#sezioneErrori").show();
				}else if(data.warnings) {
					$("#warningsTrasmissione").html(data.warnings);
					$("#sezioneWarning").show();

				}
			},

			error: function(e){
				alert("Errore nella richiesta di chiusura del fascicolo");
			},
		});

		$("#codicefascicolodisplay").val($("#codicefascicolo").val());
		$("#mascheraParametriWSDM").dialog(opt).dialog("open");
		if($("#sezioneErrori").is(":hidden"))
			$("#chiudiFascicoloWsdmButton").show();

	});

	function _chiudiFascicolo() {
		$("#sezioneErrori").hide();
		_wait();
		$.ajax({
			type: "GET",
			dataType: "json",
			async: false,
			url: "pg/WSDMChiudiFascicolo.do",
			data : {
				username: $("#username").val(),
				password: $("#password").val(),
				ruolo: $("#ruolo option:selected").val(),
				nome : $("#nome").val(),
				cognome : $("#cognome").val(),
				codiceuo : $("#codiceuo option:selected").val(),
				codicefascicolo : $("#codicefascicolo").val(),
				servizio: $("#servizio").val(),
				idconfi: $("#idconfi").val(),
				metodo: "chiudi",
				key1: $("#key1").val()
			},

			complete: function() {
				_nowait();
			},

			success: function(data){
				if(data.esito === 'OK') {
					$("#sezioneMessaggi").html("Fascicolo chiuso con successo<br><br>");
					$("#chiudiFascicoloWsdmButton").hide();
					$("#annullaFascicoloWsdmButton").hide();
					$("#refreshWsdmButton").show();
				}else if(data.messaggio) {
					$("#erroriTrasmissione").html(data.messaggio);
					$("#sezioneErrori").show();
					$("#chiudiFascicoloWsdmButton").hide();
				}
			},

			error: function(e){
				alert("Errore nella richiesta di chiusura del fascicolo");
			},
		});



	};

});




