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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera='GARE-scheda-contratto'>
	
        <c:choose>
		<c:when test='${fn:contains(key,"GARECONT")}'>
			<c:set var="codiceGara" value='${gene:getValCampo(key,"NGARA")}'/>
			<c:set var="ncont" value='${gene:getValCampo(key,"NCONT")}'/>
			<c:set var="dittao" value='${param.codimp}'/>
			<c:choose>
				<c:when test="${param.modcont eq '1' }">
					<c:set var="ngara" value='${param.ngaral}' />
				</c:when>
				<c:otherwise>
					<c:set var="ngara" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EstrazioneLottoAggiudicatoFunction", pageContext, codiceGara,dittao)}' />
				</c:otherwise>
			</c:choose>
			
			<c:set var="key" value='GARE.NGARA=T:${ngara}' scope="request" />
			<c:if test="${param.isAccordoQuadro eq '1' }">
				<c:set var="esecscig" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetEsecscigFunction", pageContext, codiceGara,ncont)}'/>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:set var="codiceGara" value=''/>
			<c:set var="dittao" value=''/>
			<c:set var="ngara" value='${gene:getValCampo(key,"NGARA")}' />
			<c:if test="${param.isAccordoQuadro eq '1' }">
				<c:set var="esecscig" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetEsecscigFunction", pageContext, param.codcont,param.ncont)}'/>
			</c:if>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${param.isAccordoQuadro eq '1' and (empty esecscig or esecscig eq '' or esecscig ne '1')}">
		<c:set var="visualizzaControlloSpesa" value="true"/>
	</c:if>

			
	<c:set var="tipoContratto" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlliVisualizzazionePaginaContrattoFunction", pageContext, ngara,"numeroGara")}' />
	
	<c:set var="ragioneSociale" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,ngara,codiceGara,dittao)}' />
	
	<c:set var="stringaTitolo" value='della ditta ${ragioneSociale}'/>
	<c:if test="${param.modcont eq '1'}">
		<c:set var="stringaTitolo" value='del lotto ${ngara}'/>
	</c:if>
			
	<c:choose>
		<c:when test="${tipoContratto eq 'stipula' }">
			<gene:setString name="titoloMaschera" value='Stipula accordo quadro ${stringaTitolo}'/>
		</c:when>
		<c:when test="${tipoContratto eq 'aggEff' }">
			<gene:setString name="titoloMaschera" value='Aggiudicazione efficace ${stringaTitolo}'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Contratto ${stringaTitolo}'/>
		</c:otherwise>
	</c:choose>
	
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
	
	
	
	<gene:redefineInsert name="corpo">
	  	<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Dati generali" idProtezioni="${codiceProtezione }">
				<jsp:include page="${paginaInclusa }" />
			</gene:pagina>
			<gene:pagina title="Lotti aggiudicati" idProtezioni="LOTTIAGGIUD" visibile="${param.modcont eq '2'}">
				<jsp:include page="gare-pg-lottiAggiudicati.jsp" />
			</gene:pagina>
			<gene:pagina title="Controllo spesa" idProtezioni="SPESA" visibile="${visualizzaControlloSpesa eq 'true'}">
				<jsp:include page="gare-pg-listaControlloSpesa.jsp" />
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>
