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



<c:set var="codgar1" value="${param.codgar1}" />
<c:set var="iterga" value="${param.iterga}" />

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Acquisisci documenti" />


<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAcquisisciDocumentiRda">
	
			<gene:campoScheda>
				<td colspan="2">
					<i>Mediante questa funzione si importano i documenti della pratica della gara</i>
					<br>
					<b>Attenzione!! Eventuali documenti precedentemente importati saranno eliminati prima della nuova importazione</b>
					<br>
					<c:if test='${not empty requestScope.importRdaEseguito and requestScope.importRdaEseguito eq "1"}' >
						<br>
						<span>
						${requestScope.messageInfo}
						</span>
						<br>
					</c:if>
				</td>
			</gene:campoScheda>
			
			<gene:campoScheda campo="CODGAR1" visibile="false" value="${param.codiceGara}"/>
			<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" value="${param.iterga}"/>
	
			<input type="hidden" name="codgar1" value="${param.codiceGara}">
			<input type="hidden" name="iterga" value="${param.iterga}">
			
	</gene:formScheda>
  </gene:redefineInsert>
  <c:if test='${not empty requestScope.importRdaEseguito}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>

  

	<gene:javaScript>
		function conferma() {
			
			document.forms[0].jspPathTo.value="gare/commons/popup-AcquisisciDocumentiRda.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			opener.historyReload();
			window.close();
		}
	</gene:javaScript>
</gene:template>
</div>
