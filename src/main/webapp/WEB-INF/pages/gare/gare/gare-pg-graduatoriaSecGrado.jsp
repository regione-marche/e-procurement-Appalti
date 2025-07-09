<%
/*
 * Created on: 14-lug-2008
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

<c:set var="ngara" value='${gene:getValCampo(key,"NGARA")}'/>
<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, ngara)}' scope="request"/>
<c:set var="ngaraSecondoGradoConcluso" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select ngara from gare,torn where ngarapa=? and codgar=codgar1 and torn.iterga=10", ngara)}' />
	
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="CODGAR=T:$${ngaraSecondoGradoConcluso}"/>
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
</jsp:include>

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
</gene:redefineInsert>

<gene:redefineInsert name="listaNuovo" />
<gene:redefineInsert name="listaEliminaSelezione" />
<gene:redefineInsert name="documentiAzioni" />
<gene:redefineInsert name="addToAzioni" >
	<c:if test='${autorizzatoModifiche ne "2" and (meruolo eq 1 or meruolo eq 3) and isfineconc ne "1"}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:rimuoviAnonimato();" title='Rimuovi anonimato' tabindex="1507">
								Rimuovi anonimato
							</a>
						</td>
					</tr>
	</c:if>
</gene:redefineInsert>

<table class="dettaglio-tab-lista">
<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>
		<tr>
			<td >
			<c:set var="where" value="DITG.NGARA5 = '${ngaraSecondoGradoConcluso}' AND DITG.STAGGI>1"/>
				<gene:formLista entita="DITG" where="${where}" tableclass="datilista" sortColumn="-6;4" pagesize="${requestScope.risultatiPerPagina}" gestisciProtezioni="true" >
					
					<gene:campoLista campo="CODGAR5" visibile="false"  />
					<gene:campoLista campo="DITTAO"  visibile="false" />
					<gene:campoLista campo="NGARA5"  visibile="false"  />
					<gene:campoLista title="N.pl" campo="NUMORDPL" headerClass="sortable" width="32" />
					<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
					<gene:campoLista campo="NOMIMO" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda") and isfineconc eq "1", link, "")}' />
					<gene:campoLista title="Punteggio" width="130" campo="RIBAUO" headerClass="sortable"  definizione="F13.9;0;;;RIBAUO"/>
					<gene:campoLista campo="STAGGI" headerClass="sortable" />
					<c:choose>
						<c:when test="${ datiRiga.DITG_STAGGIALI eq 2 and datiRiga.DITG_STAGGI eq 3}">
							<c:set var="nomeImg" value="staggi3sup.png"/>
							<c:set var="desc" value="Anomala e ala superiore"/>
						</c:when>
						<c:when test="${ datiRiga.DITG_STAGGIALI eq 2 and datiRiga.DITG_STAGGI eq 6}">
							<c:set var="nomeImg" value="staggi6sup.png"/>
							<c:set var="desc" value="Non anomala e ala superiore"/>
						</c:when>
						<c:when test="${ datiRiga.DITG_STAGGIALI eq 7 and datiRiga.DITG_STAGGI eq 3}">
							<c:set var="nomeImg" value="staggi3inf.png"/>
							<c:set var="desc" value="Anomala e ala inferiore"/>
						</c:when>
						<c:when test="${ datiRiga.DITG_STAGGIALI eq 7 and datiRiga.DITG_STAGGI eq 6}">
							<c:set var="nomeImg" value="staggi6inf.png"/>
							<c:set var="desc" value="Non anomala e ala inferiore"/>
						</c:when>
						<c:otherwise>
							<c:set var="nomeImg" value="staggi${datiRiga.DITG_STAGGI}.png"/>
							<c:choose>
								<c:when test="${ datiRiga.DITG_STAGGI eq 1}">
									<c:set var="desc" value="Fuori limite"/>
								</c:when>
								<c:when test="${ datiRiga.DITG_STAGGI eq 2}">
									<c:set var="desc" value="Ala superiore"/>
								</c:when>
								<c:when test="${ datiRiga.DITG_STAGGI eq 3}">
									<c:set var="desc" value="Anomala"/>
								</c:when>
								<c:when test="${ datiRiga.DITG_STAGGI eq 4}">
									<c:set var="desc" value="Prima ditta classificata"/>
								</c:when>
								<c:when test="${ datiRiga.DITG_STAGGI eq 5}">
									<c:set var="desc" value="Seconda ditta classificata"/>
								</c:when>
								<c:when test="${ datiRiga.DITG_STAGGI eq 6}">
									<c:set var="desc" value="Non anomala"/>
								</c:when>
								<c:when test="${ datiRiga.DITG_STAGGI eq 7}">
									<c:set var="desc" value="Ala inferiore"/>
								</c:when>
								<c:when test="${ datiRiga.DITG_STAGGI eq 10}">
									<c:set var="desc" value="In graduatoria"/>
								</c:when>
							</c:choose>
							
						</c:otherwise>
					</c:choose>
					<gene:campoLista title="&nbsp;" width="20">
						<img width="16" height="16" alt= "Stato di aggiudicazione" src="${pageContext.request.contextPath}/img/${nomeImg}" title="${desc}" alt="${desc}"/>
					</gene:campoLista>
					<gene:campoLista campo="CONGRUO" headerClass="sortable" />
					
					<c:if test="${ not empty urlWsArt80 and urlWsArt80 ne '' and isfineconc eq '1'}">
						<jsp:include page="/WEB-INF/pages/gare/gare/gare-colonna-art80.jsp">
							<jsp:param name="ditta" value="${datiRiga.DITG_DITTAO }"/>
						</jsp:include>
					</c:if>
				</gene:formLista>
			</td>
		</tr>
		<c:if test='${autorizzatoModifiche ne "2" and (meruolo eq 1 or meruolo eq 3) and isfineconc ne "1"}'>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button"  class="bottone-azione" value="Rimuovi anonimato" title="Rimuovi anonimato" onclick="javascript:rimuoviAnonimato();">
					</td>
			</tr>
		</c:if>			 	
</table> 
<gene:javaScript>

	function rimuoviAnonimato() {
		var href="href=gare/gare/gare-popup-rimuoviAnonimato.jsp&ngara=${ngara}&tipoconc=2";
		openPopUpCustom(href, "rimuoviAnonimato", 700, 350, "yes", "yes");
	}
	
</gene:javaScript>