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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${RISULTATO != null}'>
			<c:set var="ngara" value='${NGARA}' />
			<c:set var="modoRichiamo" value='${MODORICHIAMO}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${fn:substringAfter(param.ngara,':')}" />
			<c:set var="modoRichiamo" value='${param.modoRichiamo}' />
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
		<c:when test='${step eq 50}'>
			<c:set var="tipoCriteri" value='tecnici' />
		</c:when>
		<c:otherwise>
			<c:set var="tipoCriteri" value='economici' />
		</c:otherwise>
	</c:choose>
	
	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, ngara, "SC", "20")}
	
	<gene:setString name="titoloMaschera" value="Esportazione dei criteri di valutazione ${tipoCriteri} della gara ${ngara}" />

	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${ngara}" />
		
		<c:choose>
			<c:when test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
				<c:set var="modo" value="APRI" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>

	
		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" 
			gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupExportOEPV">
		
			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false" />
			<gene:campoScheda campo="NOT_GAR" visibile="false" />
			
			<c:choose>
				<c:when test='${RISULTATO != null}'>
					<c:if test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
						<gene:campoScheda>
							<td colSpan="2">Esportazione completata.
								<br>&nbsp;
								<br>&nbsp;
						    <br><b>ATTENZIONE: salvare il file prodotto prima di procedere alla sua modifica.</b>
							  <br>&nbsp;
							  <br><b>ATTENZIONE: il file prodotto potr&agrave; essere usato in importazione solo dalla stessa gara da cui &egrave; stato esportato.</b>
								<br>&nbsp;
								<br>&nbsp;
							</td>
						</gene:campoScheda>
					</c:if>
				</c:when>
				<c:otherwise>
					<gene:campoScheda>
						<td colSpan="2">
							Mediante questa funzione è possibile esportare in formato excel la lista delle ditte ammesse
							alla gara con il dettaglio dei criteri di valutazione ${tipoCriteri } ai fini 
							dell'inserimento dei punteggi.
						  <br>&nbsp;
						  <br>&nbsp;
						</td>
					</gene:campoScheda>
					
					<c:if test='${step ne 50}'>
						<gene:campoScheda>
							<td class="etichetta-dato">
									Scegliere il dato da gestire nel file excel
								</td>
								<td class="valore-dato">
									<input type="radio" value="IMPOFF" name="campo" id="importo" checked="checked"  />
										 Importo offerto
									<br>
									<input type="radio" value="RIBOEPV" name="campo" id="ribasso" />
										 Ribasso offerto
									
								</td>
						</gene:campoScheda>
					</c:if>
					
					<gene:campoScheda>
						<c:choose>
							<c:when test='${documentiAssociatiDB ne "1"}'>
								<td class="etichetta-dato">
									Archiviare il file prodotto nei documenti associati della gara ?
								</td>
								<td class="valore-dato">
									<select name="archiviaXLSDocAss" id="archiviaXLSDocAss" >
										<option value="1" <c:if test='${archiviaXLSDocAss eq "1"}'>selected="selected"</c:if> >Si</option>
										<option value="2" <c:if test='${archiviaXLSDocAss ne "1"}'>selected="selected"</c:if> >No</option>
									</select>
								</td>
							</c:when>
							<c:otherwise>
								<input type="hidden" name="archiviaXLSDocAss" id="archiviaXLSDocAss" value="2" />
							</c:otherwise>
						</c:choose>
					</gene:campoScheda>
					<gene:campoScheda>
						<td colSpan="2">
						  <br>&nbsp;
						  <br><b>ATTENZIONE: salvare il file prodotto prima di procedere alla sua modifica</b>
						  <br>&nbsp;
						  <br><b>ATTENZIONE: il file prodotto potr&agrave; essere usato in importazione solo dalla stessa gara da cui &egrave; stato esportato.</b>
						  <br>&nbsp;
						  <br>&nbsp;
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>

			<gene:campoScheda campo="MODORICHIAMO" title="Modo richiamo"
				modificabile="false" value='${modoRichiamo}'
				definizione="T100" campoFittizio="true" visibile="false" />
			
			<c:choose>
				<c:when test='${RISULTATO != null}'>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:when>
				
				<c:otherwise>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Esporta" title="Esporta" onclick="javascript:esporta()">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>
			<input type="hidden" name="step" value="${step}">
		</gene:formScheda>
		<form action="${contextPath}/DownloadTempFile.do" method="post" name="downloadExportForm" >
			<input type="hidden" name="nomeTempFile" value="${nomeFileExcel}" />
			
		</form>
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-exportOEPV.jsp";
	
		<c:if test='${modoRichiamo eq "ESPORTA" && RISULTATO eq "OPERAZIONEESEGUITA"}'>
			window.setTimeout("document.downloadExportForm.submit();", 250);
		</c:if>

		showObj("jsPopUpGARE_CODGAR1", false);
		showObj("jsPopUpGARE_NGARA", false);
		showObj("jsPopUpGARE_NOT_GAR", false);
		
		function annulla(){
			window.close();
		}
		
		function esporta(){
			schedaConferma();
		}
	
	</gene:javaScript>
</gene:template>

</div>