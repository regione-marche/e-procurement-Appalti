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
			<c:set var="codgar" value='${CODGAR}' />
			<c:set var="lottoSelezionato" value='${LOTTOSELEZIONATO}' />
			<c:set var="listaLottiSelezionati" value="${LISTALOTTISELEZIONATI}" />
			<c:set var="archiviaXLSDocAss" value="${ARCHIVIAXLSDOCASS}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${fn:substringAfter(param.codgar,':')}" />
			<c:set var="lottoSelezionato" value="" />
			<c:set var="listaLottiSelezionati" value="" />
			<c:set var="archiviaXLSDocAss" value="" />
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
			<c:set var="tipoPunteggio" value='tecnici' />
		</c:when>
		<c:otherwise>
			<c:set var="tipoPunteggio" value="economici" />
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
	
	<c:if test="${isProceduraTelematica eq 'true' }">
		<c:choose>
			<c:when test="${tipoPunteggio eq 'tecnici' }">
				<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11B" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, codgar, "FS11B" )}' />
			</c:when>
			<c:otherwise>
				<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11C" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, codgar, "FS11C" )}' />
			</c:otherwise>
		</c:choose>
	</c:if>
	
	
	
	<gene:setString name="titoloMaschera" value="Importazione dei punteggi ${tipoPunteggio } assegnati alle ditte di uno o pi&ugrave; lotti della gara ${codgar}" />

	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${codgar}" />

		<form method="post" enctype="multipart/form-data" name="importazioneExcel" action="${pageContext.request.contextPath}/pg/EseguiImportOEPVOffertaUnica.do" >
			<table class="dettaglio-notab-width-50">
				<c:choose>
					<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11B == 'true'}">
						<br>
						<c:set var="blocco" value="true"/>
						Per procedere con l'importazione dei punteggi, deve essere prima acquisita la busta tecnica per ogni ditta in gara.<br>
						<br>&nbsp;
						<br>&nbsp;
						<tr>
							<td class="comandi-dettaglio" colSpan="2">
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</tr>
					</c:when>
					<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11C == 'true'}">
						<br>
						<c:set var="blocco" value="true"/>
						Per procedere con l'importazione dei punteggi, deve essere prima acquisita la busta economica per ogni ditta in gara.<br>
						<br>&nbsp;
						<br>&nbsp;
						<tr>
							<td class="comandi-dettaglio" colSpan="2">
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:set var="blocco" value="false"/>
						<tr>
							<td colspan="2">
								Mediante questa funzione è possibile <b>importare</b> da file Excel
								i punteggi ${tipoPunteggio } assegnati alle ditte ammesse di uno o pi&ugrave; lotti.
							   <br>&nbsp;<br>
								<c:if test="${!empty RISULTATO and RISULTATO eq 'OPERAZIONEESEGUITA'}">
							  		Il lotto <b>${lottoSelezionato}</b> è stato aggiornato correttamente.<br>
							  		E', ora, possibile aggiornare i punteggi ${tipoPunteggio } di un altro lotto.
							  		<br>&nbsp;<br>
							  	</c:if>
								Utilizzare l'icona <img align="middle" width="16" height="16" src="${pageContext.request.contextPath}/img/import.gif"/> per importare i punteggi ${tipoPunteggio } nel lotto di interesse:
								<br>
								<br>
							</td>
						</tr>
						
						<tr>
							<td class="etichetta-dato">File Excel da importare(*)</td>
							<td class="valore-dato"><input type="file" name="selezioneFile" maxlength="255" size="65" onkeydown="return bloccaCaratteriDaTastiera(event);"></td>
						</tr>
						<c:choose>
							<c:when test='${documentiAssociatiDB ne "1"}'>
								<tr>
									<td class="etichetta-dato">
										Archiviare il file nei documenti associati al lotto?
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
					</c:otherwise>
				</c:choose>
			</table>
			<input type="hidden" name="codgar" value="${codgar}">
			<input type="hidden" name="lottoSelezionato" value="">
			<input type="hidden" name="listaLottiSelezionati" value="${listaLottiSelezionati}">
			<input type="hidden" name="archiviaXLS" value="${archiviaXLS}">
			<input type="hidden" name="step" value="${step}">
		</form>
			
			
		<c:if test="${blocco eq 'false' }">
			<table class="dettaglio-notab">	
				<tr>
					<td colspan="2">
						<c:set var="filtroLotti" value="GARE.CODGAR1 = '${codgar}' AND MODLICG = 6 AND (GARE.GENERE is null)" />
						<gene:formLista entita="GARE" sortColumn="3" where="${filtroLotti}">
								<gene:campoLista title="&nbsp;" width="20">
									<a href="javascript:importa('${datiRiga.GARE_NGARA}');" title="Importa i punteggi nel lotto" >
										<img align="middle"  width="16" height="16" title="Importa i punteggi ${tipoPunteggio } nel lotto" alt="Importa i punteggi ${tipoPunteggio } nel lotto" src="${pageContext.request.contextPath}/img/import.gif"/>
									</a>						
								</gene:campoLista>
								<gene:campoLista title="Importato?" width="80">
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
	
				<tr>
		    		<td colspan="2">
					  <br>&nbsp;
					  <br><b>ATTENZIONE: durante l'operazione di importazione i punteggi ${tipoPunteggio } gi&agrave; assegnati alle ditte verranno cancellati.</b>
				  	  <br>&nbsp;
					  <br>&nbsp;
		      		</td>
	  	  		</tr>
				
				<tr>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>					
	
			</table>
		</c:if>
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


		function importa(lottoSelezionato){
			if (document.importazioneExcel.selezioneFile) {
				if (document.importazioneExcel.selezioneFile.value != "") {
					bloccaRichiesteServer();
					document.importazioneExcel.lottoSelezionato.value=lottoSelezionato;
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