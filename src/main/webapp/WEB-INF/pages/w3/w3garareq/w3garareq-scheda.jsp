<%/*
       * Created on 28-Mar-2008
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/funzioniIDGARACIG.js"></script>

<c:set var="entita" value="W3GARAREQ" />
<c:set var="numgara" value='${gene:getValCampo(key,"NUMGARA")}' scope="request" />
<c:set var="numreq" value='${gene:getValCampo(key,"NUMREQ")}' scope="request" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W3GARAREQ-scheda" schema="W3">
	<c:set var="entita" value="W3GARAREQ" />
	
	<c:choose>
		<c:when test='${modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value="Nuovo requisito" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Requisito N. ${numreq}"/>	
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Dati del requisito" idProtezioni="W3GARAREQ">
				<jsp:include page="w3garareq-pg-w3garareq.jsp" />
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
	
<gene:redefineInsert name="addToDocumenti" >
	<jsp:include page="/WEB-INF/pages/w3/commons/addtodocumenti-requisiti-gara.jsp" />
</gene:redefineInsert>

</gene:template>
