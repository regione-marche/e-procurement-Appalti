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
		<gene:campoScheda title="Numero gara" entita="W3LOTTCATE" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCATE_NUMGARA" value="${item[0]}"/>
		<gene:campoScheda title="Numero lotto" entita="W3LOTTCATE" campo="NUMLOTT_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCATE_NUMLOTT" value="${item[1]}"/>
		<gene:campoScheda title="Numero categoria" entita="W3LOTTCATE" campo="NUMCATE_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3LOTTCATE_NUMCATE" value="${item[2]}"/>
		<gene:campoScheda title="Ulteriore categoria" entita="W3LOTTCATE" campo="CATEGORIA_${param.contatore}" campoFittizio="true" definizione="T5;0;W3z03;;W3LOTTCATE_CATEGORIA" value="${item[3]}"/>
	</c:when>

	<c:when test="${param.tipoDettaglio eq 2}">
		<gene:campoScheda title="Numero gara" entita="W3LOTTCATE" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCATE_NUMGARA" value="${numgara}"/>
		<gene:campoScheda title="Numero lotto" entita="W3LOTTCATE" campo="NUMLOTT_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCATE_NUMLOTT" value="${numlott}"/>
		<gene:campoScheda title="Numero categoria" entita="W3LOTTCATE" campo="NUMCATE_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3LOTTCATE_NUMCATE" />
		<gene:campoScheda title="Ulteriore categoria" entita="W3LOTTCATE" campo="CATEGORIA_${param.contatore}" campoFittizio="true" definizione="T5;0;W3z03;;W3LOTTCATE_CATEGORIA" />
	</c:when>

	<c:when test="${param.tipoDettaglio eq 3}">
		<gene:campoScheda title="Numero gara" entita="W3LOTTCATE" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCATE_NUMGARA" value="${numgara}"/>
		<gene:campoScheda title="Numero lotto" entita="W3LOTTCATE" campo="NUMLOTT_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3LOTTCATE_NUMLOTT" value="${numlott}"/>
		<gene:campoScheda title="Numero categoria" entita="W3LOTTCATE" campo="NUMCATE_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3LOTTCATE_NUMCATE" />
		<gene:campoScheda title="Ulteriore categoria" entita="W3LOTTCATE" campo="CATEGORIA_${param.contatore}" campoFittizio="true" definizione="T5;0;W3z03;;W3LOTTCATE_CATEGORIA" />
	</c:when>

</c:choose>

<gene:javaScript>
	modifySelectOption($("#W3LOTTCATE_CATEGORIA_${param.contatore}"));
</gene:javaScript>
