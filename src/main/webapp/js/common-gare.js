/*
*	Funzioni comuni per l'applicativo gare
*
*/

// Fase Apertura documentazione amministrativa (pagina a lista)
step1Wizard="20";
// Fase Sorteggio controllo requisiti (pagina a lista)
step2Wizard="30";
// Fase Chiusura verifica doc. amministrativa (pagina a scheda)
step3Wizard="35";
// Fase Esito controllo sorteggiate (pagina a lista)
step4Wizard="40";
// Fase Conclusione comprova requisiti (pagina a scheda)
step5Wizard="45";
// Fase Valutazione tecnica (pagina a lista)
step6Wizard="50";
// Fase Chiusura valutazione tecnica (pagina a scheda)
step6_5Wizard="55";
// Fase Apertura offera economica (pagina a lista)
step7Wizard="60";
//Fase Apertura asta elettronica
step7_5Wizard="65";
// Fase Calcolo aggiudicazione (pagina a lista)
step8Wizard="70";
// Fase Aggiudicazione provvisoria (pagina a scheda) %>
step9Wizard="75";
// Fase Aggiudicazione definitiva (pagina a scheda)
step10Wizard="80";

var paginaAttivaWizard;
var currentRow;
var offtel;
var bloccoCongruo;
var aggiudicazioneProvvisoria;
var attivaValutazioneTec;
var ribcal;
var garaLottoUnico;
var punteggioTecnico;
var isProceduraTelematica;
var modlicg;
var isGaraLottiConOffertaUnica;
var esistonoDitteSenzaReqmin;
var bloccoPunteggiNonTuttiValorizzati;
var bloccoPunteggiFuoriIntervallo;
var sogliaMinimaCriteriImpostata;
var esistonoDitteSenzaPunteggio;
var messaggioControlloPunteggi;
var punteggioEconomico;
var sogliaTecnicaMinima;
var sogliaEconomicaMinima;
var contextPath;
var updateLista;
var faseCalcolata;
var rowCount;
var numeroDitteSorteggiate;
var codiceElenco;
var meruolo;
var IsW_CONFCOMPopolata;
var whereBusteAttiveWizard;
var lottoDiGara;
var bloccoAggiudicazione;
var faseGara;
var key;
var numDitteSorteggiateEsclusePaginaCorrente;
var bustalotti;
var controlloPartgarLotti;
var controlloInvoffLotti;
var codGara;
var paginaFasiGara;
var esistonoAcquisizioniOfferteDaElaborareFS11A;
var esistonoAcquisizioniOfferteDaElaborareFS11B;
var EsistonoAcquisizioniOfferteDaElaborareFS11C;
var pgAsta;
var stepgar;
var visOffertaEco;
var esitoControlloPunteggiTecSopraSogia;
var esitocontrolloRiparametrazioneTec;
var esitocontrolloRiparametrazioneEco;
var elencoLottiNonRiparam;
var genereGara;
var isVecchiaOepv;
var importoVisibile;
var garaInversa;
var modGaraInversa;
var compreq;
var sezionitec;
var numDitteStatoSoccorso;

function setImportoVisibile(importoVis){
	importoVisibile = importoVis;
}

function setPaginaAttivaWizard(paginaAttivaW){
	paginaAttivaWizard = paginaAttivaW;
}	

function setCurrentRow(valore){
	if(valore == null)
		currentRow = 0;
	else
		currentRow = valore;
}

function setOfftel(offte){
	offtel = offte;
}

function setBloccoCongruo(bloccoC){
	bloccoCongruo = bloccoC;
}

function setModalitaAggiudicazioneGara(modAggiudGara){
	modalitaAggiudicazioneGara = modAggiudGara;
}

function setAggiudicazioneProvvisoria(aggProvv){
	aggiudicazioneProvvisoria = aggProvv;
}

function setAttivaValutazioneTec(attValutazioneTec){
	attivaValutazioneTec = attValutazioneTec;
}

function setRibcal(ribCal){
	ribcal = ribCal;
}

function setGaraLottoUnico(valore){
	garaLottoUnico = valore;
}

function setPunteggioTecnico(punTec){
	punteggioTecnico = punTec;
}

function setIsProceduraTelematica(isProcTelematica){
	isProceduraTelematica = isProcTelematica;
}

function setModlicg(modlic){
	modlicg = modlic;
}

function setVecchiaOepv(vecchia){
	isVecchiaOepv = vecchia;
}

function setIsGaraLottiConOffertaUnica(isGaraLottiOffertaUnica){
	isGaraLottiConOffertaUnica = isGaraLottiOffertaUnica;
}

function setEsistonoDitteSenzaReqmin(esDitteSenzaReqmin){
	esistonoDitteSenzaReqmin = esDitteSenzaReqmin;
}

function setBloccoPunteggiNonTuttiValorizzati(bloccoPuntNonTuttiValorizzati){
	bloccoPunteggiNonTuttiValorizzati = bloccoPuntNonTuttiValorizzati;
}

function setBloccoPunteggiFuoriIntervallo(bloccoPuntFuoriIntervallo){
	bloccoPunteggiFuoriIntervallo = bloccoPuntFuoriIntervallo;
}

function setSogliaMinimaCriteriImpostata(sogliaMinCriteriImpostata){
	sogliaMinimaCriteriImpostata = sogliaMinCriteriImpostata;
}

function setEsistonoDitteSenzaPunteggio(esDitteSenzaPunteggio){
	esistonoDitteSenzaPunteggio = esDitteSenzaPunteggio;
}

function setMessaggioControlloPunteggi(msgControlloPunteggi){
	messaggioControlloPunteggi = msgControlloPunteggi;
}

function setPunteggioEconomico(puntEconomico){
	punteggioEconomico = puntEconomico;
	if(punteggioEconomico < 0)
		punteggioEconomico =0;
}

function setSogliaTecnicaMinima(valore){
	sogliaTecnicaMinima = valore;
}

function setSogliaEconomicaMinima(sogliaEcoMinima){
	sogliaEconomicaMinima = sogliaEcoMinima;
}

function setContextPath(percorso){
	contextPath = percorso;
}

function setUpdateLista(update){
	updateLista = update;
}

function setFaseCalcolata(tmp){
	faseCalcolata = tmp;
}

function setRowCount(numero){
	rowCount = numero;
	if(rowCount!=null && rowCount!="")
		rowCount = parseInt(rowCount);
	else
		rowCount = 0;
}


function setCodiceElenco(elenco){
	codiceElenco = elenco;
}

function setMeruolo(valore){
	meruolo = valore;
}

function setIsW_CONFCOMPopolata(valore){
	IsW_CONFCOMPopolata = valore;
}

function setWhereBusteAttiveWizard(valore){
	whereBusteAttiveWizard = valore;
}

function setLottoDiGara(valore){
	lottoDiGara= valore;
}

function setBloccoAggiudicazione(valore){
	bloccoAggiudicazione = valore;
}

function setBustalotti(valore){
	bustalotti = valore;
}

function setControlloPartgarLotti(valore){
	controlloPartgarLotti = valore;
}

function setControlloInvoffLotti(valore){
	controlloInvoffLotti = valore;
}

function setPaginaFasiGara(valore){
	paginaFasiGara = valore;
}

function calcoloPercentualeCauzione(ribauo){
	var ribasso = 0;
	var percauz = 10;
	ribasso=Math.abs(ribauo);
	ribasso=round(ribasso,9);
        ribasso = ribasso - 10;
	if (ribasso > 0)
		percauz += ribasso;
	ribasso = ribasso - 10;
	if (ribasso > 0)
		percauz += ribasso;
        return percauz;
}

function setFaseGara(valore){
	faseGara = valore;
}

function setKey(valore){
	key = valore;
}

function setCodGara(valore){
	codGara = valore;
}

function setEsistonoAcquisizioniOfferteDaElaborareFS11A(valore){
	esistonoAcquisizioniOfferteDaElaborareFS11A = valore;
}

function setEsistonoAcquisizioniOfferteDaElaborareFS11B(valore){
	esistonoAcquisizioniOfferteDaElaborareFS11B = valore;
}

function setEsistonoAcquisizioniOfferteDaElaborareFS11C(valore){
	esistonoAcquisizioniOfferteDaElaborareFS11C = valore;
}

function setPgAsta(valore){
	pgAsta = valore;
}

function setStepgar(valore){
	stepgar = valore;
}

function setVisOffertaEco(valore){
	visOffertaEco = valore;
}

function setEsitoControlloPunteggiTecSopraSogia(valore){
	esitoControlloPunteggiTecSopraSogia = valore;
}

function setEsitocontrolloRiparametrazioneTec(valore){
	esitocontrolloRiparametrazioneTec = valore;
}

function setEsitocontrolloRiparametrazioneEco(valore){
	esitocontrolloRiparametrazioneEco = valore;
}

function setElencoLottiNonRiparam(valore){
	elencoLottiNonRiparam = valore;
}

function setGenereGara(valore){
	genereGara = valore;
}

function setGaraInversa(valore){
	garaInversa=valore;
}

function setModGaraInversa(valore){
	modGaraInversa=valore;
}

function setCompreq(valore){
	compreq=valore;
}

function setSezionitec(valore){
	sezionitec=valore;
}

function setNumDitteStatoSoccorso(valore){
	numDitteStatoSoccorso=valore;
}

function calcolaRIBAUO(impapp, onprge, impsic, impnrl, sicinc, impoff, onsogrib) {
	var ribauo = 0;

	var den = 0;
	den = toVal(impapp) - toVal(impsic) - toVal(impnrl);
	if(onsogrib!="1")
	  den -= toVal(onprge);

	if (den <= 0) {
		ribauo = 0;
	} else {
		ribauo = toVal(impoff) - toVal(impapp);
		if (sicinc != null && sicinc=="2")
			ribauo += toVal(impsic);
		ribauo = ribauo * 100 / den;
	}
	return ribauo;
}
	
	


function conferma(){
	if(paginaAttivaWizard == step9Wizard || paginaAttivaWizard == step3Wizard || paginaAttivaWizard == step5Wizard || paginaAttivaWizard == step6_5Wizard || (paginaAttivaWizard == step7_5Wizard && pgAsta == '2')){
		document.forms[0].updateLista.value = "0";
		schedaConferma();
	}else if(paginaAttivaWizard == step8Wizard){
		listaConferma();
	}else{
		if(paginaAttivaWizard == step1Wizard && modGaraInversa=="true"){
			var dittaProvv= document.getElementById("dittaProv").value;
			var dittaDef= document.getElementById("dittAggaDef").value;
			if(dittaProvv !=null && dittaProvv !="" && (dittaDef == null || dittaDef == "")){
				var esisteAmminversa=false;
				for(var i=0; i < currentRow + 1; i++){
					var staggi = getValue("DITG_STAGGI_" + (i+1));
					var amminversa = getValue("DITG_AMMINVERSA_" + (i+1));
					var amminversaOrig = getOriginalValue("DITG_AMMINVERSA_" + (i+1));
					if(amminversa=="2" && staggi==4 && amminversa!=amminversaOrig){
						esisteAmminversa=true;
						break;
					}
				}
				if(esisteAmminversa){
					var risposta=confirm("Procedendo al salvataggio, verra' annullata la proposta di aggiudicazione della gara dal momento che, nella verifica della documentazione amministrativa dopo l'apertura delle offerte (procedura inversa), la ditta prima classificata e' risultata 'Non idonea'.\nContinuare?");
					if(!risposta)
						return;
				}
			}
		}
		if (paginaAttivaWizard >= step1Wizard && paginaAttivaWizard <= step7Wizard){
			// Riabilitazione prima del salvataggio delle combobox disabilitate all'utente
			for(var i=0; i < currentRow + 1; i++){
							
				if(document.getElementById("V_DITGAMMIS_AMMGAR_" + (i+1))!=null)
					document.getElementById("V_DITGAMMIS_AMMGAR_" + (i+1)).disabled = false;
				document.getElementById("DITG_RIBAUO_" + (i+1)).disabled = false;
				document.getElementById("DITG_IMPOFF_" + (i+1)).disabled = false;
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					document.getElementById("DITG_REQMIN_" + (i+1)).disabled = false;
				document.getElementById("DITG_PARTGAR_" + (i+1)).disabled = false;
			}
		}
	
		listaConferma();
	}
}


function inizializzaLista(){
	var numeroDitte = currentRow + 1;
	
	for(var t=0; t < numeroDitte; t++){
		if(getValue("V_DITGAMMIS_MOTIES_" + (t+1)) == 98 || getValue("V_DITGAMMIS_MOTIES_" + (t+1)) == 99){
			if (paginaAttivaWizard != step8Wizard){
				document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = true;
				document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = true;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + (t+1)).disabled = true;
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					document.getElementById("DITG_REQMIN_" + (t+1)).disabled = true;
			}
		}
		

		if(paginaAttivaWizard == step6Wizard){
		  if(getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == "2" || getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == "6"  || getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == "9"){
//				document.getElementById("DITG_PUNTEC_" + (t+1)).disabled = true;
//				document.getElementById("DITG_PARTGAR_" + (t+1)).disabled = true;
			} else {
				if(getValue("DITG_PARTGAR_" + (t+1)) == "2"){
					if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
						document.getElementById("DITG_REQMIN_" + (t+1)).disabled = true;
				}
			}
			
		}

		if(paginaAttivaWizard == step7Wizard){
			//alert("V_DITGAMMIS_AMMGAR_" + (t+1) + " = " + getValue("V_DITGAMMIS_AMMGAR_" + (t+1)));
		  if(getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == 2 || getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == "6" || getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == "9"){
//				document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = true;
//				document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = true;
//				document.getElementById("DITG_PUNECO_" + (t+1)).disabled = true;
//				document.getElementById("DITG_PARTGAR_" + (t+1)).disabled = true;
			} else {
				if(getValue("DITG_PARTGAR_" + (t+1)) == "2"){
					document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = true;
					document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = true;
					
				}
			}
			if( offtel == '1'){
				document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = true;
				document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = true;
			}
		}
		if (paginaAttivaWizard == step8Wizard && bloccoCongruo == true){
			if(getValue("STAGGI_FIT_" + (t+1)) != 6 && getValue("STAGGI_FIT_" + (t+1)) != 4 && getValue("STAGGI_FIT_" + (t+1)) != 5 && getValue("STAGGI_FIT_" + (t+1)) != 10)
				document.getElementById("DITG_CONGRUO_" + (t+1)).disabled = true;
		}
		
		if(modGaraInversa=="true"){
			gestioneVisualizzazioneCampoAmminversa(t+1);
				
		}
	}
}

function gestioneVisualizzazioneCampoAmminversa(indice){
	if((getValue("STATO_BUSTA_" + indice)=='Si' || getValue("STATO_BUSTA_" + indice)=='NonEsiste') && (getValue("DITG_FASGAR_" + indice) >= 5 || getValue("DITG_FASGAR_" + indice)=="" || getValue("DITG_FASGAR_" + indice)==null)){
		showObj("colDITG_AMMINVERSA_" + indice, true);
	}else{
		showObj("colDITG_AMMINVERSA_" + indice, false);
	}
}

function inizializzaVisualizzazioneListaModGaraInversa(){
	var numeroDitte = currentRow + 1;
	for(var t=0; t < numeroDitte; t++){
		gestioneVisualizzazioneCampoAmminversa(t+1);
	}
}

function aggiornaPerCambioAmmessaGara(){
	var objId = this.id;
	var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
	
	setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, "");
	setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, "");
	if(this.value != "2" && this.value != "6" && this.value != "9"){
		setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
		setValue("DITG_INVOFF_" + numeroRiga, "1");
		setValue("V_DITGAMMIS_MOTIES_" + numeroRiga, "");
		if(paginaAttivaWizard == step6Wizard || paginaAttivaWizard == step7Wizard){
			if( (this.value !="") && (getValue("DITG_PARTGAR_" + numeroRiga) == null || getValue("DITG_PARTGAR_" + numeroRiga) == ""))
				setValue("DITG_PARTGAR_" + numeroRiga, "1");
		}
		
		if(paginaAttivaWizard == step6Wizard){
			if(getValue("DITG_PARTGAR_" + numeroRiga)!=2){
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					document.getElementById("DITG_REQMIN_" + numeroRiga).disabled = false;
			}
		}else if(paginaAttivaWizard == step7Wizard && offtel != '1'){
			if(getValue("DITG_PARTGAR_" + numeroRiga)!=2){
				document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = false;
				document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = false;
			}
		}
		document.getElementById("DITG_PARTGAR_" + numeroRiga).disabled = false;
	} else {
		
		if( garaLottoUnico != "true" && garaLottoUnico!= true ){
			if(document.forms[0].garaLottiOmogenea.value == "true"){
				if(confirm("Confermi l'esclusione della ditta anche dagli altri lotti non ancora aggiudicati della gara?"))
					setValue("ESCLUDI_DITTA_ALTRI_LOTTI_" + numeroRiga, 1);
				else
					setValue("ESCLUDI_DITTA_ALTRI_LOTTI_" + numeroRiga, 2);
			}
		}
		
		
	}
	
	//Per le gare inverse, se la ditta viene esclusa il campo amminversa deve essere sbaincato e nascosto
	if( paginaAttivaWizard == step1Wizard && garaLottoUnico == true && garaInversa == "1"){
		if(this.value == "2" || this.value == "6" || this.value == "9"){
			setValue("DITG_AMMINVERSA_" + numeroRiga, "");
			showObj("colDITG_AMMINVERSA_" + numeroRiga, false);
		}else{
			if(getValue("STATO_BUSTA_" + numeroRiga)=='Si' || getValue("STATO_BUSTA_" + numeroRiga)=='NonEsiste')
				showObj("colDITG_AMMINVERSA_" + numeroRiga, true);	
		}
	}
}


function avanti(){
	if (isProceduraTelematica == "true" && (paginaAttivaWizard == step1Wizard || paginaAttivaWizard == step6Wizard || paginaAttivaWizard == step7Wizard) && updateLista != 1){
		if(paginaAttivaWizard == step1Wizard){
			if(esistonoAcquisizioniOfferteDaElaborareFS11A == "true" && garaInversa != "1"){
				alert("Per procedere alla fase seguente deve essere acquisita la busta amministrativa per ogni ditta nella lista");
				return;
			}
		}
		if(bustalotti==2){
			if(controlloInvoffLotti == "false" && paginaAttivaWizard == step1Wizard){
				alert("Per procedere alla fase seguente deve essere dettagliato l'invio offerta ai singoli lotti della gara per ogni ditta nella lista");
				return;
			}
		}
		
		
		var ammgarTuttiValorizzati=true;
		//Il controllo non va applicato per le gare inverse nello step step1Wizard 
		var eseguireControllo=true;
		if(garaInversa == "1" && paginaAttivaWizard == step1Wizard)
			eseguireControllo=false;
		if(rowCount>0 && eseguireControllo ){
			for(var i=0; i < currentRow + 1; i++){
				var temp = getValue("V_DITGAMMIS_AMMGAR_FITTIZIO_" + (i+1));
				if(temp==null || temp==""){
					ammgarTuttiValorizzati = false;
					break;
				}
			}
		}
		if(!ammgarTuttiValorizzati){
			alert("Per procedere alla fase seguente deve essere specificato lo stato di ammissione per ogni ditta nella lista");
			return;
		}
		
		if(numDitteStatoSoccorso != null && numDitteStatoSoccorso !="" && numDitteStatoSoccorso !="0"){
			alert("Per procedere alla fase seguente non ci devono essere ditte nella lista con soccorso istruttorio in corso");
			return;
		}
		
		if(paginaAttivaWizard == step1Wizard && compreq == '1' && garaInversa != "1" && faseGara < 5){
			var estimpValorizzati=false;
			if(rowCount>0 ){
				for(var i=0; i < currentRow + 1; i++){
					var temp = getValue("DITG_ESTIMP_FITTIZIO_" + (i+1));
					if(temp!=null && temp!=""){
						estimpValorizzati = true;
						break;
					}
				}
			}
			if(!estimpValorizzati){
				alert("Per procedere alla fase seguente deve essere fatto il sorteggio sulle ditte nella lista per la verifica requisiti");
				return;
			}
		}
	}
	
	if(isProceduraTelematica =="true" && paginaAttivaWizard == step7_5Wizard && updateLista != 1 && stepgar < step8Wizard){
		alert("Per procedere alla fase seguente deve essere conclusa l'asta.");
		return;
	}
	
	if(isProceduraTelematica =="true" && paginaAttivaWizard == step6Wizard && updateLista != 1){
		if(esistonoAcquisizioniOfferteDaElaborareFS11B == "true"){
			alert("Per procedere alla fase seguente deve essere acquisita la busta tecnica per ogni ditta nella lista");
			return;
		}
		var valoriTuttiPresenti=true;
		var msg="";
		if(modlicg=="6" || attivaValutazioneTec == "true"){
			var punteggioTecnicoMax = toVal(punteggioTecnico); 
			if(isGaraLottiConOffertaUnica != "true"){
				for(var i=0; i < currentRow + 1; i++){
					var puntec = getValue("PUNTEC_FIT_" + (i+1));
					var ammessa = getValue("DITG_AMMGAR_" + (i+1));
					var reqmin = getValue("REQMIN_FIT_" + (i+1));
					
					if(attivaValutazioneTec == "true" && (reqmin==null || reqmin=="") && bustalotti==1){
						valoriTuttiPresenti = false;
						msg="Per procedere alla fase seguente deve essere specificata la conformita' ai requisiti minimi per ogni ditta nella lista";
						break;
					}
					
					if(ammessa=="1"){
						if(attivaValutazioneTec == "true" && (reqmin==null || reqmin=="") && bustalotti!=1){
							valoriTuttiPresenti = false;
							msg="Per procedere alla fase seguente deve essere specificata la conformita' ai requisiti minimi per ogni ditta in gara";
							break;
						}
						
						if(modlicg=="6" && (puntec==null || puntec=="")){
							valoriTuttiPresenti = false;
							msg="Per procedere alla fase seguente deve essere specificato il punteggio tecnico per ogni ditta in gara.\nAttivare la funzione 'Calcolo punteggi' per eseguire il calcolo.";
							break;
						}else if(puntec != null && puntec!="" && puntec>punteggioTecnicoMax){
							valoriTuttiPresenti = false;
							msg="Per procedere alla fase seguente tutte le ditte in gara devono avere un punteggio tecnico inferiore al punteggio tecnico massimo.\nConsultare il prospetto punteggi ditte per i dettagli.";
							break;
						}
						
					}
				}
			}
			if((attivaValutazioneTec == "true" || modlicg=="6") && isGaraLottiConOffertaUnica == "true" && valoriTuttiPresenti){
				if(esistonoDitteSenzaReqmin=='true' ){
					valoriTuttiPresenti = false;
					msg="Per procedere alla fase seguente deve essere specificata la conformita' ai requisiti minimi per ogni ditta nella lista";
				}else if(esistonoDitteSenzaPunteggio=='true'){
					valoriTuttiPresenti = false;
					msg="Per procedere alla fase seguente deve essere specificato il punteggio tecnico per ogni ditta in gara";
				}else if(esitocontrolloRiparametrazioneTec == "NOK"){
					valoriTuttiPresenti = false;
					msg="Per procedere alla fase seguente deve essere eseguita la riparametrazione dei punteggi tecnici per ogni ditta in gara";
					if(elencoLottiNonRiparam.indexOf(",")>0)
						msg+="(verificare i lotti " + elencoLottiNonRiparam + ")";
					else
						msg+="(verificare il lotto " + elencoLottiNonRiparam + ")";
				}else if(messaggioControlloPunteggi!=null && messaggioControlloPunteggi!=""){
					messaggioControlloPunteggi= messaggioControlloPunteggi.replace(new RegExp("con l'apertura delle offerte economiche", 'g'),"alla fase seguente");
					messaggioControlloPunteggi= messaggioControlloPunteggi.replace(/<br>/g,"\n");
					valoriTuttiPresenti = false;
					msg=messaggioControlloPunteggi;
				}
			}
			if(!valoriTuttiPresenti){
				alert(msg);
				return;
			}
		}
		if(modlicg=="6" && valoriTuttiPresenti){
			if(esitocontrolloRiparametrazioneTec == "NOK" && isGaraLottiConOffertaUnica != "true"){
				alert("Per procedere alla fase seguente deve essere eseguita la riparametrazione dei punteggi tecnici per ogni ditta in gara.\nAttivare la funzione 'Esclusione soglia minima e riparametrazione' per eseguire il calcolo");
				return;
			}
			if(esitoControlloPunteggiTecSopraSogia == "false"){
				alert("Per procedere alla fase seguente tutte le ditte in gara devono avere un punteggio tecnico superiore alla soglia minima.\nConsultare il prospetto punteggi ditte per i dettagli.");
				return;
			}
		}
		
		if((modlicg=="6" || attivaValutazioneTec == "true") && isGaraLottiConOffertaUnica != "true"){
			if(bloccoPunteggiNonTuttiValorizzati=="true"){
				msg="Per procedere alla fase seguente tutte le ditte in gara devono avere un punteggio per ogni singolo criterio.\nConsultare il prospetto punteggi ditte per i dettagli.";
				alert(msg);
				return;
			}if(bloccoPunteggiFuoriIntervallo=="true"){
				msg="Per procedere alla fase seguente tutte le ditte in gara devono avere, per ogni singolo criterio, un punteggio inferiore al punteggio massimo";
				if(sogliaMinimaCriteriImpostata=="true")
				 msg+=" e superiore alla soglia minima";
				msg+=" del criterio stesso.\nConsultare il prospetto punteggi ditte per i dettagli.";
				alert(msg);
				return;
			}
		}	
		
		
	}
	
	
	if( paginaAttivaWizard == step7Wizard && updateLista != 1){
		if(esistonoAcquisizioniOfferteDaElaborareFS11C == "true" && isProceduraTelematica == "true"){
			alert("Per procedere alla fase seguente deve essere acquisita la busta economica per ogni ditta nella lista");
			return;
		}
		var valoriTuttiPresenti=true;
		var msg="";
		if(modlicg=="6"){
			if(esistonoDitteSenzaPunteggio=='true'){
				msg="Per procedere alla fase seguente deve essere specificato il punteggio tecnico per ogni ditta in gara.\nAttivare la funzione 'Calcolo punteggi' nella fase 'Valutazione tecnica' per eseguire il calcolo.";
				alert(msg);
				return;
			}
			var punteggioTecnicoMax = toVal(punteggioTecnico);
			var punteggioEconomicoMax = toVal(punteggioEconomico);
			for(var i=0; i < currentRow + 1; i++){
				var puneco = getValue("PUNECO_FIT_" + (i+1));
				var impoff = getValue("IMPOFF_FIT_" + (i+1));
				var ammessa = getValue("DITG_AMMGAR_" + (i+1));
				if((ammessa=="1" && isProceduraTelematica == "true") || (isProceduraTelematica != "true" && (ammessa=="1" || ammessa== null || ammessa == ""))){
					console.log(isVecchiaOepv);
					if(((puneco==null || puneco=="") || (isVecchiaOepv && (impoff=="" || impoff==null)))){
						valoriTuttiPresenti = false;
						if(importoVisibile == "true"){
							msg="Per procedere alla fase seguente deve essere specificato il punteggio economico e l'importo offerto per ogni ditta in gara.\nAttivare la funzione 'Calcolo punteggi' nella fase corrente per eseguire il calcolo.";
						}else{
							msg="Per procedere alla fase seguente deve essere specificato il punteggio economico per ogni ditta in gara.\nAttivare la funzione 'Calcolo punteggi' nella fase corrente per eseguire il calcolo.";
						}
						break;
					}else if(puneco>punteggioEconomicoMax){
						valoriTuttiPresenti = false;
						msg="Per procedere alla fase seguente tutte le ditte in gara devono avere un punteggio economico inferiore al punteggio economico massimo.\nConsultare il prospetto punteggi ditte per i dettagli.";
						break;
					}
				}
			}
			if(valoriTuttiPresenti){
				if(esitocontrolloRiparametrazioneTec == "NOK"){
					valoriTuttiPresenti = false;
					msg="Per procedere alla fase seguente deve essere eseguita la riparametrazione dei punteggi tecnici per ogni ditta in gara.\nAttivare la funzione 'Esclusione soglia minima e riparametrazione' nella fase 'Valutazione tecnica' per eseguire il calcolo.";
				}
				else if(esitocontrolloRiparametrazioneEco == "NOK"){
					valoriTuttiPresenti = false;
					msg="Per procedere alla fase seguente deve essere eseguita la riparametrazione dei punteggi economici per ogni ditta in gara.\nAttivare la funzione 'Esclusione soglia minima e riparametrazione' nella fase corrente per eseguire il calcolo.";
				}
				else if(bloccoPunteggiNonTuttiValorizzati=="true"){
					msg="Per procedere alla fase seguente tutte le ditte in gara devono avere un punteggio per ogni singolo criterio.\nConsultare il prospetto punteggi ditte per i dettagli.";
					valoriTuttiPresenti = false;
				}
				else if(messaggioControlloPunteggi!=null && messaggioControlloPunteggi!=""){
					messaggioControlloPunteggi= messaggioControlloPunteggi.replace("<HTML>","");
					messaggioControlloPunteggi= messaggioControlloPunteggi.replace(new RegExp("con l'apertura delle offerte economiche", 'g'),"alla fase seguente");
					messaggioControlloPunteggi= messaggioControlloPunteggi.replace(/<br>/g,"\n");
					valoriTuttiPresenti = false;
					msg=messaggioControlloPunteggi;
				}
			}
			
		}
		else if(modlicg=="5" || modlicg=="14" || ((modlicg=="1" || modlicg=="13" || modlicg=="17") && ribcal == "2")){
			for(var i=0; i < currentRow + 1; i++){
				var ribauo = getValue("RIBAUO_FIT_" + (i+1));
				var impoff = getValue("IMPOFF_FIT_" + (i+1));
				var ammessa = getValue("DITG_AMMGAR_" + (i+1));
				if((ammessa=="1" && isProceduraTelematica == "true") || (isProceduraTelematica != "true" && (ammessa=="1" || ammessa== null || ammessa == ""))){
					if((ribauo==null || ribauo=="") || (impoff=="" || impoff==null)){
						valoriTuttiPresenti = false;
						msg="Per procedere alla fase seguente deve essere specificato il ribasso e l'importo offerto per ogni ditta nella lista";
						if(modlicg=="17"){
							msg="Per procedere alla fase seguente deve essere specificato il rialzo e l'importo offerto per ogni ditta nella lista";
						}
						break;
					}
				}
			}
			
		}
		else if(modlicg=="1" || modlicg=="13" || modlicg=="17"){
			for(var i=0; i < currentRow + 1; i++){
				var ribauo = getValue("RIBAUO_FIT_" + (i+1));
				var ammessa = getValue("DITG_AMMGAR_" + (i+1));
				if((ammessa=="1" && isProceduraTelematica == "true") || (isProceduraTelematica != "true" && (ammessa=="1" || ammessa== null || ammessa == ""))){
					if(ribauo==null || ribauo==""){
						valoriTuttiPresenti = false;
						msg="Per procedere alla fase seguente deve essere specificato il ribasso offerto per ogni ditta nella lista";
						if(modlicg=="17"){
							msg="Per procedere alla fase seguente deve essere specificato il rialzo offerto per ogni ditta nella lista";
						}
						break;
					}
				}
			}
			
		}
		if(!valoriTuttiPresenti){
			alert(msg);
			return;
		}
		
		if(bustalotti==1 && isProceduraTelematica == "true"){
			var ammgarTuttiValorizzati=true;
			if(rowCount>0){
				for(var i=0; i < currentRow + 1; i++){
					var temp = getValue("DITG_AMMGAR_" + (i+1));
					if(temp==null || temp==""){
						ammgarTuttiValorizzati = false;
						break;
					}
				}
			}
			if(!ammgarTuttiValorizzati){
				alert("Per procedere alla fase seguente deve essere specificato lo stato di ammissione per ogni ditta nella lista");
				return;
			}
		}
	}
	
	if(document.forms[0].action.indexOf("Scheda.do") >= 0){
		document.forms[0].action = contextPath + "/Lista.do?"+csrfToken;
		if(paginaAttivaWizard == step7_5Wizard && pgAsta == "2"){
			document.forms[0].action += "&metodo=leggi";
		}
		document.forms[0].keyParent.value = document.forms[0].key.value;
	} else {
		document.forms[0].pgSort.value = "";
		document.forms[0].pgLastSort.value = "";
		document.forms[0].pgLastValori.value = "";
	}

	setValue("DIREZIONE_WIZARD", "AVANTI");
	listaVaiAPagina(0);
}

function ulterioriCampi(indiceRiga, chiaveRiga){
	var href = null;
	href = "href=gare/ditg/ditg-schedaPopup-fasiGara.jsp";
	if(updateLista == "1")
		href += "&modo=MODIFICA";
	href += "&indiceRiga=" + indiceRiga;
	href += "&key=" + chiaveRiga;
	href += "&stepWizard=" + faseCalcolata;
	href += "&paginaAttivaWizard=" + paginaAttivaWizard;
	href += "&moties=" + getValue('V_DITGAMMIS_MOTIVESCL_'+indiceRiga); //href += "&moties=" + getValue('V_DITG_MOTIES_'+indiceRiga);
	if(isGaraLottiConOffertaUnica == "true"){
		href += "&isGaraLottiConOffertaUnica=true";
		href += "&bustalotti=" + bustalotti;
	}
	href += "&sezionitec=" + sezionitec;
	href += "&offtel=" + offtel;
	href += "&isProceduraTelematica=" + isProceduraTelematica;
	openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
}

function ulterioriCampiOld(indiceRiga, chiaveRiga){
	var href = "href=gare/ditg/ditg-schedaPopup-fasiGara.jsp";
	href += "&indiceRiga=" + indiceRiga;
	href += "&key=" + chiaveRiga;
	href += "&stepWizard=" + faseCalcolata;
	href += "&paginaAttivaWizard=" + paginaAttivaWizard;
	href += "&moties=" + getValue('V_DITGAMMIS_MOTIVESCL_'+indiceRiga);
    if(updateLista == "1" && !(getValue("V_DITGAMMIS_AMMGAR_" + indiceRiga) == "3" && getValue("DITGAMMIS_AMMGAR_" + indiceRiga) != getValue("V_DITGAMMIS_AMMGAR_" + indiceRiga))){
		href += "&modo=MODIFICA";
	}
    if(isGaraLottiConOffertaUnica == "true")
		href += "&isGaraLottiConOffertaUnica=true";
	openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
}





function archivioImpresa(codiceImpresa){
	if(updateLista == 1){
		var href = ("href=gene/impr/impr-scheda-popup.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		openPopUp(href, "schedaImpresa");
	}else{
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
	}
}

function calcolaIAGPRO(valori){
	ret = 0;
	if (valori[0]!="")
		ret += eval(valori[0]);
	if (valori[1]!="") 
		ret += eval(valori[1]);
	if (valori[2] == "2") {
		if (valori[3]!="") {
			ret += eval(valori[3]);
		}
	}

	return eval(ret).toFixed(2);
}


function modelliPredispostiLocale(){
	/***********************************************************
		Copia della funzione modelliPredisposti dal file jsAzioniTags.jsp
		e addattamento al caso specifico
	 ***********************************************************/
	var entita,valori;

	try {
		if(isGaraLottiConOffertaUnica == "true"){
			if(paginaAttivaWizard == step9Wizard || paginaAttivaWizard == step10Wizard)
				entita = "GARE";
			else
				entita = "TORN";
		}else{
			entita = "GARE";
		}
		
		if(document.forms[0].key.value!=''){
			valori=document.forms[0].name+".key";
		} else if(document.forms[0].keyParent.value!=''){
			valori=document.forms[0].name+".keyParent";
		} else if(document.forms[0].keys.value!=''){
			valori=document.forms[0].name+".keys";
		}
		compositoreModelli(contextPath,entita,'',valori);
	}catch(e){
	}
}
	
function verificaDocumentiRichiesti(chiaveRiga,tipo,comunicazioniVis,documentiElenco,autorizzatoModifiche){
	var chiave = chiaveRiga;
	var href;
	if(tipo=="CONSULTAZIONE"){
		href = "href=gare/imprdocg/imprdocg-listaPopup.jsp";
		
		if(documentiElenco == "true"){
			var vetTmp = chiaveRiga.split(";");
			var dittao= vetTmp[1].substring(vetTmp[0].indexOf(":"));
			var codiceGara= "$" + codiceElenco;
			var ngara=codiceElenco;
			chiave = "DITG.CODGAR5=T:" + codiceGara + ";DITG.DITTAO=T:" + dittao + ";DITG.NGARA5=T:" + ngara;
		}
	}
	else
		href = "href=gare/imprdocg/imprdocg-scheda.jsp";
	
	href += "&key="+chiave;
	href += "&stepWizard=" + faseCalcolata;
	href += "&tipo=" + tipo;
	href += "&comunicazioniVis=" + comunicazioniVis;
	if(genereGara!=null && genereGara!="")
		href += "&genereGara=" +  genereGara;
	href += "&aut=" + autorizzatoModifiche;
	if(tipo == "CONSULTAZIONE")
		openPopUpCustom(href, "verificaDocumentiRichiesti", 1100, 550, "yes", "yes");
	else
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
	//openPopUpCustom(href, "verificaDocumentiRichiesti", 1100, 550, "yes", "yes");
}
	
//Funzione che blocca l'invio sui campi editabili
function stopRKey(evt) {
     var evt = (evt) ? evt : ((event) ? event : null);
     var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
     if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
}
    
function impostaFiltro(){
	var comando = "href=gare/commons/popup-trova-filtroDitte.jsp";
	var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
 	comando+="&dittePerPagina=" + risultatiPerPagina;
 	comando+="&codiceGara=" + codGara;
	openPopUpCustom(comando, "impostaFiltro", 850, 550, "yes", "yes");
}

function impostaFiltroLotti(){
	var comando = "href=gare/commons/popup-trova-filtroLotti.jsp";
	var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
 	comando+="&lottiPerPagina=" + risultatiPerPagina;
 	comando+="&codiceGara=" + codGara;
	openPopUpCustom(comando, "impostaFiltroLotti", 850, 550, "yes", "yes");
}

function integraCodiceCig(ngara,codgar1,isPlicoUnico,cig,numavcp){
	cig = encodeURIComponent(cig);
	var href = "href=gare/commons/popup-IntegraCodiceCig.jsp";
	href+="&ngara=" + ngara + "&codgar1=" + codgar1 + "&isPlicoUnico=" + isPlicoUnico + "&codcig=" + cig + "&numavcp=" + numavcp;
	openPopUpCustom(href, "integraCodiceCig", 700, 400, "yes", "yes");
}
	
function AnnullaFiltro(annulla){
 var comando = "href=gare/commons/popup-filtro.jsp&annulla="+annulla;
 openPopUpCustom(comando, "impostaFiltro", 750, 350, "yes", "yes");
}

function apriDettaglioPerLotto(chiave){
	var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/dettaglioOffertePerLotti-OffertaUnicaLotti.jsp";
	href += "&chiave=" + chiave + "&paginaAttivaWizard=" + paginaAttivaWizard;
			
	document.location.href = href;
}
	
function apriPopupProspettoPunteggi(chiave){
	var ngara = chiave.substring(chiave.indexOf(":")+1);
	var href="href=gare/gare/popup-gare-prospettoPunteggi.jsp";
	href+="?ngara="+ngara;
	openPopUpCustom(href, "prospettoPunteggi", 750, 690, "yes", "yes");
}

function inviaComunicazione(chiaveRiga,inviaComunicazioneAbilitato,idconfi){
	if(inviaComunicazioneAbilitato=="NoPec"){
		alert("Non e' possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC specificato in anagrafica");
		return;
	}else if(inviaComunicazioneAbilitato=="NoPecRTI"){
		alert("Non e' possibile procedere.\nLa mandataria del raggruppamento selezionato non ha un indirizzo PEC specificato in anagrafica");
		return;
	}else if(inviaComunicazioneAbilitato=="NoMail"){
		alert("Non e' possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC o E-mail specificato in anagrafica");
		return;
	}else if(inviaComunicazioneAbilitato=="NoMailRTI"){
		alert("Non e' possibile procedere.\nLa mandataria del raggruppamento selezionato non ha un indirizzo PEC o E-mail specificato in anagrafica");
		return;
	}else if(inviaComunicazioneAbilitato=="NoMailFax"){
		alert("Non e' possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC o E-mail o un numero fax specificato in anagrafica");
		return;
	}else if(inviaComunicazioneAbilitato=="NoMailFaxRTI"){
		alert("Non e' possibile procedere.\nLa mandataria del raggruppamento selezionato non ha un indirizzo PEC o E-mail o un numero fax specificato in anagrafica");
		return;
	}else if(inviaComunicazioneAbilitato=="NoMandatariaRTI"){
		alert("Non e' possibile procedere.\nNell'anagrafica del raggruppamento selezionato non e' specificata la mandataria");
		return;
	}else{
		var chiaveVet= chiaveRiga.split(";");
		var codiceDitta=chiaveVet[1].substring(chiaveVet[1].indexOf(":")+1);
		var numeroGara = chiaveVet[2].substring(chiaveVet[1].indexOf(":")+1);
		var codiceGara = chiaveVet[0].substring(chiaveVet[1].indexOf(":")+2);
		if((bustalotti == 1 && paginaFasiGara == "aperturaOffAggProvLottoOffUnica" ) || (bustalotti == 2 && paginaFasiGara == "aggiudProvDefOffertaUnica"))
			numeroGara = codiceGara;
		
		if(IsW_CONFCOMPopolata == "true"){
			href = contextPath + "/pg/InitNuovaComunicazione.do?genere=4&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara; 
			if(isGaraLottiConOffertaUnica != "true" && !(bustalotti == 1 && paginaFasiGara == "aperturaOffAggProvLottoOffUnica")){
				href += "&keyParent=GARE.NGARA=T:" + numeroGara;
			}else{
				href += "&keyParent=TORN.CODGAR=T:" + numeroGara;
			}
			href += "&ditta=" + codiceDitta + "&stepWizard=" + faseCalcolata + "&whereBusteAttiveWizard=" + whereBusteAttiveWizard;
		}else{
			var href = contextPath + "/Lista.do?numModello=0&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara ;
			if(isGaraLottiConOffertaUnica != "true" && !(bustalotti == 1 && paginaFasiGara == "aperturaOffAggProvLottoOffUnica")){
				href += "&keyParent=GARE.NGARA=T:" + numeroGara;
			}else{
				href += "&keyParent=TORN.CODGAR=T:" + numeroGara;
			}
			href += "&ditta=" + codiceDitta+ "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp";
		}
		if(lottoDiGara == "true" && garaLottoUnico!= "true" && garaLottoUnico!= true ){
			var entitaWSDM="TORN";
			var chiaveWSDM=codiceGara;
		}else{
			var entitaWSDM="GARE";
			var chiaveWSDM=numeroGara;
		}
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM+"&"+csrfToken;
		if(idconfi){href+="&idconfi=" + idconfi;}
		document.location.href = href;
	}
	
	
}	


function listaDitteConsorziate(chiaveRiga){
	var bloccoPagina=false;	
	if(faseGara!=null || faseGara!="")
		faseGara = parseInt(faseGara);
	else
		faseGara = 0;
	if(bloccoAggiudicazione=="1" || (isProceduraTelematica=="true" && faseGara>=5))
		bloccoPagina=true;			
	var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gene/ragdet/ragdet-lista.jsp";
	href += "&key=" + chiaveRiga+ "&bloccoPagina=" + bloccoPagina;
	document.location.href = href;
}

function confermaChiusuraAperturaFasi(operazione,bustalotti){
	var chiave=key;
	var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
	var href = "href=gare/commons/popupChiusuraAperturaFasi.jsp&ngara=" + ngara + "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica + "&operazione=" + operazione;
	href += "&bustalotti=" + bustalotti + "&isProceduraTelematica=" + isProceduraTelematica + "&paginaAttivaWizard=" + paginaAttivaWizard;
	openPopUpCustom(href, "conferma_chiusura_apertura_fasi", "450", "350", "no", "no");
}

function attivaAperturaOfferte(){
	setValue("CHIUSURA_APERTURA_FASI", "ATTIVA");
	if(isGaraLottiConOffertaUnica == "true"){
		document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
		document.forms[0].entita.value="TORN";
		var tmpKey = document.forms[0].key.value;
		document.forms[0].key.value= "TORN.CODGAR=T:" + tmpKey.substr(tmpKey.indexOf(":")+1);
	}
	document.forms[0].modo.value = "MODIFICA";
	schedaConferma();
}

function disattivaAperturaOfferte(){
	setValue("CHIUSURA_APERTURA_FASI", "DISATTIVA");
	if(isGaraLottiConOffertaUnica == "true"){
		document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
		document.forms[0].entita.value="TORN";
		var tmpKey = document.forms[0].key.value;
		document.forms[0].key.value= "TORN.CODGAR=T:" + tmpKey.substr(tmpKey.indexOf(":")+1);
	}
	document.forms[0].modo.value = "MODIFICA";
	schedaConferma();
}

function dettaglioPartecipazioneLotti(chiaveRiga, bustalotti){
	var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioPartecipazioneDittaPerLotti.jsp";
	href += "&key=" + chiaveRiga + "&stepWizard=" + paginaAttivaWizard + "&bustalotti=" + bustalotti;
	href += "&isProceduraTelematica=" + isProceduraTelematica + "&faseGara=" + faseGara;
	document.location.href = href;
}

function dettaglioAmmissioneLotti(chiaveRiga, bustalotti){
	var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioAmmissioneDittaPerLotti.jsp";
	href += "&key=" + chiaveRiga + "&stepWizard=" + paginaAttivaWizard + "&bustalotti=" + bustalotti;
	href += "&isProceduraTelematica=" + isProceduraTelematica + "&faseGara=" + faseGara;
	document.location.href = href;
}

function aperturaBuste(chiaveRiga,tipoBusta) {
	var vetTmp = chiaveRiga.split(";");
	var dittao= vetTmp[1].substring(vetTmp[0].indexOf(":"));
	var ngara = vetTmp[2].substring(vetTmp[0].indexOf(":"));
	var codiceGara = vetTmp[0].substring(vetTmp[0].indexOf(":") + 1);
	var pagina="";
	if(tipoBusta=="FS11A")
		pagina="gare-popup-attivaaperturadoc.jsp";
	else if(tipoBusta=="FS11B" || tipoBusta=="FS11B-QL" || tipoBusta=="FS11B-QN" )
		pagina="gare-popup-acquisizioniBusteTecniche.jsp";
	else if(tipoBusta=="FS11C")
		pagina="gare-popup-acquisizioniBusteEconomiche.jsp";
	else 
		pagina="gare-popup-acquisizioniBustePrequalifica.jsp";
	var tmpBustalotti=0;
	if(bustalotti!=null)
		tmpBustalotti = bustalotti;
	href = "href=gare/gare/" + pagina + "&ngara=" + ngara + "&codgar=" + codiceGara + "&dittao=" + dittao + "&bustalotti=" + tmpBustalotti;
	if(tipoBusta=="FS11A"){
		href += "&garaInversa=" + garaInversa;
	}
	if(tipoBusta=="FS11B-QL"){
		href += "&sez=1";
	}
	if(tipoBusta=="FS11B-QN"){
		href += "&sez=2";
	}
	openPopUpCustom(href, "attivaaperturadoc", 500, 350, "yes", "yes");
}

function AttivaAperturaEconomiche() {
	var chiave=key;
	var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
	if( paginaAttivaWizard == step6_5Wizard){
		var fase=50;
		var attivaValTec= attivaValutazioneTec;
	}else{
		var fase=60;
		var attivaValTec= "false";
	}
	
	href = "href=gare/gare/gare-popup-attivaAperturaEconomiche.jsp&ngara=" + ngara + "&codgar=" + codGara + "&modlicg="+modlicg + "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica + "&fase="+fase;
	href += "&attivaValutazioneTec=" + attivaValTec;
	if(bustalotti!=null)
		href += "&bustalotti=" + bustalotti;
	else
		href += "&bustalotti=";
	if( paginaAttivaWizard == step3Wizard){
		href += "&attivaValTec=2";
	}
	href += "&visOffertaEco=" + visOffertaEco;
	openPopUpCustom(href, "attivaaperturaeconomiche", 450, 350, "yes", "yes");
}

function verificaPassword(pwd, pwd1, tipo){
	var caratteriAmmessi = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	var index = 0;
  	var result = true;
  	var msgTipo =" per la busta amministrativa";
  	if(tipo=="B")
  		msgTipo =" per la busta tecnica";
  	else if(tipo=="C")
  		msgTipo =" per la busta economica";
  	else if(tipo=="A0")
  		msgTipo =" per la busta di prequalifica";
  	if(pwd==null || pwd == "" ){
  		alert("Specificare la password" + msgTipo);
  		return -1;
  	}
  	if(pwd1 == null || pwd1 == ""){
  		alert("Reinserire per conferma la password" + msgTipo);
  		return -1;
  	}
  	while(index < pwd.length & result){
	  	if(caratteriAmmessi.indexOf(pwd.charAt(index)) < 0){
		  	result = false;
	  		alert('Errore: \"'+ pwd.charAt(index) + '\" carattere non ammesso nel campo password' + msgTipo);
	  		return -1;
	  	} else {
	  		index = index+1;
	  	}
	  }
	if(pwd.length < 8){
		alert('Errore: numero minimo di caratteri nel campo password non raggiunto \nLunghezza minima 8 caratteri' + msgTipo);
		return -1;
	}
	if(pwd!=pwd1){
		alert("I valori inseriti nei campi 'Password' e  'Conferma password'" +msgTipo +" non coincidono");
		return -1;
	}
	return 1;
}


/*
 * La funzione effettua il calcolo di IMPGAR alla modifica di IMPQUA,
 * in particolare se numeroIterazioni e' valorizzato e positivo allora 
 * si deve ciclare perche' richiamato da sezioni dinamiche, se invece
 * vale 0 non si deve fare nulla, se vale -1 allora la funzione viene
 * richiamata da una sezione non dinamica
 */
function aggiornaImpgarDaImpqua(impqua,accqua,numeroIterazioni){
	if(accqua==1){
		var impapp = getValue("GARE_IMPAPP");
		var impquaOriginale = getOriginalValue("GARECONT_IMPQUA");
		if(parseFloat(impqua) > parseFloat(impapp) && impquaOriginale!=impqua){
			if(!confirm("L'importo specificato supera l'importo a base di gara.\nConfermi la modifica?")){
			 setValue("GARECONT_IMPQUA",impquaOriginale);
			 return ;
			}
		}
		
		if(numeroIterazioni==-1 || numeroIterazioni>0){
			var onprge = getValue("GARE_ONPRGE");
			var impsic = getValue("GARE_IMPSIC");
			var impnrl = getValue("GARE_IMPNRL");
			var sicinc = getValue("GARE_SICINC");
			var onsogrib = getValue("GARE_ONSOGRIB");
			var modlicg=getValue("GARE_MODLICG");
			
			var valoreMaxIterazioni = numeroIterazioni;
			if(numeroIterazioni==-1)
				valoreMaxIterazioni =1;
			for (var i=1; i<=valoreMaxIterazioni;i++ ){
				var campoRibauo = "DITG_RIBAUO_" + i;
				var campoImpoff = "DITG_IMPOFF_" + i;
				var campoRidiso = "DITGAQ_RIDISO_" + i;
				var campoImpgar = "DITGAQ_IMPGAR_" + i;
				
				if(numeroIterazioni==-1){
					campoRibauo = "DITG_RIBAUO";
					campoImpoff = "DITG_IMPOFF";
					campoRidiso = "GARE_RIDISO";
					campoImpgar = "GARE_IMPGAR";
				}
				var importoGaranzia = 0;
				var ribauo = getValue(campoRibauo);
				if(impqua >0){
					if(ribauo == null || ribauo == "" || modlicg==6){
						var impoff = getValue(campoImpoff);
						ribauo = calcolaRIBAUO(impapp,onprge,impsic,impnrl,sicinc,impoff,onsogrib);
						ribauo = parseFloat(ribauo);
					}
					var percauz = calcoloPercentualeCauzione(ribauo);
					importoGaranzia = round((impqua * parseFloat(percauz)) / 100, 2);
				}
				var ridiso = getValue(campoRidiso);
			
				if (ridiso==1) {
					importoGaranzia = importoGaranzia / 2;
				}
				setValue(campoImpgar,toMoney(importoGaranzia));
			}
			
		}
		
	}
}

function EvadiIdsAssociati() {
	var codiceGara = getValue("GARE_CODGAR1");
	href = "href=gare/gare/gare-popup-evadiidsassociati.jsp&codiceGara=" + codiceGara + "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica;
	openPopUpCustom(href, "evadiidsassiciati", 450, 350, "yes", "yes");
}

//Se RIDISO = 1 si dimezza il valore di IMPGAR
//Se RIDISO = 2 si raddoppia il valore di IMPGAR 
function calcolaIMPGAR(){
	var newRidiso = getValue("GARE_RIDISO");
	var oldRidiso = ridisoPrec;
	var importoCauzione = toVal(getValue("GARE_IMPGAR"));
	
	if(importoCauzione == null ||importoCauzione=="") importoCauzione=0;
	importoCauzione=parseFloat(importoCauzione);
	
	if (newRidiso == null || newRidiso=="") newRidiso=2;
	if (oldRidiso == null || oldRidiso=="") oldRidiso=2;
	
	if (newRidiso != oldRidiso) {
		if (newRidiso==1) {
			importoCauzione = importoCauzione / 2;
		}else{ 
			importoCauzione = importoCauzione * 2;
		}
	}
	ridisoPrec = newRidiso;
	return toMoney(importoCauzione);
}

//La funzione aggiorna il valore dell'importo cauzione in funzione di RIDISO
//Se ridiso=1 si dimezza l'importo cauzione, altrimenti si raddoppia
function aggiornaImportoCauzione(modcont){
	var newRidiso = getValue("GARE_RIDISO");
	var oldRidiso = ridisoPrec;
	var nomeCampoImporto = "CAUZIONE";
	if(modcont=='1')
		nomeCampoImporto = "GARE_IMPGAR";
	
	var importoCauzione = getValue(nomeCampoImporto);
	
	if(importoCauzione == null ||importoCauzione=="") importoCauzione=0;
	importoCauzione=parseFloat(importoCauzione);
	
	if (newRidiso == null || newRidiso=="") newRidiso=2;
	if (oldRidiso == null || oldRidiso=="") oldRidiso=2;
	
	if (newRidiso != oldRidiso) {
		if (newRidiso==1) {
			importoCauzione = importoCauzione / 2;
		}else{ 
			importoCauzione = importoCauzione * 2;
		}
		setValue(nomeCampoImporto,  round(eval(importoCauzione), 5));
	}
	ridisoPrec = newRidiso;
}

function apriDocumento(doc){
	var w = 700;
	var h = 500;
	var l = Math.floor((screen.width-w)/2);
	var t = Math.floor((screen.height-h)/2);
	window.open(doc, "doc", "toolbar=no,menubar=no,width=" + w + ",height=" + h + ",top=" + t + ",left=" + l + ",resizable=yes,scrollbars=yes");
}

function apriPopupInviaInvito(ngara,codgar,integrazioneWSDM,idconfi){
	var href = "href=gare/gare/fasiGara/popupInviaInvito.jsp?codgar=" + codgar + "&ngara=" + ngara + "&step=1&bustalotti=" + bustalotti;
	if(idconfi){href+="&idconfi="+idconfi;}
	var dim1=850;
	var dim2=600;
	if(integrazioneWSDM =='1'){
		var entita="GARE";
		href += "&entita=GARE";
	}
	openPopUpCustom(href, "inviaInvito", dim1, dim2, "no", "yes");
}

function concludiAsta(ngara){
	var href="href=gare/gare/fasiGara/popupConcludiAsta.jsp&ngara=" + ngara ;
	openPopUpCustom(href, "concludiAsta", 600, 300, "yes", "yes");
}

function dettaglioAvvalimento(chiaveRiga){
	var bloccoPagina=false;	
	if(faseGara!=null || faseGara!="")
		faseGara = parseInt(faseGara);
	else
		faseGara = 0;
	if(bloccoAggiudicazione=="1" || (isProceduraTelematica=="true" && faseGara>=5))
		bloccoPagina=true;			
	
	var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioAvvalimentoDitta.jsp";
	href += "&key=" + chiaveRiga + "&bloccoPagina=" + bloccoPagina;
	document.location.href = href;
}

/*
 *  Si verifica se un valore e' contenuto in una stringa del tipo: aa,bb,cc,...
 */
function valoreInStringa(valore,stringa){
	var trovato=false;
	if(stringa!=null && stringa!=""){
		var tmp = stringa.split(",");
		if(tmp.length>1){
			for(i=0;i<tmp.length;i++){
				if(tmp[i]==valore){
					trovato = true;
					break;
				}
			}
		}else{
			if(tmp[0]==valore)
				trovato = true;
		}
	}
	return trovato;
}

function getValCampoChiave(valori, campo) {
    var vals = valori.split(";");
    // Scorro tutti i valori per ricercare i voluti
    for (i = 0; i < vals.length; i++) {
      if (vals[i].indexOf('=') >= 0) {
        var nomeCampo = vals[i].substring(0, vals[i].indexOf('='));
        if (campo.indexOf('.') < 0) {
          nomeCampo = nomeCampo.substring(nomeCampo.indexOf('.') + 1);
        }
        // Se il nome e' quello cercato allora restituisco il valore
        if (nomeCampo==campo) {
          var valoreCampo = vals[i].substring(vals[i].indexOf('=') + 1);
          if (valoreCampo.length > 1 && valoreCampo.charAt(1) == ':')
            valoreCampo = valoreCampo.substring(2);
          return valoreCampo;
        }
      }
    }
    return "";
  }

function checkDatiRichiestaOfferta(dataOfferta, oraPresentazioneOfferte, dataTermineRichiestaPartecipazione, oraTerminePresentazioneOfferta, messaggio){
	var dataDiConfronto = dataTermineRichiestaPartecipazione.split("/");
	var valoreCampo = ("" + dataOfferta).split("/");
	var dataDiRiferimento = new Date(dataDiConfronto[2], dataDiConfronto[1] - 1, dataDiConfronto[0]);
	var data = new Date(valoreCampo[2], valoreCampo[1] - 1, valoreCampo[0]);
	
	var msg = messaggio + " (" + dataTermineRichiestaPartecipazione + ").";
			
	if(oraTerminePresentazioneOfferta!=null && oraTerminePresentazioneOfferta!=""){
		if(oraPresentazioneOfferte==null || oraPresentazioneOfferte==""){
			oraPresentazioneOfferte="00:00";
			
		}
		oraPresentazioneOfferte = oraPresentazioneOfferte.split(":");
		data.setHours(oraPresentazioneOfferte[0]*1);
		data.setMinutes(oraPresentazioneOfferte[1]*1)
		
		oraTerminePresentazioneOfferta = oraTerminePresentazioneOfferta.split(":");
		dataDiRiferimento.setHours(oraTerminePresentazioneOfferta[0]*1);
		dataDiRiferimento.setMinutes(oraTerminePresentazioneOfferta[1]*1);
		
		msg = messaggio + " ("+ dataTermineRichiestaPartecipazione + " " + oraTerminePresentazioneOfferta[0] + ":" + oraTerminePresentazioneOfferta[1] + ").";			  											  					
	}
				
	if(data.getTime() > dataDiRiferimento.getTime()){
		alert(msg);
	}
	
}

function dettaglioValutazioneTecEco(chiave,tipo,esistonoDitteConPunteggio,fasgar,autorizzatoModifiche){
	var sez = "";
	if (tipo == "1-sez") {
		tipo = "1";
		sez = "1";
	}
	var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/goev/goev-dettaglioValutazioneTecEco.jsp";
	href += "&chiave=" + chiave+ "&tipo=" + tipo + "&esistonoDitteConPunteggio=" + esistonoDitteConPunteggio + "&faseGara=" + fasgar + "&autorizzatoModifiche=" + autorizzatoModifiche;
	href += "&sezTec=" + sez;
	document.location.href = href;
}

function apriPopupCalcoloPunteggi(chiave,tipoPunteggio,lottoPlicoUnico,paginaAttivaWizard){
	var href = "href=gare/commons/popupCalcoloPunteggi.jsp?chiave=" + chiave+ "&tipo=" + tipoPunteggio + "&isProceduraTelematica=" + isProceduraTelematica;
	href +="&AttivaValutazioneTec=" + attivaValutazioneTec + "&bustalotti=" + bustalotti + "&lottoPlicoUnico=" + lottoPlicoUnico + "&paginaAttivaWizard="+paginaAttivaWizard;
	openPopUpCustom(href, "calcoloPunteggi", 700, 400, "no", "no");
}

function apriPopupAnnullaCalcoloPunteggi(chiave,tipoPunteggio){
	var href = "href=gare/commons/popupAnnullaCalcoloPunteggi.jsp?chiave=" + chiave+ "&tipo=" + tipoPunteggio;
	openPopUpCustom(href, "annullaCalcoloPunteggi", 700, 300, "no", "no");
	
}

function apriPopupAnnullaRiservatezza(chiave, idconfi){
	var href = "href=gare/commons/popupAnnullaRiservatezza.jsp?chiave=" + chiave;
	if(idconfi){href+="&idconfi=" + idconfi;}
	openPopUpCustom(href, "annullaRiservatezza", 700, 300, "no", "no");
	
}

function apriPopupEsclusioneSogliaMinima(chiave,tipoPunteggio,tipoTitolo,tipoRiparam){
	var href = "href=gare/commons/popupEsclusioneSogliaMinima.jsp?chiave=" + chiave+ "&tipo=" + tipoPunteggio + "&tipoTitolo="+tipoTitolo;
	href +="&tipoRiparam=" + tipoRiparam;
	openPopUpCustom(href, "esclusioneSogliaMinima", 700, 300, "no", "no");
	
}

// Funzione per verificare che una stringa rappresenti un numero
function is_numeric(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

// Funzione per la validazione del codice CIG
function controllaCIG(nomeCampo) {
	var cig = getValue(nomeCampo);
	
	if (cig == '') {
		return true;
	}
	var strC1; // primo carattere
	var strC1_7; // primi 7 caratteri
	var strC4_10; // dal 4 al 10 carattere
	
	var strK; // Firma
	var strK_chk; // variabile d'appoggio per il controllo della firma
	if (cig != '' && (cig.length != 10 || cig == "0000000000")) {
		//Errori di struttura
		//alert ("Codice CIG non valido");
		return false;
	}
	// Verifico se si tratta di cig o smart cig
	strC1 = cig.charAt(0); //Estraggo il primo carattere
	//alert ("'" + strC1 + "', '" + strC1_7 + "', '" + strK_chk);
	if (is_numeric(strC1)) {
	 	strC1_7 = cig.substring(0,7); // Estraggo la parte significativa
	 	strK = cig.substring(7,10);   // Estraggo la firma
	 	strK = parseInt(strK,16);     // Trasformo in decimale
	 	try {
	 		//Calcola Firma
			strK_chk = ((strC1_7 * 1 / 1) * 211 % 4091).toString(16);
			strK_chk = parseInt(strK_chk,16); // Trasformo in decimale
	 	} catch (err) {
			//Impossibile calcolare la firma
			//alert ("Codice CIG non valido");
			return false;
	 	}
	 	if (strK_chk != strK) {
			//La firma non coincide
			//alert ("Codice CIG non valido");
			return false;
	 	}
	} else if (strC1.toUpperCase() == 'X' || strC1.toUpperCase() == 'Z' || strC1.toUpperCase() == 'Y') {
		// SmartCIG
		strK = cig.substring(1,3);//Estraggo la firma
		strK = parseInt(strK,16);//trasformo in decimale
		strC4_10 = cig.substring(3,10);
		strC4_10 = parseInt(strC4_10,16);//trasformo in decimale
		
		try {
			//Calcola Firma
			strK_chk = ((strC4_10 * 1 / 1) * 211 % 251).toString(16) ;
			strK_chk = parseInt(strK_chk,16);
		} catch (err) {
			//Impossibile calcolare la firma
			//alert ("Codice CIG non valido");
			return false;
		}
		if (strK_chk != strK) {
			//La firma non coincide
			//alert ("Codice CIG non valido");
			return false;
		}
	} else if (strC1.toUpperCase() != '#' && strC1.toUpperCase() != '$' && cig.toUpperCase().indexOf('NOCIG') != 0) {
		//alert ("Codice CIG non valido");
		return false;
	}
	return true;
}

function inizializzaNumeri(numero, tipo){
	if(numero==null || numero=="")
		numero=0;
	if(tipo=="F")
		numero=parseFloat(numero);
	else
		numero=parseInt(numero);
	return numero;
}

function setVisibilitaDaAmmrin(valore,valmax) {
	var visibile = false;
	if (valore == 1) {
		visibile = true;
	}else{
		var imprin=getValue("GARE1_IMPRIN");
		imprin=inizializzaNumeri(imprin,"F");
		
		setValue("GARE1_DESRIN","");
		setValue("GARE1_IMPRIN","");
		valmax = valmax - imprin;
		if(valmax!=0)
			setValue("VALMAX_FIT",round(valmax,2));
		else
			setValue("VALMAX_FIT","");
	}
	showObj("rowGARE1_DESRIN", visibile);
	showObj("rowGARE1_IMPRIN", visibile);
}

function setVisibilitaDaAmmpoz(valore,valmax) {
	var visibile = false;
	if (valore == 1) {
		visibile = true;
	}else{
		var impserv=getValue("GARE1_IMPSERV");
		impserv=inizializzaNumeri(impserv,"F");
		
		var imprror=getValue("GARE1_IMPPROR");
		imprror=inizializzaNumeri(imprror,"F");
		
		var impaltro=getValue("GARE1_IMPALTRO");
		impaltro=inizializzaNumeri(impaltro,"F");
		
		setValue("GARE1_DESOPZ","");
		setValue("GARE1_IMPSERV","");
		setValue("GARE1_IMPPROR","");
		setValue("GARE1_IMPALTRO","");
		var valmax = valmax - impserv - imprror - impaltro;
		if(valmax!=0)
			setValue("VALMAX_FIT",round(valmax,2));
		else
			setValue("VALMAX_FIT","");
	}
	showObj("rowGARE1_DESOPZ", visibile);
	showObj("rowGARE1_IMPSERV", visibile);
	showObj("rowGARE1_IMPPROR", visibile);
	showObj("rowGARE1_IMPALTRO", visibile);
}

function calcoloValmax(imprin,impserv,imppror,impaltro,impapp){
	impapp=inizializzaNumeri(impapp,"F");
	var tot=impapp;
	
	imprin=inizializzaNumeri(imprin,"F");
	tot+=imprin;
	
	impserv=inizializzaNumeri(impserv,"F");
	tot+=impserv;
	
	imppror=inizializzaNumeri(imppror,"F");
	tot+=imppror;
	
	impaltro=inizializzaNumeri(impaltro,"F");
	tot+=impaltro;	
	
	if(tot==0)
		setValue("VALMAX_FIT","");
	else
		setValue("VALMAX_FIT",round(tot,2));
}

function gestioneTipneg_Alteng(iterga,entita) {
	//Se iterga != 1,2 si visualizzano alteng e tipneg, altrimenti si nascondono
	var vis = true;
	var campoTipneg=entita+"_TIPNEG";
	if(iterga=="1" || iterga =="2" || iterga=="" || iterga==null)
		vis=false;
	showObj("row" + campoTipneg,vis);
	showObj("rowTORN_ALTNEG",vis);
	if (!vis) {
		setValue(campoTipneg,"");
		setValue("TORN_ALTNEG","");
	}
}

//Funzione per aggiornare il campo IDIAUT
function settaContributo(importoAppalto,arrayScaglioni,campo){
	var importoGara;
	if (importoAppalto == null || importoAppalto =="") 
		importoGara=0;
	else 
		importoGara = importoAppalto;
	importoGara = parseFloat(importoGara);
	for(var i=0; i < arrayScaglioni.length; i++){
		var scaglione = parseFloat(arrayScaglioni[i][1]);
		if (importoGara < scaglione) {
			if (arrayScaglioni[i][0] == null || arrayScaglioni[i][0] == "") setValue(campo,  "");
			else setValue(campo,  eval(parseFloat(arrayScaglioni[i][0])));
			break;
		}
	}
}


function controlloCampoAqnumope(aqnumope) {
	if (aqnumope!=null && aqnumope!="") {
		if (toVal(aqnumope)>1)
			return true;
		else
			return false;
	} else {
		return true;
	}
}

function gestioneAQNUMOPE(aqoper) {
	if(aqoper == '2') {
		showObj("rowGARE1_AQNUMOPE",true);
	} else {
		showObj("rowGARE1_AQNUMOPE",false);
		setValue("GARE1_AQNUMOPE","");
	}
}

function gestioneAccordoQuadroLotti(modo,initACCQUATorn,initAQOPER) {
	var accqua;
	
	if(modo == "NUOVO")
		accqua=initACCQUATorn;
	else
		accqua=getValue("TORN_ACCQUA");
	
	if (accqua == '1') {
		showObj("rowTMP",false);
		showObj("rowGARE_DINLAVG",false);
		showObj("rowGARE_TEUTIL",false);
		showObj("rowGARE_TEMESI",false);
		if(initAQOPER!="2")
			showObj("rowGARE1_AQNUMOPE",false);
	}else{
		showObj("rowGARE1_AQOPER",false);
		showObj("rowGARE1_AQNUMOPE",false);
	}
}

function importaExcelVarPrezzi(ngara,codiceGara,tipologiaGara,codiceDitta){
	var act = contextPath + "/pg/InitImportVariazionePrezziGara.do";
	if(codiceDitta!=null && codiceDitta!='')
		act = contextPath + "/pg/InitVariazionePrezziDitta.do";
	
	var par = "ngara=" + ngara;
	par += "&codiceGara=" + codiceGara;
	if(codiceDitta!=null && codiceDitta!='')
		par += "&codiceDitta=" + codiceDitta;
	
	if(tipologiaGara == "3")
		par += "&garaLottiConOffertaUnica=1";
	else
		par += "&garaLottiConOffertaUnica=2";
	
	openPopUpActionCustom(act, par, 'importVariazionePrezzi', 700, 500, "yes", "yes");
}

function dettaglioMetodoCalcoloAnomalia(ngara, isGaraLottiConOffertaUnica, gestioneSogliaAutomatica,isGaraDLGS2017,risultatiPerPagina,blocco){
	var pagina="gare-popup-metodoCalcoloAnomalia.jsp";
	if(isGaraLottiConOffertaUnica=="true")
		pagina="gare-popup-metodoCalcoloAnomalia-OffUnica.jsp";
	var href = "href=gare/gare/" + pagina;
	href += "?ngara=" + ngara;
	href += "&isGaraDLGS2017=" + isGaraDLGS2017;
	href += "&paginazione=" + risultatiPerPagina; 
	href += "&blocco=" + blocco;
	var altezza = 450;
	if(isGaraLottiConOffertaUnica=="true")
		altezza = 650;
	openPopUpCustom(href, "dettaglioMetodoCalcoloSoglia", 750, altezza, "no", "yes");

}

function AnnullaAperturaOfferte(chiave,bustalotti){
	chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
	var href = "href=gare/gare/gare-popup-annullaAperturaOfferte.jsp";
	href += "&ngara=" + chiave;
	href += "&bustalotti=" + bustalotti;
	openPopUpCustom(href, "annullaAperturaOfferte", 700, 300, "yes", "yes");
} 

function aggiornaCompreq(compreq){
	if(compreq == "1")
	 setValue("TORN_COMPREQ","1");
}

function apriDettaglioSorteggioDitteVerificaRequisiti(ngara,lotto){
	var pagina="gare-popup-sorteggioDitteVerificaRequisiti.jsp";
	var href = "href=gare/gare/" + pagina;
	href += "&ngara=" + ngara;
	href += "&lotto=" + lotto;
	openPopUpCustom(href, "sorteggioDitteVerificaRequisiti", 700, 300, "no", "no");
}

function inizializzazioniDaRibcal(ribcal,critlic){
	if(critlic != 3){
		if(ribcal==3){
			setValue("TORN_OFFAUM","2");
			$('#TORN_OFFAUM').attr('disabled', 'disabled');	
		}else{
			$('#TORN_OFFAUM').removeAttr('disabled');
		}
	}
}

function apriPopupInsertGruppo15(codiceGara,tipologia,ngara){
	var pagina="gare/documgara/conferma-ins-doc-gruppo15.jsp";
	var href = "href=" + pagina;
	href += "?codgar=" + codiceGara + "&tipologia=" + tipologia + "&ngara=" + ngara;
	openPopUpCustom(href, "inserimentoDocumentiDelibera", 700, 300, "no", "no");
}

function tracciamentoDownloadDocimpresa(idprg, iddocdg,ngara,dittao,doctel){
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/TracciamentoDownloadDocimpresa.do",
		data: "idprg=" + idprg + "&iddocdg=" + iddocdg + "&ngara=" + ngara + "&dittao=" + dittao + "&doctel=" + doctel,
		error: function(e){
			alert("Errore durante la tracciatura dell'operazione di download del file");
		}
	});
	
}

function tracciamentoDownloadFS12(idprg, iddocdg,gara,comkey1){
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/TracciamentoDownloadFS12.do",
		data: "idprg=" + idprg + "&iddocdg=" + iddocdg + "&gara=" + gara + "&comkey1=" + comkey1,
		error: function(e){
			alert("Errore durante la tracciatura dell'operazione di download del file");
		}
	});
	
}

function aggiornamentoPresaVisioneDocDitta(codgar,ngara,codimp, norddoci, proveni,syscon){
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/AggiornamentoPresaVisioneDocDitta.do",
		data: "codgar=" + codgar + "&ngara=" + ngara + "&codimp=" + codimp + "&norddoci=" + norddoci + "&proveni=" + proveni + "&syscon="+syscon,
		error: function(e){
			alert("Errore durante l'aggiornamento dei campi di presa visione del documento");
		}
	});
	
}

function annullaRicezionePlichiDomande(tipo,ngara,iterga,codgar,genere){
	var pagina="gare/commons/popupAnnullaRicezionePlichiDomande.jsp";
	var href = "href=" + pagina;
	href += "?tipo=" + tipo + "&ngara=" + ngara + "&iterga=" + iterga + "&codgar=" + codgar +  "&genere=" + genere;
	openPopUpCustom(href, "annullaRicezionePlichiDomand", 500, 300, "no", "no");
}

function apriCreaFascicolo(ngara,codiceGara,idconfi,genere){
	var href = "href=gare/commons/popupCreaFascicolo.jsp";
	href += "?ngara=" + ngara + "&codiceGara=" + codiceGara + "&idconfi="+ idconfi + "&genere=" + genere;
	openPopUpCustom(href, "creaFascicolo", 800, 550, "no", "no");
}

function bloccaDopoPubblicazione(bloccoModificatiDati) {
	if (bloccoModificatiDati=='true') {
		var notInput="";
		var notJsPopUp="";
		var arrayInput = 
		[
			{"campo" : "TORN_CODRUP"},
			{"campo" : "NOMTEC"},
			{"campo" : "TECNI_NOMTEC"},
			{"campo" : "GARE_DINLAVG"},
			{"campo" : "GARE_TEUTIL"},
			{"campo" : "TECNI_NOMTEC"},
			{"campo" : "GARE_CUPPRG"},
			{"campo" : "GARE_CUPMST"},
			{"campo" : "GARE_SUBGAR"},
			{"campo" : "GARE1_CODCUI"},
			{"campo" : "GARE1_ANNINT"},
			{"campo" : "GARE1_DTERMCON"},
			{"campo" : "GARE1_NGIOCON"},
			{"campo" : "TORN_NORMA"},
			{"campo" : "TORN_AQDURATA"},
			{"campo" : "TORN_AQTEMPO"},
			{"campo" : "TORN_SOMMAUR"},
			{"campo" : "TORN_CODNUTS"},
			{"campo" : "TORN_SELPAR"},
			{"campo" : "TORN_MINOPE"},
			{"campo" : "TORN_MAXOPE"},
			{"campo" : "TORN_DATDOC"},
			{"campo" : "TORN_ORADOC"},
			{"campo" : "TORN_IMPDOC"},
			{"campo" : "TORN_MOTACC"},
			{"campo" : "TORN_BANWEB"},
			{"campo" : "TORN_NOFDIT"},
			{"campo" : "TORN_NGADIT"},
			{"campo" : "TORN_CONTOECO"}
		];
		 $.each(arrayInput,function( index, value ) {
			notInput =   notInput+":not('#"+value.campo+"')";
			notJsPopUp = notJsPopUp+":not('#jsPopUp"+value.campo+"')";
		 });	
		$("input:not([type=button])" + notInput + ":not([id^='GARTECNI_']):not([id^='GARCPV_']):not([id^='XTORN_']):not([id^='XGARE_'])").each(function(i) {
				$(this).attr('readonly','readonly');
				$(this).attr('tabindex','-1');
				$(this).css('border-color','#A3A6FF');
				$(this).css('border-width','1px');
				$(this).css('background-color','#E0E0E0');
		});
		var arrayText = 
		[
			{"campo" : "GARE_NOT_GAR"},
			{"campo" : "GARE_NOTEGA"},
			{"campo" : "TORN_DESTOR"},
			{"campo" : "TORN_PAGDOC"},
			{"campo" : "TORN_MODFIN"},
			{"campo" : "TORN_PROGEU"},
			{"campo" : "TORN_SELOPE"}
		];
		var notText="";
		 $.each(arrayText,function( index, value ) {
			notText =   notText+":not('#"+value.campo+"')";
			notJsPopUp = notJsPopUp+":not('#jsPopUp"+value.campo+"')";
		 });	
		$("textarea" + notText + ":not([id^='XTORN_']):not([id^='XGARE_'])").each(function(i) {
			$(this).attr('readonly','readonly');
			$(this).attr('tabindex','-1');
			$(this).css('border-color','#A3A6FF');
			$(this).css('border-width','1px');
			$(this).css('background-color','#E0E0E0');
		});
		var arraySelect = 
		[
			{"campo" : "GARE_TEMESI"},
			{"campo" : "GARE_SEGRETA"},
			{"campo" : "GARE_TIPLAV"},
			{"campo" : "TORN_AQTEMPO"},
			{"campo" : "TORN_SOMMAUR"},
			{"campo" : "TORN_CODNUTS"},
			{"campo" : "TORN_SELPAR"},
			{"campo" : "TORN_MINOPE"},
			{"campo" : "TORN_MAXOPE"},
			{"campo" : "TORN_AMMVAR"},
			{"campo" : "TORN_PROFAS"},
			{"campo" : "TORN_MODREA"},
			{"campo" : "TORN_ELEPAR"},
			{"campo" : "TORN_APFINFC"},
			{"campo" : "TORN_PROGEU"},
			{"campo" : "TORN_ACCAPPUB"},
			{"campo" : "TORN_OFFLOT"},
			{"campo" : "TORN_OGGCONT"},
			{"campo" : "V_GARE_TORN_OGGCONT_LAVORI"},
			{"campo" : "V_GARE_TORN_OGGCONT_FORNITURE"},
			{"campo" : "TORN_TIPLAV"},
			{"campo" : "TORN_PROURG"},
			{"campo" : "TORN_PREINF"},
			{"campo" : "TORN_DOCWEB"},
			{"campo" : "TORN_TERRID"},
			{"campo" : "TORN_NORMA"},
			{"campo" : "TORN_NORMA1"},
			{"campo" : "TORN_TUS"},
			{"campo" : "TORN_URBASCO"}
		];
		var notSelect="";
		 $.each(arraySelect,function( index, value ) {
			notSelect =   notSelect+":not('#"+value.campo+"')";
			notJsPopUp = notJsPopUp+":not('#jsPopUp"+value.campo+"')";
		 });	
		$("select:not([id^='GARTECNI_'])" + notSelect + ":not([id^='XGARE_']):not([id^='XTORN_'])").each(function(i) {
			$(this).attr('readonly','readonly');
			$(this).attr('tabindex','-1');
			$(this).css('border-colojsPopUpr','#A3A6FF');
			$(this).css('border-width','1px');
			$(this).css('background-color','#E0E0E0');
			var cloneid = $(this).attr('id') + "_clone";
			if ($("#" + cloneid).size() == 0) {
				var columnclone = $(this).clone();
				columnclone.attr('id',cloneid);
				columnclone.prop("disabled", true);
				columnclone.css('color','#000000');
				$(this).after(columnclone);
				$(this).hide();
				columnclone.val($(this).val());
			}
		});
		$("[id^='rowLink']:not('#rowLinkAddULTREFINC'):not('#rowLinkAddCPVCOMP')").hide();
		
		$.each($("[id^='rowtitolo']:not([id^='rowtitoloULTREFINC']):not([id^='rowtitoloCPVCOMP'])"),function( index, value ) {
			$(this).find("td[id^='tdTitoloDestra']").hide();
		});	
		
		jQuery("input" + notInput + ":not([id^='GARTECNI_'])" + ":not([id^='XTORN_']):not([id^='XGARE_'])").bind('mousedown', function(e) {
			e.preventDefault();
		});
		$("[id^='jsPopUp']"+notJsPopUp+":not([id^='jsPopUpGARTECNI_']):not([id^='jsPopUpGARCPV_']):not([id^='jsPopUpXTORN_']):not([id^='jsPopUpXGARE_'])").hide();
	}
}

function richiestaSoccorsoIstruttorio(chiaveRiga,modelloSoccoroIstruttorioConfigurato,idconfi,numeroModello,stepWizard){
	if(modelloSoccoroIstruttorioConfigurato != "true"){
		alert("Non e' configurato il modello per la richiesta di soccorso istruttorio");
		return;
	}else{
		var chiaveVet= chiaveRiga.split(";");
		var codiceDitta=chiaveVet[1].substring(chiaveVet[1].indexOf(":")+1);
		var numeroGara = chiaveVet[2].substring(chiaveVet[1].indexOf(":")+1);
		var codiceGara = chiaveVet[0].substring(chiaveVet[1].indexOf(":")+2);
		if((bustalotti == 1 && paginaFasiGara == "aperturaOffAggProvLottoOffUnica" ) || (bustalotti == 2 && paginaFasiGara == "aggiudProvDefOffertaUnica"))
			numeroGara = codiceGara;
				
		var href = contextPath + "/Lista.do?"+csrfToken+"&numModello=" + numeroModello + "&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara ;
		if(isGaraLottiConOffertaUnica != "true" && !(bustalotti == 1 && paginaFasiGara == "aperturaOffAggProvLottoOffUnica")){
			href += "&keyParent=GARE.NGARA=T:" + numeroGara;
		}else{
			href += "&keyParent=TORN.CODGAR=T:" + numeroGara;
		}
		href += "&ditta=" + codiceDitta+ "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp";
		
		if(lottoDiGara == "true" && garaLottoUnico!= "true" && garaLottoUnico!= true ){
			var entitaWSDM="TORN";
			var chiaveWSDM=codiceGara;
		}else{
			var entitaWSDM="GARE";
			var chiaveWSDM=numeroGara;
		}
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM+"&"+csrfToken;
		if(idconfi){href+="&idconfi=" + idconfi;}
		href+="&stepWizard=" + stepWizard;
		document.location.href = href;
	}
	
	
}	