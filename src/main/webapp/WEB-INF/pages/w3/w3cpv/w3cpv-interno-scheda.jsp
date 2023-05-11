<%
	/*
	 * Created on 07-Ott-2010
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<c:set var="numgara" value="${gene:getValCampo(key, 'NUMGARA')}" />
<c:set var="numlott" value="${gene:getValCampo(key, 'NUMLOTT')}" />

<c:choose>

	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda entita="W3CPV" campo="NUMGARA_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;W3CPVNUMGARA" value="${item[0]}" />
		<gene:campoScheda entita="W3CPV" campo="NUMLOTT_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;W3CPVNUMLOTT" value="${item[1]}" />
		<gene:campoScheda entita="W3CPV" campo="NUM_CPV_${param.contatore}" visibile="false" campoFittizio="true" definizione="N3;1;;;W3CPVNUMCPV" value="${item[2]}" />
		<gene:campoScheda entita="W3CPV" campo="CPV_${param.contatore}" speciale="true" href="#" title="Codice CPV Secondario" value="${item[3]}" campoFittizio="true" definizione="T12;0;;;W3CPVCPV" >
			<gene:popupCampo titolo="Dettaglio CPV" href="#" />
		</gene:campoScheda>

	</c:when>

	<c:when test="${param.tipoDettaglio eq 2}">
		<gene:campoScheda entita="W3CPV" campo="NUMGARA_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;W3CPVNUMGARA" />
		<gene:campoScheda entita="W3CPV" campo="NUMLOTT_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;W3CPVNUMLOTT" />
		<gene:campoScheda>
			<td colspan="2"><b>Nessun CPV Secondario inserito.</b></td>
		</gene:campoScheda>
	</c:when>

	<c:when test="${param.tipoDettaglio eq 3}">
		<gene:campoScheda entita="W3CPV" campo="NUMGARA_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;W3CPVNUMGARA" value="${numgara}" />
		<gene:campoScheda entita="W3CPV" campo="NUMLOTT_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;W3CPVNUMLOTT" value="${numlott}" />
		<gene:campoScheda entita="W3CPV" campo="NUM_CPV_${param.contatore}" visibile="false" campoFittizio="true" definizione="N3;1;;;W3CPVNUMCPV" />
		<gene:campoScheda entita="W3CPV" campo="CPV_${param.contatore}" speciale="true" href="#" title="Codice CPV Secondario" campoFittizio="true" definizione="T12;0;;;W3CPVCPV" >
			<gene:popupCampo titolo="Dettaglio CPV" href="#" />
		</gene:campoScheda>
	</c:when>

</c:choose>
