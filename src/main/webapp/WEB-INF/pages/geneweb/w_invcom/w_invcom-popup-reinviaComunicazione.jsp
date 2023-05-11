<%
	/*
	 * Created on 03-nov-2009
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

<%//Popup per annullare l'aggiudicazione definitiva%>

<c:choose>
	<c:when test='${RISULTATO eq "ESEGUITO"}' >
		<script type="text/javascript">
				opener.historyReload();
				window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${empty param.ngara}'>
			<c:set var="ngara" value='${ngara}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${param.ngara}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Reinvia comunicazione" />
	
	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${ngara}" />
		<c:set var="modo" value="MODIFICA" scope="request" />

		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupReinviaComunicazioni">
			
			<gene:campoScheda>
				<td colSpan="2"><br>
				Mediante questa funzione è possibile ritentare l'invio della comunicazione, il cui precedente invio ha presentato errore in fase di protocollazione.
				<br>L'operazione ha effetto anche sulle altre comunicazioni in lista che sono nello stesso stato di errore.<br><br>
				Confermi l'operazione?<br><br>
				</td>
			</gene:campoScheda>
			
			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false" />
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${RISULTATO ne "ESEGUITO"}'>
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</gene:campoScheda>
			
		</gene:formScheda>
		
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="geneweb/w_invcom/w_invcom-popup-reinviaComunicazione.jsp";
	
	    function annulla(){
			window.close();
		}
		
		function conferma(){
			schedaConferma();
		}
		
	</gene:javaScript>
</gene:template>

</div>

</c:otherwise>
</c:choose>