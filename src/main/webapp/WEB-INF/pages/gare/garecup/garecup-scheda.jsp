<%
/*
 * Created on: 07/03/2019
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


<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="T20;0" value="${item[1]}" />
		<gene:campoScheda campo="CUP_${param.contatore}" entita="GARECUP"  campoFittizio="true"  title="Codice CUP di progetto" definizione="T15;0"  value="${item[2]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="N12;1"  />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="T20;0"  value="${param.chiave }"/>
		<gene:campoScheda campo="CUP_${param.contatore}" entita="GARECUP"  campoFittizio="true"  title="Codice CUP di progetto" definizione="T15;0" />
	</c:otherwise>
</c:choose>


<gene:javaScript>
	<c:if test='${modo eq "MODIFICA" }'>
		$("#GARECUP_CUP_${param.contatore}").on('change', function() {
		 	validazioneCup(this);
		});
	</c:if>
</gene:javaScript>