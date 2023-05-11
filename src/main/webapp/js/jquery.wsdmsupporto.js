/*
*	Funzioni di supporto per la gestione
*	dell'integrazione con il server EldasoftWSDM.
*
*/

var tempo = 400;
var _fascicolazioneAbilitata = 0;
var _tipoWSDM;
var _fascicoliPresenti = 0;
var _delegaInvioMailDocumentaleAbilitata = 0;
var _ufficioIntestatario;
var _genereGara;
var _codiceGara;
var _codiceGaraStipula;
var _ngaraStipula;
var _logincomune;
var _indirizzoMittente;
var _abilitazioneGare;
var _oggettoGara;
var _gestioneStrutturaCompetente = 0;
var _bloccoModificaIndirizzoMittenteAbilitata = 0;
var _associazioneDocumentiProtocolloAbilitata = 0;
var _tipoGara;
var _docTuttiNonProtocollati = false;
_classificaObbligatoria = false;
_ufficioObbligatorio = false;
_letturaMittenteDaServzio = false;
var _isStipula = false;
var msgNoFascicoloLapisopera = "Non risulta ancora associato il fascicolo alla procedura. Procedere all'associazione mediante la funzione 'Associa fascicolo' disponibile nella pagina 'Dati generali' della procedura.";
var _associaCreaFunz = false;
var messaggioLapisoperaAllegati = "ATTENZIONE: il sistema di protocollo accetta solo comunicazioni che abbiano un allegato principale (il primo in lista) firmato digitalmente nel formato '.pdf.p7m'.\r\nPer inviare la comunicazione senza protocollarla e' possibile utilizzare la funzione 'Invia Comunicazione' disponibile nel menu' laterale della pagina 'Dati generali' della comunicazione.\r\nIl fornitore ricevera' la notifica di ricezione di una nuova comunicazione tramite mail non pec.";
var formatoFileFirmatoLapis = 'PDF.P7M';
/*
 * Funzione di attesa
 */
function _wait() {
	document.getElementById('bloccaScreen').style.visibility = 'visible';
	$('#bloccaScreen').css("width", $(document).width());
	$('#bloccaScreen').css("height", $(document).height());
	document.getElementById('wait').style.visibility = 'visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
}


/*
 * Nasconde l'immagine di attesa
 */
function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}


/*
 * Creazione tabella con le credenziali per il collegamento 
 * al servizio remoto.
 * Richiede la presenta di un oggetto <div id="wslogincontainer"/>
 */
function _creaWSLogin() {

	var _form = $("<form/>", { "id": "formwslogin", "name": "formwslogin" });

	var _table = $("<table/>", { "id": "wslogin", "class": "wsdmscheda", "cellspacing": "0", "width": "100%" });
	var _tbody = $("<tbody/>");

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Utente", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _input = $("<input/>", { "id": "username", "name": "username", "title": "Utente", "class": "testo", "type": "text", "size": "24", "maxlength": "20" });
	_td.append(_input);
	_tr.append(_td);
	_tbody.append(_tr);

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Password", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _input = $("<input/>", { "id": "password", "name": "password", "title": "Password", "class": "testo", "type": "password", "size": "24", "maxlength": "20" });
	_td.append(_input);
	_tr.append(_td);
	_tbody.append(_tr);

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Ruolo", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _select = $("<select/>", { "id": "ruolo", "name": "ruolo" });
	var _input = $("<input/>", { "id": "ruolovisualizza", "name": "ruolovisualizza", "title": "Ruolo", "class": "testo", "type": "text", "size": "24", "maxlength": "20" });
	_td.append(_select);
	_td.append(_input);
	_tr.append(_td);
	_tbody.append(_tr);

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Nome", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _input = $("<input/>", { "id": "nome", "name": "nome", "title": "Nome", "class": "testo", "type": "text", "size": "24", "maxlength": "20" });
	_td.append(_input);
	_tr.append(_td);
	_tbody.append(_tr);

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Cognome", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _input = $("<input/>", { "id": "cognome", "name": "cognome", "title": "Cognome", "class": "testo", "type": "text", "size": "24", "maxlength": "20" });
	_td.append(_input);
	_tr.append(_td);
	_tbody.append(_tr);

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Codice UO", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _select = $("<select/>", { "id": "codiceuo", "name": "codiceuo" });
	var _input = $("<input/>", { "id": "codiceuovisualizza", "name": "codiceuovisualizza", "title": "Codice UO", "class": "testo", "type": "text", "size": "24", "maxlength": "20" });
	_td.append(_select);
	_td.append(_input);
	_tr.append(_td);

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Identificativo utente", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _input = $("<input/>", { "id": "idutente", "name": "idutente", "title": "Identificativo utente", "class": "testo", "type": "text", "size": "24", "maxlength": "20" });
	_td.append(_input);
	_tr.append(_td);
	_tbody.append(_tr);

	var _tr = $("<tr/>");
	var _td = $("<td/>", { "text": "Unit&agrave; operativa", "class": "etichetta" });
	_tr.append(_td);
	var _td = $("<td/>", { "class": "valore" });
	var _input = $("<input/>", { "id": "idutenteunop", "name": "idutenteunop", "title": "Unit&agrave; operativa", "class": "testo", "type": "text", "size": "24", "maxlength": "20" });
	_td.append(_input);
	_tr.append(_td);
	_tbody.append(_tr);

	_tbody.append(_tr);
	_table.append(_tbody);
	_form.append(_table);

	$("#wslogincontainer").append(_form);

	var sistema = $("#tiposistemaremoto").val();
	var idconfi = $("#idconfi").val();

	_popolaTabellato("ruolo", "ruolo");
	_popolaTabellato("codiceuo", "codiceuo");
	_popolaTabellato("idutente", "idutente");
	_popolaTabellato("idutenteunop", "idutenteunop");
	_popolaTabellato("classifica", "classificadocumento");
	_popolaTabellato("tipodocumento", "tipodocumento");
	_popolaTabellato("mittenteinterno", "mittenteinterno");
	_popolaTabellato("indirizzomittente", "indirizzomittente");
	_popolaTabellato("mezzo", "mezzoinvio");
	_popolaTabellato("mezzo", "mezzo");
	_popolaTabellato("supporto", "supporto");
	_popolaTabellato("sottotipo", "sottotipo");


	_getWSLogin();
	_gestioneWSLogin();
}


/*
 * Validazione del form con le credenziali
 * per il collegamento al servizio remoto
 */
function _validateWSLogin() {
	$("#formwslogin").validate({
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
			idutenteunop: "required"
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
			idutenteunop: "Specificare l'identificativo dell'unit&agrave; operativa"
		},
		errorPlacement: function(error, element) {
			error.insertAfter($(element));
			error.css("margin-right", "5px");
			error.css("float", "right");
			error.css("vertical-align", "top");
		}
	});
}

/*
 * Lettura dell'utente e degli attributi per la connessione al servizio remoto.
 */
function _getWSLogin() {
	var syscon = $("#syscon").val();
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();

	var urlServizio = "pg/GetWSLogin.do";
	var currentUrl = window.location.href;
	if (currentUrl.indexOf("/pg/") > 0) {
		urlServizio = "GetWSLogin.do";
	}

	/*
	 * Lettura delle informazioni di login memorizzati nella 
	 * tabella WSLogin.
	 */
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: urlServizio,
		data: {
			syscon: syscon,
			servizio: servizio,
			idconfi: idconfi
		},
		success: function(data) {
			if (data) {
				$.map(data, function(item) {
					$("#username").val(item[0]);
					$("#password").val(item[1]);
					if (item[2] != null && item[2] != "") {
						$("#ruolo").val(item[2]).attr("selected", "selected");
						$("#ruolovisualizza").val(item[2]);
					}
					$("#nome").val(item[3]);
					$("#cognome").val(item[4]);
					if (item[5] != null && item[5] != "") {
						$("#codiceuo").val(item[5]).attr("selected", "selected");
						$("#codiceuovisualizza").val(item[5]);
					}
					$("#idutente").val(item[6]);
					$("#idutenteunop").val(item[7]);
					_logincomune = item[8];
				});
			}
		},
		error: function(e) {
			alert("Errore durante la lettura dell'utente e dei suoi attributi");
		}
	});

}


/*
 * Gestione del modo di apertura.
 * Se in visualizzazione e' necessario impostare alcuni 
 * campi in modalita' di sola lettura.
 */
function _gestioneWSLogin() {
	var modoapertura = $("#modoapertura").val();
	var tiposistemaremoto = $("#tiposistemaremoto").val();

	if (tiposistemaremoto == "IRIDE" || tiposistemaremoto == "JIRIDE") {
		$("#password").hide();
		$("#password").closest('tr').hide();
		$("#nome").hide();
		$("#nome").closest('tr').hide();
		$("#cognome").hide();
		$("#cognome").closest('tr').hide();
		$("#codiceuo").hide();
		$("#codiceuo").closest('tr').hide();
		$("#idutente").hide();
		$("#idutente").closest('tr').hide();
		$("#idutenteunop").hide();
		$("#idutenteunop").closest('tr').hide();
	}

	if (tiposistemaremoto == "ENGINEERING") {
		$("#ruolo").hide();
		$("#ruolo").closest('tr').hide();
		$("#nome").hide();
		$("#nome").closest('tr').hide();
		$("#cognome").hide();
		$("#cognome").closest('tr').hide();
		$("#codiceuo").hide();
		$("#codiceuo").closest('tr').hide();
	}

	if (tiposistemaremoto == "ENGINEERINGDOC") {
		$("#ruolo").hide();
		$("#ruolo").closest('tr').hide();
		$("#nome").hide();
		$("#nome").closest('tr').hide();
		$("#cognome").hide();
		$("#cognome").closest('tr').hide();
		$("#codiceuo").hide();
		$("#codiceuo").closest('tr').hide();
		$("#idutente").hide();
		$("#idutente").closest('tr').hide();
		$("#idutenteunop").hide();
		$("#idutenteunop").closest('tr').hide();
	}

	if (tiposistemaremoto == "PALEO") {
		$("#idutente").hide();
		$("#idutente").closest('tr').hide();
		$("#idutenteunop").hide();
		$("#idutenteunop").closest('tr').hide();
	}


	if (tiposistemaremoto == "FOLIUM" || tiposistemaremoto == "ARCHIFLOWFA" || tiposistemaremoto == "EASYDOC" || tiposistemaremoto == "SMAT" || tiposistemaremoto == "TITULUS" || tiposistemaremoto == "ARCHIFLOW" || tiposistemaremoto == "PRISMA"
		|| tiposistemaremoto == "INFOR" || tiposistemaremoto == "URBI" || tiposistemaremoto == "PROTSERVICE" || tiposistemaremoto == "JPROTOCOL" || tiposistemaremoto == "ITALPROT" || tiposistemaremoto == "JDOC" || tiposistemaremoto == "DOCER"
		|| tiposistemaremoto == "NUMIX") {
		$("#ruolo").hide();
		$("#ruolo").closest('tr').hide();
		$("#nome").hide();
		$("#nome").closest('tr').hide();
		$("#cognome").hide();
		$("#cognome").closest('tr').hide();
		$("#codiceuo").hide();
		$("#codiceuo").closest('tr').hide();
		$("#idutente").hide();
		$("#idutente").closest('tr').hide();
		$("#idutenteunop").hide();
		$("#idutenteunop").closest('tr').hide();

		if (tiposistemaremoto == "INFOR" || tiposistemaremoto == "PROTSERVICE" || tiposistemaremoto == "JPROTOCOL") {
			$("#password").hide();
			$("#password").closest('tr').hide();
		}
	}

	if (tiposistemaremoto == "LAPISOPERA") {
		$("#ruolo").hide();
		$("#ruolo").closest('tr').hide();
		$("#nome").hide();
		$("#nome").closest('tr').hide();
		$("#codiceuo").hide();
		$("#codiceuo").closest('tr').hide();
		$("#idutente").hide();
		$("#idutente").closest('tr').hide();
		$("#idutenteunop").hide();
		$("#idutenteunop").closest('tr').hide();
	}

	if (modoapertura == "VISUALIZZA") {
		$("#username").prop("readonly", true);
		$("#username").addClass("readonly");
		$("#password").prop("readonly", true);
		$("#password").addClass("readonly");
		$("#ruolovisualizza").show();
		$("#ruolovisualizza").prop("readonly", true);
		$("#ruolovisualizza").addClass("readonly");
		$("#ruolo").hide();
		$("#nome").prop("readonly", true);
		$("#nome").addClass("readonly");
		$("#cognome").prop("readonly", true);
		$("#cognome").addClass("readonly");
		$("#codiceuovisualizza").show();
		$("#codiceuovisualizza").prop("readonly", true);
		$("#codiceuovisualizza").addClass("readonly");
		$("#codiceuo").hide();
		$("#idutente").prop("readonly", true);
		$("#idutente").addClass("readonly");
		$("#idutenteunop").prop("readonly", true);
		$("#idutenteunop").addClass("readonly");
	} else {
		$("#username").prop("readonly", false);
		$("#username").removeClass("readonly");
		$("#password").prop("readonly", false);
		$("#password").removeClass("readonly");
		$("#ruolovisualizza").hide();
		$("#ruolovisualizza").prop("readonly", false);
		$("#ruolovisualizza").removeClass("readonly");
		$("#ruolo").show();
		$("#nome").prop("readonly", false);
		$("#nome").removeClass("readonly");
		$("#cognome").prop("readonly", false);
		$("#cognome").removeClass("readonly");
		$("#codiceuovisualizza").hide();
		$("#codiceuovisualizza").prop("readonly", false);
		$("#codiceuovisualizza").removeClass("readonly");
		$("#codiceuo").show();
		$("#idutente").prop("readonly", false);
		$("#idutente").removeClass("readonly");
		$("#idutenteunop").prop("readonly", false);
		$("#idutenteunop").removeClass("readonly");
	}

	if (_logincomune == "1") {
		bloccaCampiLoginComune();
	}
}


function bloccaCampiLoginComune() {
	$("#username").prop("readonly", true);
	$("#username").addClass("readonly");
	$("#password").prop("readonly", true);
	$("#password").addClass("readonly");
	$("#nome").prop("readonly", true);
	$("#nome").addClass("readonly");
	$("#cognome").prop("readonly", true);
	$("#cognome").addClass("readonly");
	$("#idutente").prop("readonly", true);
	$("#idutente").addClass("readonly");
	$("#idutenteunop").prop("readonly", true);
	$("#idutenteunop").addClass("readonly");
	$("#ruolovisualizza").show();
	$("#ruolovisualizza").prop("readonly", true);
	$("#ruolovisualizza").addClass("readonly");
	$("#ruolo").hide();
	$("#codiceuovisualizza").show();
	$("#codiceuovisualizza").prop("readonly", true);
	$("#codiceuovisualizza").addClass("readonly");
	$("#codiceuo").hide();
}

/*
 * Salva l'utente e gli attributi attributi per la connessione ai servizi remoti,
 * i dati vengono presi dai campi nelle pagine
 */
function _setWSLogin() {
	var syscon = $("#syscon").val();
	var servizio = $("#servizio").val();
	var username = $("#username").val();
	var password = $("#password").val();
	var ruolo = $("#ruolo option:selected").val();
	var nome = $("#nome").val();
	var cognome = $("#cognome").val();
	var codiceuo = $("#codiceuo option:selected").val();
	var idutente = $("#idutente").val();
	var idutenteunop = $("#idutenteunop").val();
	var idconfi = $("#idconfi").val();

	$.ajax({
		type: "GET",
		async: false,
		url: "pg/SetWSLogin.do",
		data: {
			syscon: syscon,
			servizio: servizio,
			username: username,
			password: password,
			ruolo: ruolo,
			nome: nome,
			cognome: cognome,
			codiceuo: codiceuo,
			idutente: idutente,
			idutenteunop: idutenteunop,
			idconfi: idconfi
		}
	});
}

/*
 * Salva l'utente e gli attributi attributi per la connessione ai servizi remoti,
 * i dati vengono passati come parametri
 */
function setWSLogin(syscon, servizio, username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop, idconfi) {

	$.ajax({
		type: "GET",
		async: false,
		url: "pg/SetWSLogin.do",
		data: {
			syscon: syscon,
			servizio: servizio,
			username: username,
			password: password,
			ruolo: ruolo,
			nome: nome,
			cognome: cognome,
			codiceuo: codiceuo,
			idutente: idutente,
			idutenteunop: idutenteunop,
			idconfi: idconfi
		}
	});
}

/*
 * Lettura del tabellato identificato da "nome".
 * Richiede che nel DOM sia gia' presente un oggetto
 * di tipo "select" con "id" e "name" uguale a "id" 
 */
function _popolaTabellato(codice, id) {

	var sistema = $("#tiposistemaremoto").val();
	var idconfi = $("#idconfi").val();
	_popolaTabellatoByParam(codice, id, sistema, idconfi);

}

/*
 * Lettura del tabellato identificato da "nome".
 * Viene fornito in input l'url di configurazione dell'url
 * Richiede che nel DOM sia gia' presente un oggetto
 * di tipo "select" con "id" e "name" uguale a "id" 
 */
function _popolaTabellatoByParam(codice, id, sistema, idconfi) {
	var servizio = $("#servizio").val();
	var tabellatiInDB = $("#tabellatiInDB").val();

	var urlServizio = "pg/GetWSDMTabellato.do";
	var currentUrl = window.location.href;
	if (currentUrl.indexOf("/pg/") > 0) {
		urlServizio = "GetWSDMTabellato.do";
	}

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: urlServizio,
		data: {
			codice: codice,
			sistema: sistema,
			idconfi: idconfi,
			servizio: servizio,
			tabellatiInDB: tabellatiInDB
		},
		success: function(data) {
			if (data) {
				$("#" + id).append($("<option/>", { value: "", text: "" }));
				$.map(data, function(item) {
					$("#" + id).append($("<option/>", { value: item[0], text: item[1] }));
				});

				//Se � presente un solo valore nel tabellato allora lo si seleziona
				if ($("#" + id + " option").length == 2) {
					$("#" + id + " option").eq(1).prop('selected', true);
				}
			}
		},
		error: function(e) {
			alert("Errore durante la lettura del tabellato " + codice);
		}
	});
}

function _popolaTabellatoByUrl(nome, id, url) {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMTabellatoByUrl.do",
		data: "nome=" + nome + "&url=" + url,
		success: function(data) {
			if (data) {
				$("#" + id).append($("<option/>", { value: "", text: "" }));
				$.map(data, function(item) {
					$("#" + id).append($("<option/>", { value: item[0], text: item[1] }));
				});

				//Se � presente un solo valore nel tabellato allora lo si seleziona
				if ($("#" + id + " option").length == 2) {
					$("#" + id + " option").eq(1).prop('selected', true);
				}
			}
		},
		error: function(e) {
			alert("Errore durante la lettura del tabellato " + nome);
		}
	});
}

/*
 * Verifica se ? abilitata la gestione dei fascicoli
 */
function _controlloPresenzaFascicolazione() {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "pg.wsdm.applicaFascicolazione"
		},
		success: function(data) {
			if (data) {
				_fascicolazioneAbilitata = data.propertyWSDMCONFIPRO;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se ? impostata la fascicolazione");
		}
	});
}

/*
 * Verifica se e' abilitata la gestione dell'invio mail dal documentale
 */
function _controlloDelegaInvioMailAlDocumentale() {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "pg.wsdm.invioMailPec"
		},
		success: function(data) {
			if (data) {
				_delegaInvioMailDocumentaleAbilitata = data.propertyWSDMCONFIPRO;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se e' impostata la delega invio mail al documentale");
		}
	});
}

function _inizializzazioni() {
	if ($("#entita").val() == "G1STIPULA")
		_isStipula = true;

	//Caricamento del codice della gara e del genere
	if (!_isStipula)
		_caricamentoCodiceGenereGara();
	_controlloObbligatorietaClassifica();
	_controlloObbligatorietaUfficio();

	if (_isStipula)
		_getCodiceDellaGaraDellaStipula();

	if (_fascicolazioneAbilitata == 0) {
		$("#inserimentoinfascicolo").val("NO");
	} else if (_fascicolazioneAbilitata == 1 && _fascicoliPresenti > 0) {
		$("#inserimentoinfascicolo").val("SI_FASCICOLO_ESISTENTE");
	} else {
		$("#inserimentoinfascicolo").val("SI_FASCICOLO_NUOVO");
		if (_tipoWSDM != "ITALPROT")
			_inizializzazioneDaOggettoGara();
	}

	_caricamentoCodiceUfficioIntestatario();
	$("#societa").val(_ufficioIntestatario);

	if (!_isStipula)
		_valorizzazioneCodiceGaraLotto();

	if (_genereGara != 10 && _genereGara != 20 && !_isStipula)
		_caricamentoCodiceCig();

	if (_tipoWSDM == "ARCHIFLOW") {
		//Si valorizza il campo mittente col codice dell'ufficio intestatario
		var optionvalue = "<option value='" + _ufficioIntestatario + "'>" + _ufficioIntestatario + "</option>";
		$("#mittenteinterno").append(optionvalue);
		$("#mittenteinterno").val(_ufficioIntestatario);
	}

	if (_tipoWSDM == "JIRIDE") {
		_controlloGestioneStrutturaCompetente();
		if (_gestioneStrutturaCompetente == 1) {
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
		}
		if (_genereGara == 10 || _genereGara == 11 || _genereGara == 20 || _isStipula) {
			$("#livelloriservatezza").hide();
			$("#livelloriservatezza").closest('tr').hide();
		}
		_controlloLetturaMittenteServizio();
		if (_letturaMittenteDaServzio) {
			$('#mittenteinterno').empty();
			_popolaTabellatoJirideMittente("mittenteinterno");
			_controlloDelegaInvioMailAlDocumentale();
			if (_delegaInvioMailDocumentaleAbilitata == 1) {
				$('#indirizzomittente').empty();
				_popolaTabellatoJirideMittente("indirizzomittente");
			}
		}
	}

	$('#inserimentoinfascicolo').trigger('change');
	if (_fascicolazioneAbilitata == 1 || _tipoWSDM == "PALEO" || _tipoWSDM == "TITULUS" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "SMAT" || _tipoWSDM == "URBI") {
		if (_tipoWSDM != "LAPISOPERA") {
			$("#classificadocumento").hide();
			$("#classificadocumento").closest('tr').hide();
		}
		$("#idtitolazione").hide();
		$("#idtitolazione").closest('tr').hide();
	}


	if (_tipoWSDM == "TITULUS") {
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#indirizzomittente").hide();
		$("#indirizzomittente").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
		var password = $("#password").val();
		var username = $("#username").val();
		if (username != null && password != null && username != "" && password != "" && $("#inserimentoinfascicolo").val() == "SI_FASCICOLO_NUOVO") {
			_popolaTabellatoCodiceAoo();
		}
		if ($("#inserimentoinfascicolo").val() == "SI_FASCICOLO_NUOVO") {
			_inizializzaCodiceAooFiltrato();
			_inizializzaCodiceUfficioFiltrato();

		}

		gestioneCampoPosizioneTitulus();
		oggettoDocumentoTitulus();
	}

	if (_tipoWSDM == "ARCHIFLOW") {
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#mezzoinvio").hide();
		$("#mezzoinvio").closest('tr').hide();
		$("#mezzoinvio").val('');
		$("#mezzo").closest('tr').show();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();

	}

	if (_tipoWSDM == "SMAT") {
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#indirizzomittente").hide();
		$("#indirizzomittente").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
	}

	if (_tipoWSDM == "FOLIUM" || _tipoWSDM == "EASYDOC") {
		$("#idtitolazione").hide();
		$("#idtitolazione").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#indirizzomittente").hide();
		$("#indirizzomittente").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
	}

	if (_tipoWSDM == "ARCHIFLOWFA") {
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mezzoinvio").hide();
		$("#mezzoinvio").closest('tr').hide();
		$("#mezzoinvio").val('');
		$("#mezzo").closest('tr').show();
		$("#trSupporto").show();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();

		if ($("#inserimentoinfascicolo").val() == "SI_FASCICOLO_ESISTENTE") {
			_getWSDMFascicolo(false, 600);
			_setDescrizioneCodiceTabellato("classificafascicolo", $("#classificafascicolonuovo").val(), "classificafascicolodescrizione", 2);
		}


	}

	if (_tipoWSDM == "PRISMA") {
		$("#idtitolazione").hide();
		$("#idtitolazione").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#indirizzomittente").hide();
		$("#indirizzomittente").closest('tr').hide();
		$("#mezzoinvio").hide();
		$("#mezzoinvio").closest('tr').hide();
		$("#mezzoinvio").val('');
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();

	}

	if (_tipoWSDM == "ARCHIFLOWFA" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "JIRIDE") {
		if (_delegaInvioMailDocumentaleAbilitata == 1) {
			_caricamentoIndirizzoMittenteGara();
			if (_indirizzoMittente != null && _indirizzoMittente != "") {
				$("#indirizzomittente").val(_indirizzoMittente);
				if ($("#indirizzomittente :selected").text() != "" && $("#indirizzomittente :selected").text() != null) {
					_abilitazioneGare = $("#abilitazioneGare").val();
					_controlloBloccoModificaIndirizzoMittente();
					if (_abilitazioneGare == "U" && _bloccoModificaIndirizzoMittenteAbilitata == 1) {
						$("#indirizzomittente").hide();
						$("#indirizzomittenteisualizza").show();
						$("#indirizzomittenteisualizza").text($("#indirizzomittente :selected").text());
					}
				}
			}
		}
	}

	if (_tipoWSDM == "INFOR") {
		//Se il tabellato è popolato non lo nascondo
		if ($('#tipodocumento option').length < 2) {
			$("#tipodocumento").hide();
			$("#tipodocumento").closest('tr').hide();
		}
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();

	}

	if (_tipoWSDM == "URBI") {
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#mezzoinvio").hide();
		$("#mezzoinvio").closest('tr').hide();
		$("#mezzoinvio").val('');
		$("#mezzo").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();

	}

	if (_tipoWSDM == "PROTSERVICE" || _tipoWSDM == "ITALPROT") {
		$("#idtitolazione").hide();
		$("#idtitolazione").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#mezzoinvio").hide();
		$("#mezzoinvio").closest('tr').hide();
		$("#mezzoinvio").val('');
		$("#mezzo").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
	}

	if (_tipoWSDM == "JPROTOCOL") {
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#idtitolazione").hide();
		$("#idtitolazione").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#mezzoinvio").hide();
		$("#mezzoinvio").closest('tr').hide();
		$("#mezzoinvio").val('');
		$("#mezzo").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
	}

	if (_tipoWSDM == "JDOC") {
		$("#idtitolazione").hide();
		$("#idtitolazione").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#rigaSottotipo").show();
		$("#mezzoinvio").hide();
		$("#mezzoinvio").closest('tr').hide();
		$("#mezzoinvio").val('');
		$("#mezzo").closest('tr').show();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
	}

	if (_tipoWSDM == "DOCER") {
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#indirizzomittente").hide();
		$("#indirizzomittente").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#rigaTipoFirma").show();
	}

	if (_tipoWSDM == "ENGINEERINGDOC") {
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();

	}

	if (_tipoWSDM == "NUMIX") {
		$("#classificadocumento").hide();
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#indirizzomittente").hide();
		$("#indirizzomittente").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
		$("#mezzo").closest('tr').show();
		$("#idtitolazione").hide();
		$("#idtitolazione").closest('tr').hide();
	}

	if (_tipoWSDM == "LAPISOPERA") {
		$("#tipodocumento").hide();
		$("#tipodocumento").closest('tr').hide();
		$("#codiceregistrodocumento").hide();
		$("#codiceregistrodocumento").closest('tr').hide();
		$("#mittenteinterno").hide();
		$("#mittenteinterno").closest('tr').hide();
		$("#indirizzomittente").hide();
		$("#indirizzomittente").closest('tr').hide();
		$("#idindice").hide();
		$("#idindice").closest('tr').hide();
		$("#idunitaoperativamittente").hide();
		$("#idunitaoperativamittente").closest('tr').hide();
	}

	if ($("#classificafascicolonuovo option").length == 2 || (_tipoWSDM == "PRISMA" && $("#classificafascicolonuovo option").length == 1)) {
		if (_tipoWSDM != "TITULUS")
			$('#classificadocumento').val($('#classificafascicolonuovo').val());
		$('#idtitolazione').val($('#classificafascicolonuovo').val());

	}
}


/*
 * Caricamento Oggetto della gara nei vari campi
 */
function _inizializzazioneDaOggettoGara() {
	var chiave1 = $("#chiaveOriginale").val();
	if (_isStipula)
		chiave1 = $("#key1").val();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetOggettoGara.do",
		data: {
			chiave1: chiave1,
			codiceGara: _codiceGara,
			genereGara: _genereGara,
			isStipula: _isStipula
		},
		success: function(data) {
			if (data) {
				_oggettoGara = data.oggettoGara;
				var testoFascicolo = _getCodiceDellaGara($("#chiaveOriginale").val());
				/*
				if(_genereGara==1 || _genereGara==3){
					testoFascicolo = _codiceGara;
				}else{
					testoFascicolo = $("#chiaveOriginale").val();
				}
				*/

				testoFascicolo += " - " + data.oggettoGara;
				$("#oggettofascicolonuovo").val(testoFascicolo);
				$("#descrizionefascicolonuovo").val(testoFascicolo);
				$("#oggettofascicolo").text(testoFascicolo);
			}
		},
		error: function(e) {
			alert("Errore nel caricamento dell'oggetto del fascicolo'");
		}
	});
}

/*
 * Validazione wslogin
 */
function _validazioneWSLogin() {
	$("#richiestawslogin").validate({
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
			idutenteunop: "required"
		},
		messages: {
			username: "Specificare l'utente",
			password: "Specificare la password",
			ruolo: "Specificare il ruolo",
			ruolovisualizza: "Specificare il ruolo",
			nome: "Specificare il nome",
			cognome: "Specificare il cognome",
			codiceuo: "Specificare il codice dell'unit&agrave; organizzativa",
			codiceuovisualizza: "Specificare il codice dell'unit&agrave organizzativa",
			idutente: "Specificare l'identificativo utente",
			idutenteunop: "Specificare l'identificativo utente dell'unit&agrave operativa"
		},
		errorPlacement: function(error, element) {
			error.insertAfter($(element));
			error.css("margin-right", "5px");
			error.css("float", "right");
			error.css("vertical-align", "top");
		}
	});
}

function _getTipoWSDM() {
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMRemote.do",
		data: {
			servizio: servizio,
			idconfi: idconfi
		},
		success: function(data) {
			if (data) {
				$.map(data, function(item) {
					_tipoWSDM = item[0];
				});
			}
		}
	});
}

/*
 * Gestione associazione al fascicolo
 */
function _gestioneInserimentoInFascicolo() {
	var inserimentoinfascicolo = $("#inserimentoinfascicolo option:selected").val();
	if (inserimentoinfascicolo == "NO" || inserimentoinfascicolo == "") {
		$("#codicefascicolo").parent().parent().hide();
		$("#annofascicolo").parent().parent().hide();
		$("#numerofascicolo").parent().parent().hide();
		$("#oggettofascicolo").parent().parent().hide();
		$("#oggettofascicolonuovo").parent().parent().hide();
		$("#classificafascicolodescrizione").parent().parent().hide();
		$("#classificafascicolonuovo").parent().parent().hide();
		$("#descrizionefascicolo").parent().parent().hide();
		$("#descrizionefascicolonuovo").parent().parent().hide();
		$("#sezionedatifascicolo").hide();

		/*
		if ($('#tiposistemaremoto').val() == 'IRIDE' || $('#tiposistemaremoto').val() == 'JIRIDE' || $('#tiposistemaremoto').val() == 'PALEO' || $('#tiposistemaremoto').val() == 'ENGINEERING'
		  ||  $('#tiposistemaremoto').val() == 'ARCHIFLOW' || $('#tiposistemaremoto').val() == 'SMAT' || $('#tiposistemaremoto').val() == 'FOLIUM') {
			$("#codiceaoonuovo").parent().parent().hide();
			$("#codiceufficionuovo").parent().parent().hide();
			$("#sezioneamministrazioneorganizzativa").hide();
		}
		*/
		if ($('#tiposistemaremoto').val() == "TITULUS") {
			$("#sezioneamministrazioneorganizzativa").show();
			$("#sezionecodiceaoo").show();

		}

		if ($('#tiposistemaremoto').val() == "ARCHIFLOWFA") {
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
		}

		if ($('#tiposistemaremoto').val() == "FOLIUM") {
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
		}

		if ($('#tiposistemaremoto').val() == "ENGINEERINGDOC") {
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
		}

		if ($('#tiposistemaremoto').val() == "NUMIX") {
			_popolaTabellatoClassificaNumix();
			_inizializzaClassificaDocFiltrata();
		}

	}

	if (inserimentoinfascicolo == "SI_FASCICOLO_ESISTENTE") {
		if ($('#tiposistemaremoto').val() != 'FOLIUM' && $('#tiposistemaremoto').val() != 'INFOR' && $('#tiposistemaremoto').val() != 'JPROTOCOL')
			$("#codicefascicolo").parent().parent().show();
		else
			$("#codicefascicolo").parent().parent().hide();

		if ($('#tiposistemaremoto').val() == 'IRIDE' || $('#tiposistemaremoto').val() == 'JIRIDE' || $('#tiposistemaremoto').val() == 'ENGINEERING' || $('#tiposistemaremoto').val() == 'ARCHIFLOW'
			|| $('#tiposistemaremoto').val() == 'PRISMA' || $('#tiposistemaremoto').val() == 'INFOR' || $('#tiposistemaremoto').val() == 'JPROTOCOL' || $('#tiposistemaremoto').val() == 'JDOC'
			|| $('#tiposistemaremoto').val() == 'DOCER' || $('#tiposistemaremoto').val() == 'ENGINEERINGDOC' || $('#tiposistemaremoto').val() == 'LAPISOPERA') {
			$("#annofascicolo").parent().parent().show();
			$("#numerofascicolo").parent().parent().show();
			if ($('#tiposistemaremoto').val() == 'JIRIDE' || $('#tiposistemaremoto').val() == 'JPROTOCOL' || $('#tiposistemaremoto').val() == 'JDOC' || $('#tiposistemaremoto').val() == 'DOCER'
				|| $('#tiposistemaremoto').val() == 'LAPISOPERA') {
				$("#oggettofascicolo").parent().parent().hide();
			} else {
				$("#oggettofascicolo").parent().parent().show();
			}
			$("#oggettofascicolonuovo").parent().parent().hide();
			if ($('#tiposistemaremoto').val() == 'ARCHIFLOW' || $('#tiposistemaremoto').val() == 'JIRIDE' || $('#tiposistemaremoto').val() == 'JDOC')
				$("#classificafascicolodescrizione").parent().parent().hide();
			else
				$("#classificafascicolodescrizione").parent().parent().show();
			$("#classificafascicolonuovo").parent().parent().hide();
			if ($('#tiposistemaremoto').val() != 'PRISMA' && $('#tiposistemaremoto').val() != 'INFOR' && $('#tiposistemaremoto').val() != 'JIRIDE' && $('#tiposistemaremoto').val() != 'JPROTOCOL'
				&& $('#tiposistemaremoto').val() != 'JDOC' && $('#tiposistemaremoto').val() != 'DOCER' && $('#tiposistemaremoto').val() != 'LAPISOPERA')
				$("#descrizionefascicolo").parent().parent().show();
			else
				$("#descrizionefascicolo").parent().parent().hide();
			$("#descrizionefascicolonuovo").parent().parent().hide();
		} else {
			if ($('#tiposistemaremoto').val() != 'ITALPROT')
				$("#annofascicolo").parent().parent().hide();
			$("#numerofascicolo").parent().parent().hide();
			if ($('#tiposistemaremoto').val() != 'TITULUS' && $('#tiposistemaremoto').val() != 'SMAT' && $('#tiposistemaremoto').val() != 'ARCHIFLOWFA')
				$("#oggettofascicolo").parent().parent().hide();
			$("#oggettofascicolonuovo").parent().parent().hide();
			if ($('#tiposistemaremoto').val() != 'ARCHIFLOWFA' && $('#tiposistemaremoto').val() != 'FOLIUM' && $('#tiposistemaremoto').val() != 'TITULUS' && $('#tiposistemaremoto').val() != 'ITALPROT')
				$("#classificafascicolodescrizione").parent().parent().hide();
			$("#classificafascicolonuovo").parent().parent().hide();
			$("#descrizionefascicolo").parent().parent().hide();
			$("#descrizionefascicolonuovo").parent().parent().hide();
		}

		$("#codicefascicolo").prop("readonly", true);
		$("#codicefascicolo").addClass("readonly");
		if ($('#tiposistemaremoto').val() == 'IRIDE' || $('#tiposistemaremoto').val() == 'JIRIDE' || $('#tiposistemaremoto').val() == 'ENGINEERING' || $('#tiposistemaremoto').val() == 'ARCHIFLOW' || $('#tiposistemaremoto').val() == 'PRISMA'
			|| $('#tiposistemaremoto').val() == 'INFOR' || $('#tiposistemaremoto').val() == 'JPROTOCOL' || $('#tiposistemaremoto').val() == 'JDOC' || $('#tiposistemaremoto').val() == 'DOCER'
			|| $('#tiposistemaremoto').val() == 'ITALPROT' || $('#tiposistemaremoto').val() == 'ENGINEERINGDOC' || $('#tiposistemaremoto').val() == 'LAPISOPERA') {
			$("#annofascicolo").prop("readonly", true);
			$("#annofascicolo").addClass("readonly");
			$("#numerofascicolo").prop("readonly", true);
			$("#numerofascicolo").addClass("readonly");
		}

		if ($('#tiposistemaremoto').val() != 'SMAT') {
			_getWSFascicolo($('#tiposistemaremoto').val());
		} else {
			$("#oggettofascicolo").parent().parent().hide();
			$("#codicefascicolo").val($("#key1").val());
		}

		if ($('#tiposistemaremoto').val() == 'TITULUS') {
			_inizializzazioneDaOggettoGara();

			var labelCodiceAoo = $("#codiceaoonuovo").val();
			if ($('#codiceaoodes').val() != null && $('#codiceaoodes').val() != "")
				labelCodiceAoo += " - " + $('#codiceaoodes').val();
			$('#codiceaoo').text(labelCodiceAoo);
			$('#codiceaoonuovo').hide();
			$('#codiceaoo').show();

			var labelCodiceUfficio = $("#codiceufficionuovo").val();
			if ($('#codiceufficiodes').val() != null && $('#codiceufficiodes').val() != "")
				labelCodiceUfficio += " - " + $('#codiceufficiodes').val();
			$('#codiceufficio').text(labelCodiceUfficio);
			$('#codiceufficionuovo').hide();
			$('#codiceufficio').show();

			$("#sezioneamministrazioneorganizzativa").show();
			$("#sezionecodiceaoo").show();
			$("#sezionecodiceufficio").show();
			$("#obbligatorio").hide();
			$("#oggettofascicolo").parent().parent().hide();
		} else if ($('#tiposistemaremoto').val() == 'ARCHIFLOWFA') {
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			$('#struttura').text($("#strutturaonuovo").val());
			$('#strutturaonuovo').hide();
			$('#struttura').show();
		} else if ($('#tiposistemaremoto').val() == 'JDOC') {
			_caricamentoDatiRUP();
		} else {
			if ($('#tiposistemaremoto').val() != 'SMAT' && $('#tiposistemaremoto').val() != 'FOLIUM' && $('#tiposistemaremoto').val() != 'JIRIDE' && $('#tiposistemaremoto').val() != 'JPROTOCOL'
				&& $('#tiposistemaremoto').val() != 'DOCER' && $('#tiposistemaremoto').val() != 'ITALPROT' && $('#tiposistemaremoto').val() != 'LAPISOPERA')
				_getAltriDatiFascicolo();
		}
		if ($('#tiposistemaremoto').val() == 'FOLIUM') {
			$("#codicefascicolo").parent().parent().hide();
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			$('#struttura').text($("#strutturaonuovo").val());
			$('#strutturaonuovo').hide();
			$('#struttura').show();
		}

		if ($('#tiposistemaremoto').val() == 'JIRIDE') {
			$("#sezionestruttura").hide();
			$("#sezionestrutturacompetente").hide();
		}


		if ($('#tiposistemaremoto').val() == 'PRISMA') {
			$("#codicefascicolo").parent().parent().hide();
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			$('#struttura').text($("#strutturaonuovo").val());
			$('#strutturaonuovo').hide();
			$('#struttura').show();
			//_getWSDMDescrizioneTabellatoPrisma();
		}

		if ($('#tiposistemaremoto').val() == 'INFOR') {
			_setDescrizioneCodiceTabellato("classificafascicolo", $("#classificafascicolonuovo").val(), "classificafascicolodescrizione", 2);
			_controlloGestioneStrutturaCompetente();
			if (_gestioneStrutturaCompetente == 1) {
				$("#sezionestruttura").show();
				$("#sezionestrutturacompetente").show();
				$('#struttura').text($("#strutturaonuovo").val());
				$('#strutturaonuovo').hide();
				$('#struttura').show();
			}
		}

		if ($('#tiposistemaremoto').val() == 'JPROTOCOL') {
			_setDescrizioneCodiceTabellato("classificafascicolo", $("#classificafascicolonuovo").val(), "classificafascicolodescrizione", 2);
		}

		if ($('#tiposistemaremoto').val() == 'ITALPROT') {
			//_setDescrizioneCodiceTabellato("classificafascicolo",$("#classificafascicolonuovo").val(),"classificafascicolodescrizione",2);
			$("#classificafascicolodescrizione").text($("#classificafascicolonuovo").val());
		}

		if ($('#tiposistemaremoto').val() == 'DOCER') {
			$("#codicefascicolo").parent().parent().hide();
		}

		if ($('#tiposistemaremoto').val() == 'ENGINEERINGDOC') {
			$("#uocompetenzaTxt").attr('style', 'resize: none;border: none; outline: none;');
			$("#sezioneuocompetenza").show();
			$("#selezioneuocompetenza").hide();
		}

		if ($('#tiposistemaremoto').val() == 'LAPISOPERA') {
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			$('#struttura').text($("#strutturaonuovo").val());
			$('#strutturaonuovo').hide();
			$('#struttura').show();
			
			if (_associaCreaFunz == false)
				$('#linkleggiDatiFascicolo').show();
		}
	}

	if (inserimentoinfascicolo == "SI_FASCICOLO_NUOVO") {
		if ($('#tiposistemaremoto').val() != 'SMAT' && $('#tiposistemaremoto').val() != 'ARCHIFLOWFA' && $('#tiposistemaremoto').val() != 'LAPISOPERA')
			$("#codicefascicolo").parent().parent().hide();
		if ($('#tiposistemaremoto').val() == 'TITULUS' || $('#tiposistemaremoto').val() == 'SMAT')
			$("#codicefascicolo").val($("#key1").val());
		if ($('#tiposistemaremoto').val() != 'PRISMA' && $('#tiposistemaremoto').val() != 'LAPISOPERA') {
			if ($('#tiposistemaremoto').val() != 'ITALPROT')
				$("#annofascicolo").parent().parent().hide();
			$("#numerofascicolo").parent().parent().hide();
		}
		if ($('#tiposistemaremoto').val() != 'ARCHIFLOWFA')
			$("#oggettofascicolo").parent().parent().hide();
		else
			$("#oggettofascicolo").text('');
		$("#classificafascicolodescrizione").parent().parent().hide();
		if ($('#tiposistemaremoto').val() == 'ARCHIFLOW' || $('#tiposistemaremoto').val() == 'SMAT' || $('#tiposistemaremoto').val() == 'JDOC')
			$("#classificafascicolonuovo").parent().parent().hide();
		$("#descrizionefascicolo").parent().parent().hide();
		if ($('#tiposistemaremoto').val() == 'TITULUS')
			$("#descrizionefascicolonuovo").parent().parent().hide();
		if ($('#tiposistemaremoto').val() == 'SMAT') {
			$("#codicefascicolo").prop("readonly", true);
			$("#codicefascicolo").addClass("readonly");
			$("#oggettofascicolonuovo").parent().parent().hide();
			$("#descrizionefascicolonuovo").parent().parent().hide();
		}

		if ($('#tiposistemaremoto').val() == "TITULUS") {
			if (!_classificaObbligatoria)
				$("#classificaObbligatoriaNuovo").hide();
			if (!_ufficioObbligatorio)
				$("#ufficioObbligatorioNuovo").hide();
			$("#sezioneamministrazioneorganizzativa").show();
			$("#sezionecodiceaoo").show();
			$("#sezionecodiceufficio").show();
			$("#codiceaoonuovo").hide();
			$("#codiceufficionuovo").hide();
			if (_genereGara == 1 || _genereGara == 2 || _genereGara == 3) {
				_inizializzazioneTipoGara();
			}
			_popolaTabellatoClassificaTitulus();
		}

		if ($('#tiposistemaremoto').val() == "ARCHIFLOWFA") {
			$("#linkleggifascicolo").show();
			$("#oggettofascicolo").parent().parent().hide();
			$("#oggettofascicolonuovo").val(null);
			$("#oggettofascicolonuovo").prop("readonly", true);
			$("#oggettofascicolonuovo").addClass("readonly");
			$('#classificafascicolonuovo').attr('disabled', 'disabled');
			$("#descrizionefascicolonuovo").parent().parent().hide();
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			_popolaTabellato("struttura", "strutturaonuovo");
		}

		if ($('#tiposistemaremoto').val() == "FOLIUM") {
			$("#oggettofascicolonuovo").parent().parent().hide();
			$('#classificafascicolonuovo').parent().parent().hide();
			$("#descrizionefascicolonuovo").parent().parent().hide();
			$("#categoria").parent().parent().show();
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			_popolaTabellato("struttura", "strutturaonuovo");
		}

		if ($('#tiposistemaremoto').val() == "PRISMA") {
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			$("#classificafascicolonuovo").hide();
			$("#classificafascicolonuovoPrisma").show();
			_popolaTabellato("struttura", "strutturaonuovo");
			$("#descrizionefascicolonuovo").parent().parent().hide();
			$("#oggettofascicolonuovo").val(null);
			$("#oggettofascicolonuovo").prop("readonly", true);
			$("#oggettofascicolonuovo").addClass("readonly");
			$("#linkleggifascicoloPrisma").show();
		}

		if ($('#tiposistemaremoto').val() == "JIRIDE") {
			_popolaTabellato("tipofascicolo", "tipofascicolonuovo");
			var numElementi = $('#tipofascicolonuovo > option').length;
			if (numElementi > 1) {
				$("#trtipofascicolo").show();
			}
			if (_gestioneStrutturaCompetente == 1)
				_popolaTabellatoStrutturaDaServizio();
		}

		if ($('#tiposistemaremoto').val() == 'INFOR') {
			$("#descrizionefascicolonuovo").parent().parent().hide();
			_controlloGestioneStrutturaCompetente();
			if (_gestioneStrutturaCompetente == 1) {
				_popolaTabellato("struttura", "strutturaonuovo");
				$("#sezionestruttura").show();
				$("#sezionestrutturacompetente").show();
			}
		}

		if ($('#tiposistemaremoto').val() == 'JDOC') {
			_caricamentoDatiRUP();
			$("#rigaAcronimoRup").show();
			$("#rigaNomeRup").show();
		}

		if ($('#tiposistemaremoto').val() == 'ITALPROT') {
			$("#classificafascicolonuovo").hide();
			$("#classificafascicolonuovoItalprot").show();
			$("#oggettofascicolonuovo").parent().parent().hide();
			$("#descrizionefascicolonuovo").parent().parent().hide();
			$("#trricercafascicolo").show();
			$("#linkleggifascicoliItalprot").show();
		}

		if ($('#tiposistemaremoto').val() == "ENGINEERINGDOC") {
			_popolaTabellato("struttura", "strutturaonuovo");
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			$("#sezioneuocompetenza").show();
		}

		if ($('#tiposistemaremoto').val() == "LAPISOPERA") {
			$("#annofascicolo").prop("readonly", true);
			$("#annofascicolo").addClass("readonly");
			$("#numerofascicolo").prop("readonly", true);
			$("#numerofascicolo").addClass("readonly");
			$("#codicefascicolo").prop("readonly", true);
			$("#codicefascicolo").addClass("readonly");
			$("#descrizionefascicolonuovo").parent().parent().hide();
			$("#oggettofascicolonuovo").parent().parent().hide();
			$("#classificafascicolonuovo").parent().parent().hide();
			$("#oggettofascicolo").text("");
			$("#oggettofascicolo").parent().parent().show();
			$("#classificafascicolodescrizione").parent().parent().show();
			$("#sezionestruttura").show();
			$("#sezionestrutturacompetente").show();
			$("#strutturaonuovo").hide();
			$("#struttura").show();
			if (_associaCreaFunz == false) {
				$('#documentifascicolomessaggio').text(msgNoFascicoloLapisopera);
				$('#documentifascicolomessaggio').show();
				$('#wsdmprotocollapulsante').hide();
				$('#archiviaWsdmButton').hide();
			}
		}
	}

	if ((inserimentoinfascicolo == "SI_FASCICOLO_ESISTENTE" || inserimentoinfascicolo == "SI_FASCICOLO_NUOVO") && $('#tiposistemaremoto').val() == "ARCHIFLOWFA") {
		if (inserimentoinfascicolo == "SI_FASCICOLO_ESISTENTE")
			_inizializzazioneDaOggettoGara();
		var testoOggettoDocumento = _getCodiceDellaGara($("#chiaveOriginale").val()) + " - ";
		testoOggettoDocumento += $('#oggettodocumento').val() + " - " + _oggettoGara;
		if (testoOggettoDocumento.length > 4000)
			testoOggettoDocumento = testoOggettoDocumento.substr(0, 4000);
		$('#oggettodocumento').val(testoOggettoDocumento);
	}

	if ((inserimentoinfascicolo == "SI_FASCICOLO_ESISTENTE" || inserimentoinfascicolo == "SI_FASCICOLO_NUOVO") && $('#tiposistemaremoto').val() == "ENGINEERINGDOC") {
		var testoOggettoDocumento = _getCodiceDellaGara($("#chiaveOriginale").val()) + " - ";
		testoOggettoDocumento += $('#oggettodocumento').val();
		$('#oggettodocumento').val(testoOggettoDocumento);
	}

}

/*
 * Lettura di altri dati del fascicolo direttamente dal servizio remoto
 */
function _getAltriDatiFascicolo() {
	if ($("#tipoPagina").val() == "COMUNICAZIONE") {
		$("#richiestawslogin").validate().form();
		$("#richiestainserimentoprotocollo").validate();
	}
	_getWSDMFascicolo(false, 600);
}

/*
 * Controllo valorizzazione dei campi obbligatori
 */
function controlloCampiObbligatori() {
	var errori = false;
	clearMsg();
	var arrayCampi = new Array("#username", "#password", "#ruolo", "#nome", "#cognome", "#codiceuo", "#idutente", "#idutenteunop", "#classificadocumento", "#codiceregistrodocumento",
		"#tipodocumento", "#oggettodocumento", "#mittenteinterno", "#indirizzomittente", "#mezzoinvio", "#idindice", "#idtitolazione", "#idunitaoperativamittente",
		"#oggettofascicolonuovo", "#classificafascicolonuovo", "#descrizionefascicolonuovo", "#codiceaoo_filtro", "#mezzo", "#sottotipo", "#tipofirma", "#classdoc_filtro");
	var messaggiErrori = new Array("l'utente", "la password", "il ruolo", "il nome", "il cognome", "il codice unit&agrave; organizzativa",
		"l'identificativo dell'utente", "l'identificativo utente dell'unit&agrave operativa",
		"la classifica del documento", "il codice registro del documento", " il tipo documento", "l'oggetto del documento", "il mittente interno",
		"l'indirizzo mittente", "il mezzo invio", "l'indice", "la classifica del documento", "l'unit&agrave operativa mittente",
		"l'oggetto del fascicolo", "la classifica del fascicolo", "la descrizione del fascicolo", "il codice AOO", "il mezzo", "il sottotipo", "il tipo di firma", "la classifica");

	if (_tipoWSDM == 'JDOC' || $('#tiposistemaremoto').val() == 'JDOC') {
		var nomeRUP = $("#nomeRup").text();
		var acronimo = $("#acronimoRup").text(acronimo);
		if (nomeRUP == null || nomeRUP == '') {
			outMsg("Non e' possibile procedere poiche' non sono valorizzati nome e cognome del RUP", "ERR");
			onOffMsg();
			return true;
		}
	}
	for (var i = 0; i < arrayCampi.length; i++) {
		//nel caso di ARCHIFLOW si deve controllare che sia valorizzata la stazione appaltante della gara,
		//il cui valore viene riportato nel campo #mittenteinterno che per� � nascosto
		if ((_tipoWSDM == 'ARCHIFLOW' || $('#tiposistemaremoto').val() == 'ARCHIFLOW') && i == 12) {
			var valore = $(arrayCampi[i]).val();
			if (valore == null || valore == "") {
				outMsg("Non e' possibile procedere poiche' non e' specificata la Stazione appaltante della gara", "ERR");
				onOffMsg();
				errori = true;
			}

		}

		//Nel caso di TITULUS il campo mezzoinvio non � obbligatorio, quindi va saltato il controllo
		//Nel caso di TITULUS la classifica non � obbligatoria
		if (!((_tipoWSDM == 'TITULUS' || $('#tiposistemaremoto').val() == 'TITULUS') && i == 14) && !((_tipoWSDM == 'ARCHIFLOWFA' || $('#tiposistemaremoto').val() == 'ARCHIFLOWFA') && (i == 18 || i == 19))
			&& !((_tipoWSDM == 'PRISMA' || $('#tiposistemaremoto').val() == 'PRISMA') && i == 18) && !(_tipoWSDM == 'TITULUS' && !_classificaObbligatoria && i == 19)) {
			if (verificaCampo(arrayCampi[i], messaggiErrori[i]))
				errori = true;
		}




		//nel caso di ARCHIFLOWFA l'oggetto e la classifica del fascicolo vengono caricati dal servizio 
		if ((_tipoWSDM == 'ARCHIFLOWFA' || $('#tiposistemaremoto').val() == 'ARCHIFLOWFA') && (i == 18 || i == 19)) {
			var msg = "";
			if (i == 18)
				msg = "Per valorizzare l'oggetto del fascicolo si deve leggere il fascicolo";
			else
				msg = "Per valorizzare la classifica del fascicolo si deve leggere il fascicolo";
			var valore = $(arrayCampi[i]).val();
			if (valore == null || valore == "") {
				outMsg(msg, "ERR");
				onOffMsg();
				errori = true;
			}
		}
		if ((_tipoWSDM == 'PRISMA' || $('#tiposistemaremoto').val() == 'PRISMA') && i == 18) {
			var msg = "Per valorizzare l'oggetto del fascicolo si deve leggere il fascicolo";
			var valore = $(arrayCampi[i]).val();
			if (valore == null || valore == "") {
				outMsg(msg, "ERR");
				onOffMsg();
				errori = true;
			}
		}

	}

	//nel caso di ENGINEERING la descrizione del fascicolo deve essere < 75 caratteri
	if (_tipoWSDM == 'ENGINEERING' || $('#tiposistemaremoto').val() == 'ENGINEERING') {
		if ($('#descrizionefascicolonuovo').is(':visible')) {
			var valore = $('#descrizionefascicolonuovo').val();
			if (valore.length > 75) {
				errori = true;
				outMsg("La descrizione del fascicolo non deve essere superiore a 75 caratteri", "ERR");
				onOffMsg();
			}
		}
	}

	//nel caso di ENGINEERING la descrizione del fascicolo deve essere < 75 caratteri
	if (_tipoWSDM == 'TITULUS' || $('#tiposistemaremoto').val() == 'TITULUS') {
		if ($('#oggettodocumento').is(':visible')) {
			var valore = $('#oggettodocumento').val();
			if (valore.length < 30) {
				errori = true;
				outMsg("L'oggetto dell'elemento documentale deve avere una lunghezza di almeno 30 caratteri", "ERR");
				onOffMsg();
			}
		}
		if (_ufficioObbligatorio && $('#codiceufficio_filtro').is(':visible')) {
			var valore = $('#codiceufficio_filtro').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare il codice ufficio", "ERR");
				onOffMsg();
			}
		}



	}

	//ARCHIFLOWA 
	if (_tipoWSDM == 'ARCHIFLOWFA' || $('#tiposistemaremoto').val() == 'ARCHIFLOWFA') {
		if ($('#oggettodocumento').is(':visible')) {
			var valore = $('#oggettodocumento').val();
			if (valore.length > 4000) {
				errori = true;
				outMsg("L'oggetto dell'elemento documentale deve avere una lunghezza massima di 4000 caratteri", "ERR");
				onOffMsg();
			}
		}
		//il campo supporto � obbligatorio
		if ($('#supporto').is(':visible')) {
			var valore = $('#supporto').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare il supporto", "ERR");
				onOffMsg();
			}
		}

		//il campo codice fascicolo � obbligatorio
		if ($('#codicefascicolo').is(':visible')) {
			var valore = $('#codicefascicolo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare il codice fascicolo", "ERR");
				onOffMsg();
			}
		}

		//il campo struttura � obbligatorio
		if ($('#strutturaonuovo').is(':visible')) {
			var valore = $('#strutturaonuovo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare la struttura", "ERR");
				onOffMsg();
			}
		}
	}

	//PRISMA 
	if (_tipoWSDM == 'PRISMA' || $('#tiposistemaremoto').val() == 'PRISMA') {
		//il campo struttura � obbligatorio


		if ($('#annofascicolo').is(':visible')) {
			var valore = $('#annofascicolo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare l'anno del fascicolo", "ERR");
				onOffMsg();
			}
		}

		if ($('#numerofascicolo').is(':visible')) {
			var valore = $('#numerofascicolo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare il numero del fascicolo", "ERR");
				onOffMsg();
			}
		}

		if ($('#classificafascicolonuovoPrisma').is(':visible')) {
			var valore = $('#classificafascicolonuovoPrisma').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare la classifica del fascicolo", "ERR");
				onOffMsg();
			}
		}

		if ($('#strutturaonuovo').is(':visible')) {
			var valore = $('#strutturaonuovo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare la struttura", "ERR");
				onOffMsg();
			}
		}

	}

	//ITALPROT 
	if (_tipoWSDM == 'ITALPROT' || $('#tiposistemaremoto').val() == 'ITALPROT') {
		if ($('#annofascicolo').is(':visible')) {
			var valore = $('#annofascicolo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare l'anno del fascicolo", "ERR");
				onOffMsg();
			}
		}

		if ($('#classificafascicolonuovoItalprot').is(':visible')) {
			var valore = $('#classificafascicolonuovoItalprot').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare la classifica del fascicolo", "ERR");
				onOffMsg();
			}
		}

		if ($('#listafascicoli').is(':visible')) {
			var valore = $('#listafascicoli').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare il fascicolo", "ERR");
				onOffMsg();
			}
		}



	}

	if ((_tipoWSDM == 'FOLIUM' || $('#tiposistemaremoto').val() == 'FOLIUM') && $('#categoria').parent().parent().is(':visible')) {
		var categoria = $("#categoria").val();
		var classe = $("#classe").val();
		var sottoclasse = $("#sottoclasse").val();
		var sottosottoclasse = $("#sotto-sottoclasse").val();
		var fascicoloFolium = $("#fascicoloFolium").val();
		var titolare = $("#titolare").val();
		if (categoria == null)
			categoria = "";
		if (classe == null)
			classe = "";
		if (sottoclasse == null)
			sottoclasse = "";
		if (sottosottoclasse == null)
			sottosottoclasse = "";
		if (fascicoloFolium == null)
			fascicoloFolium = "";
		if (titolare == null)
			titolare = "";

		if (categoria == "") {
			errori = true;
			outMsg("Specificare almeno il primo livello della classifica del fascicolo", "ERR");
			onOffMsg();
		} else
			popolaClassificaFoliumDaCampi(categoria, classe, sottoclasse, sottosottoclasse, fascicoloFolium, titolare);

	}

	if (_tipoWSDM == 'JIRIDE' || $('#tiposistemaremoto').val() == 'JIRIDE') {
		//var numElementi = $('#tipofascicolonuovo > option').length;
		//if(numElementi > 1){
		if ($('#tipofascicolonuovo').is(':visible')) {
			var valore = $('#tipofascicolonuovo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare il tipo del fascicolo", "ERR");
				onOffMsg();
			}
		}
		if ($('#strutturaonuovo').is(':visible')) {
			var valore = $('#strutturaonuovo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare la struttura", "ERR");
				onOffMsg();
			}
		}
		if ($('#livelloriservatezza').is(':visible')) {
			var valore = $('#livelloriservatezza').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare il livello riservatezza", "ERR");
				onOffMsg();
			}
		}
	}

	//nel caso di INFOR la descrizione del fascicolo deve essere < 75 caratteri
	if (_tipoWSDM == 'INFOR' || $('#tiposistemaremoto').val() == 'INFOR' || _tipoWSDM == 'JPROTOCOL' || $('#tiposistemaremoto').val() == 'JPROTOCOL') {
		if ($('#oggettofascicolonuovo').is(':visible')) {
			var numCaratteri = 255;
			if (_tipoWSDM == 'JPROTOCOL' || $('#tiposistemaremoto').val() == 'JPROTOCOL')
				numCaratteri = 160;
			var valore = $('#oggettofascicolonuovo').val();
			if (valore.length > numCaratteri) {
				errori = true;
				outMsg("L'oggetto del fascicolo non deve essere superiore a " + numCaratteri + " caratteri", "ERR");
				onOffMsg();
			}
		}
	}

	//nel caso di JDOC la descrizione del fascicolo deve essere <= 500 caratteri
	if (_tipoWSDM == 'JDOC' || $('#tiposistemaremoto').val() == 'JDOC') {
		if ($('#oggettodocumento').is(':visible')) {
			var valore = $('#oggettodocumento').val();
			if (valore.length > 500) {
				errori = true;
				outMsg("L'oggetto dell'elemento documentale deve avere una lunghezza massima di 500 caratteri", "ERR");
				onOffMsg();
			}
		}
		if ($('#oggettofascicolonuovo').is(':visible')) {
			var valore = $('#oggettofascicolonuovo').val();
			if (valore.length > 500) {
				errori = true;
				outMsg("L'oggetto del fascicolo non deve essere superiore a 500 caratteri", "ERR");
				onOffMsg();
			}
		}
	}

	if (_tipoWSDM == 'ENGINEERINGDOC' || $('#tiposistemaremoto').val() == 'ENGINEERINGDOC' || _tipoWSDM == 'INFOR' || $('#tiposistemaremoto').val() == 'INFOR') {
		if ($('#strutturaonuovo').is(':visible')) {
			var valore = $('#strutturaonuovo').val();
			if (valore == null || valore == "") {
				errori = true;
				outMsg("Specificare la struttura", "ERR");
				onOffMsg();
			}
		}
		if (_tipoWSDM == 'ENGINEERINGDOC' || $('#tiposistemaremoto').val() == 'ENGINEERINGDOC') {
			if ($('#uocompetenzaTxt').is(':visible')) {
				var valore = $('#uocompetenzaTxt').val();
				if (valore == null || valore == "") {
					errori = true;
					var msg = "Specificare l'unit&agrave; operativa di competenza";
					if (_fascicoliPresenti > 0)
						msg = "Specificare l'unit&agrave; operativa di competenza. Utilizzare la funzione 'Modifica U.O. di competenza' disponibile nella scheda della procedura";
					outMsg(msg, "ERR");
					onOffMsg();
				}
			}
		}
	}

	return errori;
}

/*
 * Controllo valorizzazione del campo obbligatori
 *
 */
function verificaCampo(campo, messaggio) {
	if ($(campo).is(':visible')) {
		if (_fascicoliPresenti > 0 && campo == "#descrizionefascicolonuovo")
			return true;
		var valore = $(campo).val();
		if (valore == null || valore == "") {
			outMsg("Specificare " + messaggio, "ERR");
			onOffMsg();
			return true;
		}
	}
	return false;
}

function _testURL(url, tns) {
	var _URLvalido = false;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		timeout: 3000,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetURL.do",
		data: "url=" + url + "?wsdl&tns=" + tns,
		success: function(data) {
			if (data == true) {
				_URLvalido = true;
			}
		}
	});
	return _URLvalido;
}

function _inizializzazioneCodiceFascicoloENGINEERINGDOC(codice) {
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val(); var sistema = $("#tiposistemaremoto").val();
	var tabellatiInDB = $("#tabellatiInDB").val();
	var messaggio = "Non e' stato trovato il codice del fascicolo nel file di configurazione";
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMTabellato.do",
		data: {
			codice: codice,
			sistema: sistema,
			idconfi: idconfi,
			tabellatiInDB: tabellatiInDB,
			servizio: servizio
		},
		success: function(data) {
			if (data != null && data != "") {
				$.map(data, function(item) {
					$("#codicefascicolo").val(item[0]);

				});
			} else {

				$('#documentifascicolomessaggio').text(messaggio);
				$('#documentifascicolomessaggio').show(tempo);
			}
		},
		error: function(e) {
			$('#documentifascicolomessaggio').text(messaggio);
			$('#documentifascicolomessaggio').show(tempo);
		}
	});
}

function _popolaTabellatoCodiceAoo() {
	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetWSDMListaAmministrazioniAoo.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			ruolo: $("#ruolo option:selected").val(),
			nome: $("#nome").val(),
			cognome: $("#cognome").val(),
			codiceuo: $("#codiceuo option:selected").val(),
			idutente: $("#idutente").val(),
			idutenteunop: $("#idutenteunop").val(),
			servizio: $("#servizio").val(),
			idconfi: $("#idconfi").val()

		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori della AOO
					if (json.data != null) {
						var numElementi = json.iTotalRecords;
						var vettoreDati = new Array(numElementi);
						$("#codiceaoonuovo").append($("<option/>", { value: "", text: "" }));
						$("#codiceaoonuovo_filtro").append($("<option/>", { value: "", text: "" }));
						$.map(json.data, function(item) {
							//$("#codiceaoonuovo").append($("<option/>", {value: item.codiceaoo, text: item.codiceaoo + " - " + item.descrizioneaoo }));
							vettoreDati.push(item.codiceaoo + " - " + item.descrizioneaoo);
						});
						//Ordinamento dei valori
						vettoreDati.sort();
						var codiceaoo;
						var descrizione;
						vettoreDati.forEach(function(item, index, array) {
							codiceaoo = item.split(" - ")[0];
							descrizione = item.substring(item.indexOf(" - ") + 3);
							$("#codiceaoonuovo").append($("<option/>", { value: codiceaoo, text: descrizione }));
							$("#codiceaoonuovo_filtro").append($("<option/>", { value: codiceaoo, text: item }));
						});

						//Se � presente un solo valore nel tabellato allora lo si seleziona
						if ($("#codiceaoonuovo option").length == 2) {
							$("#codiceaoonuovo option").eq(1).prop('selected', true);
							$("#codiceaoonuovo_filtro option").eq(1).prop('selected', true);

							//popolamento del tabellato degli uffici
							if ($("#inserimentoinfascicolo").val() == "SI_FASCICOLO_NUOVO") {
								_popolaTabellatoUffici();
							}
						}

						$('#amministrazioneorganizzativamessaggio').hide();
						_nowait();
					}
				} else {
					//var messaggio = "Non e' stato possibile caricare i valori del codice AOO dal servizio";
					var messaggio = json.messaggio;
					$('#amministrazioneorganizzativamessaggio').text(messaggio);
					$('#amministrazioneorganizzativamessaggio').show(tempo);
					_nowait();
					$('#codiceaoonuovo').find('option').not('[value=123]').remove();
					$('#codiceaoonuovo_filtro').empty();
					$('#codiceaoo_filtro').val('');
					$('#codiceaoo_filtro').attr('title', '');
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori del codice AOO dal servizio";
			$('#amministrazioneorganizzativamessaggio').text(messaggio);
			$('#amministrazioneorganizzativamessaggio').show(tempo);
			_nowait();
			$('#codiceaoonuovo').find('option').not('[value=123]').remove();
			$('#codiceaoonuovo_filtro').empty();
			$('#codiceaoo_filtro').val('');
			$('#codiceaoo_filtro').attr('title', '');
		}
	});
}


function _popolaTabellatoUffici() {
	var idprofiloutente = $("#idprofiloutente").val();
	var gara = _codiceGara;
	if (_isStipula)
		gara = _codiceGaraStipula;
	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetWSDMListaUffici.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			codiceaoo: $("#codiceaoonuovo option:selected").val(),
			ruolo: $("#ruolo option:selected").val(),
			servizio: $("#servizio").val(),
			idconfi: $("#idconfi").val(),
			idprofiloutente: idprofiloutente,
			gara: gara,
			tipo: "uffici"
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli uffici
					if (json.data != null) {
						var numElementi = json.iTotalRecords;
						var vettoreDati = new Array(numElementi);
						$("#codiceufficionuovo").append($("<option/>", { value: "", text: "" }));
						$("#codiceufficionuovo_filtro").append($("<option/>", { value: "", text: "" }));
						$.map(json.data, function(item) {
							//$("#codiceufficionuovo").append($("<option/>", {value: item.codice, text: item.codice + " - " + item.descrizione }));
							vettoreDati.push(item.codice + " - " + item.descrizione);
						});

						//Ordinamento dei valori
						vettoreDati.sort();
						var codiceufficio;
						var descrizione;
						vettoreDati.forEach(function(item, index, array) {
							codiceufficio = item.split(" - ")[0];
							descrizione = item.substring(item.indexOf(" - ") + 3);
							$("#codiceufficionuovo").append($("<option/>", { value: codiceufficio, text: descrizione }));
							$("#codiceufficionuovo_filtro").append($("<option/>", { value: codiceufficio, text: item }));
						});

						//Se � presente un solo valore nel tabellato allora lo si seleziona
						if ($("#codiceufficionuovo option").length == 2) {
							$("#codiceufficionuovo option").eq(1).prop('selected', true);
							$("#codiceufficionuovo_filtro option").eq(1).prop('selected', true);
							$('#codiceufficio_filtro').val($("#codiceufficionuovo option").eq(1).text());
						}

						$('#amministrazioneorganizzativamessaggio').hide();
						_nowait();
					}
				} else {
					//var messaggio = "Non e' stato possibile caricare i valori del codice AOO dal servizio";
					var messaggio = json.messaggio;
					$('#amministrazioneorganizzativamessaggio').text(messaggio);
					$('#amministrazioneorganizzativamessaggio').show(tempo);
					_nowait();
					$('#codiceufficionuovo').find('option').not('[value=123]').remove();
					$('#codiceufficionuovo_filtro').empty();
					$('#codiceufficio_filtro').val('');
					$('#codiceufficio_filtro').attr('title', '');
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori degli uffici dal servizio";
			$('#amministrazioneorganizzativamessaggio').text(messaggio);
			$('#amministrazioneorganizzativamessaggio').show(tempo);
			_nowait();
			$('#codiceufficionuovo').find('option').not('[value=123]').remove();
			$('#codiceufficionuovo_filtro').empty();
			$('#codiceufficio_filtro').val('');
			$('#codiceufficio_filtro').attr('title', '');
		}
	});
}


function caricamentoCodiceAooTITULUS() {
	if ($("#inserimentoinfascicolo").val() == "SI_FASCICOLO_NUOVO") {
		var password = $("#password").val();
		var username = $("#username").val();
		if (username != null && password != null && username != "" && password != "") {
			_popolaTabellatoCodiceAoo();
		} else {
			$('#codiceaoonuovo').find('option').not('[value=123]').remove();
			$('#codiceaoonuovo_filtro').empty();
			$('#codiceaoo_filtro').val('');
			$('#codiceaoo_filtro').attr('title', '');
		}
	}
}

function caricamentoUfficioTITULUS() {
	if ($("#inserimentoinfascicolo").val() == "SI_FASCICOLO_NUOVO") {
		var codiceaoo = $("#codiceaoonuovo option:selected").val();
		$('#codiceufficionuovo').find('option').not('[value=123]').remove();
		$('#codiceufficionuovo_filtro').empty();
		$('#codiceufficio_filtro').val('');
		if (codiceaoo != null && codiceaoo != "") {
			_popolaTabellatoUffici();
		} else {
			$('#codiceufficionuovo').find('option').not('[value=123]').remove();
			$('#codiceufficionuovo_filtro').empty();
			$('#codiceufficio_filtro').val('');
			$('#codiceufficio_filtro').attr('title', '');

		}
	}
}


/*
 * Caricamento codice ufficio intestatario 
 */
function _caricamentoCodiceUfficioIntestatario() {
	var chiave = _codiceGara;
	if (_isStipula)
		chiave = _codiceGaraStipula;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetCodiceUfficioIntestatario.do",
		data: {
			codiceGara: chiave
		},
		success: function(data) {
			if (data) {
				_ufficioIntestatario = data.cenint;
			}
		},
		error: function(e) {
			alert("Errore nella lettura del codice dell'ufficio intestatario");
		}
	});
}

/*
 * Caricamento codice ufficio intestatario 
 */
function _caricamentoIndirizzoMittenteGara() {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetIndirizzoMittenteGara.do",
		data: {
			chiave1: $("#chiaveOriginale").val()
		},
		success: function(data) {
			if (data) {
				_indirizzoMittente = data.committ;
			}
		},
		error: function(e) {
			alert("Errore nella lettura dell'indirizzo mittente");
		}
	});
}

/*
 * Caricamento codice e genere della gara/elenco 
 */
function _caricamentoCodiceGenereGara() {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetCodiceGenereGara.do",
		data: {
			chiave1: $("#chiaveOriginale").val()
		},
		success: function(data) {
			if (data) {
				_genereGara = data.genereGara;
				_codiceGara = data.codiceGara;
			}
		},
		error: function(e) {
			alert("Errore nella lettura del codice gara e del genere della gara");
		}
	});
}

function _valorizzazioneCodiceGaraLotto() {
	var tmpCodiceGara = _codiceGara;
	if (_genereGara != 1) {
		tmpCodiceGara = $("#chiaveOriginale").val();
	}
	$("#codicegaralotto").val(tmpCodiceGara);
}

/*
 * Caricamento codice ufficio intestatario 
 */
function _caricamentoCodiceCig() {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetCodiceCig.do",
		data: {
			codiceGara: _codiceGara,
			genereGara: _genereGara
		},
		success: function(data) {
			if (data) {
				$("#cig").val(data.cig);
			}
		},
		error: function(e) {
			alert("Errore nella lettura del codice cig");
		}
	});
}

function gestioneCampiLoginConfigurazione(azione, tipoCampi, tipoWSDM) {
	var visibile = false;
	if (azione == "nascondi") {
		$("#parametri" + tipoCampi + "Riga").hide();
		$("#utente" + tipoCampi + "Riga").hide();
		$("#pwd" + tipoCampi + "Riga").hide();
		$("#ruolo" + tipoCampi + "Riga").hide();
		$("#nome" + tipoCampi + "Riga").hide();
		$("#cognome" + tipoCampi + "Riga").hide();
		$("#cuo" + tipoCampi + "Riga").hide();
		$("#idUtente" + tipoCampi + "Riga").hide();
		$("#iduo" + tipoCampi + "Riga").hide();
	} else {
		$("#parametri" + tipoCampi + "Riga").show();
		$("#utente" + tipoCampi + "Riga").show();
		//Password visibile per PALEO/ENGINEERING/TITULUS/ARCHIFLOW/ENGINEERINGDOC
		if (tipoWSDM != "IRIDE" && tipoWSDM != "JIRIDE" && tipoWSDM != "INFOR" && tipoWSDM != "PROTSERVICE" && tipoWSDM != "JPROTOCOL")
			visibile = true;
		gestioneVisualizzazioneCampo("#pwd" + tipoCampi + "Riga", visibile);

		//ruolo visibile solo per PALEO/IRIDE/JIRIDE
		if (tipoWSDM == "PALEO" || tipoWSDM == "IRIDE" || tipoWSDM == "JIRIDE")
			visibile = true;
		else
			visibile = false;
		gestioneVisualizzazioneCampo("#ruolo" + tipoCampi + "Riga", visibile);

		//nome visibile solo per PALEO
		if (tipoWSDM == "PALEO")
			visibile = true;
		else
			visibile = false;
		gestioneVisualizzazioneCampo("#nome" + tipoCampi + "Riga", visibile);

		//cognome visibile solo per PALEO e LAPISOPERA
		if (tipoWSDM == "PALEO" || tipoWSDM == "LAPISOPERA")
			visibile = true;
		else
			visibile = false;
		gestioneVisualizzazioneCampo("#cognome" + tipoCampi + "Riga", visibile);

		//cuo visibile solo per PALEO
		if (tipoWSDM == "PALEO")
			visibile = true;
		else
			visibile = false;
		gestioneVisualizzazioneCampo("#cuo" + tipoCampi + "Riga", visibile);

		//idUtente visibile solo per ENGINEERING
		if (tipoWSDM == "ENGINEERING")
			visibile = true;
		else
			visibile = false;
		gestioneVisualizzazioneCampo("#idUtente" + tipoCampi + "Riga", visibile);

		//iduo visibile solo per ENGINEERING
		if (tipoWSDM == "ENGINEERING")
			visibile = true;
		else
			visibile = false;
		gestioneVisualizzazioneCampo("#iduo" + tipoCampi + "Riga", visibile);

	}
}

function gestioneVisualizzazioneCampo(campo, visibile) {
	if (visibile)
		$(campo).show();
	else
		$(campo).hide();
}

/*
 * Lettura del sistema remoto di protocollazione passando come parametro l'url
 * di configurazione del sistema remoto
 */
function getWSTipoSistemaRemoto(urlConfigurazione) {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMRemoteByUrlConfigurazione.do",
		data: "urlConfiruazione=" + urlConfigurazione,
		success: function(data) {
			if (data) {
				$.map(data, function(item) {
					$("#tiposistemaremoto").val(item[0]);
					_getTabellatiInDB();
				});
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della tipologia di sistema remoto");
		}
	});

}

/*
 * Lettura del sistema remoto di protocollazione
 */
function _getWSTipoSistemaRemoto() {
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMRemote.do",
		data: {
			servizio: servizio,
			idconfi: idconfi
		},
		success: function(data) {
			if (data) {
				$.map(data, function(item) {
					$("#tiposistemaremoto").val(item[0]);
					_getTabellatiInDB();
				});
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della tipologia di sistema remoto");
		}
	});
}

/*
 * Lettura del sistema remoto di protocollazione
 */
function _getTabellatiInDB() {
	var urlServizio = "pg/IsTabellatiWsdmPresentiInDB.do";
	var currentUrl = window.location.href;
	if (currentUrl.indexOf("/pg/") > 0) {
		urlServizio = "IsTabellatiWsdmPresentiInDB.do";
	}

	var idconfi = $("#idconfi").val();
	var tiposistemaremoto = $("#tiposistemaremoto").val();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: urlServizio,
		data: {
			idconfi: idconfi,
			sistema: tiposistemaremoto
		},
		success: function(data) {
			if (data) {
				$.map(data, function(item) {
					$("#tabellatiInDB").val(item[0]);
				});
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della tipologia di sistema remoto");
		}
	});
}

/*
 * Lettura dell'utente e degli attributi per la connessione al servizio remoto
 * nella pagina di configurazione.
 */
function _getWSLoginConfigurazione(syscon, servizio, modo, idconfi) {

	/*
	 * Lettura delle informazioni di login memorizzati nella 
	 * tabella WSLogin.
	 */
	var suffisso = "Prot";
	if (servizio == "DOCUMENTALE")
		suffisso = "Doc";
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSLogin.do",
		data: {
			syscon: syscon,
			servizio: servizio,
			idconfi: idconfi
		},
		success: function(data) {
			if (data) {
				$.map(data, function(item) {
					if (modo == "VIS")
						$("#username" + suffisso).html(item[0]);
					else
						$("#username" + suffisso).val(item[0]);

					if (item[1] != null && item[1] != "") {
						var len = item[1].length;
						if (modo == "VIS") {
							if (len > 0) {
								var str = "";
								for (var i = 0; i < len; i++)
									str += "*";
								$("#pwd" + suffisso).html(str);
							} else {
								$("#pwd" + suffisso).html(item[1]);
							}
						} else {
							$("#pwd" + suffisso).val(item[1]);
						}
					}

					if (item[2] != null && item[2] != "") {
						if (modo == "VIS") {
							//Il campo � tabellato, quindi si deve prendere la descrizione dal campo select nascosto
							$("#ruolo" + suffisso + "Select").val(item[2]).attr("selected", "selected");
							$("#ruolo" + suffisso).html($("#ruolo" + suffisso + "Select option:selected").text());
						} else
							$("#ruolo" + suffisso).val(item[2]).attr("selected", "selected");
					}

					if (modo == "VIS")
						$("#nome" + suffisso).html(item[3]);
					else
						$("#nome" + suffisso).val(item[3]);

					if (modo == "VIS")
						$("#cognome" + suffisso).html(item[4]);
					else
						$("#cognome" + suffisso).val(item[4]);

					if (item[5] != null && item[5] != "") {
						if (modo == "VIS") {
							//Il campo � tabellato, quindi si deve prendere la descrizione dal campo select nascosto
							$("#cuo" + suffisso + "Select").val(item[5]).attr("selected", "selected");
							$("#cuo" + suffisso).html($("#cuo" + suffisso + "Select option:selected").text());
						} else
							$("#cuo" + suffisso).val(item[5]).attr("selected", "selected");
					}

					if (modo == "VIS")
						$("#idUtente" + suffisso).html(item[6]);
					else
						$("#idUtente" + suffisso).val(item[6]);

					if (modo == "VIS")
						$("#iduo" + suffisso).html(item[7]);
					else
						$("#iduo" + suffisso).val(item[7]);
				});
			}
		},
		error: function(e) {
			alert("Errore durante la lettura dell'utente e dei suoi attributi");
		}
	});

}

/*
 * Validazione del form della pagina di configurazionecon le credenziali 
 * per il collegamento al servizio remoto
 */
function _validateWSLoginConfigurazione() {
	$('form[name="formProprieta"]').validate({
		rules: {
			usernameProt: "required",
			pwdProt: "required",
			nomeProt: "required",
			cognomeProt: "required",
			cuoProt: "required",
			idUtenteProt: "required",
			iduoProt: "required",
			usernameDoc: "required",
			pwdDoc: "required",
			nomeDoc: "required",
			cognomeDoc: "required",
			cuoDoc: "required",
			idUtenteDoc: "required",
			iduoDoc: "required"
		},
		messages: {
			usernameProt: "Specificare l'utente",
			pwdProt: "Specificare la password",
			nomeProt: "Specificare il nome",
			cognomeProt: "Specificare il cognome",
			cuoProt: "Specificare il codice dell'unit&agrave organizzativa",
			idUtenteProt: "Specificare l'identificativo dell'utente",
			iduoProt: "Specificare l'identificativo dell'unit&agrave; operativa",
			usernameDoc: "Specificare l'utente",
			pwdDoc: "Specificare la password",
			nomeDoc: "Specificare il nome",
			cognomeDoc: "Specificare il cognome",
			cuoDoc: "Specificare il codice dell'unit&agrave organizzativa",
			idUtenteDoc: "Specificare l'identificativo dell'utente",
			iduoDoc: "Specificare l'identificativo dell'unit&agrave; operativa"
		},
		errorPlacement: function(error, element) {
			error.insertAfter($(element));
			error.css("margin-right", "5px");
			error.css("float", "right");
			error.css("vertical-align", "top");
		}
	});

}

/*
 * Lettura del tabellato tipodocumento
 * I valori restituiti vengono filtrati in base al genere 
 */
function _popolaTabellatoTipodocumentoFiltrato() {
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMTabellato.do",
		data: {
			codice: codice,
			sistema: sistema,
			idconfi: idconfi,
			servizio: servizio,
			genereGara: _genereGara
		},
		success: function(data) {
			if (data) {
				$("#" + id).append($("<option/>", { value: "", text: "" }));
				$.map(data, function(item) {
					$("#" + id).append($("<option/>", { value: item[0], text: item[1] }));
				});

				//Se � presente un solo valore nel tabellato allora lo si seleziona
				if ($("#" + id + " option").length == 2) {
					$("#" + id + " option").eq(1).prop('selected', true);
				}

			}
		},
		error: function(e) {
			alert("Errore durante la lettura del tabellato " + nome);
		}
	});
}

/*
 * Si determina, in base al genere della gara, il codice della gara
 */
function _getCodiceDellaGara(codice) {
	var ret;
	if (_isStipula) {
		ret = codice + "(rif.gara:" + _ngaraStipula + ")";
	} else if (_genereGara == 1 || _genereGara == 3) {
		ret = _codiceGara;
	} else {
		ret = codice;
	}
	return ret;
}

/*
* Viene restituito il codice della gara associato alla stipula
*/
function _getCodiceDellaGaraDellaStipula() {
	var ret = "";
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetGaraStipula.do",
		data: {
			chiave: $("#key1").val()
		},
		success: function(data) {
			if (data) {
				_ngaraStipula = data.ngaraStipula;
				_codiceGaraStipula = data.codgarStipula;
			}
		},
		error: function(e) {
			alert("Errore nella lettura del codice della gara della stipula");
		}
	});
	return ret;
}

/*
 * Lettura della descrione del tabellato identificato da "nome".
 * Viene valorizzato l'oggetto identificato da id, che pu� essere 
 * una select(tipo=1) oppure uno span(id=2)
 * 
 */
function _setDescrizioneCodiceTabellato(nome, valore, id, tipo) {
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMDescrizioneCodiceTabellato.do",
		data: {
			nome: nome,
			servizio: servizio,
			valore: valore,
			idconfi: idconfi
		},
		success: function(data) {
			if (data) {
				if (tipo == 1)
					$("#" + id).val(data.descrizione);
				else if (tipo == 2)
					$("#" + id).text(data.descrizione);
				return data.descrizione;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura del tabellato " + nome);
		}
	});
}

function gestionemodificacampofascicolo() {
	$('#oggettofascicolonuovo').val('');
	$('#oggettofascicolo').text('');
	$('#classificafascicolonuovo').val('');
}

function gestioneletturafascicolo() {
	var codice = $("#codicefascicolo").val();
	if (codice != null && codice != "") {
		if (confirm("Confermi la rilettura dei dati del fascicolo?")) {
			var classificaOrignale;
			var strutturaOriginale;
			_getWSDMFascicolo(false, 0);
			if (_tipoWSDM == "LAPISOPERA") {
				_setWSFascicolo();
				$("#classificafascicolodescrizione").text($('#classificafascicolonuovo').val());
			}
		}
	} else {
		alert("Valorizzare il codice fascicolo");
	}

}

function gestioneletturafascicoloPrisma() {
	var anno = $("#annofascicolo").val();
	var numero = $("#numerofascicolo").val();
	var classifica = $("#classificafascicolonuovoPrisma").val();
	if (anno != null && anno != "" && numero != null && numero != "" && classifica != null && classifica != "") {
		_getWSDMFascicolo(false, 0);
	} else {
		alert("Valorizzare anno, numero e classifica del fascicolo");
	}

}

function gestionemodificacampoannofascicolo() {
	if ($('#oggettofascicolo').text() != null && $('#oggettofascicolo').text() != "") {
		$('#oggettofascicolonuovo').val('');
		$('#oggettofascicolo').text('');
		$('#classificafascicolonuovoPrisma').val('');
		$('#numerofascicolo').val('');
	}

}

function gestionemodificacamponumerofascicolo() {
	if ($('#oggettofascicolo').text() != null && $('#oggettofascicolo').text() != "") {
		$('#oggettofascicolonuovo').val('');
		$('#oggettofascicolo').text('');
		$('#classificafascicolonuovoPrisma').val('');
		$('#annofascicolo').val('');
	}

}

function gestionemodificacampoclassificafascicolo() {
	if ($('#oggettofascicolo').text() != null && $('#oggettofascicolo').text() != "") {
		$('#oggettofascicolonuovo').val('');
		$('#oggettofascicolo').text('');
		$('#annofascicolo').val('');
		$('#numerofascicolo').val('');
	}
}

function popolaClassificaFoliumDaCampi(categoria, classe, sottoclasse, sottosottoclasse, fascicoloFolium, titolare) {
	var classifica = categoria + "." + classe + "." + sottoclasse + "." + sottosottoclasse + "." + fascicoloFolium + "." + titolare;
	$('#classificafascicolonuovo').find('option').not('[value=123]').remove();
	$("#classificafascicolonuovo").append($("<option/>", { value: classifica, text: classifica }));
	$("#classificafascicolonuovo option").eq(1).prop('selected', true);
	$('#classificadocumento').find('option').not('[value=123]').remove();
	$("#classificadocumento").append($("<option/>", { value: classifica, text: classifica }));
	$("#classificadocumento option").eq(1).prop('selected', true);
}

function _popolaTabellatoStrutturaDaServizio() {
	var idprofiloutente = $("#idprofiloutente").val();
	var gara = _codiceGara;
	if (_isStipula)
		gara = _codiceGaraStipula;
	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetWSDMListaUffici.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			codiceaoo: $("#codiceaoonuovo option:selected").val(),
			ruolo: $("#ruolo option:selected").val(),
			servizio: $("#servizio").val(),
			idconfi: $("#idconfi").val(),
			idprofiloutente: idprofiloutente,
			gara: gara,
			tipo: "struttura"
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli uffici
					if (json.data != null) {
						$("#strutturaonuovo").append($("<option/>", { value: "", text: "" }));
						$.map(json.data, function(item) {
							$("#strutturaonuovo").append($("<option/>", { value: item.codice, text: item.codice + " - " + item.descrizione }));
						});

						//Se � presente un solo valore nel tabellato allora lo si seleziona
						if ($("#strutturaonuovo option").length == 2) {
							$("#strutturaonuovo option").eq(1).prop('selected', true);
						}

						$('#strutturacompetentemessaggio').hide();
						_nowait();
					}
				} else {
					//var messaggio = json.messaggio;
					//$('#strutturacompetentemessaggio').text(messaggio);
					//$('#strutturacompetentemessaggio').show(tempo);	
					_nowait();
					$('#strutturaonuovo').empty();
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori degli uffici dal servizio";
			$('#strutturacompetentemessaggio').text(messaggio);
			$('#strutturacompetentemessaggio').show(tempo);
			_nowait();
			$('#strutturaonuovo').empty();
		}
	});
}

/*
 * Verifica se e' abilitata la gestione della struttura competente
 */
function _controlloGestioneStrutturaCompetente() {
	var ret;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "wsdm.gestioneStrutturaCompetente"
		},
		success: function(data) {
			if (data) {
				_gestioneStrutturaCompetente = data.propertyWSDMCONFIPRO;
				ret = data.propertyWSDMCONFIPRO;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se e' impostata la gestione della struttura competente");
		}
	});
	return ret;
}

function caricamentoStrutturaJIRIDE() {
	if ($("#inserimentoinfascicolo").val() == "SI_FASCICOLO_NUOVO") {
		var ruolo = $("#ruolo option:selected").val();
		var username = $("#username").val();
		$('#strutturaonuovo').empty();
		if (ruolo != null && ruolo != "" && username != null && username != "") {
			_popolaTabellatoStrutturaDaServizio();
		} else {
			$('#codiceufficionuovo').empty();
		}
	}
}

function _getDescrizioneTabellatoStrutturaDaServizio() {
	var idprofiloutente = $("#idprofiloutente").val();
	var gara = _codiceGara;
	if (_isStipula)
		gara = _codiceGaraStipula;
	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetWSDMListaUffici.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			codiceaoo: $("#strutturaonuovo option:selected").val(),
			ruolo: $("#ruolo option:selected").val(),
			servizio: $("#servizio").val(),
			idconfi: $("#idconfi").val(),
			idprofiloutente: idprofiloutente,
			gara: gara,
			tipo: "struttura"
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli uffici
					if (json.data != null) {

						$('struttura').text(json.data.descrizioneufficio);

						$('#strutturacompetentemessaggio').hide();
						_nowait();
					}
				} else {
					//var messaggio = "Non e' stato possibile caricare i valori del codice AOO dal servizio";
					var messaggio = json.messaggio;
					$('#strutturacompetentemessaggio').text(messaggio);
					$('#strutturacompetentemessaggio').show(tempo);
					_nowait();

				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori degli uffici dal servizio";
			$('#strutturacompetentemessaggio').text(messaggio);
			$('#strutturacompetentemessaggio').show(tempo);
			_nowait();

		}
	});



}

/*
 * Verifica se e' abilitato il blocco in modifica dell'indirizzo mittente
 */
function _controlloBloccoModificaIndirizzoMittente() {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "wsdm.bloccoIndirizzoMittente"
		},
		success: function(data) {
			if (data) {
				_bloccoModificaIndirizzoMittenteAbilitata = data.propertyWSDMCONFIPRO;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se e' impostatil blocco per la modifica dell'indirizzo mittente");
		}
	});
}

/*
 * Verifica se e' abilitata la gestione dell'associazione documento al protocollo
 */
function _controlloAssociaDocumentiProtocollo() {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "wsdm.associaDocumentiProtocollo"
		},
		success: function(data) {
			if (data) {
				_associazioneDocumentiProtocolloAbilitata = data.propertyWSDMCONFIPRO;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se e' impostata l'associazione documenti al protocollo");
		}
	});
}

/*
 * La funzione viene adoperata per gestire i campi della pagina wslogin in modalit� contratta,
 * quindi viene richiamata ogni volta che si espande o contrae il dettaglio.
 * Nel caso di login comune i campi devono essere bloccati; tale operazione non viene fatta all'interno
 * di questa funzione, poich� verrebbe eseguita ogni volta che si espande o contrae il dettaglio.
 * Quindi al caricamento della pagina in cui � inserita la wslogin si deve effettuare la chiamata a bloccaCampiLoginComune()
 * prima di questa funzione se � attivo il login comune 
 */
function _gestioneWSLoginContratto(visibile) {
	var tiposistemaremoto = $("#tiposistemaremoto").val();

	if (!visibile) {
		$("#username").hide();
		$("#username").closest('tr').hide();
		$("#ruolo").hide();
		$("#ruolo").closest('tr').hide();
		$("#password").hide();
		$("#password").closest('tr').hide();
		$("#nome").hide();
		$("#nome").closest('tr').hide();
		$("#cognome").hide();
		$("#cognome").closest('tr').hide();
		$("#codiceuo").hide();
		$("#codiceuo").closest('tr').hide();
		$("#idutente").hide();
		$("#idutente").closest('tr').hide();
		$("#idutenteunop").hide();
		$("#idutenteunop").closest('tr').hide();
	} else {
		$("#username").show();
		$("#username").closest('tr').show();
		if (tiposistemaremoto != "JIRIDE" && tiposistemaremoto != "INFOR" && tiposistemaremoto != "PROTSERVICE" && tiposistemaremoto != "JPROTOCOL") {
			$("#password").show();
			$("#password").closest('tr').show();
		}
		if (tiposistemaremoto == "JIRIDE" || tiposistemaremoto == "PALEO") {
			if (_logincomune == "1") {
				$("#ruolo").hide();
				$("#ruolovisualizza").show();

			} else {
				$("#ruolo").show();
				$("#ruolovisualizza").hide();
			}
			$("#ruolo").closest('tr').show();
		}
		if (tiposistemaremoto == "PALEO") {
			$("#nome").show();
			$("#nome").closest('tr').show();
			$("#cognome").show();
			$("#cognome").closest('tr').show();

			if (_logincomune == "1") {
				$("#codiceuo").hide();
				$("#codiceuovisualizza").show();
			} else {
				$("#codiceuo").show();
				$("#codiceuovisualizza").hide();
			}

			$("#codiceuo").closest('tr').show();
		}
		if (tiposistemaremoto == "ENGINEERING") {
			$("#idutente").show();
			$("#idutente").closest('tr').show();
			$("#idutenteunop").show();
			$("#idutenteunop").closest('tr').show();
		}
		if (tiposistemaremoto == "LAPISOPERA") {
			$("#cognome").show();
			$("#cognome").closest('tr').show();
		}
	}
}

/**
 * Inizializzazione del widget per la gestione del filtro nella lista del codice AOO per TITULUS
 * viene sfruttato il plugin autocomplete
 */
function _inizializzaCodiceAooFiltrato() {
	$.widget("custom.comboboxAoo", {
		_create: function() {
			this.wrapper = $("<span>")
				.addClass("custom-combobox")
				.insertAfter(this.element);

			this.element.hide();
			this._createAutocomplete();
			this._createShowAllButton();
		},

		_createAutocomplete: function() {
			var selected = this.element.children(":selected"),
				value = selected.val() ? selected.text() : "";

			this.input = $("<input>")
				.appendTo(this.wrapper)
				.val(value)
				.attr({ id: "codiceaoo_filtro", name: "codiceaoo_filtro", size: "45" })
				.addClass("custom-combobox-input ui-widget ui-widget-content  ui-corner-left")
				.autocomplete({
					delay: 0,
					minLength: 0,
					source: $.proxy(this, "_source")
				})
				.tooltip({
					classes: {
						"ui-tooltip": "ui-state-highlight"
					}
				});

			this._on(this.input, {
				autocompleteselect: function(event, ui) {
					ui.item.option.selected = true;
					this._trigger("select", event, {
						item: ui.item.option
					});
					$('#codiceaoonuovo').val(ui.item.option.value);
					$('#codiceaoonuovo').trigger("change");
					$('#codiceaoo_filtro').attr("title", ui.item.option.text);
				},

				autocompletechange: "_removeIfInvalid"
			});
		},

		_createShowAllButton: function() {
			var input = this.input,
				wasOpen = false;

			$("<a>")
				.attr("tabIndex", -1)
				.attr("title", "Visualizza tutto")
				.tooltip()
				.appendTo(this.wrapper)
				.button({
					icons: {
						primary: "ui-icon-triangle-1-s"
					},
					text: false
				})
				.removeClass("ui-corner-all")
				.addClass("custom-combobox-toggle ui-corner-right")
				.on("mousedown", function() {
					wasOpen = input.autocomplete("widget").is(":visible");
				})
				.on("click", function() {
					input.trigger("focus");

					// Close if already visible
					if (wasOpen) {
						return;
					}

					// Pass empty string as value to search for, displaying all results
					input.autocomplete("search", "");
				});
		},

		_source: function(request, response) {
			var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
			response(this.element.children("option").map(function() {
				var text = $(this).text();
				if (this.value && (!request.term || matcher.test(text)))
					return {
						label: text,
						value: text,
						option: this
					};
			}));
		},

		_removeIfInvalid: function(event, ui) {

			// Selected an item, nothing to do
			if (ui.item) {
				return;
			} else {
				$('#codiceaoonuovo').val('');
				$('#codiceufficionuovo').empty();
				$('#codiceaoo_filtro').attr("title", "");
				$('#codiceufficio_filtro').val('');
				$('#codiceufficio_filtro').attr("title", "");
			}

			// Search for a match (case-insensitive)
			var value = this.input.val(),
				valueLowerCase = value.toLowerCase(),
				valid = false;
			this.element.children("option").each(function() {
				if ($(this).text().toLowerCase() === valueLowerCase) {
					this.selected = valid = true;
					return false;
				}
			});

			// Found a match, nothing to do
			if (valid) {
				return;
			}

			// Remove invalid value
			this.input
				.val("")
				.attr("title", "Codice " + value + " non trovato nell'elenco")
				.tooltip("open");
			this.element.val("");
			this._delay(function() {
				this.input.tooltip("close").attr("title", "");
			}, 2500);
			this.input.autocomplete("instance").term = "";

			//Si deve sbiancare se valorizzato codice codiceaoonuovo
			$('#codiceaoonuovo').val("");
			$('#codiceufficionuovo').empty();
			$('#codiceaoo_filtro').attr("title", "");
			$('#codiceufficio_filtro').val('');
			$('#codiceufficio_filtro').attr("title", "");

		},

		_destroy: function() {
			this.wrapper.remove();
			this.element.show();
		}
	});

	$("#codiceaoonuovo_filtro").comboboxAoo();

	$('.codiceaoo_filtro').tooltip().click(function() {
		$('.codiceaoo_filtro').tooltip("close");
	});


}


/**
 * Inizializzazione del widget per la gestione del filtro nella lista del codice ufficio per TITULUS
 * viene sfruttato il plugin autocomplete
 */
function _inizializzaCodiceUfficioFiltrato() {
	$.widget("custom.comboboxUffici", {
		_create: function() {
			this.wrapper = $("<span>")
				.addClass("custom-combobox")
				.insertAfter(this.element);

			this.element.hide();
			this._createAutocomplete();
			this._createShowAllButton();
		},

		_createAutocomplete: function() {
			var selected = this.element.children(":selected"),
				value = selected.val() ? selected.text() : "";

			this.input = $("<input>")
				.appendTo(this.wrapper)
				.val(value)
				.attr({ id: "codiceufficio_filtro", name: "codiceufficio_filtro", size: "45", height: "20px" })
				.addClass("custom-combobox-input ui-widget ui-widget-content ui-corner-left")
				.autocomplete({
					delay: 0,
					minLength: 0,
					source: $.proxy(this, "_source")
				})
				.tooltip({
					classes: {
						"ui-tooltip": "ui-state-highlight"
					}
				});

			this._on(this.input, {
				autocompleteselect: function(event, ui) {
					ui.item.option.selected = true;
					this._trigger("select", event, {
						item: ui.item.option
					});
					$('#codiceufficionuovo').val(ui.item.option.value);
					$('#codiceufficio_filtro').attr("title", ui.item.option.text);
				},

				autocompletechange: "_removeIfInvalid",

				change: function() {
					var value = $('#codiceufficio_filtro').val();
					if (value == null || value == "") {
						//E' stato sbiancato il contenuto del campo, si deve sbiancare il codice ufficio
						$('#codiceufficionuovo').val('');
					}
				}
			});
		},

		_createShowAllButton: function() {
			var input = this.input,
				wasOpen = false;

			$("<a>")
				.attr("tabIndex", -1)
				.attr("title", "Visualizza tutto")
				.tooltip()
				.appendTo(this.wrapper)
				.button({
					icons: {
						primary: "ui-icon-triangle-1-s"
					},
					text: false
				})
				.removeClass("ui-corner-all")
				.addClass("custom-combobox-toggle ui-corner-right")
				.on("mousedown", function() {
					wasOpen = input.autocomplete("widget").is(":visible");
				})
				.on("click", function() {
					input.trigger("focus");

					// Close if already visible
					if (wasOpen) {
						return;
					}

					// Pass empty string as value to search for, displaying all results
					input.autocomplete("search", "");
				});
		},

		_source: function(request, response) {
			var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
			response(this.element.children("option").map(function() {
				var text = $(this).text();
				if (this.value && (!request.term || matcher.test(text)))
					return {
						label: text,
						value: text,
						option: this
					};
			}));
		},

		_removeIfInvalid: function(event, ui) {

			// Selected an item, nothing to do
			if (ui.item) {
				return;
			}

			// Search for a match (case-insensitive)
			var value = this.input.val(),
				valueLowerCase = value.toLowerCase(),
				valid = false;

			/*
			if(value=="" || value==null){
				//E' stato sbiancato il contenuto del campo, si deve sbiancare il codice ufficio
				$('#codiceufficionuovo').val('');
				return;
			}
			*/

			this.element.children("option").each(function() {
				if ($(this).text().toLowerCase() === valueLowerCase) {
					this.selected = valid = true;
					return false;
				}
			});

			// Found a match, nothing to do
			if (valid) {
				return;
			}

			// Remove invalid value
			this.input
				.val("")
				.attr("title", "Codice " + value + " non trovato nell'elenco")
				.tooltip("open");
			this.element.val("");
			this._delay(function() {
				this.input.tooltip("close").attr("title", "");
			}, 2500);
			this.input.autocomplete("instance").term = "";

			//Si deve sbiancare il codice codiceufficio
			$('#codiceufficionuovo').val('');
		},

		_destroy: function() {
			this.wrapper.remove();
			this.element.show();
		}
	});

	$("#codiceufficionuovo_filtro").comboboxUffici();

	$('.codiceufficio_filtro').tooltip().click(function() {
		$('.codiceufficio_filtro').tooltip("close");
	});

	if (!_ufficioObbligatorio && $.fn.rules) {
		$("#codiceufficio_filtro").rules("remove");
	}

}

function _popolaTabellatoClassificaTitulus() {
	if ($("#username").val() == null || $("#username").val() == "" || $("#password").val() == null || $("#password").val() == "")
		return;

	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetWSDMListaClassifiche.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			idconfi: $("#idconfi").val(),
			tipoGara: _tipoGara,
			servizio: $("#servizio").val()

		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli uffici
					if (json.data != null) {
						//La classifica documentale deve essere allineata alla classifica del fascicolo
						$("#classificafascicolonuovo").empty();
						$('#classificadocumento').empty();
						$("#classificafascicolonuovo").append($("<option/>", { value: "", text: "" }));
						$("#classificadocumento").append($("<option/>", { value: "", text: "" }));
						$.map(json.data, function(item) {
							$("#classificafascicolonuovo").append($("<option/>", { value: item.codice + " .-+-. " + item.descrizione + " .-+-. " + item.voce, text: item.codice + " - " + item.descrizione + " (" + item.voce + ")" }));
							$("#classificadocumento").append($("<option/>", { value: item.codice }));
						});

						//Se � presente un solo valore nel tabellato allora lo si seleziona
						if ($("#classificafascicolonuovo option").length == 2) {
							$("#classificafascicolonuovo option").eq(1).prop('selected', true);
							//Si devono valorizzare i campi hidden
							var valore = $("#classificafascicolonuovo option:selected").val();
							var descrizione = $("#classificafascicolonuovo option:selected").text();
							var codice = valore.split(" .-+-. ")[0];
							var descrizione = valore.split(" .-+-. ")[1];
							var voce = valore.split(" .-+-. ")[2];
							$('#classificadocumento').val(codice);

							$("#classificadescrizione").val(descrizione);
							$("#voce").val(voce);
						}

						$('#classificafascicolonuovomessaggio').hide();
						_nowait();
					}
				} else {
					//var messaggio = "Non e' stato possibile caricare i valori del codice AOO dal servizio";
					var messaggio = json.messaggio;
					$('#classificafascicolonuovomessaggio').text(messaggio);
					$('#classificafascicolonuovomessaggio').show(tempo);
					_nowait();
					$('#classificafascicolonuovo').empty();
					$('#classificadocumento').empty();
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori delle classifiche dal servizio";
			$('#classificafascicolonuovomessaggio').text(messaggio);
			$('#classificafascicolonuovomessaggio').show(tempo);
			_nowait();
			$('#classificafascicolonuovo').empty();
		}
	});
}

function _inizializzazioneTipoGara() {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetTipoGara.do",
		data: {
			codiceGara: _codiceGara

		},
		success: function(data) {
			if (data) {
				_tipoGara = data.tipoGara;

			}
		},
		error: function(e) {
			alert("Errore nel tipo della gara");
		}
	});
}

function _controlloProtocollazioneDocBando() {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/ControlloProtocollazioneDocBando.do",
		data: {
			entita: $("#entita").val(),
			key1: $("#key1").val(),
			ngara: $("#ngara").val(),
			codgar: $("#codgar").val(),
			genereGara: $("#genereGara").val()

		},
		success: function(data) {
			if (data) {
				if (data.esito == true)
					_docTuttiNonProtocollati = true;

			}
		},
		error: function(e) {
			alert("Errore nel tipo della gara");
		}
	});
}

/*
*
* Caricamento codice ufficio intestatario 
*/
function _caricamentoDatiRUP() {
	var codrup = $("#codrup").val();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetDatiRUP.do",
		data: {
			codiceGara: _codiceGara,
			codrup: codrup
		},
		success: function(data) {
			if (data) {
				var rup = data.cognome + " " + data.nome;
				var acronimo = "";
				if (data.nome.length > 0)
					acronimo += data.nome.substring(0, 1);
				if (data.cognome.length > 0)
					acronimo += data.cognome.substring(0, 1);
				$("#nomeRup").text(rup);
				$("#acronimoRup").text(acronimo);
				$("#RUP").val(rup);
			}
		},
		error: function(e) {
			alert("Errore nella lettura del codice dell'ufficio intestatario");
		}
	});
}

/*
 * Verifica se e' obbligatorio valorizzare la classifica
 */
function _controlloObbligatorietaClassifica() {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "wsdm.obbligoClassificaFascicolo"
		},
		success: function(data) {
			if (data) {
				if (data.propertyWSDMCONFIPRO == '1')
					_classificaObbligatoria = true;
				else
					_classificaObbligatoria = false;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se e' obbligatoria la valorizzazione della classifica");
		}
	});
}

/*
 * Verifica se e' obbligatorio valorizzare il codice ufficio
 */
function _controlloObbligatorietaUfficio() {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "wsdm.obbligoUfficioFascicolo"
		},
		success: function(data) {
			if (data) {
				if (data.propertyWSDMCONFIPRO == '1')
					_ufficioObbligatorio = true;
				else
					_ufficioObbligatorio = false;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se e' obbligatoria la valorizzazione della classifica");
		}
	});
}

function oggettoDocumentoTitulus() {
	var oggettoDocumento = $("#oggettodocumento").val() + ": ";
	var oggettoGara = _oggettoGara;
	if (oggettoGara == null)
		oggettoGara = "";
	oggettoDocumento += oggettoGara;
	switch (_genereGara) {
		case 10:
			oggettoDocumento += " - Codice elenco:";
			break;
		case 11:
			oggettoDocumento += " - Codice avviso:";
			break;
		case 20:
			oggettoDocumento += " - Codice catalogo:";
			break;
		default:
			oggettoDocumento += " - Codice gara:";

	}
	var codiceGara = _getCodiceDellaGara($("#chiaveOriginale").val());
	oggettoDocumento += codiceGara;
	if (_genereGara != 10 && _genereGara != 11 && _genereGara != 20)
		oggettoDocumento += " - CIG:" + $("#cig").val();
	$("#oggettodocumento").val(oggettoDocumento);
}

/*
 * Verifica se e' obbligatorio valorizzare la classifica
 */
function _controlloLetturaMittenteServizio() {

	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "wsdm.tabellatiJiride.letdir"
		},
		success: function(data) {
			if (data) {
				if (data.propertyWSDMCONFIPRO == '1')
					_letturaMittenteDaServzio = true;
				else
					_letturaMittenteDaServzio = false;
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della configurazione per stabilire se il mittente va letto da servizio");
		}
	});
}

function _popolaTabellatoJirideMittente(tipo) {
	var idprofiloutente = $("#idprofiloutente").val();
	var gara = _codiceGara;
	if (_isStipula)
		gara = _codiceGaraStipula;
	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetWSDMListaUffici.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			codiceaoo: $("#codiceaoonuovo option:selected").val(),
			ruolo: $("#ruolo option:selected").val(),
			servizio: $("#servizio").val(),
			idconfi: $("#idconfi").val(),
			idprofiloutente: idprofiloutente,
			gara: gara,
			tipo: tipo
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli uffici
					if (json.data != null) {
						if (tipo == "mittenteinterno") {
							$("#mittenteinterno").append($("<option/>", { value: "", text: "" }));
							$.map(json.data, function(item) {
								$("#mittenteinterno").append($("<option/>", { value: item.codice, text: item.codice + " - " + item.descrizione }));
							});

							//Se e' presente un solo valore nel tabellato allora lo si seleziona
							if ($("#mittenteinterno option").length == 2) {
								$("#mittenteinterno option").eq(1).prop('selected', true);
							}
						} else {
							$("#indirizzomittente").append($("<option/>", { value: "", text: "" }));
							$.map(json.data, function(item) {
								$("#indirizzomittente").append($("<option/>", { value: item.codice, text: item.codice + " - " + item.descrizione }));
							});

							//Se e' presente un solo valore nel tabellato allora lo si seleziona
							if ($("#indirizzomittente option").length == 2) {
								$("#indirizzomittente option").eq(1).prop('selected', true);
							}
						}
						_nowait();
					}
				} else {
					//var messaggio = json.messaggio;
					//if(messaggio != "NoUtente")
					//	alert(messaggio);
					_nowait();
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori";
			if (tipo == "mittenteinterno")
				messaggio += " del mittente interno";
			else
				messaggio += " dell'indirizzo del mittente";
			alert(messaggio);
			_nowait();
		}
	});
}

function gestioneCampoPosizioneTitulus() {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWsdmConfipro.do",
		data: {
			idconfi: $("#idconfi").val(),
			chiave: "wsdm.posizioneAllegatoComunicazione"
		},
		success: function(data) {
			if (data) {
				if (data.propertyWSDMCONFIPRO == '1')
					$("#posAllegato option").eq(0).prop('selected', true);
				else
					$("#posAllegato option").eq(1).prop('selected', true);
			}
		}
	});
	$("#sezionePosizioneAllegato").show();
}

function gestioneletturafascicoliItalprot() {
	var utente = $("#username").val();
	var password = $("#password").val();
	if (utente == null || utente == "" && password == null || password == "") {
		alert("Valorizzare utente e password");
		return;
	}
	var servizio = $("#servizio").val();
	var anno = $("#annofascicolo").val();
	var classifica = $("#classificafascicolonuovoItalprot").val();
	if ((anno == null || anno == "") && (classifica == null || classifica == "")) {
		alert("Valorizzare l'anno o la classifica o entrambi");
		return;
	}
	$('#listafascicoli').empty();
	_wait();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMListaFascicoli.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			anno: anno,
			classifica: classifica,
			servizio: servizio,
			idconfi: $("#idconfi").val()
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli uffici
					if (json.data != null) {
						$("#listafascicoli").append($("<option/>", { value: "", text: "" }));
						var desc;
						var annoFascicolo;
						var classificaFascicolo;
						var codice;
						$.map(json.data, function(item) {
							annoFascicolo = item.anno;
							classificaFascicolo = item.classifica;
							desc = item.descrizione;
							codice = item.codice;
							if (desc != null && desc.length > 100)
								desc = desc.substring(0, 100) + "...";

							$("#listafascicoli").append($("<option/>", { value: codice + '§' + annoFascicolo + '§' + classificaFascicolo, text: codice + " - " + desc }));
						});

						_nowait();
					}
				} else {
					_nowait();
					var messaggio = "Non e' stato possibile caricare la lista dei fascicoli: " + json.messaggio;
					alert(messaggio);
				}
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della lista dei tabellati " + e);
		}
	});
}

function gestioneletturafascicoliLapisopera() {
	var utente = $("#username").val();
	var password = $("#password").val();
	var cognome = $("#cognome").val();
	if (utente == null || utente == "" || password == null || password == "" || cognome == null || cognome == "") {
		alert("Valorizzare utente, password e cognome");
		return;
	}
	var servizio = $("#servizio").val();
	var codicefascicolo = $("#codicefascicoloRic").val();
	var classifica = $("#classificafascicoloRic").val();
	var oggetto = $("#oggettofascicoloRic").val();
	var struttura = $("#strutturaRic").val();
	var codiceproc = $("#codiceproceduraRic").val();
	var cig = $("#cigRic").val();

	if ((codicefascicolo == null || codicefascicolo.trim() == "") && (classifica == null || classifica == "") && (oggetto == null || oggetto.trim() == "") && (struttura == null || struttura == "")
		&& (codiceproc == null || codiceproc.trim() == "") && (cig == null || cig.trim() == "")) {
		alert("Valorizzare almeno un campo fra quelli della sezione 'parametri di ricerca'");
		return;
	}
	$('#listafascicoliLapis').empty();
	_wait();
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSDMListaFascicoli.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			cognome: $("#cognome").val(),
			codicefascicolo: codicefascicolo,
			anno: "",
			classifica: classifica,
			oggetto: oggetto,
			struttura: struttura,
			codiceproc: codiceproc,
			cig: cig,
			servizio: servizio,
			idconfi: $("#idconfi").val()
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori degli uffici
					if (json.data != null) {
						$("#listafascicoliLapis").append($("<option/>", { value: "", text: "" }));
						var annoFascicolo;
						var classificaFascicolo;
						var classificaDescFascicolo;
						var oggetto;
						var codice;
						var numero;
						var struttura;
						$.map(json.data, function(item) {
							annoFascicolo = item.anno;
							classificaFascicolo = item.classifica;
							classificaDescFascicolo = item.classificaDesc;
							oggetto = item.descrizione;
							codice = item.codice;
							numero = item.numero;
							struttura = item.struttura;
							if (oggetto != null && oggetto.length > 100)
								oggetto = oggetto.substring(0, 100) + "...";

							var valore = codice + '§' + annoFascicolo + '§' + classificaFascicolo + '§' + classificaDescFascicolo + '§' + oggetto + '§' + numero + '§' + struttura;

							$("#listafascicoliLapis").append($("<option/>", { value: valore, text: codice + " - " + oggetto }));
						});

						_nowait();
					}
				} else {
					_nowait();
					var messaggio = "Non e' stato possibile caricare la lista dei fascicoli: " + json.messaggio;
					alert(messaggio);
				}
			}
		},
		error: function(e) {
			alert("Errore durante la lettura della lista dei tabellati " + e);
		}
	});
}

/*
* valore = codice + '§' + annoFascicolo + '§' + classificaFascicolo + '§' + classificaDescFascicolo + '§' + oggetto + '§' + numero + '§' + struttura;
*/

function gestioneSelezioneFascicolo(valore, tipoSistema) {
	var dati = valore.split('§');
	var codice = dati[0];
	var anno = dati[1];
	var classifica = dati[2];
	var classificaDescFascicolo = "";
	var oggetto = "";
	var numero = "";
	var struttura = "";
	if (tipoSistema == "LAPISOPERA") {
		classificaDescFascicolo = dati[3];
		oggetto = dati[4];
		numero = dati[5];
		struttura = dati[6];
	}
	$('#codicefascicolo').val(codice);
	if ($('#annofascicolo').val() == null || $('#annofascicolo').val() == "")
		$('#annofascicolo').val(anno);
	if ($("#classificafascicolonuovoItalprot").val() == null || $("#classificafascicolonuovoItalprot").val() == "")
		$('#classificafascicolonuovoItalprot').val(classifica);
	$("#classificadocumento").empty();
	$("#classificafascicolonuovo").empty();
	if (classifica != null) {
		if (classificaDescFascicolo == null || classificaDescFascicolo == "")
			classificaDescFascicolo = classifica;
		$("#classificadocumento").append($("<option/>", { value: classifica, text: classifica }));
		$("#classificadocumento option").eq(0).prop('selected', true);
		$("#classificafascicolonuovo").append($("<option/>", { value: classifica, text: classifica }));
		$("#classificafascicolonuovo option").eq(0).prop('selected', true);
	}
	if (tipoSistema == "LAPISOPERA") {
		$("#classificadescrizione").val(classificaDescFascicolo);
		$('#oggettofascicolonuovo').val(oggetto);
		$('#oggettofascicolo').text(oggetto);
		$('#classificafascicolodescrizione').text(classifica);
		$('#numerofascicolo').val(numero);
		$("#strutturaonuovo").empty();
		$("#strutturaonuovo").append($("<option/>", { value: struttura, text: struttura }));
		$("#strutturaonuovo option").eq(0).prop('selected', true);
		$('#struttura').text(struttura);
	}
}

function _popolaTabellatoClassificaNumix() {
	if ($("#username").val() == null || $("#username").val() == "" || $("#password").val() == null || $("#password").val() == "")
		return;

	_wait();
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/GetWSDMListaClassifiche.do",
		data: {
			username: $("#username").val(),
			password: $("#password").val(),
			idconfi: $("#idconfi").val(),
			tipoGara: "",
			servizio: $("#servizio").val()

		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					//json.data contiene la lista dei valori delle classifiche
					if (json.data != null) {
						$('#classificadocumento').empty();
						$('#classificadocumento_filtro').empty();
						$("#classificadocumento").append($("<option/>", { value: "", text: "" }));
						$("#classificadocumento_filtro").append($("<option/>", { value: "", text: "" }));
						$.map(json.data, function(item) {
							$("#classificadocumento").append($("<option/>", { value: item.codice, text: item.codice + " - " + item.descrizione }));
							$("#classificadocumento_filtro").append($("<option/>", { value: item.codice, text: item.codice + " - " + item.descrizione }));
						});

						//Se e' presente un solo valore nel tabellato allora lo si seleziona
						if ($("#classificadocumento option").length == 2) {
							$("#classificadocumento option").eq(1).prop('selected', true);
							$("#classificadocumento_filtro option").eq(1).prop('selected', true);
						}

						$('#classificadocumentomessaggio').hide();
						_nowait();
					}
				} else {
					//var messaggio = "Non e' stato possibile caricare i valori del codice AOO dal servizio";
					var messaggio = json.messaggio;
					$('#classificadocumentomessaggio').text(messaggio);
					$('#classificadocumentomessaggio').show(tempo);
					_nowait();
					$('#classificadocumento').empty();
					$('#classificadocumento_filtro').empty();
					$('#classdoc_filtro').val('');
					$('#classdoc_filtro').attr('title', '');
				}
			}
		},
		error: function(e) {
			var messaggio = "Non e' stato possibile caricare i valori delle classifiche dal servizio";
			$('#classificadocumentomessaggio').text(messaggio);
			$('#classificadocumentomessaggio').show(tempo);
			_nowait();
			$('#classificadocumento').empty();
			$('#classificadocumento_filtro').empty();
			$('#classdoc_filtro').val('');
			$('#classdoc_filtro').attr('title', '');
		}
	});
}

/**
 * Inizializzazione del widget per la gestione del filtro nella lista del codice ufficio per TITULUS
 * viene sfruttato il plugin autocomplete
 */
function _inizializzaClassificaDocFiltrata() {
	$.widget("custom.comboboxClassifica", {
		_create: function() {
			this.wrapper = $("<span>")
				.addClass("custom-combobox")
				.insertAfter(this.element);

			this.element.hide();
			this._createAutocomplete();
			this._createShowAllButton();
		},

		_createAutocomplete: function() {
			var selected = this.element.children(":selected"),
				value = selected.val() ? selected.text() : "";

			this.input = $("<input>")
				.appendTo(this.wrapper)
				.val(value)
				.attr({ id: "classdoc_filtro", name: "classdoc_filtro", size: "45", height: "20px" })
				.addClass("custom-combobox-input ui-widget ui-widget-content ui-corner-left")
				.autocomplete({
					delay: 0,
					minLength: 0,
					source: $.proxy(this, "_source")
				})
				.tooltip({
					classes: {
						"ui-tooltip": "ui-state-highlight"
					}
				});

			this._on(this.input, {
				autocompleteselect: function(event, ui) {
					ui.item.option.selected = true;
					this._trigger("select", event, {
						item: ui.item.option
					});
					$('#classificadocumento').val(ui.item.option.value);
					$('#classdoc_filtro').attr("title", ui.item.option.text);
				},

				autocompletechange: "_removeIfInvalid",

				change: function() {
					var value = $('#classdoc_filtro').val();
					if (value == null || value == "") {
						//E' stato sbiancato il contenuto del campo, si deve sbiancare la classifica
						$('#classificadocumento').val('');
					}
				}
			});
		},

		_createShowAllButton: function() {
			var input = this.input,
				wasOpen = false;

			$("<a>")
				.attr("tabIndex", -1)
				.attr("title", "Visualizza tutto")
				.tooltip()
				.appendTo(this.wrapper)
				.button({
					icons: {
						primary: "ui-icon-triangle-1-s"
					},
					text: false
				})
				.removeClass("ui-corner-all")
				.addClass("custom-combobox-toggle ui-corner-right")
				.on("mousedown", function() {
					wasOpen = input.autocomplete("widget").is(":visible");
				})
				.on("click", function() {
					input.trigger("focus");

					// Close if already visible
					if (wasOpen) {
						return;
					}

					// Pass empty string as value to search for, displaying all results
					input.autocomplete("search", "");
				});
		},

		_source: function(request, response) {
			var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
			response(this.element.children("option").map(function() {
				var text = $(this).text();
				if (this.value && (!request.term || matcher.test(text)))
					return {
						label: text,
						value: text,
						option: this
					};
			}));
		},

		_removeIfInvalid: function(event, ui) {

			// Selected an item, nothing to do
			if (ui.item) {
				return;
			}

			// Search for a match (case-insensitive)
			var value = this.input.val(),
				valueLowerCase = value.toLowerCase(),
				valid = false;

			/*
			if(value=="" || value==null){
				//E' stato sbiancato il contenuto del campo, si deve sbiancare il codice ufficio
				$('#codiceufficionuovo').val('');
				return;
			}
			*/

			this.element.children("option").each(function() {
				if ($(this).text().toLowerCase() === valueLowerCase) {
					this.selected = valid = true;
					return false;
				}
			});

			// Found a match, nothing to do
			if (valid) {
				return;
			}

			// Remove invalid value
			this.input
				.val("")
				.attr("title", "Codice " + value + " non trovato nell'elenco")
				.tooltip("open");
			this.element.val("");
			this._delay(function() {
				this.input.tooltip("close").attr("title", "");
			}, 2500);
			this.input.autocomplete("instance").term = "";

			//Si deve sbiancare il codice codiceufficio
			$('#classificadocumento').val('');
		},

		_destroy: function() {
			this.wrapper.remove();
			this.element.show();
		}
	});

	$("#classificadocumento_filtro").comboboxClassifica();

	$('.classdoc_filtro').tooltip().click(function() {
		$('.classdoc_filtro').tooltip("close");
	});

}

function caricamentoClassificaNumix() {

	var password = $("#password").val();
	var username = $("#username").val();
	if (username != null && password != null && username != "" && password != "") {
		_popolaTabellatoClassificaNumix();
	} else {
		$('#classificadocumento').empty()
		$('#classificadocumento_filtro').empty();
		$('#classdoc_filtro').val('');
		$('#classdoc_filtro').attr('title', '');
	}

}

function controlloFormatoAllegato(nomeFile) {
	var fileFirmato = false;
	nomeFile = nomeFile.toUpperCase();
	var dimEstentisone = formatoFileFirmatoLapis.length;
	if (formatoFileFirmatoLapis == nomeFile.substring(nomeFile.length - dimEstentisone))
		fileFirmato = true;
	return fileFirmato;
}

