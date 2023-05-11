<%
/*
 * Created on: 07-07-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /*
  * Pagina a scheda relativa alla fase 'Chiusura verifica documentazione amministrativa'
  * del wizard Fasi di gara (terzo step).
  *
  * Osservazione: questa jsp e' stata copiata e modifica per le gare a lotti ad
  * offerta unica (vedi fasiGara-ChiusuraVerificaDocAmm-OffertaUnica.jsp)
  *
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

	<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

	<tr>
		<td ${stileDati} >
			&nbsp;
		</td>
	</tr>
	<tr>
		<td ${stileDati} >
				<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGara_ChiusuraVerificaDocAmm" >
					<gene:redefineInsert name="noteAvvisi" />
					<gene:redefineInsert name="documentiAssociati" >
						<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>
							<tr>
								<c:choose>
					        <c:when test='${isNavigazioneDisattiva ne "1" and updateLista ne 1}'>
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
								<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and autorizzatoModifiche ne 2 
									and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteEconomiche")
									and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
									<c:if test="${isProceduraTelematica eq 'true' and faseGara eq 5}">
										<tr>
											<td class="vocemenulaterale">
												<c:choose>
													<c:when test="${visOffertaEco eq 'false' and bustalotti eq '2'}">
														<c:set var="etichettaAttivazione" value="Attiva calcolo aggiudicazione"/>
													</c:when>
													<c:otherwise>
														<c:set var="etichettaAttivazione" value="Attiva apertura offerte economiche"/>
													</c:otherwise>
												</c:choose>
												<a href="javascript:AttivaAperturaEconomiche();" title='${etichettaAttivazione }' tabindex="1504">
													${etichettaAttivazione }
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1505">
											< Fase precedente
										</a>
									</td>
								</tr>
								<c:if test="${isProceduraTelematica ne 'true' or (isProceduraTelematica eq 'true' and faseGara >5 and visOffertaEco eq true)}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1506">
												Fase seguente >
											</a>
										</td>
									</tr>
								</c:if>
							</gene:redefineInsert>
							
							<gene:redefineInsert name="schedaModifica" >
								<c:if test='${autorizzatoModifiche ne 2 and bloccoAggiudicazione ne 1}'>
								<c:if test='${gene:checkProt(pageContext, strProtModificaFasiGara)}'>
								<c:if test='${isProceduraTelematica ne "true" or (isProceduraTelematica eq "true" and faseGara eq 5 )}'>	
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

						<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
						<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
						<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
						<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
						<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
						<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />

						<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
						<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
						
						<gene:campoScheda campo="NGARA" entita="GARE" where="TORN.CODGAR=GARE.CODGAR1 and TORN.CODGAR=GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="CODGAR1" entita="GARE" where="TORN.CODGAR=GARE.CODGAR1 and TORN.CODGAR=GARE.NGARA" visibile="false" />
					
						<gene:campoScheda campo="CODGAR"  visibile="false" />
					
						<gene:campoScheda campo="CODIGA" entita="GARE" where="TORN.CODGAR=GARE.CODGAR1 and TORN.CODGAR=GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="FASGAR" entita="GARE" where="TORN.CODGAR=GARE.CODGAR1 and TORN.CODGAR=GARE.NGARA" visibile="false"/>

					<gene:gruppoCampi idProtezioni="VERIFICREQUI">
						<gene:campoScheda>
							<td colspan="2"><b>Chiusura valutazione tecnica</b></td>
						</gene:campoScheda>
							<c:set var="whereGarestati" value="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}"/>
							<gene:campoScheda campo="NPLETTCOMESCL" entita="GARESTATI" from="GARE" where="${whereGarestati }" />
							<gene:campoScheda campo="DPLETTCOMESCL" entita="GARESTATI" from="GARE" where="${whereGarestati }" />
			 				<gene:campoScheda campo="NPLETTRICHCC" entita="GARESTATI" from="GARE" where="${whereGarestati }" />
							<gene:campoScheda campo="DLETTRICHCC"  entita="GARESTATI" from="GARE" where="${whereGarestati }" />
							<gene:campoScheda campo="DTERMPRESCC"  entita="GARESTATI" from="GARE" where="${whereGarestati }" />
							<gene:campoScheda campo="DVVERCC"      entita="GARESTATI" from="GARE" where="${whereGarestati }" />
					</gene:gruppoCampi>
				</gene:formScheda>
			</td>
		</tr>
