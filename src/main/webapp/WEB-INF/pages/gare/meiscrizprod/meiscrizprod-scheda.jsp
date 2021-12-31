<%
/*
 * Created on: 06/12/2013
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MEISCRIZPROD-scheda">
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "MEISCRIZPROD")}'/>
	
	
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<c:if test='${param.daListaProdotti eq 1}'>
			<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
		</c:if>
		<gene:redefineInsert name="head">
                <script type="text/javascript" src="${contextPath}/js/date.js"></script>
         </gene:redefineInsert>
		<gene:formScheda entita="MEISCRIZPROD" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniMEISCRIZPROD" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMEISCRIZPROD">
			
			<jsp:include page="/WEB-INF/pages/gare/meiscrizprod/meiscrizprod-interno-scheda.jsp"/>
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.MEISCRIZPROD_CODGAR}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		
		document.forms[0].encoding="multipart/form-data";
				
		function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
			
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}

		function archivioImpresa(){
			var codiceImpresa = getValue("MEISCRIZPROD_CODIMP");
			var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
			document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
		}
		
		<c:if test="${modo eq 'VISUALIZZA'}">
			$('[id^="rowtitoloMEALLARTCAT_"]').hide();
			$('[id^="rowtitoloIMMAGINE_"]').hide();
			$('[id^="rowtitoloCERTIFICAZIONI_"]').hide();
			$('[id^="rowtitoloSCHEDE_"]').hide();
		</c:if>
		
	</gene:javaScript>
</gene:template>