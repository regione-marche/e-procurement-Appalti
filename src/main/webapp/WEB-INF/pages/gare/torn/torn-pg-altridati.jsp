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

<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsApplicaFascicolazioneValidaFunction",  pageContext, gene:getValCampo(param.key, "CODGAR"), idconfi)}' />

<%/* Altri dati generali della gara */%>
<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreTORN">

	<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
	<input type="hidden" name="paginaAltriDatiTorn" value="si" />
	
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<gene:campoScheda campo="CODGAR" visibile="false" />
	
	<gene:campoScheda campo="TIPOLOGIA" title="Tipologia gara" entita="V_GARE_TORN" visibile='false' campoFittizio="true" value="${tipologiaGara}" definizione="N1"/>
	<gene:campoScheda campo="CATIGA_FIT" campoFittizio="true" definizione="T30" value="${catiga}" visibile="false" />
	<gene:campoScheda campo="OFFTEL" visibile="false" />
	
	<gene:campoScheda visibile="${bloccoModificatiDati eq 'true' and modo eq 'MODIFICA'}">
		<td colspan="2" style="color:#0000FF">
		<br><b>ATTENZIONE:</b>&nbsp;
		Parte dei dati sono in sola consultazione perché la gara è pubblicata su portale Appalti<br>&nbsp;
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
	</jsp:include>
	
	<gene:gruppoCampi idProtezioni="ESPLGARA" visibile="${datiRiga.TORN_GARTEL eq 1}">
		<gene:campoScheda>
			<td colspan="2"><b>Sedute telematiche/espletamento gara</b></td>
		</gene:campoScheda>
		<gene:campoScheda entita="GARE1" campo="ESPLPORT" where="TORN.CODGAR = GARE1.NGARA" />
		<gene:campoScheda entita="GARE1" campo="ESPLECO" where="GARE1.NGARA = GARE.NGARA" />
		<gene:campoScheda entita="GARE1" campo="ESPLBASE" where="GARE1.NGARA = GARE.NGARA" visibile="${datiRiga.TORN_OFFTEL eq 3}"/>
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
	
	<c:if test="${isFascicoloDocumentaleAbilitato eq '1'}">
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO", idconfi)}' />
		<gene:gruppoCampi idProtezioni="ISPGD">
			<gene:campoScheda>
				<td colspan="2"><b>Integrazione con sistema di protocollazione e gestione documentale</b></td>
			</gene:campoScheda>
			<c:if test="${!empty datiRiga.WSFASCICOLO_CODICE and datiRiga.WSFASCICOLO_CODICE !=''}">
				<c:set var="rifFascicolo" value="Cod.: ${datiRiga.WSFASCICOLO_CODICE}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_ANNO and datiRiga.WSFASCICOLO_ANNO !=''}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Anno: ${datiRiga.WSFASCICOLO_ANNO}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_NUMERO and datiRiga.WSFASCICOLO_NUMERO !=''}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Num.: ${datiRiga.WSFASCICOLO_NUMERO}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_CLASSIFICA and datiRiga.WSFASCICOLO_CLASSIFICA !='' && tipoWSDM ne 'JIRIDE'}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Classifica: ${datiRiga.WSFASCICOLO_CLASSIFICA}"/>
			</c:if>
			<c:if test="${tipoWSDM eq 'TITULUS'|| tipoWSDM eq 'ENGINEERINGDOC'}">
				<c:choose>
					<c:when test="${tipoWSDM eq 'ENGINEERINGDOC'}">
						<c:set var="labelCoduff" value="U.O. di competenza"/>
					</c:when>
					<c:otherwise>
						<c:set var="labelCoduff" value="Ufficio"/>
					</c:otherwise>
				</c:choose>
				<c:if test="${!empty datiRiga.WSFASCICOLO_CODAOO and datiRiga.WSFASCICOLO_CODAOO !=''}" >
					<c:set var="rifFascicolo" value="${rifFascicolo } - AOO: ${datiRiga.WSFASCICOLO_CODAOO}"/>
					<c:if test="${!empty datiRiga.WSFASCICOLO_DESAOO and datiRiga.WSFASCICOLO_DESAOO !=''}" >
						<c:set var="rifFascicolo" value="${rifFascicolo } ${datiRiga.WSFASCICOLO_DESAOO}"/>
					</c:if>
				</c:if>
				<c:if test="${!empty datiRiga.WSFASCICOLO_CODUFF and datiRiga.WSFASCICOLO_CODUFF !=''}" >
					<c:set var="rifFascicolo" value="${rifFascicolo } - ${labelCoduff}: ${datiRiga.WSFASCICOLO_CODUFF}"/>
					<c:if test="${!empty datiRiga.WSFASCICOLO_DESUFF and datiRiga.WSFASCICOLO_DESUFF !=''}" >
						<c:set var="rifFascicolo" value="${rifFascicolo } ${datiRiga.WSFASCICOLO_DESUFF}"/>
					</c:if>
				</c:if>
			</c:if>
			<c:choose>
				<c:when test="${tipologiaGara == '3' }">
					<c:set var="entWSDM" value="'GARE'"/>
				</c:when>
				<c:otherwise>
					<c:set var="entWSDM" value="'TORN'"/>
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="ISPGD_FIT" campoFittizio="true" title="Riferimento al fascicolo" definizione="T40;"  value="${rifFascicolo}" modificabile="false"/>
			<gene:campoScheda campo="CODICE" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=TORN.CODGAR and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
			<gene:campoScheda campo="ANNO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=TORN.CODGAR and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
			<gene:campoScheda campo="NUMERO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=TORN.CODGAR and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
			<gene:campoScheda campo="CLASSIFICA" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
			<gene:campoScheda campo="CODAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
			<gene:campoScheda campo="DESAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
			<gene:campoScheda campo="CODUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
			<gene:campoScheda campo="DESUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA=${entWSDM}" visibile="false"/>
		</gene:gruppoCampi>
	</c:if>
	
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
	
	function changeComune(provincia, nomeUnCampoInArchivio) {
		changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
		setValue("GARE_LOCLAV", "");
		setValue("GARE_LOCINT", "");
	}
	
	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		bloccaDopoPubblicazione(bloccoModificatiDati,"");
	</c:if>

</gene:javaScript>
