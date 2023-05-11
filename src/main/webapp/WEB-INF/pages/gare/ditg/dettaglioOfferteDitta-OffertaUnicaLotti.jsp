<%
/*
 * Created on: 25-nov-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* Pagina per visualizzazione/inserimento dell'offerta tecnica/economica di
  * una ditta per i diversi lotti per le gare a lotti con offerta unica, accessibile
  * dalle fasi offerta economica e offerta tecnica delle fasi di gara.
  *
  * Questa pagina e' stata ispirata alle fasi di gara (gare-pg-fasiGara.jsp) della
  * quale riprende:
  * - la logica per l'inizializzazione della lista;
  * - la logica del gestore di salvataggio;
  * - molto codice JS per aggiornamento dei campi;
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<c:choose>
	<c:when test='${not empty param.visOffertaEco}'>
		<c:set var="visOffertaEco" value="${param.visOffertaEco}" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="visOffertaEco" value="${visOffertaEco}" scope="request"/>
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneOfferteTecnicheEconomicheDittaFunction" parametro="${key}" />

<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="ditta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<jsp:include page="../gare/fasiGara/defStepWizardFasiGara.jsp" />



<% // Set di una variabile temporanea per costruire il valore delle stringhe     %>
<% // strProtVisualizzaFasiGara e strProtModificaFasiGara in funzione dello step %>
<% // del wizard attivo. Questa variabile e' stata introdotta per non modificare %>
<% // i record presenti nella tabella W_OGGETTI (e tabelle collegate W_AZIONI e  %>
<% // W_PROAZI e di tutti di i profili esistenti) in seguito all'introduzione di %>
<% // nuovi step nel wizard fasi di gara %>

<c:set var="varTmp" value="${paginaAttivaWizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:set var="strProtModificaFasiGara" value="FUNZ.VIS.ALT.GARE.DITG-OFFUNICA.MOD-FASE${varTmp}" />

<c:set var="filtroValutazione" value="${param.filtroValutazione}"  scope="request"/>

<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.gare.note")}' scope="request"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DITG-OFFUNICA">
	<% // Settaggio delle stringhe utilizzate nel template %>


<c:choose>
	<c:when test='${paginaAttivaWizard eq step6Wizard and isOffertaPerLotto ne "true"}'>
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"OFFERTE_TECNICHE_DITTA")}' />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step1Wizard and isOffertaPerLotto ne "true"}'>
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"VERIFICA_CONFORMITA_DITTA")}' />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step6Wizard and isOffertaPerLotto eq "true"}'>
		<gene:setString name="titoloMaschera" value='Valutazione tecnica del lotto ${numeroGara }' />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step7Wizard and isOffertaPerLotto eq "true"}'>
		<gene:setString name="titoloMaschera" value='Valutazione economica del lotto ${numeroGara }' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"OFFERTE_ECONOMICHE_DITTA")}' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${paginaAttivaWizard eq step6Wizard && isGaraLottiConOffertaUnica}' >
		<c:set var="dettaglioTitle" value="Verifica conformità lavorazioni e forniture" />
		<c:set var="dettaglioNota" value="Valutazione tecnica"/>
	</c:when>
	<c:otherwise>
		<c:set var="dettaglioTitle" value="Dettaglio offerta prezzi" />
		<c:set var="dettaglioNota" value="Valutazione economica"/>
	</c:otherwise>
</c:choose>


<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoAmmgarFunction", pageContext,numeroGara,ditta,paginaAttivaWizard)}' />

<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />


	<gene:redefineInsert name="corpo">
	
	
<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>
	
<table class="lista">
	<c:if test='${numeroLottiOEPV > 0 and isOffertaPerLotto ne "true"}'>
		<tr>
			<td>
				<br>
				<b>ATTENZIONE:</b> Per modificare i dati dei lotti con criterio di aggiudicazione 'Offerta economicamente più vantaggiosa' si deve accedere alla gestione per lotto. 
				 Tornare alla pagina chiamante e attivare la funzione '${dettaglioNota } per lotto'.
			</td>
		</tr>
	</c:if>
	<c:choose>
		<c:when test='${paginaAttivaWizard eq step6Wizard and isOffertaPerLotto eq "true" and modalitaAggiudicazioneGara eq 6}'>
			<tr>
				<td >
					<c:choose>
						<c:when test="${punteggioTecnico < 0.0}">
							<c:set var="msgPunteggioTecnico" value="non definito"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber type="number" value="${punteggioTecnico}" var="punteggioTec" />
							<c:set var="msgPunteggioTecnico" value="${punteggioTec}"/>
						</c:otherwise>
					</c:choose>
					
					<b>Punteggio tecnico massimo:</b> ${msgPunteggioTecnico} <c:if test="${!empty sogliaTecnicaMinima }"> <fmt:formatNumber type="number" value="${sogliaTecnicaMinima}" var="SogliaMinTecFormat" />&nbsp;&nbsp;&nbsp;&nbsp;<b>Soglia minima:</b>&nbsp;${SogliaMinTecFormat }</c:if>
				</td>
			</tr>
			<c:if test="${isGaraTelematica }">
				<c:set var="whereSezTec" value="NGARA='${numeroGara }'"/>
				<c:set var="sezionitec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "SEZIONITEC","GARE1", whereSezTec)}'/>
				<c:if test='${sezionitec eq 1}'>
					<c:choose>
						<c:when test="${empty punteggioTecnicoQualitativo }">
							<c:set var="msgPunteggioTecnicoQualitativo" value="non definito"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber type="number" value="${punteggioTecnicoQualitativo}" var="punteggioTecQualitativo" />
							<c:set var="msgPunteggioTecnicoQualitativo" value="${punteggioTecQualitativo}"/>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${empty punteggioTecnicoQuantitativo }">
							<c:set var="msgPunteggioTecnicoQuantitativo" value="non definito"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber type="number" value="${punteggioTecnicoQuantitativo}" var="punteggioTecQuantitativo" />
							<c:set var="msgPunteggioTecnicoQuantitativo" value="${punteggioTecQuantitativo}"/>
						</c:otherwise>
					</c:choose>
					<tr>
					<td ${stileDati}><b>di cui qualitativi:</b> ${msgPunteggioTecnicoQualitativo}</td>
					</tr>
					<tr>
					<td ${stileDati}><b>di cui quantitativi:</b> ${msgPunteggioTecnicoQuantitativo}</td>
					</tr>
				</c:if>
			</c:if>
			<tr>
				<td ${stileDati} >
					<b>Riparametrazione ?</b> ${msgRiptec }
				</td>
			</tr>
			<c:if test='${STATOCG eq 1}'>
				<tr>
					<td ${stileDati} >
						<b>Valutazione in corso su app</b>
					</td>
				</tr>
			</c:if>
		</c:when>

		<c:when test='${paginaAttivaWizard eq step7Wizard and isOffertaPerLotto eq "true" and modalitaAggiudicazioneGara eq 6}'>
			<tr>
				<td  >
					<c:choose>
						<c:when test="${punteggioEconomico < 0.0}">
							<c:set var="msgPunteggioEconomico" value="non definito"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber type="number" value="${punteggioEconomico}" var="punteggioEco" />
							<c:set var="msgPunteggioEconomico" value="${punteggioEco}"/>
						</c:otherwise>
					</c:choose>
					<b>Punteggio economico massimo:</b> ${msgPunteggioEconomico}  <c:if test="${!empty sogliaEconomicaMinima }"> <fmt:formatNumber type="number" value="${sogliaEconomicaMinima}" var="SogliaMinEcoFormat" />&nbsp;&nbsp;&nbsp;&nbsp;<b>Soglia minima:</b>&nbsp;${SogliaMinEcoFormat }</c:if>
				</td>
			</tr>
			<tr>
				<td ${stileDati} >
					<b>Riparametrazione ?</b> ${msgRipeco }
				</td>
			</tr>
		</c:when>
	</c:choose>
	
	<c:if test='${!empty filtroDitte and isOffertaPerLotto eq "true"}'>
		<tr>
			<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
			 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span> 
			 <c:if test='${updateLista ne 1}'>
				 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro();" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
				 <a class="link-generico" href="javascript:AnnullaFiltro();">Cancella filtro</a> ]
			 </c:if>
			</td>
		</tr>
	</c:if>
	
	<c:if test='${!empty filtroValutazione and isOffertaPerLotto eq "true"}'>
		<tr>
			<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
			 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata su dettaglio valutazione non completato</span> 
			 <c:if test='${updateLista ne 1}'>
				 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro();" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
				 <a class="link-generico" href="javascript:AnnullaFiltro();">Cancella filtro</a> ]
			 </c:if>
			</td>
		</tr>
	</c:if>
	
	<c:choose>
		<c:when test='${isOffertaPerLotto eq "true"}'>
			<c:set var="ordinamento" value="5"/>
		</c:when>
		<c:otherwise>
			<c:set var="ordinamento" value="3"/>
		</c:otherwise>
	</c:choose>
	
	<!-- inizia pagine a lista -->

		<tr>
			<td >
				<gene:formLista entita="DITG" where='${whereDITG} ${filtroValutazione}' tableclass="datilista" sortColumn="${ordinamento }" pagesize="20" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOfferteTecnicaEconomica" gestisciProtezioni="true" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1 and bloccoAggiudicazione ne 1}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:conferma();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:if test='${paginaAttivaWizard eq step6Wizard and bloccoAggiudicazione ne 1 and isOffertaPerLotto ne "true"}'>
									<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Esporta-excel-oepv") and isGaraLottiConOffertaUnica ne "true"}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupEsportaInExcel('${key}');" title='Esporta in Excel' tabindex="1500">
													Esporta in Excel
												</a>
											</td>
										</tr>
									</c:if>	
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Importa-excel-oepv") and isGaraLottiConOffertaUnica ne "true"}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupImportaDaExcel('${key}');" title='Importa da Excel' tabindex="1501">
													Importa da Excel
												</a>
											</td>
										</tr>	
									</c:if>
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaFasiGara) and datiRiga.rowCount > 0 and (((paginaAttivaWizard eq step10Wizard or (paginaAttivaWizard < step8Wizard and bloccoAggiudicazione ne 1)) and not isGaraTelematica)
									or (isGaraTelematica and ((faseGara eq 2 or faseGara eq 3 or faseGara eq 4) and paginaAttivaWizard eq step1Wizard) or (faseGara eq 5 and paginaAttivaWizard eq step6Wizard) or (faseGara eq 6 and paginaAttivaWizard eq step7Wizard))) and !bloccoAmmgar}'>
									<c:if test='${not (modalitaAggiudicazioneGara eq 6 and isOffertaPerLotto eq "true" and (paginaAttivaWizard eq  step6Wizard or paginaAttivaWizard eq  step7Wizard) and esistonoDitteConPunteggio)}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
													${gene:resource("label.tags.template.dettaglio.schedaModifica")}
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								<c:if test='${datiRiga.rowCount > 0 and gene:checkProt(pageContext, strProtModificaFasiGara) and (((numeroLottiMigliorOffertaPrezzi > 0 or numeroLottiOEPV > 0) and (param.paginaAttivaWizard eq step7Wizard or paginaAttivaWizard eq step7Wizard)) or (numeroLottiOEPVMigliorOffertaPrezziValtec>0 and (param.paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step6Wizard))) and gene:checkProtFunz(pageContext, "ALT", "VisualizzaDettaglioPrezzi") and isOffertaPerLotto ne "true"}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:dettaglioOffertaDitta();" title='${dettaglioTitle}' tabindex="1505">
												${dettaglioTitle}
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${(paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard) and autorizzatoModifiche ne 2 and modalitaAggiudicazioneGara eq 6 and isOffertaPerLotto eq "true" and updateLista ne 1 }'>
									<c:if test='${ ((faseGara <7 and isGaraTelematica ne "true") or (isGaraTelematica eq "true" and ((paginaAttivaWizard eq step6Wizard and faseGara eq 5) or (paginaAttivaWizard eq step7Wizard and faseGara eq 6) ))) }'>
										<c:if test='${ gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.CalcoloPunteggi")}'>
											<c:choose>
												<c:when test="${paginaAttivaWizard eq step6Wizard }">
													<c:set var="tipoPunteggio" value="1"/>
												</c:when>
												<c:otherwise>
													<c:set var="tipoPunteggio" value="2"/>
												</c:otherwise>
											</c:choose>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:apriPopupCalcoloPunteggi('${key}',${tipoPunteggio},'true',${paginaAttivaWizard });" title='Calcolo punteggi (1)' tabindex="1506">
														Calcolo punteggi (1)
													</a>
												</td>
											</tr>
										</c:if>
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.EsclusioneSoglia")}'>
											<c:set var="msgEsclusione" value="Esclusione soglia minima e riparametrazione (2)"/>
											<c:set var="tipoTitolo" value="1"/>
											<c:choose>
												<c:when test="${paginaAttivaWizard eq step6Wizard }">
													<c:set var="tipoPunteggio" value="1"/>
													<c:set var="tipoRiparam" value="${RIPTEC }"/>
													<c:if test="${RIPTEC eq 3 or empty RIPTEC }">
														<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
														<c:set var="tipoTitolo" value="2"/>
													</c:if>
												</c:when>
												<c:otherwise>
													<c:set var="tipoPunteggio" value="2"/>
													<c:set var="tipoRiparam" value="${RIPECO }"/>
													<c:if test="${RIPECO eq 3 or empty RIPECO }">
														<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
														<c:set var="tipoTitolo" value="2"/>
													</c:if>
												</c:otherwise>
											</c:choose>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:apriPopupEsclusioneSogliaMinima('${key}',${tipoPunteggio},${tipoTitolo },'${tipoRiparam }');" title='${msgEsclusione}' tabindex="1507">
														${msgEsclusione}
													</a>
												</td>
											</tr>
										</c:if>
									
									 
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.AnnullaCalcoloPunteggi")}'>
											<c:choose>
												<c:when test="${paginaAttivaWizard eq step6Wizard }">
													<c:set var="tipoPunteggio" value="1"/>
												</c:when>
												<c:otherwise>
													<c:set var="tipoPunteggio" value="2"/>
												</c:otherwise>
											</c:choose>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:apriPopupAnnullaCalcoloPunteggi('${key}',${tipoPunteggio});" title='Annulla calcolo punteggi' tabindex="1508">
														Annulla calcolo punteggi
													</a>
												</td>
											</tr>
										</c:if>
										
									</c:if>
									<c:if test='${paginaAttivaWizard eq step6Wizard && STATOCG eq 2 && faseGara eq 5 && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.RipristinaValutazioneEval")}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupRipristinaValutazioneMEVAL('${codiceGara}','${numeroGara}');" title='Ripristina valutazione su M-Eval' tabindex="1508">
													Ripristina valutazione su M-Eval
												</a>
											</td>
										</tr>
									</c:if>	
								</c:if>
								<c:if test='${(paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard) and modalitaAggiudicazioneGara eq 6 and isOffertaPerLotto eq "true" and updateLista ne 1 }'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupProspettoPunteggi('${key}');" title='Prospetto punteggi ditte' tabindex="1509">
												Prospetto punteggi ditte
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test="${datiRiga.rowCount > 0 and modalitaAggiudicazioneGara eq 6 and isOffertaPerLotto eq 'true' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.ImpostaFiltroVerificaValutazione')}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:impostaFiltroVerificaValutazione('${gene:getValCampo(key,"NGARA")}');" title='Verifica dettaglio valutazione completato' tabindex="1509">
												Verifica dettaglio valutaz.completato
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${datiRiga.rowCount > 0 and isOffertaPerLotto eq "true"}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1510">
												Imposta filtro
											</a>
										</td>
									</tr>
								</c:if>
								
							</c:otherwise>
						</c:choose>
					</gene:redefineInsert>

					<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="NGARA5" headerClass="sortable" visibile='${isOffertaPerLotto ne "true" }' width="120" title="Codice lotto"/>
					<gene:campoLista campo="NGARA5fit" campoFittizio="true" definizione="T10" visibile="false" value="${datiRiga.DITG_NGARA5}" edit="${updateLista eq 1}" />
					<gene:campoLista campo="NUMORDPL" title="N.pl" headerClass="sortable" width="32" visibile='${isOffertaPerLotto eq "true" }'/>
					
					<gene:campoLista campo="CODIGA" title="Lotto" entita="GARE" where="GARE.NGARA = DITG.NGARA5" headerClass="sortable" width="100" visibile='${isOffertaPerLotto ne "true" }'/>
					
					<gene:campoLista campo="NOT_GAR" visibile='${isOffertaPerLotto ne "true" }' entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" ordinabile="false" />
					<gene:campoLista campo="MODLICG" visibile="false" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" />
					<gene:campoLista visibile="false" campoFittizio="true" campo="MODLICG" edit="true"  definizione="T10"  value="${datiRiga.GARE_MODLICG}"/>
					<gene:campoLista campo="DETLICG" visibile="false" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" />
					<gene:campoLista visibile="false" campoFittizio="true" campo="DETLICG" edit="true"  definizione="T10"  value="${datiRiga.GARE_DETLICG}"/>
					<gene:campoLista campo="VALTEC"  visibile="false" entita="GARE1" where="GARE1.CODGAR1=DITG.CODGAR5 and GARE1.NGARA=DITG.NGARA5" />
					<gene:campoLista visibile="false" campoFittizio="true" campo="VALTEC" edit="true"  definizione="T10"  value="${datiRiga.GARE1_VALTEC}"/>
					<gene:campoLista title="N." campo="NPROGG" visibile="false" />
					<gene:campoLista campo="NOMIMO" headerClass="sortable" visibile='${isOffertaPerLotto eq "true" }' />

					<gene:campoLista campo="NPROFF" visibile='false' />
					<gene:campoLista campo="NPRREQ" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DATREQ" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="ORAREQ" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="MEZREQ" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" title="Off.Ammessa?" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" width="80" headerClass="sortable" edit="${updateLista eq 1}" visibile='${!isPrequalifica }'/> <!-- definizione="N5;0;;SN;AMMGARAM1"  -->
					<c:if test='${paginaAttivaWizard eq step7Wizard}'>
						<gene:campoLista campo="AMMINVERSA" headerClass="sortable" edit="false" title="Esito verif. proc.inversa" width="80" visibile="${garaInversa eq '1' }" />
					</c:if>
					<gene:campoLista campo="PARTGAR" title="Partecipa al lotto?" headerClass="sortable" edit="${updateLista eq 1}" visibile="false"/>

					<gene:campoLista campo="ESTIMP" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />

	<c:choose>
		<c:when test='${paginaAttivaWizard eq step6Wizard}'>
			<gene:campoLista campo="REQMIN" visibile="${numeroLottiValtec > 0 }" width="100" edit="${updateLista eq 1}" />
			<gene:campoLista campo="AMMINVERSA" headerClass="sortable" edit="false" title="Esito verif. proc.inversa" width="80" visibile="${garaInversa eq '1' }" />
			<gene:campoLista campo="PUNTEC" visibile="${numeroLottiOEPV > 0 && (isOffertaPerLotto ne 'true' || (isOffertaPerLotto eq 'true' && modalitaAggiudicazioneGara eq 6))}" width="130" edit="false" />
			<gene:campoLista campo="PUNTECRIP" title="Punteggio riparametrato" visibile="${lottiOEPVRiparamTecPresenti}" width="130" edit="false" />
		</c:when>
		<c:otherwise>
			<gene:campoLista campo="REQMIN" visibile="false" edit="${updateLista eq 1}" />
			<gene:campoLista campo="PUNTEC" visibile="false" edit="false" />
			<gene:campoLista campo="PUNTECRIP" visibile="false" edit="false" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${paginaAttivaWizard eq step7Wizard}'>
			<c:if test='${(numeroLotti - numeroLottiOEPV) > 0 or datiRiga.rowCount == 0}'>
				<gene:campoLista campo="RIBAUO" title="Ribasso offerto" width="130" headerClass="sortable" visibile='${isOffertaPerLotto ne "true" }' edit="${updateLista eq 1}" definizione="F13.9;0;;PRC;RIBAUO" />
			</c:if>
			<c:if test='${numeroLottiOEPV > 0 and isOffertaPerLotto eq "true"}'>
				<gene:campoLista title="Ribasso offerto" tooltip="Ribasso offerto" width="130" campo="RIBOEPV" headerClass="sortable" edit="${updateLista eq 1 }" definizione="F13.9;0;;PRC;G1RIBOEPV"/>
			</c:if>
			
			<c:if test='${(numeroLottiMigliorOffertaPrezzi + numeroLottiOEPV + numeroLottiOfferteImporto) > 0}'>
				<gene:campoLista campo="IMPOFF" title="Importo" width="${gene:if(updateLista eq 1, '150', '')}" headerClass="sortable" visibile="true" edit="${updateLista eq 1}" />
			</c:if>

			<c:if test='${numeroLottiOEPV > 0}'>
				<gene:campoLista campo="PUNECO" visibile="true" width="130" edit="false" definizione="F13.9;0;;;PUNECO" />
				<gene:campoLista campo="PUNECORIP" title="Punteggio riparametrato" width="130" edit="false"  visibile="${lottiOEPVRiparamEcoPresenti}"/>
			</c:if>
			
			<%//Campi di gare per il calcolo del ribasso %>
			<gene:campoLista campo="RIBCAL" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="ONPRGE" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="IMPAPP" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="IMPSIC" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="IMPNRL" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>
			<gene:campoLista campo="SICINC" entita="GARE" where="GARE.CODGAR1=DITG.CODGAR5 and GARE.NGARA=DITG.NGARA5" visibile="false" edit="${updateLista eq 1}"/>	
			<gene:campoLista campo="IMPSIC_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.GARE_IMPSIC}"/>
			<gene:campoLista campo="SICINC_FIT" visibile="false" edit="true" campoFittizio="true" definizione="T1" value="${datiRiga.GARE_SICINC}"/>	
		</c:when>
		<c:otherwise>
			<gene:campoLista campo="RIBAUO" title="Ribasso offerto" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;PRC;RIBAUO" />
			<gene:campoLista campo="IMPOFF" title="Importo" visibile="false" edit="${updateLista eq 1}" />
			<gene:campoLista campo="PUNECO" visibile="false" edit="false" definizione="F13.9;0;;;PUNECO" />
			<gene:campoLista campo="PUNECORIP" visibile="false" edit="false"  />
		</c:otherwise>
	</c:choose>

			<gene:campoLista campo="STAGGI" headerClass="sortable" visibile="false" />
			<gene:campoLista campo="CONGRUO" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
					
				

					<gene:campoLista campo="INVOFF"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="INVGAR"  visibile="false" edit="${updateLista eq 1}"  />
					<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					
					<gene:campoLista campo="CONGMOT" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="CONGALT" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPSICAZI" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPMANO" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPPERM" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPCANO" visibile="false" edit="${updateLista eq 1}" />
										
					<gene:campoLista campo="ESCLUDI_DITTA_ALTRI_LOTTI" visibile="false" value="0" campoFittizio="true" definizione="N2" edit="${updateLista eq 1}"/>

					<% // Questo campo non viene MAI modificato nella pagina e la sua valorizzazione viene gestito solo lato server. %>
					<% // E' presente nella pagina come campo hidden per capire se il valore del campo V_DITGAMMIS.AMMGAR e' ereditato %>
					<% // da una fase precedente o e' effettivamente un valore esistente per lo step del wizard in visualizzazione %>
					<gene:campoLista campo="AMMGAR" entita="DITGAMMIS" where="DITGAMMIS.CODGAR=DITG.CODGAR5 and DITGAMMIS.NGARA=DITG.NGARA5 and DITGAMMIS.DITTAO=DITG.DITTAO and DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					
				<c:if test='${updateLista eq 0}' >
					<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
				</c:if>
					
					<c:if test='${isOffertaPerLotto eq "true" and updateLista ne 1 and modalitaAggiudicazioneGara eq 6}'>
						<c:if test="${paginaAttivaWizard eq step6Wizard}">
							<gene:campoLista title="&nbsp;" width="20">
							<c:set var="tipoTec" value="1"/>
							<c:if test="${sezionitec eq '1' }">
								<c:set var="tipoTec" value="1-sez"/>
							</c:if>
							<a href="javascript:dettaglioValutazioneTecEco('${chiaveRigaJava}','${tipoTec}','${esistonoDitteConPunteggio}','${faseGara}','${autorizzatoModifiche}');" title="Dettaglio valutazione tecnica" >
										<img width="16" height="16" title="Dettaglio valutazione tecnica" alt="Dettaglio valutazione tecnica" src="${pageContext.request.contextPath}/img/valutazionetecnica.png"/>
							</a>
							</gene:campoLista>
						</c:if>
						<c:if test="${paginaAttivaWizard eq step7Wizard}">
							<gene:campoLista title="&nbsp;" width="20">
							<a href="javascript:dettaglioValutazioneTecEco('${chiaveRigaJava}','2','${esistonoDitteConPunteggio}','${faseGara}','${autorizzatoModifiche}');" title="Dettaglio valutazione economica" >
										<img width="16" height="16" title="Dettaglio valutazione economica" alt="Dettaglio valutazione economica" src="${pageContext.request.contextPath}/img/valutazionetecnica.png"/>
							</a>
							</gene:campoLista>
						</c:if>
					</c:if>
					
					
					<c:if test="${iconaNoteAttiva eq 1}">
						<gene:campoLista campo="ALTNOT"  visibile="false"/>
					</c:if>
					<gene:campoLista title="&nbsp;" width="20">
						<c:if test="${iconaNoteAttiva eq 1}">
							<c:choose>
								<c:when test="${not empty datiRiga.DITG_ALTNOT}">
									<c:set var="note" value="_note"/>
									<c:set var="iconaTooltip" value="Ulteriori dettagli con note"/>
								</c:when>
								<c:otherwise>
									<c:set var="note" value=""/>
									<c:set var="iconaTooltip" value="Ulteriori dettagli"/>
								</c:otherwise>
							</c:choose>	
						</c:if>
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';ulterioriCampi(${currentRow+1}, '${chiaveRigaJava}');" title="Ulteriori dettagli" >
							<img width="16" height="16" title="${iconaTooltip}" alt="Ulteriori dettagli" src="${pageContext.request.contextPath}/img/opzioni${note}.png"/>
						</a>
					</gene:campoLista>
					<gene:campoLista campo="RIPTEC" visibile="false" entita="GARE1" where="ARE1.CODGAR1=DITG.CODGAR5 and GARE1.NGARA=DITG.NGARA5" />
					<gene:campoLista visibile="false" campoFittizio="true" campo="RIPTEC" edit="true"  definizione="T10"  value="${datiRiga.GARE1_RIPTEC}"/>
					<gene:campoLista campo="RIPECO" visibile="false" entita="GARE1" where="GARE1.CODGAR1=DITG.CODGAR5 and GARE1.NGARA=DITG.NGARA5" />									
					<gene:campoLista visibile="false" campoFittizio="true" campo="RIPECO" edit="true"  definizione="T10"  value="${datiRiga.GARE1_RIPECO}"/>
					
					<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
					<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
					<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
					<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
					<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="isBloccoAggiudicazione" id="isBloccoAggiudicazione" value="${bloccoAggiudicazione}" />
					<input type="hidden" name="filtroValutazione" id="filtroValutazione" value="" />
					
					<input type="hidden" name="isOffertaPerLotto" id="isOffertaPerLotto" value="${isOffertaPerLotto}" />
					<input type="hidden" name="visOffertaEco" id="visOffertaEco" value="${visOffertaEco}" />
					
				</gene:formLista>
			</td>
		</tr>
	
<!-- fine pagine a lista -->

	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1 and bloccoAggiudicazione ne 1}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
					<c:if test='${isOffertaPerLotto eq "true"  and (paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard) and autorizzatoModifiche ne 2 and modalitaAggiudicazioneGara eq 6 and isOffertaPerLotto eq "true" and updateLista ne 1 }'>
						<c:if test='${ ((faseGara <7 and isGaraTelematica ne "true") or (isGaraTelematica eq "true" and ((paginaAttivaWizard eq step6Wizard and faseGara eq 5) or (paginaAttivaWizard eq step7Wizard and faseGara eq 6) ))) }'>
							<c:if test='${ gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.CalcoloPunteggi")}'>
								<c:choose>
									<c:when test="${paginaAttivaWizard eq step6Wizard }">
										<c:set var="tipoPunteggio" value="1"/>
									</c:when>
									<c:otherwise>
										<c:set var="tipoPunteggio" value="2"/>
									</c:otherwise>
								</c:choose>
								<INPUT type="button" class="bottone-azione" value="Calcolo punteggi (1)" title="Calcolo punteggi (1)" onclick="javascript:apriPopupCalcoloPunteggi('${key}',${tipoPunteggio},'true',${paginaAttivaWizard },'${STATOCG}');"/>&nbsp;
							</c:if>
							<c:if test='${ gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.EsclusioneSoglia")}'>
								<c:set var="msgEsclusione" value="Esclusione soglia minima e riparametrazione (2)"/>
								<c:set var="tipoTitolo" value="1"/>
								<c:choose>
									<c:when test="${paginaAttivaWizard eq step6Wizard }">
										<c:set var="tipoPunteggio" value="1"/>
										<c:set var="tipoRiparam" value="${RIPTEC }"/>
										<c:if test="${RIPTEC eq 3 or empty RIPTEC }">
											<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
											<c:set var="tipoTitolo" value="2"/>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:set var="tipoPunteggio" value="2"/>
										<c:set var="tipoRiparam" value="${RIPECO }"/>
										<c:if test="${RIPECO eq 3 or empty RIPECO }">
											<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
											<c:set var="tipoTitolo" value="2"/>
										</c:if>
									</c:otherwise>
								</c:choose>
								<INPUT type="button" class="bottone-azione" value="${msgEsclusione}" title="${msgEsclusione}" onclick="javascript:apriPopupEsclusioneSogliaMinima('${key}',${tipoPunteggio},${tipoTitolo },'${tipoRiparam }');"/>&nbsp;&nbsp;
								<br>
								<br>
							</c:if>
						</c:if>
					</c:if>
					<c:if test='${isOffertaPerLotto ne "true" }' >
						<INPUT type="button" class="bottone-azione" value="Torna a elenco concorrenti" title="Torna a elenco concorrenti" onclick="javascript:historyVaiIndietroDi(1);"/>&nbsp;
					</c:if>
					<c:if test='${isOffertaPerLotto eq "true" }' >
						<INPUT type="button" class="bottone-azione" value="Torna a elenco lotti" title="Torna a elenco lotti" onclick="javascript:historyVaiIndietroDi(1);"/>&nbsp;
					</c:if>
					

				<%//CF230511 %>
					<c:choose>
						<c:when test='${paginaAttivaWizard eq step6Wizard && isGaraLottiConOffertaUnica eq "true"}' >
							<c:set var="dettaglioTitle" value="Verifica conformità lavorazioni e forniture" />
						</c:when>
						<c:otherwise>
							<c:set var="dettaglioTitle" value="Dettaglio offerta prezzi" />
						</c:otherwise>
					</c:choose>
					
					<c:if test='${datiRiga.rowCount > 0 and gene:checkProt(pageContext, strProtModificaFasiGara) and (((numeroLottiMigliorOffertaPrezzi > 0 or numeroLottiOEPV > 0) and (param.paginaAttivaWizard eq step7Wizard or paginaAttivaWizard eq step7Wizard)) or (numeroLottiOEPVMigliorOffertaPrezziValtec>0 and (param.paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step6Wizard))) and gene:checkProtFunz(pageContext, "ALT", "VisualizzaDettaglioPrezzi") and isOffertaPerLotto ne "true"}'>
					<!-- c:if test='${datiRiga.rowCount > 0 and gene:checkProt(pageContext, strProtModificaFasiGara) and ((numeroLottiMigliorOffertaPrezzi > 0 or numeroLottiOEPV > 0) and (param.paginaAttivaWizard eq step7Wizard or paginaAttivaWizard eq step7Wizard or (numeroLottiValtec>0 and (param.paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step6Wizard)))) and gene:checkProtFunz(pageContext, "ALT", "VisualizzaDettaglioPrezzi") and isOffertaPerLotto ne "true"}'-->
							<INPUT type="button"  class="bottone-azione" value='${dettaglioTitle}' title='${dettaglioTitle}' onclick="javascript:dettaglioOffertaDitta();">
					</c:if>
										
					<c:if test='${(datiRiga.rowCount > 0 and bloccoAggiudicazione ne 1)}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaFasiGara) and !bloccoAmmgar and 
						(not isGaraTelematica or (isGaraTelematica and ((faseGara eq 2 or faseGara eq 3 or faseGara eq 4) and paginaAttivaWizard eq step1Wizard) or (faseGara eq 5 and paginaAttivaWizard eq step6Wizard) or (faseGara eq 6 and paginaAttivaWizard eq step7Wizard))   )}'>
							<c:if test='${not (modalitaAggiudicazioneGara eq 6 and isOffertaPerLotto eq "true" and (paginaAttivaWizard eq  step6Wizard or paginaAttivaWizard eq  step7Wizard) and esistonoDitteConPunteggio)}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaLista();">
							</c:if>
						</c:if>
					</c:if>
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
</table>

<gene:javaScript>

setBustalotti(${bustalotti });
setIsProceduraTelematica("${isGaraTelematica }");
setAttivaValutazioneTec("${attivaValutazioneTec }");

document.getElementById("numeroDitte").value = ${currentRow}+1;

var isOffertaPerLotto="${isOffertaPerLotto}";

<c:if test='${updateLista eq 1}'>
		function conferma(){
			// Riabilitazione prima del salvataggio delle combobox disabilitate all'utente
			for(var i=0; i < ${currentRow}+1; i++){
				document.getElementById("V_DITGAMMIS_AMMGAR_" + (i+1)).disabled = false;
				if(document.getElementById("DITG_PARTGAR_" + (i+1)) !=null)
					document.getElementById("DITG_PARTGAR_" + (i+1)).disabled = false;
				if (document.getElementById("DITG_RIBAUO_" + (i+1)) != null)
					document.getElementById("DITG_RIBAUO_" + (i+1)).disabled = false;
				if (document.getElementById("DITG_IMPOFF_" + (i+1)) != null)
					document.getElementById("DITG_IMPOFF_" + (i+1)).disabled = false;
				if (document.getElementById("DITG_REQMIN_" + (i+1)) != null)
					document.getElementById("DITG_REQMIN_" + (i+1)).disabled = false;
			}

			listaConferma();
		}

		function inizializzaLista(){
			var numeroDitte = ${currentRow}+1;
			
			for(var t=0; t < numeroDitte; t++){
				if(getValue("V_DITGAMMIS_MOTIVESCL_" + (t+1)) == 98 || getValue("V_DITGAMMIS_MOTIVESCL_" + (t+1)) == 99){
					document.getElementById("V_DITGAMMIS_AMMGAR_" + (t+1)).disabled = true;
					if (document.getElementById("DITG_REQMIN_" + (t+1)) != null)
						document.getElementById("DITG_REQMIN_" + (t+1)).disabled = true;
					
					if (document.getElementById("DITG_RIBAUO_" + (i+1)) != null)
						document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = true;
					if (document.getElementById("DITG_IMPOFF_" + (t+1)) != null)
					  document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = true;
					
				}

			<c:if test='${paginaAttivaWizard eq step1Wizard}' >
			  if(getValue("DITG_PARTGAR_" + (t+1)) == "2"){
					document.getElementById("DITG_REQMIN_" + (t+1)).disabled = true;	
					
				}
			</c:if>
			
			<c:if test='${paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard}' >
				if(getValue("MODLICG_" + (t+1)) == "6" && isOffertaPerLotto != "true"){
				 	if (document.getElementById("V_DITGAMMIS_AMMGAR_" + (t+1)) != null)
							document.getElementById("V_DITGAMMIS_AMMGAR_" + (t+1)).disabled = true;	
				 }
			</c:if>
			
			<c:if test='${paginaAttivaWizard eq step6Wizard}' >
			  if(getValue("DITG_PARTGAR_" + (t+1)) == "2" || (getValue("MODLICG_" + (t+1)) == "6" && isOffertaPerLotto != "true")){
					if (document.getElementById("DITG_REQMIN_" + (t+1)) != null)
						document.getElementById("DITG_REQMIN_" + (t+1)).disabled = true;	
					
			 }
			 
				
			</c:if>

			<c:if test='${paginaAttivaWizard eq step7Wizard}' >
				//alert("V_DITGAMMIS_AMMGAR_" + (t+1) + " = " + getValue("V_DITGAMMIS_AMMGAR_" + (t+1)));
			  if(getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == 2 || getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == 6 || getValue("V_DITGAMMIS_AMMGAR_" + (t+1)) == 9){
					if (document.getElementById("DITG_RIBAUO_" + (t+1)) != null)
						document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = false;
					if (document.getElementById("DITG_IMPOFF_" + (t+1)) != null)
						document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = false;
					if (document.getElementById("DITG_PARTGAR_" + (t+1)) != null)
						document.getElementById("DITG_PARTGAR_" + (t+1)).disabled = false;
					
				}
				else {
						if(getValue("DITG_PARTGAR_" + (t+1)) == "2" || (getValue("MODLICG_" + (t+1)) == "6" && isOffertaPerLotto != "true")){
							if (document.getElementById("DITG_IMPOFF_" + (t+1)) != null)
								document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = true;
							if (document.getElementById("DITG_RIBAUO_" + (t+1)) != null)
								document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = true;
							
						}
					}
					
				<c:if test='${offtel eq 1}' >
						if (document.getElementById("DITG_RIBAUO_" + (t+1)) != null)
							document.getElementById("DITG_RIBAUO_" + (t+1)).disabled = true;
						if (document.getElementById("DITG_IMPOFF_" + (t+1)) != null)
							document.getElementById("DITG_IMPOFF_" + (t+1)).disabled = true;
					</c:if>
					
				
				 	
			</c:if>
			
				//Se PARTGAR=2 si deve disabilitare il campo AMMGAR
				var partgar = getValue("DITG_PARTGAR_" + (t+1));
				if(partgar == 2 )
					document.getElementById("V_DITGAMMIS_AMMGAR_" + (t+1)).disabled = true;
				
				<c:if test='${bustalotti eq 1 && isGaraTelematica eq true}' >
					if (document.getElementById("DITG_PARTGAR_" + (t+1)) != null)
						document.getElementById("DITG_PARTGAR_" + (t+1)).disabled = true;
				</c:if>
			}
			
		}
	
		// Funzione per associare le funzioni JS da eseguire al momento di modifica
		// di un campo. Questo e' necessario perchè la lista e' stata progetta per
		// non essere mai modificabile e quindi il tag gene:campoLista non associa
		// mai una funzione JS all'evento onchange.
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
				if (document.getElementById("DITG_PARTGAR_" + i) != null)
					document.getElementById("DITG_PARTGAR_" + i).onchange = aggiornaPerCambioPartecipazioneGara;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
		<c:choose>
			<c:when test='${paginaAttivaWizard eq step6Wizard}' >
				document.getElementById("DITG_REQMIN_" + i).onchange = aggiornaPerCambioRequisitiMinimi;
			</c:when>
			<c:when test='${paginaAttivaWizard eq step7Wizard}' >
				var modlicg = getValue("MODLICG_" + i);
				if (document.getElementById("DITG_IMPOFF_" + i) != null && modlicg!=6)
					document.getElementById("DITG_IMPOFF_" + i).onchange = aggiornaPerCambioImportoOfferto;
				if(modlicg == "6"){
					activeForm.setDominio("DITG_RIBAUO_" + i,"");
					<c:if test="${ isOffertaPerLotto eq 'true' }">
						document.getElementById("DITG_RIBOEPV_" + i).onchange = aggiornaPerCambioRibassoOEPV;
						document.getElementById("DITG_IMPOFF_" + i).onchange = aggiornaRIBOEPVPerCambioImporto;
					</c:if>
				} else {
					document.getElementById("DITG_RIBAUO_" + i).onchange = aggiornaPerCambioRibassoAumento;
					//	activeForm.addCampo(document.getElementById("DITG_IMPOFF_" + i));
					//	activeForm.setDominio("DITG_IMPOFF_" + i,"MONEY5");
					//	activeForm.setTipo("DITG_IMPOFF_" + i,"F15.5");
				}
			</c:when>
		</c:choose>
			}
		}
		
		// Funzioni JS richiamate subito dopo la creazione della pagina per l'
		associaFunzioniEventoOnchange();
		inizializzaLista();
		
		document.getElementById("numeroDitteTotali").value = ${datiRiga.rowCount};
		<c:if test='${paginaAttivaWizard eq step7Wizard and modalitaAggiudicazioneGara ne 6}'>
			if(document.getElementById("numeroDitte").value==1){
	          document.onkeypress = stopRKey;
	        }
		</c:if>
		
		function aggiornaPerCambioAmmessaGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value != "2" && this.value != "6"){
				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
				setValue("DITG_INVOFF_" + numeroRiga, "1");
				setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, "");
				setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, "");
	
				
				<c:if test='${paginaAttivaWizard == step6Wizard}' >
					if(getValue("DITG_PARTGAR_" + numeroRiga)!=2){
						
						if (document.getElementById("DITG_REQMIN_" + numeroRiga) != null)
							document.getElementById("DITG_REQMIN_" + numeroRiga).disabled = false;
					}
				</c:if>
				
				<c:if test='${paginaAttivaWizard == step7Wizard && offtel ne 1}' >
					if(getValue("DITG_PARTGAR_" + numeroRiga)!=2){
						if (document.getElementById("DITG_IMPOFF_" + numeroRiga) != null)
						   document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = false;
						if (document.getElementById("DITG_RIBAUO_" + numeroRiga) != null)
							document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = false;
					}
				</c:if>
				
				<c:if test='${!(bustalotti eq 1 && isGaraTelematica eq true)}' >
					if (document.getElementById("DITG_PARTGAR_" + numeroRiga) != null)
						document.getElementById("DITG_PARTGAR_" + numeroRiga).disabled = false;
				</c:if>	
							
			} 
			<c:if test='${paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard}' >
				if( (this.value !="") && (getValue("DITG_PARTGAR_" + numeroRiga) == null || getValue("DITG_PARTGAR_" + numeroRiga) == ""))
					setValue("DITG_PARTGAR_" + numeroRiga, "1");
			</c:if>
		}
	
		
	<c:if test='${paginaAttivaWizard eq step6Wizard}' >
		function aggiornaPerCambioRequisitiMinimi(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var reqmin = this.value;
			if(reqmin==2){
				var ammissione = getValue("V_DITGAMMIS_AMMGAR_" + numeroRiga);
				if(ammissione == "" || (ammissione != 2 && ammissione !=6)){
					var msg="Confermi l'esclusione della ditta dal lotto in quanto non conforme ai requisiti minimi?";
					if(confirm(msg)){
						setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga,"2");
						document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).onchange();
						setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga,"102");
					}
				}
			}
			var tmp = getValue("DITG_PARTGAR_" + numeroRiga);
			if((tmp == null || tmp == "" || tmp == "0") && (reqmin != null && reqmin !="")){
				setValue("DITG_INVGAR_" + numeroRiga, 1);
				setValue("DITG_INVOFF_" + numeroRiga, 1);
				setValue("DITG_PARTGAR_" + numeroRiga, 1);
				
				
			}
		}
		
	
		
		
		
		
	</c:if>

	<c:if test='${paginaAttivaWizard eq step7Wizard}' >

		function aggiornaPerCambioRibassoAumento(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var modlicg = getValue("MODLICG_" + numeroRiga);
			var ribcal= toVal(getValue("GARE_RIBCAL_" + numeroRiga));
			
			if(modlicg != "6"){
				// All'oggetto JS CampoObj del campo DITG_RIBAUO_<i> si cambia la funzione
				// di validazione, per poter effettuare in un'unica funzione i controlli
				// necessari al ribasso offerto o al punteggio all'onchange del campo
				activeForm.getCampo("DITG_RIBAUO_" + numeroRiga).setFnValidazione(validazioneRIBAUO);
				if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
					var tmp = null;
					tmp = getValue("DITG_PARTGAR_" + numeroRiga);
					var ribauo = this.value;
					if((tmp == null || tmp == "" || tmp == "0") && (ribauo != null && ribauo!="")){
						setValue("DITG_PARTGAR_" + numeroRiga, 1);
						setValue("DITG_INVGAR_" + numeroRiga, 1);
						setValue("DITG_INVOFF_" + numeroRiga, 1);
						
					}
					
					
					//Aggiornamento impoff
					var ribauo = this.value;
					if((modlicg == "5" || modlicg == "14" || modlicg == "16") && ribcal == 1 ) {
						if(ribauo != null && ribauo!=""){
							ribauo = toVal(ribauo);
							var impsic = toVal(getValue("GARE_IMPSIC_" + numeroRiga)); 
							var impnrl = toVal(getValue("GARE_IMPNRL_" + numeroRiga));
							var onprge = toVal(getValue("GARE_ONPRGE_" + numeroRiga));
							var sicinc = toVal(getValue("GARE_SICINC_" + numeroRiga));
							var impapp = toVal(getValue("GARE_IMPAPP_" + numeroRiga));
							
							if (impapp == null || impapp=="") impapp=0;
							if (impsic == null || impsic=="") impsic=0;
							if (impnrl == null || impnrl=="") impnrl=0;
							if (onprge == null || onprge=="") onprge=0;
							
							var importo1 = parseFloat(impapp) - parseFloat(impsic) - parseFloat(impnrl) - parseFloat(onprge);
							var importo2 = parseFloat(impnrl);
												
							if (sicinc=='1')
								importo2 += parseFloat(impsic);	
							
							importo1 = round(importo1,5);
							importo2 = round(importo2,5);
							
							<c:if test='${not empty numeroCifreDecimaliRibasso}'>
								cifreDecimali =${numeroCifreDecimaliRibasso};
							</c:if>
	                        ribauo = round(ribauo,cifreDecimali);
								
							var impoff = (importo1 * (1 + ribauo/100)) + importo2;
							setValue("DITG_IMPOFF_" + numeroRiga, round(impoff,5));
						}else{
							setValue("DITG_IMPOFF_" + numeroRiga, "");
						}
							
					}
					
				}
			}
		}

		function validazioneRIBAUO(refVal, msg, obj){
			var result = false;
			var objId = this.obj.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);

			if(getValue("MODLICG_" + numeroRiga) != "6"){
				if(checkFloat(refVal, msg, obj)){
					if(getValue("MODLICG_" + numeroRiga) != "6" && refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})
						refVal.setValue((-1) * refVal.value);
	
			<c:choose>
				<c:when test='${not empty numeroCifreDecimaliRibasso}'>
					var tmp = null;
					if(this.getValue() != null && this.getValue() != ""){
						// Controllo del numero di cifre decimali rispetto
						// (il campo RIBAUO e' definito in C0CAMPI come F13.9)
						var numeroCifreDecimaliRibasso = ${numeroCifreDecimaliRibasso};
						tmp = this.getValue();
						if(this.getValue().indexOf(".") >= 0){
							if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliRibasso){
								result = true;
							} else {
								msg.setValue("Per il ribasso è possibile indicare al piu' ${numeroCifreDecimaliRibasso} cifre decimali");
								sbiancaCampi = false;
							}
						} else {
							result = true;
						}
					}
				</c:when>
				<c:otherwise>
					result = true;
				</c:otherwise>
			</c:choose>
				}
			}
			return result;
		}

		
		
		function aggiornaPerCambioImportoOfferto(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			activeForm.getCampo("DITG_IMPOFF_" + numeroRiga).setFnValidazione(validazioneIMPOFF);
			if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
				var impoff = toVal(this.value);
				var cifreDecimali = 9;
				var ribcal= toVal(getValue("GARE_RIBCAL_" + numeroRiga));
				var modlicg = getValue("MODLICG_" + numeroRiga);
				if (ribcal == 2 && modlicg != 6) {
					if (impoff == null || impoff == ""){
							setValue("DITG_RIBAUO_" + numeroRiga, "");
					}else{
						var impapp = toVal(getValue("GARE_IMPAPP_" + numeroRiga)); 
						var impsic = toVal(getValue("GARE_IMPSIC_" + numeroRiga)); 
						var impnrl = toVal(getValue("GARE_IMPNRL_" + numeroRiga));
						var onprge = toVal(getValue("GARE_ONPRGE_" + numeroRiga));
						var sicinc = toVal(getValue("GARE_SICINC_" + numeroRiga));
						
						if (impapp == null || impapp=="") impapp=0;
						if (impsic == null || impsic=="") impsic=0;
						if (impnrl == null || impnrl=="") impnrl=0;
						if (onprge == null || onprge=="") onprge=0;
										
						var numeratore = parseFloat(impoff) + parseFloat(onprge) - parseFloat(impapp);
						
						if (sicinc==null ||sicinc=="" || sicinc != '1')
							numeratore += parseFloat(impsic);
							
						var denominatore = parseFloat(impapp) - parseFloat(impsic) - parseFloat(impnrl) - parseFloat(onprge);
						
						var ribauo; 
						if (denominatore != 0)
							ribauo =  numeratore * 100 / denominatore ;
						else
							ribauo = 0;
						if (ribauo >0 && ${requestScope.offaum != "1"}){
							alert('Non sono ammesse offerte in aumento');
							setValue("DITG_RIBAUO_" + numeroRiga, "");
						}else{
							<c:if test='${not empty numeroCifreDecimaliRibasso}'>
								cifreDecimali =${numeroCifreDecimaliRibasso};
							</c:if>
							
							setValue("DITG_RIBAUO_" + numeroRiga, round(ribauo,cifreDecimali));
							
							//Si richama l'evento onchange del campo RIBAUO
							document.getElementById("DITG_RIBAUO_" + numeroRiga).onchange();
						}		
					} 
				}
			}
		}
		
		function validazioneIMPOFF(refVal, msg, obj){
			var result = false;
			if(checkFloat(refVal, msg, obj)){
				result=true;  
			}
			return result;
		}

		<c:if test='${isOffertaPerLotto eq "true"}'>
			function aggiornaPerCambioRibassoOEPV(){
				var objId = this.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
				var modlicg = getValue("MODLICG_" + numeroRiga);
				var ribcal= toVal(getValue("GARE_RIBCAL_" + numeroRiga));
				
				
				// All'oggetto JS CampoObj del campo DITG_RIBAUO_<i> si cambia la funzione
				// di validazione, per poter effettuare in un'unica funzione i controlli
				// necessari al ribasso offerto o al punteggio all'onchange del campo
				activeForm.getCampo("DITG_RIBOEPV_" + numeroRiga).setFnValidazione(validazioneRIBOEPV);
				if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
					
					//Aggiornamento impoff
					var riboepv = this.value;
					
					if(riboepv != null && riboepv!=""){
						riboepv = toVal(riboepv);
						var impsic = toVal(getValue("GARE_IMPSIC_" + numeroRiga)); 
						var impnrl = toVal(getValue("GARE_IMPNRL_" + numeroRiga));
						var onprge = toVal(getValue("GARE_ONPRGE_" + numeroRiga));
						var sicinc = toVal(getValue("GARE_SICINC_" + numeroRiga));
						var impapp = toVal(getValue("GARE_IMPAPP_" + numeroRiga));
						
						if (impapp == null || impapp=="") impapp=0;
						if (impsic == null || impsic=="") impsic=0;
						if (impnrl == null || impnrl=="") impnrl=0;
						if (onprge == null || onprge=="") onprge=0;
						
						var importo1 = parseFloat(impapp) - parseFloat(impsic) - parseFloat(impnrl) - parseFloat(onprge);
						var importo2 = parseFloat(impnrl);
											
						if (sicinc=='1')
							importo2 += parseFloat(impsic);	
						
						importo1 = round(importo1,5);
						importo2 = round(importo2,5);
						
						<c:if test='${not empty numeroCifreDecimaliRibasso}'>
							cifreDecimali =${numeroCifreDecimaliRibasso};
						</c:if>
                        riboepv = round(riboepv,cifreDecimali);
							
						var impoff = (importo1 * (1 + riboepv/100)) + importo2;
						setValue("DITG_IMPOFF_" + numeroRiga, round(impoff,5));
					}else{
						setValue("DITG_IMPOFF_" + numeroRiga, "");
					}
							
					
					
				}
				
			}
	
			function validazioneRIBOEPV(refVal, msg, obj){
				var result = false;
				var objId = this.obj.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
	
				
				if(checkFloat(refVal, msg, obj)){
					if(refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})
						refVal.setValue((-1) * refVal.value);
	
			<c:choose>
				<c:when test='${not empty numeroCifreDecimaliRibasso}'>
					var tmp = null;
					if(this.getValue() != null && this.getValue() != ""){
						var numeroCifreDecimaliRibasso = ${numeroCifreDecimaliRibasso};
						tmp = this.getValue();
						if(this.getValue().indexOf(".") >= 0){
							if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliRibasso){
								result = true;
							} else {
								msg.setValue("Per il ribasso è possibile indicare al piu' ${numeroCifreDecimaliRibasso} cifre decimali");
								sbiancaCampi = false;
							}
						} else {
							result = true;
						}
					}
				</c:when>
				<c:otherwise>
					result = true;
				</c:otherwise>
			</c:choose>
				}
				
				return result;
			}
			
			function aggiornaRIBOEPVPerCambioImporto(){
				var objId = this.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
				activeForm.getCampo("DITG_IMPOFF_" + numeroRiga).setFnValidazione(validazioneIMPOFF);
				if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
					var impoff = toVal(this.value);
					var cifreDecimali = 9;
					
					if (impoff == null || impoff == ""){
							setValue("DITG_RIBOEPV_" + numeroRiga, "");
					}else{
						var impapp = toVal(getValue("GARE_IMPAPP_" + numeroRiga)); 
						var impsic = toVal(getValue("GARE_IMPSIC_" + numeroRiga)); 
						var impnrl = toVal(getValue("GARE_IMPNRL_" + numeroRiga));
						var onprge = toVal(getValue("GARE_ONPRGE_" + numeroRiga));
						var sicinc = toVal(getValue("GARE_SICINC_" + numeroRiga));
						
						if (impapp == null || impapp=="") impapp=0;
						if (impsic == null || impsic=="") impsic=0;
						if (impnrl == null || impnrl=="") impnrl=0;
						if (onprge == null || onprge=="") onprge=0;
										
						var numeratore = parseFloat(impoff) + parseFloat(onprge) - parseFloat(impapp);
						
						if (sicinc==null ||sicinc=="" || sicinc != '1')
							numeratore += parseFloat(impsic);
							
						var denominatore = parseFloat(impapp) - parseFloat(impsic) - parseFloat(impnrl) - parseFloat(onprge);
						
						var riboepv; 
						if (denominatore != 0)
							riboepv =  numeratore * 100 / denominatore ;
						else
							riboepv = 0;
						if (riboepv >0 && ${requestScope.offaum != "1"}){
							alert('Non sono ammesse offerte in aumento');
							setValue("DITG_RIBOEPV_" + numeroRiga, "");
						}else{
							<c:if test='${not empty numeroCifreDecimaliRibasso}'>
								cifreDecimali =${numeroCifreDecimaliRibasso};
							</c:if>
							
							setValue("DITG_RIBOEPV_" + numeroRiga, round(riboepv,cifreDecimali));
														
						}		
					} 
				
				}
			}
		</c:if>

	</c:if>

<c:choose>
	<c:when test='${paginaAttivaWizard eq step6Wizard and (modalitaAggiudicazioneGara eq 6 || numeroLottiValtec >0)}' >
		<% // Funzione per la pagina punteggio tecnico %>
		function aggiornaPerCambioPartecipazioneGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value == "2"){
				setValue("DITG_REQMIN_" + numeroRiga, "");
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
				
				setValue("DITG_IMPOFF_" + numeroRiga, "");
				setValue("DITG_RIBAUO_" + numeroRiga, "");
				setValue("DITG_REQMIN_" + numeroRiga, "");
				setValue("DITG_IMPSICAZI_" + numeroRiga, "");
				setValue("DITG_IMPMANO_" + numeroRiga, "");
				setValue("DITG_IMPPERM_" + numeroRiga, "");
				setValue("DITG_IMPCANO_" + numeroRiga, "");
				
				if (document.getElementById("DITG_REQMIN_" + numeroRiga) != null)
					document.getElementById("DITG_REQMIN_" + numeroRiga).disabled = true;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = true;
				//Si richama l'evento onchange del campo AMMGAR
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).onchange();
			} else {
				
				if(getValue("DITG_REQMIN_" + numeroRiga) == "")
					setValue("DITG_REQMIN_" + numeroRiga, "");
				if(document.getElementById("DITG_REQMIN_" + numeroRiga)!=null)
					document.getElementById("DITG_REQMIN_" + numeroRiga).disabled = false;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = false;
			}
		}
	</c:when>
	<c:when test='${paginaAttivaWizard eq step7Wizard}' >
		<% // Funzione per la pagina offerte economiche %>
		function aggiornaPerCambioPartecipazioneGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value == "2"){
				setValue("DITG_IMPOFF_" + numeroRiga, "");
				setValue("DITG_RIBAUO_" + numeroRiga, "");
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
				setValue("DITG_REQMIN_" + numeroRiga, "");
				if (document.getElementById("DITG_IMPOFF_" + numeroRiga) != null)
					document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = true;
				if (document.getElementById("DITG_RIBAUO_" + numeroRiga) != null)
					document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = true;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = true;
				//Si richama l'evento onchange del campo AMMGAR
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).onchange();
				setValue("DITG_IMPSICAZI_" + numeroRiga, "");
				setValue("DITG_IMPMANO_" + numeroRiga, "");
				setValue("DITG_IMPPERM_" + numeroRiga, "");
				setValue("DITG_IMPCANO_" + numeroRiga, "");
			} else {
				//setValue("DITG_IMPOFF_" + numeroRiga, "");
				//setValue("DITG_RIBAUO_" + numeroRiga, "");
				if (document.getElementById("DITG_IMPOFF_" + numeroRiga) != null)
					document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = false;
				if (document.getElementById("DITG_RIBAUO_" + numeroRiga) != null)
					document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = false;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = false;
			}
		}
	</c:when>
</c:choose>

	

</c:if>


		function annulla(){
			listaAnnullaModifica();
		}

		function modificaLista(){
			document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 2;
			listaApriInModifica();
		}
		
		function ulterioriCampi(indiceRiga, chiaveRiga){
			href = "href=gare/ditg/ditg-schedaPopup-fasiGara.jsp";
			<c:if test='${updateLista eq "1"}' >
				var modlicg= getValue("MODLICG_" + indiceRiga);
				if(modlicg!=6 || (modlicg==6 && isOffertaPerLotto == 'true'))
					href += "&modo=MODIFICA";
			</c:if>
			href += "&indiceRiga=" + indiceRiga;
			href += "&key=" + chiaveRiga;
			href += "&stepWizard=${varTmp}";
			href += "&paginaAttivaWizard=${paginaAttivaWizard}";
			href += "&moties=" + getValue('V_DITGAMMIS_MOTIVESCL_'+indiceRiga); //href += "&moties=" + getValue('V_DITG_MOTIES_'+indiceRiga);
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			href += "&isGaraLottiConOffertaUnica=true";
			var bustalotti= "${bustalotti }";
			href += "&bustalotti=" + bustalotti;
		</c:if>
		<c:if test='${paginaAttivaWizard eq step1Wizard and isOffertaPerLotto ne "true"}'>
			href += "&isUlterioriCampiVerifica=true";
		</c:if>
			href += "&visPartgarUlterioriCampiOffUnica=true";
			var offtel="${offtel }";
			href += "&offtel=" + offtel;
			href += "&sezionitec=${sezionitec}";
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
	
	<c:if test='${paginaAttivaWizard eq step6Wizard}'>
	
		function apriPopupEsportaInExcel(ngara){
			openPopUpCustom("href=gare/gare/gare-popup-exportOEPV.jsp&modoRichiamo=ESPORTA&ngara="+ngara, "esportaImportaExcel", 700, 500, "yes", "yes");
		}

		function apriPopupImportaDaExcel(ngara){
			var isProceduraTelematica="${isGaraTelematica }";
			var bustalotti ="${bustalotti}";
			openPopUpCustom("href=gare/gare/gare-popup-importOEPV.jsp&ngara=" + ngara + "&isProceduraTelematica=" + isProceduraTelematica + "&bustalotti="+bustalotti, "esportaImportaExcel", 700, 500, "yes", "yes");
		}

	</c:if>

		function calcolaIAGPRO(valori){
			ret = 0;
			if (valori[0]!="")
				ret += eval(valori[0]);
			if (valori[1]!="") 
				ret += eval(valori[1]);
			if (valori[2] == "2") {
				if (valori[3]!="") {
					ret += eval(valori[3]);
				}
			}
			return eval(ret).toFixed(2);
		}
	
		function dettaglioOffertaDitta(){
			var chiave="${key}";
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/v_gcap_dpre/v_gcap_dpre-lista.jsp";
			href += "&key="+chiave;
			href += "&stepWizard=${varTmp}";
			href += "&paginaAttivaWizard=${paginaAttivaWizard}";
			href += "&isGaraTelematica=${isGaraTelematica }";
			href += "&faseGara=${faseGara }";
			href += "&bustalotti=${bustalotti}";
			document.location.href = href;
		}

<% // All'apertura si nascondono i campi che non si vuole siano visibili/modificabili dall'utente %>

	<c:if test='${paginaAttivaWizard eq step7Wizard}'>
		<c:if test='${(numeroLottiOEPV + numeroLottiMigliorOffertaPrezzi + numeroLottiOfferteImporto) > 0 and numeroLottiMigliorOffertaPrezzi + numeroLottiOfferteImporto < numeroLotti}'>
			var tmpModlicg = null;
			var tmpDetlicg = null;
			for(var t=1; t <= ${datiRiga.rowCount}; t++){
				tmpModlicg = getValue("MODLICG_"+t);
				tmpDetlicg = getValue("DETLICG_"+t);
				if(tmpModlicg != "6")
					showObj("colDITG_PUNECO_" + t, false);
				
				if((tmpModlicg == "1" || tmpModlicg == "13" || tmpModlicg == "15") && tmpDetlicg!=4)
					showObj("colDITG_IMPOFF_" + t, false);
			}
		</c:if>
		<c:if test='${numeroLottiOEPV > 0}'>
			var tmpModlicg = null;
			var ripeco=null;
			for(var t=1; t <= getValue("numeroDitte"); t++){
				tmpModlicg = getValue("MODLICG_"+t);
				ripeco = getValue("RIPECO_"+t);
				if(tmpModlicg == "6")
					showObj("colDITG_RIBAUO_" + t, false);
				if(tmpModlicg != "6" || (ripeco != "1" && ripeco !="2"))
					showObj("colDITG_PUNECORIP_" + t, false);
			}
		</c:if>
	</c:if>
	
	<c:if test='${paginaAttivaWizard eq step6Wizard}'>
		var tmpModlicg = null;
		var riptec=null;
		for(var t=1; t <= getValue("numeroDitte"); t++){
			tmpModlicg = getValue("MODLICG_"+t);
			riptec = getValue("RIPTEC_"+t); 
			if(tmpModlicg != "6")
				showObj("colDITG_PUNTEC_" + t, false);
				
			if(tmpModlicg != "6" || (riptec!="1" && riptec!="2"))
					showObj("colDITG_PUNTECRIP_" + t, false);
		}
		var tmpValtec = null;
		for(var t=1; t <= getValue("numeroDitte"); t++){
			tmpValtec = getValue("VALTEC_"+t);
			if(tmpValtec != "1")
				showObj("colDITG_REQMIN_" + t, false);
		}
	</c:if>
		
		//Funzione che blocca l'invio sui campi editabili
		function stopRKey(evt) {
          var evt = (evt) ? evt : ((event) ? event : null);
          var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
          if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
        }
        
        //listaPunteggi = ${listaPunteggi } ;
        arrayPunteggi= new Array();
        <c:forEach items="${listaPunteggi}" var="punteggio" varStatus="indice" >
        	arrayPunteggi[${indice.index}] = "${punteggio}";
        </c:forEach>
        
        //Caricamento dei valori delle soglie
        arraySoglie= new Array();
        <c:forEach items="${listaSoglie}" var="soglia" varStatus="indice" >
        	arraySoglie[${indice.index}] = "${soglia}";
        </c:forEach>
        
        function impostaFiltro(){
			var comando = "href=gare/commons/popup-trova-filtroDitte.jsp";
			var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
		 	comando+="&dittePerPagina=" + risultatiPerPagina;
			comando+="&codiceGara=${codiceGara }";
			openPopUpCustom(comando, "impostaFiltro", 850, 500, "yes", "yes");
		}
		
		function impostaFiltroVerificaValutazione(ngara){
			var tippar;
			<c:if test="${paginaAttivaWizard eq step6Wizard}">
				tippar = "1";
			</c:if>
			<c:if test="${paginaAttivaWizard eq step7Wizard}">
				tippar = "2";
			</c:if>
			var lottoPlicoUnico = "${param.lottoPlicoUnico}";
			var codgar = "${codiceGara}";
			var comando = "href=gare/commons/popup-trova-filtroValutazioneCommissione.jsp";
			var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
			comando+="&dittePerPagina=" + risultatiPerPagina;
			comando+="&ngara=" + ngara;
			comando+="&codgar=" + codgar;
			comando+="&tippar=" + tippar;
			comando+="&lottoPlicoUnico=1";
			openPopUpCustom(comando, "impostaFiltroValutazione", 900, 550, "yes", "yes");
		}
		
		function AnnullaFiltro(){
		 var comando = "href=gare/commons/popup-filtro.jsp&annulla=2";
		 openPopUpCustom(comando, "impostaFiltro", 750, 350, "yes", "yes");
		}	
        
        function apriPopupProspettoPunteggi(chiave){
			var tmp = chiave.split(";");
			var codgar = tmp[0].substring(chiave.indexOf(":")+1);
			var ngara = tmp[1].substring(chiave.indexOf(":"));
			var href="href=gare/gare/popup-gare-prospettoPunteggi.jsp";
			href+="?ngara="+ngara;
			href+="&codgar="+codgar;
			href+="&isOffertaUnica=true";
			openPopUpCustom(href, "prospettoPunteggi", 750, 690, "yes", "yes");
			
		}
		
		<c:if test='${paginaAttivaWizard eq step7Wizard}'>
			var numeroDitte = ${currentRow}+1;
			
			for(var t=0; t < numeroDitte; t++){
				//inizializzazione della label del campo IMPOFF
				
		<c:choose>
           	<c:when test="${updateLista eq 1}">
           		var impsic = toVal(getValue("GARE_IMPSIC_" + (t+1))); 
				var sicinc = toVal(getValue("GARE_SICINC_" + (t+1))); 
           	</c:when>
           	<c:otherwise>
           		var impsic = toVal(getValue("IMPSIC_FIT_" + (t+1))); 
				var sicinc = toVal(getValue("SICINC_FIT_" + (t+1))); 
           	</c:otherwise>
           </c:choose>
				
				if(impsic!=null && impsic!=""){
					var label="Importo offerto presentato dalla ditta";
					if(sicinc=="1")
		              label+=", comprensivo degli oneri sicurezza";
		            else
		              label+=", non comprensivo degli oneri sicurezza";
		            <c:choose>
		            	<c:when test="${updateLista eq 1}">
		            		$('#DITG_IMPOFF_' + (t+1)).prop('title', label);
		            	</c:when>
		            	<c:otherwise>
		            		$('#colDITG_IMPOFF_' + (t+1) + ' span:first').attr('title', label);
		            	</c:otherwise>
		            </c:choose>
		            
				}
			}
		</c:if>
</gene:javaScript>

</gene:redefineInsert>
</gene:template>