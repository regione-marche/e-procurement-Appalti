<%
/*
 * Created on: 10-01-2013
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

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
	<style type="text/css">
	 TD.etichetta-atto {
		width: 120px;
		HEIGHT: 22px;
		PADDING-RIGHT: 10px;
		TEXT-ALIGN: right;
	}
	</style>
</gene:redefineInsert>

<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestionePagineAggProvvDefOffertaUnicaFunction",  pageContext, key,"AggDef")}'/>

<c:set var="codiceGara" value='${gene:getValCampo(key, "CODGAR")}'/>
<c:set var="visualizzaPopUp" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}'/>
<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImportExportZoo")}'>
	<c:set var="tipoFornitura" value="98" />
 </c:if>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:set var="modelliPredispostiAttivoIncondizionato" value="1" scope="request" />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

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
			<span class="avanzamento-paginevisitate"><c:out value="${fn:trim(strPagineVisitate)}" escapeXml="true" /></span><span class="avanzamento-paginedavisitare"><c:out value="${strPagineDaVisitare}" escapeXml="true" /></span>
		</td>
	</tr>

<c:choose>
	<c:when test='${paginaAttivaWizard == 1}'>
		
		<jsp:include page="fasiGara/defStepWizardFasiGara.jsp" />
		
		<c:set var="varTmp" value="${step7Wizard/10}" />
		<c:if test='${fn:endsWith(varTmp, ".0")}'>
			<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
		</c:if>
		<c:set var="fasgarStepgarOffEconomica" value='${varTmp }' />
		<c:set var="temp" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GestioneAggiudicazioneProvvisoriaFunction",  pageContext, codiceGara,codiceGara,"",fasgarStepgarOffEconomica)}'/>
		
		<c:set var="strProtModificaDatiGara" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.AGGIUDDEF.MOD-AGGPROVV" scope="request"/>
		<!-- Inizio Pagina scheda Chiusura agg.provvisoria -->
		<tr>
			<td >
				&nbsp;
			</td>
		</tr>
		
		<tr>
			<td>
				<c:set var="where" value='TORN.CODGAR=GARE.NGARA'/>
				<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAggProvvOffertaUnica">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<gene:redefineInsert name="schedaConferma">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:conferma();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
							</gene:redefineInsert>
							<gene:redefineInsert name="schedaAnnulla">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</gene:redefineInsert>
						</c:when>
						<c:otherwise>
							<gene:redefineInsert name="schedaNuovo" />
							<gene:redefineInsert name="schedaModifica" />
							<gene:redefineInsert name="modelliPredisposti">
								<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>
									<tr>
										<c:choose>
											<c:when test='${isNavigazioneDisabilitata ne "1"}'>
												<td class="vocemenulaterale">
													<a href="javascript:modelliPredispostiLocale();" title="Modelli predisposti" tabindex="1510">
														${gene:resource("label.tags.template.documenti.modelliPredisposti")}
													</a>
												</td>
											</c:when>
											<c:otherwise>
												<td>
													${gene:resource("label.tags.template.documenti.modelliPredisposti")}
													</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:if>
							</gene:redefineInsert>

							<gene:redefineInsert name="addToAzioni">
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaDatiGara)}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaScheda();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
												${gene:resource("label.tags.template.dettaglio.schedaModifica")}
											</a>
										</td>
									</tr>
								</c:if>
																
								
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:avanti();" title='Fase seguente' tabindex="1506">
											Fase seguente >
										</a>
									</td>
								</tr>
								

							</gene:redefineInsert>
						</c:otherwise>
					</c:choose>
					<gene:campoScheda campo="NGARA" entita="GARE" where = "${where}" visibile="false" />
					<gene:campoScheda campo="CODGAR1" entita="GARE" where = "${where}" visibile="false"  />
					<gene:campoScheda campo="CODGAR" visibile="false" />
					
					<gene:gruppoCampi idProtezioni="AGG">
						<gene:campoScheda>
							<td colspan="2"><b>Proposta di aggiudicazione lotti</b></td>
						</gene:campoScheda>
						<gene:campoScheda title="Numero lotti aggiudicati" campo="LOTTI_AGGIUDICATI" campoFittizio="true" definizione="N3" modificabile="false" value="${numLottiAggiudicati}"/>
						<gene:campoScheda title="Numero lotti non aggiudicati" campo="LOTTI_NO_AGGIUDICATI" campoFittizio="true" definizione="N3" modificabile="false" value="${numLottiNoAggiudicati}"/>
					</gene:gruppoCampi>
					
					<gene:gruppoCampi idProtezioni="VERB_AGG_PROV">
						<gene:campoScheda>
							<td colspan="2"><b>Verbale proposta di aggiudicazione</b></td>
						</gene:campoScheda>
						<gene:campoScheda entita="GARE" where = "${where}" campo="DVPROV" />
		 				<gene:campoScheda entita="GARE" where = "${where}" campo="NVPROV" />
						<gene:campoScheda entita="GARE" where = "${where}" campo="DLETTAGGPROV" />
						<gene:campoScheda campo="NPLETTAGGPROVV" entita="GARE1" where="GARE1.NGARA=TORN.CODGAR"/>
						<gene:campoScheda campo="NGARA" entita="GARE1" where="GARE1.NGARA=TORN.CODGAR" visibile="false"/>
						<gene:campoScheda entita="GARE" where = "${where}" campo="DSEDPUBEVA" />
						<gene:campoScheda entita="GARE" where = "${where}" campo="DAVVPRVREQ" />
					</gene:gruppoCampi>
					
					<gene:gruppoCampi idProtezioni="ESCLUSIONE">
						<gene:campoScheda >
							<td colspan="2"><b>Esclusione in fase di aggiudicazione</b></td>
						</gene:campoScheda>
						<gene:campoScheda entita="GARESTATI" campo="NPLETTCOMESCLOFEC"  title="N.prot. lett. comun. esclusioni apertura off.economica" campoFittizio="true" definizione="T20;;;;NPROTLCE" value="${nprotEc}" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
						<gene:campoScheda entita="GARESTATI" campo="DPLETTCOMESCLOFEC"  title="Data lett. comunicazione esclusioni apertura off.economica" campoFittizio="true" definizione="D;;;;DATLCE" value="${dprotEc}" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
					</gene:gruppoCampi>
					
					
					<gene:gruppoCampi idProtezioni="COMPROVAREQ" >
						<gene:campoScheda>
							<td colspan="2"><b>Comprova requisiti di ordine generale</b></td>
						</gene:campoScheda>
						<gene:campoScheda entita="GARE" where = "${where}" campo="DRICHDOCCR" />
						<gene:campoScheda entita="GARE" where = "${where}" campo="NPROREQ" />
						<gene:campoScheda entita="GARE" where = "${where}" campo="DTERMDOCCR" />
					</gene:gruppoCampi>
													
					<gene:gruppoCampi idProtezioni="CONTRATTPROC" >
						<gene:campoScheda>
							<td colspan="2"><b>Controllo sugli atti della procedura di affidamento (art.33 Dlgs.50/2016)</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="DATAPAGG" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" />
						<gene:campoScheda campo="NPRAPAGG" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" />
					</gene:gruppoCampi>

					<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N2" value="${paginaAttivaWizard}" />
					<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
					<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
					<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
				</gene:formScheda>
			</td>
		</tr>
		<!-- Fine Pagina scheda Chiusura agg.provvisoria -->

	</c:when>
	<c:when test='${empty paginaAttivaWizard or paginaAttivaWizard == 2}'>
		<!-- Inizio Pagina lista aggiudicazione definitiva lotti-->
		
		<jsp:include page="/WEB-INF/pages/gare/gare/lista-lotti-aggiudicazione.jsp" >
			<jsp:param name="tipoAggiudicazione" value="definitiva"/>
		</jsp:include>
	
	<!-- Fine Pagina lista aggiudicazione definitiva lotti -->
	</c:when>
	<c:otherwise>
		<c:set var="strProtModificaDatiGara" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.AGGIUDDEF.MOD-DATIGARA" scope="request"/>
		<!-- Inizio Pagina scheda dati di gara -->
		<tr>
			<td >
				&nbsp;
			</td>
		</tr>
		
		<tr>
			<td>
				<c:set var="where" value='TORN.CODGAR=GARE.NGARA'/>
				<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAggProvvOffertaUnica">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<gene:redefineInsert name="schedaConferma">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:conferma();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
							</gene:redefineInsert>
							<gene:redefineInsert name="schedaAnnulla">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</gene:redefineInsert>
						</c:when>
						<c:otherwise>
							<gene:redefineInsert name="schedaNuovo" />
							<gene:redefineInsert name="schedaModifica" />
							<gene:redefineInsert name="modelliPredisposti">
								<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>
									<tr>
										<c:choose>
											<c:when test='${isNavigazioneDisabilitata ne "1"}'>
												<td class="vocemenulaterale">
													<a href="javascript:modelliPredispostiLocale();" title="Modelli predisposti" tabindex="1510">
														${gene:resource("label.tags.template.documenti.modelliPredisposti")}
													</a>
												</td>
											</c:when>
											<c:otherwise>
												<td>
													${gene:resource("label.tags.template.documenti.modelliPredisposti")}
													</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:if>
							</gene:redefineInsert>

							<gene:redefineInsert name="addToAzioni">
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaDatiGara)}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaScheda();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
												${gene:resource("label.tags.template.dettaglio.schedaModifica")}
											</a>
										</td>
									</tr>
								</c:if>
									
								<c:if test='${paginaAttivaWizard > 1}'>	
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:indietro();" title='Fase precedente' tabindex="1505">
												< Fase precedente
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${paginaAttivaWizard < 2}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1506">
												Fase seguente >
											</a>
										</td>
									</tr>
								</c:if>

							</gene:redefineInsert>
						</c:otherwise>
					</c:choose>
					<gene:campoScheda campo="NGARA" entita="GARE" where = "${where}" visibile="false" />
					<gene:campoScheda campo="CODGAR1" entita="GARE" where = "${where}" visibile="false"  />
					<gene:campoScheda campo="CODGAR" visibile="false" />
					<gene:campoScheda campo="TIPGEN" visibile="false" />
					<gene:campoScheda campo="AQOPER" visibile="false" />
					<gene:campoScheda campo="ELENCOE" entita="GARE" where = "${where}" visibile="false" />
					<gene:gruppoCampi idProtezioni="AGG">
						<gene:campoScheda>
							<td colspan="2"><b>Aggiudicazione definitiva lotti</b></td>
						</gene:campoScheda>
						<gene:campoScheda title="Numero lotti aggiudicati" campo="LOTTI_AGGIUDICATI" campoFittizio="true" definizione="N3" modificabile="false" value="${numLottiAggiudicati}"/>
						<gene:campoScheda title="Numero lotti non aggiudicati" campo="LOTTI_NO_AGGIUDICATI" campoFittizio="true" definizione="N3" modificabile="false" value="${numLottiNoAggiudicati}"/>
					</gene:gruppoCampi>
					
					<gene:gruppoCampi idProtezioni="VERB_AGG_DEF" >
						<gene:campoScheda>
							<td colspan="2"><b>Verbale aggiudicazione definitiva</b></td>
						</gene:campoScheda>
						<gene:campoScheda entita="GARE" where = "${where}" campo="DVERAG" />
		 				<gene:campoScheda entita="GARE" where = "${where}" campo="NPROVA"/>
					</gene:gruppoCampi>
					<gene:gruppoCampi idProtezioni="ATTO_AGG">
						<gene:campoScheda addTr="false">
							<tr id="rowTITOLO_ATTO_AGGIUDICAZIONE">
								<td><b>Atto di aggiudicazione (comune a tutti i lotti)</b></td>
								<td style="text-align: right; padding-right:10px; color:black;"><c:if test='${autorizzatoModifiche ne 2 and updateLista ne 1}'><a style="color: black;" href="javascript:apriModaleAtto();" title="Imposta atto aggiudicazione comune a tutti i lotti" >Imposta atto aggiudicazione comune a tutti i lotti</a></c:if></td>
							</tr>
						</gene:campoScheda>
						<gene:campoScheda entita="GARE" where = "${where}" campo="TATTOA" modificabile="false"/>
						<gene:campoScheda entita="GARE" where = "${where}" campo="DATTOA" modificabile="false"/>
						<gene:campoScheda entita="GARE" where = "${where}" campo="NATTOA" modificabile="false"/>
						<gene:campoScheda entita="GARE" where = "${where}" campo="NPROAA" modificabile="false"/>
					</gene:gruppoCampi>
					
					<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AGGDEF-scheda.AGGDEF.attiMultipli")}'>
					 <c:set var="tipoRelazione" value="TORN" />
					 	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneAttiGaraFunction" parametro='${tipoRelazione};${gene:getValCampo(key, "CODGAR")}' />
		 			 	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
							<jsp:param name="entita" value='GAREATTI'/>
							<jsp:param name="chiave" value='${tipoRelazione};${codiceGara}'/>
							<jsp:param name="nomeAttributoLista" value='attiGara' />
							<jsp:param name="idProtezioni" value="GAREATTIAGG" />
							<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gareatti/atti-aggiudicazione-gara.jsp"/>
							<jsp:param name="arrayCampi" value="'GAREATTI_ID_','GAREATTI_CODGAR','GAREATTI_NGARA_','GAREATTI_NUMATTO_','GAREATTI_TIPOATTO_','GAREATTI_DATAATTO_','GAREATTI_NUMPROTATTO_','GAREATTI_DATAPROTATTO_'" />
							<jsp:param name="sezioneListaVuota" value="false" />		
							<jsp:param name="titoloSezione" value="Altro atto di aggiudicazione" />
							<jsp:param name="titoloNuovaSezione" value="Nuovo atto di aggiudicazione" />
							<jsp:param name="descEntitaVociLink" value="atto di aggiudicazione" />
							<jsp:param name="msgRaggiuntoMax" value="gli atti di aggiudicazione"/>
							<jsp:param name="usaContatoreLista" value="true"/>
		 			 	</jsp:include>
					</c:if>
					
													
					<gene:gruppoCampi idProtezioni="COMUNICAZ">
						<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.GAROFF")}'>
							<c:set var="dicituraCauzione" value="e svincolo garanzia provvisoria"/>
						</c:if>
						<gene:campoScheda>
							<td colspan="2"><b>Comunicazione alle ditte dell'aggiudicazione definitiva ${dicituraCauzione}</b></td>
						</gene:campoScheda>
						<gene:campoScheda entita="GARE" where = "${where}" campo="DCOMAG" />
		 				<gene:campoScheda entita="GARE" where = "${where}" campo="NCOMAG" />
						<gene:campoScheda entita="GARE" where = "${where}" campo="DCOMNG" />
		 				<gene:campoScheda entita="GARE" where = "${where}" campo="NCOMNG" />
						<gene:campoScheda campo="NGARA" entita="GARE1" where = "TORN.CODGAR=GARE1.NGARA" visibile="false"/>
						<gene:campoScheda campo="DCOMSVIP" entita="GARE1" where = "TORN.CODGAR=GARE1.NGARA"/>
						<gene:campoScheda campo="NCOMSVIP" entita="GARE1" where = "TORN.CODGAR=GARE1.NGARA"/>
					</gene:gruppoCampi>

					<gene:gruppoCampi idProtezioni="COMUNOSSER">
						<gene:campoScheda>
							<td colspan="2"><b>Comunicazioni osservatorio</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="DCOMAGGSA"  entita="GARE" where = "${where}"/>
					</gene:gruppoCampi>
					
					<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N2" value="${paginaAttivaWizard}" />
					<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
					<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
					<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
					<input type="hidden" id="aggiudDef" name="aggiudDef" value="Si" />
				</gene:formScheda>
			</td>
		</tr>
		<!-- Fine Pagina scheda dati di gara -->
	</c:otherwise>
</c:choose>
	
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1  }'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
					<c:if test='${paginaAttivaWizard != 2  and autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaDatiGara)}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaScheda();">&nbsp;&nbsp;&nbsp;
					</c:if>
					
					<c:if test='${paginaAttivaWizard > 1}'>
						<INPUT type="button"  class="bottone-azione" value='< Fase precedente' title='Fase precedente' onclick="javascript:indietro();">&nbsp;
					</c:if>
					
					<c:if test='${paginaAttivaWizard < 3 }'>
						<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">
					</c:if>
						
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
</table>
<c:if test='${paginaAttivaWizard != 1 and autorizzatoModifiche ne 2 and updateLista ne 1}'>
${gene:callFunction3("it.eldasoft.gene.tags.functions.GetListaValoriTabellatoFunction",  pageContext,"A2045", "valoriTabA2045")}
<c:set var ="lottiAggiudicati" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoLottiAggiudicatiFunction",  pageContext,codiceGara)}'/>
 
<form id="formImpostaAtto">
	<div id="mascheraDatiAtto" title="Imposta atto aggiudicazione" style="display:none;">
	<table class="sceltaQform">
		<tr style="font:11px Verdana, Arial, Helvetica, sans-serif"> 
			<td colspan="2">
				<br>
				Mediante tale funzione è possibile specificare l'atto di aggiudicazione della gara, comune a tutti i lotti.
				<br>Alla conferma, i valori indicati verranno replicati in tutti i lotti della gara.
				<br><br>
			</td>				
		</tr>
		<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.TATTOA") }'>
		<tr style="font:11px Verdana, Arial, Helvetica, sans-serif">
			<td class="etichetta-atto">Tipo atto</td>	
			<td class="valore-dato">
			<select id="tattoa" name="tattoa" title="Tipo atto" >
				<option value="" title="&nbsp;" selected="selected" >&nbsp;</option>
				<c:forEach var="atto" items="${requestScope.valoriTabA2045}">
					<option value="${atto.tipoTabellato }" title="${atto.descTabellato }" <c:if test="${atto.tipoTabellato eq  datiRiga.GARE_TATTOA}">selected="selected"</c:if>>${atto.descTabellato }</option>
				</c:forEach>
			</select>
			</td>				
		</tr>
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.DATTOA") }'>
		<tr style="font:11px Verdana, Arial, Helvetica, sans-serif">
			<td class="etichetta-atto">Data</td>	
			<td >
			<input type="text" name="dattoa" id="dattoa" value="${datiRiga.GARE_DATTOA }" />
			</td>				
		</tr>
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NATTOA") }'>
		<tr style="font:11px Verdana, Arial, Helvetica, sans-serif">
			<td class="etichetta-atto">Numero</td>	
			<td >
			<input type="text" name="nattoa" id="nattoa" value="${datiRiga.GARE_NATTOA }" maxlength="16"/>
			</td>				
		</tr>
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NPROAA") }'>
		<tr style="font:11px Verdana, Arial, Helvetica, sans-serif">
			<td class="etichetta-atto">Numero protocollo</td>	
			<td >
			<input type="text" name="nproaa" id="nproaa" value="${datiRiga.GARE_NPROAA }" maxlength="10" />
			</td>				
		</tr>
		</c:if>
	</table>
	</div>
		
</form>
</c:if>
<gene:javaScript>

	function aggiudicazioneLotto(){
		var bustalotti="${ bustalotti}";
		document.location.href=contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-pg-aggiudLotto.jsp&key="+chiaveRiga + "&bustalotti=" + bustalotti;
		
	}

	
	
		function avanti(){
			if(document.forms[0].action.indexOf("Scheda.do") >= 0){
				document.forms[0].action = "${pageContext.request.contextPath}/Lista.do?"+csrfToken;
				document.forms[0].keyParent.value = document.forms[0].key.value;
			} else {
				document.forms[0].pgSort.value = "";
				document.forms[0].pgLastSort.value = "";
				document.forms[0].pgLastValori.value = "";
			}
			setValue("WIZARD_PAGINA_ATTIVA", ${paginaAttivaWizard + 1});
			listaVaiAPagina(0);
		}
		
	
	
	
	
		function indietro(){
			if(document.forms[0].action.indexOf("Scheda.do") >= 0){
				document.forms[0].action = "${pageContext.request.contextPath}/Lista.do?"+csrfToken;
				document.forms[0].keyParent.value = document.forms[0].key.value;
			} else {
				document.forms[0].pgSort.value = "";
				document.forms[0].pgLastSort.value = "";
				document.forms[0].pgLastValori.value = "";
			}
			setValue("WIZARD_PAGINA_ATTIVA", ${paginaAttivaWizard - 1});
			listaVaiAPagina(0);
		}
		
		function annulla(){
			document.forms[0].updateLista.value = "0";
			schedaAnnulla();
		}
			
		function modificaScheda(){
			document.forms[0].updateLista.value = "1";
			schedaModifica();
		}
		
		function conferma(){
			document.forms[0].updateLista.value = "0";
			schedaConferma();
		}
		
		function modelliPredispostiLocale(){
		/***********************************************************
			Copia della funzione modelliPredisposti dal file jsAzioniTags.jsp
			e addattamento al caso specifico
		 ***********************************************************/
			var entita,valori;
	
			try {
				entita = "TORN";
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
		
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
		redefineLabels();
		redefineTooltips();
		redefineTitles();
	</c:if>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>

	<c:if test='${paginaAttivaWizard != 1 and autorizzatoModifiche ne 2 and updateLista ne 1}'>
	
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.TATTOA") }'>
				$( "#tattoa" ).prop( "disabled", false );
			</c:when>
			<c:otherwise>
				$( "#tattoa" ).prop( "disabled", true );
				$( "#tattoa" ).css('background-color','#EFEFEF');
			</c:otherwise>
		</c:choose>
				
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.DATTOA") }'>
				$( "#dattoa" ).prop( "disabled", false );
				$( function() {
					$( "#dattoa" ).datepicker();
				  } );
			</c:when>
			<c:otherwise>
				$( "#dattoa" ).prop( "disabled", true );
				$( "#dattoa" ).css('background-color','#EFEFEF');
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.NATTOA") }'>
				$( "#nattoa" ).prop( "disabled", false );
			</c:when>
			<c:otherwise>
				$( "#nattoa" ).prop( "disabled", true );
				$( "#nattoa" ).css('background-color','#EFEFEF');
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.NPROAA") }'>
				$( "#nproaa" ).prop( "disabled", false );
			</c:when>
			<c:otherwise>
				$( "#nproaa" ).prop( "disabled", true );
				$( "#nproaa" ).css('background-color','#EFEFEF');
			</c:otherwise>
		</c:choose>
		
		function apriModaleAtto(){
			var opt = {
				open: function(event, ui) { 
					$(this).parent().children().children('.ui-dialog-titlebar-close').hide();
					$(this).parent().css("border-color","#C0C0C0");
					var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
					_divtitlebar.css("border","0px");
					_divtitlebar.css("background","#FFFFFF");
					var _dialog_title = $(this).parent().find("span.ui-dialog-title");
					_dialog_title.css("font-size","13px");
					_dialog_title.css("font-weight","bold");
					_dialog_title.css("color","#002856");
					$(this).parent().find("div.ui-dialog-buttonpane").css("background","#FFFFFF");
				},
				autoOpen: false,
				modal: true,
				width: 550,
				height:300,
				title: "Imposta atto di aggiudicazione comune a tutti i lotti",
				buttons: {
				"Conferma": function() {
					confermaModaleAtto();
				},
				"Annulla": function() {
						$( this ).dialog( "close" );
						
					}
				 }
			};
			
			$("#mascheraDatiAtto").dialog(opt).dialog("open");
		}
		
		function confermaModaleAtto(){
			var tattoa=$("#tattoa").val();
			var dattoa=$("#dattoa").val();
			var nattoa=$("#nattoa").val();
			var nproaa=$("#nproaa").val();
			var codiceGara="${codiceGara}";
			
			var lottiAggiudicati="${lottiAggiudicati}";
			if((dattoa==null || dattoa=="") && lottiAggiudicati=="true"){
				alert("Non è possibile annullare la data poichè vi sono lotti aggiudicati.");
				return;
			}
			
			$.ajax({
				type: "POST",
				dataType: "json",
				async: false,
				beforeSend: function(x) {
					if(x && x.overrideMimeType) {
						x.overrideMimeType("application/json;charset=UTF-8");
					}
				},
				url: "pg/ImpostaAttoLotti.do",
				data: {
					tattoa: tattoa ,
		            dattoa: dattoa,
		            nattoa: nattoa,
		            nproaa: nproaa,
		            codiceGara:codiceGara
				},
				success: function(data){
					historyReload();
					$( this ).dialog( "close" );
				},
				error: function(e){
					alert("Errore nell'aggiornamento dei dati");
				}
			});
		}
	</c:if>
</gene:javaScript>