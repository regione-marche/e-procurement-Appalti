<%/*
   * Created on 17-ott-2007
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

<jsp:include page="/WEB-INF/pages/gare/gare/fasiGara/defStepWizardFasiGara.jsp" />

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />



<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.RiepilogoImportiFunction", pageContext, codiceGara,numeroGara,codiceDitta)}' />
 <c:set var="tipoArticolo" value="lavorazione o fornitura" />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${codiceGara}' />

<c:if test='${integrazioneAUR eq "1"}'>
	<c:set var="carrello" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCarrelloAURFunction", pageContext, numeroGara)}' />
	<c:set var="fornitoreAcquisitoAUR" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.FornitoreAcquisitoAURFunction", pageContext, codiceGara,numeroGara,codiceDitta)}' />
</c:if>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

	<c:choose>
		<c:when test='${not empty param.stepWizard}'>
			<c:set var="stepWizard" value="${param.stepWizard}" />
		</c:when>
		<c:otherwise>
			<c:set var="stepWizard" value="${stepWizard}" />
		</c:otherwise>
	</c:choose>

	
	<c:choose>
		<c:when test='${not empty param.faseGara}'>
			<c:set var="faseGara" value="${param.faseGara}" />
		</c:when>
		<c:otherwise>
			<c:set var="faseGara" value="${faseGara}" />
		</c:otherwise>
	</c:choose>
	
	
	<c:choose>
		<c:when test='${faseGara >= 7}'>
			<c:set var="bloccoAggiudicazione" value="1" scope ="request" />
		</c:when>
		<c:otherwise>
			<c:set var="bloccoAggiudicazione" value="0" scope = "request"/>
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
			<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
			<c:set var="tipologiaGara" value="3" />
		</c:when>
		<c:otherwise>
			<c:set var="tipologiaGara" value="" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.isGaraTelematica}'>
			<c:set var="isGaraTelematica" value="${param.isGaraTelematica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isGaraTelematica" value="${isGaraTelematica}" />
		</c:otherwise>
	</c:choose>
	
	
	
	
	<c:choose>
		<c:when test='${not empty param.offtel}'>
			<c:set var="offtel" value="${param.offtel}" />
		</c:when>
		<c:otherwise>
			<c:set var="offtel" value="${offtel}" />
		</c:otherwise>
	</c:choose>
	
		
<c:choose>
	<c:when test='${not empty param.modlicg}'>
		<c:set var="modlicg" value="${param.modlicg}" />
	</c:when>
	<c:otherwise>
		<c:set var="modlicg" value="${modlicg}" />
	</c:otherwise>
</c:choose>
	
<c:choose>
	<c:when test='${not empty param.daAgg}'>
		<c:set var="daAgg" value="${param.daAgg}" />
	</c:when>
	<c:otherwise>
		<c:set var="daAgg" value="${daAgg}" />
	</c:otherwise>
</c:choose>	

<c:choose>
	<c:when test='${not empty param.bustalotti}'>
		<c:set var="bustalotti" value="${param.bustalotti}" />
	</c:when>
	<c:otherwise>
		<c:set var="bustalotti" value="${bustalotti}" />
	</c:otherwise>
</c:choose>	

<c:choose>
	<c:when test='${not empty param.riboepvVis}'>
		<c:set var="riboepvVis" value="${param.riboepvVis}" />
	</c:when>
	<c:otherwise>
		<c:set var="riboepvVis" value="${riboepvVis}" />
	</c:otherwise>
</c:choose>	
	
<c:if test="${modlicg eq '6' }">
	<c:set var="esistonoPunteggiEco" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggioValorizzatoFunction", pageContext, numeroGara,"2")}' />
</c:if>

<c:set var="tipoForniture" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction", pageContext, codiceGara)}' />
<c:if test='${tipoForniture == 98}'>
	<c:set var="tipoArticolo" value="prodotto" />
	<c:if test='${isGaraLottiConOffertaUnica}'>
		<c:set var="parametro" value="${codiceGara}" />
	</c:if>
	<c:if test='${!isGaraLottiConOffertaUnica}'>
		<c:set var="parametro" value="${numeroGara}" />
	</c:if>
	<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,parametro)}' />
</c:if>

	
<c:if test='${isGaraLottiConOffertaUnica}'>
	<c:if test="${stepWizard < 10 }">
		<c:set var="stepWizard" value="${stepWizard}0" />
	</c:if>
	<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoAmmgarFunction", pageContext,codiceGara,codiceDitta,stepWizard)}' />
</c:if>	

<c:choose>
		<c:when test='${isGaraLottiConOffertaUnica eq "true" and stepWizard eq step6Wizard}' >
			<c:set var="isPrequalifica" value="true" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="isPrequalifica" value="false" scope="request" />
		</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isRicercaMercatoNegoziata}'>
		<c:set var="isRicercaMercatoNegoziata" value="${param.isRicercaMercatoNegoziata}" />
	</c:when>
	<c:otherwise>
		<c:set var="isRicercaMercatoNegoziata" value="${isRicercaMercatoNegoziata}" />
	</c:otherwise>
</c:choose>	


<c:set var="IsDittaAggiudicataria" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsDittaAggiudicatariaFunction", pageContext,numeroGara,codiceDitta,isGaraLottiConOffertaUnica)}'/>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />

<c:if test='${integrazioneWSERP eq "1" && requestScope.tipoWSERP eq "SMEUP"}'>
	<c:set var="presenzaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPPresenzaRdaFunction", pageContext, codiceGara, numeroGara, requestScope.tipoWSERP)}' />
</c:if>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GCAP_DPRE-lista">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	
	<c:choose>
	<c:when test='${ stepWizard eq step6Wizard}' >
		<c:set var="dettaglioTitle" value="Verifica conformità lavorazioni e forniture della ditta ${nomimo}" />
		<c:set var="wherePREQ" value='' />
	</c:when>
	<c:otherwise>
		<c:set var="dettaglioTitle" value="Dettaglio offerta prezzi della ditta ${nomimo}" />
		<c:set var="wherePREQ" value=' and exists (select ngara5 from ditg where (fasgar is null or fasgar>5) and codgar5=v_gcap_dpre.codgar and ngara5=v_gcap_dpre.ngara  and dittao=v_gcap_dpre.cod_ditta)'/>
	</c:otherwise>
	</c:choose>

	<gene:setString name="titoloMaschera" value='${dettaglioTitle}'/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<gene:set name="titoloMenu">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
	
	
	<% // viene gestita l'apertura della pagina anche da TORN %>
	<c:choose>
		<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
			<% // Apertura da GARE %>
			<c:set var="ordinamento" value="8;3"/>
			<c:set var="where" value='V_GCAP_DPRE.NGARA = #DITG.NGARA5# AND V_GCAP_DPRE.CODGAR = #DITG.CODGAR5# AND V_GCAP_DPRE.COD_DITTA = #DITG.DITTAO# ' />
		</c:when>
		<c:otherwise>
			<% // Apertura da TORN %>
			<c:set var="ordinamento" value="7;8;3"/>
			<c:choose>
				<c:when test="${ stepWizard eq step6Wizard}">
					<c:set var="where" value='V_GCAP_DPRE.CODGAR = #DITG.CODGAR5# AND V_GCAP_DPRE.COD_DITTA = #DITG.DITTAO# and exists (select g.ngara from gare g,ditg, gare1 g1 where modlicg in (5,6,14,16) and g.codgar1=v_gcap_dpre.codgar and g.ngara=v_gcap_dpre.ngara'/>
					<c:set var="where" value="${where } and g.codgar1=codgar5 and ngara5=g.ngara and dittao = v_gcap_dpre.cod_ditta and (partgar='1' or partgar is null) and (ditg.fasgar > 4 or ditg.fasgar is null) and g1.ngara = g.ngara and g1.valtec='1')  "/>
					<c:set var="whereOEPV" value="select distinct(ngara)  from V_GCAP_DPRE where ${where}"/>
					<c:set var="whereOEPV" value="${fn:replace(whereOEPV,'in (5,6,14,16)', ' = 6') }"/>
					<c:set var="codiceGaraApici" value="'${codiceGara }'"/>
					<c:set var="whereOEPV" value="${fn:replace(whereOEPV,'#DITG.CODGAR5#', codiceGaraApici) }"/>
					<c:set var="codiceDittaApici" value="'${codiceDitta }'"/>
					<c:set var="whereOEPV" value="${fn:replace(whereOEPV,'#DITG.DITTAO#', codiceDittaApici) }"/>
					<c:set var="esistonoLavorazioniOEPVConPunteggioTEC" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoLavorazioniOEPVDittePunteggioValorizzatoFunction", pageContext,whereOEPV,"TEC")}'/>
 				</c:when>
				<c:otherwise>
					<c:set var="where" value='V_GCAP_DPRE.CODGAR = #DITG.CODGAR5# AND V_GCAP_DPRE.COD_DITTA = #DITG.DITTAO# and exists (select ngara from gare,ditg where modlicg in (5,6,14,16) and codgar1=v_gcap_dpre.codgar and ngara=v_gcap_dpre.ngara'/>
					<c:set var="where" value="${where } and codgar1=codgar5 and ngara5=ngara and dittao = v_gcap_dpre.cod_ditta and (partgar='1' or partgar is null))  "/>
					<c:if test="${stepWizard eq step7Wizard }">
						<c:set var="whereOEPV" value="select distinct(ngara)  from V_GCAP_DPRE where ${where}"/>
						<c:set var="whereOEPV" value="${fn:replace(whereOEPV,'in (5,6,14,16)', ' = 6') }"/>
						<c:set var="codiceGaraApici" value="'${codiceGara }'"/>
						<c:set var="whereOEPV" value="${fn:replace(whereOEPV,'#DITG.CODGAR5#', codiceGaraApici) }"/>
						<c:set var="codiceDittaApici" value="'${codiceDitta }'"/>
						<c:set var="whereOEPV" value="${fn:replace(whereOEPV,'#DITG.DITTAO#', codiceDittaApici) }"/>
						<c:set var="esistonoLavorazioniOEPVConPunteggioECO" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoLavorazioniOEPVDittePunteggioValorizzatoFunction", pageContext,whereOEPV,"ECO")}'/>
					</c:if>
				</c:otherwise>
			</c:choose>
			
		</c:otherwise>
	</c:choose>
	
	<c:set var="ricercaVariazioneNeiLotti" value="2" />
	<c:if test='${isGaraLottiConOffertaUnica eq "true"}' >
		<c:set var="ricercaVariazioneNeiLotti" value="1" />
	</c:if>
	
	<c:set var="esisteVariazionePrezzo" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoVariazioniPrezzoFunction",  pageContext, numeroGara, "", codiceDitta,ricercaVariazioneNeiLotti)}'/>
	
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
		
  	<% // Creo la lista per gcap e dpre mediante la vista v_gcap_dpre %>
		<table class="lista">
			<c:if test="${ stepWizard ne step6Wizard}">
			<tr>
				<td>
					<table class="arealayout">
						<c:if test='${isPrequalifica eq "false" and isGaraLottiConOffertaUnica ne "true"}' >
						
							<tr>
								<td width="300px">Importo netto derivante dal dettaglio prezzi:</td>
								<td align="right" width="135px"> &nbsp;<span id="totaleNetto">${totaleNetto} &euro;&nbsp;&nbsp;</span></td>
								<td></td>
							</tr>
							<tr>
								<td width="300px">Importo non soggetto a ribasso:</td>
								<td align="right" width="135px">&nbsp;<span id="importoNoRibasso">${importoNoRibasso} &euro;&nbsp;&nbsp;</span>
								</td>
								<td></td>
							</tr>
							<c:if test='${sicinc ne "2" or sicinc eq ""}'>
								<tr>
									<td width="300px">Importo sicurezza:</td>
									<td align="right" width="135px" >&nbsp;<span id="importoSicurezza">${importoSicurezza} &euro;&nbsp;&nbsp;</span>
									</td>
									<td></td>
								</tr>
							</c:if>
							<tr>
								<td width="300px" ><b>Totale offerto complessivo della ditta:</b></td>
								<td align="right" width="135px"  class="bordoSuperiore">&nbsp;<span id="totaleOfferto">${totaleOfferto} &euro;&nbsp;&nbsp;</span>
								</td>
								<td align="left">
									&nbsp;&nbsp;&nbsp;
									
								</td>
							</tr>
							<tr>
								<td width="300px"><b>Importo offerto effettivo:</b></td>
								<td align="right" width="135px">&nbsp;<span id="importoEffettivo">${importoEffettivo} &euro;&nbsp;&nbsp;</span>
								</td>
								<td></td>
							</tr>
							<c:if test='${ribcal eq 3}'>
								<tr>
									<td colspan="3"/> 
								</tr>
								<tr>
									<td width="300px">Ribasso pesato offerto complessivo:</td>
									<td align="right" width="135px" >&nbsp;<span id="ribassoPesato">${ribasso} %&nbsp;</span>
									</td>
									<td></td>
								</tr>
							</c:if>
						
						</c:if>
					</table>
				</td>
			</tr>
			</c:if>	
			
			<tr>
				<td>
  				<gene:formLista entita="V_GCAP_DPRE" where='${where} ${wherePREQ}' pagesize="20" tableclass="datilista" sortColumn="${ordinamento}" gestisciProtezioni="true"
  					gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreV_GCAP_DPRE">
  					
  					<c:set var="issoloditta" value='${datiRiga.V_GCAP_DPRE_ISSOLODITTA}'/>
					<c:set var="isquantimod" value='${datiRiga.V_GCAP_DPRE_ISQUANTIMOD}'/>
					
					<c:if test='${(isPrequalifica eq "true" and (bloccoAggiudicazione eq 1 or (isGaraTelematica and stepWizard eq step6Wizard and faseGara > 5))) or updateLista eq 1 or esisteGaraOLIAMM eq "true" or offtel eq 1 or presenzaRda eq "true" or esisteVariazionePrezzo eq "true"}'>
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
					</c:if>
  					
  					<gene:redefineInsert name="addToAzioni" >

<c:choose>
<c:when test='${(isPrequalifica eq "true" )}'>
 <c:if test='${bloccoAggiudicazione ne 1 and esisteVariazionePrezzo ne "true"}'>
 	
						<c:choose>
							<c:when test='${updateLista eq 1 }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaFasiGara) and datiRiga.rowCount > 0 and (stepWizard eq step10Wizard or (((stepWizard < step8Wizard and not isGaraTelematica) or isGaraTelematica and faseGara eq 5) and bloccoAggiudicazione ne 1)) and (empty offtel or offtel ne 1)}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
												${gene:resource("label.tags.template.dettaglio.schedaModifica")}
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 and bloccoAmmgar ne true and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.calcolaConformitaSingoliLotti") and (not isGaraTelematica or (isGaraTelematica and faseGara eq 5)) and (empty offtel or offtel ne 1) and esistonoLavorazioniOEPVConPunteggioTEC ne "si" }'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:AggiornaImporto('${totaleOfferto}');" title='Calcola conformità singoli lotti' tabindex="1506">
												Calcola conformità singoli lotti
											</a>
										</td>
									</tr>	
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.Importa-excel-offertaPrezzi") and offtel ne 1 and isPrequalifica eq "false" and (presenzaRda eq "1" || presenzaRda eq "2")}'>
	  					    		<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupImportaDaExcel();" title='Importa da Excel' tabindex="1503">
												Importa da Excel
											</a>
										</td>
									</tr>	
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 && (tipoforniture eq 1 or tipoforniture eq 2) && integrazioneAUR eq "1" && fornitoreAcquisitoAUR eq "SI" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.importOffertaAUR")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:acquisisciDaAUR();" title='Acquisisci offerta da AUR' tabindex="1504">
												Acquisisci offerta da AUR
											</a>
										</td>
									</tr>	
								</c:if>
								<c:if test='${(autorizzatoModifiche ne "2") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.cancellaDettaglioPrezzi") and offtel ne 1 and isPrequalifica eq "false"}'>
			  						<tr>
										<td class="vocemenulaterale">
											<a href="javascript:cancellaDettaglioPrezzi();" title="Cancella dettaglio prezzi" tabindex="1505">
													Cancella dettaglio prezzi
											</a>
								  		</td>
								    </tr>
								</c:if>
							
							</c:otherwise>
						</c:choose>
 </c:if>
</c:when>
<c:otherwise>
	 											
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.Importa-excel-offertaPrezzi") and offtel ne 1 and stepWizard ne step6Wizard and presenzaRda ne "true" and esisteVariazionePrezzo ne "true"}'>
					    		<tr>
									<td class="vocemenulaterale">
										<a href="javascript:apriPopupImportaDaExcel();" title='Importa da Excel' tabindex="1503">
											Importa da Excel
										</a>
									</td>
								</tr>	
							</c:if>
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.importXLSVariazionePrezzi") and IsDittaAggiudicataria eq "true" and presenzaRda ne "true" and tipoForniture ne 1 and tipoForniture ne 2 and tipoForniture ne 98}'>
					    		<tr>
									<td class="vocemenulaterale">
										<a href="javascript:importaExcelVarPrezzi('${numeroGara }','${codiceGara }','${tipologiaGara }','${codiceDitta }');" title='Importa da Excel per variazione prezzi' tabindex="1503">
											Importa da Excel per variazione prezzi
										</a>
									</td>
								</tr>	
							</c:if>
							<c:if test='${autorizzatoModifiche ne 2 && (tipoforniture eq 1 or tipoforniture eq 2) && integrazioneAUR eq "1" && fornitoreAcquisitoAUR eq "SI" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.importOffertaAUR") && stepWizard ne step6Wizard and esisteVariazionePrezzo ne "true"}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:acquisisciDaAUR();" title='Acquisisci offerta da AUR' tabindex="1504">
											Acquisisci offerta da AUR
										</a>
									</td>
								</tr>	
							</c:if>
							
	  						<c:if test='${(autorizzatoModifiche ne "2") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.cancellaDettaglioPrezzi") and offtel ne 1 and stepWizard ne step6Wizard and esisteVariazionePrezzo ne "true"}'>
		  						<tr>
									<td class="vocemenulaterale">
										<a href="javascript:cancellaDettaglioPrezzi();" title="Cancella dettaglio prezzi" tabindex="1505">
												Cancella dettaglio prezzi
										</a>
							  		</td>
							    </tr>
							</c:if>
							<c:if test='${(autorizzatoModifiche ne "2") and (dittaAggiudicataria eq codiceDitta) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.verificaCorrezionePrezzi") and (isGaraLottiConOffertaUnica ne "true") and (tipoAppalto eq "1") and stepWizard ne step6Wizard and esisteVariazionePrezzo ne "true"}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:verificaCorrezionePrezzi();" title="Verifica e correzione prezzi" tabindex="1506">
												Verifica e correzione prezzi
										</a>
							  		</td>
							    </tr>
	  						</c:if>
	  						<c:if test='${(autorizzatoModifiche ne "2") and esistonoPunteggiEco ne "si" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.aggiornaImportoOfferto") and ((totaleOfferto ne importoEffettivo and isGaraLottiConOffertaUnica) or !isGaraLottiConOffertaUnica) and bloccoAggiudicazione ne 1 and offtel ne 1 and stepWizard ne step6Wizard and esistonoLavorazioniOEPVConPunteggioECO ne "si" and esisteVariazionePrezzo ne "true"}'>
								<c:set var="titoloFunzione" value="Calcola importo offerto da dettaglio prezzi" />
								<c:if test='${isGaraLottiConOffertaUnica}'>
									<c:set var="titoloFunzione" value="Calcola importo offerto singoli lotti" />
								</c:if>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:AggiornaImporto('${totaleOfferto}')" title="${titoloFunzione }" tabindex="1506">
												${titoloFunzione }
										</a>
							  		</td>
							    </tr>
							</c:if>	 

</c:otherwise>
</c:choose>
  					</gene:redefineInsert>
  					 					
  					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
						<c:if test="${currentRow >= 0 and (updateLista eq 0)}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.V_GCAP_DPRE-scheda")}' >
									<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza ${tipoArticolo}"/>
								</c:if>
  							
  							<c:if test='${isPrequalifica eq "false" or bloccoAggiudicazione ne 1}'>
								<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.V_GCAP_DPRE-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD") && !(datiRiga.V_GCAP_DPRE_SOLSIC eq 1 || datiRiga.V_GCAP_DPRE_SOGRIB eq 1) and offtel ne 1
									and (not isGaraTelematica or (isGaraTelematica && faseGara eq 5 && stepWizard eq step6Wizard) or (faseGara eq 6 && stepWizard eq step7Wizard))}' >
									
									<c:if test="${esisteVariazionePrezzo ne 'true' }">
										<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica ${tipoArticolo}" />
									</c:if>
								</c:if>
								<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL") && issoloditta eq "1" && esisteGaraOLIAMM ne "true" && offtel ne 1 and esisteVariazionePrezzo ne "true"}' >
									<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina ${tipoArticolo}" />
								</c:if>
							</c:if>
							</gene:PopUp>
							<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && issoloditta eq "1" && esisteGaraOLIAMM ne "true" && esisteVariazionePrezzo ne "true"}'>
								<input type="checkbox" name="keys" value="${chiaveRiga}"  />
							</c:if>					
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="CODGAR" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="CONTAF" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="COD_DITTA" visibile="false" edit="${updateLista eq 1}"/>
					
					<gene:campoLista title="" width="20" >
					<c:choose>
						<c:when test='${issoloditta eq 1}'>
							<c:choose>
								<c:when test='${tipoForniture eq "98"}'>
									<span id="INFO_TOOLTIP${currentRow }" title=" - Prodotto definito dalla ditta">
										<IMG SRC="${contextPath}/img/issoloditta.gif" >
									</span>
								</c:when>
								<c:otherwise>
									<span id="INFO_TOOLTIP${currentRow }" title=" - Lavorazione o fornitura definita dalla ditta">
									<IMG SRC="${contextPath}/img/issoloditta.gif" >
									</span>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test='${tipoForniture ne "98" and isquantimod eq 1}'>
							<span id="INFO_TOOLTIP${currentRow }" title=" - Quantità della lavorazione o fornitura modificata dalla ditta">
							<IMG SRC="${contextPath}/img/isquantimod.png"  >
							</span>
						</c:when>
						<c:when test='${tipoForniture eq "98" }'>
							<c:set var="msgIncongruenzeTitolo" value=""/>
							<c:set var="msgIncongruenze" value=""/>
							
							<c:if test="${datiRiga.V_GCAP_DPRE_UNIMIS ne datiRiga.V_GCAP_DPRE_UNIMISEFF}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li>Unità di misura della confezione diversa da quella definita per il prodotto</li> "/>
							</c:if>
							<c:if test="${empty datiRiga.DPRE_SAN_NUNICONF and !empty datiRiga.V_GCAP_DPRE_QUANTI}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li>Quantità per confezione non specificata</li> "/>
							</c:if>
							<c:if test="${datiRiga.V_GCAP_DPRE_UNIMIS eq datiRiga.V_GCAP_DPRE_UNIMISEFF and !empty datiRiga.DPRE_SAN_QUANTIUNI and datiRiga.DPRE_SAN_QUANTIUNI+1 < datiRiga.GCAP_QUANTI+1}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li><b>Quantità complessiva offerta inferiore alla richiesta </b></li> "/>
							</c:if>
							<c:if test="${datiRiga.V_GCAP_DPRE_UNIMIS eq datiRiga.V_GCAP_DPRE_UNIMISEFF and !empty datiRiga.DPRE_SAN_QUANTIUNI and datiRiga.DPRE_SAN_QUANTIUNI+1 > datiRiga.GCAP_QUANTI+1}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li>Quantità complessiva offerta superiore alla richiesta</li> "/>
							</c:if>
							<c:if test="${datiRiga.V_GCAP_DPRE_UNIMIS eq datiRiga.V_GCAP_DPRE_UNIMISEFF and !empty datiRiga.DPRE_SAN_NUNICONF and !empty datiRiga.GCAP_NUNICONF and datiRiga.DPRE_SAN_NUNICONF+1 > datiRiga.GCAP_NUNICONF+1}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li><b>Quantità per confezione offerta maggiore del valore massimo stabilito</b></li> "/>
							</c:if>
							
							<c:if test="${empty datiRiga.DPRE_SAN_CODPROD and !empty datiRiga.V_GCAP_DPRE_QUANTI}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li>Codice prodotto fornitore non specificato</li> "/>
							</c:if>
							<c:if test="${!empty datiRiga.GCAP_IVAPROD and !empty datiRiga.DPRE_SAN_IVAPVPUBBL and datiRiga.GCAP_IVAPROD ne datiRiga.DPRE_SAN_IVAPVPUBBL}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li>Aliquota iva della confezione diversa da quella definita per il prodotto</li> "/>
							</c:if>
							<c:if test="${empty datiRiga.GCAP_IVAPROD and empty datiRiga.DPRE_SAN_IVAPVPUBBL and !empty datiRiga.V_GCAP_DPRE_QUANTI}">
								<c:set var="msgIncongruenze" value="${msgIncongruenze }<li>Aliquota iva non specificata</li> "/>
							</c:if>
							
							<c:if test='${msgIncongruenze ne ""}'>
								<c:set var="msgIncongruenze" value="${msgIncongruenzeTitolo} - ${msgIncongruenze }"/>
								<span id="INFO_TOOLTIP${currentRow }" title="${msgIncongruenze }">
									<IMG SRC="${contextPath}/img/isquantimod.png" >
								</span>
							</c:if>
							
						</c:when>
					</c:choose>
					</gene:campoLista>
					<gene:campoLista campo="NGARA" visibile="false" title='Codice lotto' edit = "${updateLista eq 1}"/>
					<gene:campoLista campo="NGARA" visibile='${isGaraLottiConOffertaUnica eq "true"}' title='Codice lotto'/>
					<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.V_GCAP_DPRE-scheda") and (updateLista eq 0)}'/>				
					<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="NORVOC" title="N." headerClass="sortable" width="30" visibile="false"/>
					<gene:campoLista campo="CODIGA" title="Lotto" visibile='${isGaraLottiConOffertaUnica eq "true"}' headerClass="sortable"/>
					<gene:campoLista campo="CODVOC" title="Voce" headerClass="sortable" width="80" href="${gene:if(visualizzaLink, link, '')}" />
					<gene:campoLista campo="VOCE" headerClass="sortable" />
					<gene:campoLista title="Um" campo="UNIMIS" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" width="55" visibile = '${isPrequalifica eq "false" and tipoForniture ne "98" and stepWizard ne step6Wizard }' headerClass="sortable"/>
					<gene:campoLista title="Um" campo="UNIMISEFF" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" width="55" visibile = '${isPrequalifica eq "false" and tipoForniture eq "98" and stepWizard ne step6Wizard}' headerClass="sortable"/>
					<gene:campoLista campo="QUANTIEFF" visibile = '${isPrequalifica eq "false" and tipoForniture ne "98" and stepWizard ne step6Wizard}' headerClass="sortable" width="80"/>
					<gene:campoLista campo="QUANTI" title="Numero confezioni" visibile = '${isPrequalifica eq "false" and tipoForniture eq "98" and stepWizard ne step6Wizard}' headerClass="sortable" width="80"/>
	  				<gene:campoLista campo="PERRIB" visibile = '${isPrequalifica eq "false" and stepWizard ne step6Wizard and ribcal eq "3"}' headerClass="sortable" width="80" title="Ribasso offerto"/>
	  				<gene:campoLista campo="PREOFF" title='${gene:if(tipoForniture eq "98","Prezzo offerto per confezione","Prezzo unitario")}' visibile = '${isPrequalifica eq "false" and stepWizard ne step6Wizard}' headerClass="sortable" />
	  				<gene:campoLista campo="PERCIVAEFF" visibile = '${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.PERCIVA") and (tipoForniture eq "3" or tipoForniture eq "") and isPrequalifica eq "false" and stepWizard ne step6Wizard}' headerClass="sortable" width="80"/>
					<gene:campoLista campo="IMPOFF" visibile = '${isPrequalifica eq "false" and stepWizard ne step6Wizard}' headerClass="sortable" />
					<gene:campoLista campo="DATACONSOFF" visibile = '${isRicercaMercatoNegoziata eq "true"}' headerClass="sortable" />
					<gene:campoLista campo="TIPOLOGIA" visibile = '${isRicercaMercatoNegoziata eq "true"}' headerClass="sortable" />
					<gene:campoLista campo="NOTE" visibile = '${isRicercaMercatoNegoziata eq "true"}'  />
					<gene:campoLista campo="RIBPESO" visibile = '${isPrequalifica eq "false" and stepWizard ne step6Wizard and ribcal eq "3"}' headerClass="sortable" width="80" title="Ribasso pesato"/>
					<gene:campoLista campo="ISSOLODITTA" visibile="false" />
					<gene:campoLista campo="ISQUANTIMOD" visibile="false" />
					<gene:campoLista campo="SOLSIC" visibile="false" />
					<gene:campoLista campo="SOGRIB" visibile="false" />
					<gene:campoLista campo="ACQUISITO" entita="DPRE_SAN" visibile="false" where="DPRE_SAN.NGARA=V_GCAP_DPRE.NGARA and DPRE_SAN.CONTAF=V_GCAP_DPRE.CONTAF and DPRE_SAN.DITTAO=V_GCAP_DPRE.COD_DITTA" edit = "${updateLista eq 1}"/>
					<gene:campoLista campo="CODAIC" entita="DPRE_SAN" visibile="false" where="DPRE_SAN.NGARA=V_GCAP_DPRE.NGARA and DPRE_SAN.CONTAF=V_GCAP_DPRE.CONTAF and DPRE_SAN.DITTAO=V_GCAP_DPRE.COD_DITTA" edit = "${updateLista eq 1}"/> 
					<gene:campoLista campo="CODPROD" entita="DPRE_SAN" visibile="false" where="DPRE_SAN.NGARA=V_GCAP_DPRE.NGARA and DPRE_SAN.CONTAF=V_GCAP_DPRE.CONTAF and DPRE_SAN.DITTAO=V_GCAP_DPRE.COD_DITTA" edit = "${updateLista eq 1}"/> 
					<gene:campoLista campo="REQMIN" visibile = '${isPrequalifica eq "true" or stepWizard eq step6Wizard}' edit = "${updateLista eq 1 and bloccoAggiudicazione ne 1}"/>
  					<c:if test='${tipoForniture eq "98" }'>
						<gene:campoLista campo="QUANTI" entita='GCAP' where="GCAP.NGARA = V_GCAP_DPRE.NGARA AND GCAP.CONTAF=V_GCAP_DPRE.CONTAF" visibile = "false"/>
  						<gene:campoLista campo="NUNICONF" entita='DPRE_SAN' where="DPRE_SAN.NGARA=V_GCAP_DPRE.NGARA and DPRE_SAN.CONTAF=V_GCAP_DPRE.CONTAF and DPRE_SAN.DITTAO=V_GCAP_DPRE.COD_DITTA" visibile="false"/>
  						<gene:campoLista campo="QUANTIUNI" entita='DPRE_SAN' where="DPRE_SAN.NGARA=V_GCAP_DPRE.NGARA and DPRE_SAN.CONTAF=V_GCAP_DPRE.CONTAF and DPRE_SAN.DITTAO=V_GCAP_DPRE.COD_DITTA" visibile="false"/>
  						<gene:campoLista campo="NUNICONF" entita='GCAP' where="GCAP.NGARA = V_GCAP_DPRE.NGARA AND GCAP.CONTAF=V_GCAP_DPRE.CONTAF" visibile = "false"/>
  						<gene:campoLista campo="IVAPVPUBBL" entita='DPRE_SAN' where="DPRE_SAN.NGARA=V_GCAP_DPRE.NGARA and DPRE_SAN.CONTAF=V_GCAP_DPRE.CONTAF and DPRE_SAN.DITTAO=V_GCAP_DPRE.COD_DITTA" visibile="false"/>
  						<gene:campoLista campo="IVAPROD" entita='GCAP' where="GCAP.NGARA = V_GCAP_DPRE.NGARA AND GCAP.CONTAF=V_GCAP_DPRE.CONTAF" visibile = "false"/>
  					</c:if>
  					
						<input type="hidden" name="AGGIORNA_DA_LISTA" id="AGGIORNA_DA_LISTA" value="0" />
						<input type="hidden" name="TOTALE_OFFERTO" value="${totaleOfferto}" />
						<input type="hidden" name="NUMERO_GARA" value="${numeroGara }" />
						<input type="hidden" name="CODICE_GARA" value="${codiceGara }" />
						<input type="hidden" name="CODICE_DITTA" value="${codiceDitta }" />
						<input type="hidden" name="DITTA_AGG" value='${gene:if(dittaAggiudicataria eq codiceDitta, "SI", "NO")}' />
						<input type="hidden" name="SICINC" value="${sicinc}" />
						<input type="hidden" name="GARAOFFERTAUNICA" value="${isGaraLottiConOffertaUnica}" />
						<input type="hidden" name="FASEGARA" value="${faseGara}" />
						<input type="hidden" name="RIBCAL" value="${ribcal}" />
						<input type="hidden" name="stepWizard" value="${stepWizard}" />
						<input type="hidden" name="numeroProdotti" id="numeroProdotti" value="" />
						<input type="hidden" name="PREQUALIFICA" value="${isPrequalifica}" />
						<input type="hidden" name="BLOCCO_AGG" value="${bloccoAggiudicazione}" />
						<input type="hidden" name="BLOCCO_VARPREZZI" value="${esisteVariazionePrezzo}" />
						
						<input type="hidden" name="isGaraTelematica" value="${isGaraTelematica}" />
						<input type="hidden" name="faseGara" value="${faseGara}" />
						<input type="hidden" name="offtel" value="${offtel}" />
						<input type="hidden" name="modlicg" value="${modlicg}" />
						<input type="hidden" name="daAgg" value="${daAgg}" />	
						<input type="hidden" name="riboepvVis" value="${riboepvVis}" />
						<input type="hidden" name="isRicercaMercatoNegoziata" value="${isRicercaMercatoNegoziata}" />
										
				</gene:formLista>
				</td>
			</tr>
			<tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:set var="pulsanteVisibile" value="false" />
					<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
						
						<c:if test='${isPrequalifica eq "true" }'>
							<c:if test='${autorizzatoModifiche ne "2" && bloccoAggiudicazione ne 1 && esisteVariazionePrezzo ne "true"}'>
								<c:choose>
								<c:when test='${updateLista eq 1}'>
									<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
									<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
								</c:when>
								<c:otherwise>
									<c:if test='${bloccoAmmgar ne true and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GCAP_DPRE-lista.calcolaConformitaSingoliLotti") and (not isGaraTelematica or (isGaraTelematica and faseGara eq 5)) and esistonoLavorazioniOEPVConPunteggioTEC ne "si"}'>
										<c:set var="pulsanteVisibile" value="true" />
										<input type="button" class="bottone-azione" value="Calcola conformità singoli lotti" title="Calcola conformità singoli lotti" onclick="javascript:AggiornaImporto('${totaleOfferto}');"/>&nbsp;
									</c:if>
									
									<c:if test='${datiRiga.rowCount > 0 and ((isGaraTelematica && faseGara eq 5) or not isGaraTelematica) }'>
										<c:set var="pulsanteVisibile" value="true" />
										<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaLista();">
									</c:if>
								</c:otherwise>
							  </c:choose>
						  	</c:if>
						  	
					  	</c:if>
					</c:if>
					<c:if test='${isPrequalifica eq "false" }'>
						<c:choose>
							<c:when test='${updateLista eq 1}'>
								<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
								<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
							</c:when>
							<c:otherwise>
								<c:if test='${bloccoAggiudicazione ne 1 && esisteVariazionePrezzo ne "true" && esisteVariazionePrezzo ne "true" && autorizzatoModifiche ne "2" && stepWizard eq step6Wizard && (not isGaraTelematica or (isGaraTelematica && faseGara eq 5)) && isGaraLottiConOffertaUnica ne "true"}'>
									<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:listaApriInModifica();">
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:if test='${(isPrequalifica eq "false" or (bloccoAggiudicazione ne 1 and !(isPrequalifica eq "true" and isGaraTelematica && faseGara > 5))) and esisteGaraOLIAMM ne "true" and offtel ne 1 and presenzaRda ne "true" and esisteVariazionePrezzo ne "true"}'>
						<c:if test='${autorizzatoModifiche ne "2" && gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") and updateLista ne 1 }'>
							<c:set var="pulsanteVisibile" value="true" />
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
					  	</c:if>
		 				 <c:if test='${autorizzatoModifiche ne "2" && gene:checkProtFunz(pageContext,"INS","LISTANUOVO") and updateLista ne 1 and offtel ne 1 and presenzaRda ne "true"}'>
					    	<c:set var="pulsanteVisibile" value="true" />
					    	<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovo()">
				     	</c:if>
					</c:if>
					
					<c:choose>
						<c:when test="${isPrequalifica eq 'true' }">
							<c:set var="testoIndietro" value="Torna alla valutazione tecnica della ditta" />
						</c:when>
						<c:when test="${daAgg eq 'SI' }">
							<c:set var="testoIndietro" value="Torna al dettaglio aggiudicazione" />
						</c:when>
						<c:when test="${isGaraLottiConOffertaUnica eq 'true' and bustalotti eq 2}">
							<c:set var="testoIndietro" value="Torna a offerta economica della ditta" />
						</c:when>
						<c:otherwise>
							<c:set var="testoIndietro" value="Torna a elenco concorrenti" />
						</c:otherwise>
					</c:choose>
					<c:if test='${updateLista ne 1 }'>
						<c:if test='${pulsanteVisibile eq "true"}'>
							<br><br>
						</c:if>
						<INPUT type="button"  class="bottone-azione" value='${testoIndietro }' title='${testoIndietro }' onclick="javascript:historyVaiIndietroDi(1);">
					</c:if>
				</td>
			</tr>
			</tr>
			
		</table>
  </gene:redefineInsert>
  <gene:javaScript>
  	document.getElementById("numeroProdotti").value = ${currentRow}+1;
  
  	setContextPath("${pageContext.request.contextPath}");
  
	function annulla(){
		listaAnnullaModifica();
	}

	function modificaLista(){
		document.forms[0].updateLista.value = "1";
		listaVaiAPagina(document.forms[0].pgCorrente.value);
	}
	function listaConferma() {
		document.getElementById("AGGIORNA_DA_LISTA").value = "2";
		document.forms[0].metodo.value = "updateLista";
		document.forms[0].key.value = document.forms[0].keyParent.value;
		document.forms[0].pgVaiA.value = document.forms[0].pgCorrente.value;
		document.forms[0].updateLista.value = "0";
		bloccaRichiesteServer();
		document.forms[0].submit();
	}

	// Aggiornamento di IMPOFF.DITG
	/*
	function AggiornaImporto(importo,tipoGara,isPrequalifica){
		var titolo;
		var faseGara = ${faseGara};
		var ribcal = ${ribcal};
		<c:choose>
			<c:when test='${isPrequalifica eq "true"}'>
				titolo = "Confermi l'aggiornamento della verifica della conformità dei prodotti sui singoli lotti?";
			</c:when>
		<c:otherwise>
			<c:choose>
			<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
				if (faseGara < 7 && ribcal == 2) titolo = "Confermi l'aggiornamento dell'importo offerto della ditta e del corrispondente ribasso percentuale?";
				else if(faseGara >= 7 && ribcal == 2)
					titolo = "Confermi l'aggiornamento dell'importo offerto della ditta?\nIl corrispondente ribasso percentuale non viene aggiornato\nperchè la gara risulta già aggiudicata o in fase di aggiudicazione";
				else
					titolo = "Confermi l'aggiornamento dell'importo offerto della ditta?";
			</c:when>
			<c:otherwise>
			if (faseGara < 7 && ribcal == 2) titolo = "Confermi l'aggiornamento dell'importo offerto della ditta e del corrispondente ribasso percentuale sui singoli lotti?";
			else if(faseGara >= 7 && ribcal == 2)
				titolo = "Confermi l'aggiornamento dell'importo offerto della ditta sui singoli lotti?\nIl corrispondente ribasso percentuale non viene aggiornato\nperchè la gara risulta già aggiudicata o in fase di aggiudicazione";
			else
				titolo = "Confermi l'aggiornamento dell'importo offerto della ditta sui singoli lotti?";
			</c:otherwise>
			</c:choose>
		</c:otherwise>
		</c:choose>
		
		if(confirm(titolo)){
			document.getElementById("AGGIORNA_DA_LISTA").value = "1";
			document.forms[0].metodo.value = "updateLista";
			document.forms[0].key.value = document.forms[0].keyParent.value;
			document.forms[0].pgVaiA.value = document.forms[0].pgCorrente.value;
			document.forms[0].updateLista.value = "0";
			bloccaRichiesteServer();
			document.forms[0].submit();
		}else{
		  historyVaiIndietroDi(1);
		}		
	}
	*/
	
	function AggiornaImporto(importo){
		var href = "href=gare/commons/popupAggiornaImportoOfferto.jsp";
		var totaleOfferto = document.forms[0].TOTALE_OFFERTO.value;
		var numeroGara = document.forms[0].NUMERO_GARA.value;
		var codiceGara = document.forms[0].CODICE_GARA.value;
		var codiceDitta = document.forms[0].CODICE_DITTA.value;
		var dittaAgg = document.forms[0].DITTA_AGG.value;				
		var sicinc = document.forms[0].SICINC.value;				
		var isGaraLottiConOffertaUnica = document.forms[0].GARAOFFERTAUNICA.value;
		var faseGara = document.forms[0].FASEGARA.value;					
		var ribcal = document.forms[0].RIBCAL.value;
		var stepWizard = document.forms[0].stepWizard.value;				
		var isPrequalifica = document.forms[0].PREQUALIFICA.value;
		href +="?totaleOfferto=" + totaleOfferto;
		href +="&numeroGara=" + numeroGara;
		href +="&codiceGara=" + codiceGara;
		href +="&codiceDitta=" + codiceDitta;
		href +="&dittaAgg=" + dittaAgg;
		href +="&sicinc=" + sicinc;
		href +="&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica;
		href +="&faseGara=" + faseGara;
		href +="&ribcal=" + ribcal;
		href +="&stepWizard=" + stepWizard;
		href +="&isPrequalifica=" + isPrequalifica;
		href +="&bloccoAmmgar=${bloccoAmmgar }" ;
		href +="&onsogrib=${onsogrib }" ;
		href +="&riboepvVis=${riboepvVis}";
		
		openPopUpCustom(href, "AggiornaImportoOfferto", 600, 350, "no", "no");
	}
	
  //Apertura della popup per la cancellazione del dettaglio prezzi
	function cancellaDettaglioPrezzi(){
		var numeroGara="${numeroGara}";
		var codiceDitta="${codiceDitta}";
		var codiceGara="${codiceGara}";
		var garaLottiConOffertaUnica = "${isGaraLottiConOffertaUnica}";
		
		//var href = "href=gare/commons/conferma-canc-dettaglioPrezzi.jsp?gara=" + numeroGara + "&ditta=" + codiceDitta ;
		var href = "href=gare/commons/conferma-canc-dettaglioPrezzi.jsp?gara=" + numeroGara + "&ditta=" + codiceDitta + "&codiceGara=" + codiceGara + "&garaOffertaUnica=" + garaLottiConOffertaUnica;
		openPopUpCustom(href, "CancellaDettaglioPrezzi", 600, 250, "no", "no");
	}
	
	function apriPopupImportaDaExcel(){
		var ngara="${numeroGara}";
		var codiceDitta="${codiceDitta}";

	  var act = "${pageContext.request.contextPath}/pg/InitImportLavorazioniFornitureDitta.do";
   	  var par = "ngara=" + ngara + "&codiceDitta=" + codiceDitta  + "&isPrequalifica=" + ${isPrequalifica} ;
   	  var codiceGara="${codiceGara }";
  	  par += "&codiceGara=" + codiceGara;
  <c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
  	par += "&garaLottiConOffertaUnica=1";
  </c:if>
  
		openPopUpActionCustom(act, par, 'importOffertaPrezzi', 700, 500, "yes", "yes");
	}
	
	//Apertura della popup per la verifica e correzione prezzi
	function verificaCorrezionePrezzi(){
		var numeroGara="${numeroGara }";
		var codiceDitta="${codiceDitta }";
		var codiceGara="${codiceGara }";
				
		var href = "href=gare/commons/popupVerificaCorrezionePrezzi.jsp?codgar=" + codiceGara + "&gara=" + numeroGara + "&ditta=" + codiceDitta ;
		openPopUpCustom(href, "VerificaCorrezionePrezzi", 700, 350, "no", "no");
	}	
	
	function tornaARiepilogo() {
		historyVaiIndietroDi(1);
	}
	
	
	
	//Si imposta la scheda che deve essere aperta se TIPFORN.TORN = 1 oppure 2
	var tipoforniture="${tipoForniture}"
	if (tipoforniture == "1" || tipoforniture == "2")
		document.forms[0].pathScheda.value="/WEB-INF/pages/gare/v_gcap_dpre/v_gcap_dpre_farmaci_dispositivi-scheda.jsp";
	if (tipoforniture == "98"){
		document.forms[0].pathScheda.value="/WEB-INF/pages/gare/v_gcap_dpre/v_gcap_dpre_prodotti-scheda.jsp";
	}
		
	
	function acquisisciDaAUR(){
		
		var numeroGara="${numeroGara }";
		var codiceDitta="${codiceDitta }";
		var carrello = "${carrello }";
			
		var href = "href=gare/commons/popupInserOffertaFornitoreAUR.jsp";
		href += "&numeroGara=" + numeroGara ;
		href += "&codiceDitta=" + codiceDitta;
		href += "&carrello=" + carrello;
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
	  		href += "&garaLottiConOffertaUnica=1";
	  	</c:if>
		href += "&tipoForniture=" + tipoforniture;
		openPopUpCustom(href, "inserOffertaFornitoreAUR", 550, 350, "yes", "yes");
	}
	
	
	//Associazione del plugin jquery-tooltip allo span con
	//id = INFO_TOOLTIP + (contatore)
	var numeroRighe = ${currentRow};
	for(var t=0; t <= numeroRighe; t++){
		$("#INFO_TOOLTIP" + t).tooltip({ 
		    track: true, 
		    delay: 250, 
		    showURL: false, 
		    opacity: 1, 
		    fixPNG: true, 
		    showBody: " - ", 
		    extraClass: "pretty fancy", 
		    top: 15, 
		    left: 5 
		});
	}
	 
	 
	
	</gene:javaScript>
</gene:template>