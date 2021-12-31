<%
/*
 * Created on: 26/05/2009
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

<c:set var="id" value='${fn:substringBefore(param.chiave, ";")}' />

<c:choose>
<c:when test="${param.tipoDettaglio eq 1}">		
			<gene:campoScheda campo="NORPAR_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="false" definizione="F8.3;0;;;G1NORPARM" value="${item[5]}" />
			<gene:campoScheda campo="IDCRIMOD_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="false" definizione="F8.3;0;;;G1IDCMOD" value="${item[4]}" />
			<gene:campoScheda campo="NECVAN1_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="false" definizione="N7;0;;;G1NECVAN1M" value="${item[3]}" />
			<gene:campoScheda campo="NORPAR1_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="true" definizione="F8.3;0;;;G1NORPAR1M" value="${item[5]}" />
			<gene:campoScheda campo="DESPAR_${param.contatore}" entita="GOEVMOD" campoFittizio="true" definizione="T2000;0;;NOTE;G1DESPARM" value="${item[1]}" obbligatorio="true"/> 
			<gene:campoScheda campo="MAXPUN_${param.contatore}" entita="GOEVMOD" campoFittizio="true" definizione="F8.3;0;;;G1MAXPUNM" value="${item[0]}" obbligatorio="true"/>
			<gene:campoScheda campo="LIVPAR_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="false" definizione="N7;0;;;G1LIVPARM" value="${item[2]}" />
</c:when>
<c:otherwise>
			<gene:campoScheda campo="IDCRIMOD_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="false" definizione="F8.3;0;;;G1IDCMOD" value="${idcrimod}" />
			<gene:campoScheda campo="NECVAN1_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="true" definizione="N7;0;;;G1NECVAN1M" />
			<gene:campoScheda campo="NORPAR1_${param.contatore}" entita="GOEVMOD" campoFittizio="true" definizione="F8.3;0;;;G1NORPAR1M" />
			<gene:campoScheda campo="DESPAR_${param.contatore}" entita="GOEVMOD" campoFittizio="true" definizione="T2000;0;;NOTE;G1DESPARM" obbligatorio="true"/> 
			<gene:campoScheda campo="MAXPUN_${param.contatore}" entita="GOEVMOD" campoFittizio="true" definizione="F8.3;0;;;G1MAXPUNM" obbligatorio="true"/>
			<gene:campoScheda campo="LIVPAR_${param.contatore}" entita="GOEVMOD" campoFittizio="true" visibile="true" definizione="N7;0;;;G1LIVPARM" />
			<gene:campoScheda campo="NORPAR_${param.contatore}" entita="GOEVMOD" campoFittizio="true"   visibile="true" definizione="F8.3;0;;;G1NORPARM" value="${id}"/>
</c:otherwise>
</c:choose>
