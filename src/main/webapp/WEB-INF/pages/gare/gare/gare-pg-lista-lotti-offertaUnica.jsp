<%
/*
 * Created on: 19-ott-2007
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

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
<gene:redefineInsert name="listaNuovo" />
<gene:redefineInsert name="pulsanteListaInserisci"/>

<c:set var="codiceGara" value='${gene:getValCampo(key, "TORN.CODGAR")}' />
<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara }" />
	<jsp:param name="filtroCampoEntita" value="codgar = '${codiceGara}'" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"TORN.CODGAR"))}' scope="request"/>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<table class="arealayout">
				<tr>
					<td>
						<br><b>ATTENZIONE:</b>&nbsp;
						L'apertura delle offerte e il calcolo dell'aggiudicazione procede sui singoli lotti della gara elencati nella lista sottostante. Cliccare sul 'Codice lotto' per accedere alla gestione delle fasi di ogni lotto.
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<c:if test="${!empty filtroLotti }">
		<tr>
			<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
			 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span>
			 <c:if test='${updateLista ne 1}'>
				 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro(7);" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
				 <a class="link-generico" href="javascript:AnnullaFiltro(7);">Cancella filtro</a> ]
			 </c:if>
			</td>
		</tr>
	</c:if>
	<tr>
		<td>
			<gene:formLista entita="GARE" where="GARE.CODGAR1 = #TORN.CODGAR# AND GARE.NGARA <>#TORN.CODGAR# ${filtroLotti}" sortColumn="2" tableclass="datilista" pagesize="25" gestisciProtezioni="true" >
				<gene:redefineInsert name="addToAzioni" >
					<c:if test="${isProceduraTelematica eq 'true' && meruolo eq '1'  && autorizzatoModifiche ne 2 && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AnnullaAperturaOfferte')}">
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:AnnullaAperturaOfferte('${key}','${bustalotti }');" title='Annulla apertura offerte' tabindex="1511">
									Annulla apertura offerte
								</a>
							</td>
						</tr>
					</c:if>
					<tr>
						<td class="vocemenulaterale">
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaFiltroLotti")}'>	
								<a href="javascript:impostaFiltroLotti();" title='Imposta filtro' tabindex="1512">
									Imposta filtro
								</a>
							</c:if>
						</td>
					</tr>
				</gene:redefineInsert>
				
				<gene:campoLista title="Opzioni" width="50">
						
				<c:if test='${currentRow >= 0}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:set var="link" value="" />
						<c:if test='${gene:checkProt(pageContext, "PAGE.VIS.GARE.GARE-scheda.FASIGARA") && (isProceduraTelematica eq "false" || (isProceduraTelematica eq "true" && not empty datiRiga.GARE_FASGAR ))}' >
							<gene:PopUpItem title="Apertura offerte e calcolo aggiudicazione del lotto" href="javascript:aperturaOfferte()" />
							<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';aperturaOfferte();" />
						</c:if>
					</gene:PopUp>
				</c:if>
				</gene:campoLista>
				<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" href="${link}"/>
				<gene:campoLista campo="CODIGA" title="Lotto" headerClass="sortable" width="50"/>
				<gene:campoLista campo="CODCIG" headerClass="sortable" />
				<gene:campoLista campo="NOT_GAR" headerClass="sortable"/>
				<gene:campoLista campo="IMPAPP" width="120" headerClass="sortable"  />
				<gene:campoLista campo="FASGAR"  width="150" headerClass="sortable" />
				<gene:campoLista campo="MODLICG"  visibile="false" />
				<gene:campoLista campo="CODGAR1"  visibile="false" />
				<gene:campoLista campo="VALTEC"  entita = "GARE1" where= "GARE1.NGARA = GARE.NGARA" visibile="false" />
				<gene:campoLista campo="ESINEG" visibile="false" />
				<gene:campoLista title="Lotto annullato?" width="50" ordinabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.ESINEG")}'>
				
					<c:choose>
						<c:when test="${not empty datiRiga.GARE_ESINEG}">
							Si
						</c:when>
						<c:otherwise>
							&nbsp;
						</c:otherwise>
					</c:choose>
				
				</gene:campoLista>
			</gene:formLista>
		</td>
	</tr>
</table>

<gene:javaScript>
	function aperturaOfferte(){
		document.location.href=contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-pg-aperturaOffAggiudProvLotti.jsp&key=" + chiaveRiga + "&paginaFasiGara=aperturaOffAggProvLottoOffUnica&LottoplicoUnico=true&idconfi=${idconfi}";
	}
</gene:javaScript>