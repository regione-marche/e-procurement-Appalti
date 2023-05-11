
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

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<c:set var="codgar" value="${param.codgar}" />
	<gene:setString name="titoloMaschera"  value='Invio dei dati a Formulari Europei per la creazione dei formulari GUUE'  />


	<gene:redefineInsert name="corpo">
	
		<table class="lista">
			<tr>
				<br>
				I dati della gara selezionata sono stati inviati a <b>Formulari Europei.</b>
				<br>
				<br>
				Il controllo dei dati del bando/avviso ed il successivo invio al <b>SIMAP della Comunit&agrave; Europea</b>	deve essere effettuato mediante il prodotto Formulari Europei.
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
	
		document.forms[0].jspPathTo.value="gare/commons/popup-invia-bando-avviso-simap-success.jsp";
		
		function annulla(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

