
<%
  /*
			 * Created on 01-07-2013
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="registroDiSistema" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.registroDiSistema")}'/>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<c:choose>
		<c:when test='${!empty param.idstipula}'>
			<c:set var="idstipula" value="${param.idstipula}" />
		</c:when>
		<c:otherwise>
			<c:set var="idstipula" value="${idstipula}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${!empty idstipula}">
			<c:set var="oggetto" value=" della stipula"/>
		</c:when>
		<c:when test="${param.genere eq '10'}">
			<c:set var="oggetto" value=" dell'elenco operatori"/>
		</c:when>
		<c:when test="${param.genere eq '20'}">
			<c:set var="oggetto" value=" del catalogo elettronico"/>
		</c:when>
		<c:when test="${param.genere eq '11'}">
			<c:set var="oggetto" value=" dell'avviso"/>
		</c:when>
		<c:otherwise>
			<c:set var="oggetto" value=" della gara"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${!empty idstipula}">
			<c:set var="chiaveTemp" value="${idstipula }"/>
		</c:when>
		<c:otherwise>
			<c:set var="chiaveTemp" value="${param.codgar }"/>
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Export documenti ${oggetto} ${param.codice} su file zip" />
	
	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/InviaRichiestaExportDocumenti.do" method="post" name="formInviaExportDocumenti" >
		 	<input type=hidden name="codgar" value="${param.codgar}" />
		 	<input type=hidden name="codice" value="${param.codice}" />
		 	<input type=hidden name="genere" value="${param.genere}" />
		 	<input type=hidden name="oggetto" value="${oggetto}" />
		 	<input type=hidden name="entita" value="${param.entita}" />
		 	<input type=hidden name="idstipula" value="${idstipula}" />
			<c:set var="temp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.UltimoExportDocumentiGaraFunction", pageContext, chiaveTemp)}'/>
			
			<table class="dettaglio-notab">
				<tr>
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione inoltra la richiesta al sistema per la 
						generazione di un file zip contenente tutti i documenti${oggetto}.
						Nello zip vengono inclusi tutti i file allegati alla documentazione${oggetto} 
						e ai documenti associati, i file allegati alle comunicazioni inviate dall'ente e 
						quelli allegati alla documentazione presentata dalle ditte. 
						Viene inoltre incluso un file di indice in formato csv che riporta per ogni file i dettagli di riferimento${oggetto}.
						<br><br>
						<c:if test="${!empty ultimoExport}">
							E' stata già inoltrata una richiesta di export documenti ${oggetto} da <b>${ultimoExport}</b>
						<br><br>						
						</c:if>

						<c:if test="${registroDiSistema ne '0'}">
							<div>
								<c:set var="isGaraConclusa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckIsGaraConclusaFunction", pageContext, param.codgar)}' />
								<c:set var="esportaLogAttivo" value='${!((param.genere eq 1 || param.genere eq 2 || param.genere eq 3) && isGaraConclusa eq "false")}'/>
								<b>Esportare l'estratto del Registro di sistema (log) per la procedura?</b>&nbsp;&nbsp;
								<select id="esportaLog" name="esportaLog" title="Esporta Log">
									<c:if test='${esportaLogAttivo}'>
										<option value="1" selected="selected">Sì</option>
									</c:if>
									<option value="0">No</option>
								</select>
								<c:choose>
									<c:when test='${esportaLogAttivo}'>
									<br>
									L'estratto del Registro di sistema conterrà le informazioni di tracciabilità (log) raccolte per la procedura avvenute negli ultimi 24 mesi (salvo diversi accordi con il gestore del sistema).
									<c:choose>
									  <c:when test='${(param.genere eq "1") or (param.genere eq "2") or (param.genere eq "3")}'>
										Per ragioni di riservatezza i dati sono esportabili solo al momento dell'aggiudicazione e
									  </c:when>
									  <c:otherwise>
										I dati
									  </c:otherwise>
									</c:choose>
									riguardano i soli eventi riferiti alla procedura; diversamente è possibile richiederne un'estrazione al "Gestore del sistema".
								  </c:when>
								  <c:otherwise>
									  <br>
									Per ragioni di riservatezza la funzionalità sarà disponibile solo dopo l'aggiudicazione o a procedura annullata o deserta.
								  </c:otherwise>
								</c:choose>
								<br>
								<br>
							</div>
						</c:if>

						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ExportDocumenti.METAINFO")}'>
							<div>
							   <b>Esportare i metadati XML utili all'esportazione del Fascicolo?</b>&nbsp;&nbsp;
							   <select id="includiXmlMetadati" name="includiXmlMetadati" title="Includi metadati XML" >
									<option value="1" title="Si" >Si</option>
									<option value="0" title="No" selected="selected">No</option>
							   </select>
							</div>
							(rif. allegato 5 delle Linee Guida AgID - esclusi i metadati dipendenti dal piano di classificazione e piano di organizzazione)
							<br><br>
						</c:if>
						<c:if test='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.MostraFiltroEMLFunction", pageContext,param.codice) eq "true"}'>
							<div>
								<b>Esportare le ricevute di consegna delle PEC?</b>&nbsp;&nbsp;
								<select id="esportaEml" name="esportaEml" title="Esporta EML?" >
									<option value="1" title="Si">Si</option>
									<option value="2" title="No" selected="selected">No</option>
								</select>
							</div>
							<br><br>
						</c:if>
						Confermi l'invio della richiesta?
						<br>
						<br>
					</td>
				</tr>
				<tr>			
					<td colspan="2" class="comandi-dettaglio">					
						<INPUT type="button" class="bottone-azione" value="Invia richiesta" title="Invia richiesta"	onclick="javascript:inviarichiesta();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		function inviarichiesta() {
			document.formInviaExportDocumenti.submit();
			bloccaRichiesteServer();
		}

		$(document).ready(function(){
			<c:if test="${not esportaLogAttivo}">
				$('#esportaLog').on('mousedown', function(e) {
					e.preventDefault();
					this.blur();
					window.focus();
				}).css('background-color','#EFEFEF');
			</c:if>
		})
	
	</gene:javaScript>
</gene:template></div>

