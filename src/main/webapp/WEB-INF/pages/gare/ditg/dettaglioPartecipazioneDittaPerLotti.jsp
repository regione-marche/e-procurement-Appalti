<%
/*
 * Created on: 30-10-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* Pagina richiamata nel caso di gara ad offerta unica (sia nel caso bustalotti=1 che
  * che bustalotti =2) per specifare la partecipazione della ditta ai lotti
  *
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="ditta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara }" />
	<jsp:param name="filtroCampoEntita" value="codgar = '${codiceGara}'" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<jsp:include page="../gare/fasiGara/defStepWizardFasiGara.jsp" />

<% // Set di una variabile temporanea per costruire il valore delle stringhe     %>
<% // strProtVisualizzaFasiGara e strProtModificaFasiGara in funzione dello step %>
<% // del wizard attivo. Questa variabile e' stata introdotta per non modificare %>
<% // i record presenti nella tabella W_OGGETTI (e tabelle collegate W_AZIONI e  %>
<% // W_PROAZI e di tutti di i profili esistenti) in seguito all'introduzione di %>
<% // nuovi step nel wizard fasi di gara %>


<c:choose>
	<c:when test="${not empty param.stepWizard }">
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.bustalotti }">
		<c:set var="bustalotti" value="${param.bustalotti}" />
	</c:when>
	<c:otherwise>
		<c:set var="bustalotti" value="${bustalotti}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test="${not empty param.isProceduraTelematica }">
		<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.faseGara }">
		<c:set var="faseGara" value="${param.faseGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="faseGara" value="${faseGara}" />
	</c:otherwise>
</c:choose>

<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="ditta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />

<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoAmmgarFunction", pageContext,numeroGara,ditta,stepWizard)}' />

<c:set var="paginaAttivaWizard" value="${stepWizard}" scope="request"/>
<c:set var="varTmp" value="${paginaAttivaWizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DITG-OFFUNICA-PARTGAR">
	<% // Settaggio delle stringhe utilizzate nel template %>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara, codiceGara, ditta)}' />

<c:choose>
	<c:when test="${stepWizard eq '20' }">
		<gene:setString name="titoloMaschera" value='Dettaglio invio offerta ai lotti di gara della ditta ${nomimo }' />
		<c:set var="varTmp" value='1' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='Dettaglio partecipazione ai lotti di gara della ditta ${nomimo }' />
	</c:otherwise>
</c:choose>

	
<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoAmmgarFunction", pageContext,numeroGara,ditta,paginaAttivaWizard)}' />

<c:choose>
	<c:when test="${paginaAttivaWizard eq '-40' }">
		<c:set var="condizioneTelematica" value="${(isProceduraTelematica eq 'true' && (bustalotti eq '1' || (bustalotti eq '2' && faseGara ne '-4' && faseGara ne '-3' )))}" />
	</c:when>
	<c:otherwise>
		<c:set var="condizioneTelematica" value="${(isProceduraTelematica eq 'true' && (bustalotti eq '1' || (bustalotti eq '2' && faseGara ne '2' && faseGara ne '3' && faseGara ne '4')))}" />
	</c:otherwise>
</c:choose>

<c:set var="whereDITG" value='DITG.CODGAR5 = #DITG.CODGAR5# and DITG.NGARA5 <> #DITG.NGARA5# and DITG.DITTAO = #DITG.DITTAO#' />
<c:if test="${stepWizard eq '20' }">
	<c:set var="whereDITG" value='${whereDITG } and (DITG.FASGAR >= 1 or DITG.FASGAR is null)' />
</c:if>
	<gene:redefineInsert name="corpo">

<table class="lista">

		
	<!-- inizia pagine a lista -->

		<tr>
			<td >
				<gene:formLista entita="DITG" where='${whereDITG}' tableclass="datilista" sortColumn="1" pagesize="100" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOfferteTecnicaEconomica" gestisciProtezioni="true" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1 and bloccoAggiudicazione ne 1}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<c:if test="${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,'MOD','MOD') and not ( condizioneTelematica ||
										 (isProceduraTelematica ne 'true' && faseGara >= '7') || bloccoAmmgar eq 'true') }">
										<td class="vocemenulaterale">
											<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1501">
												${gene:resource("label.tags.template.dettaglio.schedaModifica")}
											</a>
										</td>
									</c:if>
									
								</tr>
							</c:otherwise>
						</c:choose>
					</gene:redefineInsert>
					<gene:campoLista campo="NGARA5" headerClass="sortable" width="120" title="Codice lotto"/>
					<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="CODIGA" title="Lotto" entita="GARE" where="GARE.NGARA = DITG.NGARA5" headerClass="sortable" width="100"/>
					<gene:campoLista campo="NOT_GAR" title="Descrizione" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" ordinabile="false" />
					<c:choose>
						<c:when test="${paginaAttivaWizard eq '-40' }">
							
							<c:choose>
								<c:when test="${updateLista eq 1}">
									<c:set var="testoCampoPartgar" value="Partecipa al lotto?<br><a href='javascript:selezionaTutti(1,1);' Title='Imposta tutti a Sì'><img src='${pageContext.request.contextPath}/img/partecipaTuttiSi.png' height='15' width='15' alt='Imposta tutti a Sì' ></a>" />
									<c:set var="testoCampoPartgar" value="${testoCampoPartgar }&nbsp;<a href='javascript:selezionaTutti(2,1);' Title='Imposta tutti a No'><img src='${pageContext.request.contextPath}/img/partecipaTuttiNo.png' height='15' width='15' alt='Imposta tutti a No'></a>" />
									<c:set var="dimCampo" value="170" />
								</c:when>
								<c:otherwise>
									<c:set var="testoCampoPartgar" value="Partecipa al lotto?" />
									<c:set var="dimCampo" value="130" />
								</c:otherwise>
							</c:choose>
							<gene:campoLista campo="PARTGAR" title="${testoCampoPartgar }" headerClass="sortable" edit="${updateLista eq 1}" width="${dimCampo }"/>
							<gene:campoLista campo="INVOFF"  visibile="false" edit="${updateLista eq 1}" />
						
						</c:when>
						<c:otherwise>
							
							<c:choose>
								<c:when test="${updateLista eq 1}">
									<c:set var="testoCampoInvoff" value="Inviato offerta?<br><a href='javascript:selezionaTutti(1,2);' Title='Imposta tutti a Sì'><img src='${pageContext.request.contextPath}/img/partecipaTuttiSi.png' height='15' width='15' alt='Imposta tutti a Sì'></a>" />
									<c:set var="testoCampoInvoff" value="${testoCampoInvoff }&nbsp;<a href='javascript:selezionaTutti(2,2);' Title='Imposta tutti a No'><img src='${pageContext.request.contextPath}/img/partecipaTuttiNo.png' height='15' width='15' alt='Imposta tutti a No'></a>" />
									<c:set var="dimCampo" value="170" />
									
								</c:when>
								<c:otherwise>
									<c:set var="testoCampoInvoff" value="Inviato offerta?" />
									<c:set var="dimCampo" value="130" />
									
								</c:otherwise>
							</c:choose>
							<gene:campoLista campo="INVOFF"   title="${testoCampoInvoff }" headerClass="sortable" edit="${updateLista eq 1}" width="${dimCampo }"/>
							<gene:campoLista campo="PARTGAR"  visibile="false" edit="${updateLista eq 1}" />
						</c:otherwise>
					</c:choose>
					
					<gene:campoLista campo="REQMIN" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNTEC" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="RIBAUO" title="Ribasso offerto" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;PRC;RIBAUO" />
					<gene:campoLista campo="IMPOFF" title="Importo" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;;PUNECO" />
					<gene:campoLista campo="INVGAR"  visibile="false" edit="${updateLista eq 1}"  />
					<% // Questo campo non viene MAI modificato nella pagina e la sua valorizzazione viene gestito solo lato server. %>
					<% // E' presente nella pagina come campo hidden per capire se il valore del campo V_DITGAMMIS.AMMGAR e' ereditato %>
					<% // da una fase precedente o e' effettivamente un valore esistente per lo step del wizard in visualizzazione %>
					<gene:campoLista campo="AMMGAR" entita="DITGAMMIS" where="DITGAMMIS.CODGAR=DITG.CODGAR5 and DITGAMMIS.NGARA=DITG.NGARA5 and DITGAMMIS.DITTAO=DITG.DITTAO and DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPSICAZI" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPMANO" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPPERM" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPCANO" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" title="Off.Ammessa?" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" edit="${updateLista eq 1}" visibile='false'/>
					<gene:campoLista campo="NGARA5fit" campoFittizio="true" definizione="T10" visibile="false" value="${datiRiga.DITG_NGARA5}" edit="${updateLista eq 1}" />
				<c:if test='${updateLista eq 0}' >
					<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
				</c:if>
					<gene:campoLista campo="FASGAR"  visibile="false" edit="${updateLista eq 1}"  />
					
					<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
					<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
					<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
					<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
					<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="isBloccoAggiudicazione" id="isBloccoAggiudicazione" value="${bloccoAggiudicazione}" />
					
					<input type="hidden" name="isOffertaPerLotto" id="isOffertaPerLotto" value="${isOffertaPerLotto}" />
					<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard}" />
					<input type="hidden" name="dettaglioPartecipazioneDittaPerLotti" id="dettaglioPartecipazioneDittaPerLotti" value="" />
					<input type="hidden" name="dettaglioInvioOffertoDittaPerLotti" id="dettaglioInvioOffertoDittaPerLotti" value="" />
					<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti} " />
					<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica} " />
					<input type="hidden" name="faseGara" id="faseGara" value="${faseGara} " />
					<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara}" />
					<input type="hidden" name="ditta" id="ditta" value="${ditta}" />
					<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}" />
				</gene:formLista>
			</td>
		</tr>
	
<!-- fine pagine a lista -->

	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
					<INPUT type="button"  class="bottone-azione" value='Torna a elenco concorrenti' title='Torna a elenco concorrenti' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
					<c:if test="${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,'MOD','MOD') and not (condizioneTelematica ||
						 (isProceduraTelematica ne 'true' && faseGara >= '7') || bloccoAmmgar eq 'true') }">
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaLista();">
					</c:if>
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
</table>

<gene:javaScript>

document.getElementById("numeroDitte").value = ${currentRow}+1;
<c:choose>
	<c:when test="${stepWizard eq '20' }">
		document.getElementById("dettaglioPartecipazioneDittaPerLotti").value = false;
		document.getElementById("dettaglioInvioOffertoDittaPerLotti").value = true;
	</c:when>
	<c:otherwise>
		document.getElementById("dettaglioPartecipazioneDittaPerLotti").value = true;
		document.getElementById("dettaglioInvioOffertoDittaPerLotti").value = false;
	</c:otherwise>
</c:choose>

<c:if test='${updateLista eq 1}'>
		
		// Funzione per associare le funzioni JS da eseguire al momento di modifica
		// di un campo. Questo e' necessario perchè la lista e' stata progetta per
		// non essere mai modificabile e quindi il tag gene:campoLista non associa
		// mai una funzione JS all'evento onchange.
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
				document.getElementById("DITG_PARTGAR_" + i).onchange = aggiornaPerCambioPartecipazioneGara;
				document.getElementById("DITG_INVOFF_" + i).onchange = aggiornaPerCambioInvioOfferta;
			}
		}
		
		// Funzioni JS richiamate subito dopo la creazione della pagina per l'
		associaFunzioniEventoOnchange();
				
			
		function aggiornaPerCambioPartecipazioneGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value == "2"){
				setValue("DITG_PUNTEC_" + numeroRiga, "");
				setValue("DITG_REQMIN_" + numeroRiga, "");
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
				
				setValue("DITG_PUNECO_" + numeroRiga, "");
				setValue("DITG_IMPOFF_" + numeroRiga, "");
				setValue("DITG_RIBAUO_" + numeroRiga, "");
				setValue("DITG_REQMIN_" + numeroRiga, "");
				setValue("DITG_IMPSICAZI_" + numeroRiga, "");
				setValue("DITG_IMPMANO_" + numeroRiga, "");
				setValue("DITG_IMPPERM_" + numeroRiga, "");
				setValue("DITG_IMPCANO_" + numeroRiga, "");
				
				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
				<c:if test="${paginaAttivaWizard eq step1Wizard }">
					setValue("DITG_INVOFF_" + numeroRiga, "1");
				</c:if>
					
				
				setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, "");
				setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, "");
			}
		}
		
		function aggiornaPerCambioInvioOfferta(){
		var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var partgar = getValue("DITG_PARTGAR_" + numeroRiga);
			if(partgar== null || partgar== "")
				setValue("DITG_PARTGAR_" + numeroRiga, "1");
			
			setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, "");
			setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, "");
						
			if(this.value == "2"){
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "2");
				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
				
			}else{
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
			
			}
			

		}

</c:if>


		function annulla(){
			listaAnnullaModifica();
		}

		function modificaLista(){
			document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 2;
			listaApriInModifica();
		}
		
		<c:if test="${updateLista eq 1}">
			$("#titDITG_PARTGAR").attr('class', '');
			$("#titDITG_INVOFF").attr('class', '');
			
			function selezionaTutti(valore,idCampo){
				var continuare= true;
				var msgCampo;
				var campo;
				if(idCampo==1){
					msgCampo="Partecipa al lotto";
					campo = "PARTGAR";
				}else{
					msgCampo="Inviato offerta";
					campo = "INVOFF";
				}
				if(valore==2){
					
					continuare=confirm("Confermi l'impostazione a 'No' del campo '" + msgCampo + "' per tutti i lotti nella lista?");
				} else if (valore==1){
					continuare=confirm("Confermi l'impostazione a 'Si' del campo '" + msgCampo + "' per tutti i lotti nella lista?");
				}
				if(continuare){
					for(var i=1; i <= ${currentRow}+1; i++){
						document.getElementById("DITG_" + campo + "_" + i).value=valore;
						//Si richama l'evento onchange del campo PARTGAR
						document.getElementById("DITG_" + campo + "_" + i).onchange();
					}
				}
			}
        </c:if>
       
        
</gene:javaScript>

</gene:redefineInsert>
</gene:template>