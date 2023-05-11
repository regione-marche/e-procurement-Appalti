
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:set var="nomeApplicativo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", nomeAppicativoCig)}'/>
<c:if test="${empty nomeApplicativo or nomeApplicativo eq '' }">
	<c:set var="nomeApplicativo" value='Vigilanza' />
</c:if>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<c:set var="codgar" value="${param.codgar}" />
	<gene:setString name="titoloMaschera"  value='Invio dei dati a ${nomeApplicativo } per la richiesta di generazione del codice CIG'  />


	<gene:redefineInsert name="corpo">
	
		<table class="lista">
			<tr>
				<br>
				<br>
				<c:if test="${not empty erroriBloccanti}">
					<b>Non è possibile procedere all'invio dei dati per i seguenti motivi:</b>
				</c:if>
				<ul>
				<c:forEach items="${erroriBloccanti}" step="1" var="item">
					<li>${item}
				</c:forEach>
				</ul>
				<br>
				<c:if test="${not empty erroriNonBloccanti}">
					<b>L'invio dei dati non è completo per i seguenti motivi:</b>
				</c:if>
				<ul>
				<c:forEach items="${erroriNonBloccanti}" step="1" var="item">
					<li>${item}
				</c:forEach>
				</ul>
			</tr>

			<tr>	
				<td>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	
		document.forms[0].jspPathTo.value="gare/commons/popup-invia-dati-richiesta-cig-success.jsp";
		
		function annulla(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>