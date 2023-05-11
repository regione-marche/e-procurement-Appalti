
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

	<gene:setString name="titoloMaschera"  value='Richiesta di cancellazione dati'  />
	<gene:redefineInsert name="corpo">
		<table class="lista">
		
			<tr>
				<br>
				L'invio all'ANAC della richiesta di cancellazione 
				è avvenuta con successo.
				<br>
				<br>
				<c:if test="${entita ne 'W3SMARTCIG'}">
					Da questo momento non sar&agrave; pi&ugrave; possibile modificare i dati.
				</c:if>		
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
			<c:when test="${fromGare eq 1 && entita ne 'W3LOTT'}">
				window.opener.historyVaiIndietroDi(1);
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${entita eq 'W3SMARTCIG'}">
						window.opener.location=contextPath+'/History.do?'+csrfToken+'&metodo=reload';
					</c:when>
					<c:otherwise>
						window.opener.selezionaPagina(0);
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
		
		function chiudi(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

