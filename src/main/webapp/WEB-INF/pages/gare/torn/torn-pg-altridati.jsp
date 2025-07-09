<%
/*
 * Created on: 28/10/2008
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.nuts.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >

<c:set var="elencoeVisibile" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaElencoOperatoriFunction', pageContext, gene:getValCampo(param.key, 'CODGAR'),'TORN')}" />
<c:if test='${tipologiaGara == "3"}'>
	<c:set var="catiga" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.GetCatigaFunction', pageContext, gene:getValCampo(param.key, 'CODGAR'))}" />
	<c:set var="isIntegrazioneOLIAMM" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteViewOLIAMMFunction",  pageContext)}' />
	<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,gene:getValCampo(param.key, "CODGAR"))}' />
</c:if>

<c:set var="codiceGara" value='${gene:getValCampo(key, "CODGAR")}'/>

<c:set var="sql" value="select vernpa from torn where codgar=?"/>
<c:set var="chiaveSQL" value='${codiceGara}'/>
<c:set var="verNPA" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, chiaveSQL)}'/>

<c:if test='${modo ne "NUOVO"}'>
	<c:set var="bloccoModificaPCP" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.npa.GetBloccoModificaPCP", pageContext, codiceGara, "true")}'/>
</c:if>

<c:if test='${modo eq "MODIFICA" and tipologiaGara == "3"}'>
	<c:set var="bloccoGcap" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoGcapFunction",  pageContext,gene:getValCampo(param.key, "CODGAR"),"OFFERTA_UNICA")}' />
	<c:set var="bloccoLotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoLottiFunction",  pageContext,gene:getValCampo(param.key, "CODGAR"))}' />
</c:if>


<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,gene:getValCampo(param.key, "CODGAR"),"BANDO","false")}' />
<c:choose>
	<c:when test='${tipologiaGara == "3"}'>
		<c:set var="controlloTuttiLotti" value="false"/>
	</c:when>
	<c:otherwise>
		<c:set var="controlloTuttiLotti" value="true"/>
	</c:otherwise>
</c:choose>
<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,gene:getValCampo(param.key, "CODGAR"),"ESITO",controlloTuttiLotti)}' />
<c:if test="${!isProceduraTelematica }">
	<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' />
</c:if>

<c:choose>
	<c:when test='${(isProceduraTelematica || applicareBloccoPubblicazioneGareNonTelematiche eq "1") && (bloccoPubblicazionePortaleEsito eq "TRUE" || bloccoPubblicazionePortaleBando eq "TRUE") }'>
		<c:set var="modificaLabel" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' />
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

<c:if test="${bloccoModificaPCP}">
	<c:set var="bloccoModificatiDati" value='true' />
</c:if>

<%/* Altri dati generali della gara */%>
<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreTORN">

	<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
	<input type="hidden" name="bloccoModificaPCP" value="${bloccoModificaPCP}" />
	<input type="hidden" name="paginaAltriDatiTorn" value="si" />
	
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<gene:campoScheda campo="CODGAR" visibile="false" />
	<gene:campoScheda campo="ISCONCES" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
	
	<gene:campoScheda campo="TIPOLOGIA" title="Tipologia gara" entita="V_GARE_TORN" visibile='false' campoFittizio="true" value="${tipologiaGara}" definizione="N1"/>
	<gene:campoScheda campo="CATIGA_FIT" campoFittizio="true" definizione="T30" value="${catiga}" visibile="false" />
	<gene:campoScheda campo="OFFTEL" visibile="false" />
	
	<gene:campoScheda visibile="${bloccoModificatiDati eq 'true' and modo eq 'MODIFICA'}">
		<td colspan="2" style="color:#0000FF">
		<br><b>ATTENZIONE:</b>&nbsp;
		Parte dei dati sono in sola consultazione perché <c:if test="${bloccoModificaPCP}"> sono state inviate delle schede ad ANAC. </c:if>
		<c:if test="${bloccoModificaPubblicazione}">
			<c:choose>
				<c:when test="${bloccoModificaPCP}">La gara è anche </c:when>
				<c:otherwise>la gara è </c:otherwise>
			</c:choose>
			pubblicata su portale Appalti.
		 </c:if>
		    <br>&nbsp;
		</td>
	</gene:campoScheda>
	
	<%/* Campi per GARA DIVISA IN LOTTI CON OFFERTA UNICA */%>
	<c:if test='${tipologiaGara == "3"}'>
		<gene:campoScheda campo="NGARA" entita="GARE" visibile='false' where="TORN.CODGAR = GARE.NGARA "/>
		<gene:campoScheda campo="GENERE" entita="GARE" visibile='false' where="TORN.CODGAR = GARE.NGARA "/>
		
		<gene:gruppoCampi idProtezioni="LOC">
			<gene:campoScheda>
				<td colspan="2"><b>Luogo principale di esecuzione o consegna</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="LOCNAZ" entita="GARE1" where="TORN.CODGAR = GARE1.NGARA" modificabile="${campiModificabili}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoLOCNAZ"/>
				<gene:fnJavaScriptScheda funzione='changeNazione("#GARE1_LOCNAZ#")' elencocampi='GARE1_LOCNAZ' esegui="true"/>
			<gene:campoScheda campo="PROSLA" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${campiModificabili}">
				<c:set var="functionId" value="default_${!empty datiRiga.GARE_PROSLA}" />
				<c:if test="${!empty datiRiga.GARE_PROSLA}">
					<c:set var="parametriWhere" value="T:${datiRiga.GARE_PROSLA}" />
				</c:if>
			</gene:campoScheda>
			<gene:archivio titolo="Comuni" 
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.PROSLA") and gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.LOCLAV") and gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.LOCINT"),"gene/commons/istat-comuni-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="G_COMUNI.PROVINCIA;G_COMUNI.DESCRI;G_COMUNI.CODISTAT"
				functionId="${functionId}"
				parametriWhere="${parametriWhere}"
				chiave="" 
				formName="" 
				inseribile="false" >
			<gene:campoScheda campoFittizio="true" campo="COM_PROLAV" definizione="T9" visibile="false"/>
			<gene:campoScheda campo="LOCLAV" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${campiModificabili}"/>
			<gene:campoScheda campo="LOCINT" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${campiModificabili}"/>
			</gene:archivio>
			<gene:campoScheda campo="LOCINT_FITT" campoFittizio="true" definizione="T9;;;;G1LOCINT" />
			<gene:campoScheda campo="NOMSSL" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${campiModificabili}"/>
			<gene:campoScheda campo="NUMSSL" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${campiModificabili}"/>
		</gene:gruppoCampi>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="CONTR">
		<gene:campoScheda>
			<td colspan="2"><b>Contributo a Autorit&agrave; Nazionale AntiCorruzione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ISTAUT" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<%/* Campi per GARA DIVISA IN LOTTI CON OFFERTA UNICA */%>
	<c:if test='${tipologiaGara == "3"}'>
		
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneCopertureAssicurativeFunction" parametro='${gene:getValCampo(key, "CODGAR")}' />

		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='GARASS'/>
			<jsp:param name="chiave" value='${gene:getValCampo(key, "CODGAR")}'/>
			<jsp:param name="nomeAttributoLista" value='copertureAssicurative' />
			<jsp:param name="idProtezioni" value="ASS" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garass/copertura-assicurativa.jsp"/>
			<jsp:param name="arrayCampi" value="'GARASS_NGARA_', 'GARASS_NUMASS_', 'GARASS_TIPASS_', 'GARASS_IMPASS_', 'GARASS_NOTASS_'"/>
			<jsp:param name="titoloSezione" value="Copertura assicurativa" />
			<jsp:param name="titoloNuovaSezione" value="Nuova copertura assicurativa" />
			<jsp:param name="descEntitaVociLink" value="copertura assicurativa" />
			<jsp:param name="msgRaggiuntoMax" value="e coperture assicurative"/>
			<jsp:param name="sezioneEliminabile" value="${campiModificabili }"/>
			<jsp:param name="sezioneInseribile" value="${campiModificabili }"/>
			<jsp:param name="datiModificabili" value="${campiModificabili }"/>
		</jsp:include>
		
		
			
		
		

		<gene:gruppoCampi idProtezioni="ELE" visibile="${elencoeVisibile eq '1'}">
			<gene:campoScheda>
				<td colspan="2"><b>${gene:if(datiRiga.TORN_ITERGA == 6, 'Riferimento a catalogo elettronico', 'Riferimento a elenco operatori economici')}</b></td>
			</gene:campoScheda>
			<gene:archivio titolo="${gene:if(datiRiga.TORN_ITERGA == 6, 'Cataloghi elettronici', 'Elenco operatori economici')}"
				obbligatorio="" 
				inseribile="true"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GAREALBO.NGARA"),"gare/garealbo/garealbo-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="GAREALBO.NGARA;GAREALBO.OGGETTO" 
				functionId="skip"
				chiave="GARE.ELENCOE"
				formName="formArchivioElencoOperatori">
				<gene:campoScheda campo="ELENCOE" entita="GARE" where="TORN.CODGAR = GARE.NGARA " title="${gene:if(datiRiga.TORN_ITERGA == 6, 'Codice catalogo', 'Codice elenco')}" modificabile="${campiModificabili}"/>
				<gene:campoScheda campo="OGGETTO" entita="GAREALBO" from="GARE" title="${gene:if(datiRiga.TORN_ITERGA == 6, 'Descrizione catalogo', 'Descrizione elenco')}" where="TORN.CODGAR = GARE.NGARA and GAREALBO.NGARA = GARE.ELENCOE" modificabile="${campiModificabili}"/>
			</gene:archivio>
		</gene:gruppoCampi>
		
		<c:if test="${elencoeVisibile eq '1'}">
			<gene:fnJavaScriptScheda funzione="aggiornaFiltroArchivioElencoOperatori()" elencocampi="" esegui="true" />
		</c:if>
		
		
		<c:if test='${isIntegrazioneOLIAMM eq "true" }'>
			<gene:gruppoCampi idProtezioni="OLIAMM" visibile = "${datiRiga.TORN_TIPGEN eq 2 and (datiRiga.TORN_MODLIC  eq 6 or datiRiga.TORN_MODLIC  eq 5 or datiRiga.TORN_MODLIC  eq 14)}" >
				<gene:campoScheda>
					<td colspan="2"><b>Riferimento a gara OLIAMM</b></td>
				</gene:campoScheda>
				<c:choose>
					<c:when test='${(bloccoGcap eq "true" or bloccoLotti eq "true") or esisteGaraOLIAMM eq "true" }'>
						<gene:campoScheda campo="CLIV1" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="false"/>
						<gene:campoScheda campo="RIFERIMENTO" entita="V_GARE_OUT" from="GARE" where="V_GARE_OUT.ID_LISTA=GARE.CLIV1 and TORN.CODGAR = GARE.NGARA" modificabile="false"/>
					</c:when>
					<c:otherwise>
						<gene:archivio titolo="gare OLIAMM"
							obbligatorio="" 
							lista="gare/commons/popup-associa-OLIAMM.jsp" 
							scheda="" 
							schedaPopUp="" 
							campi="V_GARE_OUT.ID_LISTA;V_GARE_OUT.RIFERIMENTO" 
							functionId="default"
							parametriWhere="T:S"
							chiave="GARE.CLIV1"
							formName="formArchivioOLIAMM">
							<gene:campoScheda campo="CLIV1" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${campiModificabili}"/>
							<gene:campoScheda campo="RIFERIMENTO" entita="V_GARE_OUT" from="GARE" where="V_GARE_OUT.ID_LISTA=GARE.CLIV1 and TORN.CODGAR = GARE.NGARA" modificabile="${campiModificabili}"/>
						</gene:archivio>
					</c:otherwise>
				</c:choose>
				
				<gene:campoScheda campo="CLIV2" entita="GARE" where="TORN.CODGAR = GARE.NGARA " modificabile="${campiModificabili}"/>
			</gene:gruppoCampi>
		</c:if>
		
		
	</c:if>
	
	
	
	<gene:campoScheda campo="PROURG" visibile="false" />
	<gene:campoScheda campo="TIPGAR" visibile="false" />
	<gene:campoScheda campo="ITERGA" visibile="false" />
	<gene:campoScheda campo="MODLIC" visibile="false" />
	<gene:campoScheda campo="TIPGEN" visibile="false" />
	<gene:campoScheda campo="IMPTOR" visibile="false" />
	<gene:campoScheda campo="GARTEL" visibile="false" />
	
	<c:choose>
		<c:when test='${tipologiaGara == "3"}'>
			<c:set var="garaOfferteDistinte" value="false"/>
		</c:when>
		<c:otherwise>
			<c:set var="garaOfferteDistinte" value="true"/>
		</c:otherwise>
	</c:choose>
	
	<jsp:include page="/WEB-INF/pages/gare/torn/torn-sez-daticomplementari.jsp" >
		<jsp:param name="tornata" value="true"/>
		<jsp:param name="garaLottoUnico" value="false"/>
		<jsp:param name="campoProceduraAccelerata" value="TORN_PROURG"/>
		<jsp:param name="campoTipoProcedura" value="TORN_ITERGA"/>
		<jsp:param name="campoModalitaAggiudicazione" value="TORN_MODLIC"/>
		<jsp:param name="campoTipoAppalto" value="TORN_TIPGEN"/>
		<jsp:param name="campoImporto" value="TORN_IMPTOR"/>
		<jsp:param name="garaOfferteDistinte" value="${garaOfferteDistinte}"/>
		<jsp:param name="campoGaraTelematica" value="TORN_GARTEL"/>
		<jsp:param name="datiModificabili" value="true"/>
		<jsp:param name="verNPA" value="${verNPA}"/>
	</jsp:include>
	
	<gene:gruppoCampi idProtezioni="ULTDATICONCES" visibile="${datiRiga.TORN_ISCONCES eq '1'}">
		<gene:campoScheda>
			<td colspan="2"><b>Ulteriori dati contratto di concessione </b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Imprese"
			lista='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-lista-popup.jsp","")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp", "")}'
			campi="IMPR.CODIMP;IMPR.NOMEST"
			chiave="TORN_CODPROMO"
			formName="formArchivioImprese" 
			functionId="ditteDitg">
			<gene:campoScheda campo="CODPROMO"/>
			<gene:campoScheda campo="NOMEST" entita="IMPR" where="TORN.CODPROMO=IMPR.CODIMP" />
		</gene:archivio>

		<gene:campoScheda campo="ISPRELPROM"/>
		<gene:campoScheda campo="ISSOCSCOPO"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="SOPRALLUOGO" visibile='${tipologiaGara == "3" }'>
		<gene:campoScheda>
			<td colspan="2"><b>Gestione del sopralluogo</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA"  entita="GARE1" where="TORN.CODGAR = GARE1.NGARA" visibile="false"/>
		<gene:campoScheda campo="SOPROBBL"  entita="GARE1" where="TORN.CODGAR = GARE1.NGARA " modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="SOPRDATA"  entita="GARE1" where="TORN.CODGAR = GARE1.NGARA " modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="SOPRORA"  entita="GARE1" where="TORN.CODGAR = GARE1.NGARA " modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="SOPRREF" entita="GARE1" where="TORN.CODGAR = GARE1.NGARA " modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>

	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="TORN"/>
	</jsp:include>

	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="${key}"/>
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
				<gene:redefineInsert name="addToAzioni">
					<c:if test='${bloccoModificatiDati and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2"}' >
					<tr>
						<td class="vocemenulaterale" >
								<a href="javascript:schedaModifica();" title="${modificaLabel}">
							${modificaLabel}</a>
						</td>
					</tr>
					</c:if>
					<c:if test='${tipoWSDM eq "ENGINEERINGDOC" and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ModificaUOCompetenza")}'>
						<c:choose>
							<c:when test='${tipologiaGara == "3"}'>
								<c:set var="entitaWSDM" value="GARE"/>
								<c:set var="genereW" value="3"/>
							</c:when>
							<c:otherwise>
								<c:set var="entitaWSDM" value="TORN"/>
								<c:set var="genereW" value="1"/>
							</c:otherwise>
						</c:choose>
						<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, entitaWSDM, datiRiga.TORN_CODGAR,idconfi)}' scope="request"/>
						<c:if test="${esisteFascicoloAssociato eq 'true' }">
							<tr>
								<td class="vocemenulaterale" >
									<c:if test='${isNavigazioneDisattiva ne "1"}'>
										<a href="javascript:apriPopupModificaUOCompetenza('${datiRiga.TORN_CODGAR}',${entWSDM},${idconfi});" title="Modifica U.O. di competenza" >
									</c:if>
										Modifica U.O. di competenza
									<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
								</td>
							</tr>
						</c:if>
					</c:if>
					<c:if test="${bloccoPubblicazionePortaleBando eq 'TRUE' && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.ModificaSchedaP31')}" >
						<c:set var="schedaP3_1Compatibile" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.npa.IsSchedaP3_1CompatibileFunction", pageContext, codiceGara)}'/>
						<c:if test="${schedaP3_1Compatibile eq 'true'}" >
							<tr>
								<td class="vocemenulaterale" >
										<a href="javascript:visualizzaG1SCHEP31();" title="Ridefinisci dati scheda P3_1">
									Ridefinisci dati scheda P3_1</a>
								</td>
							</tr>
						</c:if>
					</c:if>
					
				</gene:redefineInsert>
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
					</c:if>
				</gene:insert>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
		
	<c:if test='${bloccoModificatiDati}'>
	 	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	 </c:if>
	 	
	 <c:if test='${modoAperturaScheda ne "VISUALIZZA"}' >
		<gene:fnJavaScriptScheda funzione='changeComune("#GARE_PROSLA#", "COM_PROLAV")' elencocampi='GARE_PROSLA' esegui="false"/>
		<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("GARE_PROSLA", "#COM_PROLAV#")' elencocampi='COM_PROLAV' esegui="false"/>
	</c:if>
	
	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${bloccoModificatiDati}"/>
	 
</gene:formScheda>
<gene:javaScript>
	<c:if test='${tipologiaGara == "3"}'>
				
		function aggiornaFiltroArchivioElencoOperatori(){
		var catiga = getValue("CATIGA_FIT");
		var tipgen = getValue("TORN_TIPGEN");
		var iterga = getValue("TORN_ITERGA");
		
		var archFunctionId = "";
		var archWhereParametriLista = "";
		
		<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />
		
		<c:choose>
			<c:when test='${dbms eq "ORA"}'>
				if(document.formArchivioElencoOperatori !=null ){
					if(catiga != null && catiga != ""){
						archFunctionId += "notBlankCatigaOra";
					}else{
						archFunctionId += "blankCatigaOra";
					}
				}
			</c:when>
			<c:when test='${dbms eq "MSQ"}'>
				if(document.formArchivioElencoOperatori !=null ){
					if(catiga != null && catiga != ""){
						archFunctionId += "notBlankCatigaMsq";
					}else{
						archFunctionId += "blankCatigaMsq";
					}
				}
			</c:when>
			<c:when test='${dbms eq "POS"}'>
				if(document.formArchivioElencoOperatori !=null ){
					if(catiga != null && catiga != ""){
						archFunctionId += "notBlankCatigaPos";
					}else{
						archFunctionId += "blankCatigaPos";
					}
				}
			</c:when>
		</c:choose>
		
		archFunctionId += "_torn"
		
		if (archFunctionId.startsWith("not")) {
			archWhereParametriLista = "T:" + catiga;
		}

		if (tipgen == null) {
			archFunctionId += "_0"; 
		} else {
			archFunctionId += "_" + tipgen;
		}
		
		archFunctionId += "_" + (iterga == 6 && document.formArchivioElencoOperatori !=null);
		
		document.formArchivioElencoOperatori.archFunctionId.value = archFunctionId;
		if (archWhereParametriLista != "") {
			document.formArchivioElencoOperatori.archWhereParametriLista.value = archWhereParametriLista;
		}
	}
		
		
	</c:if>
	function collegaFascicolo(codgar) {
	   var comando = "href=gare/commons/popup-associa-fascicolo.jsp";
	   comando = comando + "&ngara=" + codgar;
	   openPopUpCustom(comando, "collegafascicolo", 450, 350, "yes", "yes");
	}

	function changeNazione(valore){
		if(valore==='1' || valore === ''){
			showObj("rowGARE_PROSLA", true);
			showObj("rowGARE_LOCLAV", true);
			showObj("rowGARE_LOCINT", true);
			showObj("rowLOCINT_FITT", false);
		}else{
			setValue("GARE_PROSLA", "");
			showObj("rowGARE_PROSLA", false)
			showObj("rowGARE_LOCLAV", false);
			showObj("rowGARE_LOCINT", false);
			setValue("LOCINT_FITT",$('#GARE_LOCINT').val());
			showObj("rowLOCINT_FITT", true);
			$("#LOCINT_FITT" ).prop("readonly", true);
			$("#LOCINT_FITT" ).css('background-color','#EFEFEF');
		}
	}

	var oldLOCNAZ=getValue("GARE1_LOCNAZ");
	$("#GARE1_LOCNAZ").on('change', function() {
		if(this.value!=='1' && this.value!==''){
			var codistat=getCodIstatImpresa(this.value);
			setValue("GARE_LOCINT", codistat);
			setValue("LOCINT_FITT", codistat);
		}else{
			if(oldLOCNAZ!=='1' && oldLOCNAZ !== '')
				setValue("GARE_LOCINT", '');
		}
		oldLOCNAZ=getValue("GARE1_LOCNAZ");
	});

	function changeComune(provincia, nomeUnCampoInArchivio) {
		changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
		setValue("GARE_LOCLAV", "");
		setValue("GARE_LOCINT", "");
	}
	
	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		var bloccoModificaPCP = "${bloccoModificaPCP}";
		bloccaDopoPubblicazione(bloccoModificatiDati,"","",bloccoModificaPCP,"${impoOpzRinnModificabili}","${bloccoModificaPubblicazione}");
	</c:if>
	
	
	function visualizzaG1SCHEP31(){
		formVisualizzaG1SCHEP31.submit();
	}

</gene:javaScript>

<form name="formVisualizzaG1SCHEP31" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/g1schep31/g1schep31-pagine-scheda.jsp" /> 
	<input type="hidden" name="key" value="TORN.CODGAR=T:${codiceGara};" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
</form>
