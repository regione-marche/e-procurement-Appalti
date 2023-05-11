<%/*
   * Created on 15-lug-2008
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
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GAREAVVISI-scheda">
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"GAREAVVISI")}' />
	<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, gene:getValCampo(key,"NGARA"))}' />
	<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codgar)}' scope="request"/>
	<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true" >
		
			<c:set var="tipologiaGara" value='11' scope="request"/>
			
			<gene:pagina title="Dati generali" idProtezioni="DATIGEN">
				<jsp:include page="gareavvisi-datigen.jsp" />
			</gene:pagina>
			<gene:pagina title="Documentazione" idProtezioni="DOC">
				<jsp:include page="gareavvisi-pg-documentazione.jsp" />
			</gene:pagina>
			<gene:pagina title="Pubblicità avviso" idProtezioni="PUBBLI" >
				<jsp:include page="gareavvisi-pg-pub-bando.jsp" />
			</gene:pagina>
			
			
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>	