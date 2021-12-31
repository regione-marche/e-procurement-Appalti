<%
  /*
			 * Created on 15/09/2020
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:choose>
	<c:when test='${!empty ngara}'>
		<c:set var="ngara" value='${ngara}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${param.ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty genere}'>
		<c:set var="genere" value='${genere}' />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${param.genere}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	<c:set var="modo" value="NUOVO" scope="request" />
		
	<gene:setString name="titoloMaschera" value="Sorteggio per verifica documenti" />

	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupSorteggioVerificaDoc">
			<gene:campoScheda>
			<td colSpan="2">
			<c:choose>
				<c:when test="${!empty sorteggioEseguito and sorteggioEseguito eq '-1'}">
					<br>
					Non è stato possibile effettuare il sorteggio.
					<br>
		  			<br>
				</c:when>
				<c:when test="${!empty sorteggioEseguito and sorteggioEseguito eq '1'}">
					<br>
					Operazione completata.<br><br>
					Numero operatori coinvolti nel sorteggio: ${numDitte}<br>
					Numero operatori sorteggiati: ${numDitteSorteggiate }<br>
					<br>
		  			<br>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${genere eq 10 }">
							<c:set var="msgGenere" value="elenco"/>
						</c:when>
						<c:otherwise>
							<c:set var="msgGenere" value="catalogo"/>
						</c:otherwise>
					</c:choose>
					Mediante questa funzione viene effettuato il sorteggio degli operatori in ${msgGenere } per la verifica dei documenti.<br>
					Il sorteggio riguarda gli operatori in stato 'Abilitato' che non sono mai stati sorteggiati oppure sono stati sorteggiati prima della data specificata sotto (indicare una data successiva a quella odierna per non impostare alcun limite).<br><br>
					Confermi l'operazione?
					<br><br>
				</c:otherwise>
			</c:choose>
			</td>
			</gene:campoScheda>
			<gene:campoScheda campo="PERCENTUALE" title="Percentuale operatori da sorteggiare" campoFittizio="true" definizione="N3;0;;PRC" obbligatorio="true" visibile="${empty sorteggioEseguito }" defaultValue="10">
				<gene:checkCampoScheda funzione='controlloPercentuale(##)' obbligatorio="true" messaggio="I valori consentiti sono compresi fra 0 e 100" onsubmit="false"/>
			</gene:campoScheda>
			<gene:campoScheda campo="DATA" title="Data da cui escludere gli operatori dal sorteggio" campoFittizio="true" definizione="D;0;;DATA_ELDA " visibile="${empty sorteggioEseguito }" obbligatorio="true"/>
			<input type="hidden" name="ngara" id="ngara" value="${ngara }"/>
			<input type="hidden" name="genere" id="genere" value="${genere }"/>
		</gene:formScheda>
	  	</gene:redefineInsert>
<c:if test='${not empty requestScope.sorteggioEseguito}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>	
	<gene:javaScript>

		
		function annulla(){
			window.close();
		}
		
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/popupSorteggioVerificaDoc.jsp";
			schedaConferma();
		}
		
		function controlloPercentuale(percentuale){
			if(percentuale !=null){
				if(percentuale >=0 && percentuale <=100)
					return true;
				else
					return false;
			
			}else
			 return true;
		}
		
				
	</gene:javaScript>
</gene:template>
</div>

