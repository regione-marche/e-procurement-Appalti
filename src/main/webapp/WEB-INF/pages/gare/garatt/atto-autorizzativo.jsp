<%
/*
 * Created on: 28/10/2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGARAT" value="${item[0]}" />
		<gene:campoScheda campo="NUMATT_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMATT" value="${item[1]}" />
		<gene:campoScheda campo="TATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="N7;0;A2045;;G1TATTOAT" value="${item[2]}" />
		<gene:campoScheda campo="DATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="D;0;;;G1DATTOAT" value="${item[3]}" />
		<gene:campoScheda campo="NATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T30;0;;;G1NATTOAT" value="${item[4]}" />
		<gene:campoScheda campo="DATRICT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="D;0;;;G1DATRICAT" value="${item[5]}" />
		<gene:campoScheda campo="DPROAA_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="D;0;;;G1DPROAA" value="${item[7]}" />
		<gene:campoScheda campo="NPROAT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T20;0;;;G1NPROAAT" value="${item[6]}" />
		<gene:campoScheda campo="NOTEAT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTEATAT" value="${item[8]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGARAT" value="${param.chiave}" />
		<gene:campoScheda campo="NUMATT_${param.contatore}" entita="GARATT" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMATT" />
		<gene:campoScheda campo="TATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="N7;0;A2045;;G1TATTOAT" />
		<gene:campoScheda campo="DATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="D;0;;;G1DATTOAT" /> 
		<gene:campoScheda campo="NATTOT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T30;0;;;G1NATTOAT" />
		<gene:campoScheda campo="DATRICT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="D;0;;;G1DATRICAT" />
		<gene:campoScheda campo="DPROAA_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="D;0;;;G1DPROAA" />
		<gene:campoScheda campo="NPROAT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T20;0;;;G1NPROAAT" />
		<gene:campoScheda campo="NOTEAT_${param.contatore}" entita="GARATT" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTEATAT"  />
	</c:otherwise>
</c:choose>