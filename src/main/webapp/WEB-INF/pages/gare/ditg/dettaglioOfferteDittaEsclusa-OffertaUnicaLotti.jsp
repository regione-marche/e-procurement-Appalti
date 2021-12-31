<%
/*
 * Created on: 05-05-11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* Pagina per visualizzazione/inserimento dell'offerta tecnica/economica di
  * una ditta esclusa per i diversi lotti per le gare a lotti con offerta unica, 
  *
  * Questa pagina prende spunto dalla pagina dettaglioOfferteDitta-OffertaUnicaLotti.jsp
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:set var="temp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneOfferteDittaEsclusaFunction", pageContext,key)}' />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="fale">
	

<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"OFFERTE_DITTA_ESCLUSA")}' />


	<gene:redefineInsert name="corpo">

<table class="lista">

<c:set var="whereDITG" value=" DITG.CODGAR5 = #DITG.CODGAR5# and DITG.NGARA5 <> #DITG.NGARA5# and DITG.DITTAO = #DITG.DITTAO# AND DITG.AMMGAR='2' AND (DITG.PARTGAR = '1' OR DITG.PARTGAR IS NULL)" />

	<!-- inizia pagine a lista -->

		<tr>
			<td >
				<gene:formLista entita="DITG" where='${whereDITG}' tableclass="datilista" sortColumn="3" pagesize="20" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInserisciOfferteDitteEscluse" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1 }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaModifica")}
										</a>
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</gene:redefineInsert>

					<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="NGARA5" headerClass="sortable" visibile="true" width="200" title="Codice lotto"/>
					<gene:campoLista campo="NGARA5fit" entita="DITG" campoFittizio="true" definizione="T10" visibile="false" value="${datiRiga.DITG_NGARA5}" edit="${updateLista eq 1}" />
					
					<gene:campoLista campo="CODIGA" title="Lotto" entita="GARE" where="GARE.NGARA = DITG.NGARA5" headerClass="sortable" width="100" />
									
					<gene:campoLista campo="MODLICG" visibile="false" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" />
					<gene:campoLista visibile="false" campoFittizio="true" campo="MODLICG" edit="true"  definizione="T10"  value="${datiRiga.GARE_MODLICG}"/>
										
			
			<c:if test='${(numeroLotti - numeroLottiOEPV) > 0 or datiRiga.rowCount == 0}'>
				<gene:campoLista campo="RIBAUO" title="Ribasso offerto" width="150" headerClass="sortable" visibile="true" edit="${updateLista eq 1}" definizione="F13.9;0;;PRC;RIBAUO" />
			</c:if>

			<c:if test='${(numeroLottiMigliorOffertaPrezzi + numeroLottiOEPV) > 0}'>
				<gene:campoLista campo="IMPOFF" title="Importo" width="${gene:if(updateLista eq 1, '150', '')}" headerClass="sortable" visibile="true" edit="${updateLista eq 1}" />
			</c:if>
			
			<c:if test="${modalitaAggiudicazioneGara eq 6}">
				<gene:campoLista campo="PUNTEC" edit="${updateLista eq 1}" width="130"/>
			</c:if>
						
			<c:if test='${numeroLottiOEPV > 0}'>
				<gene:campoLista campo="PUNECO" visibile="true" width="130" edit="${updateLista eq 1}" definizione="F13.9;0;;;PUNECO" />
			</c:if>
			
			<%//Campi di gare per il calcolo del ribasso %>
			<gene:campoLista campo="RIBCAL" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="ONPRGE" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="IMPAPP" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="IMPSIC" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="IMPNRL" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="SICINC" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>			
		
			<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
			<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
			<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
			
			</gene:formLista>
			</td>
		</tr>
	
<!-- fine pagine a lista -->

	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1 }'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
				</c:when>
				<c:otherwise>
					<INPUT type="button" class="bottone-azione" value="Torna a elenco concorrenti esclusi" title="Torna a elenco concorrenti esclusi" onclick="javascript:historyVaiIndietroDi(1);"/>&nbsp;
					<c:if test='${datiRiga.rowCount > 0 }'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">
					</c:if>
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
</table>

<gene:javaScript>

<c:if test='${updateLista eq 1}'>
			
	
		// Funzione per associare le funzioni JS da eseguire al momento di modifica
		// di un campo. Questo e' necessario perchè la lista e' stata progetta per
		// non essere mai modificabile e quindi il tag gene:campoLista non associa
		// mai una funzione JS all'evento onchange.
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
				if (document.getElementById("DITG_IMPOFF_" + i) != null)
					document.getElementById("DITG_IMPOFF_" + i).onchange = aggiornaPerCambioImportoOfferto;
				if(getValue("MODLICG_" + i) == "6"){
					if (document.getElementById("DITG_PUNTEC_" + i) != null)
						document.getElementById("DITG_PUNTEC_" + i).onchange = aggiornaPerCambioPunteggioTecnico;
					if (document.getElementById("DITG_PUNECO_" + i) != null)
						document.getElementById("DITG_PUNECO_" + i).onchange = aggiornaPerCambioPunteggioEconomico;
					activeForm.setDominio("DITG_RIBAUO_" + i,"");
					
				} else {
					document.getElementById("DITG_RIBAUO_" + i).onchange = aggiornaPerCambioRibassoAumento;
					
				}
			
			}
		}
		
		// Funzioni JS richiamate subito dopo la creazione della pagina per l'
		associaFunzioniEventoOnchange();
		document.getElementById("numeroDitte").value = ${currentRow}+1;
			
		function aggiornaPerCambioPunteggioTecnico(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			// All'oggetto JS CampoObj del campo DITG_PUNTEC_<i> si cambia la funzione
			// di validazione, per poter effettuare in un'unica funzione i controlli
			// necessari al ribasso offerto o al punteggio all'onchange del campo
			activeForm.getCampo("DITG_PUNTEC_" + numeroRiga).setFnValidazione(validazionePUNTEC);
			callObjFn("local" + activeForm.getFormName(), 'onChange', this);
		}			
		
		function validazionePUNTEC(refVal, msg, obj){
			var result = false;
			
			if(checkFloat(refVal, msg, obj)){
			<c:choose>
				<c:when test='${not empty numeroCifreDecimaliPunteggioTecnico}'>
					var tmp = null;
					if(this.getValue() != null && this.getValue() != ""){
						// Controllo del numero di cifre decimali rispetto
						// (il campo PUNTEC e' definito in C0CAMPI come F13.9)
						var numeroCifreDecimaliPunteggioTecnico = ${numeroCifreDecimaliPunteggioTecnico};
						tmp = this.getValue();
						if(this.getValue().indexOf(".") >= 0){
							if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliPunteggioTecnico){
								result = true;
							} else {
								msg.setValue("Per il punteggio tecnico è possibile indicare al piu' ${numeroCifreDecimaliPunteggioTecnico} cifre decimali");
								sbiancaCampi = false;
							}
						} else {
							result = true;
						}
					}
				</c:when>
				<c:otherwise>
					result = true;
				</c:otherwise>
			</c:choose>
				var objId = this.obj.id;
				var numeroRiga = (objId.substr(objId.lastIndexOf("_")+1)-1);
				var valore = toVal(this.getValue());
				if(arrayPunteggiTecnici[numeroRiga ]>=0 && result==true){
					if(valore > arrayPunteggiTecnici[numeroRiga ]){
						alert("Il punteggio tecnico specificato è maggiore del punteggio tecnico massimo del lotto (" +arrayPunteggiTecnici[numeroRiga] + ")");
						
					}
					
					if(arraySoglieTecniche[numeroRiga]!="" && arraySoglieTecniche[numeroRiga]>=0){
						var sogliaTecnicaMinima = parseFloat(arraySoglieTecniche[numeroRiga]);
						if(sogliaTecnicaMinima > valore){
							var msg = "Il punteggio tecnico specificato è inferiore alla soglia minima stabilita per il lotto (" + sogliaTecnicaMinima + ").";
							alert(msg);
								
						}
					}
					
					
				}
				
				
			
			
			}
			return result;
		}
	

	

		function aggiornaPerCambioRibassoAumento(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var modlicg = getValue("MODLICG_" + numeroRiga);
			var ribcal= toVal(getValue("GARE_RIBCAL_" + numeroRiga));
			
			if(modlicg != "6"){
				// All'oggetto JS CampoObj del campo DITG_RIBAUO_<i> si cambia la funzione
				// di validazione, per poter effettuare in un'unica funzione i controlli
				// necessari al ribasso offerto o al punteggio all'onchange del campo
				activeForm.getCampo("DITG_RIBAUO_" + numeroRiga).setFnValidazione(validazioneRIBAUO);
				callObjFn("local" + activeForm.getFormName(), 'onChange', this);
				
				//Aggiornamento impoff
				var ribauo = this.value;
				if((modlicg == "5" || modlicg == "14" || modlicg == "16") && ribcal == 1 ) {
					if(ribauo != null && ribauo!=""){
						ribauo = toVal(ribauo);
						var impsic = toVal(getValue("GARE_IMPSIC_" + numeroRiga)); 
						var impnrl = toVal(getValue("GARE_IMPNRL_" + numeroRiga));
						var onprge = toVal(getValue("GARE_ONPRGE_" + numeroRiga));
						var sicinc = toVal(getValue("GARE_SICINC_" + numeroRiga));
						var impapp = toVal(getValue("GARE_IMPAPP_" + numeroRiga));
						
						if (impapp == null || impapp=="") impapp=0;
						if (impsic == null || impsic=="") impsic=0;
						if (impnrl == null || impnrl=="") impnrl=0;
						if (onprge == null || onprge=="") onprge=0;
						
						var importo1 = parseFloat(impapp) - parseFloat(impsic) - parseFloat(impnrl) - parseFloat(onprge);
						var importo2 = parseFloat(impnrl);
											
						if (sicinc=='1')
							importo2 += parseFloat(impsic);	
						
						importo1 = round(importo1,5);
						importo2 = round(importo2,5);
						
						<c:if test='${not empty numeroCifreDecimaliRibasso}'>
							cifreDecimali =${numeroCifreDecimaliRibasso};
						</c:if>
	                       ribauo = round(ribauo,cifreDecimali);
							
						var impoff = (importo1 * (1 + ribauo/100)) + importo2;
						setValue("DITG_IMPOFF_" + numeroRiga, round(impoff,5));
					}else{
						setValue("DITG_IMPOFF_" + numeroRiga, "");
					}
					
					
				}
			}
		}

		function validazioneRIBAUO(refVal, msg, obj){
			var result = false;
			var objId = this.obj.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);

			if(getValue("MODLICG_" + numeroRiga) != "6"){
				if(checkFloat(refVal, msg, obj)){
					if(getValue("MODLICG_" + numeroRiga) != "6" && refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})
						refVal.setValue((-1) * refVal.value);
	
			<c:choose>
				<c:when test='${not empty numeroCifreDecimaliRibasso}'>
					var tmp = null;
					if(this.getValue() != null && this.getValue() != ""){
						// Controllo del numero di cifre decimali rispetto
						// (il campo RIBAUO e' definito in C0CAMPI come F13.9)
						var numeroCifreDecimaliRibasso = ${numeroCifreDecimaliRibasso};
						tmp = this.getValue();
						if(this.getValue().indexOf(".") >= 0){
							if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliRibasso){
								result = true;
							} else {
								msg.setValue("Per il ribasso è possibile indicare al piu' ${numeroCifreDecimaliRibasso} cifre decimali");
								sbiancaCampi = false;
							}
						} else {
							result = true;
						}
					}
				</c:when>
				<c:otherwise>
					result = true;
				</c:otherwise>
			</c:choose>
				}
			}
			return result;
		}

		function aggiornaPerCambioPunteggioEconomico(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
		
			if(getValue("MODLICG_" + numeroRiga) == "6"){
				// All'oggetto JS CampoObj del campo DITG_PUNECO_<i> si cambia la funzione
				// di validazione, per poter effettuare in un'unica funzione i controlli
				// necessari al punteggio economico all'onchange del campo
				activeForm.getCampo("DITG_PUNECO_" + numeroRiga).setFnValidazione(validazionePUNECO);
				callObjFn("local" + activeForm.getFormName(), 'onChange', this);
				
			}
		}

		function validazionePUNECO(refVal, msg, obj){
			var objId = this.obj.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var result = false;
			if(getValue("MODLICG_" + numeroRiga) == "6"){
				if(checkFloat(refVal, msg, obj)){
	
			<c:choose>
				<c:when test='${not empty numeroCifreDecimaliPunteggioTecnico}'>
					var tmp = null;
					if(this.getValue() != null && this.getValue() != ""){
						// Controllo del numero di cifre decimali rispetto
						// (il campo PUNECO e' definito in C0CAMPI come F13.9)
						var numeroCifreDecimaliPunteggioTecnico = ${numeroCifreDecimaliPunteggioTecnico};
						tmp = this.getValue();
						if(this.getValue().indexOf(".") >= 0){
							if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliPunteggioTecnico){
								result = true;
							} else {
								msg.setValue("Per il punteggio economico è possibile indicare al piu' ${numeroCifreDecimaliPunteggioTecnico} cifre decimali");
								sbiancaCampi = false;
							}
						} else {
							result = true;
						}
					}
				</c:when>
				<c:otherwise>
					result = true;
				</c:otherwise>
			</c:choose>
				}
				var objId = this.obj.id;
				var numeroRiga = (objId.substr(objId.lastIndexOf("_")+1)-1);
				var valore = toVal(this.getValue());
				if(arrayPunteggiEconomici[numeroRiga ]>=0 && result==true){
					if(valore > arrayPunteggiEconomici[numeroRiga ]){
						alert("Il punteggio economico specificato è maggiore del punteggio economico massimo del lotto (" +arrayPunteggiEconomici[numeroRiga] + ")");
						
					}
					
					if(arraySoglieEconomiche[numeroRiga]!="" && arraySoglieEconomiche[numeroRiga]>=0){
						var sogliaEconomicaMinima = parseFloat(arraySoglieEconomiche[numeroRiga]);
						if(sogliaEconomicaMinima > valore){
							var msg = "Il punteggio economico specificato è inferiore alla soglia minima stabilita per il lotto (" + sogliaEconomicaMinima + ").";
							alert(msg);
								
						}
					}
				}
				
			}
			return result;
		}
		
		function aggiornaPerCambioImportoOfferto(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var impoff = toVal(this.value);
			var cifreDecimali = 9;
			var ribcal= toVal(getValue("GARE_RIBCAL_" + numeroRiga));
			var modlicg = getValue("MODLICG_" + numeroRiga);
			if (ribcal == 2 && modlicg != 6) {
				if (impoff == null || impoff == ""){
						setValue("DITG_RIBAUO_" + numeroRiga, "");
				}else{
					var impapp = toVal(getValue("GARE_IMPAPP_" + numeroRiga)); 
					var impsic = toVal(getValue("GARE_IMPSIC_" + numeroRiga)); 
					var impnrl = toVal(getValue("GARE_IMPNRL_" + numeroRiga));
					var onprge = toVal(getValue("GARE_ONPRGE_" + numeroRiga));
					var sicinc = toVal(getValue("GARE_SICINC_" + numeroRiga));
					
					if (impapp == null || impapp=="") impapp=0;
					if (impsic == null || impsic=="") impsic=0;
					if (impnrl == null || impnrl=="") impnrl=0;
					if (onprge == null || onprge=="") onprge=0;
									
					var numeratore = parseFloat(impoff) + parseFloat(onprge) - parseFloat(impapp);
					
					if (sicinc==null ||sicinc=="" || sicinc != '1')
						numeratore += parseFloat(impsic);
						
					var denominatore = parseFloat(impapp) - parseFloat(impsic) - parseFloat(impnrl) - parseFloat(onprge);
					
					var ribauo; 
					if (denominatore != 0)
						ribauo =  numeratore * 100 / denominatore ;
					else
						ribauo = 0;
					if (ribauo >0 && ${requestScope.offaum != "1"}){
						alert('Non sono ammesse offerte in aumento');
						setValue("DITG_RIBAUO_" + numeroRiga, "");
					}else{
						<c:if test='${not empty numeroCifreDecimaliRibasso}'>
							cifreDecimali =${numeroCifreDecimaliRibasso};
						</c:if>
						
						setValue("DITG_RIBAUO_" + numeroRiga, round(ribauo,cifreDecimali));
						
						//Si richama l'evento onchange del campo RIBAUO
						document.getElementById("DITG_RIBAUO_" + numeroRiga).onchange();
					}		
				} 
			}
		}

	


</c:if>

<% // All'apertura si nascondono i campi che non si vuole siano visibili/modificabili dall'utente %>

	
		<c:if test='${(numeroLottiOEPV + numeroLottiMigliorOffertaPrezzi) > 0 and numeroLottiMigliorOffertaPrezzi < numeroLotti}'>
			var tmpModlicg = null;
			for(var t=1; t <= ${datiRiga.rowCount}; t++){
				tmpModlicg = getValue("MODLICG_"+t);
				if(tmpModlicg != "6")
					showObj("colDITG_PUNECO_" + t, false);
				if(tmpModlicg == "1" || tmpModlicg == "13" || tmpModlicg == "15")
					showObj("colDITG_IMPOFF_" + t, false);
			}
		</c:if>
		<c:if test='${numeroLottiOEPV > 0}'>
			var tmpModlicg = null;
			for(var t=1; t <= ${datiRiga.rowCount}; t++){
				tmpModlicg = getValue("MODLICG_"+t);
				if(tmpModlicg == "6")
					showObj("colDITG_RIBAUO_" + t, false);
			}
			
			
		
		</c:if>
	
	
	
		var tmpModlicg = null;
		for(var t=1; t <= ${datiRiga.rowCount}; t++){
			tmpModlicg = getValue("MODLICG_"+t);
			if(tmpModlicg != "6")
				showObj("colDITG_PUNTEC_" + t, false);
		}
	
		
	arrayPunteggiTecnici= new Array();
    <c:forEach items="${listaPunteggiTecnici}" var="punteggioTecnico" varStatus="indice1" >
    	arrayPunteggiTecnici[${indice1.index}] = "${punteggioTecnico}";
    </c:forEach>
    
    arrayPunteggiEconomici= new Array();
    <c:forEach items="${listaPunteggiEconomici}" var="punteggioEconomico" varStatus="indice2" >
    	arrayPunteggiEconomici[${indice2.index}] = "${punteggioEconomico}";
    </c:forEach>
    
    arraySoglieTecniche= new Array();
    <c:forEach items="${listaSoglieTecniche}" var="sogliaTecnica" varStatus="indice3" >
    	arraySoglieTecniche[${indice3.index}] = "${sogliaTecnica}";
    </c:forEach>
    
    arraySoglieEconomiche= new Array();
    <c:forEach items="${listaSoglieEconomiche}" var="sogliaEconomica" varStatus="indice4" >
    	arraySoglieEconomiche[${indice4.index}] = "${sogliaEconomica}";
    </c:forEach>
    
</gene:javaScript>

</gene:redefineInsert>
</gene:template>