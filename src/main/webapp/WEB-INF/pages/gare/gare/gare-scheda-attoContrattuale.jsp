<%/*
       * Created on 02-dic-2009
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


<c:set var="ngara" value='${gene:getValCampo(key,"NGARA")}' />
	
   <c:set var="tipoContratto" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlliVisualizzazionePaginaContrattoFunction", pageContext, ngara,"numeroGara")}' />



<c:choose>
	<c:when test="${tipoContratto eq 'stipula' }">
		<c:set var="codiceProtezione" value='STIPULA'/>
		<c:set var="paginaInclusa" value='gare-pg-stipula-accordo-quadro-OffertaUnica.jsp'/>
	</c:when>
	<c:when test="${tipoContratto eq 'aggEff' }">
		<c:set var="codiceProtezione" value='AGGEFF'/>
		<c:set var="paginaInclusa" value='gare-pg-aggiudicazione-efficace-OffertaUnica.jsp'/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceProtezione" value='ATTOCONTR'/>
		<c:set var="paginaInclusa" value='gare-pg-attoContrattuale.jsp'/>
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera='GARE-scheda-contratto.${codiceProtezione }'>

	<c:choose>
		<c:when test="${tipoContratto eq 'stipula' }">
			<gene:setString name="titoloMaschera" value='Stipula accordo quadro del lotto ${ngara}'/>
		</c:when>
		<c:when test="${tipoContratto eq 'aggEff' }">
			<gene:setString name="titoloMaschera" value='Aggiudicazione efficace del lotto ${ngara}'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Contratto del lotto ${ngara}'/>
		</c:otherwise>
	</c:choose>	
	
	<gene:redefineInsert name="corpo">
		<jsp:include page="${paginaInclusa }" />
	</gene:redefineInsert>
</gene:template>
