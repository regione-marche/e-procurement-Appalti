
<%
	/*
	 * Created on 04-05-2011
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${!empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value='${param.isGaraLottiConOffertaUnica}' />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value='${isGaraLottiConOffertaUnica}' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.modalitaGara}'>
		<c:set var="modalitaGara" value='${param.modalitaGara}' />
	</c:when>
	<c:otherwise>
		<c:set var="modalitaGara" value='${modalitaGara}' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
		<c:set var="where" value="DITG.CODGAR5 = '${codiceGara}' AND DITG.CODGAR5 = DITG.NGARA5 AND DITG.AMMGAR='2' AND (DITG.PARTGAR = '1' OR DITG.PARTGAR IS NULL) AND (DITG.INVOFF='1' OR DITG.INVOFF IS NULL)" />
	</c:when>
	<c:otherwise>
		<c:set var="where" value="DITG.CODGAR5 = '${codiceGara}' AND DITG.NGARA5 = '${numeroGara }' AND DITG.AMMGAR='2' AND (DITG.PARTGAR = '1' OR DITG.PARTGAR IS NULL) AND (DITG.INVOFF='1' OR DITG.INVOFF IS NULL)" />
	</c:otherwise>
</c:choose>

<c:set var="temp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneInserisciOfferteDitteEscluseFunction", pageContext,key, isGaraLottiConOffertaUnica)}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="false">
	<c:choose>
		<c:when test='${isGaraLottiConOffertaUnica eq true}'>
			<gene:setString name="titoloMaschera" value="Lista concorrenti esclusi per la gara ${numeroGara}" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Inserimento offerte ditte escluse per la gara ${numeroGara}" />
		</c:otherwise>
	</c:choose>
	<gene:setString name="entita" value="DITG" />
	<gene:redefineInsert name="corpo">
			
		<table class="lista">
			
			<c:if test='${modalitaGara eq "6" and isGaraLottiConOffertaUnica ne "true"}'>
				<tr>
					<td >
						<c:choose>
							<c:when test="${punteggioTecnico < 0}">
								<c:set var="msgPunteggioTecnico" value="non definito"/>
							</c:when>
							<c:otherwise>
								<c:set var="msgPunteggioTecnico" value="${punteggioTecnico}"/>
							</c:otherwise>
						</c:choose>
						
						<b>Punteggio tecnico massimo:</b> ${msgPunteggioTecnico} <c:if test="${!empty sogliaTecnicaMinima }"> &nbsp;&nbsp;&nbsp;&nbsp;<b>Soglia minima:</b>&nbsp;${sogliaTecnicaMinima }</c:if>
					</td>
				</tr>
			</c:if>

			<c:if test='${modalitaGara eq "6" and isGaraLottiConOffertaUnica ne "true"}'>
				<tr>
					<td  >
						<c:choose>
							<c:when test="${punteggioEconomico < 0}">
								<c:set var="msgPunteggioEconomico" value="non definito"/>
							</c:when>
							<c:otherwise>
								<c:set var="msgPunteggioEconomico" value="${punteggioEconomico}"/>
							</c:otherwise>
						</c:choose>
						<b>Punteggio economico massimo:</b> ${msgPunteggioEconomico}  <c:if test="${!empty sogliaEconomicaMinima }"> &nbsp;&nbsp;&nbsp;&nbsp;<b>Soglia minima:</b>&nbsp;${sogliaEconomicaMinima }</c:if>
					</td>
				</tr>
			</c:if>
			
			
			
			<tr>
				<td><gene:formLista entita="DITG" pagesize="20" tableclass="datilista" gestisciProtezioni="false" sortColumn="4" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInserisciOfferteDitteEscluse">
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />
					<gene:redefineInsert name="addToAzioni" >
						<c:if test="${ isGaraLottiConOffertaUnica ne true}" >
						<c:choose>
						<c:when test='${updateLista eq 1}'>
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
							<c:if test="${datiRiga.rowCount > 0}">
							<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaModifica")}
										</a>
									</td>
								</tr>
								</c:if>
						</c:otherwise>
						</c:choose>
						</c:if>
					</gene:redefineInsert>
										
					<gene:campoLista campo="CODGAR5" visibile="false"  edit="${updateLista eq 1}"/>
					<gene:campoLista campo="NGARA5"  visibile="false"  edit="${updateLista eq 1}"/>
					<gene:campoLista campo="DITTAO" visibile="false"  edit="${updateLista eq 1}"/>
					<gene:campoLista title="N." campo="NPROGG" headerClass="sortable" />
					<gene:campoLista campo="NOMIMO" headerClass="sortable" />
					<gene:campoLista campo="FASGAR" headerClass="sortable" />
					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
							<gene:campoLista title="&nbsp;" width="20">
								<a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioOffertaDittaPerSingoliLotti();" title="Offerta della ditta esclusa per i singoli lotti" >
									<img width="16" height="16" title="Offerta della ditta esclusa per i singoli lotti" alt="Offerta della ditta esclusa per i singoli lotti" src="${pageContext.request.contextPath}/img/offertaditta.png"/>
								</a>
							</gene:campoLista>
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="RIBAUO" title="Ribasso offerto" headerClass="sortable" edit="${updateLista eq 1}" visibile='${modalitaGara ne "6"}' definizione="F13.9;0;;PRC;RIBAUO"/>
							<gene:campoLista campo="IMPOFF" title="Importo" headerClass="sortable" edit="${updateLista eq 1}" visibile='${modalitaGara eq "6" or modalitaGara eq "5" or modalitaGara eq "14" or modalitaGara eq "16"}'/>
							<gene:campoLista campo="PUNTEC" headerClass="sortable" edit="${updateLista eq 1}" visibile='${modalitaGara eq "6"}' />
							<gene:campoLista campo="PUNECO" headerClass="sortable" edit="${updateLista eq 1}" visibile='${modalitaGara eq "6"}' definizione="F13.9;0;;;PUNECO"/>	
						</c:otherwise>
					</c:choose>
					
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="modalitaGara" id="modalitaGara" value="${modalitaGara}" />
					<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
				</gene:formLista></td>
			</tr>
			<tr>
		
		<td class="comandi-dettaglio" colSpan="2">
			<c:if test="${ isGaraLottiConOffertaUnica ne true}" >
			<c:choose>
				<c:when test='${updateLista eq 1}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
				</c:when>
				<c:otherwise>
					<c:if test="${datiRiga.rowCount > 0}">
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;&nbsp;
					</c:if>
				</c:otherwise>
			</c:choose>
			</c:if>
			&nbsp;
		</td>
		
	</tr>
			
					
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		// Funzione per associare le funzioni JS da eseguire al momento di modifica
		// di un campo. Questo e' necessario perchè la lista e' stata progetta per
		// non essere mai modificabile e quindi il tag gene:campoLista non associa
		// mai una funzione JS all'evento onchange.
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
			<c:choose>
				<c:when test='${modalitaGara eq 6}'>
					activeForm.setDominio("DITG_RIBAUO_" + i,"");
					document.getElementById("DITG_PUNTEC_" + i).onchange = aggiornaPerCambioPunteggioTecnico;
					document.getElementById("DITG_PUNECO_" + i).onchange = aggiornaPerCambioPunteggioEconomico;
				</c:when>
				<c:otherwise>
					if(document.getElementById("DITG_RIBAUO_" + i)!= null)
						document.getElementById("DITG_RIBAUO_" + i).onchange = aggiornaPerCambioRibassoAumento;
				</c:otherwise>
				
			</c:choose>
			<c:if test='${ribcal eq "2"}'>
				document.getElementById("DITG_IMPOFF_" + i).onchange = aggiornaPerCambioImportoOfferto;
			</c:if>
			}
		}
		
		function aggiornaPerCambioRibassoAumento(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
		
			// All'oggetto JS CampoObj del campo DITG_RIBAUO_<i> si cambia la funzione
			// di validazione, per poter effettuare in un'unica funzione i controlli
			// necessari al ribasso offerto o al punteggio all'onchange del campo
			activeForm.getCampo("DITG_RIBAUO_" + numeroRiga).setFnValidazione(validazioneRIBAUO);
			callObjFn("local" + activeForm.getFormName(), 'onChange', this);
			
			<c:if test="${(modalitaGara eq '5' or modalitaGara eq '14' or modalitaGara eq '16') and ribcal eq '1'}" >
					//Aggiornamento di impoff
					var ribauo = this.value;
					if(ribauo != null && ribauo !=""){
						ribauo = toVal(ribauo);
						var importo1 = ${importo1 };
						importo1 = parseFloat(importo1);
						var importo2 = ${importo2 }; 
						importo2 = parseFloat(importo2);
						var impoff = (importo1 * (1 + ribauo/100)) + importo2;
						setValue("DITG_IMPOFF_" + numeroRiga, round(impoff,5));
					}else{
						setValue("DITG_IMPOFF_" + numeroRiga, "");
					}
					
				</c:if>
		}
		
		function validazioneRIBAUO(refVal, msg, obj){
			var result = false;
			if(checkFloat(refVal, msg, obj)){
				if(${modalitaGara ne 6} && refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})  
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
			return result;
		}
		
		<c:if test='${modalitaGara eq 6}'>
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
				<c:when test='${not empty numeroCifreDecimaliPunteggioTecnico and modalitaGara eq 6}'>
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
				var punteggioTecnico = ${punteggioTecnico };
				var valore = toVal(this.getValue());
				if(punteggioTecnico>=0 && result==true){
					if(valore > punteggioTecnico){
						alert("Il punteggio tecnico specificato è maggiore del punteggio tecnico massimo");
						
					}
				}
				var sogliaTecnicaMinima = "${sogliaTecnicaMinima }";
				if(sogliaTecnicaMinima!=""){
					sogliaTecnicaMinima = parseFloat(sogliaTecnicaMinima);
					if(sogliaTecnicaMinima > valore){
						var msg = "Il punteggio tecnico specificato è inferiore alla soglia minima stabilita. ";
						alert(msg);
					}
				}
			
			}
			return result;
		}
		
		function aggiornaPerCambioPunteggioEconomico(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
		
			// All'oggetto JS CampoObj del campo DITG_PUNECO_<i> si cambia la funzione
			// di validazione, per poter effettuare in un'unica funzione i controlli
			// necessari al punteggio economico all'onchange del campo
			activeForm.getCampo("DITG_PUNECO_" + numeroRiga).setFnValidazione(validazionePUNECO);
			callObjFn("local" + activeForm.getFormName(), 'onChange', this);
		}

		function validazionePUNECO(refVal, msg, obj){
			var result = false;
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
				var punteggioEconomico = ${punteggioEconomico };
				var valore = toVal(this.getValue());
				if(punteggioEconomico>=0 && result==true){
					if(valore > punteggioEconomico){
						alert("Il punteggio economico specificato è maggiore del punteggio economico massimo");
						
					}
				}
				
				var sogliaEconomicaMinima = "${sogliaEconomicaMinima }";
				if(sogliaEconomicaMinima!=""){
					sogliaEconomicaMinima = parseFloat(sogliaEconomicaMinima);
					if(sogliaEconomicaMinima > valore){
						var msg = "Il punteggio economico specificato è inferiore alla soglia minima stabilita. ";
						alert(msg);
							
					}
				}
				
			}
			return result;
		}
		</c:if>
		
		<c:if test="${ ribcal eq '2'}" >
		function aggiornaPerCambioImportoOfferto(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var impoff = toVal(this.value);
			var cifreDecimali = 9;
			if (impoff == null || impoff == ""){
					setValue("DITG_RIBAUO_" + numeroRiga, "");
			}else{
				var numeratore = ${numeratore }; 
				numeratore = parseFloat(numeratore);
				var denominatore = ${denominatore };
				denominatore = parseFloat(denominatore);
				var ribauo; 
				if (denominatore != 0)
					ribauo = (impoff + numeratore) * 100 / denominatore ;
				else
					ribauo = 0;
				
				if (ribauo >0 && ${requestScope.offaum != "1"}){
					alert('Non sono ammesse offerte in aumento');
					setValue("DITG_RIBAUO_" + numeroRiga, "");

				}else {
					<c:if test='${not empty numeroCifreDecimaliRibasso}'>
						cifreDecimali =${numeroCifreDecimaliRibasso};
					</c:if>
					
					setValue("DITG_RIBAUO_" + numeroRiga, round(ribauo,cifreDecimali));
					
					//Si richama l'evento onchange del campo RIBAUO
					document.getElementById("DITG_RIBAUO_" + numeroRiga).onchange();
				}
			} 
		}
	</c:if>
	<c:if test="${ isGaraLottiConOffertaUnica ne true and updateLista eq 1}" >
		associaFunzioniEventoOnchange();
		document.getElementById("numeroDitte").value = ${currentRow}+1;	
	</c:if>
	
	function dettaglioOffertaDittaPerSingoliLotti(){
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioOfferteDittaEsclusa-OffertaUnicaLotti.jsp";
			href += "&key=" + chiaveRiga;
			document.location.href = href;
	}
	</gene:javaScript>
</gene:template>