
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

<div style="width:97%;">
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:set var="nomeApplicativo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", nomeAppicativoCig)}'/>
<c:if test="${empty nomeApplicativo or nomeApplicativo eq '' }">
	<c:set var="nomeApplicativo" value='Vigilanza' />
</c:if>

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<c:set var="codgar" value="${param.codgar}" />
	<c:set var="operazione" value="${param.operazione}" />
	<gene:setString name="titoloMaschera"  value='Invio dei dati a ${nomeApplicativo } per la richiesta di generazione del codice CIG'  />

	<gene:redefineInsert name="corpo">
	
		<table class="lista">
		 <c:if test='${operazione eq "OP1" || operazione eq "OP1.1"}'>
			<tr>
				<br>
				I dati della gara selezionata sono stati inviati a <b>${nomeApplicativo}.</b>
				<br>
				<c:if test="${!empty esitoWS and esitoWS !='' }">
				<br>
				<textarea cols="50" rows="10" readonly="readonly"><c:out value="${esitoWS}"/></textarea>
				<br>
				</c:if>
				<br>
				Il controllo dei dati per la richiesta del codice CIG ed il successivo invio al <b>SIMOG</b> deve essere effettuato mediante il prodotto ${nomeApplicativo}.
				<br>
				<br>
			</tr>
			<tr>	
				<td>
					&nbsp;
				</td>
			</tr>
		 </c:if>
		 <c:if test='${operazione eq "OP2"}'>
			<tr>
				<br>
				Consultazione dei dati di gara completata.
				<br>
				<c:if test="${!empty esitoWS and esitoWS !='' }">
				<br>
				<br>
				<textarea cols="50" rows="10" readonly="readonly"><c:out value="${esitoWS}"/></textarea>
				<br>
				</c:if>
				<br>
			</tr>
			<tr>	
				<td>
					&nbsp;
				</td>
			</tr>
		 </c:if>
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
			window.opener.historyReload();
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

