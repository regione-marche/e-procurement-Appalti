<%
	/*
	 * Created on: 13-11-2013
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
	/* Popup per l'importazione dati dal modello excel degli adempimenti di legge 190 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<gene:template file="popup-message-template.jsp">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	
	<c:choose>
		<c:when test='${not empty param.chiave}'>
			<c:set var="chiave" value="${param.chiave}" />
		</c:when>
		<c:otherwise>
			<c:set var="chiave" value="${chiave}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.anno}'>
			<c:set var="anno" value="${param.anno}" />
		</c:when>
		<c:otherwise>
			<c:set var="anno" value="${anno}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty numRigheTotali}'>
			<c:set var="processato" value="true" />
		</c:when>
		<c:otherwise>
			<c:set var="processato" value="false" />
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Importa dati"/>
	
	<c:set var="esisteTorn" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteTabellaFunction", pageContext, "TORN")}'/>
	<c:if test="${esisteTorn eq '0' }">
		<c:set var="radioNascosti" value='style="display:none"'/>
	</c:if>

	<gene:redefineInsert name="corpo">
		<form name="importaDati" id="importaDati" action="${pageContext.request.contextPath}/pg/ImportAdempimenti190.do" enctype="multipart/form-data" method="post" 
					<c:if test="${processato}">style="display: none"</c:if>>
			<input type="hidden" name="chiave" id="chiave" value="${chiave}" />
			<input type="hidden" name="anno" id="anno" value="${anno}" />
			<input type="hidden" name="aggiorna" id="aggiorna" value="false" />
			<br>
			<div>
				Scegliere il file da importare (.xls/.xlsx/.ods/.ots):<br>
				<input type="file" style="width: 400px;" id="selezioneFile" name="selezioneFile" class="file"/>
			</div>
			<br><br>
			<div ${radioNascosti }>
				Per i lotti dell'adempimento derivanti dai dati correnti, intendi:
				<ul style="list-style-type: none;">
					<li >
						<input type="radio" id="radioNonAggiornare" name="radioOpzioneAggiorna" checked="checked" value="0" onclick="aggiornaOpzioni(this);"/>lasciare i dati inalterati (non aggiornare i dati)
					</li>
					<li>
						<input type="radio" id="radioAggiornare" name="radioOpzioneAggiorna" value="1" onclick="aggiornaOpzioni(this);"/>aggiornare nel lotto i seguenti dati:
					</li>
				</ul>
			</div>
			<div id="opzioni" style="display:none">
				<ul style="list-style-type: none;">
					<li>
						<input type="checkbox" name="impAggiudicazione" id="impAggiudicazione" value="true"/>importo aggiudicazione
					</li>
					<li>
						<input type="checkbox" name="dataInizio" id="dataInizio" value="true" />data inizio *
					</li>
					<li>
						<input type="checkbox" name="dataUltimazione" id="dataUltimazione" value="true" />data ultimazione *
					</li>
					<li>
						<input type="checkbox" name="impLiquidato" id="impLiquidato" value="true" />importo liquidato *
					</li>
					<li>
						<input type="checkbox" name="partecipante" id="partecipante" value="true" />partecipanti
					</li>
					<li>
						<br>* dati per cui l'aggiornamento viene fatto anche nei dati correnti
					</li> 
				</ul>
			</div>
			<br>
			<div>
				<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/Legge190-2012_ImportExportExcel.pdf');" title="Consulta manuale" style="color:#002E82;">
					<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
				</a>
				<br><br>
			</div>
		</form>
			<div id="importResult" <c:if test="${!processato}">style="display: none"</c:if>>
				<br>
				<span>Numero di righe totali analizzate nel file:</span>&nbsp;<c:out value="${numRigheTotali}" /><br><br>
				<span>Numero di righe processate con successo:</span>&nbsp;<c:out value="${numRigheSuccesso}" /><br><br>
				<div id="errori" style="display: <c:choose><c:when test="${!empty errori && fn:length(errori) > 0 }">block</c:when><c:otherwise>none</c:otherwise></c:choose>">
					<span><b>Righe non importate a seguito di errore:&nbsp;<c:out value="${fn:length(errori)}"/></b></span><br><br>
					<span>Selezionare un numero di riga per avere dettaglio sugli errori</span><br>
					<span>(disponibile solo per i primi 50 record problematici)</span><br>
					<div style="clear: both"></div>
					<div class="left" style="width: 85%; margin-right: 20px;">
						<textarea id="erroriRiga" style="width: 100%; height: 100px;"></textarea>
					</div>
					<div class="left">
						<select id="righeErrori" onchange="showErrorDetail();">
							<option value="" selected>..</option>
							<c:forEach var="errore" items="${errori}" end="50">
									<option value="${errore.key}">${errore.key}</option>
							</c:forEach>
						</select>
					</div>					
				</div>
			</div>
	</gene:redefineInsert>
	<gene:redefineInsert name="buttons">
		<input type="button" class="bottone-azione" id="confirm" value="Conferma" title="Conferma"	onclick="conferma();" <c:if test="${processato}">style="display: none"</c:if>/>
		<input type="button" class="bottone-azione"  id="cancel" value="Annulla" title="Annulla" onclick="chiudi();"/>&nbsp;&nbsp;
	</gene:redefineInsert>
		
	<gene:javaScript>
		
		var mappaErrori;

		function aggiornaOpzioni(param) {

			var scelta = $(param).val();
			if (scelta != '' && scelta == 1) {
				$("#dataInizio").attr('checked', true);
				$("#dataUltimazione").attr('checked', true);
				$("#impLiquidato").attr('checked', true);
				$("#aggiorna").val("true");
				$("#opzioni").show();
			} else {
				$("#dataInizio").attr('checked', false);
				$("#dataUltimazione").attr('checked', false);
				$("#impLiquidato").attr('checked', false);
				$("#aggiorna").val("false");
				$("#opzioni").hide();
			}
		}

		function conferma() {

			var nomeCompletoFile = $("#selezioneFile").val();
			if(nomeCompletoFile != ""){
				var estensione = nomeCompletoFile.substr(nomeCompletoFile.lastIndexOf('.') + 1).toUpperCase();
				if(estensione == "XLS" || estensione == "XLSX" || estensione == "ODS" || estensione == "OTS") {
					bloccaRichiesteServer();
					$("#importaDati").submit();
				} else {
					alert("E' stato selezionato un file non valido.\nSelezionare un file excel con estensione .xls/.xlsx/.ods/.ots");
				}
			} else {
				alert("Non è stato selezionato alcun file excel");
			}
		}

		function chiudi(){
			window.opener.historyReload();
			window.close();
		}
		
			mappaErrori = [
			<c:if test="${errori != null}">
				<c:forEach items="${errori}" var="errore" varStatus="status" end="50">
				{
					indiceRiga: "${errore.key}",
					errori : [
						<c:forEach items="${errore.value}" var="causa" varStatus="causaStatus">
				    "${causa}"
						<c:if test="${!causaStatus.last}">
            ,
						</c:if>
						</c:forEach>
					]
				}
				<c:if test="${!status.last}">
				,
				</c:if>
				</c:forEach>
			</c:if>
			];
		
		function showErrorDetail() {
			var indiceRiga = $("#righeErrori").find(":selected").text();
			var erroriRiga = undefined;
			if (typeof indiceRiga != "undefined" && indiceRiga != "") {
				for(var i = 0, l = mappaErrori.length; i < l; i++) {
					if (mappaErrori[i].indiceRiga == indiceRiga) {
						erroriRiga = mappaErrori[i].errori;
					}
				}
			}			
			var stringaErrori = "";
			if (typeof erroriRiga != "undefined") {
				for(var i = 0, l = erroriRiga.length; i < l; i++) {
					stringaErrori = stringaErrori + "* " + erroriRiga[i] + "\n";
				}
			}
			$("#erroriRiga").text(stringaErrori);
		}

	</gene:javaScript>
		
</gene:template>