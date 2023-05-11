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
	<c:when test='${not empty param.codgar}'>
		<c:set var="locCodgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="locCodgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.garaLottiOffDist}'>
		<c:set var="locGaraLottiOffDist" value="${param.garaLottiOffDist}" />
	</c:when>
	<c:otherwise>
		<c:set var="locGaraLottiOffDist" value="${garaLottiOffDist}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isCodificaAutomatica}'>
		<c:set var="locIsCodificaAutomatica" value="${param.isCodificaAutomatica}" />
	</c:when>
	<c:otherwise>
		<c:set var="locIsCodificaAutomatica" value="${isCodificaAutomatica}" />
	</c:otherwise>
</c:choose>

<c:set var="isRichiestaCigGara" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteRichiestaCigGaraFunction",  pageContext, "GARA", locCodgar )}'/>

<c:set var="esistonoLotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoLottiFunction", pageContext, locCodgar)}'/>

<c:choose>
<c:when test='${esistonoLotti && (isRichiestaCigGara ne "SI") && empty param.doubleCheck && empty RISULTATO}'>
<table style="width:100%;">
	<tr>
		<td>
			<div class="contenitore-popup">

						<table class="dettaglio-notab">
							<tr>
						    	<td colspan="2">
										<br>
						    			Mediante questa funzione è possibile <b>importare</b> la lista dei lotti della gara.
										<br>
										<br><b>ATTENZIONE: confermando l'operazione, i lotti attualmente definiti per la gara, con i relativi dati di dettaglio (criteri di valutazione, lista lavorazioni, documenti allegati ...), verranno eliminati.</b>
										<br><br>Confermi l'operazione?
									  	<br>&nbsp;
									  	<br>&nbsp;
						      	</td>
					  	  	</tr>
							
					  	<tr>
						    <td class="comandi-dettaglio" colspan="2">
							    	<input type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;
									<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
									<input type="hidden" name="codgar" id="codgar" value="${locCodgar}"/>
					  	  </td>
						  </tr>
						</table>
						<input type="hidden" name="garaLottiOffDist" id="garaLottiOffDist" value="${locGaraLottiOffDist}" />
						<input type="hidden" name="isCodificaAutomatica" id="isCodificaAutomatica" value="${locIsCodificaAutomatica}" />
						
			</div>
		</td>
	</tr>
</table>
<script type="text/javascript">
<!--
	function initPagina(){
		checkAttivaBloccoPaginaPopup();
			document.getElementById("titolomaschera").innerHTML = "Importazione dei lotti della gara ${locCodgar}";
	}

	function conferma() {
		var href;
		href = "codgar="+"${locCodgar}";
		href += "&isCodificaAutomatica=" + "${locIsCodificaAutomatica}";
		href += "&garaLottiOffDist=" + "${locGaraLottiOffDist}";
		href += "&doubleCheck=OK";
		document.location.href = "${pageContext.request.contextPath}/pg/InitImportLottiGara.do?" + href;	
	}


	function annulla(){
		window.close();
	}
-->
</script>
</c:when>
<c:otherwise>
<table style="width:100%;">
	<tr>
		<td>
			<div class="contenitore-popup">

			<c:choose>
				<c:when test='${empty RISULTATO or RISULTATO eq "KO"}'>
					<form method="post" enctype="multipart/form-data" action="${contextPath}/pg/EseguiImportLottiGara.do" name="importLottiGara" >
						<table class="dettaglio-notab">
						<c:choose>
						<c:when test='${isRichiestaCigGara ne "SI"}'>
						    <tr>
							    	<td colspan="2">
							    		<p>
							    			Mediante questa funzione è possibile <b>importare</b> la lista dei lotti della gara.
											  <br><br>Selezionare il file da importare.
											  <br>&nbsp;
											  <br>&nbsp;
											</p>
										</td>
									</tr>
									<tr>
										<td class="etichetta-dato">File Excel da importare (*)</td>
										<td class="valore-dato"><input type="file" name="selezioneFile" maxlength="255" size="55" onkeydown="return bloccaCaratteriDaTastiera(event);"></td>
									</tr>
									<tr>
						    		<td colspan="2">
								 	    <br>&nbsp;
										<c:if test='${esistonoLotti }'>
											<br><b>ATTENZIONE: durante l'operazione di import verranno eliminati i lotti esistenti della gara.</b>
										  	<br>&nbsp;
									     </c:if>
									  	<br>&nbsp;
							      	</td>
							  	  	</tr>
						 </c:when>
						<c:otherwise>
						 	<c:set var="msgSimog" value="Non è possibile procedere con l'importazione dei lotti perchè è stata già presentata la richiesta CIG a SIMOG per la gara" />
									<tr>
						    		<td colspan="2">
										  <br>&nbsp;
										  <br>${msgSimog}
									  	<br>&nbsp;
									  	<br>&nbsp;
						      	</td>
					  	  	</tr>
     					</c:otherwise>
						</c:choose>

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
									<textarea cols="95" rows="12" readonly="readonly" style="max-width:100%"><c:forEach items="${loggerImport.listaMsgVerificaFoglio}" var="msgErrore" ><c:out value="${msgErrore}" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/></c:forEach></textarea>
								</td>
							</tr>
						</c:if>
						</c:if>
							
					  	<tr>
						    <td class="comandi-dettaglio" colspan="2">
							    	<c:if test='${isRichiestaCigGara ne "SI"}'>
							    		<input type="button" class="bottone-azione" value="Importa" title="Importa" onclick="javascript:importa();">&nbsp;
							    	</c:if>
									<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
									<input type="hidden" name="codgar" id="codgar" value="${locCodgar}"/>
					  	  </td>
						  </tr>
						</table>
						<input type="hidden" name="garaLottiOffDist" id="garaLottiOffDist" value="${locGaraLottiOffDist}" />
						<input type="hidden" name="isCodificaAutomatica" id="isCodificaAutomatica" value="${locIsCodificaAutomatica}" />
						
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
										<td ></td>
									</tr>
								<c:if test='${loggerImport.numeroRecordImportati > 0 or loggerImport.numeroRecordNonImportati > 0}'>
									<% // Visualizza le due righe relative alle voci aggiunte dalla ditta solo se ce ne sono nell'excel, visto che questo caso si presenta raramente %>
									<tr>
										<td width="45%">Numero righe inserite:</td>
										<td width="5%" align="right">${loggerImport.numeroRecordImportati}</td> <!-- align="right" -->
										<td ></td>
									</tr>
								</c:if>
			
							<c:if test='${loggerImport.numeroRecordImportati > 0 or loggerImport.numeroRecordNonImportati > 0 or loggerImport.numeroRecordAggiornati > 0 or loggerImport.numeroRecordNonAggiornati > 0}'>
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
	function importa(){
		if(document.importLottiGara.selezioneFile) {
			var nomeCompletoFile = "" + document.importLottiGara.selezioneFile.value;
			if(nomeCompletoFile != ""){
				if(nomeCompletoFile.substr(nomeCompletoFile.lastIndexOf('.')+1).toUpperCase() == "XLS" || nomeCompletoFile.substr(nomeCompletoFile.lastIndexOf('.')+1).toUpperCase() == "XLSX"){
					bloccaRichiesteServer();
					setTimeout("document.importLottiGara.submit()", 150);
				} else {
					alert("Indicare un file Excel");
					document.importLottiGara.selezioneFile.value = "";
				}
			}
			if (document.importLottiGara.selezioneFile.value == "") {
				alert("Deve essere indicato il file Excel da importare.");				
			}
		}
	}

	function annulla(){
		window.close();
	}

	function initPagina(){
		checkAttivaBloccoPaginaPopup();
			document.getElementById("titolomaschera").innerHTML = "Importazione dei lotti della gara ${locCodgar}";
	}

	<c:if test='${RISULTATO eq "OK"}'>
		<% // Ricarica della lista sottostante %>
		var n = window.opener.document.forms[0].pgCorrente.value;
		window.opener.listaVaiAPagina(n);
		
		window.onfocus=fnFocus;
	</c:if>

-->
</script>
	</c:otherwise>
</c:choose>