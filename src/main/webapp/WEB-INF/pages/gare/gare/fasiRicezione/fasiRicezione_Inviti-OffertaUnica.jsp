<%
/*
 * Created on: 20-apr-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* 
  * Pagina a scheda relativa alla fase 'Inviti' del wizard Ricezione offerte per 
  * gare a lotti con offerta unica.
  * 
  * Questa pagina e' stata creata dalla copia della pagina fasiRicezione-Inviti.jsp:
  * la modifica consiste nel passare dall'entita' GARE all'entita' TORN, introducendo
  * quindi le join del caso nei vari campi
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.storico.rettifica.termini.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>	

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="codgar1" value='${gene:getValCampo(key, "TORN.CODGAR")}' />
<c:set var="genereGara" value="3" scope="request"/>
<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.CaricaDescPuntiContattoFunction",  pageContext, codgar1, "TORN","")}'/>

<c:set var="condizioniDocumentazioneRichiesta" value='${gene:checkProt(pageContext,"SEZ.VIS.GARE.TORN-OFFUNICA-scheda.DOCUMGARA.DOCUMCONC") }'/>

<c:set var="condizioniAtti" value='${gene:checkProt(pageContext,"SEZ.VIS.GARE.TORN-OFFUNICA-scheda.DOCUMGARA.DOCUMGARA")}'/>
<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:if test='${firmaProvider eq 2}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:set var="isFaseRicezione" value='true' scope="request"/>

<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, key)}' scope="request" />

<c:set var="cifraturaBuste" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneCifraturaBusteFunction", pageContext)}'/>

<c:if test="${integrazioneWSDM == '1'}">
	<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", codgar1,idconfi)}' scope="request"/>
</c:if>

<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>
<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>

<c:set var="bloccoPubblicazionePortale13" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext, codgar1,"BANDO13","false")}' />
<c:if test="${bloccoPubblicazionePortale13 eq 'TRUE'}">
	<c:set var="numDitteGaraCorso" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ConteggioDitteFunction", pageContext, codgar1,"ACQUISIZIONE = 9 AND AMMGAR = 2")}' scope="request"/>
</c:if>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

	<tr>
		<td ${stileDati} >
			&nbsp;
		</td>
	</tr>
	<tr>
		<td ${stileDati} ><!-- where="${whereGARE}"  -->
				<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione_Inviti" 
					plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDocumentazioneGara">
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
					<gene:redefineInsert name="noteAvvisi" />
					<c:choose>
						<c:when test='${(modo eq "MODIFICA" or updateLista eq 1) and bloccoAggiudicazione ne 1}'>
							<gene:redefineInsert name="schedaConferma">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:conferma();" title="Salva modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
								</tr>
							</gene:redefineInsert>
							<gene:redefineInsert name="schedaAnnulla">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1502">
										${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a></td>
								</tr>
							</gene:redefineInsert>
						</c:when>
						<c:otherwise>
						
							<!--c-:-set var="strProtModificaFasiRicezione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.MOD-FASE${paginaAttivaWizard}" scope="request" /-->
							<!--c-:-set var="strProtVisualizzaFasiRicezione" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIRICEZIONE.VIS-FASE${paginaAttivaWizard}" scope="request" /-->
							<gene:redefineInsert name="schedaNuovo" />
							<gene:redefineInsert name="schedaModifica" >
								<c:if test='${tipoDoc ne 10 and autorizzatoModifiche ne 2 and bloccoAggiudicazione ne 1 and condizioneModificaSezioneProfilo eq true and gene:checkProt(pageContext, strProtModificaFasiRicezione) and ((datiRiga.TORN_GARTEL ne 1 and !(applicareBloccoPubblicazioneGareNonTelematiche eq "1" and bloccoPubblicazionePortale eq "SI")) or (datiRiga.TORN_GARTEL eq 1 and (datiRiga.GARE_FASGAR eq -3 or datiRiga.GARE_FASGAR eq -4)))
										and (tipoDoc eq 1 || (tipoDoc ne 1 and tipoDoc ne 10 and bloccoPubblicazioneEsitoPortale ne "SI" and bloccoPubblicazionePortale ne "SI")
										|| (tipoDoc eq 10 and bloccoPubblicazioneEsitoPortale ne "SI" and bloccoPubblicazionePortale ne "SI" and bloccoPubblicazionePortale11 ne "TRUE"))}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaLista();" title="Modifica dati" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaModifica")}</a></td>
									</tr>
								</c:if>
							</gene:redefineInsert>
							
							<gene:redefineInsert name="addToAzioni">
								
								<c:if test='${autorizzatoModifiche ne 2 and modoAperturaScheda eq "VISUALIZZA" and isIntegrazionePortaleAlice eq "true"  and (itergaMacro eq "3" || itergaMacro eq "2") and bloccoPubblicazionePortale ne "SI" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.RICINV.PubblicaSuAreaRiservata")
									and (isProceduraTelematica ne "true" or (isProceduraTelematica eq "true" and (meruolo eq "1" or meruolo eq "3") and (faseGara eq "-3" || faseGara eq "-4")))}'>
									<c:set var="vocePubblicaPortale" value="Pubblica su portale Appalti"/>
									<c:if test='${isProceduraTelematica eq "true"}'>
										<c:set var="vocePubblicaPortale" value="Invia invito e pubblica su portale Appalti"/>
									</c:if>
									<tr>
										<td class="vocemenulaterale" >
											<c:if test='${isNavigazioneDisattiva ne "1"}'>
												<a href="javascript:pubblicaSuAreaRiservata(3)" title="${vocePubblicaPortale}" tabindex="1502">
											</c:if>
												${vocePubblicaPortale}
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
										</td>
									</tr>
								</c:if>	
								
								<c:if test='${autorizzatoModifiche ne 2 and modoAperturaScheda eq "VISUALIZZA" and isIntegrazionePortaleAlice eq "true" and bloccoPubblicazionePortale13 eq "TRUE" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.RICINV.InvioInvitiAGaraCorso")
								   and isProceduraTelematica eq "true" and (meruolo eq "1" or meruolo eq "3") and faseGara eq 1 and (!empty numDitteGaraCorso and numDitteGaraCorso ne "" and numDitteGaraCorso ne "0")}' >
									<tr>
										<td class="vocemenulaterale" >
											<c:if test='${isNavigazioneDisattiva ne "1"}'>
												<a href="javascript:pubblicaSuAreaRiservata(5)" title="Invia invito a gara in corso" tabindex="1502">
											</c:if>
												Invia invito a gara in corso
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
										</td>
									</tr>
								</c:if>	
								
								
								<c:if test='${autorizzatoModifiche ne "2" and modo eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.RettificaTermini" ) 
									and tipoDoc eq 1 and (datiRiga.TORN_GARTEL ne 1 or (datiRiga.TORN_GARTEL eq 1 and bloccoPubblicazionePortale eq "SI"))}'>
								  	<c:set var="esisteFunzioneRettificaTermini" value="true" scope="request"/>
								  	<tr>    
								    	  <td class="vocemenulaterale">
									      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
													<a href="javascript:popupRettificaTermini(${datiRiga.TORN_ITERGA },'${datiRiga.TORN_CODGAR}','${datiRiga.TORN_GARTEL }');" title="Rettifica termini di gara" tabindex="1510">
											</c:if>
											  Rettifica termini di gara
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
										</td>
									</tr>
								</c:if>
								
								<c:if test='${numDocAttesaFirma > 0 and modo eq "VISUALIZZA"}'>
									<tr>
										<td class="vocemenulaterale" >
											<c:if test='${isNavigazioneDisattiva ne "1"}'>
												<a href="javascript:historyReload();" title='Rileggi dati' tabindex="1505">
											</c:if>
												Rileggi dati
											<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
										</td>
									</tr>
								</c:if>	
								
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:indietro();" title='Fase precedente' tabindex="1505">
											< Fase precedente
										</a>
									</td>
								</tr>
								<c:if test="${!(datiRiga.TORN_GARTEL eq 1 and datiRiga.GARE_FASGAR < 1) }">
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
						<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N1" value="${paginaAttivaWizard}" />
						
						<gene:campoScheda campo="NGARA" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="CODGAR1" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false" />

						<gene:campoScheda campo="CODGAR" visibile="false" />
						<gene:campoScheda campo="GARTEL"  visibile="false" />
						<gene:campoScheda campo="CODIGA" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false" />
						<gene:campoScheda campo="FASGAR" entita="GARE" where="TORN.CODGAR=GARE.NGARA" visibile="false"/>
						<gene:campoScheda campo="ITERGA" visibile="false"/>
						<gene:campoScheda campo="MODLIC" visibile="false"/>
						
						<gene:campoScheda campo="IMPTOR"  visibile="false"/>
						<gene:campoScheda campo="TIPGAR"  visibile="false"/>
						<gene:campoScheda campo="TIPGEN"  visibile="false"/>
						<gene:campoScheda campo="PROURG"  visibile="false"/>
						<gene:campoScheda campo="BANWEB"  visibile="false"/>
						<gene:campoScheda campo="DOCWEB"  visibile="false"/>
						<gene:campoScheda campo="TERRID"  visibile="false"/>
						<gene:campoScheda campo="OGGCONT"  visibile="false"/>
						<gene:campoScheda campo="CENINT"  visibile="false"/>
						<gene:campoScheda campo="VALTEC"  visibile="false"/>
						
						<gene:campoScheda>
							<td colspan="2">
								<br>
								<input type="radio" value="1" name="filtroDocumentazione" id="datiInvito" <c:if test='${tipoDoc eq 1}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione(1);" />
								 Dati dell'invito
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<c:if test='${condizioniAtti}'>
									<input type="radio" value="10" name="filtroDocumentazione" id="atti" <c:if test='${tipoDoc eq 10}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione(10);" />
									 Documenti e atti  
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:if>
								<c:if test='${condizioniDocumentazioneRichiesta}'>
									<input type="radio" value="3" name="filtroDocumentazione" id="documentazioneRichiesta" <c:if test='${tipoDoc eq 3}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione(3);" />
									Documenti richiesti ai concorrenti
								</c:if>
								<br><br>
							</td>
						</gene:campoScheda>	
					<c:choose>	
					<c:when test="${tipoDoc eq 1 }">
						
						<c:set var="condizioneModificaSezioneProfilo" value='true' scope="request"/>
						
						<gene:gruppoCampi idProtezioni="RIEPDITTE" visibile="${isVisibileSezRiepilogoDitte}">
							<gene:campoScheda>
								<td colspan="2"><b>Riepilogo ditte</b></td>
							</gene:campoScheda>
							<gene:campoScheda title="Numero ditte che hanno presentato domanda di partecipazione" campo="numeroDittePartecipanti" campoFittizio="true" definizione="N7" value="${numeroDittePartecipanti}" visibile="${not isProceduraAggiudicazioneAperta}" modificabile="false" />
							<gene:campoScheda title="Numero ditte escluse" campo="numeroDitteEscluse" campoFittizio="true" definizione="N7" value="${numeroDitteEscluse}" visibile="${not isProceduraAggiudicazioneAperta}" modificabile="false" />
							<gene:campoScheda title="Numero ditte da invitare" campo="numeroDitteDaInvitare" campoFittizio="true" definizione="N7" value="${numeroDitteDaInvitare}" modificabile="false" />
							<gene:campoScheda title="Di cui numero ditte ammesse con riserva" campo="numeroDiCuiDitteAmmesseConRiserva" campoFittizio="true" definizione="N7" value="${numeroDiCuiDitteAmmesseConRiserva}" modificabile="false" visibile="${numeroDiCuiDitteAmmesseConRiserva ne '0'}"/>
						</gene:gruppoCampi>
	
						<gene:gruppoCampi idProtezioni="VERREQ" visibile="${isVisibileSezVerificaRequisiti}">
							<gene:campoScheda>
								<td colspan="2"><b>Verifica requisiti</b></td>
							</gene:campoScheda>
							<gene:campoScheda campo="DVVREQSEDRIS" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
			 				<gene:campoScheda campo="NPLETTRICHCC" entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}"/>
							<gene:campoScheda campo="DLETTRICHCC"  entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}"/>
							<gene:campoScheda campo="DTERMPRESCC"  entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}"/>
							<gene:campoScheda campo="DVVERCC"      entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}"/>
						</gene:gruppoCampi>
	
						<gene:gruppoCampi idProtezioni="ATAMMESCL" visibile="${isVisibileSezAttoAmmissioneEsclusione}">
							<gene:campoScheda>
								<td colspan="2"><b>Atto di ammissione o esclusione</b></td>
							</gene:campoScheda>
							<gene:campoScheda campo="TATTAMMESCL" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
							<gene:campoScheda campo="NATTAMMESCL" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
							<gene:campoScheda campo="DATTAMMESCL" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
			 				<gene:campoScheda campo="NPLETTCOMESCL" entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}"/>
							<gene:campoScheda campo="DPLETTCOMESCL" entita="GARESTATI" from="GARE" where="TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=TORN.CODGAR and GARESTATI.CODGAR=GARE.CODGAR1 and GARESTATI.NGARA=GARE.NGARA and GARESTATI.FASGAR=${fasGarPerEsclusioneDitta} and GARESTATI.STEPGAR=${paginaAttivaWizard}"/>
						</gene:gruppoCampi>
	
						<gene:gruppoCampi idProtezioni="ESTRINV" visibile="${isVisibileSezEstremiInvito}">
							<gene:campoScheda>
								<td colspan="2"><b>Estremi invito</b></td>
							</gene:campoScheda>
								<gene:campoScheda campo="DINVIT" />
					 			<gene:campoScheda campo="NPROTI" visibile="${(integrazioneWSDM != '1') or (integrazioneWSDM == '1' and not (isProceduraTelematica and abilitatoInvioSingolo))}"/>
						</gene:gruppoCampi>
						
						<c:if test='${esisteFunzioneRettificaTermini eq "true" }'>
							<c:set var="esisteRettificaOfferta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDatiRettificaTerminiFunction",  pageContext, codgar1, "2")}'/>
							<c:set var="esisteRettificaApertura" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDatiRettificaTerminiFunction",  pageContext, codgar1, "3")}'/>
						</c:if>
						
						<gene:gruppoCampi idProtezioni="PRESOFF" visibile="${isVisibileSezTerminiPresOfferta}">
							<gene:campoScheda>
								<td colspan="2"><b>Termini per la presentazione dell'offerta</b><c:if test="${esisteRettificaOfferta eq 'true' }"> <span style="float:right"><a id="aLinkVisualizzaDettaglioRettificaTerminiOfferta" href="javascript:showDettRetifica(2);" class="link-generico">Visualizza termini di gara precedenti alla rettifica</a></span></c:if></td>
							</gene:campoScheda>
							<c:choose>
								<c:when test='${garaLottoUnico or isGaraLottiConOffertaUnica eq "true"}'>
									<gene:campoScheda campo="DTEOFF" speciale='${gene:checkProtFunz(pageContext, "ALT","CalcoloTermineMinimo")}'>
										<c:if test='${gene:checkProtFunz(pageContext, "ALT","CalcoloTermineMinimo")}'>
											<gene:popupCampo titolo="Calcola termine minimo" href="calcolaTermineMinimo('DTEOFF','TORN');" />
										</c:if>
									</gene:campoScheda>
									<gene:campoScheda campo="OTEOFF" />
									<gene:campoScheda campo="PCOOFF_FIT" campoFittizio="true" title="Presentazione presso" definizione="T2;;A1098;;" 
										visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOOFF") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCOFF")}'
										gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
										<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOOFF_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
									</gene:campoScheda>
									<gene:archivio titolo="Punti di contatto" 
										lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.PCOOFF"),"gene/punticon/punticon-lista-popup.jsp","")}' 
										scheda=''
										schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
										campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
										functionId="default"
										parametriWhere="T:${datiRiga.TORN_CENINT}"
										chiave="TORN_PCOOFF;CENINT_PCOOFF"
										formName="formPuntiContPresentazioneOfferta"
										inseribile="false">
											<gene:campoScheda campo="CENINT_PCOOFF" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
											<gene:campoScheda campo="PCOOFF" visibile="false"/>
											<gene:campoScheda campo="NOMPUN_PCOOFF" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOOFF }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOOFF")}'/>
									</gene:archivio>
									
									<gene:campoScheda campo="LOCOFF" />
									<gene:campoScheda campo="VALOFF" />
								</c:when>
								<c:when test="${!garaLottoUnico}">
									<gene:campoScheda campo="DTEOFF" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
									<gene:campoScheda campo="OTEOFF" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
								</c:when>
							</c:choose>
							<gene:campoScheda campo="DTERMRICHCPO"  />
							<gene:campoScheda campo="DTERMRISPCPO"  />
							<gene:campoScheda addTr="false" nome="pippo" visibile="${esisteRettificaOfferta eq 'true' }">
									<tr id="rigaTabellaRettificaTerminiOfferta">
										<td colspan="2">
											<table id="tabellaRettificaTerminiOfferta" class="griglia" >
												
							</gene:campoScheda>
								
								
							<gene:campoScheda addTr="false" nome="pippo1" visibile="${esisteRettificaOfferta eq 'true' }">
											
											</table>
										<td>
									<tr>
							</gene:campoScheda>	
											
						</gene:gruppoCampi>
						<gene:gruppoCampi idProtezioni="APERPLIC" visibile="${isVisibileSezAperturaPlichi}">
							<gene:campoScheda>
								<td colspan="2"><b>Apertura plichi</b><c:if test="${esisteRettificaApertura eq 'true' }"> <span style="float:right"><a id="aLinkVisualizzaDettaglioRettificaTerminiApertura" href="javascript:showDettRetifica(3);" class="link-generico">Visualizza termini di gara precedenti alla rettifica</a></span></c:if></td>
							</gene:campoScheda>
							<c:choose>
								<c:when test='${garaLottoUnico or isGaraLottiConOffertaUnica eq "true"}'>
									<gene:campoScheda title="Data" campo="DESOFF" />
									<gene:campoScheda title="Ora" campo="OESOFF" />
								</c:when>
								<c:when test="${!garaLottoUnico}">
									<gene:campoScheda title="Data" campo="DESOFF" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
									<gene:campoScheda title="Ora" campo="OESOFF" entita="GARE" where="TORN.CODGAR=GARE.NGARA" />
								</c:when>
							</c:choose>
							<gene:campoScheda campo="PCOGAR_FIT" campoFittizio="true" title="Presso" definizione="T2;;A1098;;" 
								visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOGAR") || gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.LOCGAR")}'
								gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso">
								<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOGAR_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
							</gene:campoScheda>
							<gene:archivio titolo="Punti di contatto" 
								lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.PCOGAR"),"gene/punticon/punticon-lista-popup.jsp","")}' 
								scheda=''
								schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
								campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
								functionId="default"
								parametriWhere="T:${datiRiga.TORN_CENINT}"
								chiave="TORN_PCOGAR;CENINT_PCOGAR"
								formName="formPuntiContAperturaPlichi"
								inseribile="false">
									<gene:campoScheda campo="CENINT_PCOGAR" campoFittizio="true" definizione="T16" visibile="false" value="${datiRiga.TORN_CENINT}"/>
									<gene:campoScheda campo="PCOGAR" visibile="false"/>
									<gene:campoScheda campo="NOMPUN_PCOGAR" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOGAR }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.PCOGAR")}'/>
							</gene:archivio>
							<gene:campoScheda campo="LOCGAR" />
							<gene:campoScheda addTr="false" visibile="${esisteRettificaApertura eq 'true' }">
									<tr id="rigaTabellaRettificaTerminiApertura">
										<td colspan="2">
											<table id="tabellaRettificaTerminiApertura" class="griglia" >
												
							</gene:campoScheda>
								
								
							<gene:campoScheda addTr="false" visibile="${esisteRettificaApertura eq 'true' }">
											
											</table>
										<td>
									<tr>
							</gene:campoScheda>	
						</gene:gruppoCampi>
						<c:if test='${isIntegrazionePortaleAlice eq "true"  and (itergaMacro eq "3" || itergaMacro eq "2") and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.RICINV.PubblicaSuAreaRiservata")}'>
						<gene:gruppoCampi idProtezioni="AREARIS" visibile="${isVisibileSezAperturaPlichi}">
							<gene:campoScheda>
								<td colspan="2"><b>Pubblicazione su portale Appalti</b></td>
							</gene:campoScheda>
							<gene:campoScheda campo="TIPPUB" entita="PUBBLI" where="TORN.CODGAR=PUBBLI.CODGAR9 and (TIPPUB=13 or TIPPUB=23)" modificabile="false"/>
							<gene:campoScheda campo="DATPUB" entita="PUBBLI" where="TORN.CODGAR=PUBBLI.CODGAR9 and (TIPPUB=13 or TIPPUB=23)" modificabile="false"/>
							<c:if test="${modo eq 'VISUALIZZA' }">
								<c:set var="whereNumComunicazioni" value="idprg='PG' and comstato = '15' and comkey1='${codgar1 }'"/>
								<c:set var="numComunicazioniErrore" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext,"count(idcom)","W_INVCOM",whereNumComunicazioni)}'/>
								<c:if test='${!empty numComunicazioniErrore and numComunicazioniErrore ne "" and numComunicazioniErrore ne "0" }'>
									<gene:campoScheda>
										<td class="etichetta-dato"><span style="color:red" ><b>ATTENZIONE:</b></span></td>
										<td class="valore-dato"><span style="color:red" ><b>${numComunicazioniErrore }</b> inviti non sono stati inviati in seguito a errore in protocollazione</span></td>
									</gene:campoScheda>
								</c:if>
							</c:if>
						</gene:gruppoCampi>
						</c:if>
						
						
						
						
						<c:if test="${modo ne 'VISUALIZZA' }">
							<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOOFF_FIT#','LOCOFF','PCOOFF')" elencocampi="PCOOFF_FIT" esegui="false" />
							<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOGAR_FIT#','LOCGAR','PCOGAR')" elencocampi="PCOGAR_FIT" esegui="false" />
						</c:if>
				</c:when>
					<c:when test="${tipoDoc eq 10 or tipoDoc eq 3}">
						<c:choose>
							<c:when test="${garaLottoUnico}">
								<c:set var="chiave1"  value="${codgar1 }"/>
								<c:set var="chiave2"  value="${numeroGara }"/>
							</c:when>
							<c:otherwise>
								<c:set var="chiave1"  value="${codgar1 }"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${tipoDoc eq 10}">
								<c:set var="dati" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetTipologieDocumentiJsonFunction", pageContext,codiceGara,numeroGara)}'></c:set>
							</c:when>
							<c:otherwise>
								<c:set var="varQuestionari" value="${gestioneQuestionariPreq},${gestioneQuestionariAmm},${gestioneQuestionariTec},${gestioneQuestionariEco}" />
								<c:set var="dati" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetBusteDocumentiConcorrentiJsonFunction", pageContext,codiceGara,numeroGara,varQuestionari)}'></c:set>
							</c:otherwise>
						</c:choose>
		
						<gene:redefineInsert name="schedaModifica"/>
						<gene:redefineInsert name="pulsanteModifica"/>
						<gene:campoScheda>
							<c:if test="${dati eq '[]'}">
								<td colspan="2"><b>Non &egrave; possibile effettuare pubblicazioni</b></td>
							</c:if>
							<c:if test="${dati ne '[]'}">
								<td colspan="2"><b>&nbsp;</b>
								<br>
								<div id="jstree2" class="demo"></div></td>		
							</c:if>
						</gene:campoScheda>
						
						<gene:redefineInsert name="head">

						<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />
						<script type="text/javascript" src="${pageContext.request.contextPath}/js/jstree.min.js"></script>
						<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
						<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.alphanum.js"></script>
						<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.character.js"></script>
						<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	

						<style type="text/css">
							#jstree2 .even > .jstree-wholerow { background:none;transition: 0.3s; } 
							#jstree2 .odd > .jstree-wholerow { background:none;transition: 0.3s; } 
							#jstree2 .even > .jstree-search{font-style:normal;color:black;font-weight:normal}
							#jstree2 .odd > .jstree-search{font-style:normal;color:black;font-weight:normal}
							#jstree2 .jstree-search{font-style:normal;color:black;font-weight:normal}
							
							#jstree2 .even.jstree-leaf{background:#E7F1FF;}
							#jstree2 .odd.jstree-leaf{background:#CEDAEB;}
							
							#jstree2 .even.jstree-leaf{padding-left:10px;}
							#jstree2 .odd.jstree-leaf{padding-left:10px;}
							
							#jstree2 .jstree-leaf{transition: 0.3s;}
							#jstree2 .jstree-leaf:hover{background:#7A91E6;}
							
							#jstree2 .even { background:none; }
							#jstree2 .odd  { background:none; }
							#jstree2 .even > .jstree-search{font-style:normal;color:black;font-weight:normal}
							#jstree2 .odd > .jstree-search{font-style:normal;color:black;font-weight:normal}
							
							#jstree2 .jstree-icon{width:0px;}
							#jstree2 .jstree-anchor img {padding:5px;padding-right:6px;}
							
							#jstree2 .jstree-anchor { height:auto !important; white-space:normal !important; width:100%; }
							
							#jstree2, .demo { max-width:100%; overflow:auto; box-shadow:0 0 5px #ccc; padding:10px; border-radius:5px; }
							
							.jstree-default .jstree-anchor{
								line-height: 14px; 
								padding: 7 0 7 0;
								height: auto !important;
							}
							
							SPAN.mcontatore {
							font: 10px Verdana, Arial, Helvetica, sans-serif;
							font-weight: bold;
							color: #FFFFFF;
							border: 1px solid #D30000;
							background-color: #D30000;
							padding-left: 2px;
							padding-right: 2px;
							float: right;
							-moz-border-radius-topleft: 2px; 
							-webkit-border-top-left-radius: 2px; 
							-khtml-border-top-left-radius: 2px; 
							border-top-left-radius: 2px; 
							-moz-border-radius-topright: 2px;
							-webkit-border-top-right-radius: 2px;
							-khtml-border-top-right-radius: 2px;
							border-top-right-radius: 2px;
							-moz-border-radius-bottomleft: 2px; 
							-webkit-border-bottom-left-radius: 2px; 
							-khtml-border-bottom-left-radius: 2px; 
							border-bottom-left-radius: 2px; 
							-moz-border-radius-bottomright: 42px;
							-webkit-border-bottom-right-radius: 2px;
							-khtml-border-bottom-right-radius: 2px;
							border-bottom-right-radius: 2px;
							}
						</style>
							
						</gene:redefineInsert>
					</c:when>
				</c:choose>
					<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
						<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
						<input type="hidden" id="pgSort" name="pgSort" value="" />
						<input type="hidden" id="pgLastSort" name="pgLastSort" value="" />
						<input type="hidden" id="pgLastValori" name="pgLastValori" value="" />
						<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
												
						<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
				</gene:formScheda>
				<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
				<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
					<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
					<input type="hidden" name="idprg" id="idprg" value="" />
					<input type="hidden" name="iddocdig" id="iddocdig" value="" />
				</form>
				<c:if test="${tipoDoc eq 3 and fn:contains(listaOpzioniDisponibili, 'OP135#')}">
					<jsp:include page="/WEB-INF/pages/gare/commons/modalePopupInserimentodocumenti.jsp" />
				</c:if>
			</td>
		</tr>
		<form name="formVisualizzaDocumenti" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="gare/documgara/documenti-tipologia.jsp" /> 
			<input type="hidden" name="key" value="" />
			<input type="hidden" name="codiceGara" value="${codiceGara}" />
			<input type="hidden" name="tipologiaDoc" value="" />
			<input type="hidden" name="gruppo" value="" />
			<input type="hidden" name="firstTimer" value="true" />
			<input type="hidden" name="lottoFaseInvito" value="true" />
			<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />	
			<input type="hidden" name="isProceduraTelematica" value="${isProceduraTelematica}" />
			<input type="hidden" name="busta" value="" />
			<input type="hidden" name="titoloBusta" value="" />
			<input type="hidden" name="bustalotti" value="${bustalotti}" />
			<input type="hidden" name="modoQform" id="modoQform" value="" />
		</form> 
		
<gene:javaScript>

//Apertura popup per la pubblicazione su Area Riservata portale Alice Gare
//La funzione viene adoperata nel caso di bando=3 per l'invio invito e pubblicazione portale Appalti
//nel caso di bando=5 per l'invito a gara in corso
function pubblicaSuAreaRiservata(bando) {
	var isProceduraTelematica = ${isProceduraTelematica };
	var integrazioneWSDM = "${integrazioneWSDM}";
	var href = "href=gare/commons/popupPubblicaSuPortale.jsp?codgar="+getValue("GARE_CODGAR1")+"&bando=3&isProceduraTelematica=" + isProceduraTelematica + "&step=1";
	var href = "href=gare/commons/popupPubblicaSuPortale.jsp?codgar="+getValue("GARE_CODGAR1")+"&bando=" + bando +"&isProceduraTelematica=" + isProceduraTelematica + "&step=1";
	dim1 = 800;
	dim2 = 650;
	if(isProceduraTelematica && bando==3){
		dim1=850;
		<c:choose>
			<c:when test="${cifraturaBuste eq '1'}">
				dim2=800;
			</c:when>
			<c:otherwise>
				dim2=600;
			</c:otherwise>
		</c:choose>
	}else if(isProceduraTelematica && bando==5){
		dim2=650;
	}
	if(integrazioneWSDM =='1'){
		if(!isProceduraTelematica){
			<c:if test="${esisteFascicoloAssociato ne true }">
				dim1=850;
				dim2=500;
			</c:if>
		}
		href += "&entita=GARE&entitaWSDM=GARE";
	}
	if(idconfi){
		href +="&idconfi="+idconfi;
	}
	href +="&valtec="+getValue("TORN_VALTEC");
	openPopUpCustom(href, "pubblicaSuPortale", dim1, dim2, "no", "yes");
}

function calcolaTermineMinimo(campo){
		var isGaraLottoUnico = "${garaLottoUnico }";
		var tipgen = getValue("TORN_TIPGEN");
		var tipgar;
		var iterGara;
		var importo;
		tipgar = getValue("TORN_TIPGAR");
		iterGara = getValue("TORN_ITERGA");
		importo = getValue("TORN_IMPTOR");
		var prourg = getValue("TORN_PROURG");
		var docweb = getValue("TORN_DOCWEB");
		var terrid = getValue("TORN_TERRID");
		var href = "href=gare/commons/popup-calcolaTermineMinimo.jsp";
		href += "&isGaraLottoUnico=" + isGaraLottoUnico;
		href += "&tipgen=" + tipgen;
		href += "&tipgar=" + tipgar;
		href += "&iterGara=" + iterGara; 
		href += "&importo=" + importo;
		href += "&prourg=" + prourg;
		href += "&docweb=" + docweb;
		href += "&terrid=" + terrid;
		href += "&campo=" + campo;
		href += "&entita=TORN";
		href += "&faseInviti=Si";
		var dinvit = getValue("TORN_DINVIT");
		href += "&dinvit=" + dinvit;
		openPopUpCustom(href, "calcolaTermineMinimo", 700, 600, 1, 1);
	}
	
	function showCampiContatto(valore,nomeCampo,nomeCampoFit){
		if(valore == 2) {
			showObj("rowTORN_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, true);
			setValue("TORN_" + nomeCampo, "");
		}else if(valore == 3){
			showObj("rowTORN_" + nomeCampo, true);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("TORN_" + nomeCampoFit, "");
			setValue("NOMPUN_" + nomeCampoFit, "");
		}else{
			showObj("rowTORN_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("TORN_" + nomeCampo, "");
			setValue("TORN_" + nomeCampoFit, "");
		}
	}
	
	
	function valorizzaCampiPresso(punto,luogo,campoPresso){
		var valore="";
		<c:choose>
			<c:when test="${modo eq 'VISUALIZZA' }">
				if(punto!="")
					valore = "Punto di contatto";
				else if(luogo!="")
					valore = "Altro (specificare)";
				else
					valore = "Stazione appaltante";	
			</c:when>
			<c:otherwise>
				if(punto!="")
					valore = 2;
				else if(luogo!="")
					valore =3;
				else
					valore = 1;
			</c:otherwise>
		</c:choose>
		setValue(campoPresso,valore);
	}
	
	function checkPresenzaCenint(valore){
		if(valore==2){
			var cenint = getValue("TORN_CENINT");
			if(cenint=="" || cenint == null)
				return false;
		}
		return true;
	}
	
	function puntiContattoSezioneTerminiPresDomandaOfferta(){
		
			showObj("rowPCOOFF_FIT", true);
			var punti = getValue("TORN_PCOOFF");
			var luogo = getValue("TORN_LOCOFF");
			if(punti != ""){
				showObj("rowNOMPUN_PCOOFF", true);
				showObj("rowTORN_LOCOFF", false);
				//setValue("TORN_LOCOFF", "");
			}else if(luogo!=""){
				showObj("rowTORN_LOCOFF", true);
				showObj("rowNOMPUN_PCOOFF", false);
				//setValue("TORN_PCOOFF", "");
				//setValue("NOMPUN_PCOOFF", "");
			}else{
				showObj("rowTORN_LOCOFF", false);
				showObj("rowNOMPUN_PCOOFF", false);
				//setValue("TORN_LOCOFF", "");
				//setValue("TORN_PCOOFF", "");
				//setValue("NOMPUN_PCOOFF", "");
			}
		
	}
	
	
	<c:if test="${isVisibileSezTerminiPresOfferta }">
		var punto = getValue("TORN_PCOOFF");
		var luogo = getValue("TORN_LOCOFF");
		valorizzaCampiPresso(punto,luogo,"PCOOFF_FIT");
		
		puntiContattoSezioneTerminiPresDomandaOfferta();
	</c:if>
	
	function puntiContattoSezioneAperturaOfferte(){
		
			showObj("rowPCOGAR_FIT", true);
			var punti = getValue("TORN_PCOGAR");
			var luogo = getValue("TORN_LOCGAR");
			if(punti != ""){
				showObj("rowNOMPUN_PCOGAR", true);
				showObj("rowTORN_LOCGAR", false);
				//setValue("TORN_LOCGAR", "");
			}else if(luogo!=""){
				showObj("rowTORN_LOCGAR", true);
				showObj("rowNOMPUN_PCOGAR", false);
				//setValue("TORN_PCOGAR", "");
				//setValue("NOMPUN_PCOGAR", "");
			}else{
				showObj("rowTORN_LOCGAR", false);
				showObj("rowNOMPUN_PCOGAR", false);
				//setValue("TORN_LOCGAR", "");
				//setValue("TORN_PCOGAR", "");
				//setValue("NOMPUN_PCOGAR", "");
			}
		
	}
	
	<c:if test="${isVisibileSezAperturaPlichi }">
		punto = getValue("TORN_PCOGAR");
		luogo = getValue("TORN_LOCGAR");
		valorizzaCampiPresso(punto,luogo,"PCOGAR_FIT");
		
		puntiContattoSezioneAperturaOfferte();
	</c:if>
	
	function cambiaTipoDocumentazione(tipoDoc){
		document.forms[0].metodo.value="apri";
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		bloccaRichiesteServer();
		document.forms[0].submit();
		
	}
	
	<c:if test="${tipoDoc ne 1 }">
		//document.forms[0].encoding="multipart/form-data";
		
		function scegliFile(indice) {
			var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
			var lunghezza_stringa=selezioneFile.length;
			var posizione_barra=selezioneFile.lastIndexOf("\\");
			var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			var tipoDoc="${tipoDoc}";
			if(tipoDoc=="6"){
				var formatoAllegati="${formatoAllegati}";
				if(!controlloTipoFile(nome,formatoAllegati)){
					alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: " + formatoAllegati);
					document.getElementById("selFile[" + indice + "]").value="";
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					return;
				}
			}
			if(nome.length>100){
				alert("Il nome del file non può superare i 100 caratteri!");
				document.getElementById("selFile[" + indice + "]").value="";
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
			}else{
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
				$("#spanRichiestaFirma_" + indice).show();
			}
		}
		
		function scegliFileDocumentale(param1,param2,indice) {
			var selezioneFile = param1;
			var lunghezza_stringa=selezioneFile.length;
			var posizione_barra=selezioneFile.lastIndexOf("\\");
			var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			var tipoDoc="${tipoDoc}";
			if(tipoDoc=="6"){
				var formatoAllegati="${formatoAllegati}";
				if(!controlloTipoFile(nome,formatoAllegati)){
					alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: " + formatoAllegati);
					document.getElementById("selFile[" + indice + "]").value="";
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
					return;
				}
			}
			if(nome.length>100){
				alert("Il nome del file non può superare i 100 caratteri!");
				document.getElementById("selFile[" + indice + "]").value="";
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
			}else{
				setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
				$("#spanRichiestaFirma_" + indice).show();
			}
		}
		
		
			
	</c:if>
	
	function popupRettificaTermini(iterga,codgar,gartel){
		var comando = "href=gare/commons/popup-rettificaTermini.jsp&codgar=" + codgar + "&ngara=" + codgar + "&iterga=" + iterga +"&pagina=Invito" + "&tipoGara=3" + "&gartel="+gartel;
	 	openPopUpCustom(comando, "rettificaTermini", 700, 350, "yes", "yes");
	}
	
	$("#rigaTabellaRettificaTerminiOfferta").hide();
	$("#tabellaRettificaTerminiOfferta").hide();
	
	$("#rigaTabellaRettificaTerminiApertura").hide();
	$("#tabellaRettificaTerminiApertura").hide();
	
	storicoOffertaCreato = false;
	storicoAperturaPartecipazioneCreato = false;
	
	function showDettRetifica(tipo){
		var codgar=getValue("GARE_CODGAR1");
		var contextPath = "${contextPath}";
		if(tipo==2 && storicoOffertaCreato==false){
			var visualizzaDataChiarimenti = "${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRICHCPO') }";
			var visualizzaRispostaChiarimenti = "${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRISPCPO') }";
			caricamentoStoricoRettificaTermini(codgar, tipo, contextPath,visualizzaDataChiarimenti,visualizzaRispostaChiarimenti);
			storicoOffertaCreato=true;
		}else if(tipo==3 && storicoAperturaPartecipazioneCreato==false){
			caricamentoStoricoRettificaTermini(codgar, tipo, contextPath,"false","false");
			storicoAperturaPartecipazioneCreato=true;
		}
				
		if(tipo==2){
			if ($('#tabellaRettificaTerminiOfferta').is(':visible')) {  
				$('#tabellaRettificaTerminiOfferta').hide();
				$("#rigaTabellaRettificaTerminiOfferta").hide();
				$('#aLinkVisualizzaDettaglioRettificaTerminiOfferta').text('Visualizza termini di gara precedenti alla rettifica');
			}else{
				$('#aLinkVisualizzaDettaglioRettificaTerminiOfferta').text('Nascondi termini di gara precedenti alla rettifica');
				$('#tabellaRettificaTerminiOfferta').show();
				$("#rigaTabellaRettificaTerminiOfferta").show();
		   }
		}else if(tipo==3){
			if ($('#tabellaRettificaTerminiApertura').is(':visible')) {  
				$('#tabellaRettificaTerminiApertura').hide();
				$("#rigaTabellaRettificaTerminiApertura").hide();
				$('#aLinkVisualizzaDettaglioRettificaTerminiApertura').text('Visualizza termini di gara precedenti alla rettifica');
			}else{
				$('#aLinkVisualizzaDettaglioRettificaTerminiApertura').text('Nascondi termini di gara precedenti alla rettifica');
				$('#tabellaRettificaTerminiApertura').show();
				$("#rigaTabellaRettificaTerminiApertura").show();
		   }
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

	<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and tipologiaGara eq "3" and  bustalotti eq "1" and tipoDoc eq 3}'>
		
		var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
		function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
			showNextElementoSchedaMultipla_Default(tipo, campi,visibilitaCampi);
			var indice = eval("lastId" + tipo + "Visualizzata");
			var busta = getValue("DOCUMGARA_BUSTA_" + indice);
			if (busta != '2' && busta != '3') {
				$("#rowDOCUMGARA_NGARA_" + indice).hide();
				//document.getElementById("rowDOCUMGARA_NGARA_" + indice).style.display = 'none';
				//document.forms[0].DOCUMGARA_NGARA_${param.contatore}.value = '';
			}
		}
		showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
	</c:if>
	
	<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and gestioneUrl eq "true" and (tipoDoc eq 1 or tipoDoc eq 6 or tipoDoc eq 10)}'>
		
		var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
		function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
			showNextElementoSchedaMultipla_Default(tipo, campi,visibilitaCampi);
			var indice = eval("lastId" + tipo + "Visualizzata");
			$("#rowDOCUMGARA_URLDOC_" + indice).hide();
			$('#DOCUMGARA_ALLMAIL_' + indice).attr('disabled',false);
		}
		showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
	</c:if>
	
	<c:if test="${tipoDoc eq 10 or tipoDoc eq 3}">
									
	$(function () {
		$('#jstree2').jstree({
			'plugins':["wholerow","search","ui"], 
			'core' : {
				"themes" : { "stripes" : true,
							 "icons" : false},
				'data' : ${dati}
					},
			"search": {
				'case_insensitive': true,
				'show_only_matches': true
				}
		});
		
		$('#radioTutte').click(function () {
			if ($(this).is(':checked')) {
				$('#jstree2').jstree(true).search('');
			}
		});
	});

	$("#jstree2").bind('ready.jstree', function(event, data) {
	});

	$('#jstree2').on('select_node.jstree', function (e, data) {
		data.instance.toggle_node(data.node);
	});

	$("#jstree2").bind("select_node.jstree", function (e, data) {
		 var href = data.node.a_attr.href;
		 document.location.href = href;
	});

	function cambiaTipoDocumentazione(tipoDoc){
		document.forms[0].metodo.value="apri";
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		bloccaRichiesteServer();
		document.forms[0].action += "&tipoDoc=" + tipoDoc;
		document.forms[0].submit();
	}

	function visualizzaDocumenti(codiceGara,tipoPubblicazione,gruppo){
	var href = contextPath + "/ApriPagina.do?href=gare/documgara/documenti-tipologia.jsp";
	formVisualizzaDocumenti.key.value = "TORN.CODGAR=T:" + codiceGara;
	formVisualizzaDocumenti.tipologiaDoc.value = tipoPubblicazione;
	formVisualizzaDocumenti.gruppo.value = gruppo;
	formVisualizzaDocumenti.submit();
	}
	
	function visualizzaDocumentiConcorrenti(codgar,busta,titoloBusta){
	var gestioneQuestionariPreq = $('#gestioneQuestionariPreq').val();
		var gestioneQuestionariAmm = $('#gestioneQuestionariAmm').val();
		var gestioneQuestionariTec = $('#gestioneQuestionariTec').val();
		var gestioneQuestionariEco = $('#gestioneQuestionariEco').val();
		var noModaleDocumentiQform = $('#noModaleDocumentiQform').val();
		if(noModaleDocumentiQform=="true"){
			if(gestioneQuestionariPreq=="INSQFORM")
				gestioneQuestionariPreq = "MODALE-INSQFORM";
			if(gestioneQuestionariAmm=="INSQFORM")
				gestioneQuestionariAmm = "MODALE-INSQFORM";
			if(gestioneQuestionariTec=="INSQFORM")
				gestioneQuestionariTec = "MODALE-INSQFORM";
			if(gestioneQuestionariEco=="INSQFORM")
				gestioneQuestionariEco = "MODALE-INSQFORM";
		}
		var autorizzatoModifiche = "${autorizzatoModifiche}";
		if((busta == 2 && gestioneQuestionariTec=="listaTEC") || (busta == 3 && gestioneQuestionariEco=="listaECO")){
			var href = contextPath + "/ApriPagina.do?href=gare/documgara/lista-lotti.jsp";
			formVisualizzaDocumenti.href.value="gare/documgara/lista-lotti.jsp";
			formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + codgar;
			formVisualizzaDocumenti.busta.value = busta;
			formVisualizzaDocumenti.titoloBusta.value = titoloBusta;
			formVisualizzaDocumenti.gruppo.value = 3;
			formVisualizzaDocumenti.submit();
		}else if(((busta == 4 && gestioneQuestionariPreq=="INSQFORM") || (busta == 1 && gestioneQuestionariAmm=="INSQFORM")) && autorizzatoModifiche != "2" ){
			apriModaleQform(codgar,busta, titoloBusta);
		}else if(((busta == 4 && gestioneQuestionariPreq=="MODALE-INSQFORM") || (busta == 1 && gestioneQuestionariAmm=="MODALE-INSQFORM") || (busta == 2 && gestioneQuestionariTec=="MODALE-INSQFORM") || (busta == 3 && gestioneQuestionariEco=="MODALE-INSQFORM")) && autorizzatoModifiche != "2" ){
			var href = contextPath + "/ApriPagina.do?href=gare/documgara/qform.jsp";
			formVisualizzaDocumenti.href.value="gare/documgara/qform.jsp";
			formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + codgar;
			formVisualizzaDocumenti.busta.value = busta;
			formVisualizzaDocumenti.titoloBusta.value = titoloBusta;
			formVisualizzaDocumenti.gruppo.value = 3;
			formVisualizzaDocumenti.modoQform.value="INSQFORM";
			formVisualizzaDocumenti.submit();
		}else if((busta == 4 && gestioneQuestionariPreq=="VISQFORM") || (busta == 1 && gestioneQuestionariAmm=="VISQFORM") || (busta == 2 && gestioneQuestionariTec=="VISQFORM") || (busta == 3 && gestioneQuestionariEco=="VISQFORM")){
			var href = contextPath + "/ApriPagina.do?href=gare/documgara/qform.jsp";
			formVisualizzaDocumenti.href.value="gare/documgara/qform.jsp";
			formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + codgar;
			formVisualizzaDocumenti.busta.value = busta;
			formVisualizzaDocumenti.titoloBusta.value = titoloBusta;
			formVisualizzaDocumenti.gruppo.value = 3;
			formVisualizzaDocumenti.modoQform.value="VISQFORM";
			formVisualizzaDocumenti.submit();
		}else {
			var href = contextPath + "/ApriPagina.do?href=gare/documgara/documenti-tipologia.jsp";
			formVisualizzaDocumenti.key.value = "TORN.CODGAR=T:" + codgar;
			formVisualizzaDocumenti.busta.value = busta;
			formVisualizzaDocumenti.titoloBusta.value = titoloBusta;
			formVisualizzaDocumenti.gruppo.value = 3;
			formVisualizzaDocumenti.submit();
		}
	}						
	</c:if>
	
</gene:javaScript>
		