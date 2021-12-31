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


<c:if test="${param.tipoDettaglio eq 1}">
	<gene:campoScheda campo="ID_${param.contatore}" entita="MEALLISCRIZPROD" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
	<gene:campoScheda campo="IDISCRIZPROD_${param.contatore}" entita="MEALLISCRIZPROD" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[1]}" />
	<gene:campoScheda campo="IDPRG_${param.contatore}" entita="MEALLISCRIZPROD" campoFittizio="true"  visibile="false" definizione="T2;0" value="${item[2]}" />
	<gene:campoScheda campo="IDDOCDIG_${param.contatore}" entita="MEALLISCRIZPROD" campoFittizio="true"  visibile="false" definizione="N12;0" value="${item[3]}" />
	<c:choose>
		<c:when test="${item[5] eq '1'}">
			<c:set var="titolo" value="Immagine"/>
		</c:when>
		<c:when test="${item[5] eq '2'}">
			<c:set var="titolo" value="Certificazione n.${param.contatore}"/>
		</c:when>
		<c:otherwise>
			<c:set var="titolo" value="Scheda tecnica n.${param.contatore}"/>
		</c:otherwise>
	</c:choose>
	
	<gene:campoScheda title="${titolo }" campo="DIGNOMDOC_${param.contatore}" entita="W_DOCDIG" modificabile="false" campoFittizio="true" definizione="T100;0"  value="${item[4]}" href="javascript:visualizzaFileAllegato('${item[2]}','${item[3]}','${item[4]}');"/>
</c:if>



