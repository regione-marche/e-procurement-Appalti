<%
/*
 * Created on: 12-06-2019
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.lotto}'>
		<c:set var="lotto" value="${param.lotto}" />
	</c:when>
	<c:otherwise>
		<c:set var="lotto" value="${lotto}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${sorteggioEseguito eq "1"}' >
		<script type="text/javascript">
		window.opener.historyReload();
		window.close();
		</script>
	</c:when>
	<c:otherwise>

<c:set var="controllo" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.InizializzaziPaginaSorteggioDitteVerificaRequisitiFunction", pageContext, ngara, "10")}'/>

<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Sorteggio ditte per verifica requisiti per la gara ${ngara }' />
	<c:set var="modo" value="NUOVO" scope="request" />
	<gene:redefineInsert name="corpo">
		<br>
		Mediante questa funzione viene effettuato il sorteggio sulle ditte in gara per la verifica dei requisiti. 
		<br>
		<c:choose>
			<c:when test='${ controllo eq "-1"}' >
				<br><b>Non è possibile procedere al sorteggio perchè non ci sono ditte in gara.</b>	
			</c:when>
			<c:when test='${ controllo eq "-2"}' >
				<br><b>Non è possibile procedere al sorteggio perchè ci sono già delle ditte sorteggiate.</b>
			</c:when>
			<c:otherwise>
				Indicare la percentuale di ditte da sorteggiare.
			</c:otherwise>
		</c:choose>
				
		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupSorteggioDitteVerificaRequisiti">
			<gene:campoScheda>
					<td colSpan="2"><br></td>
				</gene:campoScheda>
			<gene:campoScheda campo="PERC_DITTE" title="Percentuale ditte da sorteggiare" campoFittizio="true" value="10" definizione="N3;;;PRC;" visibile='${ controllo ne "-1" and controllo ne "-2"}' >
				<gene:checkCampoScheda funzione='calcoloNumDitte(##)' obbligatorio="true" messaggio="Specificare un valore minore o uguale a 100" onsubmit="false"/>
			</gene:campoScheda>
			<gene:campoScheda campo="NUM_DITTE" title="Numero ditte in gara" campoFittizio="true" definizione="N5;;;;" value="${numDitte }" modificabile="false" visibile='${ controllo ne "-1" and controllo ne "-2"}'/>
			<gene:campoScheda campo="NUM_DITTE_SORTEGGIATE" title="Numero ditte da sorteggiare" campoFittizio="true" definizione="N5;;;;" value="${numDitteSorteggiate}" modificabile="false" visibile='${ controllo ne "-1" and controllo ne "-2"}'/>
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
			<input type="hidden" name="nDitteSorteggiate" id="nDitteSorteggiate" value="${numDitteSorteggiate}" />
			<input type="hidden" name="lotto" id="lotto" value="${lotto}" />
			<!-- il campo successivo è stato inserito al solo fine di evitare che se si preme invio dovo avere inserito la percentuale scatti il submit automatico -->
			<input type="text" value="" style="display: none;"/>
		</gene:formScheda>
					
  </gene:redefineInsert>
	 <c:if test='${ controllo eq "-1" or controllo eq "-2"}' >
	  	<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
		</gene:redefineInsert>
	  </c:if>
	
	<gene:javaScript>
		
		
		
		function annulla(){
			window.close();
		}

		function conferma(){
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-sorteggioDitteVerificaRequisiti.jsp";
			schedaConferma();
		}
		
		function calcoloNumDitte(percentuale){
			var ret=true;
			if(percentuale > 0 && percentuale <= 100){
				var numDitte = parseInt("${ numDitte}");
				percentuale = percentuale * 0.01;
				var numDitteSorteggiate = Math.ceil(numDitte * percentuale);
				$("#NUM_DITTE_SORTEGGIATEview").text(numDitteSorteggiate);
				$("#nDitteSorteggiate").val(numDitteSorteggiate);
			}else{
				ret=false;
			}
			return ret;
		}
	</gene:javaScript>
</gene:template>

</c:otherwise>
</c:choose>