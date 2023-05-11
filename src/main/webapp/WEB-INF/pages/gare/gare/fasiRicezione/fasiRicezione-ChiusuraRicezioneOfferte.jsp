<%
/*
 * Created on: 30-apr-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* Pagina a scheda relativa alla fase 'Chiusura ricezione offerte' del wizard
  * Ricezione offerte (ultimo step)
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
			<c:set var="nGara" value="${fn:substringAfter(key, ':')}" />
				<gene:formScheda entita="GARE" where="${whereGARE}" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreSetFase" >
					<gene:redefineInsert name="documentiAssociati" />
					<gene:redefineInsert name="noteAvvisi" />
					<c:choose>
						<c:when test='${updateLista eq 1 and bloccoAggiudicazione ne 1}'>
							<gene:redefineInsert name="addAzioni">
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
							</gene:redefineInsert>
						</c:when>
						<c:otherwise>
							<c:set var="whereNobustamm" value="codgar='${datiRiga.GARE_CODGAR1  }'"/>
							<c:set var="nobustamm" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "nobustamm","torn", whereNobustamm)}' />
							<c:choose>
								<c:when test='${ nobustamm ne "1"}'>
									<c:set var="msg" value="documentazione amministrativa"/>
									<c:set var="msgCorto" value="doc.amministrativa"/>
								</c:when>
								<c:otherwise>
									<c:set var="msg" value="offerte"/>
									<c:set var="msgCorto" value="offerte"/>
								</c:otherwise>
							</c:choose>
							
							<gene:redefineInsert name="schedaNuovo" />
							<gene:redefineInsert name="schedaModifica" />
							<gene:redefineInsert name="addToAzioni">
								<c:choose>
									<c:when test='${(empty datiRiga.GARE_FASGAR or datiRiga.GARE_FASGAR eq "") or (not empty datiRiga.GARE_FASGAR and datiRiga.GARE_FASGAR ne "" and datiRiga.GARE_FASGAR < 2)}' >
		                				<c:set var="faseRicezioneChiusa" value="2" scope="request" />
										<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaFasiRicezione) and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AttivaAperturaDoc")}'>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:confermaChiusuraRicezione('ATTIVA');" title='Attiva apertura ${msg}' tabindex="1504">Attiva apertura ${msgCorto}</a>
												</td>
											</tr>
										</c:if>
									</c:when>
									<c:otherwise>
		                				<c:set var="faseRicezioneChiusa" value="1" scope="request" />
										<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtModificaFasiRicezione) and bloccoAggiudicazione ne 1 and isProceduraTelematica ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIRICEZIONE.AttivaAperturaDoc")}'>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:confermaChiusuraRicezione('DISATTIVA');" title='Disattiva apertura ${msg}' tabindex="1504">Disattiva apertura ${msgCorto}</a>
												</td>
											</tr>
										</c:if>
									</c:otherwise>
								</c:choose>
									<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
									<c:if test="${autorizzatoModifiche ne 2 and isProceduraTelematica eq 'true' and (fn:contains(listaOpzioniUtenteAbilitate, 'ou89#') or fn:contains(listaOpzioniUtenteAbilitate, 'ou233#')) and datiRiga.GARE_FASGAR >=1 and datiRiga.GARE_FASGAR < 5}">
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:annullaRicezionePlichiDomande(1,'${nGara }',${datiRiga.TORN_ITERGA},'${datiRiga.GARE_CODGAR1 }','${datiRiga.V_GARE_GENERE_GENERE }');" title='Annulla ricezione offerte' tabindex="1505">
													Annulla ricezione offerte
												</a>
											</td>
										</tr>
									</c:if>
																									
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:indietro();" title='Fase precedente' tabindex="1506">
												< Fase precedente
											</a>
										</td>
									</tr>

								

							</gene:redefineInsert>
						</c:otherwise>
					</c:choose>
					<gene:gruppoCampi>
						<gene:campoScheda>
							<td colspan="2"><b>Dati di sintesi</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N1" value="${paginaAttivaWizard}" />
						<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
						<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
						<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
						<gene:campoScheda campo="NGARA" visibile="false" />
						<gene:campoScheda campo="CODGAR1" visibile="false" />
					<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
						<gene:campoScheda campo="CODGAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false" />
					</c:if>
						<gene:campoScheda campo="CODIGA" visibile="false" />
						<gene:campoScheda campo="FASGAR" visibile="false"/>
						<gene:campoScheda title="Numero ditte che hanno presentato domanda di partecipazione" campo="numeroDittePartecipanti" campoFittizio="true" definizione="N7" value="${numeroDittePartecipanti}" visibile="${not isProceduraAggiudicazioneAperta and not isProceduraNegoziata}" modificabile="false" />
						<gene:campoScheda title="Numero ditte invitate" campo="numeroDitteInvitate" campoFittizio="true" definizione="N7" value="${numeroDitteInvitate}" visibile="${not isProceduraAggiudicazioneAperta}" modificabile="false" />
						<gene:campoScheda title="Numero ditte che hanno presentato offerta" campo="numeroDitteConOfferta" campoFittizio="true" definizione="N7" value="${numeroDitteConOfferta}" modificabile="false" />
						<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
				
					</gene:gruppoCampi>

					<gene:gruppoCampi>
						<gene:campoScheda>
							<td colspan="2"><b>Chiusura ricezione offerte</b></td>
						</gene:campoScheda>
						<gene:campoScheda title="Fase di ricezione offerte chiusa?" campo="CHIUSURA_FASE_OFFERTE" definizione="N1;0;;SN" campoFittizio="true" value="${faseRicezioneChiusa}" />
						<gene:campoScheda campo="CHIUSURA_FASI_RICEZIONE" definizione="T20;0" campoFittizio="true" visibile="false" value="" />
						<gene:campoScheda campo="METSOGLIA" definizione="N2;0" campoFittizio="true" visibile="false" value="" />
						<gene:campoScheda campo="METCOEFF" definizione="F2.2;0" campoFittizio="true" visibile="false" value="" />
						<gene:campoScheda campo="GESTIONE_SOGLIA" definizione="T10;0" campoFittizio="true" visibile="false" value="NO" />
						<gene:campoScheda campo="ISGARADLGS2017" definizione="T10;0" campoFittizio="true" visibile="false" value="NO" />
						<gene:campoScheda campo="APPLICA_TUTTI_LOTTI" definizione="T10;0" campoFittizio="true" visibile="false" value="1" />
					</gene:gruppoCampi>
					
					<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false" />
					<gene:campoScheda campo="GENERE" entita="V_GARE_GENERE" where="GARE.CODGAR1=V_GARE_GENERE.CODGAR" visibile="false" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					
				</gene:formScheda>
			</td>
		</tr>