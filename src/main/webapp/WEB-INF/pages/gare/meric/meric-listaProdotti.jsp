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

<c:set var="where" value='V_ODAPROD.IDRIC = #V_ODAPROD.IDRIC# '/>
<c:set var="idric" value='${gene:getValCampo(key, "V_ODAPROD.IDRIC")}' />
<c:set var="whereConteggio" value='V_ODAPROD.IDRIC = ${idric } '/>
<c:set var="ngara" value='${gene:getValCampo(key, "V_ODAPROD.NGARA")}' />

<c:choose>
	<c:when test="${ngara != null && ngara != '' }">
		<c:set var="where" value='${where} and V_ODAPROD.NGARA = #V_ODAPROD.NGARA# '/>
		<c:set var="whereConteggio" value="${whereConteggio} and V_ODAPROD.NGARA = '${ngara }'"/>
	</c:when>
	<c:otherwise>
		<c:set var="where" value='${where} and V_ODAPROD.CODIMP = #V_ODAPROD.CODIMP# '/>
		<c:set var="codimp" value='${gene:getValCampo(key, "V_ODAPROD.CODIMP")}' />
		<c:set var="whereConteggio" value="${whereConteggio} and V_ODAPROD.CODIMP = '${codimp }'"/>
	</c:otherwise>
</c:choose>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MERIC-listaOrdini" >
	<gene:redefineInsert name="head">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery/jquery.tooltip.css" />
		<script src="${pageContext.request.contextPath}/js/jquery.tooltip.js" type="text/javascript"></script>	
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Lista prodotti dell'ordine di acquisto ${ngara}"/>
	<gene:redefineInsert name="corpo">
		<table class="lista">
			<jsp:include page="/WEB-INF/pages/gare/commons/v_odaprod-internoLista.jsp">
				<jsp:param name="where" value="${where }"/>
				<jsp:param name="whereConteggio" value="${whereConteggio }"/>
			</jsp:include>
		</table>
	</gene:redefineInsert>
</gene:template>

