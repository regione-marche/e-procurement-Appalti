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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneDitteConcorrentiFunction" parametro="${key}" />

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<jsp:include page="./fasiRicezione/defStepWizardFasiRicezione.jsp" />
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="paginaAttivaWizard" value="${step3Wizard}"/>


<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:set var="bloccaSelezioneDaElenco" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsGaraConDitteDaElencoFunction",  pageContext, gene:getValCampo(key,"NGARA"))}'/>

<c:set var="ngaraaq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNGARAAQFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request"/>

<table class="dettaglio-tab-lista">

<c:set var="visualizzareExportDitte" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1164","1","true")}' />
<c:set var="visualizzareImportDitte" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1164","2","true")}' />		

<c:set var="isPopolatatW_PUSER" value='${gene:callFunction("it.eldasoft.gene.tags.functions.isPopolatatW_PUSERFunction", pageContext)}' /> 

<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="whereDITG" value='DITG.NGARA5 = #TORN.CODGAR#' scope="request"/>
		<c:set var="whereGARE" value='GARE.NGARA = #TORN.CODGAR#' scope="request"/>
		<c:set var="codiceGara" value='${gene:getValCampo(key,"CODGAR")}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA#' scope="request"/>
		<c:set var="whereGARE" value='GARE.NGARA = #GARE.NGARA#' scope="request"/>
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
	</c:otherwise>
</c:choose>

<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.gare.note")}' scope="request"/>

<c:set var="whereDITG" value="${whereDITG } and DITG.RTOFFERTA is null" />

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

<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>
		
<c:if test="${!empty filtroDitte }">
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

	<tr>
		<td ${stileDati} >
			<gene:formLista entita="DITG" where="${whereDITG}${filtroDitte }" tableclass="datilista" sortColumn="4;5" pagesize="20" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione">
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
							
						<c:if test='${isGaraUsoAlbo and autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS","INS")}'>
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
												<a href="javascript:SelEleOpEco(0);" title='Sel. da elenco mediante rotazione' tabindex="1502">
													Sel. da elenco mediante rotazione
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								<c:if test="${(modalitaSelezioneDitteElenco eq 'AUTO' or  modalitaSelezioneDitteElenco eq 'MISTA') and  gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selDaElenco')}">
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:SelEleOpEco(1);" title='Sel. autom. da elenco mediante rotazione' tabindex="1503">
													Sel. autom. da elenco mediante rotazione
												</a>
											</td>
										</tr>
								</c:if>
							</c:if>
							
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.EsportaDitteInGara")  and garaLottoUnico and visualizzareExportDitte eq "1" }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:exportDitte();" title='Esporta in formato M-Appalti' tabindex="1500">
											Esporta in formato M-Appalti
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.ImportaDitteInGara") and autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS","INS") and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") and empty ngaraaq and visualizzareImportDitte eq "1"}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:importDitte();" title='Importa da formato M-Appalti' tabindex="1500">
											Importa da formato M-Appalti
										</a>
									</td>
								</tr>
							</c:if>

							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS","INS") and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") and empty ngaraaq}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:aggiungiDitta();" title='Aggiungi ditta da anagrafica' tabindex="1503">
											Aggiungi ditta da anagrafica
										</a>
									</td>
								</tr>
							</c:if>
							
							
						
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1504">
													${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
									</td>
								</tr>
							</c:if>
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "ALT","Copia-ditte") and garaLottoUnico eq "false" and isGaraLottiConOffertaUnica eq "false" and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA")}'>
								<tr>
									<td class="vocemenulaterale" >
										<a href="javascript:apriPopupCopiaDitte()" title="Copia ditte" tabindex="1505">
										Copia ditte
										</a>
									</td>
								</tr>
							</c:if>
							
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "MOD","MOD") and datiRiga.rowCount > 0 }'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1505">
											${gene:resource("label.tags.template.dettaglio.schedaModifica")}
										</a>
									</td>
								</tr>
							</c:if>
														
							<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.RiassegnaNumOrdine")}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:RiassegnaNumOrd();" title='Riassegna numero ordine' tabindex="1508">
											Riassegna numero ordine
										</a>
									</td>
								</tr>
							</c:if>		
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1509">
										Imposta filtro
									</a>
								</td>
							</tr>
						</c:otherwise>
						</c:choose>
				</gene:redefineInsert>
		
		
		<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,chiaveRigaJava)}'/>
				
		<c:set var="condizioneEliminazione" value='${(gene:checkProtFunz(pageContext, "DEL","DEL") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")) and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA")}'/>
		
		<c:choose>
			<c:when test='${(empty updateLista or updateLista ne 1) and autorizzatoModifiche ne 2 and (condizioneEliminazione
			 or gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate") or gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento"))}' >
				<gene:set name="titoloMenu">
					<c:if test='${condizioneEliminazione}'>
						<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
					</c:if>
				</gene:set>

					<c:set var="condizioneDettaglioConsorziate" value='${(tipoImpresa == "2" || tipoImpresa == "11") && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate")}'/>

					<c:set var="numAvvalimentiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroAvvalimentiDittaFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA5"),gene:getValCampo(chiaveRigaJava, "DITTAO"))}' />
					<c:choose>
						<c:when test="${numAvvalimentiDitta ne '0'}">
							<c:set var="numAvvalimentiDitta" value="(${numAvvalimentiDitta})"/>
						</c:when>
						<c:otherwise>
							<c:set var="numAvvalimentiDitta" value=""/>
						</c:otherwise>
					</c:choose>
					
					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" >
					<c:if test="${ (condizioneEliminazione and datiRiga.DITG_ACQAUTO ne '1') || condizioneDettaglioConsorziate || gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RicorsoAvvalimento')}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
					
						<c:if test='${condizioneDettaglioConsorziate}'>
							<gene:PopUpItem title="Dettaglio consorziate esecutrici" href="listaDitteConsorziate('${chiaveRigaJava}')" />
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}'>
							<gene:PopUpItem title="Avvalimento ${numAvvalimentiDitta}" href="dettaglioAvvalimento('${chiaveRigaJava}')" />
						</c:if>
						<c:if test='${condizioneEliminazione and datiRiga.DITG_ACQAUTO ne "1" }'>
							<gene:PopUpItem title="Elimina ditta" href="listaElimina()" />
						</c:if>
						
						</gene:PopUp>
					</c:if>
					
					<c:if test='${(gene:checkProtFunz(pageContext, "DEL","DEL") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")) and( empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or (modalitaSelezioneDitteElenco eq "MISTA" and datiRiga.DITG_ACQAUTO ne "1"))}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
			</c:when>
			<c:otherwise>
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
			</c:otherwise>
		</c:choose>			

				<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NGARA5"  visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista title="N." campo="NPROGG" headerClass="sortable" width="50" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroProgressivoDITG"/>
				<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
				<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NOMEST" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" headerClass="sortable"   href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda") and updateLista ne 1, link, "")}'/>
				<gene:campoLista title="NumProgDittaFittizio" campo="NUM_PROGG" entita="DITG" visibile="false" campoFittizio="true" definizione="N7" edit="${updateLista eq 1}" value="${datiRiga.DITG_NPROGG}" computed="true" />
				<gene:campoLista campo="DINVIG" title="Data invito" headerClass="sortable" edit="${updateLista eq 1}" visibile="false"/>
				<gene:campoLista campo="INVOFF" headerClass="sortable" edit="${updateLista eq 1}" width="60"/>
				
				<gene:campoLista campo="DATOFF" headerClass="sortable" edit="${updateLista eq 1}" />
				<gene:campoLista campo="RIBAUO" title="Ribasso" headerClass="sortable" edit="${updateLista eq 1}" />
				<gene:campoLista campo="IMPOFF" title="Importo offerto" headerClass="sortable" edit="${updateLista eq 1}" />
				
				<gene:campoLista campo="NPROTG" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NPROFF" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="DPROFF" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista  campo="DPROFF_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="${datiRiga.DITG_DPROFF}" edit="true" visibile="false"/>
				<gene:campoLista campo="ORAOFF" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="MEZOFF" visibile="false" edit="${updateLista eq 1}" />
				
				<gene:campoLista campo="AMMGAR"  visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="MOTIES"  visibile="false" edit="${updateLista eq 1}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTestoCodifcaDoppiApici"/>
				<gene:campoLista campo="ANNOFF"  visibile="false" edit="${updateLista eq 1}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTestoCodifcaDoppiApici"/>
				
				<c:if test="${iconaNoteAttiva eq 1}">
					<gene:campoLista campo="ALTNOT"  visibile="false"/>
				</c:if>
				
				<c:if test="${(updateLista ne 1) and !empty codiceElenco and codiceElenco != '' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.VerificaDocumentiSelezioneDitteElenco')}">
					<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${datiRiga.DITG_ACQUISIZIONE == 3 }">
							<a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocRichiesti('${chiaveRigaJava}');" title="'Consultazione documenti iscrizione a elenco" >
								<img width="16" height="16" title="Consultazione documenti iscrizione a elenco" alt="Consultazione documenti iscrizione a elenco" src="${pageContext.request.contextPath}/img/documentazione_elenco.png"/>
							</a>
						</c:if>
					</gene:campoLista>
				</c:if>
				
				<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.DitteConcorrentiUlterioriDettagli")}'>
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
							<img width="16" height="16" title="${iconaTooltip}" alt="Ulteriori dettagli" src="${pageContext.request.contextPath}/img/opzioni${note}${estensioneDisabilitata}.png"/>
						</a>
					</gene:campoLista>
				</c:if>	
				
				<c:if test="${updateLista ne 1 and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.NoteAvvisiImpresa')}" >
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
				<c:if test="${ not empty urlWsArt80 and urlWsArt80 ne '' and updateLista ne 1 }">
					<jsp:include page="/WEB-INF/pages/gare/gare/gare-colonna-art80.jsp">
						<jsp:param name="ditta" value="${datiRiga.DITG_DITTAO }"/>
					</jsp:include>
				</c:if>		
				<c:if test="${updateLista ne 1}">
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
				<gene:campoLista campo="ACQUISIZIONE" visibile="false" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="TIPRIN" visibile="false" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="ACQAUTO" visibile="false" />
				<gene:campoLista visibile="false">
					<input type="hidden" name="counter" id="counter" value="${datiRiga.rowCount}" />
				</gene:campoLista >
				<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
				<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
				<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
				<input type="hidden" name="inserimentoDitteIterSemplificato" id="inserimentoDitteIterSemplificato" value="SI" />
				<input type="hidden" name="DTEOFF" id="DTEOFF" value="${dataTerminePresentazioneOfferta}" />
				<input type="hidden" name="OTEOFF" id="OTEOFF" value="${oraTerminePresentazioneOfferta}" />
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
					<c:if test='${datiRiga.rowCount > 0}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "MOD", "MOD")}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;&nbsp;
						</c:if>
					</c:if>
					&nbsp;<br><br>
					<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS", "INS") and (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") and empty ngaraaq}'>
						<INPUT type="button"  class="bottone-azione" value='Aggiungi ditta da anagrafica' title='Aggiungi ditta da anagrafica' onclick="javascript:aggiungiDitta();">&nbsp;&nbsp;&nbsp;
					</c:if>
					<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL") and empty modalitaSelezioneDitteElenco or (modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:if>
					
					<c:if test='${isGaraUsoAlbo and autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS", "INS")}'>
						<br><br>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.selDaElenco")}'>
							<c:set var="aggnumord" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetAGGNUMORDFunction", pageContext, codiceElenco)}'/>
							<c:set var="stazioneAppaltante" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetStazioneAppaltanteFunction", pageContext, gene:getValCampo(key,"NGARA"), "GARE")}'/>
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
						<INPUT type="button"  class="bottone-azione" value='Esporta in formato M-Appalti' title='Esporta in formato M-Appalti' onclick="javascript:exportDitte();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.ImportaDitteInGara") and autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS", "INS")
								 and empty (empty modalitaSelezioneDitteElenco or modalitaSelezioneDitteElenco eq "MAN" or  modalitaSelezioneDitteElenco eq "MISTA") and empty ngaraaq and visualizzareImportDitte eq "1"}'>
						<br><br>
						<INPUT type="button"  class="bottone-azione" value='Importa da formato M-Appalti' title='Importa da formato M-Appalti' onclick="javascript:importDitte();">&nbsp;&nbsp;&nbsp;
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
		setCodiceElenco("${codiceElenco }");
		
		
		document.getElementById("numeroDitte").value = ${currentRow}+1;
				
		function aggiungiDitta(){
			var href = "href=gare/ditg/ditg-schedaPopup-insert.jsp";
			href += "&modo=NUOVO&faseRicezione=${paginaAttivaWizard}";
		<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
			href += "&isGaraLottiConOffertaUnica=true";
		</c:if>
			href += "&inserimentoDitteIterSemplificato=SI";
			openPopUpCustom(href, "aggiungiDitta", 800, 600, "yes", "yes");
		}
	

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
			selezionaPagina(eval(document.pagineForm.activePage.value)+1);
		</c:if>
		

		function apriPopupCopiaDitte() {
			var href = "href=gare/gare/gare-popup-copia-ditte.jsp?codgar="+getValue("GARE_CODGAR1")+"&lottoSorgente="+getValue("GARE_NGARA");
			openPopUpCustom(href, "copiaDitte", 600, 450, "yes", "yes");
		}
		
		function copiaDitta(chiave) {
			var href = "href=gare/gare/gare-popup-copia-ditta.jsp";
			var numeroFaseAttiva = ${paginaAttivaWizard};
			href+="?chiave=" + chiave;
			href+="&numeroFaseAttiva=" + numeroFaseAttiva;
			
			openPopUpCustom(href, "copiaDitta", 600, 450, "yes", "yes");
			
		}
		
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
			
			if(bloccoAggiudicazione=="1")
				bloccoPagina=true;				
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gene/ragdet/ragdet-lista.jsp";
			href += "&key=" + chiaveRiga+ "&bloccoPagina=" + bloccoPagina;
			document.location.href = href;
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
			
			if(selezioneAutomaticaDitte && bloccaSelezioneDaElenco == "true"){
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
			href += "&maxSupDitteSel=${numSupDitteSel}";
			href += "&aggnumord=${aggnumord}"
			var tipoCategoria = "${tipoCategoria}";
			href += "&tipoCategoria=${tipoCategoria }";	
			href += "&tipoalgo=" + tipoalgo;
			href += "&stazioneAppaltante=" + stazioneAppaltante;
			
			href += "&inserimentoDitteIterSemplificato=SI";
			href += "&selezioneAutomaticaDitte=" + selezioneAutomaticaDitte;
			href += "&modalitaSelezioneMista=" + modalitaSelezioneMista;
			openPopUpCustom(href, "elencoOperatoriEconomici", 1000, 550, "yes", "yes");
		
		}
		
		function SelUltimaAgg(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			
			categoriaPrev ="${codiceCategoriaPrev }";
			var tipoalgo ="${tipoalgo }";
			var isFoglia = "${isFoglia }";
			if((tipoalgo == "1" || tipoalgo=="3" || tipoalgo == "4" || tipoalgo == "5") && (categoriaPrev == "" || isFoglia == "2")){
				var msg="Non è possibile procedere poichè nell'elenco il criterio di rotazione è su numero inviti per categoria/classe e\n";
				if(categoriaPrev == "")
					msg+="non è stata specificata nella gara la categoria prevalente";
				else
					msg+="la categoria prevalente specificata è una categoria di livello intermedio";
					
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
				
		function apriPopupCopiaDitte() {
			var lotto="${key }";
			lotto= lotto.substr(lotto.lastIndexOf(":") + 1);
			var filtroGara="${inputFiltro}";
			filtroGara = filtroGara.substr(filtroGara.lastIndexOf(":") + 1);
			var href = "href=gare/gare/gare-popup-copia-ditte.jsp?codgar="+filtroGara+"&lottoSorgente="+lotto;
			openPopUpCustom(href, "copiaDitte", 600, 450, "yes", "yes");
		}
		
		function annulla(){
			document.forms[0].updateLista.value = "0";
			listaAnnullaModifica();
		}
		
		function ulterioriCampi(indiceRiga, chiaveRiga){
			var href = null;
	
			href = "href=gare/ditg/ditg-schedaPopup-ditteConcorrenti.jsp";
		<c:if test='${updateLista eq 1}'>
			href += "&modo=MODIFICA";
		</c:if>
			href += "&indiceRiga=" + indiceRiga;
			href += "&key=" + chiaveRiga;
			href += "&stepWizard=${varTmp}";
			href += "&paginaAttivaWizard=${paginaAttivaWizard}";
			//href += "&moties=" + getValue('V_DITGAMMIS_MOTIVESCL_'+indiceRiga); //href += "&moties=" + getValue('V_DITG_MOTIES_'+indiceRiga);
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
	
		
		<c:if test='${updateLista eq 1}'>
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
				document.getElementById("DITG_INVOFF_" + i).onchange = aggiornaPerCambioInvioOfferta;
				if(document.getElementById("DITG_RIBAUO_" + i)!= null)
					document.getElementById("DITG_RIBAUO_" + i).onchange = aggiornaPerCambioRibassoAumento;
				if(document.getElementById("DITG_DATOFF_" + i)!= null)	
					document.getElementById("DITG_DATOFF_" + i).onchange = checkDataRicezioneOfferta;
				if(document.getElementById("DITG_IMPOFF_" + i)!= null)
					document.getElementById("DITG_IMPOFF_" + i).onchange = aggiornaPerCambioImportoOfferto;
					
			}
		}
		
		associaFunzioniEventoOnchange();
		
		function aggiornaPerCambioInvioOfferta(){
			//alert('start aggiornaPerCambioInvioOfferta');
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);

			globalNumeroRiga = numeroRiga;
			
			if(this.value != "2"){
				setValue("DITG_AMMGAR_" + numeroRiga, "");
				setValue("DITG_TIPRIN_" + numeroRiga, "");
				if(document.getElementById("DITG_DATOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_DATOFF_" + numeroRiga).disabled = false;
				if(document.getElementById("DITG_RIBAUO_" + numeroRiga)!=null)
					document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = false;
				if(document.getElementById("DITG_IMPOFF_" + numeroRiga))
					document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = false;
			}else {
				setValue("DITG_NPROFF_" + numeroRiga, "");
				setValue("DITG_DPROFF_" + numeroRiga, "");
				setValue("DPROFF_FIT_NASCOSTO_" + numeroRiga, "");
				setValue("DITG_DATOFF_" + numeroRiga, "");
				setValue("DITG_ORAOFF_" + numeroRiga, "");
				setValue("DITG_MEZOFF_" + numeroRiga, "");
				setValue("DITG_RIBAUO_" + numeroRiga, "");
				setValue("DITG_IMPOFF_" + numeroRiga, "");
				setValue("DITG_AMMGAR_" + numeroRiga, "2");
				setValue("DITG_MOTIES_" + numeroRiga, "");
				setValue("DITG_ANNOFF_" + numeroRiga, "");
				
				if(document.getElementById("DITG_DATOFF_" + numeroRiga)!=null)
					document.getElementById("DITG_DATOFF_" + numeroRiga).disabled = true;
				if(document.getElementById("DITG_RIBAUO_" + numeroRiga)!=null)
					document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = true;
				if(document.getElementById("DITG_IMPOFF_" + numeroRiga))
					document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = true;

				
			} 
		}
		
		function aggiornaPerCambioRibassoAumento(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
		
			// All'oggetto JS CampoObj del campo DITG_RIBAUO_<i> si cambia la funzione
			// di validazione, per poter effettuare in un'unica funzione i controlli
			// necessari al ribasso offerto o al punteggio all'onchange del campo
			activeForm.getCampo("DITG_RIBAUO_" + numeroRiga).setFnValidazione(validazioneRIBAUO);
			if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
				var tmp = null;
				tmp = getValue("DITG_INVOFF_" + numeroRiga);
				if((tmp == null || tmp == "" || tmp == "0")){
					setValue("DITG_INVOFF_" + numeroRiga, 1);
				}
				var ribauo = this.value;
				if(ribauo != null && ribauo!=""){
					ribauo = toVal(ribauo);
					var importo1 = ${importo1 };
					importo1 = parseFloat(importo1);
					var importo2 = ${importo2 }; 
					importo2 = parseFloat(importo2);
					var impoff = (importo1 * (1 + ribauo/100)) + importo2;
					setValue("DITG_IMPOFF_" + numeroRiga, round(impoff,5));
				
				}else{
					setValue("DITG_IMPOFF_" + numeroRiga, "");
				}
			}
		}
		
		function validazioneRIBAUO(refVal, msg, obj){
			var result = false;
			if(checkFloat(refVal, msg, obj)){
				if(refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})  
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
			return result;
		}
		
		
		function conferma(){
	
			// Riabilitazione prima del salvataggio delle combobox disabilitate all'utente
			for(var i=1; i <= ${currentRow}+1; i++){
				if(document.getElementById("DITG_DATOFF_" + i)!=null)
					document.getElementById("DITG_DATOFF_" + i).disabled = false;
				if(document.getElementById("DITG_RIBAUO_" + i)!=null)
					document.getElementById("DITG_RIBAUO_" + i).disabled = false;
				if(document.getElementById("DITG_IMPOFF_" + i)!=null)
					document.getElementById("DITG_IMPOFF_" + i).disabled = false;
								
			}
		
			listaConferma();
		}
		
		function inizializzaLista(){
			var numeroDitte = ${currentRow}+1;
			for(var t=1; t <= numeroDitte; t++){
				if(getValue("DITG_INVOFF_" + t) == "2"){
					if(document.getElementById("DITG_DATOFF_" + t)!=null)
						document.getElementById("DITG_DATOFF_" + t).disabled = true;
					if(document.getElementById("DITG_RIBAUO_" + t)!=null)
						document.getElementById("DITG_RIBAUO_" + t).disabled = true;
					if(document.getElementById("DITG_IMPOFF_" + t))
						document.getElementById("DITG_IMPOFF_" + t).disabled = true;
					
				}
			}
		}
		
		inizializzaLista()
		
		function checkDataRicezioneOfferta(){
			if(activeForm.onChange(this)){
			<c:if test='${not empty dataTerminePresentazioneOfferta}' >
				var data = this.value;
				if(data!=null && data != ""){
					var objId = this.id;
					var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
					var ora= getValue("DITG_ORADOM_" + numeroRiga, "");
					checkDatiRichiestaOfferta(data, ora,"${dataTerminePresentazioneOfferta}","${oraTerminePresentazioneOfferta }","La data inserita e' successiva alla data termine ricezione offerte");
				}
			</c:if>
			}
		}
	</c:if>
	
	function RiassegnaNumOrd(){
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			var procNegaziata="false";
			href = "href=gare/gare/gare-popup-RiassegnaNumeroOrdine.jsp";
			href += "&WIZARD_PAGINA_ATTIVA=${paginaAttivaWizard}";
			href += "&ngara=" + chiave;
			href += "&procNegaziata=false";
			href += "&isDitteConcorrenti=true";
			openPopUpCustom(href, "riassegnaNumOrdine", 600, 350, "yes", "yes");
		}
       
		
		function impostaFiltro(){
			var comando = "href=gare/commons/popup-trova-filtroDitte.jsp";
			var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
			comando+="&dittePerPagina=" + risultatiPerPagina;
			openPopUpCustom(comando, "impostaFiltro", 850, 550, "yes", "yes");
		}
		
		function AnnullaFiltro(){
		 var comando = "href=gare/commons/popup-filtro.jsp&annulla=2";
		 
		 openPopUpCustom(comando, "impostaFiltro", 10, 10, "no", "no");
		}
		
		function verificaDocRichiesti(chiaveRiga){
			/*
			var chiave = chiaveRiga;
			href = "href=gare/imprdocg/imprdocg-listaPopup.jsp";
			
			href += "&tipo=CONSULTAZIONE";
			var vetTmp = chiaveRiga.split(";");
			var dittao= vetTmp[1].substring(vetTmp[0].indexOf(":"));
			var codiceGara= "$" + "${codiceElenco }";
			var ngara= "${codiceElenco}";
			chiave = "DITG.CODGAR5=T:" + codiceGara + ";DITG.DITTAO=T:" + dittao + ";DITG.NGARA5=T:" + ngara;
			
			href += "&key="+chiave;
			href += "&stepWizard=${varTmp}";
			href += "&comunicazioniVis=0";
			//document.location.href = href;
			openPopUpCustom(href, "verificaDocumentiRichiesti", 1100, 550, "yes", "yes");
			*/
			verificaDocumentiRichiesti(chiaveRiga,"CONSULTAZIONE",0,"true","${autorizzatoModifiche}");
		}	
		
		function aggiornaPerCambioImportoOfferto(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			
			activeForm.getCampo("DITG_IMPOFF_" + numeroRiga).setFnValidazione(validazioneIMPOFF);
			if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
				var impoff = toVal(this.value);
				var cifreDecimali = 9;
				if (impoff == null || impoff == ""){
						setValue("DITG_RIBAUO_" + numeroRiga, "");
				}else{
					var numeratore = ${numeratore }; 
					numeratore = parseFloat(numeratore);
					var denominatore = ${denominatore };
					denominatore = parseFloat(denominatore);
					var ribauo; 
					if (denominatore != 0)
						ribauo = (impoff + numeratore) * 100 / denominatore ;
					else
						ribauo = 0;
					
					if (ribauo >0 && ${requestScope.offaum != "1"}){
						alert('Non sono ammesse offerte in aumento');
						setValue("DITG_RIBAUO_" + numeroRiga, "");
	
					}else {
						<c:if test='${not empty numeroCifreDecimaliRibasso}'>
							cifreDecimali =${numeroCifreDecimaliRibasso};
						</c:if>
						
						setValue("DITG_RIBAUO_" + numeroRiga, round(ribauo,cifreDecimali));
						
						var tmp = null;
						tmp = getValue("DITG_INVOFF_" + numeroRiga);
						if((tmp == null || tmp == "" || tmp == "0")){
							setValue("DITG_INVOFF_" + numeroRiga, 1);
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

		function dettaglioAvvalimento(chiaveRiga){
			var bloccoPagina=false;	
			var bloccoAggiudicazione = "${bloccoAggiudicazione }";		
			if(bloccoAggiudicazione=="1")
				bloccoPagina=true;			
		
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioAvvalimentoDitta.jsp";
			href += "&key=" + chiaveRiga + "&bloccoPagina=" + bloccoPagina;
			document.location.href = href;
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
		
</gene:javaScript>