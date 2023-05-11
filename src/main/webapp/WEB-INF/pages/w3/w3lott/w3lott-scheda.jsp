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

<c:set var="entita" value="W3LOTT" />
<c:set var="numgara" value='${gene:getValCampo(key,"NUMGARA")}' scope="request" />
<c:set var="numlott" value='${gene:getValCampo(key,"NUMLOTT")}' scope="request" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W3LOTT-scheda" schema="W3">

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/funzioniIDGARACIG.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.nuts.mod.js"></script>
	</gene:redefineInsert>

	<c:set var="entita" value="W3LOTT" />
	
	<c:choose>
		<c:when test='${modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value="Nuovo lotto" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.GetTitleW3LOTTFunction",pageContext)}'/>	
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Dati del lotto" idProtezioni="W3LOTT">
				<jsp:include page="w3lott-pg-w3lott.jsp" />
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="addToAzioni" >
		<jsp:include page="/WEB-INF/pages/w3/commons/addtodocumenti-idgaracig.jsp" >
			<jsp:param name="entita" value='W3LOTT'/>
		</jsp:include>
	</gene:redefineInsert>

</gene:template>
