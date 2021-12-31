
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

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<gene:setString name="titoloMaschera"  value="Export documenti ${fn:replace(soggetto,'$','')} su file zip"/>

	<gene:redefineInsert name="corpo">
	
		<table class="lista">
		 	<tr>
				<br>
				La richiesta di export dei documenti è stata inoltrata correttamente.
				Una volta prodotto il file zip, verrà inviato un messaggio di notifica con il link per procedere al download.
				<br>
				<br>
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
	
		document.forms[0].jspPathTo.value="gare/commons/popup-richiesta-export-documenti-success.jsp";
		
		function annulla(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

