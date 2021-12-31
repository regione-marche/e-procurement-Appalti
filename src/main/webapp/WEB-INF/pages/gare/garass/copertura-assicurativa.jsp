<%
/*
 * Created on: 20/02/2012
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
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARASS" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARAASS" value="${item[0]}" />
		<gene:campoScheda campo="NUMASS_${param.contatore}" entita="GARASS" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NGARAASS" value="${item[1]}" />
		<gene:campoScheda campo="TIPASS_${param.contatore}" entita="GARASS" campoFittizio="true" definizione="N7;0;A1085;;G1TIPASS" value="${item[2]}" modificabile="${param.datiModificabili }"/>
		<gene:campoScheda campo="IMPASS_${param.contatore}" entita="GARASS" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPASS" value="${item[3]}" modificabile="${param.datiModificabili }"/>
		<gene:campoScheda campo="NOTASS_${param.contatore}" entita="GARASS" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTASS" value="${item[4]}" modificabile="${param.datiModificabili }"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARASS" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARAASS" value="${param.chiave}" />
		<gene:campoScheda campo="NUMASS_${param.contatore}" entita="GARASS" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NGARAASS" />
		<gene:campoScheda campo="TIPASS_${param.contatore}" entita="GARASS" campoFittizio="true" definizione="N7;0;A1085;;G1TIPASS" modificabile="${param.datiModificabili }"/>
		<gene:campoScheda campo="IMPASS_${param.contatore}" entita="GARASS" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPASS" modificabile="${param.datiModificabili }"/> 
		<gene:campoScheda campo="NOTASS_${param.contatore}" entita="GARASS" campoFittizio="true" definizione="T2000;0;;NOTE;G1NOTASS" modificabile="${param.datiModificabili }"/>
	</c:otherwise>
</c:choose>