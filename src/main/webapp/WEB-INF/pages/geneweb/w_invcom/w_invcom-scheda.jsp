<%/*
       * Created on 04-Giu-2010
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

<c:set var="configInvioCC" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "comunicazione.inviocopiaconoscenza") eq "1"}'/>

<c:if test='${modo eq "VISUALIZZA" or modo eq "MODIFICA" or empty modo}' >
	<gene:sqlSelect nome="tipoEvidenza" parametri='${key}' tipoOut="VectorString" >
		select COMPUB from W_INVCOM where IDPRG=#W_INVCOM.IDPRG# and IDCOM= #W_INVCOM.IDCOM#
	</gene:sqlSelect>
</c:if>
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_INVCOM-scheda" schema="GENEWEB">

	<c:set var="entita" value="W_INVCOM" />
	<c:set var="idconfi" value='${param.idconfi}' scope="request"/>
	<c:set var="stepWizard" value='${param.stepWizard}' scope="request"/>
	<c:choose>
		<c:when test="${!empty keyParentComunicazioni}">
			<c:set var="entitaParent" value='${fn:substringBefore(keyParentComunicazioni,".")}' scope="request" />
		</c:when>
		<c:when test="${!empty keyParent}">
			<c:set var="entitaParent" value='${fn:substringBefore(keyParent,".")}' scope="request" />
			<c:set var="keyParentComunicazioni" value="${keyParent}" scope="session" />
		</c:when>
	</c:choose>
	<c:if test="${entitaParent eq 'GAREAVVISI' }">
		<c:set var="entitaParent" value='GARE' scope="request" />
	</c:if>
	
	<c:if test='${modo eq "VISUALIZZA" or modo eq "MODIFICA" or empty modo}' >
		<c:set var="idprg" value='${gene:getValCampo(key,"IDPRG")}'/>
		<c:set var="idcom" value='${gene:getValCampo(key,"IDCOM")}'/>
		<c:set var="where" value="IDPRG='${idprg }' and IDCOM=${idcom }"/>
		<c:set var="commodello" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "COMMODELLO","W_INVCOM",where)}' scope="request"/>
	</c:if>
			
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleComunicazioneFunction", pageContext, "W_INVCOM")}' />
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
	<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>

	
	<gene:redefineInsert name="modelliPredisposti" />
	<gene:redefineInsert name="documentiAssociati" />
	<gene:redefineInsert name="noteAvvisi" />
		
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Dati generali" idProtezioni="W_INVCOM">
				<jsp:include page="w_invcom-pg-datigen.jsp" >
					<jsp:param name="idconfi" value="${idconfi}" />
					<jsp:param name="stepWizard" value="${stepWizard}" />  
				</jsp:include>
			</gene:pagina>
			<gene:pagina title="Soggetti destinatari" idProtezioni="W_INVCOMDES" selezionabile='${(tipoEvidenza[0] ne "1")}'>
				<jsp:include page="w_invcom-pg-lista-w_invcomdes.jsp" >
					<jsp:param name="idconfi" value="${idconfi}" /> 
				</jsp:include>
			</gene:pagina>
			<gene:pagina title="Soggetti destinatari in CC" idProtezioni="W_INVCOMDESCC" visibile='${configInvioCC && !(abilitatoInvioMailDocumentale && integrazioneWSDM eq "1")}' selezionabile='${(tipoEvidenza[0] ne "1")}'>

				<jsp:include page="w_invcom-pg-lista-w_invcomdescc.jsp" >
					<jsp:param name="idconfi" value="${idconfi}" /> 
				</jsp:include>
			</gene:pagina>					
			<gene:pagina title="Documenti richiesti" idProtezioni="G1DOCSOC" visibile='${(commodello eq "1")}'>
				<jsp:include page="w_invcom-pg-lista-g1docsoc.jsp" >
					<jsp:param name="idconfi" value="${idconfi}" />
				</jsp:include>
			</gene:pagina>			
			<gene:pagina title="Allegati" idProtezioni="W_DOCDIG">
				<jsp:include page="w_invcom-pg-lista-w_docdig.jsp" >
					<jsp:param name="idconfi" value="${idconfi}" /> 
				</jsp:include>
			</gene:pagina>	
		</gene:formPagine>
	</gene:redefineInsert>

</gene:template>
