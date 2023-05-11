
<%
	/*
	 * Created on 18-09-2014
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

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="ragioneSociale" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara,codiceGara,codiceDitta)}' />

<c:set var="bloccoPagina" value='${param.bloccoPagina}' />
<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />	
<c:if test="${garaInversa eq '1' }">
	<c:set var="dittAggaDef" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, numeroGara)}' />
	<c:if test="${empty dittAggaDef or dittAggaDef eq ''}">
		<c:set var="bloccoPagina" value='false' />
	</c:if>
</c:if>
	

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DITG-DETTAVV">
<gene:setString name="titoloMaschera" value="Dettaglio avvalimento della ditta ${ragioneSociale}" />
<gene:redefineInsert name="corpo">

<gene:formScheda entita="DITG" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITGAVVAL">
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>

	
	<gene:campoScheda campo="CODGAR5" visibile="false" />
	<gene:campoScheda campo="DITTAO" visibile="false" />
	<gene:campoScheda campo="NGARA5" visibile="false" />

	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetValoriDITGAVVALFunction" parametro="${key}" /> 
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='DITGAVVAL'/>
		<jsp:param name="chiave" value='${numeroGara};${codiceDitta}'/>
		<jsp:param name="nomeAttributoLista" value='datiDITGAVVAL' />
		<jsp:param name="idProtezioni" value="DITGAVVAL" />
		<jsp:param name="sezioneListaVuota" value="true" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/ditg/avvalimento-interno-scheda.jsp"/>
		<jsp:param name="arrayCampi" value="'DITGAVVAL_ID_','DITGAVVAL_NGARA_','DITGAVVAL_DITTAO_','DITGAVVAL_TIPOAV_','DITGAVVAL_DITTART_','IMPR_NOMEST1_','DITGAVVAL_DITTAAV_','IMPR_NOMEST2_','DITGAVVAL_CODCAT_','V_CAIS_TIT_DESCAT_','DITGAVVAL_NUMCLA_','DITGAVVAL_NOTEAV_'"/>
		<jsp:param name="arrayVisibilitaCampi" value="false, false, false, true, true, true, true, true, true, true, true, true" />
		<jsp:param name="titoloSezione" value="Avvalimento " />
		<jsp:param name="titoloNuovaSezione" value="Nuovo avvalimento" />
		<jsp:param name="descEntitaVociLink" value="avvalimento" />
		<jsp:param name="msgRaggiuntoMax" value="i avvalimenti"/>
		<jsp:param name="usaContatoreLista" value="true"/>
		<jsp:param name="numMaxDettagliInseribili" value="5"/>
		<jsp:param name="sezioneInseribile" value="true"/>
		<jsp:param name="sezioneEliminabile" value="true"/>
	</jsp:include>		
	
	

	<c:choose>
	<c:when test='${bloccoPagina ne true && autorizzatoModifiche ne 2}'>
		<gene:campoScheda>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>	
	</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>
	
</gene:formScheda>


</gene:redefineInsert>
</gene:template>





