
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

	<gene:setString name="titoloMaschera"  value='Riallinea dati a SIMOG'  />
	<gene:redefineInsert name="corpo">
		<table class="lista">
		<c:choose>
			<c:when test='${esito}' >
			<tr>
				<br>
				L'operazione di riallineamento dei dati &egrave; avvenuta con successo.
				<br>
				<br>
			</tr>
			</c:when>
			<c:otherwise>
			<tr>
				<br>
				L'operazione di riallineamento dei dati &egrave; stata interrotta a causa di un errore.
				<br>
				<br>
			</tr>
			</c:otherwise>
		</c:choose>	
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	
		$(document).ready(function() {
			var l = Math.floor((screen.availWidth - 500)/2);
			var t = Math.floor((screen.availHeight - 350)/2);
			window.resizeTo(500,350);
			window.moveTo(l,t);
			window.opener.selezionaPagina(0);
		});
		
		function chiudi() {
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>
