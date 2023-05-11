<%
	/*
	 * Created on: 30-08-2013
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
	/* Popup per il download del modello excel degli adempimenti di legge 190 */
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
		<c:when test='${not empty param.anno}'>
			<c:set var="anno" value="${param.anno}" />
		</c:when>
		<c:otherwise>
			<c:set var="anno" value="${anno}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.chiave}'>
			<c:set var="chiave" value="${param.chiave}" />
		</c:when>
		<c:otherwise>
			<c:set var="chiave" value="${chiave}" />
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Scarica modello excel"/>

	<gene:redefineInsert name="corpo">
		<br>
		<p>
			Selezionare la tipologia di documento excel che si desidera esportare:			
		</p>
		<br>
		<input id="r1" type="radio" name="rbexport" value="standard" checked /><span>Modello standard con alcuni dati di esempio</span><br>
		<input id="r2" type="radio" name="rbexport" value="annorif" /><span>Modello precompilato con i dati relativi all'anno di riferimento (${anno})</span><br>
		<br><br>
		<div>
			<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/Legge190-2012_ImportExportExcel.pdf');" title="Consulta manuale" style="color:#002E82;">
				<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
			</a>
			<br><br>
		</div>
		
	</gene:redefineInsert>
	<gene:redefineInsert name="buttons">
		<input type="button" class="bottone-azione" value="Conferma" id="confermaDownload" title="Conferma"	onclick="scaricaModello();"/>
		<input type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="chiudi();"/>&nbsp;&nbsp;
	</gene:redefineInsert>

	<gene:javaScript>

		function chiudi(){
			window.close();
		}

		function scaricaModello(){
			var chiave= '${chiave }';
			var anno = '${anno}';
			var tipoExport = $('input[name=rbexport]:checked').val();
			$("#confermaDownload").attr("disabled", true).css( "opacity", "0.5" );
			var href="${contextPath}/pg/ExportAdempimenti190.do?"+csrfToken+"&chiave="+chiave+"&tipoExport="+tipoExport+"&anno="+anno+"&numeroPopUp=1";
			document.location.href=href;
		}

	</gene:javaScript>
</gene:template>
