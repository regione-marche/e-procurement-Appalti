<%
/*
 * Created on: 13/11/2006
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "PERI")}' />
<c:if test="${not empty filtroLivelloUtente}" >
	<c:set var="filtroLivelloUtente" value="and ${fn:replace(filtroLivelloUtente, 'PERI.CODLAV', 'APPA.CODLAV')}" />
</c:if>


<c:set var="correttiviDefault" value="${gene:callFunction('it.eldasoft.sil.pg.tags.funzioni.GetCorrettiviDefaultFunction', pageContext)}" />
<c:set var="correttivoFornitureServizi" value="${fn:split(correttiviDefault, '#')[1]}"/>
<c:set var="integrazioneProgrammazione" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneProgrammazioneFunction", pageContext)}'/>

<c:set var="propertyCig" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  "it.eldasoft.inviodaticig.ws.url")}' scope="request"/>
<c:if test="${! empty propertyCig}">
	<c:set var="isCigAbilitato" value='1' scope="request"/>
</c:if>	

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="codiceGaraRda" value='${codiceGara}' />

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "TORN", "CODGAR")}'/>

<c:set var="esisteGaraOLIAMMF" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction", pageContext, gene:getValCampo(param.keyParent, "CODGAR"))}'/>

<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction",  pageContext,gene:getValCampo(param.keyParent, "CODGAR"))}' />
<c:set var="garaQformOfferte" value="${offtel eq '3'}"/>

<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO","false")}' />
<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"ESITO","false")}' />
<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' />

<c:choose>
	<c:when test='${(isProceduraTelematica || applicareBloccoPubblicazioneGareNonTelematiche eq "1") && (bloccoPubblicazionePortaleEsito eq "TRUE" || bloccoPubblicazionePortaleBando eq "TRUE")}'>
		<c:set var="modificaLabel" value='Modifica dopo pubblicazione' />
		<c:set var="bloccoModificaPubblicazione" value='TRUE' />
		<c:set var="bloccoModificatiDati" value='true' />
		<c:set var="campiModificabili" value="true"/>		
	</c:when>
	<c:otherwise>
		<c:set var="modificaLabel" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' />
		<c:set var="bloccoModificaPubblicazione" value='FALSE' />
		<c:set var="bloccoModificatiDati" value='false' />
		<c:set var="campiModificabili" value="true"/>
	</c:otherwise>
</c:choose>

<c:set var="integrazioneptendpoint" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazionePT.url")}'/>
<c:if test="${!empty integrazioneptendpoint && modo eq 'VISUALIZZA'}">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.integrazionept.js"></script>
</c:if>
<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
	${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.GetListaScaglioniAnacFunction', pageContext, 'A1z01','listaScaglioniA1z01')}
</c:if>


<c:set var="esisteIntegrazioneLavori" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneLavoriFunction", pageContext)}' />
<c:if test="${esisteIntegrazioneLavori eq 'TRUE'}">
	<c:set var="modcont" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetModcontFunction", pageContext, gene:getValCampo(param.keyParent, "CODGAR"))}' />
</c:if>

<c:if test='${modo eq "NUOVO"}'>
	<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaGaraFunction", pageContext, gene:getValCampo(param.keyParent,"CODGAR"))}'/>
</c:if>

<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO"}'>
	<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codiceGara)}' scope="request"/>
	<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>
	<c:choose>
		<c:when test='${! empty gene:getValCampo(param.key, "CODGAR")}' >
			<c:set var="codiceGara" value="${gene:getValCampo(param.key,'CODGAR')}"/>
		</c:when>
		<c:otherwise>
			<c:set var="codiceGara" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>
		</c:otherwise>
	</c:choose>
	<c:set var="integrazioneWSDMDocumentale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, codiceGara,idconfi)}' />
	<c:if test='${integrazioneWSDMDocumentale eq "1"}'>
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
		<c:if test="${tipoWSDM eq 'JIRIDE'}">
			<c:set var="isFascicoloDocumentaleCommessa" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", accessoFascicoloDocumentaleCommessa, idconfi)}'/>
		</c:if>
	</c:if>
</c:if>

<c:set var="propertyCig" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsCig)}' scope="request"/>
<c:if test="${! empty propertyCig}">
	<c:set var="isCigAbilitato" value='1' scope="request"/>
</c:if>	
<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG") and not (isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG"))}'>
	<c:set var="esisteAnagraficaSimog" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteAnagraficaSimogFunction", pageContext, gene:getValCampo(param.keyParent, "CODGAR"))}'/>
</c:if>

<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.IntegrazioneProgrammazione") && integrazioneProgrammazione eq "1"}'>
			<c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "rdaCollegate",codiceGaraRda,null,null)}' />
			<c:set var="conteggiolottoRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "rdaCollegate",codiceGaraRda,gene:getValCampo(key, "NGARA"),null)}' />
</c:if>
			
<%/* Dati generali della gara */%>
<gene:formScheda entita="GARE" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDatiGenerali" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE">

<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />

<c:set var="pathDocAssociatiPL" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathDocumentiAssociatiPL")}' />
<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>

<c:choose>
	<c:when test="${documentiAssociatiDB eq '1' }">
		<c:set var="controlloPahDocAssociatiPl" value="true"/>
	</c:when>
	<c:otherwise>
		<c:set var="controlloPahDocAssociatiPl" value="${!empty pathDocAssociatiPL && pathDocAssociatiPL ne '' }"/>
	</c:otherwise>
</c:choose>
	<gene:redefineInsert name="addToDocumenti" >
		<c:if test='${modo eq "VISUALIZZA" && !empty datiRiga.GARE_NUMERA && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.VisualizzaDocAppaltoDaGare") 
			&& gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NUMERA") && controlloPahDocAssociatiPl}'>
			<tr>
				<td class="vocemenulaterale">
					<a href='javascript:visualizzaDocumentiAssociatiAppalto();' title="Documenti associati dell'appalto" tabindex="1522">
						Documenti associati dell'appalto
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and isFascicoloDocumentaleCommessa eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FascicoloDocumentaleCommessa") }'>
			<c:if test="${!empty datiRiga.GARE_CLAVOR }">
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:impostaDatiDocumentaleCommessa(1);" title="Fascicolo documentale commessa" tabindex="1518">
								Fascicolo documentale commessa
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Fascicolo documentale commessa
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
			</c:if>
			<c:if test="${!empty datiRiga.GARE_NUMERA }">
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:impostaDatiDocumentaleCommessa(2);" title="Fascicolo documentale appalto" tabindex="1519">
								Fascicolo documentale appalto
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Fascicolo documentale appalto
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
			</c:if>
		</c:if>
	</gene:redefineInsert>

<%/* Viene riportato tipoAppalto, in modo tale che, in caso di errorie riapertura della pagina, 
     venga riaperta considerando il valore definito inizialmente per la prima apertura della pagina */%>
<input type="hidden" name="tipoAppalto" id="tipoAppalto" value="${param.tipoAppalto}"/>

<input type="hidden" name="LOTTO_OFFERTAUNICA" id="LOTTO_OFFERTAUNICA" value="SI"/>

	
	<c:if test='${modoAperturaScheda eq "NUOVO" }'>
		<% // campi non presenti nella pagina e da includere solo nel caso di inserimento lotto di gara, in modo da inizializzarli nel DB %>
		<gene:campoScheda campo="NAVVIGG" visibile="false" defaultValue="${requestScope.initNAVVIG}"/>
		<gene:campoScheda campo="DAVVIGG" visibile="false" defaultValue="${requestScope.initDAVVIG}"/>
		<gene:campoScheda campo="DPUBAVG" visibile="false" defaultValue="${requestScope.initDPUBAV}"/>
		<gene:campoScheda campo="DFPUBAG" visibile="false" defaultValue="${requestScope.initDFPUBA}"/>
		<gene:campoScheda campo="DIBANDG" visibile="false" defaultValue="${requestScope.initDIBAND}"/>
		
		<gene:campoScheda campo="TATTOG" visibile="false" defaultValue="${requestScope.initTATTOT}"/>
		<gene:campoScheda campo="DATTOG" visibile="false" defaultValue="${ requestScope.initDATTOT}"/> 
		<gene:campoScheda campo="NATTOG" visibile="false" defaultValue="${requestScope.initNATTOT}"/>
		<gene:campoScheda campo="NPROAG" visibile="false" defaultValue="${ requestScope.initNPROAT}"/>
	</c:if>

	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	
	
	<gene:campoScheda visibile="${ campiModificabili eq 'false' and modo eq 'MODIFICA'}">
		<td colspan="2" style="color:#0000FF">
		<br><b>ATTENZIONE:</b>&nbsp;
		Parte dei dati sono in sola consultazione perché la gara è pubblicata su portale Appalti<br>&nbsp;
		</td>	
	</gene:campoScheda>
	
	<gene:gruppoCampi idProtezioni="GEN">
		<c:if test='${modo eq "NUOVO" and not empty requestScope.initCLAVOR and not empty requestScope.initNUMERA}'>
			<gene:campoScheda campo="ISGARA_DA_APPALTO" campoFittizio="true" definizione="N1" value="1" visibile="false" />
		</c:if>
		
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<c:choose>
			<c:when test='${isCodificaAutomatica eq "false"}'>
				<c:choose>
					<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
						<gene:campoScheda campo="NGARA" title="${gene:if(garaLottoUnico, 'Codice gara', 'Codice lotto')}" modificabile="false" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="NGARA" title="Codice lotto" obbligatorio="true" modificabile='${modoAperturaScheda eq "NUOVO"}' >
							<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="true" messaggio="${msgChiaveErrore}" />		
						</gene:campoScheda>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="NGARA" title="Codice lotto" modificabile='${modo eq "NUOVO"}' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="NUMAVCP" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
		<gene:campoScheda campo="CODIGA" obbligatorio="${not gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie') && not garaLottoUnico}" modificabile ="${campiModificabili and not gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie')}"/>
		<gene:campoScheda campo="CODCIG" modificabile="${campiModificabili and esisteAnagraficaSimog ne 'true'}" defaultValue="${requestScope.initCODCIG}" />
	<c:choose>
		<c:when test='${fn:startsWith(datiRiga.GARE_CODCIG,"#") or fn:startsWith(datiRiga.GARE_CODCIG,"$") or fn:startsWith(datiRiga.GARE_CODCIG,"NOCIG")}'>
			<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="${datiRiga.GARE_CODCIG}" definizione="T10;;;;G1CODCIG" modificabile="false" />
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="" definizione="T10;;;;G1CODCIG" modificabile="false" />
		</c:otherwise>
	</c:choose>
		
		<gene:campoScheda campo="ESENTE_CIG" campoFittizio="true" computed="true" title="Esente CIG?" definizione="T10;;;SN" defaultValue="2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoSenzaNull" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.CODCIG") }' modificabile="${campiModificabili and esisteAnagraficaSimog ne 'true'}"/>
		<gene:campoScheda campo="MOTESENTECIG" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" />
		<gene:fnJavaScriptScheda funzione="gestioneEsenteCIG()" elencocampi="ESENTE_CIG" esegui="false" />
		
		<gene:campoScheda campo="CODGAR1" visibile="false" defaultValue="${gene:getValCampo(param.keyParent, 'CODGAR')}"/>
		<c:choose>
			<c:when test='${modo eq "NUOVO" and not empty requestScope.initTIPGEN}'>
				<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='false' 
					defaultValue="${requestScope.initTIPGEN}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGEN" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='false' 
					defaultValue="${param.tipoAppalto}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGEN" />
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="NOT_GAR" defaultValue="${requestScope.initNOT_GAR}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="TIPGARG" obbligatorio="true" defaultValue="${requestScope.initTIPGAR}" visibile="false"/>
		<c:choose>
			<c:when test="${(requestScope.initCRITLIC==1 && requestScope.initDETLIC==3) or garaQformOfferte}">
				<c:set var="valoreDefaultRibcal" value="${requestScope.initRIBCAL }" />
			</c:when>
			<c:when test="${requestScope.initDETLIC==4 }">
				<c:set var="valoreDefaultRibcal" value="2" />
			</c:when>
			<c:otherwise>
				<c:set var="valoreDefaultRibcal" value="1" />
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="AQOPER" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" defaultValue="${requestScope.initAQOPER}" obbligatorio="true" modificabile="${gene:if(requestScope.initAQOPER ne '1' , 'true', 'false') }"/>
		<gene:campoScheda campo="AQNUMOPE" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" defaultValue="${requestScope.initAQNUMOPE}">
			<gene:checkCampoScheda funzione='controlloCampoAqnumope("##")' obbligatorio="true" messaggio='Il valore specificato deve essere maggiore di 1.' onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="CRITLICG" obbligatorio="true" defaultValue="${requestScope.initCRITLIC}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="DETLICG" obbligatorio="true" defaultValue="${requestScope.initDETLIC}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoDETLIC" modificabile="${campiModificabili}" visibile="${not garaQformOfferte}"/>
		<gene:campoScheda campo="RIBCAL" obbligatorio="true" defaultValue="${valoreDefaultRibcal}" modificabile="${campiModificabili}" visibile="${not garaQformOfferte}" />
		<gene:campoScheda campo="RIBCAL_FIT" title="Offerta espressa mediante" campoFittizio="true" modificabile="false" definizione="T100;0;;;G1RIBCAL" visibile="${not garaQformOfferte}"/>
		<gene:campoScheda campo="CALCSOANG" obbligatorio="true" defaultValue="${requestScope.initCALCSOAN}" modificabile="${campiModificabili}"/>
		
		<gene:fnJavaScriptScheda funzione='gestioneAccordoQuadroLotti("${modo}","${requestScope.initACCQUATorn }","${requestScope.initAQOPER }")' elencocampi='TORN_ACCQUA' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneAQNUMOPE("#GARE1_AQOPER#")' elencocampi='GARE1_AQOPER' esegui="true" />
				
		<c:choose>
			<c:when test="${not empty initMODASTG}" >
				<c:set var="defaultMODASTG" value="${requestScope.initMODASTG}"/>
			</c:when>
			<c:otherwise>
				<c:set var="defaultMODASTG" value="2"/>
			</c:otherwise>
		</c:choose>
		
		<gene:campoScheda campo="MODASTG" defaultValue="${defaultMODASTG}" obbligatorio="true" modificabile="${campiModificabili}">
			<gene:checkCampoScheda funzione='(toVal("#GARE_MODASTG#") == 2 || toVal("#GARE_CALCSOANG#") == 1)' messaggio='Esclusione automatica delle offerte anomale: se non è previsto il calcolo della soglia di anomalia è possibile indicare esclusivamente il valore \"No\"' obbligatorio="true" onsubmit="true" />
		</gene:campoScheda>
		
		<gene:campoScheda campo="APPLEGREGG"  defaultValue="${requestScope.initAPPLEGREG}" modificabile="${campiModificabili}"/>
		
		<gene:campoScheda campo="MODLICG" visibile="false" defaultValue="${requestScope.initMODLIC}" >
			<gene:calcoloCampoScheda 
			funzione='calcolaMODLICG("#GARE_CRITLICG#","#GARE_DETLICG#","#GARE_CALCSOANG#","#GARE_APPLEGREGG#")' 
			elencocampi="GARE_CRITLICG;GARE_DETLICG;GARE_CALCSOANG;GARE_APPLEGREGG" />
		</gene:campoScheda>
		
		<gene:campoScheda campo="SICINC" visibile="false" defaultValue="${requestScope.initSICINC}"/>
		
		
		<gene:fnJavaScriptScheda funzione='gestioneCRITLICG("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneCOSTOFISSO_SEZIONITEC("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneCALCSOANG("#GARE_CALCSOANG#","true")' elencocampi='GARE_CALCSOANG' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneFlagSicurezzaInclusa("#GARE_MODLICG#","#GARE_DETLICG#")' elencocampi="GARE_MODLICG;GARE_DETLICG" esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneCriterioAggiudicazione("#GARE_CRITLICG#","#GARE_DETLICG#")' elencocampi='GARE_CRITLICG;GARE_DETLICG' esegui="true" />
		
		<c:if test="${ not garaQformOfferte}">
			<gene:fnJavaScriptScheda funzione='gestioneTabellatoDETLICG("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="true" />
			<gene:fnJavaScriptScheda funzione='gestioneDETLICG("#GARE_DETLICG#")' elencocampi='GARE_DETLICG' esegui="false" />
			
		</c:if>
		
		<gene:campoScheda campo="CORGAR1" defaultValue="${ requestScope.initCORGAR}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="NGARA" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false" />
		<gene:campoScheda campo="ULTDETLIC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" defaultValue="${requestScope.initULTDETLIC}" modificabile="${campiModificabili}" visibile="${not garaQformOfferte}"/>
		<gene:campoScheda campo="VALTEC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" defaultValue="${requestScope.initVALTEC}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="COSTOFISSO" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${isProceduraTelematica or param.proceduraTelematica eq '1'}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="SEZIONITEC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${(isProceduraTelematica or param.proceduraTelematica eq '1') and not garaQformOfferte}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="CONTOECO" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" defaultValue="${requestScope.initCONTECO}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="NOTEGA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="IDCOMMALBO" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" defaultValue="${requestScope.initIDCOMMALBO}" visibile="false" />
		<gene:campoScheda campo="GARTEL" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile ="false"/>
		<gene:campoScheda campo="ACCQUA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile ="false"/>
		
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioSoggettiAggregati") && modo eq "VISUALIZZA" }' >
			<gene:campoScheda addTr="false">
				<tr id="rowLinkDetSogAgg" style="display: none;">
					<td colspan="2" class="valore-dato" id="colonnaLinkDetSogAgg">
						<c:set var="titoloFunzione" value="Elenco soggetti per cui agisce la centrale di committenza"/>
						<c:set var="titoloFunzioneJs" value="Elenco soggetti per cui agisce la centrale di committenza"/>
						<c:if test="${datiRiga.TORN_ALTRISOG ne 3 or  empty datiRiga.TORN_ACCQUA}">
							<c:set var="titoloFunzione" value="Elenco soggetti qualificati a ricorrere all'accordo quadro"/>
							<c:set var="titoloFunzioneJs" value="Elenco soggetti qualificati a ricorrere all#accordo quadro"/>
						</c:if>
						<img width="16" height="16" title="Elenco soggetti" alt="Elenco soggetti" src="${pageContext.request.contextPath}/img/soggettiAggregati.png"/>
						<a href="javascript:dettaglioSoggAgg('${titoloFunzioneJs}');" title="${titoloFunzione}" >
							 ${titoloFunzione}
						</a>
				</tr>
			</gene:campoScheda>
		</c:if>
		
	</gene:gruppoCampi>
	
	<c:if test='${esisteIntegrazioneLavori eq "TRUE" and modcont ne "2"}'>

	<gene:gruppoCampi idProtezioni="RILA">
		<gene:campoScheda>
			<td colspan="2"><b>Riferimento all'appalto</b></td>
		</gene:campoScheda>

		<c:choose>
			<c:when test='${modo eq "MODIFICA"}' >
				<c:set var="functionId" value="modificaGareDatigen" />
				<c:set var="parametriWhere" value="T:${datiRiga.TORN_TIPGEN};T:${datiRiga.GARE_NGARA}" />
			</c:when>
			<c:otherwise>
				<c:set var="functionId" value="gareDatigen" />
				<c:set var="parametriWhere" value="T:${datiRiga.TORN_TIPGEN}" />
			</c:otherwise>
		</c:choose>

		<gene:archivio titolo="Appalti"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.CLAVOR"), "gare/gare/trovaAppalto/popup-lista-appalti.jsp","")}'
			scheda=""
			schedaPopUp=""
			campi="APPA.CODLAV;APPA.NAPPAL;APPA.CODCUA"
			chiave=""
			functionId="${functionId}"
			parametriWhere="${parametriWhere}"
			inseribile="false"
			formName="formArchivioAppalti" >
			<gene:campoScheda campo="CLAVOR" defaultValue="${requestScope.initCLAVOR}" modificabile="false"/>
			<gene:campoScheda campo="NUMERA"  defaultValue="${requestScope.initNUMERA}" modificabile="false"/>
			<gene:campoScheda campo="CODCUA" entita="APPA" where="GARE.CLAVOR = APPA.CODLAV and GARE.NUMERA = APPA.NAPPAL" defaultValue="${requestScope.initCODCUA}" modificabile="false"/>
		</gene:archivio>
	</gene:gruppoCampi>
	</c:if>
	
	<gene:campoScheda campo="ALTRISOG" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile ="false" defaultValue="${requestScope.initALTRISOG}" obbligatorio="true"/>
	
	<c:if test='${modoAperturaScheda ne "NUOVO" and datiRiga.TORN_ALTRISOG eq "2"}' >
		<c:set var="tmp1" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneGaraltsogFunction", pageContext, datiRiga.GARE_NGARA)}'/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="RUP" >
		<gene:campoScheda nome="RUP">
			<td colspan="2"><b>Soggetto per cui agisce la centrale di committenza</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			campi="UFFINT.CODEIN;UFFINT.NOMEIN"
			 chiave="CENINT_GARALTSOG"
			 functionId="nullIscuc|abilitazione:1_parentFormName:formUFFINTGareAltriSogg"
			 formName="formUFFINTGareAltriSogg">
			 <gene:campoScheda campo="CENINT_GARALTSOG" title="Codice stazione appaltante aderente" campoFittizio="true" definizione="T16;0;;;G1CENINTSOG" value="${initCenintGaraltsog }"
			 	modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARALTSOG.CENINT") && campiModificabili}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARALTSOG.CENINT")}' />
			 <gene:campoScheda campo="NOMEIN_GARALTSOG" title="Denominazione" campoFittizio="true" definizione="T254;0;;NOTE;NOMEIN" value="${initNomeinGaraltsog }"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") && campiModificabili}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT")}' />
		</gene:archivio>
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="GARALTSOG_CODRUP"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP_GARALTSOG" title="Codice resp.unico procedimento stazione appaltante aderente" campoFittizio="true" definizione="T10;0;;;G1CODRUPSOG" value="${requestScope.initCodrupGaraltsog}"
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARALTSOG.CODRUP") && campiModificabili}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARALTSOG.CODRUP")}' />
				<gene:campoScheda campo="NOMTEC_GARALTSOG" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1" value="${requestScope.initNomtecGaraltsog}"
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP") && campiModificabili}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}'/>
		</gene:archivio>
		
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneArchivio("#TORN_ALTRISOG#")' elencocampi='TORN_ALTRISOG' esegui="true" />
		
	<gene:fnJavaScriptScheda funzione="visualizzaESettaCorrettivo('#GARE_MODLICG#', '#TORN_TIPGEN#', '#GARE_CORGAR1#', 'GARE_CORGAR1')" elencocampi="GARE_MODLICG" esegui="true"/>
	<gene:fnJavaScriptScheda funzione='gestioneULTDETLIC("#GARE_CRITLICG#","#GARE_DETLICG#")' elencocampi='GARE_CRITLICG;GARE_DETLICG' esegui="true" />	
	
	<c:if test='${integrazioneWSERP eq "1"}'>
		<c:choose>
			<c:when test='${tipoWSERP eq "FNM"}'>
				<gene:campoScheda>
					<td colspan="2"><b>Procedimento</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NUMRDA" entita="GARERDA" where="GARE.CODGAR1=GARERDA.CODGAR AND GARE.NGARA=GARERDA.NGARA" modificabile="false" href="javascript:visMetaDati('${datiRiga.GARERDA_NUMRDA}','${datiRiga.GARERDA_ESERCIZIO}','${tipoWSERP}');"/>
				<gene:campoScheda campo="ESERCIZIO" entita="GARERDA" where="GARE.CODGAR1=GARERDA.CODGAR AND GARE.NGARA=GARERDA.NGARA" modificabile="false"/>
			</c:when>
			<c:when test='${tipoWSERP eq "CAV" || tipoWSERP eq "AMIU"}'>
				<c:if test='${modoAperturaScheda ne "NUOVO"}' >
					<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneRichiesteAcquistoFunction", pageContext, codiceGaraRda, gene:getValCampo(key, "NGARA"))}'/>
				</c:if>
				<c:set var="arrayCampiGarerda" value="'GARERDA_ID_', 'GARERDA_CODGAR_', 'GARERDA_CODCARR_', 'GARERDA_NUMRDA_', 'GARERDA_POSRDA_', 'GARERDA_DATCRE_', 'GARERDA_DATRIL_', 'GARERDA_DATACONS_', 'GARERDA_LUOGOCONS_', 'GARERDA_CODVOC_', 'GARERDA_VOCE_', 'GARERDA_CODCAT_', 'GARERDA_UNIMIS_', 'GARERDA_QUANTI_', 'GARERDA_PREZUN_', 'GARERDA_PERCIVA_', 'GARERDA_ESERCIZIO_', 'GARERDA_NGARA_'"/>
				<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
					<jsp:param name="entita" value='GARERDA'/>
					<jsp:param name="chiave" value='${gene:getValCampo(keyParent, "CODGAR")}'/>
					<jsp:param name="nomeAttributoLista" value='listaRichiesteAcquisto' />
					<jsp:param name="idProtezioni" value="GARERDA" />
					<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garerda/richieste-acquisto.jsp"/>
					<jsp:param name="arrayCampi" value="'GARERDA_ID_', 'GARERDA_CODGAR_', 'GARERDA_CODCARR_', 'GARERDA_NUMRDA_', 'GARERDA_POSRDA_', 'GARERDA_DATCRE_', 'GARERDA_DATRIL_', 'GARERDA_DATACONS_', 'GARERDA_LUOGOCONS_', 'GARERDA_CODVOC_', 'GARERDA_VOCE_', 'GARERDA_CODCAT_', 'GARERDA_UNIMIS_', 'GARERDA_QUANTI_', 'GARERDA_PREZUN_', 'GARERDA_PERCIVA_', 'GARERDA_ESERCIZIO_', 'GARERDA_NGARA_', 'GARERDA_STRUTTURA_'"/>
					<jsp:param name="sezioneListaVuota" value="true" />
					<jsp:param name="titoloSezione" value="Richiesta di acquisto" />
					<jsp:param name="titoloNuovaSezione" value="Nuova richiesta di acquisto" />
					<jsp:param name="descEntitaVociLink" value="richiesta di acquisto" />
					<jsp:param name="msgRaggiuntoMax" value="e richieste di acquisto"/>
					<jsp:param name="usaContatoreLista" value="true" />
				</jsp:include>
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
	</c:if>
		
	<jsp:include page="/WEB-INF/pages/gare/gare/gare-importoBaseAsta.jsp" >
		<jsp:param name="tipgen" value='${datiRiga.TORN_TIPGEN}'/>
		<jsp:param name="campiModificabili" value='${campiModificabili}'/>
		<jsp:param name="lottoOffertaUnica" value='true'/>
	</jsp:include>
	<gene:fnJavaScriptScheda funzione='aggiornaCategorieAppalto("GARE_IMPAPP")' elencocampi='GARE_IMPAPP' esegui="false" />
	
	<c:choose>
		<c:when test="${not empty codiceGara}">
			<c:set var="parametroCodiceGara" value='${codiceGara}'/>
		</c:when>
		<c:otherwise>
			<c:set var="parametroCodiceGara" value='${requestScope.parentCODGAR}'/>
		</c:otherwise>
	</c:choose>
	<jsp:include page="/WEB-INF/pages/gare/commons/categorie-gara.jsp" >
		<jsp:param name="tipgen" value='${datiRiga.TORN_TIPGEN}'/>
		<jsp:param name="lottoDiGara" value='true'/>
		<jsp:param name="codiceGara" value='${parametroCodiceGara}'/>
		<jsp:param name="campiModificabili" value='${campiModificabili}'/>
	</jsp:include>
	
	
	
	
	<jsp:include page="/WEB-INF/pages/gare/garcpv/codiciCPV-gara.jsp">
		<jsp:param name="datiModificabili" value="true"/>
		<jsp:param name="lottoOffertaUnica" value="true"/>
		<jsp:param name="initCODCPV" value="${initCODCPV }"/>
	</jsp:include> 
	
	<gene:gruppoCampi idProtezioni="CUP" >
		<gene:campoScheda>
			<td colspan="2"><b>Codice CUP</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CUPPRG" defaultValue ="${initCUPPRG}" />
		<gene:campoScheda campo="CUPMST"  modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CUI" >
		<gene:campoScheda>
			<td colspan="2"><b>Programmazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CODCUI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" defaultValue ="${initCODCUI}"/>
		<gene:campoScheda campo="ANNINT" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
	</gene:gruppoCampi>
	
	<c:if test='${integrazioneWSERP eq "1" && tipoWSERP eq "TPER"}'>
		<c:if test='${modoAperturaScheda ne "NUOVO"}' >
			<c:set var="ngaraCup" value='${gene:getValCampo(param.key, "NGARA")}'/>
			<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaCupFunction", pageContext, ngaraCup)}'/>
			<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
				<jsp:param name="entita" value='GARECUP'/>
				<jsp:param name="chiave" value='${ngaraCup}'/>
				<jsp:param name="nomeAttributoLista" value='listaNCup' />
				<jsp:param name="idProtezioni" value="GARECUP" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garecup/garecup-scheda.jsp"/>
				<jsp:param name="arrayCampi" value="'GARECUP_ID_', 'GARECUP_NGARA_', 'GARECUP_CUP_'"/>
				<jsp:param name="sezioneListaVuota" value="false" />
				<jsp:param name="titoloSezione" value="Altro codice CUP" />
				<jsp:param name="titoloNuovaSezione" value="Nuovo codice CUP" />
				<jsp:param name="descEntitaVociLink" value="codice CUP" />
				<jsp:param name="msgRaggiuntoMax" value="i codici CUP"/>
				<jsp:param name="usaContatoreLista" value="true" />
			</jsp:include>
		</c:if>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="OPZ" >
		<gene:campoScheda>
			<td colspan="2"><b>Opzioni e rinnovi </b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="AMMRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="DESRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
		<gene:campoScheda campo="IMPRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
		<gene:campoScheda campo="AMMOPZ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="DESOPZ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
		<gene:campoScheda campo="IMPSERV" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
		<gene:campoScheda campo="IMPPROR" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
		<gene:campoScheda campo="IMPALTRO" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
		<gene:campoScheda campo="VALMAX" entita="V_GARE_IMPORTI" where="V_GARE_IMPORTI.NGARA=GARE.NGARA" visibile="${modo eq 'VISUALIZZA' }"/>
		<gene:campoScheda campo="VALMAX_FIT" campoFittizio="true" title="Valore massimo stimato" definizione="F14.5;;;MONEY" value="${datiRiga.V_GARE_IMPORTI_VALMAX }" modificabile="false" visibile="${modo ne 'VISUALIZZA'  }"/>
	</gene:gruppoCampi>
	<gene:fnJavaScriptScheda funzione="setVisibilitaDaAmmrin('#GARE1_AMMRIN#','#VALMAX_FIT#')" elencocampi="GARE1_AMMRIN" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="setVisibilitaDaAmmpoz('#GARE1_AMMOPZ#','#VALMAX_FIT#')" elencocampi="GARE1_AMMOPZ" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="calcoloValmax('#GARE1_IMPRIN#','#GARE1_IMPSERV#','#GARE1_IMPPROR#','#GARE1_IMPALTRO#','#GARE_IMPAPP#')" elencocampi="GARE1_IMPRIN;GARE1_IMPSERV;GARE1_IMPPROR;GARE1_IMPALTRO;GARE_IMPAPP" esegui="false"/>
	
	
	<gene:gruppoCampi idProtezioni="AVC">
		<gene:campoScheda>
			<td colspan="2"><b>Contributo a Autorità Nazionale AntiCorruzione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IDIAUT" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CAU">
		<gene:campoScheda>
			<td colspan="2"><b>Garanzia provvisoria</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PGAROF" defaultValue="${requestScope.initPGAROF}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="GAROFF" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="MODCAU" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione="settaCauzione('#GARE_IMPAPP#', '#GARE_PGAROF#')" elencocampi="GARE_IMPAPP;GARE_PGAROF" esegui="false"/>
	<gene:fnJavaScriptScheda funzione="calcoloImportiAnac('#VALMAX_FIT#')" elencocampi="VALMAX_FIT;GARE_IMPAPP" esegui="false"/>
	
	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="GARE"/>
	</jsp:include>
	
	<gene:campoScheda campo="CLIV1" visibile="false"/>
	
	<gene:redefineInsert name="schedaNuovo" >
	<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO") && esisteGaraOLIAMMF == "false" && campiModificabili }'>
	<tr>
		<td class="vocemenulaterale" >
			<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:schedaNuovaGaraLotto();" title="Inserisci" tabindex="1502"></c:if>
				${gene:resource("label.tags.template.lista.listaNuovo")}
			<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
		</td>
	</tr>
	</c:if>
	</gene:redefineInsert>
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="${requestScope.inputFiltro}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
		
			</c:when>
			<c:otherwise>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='${modificaLabel}' title='${modificaLabel}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO") && esisteGaraOLIAMMF == "false" && campiModificabili && !bloccoModificatiDati}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovaGaraLotto()" id="btnNuovo">
					</c:if>
				</gene:insert>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${ modo eq "VISUALIZZA" and bloccoModificatiDati and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2"}' >
			<tr>
				<td class="vocemenulaterale" >
						<a href="javascript:schedaModifica();" title="${modificaLabel}">
					${modificaLabel}</a>
				</td>
			</tr>
		</c:if>
		<c:if test="${empty datiRiga.GARE_CODCIG or empty datiRiga.TORN_NUMAVCP }">
			<c:choose>
				<c:when test="${empty datiRiga.TORN_NUMAVCP and not empty datiRiga.GARE_CODCIG and (fn:startsWith(datiRiga.GARE_CODCIG, '#') or fn:startsWith(datiRiga.GARE_CODCIG, '$') or fn:startsWith(datiRiga.GARE_CODCIG, 'NOCIG')) }">
					<c:set var="condizioneCig" value="false"/>
				</c:when>
				<c:otherwise>
					<c:set var="condizioneCig" value="true"/>
				</c:otherwise>
			</c:choose>
		</c:if>
		<c:if test='${bloccoModificatiDati and modo eq "VISUALIZZA" and condizioneCig eq "true" and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.IntegraCodiceCig") and esisteAnagraficaSimog ne "true"}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:integraCodiceCig('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','Si','${datiRiga.GARE_CODCIG}','${datiRiga.TORN_NUMAVCP}');" title='Integra codice CIG' tabindex="1505">
						Integra codice CIG
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.IntegrazioneProgrammazione") && integrazioneProgrammazione eq "1"}'>
			
			<c:if test="${conteggioRDARDI > 0}" >
				<tr>
					<td class="vocemenulaterale" >
							<a href="javascript:getListaRdaRdi();" title="Associa Rda/RdI" tabindex="1502">
								Gestisci RdA/RdI ${conteggiolottoRDARDI > 0 ? ('('.concat(conteggiolottoRDARDI).concat(')')) : ''}
							</a>
					</td>
				</tr>
			</c:if>
		</c:if>
	</gene:redefineInsert>	

	 <c:if test='${bloccoModificatiDati}'>
	 	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	 	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	 </c:if>

	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${bloccoModificatiDati}"/> 	 
	 
</gene:formScheda>

<gene:javaScript>
	
	var idconfi = "${idconfi}";
	
	<c:if test="${!empty integrazioneptendpoint && modo eq 'VISUALIZZA'}">
		$(window).ready(function (){
			var _v1 = "${pageContext.request.contextPath}";
			var _v2 = "${integrazioneptendpoint}";
			var _v3 = 123;
			var _v4 = "1";
			myIntegrazionePT.init(_v1,_v2,_v3,_v4);
			myIntegrazionePT.creaFinestraSchedaIntervento();
			myIntegrazionePT.creaLinkSchedaIntervento($("#rowGARE1_CODCUI td:eq(1)"));
		});
	</c:if>
	
	<c:if test='${modoAperturaScheda eq "NUOVO" }'>
		function initGare1(){
			setOriginalValue("GARE1_VALTEC"," ");
			setOriginalValue("GARE1_CONTOECO","-1");
		}
		initGare1();
	
		<c:if test="${initFromApparda}">
			var numRdaList = "${initNUMRDA}";
			var voceRdaList = "${initVOCE}";
			var tipoRdaList = "${initTIPORDA}";
			var numRdaArray = numRdaList.split(';');
			var voceRdaArray = voceRdaList.split(';');
			var tipoRdaArray = tipoRdaList.split(';');
			for(var ind=0; ind < numRdaArray.length; ind++){
				if(ind>0){
					showNextElementoSchedaMultipla('GARERDA', new Array(${arrayCampiGarerda}), new Array());
				}
				var numRda =  numRdaArray[ind];
				var voceRda = voceRdaArray[ind];
				var tipoRda = tipoRdaArray[ind];
				var k =ind+1; 
				setValue("GARERDA_NUMRDA_"+k,numRda);
				setValue("GARERDA_VOCE_"+k,voceRda);
				setValue("GARERDA_ESERCIZIO_"+k,tipoRda);
			}
		</c:if>
		
		<c:if test='${integrazioneWSERP eq "1" && tipoWSERP eq "AMIU"}'>
			<c:if test="${initGarerda}">
				function initGarerda(){
					//showNextElementoSchedaMultipla('GARERDA', new Array(${arrayCampiGarerda}), new Array());
					setValue("GARERDA_NUMRDA_1","${initNUMRDA}");
				}
				initGarerda();
			</c:if>
		</c:if>
		
	</c:if>
	
	function setTipoCategorie(tipoCategoria){
		if(tipoCategoria == "") tipoCategoria = "1";
		setTipoCategoriaPrevalentePerArchivio(tipoCategoria);
		setTipoCategoriaUlteriorePerArchivio(tipoCategoria);
		if(tipoCategoria == "1"){
			showObj("CATG_CATIGA_OBBL", true);
			showObj("CATG_CATIGA_NO_OBBL", false);
		} else {
			showObj("CATG_CATIGA_OBBL", false);
			showObj("CATG_CATIGA_NO_OBBL", true);
		}
	}
	
	// occorre eseguire una chiamata esplicita in questo modo e non mediante fnJavascriptScheda con esegui=true
	// in quanto maxIdUlterioreCategoriaVisualizzabile non è ancora definito
	setTipoCategorie(getValue("TORN_TIPGEN"));
	
	// Funzione per cambiare la condizione di where nell'apertura
	// dell'archivio delle categorie dell'appalto per la categoria prevalente
	function setTipoCategoriaPrevalentePerArchivio(tipoCategoria){
		if(document.forms[0].modo.value != "VISUALIZZA"){
			document.formCategoriaPrevalenteGare.archFunctionId.value = "default_save:filtroLotto";
			var parametriWhere = "";
		
			if(getValue("CATG_CATIGA") == "" || getValue("CAIS_TIPLAVG") == ""){
				parametriWhere = "T:" + tipoCategoria;
				setValue("CAIS_TIPLAVG", "" + tipoCategoria);
			} else {
				parametriWhere = "T:" + getValue("CAIS_TIPLAVG");
			}
			
			document.formCategoriaPrevalenteGare.archWhereParametriLista.value = parametriWhere;
		}
	}
	
	function setTipoCategoriaUlteriorePerArchivio(tipoCategoria){
		if(document.forms[0].modo.value != "VISUALIZZA"){
			for(var i=1; i <= maxIdUlterioreCategoriaVisualizzabile; i++){
				eval("document.formUlterioreCategoriaGare" + i + ".archFunctionId").value = "default_save:filtroLotto";
				var parametriWhere = ""
			
				if(getValue("OPES_CATOFF_" + i) == ""){
					parametriWhere = "T:" + tipoCategoria;
				} else {
					parametriWhere = "T:" + getValue("CAIS_TIPLAVG_" + i);
				}
				
				eval("document.formUlterioreCategoriaGare" + i + ".archWhereParametriLista").value = parametriWhere;
			}
		}
	}
	
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioSoggettiAggregati") && modo eq "VISUALIZZA" }' >
		var accqua = getValue("TORN_ACCQUA");
		var altrisog = getValue("TORN_ALTRISOG");
		if((accqua==1 && (altrisog== 1 || altrisog== null || altrisog== "")) || altrisog== 3){
			$( '#rowLinkDetSogAgg' ).show( "fast" );
			//Per visualizzare il link allineato al margine sinistro.
			$("#colonnaLinkDetSogAgg").css("padding-left","0px");
		}
		
		function dettaglioSoggAgg(titolo){
			var ngara = getValue("GARE_NGARA");
			var codgar = getValue("GARE_CODGAR1");
			var comando = "href=gare/gare/popup-dettaglio-soggetti-aggregati.jsp";
		   	comando = comando + "&ngara=" + ngara + "&codgar=" + codgar;
		   	titolo = titolo.replace("#","'");
		   	comando += "&titolo=" + titolo;
		   	openPopUpCustom(comando, "dettaglioSoggAgg", 900, 550, "yes", "yes");
		}
	</c:if>
	
	function schedaNuovaGaraLotto() {
		if(document.forms[0].keyParent.value == null || document.forms[0].keyParent.value==""){
	        var codiceGara="${codiceGara}";
	        document.forms[0].keyParent.value="TORN.CODGAR=T:" + codiceGara;
	    }
        <c:choose>
		<c:when test='${tipoWSERP eq "AMIU"}'>
			var tipoAppalto = getValue("TORN_TIPGEN");
			document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associa-rda-wsdm.jsp&modo=NUOVO&tipoAppalto="+ tipoAppalto +"&chiavePadre='"+getValue("keyParent")+"'&lottoOffertaUnica=SI";
			document.location.href = href;
		</c:when>
		<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.AssociaGaraAppalto") && esisteIntegrazioneLavori eq "TRUE" && modcont eq "1"}' >
			var tipoAppalto = getValue("TORN_TIPGEN");
			var href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/trovaAppalto/associaAppalto.jsp&modo=NUOVO&tipoAppalto=" + tipoAppalto + "&chiavePadre='"+getValue("keyParent")+"'&lottoOffertaUnica=SI";
			if(idconfi){
				href = href + "&idconfi="+idconfi;
			}
			document.location.href = href;
		</c:when>
		<c:otherwise>
	        document.forms[0].action += "&tipoAppalto=" + getValue("TORN_TIPGEN");
			schedaNuovo();
		</c:otherwise>
		</c:choose>
	}
	
	activeForm.calcola("IMPMIS_RIB");
	activeForm.calcola("IMPCOR_RIB");
	activeForm.calcola("IMPAPP_RIB");
	
	
		
	// Funzione per la gestione del campo CORGAR1
	function visualizzaESettaCorrettivo(modAggiudicazione, tipoAppalto, correttivo, campoCorrettivo) {
		var correttivoFornitureServizi = '${correttivoFornitureServizi}';
		var visualizza = false;
		
		// il campo va visualizzato solo con modAggiudicazione=1 o 5
		if (modAggiudicazione == '1' || modAggiudicazione == '5') visualizza = true;

		// nel caso di spegnimento va resettato il suo valore, altrimenti se 
		// va visualizzato ed il dato è vuoto, va inizializzato con il valore di default
		if (!visualizza) setValue(campoCorrettivo, "");
		else {
			if (correttivo == "") {
				setValue(campoCorrettivo, correttivoFornitureServizi);
			}
		} 

		showObj("row"+campoCorrettivo, visualizza);
	}
	
	//Funzione per aggiornare il campo GAROFF
	function settaCauzione(importoAppalto, percentualeCauzione) {
		var decimali = "${requestScope.numeroDecimali}";
				
		var impAppalto = importoAppalto;
		if (impAppalto == null || impAppalto == ""){
			setValue("GARE_GAROFF", "");
			return;
		}
		impAppalto = parseFloat(impAppalto);
		
		var percentuale = percentualeCauzione;
		if (percentuale == null || percentuale == ""){
			setValue("GARE_GAROFF", "");
			return;
		}
		percentuale = parseFloat(percentuale);
		 
		var importoCauzioneProvvisoria;
		importoCauzioneProvvisoria = (impAppalto * percentuale / 100);
		setValue("GARE_GAROFF",  round(eval(importoCauzioneProvvisoria), parseInt(decimali)));
		 
		 
	}
	
	<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
		var arrayScaglioni = new Array();
		<c:forEach items="${listaScaglioniA1z01}" var="scaglione" varStatus="indice" >
			arrayScaglioni[${indice.index}] = new Array("${scaglione.tipoTabellato}", "${scaglione.descTabellato}");
		</c:forEach>
		//Funzione per aggiornare il campo IDIAUT
		/*
		function settaContributo(importoAppalto,arrayScaglioni){
			for(var i=0; i < arrayScaglioni.length; i++){
				var scaglione = parseFloat(arrayScaglioni[i][1]);
				var importoGara;
				if (importoAppalto == null) importoGara=0;
				else importoGara = importoAppalto;
				importoGara = parseFloat(importoGara);
				if (importoGara < scaglione) {
					if (arrayScaglioni[i][0] == null || arrayScaglioni[i][0] == "") setValue("GARE_IDIAUT",  "");
					else setValue("GARE_IDIAUT",  eval(parseFloat(arrayScaglioni[i][0])));
					break;
				}
			}
		}
		*/
		function calcoloImportiAnac(importo){
			settaContributo(importo,arrayScaglioni,"GARE_IDIAUT");
		}
	</c:if>
 
 	function gestioneCriterioAggiudicazione(critlicg,detlicg) {
		<c:if test="${ not garaQformOfferte}">
		if (critlicg == 1 || critlicg == 3) {
			showObj("rowGARE_DETLICG", true);
		} else {
			showObj("rowGARE_DETLICG", false);
		}
		
		//Visualizza il campo RIBCAL solo se Offerta prezzi 
		if (critlicg == 1 && detlicg == 3) {
			if (${modo eq 'VISUALIZZA' || !campiModificabili}){
				showObj("rowGARE_RIBCAL", true);
				//campo fittizio creato per visualizzare la descrizione del tabellato. Altrimenti si vedrebbe solo il numero
				showObj("rowRIBCAL_FIT", false);
			} else {
				showObj("rowGARE_RIBCAL", false);
				showObj("rowRIBCAL_FIT", true);
			}
		} else {
			showObj("rowGARE_RIBCAL", false);
			showObj("rowRIBCAL_FIT", false);
		}
		
		if (${modo ne 'VISUALIZZA' && gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.RIBCAL") && campiModificabili}){
			var selObj = document.getElementById("GARE_RIBCAL");
			var x = selObj.options[selObj.selectedIndex].text;
			setValue("RIBCAL_FIT", x);
		} 
		</c:if>
		if(critlicg == 3){
			showObj("rowGARE_CALCSOANG", false);
		}else{
			showObj("rowGARE_CALCSOANG", true);
		}
		
	}
	
		
	function gestioneCRITLICG(critlicg) {
		var tipgen = getValue("TORN_TIPGEN");
		var garaQformOfferte = "${garaQformOfferte}";
		if(garaQformOfferte != "true" || critlicg == 2)
			document.forms[0].GARE_DETLICG.value='';
		else if(garaQformOfferte == "true" && critlicg != 2)
			document.forms[0].GARE_DETLICG.value=4;
		document.forms[0].GARE_CALCSOANG.value=1;
		document.forms[0].GARE_APPLEGREGG.value='';
		if(garaQformOfferte != "true")
			setValue("GARE_RIBCAL",1);
		if(critlicg == 2 || critlicg == 3 ){
			document.forms[0].GARE_MODASTG.value='2';
			showObj("rowGARE_MODASTG", false);
		}else{
			document.forms[0].GARE_MODASTG.value='';
			showObj("rowGARE_MODASTG", true);
		}
		
	}
	
	function gestioneCOSTOFISSO_SEZIONITEC(critlicg) {
		var gartel = "${isProceduraTelematica }";
		if(gartel == "true" && critlicg == 2){
			showObj("rowGARE1_COSTOFISSO", true);
			showObj("rowGARE1_SEZIONITEC", true);
		}else{
			document.forms[0].GARE1_COSTOFISSO.value='';
			document.forms[0].GARE1_SEZIONITEC.value='';
			showObj("rowGARE1_COSTOFISSO", false);
			showObj("rowGARE1_SEZIONITEC", false);
		}
	}
	
	function gestioneDETLICG(detlicg) {
		var critlicg = getValue("GARE_CRITLICG");
		
		document.forms[0].GARE_APPLEGREGG.value='';
		
		//Solo se offerta prezzi unitari, se anche il padre è OP, riporta il valore del padre, altrimenti
		// imposta RIBCAL in base al tipo di gara (lavori, forniture, servizi)
		if (critlicg == 1 && detlicg == 3) {
			if ("${requestScope.initRIBCAL}" == ""){
				setValue("GARE_RIBCAL",2);
			} else {
				setValue("GARE_RIBCAL","${requestScope.initRIBCAL}");
			}
			
			if(${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.RIBCAL") && campiModificabili}) {
				var selObj = document.getElementById("GARE_RIBCAL");
				var x = selObj.options[selObj.selectedIndex].text;
				setValue("RIBCAL_FIT", x);
			}
		} else if(detlicg == 4) {
			setValue("GARE_RIBCAL",2);
			
			if(${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.RIBCAL")  && campiModificabili}) {
				var selObj = document.getElementById("GARE_RIBCAL");
				var x = selObj.options[selObj.selectedIndex].text;
				setValue("RIBCAL_FIT", x);
			}
		} else {
			setValue("GARE_RIBCAL",1);
			
			if(${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.RIBCAL")  && campiModificabili}) {
				var selObj = document.getElementById("GARE_RIBCAL");
				var x = selObj.options[selObj.selectedIndex].text;
				setValue("RIBCAL_FIT", x);
			}
		}  
	}
	
	
	function gestioneCALCSOANG(calcsoang,impostaValori) {
		var critlicg = getValue("GARE_CRITLICG");
		if (impostaValori == 'true'){
			//document.forms[0].GARE_MODASTG.value='';
			if(critlicg == 2){
				document.forms[0].GARE_MODASTG.value='2';
			}else{
				document.forms[0].GARE_MODASTG.value='';
			}
			document.forms[0].GARE_APPLEGREGG.value='';
			
		}	
		if (calcsoang == 2) {
			if (impostaValori == 'true')
				setValue("GARE_MODASTG","2");
			showObj("rowGARE_MODASTG", false);
		}else {
			//showObj("rowGARE_MODASTG", true);
			if(critlicg == 2){
				showObj("rowGARE_MODASTG", false);
			}else{
				showObj("rowGARE_MODASTG", true);
			}
		}
	}
	
		
	function calcolaMODLICG(critlicg,detlicg,calcsoang,applegregg) {
		var ret = "";
		if (critlicg!="") {
			if (critlicg == 1 && detlicg !="" && calcsoang !="") {
				if (detlicg == 1 && calcsoang == '1') ret = 13;
				if (detlicg == 1 && calcsoang == '2') ret = 1;
				if (detlicg == 2 && calcsoang == '1') ret = 13;
				if (detlicg == 2 && calcsoang == '2') ret = 1;
				if (detlicg == 3 && calcsoang == '1') ret = 14;
				if (detlicg == 3 && calcsoang == '2') ret = 5;
				if (detlicg == 4 && calcsoang == '1') ret = 13;
				if (detlicg == 5 && calcsoang == '1') ret = 13;
				if (detlicg == 4 && calcsoang == '2') ret = 1;
				if (detlicg == 5 && calcsoang == '2') ret = 1;
				//Regione Sicilia
				if(calcsoang == '1'){
					if (detlicg == 1 && applegregg == '1') ret = 15;
					if (detlicg == 2 && applegregg == '1') ret = 15;
					if (detlicg == 3 && applegregg == '2') ret = 16;
				}
			}
			if (critlicg == 2) {
				ret = 6;			
			} 
			if (critlicg == 3) {
				ret = 17;			
			}
		}
		
		return ret;
	}
	
 	var calcsoang = "${datiRiga.GARE_CALCSOANG}"
	gestioneCALCSOANG(calcsoang,"false");
 	
 	function gestioneFlagSicurezzaInclusa(modlicg, detlicg){
 		if((modlicg==1 || modlicg == 13 || modlicg ==15 || modlicg == 17) && detlicg!=4)
 			setValue("GARE_SICINC",1);
 	
 	}
 	
 	
		
	function gestioneULTDETLIC(critlic,detlic) {
		if ((critlic == 1 && (detlic == 3 || detlic == 4)) || critlic == 2) {
			showObj("rowGARE1_ULTDETLIC",true);
		}else{
			setValue("GARE1_ULTDETLIC","");
			showObj("rowGARE1_ULTDETLIC",false);
		}
	}
	
	function gestioneVisualizzazioneArchivio(altrisog) {
		if (altrisog=='2') {
			showObj("rowCENINT_GARALTSOG",true);
			showObj("rowNOMEIN_GARALTSOG",true);
			showObj("rowCODRUP_GARALTSOG",true);
			showObj("rowNOMTEC_GARALTSOG",true);
		} else {
			showObj("rowRUP", false);
			showObj("rowCENINT_GARALTSOG",false);
			showObj("rowNOMEIN_GARALTSOG",false);
			showObj("rowCODRUP_GARALTSOG",false);
			showObj("rowNOMTEC_GARALTSOG",false);
			setValue("CENINT_GARALTSOG", "");
			setValue("NOMEIN_GARALTSOG", "");
			setValue("CODRUP_GARALTSOG", "");
			setValue("NOMTEC_GARALTSOG", "");
		}
	}

<c:if test='${modoAperturaScheda ne "VISUALIZZA" }'>

	var schedaConferma_Default = schedaConferma;
	
	function controlloClassificaSezioniDinamiche(tipoAppalto) {
		var valoreClassifica="";
		var classificaDisabilitata;
		var classificaVisualizzata;
		for (var i=1; i <= idUltimaUlterioreCategoriaVisualizzata; i++) {
			var tipoCategoria = getValue("CAIS_TIPLAVG_" + i);
			var catoff = getValue("OPES_CATOFF_" + i);
			if (catoff!=null && catoff!="") {
				if (tipoCategoria==1) {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_LAVORI_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_LAVORI_" + i).disabled;
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_LAVORI_" + i);
				} else if(tipoCategoria=="2") {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_FORNITURE_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_FORNITURE_" + i).disabled;
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_FORNITURE_" + i);	
				} else if(tipoCategoria=="3") {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_SERVIZI_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_SERVIZI_" + i).disabled;
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_SERVIZI_" + i);	
				} else if(tipoCategoria=="4"){
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_LAVORI150_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_LAVORI150_" + i).disabled;	
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_LAVORI150_" + i);
				} else if(tipoCategoria=="5") {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_" + i).disabled;	
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_" + i);
				}
				if (!classificaDisabilitata && (valoreClassifica==null || valoreClassifica=="") && classificaVisualizzata) {
					clearMsg();
					var msg="Il campo 'Classifica' delle"; 
					if(tipoAppalto==1)
						msg+=" Categorie ulteriori";
					else
						msg+=" Prestazioni secondarie";
					msg+=" e' obbligatorio"; 
					
					outMsg(msg, "ERR");
					onOffMsg();
					return "false";
				}
			}
		}
		return "true";	
			
	}
	
	
	function schedaConfermaCustom() {
		clearMsg();
		setValue("GARE_CODCIG", getValue("GARE_CODCIG").toUpperCase(), false);
		
		<c:if test='${modo eq "NUOVO"}'>
			setValue("GARE_NGARA", getValue("GARE_NGARA").trim(), false);
		</c:if>
		
		<c:if test='${campiModificabili}'>
		if ("2" == getValue("ESENTE_CIG") || "No" == getValue("ESENTE_CIG")) {
			if (!controllaCIG("GARE_CODCIG")) {
				outMsg("Codice CIG non valido", "ERR");
				onOffMsg();
				return;
			}
		} else {
			setValue("GARE_CODCIG", getValue("CODCIG_FIT").toUpperCase());
		}
		</c:if>
		
		var tipoAppalto=getValue("TORN_TIPGEN");
		var tipoCategoria = getValue("CAIS_TIPLAVG");
		if (tipoCategoria == null || tipoCategoria == "")
			tipoCategoria=tipoAppalto;
		
		var catiga = getValue("CATG_CATIGA");
		if (catiga!=null && catiga!="") {
			var valoreClassifica = "";
			var classificaDisabilitata;
			var classificaVisualizzata;
			if (tipoCategoria==1) {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_LAVORI");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_LAVORI").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_LAVORI");
			} else if(tipoCategoria=="2") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_FORNITURE");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_FORNITURE").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_FORNITURE");	
			} else if(tipoCategoria=="3") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_SERVIZI");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_SERVIZI").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_SERVIZI");	
			} else if(tipoCategoria=="4") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_LAVORI150");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_LAVORI150").disabled;
				 classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_LAVORI150");	
			} else if(tipoCategoria=="5") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI");	
			}
			
			if (!classificaDisabilitata && (valoreClassifica==null || valoreClassifica=="") && classificaVisualizzata) {
				clearMsg();
				var msg="Il campo 'Classifica' della"; 
				if (tipoAppalto == 1)
					msg+=" Categoria prevalente";
				else
					msg+=" Prestazione principale";
				msg+=" e' obbligatorio"; 
				
				outMsg(msg, "ERR");
				onOffMsg();
				return;
			}
		}
		
		//Controllo classifiche delle ulteriori categorie
		if (controlloClassificaSezioniDinamiche(tipoAppalto) != "true") {
			return;
		}
		
		schedaConferma_Default();
	}
 	schedaConferma = schedaConfermaCustom;

	$("#GARE_CODCIG").css({'text-transform': 'uppercase' });

	$(function() {
	    $('#GARE_CODCIG').change(function() {
				if (!controllaCIG("GARE_CODCIG")) {
					alert("Codice CIG non valido")
					this.focus();
				}
	    });
	});

</c:if>

	function initEsenteCIG_CODCIG() {
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("GARE_CODCIG");
		//alert("esente CIG = " + esenteCig);
		//alert("Codice CIG = " + codcig);
<c:choose>
	<c:when test='${modo ne "VISUALIZZA" and campiModificabili and esisteAnagraficaSimog ne "true"}'>
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG", "1", false);
				setOriginalValue("ESENTE_CIG", "1", false);
				showObj("rowCODCIG_FIT", true);
				showObj("rowGARE_CODCIG", false);
				setValue("GARE_CODCIG", "", false);
				//setOriginalValue("GARE_CODCIG", "", false);
				showObj("rowGARE1_MOTESENTECIG", true);
			} else {
				setValue("ESENTE_CIG", "2", false);
				setOriginalValue("ESENTE_CIG", "2", false);
				showObj("rowCODCIG_FIT", false);
				showObj("rowGARE_CODCIG", true);
				showObj("rowGARE1_MOTESENTECIG", false);
				setValue("GARE1_MOTESENTECIG", "", false);
			}
		} else {
			setValue("ESENTE_CIG", "2", false);
			setOriginalValue("ESENTE_CIG", "2", false);
			showObj("rowCODCIG_FIT", false);
			showObj("rowGARE_CODCIG", true);
			showObj("rowGARE1_MOTESENTECIG", false);
			setValue("GARE1_MOTESENTECIG", "", false);
		}
	</c:when>
	<c:otherwise>
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG", "Si", false);
				showObj("rowCODCIG_FIT", true);
				showObj("rowGARE_CODCIG", false);
				showObj("rowGARE1_MOTESENTECIG", true);
			} else {
				setValue("ESENTE_CIG", "No", false);
				showObj("rowCODCIG_FIT", false);
				showObj("rowGARE_CODCIG", true);
				showObj("rowGARE1_MOTESENTECIG", false);
				setValue("GARE1_MOTESENTECIG", "", false);
			}
		} else {
			setValue("ESENTE_CIG", "No", false);
			showObj("rowCODCIG_FIT", false);
			showObj("rowGARE_CODCIG", true);
			showObj("rowGARE1_MOTESENTECIG", false);
			setValue("GARE1_MOTESENTECIG", "", false);
		}
	</c:otherwise>
</c:choose>
	}

	function gestioneEsenteCIG() {
	<c:if test='${modo ne "VISUALIZZA"}'>
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("GARE_CODCIG");
		if ("1" == esenteCig) {
			showObj("rowGARE_CODCIG", false);
			//setValue("GARE_CODCIG", "", false);
			if (getOriginalValue("CODCIG_FIT") == getValue("CODCIG_FIT")) {
				setValue("CODCIG_FIT", "", false);
			} else {
				setValue("CODCIG_FIT", getOriginalValue("CODCIG_FIT"), false);
			}
			<c:if test='${gene:checkProt(pageContext, "COLS.MAN.GARE.GARE.CODCIG")}'>
			if(getValue("CODCIG_FIT")==null || getValue("CODCIG_FIT")=="" )
				setValue("CODCIG_FIT", " ", false);
			</c:if>
			showObj("rowCODCIG_FIT", true);
			showObj("rowGARE1_MOTESENTECIG", true);
		} else {
			showObj("rowGARE_CODCIG", true);
			showObj("rowCODCIG_FIT", false);
			setValue("CODCIG_FIT", "", false);
			showObj("rowGARE1_MOTESENTECIG", false);
			setValue("GARE1_MOTESENTECIG", "", false);
		}
	</c:if>
	}
	
	initEsenteCIG_CODCIG();
	
	<c:if test='${not campiModificabili }'>
		function alertModifica(boolean){
			alert("Operazione non disponibile perchè la gara è stata pubblicata");
		} 
		nascondiCategoria = alertModifica;
		visualizzaProssimaUlterioreCategoria = alertModifica;
		visualizzaCategoria = alertModifica;
		eliminaUlterioreCategoria = alertModifica;
	</c:if>
	
	<c:if test='${integrazioneWSERP eq "1" && (tipoWSERP eq "FNM" || tipoWSERP eq "CAV" || tipoWSERP eq "AMIU")}'>
		function visMetaDati(numeroRda,esercizio,tipoWSERP){
			var ngara = getValue("GARE_NGARA");
			var href="href=gare/garerda/popup-rda-metadati.jsp&ngara=" + ngara + "&numeroRda=" + numeroRda  + "&esercizio=" + esercizio + "&tipoWSERP=" + tipoWSERP + "&idconfi=" + idconfi;
			openPopUpCustom(href, "metadatiRda", 700, 600, "yes","yes");
		}
	
	</c:if>
	
	<c:if test='${modo eq "VISUALIZZA" && !empty datiRiga.GARE_NUMERA && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.VisualizzaDocAppaltoDaGare") 
			&& gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NUMERA") && controlloPahDocAssociatiPl}'>
		function visualizzaDocumentiAssociatiAppalto() {
			var tipgen = getValue("TORN_TIPGEN");
			var ngara = getValue("GARE_NGARA");
			var comando = "href=gare/gare/gare-popup-visualizzaDocAppalto.jsp";
		 	comando = comando + "&tipgen=" + tipgen+ "&ngara=" + ngara;
		 	openPopUpCustom(comando, "visualizzaDocAppalto", 700, 450, "yes", "yes");
		}
		
		
	</c:if>
	<c:if test='${integrazioneWSERP eq "1" && tipoWSERP eq "TPER"}'>
	function validazioneCup(campo){
		var valore = campo.value;
		if(valore!=null && valore.length!=15){
			alert('Il valore del codice CUP specificato non \u00e8 valido: deve avere una lunghezza di 15 caratteri');
			this.focus();
		}
		
	}
	</c:if>
	
	
	var options;
	function gestioneTabellatoDETLICG(critlicg){
		var detlicg = getValue("GARE_DETLICG");
		var form = $("#GARE_DETLICG");
		if(options){
			options.each(function(){
			$(this).removeClass("removed");
			});
			options.appendTo(form);
		}
		if(critlicg == 3){
			$("#GARE_DETLICG").find("option[value='1']").addClass("removed");
			$("#GARE_DETLICG").find("option[value='2']").addClass("removed");
			$("#GARE_DETLICG").find("option[value='3']").addClass("removed");
			$("#GARE_DETLICG").find("option[value='5']").addClass("removed");
			document.forms[0].GARE_CALCSOANG.value=2;
			showObj("rowGARE_MODASTG", false);
		}else{
			var tipgen = getValue("TORN_TIPGEN");
			$("#GARE_DETLICG").find("option[value='6']").addClass("removed");
			if(tipgen == 1){
				$("#GARE_DETLICG").find("option[value='5']").addClass("removed");
			}else{
				$("#GARE_DETLICG").find("option[value='1']").addClass("removed");
				$("#GARE_DETLICG").find("option[value='2']").addClass("removed");
			}
		}
		options = $("#GARE_DETLICG").children().detach();
		options.not(".removed").appendTo(form);
		$("#rowGARE_DETLICG select").val(detlicg);
		
	}
	
        function impostaDatiDocumentaleCommessa(tipo){
		if(tipo==1){
			document.formwsdm.entita.value="PERI";
			document.formwsdm.key1.value="${datiRiga.GARE_CLAVOR}";
			document.formwsdm.key2.value=null;
			document.formwsdm.genereGara.value="-1";
		}else{
			document.formwsdm.entita.value="APPA";
			document.formwsdm.key1.value="${datiRiga.GARE_CLAVOR}";
			document.formwsdm.key2.value="${datiRiga.GARE_NUMERA}";
			document.formwsdm.genereGara.value="-2";
		}
		document.formwsdm.submit();
	}
	
	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		bloccaDopoPubblicazione(bloccoModificatiDati,"${offtel}");
	</c:if>
	
	function getListaRdaRdi(){
		formListaRdaRdi.submit();
	}
	
</gene:javaScript>

<style type="text/css">
	
		TABLE.integrazionept {
			margin-top: 5px;
			margin-bottom: 5px;
			padding: 0px;
			font-size: 11px;
			border-collapse: collapse;
			border-left: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
		}

		TABLE.integrazionept TR.intestazione {
			background-color: #EFEFEF;
			border-bottom: 1px solid #A0AABA;
		}
		
		TABLE.integrazionept TR.intestazione TH {
			padding: 2 15 2 5;
			text-align: center;
			font-weight: bold;
			border-left: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-bottom: 1px solid #A0AABA;
			height: 25px;
		}

		TABLE.integrazionept TR.intestazione TH.associa {
			padding: 2 5 2 5;
			width: 50px;
		}
		
		TABLE.integrazionept TR TD.center {
			text-align: center;
		}
	
	
		TABLE.integrazionept TR {
			background-color: #FFFFFF;
		}

		TABLE.integrazionept TR TD {
			padding-left: 3px;
			padding-top: 1px;
			padding-bottom: 1px;
			padding-right: 3px;
			text-align: left;
			border-left: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-bottom: 1px solid #A0AABA;
			height: 25px;
			font: 11px Verdana, Arial, Helvetica, sans-serif;
		}
		
		TABLE.integrazionept TR TD.error {
			color: #D30000;
			font-weight: bold;
			padding: 10 10 10 10;
		}

		TABLE.integrazionept TR TD.associa {
			padding: 2 5 2 5;
			width: 50px;
			text-align: center;
		}
		
		TABLE.integrazionept TR TD.codice {
			width: 80px;
		}
		
		#linkListaInterventi, #linkSchedaIntervento {
			color: black;
			float: right;
			padding-left: 5px;
			padding-right: 5px;
			padding-top: 1px;
			padding-bottom: 1px;
		}
			
	</style>
	
<form name="formListaRdaRdi" action="${pageContext.request.contextPath}/pg/CollegaScollegaRda.do" method="post">
	<input type="hidden" name="handleRda" id="handleRda" value="scollegalotto" />
	<input type="hidden" name="codgar" id="codgar" value="${codiceGaraRda}" />
	<input type="hidden" name="ngara" id="ngara" value="${datiRiga.GARE_NGARA}" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${bloccoModificatiDati}"/>
</form> 

<form name="formwsdm" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-scheda.jsp" /> 
	<input type="hidden" name="entita" value="" />
	<input type="hidden" name="key1" value="" />
	<input type="hidden" name="key2" value="" /> 
	<input type="hidden" name="key3" value="" />
	<input type="hidden" name="key4" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="genereGara" value="2" />
	<input type="hidden" name="idconfi" value="${idconfi}" />
	<input type="hidden" name="autorizzatoModifiche" value="2" />
	
</form>