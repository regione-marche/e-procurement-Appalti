<%/*
   * Created on 06-10-2009
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA POPUP PER IMPORT OFFERTA PREZZI E RISULTATO IMPORT
  // OFFERTA PREZZI

%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="locNGara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="locNGara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceDitta}'>
		<c:set var="locCodiceDitta" value="${param.codiceDitta}" />
	</c:when>
	<c:otherwise>
		<c:set var="locCodiceDitta" value="${codiceDitta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isPrequalifica and param.isPrequalifica eq "true"}' >
		<c:set var="isPrequalifica" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="isPrequalifica" value='false' />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.garaLottiConOffertaUnica}'>
		<c:set var="locGaraLottiConOffertaUnica" value="${param.garaLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="locGaraLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>


<c:if test="${!empty locCodiceDitta }">
	<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>
	<c:if test="${empty RISULTATO }">
		<c:set var="controlloPrezziOfferti" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloValorizzazionePrezziOffertiDittaFunction", pageContext,locNGara,locCodiceDitta,locGaraLottiConOffertaUnica)}'/>
	</c:if>
</c:if>

<c:if test="${!empty locCodiceDitta }">
	<c:set var="msgDitta" value=" dalla ditta"/>
</c:if>


<table style="width:100%;">
	<tr>
		<td>
			<div class="contenitore-popup">

			<c:choose>
				<c:when test='${empty RISULTATO or RISULTATO eq "KO"}'>
					<form method="post" enctype="multipart/form-data" action="${contextPath}/pg/EseguiImportVariazionePrezzi.do" name="importOffertaPrezzi" >
						<table class="dettaglio-notab">
					    <tr>
					    	<td colspan="2">
					    		<p>
					    			Mediante questa funzione è possibile <b>importare</b> le variazioni dei prezzi unitari <c:if test="${empty nomeImpresa}" >a base di gara delle lavorazioni e forniture della gara</c:if><c:if test="${not empty nomeImpresa}" >offerti della ditta</c:if>.
									  <br>
									  <br>
									 <c:if test="${controlloPrezziOfferti eq 'NOK' }">
									  <b>Per procedere alla variazione prezzi devono essere prima valorizzati tutti i prezzi unitari offerti della ditta</b>
									  <br>
									  <br>
									  </c:if>
									</p>
								</td>
							</tr>
							<tr>
								<td class="etichetta-dato">File Excel da importare (*)</td>
								<td class="valore-dato"><input type="file" name="selezioneFile" maxlength="255" size="55" onkeydown="return bloccaCaratteriDaTastiera(event);"></td>
								
							</tr>
							<tr>
							<td colspan="2">
								<br>
							<td
							</tr>
					
						<c:if test='${RISULTATO eq "KO"}'>
							<% // Visualizzazione della lista degli errori %>
							<tr>
						    	<td colspan="2">
						    		<p>
 								      <br><b>Importazione interrotta.</b>
									  <br>&nbsp;
										<c:if test='${fn:length(loggerImport.listaMsgVerificaFoglio) > 0}'>	
									  	<br><b>Messaggi di errore:</b>
									  </c:if>
									</p>
								</td>
							</tr>

						<c:if test='${fn:length(loggerImport.listaMsgVerificaFoglio) > 0}'>					
							<tr>
								<td colspan="2" class="valore-dato" style="max-width:100px">
									<textarea cols="95" rows="12" readonly="readonly" style="max-width:100%" ><c:forEach items="${loggerImport.listaMsgVerificaFoglio}" var="msgErrore" ><c:out value="${msgErrore}" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/></c:forEach></textarea>
								</td>
							</tr>
						</c:if>
						</c:if>
						
						
							
					  	<tr>
						    <td class="comandi-dettaglio" colspan="2">
									<c:if test="${controlloPrezziOfferti ne 'NOK' }">
										<input type="button" class="bottone-azione" value="Importa" title="Importa" onclick="javascript:importa();">&nbsp;
									</c:if>
									<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
									<input type="hidden" name="ngara" id="ngara" value="${locNGara}"/>
									<input type="hidden" name="codiceDitta" id="codiceDitta" value="${locCodiceDitta}"/>
									<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}"/>
					  	  </td>
						  </tr>
						</table>
						<input type="hidden" name="garaLottiConOffertaUnica" id="garaLottiConOffertaUnica" value="${locGaraLottiConOffertaUnica}" />
						<input type="hidden" name="isCodificaAutomatica" id="isCodificaAutomatica" value="${locIsCodificaAutomatica}" />
						<input type="hidden" name="isPrequalifica" id="isPrequalifica" value="${isPrequalifica}" />
						
					</form>
				</c:when>
				<c:otherwise>
					<table class="lista">
				    <tr>
				    	<td>
							<table class="arealayout" >
							    <tr>
							    	<td colspan="3">
							    			<p>
							    			  Importazione completata.
											  <br>&nbsp;
											  <br><b>Dettaglio operazione di import:</b>
											</p>
							      </td>
							    </tr>
									<tr>
										<td width="35%">Numero righe lette dal file Excel:</td>
										<td width="5%" align="right">${loggerImport.numeroRigheLette}</td> <!-- align="right" -->
									</tr>
							<tr>
								<td width="45%">Numero righe aggiornate:</td>
								<td width="5%" align="right">${loggerImport.numeroRecordAggiornati}</td> <!-- align="right" -->
							</tr>
							<tr>
								<td width="45%">Numero righe non aggiornate:</td>
								<td width="5%" align="right">${loggerImport.numeroRecordNonAggiornati}</td> <!-- align="right" -->
							</tr>
							<c:if test='${loggerImport.numeroRecordNonAggiornatiLottiNonAggiudicati > 0}'>
							<tr>
								<td width="45%">Numero righe scartate perchè lotti non aggiudicati${msgDitta}:</td>
								<td width="5%" align="right">${loggerImport.numeroRecordNonAggiornatiLottiNonAggiudicati}</td> <!-- align="right" -->
							</tr>
							</c:if>
										
							<c:if test='${loggerImport.numeroRecordAggiornati > 0 or loggerImport.numeroRecordNonAggiornati > 0 or loggerImport.numeroRecordNonAggiornatiLottiNonAggiudicati > 0}'>
								<% // Visualizzazione della lista degli errori %>
								<tr>
							    	<td colspan="3"><br><b>Messaggi di errore:</b></td>
							    </tr>
							    <tr>
							    	<td colspan="3" class="valore-dato" style="max-width:100px">
							    		<textarea cols="95" rows="12" readonly="readonly" style="max-width:100%"><c:forEach items="${loggerImport.listaMessaggiErrore}" var="msgErrore" ><c:out value="${msgErrore}" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/></c:forEach></textarea>
							    	</td>
							    </tr>
							</c:if>

							    <tr class="comandi-dettaglio">
							      <td colspan="3">
											<input type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
							      </td>
							    </tr>
								</table>
							</td>
						</tr>
					</table>
				</c:otherwise>
			</c:choose>

			</div>
		</td>
	</tr>
</table>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
			<IFRAME class="gene" id="iframepopmenu"></iframe>
			<div id="popmenu" class="popupmenuskin"
				onMouseover="highlightMenuPopup(event,'on');"
				onMouseout="highlightMenuPopup(event,'off');"></div>

<script type="text/javascript">
<!--
	var tipofornitura="${tipoFornitura}"

	function importa(){
		if(document.importOffertaPrezzi.selezioneFile) {
			var nomeCompletoFile = "" + document.importOffertaPrezzi.selezioneFile.value;
			if(nomeCompletoFile != ""){
				if(nomeCompletoFile.substr(nomeCompletoFile.lastIndexOf('.')+1).toUpperCase() == "XLS") {
					bloccaRichiesteServer();
					setTimeout("document.importOffertaPrezzi.submit()", 150);
				} else {
					alert("Indicare un file Excel");
					document.importOffertaPrezzi.selezioneFile.value = "";
				}
			}
			if (document.importOffertaPrezzi.selezioneFile.value == "") {
				alert("Deve essere indicato il file Excel da importare.");				
			}
		}
	}

	function annulla(){
		window.close();
	}

	function initPagina(){
		checkAttivaBloccoPaginaPopup();
		<c:choose>
			<c:when test='${not empty nomeImpresa}'>
				document.getElementById("titolomaschera").innerHTML = "Importazione variazione prezzi unitari offerti della ditta <c:out value='${nomeImpresa}'/> per la gara ${locNGara}";
			</c:when>
			<c:otherwise>
				document.getElementById("titolomaschera").innerHTML = "Importazione variazione prezzi unitari delle lavorazioni e forniture della gara ${locNGara}";
			</c:otherwise>
		</c:choose>
	}

	<c:if test='${RISULTATO eq "OK"}'>
		<% // Ricarica della lista sottostante %>
		var n = window.opener.document.forms[0].pgCorrente.value;
		window.opener.listaVaiAPagina(n);
		
		window.onfocus=fnFocus;
	</c:if>

-->
</script>