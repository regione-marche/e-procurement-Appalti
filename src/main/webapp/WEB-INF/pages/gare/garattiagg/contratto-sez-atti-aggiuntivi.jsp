<%
/*
 * Created on: 13/11/2006
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GARATTIAGG'/>
		<jsp:param name="chiave" value='${param.ngara};${param.ncont}'/>
		<jsp:param name="nomeAttributoLista" value='attiAggiuntiviContratto' />
		<jsp:param name="idProtezioni" value="GARATTIAGG" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garattiagg/atto-aggiuntivo-contratto.jsp"/>
		<jsp:param name="arrayCampi" value="'GARATTIAGG_ID_', 'GARATTIAGG_NGARA_', 'GARATTIAGG_NCONT_', 'GARATTIAGG_NREPAT_', 'GARATTIAGG_DAATTO_','GARATTIAGG_NIMPCO_','GARATTIAGG_TIATTO_'"/>
		<jsp:param name="sezioneListaVuota" value="false" />
		<jsp:param name="titoloSezione" value="Atto aggiuntivo" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo atto aggiuntivo" />
		<jsp:param name="descEntitaVociLink" value="atto aggiuntivo" />
		<jsp:param name="msgRaggiuntoMax" value="i atti aggiuntivi"/>
		<jsp:param name="usaContatoreLista" value="true"/>
		<jsp:param name="numMaxDettagliInseribili" value="5"/>
		<jsp:param name="sezioneInseribile" value="true"/>
		<jsp:param name="sezioneEliminabile" value="true"/>
		<jsp:param name="funzEliminazione" value="delAttoAgg"/>
	</jsp:include>
