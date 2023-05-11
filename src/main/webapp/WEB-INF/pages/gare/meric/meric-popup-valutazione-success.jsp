
<%
	/*
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

<gene:template file="popup-template.jsp">
	<c:choose>
		<c:when test="${!empty param.id}">
			<c:set var="id" value="${param.id}" />
		</c:when>
		<c:otherwise>
			<c:set var="id" value="${id}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${!empty param.tipo}">
			<c:set var="tipo" value="${param.tipo}" />
		</c:when>
		<c:otherwise>
			<c:set var="tipo" value="${tipo}" />
		</c:otherwise>
	</c:choose>
	

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<c:choose>
		<c:when test="${tipo eq 'VALUTAZIONE'}">
			<c:set var="titolo" value="Valutazione prodotti" />		
		</c:when>
		<c:otherwise>
			<c:set var="titolo" value="Aggiorna dati prodotti" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera"  value='${titolo}' />
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr>
				<br>
				L'operazione &egrave; stata eseguita con successo.
				<br>
				<br>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		window.opener.selezionaPagina(3);
		
		function chiudi(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

