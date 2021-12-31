<%
/*
 * Created on 15-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE IL CODICE PER GENERARE LA TESTATA INFORMATIVA SUL PRODOTTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaSingolaBreveParametrizzata">
	<fmt:message key="label.tags.uffint.singoloBreve" />
</c:set>

<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="profiloUtente" value="${sessionScope.profiloUtente}" scope="request"/>
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="IsProfiloRDOFunction" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsProfiloRDOFunction", pageContext)}' />

	<div class="banner">
		<c:if test='${(! empty moduloAttivo) and (! empty profiloUtente) and (isNavigazioneDisattiva ne "1")}' >
			<a href="javascript:goHome('${moduloAttivo}');" title="Torna alla homepage" tabindex="10">
		</c:if>
		
		<c:choose>
			<c:when test='${empty profiloUtente}'>
				<c:set var="nomeImmagine" value="banner_logo.png"/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}'>
				<c:set var="nomeImmagine" value="banner_logo_ElenchiOperatori.png"/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare") || gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato") || IsProfiloRDOFunction == "true"}'> 
				<c:set var="nomeImmagine" value="banner_logo_MercatoElettronico.png"/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista")}'>
				<c:set var="nomeImmagine" value="banner_logo_AppaltiAffidamenti.png"/>
			</c:when>
			<c:otherwise>
				<c:set var="nomeImmagine" value="banner_logo.png"/>
			</c:otherwise>
		</c:choose>
		
		<img src="${contextPath}/img/${nomeImmagine}" alt="Torna alla homepage" title="Torna alla homepage">
		
		<c:if test='${(! empty moduloAttivo) and (! empty profiloUtente) and (isNavigazioneDisattiva ne "1")}' >
			</a>
		</c:if>
	</div>

	<script type="text/javascript">
	//funzione richiamata dal jquery w_message per il download della documentazione gare. Lo script deve essere presente in tutte le pagine
	function downloadExportDocumenti(id){
		document.location.href='pg/DownloadExportDocumenti.do?'+csrfToken+'&id=' + id;
	}
	</script>