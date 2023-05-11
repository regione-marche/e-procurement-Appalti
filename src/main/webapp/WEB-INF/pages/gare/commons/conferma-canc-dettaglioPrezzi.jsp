<%
/*
 * Created on: 25-set-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per la cencallazione del dettaglio prezzi della ditta 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.cancellazioneEffettuata and requestScope.cancellazioneEffettuata eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<gene:setString name="titoloMaschera" value='Cancellazione dettaglio offerta prezzi della ditta' />
	
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DPRE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCancellaDettaglioPrezzi">
	
		<gene:campoScheda>
			<td>&nbsp;&nbsp;</td>
			<td>
			
					<br>
					Confermi la cancellazione di tutti i prezzi unitari inseriti e il ripristino dei dati iniziali?<br>
					<br>
		
			</td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.gara}"  visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="DITTAO" campoFittizio="true" defaultValue="${param.ditta}"  visibile="false" definizione="T10;0"/>
		<gene:campoScheda campo="CODGARA" campoFittizio="true" defaultValue="${param.codiceGara}"  visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="GARA_OFFERTA_SINGOLA" campoFittizio="true" defaultValue="${param.garaOffertaUnica}"  visibile="false" definizione="T10;0"/>
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/conferma-canc-dettaglioPrezzi.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
	
	</gene:javaScript>
</gene:template>
</div>

</c:otherwise>
</c:choose>
