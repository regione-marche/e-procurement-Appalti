<%/*
   * Created on 21-03-2022
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA POPUP PER IMPORT DITTE EXCEL E RISULTATO IMPORT

%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty param.genereGara}'>
		<c:set var="locGenereGara" value="${param.genereGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="locGenereGara" value="${genereGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="locNgara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="locNgara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="esistonoDitteInGara" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "NGARA5", locNgara,"")}' />

<c:set var="isGaraAggiudicata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsGaraAggiudicataFunction", pageContext, locNgara)}' />

<c:choose>
<c:when test='${isGaraAggiudicata}'>
<table style="width:100%;">
	<tr>
		<td>
			<div class="contenitore-popup">

						<table class="dettaglio-notab">
							<tr>
						    	<td colspan="2">
										<br>
										Mediante questa funzione è possibile <b>importare</b> la lista delle ditte in gara.
										<br>
										<br>&nbsp;
										<br><b>Non è possibile procedere con l'importazione perchè la gara è aggiudicata.</b>
									  	<br>&nbsp;
									  	<br>&nbsp;
						      	</td>
					  	  	</tr>
							
					  	<tr>
						    <td class="comandi-dettaglio" colspan="2">
									<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					  	  </td>
						  </tr>
						</table>					
			</div>
		</td>
	</tr>
</table>
<script type="text/javascript">
<!--
	function initPagina(){
		checkAttivaBloccoPaginaPopup();
			document.getElementById("titolomaschera").innerHTML = "Importazione delle ditte della gara ${locNgara}";
	}


	function annulla(){
		window.close();
	}
-->
</script>
</c:when>
<c:when test='${esistonoDitteInGara && empty param.doubleCheck && empty RISULTATO}'>
<table style="width:100%;">
	<tr>
		<td>
			<div class="contenitore-popup">

						<table class="dettaglio-notab">
							<tr>
						    	<td colspan="2">
										<br>
										Mediante questa funzione è possibile <b>importare</b> la lista delle ditte in gara.
										<br>
										<br><b>ATTENZIONE: confermando l'operazione, le ditte attualmente inserite in gara verranno eliminate.</b>
									  	<br>
									  	<br>Confermi l'operazione?<br>
									  	<br><br>
										<a href="javascript:scaricaModello();" title="Scarica modello excel" style="color:#002E82;">
											<img width="16" height="16" title="Scarica modello excel per importazione" alt="Scarica modello" src="${pageContext.request.contextPath}/img/download_modello.png"/> Scarica modello excel per importazione
										</a>
										<br><br>
						      	</td>
					  	  	</tr>
							
					  	<tr>
						    <td class="comandi-dettaglio" colspan="2">
							    	<input type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;
									<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					  	  </td>
						  </tr>
						</table>
						
			</div>
		</td>
	</tr>
</table>
<script type="text/javascript">
<!--
	function initPagina(){
		checkAttivaBloccoPaginaPopup();
			document.getElementById("titolomaschera").innerHTML = "Importazione delle ditte della gara ${locNgara}";
	}

	function conferma() {
		var href;
		href = "ngara="+"${locNgara}";
		href += "&doubleCheck=OK";
		href += "&genereGara="+"${locGenereGara}";
		document.location.href = "${pageContext.request.contextPath}/pg/InitImportDitteGaraExcel.do?" + href;	
	}


	function annulla(){
		window.close();
	}
	
	function scaricaModello(){
		var href="${contextPath}/pg/ExportDitteGara.do?"+csrfToken;
		document.location.href=href;
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
					<form method="post" enctype="multipart/form-data" action="${contextPath}/pg/EseguiImportDitteGaraExcel.do" name="importDitteGaraExcel" >
						<table class="dettaglio-notab">

						    <tr>
							    	<td colspan="2">
							    		<p>
							    			Mediante questa funzione è possibile <b>importare</b> la lista delle ditte in gara.
											  <br><br>Selezionare il file da importare.
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
										  <br><b>ATTENZIONE: durante l'operazione di import verranno eliminate le ditte esistenti nella gara.</b>
									  	<br>&nbsp;										
										<br><br>
										<a href="javascript:scaricaModello();" title="Scarica modello excel" style="color:#002E82;">
											<img width="16" height="16" title="Scarica modello excel per importazione" alt="Scarica modello excel" src="${pageContext.request.contextPath}/img/download_modello.png"/>  Scarica modello excel per importazione
										</a>
										<br><br>
						      	</td>
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
									<textarea cols="95" rows="12" readonly="readonly" style="max-width:100%"><c:forEach items="${loggerImport.listaMsgVerificaFoglio}" var="msgErrore" ><c:out value="${msgErrore}" escapeXml="false"/><c:out value="&#13;&#10;" escapeXml="false"/></c:forEach></textarea>
								</td>
							</tr>
						</c:if>
						</c:if>
							
					  	<tr>
						    <td class="comandi-dettaglio" colspan="2">
							    	<input type="button" class="bottone-azione" value="Importa" title="Importa" onclick="javascript:importa();">&nbsp;
									<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
									<input type="hidden" name="ngara" id="ngara" value="${locNgara}"/>
									<input type="hidden" name="genereGara" id="genereGara" value="${locGenereGara}"/>
					  	  </td>
						  </tr>
						</table>
						
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
										<td width="45%">Numero righe elaborate correttamente:</td>
										<td width="5%" align="right">${loggerImport.numeroRecordAggiornati}</td> <!-- align="right" -->
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
		if(document.importDitteGaraExcel.selezioneFile) {
			var nomeCompletoFile = "" + document.importDitteGaraExcel.selezioneFile.value;
			if(nomeCompletoFile != ""){
				if(nomeCompletoFile.substr(nomeCompletoFile.lastIndexOf('.')+1).toUpperCase() == "XLS" || nomeCompletoFile.substr(nomeCompletoFile.lastIndexOf('.')+1).toUpperCase() == "XLSX") {
					bloccaRichiesteServer();
					setTimeout("document.importDitteGaraExcel.submit()", 150);
				} else {
					alert("Indicare un file Excel");
					document.importDitteGaraExcel.selezioneFile.value = "";
				}
			}
			if (document.importDitteGaraExcel.selezioneFile.value == "") {
				alert("Deve essere indicato il file Excel da importare.");				
			}
		}
	}

	function annulla(){
		window.close();
	}

	function initPagina(){
		checkAttivaBloccoPaginaPopup();
			document.getElementById("titolomaschera").innerHTML = "Importazione delle ditte della gara ${locNgara}";
	}

	function scaricaModello(){
		var href="${contextPath}/pg/ExportDitteGara.do?"+csrfToken;
		document.location.href=href;
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