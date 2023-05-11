
<%
	/*
	 * Created on 09-07-2010
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="bloccoPagina" value='${param.bloccoPagina}' />
<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />	
<c:if test="${garaInversa eq '1' }">
	<c:set var="dittAggaDef" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, numeroGara)}' />
	<c:if test="${empty dittAggaDef or dittAggaDef eq ''}">
		<c:set var="bloccoPagina" value='false' />
	</c:if>
</c:if>

<c:set var="where" value='RAGDET.NGARA = #DITG.NGARA5# AND RAGDET.CODIMP = #DITG.DITTAO#' />

<c:set var="ragioneSociale" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara,codiceGara,codiceDitta)}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="RAGDET-lista" schema="GENE">
	<gene:setString name="titoloMaschera" value="Elenco consorziate esecutrici della ditta ${ragioneSociale}" />
	<gene:setString name="entita" value="RAGDET" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
			
		<table class="lista">
			<tr>
				<td><gene:formLista entita="RAGDET" pagesize="20" tableclass="datilista" gestisciProtezioni="true" sortColumn="3" where="${where}">
					
					<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
						<jsp:param name="entita" value="V_GARE_TORN"/>
						<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
						<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
					</jsp:include>
					
					<c:choose>
						<c:when test='${(gene:checkProtFunz(pageContext, "DEL","DEL") || gene:checkProtFunz(pageContext, "DEL","DEL")) && bloccoPagina ne true && autorizzatoModifiche ne 2}'>
							<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
								<c:if test="${currentRow >= 0}">
									<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
									<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
										<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
											title="Elimina" />
									</c:if>
								</gene:PopUp>
								<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
									<input type="checkbox" name="keys" value="${chiaveRiga}" />
								</c:if>
								</c:if>
							</gene:campoLista>
						</c:when>	
						<c:otherwise>
							<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
						</c:otherwise>
					</c:choose>
					
					<c:set var="link" value='javascript:archivioImpresa("${datiRiga.RAGDET_CODDIC}");' />
					<gene:campoLista campo="CODIMP"  visibile="false"/>
					<gene:campoLista campo="CODDIC"  href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
					<gene:campoLista campo="NOMEST"  entita="IMPR" where="IMPR.CODIMP=RAGDET.CODDIC" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
					<gene:campoLista campo="CFIMP"  entita="IMPR" where="IMPR.CODIMP=RAGDET.CODDIC"/>
					<gene:campoLista campo="PIVIMP"  entita="IMPR" where="IMPR.CODIMP=RAGDET.CODDIC"/>
					<gene:campoLista campo="NUMDIC"  visibile="false"/>
					<gene:campoLista campo="NGARA"  visibile="false"/>
				</gene:formLista></td>
			</tr>
			
			<gene:redefineInsert name="listaNuovo">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && bloccoPagina ne true && autorizzatoModifiche ne 2}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:aggiungiDitte('${codiceDitta}','${numeroGara}');" title="Aggiungi ditte del consorzio" tabindex="1501">
								Aggiungi ditte del consorzio</a></td>
					</tr>
				</c:if>
			</gene:redefineInsert>
			
			<c:if test='${bloccoPagina eq true || autorizzatoModifiche ne 2}'>
				<gene:redefineInsert name="listaEliminaSelezione"/>
			</c:if>
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && bloccoPagina ne true && autorizzatoModifiche ne 2}'>
						<INPUT type="button"  class="bottone-azione" value='Aggiungi ditte del consorzio' title='Aggiungi ditte del consorzio' onclick="aggiungiDitte('${codiceDitta}','${numeroGara}');">&nbsp;&nbsp;&nbsp;
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && bloccoPagina ne true && autorizzatoModifiche ne 2}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">
					</c:if>
				</td>
			</tr>
			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		function aggiungiDitte(codiceDitta,numeroGara){
			openPopUpCustom("href=gene/ragdet/ragimp-ragdet-lista-ditte-popup.jsp&codimp=" + codiceDitta + "&ngara=" + numeroGara, "aggiungiDitteRaggruppamento", 850, 500, "yes", "yes");
		}
		
		function archivioImpresa(codiceImpresa){
			var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
			document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
		}
</gene:javaScript>
</gene:template>