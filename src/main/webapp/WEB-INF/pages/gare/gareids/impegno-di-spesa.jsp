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
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GAREIDS" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGARIDS" value="${item[0]}" />
		<gene:campoScheda campo="NUMIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMIDS" value="${item[1]}" />
		<gene:campoScheda campo="DATEMISS_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="D;0;;;G1DATEMISIDS" value="${item[2]}" />
		<gene:campoScheda campo="NPROT_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="T20;0;;;G1NPROTIDS" value="${item[3]}" />
		<gene:campoScheda campo="DATRICEZ_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="D;0;;;G1DRICEZIDS" value="${item[4]}" />
		<gene:campoScheda campo="IMPIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPIDS" value="${item[5]}" />
		<gene:campoScheda campo="NOTEIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTEIDS" value="${item[6]}" />
		<gene:campoScheda campo="PROGIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" visibile="false" definizione="N12;0;;;G1PROGIDS" value="${item[7]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GAREIDS" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGARIDS" />
		<gene:campoScheda campo="NUMIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMIDS" />
		<gene:campoScheda campo="DATEMISS_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="D;0;;;G1DATEMISIDS" />
		<gene:campoScheda campo="NPROT_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="T20;0;;;G1NPROTIDS" />
		<gene:campoScheda campo="DATRICEZ_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="D;0;;;G1DRICEZIDS" />
		<gene:campoScheda campo="IMPIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPIDS" />
		<gene:campoScheda campo="NOTEIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTEIDS" />
		<gene:campoScheda campo="PROGIDS_${param.contatore}" entita="GAREIDS" campoFittizio="true" visibile="false" definizione="N12;0;;;G1PROGIDS" />
	</c:otherwise>
</c:choose>