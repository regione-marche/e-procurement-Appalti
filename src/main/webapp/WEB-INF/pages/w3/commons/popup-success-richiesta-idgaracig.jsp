
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

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<c:choose>
		<c:when test="${entita eq 'W3GARA'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione numero gara' />
		</c:when>
		<c:when test="${entita eq 'W3LOTT'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione codice CIG' />
		</c:when>
		<c:when test="${entita eq 'W3SMARTCIG'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione codice SMARTCIG' />
		</c:when>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
		
			<tr>
			<c:if test="${collegaCig ne 'true'}">
				<br>
				L'invio dei dati all'ANAC e la successiva assegnazione è avvenuta con successo.
				<br>
			</c:if>
				<br>
				<c:choose>
					<c:when test="${entita eq 'W3GARA'}">
						Alla gara è stato assegnato il numero gara <b>${idgara}</b>
					</c:when>
					<c:when test="${entita eq 'W3LOTT'}">
						Al lotto è stato assegnato il codice CIG <b>${cig}</b>
					</c:when>
					<c:when test="${entita eq 'W3SMARTCIG'}">
						Alla gara è stato assegnato il codice CIG <b>${cig}</b>
					</c:when>
				</c:choose>
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
		<c:choose>
			<c:when test="${entita eq 'W3SMARTCIG'}">
				window.opener.location=contextPath+'/History.do?'+csrfToken+'&metodo=reload';
			</c:when>
			<c:otherwise>
				window.opener.selezionaPagina(0);
			</c:otherwise>
		</c:choose>
		
		function chiudi(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

