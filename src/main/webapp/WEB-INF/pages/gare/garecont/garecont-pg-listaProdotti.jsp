<%
/*
 * Created on: 12/06/2014
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" scope="request"/>
	</c:when>
	<c:when test='${not empty requestScope.id}'>
		<c:set var="id" value="${requestScope.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:set var="ngara" value='${gene:getValCampo(key, "GARECONT.NGARA")}' />
<c:set var="where" value="V_ODAPROD.NGARA = '${ngara }' and  V_ODAPROD.IDRIC=${id }"/>

<table class="dettaglio-tab-lista">
	<jsp:include page="/WEB-INF/pages/gare/commons/v_odaprod-internoLista.jsp">
		<jsp:param name="where" value="${where }"/>
		<jsp:param name="whereConteggio" value="${where }"/>
	</jsp:include>
</table>

<gene:javaScript>

	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var id="${id }"
		document.pagineForm.action += "&id=" + id;
		selezionaPaginaDefault(pageNumber);
	}

</gene:javaScript>

