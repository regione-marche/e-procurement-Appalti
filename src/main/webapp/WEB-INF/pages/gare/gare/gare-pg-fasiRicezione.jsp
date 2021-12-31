<%
/*
 * Created on: 31-ott-2008
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

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction" parametro="${key}" />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<jsp:include page="./fasiRicezione/defStepWizardFasiRicezione.jsp" />
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<% // Il campo GARE.STEPGAR viene aggiornato solo se assume un valore < step6wizard, %>
<% // poiche' tale valore implica che le fasi di gara siano state attivate. %>
<% // Il campo GARE.FASGAR viene aggiornato seguendo la stessa regola e, per %>
<% // compatibilita' con PWB, assume valore pari a floor(GARE.STEPGAR/10), cioè il %>
<% // piu' grande intero minore o uguale a GARE.STEPGAR/10 %>

<%// Gestione dell'ordinamento delle diverse pagine del wizard: tutte le pagine
  // sono ordinate i campi DITG.NPROGG e DITG.NOMIMO, che di default sono le
  // colonne 4 e 5 rispettivamente. Tuttavia nelle fasi di ricezione con
  // paginaAttivaWizard = -5 o 1, a causa della presenza della colonna 'Opzioni'
  // le colonne di tali campi sono rispettivamente 5 e 6.
%>
<c:if test='${updateLista ne 1}' >
	<c:choose>
	  <c:when test='${((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta)) and autorizzatoModifiche ne 2 and (gene:checkProtFunz(pageContext, "DEL","DEL") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")) }' >
			<c:set var="sortingList" value="5;7" />
		</c:when>
	  <c:when test='${paginaAttivaWizard eq step5Wizard}' >
			<c:set var="sortingList" value="6;7" />
	  </c:when>
		<c:otherwise>
			<c:set var="sortingList" value="5;6" />
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test='${paginaAttivaWizard eq step5Wizard}'>
	<c:set var="plichi" value="plichi" />
</c:if>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request"/>
		<c:set var="ngaraaq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNGARAAQFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request"/>
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
 	</c:when>
 	<c:otherwise>
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"CODGAR"))}' scope="request"/>
		<c:set var="codiceGara" value='${gene:getValCampo(key,"CODGAR")}' />
		<c:if test='${paginaAttivaWizard eq step2Wizard}'>
			<c:set var="varTemp" value="TORN.CODGAR=T:${codiceGara }"/>
			<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, varTemp)}' />
		</c:if>
 	</c:otherwise>
</c:choose>

<c:set var="bloccoPubblicazionePortale11" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext, codiceGara,"BANDO11","false")}' scope="request" />
<c:set var="bloccoPubblicazionePortale13" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext, codiceGara,"BANDO13","false")}' />

<c:choose>
	<c:when test="${itergaMacro eq '2' }">
		<c:set var="bloccoPubblicazionePortale" value="${bloccoPubblicazionePortale13 }" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${bloccoPubblicazionePortale11 eq 'TRUE' || bloccoPubblicazionePortale13 eq 'TRUE'}">
				<c:set var="bloccoPubblicazionePortale" value="TRUE" scope="request"/>
			</c:when>
			<c:otherwise>
				<c:set var="bloccoPubblicazionePortale" value="FALSE" scope="request"/>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara, idconfi)}'/>

<% // Set di una variabile temporanea per costruire il valore delle stringhe     %>
<% // strProtVisualizzaFasiRicezione e strProtModificaFasiRicezione in funzione  %>
<% // dello step del wizard attivo. Questa variabile e' stata introdotta per non %>
<% // modificare i record presenti nella tabella W_OGGETTI (e tabelle collegate  %>
<% // W_AZIONI e W_PROAZI e di tutti di i profili esistenti) in seguito alla     %>
<% // introduzione di nuovi step nel wizard fasi di gara %>


<c:set var="varTmp" value="${paginaAttivaWizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
	<c:set var="whereBusteAttiveWizard" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetBusteDocumentazioneFunction",  pageContext, varTmp,"DOCUMGARA","BUSTA")}'/>
</c:if>

<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.gare.note")}' scope="request"/>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="strProtVisualizzaFasiRicezione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.VIS-FASE${varTmp}" scope="request"/>
		<c:set var="strProtModificaFasiRicezione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.MOD-FASE${varTmp}" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="strProtVisualizzaFasiRicezione" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIRICEZIONE.VIS-FASE${varTmp}" scope="request"/>
		<c:set var="strProtModificaFasiRicezione" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIRICEZIONE.MOD-FASE${varTmp}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:set var="bloccaSelezioneDaElenco" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsGaraConDitteDaElencoFunction",  pageContext, gene:getValCampo(key,"NGARA"))}'/>

<c:if test="${integrazioneWSDM == '1'}" >
	<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext,idconfi)}'/>
</c:if>

<c:set var="isPopolatatW_PUSER" value='${gene:callFunction("it.eldasoft.gene.tags.functions.isPopolatatW_PUSERFunction", pageContext)}' />

<table class="dettaglio-tab-lista">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td style="background-color:#EEEEEE; border: 1px dotted #999999; padding: 2px 0px 2px 2px; display: block;" >
			<c:set var="strPagineVisitate" value=""/>
			<c:set var="strPagineDaVisitare" value=""/>
			<c:forEach items="${pagineVisitate}" var="pagina" >
				<c:set var="strPagineVisitate" value="${strPagineVisitate} -> ${pagina}"/>	
			</c:forEach>
			<c:set var="strPagineVisitate" value="${fn:substring(strPagineVisitate, 4, fn:length(strPagineVisitate))}" />
			<c:if test='${fn:length(pagineDaVisitare) > 0}' >
				<c:forEach items="${pagineDaVisitare}" var="pagina" >
					<c:set var="strPagineDaVisitare" value="${strPagineDaVisitare} -> ${pagina}" />
				</c:forEach>
				<c:set var="strPagineDaVisitare" value="${fn:substring(strPagineDaVisitare, 0, fn:length(strPagineDaVisitare))}" />
			</c:if>
			<span class="avanzamento-paginevisitate"><c:out value="${fn:trim(strPagineVisitate)}" escapeXml="true" /></span>
			<span class="avanzamento-paginedavisitare"><c:out value="${strPagineDaVisitare}" escapeXml="true" /></span>
		</td>
	</tr>

<c:choose>
	<c:when test='${gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}' >
		<c:set var="stileDati" value="" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="stileDati" value="style='display: none;'" scope="request"/>
		<tr>
			<td>
				<br>
				<br>
					<center>Dati non disponibili per il profilo in uso</center>
				<br>
				<br>
			</td>
		</tr>
	</c:otherwise>
</c:choose>

<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard <= step6Wizard}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="whereDITG" value='DITG.NGARA5 = #TORN.CODGAR#' scope="request"/>
		<c:set var="whereGARE" value='GARE.NGARA = #TORN.CODGAR#' scope="request"/>
		<c:set var="codiceGara" value='${gene:getValCampo(key,"CODGAR")}' scope="request"/>
	</c:when>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard > step6Wizard}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="whereDITG" value='DITG.NGARA5 = #TORN.CODGAR#' scope="request"/>
		<c:set var="whereGARE" value='GARE.NGARA = #TORN.CODGAR#' scope="request"/>
		<c:set var="codiceGara" value='${gene:getValCampo(key,"NGARA")}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA#' scope="request"/>
		<c:set var="whereGARE" value='GARE.NGARA = #GARE.NGARA#' scope="request"/>
	</c:otherwise>
</c:choose>

<c:if test='${integrazioneAUR eq "1"}'>
	<c:if test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="valoreChiave" value='${gene:getValCampo(key, "GARE.NGARA")}'/>
		<c:set var="entita" value='GARE'/>
	</c:if>
	<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
		<c:set var="valoreChiave" value='${gene:getValCampo(key, "TORN.CODGAR")}'/>
		<c:set var="entita" value='TORN'/>
	</c:if>
	<c:set var="codStazioneAppaltante" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetStazioneAppaltanteFunction", pageContext,valoreChiave ,entita)}' />
</c:if>

<c:if test='${isGaraLottiConOffertaUnica eq "false"}'>
	<c:set var="codgar1" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
</c:if>


<c:if test="${isProceduraTelematica eq 'true' && paginaAttivaWizard eq step2Wizard }">
	<c:choose>
		<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
			<c:set var="valoreChiave" value="${gene:getValCampo(key,'NGARA') }"/>
		</c:when >
		<c:otherwise>
			<c:set var="valoreChiave" value='${gene:getValCampo(key, "TORN.CODGAR")}'/>
		</c:otherwise>
	</c:choose>
		
	<c:set var="esistonoAcquisizioniOfferteDaElaborareFS10A" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, valoreChiave, "FS10A" )}' />
</c:if>


<c:set var="cifraturaBuste" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneCifraturaBusteFunction", pageContext)}'/>

<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>

<c:if test="${!isProceduraTelematica && paginaAttivaWizard == step4Wizard}">
	<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' scope="request"/>
</c:if>

<c:set var="visualizzareExportDitte" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1164","1","true")}' />
<c:set var="visualizzareImportDitte" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1164","2","true")}' />

<c:if test="${paginaAttivaWizard <= step3Wizard }">
	<c:set var="whereTorn" value="CODGAR='${codiceGara }'"/>
	<c:set var="sortinv" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext,"SORTINV","TORN",whereTorn)}' />
	<c:if test='${sortinv eq 1 && (paginaAttivaWizard eq step2Wizard || paginaAttivaWizard eq step3Wizard)}' >
		<c:set var="sortingList" value="6;5" />
	</c:if>
	
</c:if>

<c:set var="isModificaTelematica" value="${isProceduraTelematica eq 'true'
		 and ((paginaAttivaWizard eq step5Wizard and faseGara eq 1)
	     or (paginaAttivaWizard eq step4Wizard and (faseGara eq -4 or faseGara eq -3))
	     or (paginaAttivaWizard eq step3Wizard and (faseGara eq -3 or empty faseGara))
	     or (paginaAttivaWizard eq step1Wizard and faseGara eq -5)
	     or (paginaAttivaWizard eq step2Wizard and faseGara eq -4))}"/>
<c:set var="isInserimentoTelematica" value="${isProceduraTelematica eq 'true'
		and (faseGara eq -3 or empty faseGara) and isProceduraNegoziata}"/>

<c:choose>
	<c:when test='${empty paginaAttivaWizard or (paginaAttivaWizard < step6Wizard and paginaAttivaWizard ne step4Wizard)}'>

		<c:if test='${((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata )) and bloccoAggiudicazione ne 1}'>
			<c:if test='${not empty numeroMinimoOperatori}'>
				<tr>
					<td ${stileDati} >
						<b>Numero minimo ditte da selezionare, ove esistenti:</b> <span id="numeroMinimoOperatori">${gene:if(numeroMinimoOperatori eq "0", "nessuna limitazione", numeroMinimoOperatori)}</span> 
					</td>
				</tr>
				<tr>
					<td>
						<b>Numero ditte selezionate:</b> <span id="numeroOperatoriSelezionati">${numeroOperatoriSelezionati}</span>
					</td>
				</tr>
			</c:if>
		</c:if>

		<c:if test="${!empty filtroDitte and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}">
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
	
	
	<c:set var="condizioniGestioneBuste" value="${updateLista ne 1 and paginaAttivaWizard eq step2Wizard and isProceduraTelematica eq 'true' and fn:contains(listaOpzioniDisponibili, 'OP114#') and (faseGara eq -4 or faseGara eq -3) and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AcquisisciBustaPreq')}" />
	
	<c:set var = "aggiungiDittaStep3" value='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and gene:checkProtFunz(pageContext, "INS","INS") and bloccoAggiudicazione ne 1
									and isProceduraTelematica eq "true" and isProceduraRistretta and paginaAttivaWizard eq step3Wizard and faseGara eq "-3" and isGaraLottiConOffertaUnica ne "true"}'/>
									
	<tr>
		<td ${stileDati} >
			<gene:formLista entita="DITG" where="${whereDITG} ${filtroFaseRicezione}${filtroDitte }" tableclass="datilista" sortColumn="${sortingList}" pagesize="${requestScope.risultatiPerPagina}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione" gestisciProtezioni="true" >
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
				<gene:redefineInsert name="documentiAzioni" />
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
						
						
						 <c:if test='${((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata )) and bloccoAggiudicazione ne 1}'>
																							
								<c:if test='${integrazioneAUR eq "1" and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.acquisizioneAUR") and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and gene:checkProtFunz(pageContext, "INS","INS") and bloccoAggiudicazione ne 1}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:acquisisciDaAUR();" title='Acquisisci da AUR' tabindex="1500">
												Acquisisci da AUR
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test='${isGaraUsoAlbo and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and bloccoAggiudicazione ne 1
										and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true") }'>
									<c:set var="tipoalgo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPOALGOFunction", pageContext, codiceElenco)}'/>
									<c:if test="${modalitaSelezioneDitteElenco eq 'MAN' or  modalitaSelezioneDitteElenco eq 'MISTA'}">	
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selUltimaAggiudicataria")}'>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:SelUltimaAgg();" title='Sel. da elenco ultima aggiudicataria' tabindex="1501">
														Sel. da elenco ultima aggiudicataria
													</a>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selDaElenco")}'>
											<c:set var="aggnumord" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetAGGNUMORDFunction", pageContext, codiceElenco)}'/>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:SelEleOpEco(0);" title='Sel. da elenco mediante rotazione' tabindex="1501">
														Sel. da elenco mediante rotazione
													</a>
												</td>
											</tr>
										</c:if>
									</c:if>
									<c:if test="${(modalitaSelezioneDitteElenco eq 'AUTO' or  modalitaSelezioneDitteElenco eq 'MISTA') and  gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selDaElenco')}">
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:SelEleOpEco(1);" title='Sel. autom. da elenco mediante rotazione' tabindex="1501">
													Sel. autom. da elenco mediante rotazione
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.EsportaDitteInGara")  and garaLottoUnico and visualizzareExportDitte eq "1"}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:exportDitte();" title='Esporta in formato M-Appalti' tabindex="1500">
												Esporta in formato M-Appalti
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.ImportaDitteInGara") and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and bloccoAggiudicazione ne 1
										and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true") and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") and empty ngaraaq and visualizzareImportDitte eq "1"}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:importDitte();" title='Importa da formato M-Appalti' tabindex="1500">
												Importa da formato M-Appalti
											</a>
										</td>
									</tr>
								</c:if>
								
								

								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and gene:checkProtFunz(pageContext, "INS","INS") and bloccoAggiudicazione ne 1
										and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true") and  (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") and empty ngaraaq}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:aggiungiDitta();" title='Aggiungi ditta da anagrafica' tabindex="1502">
												Aggiungi ditta da anagrafica
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and !empty preced
										and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selDitteRilancio") and isInserimentoTelematica eq "true"}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:SelDitteRilancio();" title='Selezione ditte per rilancio' tabindex="1502">
												Selezione ditte per rilancio
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") and bloccoAggiudicazione ne 1
										and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true") and  (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1503">
														${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
										</td>
									</tr>
								</c:if>
							</c:if>
							
							<c:if test='${aggiungiDittaStep3}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:aggiungiDitta();" title='Aggiungi ditta da anagrafica' tabindex="1502">
											Aggiungi ditta da anagrafica
										</a>
									</td>
								</tr>
							</c:if>
							
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and gene:checkProt(pageContext, strProtModificaFasiRicezione)
								and datiRiga.rowCount > 0 and bloccoAggiudicazione ne 1
								and (isProceduraTelematica ne "true" or isModificaTelematica eq "true")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
											${gene:resource("label.tags.template.dettaglio.schedaModifica")}
											<c:set var="modificaConsentita" value="true"/>
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata)) 
								and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.RiassegnaNumOrdine") and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)
								and (isProceduraTelematica ne "true" or isModificaTelematica eq "true")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:RiassegnaNumOrd();" title='Riassegna numero ordine ${plichi }' tabindex="1505">
											Riassegna numero ordine ${plichi }
										</a>
									</td>
								</tr>
							</c:if>
							
							<c:if test='${paginaAttivaWizard eq step5Wizard and fn:contains(listaOpzioniDisponibili, "OP114#")
								and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AcquisisciOfferteDaPortale") and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}'>
								<c:if test="${isProceduraTelematica eq 'true' and faseGara eq '1'}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:AcquisisciOfferteDaPortale();" title='Acquisisci offerte da portale Appalti' tabindex="1505">
												Acquisisci offerte da portale Appalti
											</a>
										</td>
									</tr>
								</c:if>
							</c:if>
							
							<c:if test='${paginaAttivaWizard eq step1Wizard and fn:contains(listaOpzioniDisponibili, "OP114#") and isProceduraRistretta and isProceduraTelematica eq "true" and faseGara eq "-5"
								and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2  and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}'>
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AcquisisciDomandePartecipazioneDaPortale")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:AcquisisciDomandeDaPortale();" title='Acquisisci domande partecipazione da portale Appalti' tabindex="1506">
												Acquisisci dom.partec. da portale Appalti
											</a>
										</td>
									</tr>
									
								</c:if>
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AttivaAperturaDomandePart")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:AttivaAperturaDomande();" title='Attiva apertura domande partecipazione' tabindex="1507">
												Attiva apertura dom.partecipazione
											</a>
										</td>
									</tr>
								</c:if>
							</c:if>
							<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
							<c:if test='${paginaAttivaWizard eq step1Wizard and isProceduraRistretta and isProceduraTelematica eq "true" and faseGara < 1
								 and autorizzatoModifiche ne 2  and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and fn:contains(listaOpzioniUtenteAbilitate, "ou89#") }'>
								
								<c:choose>
									<c:when test="${isGaraLottiConOffertaUnica eq 'true' }">
										<c:set var="nGara" value="${codiceGara }"/>
										<c:set var="genere" value="3"/>
									</c:when>
									<c:otherwise>
										<c:set var="nGara" value="${gene:getValCampo(key,'NGARA') }"/>
										<c:set var="genere" value="2"/>
									</c:otherwise>
								</c:choose>
								<tr>
										<td class="vocemenulaterale" >
											<a href="javascript:annullaRicezionePlichiDomande(2,'${nGara}',${iterga},'${codiceGara}',${genere});" title='Annulla ricezione dom.partecipazione' tabindex="1508">
												Annulla ricezione dom.partecipazione
											</a>
										</td>
									</tr>
							</c:if>
							<c:if test='${sortinv eq 1 and paginaAttivaWizard eq step2Wizard and isProceduraTelematica eq "true" and faseGara < "-3"
								 and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}'>
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.SorteggioOrdineInvito")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:SorteggioOrdineInvito();" title='Sorteggio ordine invito' tabindex="1509">
												Sorteggio ordine invito
											</a>
										</td>
									</tr>
								</c:if>
							 
							</c:if>								
								
							<c:if test='${paginaAttivaWizard eq step2Wizard and isProceduraTelematica eq "true" and faseGara < "-3"
								and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2  and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}'>
								
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AttivaDitteDaInvitare")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:AttivaElencoDitteInvitare();" title='Attiva elenco ditte da invitare' tabindex="1510">
												Attiva elenco ditte da invitare
											</a>
										</td>
									</tr>
								</c:if>
							</c:if>
									
												
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "ALT","Copia-ditte") 
								and garaLottoUnico eq "false" and isGaraLottiConOffertaUnica eq "false" 
								and ((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata))
								and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true") and  (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA")}'>
								<tr>
									<td class="vocemenulaterale" >
										<a href="javascript:apriPopupCopiaDitte()" title="Copia ditte" tabindex="1511">
										Copia ditte
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1512">
											Imposta filtro
										</a>
									</td>
								</tr>
							</c:if>
								
                            <c:if test='${(paginaAttivaWizard > step1Wizard and not (isProceduraAggiudicazioneAperta or isProceduraNegoziata)) or (paginaAttivaWizard > step3Wizard and isProceduraNegoziata) or (paginaAttivaWizard > step5Wizard and isProceduraAggiudicazioneAperta)}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1513">
											< Fase precedente
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${(paginaAttivaWizard < step6Wizard and paginaAttivaWizard != step1Wizard and paginaAttivaWizard != step2Wizard and (isProceduraTelematica ne "true" or not empty faseGara))
									or (paginaAttivaWizard == step1Wizard and !(isProceduraTelematica eq "true" and (faseGara <-4 or empty faseGara)))
									or (paginaAttivaWizard == step2Wizard and !(isProceduraTelematica eq "true" and (faseGara <-3 or empty faseGara))) }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:avanti();" title='Fase seguente' tabindex="1514">
											Fase seguente >
										</a>
									</td>
								</tr>
							</c:if>
							
						</c:otherwise>
					</c:choose>
				</gene:redefineInsert>
		
		
		<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,chiaveRigaJava)}'/>
		
		<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"4")}' />
		
		<c:set var="modelloSoccoroIstruttorioConfigurato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"56")}' />
		
		<c:set var="primoStep" value='${(paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata)}' />
		
		<c:set var="eliminazionetep3" value='${paginaAttivaWizard eq step3Wizard and isProceduraRistretta and isProceduraTelematica eq "true" and (empty updateLista or updateLista ne 1) and autorizzatoModifiche ne 2 and (gene:checkProtFunz(pageContext, "DEL","DEL") 
			     or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")) and bloccoAggiudicazione ne 1}' />
			
		<c:choose>
			<c:when test='${(empty updateLista or updateLista ne 1) and autorizzatoModifiche ne 2 and (gene:checkProtFunz(pageContext, "DEL","DEL") 
			     or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")) and primoStep  and bloccoAggiudicazione ne 1 }' >
				<gene:set name="titoloMenu">
					<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
				</gene:set>
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" >
					<c:choose>
						<c:when test='${empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA"}'>
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
									<c:if test="${(isProceduraTelematica ne 'true' or isInserimentoTelematica eq 'true' ) and datiRiga.DITG_ACQAUTO ne '1'}">
										<gene:PopUpItem title="Elimina ditta" href="listaElimina()" />
									</c:if>
									<c:if test='${gene:checkProtFunz(pageContext, "ALT","Copia-ditta") and garaLottoUnico eq "false" and isGaraLottiConOffertaUnica eq "false" 
									and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true")}'>
										<gene:PopUpItem title="Copia ditta in altri lotti" href="copiaDitta('${chiaveRigaJava}')" />
									</c:if>
									<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.InvioComunicazione')}">
										<c:set var="inviaComunicazioneAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneInvioComunicazioneFunction",  pageContext, chiaveRigaJava, abilitatoInvioMailDocumentale)}' />
										<gene:PopUpItem title="Invia comunicazione" href="inviaComunicazione('${chiaveRigaJava}','${inviaComunicazioneAbilitato }','${idconfi}')" />
									</c:if>
									
							</gene:PopUp>
						</c:when>
						<c:otherwise>
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InvioComunicazione")}'>
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
									<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.InvioComunicazione')}">
										<c:set var="inviaComunicazioneAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneInvioComunicazioneFunction",  pageContext, chiaveRigaJava, abilitatoInvioMailDocumentale)}' />
										<gene:PopUpItem title="Invia comunicazione" href="inviaComunicazione('${chiaveRigaJava}','${inviaComunicazioneAbilitato }','${idconfi}')" />
									</c:if>
							</gene:PopUp>
							</c:if>
						</c:otherwise>
					</c:choose>
					

					<c:if test="${(isProceduraTelematica ne 'true' or isInserimentoTelematica eq 'true') and(empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq 'MAN' or (modalitaSelezioneDitteElenco eq 'MISTA' and datiRiga.DITG_ACQAUTO ne '1')) }">
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
			</c:when>
			<c:when test='${(empty updateLista or updateLista ne 1) and (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InvioComunicazione") || gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.OffertaRaggruppamentoTemporaneo") || eliminazionetep3 || 
					(gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio")  and paginaAttivaWizard == step2Wizard))}' >
				<gene:campoLista title='' width='25'  >
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
						<c:if test="${datiRiga.DITG_ACQUISIZIONE eq 8 and modificaConsentita eq 'true' and paginaAttivaWizard eq step3Wizard}">
							<gene:PopUpItem title="Elimina ditta" href="listaElimina()" />
						</c:if>
						<c:if test="${isProceduraTelematica ne 'true' and tipoImpresa != '3' and tipoImpresa != '10' and (empty datiRiga.V_DITGAMMIS_AMMGAR or (datiRiga.V_DITGAMMIS_AMMGAR != '2' and datiRiga.V_DITGAMMIS_AMMGAR != '6' and datiRiga.V_DITGAMMIS_AMMGAR != '9')) 
							and isProceduraAggiudicazioneAperta ne true and paginaAttivaWizard eq step5Wizard and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.OffertaRaggruppamentoTemporaneo') and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2}">
							<gene:PopUpItem title="Presenta offerta in RT" href="presentaOffertaRaggruppamentoTemporaneo('${chiaveRigaJava}', '${tipoImpresa }')" />
						</c:if>
						<c:if test='${paginaAttivaWizard == step2Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}'>
							<c:set var="numAvvalimentiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroAvvalimentiDittaFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA5"),gene:getValCampo(chiaveRigaJava, "DITTAO"))}' />
							<c:choose>
								<c:when test="${numAvvalimentiDitta ne '0'}">
									<c:set var="numAvvalimentiDitta" value="(${numAvvalimentiDitta})"/>
								</c:when>
								<c:otherwise>
									<c:set var="numAvvalimentiDitta" value=""/>
								</c:otherwise>
							</c:choose>
							<gene:PopUpItem title="Avvalimento ${numAvvalimentiDitta}" href="dettaglioAvvalimento('${chiaveRigaJava}')" />
						</c:if>
						<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.InvioComunicazione') and autorizzatoModifiche ne 2}">
							<c:set var="inviaComunicazioneAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneInvioComunicazioneFunction",  pageContext, chiaveRigaJava, abilitatoInvioMailDocumentale)}' />
							<gene:PopUpItem title="Invia comunicazione" href="inviaComunicazione('${chiaveRigaJava}','${inviaComunicazioneAbilitato }','${idconfi}')" />
						</c:if>
						<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio') && paginaAttivaWizard == step2Wizard && autorizzatoModifiche ne 2}">
							<c:set var="statoSoccIstr" value="${datiRiga.V_DITGAMMIS_AMMGAR}"/>
							<c:if test="${statoSoccIstr ne 10 and isGaraLottiConOffertaUnica eq 'true'}">
								<c:set var="whereSoccorso" value="CODGAR='${codiceGara }' and DITTAO='${datiRiga.DITG_DITTAO}' and CODGAR!=NGARA and FASGAR=${varTmp} and AMMGAR='10'"/>
								<c:set var="numLottiSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(CODGAR)","V_DITGAMMIS", whereSoccorso)}'/>
								<c:if test="${not empty numLottiSoccorso and numLottiSoccorso ne 0 }">
									<c:set var="statoSoccIstr" value="10"/>
								</c:if>
							</c:if>
							<c:if test="${statoSoccIstr eq 10 }">
								<gene:PopUpItem title="Richiedi soccorso istruttorio" href="richiestaSoccorsoIstruttorio('${chiaveRigaJava}','${modelloSoccoroIstruttorioConfigurato }','${idconfi}','${numeroModello }','${varTmp}')" />
							</c:if>
						</c:if>
					</gene:PopUp>
				</gene:campoLista>
			</c:when>
			<c:when test='${(empty updateLista or updateLista ne 1) and paginaAttivaWizard == step2Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}' >
				<gene:campoLista title='' width='25'  >
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
						<c:set var="numAvvalimentiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroAvvalimentiDittaFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA5"),gene:getValCampo(chiaveRigaJava, "DITTAO"))}' />
						<c:choose>
							<c:when test="${numAvvalimentiDitta ne '0'}">
								<c:set var="numAvvalimentiDitta" value="(${numAvvalimentiDitta})"/>
							</c:when>
							<c:otherwise>
								<c:set var="numAvvalimentiDitta" value=""/>
							</c:otherwise>
						</c:choose>
						<gene:PopUpItem title="Avvalimento ${numAvvalimentiDitta}" href="dettaglioAvvalimento('${chiaveRigaJava}')" />
					</gene:PopUp>
				</gene:campoLista>
			</c:when>
			<c:otherwise>
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
			</c:otherwise>
		</c:choose>

				<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NGARA5"  visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista title="N." campo="NPROGG" headerClass="sortable" width="32" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroProgressivoDITG" visibile="${paginaAttivaWizard ne step5Wizard}" />
				<gene:campoLista title="N.invito" campo="NUMORDINV" headerClass="sortable" width="50" visibile="${sortinv eq 1 && (paginaAttivaWizard eq step2Wizard || paginaAttivaWizard eq step3Wizard)}" />
				<gene:campoLista title="N.pl" campo="NUMORDPL" headerClass="sortable" width="32" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroProgressivoDITG" visibile="${paginaAttivaWizard eq step5Wizard}"/>
				<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
				<gene:campoLista campo="NOMIMO" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' />
				<gene:campoLista campo="DRICIND" title="Data pres. dom.part." headerClass="sortable" visibile="${paginaAttivaWizard eq step1Wizard && ((isProceduraTelematica && updateLista ne 1) || !isProceduraTelematica)}" edit="${updateLista eq 1 }" />
				<gene:campoLista campo="ORADOM" title="Ora" visibile="${paginaAttivaWizard eq step1Wizard && ((isProceduraTelematica && updateLista ne 1) || !isProceduraTelematica)}" edit="${updateLista eq 1 }" />
				<c:if test='${paginaAttivaWizard eq step1Wizard and isProceduraTelematica && updateLista eq 1}' >
					<gene:campoLista title="Data" campo="DRICIND_FIT" campoFittizio="true" definizione="D;0;;DATA_ELDA;DRICIND" edit="false" value="${datiRiga.DITG_DRICIND}"/>
					<gene:campoLista title="Ora" campo="ORADOM_FIT" campoFittizio="true" definizione="T6;0;;;G1ORARDOM" edit="false" value="${datiRiga.DITG_ORADOM}"/>
				</c:if>
				<gene:campoLista title="NumProgDittaFittizio" campo="NUM_PROGG" entita="DITG" visibile="false" campoFittizio="true" definizione="N7" edit="${updateLista eq 1}" value="${datiRiga.DITG_NPROGG}" computed="true" />
				<gene:campoLista title="NumOrdPlicoFittizio" campo="NUM_ORDPL" entita="DITG" visibile="false" campoFittizio="true" definizione="N7" edit="${updateLista eq 1}" value="${datiRiga.DITG_NUMORDPL}" computed="true" />
				<gene:campoLista campo="NPRDOM" title="N.prot." width="40" headerClass="sortable" visibile="${paginaAttivaWizard eq step1Wizard and updateLista ne 1}" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DPRDOM" title="Data prot." headerClass="sortable" visibile="${paginaAttivaWizard eq step1Wizard and updateLista ne 1}" edit="${updateLista eq 1}" />
				<c:if test='${paginaAttivaWizard eq step1Wizard and updateLista eq 1}' >
					<gene:campoLista campo="NPRDOM_FIT" title="N.prot." campoFittizio="true" width="40" definizione="T10;0;;;G1NPRDOM" value="${datiRiga.DITG_NPRDOM}"  />
					<gene:campoLista  campo="DPRDOM_FIT" title="Data prot." campoFittizio="true" definizione="T25;0;;TIMESTAMP;G1DPRDOM" value="${datiRiga.DITG_DPRDOM}" />
					
				</c:if>
				<c:if test='${(paginaAttivaWizard eq step1Wizard or paginaAttivaWizard eq step2Wizard) and updateLista eq 1}' >
					<gene:campoLista  campo="DPRDOM_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="${datiRiga.DITG_DPRDOM}" edit="true" visibile="false"/>
				</c:if>
				<gene:campoLista campo="INVOFF" headerClass="sortable" visibile='${paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta ne "true"}' edit="${updateLista eq 1}" />
				<gene:campoLista campo="DATOFF" title="Data pres. offerta" headerClass="sortable" visibile="${paginaAttivaWizard eq step5Wizard && ((isProceduraTelematica && updateLista ne 1) || !isProceduraTelematica)}" edit="${updateLista eq 1 }" />
				<gene:campoLista campo="ORAOFF" title="Ora" headerClass="sortable" visibile="${paginaAttivaWizard eq step5Wizard && ((isProceduraTelematica && updateLista ne 1) || !isProceduraTelematica)}" edit="${updateLista eq 1 }" />
					<c:if test='${paginaAttivaWizard eq step5Wizard and isProceduraTelematica && updateLista eq 1}' >
					<gene:campoLista title="Data pres. offerta" campo="DATOFF_FIT" campoFittizio="true" definizione="D;0;;DATA_ELDA;G1DATOFF" edit="false" value="${datiRiga.DITG_DATOFF}"/>
					<gene:campoLista title="Ora" campo="ORAOFF_FIT" campoFittizio="true" definizione="T6;0;;;G1ORAOFF" edit="false" value="${datiRiga.DITG_ORAOFF}"/>
				</c:if>
				<gene:campoLista campo="NPROFF" title="N.prot." headerClass="sortable" visibile="${paginaAttivaWizard eq step5Wizard and updateLista ne 1}" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DPROFF" title="Data prot." headerClass="sortable" visibile="${paginaAttivaWizard eq step5Wizard and updateLista ne 1}" edit="${updateLista eq 1}" />
				<c:if test='${paginaAttivaWizard eq step5Wizard and updateLista eq 1}' >
					<gene:campoLista campo="NPROFF_FIT" title="N.prot." campoFittizio="true" width="40" definizione="T10;0;;;G1NPROFF" value="${datiRiga.DITG_NPROFF}" />
					<gene:campoLista  campo="DPROFF_FIT" title="Data prot." campoFittizio="true" definizione="T25;0;;TIMESTAMP;G1DPROFF" value="${datiRiga.DITG_DPROFF}"/>
					<gene:campoLista  campo="DPROFF_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="${datiRiga.DITG_DPROFF}" edit="true" visibile="false"/>
				</c:if>	
					
				<!-- il campo AMMGAR viene gestito nel gestore di submit -- gene-:-campoLista campo="AMMGAR" width="80" headerClass="sortable" visibile="${paginaAttivaWizard eq -5 or paginaAttivaWizard eq -4 or paginaAttivaWizard eq 1}" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" width="80" headerClass="sortable" ordinabile ="true" visibile="${paginaAttivaWizard eq step2Wizard}" edit="${updateLista eq 1}" />

				<gene:campoLista campo="INVGAR" headerClass="sortable" visibile="${paginaAttivaWizard eq step3Wizard}" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NPROTG" width="40" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DINVIG" title="Data" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="PARTGAR" title="Partecipa al lotto?" headerClass="sortable" edit="${updateLista eq 1}" visibile="false" />
				
				<c:set var="estensioneDisabilitata" value=''/>
				<c:set var="linkAbilitato" value='true'/>
				<c:if test="${condizioniGestioneBuste}">
					<c:set var="isBustaElaborata" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsBustaElaborataFunction", pageContext,datiRiga.DITG_NGARA5,datiRiga.DITG_DITTAO, "FS10A")}' />
					<c:if test="${isBustaElaborata eq 'No' }">
						<c:set var="estensioneDisabilitata" value='-disabilitato'/>
						<c:set var="linkAbilitato" value='false'/>
					</c:if>
				</c:if>
					
				
								
				<c:if test='${paginaAttivaWizard eq step2Wizard && updateLista ne 1 && isGaraLottiConOffertaUnica eq "true"}' >
					<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${linkAbilitato}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioPartecipazioneLotti('${chiaveRigaJava}',${bustalotti });" title="Dettaglio partecipazione ai lotti" ></c:if>
							<img width="16" height="16" title="Dettaglio partecipazione ai lotti" alt="Dettaglio partecipazione ai lotti" src="${pageContext.request.contextPath}/img/partecipazioneAiLotti${estensioneDisabilitata }.png"/>
						<c:if test="${linkAbilitato}"></a></c:if>
					</gene:campoLista>
					
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioAmmissioneDittaLotti")}' >
						<gene:campoLista title="&nbsp;" width="20" >
							<c:if test="${linkAbilitato}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioAmmissioneLotti('${chiaveRigaJava}',${bustalotti });" title="Dettaglio ammissione ai lotti" ></c:if>
								<img width="16" height="16" title="Dettaglio ammissione ai lotti" alt="Dettaglio ammissione ai lotti" src="${pageContext.request.contextPath}/img/ammissioneAiLotti${estensioneDisabilitata }.png"/>
							<c:if test="${linkAbilitato}"></a></c:if>
						</gene:campoLista>
					</c:if>
				</c:if>
				
								
				<c:if test='${paginaAttivaWizard eq step2Wizard and (updateLista ne 1) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.VerificaDocumenti")}' >
					<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${linkAbilitato}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocRichiesti('${chiaveRigaJava}','VERIFICA');" title="Verifica documenti richiesti" ></c:if>
							<img width="16" height="16" title="Verifica documenti richiesti" alt="Verifica documenti richiesti" src="${pageContext.request.contextPath}/img/documentazione${estensioneDisabilitata }.png"/>
						<c:if test="${linkAbilitato}"></a></c:if>
					</gene:campoLista>
				</c:if>
				<c:if test="${((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata)) 
					 and (updateLista ne 1) and !empty codiceElenco and codiceElenco != '' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.VerificaDocumentiSelezioneDitteElenco')}">
					<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${datiRiga.DITG_ACQUISIZIONE == 3 }">
							<a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocRichiesti('${chiaveRigaJava}','CONSULTAZIONE');" title="'Consultazione documenti iscrizione a elenco o catalogo" >
								<img width="16" height="16" title="Consultazione documenti iscrizione a elenco o catalogo" alt="Consultazione documenti iscrizione a elenco o catalogo" src="${pageContext.request.contextPath}/img/documentazione_elenco.png"/>
							</a>
						</c:if>
					</gene:campoLista>
				</c:if>
				
				
				<c:if test="${paginaAttivaWizard < step6Wizard}" >
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
						<c:if test="${linkAbilitato}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';ulterioriCampi(${currentRow+1}, '${chiaveRigaJava}');" title="Ulteriori dettagli" ></c:if>
							<img width="16" height="16" title="${iconaTooltip}" alt="Ulteriori dettagli" src="${pageContext.request.contextPath}/img/opzioni${note}${estensioneDisabilitata}.png"/>
						<c:if test="${linkAbilitato}"></a></c:if>
					</gene:campoLista>
				</c:if>
				
				<c:if test="${condizioniGestioneBuste}" >
					<gene:campoLista title="&nbsp;" width="20">
						<c:choose>
							<c:when test="${isBustaElaborata eq 'No' }">
								<c:if test="${autorizzatoModifiche ne 2}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';aperturaBuste('${chiaveRigaJava}', 'FS10A');" title="Busta prequalifica da acquisire" ></c:if>
									<img width="16" height="16" title="Busta prequalifica da acquisire" alt="Busta prequalifica da acquisire" src="${pageContext.request.contextPath}/img/bustaChiusa.png"/>
								<c:if test="${autorizzatoModifiche ne 2}"></a></c:if>
							</c:when>
							<c:when test="${isBustaElaborata eq 'NonEsiste' }">
								<img width="16" height="16" title="Busta prequalifica non presentata" alt="Busta prequalifica non presentata" src="${pageContext.request.contextPath}/img/bustaApertaVuota.png"/>
							</c:when>
							<c:otherwise>
								<img width="16" height="16" title="Busta prequalifica acquisita" alt="Busta prequalifica acquisita" src="${pageContext.request.contextPath}/img/bustaAperta.png"/>
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
				</c:if>
				
				
						
				<c:if test="${((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata)) and updateLista ne 1 and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.NoteAvvisiImpresa')}" >
					<c:set var="numAvvalimentiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroAvvalimentiDittaFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA5"),gene:getValCampo(chiaveRigaJava, "DITTAO"))}' />
					<c:set var="result" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.CheckNoteAvvisiImpresaPartecipantiFunction", pageContext,datiRiga.DITG_DITTAO, datiRiga.DITG_NGARA5)}' />
					<gene:campoLista title="&nbsp;" width="20" >
						<c:choose>
							<c:when test="${numAvvalimentiDitta ne '0'}">
								<c:set var="msgAvvalimenti" value="La ditta ha fatto ricorso all'avvalimento. "/>
							</c:when>
							<c:otherwise>
								<c:set var="msgAvvalimenti" value=""/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${controlloNoteAvvisi eq 1}">
								<c:set var="msgNote" value="Nell'anagrafica della ditta sono presenti note o avvisi in stato 'aperto'. "/>
							</c:when>
							<c:when test="${controlloNoteAvvisi eq 2}">
								<c:set var="msgNote" value="Nell'anagrafica del raggruppamento o delle ditte componenti il raggruppamento sono presenti note o avvisi in stato 'aperto'. "/>
							</c:when>
							<c:when test="${controlloNoteAvvisi eq 3}">
								<c:set var="msgNote" value="Nell'anagrafica del consorzio o delle consorziate designate come esecutrici sono presenti note o avvisi in stato 'aperto'. "/>
							</c:when>
							<c:otherwise>
								<c:set var="msgNote" value=""/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${controlloComponenti eq 1}">
								<c:set var="msgComponenti" value="Nell'anagrafica del raggruppamento non sono state specificate le ditte componenti (indicare almeno due ditte). "/>
							</c:when>
							<c:otherwise>
								<c:set var="msgComponenti" value=""/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${contolloMandataria eq 1}">
								<c:set var="msgMandataria" value="Nell'anagrafica del raggruppamento non è stata specificata la mandataria."/>
							</c:when>
							<c:otherwise>
								<c:set var="msgMandataria" value=""/>
							</c:otherwise>
						</c:choose>
						
						<c:if test="${msgAvvalimenti ne '' &&  msgNote ne ''}">
							<c:set var="msgNote" value="&#13;&#13;${msgNote }"/>
						</c:if>
						
						<c:if test="${(msgAvvalimenti ne '' || msgNote ne '') &&  msgComponenti ne ''}">
							<c:set var="msgComponenti" value="&#13;&#13;${msgComponenti }"/>
						</c:if>
						
						<c:if test="${(msgAvvalimenti ne '' || msgNote ne '' ||  msgComponenti ne '') &&  msgMandataria ne ''}">
							<c:set var="msgMandataria" value="&#13;&#13;${msgMandataria }"/>
						</c:if>
						
						<c:choose>
							<c:when test='${msgNote ne "" || msgComponenti ne "" || msgMandataria ne ""  || msgAvvalimenti ne "" }'>
								<img width="16" height="16" title="${msgAvvalimenti}${msgNote}${msgComponenti}${msgMandataria}" alt="${msgAvvalimenti}${msgNote}${msgComponenti}${msgMandataria}" src="${pageContext.request.contextPath}/img/noteAvvisiImpresa.png"/>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
				</c:if>
				<c:if test="${ not empty urlWsArt80 and urlWsArt80 ne '' and updateLista ne 1 and (paginaAttivaWizard eq step2Wizard or paginaAttivaWizard eq step3Wizard)}">
					<jsp:include page="/WEB-INF/pages/gare/gare/gare-colonna-art80.jsp">
						<jsp:param name="ditta" value="${datiRiga.DITG_DITTAO }"/>
					</jsp:include>
				</c:if>
				
				<c:if test="${(paginaAttivaWizard eq step3Wizard and isProceduraNegoziata) and updateLista ne 1}">
					<c:if test="${isPopolatatW_PUSER == 'SI'}">
						<c:choose>
							<c:when test="${tipoImpresa eq '3' or tipoImpresa eq '10'}">
								<c:set var="dittaoIcona" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMandatariaRTFunction",  pageContext, gene:getValCampo(chiaveRigaJava, "DITTAO") )}'/>
							</c:when>
							<c:otherwise>
								<c:set var="dittaoIcona" value='${gene:getValCampo(chiaveRigaJava, "DITTAO")}'/>
							</c:otherwise>
						</c:choose>
						<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, dittaoIcona )}'/>
						<gene:campoLista title="&nbsp;" width="20" >
							<c:if test="${impresaRegistrata == 'SI'}">
								<img width="16" height="16" title="Ditta registrata su portale" alt="Ditta registrata su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>
							</c:if>
						</gene:campoLista>
					</c:if>
				</c:if>
				<!-- il campo DITG.FASGAR viene gestito nel gestore di submit gene-:-campoLista campo="FASGAR" visibile="false" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
				<!-- il campo DITG.MOTIES viene gestito nel gestore di submit gene-:-campoLista campo="MOTIES" visibile="false" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
				<!-- il campo DITG.ANNOFF viene gestito nel gestore di submit gene-:-campoLista campo="ANNOFF" visibile="false" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />

				<gene:campoLista campo="RIBAUO" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="IMPOFF" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="ESTIMP" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="MEZOFF" visibile="false" edit="${updateLista eq 1}" />
								
				<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="PUNTEC" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="PLIDOM" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NOTPDOM" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="PLIOFF" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NOTPOFF" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="IMPSICAZI" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="IMPMANO" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="RTOFFERTA" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="IMPPERM" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="IMPCANO" visibile="false" edit="${updateLista eq 1}" />
				
				<gene:campoLista campo="ESCLUDI_DITTA_ALTRI_LOTTI" headerClass="sortable" visibile="false" value="0" campoFittizio="true" definizione="N2" edit="${updateLista eq 1}"/>

				<% // Questo campo non viene MAI modificato nella pagina e la sua valorizzazione viene gestito solo lato server. %>
				<% // E' presente nella pagina come campo hidden per capire se il valore del campo V_DITGAMMIS.AMMGAR e' ereditato %>
				<% // da una fase precedente o e' effettivamente un valore esistente per lo step del wizard in visualizzazione %>
				<gene:campoLista campo="AMMGAR" entita="DITGAMMIS" where="DITGAMMIS.CODGAR=DITG.CODGAR5 and DITGAMMIS.NGARA=DITG.NGARA5 and DITGAMMIS.DITTAO=DITG.DITTAO and DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}"/>
			
			<c:if test='${updateLista eq 0}' >
				<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
				
			</c:if>
				<gene:campoLista campo="ACQUISIZIONE" visibile="false" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="TIPRIN" visibile="false" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="AMMGAR" visibile="false" />
				<gene:campoLista campo="DITG_INVGAR_FIT" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_INVGAR}"/>
				<gene:campoLista campo="ACQAUTO" visibile="false" />
								
				<input type="hidden" id="isProceduraAggiudicazioneAperta" name="isProceduraAggiudicazioneAperta" value="${isProceduraAggiudicazioneAperta}" />
				<input type="hidden" id="isProceduraNegoziata" name="isProceduraNegoziata" value="${isProceduraNegoziata}" />
				<input type="hidden" id="isProceduraRistretta" name="isProceduraRistretta" value="${isProceduraRistretta}" />
				
				<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
				<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
				<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
				<input type="hidden" name="DTEOFF" id="DTEOFF" value="${dataTerminePresentazioneOfferta}" />
				<input type="hidden" name="OTEOFF" id="OTEOFF" value="${oraTerminePresentazioneOfferta}" />
				<input type="hidden" name="DTEPAR" id="DTEPAR" value="${dataTermineRichiestaPartecipazione}" />
				<input type="hidden" name="OTEPAR" id="OTEPAR" value="${oraTermineRichiestaPartecipazione}" />
				
				<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
				<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
				<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
				
				<gene:campoLista visibile="false">
					<input type="hidden" name="counter" id="counter" value="${datiRiga.rowCount}" />
				</gene:campoLista >
			</gene:formLista>
		</td>
	</tr>

	</c:when>
	<c:when test='${paginaAttivaWizard eq step4Wizard and isGaraLottiConOffertaUnica ne "true"}'>
		<!-- pagina a scheda: Fase Inviti -->
		<jsp:include page="./fasiRicezione/fasiRicezione-Inviti.jsp" />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step4Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		<!-- pagina a scheda: Fase Inviti per le gare a lotti con offerta unica -->
		<jsp:include page="./fasiRicezione/fasiRicezione_Inviti-OffertaUnica.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:include page="./fasiRicezione/fasiRicezione-ChiusuraRicezioneOfferte.jsp" />
	</c:otherwise>
</c:choose>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${(modo eq "MODIFICA" or updateLista eq 1) and bloccoAggiudicazione ne 1}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
					<c:if test='${paginaAttivaWizard eq step4Wizard and numDocAttesaFirma > 0}'>
						<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:historyReload();">
					</c:if>
										
					<c:if test='${bloccoAggiudicazione ne 1 and ((paginaAttivaWizard < step6Wizard and datiRiga.rowCount > 0) or (paginaAttivaWizard eq step4Wizard and (tipoDoc eq 1 || (tipoDoc ne 1 and tipoDoc ne 10 and bloccoPubblicazioneEsitoPortale ne "SI" and bloccoPubblicazionePortale ne "SI")
							|| (tipoDoc eq 10 and bloccoPubblicazioneEsitoPortale ne "SI" and bloccoPubblicazionePortale ne "SI" and bloccoPubblicazionePortale11 ne "TRUE"))))
							and ((isProceduraTelematica ne "true" and !(applicareBloccoPubblicazioneGareNonTelematiche eq "1" and bloccoPubblicazionePortale eq "SI")) or isModificaTelematica eq "true") and !(paginaAttivaWizard eq step4Wizard and condizioneModificaSezioneProfilo ne true)}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and gene:checkProt(pageContext, strProtModificaFasiRicezione)}'>
							
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaLista();">&nbsp;&nbsp;&nbsp;
						</c:if>
					</c:if>
					<c:if test='${(paginaAttivaWizard > step1Wizard and  not (isProceduraAggiudicazioneAperta or isProceduraNegoziata)) or (paginaAttivaWizard > step3Wizard and isProceduraNegoziata) or (paginaAttivaWizard > step5Wizard and isProceduraAggiudicazioneAperta)}' >
						<INPUT type="button"  class="bottone-azione" value='< Fase precedente' title='Fase precedente' onclick="javascript:indietro();">
					</c:if>
					<c:choose>
						<c:when test='${not empty isGaraLottiConOffertaUnica and isGaraLottiConOffertaUnica eq "true" and (paginaAttivaWizard < step4Wizard and paginaAttivaWizard != step1Wizard and !(paginaAttivaWizard eq step2Wizard and isProceduraTelematica eq "true" and faseGara < "-3") and ((isProceduraTelematica ne "true" or not empty faseGara))
								or (paginaAttivaWizard == step1Wizard and !(isProceduraTelematica eq "true" and (faseGara <-4 or empty faseGara))))}'>
							<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">
						</c:when>
						<c:when test='${(paginaAttivaWizard < step6Wizard and paginaAttivaWizard != step1Wizard and paginaAttivaWizard != step2Wizard and paginaAttivaWizard != step4Wizard and (isProceduraTelematica ne "true" or not empty faseGara))
							 or (paginaAttivaWizard == step4Wizard and !(isProceduraTelematica eq "true" and faseGara <1))
							 or (paginaAttivaWizard == step1Wizard and !(isProceduraTelematica eq "true" and (faseGara <-4 or empty faseGara)))
							 or (paginaAttivaWizard == step2Wizard and !(isProceduraTelematica eq "true" and (faseGara <-3 or empty faseGara)))}'>
							<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">						
						</c:when>
					</c:choose>

					<c:if test='${paginaAttivaWizard eq step4Wizard and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2 and fn:contains(listaOpzioniDisponibili, "OP114#")
					   and (itergaMacro eq "3" or itergaMacro eq "2") and bloccoPubblicazionePortale eq "FALSE" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.RICINV.PubblicaSuAreaRiservata")
					   and (faseGara eq "-3" || faseGara eq "-4")}' >
					   	<c:choose>
						   <c:when test='${isProceduraTelematica eq "true" and meruolo eq "1"}'>
								<br><br>
								<INPUT type="button"  class="bottone-azione" value='Invia invito e pubblica su portale Appalti' title='Invia invito e pubblica su portale Appalti' onclick="javascript:pubblicaSuAreaRiservata();">&nbsp;
							</c:when>
							<c:when test='${isProceduraTelematica ne "true"}'>
								<br><br>
								<INPUT type="button"  class="bottone-azione" value='Pubblica su portale Appalti' title='Pubblica su portale Appalti' onclick="javascript:pubblicaSuAreaRiservata();">&nbsp;
							</c:when>
						</c:choose>
					</c:if>	

					<c:if test='${paginaAttivaWizard eq step5Wizard and fn:contains(listaOpzioniDisponibili, "OP114#")
						and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AcquisisciOfferteDaPortale") and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}'>
						<c:if test="${isProceduraTelematica eq 'true' and faseGara eq '1'}">
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Acquisisci offerte da portale Appalti' title='Acquisisci offerte da portale Appalti' onclick="javascript:AcquisisciOfferteDaPortale();">&nbsp;
						</c:if>
					</c:if>
					
					<c:if test='${paginaAttivaWizard eq step1Wizard and fn:contains(listaOpzioniDisponibili, "OP114#") and isProceduraRistretta and isProceduraTelematica eq "true" and faseGara eq "-5" 
						and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2  and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)}'>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AcquisisciDomandePartecipazioneDaPortale") }'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Acquisisci domande partecipazione da portale Appalti' title='Acquisisci domande partecipazione da portale Appalti' onclick="javascript:AcquisisciDomandeDaPortale();">&nbsp;
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AttivaAperturaDomandePart") }'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Attiva apertura domande partecipazione' title='Attiva apertura domande partecipazione' onclick="javascript:AttivaAperturaDomande();">&nbsp;
						</c:if>
					</c:if>
					
					<c:if test='${paginaAttivaWizard eq step2Wizard and isProceduraTelematica eq "true" and faseGara < "-3"
								and bloccoAggiudicazione ne 1 and autorizzatoModifiche ne 2  and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and 
								gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AttivaDitteDaInvitare")}'>
							<br><br>
						<c:if test='${sortinv eq 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.SorteggioOrdineInvito")}'>	
							<INPUT type="button"  class="bottone-azione" value='Sorteggio ordine invito' title='Sorteggio ordine invito' onclick="javascript:SorteggioOrdineInvito();">&nbsp;						
						</c:if>
						<INPUT type="button"  class="bottone-azione" value='Attiva elenco ditte da invitare' title='Attiva elenco ditte da invitare' onclick="javascript:AttivaElencoDitteInvitare();">&nbsp;
					</c:if>
					
					<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AttivaAperturaDoc') and autorizzatoModifiche ne 2}">
						<c:if test='${paginaAttivaWizard > step5Wizard and faseRicezioneChiusa eq 1 and bloccoAggiudicazione ne 1 and isProceduraTelematica ne "true"}'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Disattiva apertura documentazione amministrativa' title='Disattiva apertura documentazione amministrativa' onclick="javascript:confermaChiusuraRicezione('DISATTIVA');">&nbsp;
						</c:if>
						<c:if test='${paginaAttivaWizard > step5Wizard and faseRicezioneChiusa eq 2 and bloccoAggiudicazione ne 1}'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Attiva apertura documentazione amministrativa' title='Attiva apertura documentazione amministrativa' onclick="javascript:confermaChiusuraRicezione('ATTIVA');">&nbsp;
						</c:if>
					</c:if>

					<c:if test='${((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata)) and bloccoAggiudicazione ne 1}'>
						<c:if test='${integrazioneAUR eq "1" and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.acquisizioneAUR") and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and gene:checkProtFunz(pageContext, "INS", "INS")}'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Acquisisci da AUR' title='Acquisisci da AUR' onclick="javascript:acquisisciDaAUR();">&nbsp;&nbsp;&nbsp;
						</c:if>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) 
								and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true") and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA")}'>
							<br><br>
							<c:if test='${gene:checkProtFunz(pageContext, "INS", "INS") and empty ngaraaq}'>
								<INPUT type="button"  class="bottone-azione" value='Aggiungi ditta da anagrafica' title='Aggiungi ditta da anagrafica' onclick="javascript:aggiungiDitta();">&nbsp;&nbsp;&nbsp;
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">&nbsp;&nbsp;
							</c:if>
						</c:if>
						
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) and !empty preced
										and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selDitteRilancio") and isProceduraNegoziata and isInserimentoTelematica eq "true"}'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Selezione ditte per rilancio' title='Selezione ditte per rilancio' onclick="javascript:SelDitteRilancio();">
						</c:if>
						<c:if test='${isGaraUsoAlbo and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione)
							and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true")}'>
							<br><br>
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selDaElenco")}'>
								<c:set var="aggnumord" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetAGGNUMORDFunction", pageContext, codiceElenco)}'/>
								<c:choose>
									<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
										<c:set var="ent" value='GARE'/>
										<c:set var="chiave" value='${gene:getValCampo(key,"NGARA")}' />
									</c:when>
								 	<c:otherwise>
										<c:set var="ent" value='TORN'/>
										<c:set var="chiave" value='${gene:getValCampo(key,"CODGAR")}' />
								 	</c:otherwise>
								</c:choose>
								<c:set var="stazioneAppaltante" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetStazioneAppaltanteFunction", pageContext, chiave, ent)}'/>
								
								<c:if test='${modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA"}'>
										<INPUT type="button"  class="bottone-azione" value='Sel. da elenco mediante rotazione' title='Sel. da elenco mediante rotazione' onclick="javascript:SelEleOpEco(0);">
								</c:if>
								<c:if test='${modalitaSelezioneDitteElenco eq "AUTO" or  modalitaSelezioneDitteElenco eq "MISTA"}'>
										<INPUT type="button"  class="bottone-azione" value='Sel. autom. da elenco mediante rotazione' title='Sel. autom. da elenco mediante rotazione' onclick="javascript:SelEleOpEco(1);">
								</c:if>
							</c:if>
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.EsportaDitteInGara")  and garaLottoUnico and visualizzareExportDitte eq "1"}'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Esporta in formato M-Appalti' title='Esporta in formato M-Appalti' onclick="javascript:exportDitte();">&nbsp;&nbsp;
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.ImportaDitteInGara") and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiRicezione) 
								and (isProceduraTelematica ne "true" or isInserimentoTelematica eq "true") and  (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") and visualizzareImportDitte eq "1"}'>
							<br><br>
							<INPUT type="button"  class="bottone-azione" value='Importa da formato M-Appalti' title='Importa da formato M-Appalti' onclick="javascript:importDitte();">
						</c:if>
					</c:if>
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
</table>

<gene:javaScript>
//Inizializzazioni per la libreria common-gare.js
setPaginaAttivaWizard("${paginaAttivaWizard}");
setFaseGara("${faseGara }");
setIsProceduraTelematica("${isProceduraTelematica }");
setBloccoAggiudicazione("${bloccoAggiudicazione }");
setContextPath("${pageContext.request.contextPath}");
setFaseCalcolata("${varTmp }");
setCodiceElenco("${codiceElenco }");
setIsGaraLottiConOffertaUnica("${isGaraLottiConOffertaUnica }");
setLottoDiGara("${lottoDiGara }");
setGaraLottoUnico(${garaLottoUnico});

var idconfi = '${idconfi}';

<c:if test='${updateLista eq 1}'>
		function conferma(){
	<c:choose>
		<c:when test='${paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step4Wizard}' >
			<c:if test="${tipoDoc eq 6 }">
				$('[id^="DOCUMGARA_ALLMAIL_"]').attr('disabled',false);
			</c:if>
			document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
			document.forms[0].encoding="multipart/form-data";
			document.forms[0].updateLista.value = "0";
			schedaConferma();
		</c:when>
		<c:otherwise>
		<c:if test='${paginaAttivaWizard >= step1Wizard and paginaAttivaWizard <= step5Wizard}'>
			// Riabilitazione prima del salvataggio delle combobox disabilitate all'utente
			for(var i=1; i <= ${currentRow}+1; i++){
				document.getElementById("DITG_INVGAR_" + i).disabled = false;
				document.getElementById("DITG_NPROTG_" + i).disabled = false;
				document.getElementById("DITG_DINVIG_" + i).disabled = false;
				if(document.getElementById("DITG_INVOFF_" + i)!=null)
					document.getElementById("DITG_INVOFF_" + i).disabled = false;
				document.getElementById("DITG_NPROFF_" + i).disabled = false;
				if(document.getElementById("DITG_DATOFF_" + i)!=null)
					document.getElementById("DITG_DATOFF_" + i).disabled = false;
				if(document.getElementById("DITG_ORAOFF_" + i)!=null)
					document.getElementById("DITG_ORAOFF_" + i).disabled = false;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + i).disabled = false;				
			}
		</c:if>
			listaConferma();
		</c:otherwise>
	</c:choose>
		}

	<c:if test='${paginaAttivaWizard ne step6Wizard and paginaAttivaWizard ne step4Wizard}' >
	
	var globalNumeroRiga = null;
	
		function inizializzaLista(){
			var numeroDitte = ${currentRow}+1;
			
			for(var t=1; t <= numeroDitte; t++){
			<c:if test='${paginaAttivaWizard ne step1Wizard}' >
				if(getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "98" || getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "99"){ 
					document.getElementById("V_DITGAMMIS_AMMGAR_" + t).disabled = true;
				}
			</c:if>
			<c:if test='${paginaAttivaWizard ne step2Wizard}' >
				if(getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "98" || getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "99"){ 
					document.getElementById("V_DITGAMMIS_AMMGAR_" + t).disabled = true;
				}
			</c:if>
			<c:if test='${paginaAttivaWizard eq step3Wizard}' >
				if(getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "98") 
					document.getElementById("DITG_INVGAR_" + t).disabled = true;
				if(getValue("DITG_INVGAR_" + t) == "2"){
					document.getElementById("DITG_NPROTG_" + t).disabled = true;
					document.getElementById("DITG_DINVIG_" + t).disabled = true;
				}
				<c:if test="${sortinv eq '1'}">
					var acquisizione = getValue("DITG_ACQUISIZIONE_" + t);
					if(acquisizione != 8)
						document.getElementById("DITG_INVGAR_" + t).disabled = true;
				</c:if>
			</c:if>
			<c:if test='${paginaAttivaWizard eq step5Wizard}' >
				var isProceduraTelematica= "${ isProceduraTelematica}";
				if(isProceduraTelematica == "true"){
					if(document.getElementById("DITG_INVOFF_" + t)!=null){
						document.getElementById("DITG_INVOFF_" + t).disabled = true;
					}
				}
				if(getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "98") {
					if(document.getElementById("DITG_INVOFF_" + t)!=null){
						document.getElementById("DITG_INVOFF_" + t).disabled = true;
					}
				}
				if(getValue("DITG_INVOFF_" + t) == "2"){
					document.getElementById("DITG_NPROFF_" + t).disabled = true;
					/*
					if(document.getElementById("DITG_DATOFF_" + t)!=null){
						document.getElementById("DITG_DATOFF_" + t).disabled = true;
					}
					if(document.getElementById("DITG_ORAOFF_" + t)!=null){
						document.getElementById("DITG_ORAOFF_" + t).disabled = true;
					}
					*/
					document.getElementById("V_DITGAMMIS_AMMGAR_" + t).disabled = true; //document.getElementById("DITG_AMMGAR_" + t).disabled = true;
				}
				if(getValue("DITG_INVOFF_" + t) == "2" || isProceduraTelematica == "true"){
					if(document.getElementById("DITG_DATOFF_" + t)!=null){
						document.getElementById("DITG_DATOFF_" + t).disabled = true;
					}
					if(document.getElementById("DITG_ORAOFF_" + t)!=null){
						document.getElementById("DITG_ORAOFF_" + t).disabled = true;
					}
				}
			</c:if>
			<c:if test='${paginaAttivaWizard ne step5Wizard and not isProceduraAggiudicazioneAperta}' >
					showObj("rowDITG_INVOFF", false);
			</c:if>
			}
		}
	
		// Funzione per associare le funzioni JS da eseguire al momento di modifica
		// di un campo. Questo e' necessario perchè la lista e' stata progetta per
		// non essere mai modificabile e quindi il tag gene:campoLista non associa
		// mai una funzione JS all'evento onchange.
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
	<c:choose>
		<c:when test='${paginaAttivaWizard eq step1Wizard}' >
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara; //document.getElementById("DITG_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
			if(document.getElementById("DITG_DRICIND_" + i)!=null)
				document.getElementById("DITG_DRICIND_" + i).onchange = checkDataRicezioneDomanda;
			if(document.getElementById("DITG_ORADOM_" + i)!=null)
				document.getElementById("DITG_ORADOM_" + i).onchange = checkOraRicezioneDomanda;
		</c:when>
		<c:when test='${paginaAttivaWizard eq step2Wizard}' >
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara; //document.getElementById("DITG_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
		</c:when>
		<c:when test='${paginaAttivaWizard eq step3Wizard}' >
			document.getElementById("DITG_INVGAR_" + i).onchange = aggiornaPerCambioDittaInvitata;
		</c:when>
		<c:when test='${paginaAttivaWizard eq step5Wizard}' >
			if(document.getElementById("DITG_INVOFF_" + i)!=null)
				document.getElementById("DITG_INVOFF_" + i).onchange = aggiornaPerCambioInvioOfferta;
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara; //document.getElementById("DITG_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
			if(document.getElementById("DITG_DATOFF_" + i)!=null)
				document.getElementById("DITG_DATOFF_" + i).onchange = checkDataRicezioneOfferta;
			if(document.getElementById("DITG_ORAOFF_" + i)!=null)
				document.getElementById("DITG_ORAOFF_" + i).onchange = checkOraRicezioneOfferta;
		</c:when>
	</c:choose>
			}
		}

		// Funzioni JS richiamate subito dopo la creazione della pagina per l'inizializzazione della stessa pagina
		associaFunzioniEventoOnchange();
		inizializzaLista();
		document.getElementById("numeroDitte").value = ${currentRow}+1;

		function aggiornaPerCambioAmmessaGara(){
			
			
			//alert('start aggiornaPerCambioAmmessaGara');
			var objId = null;
			var numeroRiga = null;
			if(globalNumeroRiga == null){
				objId = this.id;
				numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			} else {
				objId = "V_DITGAMMIS_AMMGAR_" + globalNumeroRiga;
				numeroRiga = globalNumeroRiga;
			}
			
			<c:if test='${paginaAttivaWizard == step2Wizard}'>
				var rtofferta = document.getElementById("DITG_RTOFFERTA_" + numeroRiga).value;
				if(rtofferta!=null && rtofferta != ""){
					alert("La ditta risulta aver presentato offerta in raggruppamento temporaneo.\nNon è pertanto possibile modificare il dato.");
					this.value = getOriginalValue("V_DITGAMMIS_AMMGAR_"+ numeroRiga);
					globalNumeroRiga = null;
					return;	
				}
			</c:if>
			
			document.getElementById("V_DITGAMMIS_MOTIVESCL_" + numeroRiga).value = "";
			document.getElementById("V_DITGAMMIS_DETMOTESCL_" + numeroRiga).value = "";
			//alert("objId = '" + objId + "', numeroRiga = '" + numeroRiga + "', getValue(objId) = '" + getValue(objId) + "'");
			if(document.getElementById(objId).value != "2" && document.getElementById(objId).value != "6"){
//				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
		<c:choose>
			<c:when test='${paginaAttivaWizard < step5Wizard}'>
				if(document.getElementById("DITG_INVOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_INVOFF_" + numeroRiga).value = "";
				document.getElementById("DITG_NPROFF_" + numeroRiga).value = "";
				if(document.getElementById("DITG_DATOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_DATOFF_" + numeroRiga).value = "";
				if(document.getElementById("DITG_ORAOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_ORAOFF_" + numeroRiga).value = "";
				if(document.getElementById("DITG_PLIOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_PLIOFF_" + numeroRiga).value = "";
				if(document.getElementById("DITG_NOTPOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_NOTPOFF_" + numeroRiga).value = "";
				if(document.getElementById("DITG_DPROFF_" + numeroRiga)!=null)
					document.getElementById("DITG_DPROFF_" + numeroRiga).value = "";
			</c:when>
			<c:otherwise>
				// Non si aggiorna DITG.INVOFF se questa funzione JS viene richiamata dalla funzione aggiornaPerCambioInvioOfferta
				if(globalNumeroRiga == null){
					if(document.getElementById("DITG_INVOFF_" + numeroRiga)!=null)
						document.getElementById("DITG_INVOFF_" + numeroRiga).value = "1";
				}
			</c:otherwise>
		</c:choose>
			<c:if test='${paginaAttivaWizard < step3Wizard && sortinv ne "1"}'>
				document.getElementById("DITG_INVGAR_" + numeroRiga).value = "1";
			</c:if>
			} else {
				//alert("Set DITG.AMMGAR = ${paginaAttivaWizard}");
//				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "${fasGarPerEsclusioneDitta}");
				// Reset del flag che memorizza se l'impresa era stata sorteggiata per la verifica
				if(getValue("DITG_ESTIMP_" + numeroRiga) == "1")
					setValue("DITG_ESTIMP_" + numeroRiga, ""); // reset del flag che memorizza se l'impresa era stata sorteggiata per la verifica
	
		<c:choose>
			<c:when test='${paginaAttivaWizard < step5Wizard}'>
				if(document.getElementById("DITG_INVOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_INVOFF_" + numeroRiga).value = "";
				setValue("DITG_NPROFF_" + numeroRiga, "");
				setValue("DITG_DPROFF_" + numeroRiga, "");
				setValue("DITG_DATOFF_" + numeroRiga, "");
				setValue("DITG_ORAOFF_" + numeroRiga, "");
				setValue("DITG_MEZOFF_" + numeroRiga, "");
				setValue("DITG_PLIOFF_" + numeroRiga, "");
				setValue("DITG_NOTPOFF_" + numeroRiga, "");
			</c:when>
			<c:otherwise>
				if(globalNumeroRiga == null){
					if(document.getElementById("DITG_INVOFF_" + numeroRiga)!=null)
						document.getElementById("DITG_INVOFF_" + numeroRiga).value = "1";
				}
			</c:otherwise>
		</c:choose>
	
			<c:if test='${paginaAttivaWizard < step3Wizard}'>
				setValue("DITG_INVGAR_" + numeroRiga, "");
				setValue("DITG_NPROTG_" + numeroRiga, "");
				setValue("DITG_DINVIG_" + numeroRiga, "");
			</c:if>
					

			<c:if test='${! garaLottoUnico and garaLottiOmogenea}' >
				if(confirm("Confermi l'esclusione della ditta anche dagli altri lotti non ancora esaminati della gara?"))
					setValue("ESCLUDI_DITTA_ALTRI_LOTTI_" + numeroRiga, 1);
				else
					setValue("ESCLUDI_DITTA_ALTRI_LOTTI_" + numeroRiga, 2);
			</c:if>

			
			}
			
			if( (this.value !="") && (getValue("DITG_PARTGAR_" + numeroRiga) == null || getValue("DITG_PARTGAR_" + numeroRiga) == ""))
				setValue("DITG_PARTGAR_" + numeroRiga, "1");
			// Reset della variabile globale globalNumeroRiga
			globalNumeroRiga = null;
		}

	</c:if>
	<c:if test='${paginaAttivaWizard eq step6Wizard}' >
		document.getElementById("CHIUSURA_FASE_OFFERTE").remove(0);
	</c:if>
	
	<c:if test='${paginaAttivaWizard eq step1Wizard}' >
		function checkDataRicezioneDomanda(){
			if(activeForm.onChange(this)){
			<c:if test='${not empty dataTermineRichiestaPartecipazione}' >
				var data = this.value;
				if(data!=null && data != ""){
					var objId = this.id;
					var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
					var ora= getValue("DITG_ORADOM_" + numeroRiga, "");
					checkDatiRichiestaOfferta(data, ora,"${dataTermineRichiestaPartecipazione}","${oraTermineRichiestaPartecipazione }","La data inserita e' successiva alla data termine richiesta partecipazione");
				}
					
			</c:if>
			}
		}
		
		function checkOraRicezioneDomanda(){
			if(activeForm.onChange(this)){
			<c:if test='${not empty dataTermineRichiestaPartecipazione}' >
				var ora = this.value;
				if(ora!=null && ora != ""){
					var objId = this.id;
					var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
					var data= getValue("DITG_DRICIND_" + numeroRiga, "");
					checkDatiRichiestaOfferta(data, ora,"${dataTermineRichiestaPartecipazione}","${oraTermineRichiestaPartecipazione }","La data inserita e' successiva alla data termine richiesta partecipazione");
				}
					
			</c:if>
			}
		}
		
		
		
	</c:if>
	
	<c:if test='${paginaAttivaWizard eq step3Wizard}' >
		function aggiornaPerCambioDittaInvitata(){
			//alert('start aggiornaPerCambioDittaInvitata');
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			
			var rtofferta = document.getElementById("DITG_RTOFFERTA_" + numeroRiga).value;
			if(rtofferta!=null && rtofferta != ""){
				alert("La ditta risulta aver presentato offerta in raggruppamento temporaneo.\nNon è pertanto possibile modificare il dato.");
				this.value = getOriginalValue("DITG_INVGAR_"+ numeroRiga);
				globalNumeroRiga = null;
				return;	
			}
			
			// Set della variabile globale globalNumeroRiga necessario per
			// l'aggiornamento dei campi che dipendono dal valore di AMMGAR
			globalNumeroRiga = numeroRiga;
			
			if(this.value != "2"){
				//if(getValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga) != "99"){
				//	setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "1");
					// Devono scattare gli aggiornamenti conseguenti al cambio di V_DITGAMMIS.AMMGAR
				//	aggiornaPerCambioAmmessaGara();
				//} else {
				//	setValue("DITG_INVOFF_" + numeroRiga, "");
				//}
				if(getValue("V_DITGAMMIS_AMMGAR_" + numeroRiga) == "2"){
					if(getOriginalValue("V_DITGAMMIS_AMMGAR_" + numeroRiga) != "2"){
						setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, getOriginalValue("V_DITGAMMIS_AMMGAR_" + numeroRiga));
						setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, getOriginalValue("V_DITGAMMIS_FASGAR_" + numeroRiga));
						setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, getOriginalValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga));
						setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, getOriginalValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga));
					} else {
						setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
						setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
						setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, "");
						setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, "");
					}
					//aggiornaPerCambioAmmessaGara();
				}
				document.getElementById("DITG_NPROTG_" + numeroRiga).disabled = false;
				document.getElementById("DITG_DINVIG_" + numeroRiga).disabled = false;
			} else {
				//if(getValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga) != "99"){
					setValue("DITG_NPROTG_" + numeroRiga, "");
					setValue("DITG_DINVIG_" + numeroRiga, "");
					setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "2");
					// Devono scattare gli aggiornamenti conseguenti al cambio di V_DITGAMMIS.AMMGAR
					aggiornaPerCambioAmmessaGara();
				//} else {
				//	setValue("DITG_NPROTG_" + numeroRiga, "");
				//	setValue("DITG_DINVIG_" + numeroRiga, "");	
				//}
				document.getElementById("DITG_NPROTG_" + numeroRiga).disabled = true;
				document.getElementById("DITG_DINVIG_" + numeroRiga).disabled = true;
			}
		}
	</c:if>

	<c:if test='${paginaAttivaWizard eq step5Wizard}' >
		function aggiornaPerCambioInvioOfferta(){
			//alert('start aggiornaPerCambioInvioOfferta');
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);

			// Set della variabile globale globalNumeroRiga necessario per
			// l'aggiornamento dei campi che dipendono dal valore di AMMGAR
			globalNumeroRiga = numeroRiga;

			if(this.value != "2"){
				
				if(getValue("V_DITGAMMIS_AMMGAR_" + numeroRiga) == "2"){
					if(getOriginalValue("V_DITGAMMIS_AMMGAR_" + numeroRiga) != "2"){
						setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
						setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
						//setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, getOriginalValue("V_DITGAMMIS_FASGAR_" + numeroRiga));
						//setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, getOriginalValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga));
					} else {
						setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
						setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
						setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga, "");
						setValue("V_DITGAMMIS_DETMOTESCL_" + numeroRiga, "");
					}
					aggiornaPerCambioAmmessaGara();
					document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = false;
				}
				document.getElementById("DITG_NPROFF_" + numeroRiga).disabled = false;
				if(document.getElementById("DITG_DATOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_DATOFF_" + numeroRiga).disabled = false;
				
				if(document.getElementById("DITG_ORAOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_ORAOFF_" + numeroRiga).disabled = false;
				
				if(getValue("DITG_NUM_ORDPL_" + numeroRiga) != getOriginalValue("DITG_NUM_ORDPL_" + numeroRiga)){
					setValue("DITG_NUM_ORDPL_" + numeroRiga, getOriginalValue("DITG_NUM_ORDPL_" + numeroRiga));
					document.getElementById("NUMORDPL_VALUE_" + numeroRiga).innerHTML = getOriginalValue("DITG_NUM_ORDPL_" + numeroRiga);
				}
				setValue("DITG_TIPRIN_" + numeroRiga, "");
			} else {
				setValue("DITG_NPROFF_" + numeroRiga, "");
				if(document.getElementById("colNPROFF_FIT_" + numeroRiga)!=null)
					document.getElementById("colNPROFF_FIT_" + numeroRiga).innerHTML="";
				setValue("DITG_DPROFF_" + numeroRiga, "");
				setValue("DPROFF_FIT_" + numeroRiga, "");
				setValue("DPROFF_FIT_NASCOSTO" + numeroRiga, "");
				if(document.getElementById("colDPROFF_FIT_" + numeroRiga)!=null)
					document.getElementById("colDPROFF_FIT_" + numeroRiga).innerHTML="";
				setValue("DITG_DATOFF_" + numeroRiga, "");
				setValue("DITG_ORAOFF_" + numeroRiga, "");
				setValue("DITG_MEZOFF_" + numeroRiga, "");

				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = true;
				document.getElementById("DITG_NPROFF_" + numeroRiga).disabled = true;
				if(document.getElementById("DITG_DATOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_DATOFF_" + numeroRiga).disabled = true;
				if(document.getElementById("DITG_ORAOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_ORAOFF_" + numeroRiga).disabled = true;

				//if(getValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga) != "99"){
				if(getValue("V_DITGAMMIS_AMMGAR_" + numeroRiga) != "2") {
					setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "2");
					// Devono scattare gli aggiornamenti conseguenti al cambio di V_DITGAMMIS_AMMGAR
					aggiornaPerCambioAmmessaGara();
				}
				//}
				setValue("DITG_NUM_ORDPL_" + numeroRiga, "");
				document.getElementById("NUMORDPL_VALUE_" + numeroRiga).innerHTML = "";
			}
		}

		function checkDataRicezioneOfferta(){
			if(activeForm.onChange(this)){
			<c:if test='${not empty dataTerminePresentazioneOfferta}' >
				var data =this.value;
				if(data!=null && data != ""){
					var objId = this.id;
					var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
					var ora= getValue("DITG_ORAOFF_" + numeroRiga, "");
					checkDatiRichiestaOfferta(data, ora, "${dataTerminePresentazioneOfferta}", "${oraTerminePresentazioneOfferta }", "La data inserita e' successiva alla data di termine ricezione offerte")	
				}
				
			</c:if>
			}
		}
		
		function checkOraRicezioneOfferta(){
					
			if(activeForm.onChange(this)){
			<c:if test='${not empty dataTerminePresentazioneOfferta}' >
				var ora = this.value;
				if(ora!=null && ora != ""){
					var objId = this.id;
					var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
					var data= getValue("DITG_DATOFF_" + numeroRiga, "");
					checkDatiRichiestaOfferta(data, ora,"${dataTerminePresentazioneOfferta}","${oraTerminePresentazioneOfferta }","La data inserita e' successiva alla data di termine ricezione offerte");
				}
					
			</c:if>
			}
			
		}
		
	</c:if>
	
</c:if>

<c:choose>
	<c:when test='${not empty paginaAttivaWizard and paginaAttivaWizard >= step1Wizard and paginaAttivaWizard < step6Wizard and paginaAttivaWizard ne step4Wizard}' >
		function annulla(){
			document.forms[0].updateLista.value = "0";
			listaAnnullaModifica();
		}

		function modificaLista(){
			document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 2;
		<c:if test='${not empty confermaDitteVincitrici_O_EscluseDaAltriLotti}' >
			if(confirm("Alcune ditte partecipanti al lotto di gara corrente e non ancora esaminate risultano escluse da altri lotti della stessa gara .\nNe confermi l'esclusione anche dal lotto corrente?")){
				document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 1;
			} else {
				document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 2;
			}
		</c:if>
			listaApriInModifica();
		}
	
	</c:when>
	<c:when test='${not empty paginaAttivaWizard and (paginaAttivaWizard >= step6Wizard or paginaAttivaWizard eq step4Wizard)}' >
		function annulla(){
			document.forms[0].updateLista.value = "0";
			schedaAnnulla();
		}
		
		function modificaLista(){
			document.forms[0].updateLista.value = "1";
			schedaModifica();
		}
		
		function confermaChiusuraRicezione(operazione){
			var href = "href=gare/commons/popupChiusuraFasiRicezione.jsp&operazione="+operazione+"&numeroPopUp=1";
			href += "&garaTelematica=${isProceduraTelematica }"
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			href += "&ngara=" + chiave;
			var faseGara = "${faseGara }";
			href += "&faseGara=" + faseGara;
			href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica}";
			href += "&codiceGara=${codiceGara}";
			openPopUpCustom(href, "conferma_chiusura_fasi_ricezione", "550", "350", "no", "no");
		}
		
		function attivaFasiGara(){
			setValue("CHIUSURA_FASI_RICEZIONE", "ATTIVA");
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
			document.forms[0].entita.value="TORN";
			var tmpKey = document.forms[0].key.value;
			document.forms[0].key.value= "TORN.CODGAR=T:" + tmpKey.substr(tmpKey.indexOf(":")+1);
		</c:if>
			document.forms[0].modo.value = "MODIFICA";
			schedaConferma();
		}
		
		function disattivaFasiGara(){
			setValue("CHIUSURA_FASI_RICEZIONE", "DISATTIVA");
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
			document.forms[0].entita.value="TORN";
			var tmpKey = document.forms[0].key.value;
			document.forms[0].key.value= "TORN.CODGAR=T:" + tmpKey.substr(tmpKey.indexOf(":")+1);
		</c:if>
			document.forms[0].modo.value = "MODIFICA";
			schedaConferma();
		}
	</c:when>
</c:choose>

	<c:if test='${paginaAttivaWizard < step6Wizard}'>
		function avanti(){
			<c:if test='${isProceduraTelematica eq "true" && paginaAttivaWizard eq step3Wizard }'>
				var invgarTuttiValorizzati=true;
				for(var i=0; i < ${currentRow}+1; i++){
					var invgar = getValue("DITG_INVGAR_FIT_" + (i+1));
					if(invgar==null || invgar==""){
						invgarTuttiValorizzati = false;
						break;
					}
				}
				if(!invgarTuttiValorizzati){
					var msg= "Per procedere alla fase seguente deve essere specificato lo stato di invito per ogni ditta nella lista.";
					var sortinv="${sortinv }";
					if(sortinv == 1)
						msg+="\nAttivare la funzione 'Sorteggio inviti' per selezionare le ditte da invitare.";
					alert(msg);
					return;
				}
			</c:if>
						
			
			
			if(document.forms[0].action.indexOf("Scheda.do") >= 0){
				document.forms[0].action = "${pageContext.request.contextPath}/Lista.do?"+csrfToken;
				document.forms[0].keyParent.value = document.forms[0].key.value;
			} else {
				document.forms[0].pgSort.value = "";
				document.forms[0].pgLastSort.value = "";
				document.forms[0].pgLastValori.value = "";
			}
			setValue("DIREZIONE_WIZARD", "AVANTI");
			listaVaiAPagina(0);
		}
		
		<c:if test='${isProceduraTelematica eq "true" && sortinv eq 1 && paginaAttivaWizard eq step2Wizard}'>
		function SorteggioOrdineInvito(){
			var chiave="${key}";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			href = "href=gare/gare/gare-popup-SorteggioOrdineInvito.jsp";
			href += "&ngara=" + chiave;
			href += "&codiceGara=${codiceGara}";
			href += "&sortinv=${sortinv}";
			openPopUpCustom(href, "sorteggioOrdineInvito", 450, 350, "yes", "yes");
		}
		</c:if>
		
		
		<c:if test='${isProceduraTelematica eq "true" && paginaAttivaWizard eq step2Wizard }'>
		function AttivaElencoDitteInvitare(){
			var chiave="${key}";
			var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
			var codiceGara = "${codiceGara}";
			var bustalotti =  "${bustalotti}";
			var sortinv =  "${sortinv}";
			href = "href=gare/gare/gare-popup-attivaElencoDitteDaInvitare.jsp&ngara=" + ngara + "&codgar=" + codiceGara + "&bustalotti=" + bustalotti + "&sortinv=" + sortinv;
			openPopUpCustom(href, "attivaelencoditteinvitare", 450, 350, "yes", "yes");
		}
		</c:if>

		function ulterioriCampi(indiceRiga, chiaveRiga){
			var href = null;
			href = "href=gare/ditg/ditg-schedaPopup-fasiRicezione.jsp";
			<c:if test='${updateLista eq "1"}' >
			href += "&modo=MODIFICA";
			</c:if>
			href += "&indiceRiga=" + indiceRiga;
			href += "&key=" + chiaveRiga;
			href += "&stepWizard=${varTmp}";
			href += "&paginaAttivaWizard=${paginaAttivaWizard}";
			href += "&moties=" + getValue('V_DITGAMMIS_MOTIVESCL_'+indiceRiga); 
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			href += "&isGaraLottiConOffertaUnica=true";
		</c:if>
			href += "&garaTelematica=${isProceduraTelematica }"; 
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
	</c:if>

	<c:if test='${paginaAttivaWizard > step1Wizard}'>
		function indietro(){
			if(document.forms[0].action.indexOf("Scheda.do") >= 0){
				document.forms[0].action = "${pageContext.request.contextPath}/Lista.do?"+csrfToken;
				document.forms[0].keyParent.value = document.forms[0].key.value;
			} else {
				document.forms[0].pgSort.value = "";
				document.forms[0].pgLastSort.value = "";
				document.forms[0].pgLastValori.value = "";
			}
			setValue("DIREZIONE_WIZARD", "INDIETRO");
			listaVaiAPagina(0);
		}
	</c:if>

	<c:if test='${updateLista ne "1" and ((paginaAttivaWizard eq step1Wizard and not isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step5Wizard and isProceduraAggiudicazioneAperta) or (paginaAttivaWizard eq step3Wizard and isProceduraNegoziata)
				or aggiungiDittaStep3 eq "true")}' >
		function aggiungiDitta(){
			var href = "href=gare/ditg/ditg-schedaPopup-insert.jsp";
			href += "&modo=NUOVO&faseRicezione=${paginaAttivaWizard}";
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			href += "&isGaraLottiConOffertaUnica=true";
		</c:if>
		<c:if test="${aggiungiDittaStep3 eq 'true' }" >
			href += "&initAcquisizione=8";
		</c:if>
			openPopUpCustom(href, "aggiungiDitta", 800, 600, "yes", "yes");
		}
		
		<c:if test="${aggiungiDittaStep3 ne 'true' }" >
		function apriPopupCopiaDitte() {
			//var href = "href=gare/gare/gare-popup-copia-ditte.jsp?codgar="+getValue("GARE_CODGAR1")+"&lottoSorgente="+getValue("GARE_NGARA");
			var chiave="${key }";
			var lottoSorgente= chiave.substr(chiave.lastIndexOf(":") + 1);
			var numerorighe=${datiRiga.rowCount};
			var codgar="${codgar1}";
						
			var href = "href=gare/gare/gare-popup-copia-ditte.jsp?codgar="+codgar+"&lottoSorgente="+lottoSorgente;
			openPopUpCustom(href, "copiaDitte", 600, 450, "yes", "yes");
		}
		
		function copiaDitta(chiave) {
			var href = "href=gare/gare/gare-popup-copia-ditta.jsp";
			var numeroFaseAttiva = ${paginaAttivaWizard};
			href+="?chiave=" + chiave;
			href+="&numeroFaseAttiva=" + numeroFaseAttiva;
			
			openPopUpCustom(href, "copiaDitta", 600, 450, "yes", "yes");
			
		}
		</c:if>
	</c:if>

	function archivioImpresa(codiceImpresa){
<c:choose>
	<c:when test='${updateLista eq 1}' >
		var href = ("href=gene/impr/impr-scheda-popup.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		openPopUp(href, "schedaImpresa");
	</c:when>
	<c:otherwise>
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
	</c:otherwise>
</c:choose>
	}

	

<c:if test='${not empty RISULTATO and RISULTATO eq "FASI_GARA_ATTIVATE"}'>
	bloccaRichiesteServer();
	document.pagineForm.action += "?"+csrfToken;
	selezionaPagina(eval(document.pagineForm.activePage.value)+1);
</c:if>

		

		function modelliPredispostiLocale(){
		/***********************************************************
			Copia della funzione modelliPredisposti dal file jsAzioniTags.jsp
			e addattamento al caso specifico
		 ***********************************************************/
			var entita,valori;

			try {
		<c:choose>
			<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
				entita = "TORN";
			</c:when>
			<c:otherwise>
				entita = "GARE";
			</c:otherwise>
		</c:choose>
				if(document.forms[0].key.value!=''){
					valori=document.forms[0].name+".key";
				} else if(document.forms[0].keyParent.value!=''){
					valori=document.forms[0].name+".keyParent";
				} else if(document.forms[0].keys.value!=''){
					valori=document.forms[0].name+".keys";
				}
				compositoreModelli('${pageContext.request.contextPath}',entita,'',valori);
			}catch(e){
			}
		}
		
		function listaDitteConsorziate(chiaveRiga){
			var bloccoPagina=false;	
			var bloccoAggiudicazione = "${bloccoAggiudicazione }";		
			var isProceduraTelematica = "${isProceduraTelematica }";
			var faseGara = "${faseGara }";
			if(faseGara!=null || faseGara!="")
				faseGara = parseInt(faseGara);
			else
				faseGara = 0;
			if(bloccoAggiudicazione=="1" || (isProceduraTelematica=="true" && faseGara>=5))
				bloccoPagina=true;
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gene/ragdet/ragdet-lista.jsp";
			href += "&key=" + chiaveRiga + "&bloccoPagina=" + bloccoPagina;
			document.location.href = href;
		}
			
		function verificaDocRichiesti(chiaveRiga,tipo){
			/*
			var chiave = chiaveRiga;
			href = "href=gare/imprdocg/imprdocg-listaPopup.jsp";
			if(tipo=="CONSULTAZIONE"){
				href += "&tipo=CONSULTAZIONE";
				var vetTmp = chiaveRiga.split(";");
				var dittao= vetTmp[1].substring(vetTmp[0].indexOf(":"));
				var codiceGara= "$" + "${codiceElenco }";
				var ngara= "${codiceElenco}";
				chiave = "DITG.CODGAR5=T:" + codiceGara + ";DITG.DITTAO=T:" + dittao + ";DITG.NGARA5=T:" + ngara;
			}
			href += "&key="+chiave;
			href += "&stepWizard=${varTmp}";
			href += "&comunicazioniVis=1";
			//document.location.href = href;
			openPopUpCustom(href, "verificaDocumentiRichiesti", 1100, 550, "yes", "yes");
			*/
			var documentiElenco;
			if(tipo=="CONSULTAZIONE")
				documentiElenco = "true";
			verificaDocumentiRichiesti(chiaveRiga,tipo,1,documentiElenco,"${autorizzatoModifiche }");
		}
		
		function SelEleOpEco(modalita){
			var selezioneAutomaticaDitte = false;
			if(modalita == 1)
				selezioneAutomaticaDitte = true;
			<c:choose>
				<c:when test="${ modalitaSelezioneDitteElenco eq 'MISTA'}">
					var modalitaSelezioneMista = true;
				</c:when>
				<c:otherwise>
					var modalitaSelezioneMista = false;
				</c:otherwise>
			</c:choose>
							
			var bloccaSelezioneDaElenco = "${bloccaSelezioneDaElenco }";
			var numeroMinimoOperatori = "${numeroMinimoOperatori }";
			
			if(selezioneAutomaticaDitte && bloccaSelezioneDaElenco == "true" ){
				alert("Non è possibile procedere con la selezione automatica delle ditte da elenco perché ci sono già delle ditte inserite in gara");
				return;
			}
			if(selezioneAutomaticaDitte &&  numeroMinimoOperatori == "0"){
				alert("Non è possibile procedere con la selezione automatica delle ditte da elenco perchè non è stato configurato il parametro \"Numero minimo ditte da selezionare\"");
				return;				
			}
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			categoriaPrev ="${codiceCategoriaPrev }";
			var tipoalgo ="${tipoalgo }";
			var isFoglia = "${isFoglia }";
			var stazioneAppaltante = "${stazioneAppaltante }";
			if((tipoalgo == "1" || tipoalgo=="3" || tipoalgo == "4" || tipoalgo == "5" || tipoalgo == "11" || tipoalgo == "12" || tipoalgo == "14" || tipoalgo == "15") && (categoriaPrev == "" || isFoglia == "2")){
				var msg="Non è possibile procedere con la selezione da elenco perchè ";
				if(categoriaPrev == "")
					msg+="non è stata specificata la categoria o prestazione prevalente della gara";
				else
					msg+="la categoria o prestazione prevalente della gara non è di massimo dettaglio";
					
				alert(msg);
				return;
			}else if((tipoalgo == "8" || tipoalgo=="9") && (stazioneAppaltante == "" || stazioneAppaltante == null)){
				var msg="Non è possibile procedere con la selezione da elenco perchè non è stata specificata la stazione appaltante della gara";
				alert(msg);
				return;
			}
			
				
			href = "href=gare/gare/gare-popup-selOpEconomici.jsp";
			href += "&" + csrfToken;
			href += "&categoriaPrev=" + categoriaPrev;
			href += "&classifica=${classifica}";
			href += "&garaElenco=${codiceElenco}";
			href += "&WIZARD_PAGINA_ATTIVA=${paginaAttivaWizard}";
			href += "&ngara=" + chiave;
			href += "&tipoGara=${tipoGara }";
			href += "&minOp=${numeroMinimoOperatori}";
			href += "&limSupDitteSel=${numSupDitteSel}";
			href += "&aggnumord=${aggnumord}"
			href += "&tipoCategoria=${tipoCategoria }";	
			href += "&tipoalgo=" + tipoalgo;
			href += "&stazioneAppaltante=" + stazioneAppaltante;
			<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
				href += "&isGaraLottiConOffertaUnica=true";
			</c:if>
			href += "&selezioneAutomaticaDitte=" + selezioneAutomaticaDitte;
			href += "&modalitaSelezioneMista=" + modalitaSelezioneMista;
			openPopUpCustom(href, "elencoOperatoriEconomici", 900, 600, "yes", "yes");
		
		}
		
		function SelUltimaAgg(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			
			categoriaPrev ="${codiceCategoriaPrev }";
			var tipoalgo ="${tipoalgo }";
			var isFoglia = "${isFoglia }";
			if((tipoalgo == "1" || tipoalgo=="3" || tipoalgo == "4" || tipoalgo == "5") && (categoriaPrev == "" || isFoglia == "2")){
				var msg="Non è possibile procedere con la selezione da elenco perchè ";
				if(categoriaPrev == "")
					msg+="non è stata specificata la categoria o prestazione prevalente della gara";
				else
					msg+="la categoria o prestazione prevalente della gara non è di massimo dettaglio";
					
				alert(msg);
				return;
			}
			
			href = "href=gare/gare/gare-popup-selOpUltimaAgg.jsp";
			href += "&categoriaPrev=" + categoriaPrev;
			href += "&classifica=${classifica}";
			href += "&garaElenco=${codiceElenco}";
			href += "&WIZARD_PAGINA_ATTIVA=${paginaAttivaWizard}";
			href += "&ngara=" + chiave;
			href += "&tipoGara=${tipoGara }";
								
			<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
				href += "&isGaraLottiConOffertaUnica=true";
			</c:if>
			openPopUpCustom(href, "elencoUltimaAggiudicataria", 900, 600, "yes", "yes");
		}
		
				
		function acquisisciDaAUR(){
			var ngara = document.forms[0].keyParent.value;
			var codStazioneAppaltante = "${codStazioneAppaltante }";
			var par = "ngara=" + ngara.substring(ngara.indexOf(":")+1);
			par += "&codStazioneAppaltante=" + codStazioneAppaltante;
			par+="&numeroFaseAttiva=${paginaAttivaWizard}";
			par+="&metodo=initTrovaDitta";
			<c:choose>
			  <c:when test='${tipologiaGara eq "3"}'>
			  	par += "&garaLottiConOffertaUnica=1";
			  </c:when>
			  <c:otherwise>
			  	par += "&garaLottiConOffertaUnica=2";
			  </c:otherwise>
			</c:choose>
			
			openPopUpActionCustom(contextPath + "/pg/ElencoFornitori.do", par, "listaElencoFornitori", 900, 750, 1, 1);
		}
		
		function RiassegnaNumOrd(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			var isProceduraNegoziata = "${isProceduraNegoziata}";
			var procNegaziata="false";
			if(isProceduraNegoziata=="true")
				procNegaziata="true";
			href = "href=gare/gare/gare-popup-RiassegnaNumeroOrdine.jsp";
			href += "&WIZARD_PAGINA_ATTIVA=${paginaAttivaWizard}";
			href += "&ngara=" + chiave;
			href += "&procNegaziata=" + procNegaziata;
			<c:if test='${isProceduraAggiudicazioneAperta eq "true"}'>
				href += "&isProceduraAggiudicazioneAperta=true";
			</c:if>
			<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
				href += "&isGaraLottiConOffertaUnica=true";
			</c:if>
			openPopUpCustom(href, "riassegnaNumOrdine", 600, 350, "no", "no");
		}
      
      function AcquisisciOfferteDaPortale() {
			var chiave="${key}";
			var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
			<c:choose>
				<c:when test='${isGaraLottiConOffertaUnica eq "false"}'>
					var codiceGara = "${codgar1 }";
				</c:when>
				<c:otherwise>
					var codiceGara = "${codiceGara }";		
				</c:otherwise>
			</c:choose>
			href = "href=gare/gare/gare-popup-acquisiscioffertedaportale.jsp&ngara=" + ngara + "&codiceGara=" + codiceGara;
			openPopUpCustom(href, "acquisisciOfferteDaPortale", 450, 350, "yes", "yes");
		}
		
	function AcquisisciDomandeDaPortale(tipo) {
			var chiave="${key}";
			var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
			<c:choose>
				<c:when test='${isGaraLottiConOffertaUnica eq "false"}'>
					var codiceGara = "${codgar1 }";
				</c:when>
				<c:otherwise>
					var codiceGara = "${codiceGara }";		
				</c:otherwise>
			</c:choose>
			href = "href=gare/gare/gare-popup-acquisiscidomandedaportale.jsp&ngara=" + ngara + "&codiceGara=" + codiceGara;
			var idconfi = "${idconfi}";
			if(idconfi){
				href += "&idconfi=" + idconfi
			}
			openPopUpCustom(href, "acquisisciDomandeDaPortale", 450, 350, "yes", "yes");
	}
			
      function impostaFiltro(){
			var comando = "href=gare/commons/popup-trova-filtroDitte.jsp";
			var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
			comando+="&dittePerPagina=" + risultatiPerPagina;
			<c:choose>
				<c:when test='${isGaraLottiConOffertaUnica eq "false"}'>
					var codiceGara = "${codgar1 }";
				</c:when>
				<c:otherwise>
					var codiceGara = "${codiceGara }";		
				</c:otherwise>
			</c:choose>
			comando+="&codiceGara=" + codiceGara;
			openPopUpCustom(comando, "impostaFiltro", 850, 550, "yes", "yes");
		}
		
	 function AnnullaFiltro(){
	 	var comando = "href=gare/commons/popup-filtro.jsp&annulla=2";
		openPopUpCustom(comando, "impostaFiltro", 10, 10, "no", "no");
	 }
	 
	 function inviaComunicazione(chiaveRiga,inviaComunicazioneAbilitato,idconfi){
			if(inviaComunicazioneAbilitato=="NoPec"){
				alert("Non è possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC specificato in anagrafica");
				return;
			}else if(inviaComunicazioneAbilitato=="NoPecRTI"){
				alert("Non è possibile procedere.\nLa mandataria del raggruppamento selezionato non ha un indirizzo PEC specificato in anagrafica");
				return;
			}else if(inviaComunicazioneAbilitato=="NoMail"){
				alert("Non è possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC o E-mail specificato in anagrafica");
				return;
			}else if(inviaComunicazioneAbilitato=="NoMailRTI"){
				alert("Non è possibile procedere.\nLa mandataria del raggruppamento selezionato non ha un indirizzo PEC o E-mail specificato in anagrafica");
				return;
			}else if(inviaComunicazioneAbilitato=="NoMailFax"){
				alert("Non è possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC o E-mail o un numero fax specificato in anagrafica");
				return;
			}else if(inviaComunicazioneAbilitato=="NoMailFaxRTI"){
				alert("Non è possibile procedere.\nLa mandataria del raggruppamento selezionato non ha un indirizzo PEC o E-mail o un numero fax specificato in anagrafica");
				return;
			}else if(inviaComunicazioneAbilitato=="NoMandatariaRTI"){
				alert("Non è possibile procedere.\nNell'anagrafica del raggruppamento selezionato non è specificata la mandataria");
				return;
			}else{
				var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
				var chiaveVet= chiaveRiga.split(";");
				var codiceDitta=chiaveVet[1].substring(chiaveVet[1].indexOf(":")+1);
				var numeroGara = chiaveVet[2].substring(chiaveVet[1].indexOf(":")+1);
				var codiceGara = chiaveVet[0].substring(chiaveVet[1].indexOf(":")+2);
				if(IsW_CONFCOMPopolata == "true"){
					href = contextPath + "/pg/InitNuovaComunicazione.do?genere=4&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara; 
					<c:choose>
						 <c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							href += "&keyParent=GARE.NGARA=T:" + numeroGara;
						 </c:when>
						 <c:otherwise>
							href += "&keyParent=TORN.CODGAR=T:" + numeroGara;
						 </c:otherwise>
					</c:choose>
					href += "&ditta=" + codiceDitta + "&stepWizard=${varTmp}" + "&whereBusteAttiveWizard=${whereBusteAttiveWizard}";
				}else{
					var href = contextPath + "/Lista.do?"+csrfToken+"&numModello=0&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara ;
					<c:choose>
						 <c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							href += "&keyParent=GARE.NGARA=T:" + numeroGara;
						 </c:when>
						 <c:otherwise>
							href += "&keyParent=TORN.CODGAR=T:" + numeroGara;
						 </c:otherwise>
					</c:choose>
					href += "&ditta=" + codiceDitta+ "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp";
				}
				<c:choose>
					<c:when test='${lottoDiGara && !garaLottoUnico}'>
						var entitaWSDM="TORN";
						var chiaveWSDM=codiceGara;
					</c:when>
					<c:otherwise>
						var entitaWSDM="GARE";
						var chiaveWSDM=numeroGara;
					</c:otherwise>
				</c:choose>
				href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
				if(idconfi){href+="&idconfi=" + idconfi;}
				
				document.location.href = href;
			}
			
			
		}	
		
		<c:if test='${isGaraLottiConOffertaUnica ne "true"}'>
			//Apertura popup per la pubblicazione su Area Riservata portale Appalti
			function pubblicaSuAreaRiservata() {
				var isProceduraTelematica = ${isProceduraTelematica };
				var integrazioneWSDM = "${integrazioneWSDM}";
				var href = "href=gare/commons/popupPubblicaSuPortale.jsp?codgar="+getValue("GARE_CODGAR1")+"&ngara="+getValue("GARE_NGARA")+"&bando=3&isProceduraTelematica=" + isProceduraTelematica + "&step=1";
				dim1 = 800;
				dim2 = 650;
				if(isProceduraTelematica){
					<c:choose>
						<c:when test="${cifraturaBuste eq '1'}">
							dim2=850;
						</c:when>
						<c:otherwise>
							dim2=650;
						</c:otherwise>
					</c:choose>
				}
				if(integrazioneWSDM =='1'){
					var entita="GARE";
					var codgar = getValue("GARE_CODGAR1");
					if(codgar.indexOf('$')!=0 )
						entita="TORN";
					href += "&entita=" + entita;
				}
				href +="&valtec="+getValue("GARE1_VALTEC");
				if(idconfi){
					href +="&idconfi="+idconfi;
				}
				openPopUpCustom(href, "pubblicaSuPortale", dim1, dim2, "no", "yes");
			}
		</c:if>

		function presentaOffertaRaggruppamentoTemporaneo(chiaveRiga,tipoImpresa){
			var href = "href=gare/ditg/ditg-schedaPopup-insert.jsp";
			href += "&modo=NUOVO&faseRicezione=${paginaAttivaWizard}";
			var chiaveVet= chiaveRiga.split(";");
			var codiceDitta=chiaveVet[1].substring(chiaveVet[1].indexOf(":")+1);
			var numeroGara = chiaveVet[2].substring(chiaveVet[2].indexOf(":")+1);
			var codiceGara = chiaveVet[0].substring(chiaveVet[0].indexOf(":")+1);
			href += "&codiceDitta=" + codiceDitta + "&numeroGara=" + numeroGara + "&tipoImpresa=" + tipoImpresa + "&offertaRT=1&codiceGara=" + codiceGara;
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			href += "&isGaraLottiConOffertaUnica=true";
		</c:if>
			openPopUpCustom(href, "presentaOfferta", 800, 600, "yes", "yes");
		}	
		
		function AttivaAperturaDomande(){
			var chiave="${key}";
			var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
			var codiceGara = "${codiceGara}";
			href = "href=gare/gare/gare-popup-attivaAperturaDomandePartecipazione.jsp&ngara=" + ngara + "&codgar=" + codiceGara;
								
			openPopUpCustom(href, "attivaaperturadomande", 450, 350, "yes", "yes");
		}
		
	function ricaricaPagina(){
		bloccaRichiesteServer();
		window.location=contextPath+'/History.do?'+csrfToken+'&metodo=reload&numeroPopUp='+getNumeroPopUp();
	}	
	
	function SelDitteRilancio(){
		var chiave="${key }";
		chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
		href = "href=gare/gare/gare-popup-selDitteRilancio.jsp";
		href += "&ngara=" + chiave;
		href += "&preced=${preced}";
		href += "&risultatiPerPagina=${requestScope.risultatiPerPagina}";
		href += "&WIZARD_PAGINA_ATTIVA=${paginaAttivaWizard}";
		openPopUpCustom(href, "elencoOperatoriEconomici", 900, 600, "yes", "yes");
		
		}
		
	function exportDitte() {
		var codgar="${codiceGara}";
		var chiave="${key}";
		var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
		href = "href=gare/gare/popup-export-ditte-gara.jsp&codgar=" + codgar + "&ngara="+ngara;
		openPopUpCustom(href, "importaditteingara", 700, 500, "yes", "yes");
	}
	
	function importDitte(){
		var codgar="${codiceGara}";
		var chiave="${key}";
		var numeroRighe = $("#counter").val();
		var dittePresenti = false;
		if(numeroRighe > 0){
			dittePresenti = true;
		}
		var plicoUnico="${isGaraLottiConOffertaUnica}";
		var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
		href = "href=gare/gare/popup-import-ditte-gara.jsp&codgar=" + codgar + "&ngara="+ngara+ "&dittePresenti="+dittePresenti;
		if(plicoUnico = "true"){
			href+="&isGaraLottiConOffertaUnica=true"; 
		}
		openPopUpCustom(href, "importaditteingara", 700, 500, "yes", "yes");
	}
	
	function apriSorteggioDitteInviti(ngara,codgar){
		var pagina="gare-popup-sorteggioDitteInviti.jsp";
		var href = "href=gare/gare/" + pagina;
		href += "&ngara=" + ngara;
		href += "&codgar=" + codgar;
		openPopUpCustom(href, "sorteggioDitteVerificaRequisiti", 700, 300, "no", "no");
	}	
	
	/*
	function richiestaSoccorsoIstruttorio(chiaveRiga,modelloSoccoroIstruttorioConfigurato,idconfi){
		var numeroModello;
		if(modelloSoccoroIstruttorioConfigurato != "true"){
			alert("Non è configurato il modello per la richiesta di soccorso istruttorio");
			return;
		}else{
			numeroModello = "${numeroModello }";
		}
			
		var chiaveVet= chiaveRiga.split(";");
		var codiceDitta=chiaveVet[1].substring(chiaveVet[1].indexOf(":")+1);
		var numeroGara = chiaveVet[2].substring(chiaveVet[1].indexOf(":")+1);
		var codiceGara = chiaveVet[0].substring(chiaveVet[1].indexOf(":")+2);
		
		var href = contextPath + "/Lista.do?"+csrfToken+"&numModello=" + numeroModello + "&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara ;
		<c:choose>
			 <c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
				href += "&keyParent=GARE.NGARA=T:" + numeroGara;
			 </c:when>
			 <c:otherwise>
				href += "&keyParent=TORN.CODGAR=T:" + numeroGara;
			 </c:otherwise>
		</c:choose>
		href += "&ditta=" + codiceDitta+ "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp";
		
		<c:choose>
			<c:when test='${lottoDiGara && !garaLottoUnico}'>
				var entitaWSDM="TORN";
				var chiaveWSDM=codiceGara;
			</c:when>
			<c:otherwise>
				var entitaWSDM="GARE";
				var chiaveWSDM=numeroGara;
			</c:otherwise>
		</c:choose>
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){href+="&idconfi=" + idconfi;}
		
		document.location.href = href;
			
			
			
		}
		*/	
</gene:javaScript>