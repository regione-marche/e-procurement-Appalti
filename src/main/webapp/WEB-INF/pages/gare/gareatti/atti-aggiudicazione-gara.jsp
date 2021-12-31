<%
/*
 * Created on: 19/12/2013
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

<c:set var="tipoRelazione" value='${fn:substringBefore(param.chiave, ";")}' />
	<c:choose>
	<c:when test='${tipoRelazione eq "TORN"}'>
		<c:set var="codiceGara" value='${fn:substringAfter(param.chiave, ";")}' />
	</c:when>
	<c:otherwise>
		<c:set var="numeroGara" value='${fn:substringAfter(param.chiave, ";")}' />
	</c:otherwise>
	</c:choose>

	<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="GAREATTI" campoFittizio="true" visibile="false" definizione="N12;1;;;G1AT_ID" value="${item[0]}" />
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GAREATTI" campoFittizio="true" visibile="false" definizione="T21;0;;;G1AT_CODGAR" value="${item[1]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GAREATTI" campoFittizio="true" visibile="false" definizione="T20;0;;;G1AT_NGARA" value="${item[2]}" />
		<gene:campoScheda campo="NUMATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true"  visibile="true" definizione="T10;0;;;G1AT_NUM" value="${item[3]}"/>
		<gene:campoScheda campo="TIPOATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true"  visibile="true" definizione="N3;0;A2045;;G1AT_TIPO" value="${item[4]}"/>
		<gene:campoScheda campo="DATAATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true" definizione="D;0;;;G1AT_DATA" value="${item[5]}" />
		<gene:campoScheda campo="NUMPROTATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true"  visibile="true" definizione="T20;0;;;G1AT_NPROT" value="${item[6]}"/>
		<gene:campoScheda campo="DATAPROTATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true" definizione="D;0;;;G1AT_DPROT" value="${item[7]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="GAREATTI" campoFittizio="true" visibile="false" definizione="N12;1;;;G1AT_ID" />
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GAREATTI" campoFittizio="true" visibile="false" definizione="T21;0;;;G1AT_CODGAR" value="${codiceGara}"/>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GAREATTI" campoFittizio="true" visibile="false" definizione="T20;0;;;G1AT_NGARA" value="${numeroGara}"/>
		<gene:campoScheda campo="NUMATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true"  visibile="true" definizione="T10;0;;;G1AT_NUM" />
		<gene:campoScheda campo="TIPOATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true"  visibile="true" definizione="N3;0;A2045;;G1AT_TIPO" />
		<gene:campoScheda campo="DATAATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true" definizione="D;0;;;G1AT_DATA" />				
		<gene:campoScheda campo="NUMPROTATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true"  visibile="true" definizione="T20;0;;;G1AT_NPROT" />
		<gene:campoScheda campo="DATAPROTATTO_${param.contatore}" entita="GAREATTI" campoFittizio="true" definizione="D;0;;;G1AT_DPROT"  />
	</c:otherwise>
	</c:choose>

