<%
	/*
	 * Created on 15-lug-2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${!empty RISULTATO}'>
			<c:set var="ngara" value='${NGARA}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${fn:substringAfter(param.ngara,':')}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.step}'>
			<c:set var="step" value='${param.step}' />
		</c:when>
		<c:otherwise>
			<c:set var="step" value="${step}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${step == 50}'>
			<c:set var="tipoPunteggio" value="tecnica" />
		</c:when>
		<c:otherwise>
			<c:set var="tipoPunteggio" value="economica" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.isProceduraTelematica}'>
			<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.bustalotti}'>
			<c:set var="bustalotti" value="${param.bustalotti}" />
		</c:when>
		<c:otherwise>
			<c:set var="bustalotti" value="${bustalotti}" />
		</c:otherwise>
	</c:choose>
	
	
	<c:if test="${isProceduraTelematica eq 'true' }">
		<c:choose>
			<c:when test='${bustalotti eq "2"}'>
				<c:set var="chiaveControlloBuste" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara)}' />
			</c:when>
			<c:otherwise>
				<c:set var="chiaveControlloBuste" value="${ngara}" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${tipoPunteggio eq 'tecnica' }">
				<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11B" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, chiaveControlloBuste, "FS11B" )}' />
			</c:when>
			<c:otherwise>
				<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11C" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, chiaveControlloBuste, "FS11C" )}' />
			</c:otherwise>
		</c:choose>
	</c:if>
	
	
	
	
	
	<gene:setString name="titoloMaschera" value="Importazione punteggi dettaglio valutazione busta ${tipoPunteggio } per la gara ${ngara}" />

	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${ngara}" />
		<c:set var="not_gar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOggettoGaraFunction", pageContext, chiave)}' />

		<form method="post" enctype="multipart/form-data" name="importazioneExcel" action="${pageContext.request.contextPath}/pg/EseguiImportOEPV.do" >
			<table class="dettaglio-notab">
				<c:choose>
					<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11B == 'true'}">
						<br>
						<c:set var="blocco" value="true"/>
						Per procedere con l'importazione dei punteggi, deve essere prima acquisita la busta tecnica per ogni ditta in gara.<br>
						<br>&nbsp;
						<br>&nbsp;
					</c:when>
					<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11C == 'true'}">
						<br>
						<c:set var="blocco" value="true"/>
						Per procedere con l'importazione dei punteggi, deve essere prima acquisita la busta economica per ogni ditta in gara.<br>
						<br>&nbsp;
						<br>&nbsp;
					</c:when>
					<c:when test='${empty RISULTATO || RISULTATO eq "ERRORI"}'>
						<tr>
							<td colspan="2">
								Mediante questa funzione vengono importati i punteggi 
								del dettaglio valutazione per la busta ${tipoPunteggio } assegnati alle ditte in gara.
							   <br>&nbsp;
							   <br>&nbsp;
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:if test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
							<tr>
								<td colspan="2">
									Importazione completata.
								   <br>&nbsp;
								   <br>&nbsp;
								</td>
							</tr>
						</c:if>
					</c:otherwise>
				</c:choose>
				
				<c:if test="${empty RISULTATO and blocco ne 'true' }">
					<tr>
						<td class="etichetta-dato">File Excel da importare (*)</td>
						<td class="valore-dato"><input type="file" name="selezioneFile" maxlength="255" size="55" onkeydown="return bloccaCaratteriDaTastiera(event);"></td>
					</tr>
					
					<c:choose>
						<c:when test='${documentiAssociatiDB ne "1"}'>
							<tr>
								<td class="etichetta-dato">
									Archiviare il file prodotto nei documenti associati della gara ?
								</td>
								<td class="valore-dato">
									<select name="archiviaXLSDocAss" id="archiviaXLSDocAss" >
										<option value="1" <c:if test='${archiviaXLSDocAss eq "1"}'>selected="selected"</c:if> >Si</option>
										<option value="2" <c:if test='${archiviaXLSDocAss ne "1"}'>selected="selected"</c:if> >No</option>
									</select>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<input type="hidden" name="archiviaXLSDocAss" id="archiviaXLSDocAss" value="2" />
						</c:otherwise>
					</c:choose>
					
					<tr>
		    		<td colspan="2">
						  <br>&nbsp;
						  <br><b>ATTENZIONE: durante l'operazione di import i punteggi gi&agrave; assegnati alle ditte verranno cancellati.</b>
					  	<br>&nbsp;
					  	<br>&nbsp;
		      	</td>
	  	  	</tr>
				</c:if>
				
				<c:choose>
					<c:when test='${!empty RISULTATO}'>
						<tr>
							<td class="comandi-dettaglio" colSpan="2">
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</tr>					
					</c:when>
					<c:otherwise>
						<tr>
							<td class="comandi-dettaglio" colSpan="2">
								<c:if test="${blocco ne 'true' }">
									<INPUT type="button" class="bottone-azione" value="Importa" title="Importa" onclick="javascript:importa()">
								</c:if>
								<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;&nbsp;
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
			<input type="hidden" name="ngara" value="${ngara}">	
			<input type="hidden" name="step" value="${step}">
		</form>
	
	</gene:redefineInsert>
	<gene:redefineInsert name="gestioneHistory" />
  	<gene:redefineInsert name="addHistory" />
	<gene:javaScript> 
		
		<c:if test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
			window.opener.listaVaiAPagina(window.opener.document.forms[0].pgCorrente.value);
		</c:if>
		
		function annulla(){
			window.close();
		}

		function importa(){
			if (document.importazioneExcel.selezioneFile) {
				if (document.importazioneExcel.selezioneFile.value != "") {
					bloccaRichiesteServer();
					document.importazioneExcel.submit();
				}
				if (document.importazioneExcel.selezioneFile.value == "") {
					alert("Deve essere indicato il file Excel da importare.");				
				}
			}
		}
	
	</gene:javaScript>
</gene:template>

</div>