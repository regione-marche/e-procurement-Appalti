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
	<gene:setString name="titoloMaschera" value="Importa dati da SAP"/>
	
	<c:set var="esisteTorn" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteTabellaFunction", pageContext, "TORN")}'/>
	<c:if test="${esisteTorn eq '0' }">
		<c:set var="radioNascosti" value='style="display:none"'/>
	</c:if>

	<c:set var="where" value="idanticor=${chiave } and (daannoprec='2' or daannoprec='3') and idcontratto is not null group by upper(idcontratto) having(count(*)>1)"/>
	<c:set var="numContrattiDuplicati" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(*)","ANTICORLOTTI", where)}'/>

	<gene:redefineInsert name="corpo">
		<form name="importaDati" id="importaDati" action="${pageContext.request.contextPath}/pg/ImportAdempimenti190SAP.do" enctype="multipart/form-data" method="post" 
					<c:if test="${processato}">style="display: none"</c:if>>
			<input type="hidden" name="chiave" id="chiave" value="${chiave}" />
			<input type="hidden" name="anno" id="anno" value="${anno}" />
			<br>
			<c:choose>
				<c:when test="${empty numContrattiDuplicati or numContrattiDuplicati eq '0'}">
					<div>
						Scegliere il file da importare (.xls/.xlsx/.ods/.ots):<br><br>
						<input type="file" style="width: 400px;" id="selezioneFile" name="selezioneFile" class="file"/>
					</div>
				</c:when>
				<c:otherwise>
					Nell'adempimento sono presenti più lotti nell'anno di riferimento con uguale codice contratto.
					<br><br>Non è possibile procedere.
				</c:otherwise>
			</c:choose>
			
			<br>
			<br>
			
		</form>
			<div id="importResult" <c:if test="${!processato}">style="display: none"</c:if>>
				<br>
				<span>Numero di righe totali analizzate nel file:</span>&nbsp;<c:out value="${numRigheTotali}" /><br><br>
				<span>Numero di righe processate con successo:</span>&nbsp;<c:out value="${numRigheSuccesso}" /><br><br>
				<div id="errori" style="display: <c:choose><c:when test="${!empty errori && fn:length(errori) > 0 }">block</c:when><c:otherwise>none</c:otherwise></c:choose>">
					<span><b>Righe non importate a seguito di errore:&nbsp;<c:out value="${fn:length(errori)}"/></b></span><br><br>
					<div style="clear: both"></div>
					<div class="left" style="width: 85%; margin-right: 20px;">
						<textarea id="erroriRiga" style="width: 100%; height: 100px;"></textarea>
					</div>
				</div>
			</div>
	</gene:redefineInsert>
	<gene:redefineInsert name="buttons">
		<c:choose>
			<c:when test="${empty numContrattiDuplicati or numContrattiDuplicati eq '0'}">
				<input type="button" class="bottone-azione" id="confirm" value="Conferma" title="Conferma"	onclick="conferma();" <c:if test="${processato}">style="display: none"</c:if>/>
				<c:choose>
				<c:when test="${!processato }">
					<c:set var="etichetta" value="Annulla"/>
				</c:when>
				<c:otherwise>
					<c:set var="etichetta" value="Chiudi"/>
				</c:otherwise>
				</c:choose>
				<input type="button" class="bottone-azione"  id="cancel" value="${etichetta }" title="${etichetta }" onclick="chiudi();"/>&nbsp;&nbsp;
			</c:when>
			<c:otherwise>
				<input type="button" class="bottone-azione"  id="cancel" value="Annulla" title="Annulla" onclick="javascript:window.close();"/>&nbsp;&nbsp;
			</c:otherwise>
		</c:choose>
		
	</gene:redefineInsert>
		
	<gene:javaScript>
		
		var mappaErrori;

		
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
			<c:if test="${processato}">
				window.opener.historyReload();
			</c:if>
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
			
			if(mappaErrori.length > 0){
				var erroriRiga = undefined;
				var stringaErrori = "";
				for(var i = 0, l = mappaErrori.length; i < l; i++) {
					erroriRiga = mappaErrori[i].errori;
					if (typeof erroriRiga != "undefined") {
						if(i>0)
							stringaErrori+="\n";
						stringaErrori+="Errori riga " + mappaErrori[i].indiceRiga + ":" + "\n" ;
						for(var j = 0, l1 = erroriRiga.length; j < l1; j++) {
							stringaErrori = stringaErrori + "* " + erroriRiga[j] + "\n";
						}
					}
				}
				$("#erroriRiga").text(stringaErrori);
			}
		
	</gene:javaScript>
		
</gene:template>