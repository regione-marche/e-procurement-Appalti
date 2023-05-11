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

<c:if test='${updateLista eq 1}'>
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request"/>
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W3GARA-scheda" schema="W3">
	<c:set var="entita" value="W3GARA" />
	<c:set var="numgara" value='${gene:getValCampo(key,"NUMGARA")}' scope="request" />
	<c:choose>
		<c:when test='${modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value="Nuova gara" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.GetTitleW3GARAFunction",pageContext)}'/>
			<c:set var="avcpass" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetAvcpassFunction", pageContext, key)}' scope="request"/>
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Dati della gara" idProtezioni="W3GARA">
				<jsp:include page="w3gara-pg-w3gara.jsp" />
			</gene:pagina>
			<gene:pagina title="Lista dei lotti" idProtezioni="W3LOTT">
				<jsp:include page="w3gara-pg-lista-w3lott.jsp" />
			</gene:pagina>
			<c:if test="${empty avcpass or avcpass=='2'}">
				<gene:pagina title="Lista dei requisiti" idProtezioni="W3REQ">
					<jsp:include page="w3gara-pg-lista-w3garareq.jsp" />
				</gene:pagina>
			</c:if>
			<gene:pagina title="Pubblicazione" idProtezioni="W3GARADOC">
				<jsp:include page="w3gara-pg-w3gara-bando.jsp" />
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>


