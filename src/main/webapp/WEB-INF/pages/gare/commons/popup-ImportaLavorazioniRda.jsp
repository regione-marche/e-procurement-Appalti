<%
/*
 * Created on: 11-10-2012
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
		Finestra per la valorizzazione del campo ESINEG 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.importRdaEseguito and requestScope.importRdaEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:set var="ngara" value="${param.ngara}" />

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Importa appalto in gara" />


<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreImportaLavorazioniRda">
	
			<gene:campoScheda>
				<td colspan="2">
					<i>Mediante questa funzione si importano le rda in lista lavorazioni</i>
					<br>
					<b>Attenzione!! Eventuali lavorazioni presenti saranno eliminate prima della nuova importazione</b>
					<br>
				</td>
			</gene:campoScheda>
			
			<gene:campoScheda campo="CODAPPALTO" title="Codice appalto" obbligatorio="true" campoFittizio="true" definizione="T21;0"/>
			<gene:campoScheda campo="NGARA" visibile="false" value="${param.ngara}"/>
	
			<input type="hidden" name="ngara" value="${param.ngara}">
			
	</gene:formScheda>
  </gene:redefineInsert>
  

	<gene:javaScript>
		function conferma() {
			
			document.forms[0].jspPathTo.value="gare/commons/popup-ImportaLavorazioniRda.jsp";
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
