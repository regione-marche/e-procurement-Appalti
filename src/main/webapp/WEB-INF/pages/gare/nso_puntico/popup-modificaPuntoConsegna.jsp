<%
/*
 * Created on: 05-03-2015
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
		Finestra per la rettifica dei termini
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.modificaEseguita and requestScope.modificaEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/date.js"></script> 
</gene:redefineInsert>

<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.idOrdine}'>
			<c:set var="idOrdine" value="${param.idOrdine}" />
		</c:when>
		<c:otherwise>
			<c:set var="idOrdine" value="${idOrdine}" />
		</c:otherwise>
	</c:choose>
	
		
	<gene:setString name="titoloMaschera" value='Modifica punto consegna' />
		
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="NSO_PUNTICONS" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupModificaPuntoConsegna" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupModificaPuntoConsegna">
		
		<gene:campoScheda campo="COD_PUNTO_CONS" campoFittizio="true" defaultValue="${initCodPuntoCons}" definizione="T20;;;;NSO_PU_CODPC" />
		<gene:campoScheda campo="INDIRIZZO" campoFittizio="true" defaultValue="${initIndirizzo}" definizione="T40;;;;NSO_PU_IND" />
		<gene:campoScheda campo="LOCALITA" campoFittizio="true" defaultValue="${initLocalita}" definizione="T40;;;;NSO_PU_LOC" />
		<gene:campoScheda campo="CAP" campoFittizio="true" defaultValue="${initCap}" definizione="T5;;;;NSO_PU_CAP" />
		<gene:campoScheda campo="CITTA" campoFittizio="true" defaultValue="${initCitta}" definizione="T36;;;;NSO_PU_CITTA" />
		<gene:campoScheda campo="CODNAZ" campoFittizio="true" defaultValue="${initCodNaz}" definizione="T2;;Ag010;;NSO_PU_CNAZ" />		
		<gene:campoScheda title="Altre indicazioni" campo="ALTRE_INDIC" campoFittizio="true" defaultValue="${initAltreIndic}" definizione="T2000;;;NOTE;NSO_PU_ALTRI" />
		<gene:campoScheda title="Consegna Domiciliare?" campo="CONS_DOMICILIO" campoFittizio="true" defaultValue="${initConsDom}" definizione="T2;0;;SN" />
	
		<input type="hidden" name="idPuntoConsegna" id="idPuntoConsegna" value="${idPuntoConsegna}" />
		<input type="hidden" name="idOrdine" id="idOrdine" value="${idOrdine}" />
	</gene:formScheda>
  </gene:redefineInsert>

	
	
	<gene:javaScript>
			
			
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/nso_puntico/popup-modificaPuntoConsegna.jsp";
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