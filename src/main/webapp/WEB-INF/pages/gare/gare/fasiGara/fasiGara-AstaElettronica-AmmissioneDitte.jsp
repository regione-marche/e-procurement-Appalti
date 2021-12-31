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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // Il campo GARE.FASGAR per compatibilita' con PWB assume valore pari a %>
<% // floor(GARE.STEPGAR/10), cioè il piu' grande intero minore o uguale a %>
<% // GARE.STEPGAR/10 %>



<c:set var="varTmp" value="${step8Wizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>
		
<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.gare.note")}' scope="request"/>
		
		<c:if test="${!empty filtroDitte  and gene:checkProt(pageContext, strProtVisualizzaFasiGara)}">
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
		
		<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA# and (DITG.FASGAR > 6 or DITG.FASGAR = 0 or DITG.FASGAR is null)' />
		
		<tr>
			<td ${stileDati} >
				<gene:formLista entita="DITG" where='${whereDITG}${filtroDitte }' tableclass="datilista" sortColumn="7;4;5" pagesize="${requestScope.risultatiPerPagina}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGara" gestisciProtezioni="true" >
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1}'>
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
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara) and datiRiga.rowCount > 0 and bloccoAggiudicazione ne 1 and faseGara eq 6}'>
									<c:if test='${modificaGaraTelematica}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
													${gene:resource("label.tags.template.dettaglio.schedaModifica")}
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								<c:if test="${gene:checkProt(pageContext, strProtVisualizzaFasiGara)}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1505">
												Imposta filtro
											</a>
										</td>
									</tr>
								</c:if>
								
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1508">
											< Fase precedente
										</a>
									</td>
								</tr>		
								<c:if test='${stepgar > 65}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1507">
												Fase seguente >
											</a>
										</td>
									</tr>	
								</c:if>	
								
							</c:otherwise>
						</c:choose>
						
								
					</gene:redefineInsert>
					
					
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />
										
					<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="NGARA5"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista title="N.pl" campo="NUMORDPL" headerClass="sortable" width="32" />
					<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
					<gene:campoLista campo="NOMIMO" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' />
					<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" width="80" headerClass="sortable" ordinabile ="true" edit="${updateLista eq 1}" />
					<gene:campoLista campo="RIBAUO" headerClass="sortable" width="130" definizione="F13.9;0;;PRC;RIBAUO" title="Ribasso offerto"/>
					<gene:campoLista campo="IMPOFF" headerClass="sortable" visibile="${modlicg eq '5' or modlicg eq '14' or detlicg eq '4' }" width="${gene:if(updateLista eq 1, '150', '')}"/>
					<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />					
					<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="ESCLUDI_DITTA_ALTRI_LOTTI" visibile="false" value="0" campoFittizio="true" definizione="N2" edit="${updateLista eq 1}"/>
					<% // Questo campo non viene MAI modificato nella pagina e la sua valorizzazione viene gestito solo lato server. %>
					<% // E' presente nella pagina come campo hidden per capire se il valore del campo V_DITGAMMIS.AMMGAR e' ereditato %>
					<% // da una fase precedente o e' effettivamente un valore esistente per lo step del wizard in visualizzazione %>
					<gene:campoLista campo="AMMGAR" entita="DITGAMMIS" where="DITGAMMIS.CODGAR=DITG.CODGAR5 and DITGAMMIS.NGARA=DITG.NGARA5 and DITGAMMIS.DITTAO=DITG.DITTAO and DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPSICAZI" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPMANO" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPPERM" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPCANO" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="INVOFF"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PARTGAR" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1}" />
					
					<c:if test='${updateLista ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.VerificaDocumenti")}' >
						<gene:campoLista title="&nbsp;" width="20" >
							<a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocumentiRichiesti('${chiaveRigaJava}','VERIFICA','1','false', '${autorizzatoModifiche}');" title="Verifica documenti richiesti" >
								<img width="16" height="16" title="Verifica documenti richiesti" alt="Verifica documenti richiesti" src="${pageContext.request.contextPath}/img/documentazione.png"/>
							</a>
						</gene:campoLista>
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
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';ulterioriCampiAsta(${currentRow+1}, '${chiaveRigaJava}');" title="Ulteriori dettagli" >
							<img width="16" height="16" title="${iconaTooltip}" alt="Ulteriori dettagli" src="${pageContext.request.contextPath}/img/opzioni${note}.png"/>
						</a>
					</gene:campoLista>
					
				<c:if test='${updateLista eq 0}' >
					<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
				</c:if>
					
					
					
					<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
					<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
					<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
					<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
					<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
					<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" id="pgAsta" name="pgAsta" value="${pgAsta }" />
				</gene:formLista>
			</td>
		</tr>
		
