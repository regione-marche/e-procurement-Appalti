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
		<gene:campoScheda title="Numero gara" entita="W3LOTTCUP" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCUP_NUMGARA" value="${item[0]}"/>
		<gene:campoScheda title="Numero lotto" entita="W3LOTTCUP" campo="NUMLOTT_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCUP_NUMLOTT" value="${item[1]}"/>
		<gene:campoScheda title="Numero CUP" entita="W3LOTTCUP" campo="NUMCUP_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3LOTTCUP_NUMCUP" value="${item[2]}"/>
		<gene:campoScheda title="CUP" entita="W3LOTTCUP" campo="CUP_${param.contatore}" campoFittizio="true" definizione="T15;;;;W3LOTTCUP_CUP" value="${item[3]}"/>
		<gene:campoScheda title="Descrizione" entita="W3LOTTCUP" campo="DATI_DIPE_${param.contatore}" campoFittizio="true" definizione="T1024;0;;;W3LOTTCUP_DATI_DIPE" modificabile="false" value="${item[4]}"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda title="Numero gara" entita="W3LOTTCUP" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCUP_NUMGARA" value="${numgara}"/>
		<gene:campoScheda title="Numero lotto" entita="W3LOTTCUP" campo="NUMLOTT_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCUP_NUMLOTT" value="${numlott}"/>
		<gene:campoScheda title="Numero CUP" entita="W3LOTTCUP" campo="NUMCUP_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3LOTTCUP_NUMCUP" />
		<gene:campoScheda title="CUP" entita="W3LOTTCUP" campo="CUP_${param.contatore}" campoFittizio="true" definizione="T15;;;;W3LOTTCUP_CUP" />
		<gene:campoScheda title="Descrizione" entita="W3LOTTCUP" campo="DATI_DIPE_${param.contatore}" campoFittizio="true" definizione="T1024;0;;;W3LOTTCUP_DATI_DIPE" modificabile="false" />
	</c:otherwise>
</c:choose>


