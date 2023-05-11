
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

		<c:if test="${entita eq 'W3GARAREQ'}">
			<gene:setString name="titoloMaschera"  value='Richiesta requisiti di gara' />
		</c:if>
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
		
			<tr>
				<br>
				L'invio dei dati all'ANAC è avvenuta con successo.
				<br>
				<br>
					<c:if test="${entita eq 'W3GARAREQ'}">
						Sono stati assegnati i requisiti alla gara con numero gara <b>${idgara}</b>
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
		window.opener.selezionaPagina(2);
		
		function chiudi(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

