<%
/*
 * Created on: 10-09-2010
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.elenco.note")}' scope="request"/>
						
<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>
<c:set var="numeroGara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="codgar" value="$${numeroGara}"/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
<c:if test="${integrazioneWSDM eq '1' }">
	<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext,idconfi)}'/>
</c:if>
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction" parametro="${key}" />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<% // Fase Ricezione domande di iscrizione (pagina a lista) %>
<c:set var="step1Wizard" value="-50" scope="request" />
<% // Fase Apertura domande di iscrizione (pagina a lista) %>
<c:set var="step2Wizard" value="-40" scope="request" />
<% // Fase Elenco concorrenti abilitati (pagina a lista) %>
<c:set var="step3Wizard" value="-30" scope="request" />

<% // Il campo GARE.STEPGAR viene aggiornato solo se assume un valore < step6wizard, %>
<% // poiche' tale valore implica che le fasi di gara siano state attivate. %>
<% // Il campo GARE.FASGAR viene aggiornato seguendo la stessa regola e, per %>
<% // compatibilita' con PWB, assume valore pari a floor(GARE.STEPGAR/10), cioè il %>
<% // piu' grande intero minore o uguale a GARE.STEPGAR/10 %>


<% // Set di una variabile temporanea per costruire il valore delle stringhe     %>
<% // strProtVisualizzaFasiRicezione e strProtModificaFasiRicezione in funzione  %>
<% // dello step del wizard attivo. Questa variabile e' stata introdotta per non %>
<% // modificare i record presenti nella tabella W_OGGETTI (e tabelle collegate  %>
<% // W_AZIONI e W_PROAZI e di tutti di i profili esistenti) in seguito alla     %>
<% // introduzione di nuovi step nel wizard fasi di gara %>
<c:set var="varTmp" value="${paginaAttivaWizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:set var="strProtVisualizzaFasiRicezione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.VIS-FASE${varTmp}" scope="request"/>
<c:set var="strProtModificaFasiRicezione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.MOD-FASE${varTmp}" scope="request"/>

<c:set var="strProtVisualizzaFasiIscrizione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.VIS-FASE${varTmp}" scope="request"/>
<c:set var="strProtModificaFasiIscrizione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.MOD-FASE${varTmp}" scope="request"/>

<c:set var="tipoalgo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPOALGOFunction", pageContext, numeroGara)}'/>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="abilitaRegistrazioneSuPortale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "archivioImprese.registraSuPortale")}'/>

<c:set var="isPopolatatW_PUSER" value='${gene:callFunction("it.eldasoft.gene.tags.functions.isPopolatatW_PUSERFunction", pageContext)}' /> 

<c:if test='${paginaAttivaWizard eq step3Wizard }'>
	${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiGarealboFunction", pageContext, numeroGara)}
</c:if>

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
	<c:when test='${gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione)}' >
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

<c:if test="${!empty filtroDitte and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione)}">
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

<c:choose>
	<c:when test="${genereGara eq 20}">
		<c:set var="genereCom" value="6"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereCom" value="5"/>
	</c:otherwise>
</c:choose>
<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,genereCom)}' />

<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>

<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA#' scope="request"/>
<c:set var="whereGARE" value='GARE.NGARA = #GARE.NGARA#' scope="request"/>

<c:set var="aggnumord" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetAGGNUMORDFunction", pageContext, numeroGara)}' />
<c:choose>
	<c:when test="${aggnumord eq 1 }">
		<c:set var="testoFunzRicalcoloNumOrdine" value='Attiva operatori abilitati' />
	</c:when>
	<c:otherwise>
		<c:set var="testoFunzRicalcoloNumOrdine" value="Assegna numero d'ordine" />
	</c:otherwise>
</c:choose>	
	<tr>
		<td ${stileDati} >
			<gene:formLista entita="DITG" where="${whereDITG} ${filtroFaseRicezione}${filtroDitte }" tableclass="datilista" sortColumn="5;6" pagesize="${requestScope.risultatiPerPagina}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione" gestisciProtezioni="true" >
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
							 <c:if test='${paginaAttivaWizard eq step1Wizard }'>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione)}'>
									<c:if test='${gene:checkProtFunz(pageContext, "INS","INS")}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:aggiungiDitta();" title='Aggiungi ditta da anagrafica' tabindex="1502">
													Aggiungi ditta da anagrafica
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") }'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1503">
													${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
											</td>
										</tr>
									</c:if>
								</c:if>
							</c:if>
							
							<c:if test='${paginaAttivaWizard eq step3Wizard }'>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione) and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.AssegnaNumOrdine")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:assegnaNumOrdine();" title='${testoFunzRicalcoloNumOrdine }' tabindex="1502">
												${testoFunzRicalcoloNumOrdine }
											</a>
										</td>
									</tr>
								</c:if>
							</c:if>
							
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione) and gene:checkProt(pageContext, strProtModificaFasiIscrizione) and datiRiga.rowCount > 0 }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1503">
											${gene:resource("label.tags.template.dettaglio.schedaModifica")}
										</a>
									</td>
								</tr>
							</c:if>
							
							<c:if test='${paginaAttivaWizard eq step2Wizard and autorizzatoModifiche ne 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.AbilitaOperatori")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:abilitaOperatori();" title='Abilita operatori in attesa verifica' tabindex="1504">
											Abilita operatori in attesa verifica
										</a>
									</td>
								</tr>
							</c:if>
							
							<c:if test='${paginaAttivaWizard eq step1Wizard }'>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione)}'>
									<c:if test='${comunicazioniPortale eq "SI" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisiciIscrizioniElencoPortaleMassivo")}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:acquisisciDaPortale();" title='Acquisisci da portale' tabindex="1505">
													Acquisisci da portale
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and genereGara ne 20 and  datiRiga.rowCount > 0 and abilitaRegistrazioneSuPortale eq "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.RegistraImpresePortale")}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:registraImprese();" title='Registra imprese sul portale' tabindex="1506">
													Registra imprese sul portale
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
							</c:if>
							
							<c:if test="${ autorizzatoModifiche ne 2 and not empty urlWsArt80 and urlWsArt80 ne '' and updateLista ne 1 and paginaAttivaWizard eq step2Wizard and gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.VerificaOpertoriArt80')}">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:verificaOperatoriArt80();" title='Richiedi verifica operatori art.80' tabindex="1505">
											Richiedi verifica operatori art.80
										</a>
									</td>
								</tr>
							</c:if>
							
							<c:if test='${autorizzatoModifiche ne 2 and updateLista ne 1 and paginaAttivaWizard eq step2Wizard and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.SorteggioVerificaDoc")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:sorteggioVerificaDoc();" title='Sorteggio per verifica documenti' tabindex="1510">
											Sorteggio per verifica documenti
										</a>
									</td>
								</tr>
							</c:if>
							
							<c:if test='${paginaAttivaWizard eq step3Wizard }'>
								<c:if test='${gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione) and gene:checkProtObj(pageContext, "MASC.VIS","GARE.DITG-listaImportoAggiudicatoOperatori") and (ctrlaggiu eq "1" or ctrlaggiu eq "2")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriProspettoImportoAggiudicato();" title='Prospetto importo aggiudicato nel periodo' tabindex="1507">
												Prospetto importo aggiudicato nel periodo
											</a>
										</td>
									</tr>
								</c:if>
							</c:if>
							<c:if test="${gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione)}">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1508">
											Imposta filtro
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${paginaAttivaWizard > step1Wizard }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1509">
											< Fase precedente
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${paginaAttivaWizard < step3Wizard}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:avanti();" title='Fase seguente' tabindex="1510">
											Fase seguente >
										</a>
									</td>
								</tr>
							</c:if>
							
						</c:otherwise>
					</c:choose>
				</gene:redefineInsert>
		
		
		<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,chiaveRigaJava)}'/>
				
		<c:choose>
			<c:when test='${(empty updateLista or updateLista ne 1)  }' >
				<c:if test="${paginaAttivaWizard eq step1Wizard and autorizzatoModifiche ne 2}">
					<gene:set name="titoloMenu">
						<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
					</gene:set>
				</c:if>
				<c:set var="titoloOpzioni" value="Opzioni<center>${titoloMenu}</center>"/>
				<c:set var="visOpzioni" value='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InvioComunicazione") || (paginaAttivaWizard eq step1Wizard and (gene:checkProtFunz(pageContext, "DEL","DEL") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")))
						|| gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio") and autorizzatoModifiche ne 2 and paginaAttivaWizard eq step2Wizard}'/>
				<c:set var="numAvvalimentiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroAvvalimentiDittaFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA5"),gene:getValCampo(chiaveRigaJava, "DITTAO"))}' />
				<c:choose>
					<c:when test="${numAvvalimentiDitta ne '0'}">
						<c:set var="numAvvalimentiDitta" value="(${numAvvalimentiDitta})"/>
					</c:when>
					<c:otherwise>
						<c:set var="numAvvalimentiDitta" value=""/>
					</c:otherwise>
				</c:choose>
				<gene:campoLista title='${gene:if(paginaAttivaWizard eq step1Wizard and autorizzatoModifiche ne 2,titoloOpzioni,"") }' width='${gene:if(paginaAttivaWizard eq step1Wizard and autorizzatoModifiche ne 2,"50","25") }' visibile="${visOpzioni}">
				<c:if test="${visOpzioni}">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
						<c:if test='${paginaAttivaWizard eq step1Wizard and (gene:checkProtFunz(pageContext, "DEL","DEL") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")) and autorizzatoModifiche ne 2}'>
							<gene:PopUpItem title="Elimina ditta" href="listaElimina()" />
						</c:if>
						<c:if test='${(paginaAttivaWizard eq step1Wizard or paginaAttivaWizard eq step2Wizard) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate") and (tipoImpresa == "2" or tipoImpresa == "11") and autorizzatoModifiche ne 2}'>
							<gene:PopUpItem title="Dettaglio consorziate esecutrici" href="listaDitteConsorziate('${chiaveRigaJava}')" />
						</c:if>
						<c:if test='${(paginaAttivaWizard eq step1Wizard or paginaAttivaWizard eq step2Wizard) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}'>
							<gene:PopUpItem title="Avvalimento ${numAvvalimentiDitta}" href="dettaglioAvvalimento('${chiaveRigaJava}')" />
						</c:if>
						<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.InvioComunicazione') and autorizzatoModifiche ne 2}">
							<c:set var="inviaComunicazioneAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneInvioComunicazioneFunction",  pageContext,chiaveRigaJava, abilitatoInvioMailDocumentale)}' />
							<gene:PopUpItem title="Invia comunicazione" href="inviaComunicazione('${chiaveRigaJava}','${inviaComunicazioneAbilitato }','${idconfi}')" />
						</c:if>
						<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio') and autorizzatoModifiche ne 2 and paginaAttivaWizard ne step3Wizard and datiRiga.V_DITGAMMIS_AMMGAR eq 10}">
							<c:set var="modelloSoccoroIstruttorioConfigurato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"56")}' />
							<gene:PopUpItem title="Richiedi soccorso istruttorio" href="richiestaSoccorsoIstruttorio('${chiaveRigaJava}','${modelloSoccoroIstruttorioConfigurato }','${idconfi}','${numeroModello }','')" />
						</c:if>
						
					</gene:PopUp>
					<c:if test="${paginaAttivaWizard eq step1Wizard and autorizzatoModifiche ne 2}">
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</c:if>
				</gene:campoLista>
			</c:when>
			<c:otherwise>
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
			</c:otherwise>
		</c:choose>

				<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NGARA5"  visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista title="N." campo="NPROGG" headerClass="sortable" width="32" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroProgressivoDITG" />
				<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
				<gene:campoLista campo="NOMIMO" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' />
				<gene:campoLista title="NumProgDittaFittizio" campo="NUM_PROGG" entita="DITG" visibile="false" campoFittizio="true" definizione="N7" edit="${updateLista eq 1}" value="${datiRiga.DITG_NPROGG}" computed="true" />
				<gene:campoLista title="NumOrdPlicoFittizio" campo="NUM_ORDPL" entita="DITG" visibile="false" campoFittizio="true" definizione="N7" edit="${updateLista eq 1}" value="${datiRiga.DITG_NUMORDPL}" computed="true" />
				<gene:campoLista campo="DRICIND" title="Data dom.iscr." headerClass="sortable" visibile="${paginaAttivaWizard eq step1Wizard}" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NPRDOM" title="N.prot." width="40" headerClass="sortable" visibile="${paginaAttivaWizard eq step1Wizard}" edit="${updateLista eq 1}" />
				
				<gene:campoLista campo="DPRDOM" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
				<c:if test='${paginaAttivaWizard eq step1Wizard and updateLista eq 1}' >
					<gene:campoLista  campo="DPRDOM_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="${datiRiga.DITG_DPRDOM}" edit="true" visibile="false"/>
				</c:if>
				
				<!-- il campo AMMGAR viene gestito nel gestore di submit -- gene-:-campoLista campo="AMMGAR" width="80" headerClass="sortable" visibile="${paginaAttivaWizard eq -5 or paginaAttivaWizard eq -4 or paginaAttivaWizard eq 1}" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" width="80" headerClass="sortable" ordinabile ="true" visibile="${paginaAttivaWizard eq step1Wizard or paginaAttivaWizard eq step2Wizard}" edit="${updateLista eq 1}" />
				<gene:campoLista campo="ABILITAZ" headerClass="sortable" visibile="${paginaAttivaWizard eq step2Wizard}" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DABILITAZ" headerClass="sortable" visibile="${paginaAttivaWizard eq step2Wizard}" edit="${updateLista eq 1}" />

				<gene:campoLista title="N.assegnato" campo="NUMORDPL" headerClass="sortable" width="32" edit="${updateLista eq 1}" visibile="${paginaAttivaWizard eq step3Wizard and (empty aggnumord or aggnumord ne 1)}"/>
				<gene:campoLista campo="DATTIVAZ" edit="${updateLista eq 1}" visibile="${paginaAttivaWizard eq step3Wizard}" />
				<gene:campoLista title="Penalità?" campo="ASS_PEN" entita="DITG" width="32" visibile="${paginaAttivaWizard eq step3Wizard and (tipoalgo eq 1 or tipoalgo eq 3 or tipoalgo eq 4 or tipoalgo eq 5 or tipoalgo eq 11 or tipoalgo eq 12 or tipoalgo eq 14 or tipoalgo eq 15)}" campoFittizio="true" definizione="T2;;;SN" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoAssegnataPenalita"/>
				
				<c:if test="${paginaAttivaWizard eq step3Wizard && (tipoalgo eq 2 || tipoalgo eq 6 || tipoalgo eq 7 || tipoalgo eq 8 || tipoalgo eq 9 || tipoalgo eq 10 || tipoalgo eq 13)}">
					<gene:campoLista campo="ALTPEN" entita="ISCRIZCAT" where="ISCRIZCAT.CODGAR=DITG.CODGAR5 and ISCRIZCAT.NGARA=DITG.NGARA5 and ISCRIZCAT.CODIMP=DITG.DITTAO and ISCRIZCAT.CODCAT='0' and ISCRIZCAT.TIPCAT=1" edit="${updateLista eq 1}" ordinabile="false"/>
					<gene:campoLista campo="NOTPEN" entita="ISCRIZCAT" where="ISCRIZCAT.CODGAR=DITG.CODGAR5 and ISCRIZCAT.NGARA=DITG.NGARA5 and ISCRIZCAT.CODIMP=DITG.DITTAO and ISCRIZCAT.CODCAT='0' and ISCRIZCAT.TIPCAT=1" visibile="false" edit="${updateLista eq 1}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTestoCodifcaDoppiApici"/>
					<gene:campoLista campo="ULTNOT" entita="ISCRIZCAT" where="ISCRIZCAT.CODGAR=DITG.CODGAR5 and ISCRIZCAT.NGARA=DITG.NGARA5 and ISCRIZCAT.CODIMP=DITG.DITTAO and ISCRIZCAT.CODCAT='0' and ISCRIZCAT.TIPCAT=1" visibile="false" edit="${updateLista eq 1}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTestoCodifcaDoppiApici"/>
					
				</c:if>
				<gene:campoLista title="N.inviti virtuali" campo="NUM_INVITI_VIRT" entita="DITG" width="32" visibile="${paginaAttivaWizard eq step3Wizard and (tipoalgo eq 1 or tipoalgo eq 5 or tipoalgo eq 7)}" campoFittizio="true" definizione="N7" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroInvitiVirtuali"/>
				
				<gene:campoLista title="N.inviti ricevuti" campo="NUM_INVITI" entita="DITG" width="32" visibile="${paginaAttivaWizard eq step3Wizard}" campoFittizio="true" definizione="N7" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroInviti"/>
				<gene:campoLista title="N.off. inviate" campo="NUM_OFFERTE" entita="DITG" width="32" visibile="${paginaAttivaWizard eq step3Wizard}" campoFittizio="true" definizione="N7" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroOfferte"/>
				<gene:campoLista title="N.gare aggiud." campo="NUM_SVOLTE" entita="DITG" width="32" visibile="${paginaAttivaWizard eq step3Wizard}" campoFittizio="true" definizione="N7" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroGareAgg" />
				<gene:campoLista campo="ACQUISIZIONE" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DSCAD" visibile="${paginaAttivaWizard eq step2Wizard && tipologiaElenco != 3 && giorniValidita >0}" edit="${updateLista eq 1}" title="Data ultimo rinnovo"/>
				
				<c:if test="${(paginaAttivaWizard eq step1Wizard) and updateLista ne 1}">
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
								<c:choose>
								<c:when test="${requestScope.registrazioneImpPortaleNonCompleta == 'SI'}">
									<img width="16" height="16" title="Impresa con registrazione non completa su portale" alt="Impresa con registrazione non completa su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita_noncompleta.png"/>
								</c:when>
								<c:otherwise>
									<img width="16" height="16" title="Impresa registrata su portale" alt="Impresa registrata su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>				
								</c:otherwise>
								</c:choose>
							</c:if>
						</gene:campoLista>
					</c:if>
				</c:if>
				
				<c:if test='${(paginaAttivaWizard >= step2Wizard  || (paginaAttivaWizard == step1Wizard and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.ElencoCategorie"))) and updateLista ne 1 }' >
				
					<c:set var="isAppVrUrlWS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "appVr.ws.url")}'/>
					<c:if test='${!empty isAppVrUrlWS}' >
						<c:set var="temp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetIVRFunction", pageContext, datiRiga.DITG_DITTAO)}' />
						<c:set var="dittao" value='${datiRiga.DITG_DITTAO}' />
						<c:set var="label" value='ATTIVA' />
						<c:set var="immagine" value="vendor_rating_attiva.png"/>
						<c:if test='${dataSospensione ne ""}'>
							<c:set var="label" value='SOSPESA ${dataSospensione}' />
							<c:set var="immagine" value="vendor_rating_sospesa.png"/>
						</c:if>
						
						<gene:campoLista title="Ivr" campo="IVR" value="${ivr}" campoFittizio="true" href="javascript:chiaveRiga='${chiaveRigaJava}';apriElencoIvrDaCodDitta('${dittao}');" visibile = "true" definizione="F24.5;0;;"/>
						
						<gene:campoLista title="&nbsp;" width="20" >
							<img width="16" height="16" title="${label}" alt="${label}" src="${pageContext.request.contextPath}/img/${immagine}"/>
						</gene:campoLista>
					</c:if>
					
					
					<c:set var="label" value="Elenco categorie iscrizione"/>
					<c:set var="immagine" value="categorie.png"/>
					<c:set var="aggCategorie" value="0"/>
					<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") && paginaAttivaWizard eq step2Wizard}'>
						<c:set var="numRichiesteAggCategorie" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumAggiornamentiCategorieSospesoFunction", pageContext, datiRiga.DITG_DITTAO, datiRiga.DITG_NGARA5 )}' />
						<c:if test='${numRichiesteAggCategorie ne "0"}'>
							<c:set var="label" value="${label } (presente richiesta di modifiche da portale da confermare)"/>
							<c:set var="immagine" value="categorieDaAggiornare.png"/>
							<c:set var="aggCategorie" value="1"/>
						</c:if>
					</c:if>
					<gene:campoLista title="&nbsp;" width="20" >
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriElencoCategorie('${chiaveRigaJava}','${aggCategorie}');" title="${label}" >
							<img width="16" height="16" title="${label}" alt="${label}" src="${pageContext.request.contextPath}/img/${immagine}"/>
						</a>
					</gene:campoLista>
				</c:if>
				
				<c:if test='${genereGara eq 20 and paginaAttivaWizard == step3Wizard and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.FASIISCRIZIONE.ElencoProdottiCaricati") and updateLista ne 1 }' >
					<c:set var="numProdotti" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoProdottiDaVerificareFunction", pageContext, datiRiga.DITG_CODGAR5, datiRiga.DITG_NGARA5, datiRiga.DITG_DITTAO)}' />
					<c:choose>
						<c:when test="${numProdotti > 0 }">
							<c:set var="immagine" value="prod-oe-verif.png"/>
						</c:when>
						<c:otherwise>
							<c:set var="immagine" value="prod-oe.png"/>
						</c:otherwise>
					</c:choose>
					<gene:campoLista title="&nbsp;" width="20" >
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriElencoProdotti('${chiaveRigaJava}');" title="Elenco prodotti caricati" >
							<img width="16" height="16" title="Elenco prodotti caricati" alt="Elenco prodotti caricati" src="${pageContext.request.contextPath}/img/${immagine}"/>
						</a>
					</gene:campoLista>
				</c:if>
				
				<c:if test="${(paginaAttivaWizard > step1Wizard ) and (updateLista ne 1) and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.VerificaDocumenti')}" >
					<gene:campoLista title="&nbsp;" width="20" >
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocRichiesti('${chiaveRigaJava}');" title="Verifica documenti richiesti" >
							<img width="16" height="16" title="Verifica documenti richiesti" alt="Verifica documenti richiesti" src="${pageContext.request.contextPath}/img/documentazione.png"/>
						</a>
					</gene:campoLista>
				</c:if>
				
				<c:if test="${paginaAttivaWizard <= step3Wizard}" >
					<c:if test="${iconaNoteAttiva eq 1}">
						<gene:campoLista campo="ALTNOT" visibile="false" />
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
				</c:if>
				
				<c:if test="${paginaAttivaWizard eq step1Wizard and updateLista ne 1 and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.NoteAvvisiImpresa')}" >
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
				
				<!-- il campo DITG.FASGAR viene gestito nel gestore di submit gene-:-campoLista campo="FASGAR" visibile="false" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
				<!-- il campo DITG.MOTIES viene gestito nel gestore di submit gene-:-campoLista campo="MOTIES" visibile="false" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
				<!-- il campo DITG.ANNOFF viene gestito nel gestore di submit gene-:-campoLista campo="ANNOFF" visibile="false" edit="${updateLista eq 1}" /-->
				<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />

				<gene:campoLista campo="ORADOM" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DSOSPE" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DREVOCA" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="STRIN" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="PLIDOM" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NOTPDOM" visibile="false" edit="${updateLista eq 1}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTestoCodifcaDoppiApici"/>
				<gene:campoLista campo="DRICRIN" visibile="false" edit="${updateLista eq 1}" />
								
				<% // Questo campo non viene MAI modificato nella pagina e la sua valorizzazione viene gestito solo lato server. %>
				<% // E' presente nella pagina come campo hidden per capire se il valore del campo V_DITGAMMIS.AMMGAR e' ereditato %>
				<% // da una fase precedente o e' effettivamente un valore esistente per lo step del wizard in visualizzazione %>
				<gene:campoLista campo="AMMGAR" entita="DITGAMMIS" where="DITGAMMIS.CODGAR=DITG.CODGAR5 and DITGAMMIS.NGARA=DITG.NGARA5 and DITGAMMIS.DITTAO=DITG.DITTAO and DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}"/>
			
			<c:if test='${updateLista eq 0}' >
				<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
				<gene:campoLista campo="ABILITAZ_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_ABILITAZ}" />
				<gene:campoLista campo="DSCAD_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="T10" value="${datiRiga.DITG_DSCAD}" />
			</c:if>
				
				
								
				<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
				<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
				<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
				<input type="hidden" name="DTEOFF" id="DTEOFF" value="${dataTerminePresentazioneOfferta}" />
				<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
				
				<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
				<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
				<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
				<c:choose>
					<c:when test="${genereGara eq 20 } ">
						<c:set var="isElenco" value="2"/>
						<c:set var="isCatalogo" value="1"/>
					</c:when>
					<c:otherwise>
						<c:set var="isElenco" value="1"/>
						<c:set var="isCatalogo" value="2"/>
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="isGaraElenco" name="isGaraElenco" value="${isElenco }" />
				<input type="hidden" id="isGaraCatalogo" name="isGaraCatalogo" value="${isCatalogo }" />
				<input type="hidden" id="isInvioMailOperatoriAbilitati" name="isInvioMailOperatoriAbilitati" value="false" />
				<input type="hidden" id="listaOperatoriAbilitati" name="listaOperatoriAbilitati" value="" />
				<input type="hidden" id="numeroOperatoriAbilitati" name="numeroOperatoriAbilitati" value="" />
				<input type="hidden" id="flagMailPec" name="flagMailPec" value="" />
				<input type="hidden" id="oggettoMail" name="oggettoMail" value="" />
				<input type="hidden" id="testoMail" name="testoMail" value="" />
				<input type="hidden" id="mittenteMail" name="mittenteMail" value="" />
				<input type="hidden" id="integrazioneWSDM" name="integrazioneWSDM" value="${integrazioneWSDM }" />
				<c:if test="${integrazioneWSDM =='1'}">
					<input id="servizio" type="hidden"  name="servizio" value="FASCICOLOPROTOCOLLO" />
					<input id="syscon" type="hidden"  name="syscon" value="${profiloUtente.id}" /> 
					<input id="tiposistemaremoto"  name="tiposistemaremoto" type="hidden" value="" />
					<input id="tabellatiInDB" type="hidden" value="" />
					<input id="idprg" type="hidden"  name="idprg" value="PG" />
					<input id="key1" type="hidden" name="key1" value="" />
					<input id="key2" type="hidden" name="key2" value="" /> 
					<input id="key3" type="hidden" name="key3" value="" /> 
					<input id="key4" type="hidden" name="key4" value="" /> 
					<input type="hidden"  name="chiaveOriginale" id="chiaveOriginale" value="" />
					<input type="hidden"  name="classificadocumento" id="classificadocumento" value="" />
					<input type="hidden" name="idtitolazione" id="idtitolazione" value="" />
					<input type="hidden" name="tipodocumento" id="tipodocumento" value="" />
					<input type="hidden" name="oggettodocumento" id="oggettodocumento" value="" />
					<input type="hidden" name="mittenteinterno" id="mittenteinterno" value="" />
					<input type="hidden" name="indirizzomittente" id="indirizzomittente" value="" />
					<input type="hidden" name="mezzoinvio" id="mezzoinvio" value="" />
					<input type="hidden" name="codiceregistrodocumento" id="codiceregistrodocumento" value="" />
					<input type="hidden" name="inout" id="inout" value="" />
					<input type="hidden" name="idindice" id="idindice" value="" />					
					<input type="hidden" name="idunitaoperativamittente" id="idunitaoperativamittente" value="" />
					<input type="hidden" name="inserimentoinfascicolo" id="inserimentoinfascicolo" value="" />
					<input type="hidden" name="codicefascicolo" id="codicefascicolo" value="" />
					<input type="hidden" name="oggettofascicolo" id="oggettofascicolo" value="" />
					<input type="hidden" name="classificafascicolo" id="classificafascicolo" value="" />
					<input type="hidden" name="descrizionefascicolo" id="descrizionefascicolo" value="" />
					<input type="hidden" name="annofascicolo" id="annofascicolo" value="" />
					<input type="hidden" name="numerofascicolo" id="numerofascicolo" value="" />
					<input type="hidden" name="username" id="username" value="" />
					<input type="hidden" name="password" id="password" value="" />
					<input type="hidden" name="ruolo" id="ruolo" value="" />
					<input type="hidden" name="nome" id="nome" value="" />
					<input type="hidden" name="cognome" id="cognome" value="" />
					<input type="hidden" name="codiceuo" id="codiceuo" value="" />
					<input type="hidden" name="idutente" id="idutente" value="" />
					<input type="hidden" name="idutenteunop" id="idutenteunop" value="" />
					<input type="hidden" name="codiceaoonuovo" id="codiceaoonuovo" value="" />
					<input type="hidden" name="codiceufficionuovo" id="codiceufficionuovo" value="" />
					<input type="hidden" name="mezzo" id="mezzo" value="" />
					<input type="hidden" name="societa" id="societa" value="" />
					<input type="hidden" name="codiceGaralotto" id="codiceGaralotto" value="" />
					<input type="hidden" name="cig" id="cig" value="" />
					<input type="hidden" name="strutturaonuovo" id="strutturaonuovo" value="" />
					<input type="hidden" name="supporto" id="supporto" value="" />
					<input type="hidden" name="tipofascicolonuovo" id="tipofascicolonuovo" value="" />
					<input type="hidden" name="classificadescrizione" id="classificadescrizione" value="" />
					<input type="hidden" name="voce" id="voce" value="" />
					<input type="hidden" name="codiceaoodes" id="codiceaoodes" value="" />
					<input type="hidden" name="codiceufficiodes" id="codiceufficiodes" value="" />
					<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
					<input type="hidden" name="RUP" id="RUP" value="" />
					<input type="hidden" name="nomeRup" id="nomeRup" value="" />
					<input type="hidden" name="acronimoRup" id="acronimoRup" value="" />
					<input type="hidden" name="sottotipo" id="sottotipo" value="" />
					<input type="hidden" name="tipofirma" id="tipofirma" value="" />
					<input type="hidden" name="idunitaoperativamittenteDesc" id="idunitaoperativamittenteDesc" value="" />
					<input type="hidden" name="uocompetenza" id="uocompetenza" value="" />
					<input type="hidden" name="uocompetenzadescrizione" id="uocompetenzadescrizione" value="" />
				</c:if>
				
				
			</gene:formLista>
		</td>
	</tr>

	
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1 }'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
					<c:if test='${paginaAttivaWizard <= step3Wizard and datiRiga.rowCount > 0}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione) and gene:checkProt(pageContext, strProtModificaFasiIscrizione)}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaLista();">&nbsp;&nbsp;&nbsp;
						</c:if>
					</c:if>
					<c:if test='${paginaAttivaWizard > step1Wizard }' >
						<INPUT type="button"  class="bottone-azione" value='< Fase precedente' title='Fase precedente' onclick="javascript:indietro();">
					</c:if>
					<c:if test='${paginaAttivaWizard < step3Wizard}'>
						<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">						
					</c:if>
				

					<c:if test='${paginaAttivaWizard eq step1Wizard}'>
						&nbsp;<br><br>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione)}'>
							<c:if test='${gene:checkProtFunz(pageContext, "INS", "INS")}'>
								<INPUT type="button"  class="bottone-azione" value='Aggiungi ditta da anagrafica' title='Aggiungi ditta da anagrafica' onclick="javascript:aggiungiDitta();">&nbsp;&nbsp;&nbsp;
							</c:if>
						</c:if>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione) and gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL")}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">
						</c:if>
					</c:if>
					
					<c:if test='${paginaAttivaWizard eq step3Wizard}'>
						&nbsp;<br><br>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiIscrizione)and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.AssegnaNumOrdine")}'>
							<INPUT type="button"  class="bottone-azione" value="${testoFunzRicalcoloNumOrdine }" title="${testoFunzRicalcoloNumOrdine }" onclick="javascript:assegnaNumOrdine();">&nbsp;&nbsp;&nbsp;
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
	setContextPath("${pageContext.request.contextPath}");
	setFaseCalcolata("${varTmp }");
	setCodiceElenco("");
	setGenereGara("${genereGara }");
	
	var genereGara="${genereGara}";
    if(genereGara =="20"){
         document.getElementById("isGaraElenco").value=2;
		 document.getElementById("isGaraCatalogo").value=1;
	}else{
         document.getElementById("isGaraElenco").value=1;
		 document.getElementById("isGaraCatalogo").value=2;
	}

<c:if test='${updateLista eq 1}'>
		function conferma(){
		// Riabilitazione prima del salvataggio delle combobox disabilitate all'utente
			for(var i=1; i <= ${currentRow}+1; i++){
				
				
				document.getElementById("V_DITGAMMIS_AMMGAR_" + i).disabled = false;				
			}
	
			<c:choose>
			<c:when test='${paginaAttivaWizard eq step2Wizard}' >
				for(var i=1; i <= ${currentRow}+1; i++){
					if(document.getElementById("DITG_DSCAD_" + i)!=null)
						document.getElementById("DITG_DSCAD_" + i).disabled = false;
				}
				<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.InvioComunicazione')}">
					verificaAbilitati('${chiaveRiga}');
				</c:if>
				
			</c:when>
			<c:otherwise>
				listaConferma();
			</c:otherwise>
			</c:choose>
		}

	
	
	var globalNumeroRiga = null;
	
		function inizializzaLista(){
			var numeroDitte = ${currentRow}+1;
			
			for(var t=1; t <= numeroDitte; t++){
			<c:if test='${paginaAttivaWizard ne step1Wizard}' >
				if(getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "98" || getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "99"){ //if(getValue("DITG_MOTIES_" + t) == "98" || getValue("DITG_MOTIES_" + t) == "99")
					document.getElementById("V_DITGAMMIS_AMMGAR_" + t).disabled = true;
				}
			</c:if>
			<c:if test='${paginaAttivaWizard ne step2Wizard}' >
				if(getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "98" || getValue("V_DITGAMMIS_MOTIVESCL_" + t) == "99"){ // if(getValue("DITG_MOTIES_" + t) == "98" || getValue("DITG_MOTIES_" + t) == "99")
					document.getElementById("V_DITGAMMIS_AMMGAR_" + t).disabled = true;
				}
			</c:if>
			<c:if test='${paginaAttivaWizard eq step2Wizard}' >
				var abilitaz = getValue("DITG_ABILITAZ_" + t);
				if(abilitaz!=1 && abilitaz!=8)
					document.getElementById("DITG_DSCAD_" + t).disabled = true;
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
		</c:when>
		<c:when test='${paginaAttivaWizard eq step2Wizard}' >
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara; //document.getElementById("DITG_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
			document.getElementById("DITG_DABILITAZ_" + i).onchange = controlloDataIscrizione;
			document.getElementById("DITG_ABILITAZ_" + i).onchange = controlliAbilitazione;
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
			
			document.getElementById("V_DITGAMMIS_MOTIVESCL_" + numeroRiga).value = "";
			//alert("objId = '" + objId + "', numeroRiga = '" + numeroRiga + "', getValue(objId) = '" + getValue(objId) + "'");
			if(document.getElementById(objId).value != "2"){
				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "");
					
			
			} else {
				//alert("Set DITG.AMMGAR = ${paginaAttivaWizard}");
				setValue("V_DITGAMMIS_FASGAR_" + numeroRiga, "${fasGarPerEsclusioneDitta}");
						
				
				
			
	
			
			
			<c:if test='${paginaAttivaWizard == step2Wizard}'>
				setValue("DITG_ABILITAZ_" + numeroRiga, "");
				setValue("DITG_DABILITAZ_" + numeroRiga, "");
			</c:if>
	
				
				
			}
			// Reset della variabile globale globalNumeroRiga
			globalNumeroRiga = null;
		}
</c:if>

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
	


	<c:if test='${paginaAttivaWizard < step3Wizard}'>
		function avanti(){
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
			href += "&moties=" + getValue('V_DITGAMMIS_MOTIVESCL_'+indiceRiga); //href += "&moties=" + getValue('V_DITG_MOTIES_'+indiceRiga);
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			href += "&isGaraLottiConOffertaUnica=true";
		</c:if>
			<c:choose>
				<c:when test="${genereGara eq 10 }">
					<c:set var="isElenco" value="true"/>
					<c:set var="isCatalogo" value="false"/>
				</c:when>
				<c:otherwise>
					<c:set var="isElenco" value="false"/>
					<c:set var="isCatalogo" value="true"/>
				</c:otherwise>
			</c:choose>
			href += "&isGaraElenco=${isElenco}";
			href += "&isGaraCatalogo=${isCatalogo}";
			href += "&tipologiaElenco=${tipologiaElenco}";
			href += "&giorniValidita=${giorniValidita}";
			href += "&tipoRinnovo=${tipoRinnovo}";
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
	
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

	<c:if test='${updateLista ne "1" and paginaAttivaWizard eq step1Wizard }' >
		function aggiungiDitta(){
			var href = "href=gare/ditg/ditg-schedaPopup-insert.jsp";
			href += "&modo=NUOVO&faseRicezione=${paginaAttivaWizard}";
			href += "&isGaraElenco=1";
			href += "&iscrizioneRT=${iscrizioneRT }";
			openPopUpCustom(href, "aggiungiDitta", 800, 600, "yes", "yes");
		}
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

	


		function apriPopupCopiaDitte() {
			var href = "href=gare/gare/gare-popup-copia-ditte.jsp?codgar="+getValue("GARE_CODGAR1")+"&lottoSorgente="+getValue("GARE_NGARA");
			openPopUpCustom(href, "copiaDitte", 600, 450, "yes", "yes");
		}

		function modelliPredispostiLocale(){
		/***********************************************************
			Copia della funzione modelliPredisposti dal file jsAzioniTags.jsp
			e addattamento al caso specifico
		 ***********************************************************/
			var entita,valori;

			try {
				entita = "GARE";
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
		
		function verificaAbilitati(chiaveRiga){
			var inclause = "";
			var numeroDitte = ${currentRow}+1;
			var numeroDitteAbilitate = 0;
			for(var t=1; t <= numeroDitte; t++){
				if(isValueChanged("DITG_ABILITAZ_"+t)){
				var isAbilitatoNow = getValue("DITG_ABILITAZ_"+t);
				 if(isAbilitatoNow == '1'){
				 	if(inclause == ""){
				 		inclause = "'"+getValue("DITG_DITTAO_"+t)+"'";
				 	}else{
				 		inclause = inclause+",'"+getValue("DITG_DITTAO_"+t)+"'";
				 	}
					numeroDitteAbilitate=numeroDitteAbilitate + 1;
				 }
				}
			}
			
			if(inclause!=""){
				href = "href=gare/imprdocg/impr-abilitati-listaPopup.jsp";
				href += "&key="+chiaveRiga;
				href += "&inclause="+inclause;
				href += "&numeroDitteAbilitate="+numeroDitteAbilitate;
				href += "&step=1";
				var integrazioneWSDM="${integrazioneWSDM}";
				var dim1=900;
				var dim2=550;
				if(integrazioneWSDM == '1'){
					dim1=900;
					dim2=700;
				}
				var idconfi = "${idconfi}";
				if(idconfi){
					href += "&idconfi=" + idconfi
				}
				openPopUpCustom(href, "verificaDocumentiRichiesti", dim1, dim2, "yes", "yes");
			}else{
				listaConferma();
			}
		}

		function verificaDocRichiesti(chiaveRiga){
			/*
			//var href = contextPath + "/ApriPagina.do?href=gare/imprdocg/imprdocg-lista.jsp";
			href = "href=gare/imprdocg/imprdocg-listaPopup.jsp";
			href += "&key="+chiaveRiga;
			href += "&stepWizard=${varTmp}";
			href += "&genereGara=${genereGara}";
			href += "&comunicazioniVis=1";
			//document.location.href = href;
			openPopUpCustom(href, "verificaDocumentiRichiesti", 1100, 550, "yes", "yes");
			*/
			
			verificaDocumentiRichiesti(chiaveRiga,"",1,"false","${autorizzatoModifiche }");
		}
		
		function apriElencoCategorie(chiaveRiga,aggCategorie){
			href = "href=gare/iscrizcat/iscrizcat-listaScheda.jsp";
			href += "&key="+chiaveRiga;
			var paginaAttivaWizard;
			<c:choose>
				<c:when test='${paginaAttivaWizard eq step1Wizard}'>
					paginaAttivaWizard=1;
				</c:when>
				<c:when test='${paginaAttivaWizard eq step2Wizard}'>
					href += "&modifica=SI";
					paginaAttivaWizard=2;
				</c:when>
				<c:otherwise>
					href += "&modifica=NO";
					paginaAttivaWizard=3;
				</c:otherwise>
			</c:choose>
			href += "&paginaAttivaWizard=" + paginaAttivaWizard;
			href += "&aggCategorie=" + aggCategorie;
			document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
		}
		
		function apriElencoIvrDaCodDitta(chiaveRiga){
			var par = "dittao=" + chiaveRiga;
			openPopUpActionCustom(contextPath + "/pg/GetListaIvr.do", par, "listaIVR", 900, 500, 1, 1);
		}
		
		function assegnaNumOrdine(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			href = "href=gare/gare/gare-popup-AssegnaNumeroOrdine.jsp";
			href += "&ngara=" + chiave;
			href += "&aggnumord=${aggnumord }";
			openPopUpCustom(href, "assegnaNumOrdine", 900, 400, "yes", "yes");
		}
		
		
		
		function acquisisciDaPortale(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			href = "href=gare/commons/popupAcquisisciDaPortale.jsp";
			href += "&ngara=" + chiave;
			href += "&registraImpr=0";
			openPopUpCustom(href, "acquisisciDaPortale", 550, 350, "yes", "yes");
		}
		
		
		function DateDaStringa(strdata){
			var giorno,mese,anno,data;
			var datePat = /^(\d{1,2})(\/|-|\.)(\d{1,2})(\/|-|\.)(\d{2,4})$/;
			var matchArray = strdata.match(datePat);
											
			giorno = (matchArray[1].length < 2) ? "0" + matchArray[1]: matchArray[1];
			mese = ((matchArray[3].length < 2) ? "0" + matchArray[3]: matchArray[3]);
			anno = ((matchArray[5].length == 2) ? "20" + matchArray[5]: matchArray[5]);
			data = new Date(anno,parseInt(mese -1),giorno);
			return data;
		}
		
		//Quando si inserisce la data di abilitazione si deve controllare che sia presente
		//la data di iscrizione. Inoltre se l'elenco è di tipo "Congelato" si deve controllare
		//che la data di abilitazione non ricada nel periodo di congelamento, calcolato come:
		//data iscrizione + tempo congelamento
		function controlloDataIscrizione(){
			
			//Vengono richiamati i javascript delle librerie generali
			//per il controllo del formato
			var formName="local" +  document.forms[0].name;
			callObjFn(formName,'onChange',this);
			
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
						
			if(this.value != null && this.value!=""){
				var dricind = getValue("DITG_DRICIND_" + numeroRiga);
				if(dricind==null || dricind==""){
					alert("Non è possibile valorizzare il campo poichè non è stata inserita la data iscrizione");
					setValue("DITG_DABILITAZ_" + numeroRiga, "");
				}else{
					var tipologiaElenco = "${tipologiaElenco }";
					var tempoCongelamento = "${tempoCongelamento }";
					if(tipologiaElenco!= null && tipologiaElenco !="" && tipologiaElenco == 2 && tempoCongelamento!=null && tempoCongelamento!=""){
						var dataIscrizione = DateDaStringa(dricind);
						var dataAbilitazione = DateDaStringa(this.value);
						var dataFineCongelamento = dataIscrizione.getTime() + tempoCongelamento * 86400000;
						if(dataAbilitazione.getTime()>=dataIscrizione.getTime() && dataAbilitazione.getTime()<= dataFineCongelamento){
							alert("La data inserita ricade nel periodo di congelamento");
							setValue("DITG_DABILITAZ_" + numeroRiga, "");
						}
					}
				}
			}else{
				//Se sbianco la data di abilitazione devo controllare se è valorizzato abilitaz,
				//se lo è non si può sbiancare la data
				var abilitaz = getValue("DITG_ABILITAZ_" + numeroRiga);
				if(abilitaz == 1) {
					alert("Non è possibile sbiancare la data di abilitazione quando l'operatore è abilitato");
					setValue("DITG_DABILITAZ_" + numeroRiga, getOriginalValue("DITG_DABILITAZ_" + numeroRiga));
				}
			}
		}
		
		//Si può inserire impostare abilitaz=1(abilitato) solo se è stata
		//inserita la data abilitazione
		function controlloDataAbilitazione(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var ret = 1;
			
			if(this.value != null && this.value=="1"){
				var dabilitaz = getValue("DITG_DABILITAZ_" + numeroRiga);
				if(dabilitaz == null || dabilitaz == ""){
					alert("Per abilitare l'operatore indicare prima la data di abilitazione");
					setValue("DITG_ABILITAZ_" + numeroRiga, getOriginalValue("DITG_ABILITAZ_" + numeroRiga));
					ret -1;
				}
			}
		}
		
		function controlliAbilitazione(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var valore = this.value;
			if(valore != null && valore=="1"){
				var dabilitaz = getValue("DITG_DABILITAZ_" + numeroRiga);
				if(dabilitaz == null || dabilitaz == ""){
					alert("Per abilitare l'operatore indicare prima la data di abilitazione");
					setValue("DITG_ABILITAZ_" + numeroRiga, getOriginalValue("DITG_ABILITAZ_" + numeroRiga));
					return;
				}
				var strin = getValue("DITG_STRIN_" + numeroRiga);
				if(strin == 2 || strin ==3){
					var descStatoRinnovo = "non conforme";
					if (strin == 2){
						descStatoRinnovo = "da verificare";
					}  
					var procedere = confirm("Il rinnovo dell'iscrizione risulta '" + descStatoRinnovo + "'.\nVuoi procedere comunque all'abilitazione dell'operatore?");
					if(!procedere){
						setValue("DITG_ABILITAZ_" + numeroRiga, getOriginalValue("DITG_ABILITAZ_" + numeroRiga));
						return;
					}
				}
			}else if(valore != null && (valore=="2" || valore =="5" || valore =="10")){
				var descStato = this.options[this.selectedIndex].text;
				if(confirm("L'impostazione dello stato a '" + descStato + "' comporta la disattivazione dell'operatore dall'elenco.\nVuoi procedere?")){
					setValue("DITG_DABILITAZ_" + numeroRiga,"");
					setValue("DITG_NUMORDPL_" + numeroRiga,"");
					setValue("DITG_DATTIVAZ_" + numeroRiga,"");
				}else{
					setValue("DITG_ABILITAZ_" + numeroRiga, getOriginalValue("DITG_ABILITAZ_" + numeroRiga));
				}
			}
			
			var data = new Date();
			var g = data.getDate();
			var m = data.getMonth() + 1;
			var a = data.getFullYear();
			if(g<10)
				g = "0" + g;
			if(m<10)
				m = "0" + m;
			var today = g + "/" + m + "/" + a;
			
			if(valore != null && valore!="7" && valore !="8" && (getOriginalValue("DITG_ABILITAZ_" + numeroRiga)=="7" || getOriginalValue("DITG_ABILITAZ_" + numeroRiga)=="8")){
				setValue("DITG_DSOSPE_" + numeroRiga,"");
			} 
			
			if(valore != null && (valore=="7" || valore =="8")){
				var dsospe = getValue("DITG_DSOSPE_" + numeroRiga);
				if(!dsospe){
					setValue("DITG_DSOSPE_" + numeroRiga,today);
				}
			}
				
			if(valore != null && valore!="5" && valore !="10" && (getOriginalValue("DITG_ABILITAZ_" + numeroRiga)=="5" || getOriginalValue("DITG_ABILITAZ_" + numeroRiga)=="10")) {
				setValue("DITG_DREVOCA_" + numeroRiga,"");
			}
			
			if(valore != null && (valore=="5" || valore =="10")){
				var drevoca = getValue("DITG_DREVOCA_" + numeroRiga);
				if(!drevoca){
					setValue("DITG_DREVOCA_" + numeroRiga,today);
				}
			}
				
			if(valore!=null && (valore==1 || valore==8))
				document.getElementById("DITG_DSCAD_" + numeroRiga).disabled = false;
			else
				document.getElementById("DITG_DSCAD_" + numeroRiga).disabled = true;
			
		}
		
		function impostaFiltro(){
			<c:choose>
				<c:when test="${genereGara eq 20}">
					<c:set var="isElenco" value="2"/>
					<c:set var="isCatalogo" value="1"/>
				</c:when>
				<c:otherwise>
					<c:set var="isElenco" value="1"/>
					<c:set var="isCatalogo" value="2"/>
				</c:otherwise>
			</c:choose>
			var isElenco = "${isElenco }";
			var isCatalogo = "${isCatalogo }";
			var tipologia = "${tipologiaElenco }";
			var tipoele = "${tipoele }";
			var comando = "href=gare/commons/popup-trova-filtroDitte.jsp&isGaraElenco=" + isElenco + "&isGaraCatalogo=" + isCatalogo;
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			comando += "&ngara=" + chiave;
			var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
			comando+="&dittePerPagina=" + risultatiPerPagina;
			comando+="&tipologia=" + tipologia;
			comando+="&tipoele=" + tipoele;
			openPopUpCustom(comando, "impostaFiltro", 850, 750, "yes", "yes");
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
			}else if(inviaComunicazioneAbilitato=="NoMandatariaRTI"){
				alert("Non è possibile procedere.\nNell'anagrafica del raggruppamento selezionato non è specificata la mandataria");
				return;
			}else{
				var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
				var genereCom = "${genereCom}";
				var chiaveVet= chiaveRiga.split(";");
				var codiceDitta=chiaveVet[1].substring(chiaveVet[1].indexOf(":")+1);
				var numeroGara = "${numeroGara }";
				if(IsW_CONFCOMPopolata == "true"){
					href = contextPath + "/pg/InitNuovaComunicazione.do?genere=" + genereCom + "&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara + "&keyParent=GARE.NGARA=T:" + numeroGara;
					href += "&ditta=" + codiceDitta;
				}else{
					
					var href = contextPath + "/Lista.do?numModello=0&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara + "&keyParent=GARE.NGARA=T:" + numeroGara;
					href += "&ditta=" + codiceDitta+ "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp";
				}
				var entitaWSDM="GARE";
				var chiaveWSDM=numeroGara;
				href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM + "&" + csrfToken;
				if(idconfi){href+="&idconfi=" + idconfi;}
				document.location.href = href;
			}
		}
		
		function registraImprese(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			href = "href=gare/commons/popupRegistraImpreseSuPortale.jsp";
			href += "&ngara=" + chiave;
			openPopUpCustom(href, "registraImprese", 650, 550, "yes", "yes");
		}	
		
		
		function apriElencoProdotti(chiaveRiga){
			href = "href=gare/meiscrizprod/meiscrizprod-listaPopup.jsp";
			href += "&key="+chiaveRiga;
			openPopUpCustom(href, "apriElencoProdotti", 900, 600, "yes", "yes");
		}
		
		function listaDitteConsorziate(chiaveRiga){
						
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gene/ragdet/ragdet-lista.jsp";
			href += "&key=" + chiaveRiga;
			document.location.href = href;
		}
		
		function dettaglioAvvalimento(chiaveRiga){
			var bloccoPagina=false;	
			var bloccoAggiudicazione = "${bloccoAggiudicazione }";		
			if(bloccoAggiudicazione=="1")
				bloccoPagina=true;			

			var href = contextPath + "/ApriPagina.do?href=gare/ditg/dettaglioAvvalimentoDitta.jsp";
			href += "&key=" + chiaveRiga + "&bloccoPagina=" + bloccoPagina;
			document.location.href = href;
		}
		
		
		function apriProspettoImportoAggiudicato(){
			var ngara = "${numeroGara }";
			var codgar = "$" + ngara;
			var genereGara = "${genereGara }";			
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/lista-ImportoAggiudicatoOperatori.jsp";
			href += "&ngara=" + ngara;
			href += "&codgar=" + codgar;
			href += "&genereGara=" + genereGara;
			href += "&risultatiPerPagina=${requestScope.risultatiPerPagina}";
			document.location.href = href;
		}
		
		function abilitaOperatori(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			href = "href=gare/gare/popupAbilitaOperatoriInAttesa.jsp";
			href += "&ngara=" + chiave;
			href += "&genere=${genereGara }";
			openPopUpCustom(href, "abilitaOperatori", 650, 350, "yes", "yes");
		}
		
		function verificaOperatoriArt80(){
			var ngara = "${numeroGara }";
			href = "href=gare/gare/popupVerificaOperatoriArt80.jsp";
			href += "&ngara=" + ngara;
			href += "&genere=${genereGara }";
			openPopUpCustom(href, "verificaOperatoriArt80", 650, 350, "yes", "yes");
		}
		
		function sorteggioVerificaDoc(){
			var ngara = "${numeroGara }";
			href = "href=gare/gare/popupSorteggioVerificaDoc.jsp";
			href += "&ngara=" + ngara;
			href += "&genere=${genereGara }";
			openPopUpCustom(href, "sorteggioVerificaDoc", 650, 350, "yes", "yes");
		}
</gene:javaScript>