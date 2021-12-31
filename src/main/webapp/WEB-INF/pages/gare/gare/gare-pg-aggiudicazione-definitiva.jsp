<%
/*
 * Created on: 12/11/2008
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

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:if test='${isGaraLottiConOffertaUnica eq "true" and not empty param.codiceGara}' >
	<c:set var="codiceGara" value='${param.codiceGara}' />
</c:if>
<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${codiceGara}' />

<c:set var="modelliPredispostiAttivoIncondizionato" value="1" scope="request" />

<c:set var="valoreMedia" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMediaScartiFunction",  pageContext, gene:getValCampo(key,"NGARA"))}'/>

<c:if test="${isGaraLottiConOffertaUnica eq 'true'}">
	<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction", pageContext, codiceGara)}' scope="request"/>
	<c:choose>
		<c:when test="${not empty param.bustalotti }">
			<c:set var="bustalotti" value="${param.bustalotti }"/>
		</c:when>
		<c:otherwise>
			<c:set var="bustalotti" value="${bustalotti }"/>
		</c:otherwise>
	</c:choose>
</c:if>

<c:choose>
	<c:when test="${isProceduraTelematica and isGaraLottiConOffertaUnica ne 'true'}">
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request"/>
	</c:when>
	<c:when test="${isProceduraTelematica and isGaraLottiConOffertaUnica eq 'true'}">
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, codiceGara)}' scope="request"/>
	</c:when>
</c:choose>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
		<c:set var="valoreChiaveRiservatezza" value="${codiceGara}"/>
	</c:when >
	<c:when test='${lottoDiGara eq "true"}'>
		<c:set var="valoreChiaveRiservatezza" value="${codiceGara}"/>
	</c:when >
	<c:otherwise>
		<c:set var="valoreChiaveRiservatezza" value="${gene:getValCampo(key,'NGARA')}"/>
	</c:otherwise>
</c:choose>

<c:set var="riservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, valoreChiaveRiservatezza, idconfi)}' scope="request"/>

<c:set var="isVecchiaOepv" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVecchiaOEPVFunction", pageContext, codiceGara)}' />
<c:set var="contieneFormato52" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.CheckContieneFormatoFunction', pageContext, '52')}" />


<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, numeroGara)}' />

<c:set var="fasgarStepgarOffEconomica" value='' />
<c:if test="${isGaraLottiConOffertaUnica ne 'true'}">
	<jsp:include page="fasiGara/defStepWizardFasiGara.jsp" />
		
	<c:set var="varTmp" value="${step7Wizard/10}" />
	<c:if test='${fn:endsWith(varTmp, ".0")}'>
		<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
	</c:if>
	<c:set var="fasgarStepgarOffEconomica" value='${varTmp }' />
</c:if>
<c:set var="temp" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GestioneAggiudicazioneProvvisoriaFunction",  pageContext, codiceGara,numeroGara,"",fasgarStepgarOffEconomica)}'/>

<c:set var="esisteODADefinito" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteODADefinitoFunction", pageContext, numeroGara)}' />

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />

<c:if test='${integrazioneWSERP eq "1"}'>
	<c:set var="presenzaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPPresenzaRdaFunction", pageContext, codiceGara, numeroGara, requestScope.tipoWSERP)}' />
	<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />
	<c:if test='${tipoWSERP eq "UGOVPA"}'>
		<c:set var="isDatiContratto" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetWSERPDatiContrattoFunction", pageContext, numeroGara,codiceGara)}' />
	</c:if>
	<c:choose>
        <c:when test='${tipoWSERP eq "SMEUP" || tipoWSERP eq "UGOVPA"}'>
        	<c:set var="comunicaEsitoTitle" value='Aggiorna RdA con i dati di aggiudicazione' />
        </c:when>
        <c:when test='${tipoWSERP eq "AVM"}'>
        	<c:set var="comunicaEsitoTitle" value='Invia aggiudicazione ad ERP' />
        </c:when>
        <c:when test='${(tipoWSERP eq "TPER")}'>
        	<c:if test='${!empty ditta}'>
        		<c:set var="nazione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNazioneDittaFunction", pageContext, ditta)}' />
        	</c:if>
            <c:set var="comunicaEsitoTitle" value='Invia aggiudicazione ad ERP' />
        </c:when>
        <c:when test='${tipoWSERP eq "CAV"}'>
        	<c:set var="comunicaEsitoTitle" value='Invia dati contratto ad ERP' />
        </c:when>
        <c:otherwise>
        <c:set var="tipoWSERP" value='' />
        </c:otherwise>
	</c:choose>
	<c:choose>        
        <c:when test='${tipoWSERP eq "AVM"}'>
        	<c:set var="comunicaEsitoMsg" value='Riferimento Rdo ERP' />
        </c:when>
        <c:when test='${tipoWSERP eq "CAV"}'>
        	<c:set var="comunicaEsitoMsg" value='Invio dati contratto ERP' />
        </c:when>
        <c:otherwise>
        </c:otherwise>
	</c:choose>
</c:if>

<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.AGGDEF.VisualizzaDettaglioPrezzi") and not empty ditta and (modlicg eq "5" or modlicg eq "14" or modlicg eq "16" or (modlicg eq "6" and (isVecchiaOepv or contieneFormato52))) }'>
				<c:set var="dettagliOffPrezziVisibile" value="true"/>
</c:if>				

<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAggiudicazioneDefinitiva">

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>

	<gene:redefineInsert name="schedaNuovo" />
	<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
		<gene:redefineInsert name="documentiAssociati" />
		<gene:redefineInsert name="noteAvvisi" />
		<gene:redefineInsert name="helpPagina" />
	</c:if>

	<gene:redefineInsert name="modelliPredisposti" >
		<tr>
			<c:choose>
        <c:when test='${isNavigazioneDisabilitata ne "1"}'>
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
	</gene:redefineInsert>
	
	<c:if test='${modo ne "MODIFICA" }'>
		<gene:redefineInsert name="addToAzioni" >
		<c:if test='${autorizzatoModifiche ne "2" and (gene:checkProtFunz(pageContext, "ALT", "SelezionaDittaAggDef") or gene:checkProtFunz(pageContext, "ALT", "AnnullaAggDef") or (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.VisualizzaDettaglioPrezzi") and not empty datiRiga.GARE_DITTA and (datiRiga.GARE_MODLICG eq "5" or datiRiga.GARE_MODLICG eq "14" or datiRiga.GARE_MODLICG eq "16")))}' >
		
			<c:if test='${gene:checkProtFunz(pageContext, "ALT", "SelezionaDittaAggDef") and (!isProceduraTelematica or (isProceduraTelematica and meruolo eq 1)) and esisteODADefinito ne "SI" and empty datiRiga.GARE_DITTA }'>
				<tr>
					<td class="vocemenulaterale">
						<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:aggiudicazioneDefinitiva();" title="Aggiudicazione definitiva" tabindex="1502"></c:if>
							Aggiudicazione definitiva
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
			<c:if test='${gene:checkProtFunz(pageContext, "ALT", "AnnullaAggDef") and (!isProceduraTelematica or (isProceduraTelematica and meruolo eq 1)) and esisteODADefinito ne "SI"}'>
				<c:if test='${!(integrazioneWSERP eq "1" && not empty datiRiga.GARE1_NUMRDO && requestScope.tipoWSERP ne "FNM")}'>
					<tr>
						<td class="vocemenulaterale">
							<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:annullaAggiudicazione('${key}');" title="Annulla aggiudicazione definitiva" tabindex="1504"></c:if>
								Annulla aggiudicazione definitiva
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
				</c:if>
			</c:if>
		</c:if>
		<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.SvincoloCauzioneProvvisoria") and not empty datiRiga.GARE_DITTA and isGaraLottiConOffertaUnica ne "true"}' >
			<tr>
				<td class="vocemenulaterale">
					<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:apriListaCauzioneDitte();"
					 title="Svincolo cauzione provvisoria" tabindex="1505"></c:if>
						Svincolo cauzione provvisoria
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>		
		
		<c:if test='${modo eq "VISUALIZZA" and gene:checkProtFunz(pageContext, "MOD","MOD") and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaGaraNonAggiud") and (bustalotti eq "1" || bustalotti eq "2")}'>
		      <td class="vocemenulaterale">
			      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
							<a href="javascript:impostaGaraNonAggiudicata('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','${datiRiga.GARE_ESINEG}','${datiRiga.GARE_DATNEG}','${datiRiga.GARE1_NPANNREVAGG}');" title="Imposta lotto non aggiudicato" tabindex="1506">
					</c:if>
					  Imposta lotto non aggiudicato
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				</td>
		</c:if>
		
		<c:if test='${autorizzatoModifiche ne "2" and (((tipoWSERP eq "SMEUP" || (tipoWSERP eq "UGOVPA" && empty datiRiga.GARE1_NUMRDO && isDatiContratto eq "true")|| (tipoWSERP eq "AVM" && empty datiRiga.GARE1_NUMRDO) || tipoWSERP eq "CAV" && empty datiRiga.GARE1_NUMRDO) and (presenzaRda eq "1" || presenzaRda eq "2")) || (tipoWSERP eq "TPER" && empty datiRiga.GARE1_NUMRDO && nazione eq "1"))}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:comunicaEsitoRdaInGara('${key}','${requestScope.tipoWSERP}');" title='${comunicaEsitoTitle}' tabindex="1507">
						${comunicaEsitoTitle}
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.AGGDEF.RettificaImportoAggiudizazione") and not empty datiRiga.GARE_DITTA and (datiRiga.TORN_ITERGA ne 6 or (datiRiga.TORN_ITERGA eq 6 and datiRiga.GARECONT_STATO <=2 ))}' >
			<c:if test='${!(integrazioneWSERP eq "1" and not empty datiRiga.GARE1_NUMRDO && requestScope.tipoWSERP ne "FNM")}'>
				<tr>
					<td class="vocemenulaterale">
						<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:rettificaImportoAgg();" title="Rettifica importo aggiudicazione" tabindex="1508"></c:if>
							Rettifica importo aggiudicazione
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>	
		</c:if>
		<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.AGGDEF.AnnullaRiservatezza") and riservatezzaAttiva eq "1"}' >
			<tr id="rigaFunzAnnullaRiservatezza">
				<td class="vocemenulaterale">
					<a href="javascript:apriPopupAnnullaRiservatezza('${valoreChiaveRiservatezza}','${idconfi}');" title='Annulla riservatezza su documentale' tabindex="1509">
						Annulla riservatezza su documentale
					</a>
				</td>
			</tr>
		</c:if>
		</gene:redefineInsert>
	</c:if>
	
	
	<gene:campoScheda campo="NGARA" visibile="false" />
	<gene:campoScheda campo="CODGAR1" visibile="false" />
	<gene:campoScheda campo="MODLICG" visibile="false" />
	<gene:campoScheda campo="CODRUP" entita="TORN" where="TORN.CODGAR = GARE.CODGAR1" visibile="false" />
	<gene:campoScheda campo="CLAVOR" visibile="false" />
	<gene:campoScheda campo="NUMERA" visibile="false" />
	<gene:campoScheda campo="OFFTEL" entita="TORN" where="TORN.CODGAR = GARE.CODGAR1" visibile="false" />
	<gene:campoScheda campo="IMPAPP" visibile="false" />
	<gene:campoScheda campo="ONPRGE" visibile="false" />
	<gene:campoScheda campo="IMPSIC" visibile="false" />
	<gene:campoScheda campo="IMPNRL" visibile="false" />
	<gene:campoScheda campo="SICINC" visibile="false" />
	<gene:campoScheda campo="ONSOGRIB" visibile="false" />
	<gene:campoScheda campo="ACCQUA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="IMPQUA" campoFittizio="true" definizione="F13.9" visibile="false"/>
	<gene:campoScheda campo="ESINEG" visibile="false" />
	<gene:campoScheda campo="ELENCOE" visibile="false" />
	<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="CENINT" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="OFFAUM" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="STATO" entita="GARECONT" where="GARE.NGARA = GARECONT.NGARA and GARECONT.NCONT=1" visibile="false"/>

	<gene:gruppoCampi idProtezioni="AGGPROV" >
		<gene:campoScheda visibile='${isGaraLottiConOffertaUnica ne "true"}'>
			<td colspan="2"><b>Chiusura proposta di aggiudicazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DVPROV" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="NVPROV" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="DLETTAGGPROV" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="NPLETTAGGPROVV" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="DSEDPUBEVA" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="DAVVPRVREQ" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="ESCLUSIONE">
		<gene:campoScheda visibile='${isGaraLottiConOffertaUnica ne "true"}'>
			<td colspan="2"><b>Esclusione in fase di apertura offerte economiche</b></td>
		</gene:campoScheda>
		<gene:campoScheda entita="GARESTATI" campo="NPLETTCOMESCLOFEC"  title="N.prot. lett. comun. esclusioni apertura off.economica" campoFittizio="true" definizione="T20;;;;NPROTLCE" value="${nprotEc}" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda entita="GARESTATI" campo="DPLETTCOMESCLOFEC"  title="Data lett. comunicazione esclusioni apertura off.economica" campoFittizio="true" definizione="D;;;;DATLCE" value="${dprotEc}" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="COMPROVAREQ" >
		<gene:campoScheda visibile='${isGaraLottiConOffertaUnica ne "true"}'>
			<td colspan="2"><b>Comprova requisiti di ordine generale</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DRICHDOCCR" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="NPROREQ" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="DTERMDOCCR" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
	</gene:gruppoCampi>
				
	<gene:gruppoCampi idProtezioni="CONTRATTPROC" >
		<gene:campoScheda visibile='${isGaraLottiConOffertaUnica ne "true"}'>
			<td colspan="2"><b>Controllo sugli atti della procedura di affidamento (art.33 Dlgs.50/2016)</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DATAPAGG" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
		<gene:campoScheda campo="NPRAPAGG" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile='${isGaraLottiConOffertaUnica ne "true"}'/>
	</gene:gruppoCampi>
				
	<gene:gruppoCampi idProtezioni="DITTAAGG" >
		<gene:campoScheda>
			<td colspan="2"><b>Aggiudicazione definitiva </b><span id="LinkVisualizzaDettaglio" style="display: none; float:right" ><a id="aLinkVisualizzaDettaglio" href="javascript:showDettRaggruppamento('');" class="link-generico"><span id="testoLinkVisualizzaDettaglio">Visualizza dettaglio consorziate esecutrici</span></a></span></td>
		</gene:campoScheda>
		<c:if test='${modo eq "VISUALIZZA"}' >
			<c:set var="link" value='javascript:archivioImpresaAggDef();' />
		</c:if>
		<c:choose>
			<c:when test='${modo eq "VISUALIZZA"}'>
				<c:set var="link" value='javascript:archivioImpresaAggDef();' />
				<gene:campoScheda campo="DITTA"  href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' visibile="${aqoper ne 2 }"/>
				<gene:campoScheda campo="NOMIMA"  href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' visibile="${aqoper ne 2 }"/>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${isGaraLottiConOffertaUnica eq 'true'}">
						<c:set var="whereArchivio" value="DITG.CODGAR5='${datiRiga.GARE_CODGAR1}' AND DITG.NGARA5 = DITG.CODGAR5 AND(DITG.INVOFF <> '2' or DITG.INVOFF is null) AND (DITG.AMMGAR <> '2' or DITG.AMMGAR is null)"/>
					</c:when>
					<c:otherwise>
						<c:set var="whereArchivio" value="DITG.NGARA5='${datiRiga.GARE_NGARA}' AND DITG.CODGAR5='${datiRiga.GARE_CODGAR1}' AND(DITG.INVOFF <> '2' or DITG.INVOFF is null) AND (DITG.AMMGAR <> '2' or DITG.AMMGAR is null)"/>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.DITTA") && gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.RIBAUO")}'>
						<gene:archivio titolo="Ditte concorrenti"
							lista="gare/ditg/ditg-lista-popup.jsp"
							scheda=""
							schedaPopUp=""
							campi="DITG.DITTAO;DITG.NOMIMO;DITG.RIBAUO;DITG.IMPOFF"
							chiave=""
							where="${whereArchivio}"
							inseribile="false"
							formName="formArchivioDitte" >
							<gene:campoScheda campo="DITTA"  visibile="${aqoper ne 2 }">
								<gene:checkCampoScheda funzione='checkEsineg()' obbligatorio="true" messaggio="Non e' possibile selezionare la ditta. La gara risulta conclusa con esito negativo" onsubmit="false"/>
							</gene:campoScheda>
							<gene:campoScheda campo="NOMIMA"  visibile="${aqoper ne 2 }">
								<gene:checkCampoScheda funzione='checkEsineg()' obbligatorio="true" messaggio="Non e' possibile selezionare la ditta. La gara risulta conclusa con esito negativo" onsubmit="false"/>
							</gene:campoScheda>
							<gene:campoScheda title="Ribasso" campo="RIBASSO_FIT" campoFittizio="true" visibile = "false" definizione="F13.9;0;;PRC"/>
							<gene:campoScheda title="Importo" campo="IMPORTO_FIT" campoFittizio="true" visibile = "false" definizione="F24.5;0;;"/>
						</gene:archivio>
					</c:when>
					<c:otherwise>
						<gene:archivio titolo="Ditte concorrenti"
							lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.DITTA"), "gare/ditg/ditg-lista-popup.jsp","")}'
							scheda=""
							schedaPopUp=""
							campi="DITG.DITTAO;DITG.NOMIMO"
							chiave=""
							where="${whereArchivio}"
							inseribile="false"
							formName="formArchivioDitte" >
							<gene:campoScheda campo="DITTA"  visibile="${aqoper ne 2 }"/>
							<gene:campoScheda campo="NOMIMA"  visibile="${aqoper ne 2 }"/>
						</gene:archivio>
					</c:otherwise>
				</c:choose>
				
				
			</c:otherwise>
		</c:choose>
		
		<gene:fnJavaScriptScheda funzione="setDattoa('#GARE_DITTA#')" elencocampi="GARE_DITTA" esegui="false" />
		
		<c:choose>
			<c:when test="${isGaraLottiConOffertaUnica eq 'true' }">
				<c:set var="gara" value="${codiceGara }"/>
			</c:when>
			<c:otherwise>
				<c:set var="gara" value="${numeroGara }"/>
			</c:otherwise>
		</c:choose>
		
		<c:set var="result" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoriDettaglioRaggruppanentoFunction", pageContext, gara, ditta, "")}' />
		
		<gene:campoScheda campoFittizio="true" nome="dettaglioRaggruppamento" >
			<td class="etichetta-dato">Consorziate esecutrici</td>
			<td>
				<table id="tabellaDettaglio" class="griglia" style="width: 99%; margin-left: 1%;">
				<tr>
					<td colspan="2" class="etichetta-dato" style="TEXT-ALIGN: center;BORDER-LEFT: #A0AABA 1px solid; width: 10%">Codice</td>
					<td colspan="2" class="etichetta-dato" style="TEXT-ALIGN: center;BORDER-LEFT: #A0AABA 1px solid; width: 90%">Ragione sociale</td>
				</tr>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
			<jsp:param name="entita" value='RAGDET'/>
			<jsp:param name="chiave" value='${gara}, ${datiRiga.GARE_DITTA }'/>
			<jsp:param name="nomeAttributoLista" value='datiRagdet' />
			<jsp:param name="idProtezioni" value="RAGDET" />
			<jsp:param name="sezioneListaVuota" value="true" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/ragdet/ragdet-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'RAGDET_CODIMP_', 'RAGDET_CODDIC_', 'RAGDET_NUMDIC_','RAGDET_NGARA_', 'IMPR_NOMEST_'"/>
		</jsp:include>
		<gene:campoScheda addTr="false">
				</table>
			</td>
		</gene:campoScheda>
		
		<gene:campoScheda campo="ULTDETLIC" entita="GARE1" where = "GARE1.NGARA = GARE.NGARA" visibile="false"/>
		<gene:campoScheda campo="RIBAGG" visibile="false" />
		<c:choose>
			<c:when test="${modlicg eq 17}">
				<gene:campoScheda title="Rialzo di aggiudicazione" campo="RIBASSO_AGGIUD" campoFittizio="true" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.RIBAGG")}' definizione="F13.9;0;;PRC" value="${datiRiga.GARE_RIBAGG}" visibile="${aqoper ne 2 }"/>		
			</c:when>
			<c:otherwise>
				<gene:campoScheda title="Ribasso di aggiudicazione" campo="RIBASSO_AGGIUD" campoFittizio="true" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.RIBAGG")}' definizione="F13.9;0;;PRC" value="${datiRiga.GARE_RIBAGG}" visibile="${aqoper ne 2 }"/>		
			</c:otherwise>
		</c:choose>
		<gene:campoScheda title="Punteggio di aggiudicazione" campo="PUNTEGGIO" campoFittizio="true" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.RIBAGG")}' definizione="F13.9;0" value="${datiRiga.GARE_RIBAGG}" visibile="${aqoper ne 2 }"/>
		<gene:campoScheda campo="RIBOEPV" visibile="${modlicg eq 6 && aqoper ne 2}" />
		<c:choose>
			<c:when test="${iaggiuinivalorizzato eq 'SI'}" >
				<gene:campoScheda campo="IAGGIU" visibile="${aqoper ne 2 }"/>
				<c:set var="dettaglioCampoIaggiu" value="5"/>
			</c:when>
			<c:when test="${datiRiga.GARE_SICINC eq 2 }" >
				<c:set var="campoRadice" value='GARE.IAGGIU' />
				<c:choose>
					<c:when test="${ empty ultdetlic}">
						<gene:campoScheda campo="IAGGIU" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC")}' visibile="${aqoper ne 2 }"/>
						<c:set var="dettaglioCampoIaggiu" value="1"/>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${ultdetlic eq 1 }">
								<gene:campoScheda campo="IAGGIU" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM")}' visibile="${aqoper ne 2 }"/>
								<c:set var="dettaglioCampoIaggiu" value="2"/>
							</c:when>
							<c:when test="${ultdetlic eq 2 }">
								<gene:campoScheda campo="IAGGIU" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
								<c:set var="dettaglioCampoIaggiu" value="3"/>
							</c:when>
							<c:otherwise>
								<gene:campoScheda campo="IAGGIU" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
								<c:set var="dettaglioCampoIaggiu" value="4"/>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:when>	
			<c:otherwise>
				<c:set var="campoRadice" value='GARE.IAGGIU' />
				<c:choose>
					<c:when test="${ empty ultdetlic}">
						<gene:campoScheda campo="IAGGIU" visibile="${aqoper ne 2 }"/>
						<c:set var="dettaglioCampoIaggiu" value="5"/>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="$ultdetlic eq 1 }">
								<gene:campoScheda campo="IAGGIU" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;DITG.IMPPERM")}' visibile="${aqoper ne 2 }"/>
								<c:set var="dettaglioCampoIaggiu" value="6"/>
							</c:when>
							<c:when test="${ultdetlic eq 2 }">
								<gene:campoScheda campo="IAGGIU" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
								<c:set var="dettaglioCampoIaggiu" value="7"/>
							</c:when>
							<c:otherwise>
								<gene:campoScheda campo="IAGGIU" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;DITG.IMPPERM;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
								<c:set var="dettaglioCampoIaggiu" value="8"/>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test="${modlicg eq 17}">
			<gene:campoScheda campo="RIBAGGINI" title="Rialzo di aggiudicazione precedente a rettifica" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${!empty datiRiga.GARE1_RIBAGGINI and aqoper ne 2}" modificabile="false" />
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="RIBAGGINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${!empty datiRiga.GARE1_RIBAGGINI and aqoper ne 2}" modificabile="false" />
		</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${iaggiuinivalorizzato eq 'SI' }">
				<c:set var="campoRadice" value='GARE1.IAGGIUINI' />
				<c:choose>
					<c:when test="${datiRiga.GARE_SICINC eq 2 }" >
						<c:choose>
							<c:when test="${ empty ultdetlic}">
								<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC")}' visibile="${aqoper ne 2 }"/>
								<c:set var="dettaglioCampoIaggiu" value="1"/>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${ultdetlic eq 1 }">
										<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM")}' visibile="${aqoper ne 2 }"/>
										<c:set var="dettaglioCampoIaggiu" value="2"/>
									</c:when>
									<c:when test="${ultdetlic eq 2 }">
										<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
										<c:set var="dettaglioCampoIaggiu" value="3"/>
									</c:when>
									<c:otherwise>
										<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
										<c:set var="dettaglioCampoIaggiu" value="4"/>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:when>	
					<c:otherwise>
						<c:set var="campoRadice" value='GARE1.IAGGIUINI' />
						<c:choose>
							<c:when test="${ empty ultdetlic}">
								<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${aqoper ne 2}" modificabile="false"/>
								<c:set var="dettaglioCampoIaggiu" value="5"/>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${ultdetlic eq 1 }">
										<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;DITG.IMPPERM")}' visibile="${aqoper ne 2 }"/>
										<c:set var="dettaglioCampoIaggiu" value="6"/>
									</c:when>
									<c:when test="${ultdetlic eq 2 }">
										<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
										<c:set var="dettaglioCampoIaggiu" value="7"/>
									</c:when>
									<c:otherwise>
										<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,campoRadice,"DITG.IMPOFF;DITG.IMPPERM;DITG.IMPCANO")}' visibile="${aqoper ne 2 }"/>
										<c:set var="dettaglioCampoIaggiu" value="8"/>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="IAGGIUINI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false" modificabile="false"/>
			</c:otherwise>
		</c:choose>
		
		<gene:campoScheda campo="IMPOFF" title="di cui importo offerto"  entita="DITG" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTA" visibile="${(datiRiga.GARE_SICINC eq 2 || !empty datiRiga.GARE1_ULTDETLIC) and aqoper ne 2}" modificabile="false"/>
		<gene:campoScheda campo="IMPSIC"  visibile="${datiRiga.GARE_SICINC eq 2 and aqoper ne 2  }" modificabile="false" />		
		<gene:campoScheda campo="IMPPERM" title="di cui importo per permuta" modificabile="false" entita="DITG" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTA" visibile="${ (datiRiga.GARE1_ULTDETLIC eq 1 or datiRiga.GARE1_ULTDETLIC eq 3) and aqoper ne 2}"/>
		<gene:campoScheda campo="IMPCANO" title="di cui importo per canone assistenza" modificabile="false" entita="DITG" where="DITG.NGARA5 = GARE.NGARA AND DITG.CODGAR5 = GARE.CODGAR1 AND DITG.DITTAO = GARE.DITTA" visibile="${ (datiRiga.GARE1_ULTDETLIC eq 2 or datiRiga.GARE1_ULTDETLIC eq 3) and aqoper ne 2 }"/>
		<gene:campoScheda campo="NOTDEFI"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" />
		<gene:campoScheda campo="NUMRDO" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" title="${comunicaEsitoMsg}" visibile='${integrazioneWSERP eq "1" && (tipoWSERP eq "AVM" || tipoWSERP eq "CAV")}' modificabile='false' />	
		<c:if test='${bustalotti eq "1" || bustalotti eq "2"}'>
			<gene:campoScheda campo="ESINEG" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false" title="Esito lotto non aggiudicato"/>
			<gene:campoScheda campo="DATNEG" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
			<gene:campoScheda campo="NPANNREVAGG" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
			<gene:campoScheda campo="NOTNEG" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"  visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
		</c:if>
		
		<gene:campoScheda campo="NGARA"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false"/>
		<gene:campoScheda campo="NUMRDO" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false"/>
		
		<gene:campoScheda campo="DVERAG" visibile='${isGaraLottiConOffertaUnica ne "true" and aqoper ne 2}'/>
		<gene:campoScheda campo="NPROVA" visibile='${isGaraLottiConOffertaUnica ne "true" and aqoper ne 2}'/>
	
		<gene:campoScheda campo="PRECUT" visibile="false"/>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.VerificaDocumenti") && modo ne "MODIFICA" && aqoper ne 2}' >
			<gene:campoScheda addTr="false">
				<tr id="rowLinkVerificaDoc">
					<td colspan="2" class="valore-dato" id="colonnaLinkVerificaDoc">
					<img width="16" height="16" title="Verifica documenti richiesti" alt="Verifica documenti richiesti" src="${pageContext.request.contextPath}/img/documentazione.png"/>
						<a href="javascript:verificaDocumentiRichiestiDaAgg('');" title="Verifica documenti richiesti" >
							 Verifica documenti richiesti
						</a>
					</td>
				</tr>
			</gene:campoScheda>
		</c:if>
		<c:if test='${dettagliOffPrezziVisibile and aqoper ne 2}'>
			<gene:campoScheda addTr="false">
				<tr id="rowLinkDettagliOffertaPrezzi">
					<td colspan="2" class="valore-dato" id="colonnaLinkDettagliOffertaPrezzi">
					<img width="16" height="16" title="Dettaglio offerta prezzi" alt="Dettaglio offerta prezzi" src="${pageContext.request.contextPath}/img/offertaprezzi.png"/>
						<a href="javascript:DettaglioOffertaPrezzi('${ditta}');" title="Dettaglio offerta prezzi" >
							 Dettaglio offerta prezzi
						</a>
					</td>
				</tr>
			</gene:campoScheda>
		</c:if>
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione='aggiornaRibagg("#RIBASSO_AGGIUD#")' elencocampi='RIBASSO_AGGIUD' esegui="false" />
	<gene:fnJavaScriptScheda funzione='aggiornaRibagg("#PUNTEGGIO#")' elencocampi='PUNTEGGIO' esegui="false" />
	<c:if test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.DITTA") && gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.RIBAUO")}'>
		<gene:fnJavaScriptScheda funzione='aggiornaRibaggDaArchivio("#RIBASSO_FIT#")' elencocampi='RIBASSO_FIT' esegui="false" />
		<gene:fnJavaScriptScheda funzione='aggiornaIaggiuDaArchivio("#IMPORTO_FIT#")' elencocampi='IMPORTO_FIT' esegui="false" />
		<gene:fnJavaScriptScheda funzione='sbiancaValoriIni()' elencocampi='GARE_DITTA' esegui="false" />
	</c:if>
	
<c:if test='${isGaraLottiConOffertaUnica ne "true"}'>
	<gene:gruppoCampi idProtezioni="ATTOAGG" >
	<gene:campoScheda addTr="false">
		<tr id="rowTITOLO_ATTO_AGGIUDICAZIONE">
			<td colspan="2"><b>Atto di aggiudicazione</b></td>
		</tr>
	</gene:campoScheda>
	<gene:campoScheda campo="TATTOA" />
	<gene:campoScheda campo="DATTOA" />
	<gene:campoScheda campo="NATTOA" />
	<gene:campoScheda campo="NPROAA" />
	<gene:campoScheda campo="DPROAA" />
	</gene:gruppoCampi>
	
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AGGDEF-scheda.AGGDEF.attiMultipli")}'>
	<c:set var="tipoRelazione" value="GARE" />
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneAttiGaraFunction" parametro='${tipoRelazione};${gene:getValCampo(key, "NGARA")}' />
		 <jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='GAREATTI'/>
			<jsp:param name="chiave" value='${tipoRelazione};${numeroGara}'/>
			<jsp:param name="nomeAttributoLista" value='attiGara' />
			<jsp:param name="idProtezioni" value="GAREATTIAGG" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gareatti/atti-aggiudicazione-gara.jsp"/>
			<jsp:param name="arrayCampi" value="'GAREATTI_ID_','GAREATTI_CODGAR_','GAREATTI_NGARA_','GAREATTI_NUMATTO_','GAREATTI_TIPOATTO_','GAREATTI_DATAATTO_','GAREATTI_NUMPROTATTO_','GAREATTI_DATAPROTATTO_'"/>
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="titoloSezione" value="Altro atto di aggiudicazione" />		
			<jsp:param name="titoloNuovaSezione" value="Nuovo atto di aggiudicazione" />
			<jsp:param name="descEntitaVociLink" value="atto di aggiudicazione" />
			<jsp:param name="msgRaggiuntoMax" value="gli atti di aggiudicazione"/>
			<jsp:param name="usaContatoreLista" value="true"/>
		 </jsp:include>
	</c:if>
	<gene:gruppoCampi idProtezioni="COMDITTE" >
		<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.GAROFF")}'>
			<c:set var="dicituraCauzione" value="e svincolo cauzione provvisoria"/>
		</c:if>
		<gene:campoScheda>
			<td colspan="2"><b>Comunicazione alle ditte dell'aggiudicazione definitiva ${dicituraCauzione}</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DCOMAG" />
		<gene:campoScheda campo="NCOMAG" />
		<gene:campoScheda campo="DCOMNG" />
		<gene:campoScheda campo="NCOMNG"/>
		<gene:campoScheda campo="DCOMSVIP" entita="GARE1" where="GARE1.NGARA = GARE.NGARA"/>
		<gene:campoScheda campo="NCOMSVIP" entita="GARE1" where="GARE1.NGARA = GARE.NGARA"/>
	</gene:gruppoCampi>
	
	
</c:if>	
	
	<gene:gruppoCampi idProtezioni="CAUZDEF" visibile="${aqoper ne 2 }">
		<gene:campoScheda nome="CAUZDEF">
			<td colspan="2"><b>Cauzione definitiva</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="RIDISO" />
		<gene:campoScheda campo="IMPGAR"  >
			<gene:calcoloCampoScheda funzione='calcolaIMPGAR()' elencocampi='GARE_RIDISO'/>
		</gene:campoScheda>
	</gene:gruppoCampi>
	
<c:if test='${isGaraLottiConOffertaUnica ne "true"}'>	
			
	<gene:gruppoCampi idProtezioni="COMUNOSSER">
		<gene:campoScheda>
			<td colspan="2"><b>Comunicazioni osservatorio</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DCOMAGGSA" />
	</gene:gruppoCampi>
</c:if>

	<gene:campoScheda campo="TIPIMP" entita="IMPR" where="IMPR.CODIMP = GARE.DITTA" visibile="false"/>
	
	<c:if test="${numeroDitteAggiudicatarie > 0 }">
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DITGAQ'/>
			<jsp:param name="chiave" value='${numeroGara};${modlicg}:${impsic }'/>
			<jsp:param name="nomeAttributoLista" value='listaDitteAggiudicatarie' />
			<jsp:param name="idProtezioni" value="DITGAQ" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/ditgaq/ditte-aggiudicatarie.jsp"/>
			<jsp:param name="arrayCampi" value="'DITGAQ_ID_', 'DITGAQ_NGARA_', 'DITGAQ_DITTAO_','IMPR_NOMIMP_','DITGAQ_RIBAGG_', 'DITG_IMPOFF_', 'GARE_IMPSIC_','DITG_IMPPERM_', 'DITG_IMPCANO_', 'DITG_RICSUB_', 'DITGAQ_PUNTOT_', 'DITGAQ_IAGGIU_', 'DITGAQ_RIDISO_','DITGAQ_IMPGAR_','DITGAQ_RIBAGGINI_','DITGAQ_IAGGIUINI_'"/>		
			<jsp:param name="titoloSezione" value="Ditta aggiudicataria" />
			<jsp:param name="titoloNuovaSezione" value="Nuova ditta aggiudicataria" />
			<jsp:param name="descEntitaVociLink" value="ditta aggiudicataria" />
			<jsp:param name="msgRaggiuntoMax" value="e ditte aggiudicatarie"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="sicinc" value="${datiRiga.GARE_SICINC }"/>
			<jsp:param name="ultdetlic" value="${datiRiga.GARE1_ULTDETLIC}"/>
			<jsp:param name="dettaglioIAGGIU" value="${dettaglioCampoIaggiu }"/>
			<jsp:param name="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica }"/>
			<jsp:param name="codiceGara" value="${codiceGara }"/>
			<jsp:param name="dettagliOffPrezziVisibile" value="${dettagliOffPrezziVisibile }"/>
		</jsp:include>
	</c:if>
	
	<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
	<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
	<input type="hidden" name=IMPQUA id="IMPQUA" value="" />
	<input type="hidden" name=bustalotti id="bustalotti" value="${bustalotti}" />
	<c:if test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.DITTA") and gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.NOMIMA")}'>
		<input type="hidden" name=profiloSemplificato id="profiloSemplificato" value="1" />
	</c:if>
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${modo eq "MODIFICA"}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</c:when>
				<c:otherwise>
					<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>	
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco lotti' title='Torna a elenco lotti' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
					</c:if>
					<c:if test='${(autorizzatoModifiche ne "2") and gene:checkProtFunz(pageContext, "ALT", "SelezionaDittaAggDef") and (!isProceduraTelematica or (isProceduraTelematica and meruolo eq 1)) and esisteODADefinito ne "SI" and empty datiRiga.GARE_DITTA}' >
						<INPUT type="button" class="bottone-azione" value='Aggiudicazione definitiva' title='Aggiudicazione definitiva' onclick="javascript:aggiudicazioneDefinitiva();" id="btnAggiudicazioneDef">
					</c:if>
					<c:if test='${(autorizzatoModifiche ne "2") and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>

	<gene:javaScript>
	
	<c:if test="${aqoper ne 2 }">
		if(getValue("GARE_MODLICG") != "6")
			$( "#GARE1_RIBAGGINIview" ).after( " %&nbsp;" );
	</c:if>
	
	if(getValue("GARE_MODLICG") == "6"){
		showObj("rowRIBASSO_AGGIUD", false);
	} else {
		showObj("rowPUNTEGGIO", false);
	}
	
	//Si redefinisce la funzione archivioLista 
	function archivioListaCustom(nomeArchivio) {		
		if (nomeArchivio == "formArchivioDitte") {
			if (!checkEsineg()) {
				alert("Non e' possibile selezionare la ditta. La gara risulta conclusa con esito negativo");
				return;
			}
		}
		archivioListaDefault(nomeArchivio);	 
	}
	var archivioListaDefault = archivioLista;
	var archivioLista = archivioListaCustom;
	
	function checkEsineg() {
		var esineg = getValue("GARE_ESINEG");
		if (esineg != null && esineg != ""){
			return false;
		}
		return true;
	} 
	
	function archivioImpresaAggDef(){
		var codiceImpresa = getValue("GARE_DITTA");
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
	}
	
	function aggiudicazioneDefinitiva(){
		var ngara = getValue("GARE_NGARA");
		var href = "href=gare/gare/gare-popup-aggiudicazione-definitiva.jsp&modoRichiamo=AGGIUDICAZIONE&ngara="+ngara;
	<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
		href += "&isGaraLottiConOffertaUnica=true";
	</c:if>
		openPopUpCustom(href, "aggiudicazioneDefinitiva", 700, 500, "yes","yes");
	}
	
	var ridisoPrec = getValue("GARE_RIDISO");
	
	//Se RIDISO = 1 si dimezza il valore di IMPGAR
	//Se RIDISO = 2 si raddoppia il valore di IMPGAR 
	function calcolaIMPGAR(){
		var newRidiso = getValue("GARE_RIDISO");
		var oldRidiso = ridisoPrec;
		var importoCauzione = toVal(getValue("GARE_IMPGAR"));
		
		if(importoCauzione == null ||importoCauzione=="") importoCauzione=0;
		importoCauzione=parseFloat(importoCauzione);
		
		if (newRidiso == null || newRidiso=="") newRidiso=2;
		if (oldRidiso == null || oldRidiso=="") oldRidiso=2;
		
		if (newRidiso != oldRidiso) {
			if (newRidiso==1) {
				importoCauzione = importoCauzione / 2;
			}else{ 
				importoCauzione = importoCauzione * 2;
			}
		}
		ridisoPrec = newRidiso;
		return toMoney(importoCauzione);
	}
	
	<c:if test="${aqoper eq 2 }">
		$(document).ready(function (){
			ridisoPrec = [] ;
			var numeroDitteAggiudicatarie = ${numeroDitteAggiudicatarie};
			if(numeroDitteAggiudicatarie>0){
				for(var i=1; i <= numeroDitteAggiudicatarie; i++){
						ridisoPrec[i] = getValue("DITGAQ_RIDISO_" + i);
				}		
			}
		});
		
		function calcolaIMPGAR(indice){
			var newRidiso = getValue("DITGAQ_RIDISO_" + indice);
			var oldRidiso = ridisoPrec[indice];
			var importoCauzione = toVal(getValue("DITGAQ_IMPGAR_" + indice));
			
			if(importoCauzione == null ||importoCauzione=="") importoCauzione=0;
			importoCauzione=parseFloat(importoCauzione);
			
			if (newRidiso == null || newRidiso=="") newRidiso=2;
			if (oldRidiso == null || oldRidiso=="") oldRidiso=2;
			
			if (newRidiso != oldRidiso) {
				if (newRidiso==1) {
					importoCauzione = importoCauzione / 2;
				}else{ 
					importoCauzione = importoCauzione * 2;
				}
			}
			ridisoPrec[indice] = newRidiso;
			return toMoney(importoCauzione);
		}
		
	</c:if>
	
	function DettaglioOffertaPrezzi(ditta){
		var ngara = "";
		var isGaraLottiConOffertaUnica="";
		var bustalotti ="${bustalotti}";
		if(bustalotti==2){
			ngara = "${codiceGara}";
			isGaraLottiConOffertaUnica="true";
		}else{
			ngara = getValue("GARE_NGARA");
			isGaraLottiConOffertaUnica="false";
		}
		var codiceGara = "${codiceGara}";
		var chiave = "DITG.CODGAR5=T:" + codiceGara + ";";
		chiave += "DITG.DITTAO=T:" + ditta + ";";
		chiave += "DITG.NGARA5=T:" + ngara;
		//alert(chiave);
		var href = contextPath + "/ApriPagina.do?href=gare/v_gcap_dpre/v_gcap_dpre-lista.jsp";
		href += "&key="+chiave;
		var offtel = getValue("TORN_OFFTEL");
		href += "&offtel="+offtel;
		href += "&modlicg=" + getValue("GARE_MODLICG");
		href += "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica;
		href += "&daAgg=SI";
		href += "&" + csrfToken;
		document.location.href = href;
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
			
	
	
	function aggiornaRibagg(ribasso){
		setValue("GARE_RIBAGG",ribasso);
		
	}
	
	function aggiornaRibaggDaArchivio(ribasso){
		//setValue("GARE_RIBAGG",ribasso);
		setValue("RIBASSO_AGGIUD",ribasso);
		
	}
	
	function aggiornaIaggiuDaArchivio(importo){
		if(importo !=null && importo !=0)
			importo = round(importo,2);
		setValue("GARE_IAGGIU",importo);
		var importoGaranzia = 0;
		var ribauo = getValue("RIBASSO_AGGIUD");
		
		var accqua = getValue("TORN_ACCQUA");
		if(accqua=='1')
			setValue("IMPQUA", getValue("GARE_IMPAPP")) ;
		
		if(importo >0){
			if(ribauo == null || ribauo == ""){
				var impapp = getValue("GARE_IMPAPP");
				var onprge = getValue("GARE_ONPRGE");
				var impsic = getValue("GARE_IMPSIC");
				var impnrl = getValue("GARE_IMPNRL");
				var sicinc = getValue("GARE_SICINC");
				var onsogrib = getValue("GARE_ONSOGRIB");
				ribauo = calcolaRIBAUO(impapp,onprge,impsic,impnrl,sicinc,importo,onsogrib);
				ribauo = parseFloat(ribauo);
			}
			var percauz = calcoloPercentualeCauzione(ribauo);
			var importoTemp = importo;
			if(accqua=='1'){
				importoTemp = getValue("GARE_IMPAPP");
				if(importoTemp !=null && importoTemp !=0)
					importoTemp = round(importoTemp,2);
			}
			importoGaranzia = round((importoTemp * parseFloat(percauz)) / 100, 2);
		}
		
		var ridiso = getValue("GARE_RIDISO");
		
		if (ridiso==1) {
			importoGaranzia = importoGaranzia / 2;
		}
		setValue("GARE_IMPGAR",toMoney(importoGaranzia));
		
	}
	
		
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
		redefineLabels();
		redefineTooltips();
		redefineTitles();
	</c:if>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>
	
	<c:choose>
		<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate") && modo eq "VISUALIZZA" && aqoper ne 2}'>
			var tipoImpresa = getValue("IMPR_TIPIMP");
			if(tipoImpresa==2 || tipoImpresa==11){
									
				$("#LinkVisualizzaDettaglio").show();
				
				//All'apertura della pagina si deve nascondere il dettaglio delle consorziate esecutrici
				$('#rowdettaglioRaggruppamento').hide();
						
			}else{
				$('#rowdettaglioRaggruppamento').hide();
			}
		</c:when>
		<c:otherwise>
			$('#rowdettaglioRaggruppamento').hide();
		</c:otherwise>
	</c:choose>
	
	function gestioneDettaglioConsorzio(indice){
		$("#LinkVisualizzaDettaglio" + indice).show();
				
		//All'apertura della pagina si deve nascondere il dettaglio delle consorziate esecutrici
		$('#rowdettaglioRaggruppamento' + indice).hide();
		
		
	}
	
	//Funzione attivata dal link Visualizza dettaglio raggruppamento per nascondere e visualizzare il dettaglio	
	function showDettRaggruppamento(indice){
		if($('#rowdettaglioRaggruppamento' + indice).is(':hidden')){
			$('#rowdettaglioRaggruppamento' + indice).show();
			$("#testoLinkVisualizzaDettaglio" + indice).text('Nascondi dettaglio consorziate esecutrici');
			$('#aLinkVisualizzaDettaglio' + indice).attr('title', 'Nascondi dettaglio consorziate esecutrici');
		}else{
			$('#rowdettaglioRaggruppamento' + indice).hide();
			$("#testoLinkVisualizzaDettaglio" + indice).text('Visualizza dettaglio consorziate esecutrici');
			$('#aLinkVisualizzaDettaglio' + indice).attr('title', 'Visualizza dettaglio consorziate esecutrici');
		}
	}
		
	function archivioImpresa(codiceImpresa){
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
	}
	
	function apriListaCauzioneDitte(){
		var codgar = getValue("GARE_CODGAR1");
		var ngara = getValue("GARE_NGARA");
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/lista-cauzioneDitte.jsp";
		href += "&codgar=" + codgar;
		href += "&ngara=" + ngara;
		document.location.href = href;
	}	
	
	
		var ultdelic = getValue("GARE1_ULTDETLIC");
		if(ultdelic!= null && ultdelic!=""){
				 		
	 		function gestioneIMPPER(){
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
	 		
	 		//Si forza la visualizzazione di un valore negativo in IMPPERM
	 		<c:choose>
		 		<c:when test="${iaggiuinivalorizzato eq 'SI' }">
		 			var showDettG1IAGGIUINI_Default = showDettG1IAGGIUINI;
			 		function showDettG1IAGGIUINI_Custom(){
				 		showDettG1IAGGIUINI_Default();
				 		gestioneIMPPER();
				 	}
				 	showDettG1IAGGIUINI =   showDettG1IAGGIUINI_Custom;
			 	</c:when>
			 	<c:otherwise>
			 		var showDettIAGGIU_Default = showDettIAGGIU;
			 		function showDettIAGGIU_Custom(){
				 		showDettIAGGIU_Default();
				 		gestioneIMPPER();
				 	}
				 	showDettIAGGIU =   showDettIAGGIU_Custom;
			 	</c:otherwise>
		 	</c:choose>
		}
	
	
	<c:if test='${bustalotti eq "1" || bustalotti eq "2"}'>
		function impostaGaraNonAggiudicata(ngara,codgar1,esineg,datneg,npannrevagg){
			var href="href=gare/commons/popup-ImpostaGaraNonAggiudicata.jsp&ngara=" + ngara + "&codgar1=" + codgar1 + "&esineg=" + esineg + "&datneg=" + datneg + "&npannrevagg=" + npannrevagg;
			href+="&isLottoOffUnica=Si";
			openPopUpCustom(href, "impostaGaraNonAggiudicata", 700, 400, "yes", "yes");
		}
	</c:if>
	
	function annullaAggiudicazione(ngara){
		var isGaraLottiConOffertaUnica = "${isGaraLottiConOffertaUnica }";
		var href="href=gare/gare/gare-popup-annulla-aggiudicazioneDefinitiva.jsp&ngara=" + ngara + "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica;
		openPopUpCustom(href, "annullaCalcoloAgg", 700, 350, "yes","yes");
	}
	
	function setDattoa(ditta){
		var dattoa=getValue("GARE_DATTOA");
		if((dattoa==null || dattoa == "") && ditta!=null && ditta!=""){
			var data = new Date();
			var g = data.getDate();
			var m = data.getMonth() + 1;
			var a = data.getFullYear();
			if(g<10)
				g = "0" + g;
			if(m<10)
				m = "0" + m;
			var dataString = g + "/" + m + "/" + a;
			setValue("GARE_DATTOA",dataString);
		}
	}
	
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.VerificaDocumenti") && modo ne "MODIFICA" && aqoper ne 2}' >
		var ditta = getValue("GARE_DITTA");
		if(ditta==null || ditta == "")
			showObj("rowLinkVerificaDoc", false);
	</c:if>
	
	<c:if test='${dettagliOffPrezziVisibile eq "true" && modo ne "MODIFICA" && aqoper ne 2}' >
		var ditta = getValue("GARE_DITTA");
		if(ditta==null || ditta == "")
			showObj("rowLinkDettagliOffertaPrezzi", false);
	</c:if>
	
	function verificaDocumentiRichiestiDaAgg(ditta){
			var codgar = getValue("GARE_CODGAR1");
			var ngara = getValue("GARE_NGARA");
			if(ditta == "" || ditta == null){
				var ditta = getValue("GARE_DITTA");
			}
			var chiave = "DITG.CODGAR5=T:" + codgar + ";DITG.DITTAO=T:" + ditta + ";DITG.NGARA5=T:" + ngara;
			setFaseCalcolata("8");
			verificaDocumentiRichiesti(chiave,'VERIFICA','1',"false","${autorizzatoModifiche}");
		}
		
	//Per visualizzare l'icona per la veridica dei documenti allineato al margine sinistro.
	$("#colonnaLinkVerificaDoc").css("padding-left","0px");
	$("#colonnaLinkDettagliOffertaPrezzi").css("padding-left","0px");
			
	function comunicaEsitoRdaInGara(ngara,tipoWSERP){
		var isGaraLottiConOffertaUnica = "${isGaraLottiConOffertaUnica }";
		var href="href=gare/gare/gare-popup-comunicaEsitoRda.jsp&ngara=" + ngara + "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica+ "&tipoWSERP=" + tipoWSERP;
		openPopUpCustom(href, "comunicaEsitoRda", 700, 600, "yes","yes");
	}
	
	function rettificaImportoAgg(){
		var codgar = getValue("GARE_CODGAR1");
		var ngara = getValue("GARE_NGARA");
		<c:choose>
			<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
				var isOfferteUnica="1";
			</c:when>
			<c:otherwise>
				var isOfferteUnica="2";
			</c:otherwise>
		</c:choose>
		var aqoper="${aqoper }";
		var modlicg="${modlicg }";
		var impsic = "${impsic }";
		var dettaglioCampoIaggiu = "${dettaglioCampoIaggiu }";
		var cauzVis="true";
		if(!document.getElementById("rowCAUZDEF"))
			cauzVis="false";
		var href = "href=gare/gare/popupRettificaImportoAggiudicazione.jsp?codgar=" + codgar+ "&ngara=" + ngara + "&isOfferteUnica="+isOfferteUnica;
		href+="&aqoper=" + aqoper + "&modlicg=" + modlicg + "&impsic="+ impsic + "&dettaglioCampoIaggiu=" + dettaglioCampoIaggiu + "&cauzVis=" + cauzVis;
		openPopUpCustom(href, "rettificaImportoAggiudicazione", 800, 500, "yes", "yes");
	}
	
	function sbiancaValoriIni(){
		$("#GARE1_RIBAGGINIview").text("");
		$("#GARE1_IAGGIUINIview").text("");
	}
	</gene:javaScript>
</gene:formScheda>