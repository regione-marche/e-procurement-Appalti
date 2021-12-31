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


<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:when test='${not empty requestScope.id}'>
		<c:set var="id" value="${requestScope.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<gene:formScheda entita="GARECONT" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdine">

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>

	<gene:campoScheda campo="NGARA" visibile="false" />		
	<gene:campoScheda campo="NCONT" visibile="false" />

				
	<gene:gruppoCampi idProtezioni="GARELIQ">
		<gene:campoScheda>
			<td colspan="2"><b>Dati di esecuzione contratto rif. L. 190/2012</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DVERBC"/>
		<gene:campoScheda campo="DCERTU"/>
		<gene:campoScheda campo="IMPLIQ"/>
	</gene:gruppoCampi>
	
	<input type="hidden" name="id" id="id" value="${id}">
	
	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="MERIC"/>
					<jsp:param name="inputFiltro" value="ID=N:${id}"/>
					<jsp:param name="filtroCampoEntita" value="IDMERIC=${id }"/>
		</jsp:include>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
</gene:formScheda>
<gene:javaScript>
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var id="${id }"
		document.pagineForm.action += "&id=" + id;
		selezionaPaginaDefault(pageNumber);
	}
</gene:javaScript>

