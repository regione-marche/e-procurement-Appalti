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
 
 /* Pagina a scheda relativa alla fase 'Aggiudicazione provvisoria' del wizard 
  * Fasi di gara (ultimo step per gare non ad offerta unica)
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<jsp:include page="defStepWizardFasiGara.jsp" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>


<c:set var="varTmp" value="${step6Wizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>
<c:set var="fasgarStepgarOffTecnica" value='${varTmp }' />

<c:set var="varTmp" value="${step7Wizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
</c:if>
<c:set var="fasgarStepgarOffEconomica" value='${varTmp }' />


<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction",  pageContext)}' />
<c:set var="temp" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GestioneAggiudicazioneProvvisoriaFunction",  pageContext, codiceGara,gene:getValCampo(key,"NGARA"),fasgarStepgarOffTecnica,fasgarStepgarOffEconomica)}'/>
<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<c:set var="appLegRegSic" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsLeggeRegioneSiciliaFunction", pageContext)}' />

<c:set var="isGaraDopoDLGS2016Manuale" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.IsGaraDopoDLGS2016ManualeFunction", pageContext, numeroGara,"true","true","false")}' />
<c:if test="${modalitaAggiudicazioneGara eq 6}">
	<c:set var="oepvDL_32_2019" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlliDL_32_2019OEPVFunction", pageContext, numeroGara)}' />
</c:if>
	<c:choose>
		<c:when test="${appLegRegSic eq 1 and valLegRegSic eq '1'}">
			<c:set var="mediaScartiTitolo" value="Incremento percentuale"/>
		</c:when>
		<c:otherwise>
			<c:set var="mediaScartiTitolo" value="Media degli scarti"/>
		</c:otherwise>
	</c:choose>
	
<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction",pageContext,codiceGara)}'/>
<c:if test="${modlicg ne 6 and esitoControlloDitteDLGS2016}">
	<c:if test="${offtel eq '3'}">
		<c:set var="ditteRibassoNullo" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteRiammesseFunction",pageContext,numeroGara,"false")}'/>
		<c:if test="${ditteRibassoNullo eq 'true'}">
			<c:set var="calcoloGradQform" value="true"/>
		</c:if>
	</c:if>
</c:if>
		
		<tr>
			<td ${stileDati} >
				&nbsp;
			</td>
		</tr>
		<tr>
			<td ${stileDati} >
				<c:set var="nGara" value="${fn:substringAfter(key, ':')}" />	
				<gene:formScheda entita="GARE" where="${whereGARE}" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGAREFasiGara" >
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
					<gene:redefineInsert name="noteAvvisi" />
					<gene:redefineInsert name="modelliPredisposti" />
					<gene:redefineInsert name="schedaNuovo" />
					<gene:redefineInsert name="schedaModifica" />

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
							<gene:redefineInsert name="addToAzioni">
								
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
												${gene:resource("label.tags.template.dettaglio.schedaModifica")}
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test = '${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.exportXMLAntimafia")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:esportaAntimafiaAccertamento('${key}');" title="Esporta ditta per accertamento antimafia" tabindex="1505">
												Esporta ditta per accertam. antimafia
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InserisciOfferteDitteEscluse" ) and isGaraLottiConOffertaUnica ne "true"}'>
									<tr>
										<td class="vocemenulaterale">
											<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:inserisciOffertaDitteEscluse();" title='Inserisci offerte ditte escluse' tabindex="1506"></c:if>
												Inserisci offerte <br>ditte escluse
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
										</td>
									</tr>
								</c:if>
								<c:if test='${modo eq "VISUALIZZA" and gene:checkProtFunz(pageContext, "MOD","MOD") and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaGaraNonAggiud") and (isGaraLottiConOffertaUnica eq "true" || param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica")}'>
								      <td class="vocemenulaterale">
									      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
													<a href="javascript:impostaGaraNonAggiudicata('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','${datiRiga.GARE_ESINEG}','${datiRiga.GARE_DATNEG}','${datiRiga.GARE1_NPANNREVAGG}');" title="Imposta lotto non aggiudicato" tabindex="1507">
											</c:if>
											  Imposta lotto non aggiudicato
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
										</td>
								</c:if>
								
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1508">
											< Fase precedente
										</a>
									</td>
								</tr>
															

							</gene:redefineInsert>
						</c:otherwise>
					</c:choose>

					<c:set var="controlloProfiloSezAgg" value='${(isGaraLottiConOffertaUnica ne "true" and gene:checkProt(pageContext,"SEZ.VIS.GARE.GARE-scheda.FASIGARA.AGG"))
								or (isGaraLottiConOffertaUnica eq "true" and gene:checkProt(pageContext, "SEZ.VIS.GARE.GARE-AGGIUDPROVDEF.AGG"))}'/>
					<gene:gruppoCampi visibile="${controlloProfiloSezAgg}">
						<gene:campoScheda>
							<td colspan="2"><b>Sintesi calcolo aggiudicazione</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N2" value="${paginaAttivaWizard}" />
						<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
						<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
						<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
						<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
						<gene:campoScheda campo="NGARA" visibile="false" />
						<gene:campoScheda campo="CODGAR1" visibile="false" />
						<gene:campoScheda campo="MODLICG" visibile="false" />
						<gene:campoScheda campo="NGARA" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" visibile="false"/>
						<gene:campoScheda campo="ULTDETLIC" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" visibile="false"/>
					<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
						<gene:campoScheda campo="CODGAR" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false" />
					</c:if>
						<gene:campoScheda campo="CODIGA" visibile="false" />
						<gene:campoScheda campo="METSOGLIA" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${ not empty datiRiga.GARE1_METSOGLIA and esitoControlloDitteDLGS2016 and empty datiRiga.GARE1_SOGLIANORMA and calcoloGradQform ne 'true'}">
							<c:if test='${modo eq "VISUALIZZA"}'>
								<span style="float: right;">
					 			<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/DLgs50-2016_calcoloSogliaAnomalia.pdf');" title="Consulta manuale" style="color:#002E82;">
					 				<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
					 			</a>
					 			</span>
				 			</c:if>
						</gene:campoScheda>
						<gene:campoScheda campo="NOFVAL" modificabile="false" />
						<gene:campoScheda title="Numero offerte accantonate per taglio delle ali" computed = "true" modificabile="false" campo="(NOFVAL - NOFMED)" definizione="N24.5;" visibile="${datiRiga.GARE_MODLICG ne 6 and calcsoang eq '1' and calcoloGradQform ne 'true' and ((esitoControlloDitteDLGS2016  and (isGaraDLGS2016 || isGaraDLGS2017)) || (controlloDitteNormativaPrecedente and !isGaraDLGS2016 and !isGaraDLGS2017)) and (empty datiRiga.GARE1_METSOGLIA || (datiRiga.GARE1_METSOGLIA ne 4 && datiRiga.GARE1_METSOGLIA ne 3))}">
							&nbsp;&nbsp;
							<c:if test="${!empty datiRiga.GARE1_NOFALASUP}">
								:&nbsp;&nbsp;&nbsp;${datiRiga.GARE1_NOFALASUP} offerte più alte <c:if test="${!empty datiRiga.GARE1_NOFALAINF}">,</c:if>
							</c:if>
							<c:if test="${!empty datiRiga.GARE1_NOFALAINF}">
								<c:if test="${empty datiRiga.GARE1_NOFALASUP}">:&nbsp;&nbsp;&nbsp;</c:if>${datiRiga.GARE1_NOFALAINF} offerte più basse
							</c:if>
						</gene:campoScheda>
						<gene:campoScheda campo="NOFMED" visibile="${modalitaAggiudicazioneGara ne 6 and (empty datiRiga.GARE1_METSOGLIA or (datiRiga.GARE1_METSOGLIA ne 3 and datiRiga.GARE1_METSOGLIA ne 4))}" modificabile="false" />
						<gene:campoScheda campo="RIPTEC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="RIPECO"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="METPUNTI" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${modalitaAggiudicazioneGara eq 6 and calcsoang eq '1' and (datiRiga.GARE1_RIPTEC eq 1 or datiRiga.GARE1_RIPTEC eq 2 or datiRiga.GARE1_RIPECO eq 1 or datiRiga.GARE1_RIPECO eq 2) and oepvDL_32_2019 ne 'graduatoria'}" modificabile="false" />
						<gene:campoScheda campo="MEDIA" visibile="${modalitaAggiudicazioneGara ne 6 and !(datiRiga.GARE1_METSOGLIA eq 4 and isGaraDLGS2016 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true')}" modificabile="false" />
						<gene:campoScheda campo="TIPGEN" entita="TORN" where="TORN.CODGAR = GARE.CODGAR1" visibile="false" />
						<gene:campoScheda campo="CORGAR" entita="TORN" modificabile="false" visibile="${not empty datiRiga.TORN_CORGAR and '0' ne datiRiga.TORN_CORGAR and datiRiga.TORN_TIPGEN eq 1 and (modalitaAggiudicazioneGara eq 1 or modalitaAggiudicazioneGara eq 5)}" />
						<gene:campoScheda campo="CORGAR1" modificabile="false" visibile="${not empty datiRiga.GARE_CORGAR1 and '0' ne datiRiga.GARE_CORGAR1 and (datiRiga.TORN_TIPGEN eq 2 or datiRiga.TORN_TIPGEN eq 3) and (modalitaAggiudicazioneGara eq 1 or modalitaAggiudicazioneGara eq 5)}" />
						<gene:campoScheda campo="MEDIASCA" title="Media degli scarti" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${(modalitaAggiudicazioneGara eq 13 or modalitaAggiudicazioneGara eq 14) and (empty datiRiga.GARE1_METSOGLIA || datiRiga.GARE1_METSOGLIA eq 1 || datiRiga.GARE1_METSOGLIA eq 5) and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true' and datiRiga.GARE1_SOGLIANORMA ne 'LR13_2019'}"/>
						<gene:campoScheda campo="SOGLIANORMA" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" visibile="false"/>
						<gene:campoScheda campo="SOGLIA1" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL >= 15}"/>
						<c:choose>
							<c:when test="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' or datiRiga.GARE1_SOGLIANORMA eq 'LR13_2019'}">
								<c:set var="titoloSommarib" value="Somma ribassi offerte mediate" />
							</c:when>
							<c:when test="${descrizioneA1132 eq '1' }">
								<c:set var="titoloSommarib" value="Somma ribassi offerte valide" />
							</c:when>
							<c:otherwise>
								<c:set var="titoloSommarib" value="Somma ribassi offerte mediate" />
							</c:otherwise>
						</c:choose>
						<gene:campoScheda title="${titoloSommarib }" campo="SOMMARIB" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${(modalitaAggiudicazioneGara eq 13 or modalitaAggiudicazioneGara eq 14) and calcoloGradQform ne 'true' and ((datiRiga.GARE1_METSOGLIA eq 2 and esitoControlloDitteDLGS2016) || (datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL >= 15) || datiRiga.GARE1_SOGLIANORMA eq 'LR13_2019')}"/>
						<gene:campoScheda campo="MEDIAIMP" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${(datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and datiRiga.GARE1_METSOGLIA eq 4 and isGaraDLGS2016 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}"/>
						<gene:campoScheda campo="SOGLIAIMP" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${(datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and datiRiga.GARE1_METSOGLIA eq 4 and isGaraDLGS2016 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}"/>
						<gene:campoScheda campo="METCOEFF" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${datiRiga.GARE1_METSOGLIA eq 5 and esitoControlloDitteDLGS2016 and empty datiRiga.GARE1_SOGLIANORMA and calcoloGradQform ne 'true'}"/>
						<gene:campoScheda campo="SOGLIAVAR" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL >= 15}"/>
						<gene:campoScheda campo="MEDIARAP" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL < 15}"/>
						<gene:campoScheda campo="LIMMAX" modificabile="false" visibile="${(modalitaAggiudicazioneGara eq 13 or modalitaAggiudicazioneGara eq 14) and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}" />
						<c:if test="${datiRiga.GARE_MODLICG ne 6 and calcsoang eq '1' and esitoControlloDitteDLGS2016 and not empty datiRiga.GARE_MEDIA and calcoloGradQform ne 'true'}">
							<c:set var="num_max_decimali" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMaxNumeroCifreDecimaliFunction",pageContext,numeroGara) }'/>
						</c:if>
						<gene:campoScheda campo="NUM_MAX_DECIMALI" title="N.decimali massimo dei valori offerti" visibile="${datiRiga.GARE_MODLICG ne 6 and calcsoang eq '1' and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true' and not empty datiRiga.GARE_MEDIA }" campoFittizio="true" definizione="N9;;;" modificabile="false" value='${num_max_decimali }'/>
						<gene:campoScheda campo="PRECUT" modificabile="false" visibile="${datiRiga.GARE_MODLICG ne 6 and calcsoang eq '1' and esitoControlloDitteDLGS2016 and not empty datiRiga.GARE_MEDIA and calcoloGradQform ne 'true'}">
							<c:choose>
								<c:when test="${datiRiga.GARE1_SOGLIACALC eq '1' }">
									<c:set var="msgSOGLIACALC" value="con arrotondamento (solo calcoli intermedi)"/>
								</c:when>
								<c:otherwise>
									<c:set var="msgSOGLIACALC" value="con troncamento (solo calcoli intermedi)"/>
								</c:otherwise>
							</c:choose>	
							&nbsp;${msgSOGLIACALC }
						</gene:campoScheda>
						<gene:campoScheda campo="SOGLIACALC" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${datiRiga.GARE_MODLICG ne 6 and calcsoang eq '1' and esitoControlloDitteDLGS2016 and not empty datiRiga.GARE_MEDIA and calcoloGradQform ne 'true'}"/>
						<gene:campoScheda campo="MODASTG" visibile="false" />
						
						<gene:campoScheda campo="ESCAUTO" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${calcsoang eq '1' and datiRiga.GARE_MODASTG eq 1 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}"/>
						<gene:campoScheda campo="LEGREGSIC" modificabile="false" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="${(modalitaAggiudicazioneGara eq 13 or modalitaAggiudicazioneGara eq 14) and esitoControlloDitteDLGS2016 and appLegRegSic eq '1' and calcoloGradQform ne 'true'}" />
						<gene:campoScheda campo="CODGAR1" visibile="false" obbligatorio="true" />
						<gene:campoScheda campo="NSORTE" modificabile="false" visibile="${modalitaAggiudicazioneGara eq 15 or modalitaAggiudicazioneGara eq 16}" />
						<gene:campoScheda campo="ALAINF" modificabile="false" visibile="${modalitaAggiudicazioneGara eq 15 or modalitaAggiudicazioneGara eq 16}" />
						<gene:campoScheda campo="ALASUP" modificabile="false" visibile="${modalitaAggiudicazioneGara eq 15 or modalitaAggiudicazioneGara eq 16}" />
						<gene:campoScheda campo="NOFALAINF" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
						<gene:campoScheda campo="NOFALASUP" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
					</gene:gruppoCampi>
					<gene:campoScheda campo="AQOPER" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false" />

					<c:set var="controlloProfiloSezDit" value='${(isGaraLottiConOffertaUnica ne "true" and gene:checkProt(pageContext,"SEZ.VIS.GARE.GARE-scheda.FASIGARA.DIT"))
								or (isGaraLottiConOffertaUnica eq "true" and gene:checkProt(pageContext, "SEZ.VIS.GARE.GARE-AGGIUDPROVDEF.DIT"))}'/>
					<gene:gruppoCampi visibile="${controlloProfiloSezDit}" >
						<gene:campoScheda>
							<tr id="rowPROPOSTA_AGGIUDICAZIONE">
								<td colspan="2"><b>Proposta di aggiudicazione</b></td>
							</tr>
						</gene:campoScheda>
						<gene:archivio titolo="Imprese"
							lista=""
							scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
							schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp", "")}'
							campi="IMPR.CODIMP;IMPR.NOMEST"
							chiave="GARE_DITTAP"
							formName="formArchivioImprese"
							inseribile="false" >
							<gene:campoScheda campo="DITTAP"  modificabile="false" />
							<gene:campoScheda campo="NOMEST" entita="IMPR" where="GARE.DITTAP=IMPR.CODIMP" modificabile="false" />
						</gene:archivio>
						<gene:campoScheda campo="DITTAP_FIT"  campoFittizio="true" value="${datiRiga.GARE_DITTAP}" definizione="T10" visibile="false" />
				<c:choose>
					<c:when test='${modalitaAggiudicazioneGara eq 6}'>
						<gene:campoScheda title="Punteggio di aggiudicazione" campo="RIBPRO" modificabile="false" definizione="F13.9;0;;;G1RIBPRO" />
					</c:when>
					<c:when test='${modalitaAggiudicazioneGara eq 17}'>
						<gene:campoScheda title="Rialzo offerto" campo="RIBPRO" modificabile="false" definizione="F13.9;0;;PRC;G1RIBPRO" />
					</c:when>
					<c:otherwise>
						<gene:campoScheda title="Ribasso di aggiudicazione" campo="RIBPRO" modificabile="false" definizione="F13.9;0;;PRC;G1RIBPRO" />
					</c:otherwise>
				</c:choose>
						<gene:campoScheda entita="DITG" campo="CODGAR5" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTAP" visibile="false"/>
						<gene:campoScheda entita="DITG" campo="DITTAO" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTAP" visibile="false"/>
						<gene:campoScheda entita="DITG" campo="NGARA5" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTAP" visibile="false"/>
						<gene:campoScheda campo="ONPRGE" visibile="false" />
						<gene:campoScheda campo="SICINC" visibile="false" />
						<gene:campoScheda campo="IMPSIC" visibile="false" />
						<c:if test='${modalitaAggiudicazioneGara ne 6 || param.isVecchiaOepv eq "true" || param.formato50 eq "true" || param.formato51 eq "true" || param.formato52 eq "true" || (offtel eq 3 and modlicg eq 6)}'>
							<c:choose>
								<c:when test="${datiRiga.GARE_SICINC eq 2 }" >
									<c:choose>
										<c:when test="${ empty datiRiga.GARE1_ULTDETLIC}">
											<gene:campoScheda campo="IAGPRO" modificabile="false" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IAGPRO","DITG.IMPOFF;GARE.IMPSIC")}'/>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${datiRiga.GARE1_ULTDETLIC eq 1 }">
													<gene:campoScheda campo="IAGPRO" modificabile="false" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IAGPRO","DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM")}'/>
												</c:when>
												<c:when test="${datiRiga.GARE1_ULTDETLIC eq 2 }">
													<gene:campoScheda campo="IAGPRO" modificabile="false" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IAGPRO","DITG.IMPOFF;GARE.IMPSIC;DITG.IMPCANO")}'/>
												</c:when>
												<c:otherwise>
													<gene:campoScheda campo="IAGPRO" modificabile="false" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IAGPRO","DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM;DITG.IMPCANO")}'/>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${ empty datiRiga.GARE1_ULTDETLIC}">
											<gene:campoScheda campo="IAGPRO" modificabile="false"/>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${datiRiga.GARE1_ULTDETLIC eq 1 }">
													<gene:campoScheda campo="IAGPRO" modificabile="false" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IAGPRO","DITG.IMPOFF;DITG.IMPPERM")}'/>
												</c:when>
												<c:when test="${datiRiga.GARE1_ULTDETLIC eq 2 }">
													<gene:campoScheda campo="IAGPRO" modificabile="false" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IAGPRO","DITG.IMPOFF;DITG.IMPCANO")}'/>
												</c:when>
												<c:otherwise>
													<gene:campoScheda campo="IAGPRO" modificabile="false" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IAGPRO","DITG.IMPOFF;DITG.IMPPERM;DITG.IMPCANO")}'/>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
							<gene:campoScheda campo="IMPOFF" title="di cui importo offerto" entita="DITG" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTAP" visibile="${datiRiga.GARE_SICINC eq 2 || !empty datiRiga.GARE1_ULTDETLIC }" modificabile="false" />
							<gene:campoScheda campo="IMPSIC" modificabile="false" visibile="${datiRiga.GARE_SICINC eq 2 }" />
							<gene:campoScheda campo="IMPPERM" title="di cui importo per permuta" modificabile="false" entita="DITG" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTAP" visibile="${ datiRiga.GARE1_ULTDETLIC eq 1 or datiRiga.GARE1_ULTDETLIC eq 3}"/>
							<gene:campoScheda campo="IMPCANO" title="di cui importo per canone assistenza" modificabile="false" entita="DITG" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTAP" visibile="${ datiRiga.GARE1_ULTDETLIC eq 2 or datiRiga.GARE1_ULTDETLIC eq 3}"/>
						</c:if>
						
						<gene:campoScheda campo="NOTPROV" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA"/>
						
						<c:if test='${isGaraLottiConOffertaUnica eq "true" || param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica"}'>
							<gene:campoScheda campo="ESINEG" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false" title="Esito lotto non aggiudicato" />
							<gene:campoScheda campo="DATNEG" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
							<gene:campoScheda campo="NPANNREVAGG" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
							<gene:campoScheda campo="NOTNEG" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"  visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
						</c:if>
						
						
					</gene:gruppoCampi>
					
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					
				</gene:formScheda>
			</td>
		</tr>

<gene:javaScript>

	function esportaAntimafiaAccertamento() {
		var href="href=gare/gare/gare-popup-exportAntimafia.jsp&codiceGara=${datiRiga.GARE_NGARA}&tipoRichiesta=ACCERTAMENTO&islottoGara=${!garaLottoUnico}";
		openPopUpCustom(href, "exportXMLantimafia", 550, 300, "yes", "yes");
	}
	
	function inserisciOffertaDitteEscluse(){
		var modlicg = "${modlicg }";
		var codiceGara = getValue("GARE_CODGAR1");
		var ditta = getValue("DITTAP_FIT");
		var ngara = getValue("GARE_NGARA");
		var chiave = "DITG.CODGAR5=T:" + codiceGara + ";";
		chiave += "DITG.DITTAO=T:" + ditta + ";";
		chiave += "DITG.NGARA5=T:" + ngara;
				
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/inserimentoOfferteDitteEscluse-lista.jsp";
		href += "&key=" + chiave;
		href += "&modalitaGara=" + modlicg;
		href += "&isGaraLottiConOffertaUnica=false";
		
		document.location.href = href;
	}
	
	<c:if test='${modalitaAggiudicazioneGara ne 6 || param.isVecchiaOepv eq "true" || param.formato50 eq "true" || param.formato51 eq "true" || param.formato52 eq "true"}'>
		var ultdelic = getValue("GARE1_ULTDETLIC");
		if(ultdelic!= null && ultdelic!=""){
			var showDettG1IAGPRO_Default = showDettG1IAGPRO;
	 		
	 		//Si forza la visualizzazione di un valore negativo in IMPPERM
		 	function showDettG1IAGPRO_Custom(){
		 		showDettG1IAGPRO_Default();
		 		if ($("#DITG_IMPPERMview").is(":visible")) {
		 			var importo = $("#DITG_IMPPERM").val();
		 			if(importo!=null){
		 				importo = importo * (-1);
		 				importo = formatNumber(importo, 20.2);
		 			}
		 			<c:choose>
		 				<c:when test="${modo eq 'MODIFICA' }">
		 					$("#DITG_IMPPERMedit").val(importo);		
		 				</c:when>
		 				<c:otherwise>
		 					if(importo!=null){
			 					importo =formatCurrency(importo,',','.');
			 					var tmp = $("#DITG_IMPPERMview");
			 					tmp.children("span").text("");
			 					tmp.children("span").append(importo);
		 					}
		 					
		 				</c:otherwise>
		 			</c:choose>
		 			
		 		}
		 	}
		 	showDettG1IAGPRO =   showDettG1IAGPRO_Custom;
		}
	</c:if>
	
	<c:if test='${isGaraLottiConOffertaUnica eq "true" || param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica"}'>
		function impostaGaraNonAggiudicata(ngara,codgar1,esineg,datneg,npannrevagg){
			var href="href=gare/commons/popup-ImpostaGaraNonAggiudicata.jsp&ngara=" + ngara + "&codgar1=" + codgar1 + "&esineg=" + esineg + "&datneg=" + datneg + "&npannrevagg=" + npannrevagg;
			href+="&isLottoOffUnica=Si";
			openPopUpCustom(href, "impostaGaraNonAggiudicata", 700, 400, "yes", "yes");
		}
	</c:if>
	
	function visualizzazioneCampiPerAQOPER(){
		var controlloProfiloSezDit =${controlloProfiloSezDit };
		var aqoper = getValue("GARE1_AQOPER");
		if(aqoper==2 && controlloProfiloSezDit){
			showObj("rowGARE_DITTAP", false);
			showObj("rowIMPR_NOMEST", false);
			showObj("rowGARE_RIBPRO", false);
			showObj("rowGARE_IAGPRO", false);
			showObj("rowGARE_IMPOFF", false);
			showObj("rowGARE_IMPSIC", false);
			showObj("rowGARE_IMPPERM", false);
			showObj("rowGARE_IMPCANO", false);
		}	
	}
	
	visualizzazioneCampiPerAQOPER();
	
</gene:javaScript>
		