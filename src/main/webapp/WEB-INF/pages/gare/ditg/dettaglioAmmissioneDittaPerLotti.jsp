<%
/*
 * Created on: 04-08-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* Pagina richiamata nel caso di gara ad offerta unica (sia nel caso bustalotti=1 che
  * che bustalotti =2) per specifare l'ammissione della ditta ai lotti
  *
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

<c:set var="risultatiPerPagina" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.fasi.paginazione")}' scope="request"/>

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


<c:set var="paginaAttivaWizard" value="${stepWizard}" scope="request"/>
<c:set var="varTmp" value="${paginaAttivaWizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DITG-OFFUNICA-AMMGAR">
	<% // Settaggio delle stringhe utilizzate nel template %>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara, codiceGara, ditta)}' />

<gene:setString name="titoloMaschera" value='Dettaglio ammissione ai lotti di gara della ditta ${nomimo }' />

<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.gare.note")}' scope="request"/>

<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoAmmgarFunction", pageContext,numeroGara,ditta,stepWizard)}' />

<c:set var="whereDITG" value="DITG.CODGAR5 = #DITG.CODGAR5# and DITG.NGARA5 <> #DITG.NGARA5# and DITG.DITTAO = #DITG.DITTAO# and (DITG.PARTGAR is null or DITG.PARTGAR = '1')"/>
<c:if test="${paginaAttivaWizard eq step1Wizard }">
	<c:set var="whereDITG" value="${whereDITG} and (DITG.FASGAR is null or DITG.FASGAR > 1) "/>
</c:if>


<c:if test="${isProceduraTelematica eq 'true' && paginaAttivaWizard eq step1Wizard}">
	<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />
	<c:if test="${garaInversa eq '1' }">
		<c:set var="isBustaElaborata" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsBustaElaborataFunction", pageContext,numeroGara,ditta, "FS11A")}' />
		<c:if test="${isBustaElaborata eq 'Si' or isBustaElaborata eq 'NonEsiste' }">
			<c:set var="visGaraInversa" value='true' />
		</c:if>
		<c:set var="faseGaraComplementare" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFASGARFunction", pageContext, codiceGara)}' />
		<c:if test="${faseGaraComplementare ne '2' }">
			<c:set var="modGaraInversa" value='true' />
		</c:if>
	</c:if>
</c:if>


<c:choose>
	<c:when test="${paginaAttivaWizard eq '-40' }">
		<c:set var="condizioneTelematica" value="${isProceduraTelematica eq 'true' && (faseGara ne '-4' && faseGara ne '-3' )}" />
	</c:when>
	<c:otherwise>
		<c:set var="condizioneTelematica" value="${(isProceduraTelematica eq 'true' && (faseGara ne '2' && faseGara ne '3' && faseGara ne '4')) and modGaraInversa ne 'true'}" />
	</c:otherwise>
</c:choose>


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
										<a href="javascript:listaConfermacustom();" title="Salva modifiche" tabindex="1500">
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
									<c:if test="${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,'MOD','MOD') and (not ( condizioneTelematica ||
										 (isProceduraTelematica ne 'true' && faseGara >= '7') || bloccoAmmgar eq 'true'))}">
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
					<gene:campoLista campo="NGARA5fit" campoFittizio="true" definizione="T10" visibile="false" value="${datiRiga.DITG_NGARA5}" edit="${updateLista eq 1}" />
					<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="CODIGA" title="Lotto" entita="GARE" where="GARE.NGARA = DITG.NGARA5" headerClass="sortable" width="100"/>
					<gene:campoLista campo="NOT_GAR" title="Descrizione" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" ordinabile="false" />
					<gene:campoLista campo="PARTGAR" title="Partecipa al lotto?" headerClass="sortable" edit="${updateLista eq 1}" visibile="false" />
					<gene:campoLista campo="REQMIN" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNTEC" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="RIBAUO" title="Ribasso offerto" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;PRC;RIBAUO" />
					<gene:campoLista campo="IMPOFF" title="Importo" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;;PUNECO" />
					<gene:campoLista campo="INVOFF"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="INVGAR"  visibile="false" edit="${updateLista eq 1}"  />
					<% // Questo campo non viene MAI modificato nella pagina e la sua valorizzazione viene gestito solo lato server. %>
					<% // E' presente nella pagina come campo hidden per capire se il valore del campo V_DITGAMMIS.AMMGAR e' ereditato %>
					<% // da una fase precedente o e' effettivamente un valore esistente per lo step del wizard in visualizzazione %>
					<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" title="Off.Ammessa?" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" edit="${updateLista eq 1 and (garaInversa ne '1' or (garaInversa eq '1'  and faseGaraComplementare <5))}" width="50"/>
					
					<c:choose>
						<c:when test="${updateLista eq 1 && (isBustaElaborata eq 'Si' or isBustaElaborata eq 'NonEsiste')}">
							<c:set var="testoCampo" value="Esito verif. proc.inversa<br><a href='javascript:selezionaTutti(1);' Title='Imposta tutti a Idonea'><img src='${pageContext.request.contextPath}/img/partecipaTuttiSi.png' height='15' width='15' alt='Imposta tutti a Idonea' ></a>" />
							<c:set var="testoCampo" value="${testoCampo }&nbsp;<a href='javascript:selezionaTutti(2);' Title='Imposta tutti a Non idonea'><img src='${pageContext.request.contextPath}/img/partecipaTuttiNo.png' height='15' width='15' alt='Imposta tutti a Non idonea'></a>" />
							<c:set var="dimCampo" value="110" />
						</c:when>
						<c:otherwise>
							<c:set var="testoCampo" value="Esito verif. proc.inversa" />
							<c:set var="dimCampo" value="80" />
						</c:otherwise>
					</c:choose>
					<gene:campoLista campo="AMMINVERSA" width="${dimCampo }" headerClass="sortable" visibile="${garaInversa eq '1' }" ordinabile ="true" edit="${updateLista eq 1}" title="${testoCampo }" />
					
					<gene:campoLista campo="AMMGAR" entita="DITGAMMIS" where="DITGAMMIS.CODGAR=DITG.CODGAR5 and DITGAMMIS.NGARA=DITG.NGARA5 and DITGAMMIS.DITTAO=DITG.DITTAO and DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPSICAZI" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPMANO" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPPERM" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPCANO" visibile="false" edit="${updateLista eq 1}" />
					
					<gene:campoLista campo="FASGAR"  visibile="false" edit="true"  />
					
					<c:if test="${iconaNoteAttiva eq 1}">
						<gene:campoLista campo="ALTNOT"  visibile="false"/>
					</c:if>
					<gene:campoLista title="&nbsp;" width="20">
						<c:if test="${iconaNoteAttiva eq 1}">
							<c:choose>
								<c:when test="${not empty datiRiga.DITG_ALTNOT}">
									<c:set var="note" value="_note"/>
									<c:set var="iconaTooltip" value="Ulteriori dettagli con note"/>
								</c:when>
								<c:otherwise>
									<c:set var="note" value=""/>
									<c:set var="iconaTooltip" value="Ulteriori dettagli"/>
								</c:otherwise>
							</c:choose>	
						</c:if>
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';ulterioriCampi(${currentRow+1}, '${chiaveRigaJava}');" title="Ulteriori dettagli" >
							<img width="16" height="16" title="${iconaTooltip}" alt="Ulteriori dettagli" src="${pageContext.request.contextPath}/img/opzioni${note}.png"/>
						</a>
					</gene:campoLista>
					
					<c:if test='${updateLista eq 0 or (updateLista eq 1 and garaInversa eq "1" and  faseGaraComplementare >=5)}' >
						<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
						
					</c:if>
					
					<c:if test='${updateLista eq 0}' >
					<gene:campoLista campo="DITG_AMMINVERSA_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_AMMINVERSA}" />
					</c:if>
					
					<c:if test="${garaInversa eq '1'  }">
						<gene:campoLista campo="DITTA"  entita="GARE" where="GARE.NGARA=DITG.NGARA5" visibile="false" edit="true"  />
						<gene:campoLista campo="DITTAP"  entita="GARE" where="GARE.NGARA=DITG.NGARA5" visibile="false" edit="true"  />
						<gene:campoLista campo="STAGGI"   visibile="false" edit="true"  />
					</c:if>
					
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
					<input type="hidden" name="dettaglioAmmissioneDittaPerLotti" id="dettaglioAmmissioneDittaPerLotti" value="true" />
					<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti}" />
					<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica}" />
					<input type="hidden" name="faseGara" id="faseGara" value="${faseGara}" />
					<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara}" />
					<input type="hidden" name="ditta" id="ditta" value="${ditta}" />
					<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}" />
					<input type="hidden" name="modGaraInversa" id="modGaraInversa" value="${modGaraInversa}" />
					<input type="hidden" name="garaInversaAmmgarBloccato" id="garaInversaAmmgarBloccato" value='${updateLista eq 1 and garaInversa eq "1" and  faseGaraComplementare >=5}' />
				</gene:formLista>
			</td>
		</tr>
	
<!-- fine pagine a lista -->

	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConfermacustom();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
					<INPUT type="button"  class="bottone-azione" value='Torna a elenco concorrenti' title='Torna a elenco concorrenti' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
					<c:if test="${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,'MOD','MOD') and (not ( condizioneTelematica ||
										 (isProceduraTelematica ne 'true' && faseGara >= '7') || bloccoAmmgar eq 'true') ) }">
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

<c:if test='${updateLista eq 1}'>
		
		
		function listaConfermacustom(){
			<c:if test="${garaInversa eq '1'}" >
				var dittaProvv;
				var dittaDef;
				var staggi;
				var amminversa;
				var amminversaOrig;
				var esisteAmminversa=false;
				for(var i=0; i < ${currentRow} + 1; i++){
					dittaProvv = getValue("GARE_DITTAP_" + (i+1));
					dittaDef = getValue("GARE_DITTA_" + (i+1));
					if(dittaProvv !=null && dittaProvv !="" && (dittaDef == null || dittaDef == "")){
						staggi = getValue("DITG_STAGGI_" + (i+1));
						amminversa = getValue("DITG_AMMINVERSA_" + (i+1));
						var amminversaOrig = getOriginalValue("DITG_AMMINVERSA_" + (i+1));
						if(amminversa=="2" && staggi==4 && amminversa!=amminversaOrig){
							esisteAmminversa=true;
							break;
						}
					}
				}
				if(esisteAmminversa){
					var risposta=confirm("Procedendo al salvataggio, verra' annullata la proposta di aggiudicazione dei lotti per cui la ditta corrente è prima classificata e che, nella verifica della documentazione amministrativa dopo l'apertura delle offerte (procedura inversa), è risultata 'Non idonea'.\nContinuare?");
					if(!risposta)
						return;
				}
				
				//Si riabilitano i campi DITG.AMMINVERSA che sono stati disabilitati perchè associati a lotti aggiudicati in via definitiva,
				//per evitare che al salvataggio della pagina si sbianchi il valore del campo
				for(var i=0; i < ${currentRow} + 1; i++){
					if(document.getElementById("DITG_AMMINVERSA_" + (i+1))!=null && document.getElementById("DITG_AMMINVERSA_" + (i+1)).disabled)
						document.getElementById("DITG_AMMINVERSA_" + (i+1)).disabled = false;
				}
				
			</c:if>
			
			listaConferma();
		}
		
		// Funzione per associare le funzioni JS da eseguire al momento di modifica
		// di un campo. Questo e' necessario perchè la lista e' stata progetta per
		// non essere mai modificabile e quindi il tag gene:campoLista non associa
		// mai una funzione JS all'evento onchange.
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
				if(document.getElementById("V_DITGAMMIS_AMMGAR_" + i)!=null)
					document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
				
			}
		}
		
		// Funzioni JS richiamate subito dopo la creazione della pagina per l'
		associaFunzioniEventoOnchange();
				
			
		function aggiornaPerCambioAmmessaGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var stepWizard = "${stepWizard }";
			if(this.value != "2" && this.value != "6" && this.value != "9"){
				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
				if(stepWizard == "20")
					setValue("DITG_INVOFF_" + numeroRiga, "1");
				setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, "");
				setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, "");
				
			} else{
				
				if(stepWizard == "-40")
					setValue("DITG_INVGAR_" + numeroRiga, "");
			}
			
			if( (this.value !="") && (getValue("DITG_PARTGAR_" + numeroRiga) == null || getValue("DITG_PARTGAR_" + numeroRiga) == ""))
				setValue("DITG_PARTGAR_" + numeroRiga, "1");
				
			<c:if test="${garaInversa eq '1' && (isBustaElaborata eq 'Si' or isBustaElaborata eq 'NonEsiste')}">
				if(this.value == "2" || this.value == "6" || this.value == "9"){
					setValue("DITG_AMMINVERSA_" + numeroRiga, "");
					showObj("colDITG_AMMINVERSA_" + numeroRiga, false);
				}else{
					showObj("colDITG_AMMINVERSA_" + numeroRiga, true);	
				}
			</c:if>
		}

	<c:if test="${garaInversa eq '1'  }">
	$("#titDITG_AMMINVERSA").attr('class', '');
	function selezionaTutti(valore){
		var continuare= true;
		if(valore==2){
			continuare=confirm("Confermi l'impostazione a 'Non idonea' del campo 'Esito verifica procedura inversa' per tutti i lotti nella lista?");
		} else if (valore==1){
			continuare=confirm("Confermi l'impostazione a 'Idonea' del campo 'Esito verifica procedura inversa' per tutti i lotti nella lista?");
		}
		if(continuare){
			for(var i=1; i <= ${currentRow}+1; i++){
				var ditta;
        		var ammgar;
				var bloccoAmmgarFaseGara = "${faseGaraComplementare >=5 }";
				for(var i=1; i <= ${currentRow}+1; i++){
					ditta = getValue("GARE_DITTA_" + i);
					if(bloccoAmmgarFaseGara == "true")
						ammgar = getValue("V_DITGAMMIS_AMMGAR_FITTIZIO_" + i);
					else
						ammgar = getValue("V_DITGAMMIS_AMMGAR_" + i);
					if((ditta==null || ditta=='') && ammgar!=2 && ammgar!=6 && ammgar!=9)
						setValue("DITG_AMMINVERSA_" + i, valore);
				}
			}
		}
	}
	</c:if>

</c:if>
	
		function ulterioriCampi(indiceRiga, chiaveRiga){
			href = "href=gare/ditg/ditg-schedaPopup-DettaglioAmmissioneLotti.jsp";
			<c:if test='${updateLista eq "1"}' >
				href += "&modo=MODIFICA";
			</c:if>
			href += "&indiceRiga=" + indiceRiga;
			href += "&key=" + chiaveRiga;
			href += "&moties=" + getValue('V_DITGAMMIS_MOTIVESCL_'+indiceRiga); //href += "&moties=" + getValue('V_DITG_MOTIES_'+indiceRiga);
			href += "&stepWizard=${varTmp}";
			href += "&paginaAttivaWizard=${paginaAttivaWizard }";
			<c:if test="${garaInversa eq '1'  }">
				href += "&garaInversa=true";
			</c:if>
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
	

		function annulla(){
			listaAnnullaModifica();
		}

		function modificaLista(){
			document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 2;
			listaApriInModifica();
		}
		
		<c:if test="${updateLista eq 1}">
			$("#titDITG_PARTGAR").attr('class', '');
			
			
        </c:if>
       
        <c:if test="${garaInversa eq '1'  }">
        	function inizializzazioneListaGaraInversa(){
        		var ditta;
        		var isBustaElaborata = "${isBustaElaborata }";
				var fasgar;
				for(var i=1; i <= ${currentRow}+1; i++){
					ditta = getValue("GARE_DITTA_" + i);
					fasgar = getValue("DITG_FASGAR_" + i);
					if(isBustaElaborata == "No" || fasgar==2)
						showObj("colDITG_AMMINVERSA_" + i, false);
					else if(ditta!=null && ditta!='')
						document.getElementById("DITG_AMMINVERSA_" + i).disabled = true;
						
				}
			}
			
			inizializzazioneListaGaraInversa();
        </c:if>
        
</gene:javaScript>

</gene:redefineInsert>
</gene:template>