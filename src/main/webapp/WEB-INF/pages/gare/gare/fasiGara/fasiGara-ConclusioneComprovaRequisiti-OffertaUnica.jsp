<%
/*
 * Created on: 19-mag-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* Pagina a scheda relativa alla fase 'Conclusione comprova requisiti'
  * del wizard Fasi di gara (quinto step) per le gare a lotti con offerta unica.
  * Questa pagina e' stata creata dalla copia della jsp fasiGara-ConclusioneComprovaRequisiti.jsp:
  * la modifica consiste nel passare dall'entita' GARE all'entita' TORN, introducendo
  * quindi le join del caso nei vari campi
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
	<tr>
		<td ${stileDati} >
			&nbsp;
		</td>
	</tr>
	<tr>
		<td ${stileDati} >
				<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGara_ConclusComprovaRequisiti">
					<gene:redefineInsert name="noteAvvisi" />
					<gene:redefineInsert name="documentiAssociati" >
						<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>
							<tr>
								<c:choose>
					        <c:when test='${isNavigazioneDisattiva ne "1"}'>
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
					<c:choose>
						<c:when test='${updateLista eq 1 and bloccoAggiudicazione ne 1}'>
							<gene:redefineInsert name="schedaConferma">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:conferma();" title="Salva modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a>
									</td>
								</tr>
							</gene:redefineInsert>
							<gene:redefineInsert name="schedaAnnulla">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1502">
										${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a>
									</td>
								</tr>
							</gene:redefineInsert>
						</c:when>
						<c:otherwise>
							<gene:redefineInsert name="schedaNuovo" />
							<gene:redefineInsert name="addToAzioni">
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaFasiGara) and bloccoAggiudicazione ne 1 and bustalotti eq 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaOfferte")}'>
									<c:choose>
										<c:when test='${(empty datiRiga.GARE_FASGAR or datiRiga.GARE_FASGAR eq "") or (not empty datiRiga.GARE_FASGAR and datiRiga.GARE_FASGAR ne "" and datiRiga.GARE_FASGAR < 5)}' >
											<c:set var="faseAperturaDocAmmChiusa" value="2" scope="request" />
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:confermaChiusuraAperturaFasi('ATTIVA','${bustalotti }');" title='Attiva apertura offerte' tabindex="1504">Attiva apertura offerte</a>
												</td>
							                </tr>
										</c:when>
										<c:otherwise>
											<c:set var="faseAperturaDocAmmChiusa" value="1" scope="request" />
											<c:if test="${isProceduraTelematica ne 'true'}">
												<tr>
													<td class="vocemenulaterale">
														<a href="javascript:confermaChiusuraAperturaFasi('DISATTIVA','${bustalotti }');" title='Disattiva apertura offerte' tabindex="1504">Disattiva apertura offerte</a>
													</td>
								               	</tr>
								           </c:if>
										</c:otherwise>
									</c:choose>
								</c:if>
								
								<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and autorizzatoModifiche ne 2 
									and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteTecniche")
									and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
									<c:if test="${isProceduraTelematica eq 'true' and isSorteggioControlloRequisiti eq 'true' and (modalitaAggiudicazioneGara eq '6' or attivaValutazioneTec) and bustalotti ne 1}">
										<c:if test="${faseGara eq '2' or faseGara eq '3' or faseGara eq '4'}">
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:confermaChiusuraAperturaFasi('ATTIVA','${bustalotti }');" title='Attiva apertura offerte tecniche' tabindex="1504">
														Attiva apertura offerte tecniche
													</a>
												</td>
											</tr>
										</c:if>
									</c:if>
								</c:if>
								
								<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and autorizzatoModifiche ne 2 
									and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteEconomiche")
									and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
									<c:if test="${isProceduraTelematica eq 'true' and isSorteggioControlloRequisiti eq 'true' and modalitaAggiudicazioneGara ne '6' and not attivaValutazioneTec and bustalotti ne 1}">
										<c:if test="${faseGara eq '2' or faseGara eq '3' or faseGara eq '4'}">
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:AttivaAperturaEconomiche();" title='Attiva apertura offerte economiche' tabindex="1504">
														Attiva apertura offerte economiche
													</a>
												</td>
											</tr>
										</c:if>
									</c:if>
								</c:if>
								
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1505">
											< Fase precedente
										</a>
									</td>
								</tr>

								<c:if test="${bustalotti eq 2}">
									<c:if test="${isProceduraTelematica ne 'true' or !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4'))}">
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:avanti();" title='Fase seguente' tabindex="1506">
													Fase seguente >
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
							</gene:redefineInsert>
							<gene:redefineInsert name="schedaModifica" >
								<c:if test='${autorizzatoModifiche ne 2 and bloccoAggiudicazione ne 1}'>
								<c:if test='${gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
									<c:if test='${isProceduraTelematica ne "true" or (isProceduraTelematica eq "true" and (faseGara eq 2 or faseGara eq 3 or faseGara eq 4))}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:modificaLista();" title="Modifica dati" tabindex="1501">
												${gene:resource("label.tags.template.dettaglio.schedaModifica")}</a></td>
										</tr>
									</c:if>
								</c:if>
								</c:if>
							</gene:redefineInsert>
						</c:otherwise>
					</c:choose>
						<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N1" value="${paginaAttivaWizard}" />
						<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
						<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
						<input type="hidden" id="pgSort" name="pgSort" value="" />
						<input type="hidden" id="pgLastSort" name="pgLastSort" value="" />
						<input type="hidden" id="pgLastValori" name="pgLastValori" value="" />

						<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
						<gene:campoScheda campo="NGARA" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="CODGAR1" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false" />

						<gene:campoScheda campo="CODGAR" visibile="false" />

						<gene:campoScheda campo="CODIGA" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="FASGAR" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false"/>
					<gene:gruppoCampi>
						<gene:campoScheda>
							<td colspan="2"><b>Conclusione verifica requisiti capacità economico-finanziaria e tecnico-organizzativa</b></td>
						</gene:campoScheda>
							<gene:campoScheda campo="DVCOMPREQ" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
			 				<gene:campoScheda campo="NPLETTCOMESCL" entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
							
							<gene:campoScheda campo="DPLETTCOMESCL" entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
							<gene:campoScheda campo="NOTCOMPREQ" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
							<gene:campoScheda campo="DINVDOCTEC" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
					</gene:gruppoCampi>
					<gene:campoScheda campo="CHIUSURA_APERTURA_FASI" definizione="T20;0" campoFittizio="true" visibile="false" value="" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
				</gene:formScheda>
			</td>
		</tr>
