<%
/*
 * Created on: 24/11/2008
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

<c:set var="numeroGara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="numeroSeduta" value='${fn:substringAfter(param.chiave, ";")}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" visibile="false" definizione="T20;1;;;GSSNGARA" value="${item[0]}" />
		<gene:campoScheda campo="NUMSED_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" visibile="false" definizione="N3;1;;;GSSNUMSED" value="${item[1]}" />
		<gene:campoScheda campo="NUMSOSP_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" visibile="false" definizione="N3;1;;;GSSNUMSOSP" value="${item[2]}" />
		<gene:campoScheda campo="ORAINI_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" definizione="T6;0;;ORA;GSSORAINI" value="${item[3]}" />
		<gene:campoScheda campo="MOTSON_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" definizione="T2000;0;;NOTE;GSSMOTSON" value="${item[4]}" />
		<gene:campoScheda campo="ORAFIN_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" definizione="T6;0;;ORA;GSSORAFIN" value="${item[5]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" visibile="false" definizione="T20;1;;;GSSNGARA" value="${numeroGara}" />
		<gene:campoScheda campo="NUMSED_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" visibile="false" definizione="N3;1;;;GSSNUMSED" value="${numeroSeduta}" />
		<gene:campoScheda campo="NUMSOSP_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" visibile="false" definizione="N3;1;;;GSSNUMSOSP" />
		<gene:campoScheda campo="ORAINI_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" definizione="T6;0;;ORA;GSSORAINI" />
		<gene:campoScheda campo="MOTSON_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" definizione="T2000;0;;NOTE;GSSMOTSON" />
		<gene:campoScheda campo="ORAFIN_${param.contatore}" entita="GARSEDSOSP" campoFittizio="true" definizione="T6;0;;ORA;GSSORAFIN" />
	</c:otherwise>
</c:choose>	