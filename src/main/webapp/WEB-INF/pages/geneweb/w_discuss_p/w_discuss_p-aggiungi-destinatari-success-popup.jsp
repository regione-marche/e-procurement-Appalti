
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


<div style="padding-left:10px; width:95%;">

	<gene:template file="popup-message-template.jsp">
		<gene:setString name="titoloMaschera" value='Destinatari delle notifiche' />

		<gene:redefineInsert name="corpo">
			<br>
			I destinatari selezionati sono state aggiunti alla lista dei destinatari delle notifiche.
			<br> 
			<br>
		</gene:redefineInsert>
		
		<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
		</gene:redefineInsert>
			
		<gene:javaScript>
			
			window.opener.listaVaiAPagina(window.opener.document.forms[0].pgCorrente.value);
			window.close();
		
			function annulla(){
				window.close();
			}
		
		</gene:javaScript>	
		
	</gene:template>

</div>
