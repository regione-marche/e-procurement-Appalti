<%
/*
 * Created on: 15-06-2020
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
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
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

<c:set var="controllo" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.InizializzaziPaginaSorteggioDitteInvitiFunction", pageContext, ngara, codgar)}'/>

<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Sorteggio operatori da invitare a presentare offerta per la gara ${ngara }' />
	<c:set var="modo" value="NUOVO" scope="request" />
	<gene:redefineInsert name="corpo">
		<br>
		Mediante questa funzione viene effettuato il sorteggio degli operatori da invitare a presentare offerta. 
		<br>
		<c:choose>
			<c:when test='${ empty numope or numope eq 0}' >
				<c:set var="errore" value="true"/>
				<br><b>Non è possibile procedere al sorteggio perchè non è stato specificato il numero di operatori da sorteggiare.</b>	
			</c:when>
			<c:when test='${ controllo eq "-1"}' >
				<c:set var="errore" value="true"/>
				<br><b>Non è possibile procedere al sorteggio perchè non ci sono operatori in gara.</b>	
			</c:when>
			<c:when test='${ controllo eq "-2"}' >
				<c:set var="errore" value="true"/>
				<br><b>Il sorteggio risulta già fatto.</b>
			</c:when>
			<c:otherwise>
			<br> Confermi l'operazione?
			</c:otherwise>
			
		</c:choose>
				
		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupSorteggioDitteInviti">
			<gene:campoScheda>
					<td colSpan="2"><br></td>
				</gene:campoScheda>
			<gene:campoScheda campo="NUM_DITTE" title="Numero operatori in gara su cui viene fatto il sorteggio" campoFittizio="true" definizione="N5;;;;" value="${numDitte }" modificabile="false" visibile='${ controllo ne "-1" and controllo ne "-2"}'/>
			<gene:campoScheda campo="NUMOPE" title="Numero operatori da sorteggiare" campoFittizio="true" definizione="N5;;;;" value="${numope}" modificabile="false" visibile='${ controllo ne "-1" and controllo ne "-2"}'/>
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
			<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
						
						
		</gene:formScheda>
					
  </gene:redefineInsert>
	 <c:if test='${ errore eq "true"}' >
	  	<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
		</gene:redefineInsert>
	  </c:if>
	
	<gene:javaScript>
		
		
		
		function annulla(){
			window.close();
		}

		function conferma(){
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-sorteggioDitteInviti.jsp";
			schedaConferma();
		}
		
		
	</gene:javaScript>
</gene:template>

</c:otherwise>
</c:choose>