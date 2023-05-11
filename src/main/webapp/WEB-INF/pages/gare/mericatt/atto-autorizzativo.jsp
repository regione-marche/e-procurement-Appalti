<%
/*
 * Created on: 20/05/2014
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
		<gene:campoScheda campo="IDRIC_${param.contatore}" entita="MERICATT" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDRICATT" value="${item[0]}" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="MERICATT" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDMERICATT" value="${item[1]}" />
		<gene:campoScheda campo="TATTO_${param.contatore}" entita="MERICATT" campoFittizio="true" definizione="N7;0;A2045;;G1TATTORIC" value="${item[2]}" />
		<gene:campoScheda campo="DATTO_${param.contatore}" entita="MERICATT" campoFittizio="true" definizione="D;0;;;G1DATTORIC" value="${item[3]}" />
		<gene:campoScheda campo="NATTO_${param.contatore}" entita="MERICATT" campoFittizio="true" definizione="T30;0;;;G1NATTORIC" value="${item[4]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="IDRIC_${param.contatore}" entita="MERICATT" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDRICATT" value="${param.chiave}" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="MERICATT" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDMERICATT" />
		<gene:campoScheda campo="TATTO_${param.contatore}" entita="MERICATT" campoFittizio="true" definizione="N7;0;A2045;;G1TATTORIC" />
		<gene:campoScheda campo="DATTO_${param.contatore}" entita="MERICATT" campoFittizio="true" definizione="D;0;;;G1DATTORIC" /> 
		<gene:campoScheda campo="NATTO_${param.contatore}" entita="MERICATT" campoFittizio="true" definizione="T30;0;;;G1NATTORIC" />
	</c:otherwise>
</c:choose>