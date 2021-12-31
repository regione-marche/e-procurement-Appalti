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

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="COMMVERB" campoFittizio="true" visibile="false" definizione="T20;1;;;CVERBNGARA" value="${item[0]}" />
		<gene:campoScheda campo="NUM_${param.contatore}" entita="COMMVERB" campoFittizio="true" visibile="false" definizione="N3;1;;;CVERBNUM" value="${item[1]}" />
		<gene:campoScheda campo="DVERB_${param.contatore}" entita="COMMVERB" campoFittizio="true" definizione="D;0;;DATA_ELDA;CVERBDVERB" value="${item[2]}" />
		<gene:campoScheda campo="NOTE_${param.contatore}" entita="COMMVERB" campoFittizio="true" definizione="T2000;0;;NOTE;CVERBNOTE" value="${item[3]}"  />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="COMMVERB" campoFittizio="true" visibile="false" definizione="T20;1;;;CVERBNGARA" />
		<gene:campoScheda campo="NUM_${param.contatore}" entita="COMMVERB" campoFittizio="true" visibile="false" definizione="N3;1;;;CVERBNUM" />
		<gene:campoScheda campo="DVERB_${param.contatore}" entita="COMMVERB" campoFittizio="true" definizione="D;0;;DATA_ELDA;CVERBDVERB" />
		<gene:campoScheda campo="NOTE_${param.contatore}" entita="COMMVERB" campoFittizio="true" definizione="T2000;0;;NOTE;CVERBNOTE" />
	</c:otherwise>
</c:choose>

