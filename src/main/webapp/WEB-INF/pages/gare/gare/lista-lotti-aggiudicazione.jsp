<%
/*
 * Created on: 11-01-2013
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="visualizzaPopUp" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}'/>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	

<c:choose>
	<c:when test="${param.tipoAggiudicazione eq 'provvisoria'}">
		<c:set var="titoloFunzione" value="Calcolo aggiudicazione lotto di gara"/>
	</c:when>
	<c:otherwise>
		<c:set var="titoloFunzione" value="Aggiudicazione definitiva lotto di gara"/>
	</c:otherwise>
</c:choose>

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
		
		<td>
			<gene:formLista entita="GARE" where="GARE.CODGAR1 = #TORN.CODGAR# AND (GARE.GENERE is null or GARE.GENERE <> 3) ${filtroLotti}" sortColumn="3" tableclass="datilista" gestisciProtezioni="true" >

				<gene:redefineInsert name="listaEliminaSelezione" />
				<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="pulsanteListaInserisci"/>
				
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp" >
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="${key}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				
				
				<gene:redefineInsert name="addToAzioni" >
					<c:if test='${param.tipoAggiudicazione eq "provvisoria"}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "ALT", "AnnullaCalcoloAggiudicazioneGare")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:apriPopupAnnullaAggiudicazione();" title='Annulla calcolo aggiudicazione' tabindex="1504">
										Annulla calcolo aggiudicazione
									</a>
								</td>
							</tr>
						</c:if>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "ALT", "CalcoloAggiudicazioneTuttiLotti")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:apriPopupCalcoloAggiudicazioneLotti();" title='Calcolo aggiudicazione su tutti i lotti' tabindex="1505">
										Calcolo aggiudicazione su tutti i lotti
									</a>
								</td>
							</tr>
						</c:if>
						<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InserisciOfferteDitteEscluse")}' >
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:inserisciOffertaDitteEscluse();" title='Inserisci offerte ditte escluse' tabindex="1506">
										Inserisci offerte <br>ditte escluse
									</a>
								</td>
							</tr>
						</c:if>
					</c:if>
					<tr>
						<td class="vocemenulaterale">
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaFiltroLotti")}'>	
								<a href="javascript:impostaFiltroLotti();" title='Imposta filtro' tabindex="1507">
									Imposta filtro
								</a>
							</c:if>
						</td>
					</tr>
					<c:if test='${param.tipoAggiudicazione eq "definitiva"}'>
						<tr>
							<td class="vocemenulaterale">
								<c:if test='${paginaAttivaWizard < 3}'>
									<a href="javascript:indietro();" title='Fase precedente' tabindex="1508">
										< Fase precedente
									</a>
								</c:if>
							</td>
						</tr>
						<tr>
							<td class="vocemenulaterale">
								<c:if test='${paginaAttivaWizard > 1}'>	
									<a href="javascript:avanti();" title='Fase seguente' tabindex="1509">
										Fase seguente >
									</a>
								</c:if>
							</td>
						</tr>
					</c:if>
				</gene:redefineInsert>
								
				<gene:campoLista title="Opzioni" width="50">
				<c:if test='${currentRow >= 0 && visualizzaPopUp}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${visualizzaPopUp && param.tipoAggiudicazione eq "provvisoria" && gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
								<gene:PopUpItem title="Calcolo aggiudicazione lotto di gara" href="javascript:calcoloAggiudicazioneLotto();" />
						</c:if>
						<c:if test='${visualizzaPopUp && param.tipoAggiudicazione eq "definitiva" && gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
								<gene:PopUpItem title="Aggiudicazione definitiva lotto di gara" href="javascript:aggiudicazioneLotto();" />
						</c:if>
					</gene:PopUp>
				</c:if>
				</gene:campoLista>
				<gene:campoLista campo="CODGAR1" headerClass="sortable" visibile="false" />

				<c:choose>
					<c:when test='${visualizzaPopUp && param.tipoAggiudicazione eq "provvisoria" && gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
						<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';calcoloAggiudicazioneLotto();"/>
					</c:when>
					<c:when test='${visualizzaPopUp && param.tipoAggiudicazione eq "definitiva" && gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
						<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';aggiudicazioneLotto();"/>
					</c:when>
					<c:otherwise>
						<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" />
					</c:otherwise>
				</c:choose>
				<gene:campoLista campo="CODIGA" title="Lotto" headerClass="sortable" width="50"/>
				<gene:campoLista campo="CODCIG" headerClass="sortable" />
				<gene:campoLista campo="NOT_GAR" headerClass="sortable" />
				<gene:campoLista campo="IMPAPP" headerClass="sortable" width="120" />
				<gene:campoLista campo="DITTA" visibile="false" />
				<gene:campoLista campo="DITTAP" visibile="false" />
				<gene:campoLista title="Fase aggiudicazione" width="150" ordinabile="false" >
				
					<c:choose>
						<c:when test="${empty datiRiga.GARE_DITTA and empty datiRiga.GARE_DITTAP}">
							&nbsp;
						</c:when>
						<c:when test="${empty datiRiga.GARE_DITTA and not empty datiRiga.GARE_DITTAP}">
							Proposta di aggiudicazione
						</c:when>
						<c:when test="${not empty datiRiga.GARE_DITTA}">
							Aggiudicazione
						</c:when>
					</c:choose>
				
				</gene:campoLista>
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
				
				<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
			</gene:formLista>
		</td>
	</tr>
		