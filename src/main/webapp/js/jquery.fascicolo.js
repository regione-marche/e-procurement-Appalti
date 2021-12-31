/*
*	Gestione del fascicolo e dei documenti protocollati e non
*
*/
var fascicoloAssociato = false;
var sbiancaDatiFascicolo = false;

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
						return $('#tiposistemaremoto').val() == 'IRIDE' || $('#tiposistemaremoto').val() == 'ENGINEERING' ;
			        }
			    },	
			    numerofascicolo: {
					"required": function() {
						return $('#tiposistemaremoto').val() == 'IRIDE' || $('#tiposistemaremoto').val() == 'JIRIDE' || $('#tiposistemaremoto').val() == 'ENGINEERING' ;
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
		_getWSFascicolo($('#tiposistemaremoto').val());
		_gestioneWSFascicolo();		
		
		_isWSDocumentoPopolato();
		if (_tableDocumentiFascicolo != null) {
			_tableDocumentiFascicolo.destroy(true);
		}
				
		
		$("#oggettofascicolo").text("");
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
		}
		
		//Se non c'è il fascicolo o il fasciolo è presente ma non ci sono elementi documentali
		//è presente la funzione "Associa fascicolo esistente", altrimenti la funzione
		//"Imposta credenziali"
		if(!fascicoloAssociato || (fascicoloAssociato && !isWSDocumentoPopolato)){
			if($("#autorizzatoModifiche").val() != "2" && $("#autorizzatoAssociaFascicolo").val() == "true" && $('#tiposistemaremoto').val()!='ENGINEERING' && $('#tiposistemaremoto').val()!='JPROTOCOL'){
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
				_getWSDMFascicolo(true,600,true);
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
		sbiancaDatiFascicolo = false;
		if($("#parametririchiestafascicolo").validate().form()){
			_wait();
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
		if($('#tiposistemaremoto').val()!="ENGINEERING" && $('#tiposistemaremoto').val()!="JPROTOCOL"){
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
	
});




