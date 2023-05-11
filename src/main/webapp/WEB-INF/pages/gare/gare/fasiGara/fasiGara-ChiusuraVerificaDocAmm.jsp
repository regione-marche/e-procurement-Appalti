<%
/*
 * Created on: 27-apr-2010
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

	<c:choose>
		<c:when test='${garaLottoUnico or isGaraLottiConOffertaUnica eq "true"}'>
			<c:set var="titoliLotti" value=""/>
		</c:when>
		<c:otherwise>
			<c:set var="titoliLotti" value="(comuni a tutti i lotti)"/>
		</c:otherwise>
	</c:choose>
	<tr>
		<td ${stileDati} >
			&nbsp;
		</td>
	</tr>
	<tr>
		<td ${stileDati} >
				<gene:formScheda entita="GARE" where="${whereGARE}" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGara_ChiusuraVerificaDocAmm" >
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
														
								
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaFasiGara) and bloccoAggiudicazione ne 1 }'>
									<c:choose>
										<c:when test='${(empty datiRiga.GARE_FASGAR or datiRiga.GARE_FASGAR eq "") or (not empty datiRiga.GARE_FASGAR and datiRiga.GARE_FASGAR ne "" and datiRiga.GARE_FASGAR < 5)}' >
											<c:set var="faseAperturaDocAmmChiusa" value="2" scope="request" />
											<c:choose>
												<c:when test="${param.gestioneGaraConcorsoProgAttiva}">
													<c:set var="etichettaPulsanteApOff" value="${etichettaInserimentoPunteggiAnonima}"  />
													<c:if test="${ gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AcquisizioneTecAnonima') }">
													<tr>
														<td class="vocemenulaterale">
															<a href="javascript:aperturaBusteTecnicheAnonime('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}');" title='${etichettaAcquisizioneAnonima }' tabindex="1504">${etichettaAcquisizioneAnonima}</a>
														</td>
									                </tr>
									                </c:if>
									                <c:if test="${ gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.ScaricaZipBusteAnonime') }">
									                <tr>
														<td class="vocemenulaterale">
															<a href="javascript:exportZipBusteAnonime('${datiRiga.GARE_NGARA}');" title='${etichettaScaricaZipAnonima}' tabindex="1504">${etichettaScaricaZipAnonima}</a>
														</td>
									                </tr>
									                </c:if>
									            </c:when>
												<c:otherwise>
													<c:set var="etichettaPulsanteApOff" value="Attiva apertura offerte"  />
												</c:otherwise>
											</c:choose>
											<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaOfferte') }">
												<tr>
													<td class="vocemenulaterale">
														<a href="javascript:confermaChiusuraAperturaFasi('ATTIVA','');" title='${etichettaPulsanteApOff}' tabindex="1504">${etichettaPulsanteApOff}</a>
													</td>
								                </tr>
							                </c:if>
										</c:when>
										<c:otherwise>
											<c:set var="faseAperturaDocAmmChiusa" value="1" scope="request" />
											<c:if test="${isProceduraTelematica ne 'true' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaOfferte') }">
												<tr>
													<td class="vocemenulaterale">
														<a href="javascript:confermaChiusuraAperturaFasi('DISATTIVA','');" title='Disattiva apertura offerte' tabindex="1504">Disattiva apertura offerte</a>
													</td>
								               	</tr>
											</c:if>
											
										</c:otherwise>
									</c:choose>
								</c:if>
								
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1505">
											< Fase precedente
										</a>
									</td>
								</tr>
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

						<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
						<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
						<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
						<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
						<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
						<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />

						<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
						<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
						
						<gene:campoScheda campo="NGARA" visibile="false" />
						<gene:campoScheda campo="CODGAR1" visibile="false" />
					<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
						<gene:campoScheda campo="CODGAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false" />
					</c:if>
						<gene:campoScheda campo="CODIGA" visibile="false" />
						<gene:campoScheda campo="FASGAR" visibile="false"/>

					<gene:gruppoCampi idProtezioni="VERIFICREQUI">
						<gene:campoScheda>
							<td colspan="2"><b>Verifica requisiti</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="DVAPDOCAMM" />
						<gene:campoScheda campo="DVVIDONOFF" />
		 				<gene:campoScheda campo="NPLETTCOMESCL" entita="GARESTATI" where="GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
						<gene:campoScheda campo="DPLETTCOMESCL" entita="GARESTATI" where="GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
		 				<gene:campoScheda campo="NPLETTRICHCC" entita="GARESTATI" where="GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
						<gene:campoScheda campo="DLETTRICHCC"  entita="GARESTATI" where="GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
						<gene:campoScheda campo="DTERMPRESCC"  entita="GARESTATI" where="GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
						<gene:campoScheda campo="DVVERCC"      entita="GARESTATI" where="GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}" />
						<gene:campoScheda campo="DGARA" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="${datiRiga.TORN_COMPREQ eq '1' }"/>
						<gene:campoScheda campo="OGARA" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="${datiRiga.TORN_COMPREQ eq '1' }"/>
					</gene:gruppoCampi>
					<gene:campoScheda campo="COMPREQ" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false" />
					<gene:campoScheda campo="CHIUSURA_APERTURA_FASI" definizione="T20;0" campoFittizio="true" visibile="false" value="" />
								
				</gene:formScheda>
			</td>
		</tr>
<gene:javaScript>
	

</gene:javaScript>