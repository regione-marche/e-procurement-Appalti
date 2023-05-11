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
			<c:set var="codgar" value='${CODGAR}' />
			<c:set var="lottoSelezionato" value='${LOTTOSELEZIONATO}' />
			<c:set var="modoRichiamo" value='${MODORICHIAMO}' />
			<c:set var="listaLottiSelezionati" value="${LISTALOTTISELEZIONATI}" />
			<c:set var="archiviaXLS" value="${ARCHIVIAXLS}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${fn:substringAfter(param.codgar,':')}" />
			<c:set var="lottoSelezionato" value="" />
			<c:set var="modoRichiamo" value='${param.modoRichiamo}' />
			<c:set var="listaLottiSelezionati" value="" />
			<c:set var="archiviaXLS" value="" />
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
	

	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codgar, "SC", "21")}
	
	<gene:setString name="titoloMaschera" value="Esportazione dei criteri di valutazione ${tipoCriteri} di uno o pi&ugrave; lotti della gara ${codgar}" />

	<gene:redefineInsert name="corpo">
		
		<c:set var="modo" value="MODIFICA" scope="request" />
		
		<gene:formScheda entita="TORN" where="TORN.CODGAR = '${codgar}'" 
			gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupExportOEPVOffertaUnica">
			<gene:campoScheda campo="CODGAR" visibile="false"/>
			<gene:campoScheda campo="LOTTOSELEZIONATO" title="Lotto selezionato"
				modificabile="false" definizione="T20" campoFittizio="true" visibile="false" />
			<gene:campoScheda campo="LISTALOTTISELEZIONATI" title="Lista lotti selezionati"
				modificabile="false" definizione="T2000" campoFittizio="true" visibile="false" value="${listaLottiSelezionati}"/>
			<gene:campoScheda campo="MODORICHIAMO" title="Modo richiamo"
				modificabile="false" value='${modoRichiamo}'
				definizione="T100" campoFittizio="true" visibile="false" />
			<gene:campoScheda campo="ARCHIVIAXLS" title="Archivia XLS"
				modificabile="false" value="${archiviaXLS}"
				definizione="T1" campoFittizio="true" visibile="false" />
			<input type="hidden" name="step" value="${step}">	
			<input type="hidden" id="campo" name="campo" value="IMPOFF">
		</gene:formScheda>
		
		<table class="dettaglio-notab">
			
			<tr>
				<td colspan="2">
					Mediante questa funzione è possibile <b>esportare</b> in formato excel, per ogni lotto della gara,
					        la lista delle ditte ammesse alla gara con il dettaglio dei criteri di valutazione 
					        ${tipoCriteri } ai fini dell'inserimento dei punteggi.
					<br>&nbsp;<br>
					<c:if test="${RISULTATO != null and RISULTATO eq 'OPERAZIONEESEGUITA'}">
				  		Il lotto <b>${lottoSelezionato}</b> è stato esportato correttamente.<br>
				  		E', ora, possibile esportare i dati di un altro lotto. 
				  		<br>&nbsp;<br>
				  	</c:if>
			  		<c:if test='${step ne 50}'>
			  			Scegliere il dato da gestire nel file excel: <br>
			  			<input type="radio" value="IMPOFF" name="campo" id="importo" checked="checked"  onclick="javascript:impostaCampo('IMPOFF');"/>Importo offerto
			  			<br>
						<input type="radio" value="RIBOEPV" name="campo" id="ribasso" onclick="javascript:impostaCampo('RIBOEPV');"/>Ribasso offerto
			  			<br><br>
			  		</c:if>
			  		Utilizzare l'icona <img align="middle" width="16" height="16" src="${pageContext.request.contextPath}/img/export.gif"/> per esportare il lotto di interesse:	
					
					
					<c:set var="where" value="GARE.CODGAR1 = ? AND MODLICG = 6 AND (GARE.GENERE is null) "/>
					<c:set var="parametri" value="T:${codgar}"/>
					${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ImpostazioneFiltroFunction", pageContext, "GARE", where, parametri)}
									
					<gene:formLista entita="GARE" sortColumn="3" >
						<gene:campoLista title="&nbsp;" width="20">
							<a href="javascript:esporta('${datiRiga.GARE_NGARA}');" title="Esporta il lotto in formato Excel" >
								<img align="middle"  width="16" height="16" title="Esporta il lotto in formato Excel" alt="Esporta il lotto in formato Excel" src="${pageContext.request.contextPath}/img/export.gif"/>
							</a>						
						</gene:campoLista>
						<gene:campoLista title="Esportato?" width="30">
							<c:choose>
								<c:when test="${fn:contains(listaLottiSelezionati,datiRiga.GARE_NGARA)}">
									Si
								</c:when>
								<c:otherwise>
									No
								</c:otherwise>
							</c:choose>
						</gene:campoLista>
						<gene:campoLista campo="CODGAR1" visibile="false" ordinabile="false"/>
						<gene:campoLista campo="NGARA" title="Codice lotto" ordinabile="false" width="110"/>	
						<gene:campoLista campo="CODIGA" title="Lotto" width="65" ordinabile="false"/>
						<gene:campoLista campo="NOT_GAR" ordinabile="false"/>
						<gene:campoLista campo="IMPAPP" width="140" ordinabile="false"/>
						
					</gene:formLista>
				</td>
			</tr>
									
			<c:choose>
				<c:when test='${documentiAssociatiDB ne "1"}'>
					<tr>
						<td class="etichetta-dato">
							Archiviare il file prodotto nei documenti associati al lotto?
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
				  <br><b>ATTENZIONE: salvare il file prodotto prima di procedere alla sua modifica</b>
				  <br>&nbsp;
				  <br><b>ATTENZIONE: il file prodotto potr&agrave; essere usato in importazione solo dalla stessa gara da cui &egrave; stato esportato.</b>
				  <br>&nbsp;
				</td>
			</tr>
			
			<tr>
				<td class="comandi-dettaglio" colspan="2">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
			
		</table>
		
		<form action="${contextPath}/DownloadTempFile.do" method="post" name="downloadExportForm" >
			<input type="hidden" name="nomeTempFile" value="${nomeFileExcel}" />
		</form>
		
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-exportOEPV-offertaUnica.jsp";
		
		var archiviaXLS =  document.getElementById("archiviaXLSDocAss");
		if (document.forms[0].ARCHIVIAXLS.value == "1") {
			archiviaXLS.value = "1";
		} else {
			archiviaXLS.value = "2";
		}
	
		<c:if test='${modoRichiamo eq "ESPORTA" && RISULTATO eq "OPERAZIONEESEGUITA"}'>
			window.setTimeout("document.downloadExportForm.submit();", 250);
		</c:if>
		
		function annulla(){
			window.close();
		}
		
		function esporta(lottoSelezionato) {
			var archiviaXLS = document.getElementById("archiviaXLSDocAss");
			document.forms[0].LOTTOSELEZIONATO.value=lottoSelezionato;
			document.forms[0].ARCHIVIAXLS.value=archiviaXLS.value;
			schedaConferma();
		}
		
		function impostaCampo(campo){
			$("#campo") .val(campo);
		}

	</gene:javaScript>
</gene:template>

</div>