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

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
</gene:redefineInsert>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.nuts.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.integrazionept.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >
<c:set var="arrotondamentoGaroff" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.GetArrotondamentoCauzioneProvvisoriaFunction', pageContext, gene:getValCampo(param.key, 'NGARA'))}" />
<c:set var="elencoeVisibile" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaElencoOperatoriFunction', pageContext, gene:getValCampo(param.key, 'NGARA'),'GARE')}" />
<c:set var="catiga" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.GetCatigaFunction', pageContext, gene:getValCampo(param.key, 'NGARA'))}" />
<c:set var="isIntegrazioneOLIAMM" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteViewOLIAMMFunction",  pageContext)}' />
<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,gene:getValCampo(param.key, "NGARA"))}' />
<c:set var="integrazioneptendpoint" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazionePT.url")}'/>

<c:if test='${modo eq "MODIFICA" }'>
	${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.GetListaScaglioniAnacFunction', pageContext, 'A1z01','listaScaglioniA1z01')}
	<c:if test="${garaLottoUnico=='true' }">
		${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.GetListaScaglioniAnacFunction', pageContext, 'A1z02','listaScaglioniA1z02')}
	</c:if>
</c:if>

<c:if test='${modo eq "MODIFICA" }'>
	<c:set var="bloccoGcap" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoGcapFunction",  pageContext,gene:getValCampo(param.key, "NGARA"),"LOTTO_INICO")}' />
</c:if>
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.CaricaDescPuntiContattoFunction",  pageContext, codiceGara, "GARECONT", "")}'/>


<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO","false")}' />
<c:choose>
		<c:when test='${garaLottoUnico}'>
			<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,gene:getValCampo(param.key, "NGARA"),"ESITO","false")}' />
		</c:when>
		<c:otherwise>
			<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"ESITO","true")}' />
		</c:otherwise>
	</c:choose>

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

<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsApplicaFascicolazioneValidaFunction",  pageContext, codiceGara, idconfi)}' />

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />

<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE">
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
	<input type="hidden" name="paginaAltriDatiTorn" value="no" />
	
	<c:if test="${isFascicoloDocumentaleAbilitato eq '1' && garaLottoUnico}">
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
	</c:if>
		
	<gene:campoScheda visibile="${bloccoModificatiDati eq 'true' and modo eq 'MODIFICA'}">
		<td colspan="2" style="color:#0000FF">
		<br><b>ATTENZIONE:</b>&nbsp;
		Parte dei dati sono in sola consultazione perché la gara è pubblicata su portale Appalti<br>&nbsp;
		</td>	
	</gene:campoScheda>
	<gene:campoScheda campo="NGARA" visibile="false" />		
	<gene:campoScheda campo="CODGAR1" visibile="false" />
	<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="CENINT" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="IMPAPP" visibile="false" />
	<gene:campoScheda campo="GARTEL" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="NGARAAQ" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="OFFTEL" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	
	<gene:campoScheda campo="CATIGA_FIT" campoFittizio="true" definizione="T30" value="${catiga}" visibile="false" />
			
	<gene:gruppoCampi idProtezioni="LOC" visibile="${!(isProceduraTelematica eq 'true' and datiRiga.TORN_ITERGA == 6)}">
		<gene:campoScheda>
			<td colspan="2"><b>Luogo principale di esecuzione o consegna</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PROSLA"  modificabile="${campiModificabili}">
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
			<gene:campoScheda campo="LOCLAV" modificabile="${campiModificabili}"/>
			<gene:campoScheda campo="LOCINT" modificabile="${campiModificabili}"/>
		</gene:archivio>
		<gene:campoScheda campo="NOMSSL" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="NUMSSL" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>


	<gene:gruppoCampi idProtezioni="CONS" visibile="${isProceduraTelematica eq 'true' and datiRiga.TORN_ITERGA == 6}">
		<gene:campoScheda>
			<td colspan="2"><b>Consegna e fatturazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="NCONT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="STATO" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="CENINT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
		<gene:campoScheda campo="PCOESE_FIT" campoFittizio="true" title="Consegna o esecuzione dell'ordine presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCESE") ) }'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso" >
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOESE_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARECONT.PCOESE"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN"
			functionId="default"
			parametriWhere="T:${datiRiga.TORN_CENINT}"
			chiave="GARECONT_PCOESE;CENINT_PCOESE"
			formName="formPuntiConsegnaOrdine"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOESE" campoFittizio="true" definizione="T16" visibile="false" value="${initCENINT}"/>
				<gene:campoScheda campo="PCOESE" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
				<c:set var="linkPCOESE" value='javascript:archivioPunticon("${initCENINT}","${datiRiga.GARECONT_PCOESE}");' />
				<gene:campoScheda campo="NOMPUN_PCOESE" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOESE }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE")}'  href='${gene:if(modo eq "VISUALIZZA",linkPCOESE,"")}' modificabile="${campiModificabili}"/>
		</gene:archivio>
		<gene:campoScheda campo="LOCESE" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="${campiModificabili}"/>

		<gene:campoScheda campo="MODPAG" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" modificabile="${campiModificabili}"/>

		<gene:campoScheda campo="PCOFAT_FIT" campoFittizio="true" title="Fatturazione presso" definizione="T2;;A1098;;" 
			visibile='${(gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOESE") || gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.LOCFAT") ) }'
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPresso" >
			<gene:checkCampoScheda funzione='checkPresenzaCenint("#PCOFAT_FIT#")' obbligatorio="true" messaggio="Impossibile selezionare il valore. Non è presente la Stazione appaltante." onsubmit="false"/>
		</gene:campoScheda>
		<gene:archivio titolo="Punti di contatto" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARECONT.PCOFAT"),"gene/punticon/punticon-lista-popup.jsp","")}' 
			scheda=''
			schedaPopUp="gene/punticon/punticon-scheda-popup.jsp"
			campi="PUNTICON.CODEIN;PUNTICON.NUMPUN;PUNTICON.NOMPUN" 
			functionId="default"
			parametriWhere="T:${datiRiga.TORN_CENINT}"
			chiave="GARECONT_PCOFAT;CENINT_PCOFAT"
			formName="formPuntiFatturazione"
			inseribile="false">
				<gene:campoScheda campo="CENINT_PCOFAT" campoFittizio="true" definizione="T16" visibile="false" value="${initCENINT}"/>
				<gene:campoScheda campo="PCOFAT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1" visibile="false"/>
				<c:set var="linkPCOFAT" value='javascript:archivioPunticon("${initCENINT}","${datiRiga.GARECONT_PCOFAT}");' />
				<gene:campoScheda campo="NOMPUN_PCOFAT" campoFittizio="true" definizione="T245;;;NOTE;G_NOMPUN" value="${initNOMPUN_PCOFAT }" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.PCOFAT")}'  href='${gene:if(modo eq "VISUALIZZA",linkPCOFAT,"")}' modificabile="${campiModificabili}"/>
		</gene:archivio>
		<gene:campoScheda campo="LOCFAT" entita="GARECONT" where="GARECONT.NGARA = GARE.NGARA and GARECONT.NCONT=1"  modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	
	<c:if test="${isProceduraTelematica eq 'true' and modo ne 'VISUALIZZA'}">
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOESE_FIT#','LOCESE','PCOESE')" elencocampi="PCOESE_FIT" esegui="false" />
		<gene:fnJavaScriptScheda funzione="showCampiContatto('#PCOFAT_FIT#','LOCFAT','PCOFAT')" elencocampi="PCOFAT_FIT" esegui="false" />
	</c:if>
	
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneTrattiStradaFunction" parametro='${gene:getValCampo(key, "NGARA")}' />

	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GARSTR'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "NGARA")}'/>
		<jsp:param name="nomeAttributoLista" value='trattiStrada' />
		<jsp:param name="idProtezioni" value="TRSTRADA" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garstr/tratto-strada.jsp"/>
		<jsp:param name="arrayCampi" value="'GARSTR_NGARA_', 'GARSTR_NUMSTR_', 'GARSTR_CODVIA_', 'ASTRA_VIAPIA_', 'GARSTR_NOTSTR_'"/>
		<jsp:param name="titoloSezione" value="Tratto di strada" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo tratto di strada" />
		<jsp:param name="descEntitaVociLink" value="tratto di strada" />
		<jsp:param name="msgRaggiuntoMax" value="i tratti di strada"/>
		<jsp:param name="sezioneEliminabile" value="${campiModificabili }"/>
		<jsp:param name="sezioneInseribile" value="${campiModificabili }"/>
		<jsp:param name="datiModificabili" value="${campiModificabili }"/>
	</jsp:include>

	<gene:gruppoCampi idProtezioni="AVC">
		<gene:campoScheda>
			<td colspan="2"><b>Contributo a Autorità Nazionale AntiCorruzione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IDIAUT" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="ISTAUT" visibile="${garaLottoUnico}" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CAU">
		<gene:campoScheda>
			<td colspan="2"><b>Garanzia provvisoria</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PGAROF" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="GAROFF" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="MODCAU" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	<gene:fnJavaScriptScheda funzione="calcolaGAROFF('#GARE_PGAROF#')" elencocampi="GARE_PGAROF" esegui="false"/>
	
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneCopertureAssicurativeFunction" parametro='${gene:getValCampo(key, "NGARA")}' />

	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='GARASS'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "NGARA")}'/>
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
	
	<jsp:include page="/WEB-INF/pages/gare/garcpv/codiciCPV-gara.jsp">
		<jsp:param name="datiModificabili" value="true"/>
	</jsp:include> 
	
	<gene:gruppoCampi idProtezioni="CUP" >
		<gene:campoScheda>
			<td colspan="2"><b>Codice CUP</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CUPPRG" />
		<gene:campoScheda campo="CUPMST"  modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CUI" >
		<gene:campoScheda>
			<td colspan="2"><b>Programmazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CODCUI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
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
		<gene:campoScheda campo="DESRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="IMPRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="AMMOPZ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="DESOPZ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="IMPSERV" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="IMPPROR" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="IMPALTRO" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="VALMAX" entita="V_GARE_IMPORTI" where="V_GARE_IMPORTI.NGARA=GARE.NGARA" visibile="${modo ne 'MODIFICA' }" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="VALMAX_FIT" campoFittizio="true" title="Valore massimo stimato" definizione="F14.5;;;MONEY;G1IMPMAX_IM" value="${datiRiga.V_GARE_IMPORTI_VALMAX }" modificabile="false" visibile="${modo eq 'MODIFICA' }"/>
	</gene:gruppoCampi>
	<gene:fnJavaScriptScheda funzione="setVisibilitaDaAmmrin('#GARE1_AMMRIN#','#VALMAX_FIT#')" elencocampi="GARE1_AMMRIN" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="setVisibilitaDaAmmpoz('#GARE1_AMMOPZ#','#VALMAX_FIT#')" elencocampi="GARE1_AMMOPZ" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="calcoloValmax('#GARE1_IMPRIN#','#GARE1_IMPSERV#','#GARE1_IMPPROR#','#GARE1_IMPALTRO#','#GARE_IMPAPP#')" elencocampi="GARE1_IMPRIN;GARE1_IMPSERV;GARE1_IMPPROR;GARE1_IMPALTRO" esegui="true"/>
	<gene:fnJavaScriptScheda funzione="calcoloImportiAnac('#VALMAX_FIT#')" elencocampi="VALMAX_FIT" esegui="false"/>
	
	<gene:gruppoCampi idProtezioni="ELE" visibile="${elencoeVisibile eq '1' && empty datiRiga.TORN_NGARAAQ}">
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
			<gene:campoScheda campo="ELENCOE" title="${gene:if(datiRiga.TORN_ITERGA == 6, 'Codice catalogo', 'Codice elenco')}" entita="GARE" modificabile="${campiModificabili}"/>
			<gene:campoScheda campo="OGGETTO" entita="GAREALBO" title="Descrizione" where="GAREALBO.NGARA = GARE.ELENCOE" modificabile="${campiModificabili}"/>
		</gene:archivio>
	</gene:gruppoCampi>
	
	<c:if test="${elencoeVisibile eq '1'}">
		<gene:fnJavaScriptScheda funzione="aggiornaFiltroArchivioElencoOperatori()" elencocampi="" esegui="true" />
	</c:if>
		
	<c:if test='${isIntegrazioneOLIAMM eq "true" }'>
		<gene:gruppoCampi idProtezioni="OLIAMM" visibile="${garaLottoUnico and datiRiga.TORN_TIPGEN eq 2 and (datiRiga.GARE_MODLICG  eq 6 or datiRiga.GARE_MODLICG  eq 5 or datiRiga.GARE_MODLICG  eq 14)}">
			<gene:campoScheda>
				<td colspan="2"><b>Riferimento a gara OLIAMM</b></td>
			</gene:campoScheda>
			<c:choose>
				<c:when test='${bloccoGcap eq "true" or esisteGaraOLIAMM eq "true" }'>
					<gene:campoScheda campo="CLIV1" modificabile="false"/>
					<gene:campoScheda campo="RIFERIMENTO" entita="V_GARE_OUT" where="V_GARE_OUT.ID_LISTA=GARE.CLIV1" modificabile="false"/>
				</c:when>
				<c:otherwise>
					<gene:archivio titolo="gare OLIAMM"
						obbligatorio="" 
						lista="gare/commons/popup-associa-OLIAMM.jsp" 
						scheda="" 
						schedaPopUp="" 
						campi="V_GARE_OUT.ID_LISTA;V_GARE_OUT.RIFERIMENTO" 
						functionId="default"
						parametriWhere="T:N"
						chiave="GARE.CLIV1"
						formName="formArchivioOLIAMM">
						<gene:campoScheda campo="CLIV1" modificabile="${campiModificabili}"/>
						<gene:campoScheda campo="RIFERIMENTO" entita="V_GARE_OUT" where="V_GARE_OUT.ID_LISTA=GARE.CLIV1" modificabile="${campiModificabili}"/>
					</gene:archivio>
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="CLIV2" modificabile="${campiModificabili}"/>
		</gene:gruppoCampi>
	</c:if>
	
	
	<gene:campoScheda campo="PROURG" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="TIPGARG" visibile="false"/>
	
	<gene:campoScheda campo="MODLICG" visibile="false"/>
	<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<c:choose>
		<c:when test='${garaLottoUnico}'>
			<c:set var="garaOfferteDistinte" value="false"/>
		</c:when>
		<c:otherwise>
			<c:set var="garaOfferteDistinte" value="true"/>
		</c:otherwise>
	</c:choose>
	<jsp:include page="/WEB-INF/pages/gare/torn/torn-sez-daticomplementari.jsp" >
		<jsp:param name="tornata" value="false"/>
		<jsp:param name="garaLottoUnico" value="${garaLottoUnico}"/>
		<jsp:param name="campoProceduraAccelerata" value="TORN_PROURG"/>
		<jsp:param name="campoTipoProcedura" value="TORN_ITERGA"/>
		<jsp:param name="campoModalitaAggiudicazione" value="GARE_MODLICG"/>
		<jsp:param name="campoTipoAppalto" value="TORN_TIPGEN"/>
		<jsp:param name="campoImporto" value="GARE_IMPAPP"/>
		<jsp:param name="garaOfferteDistinte" value="${garaOfferteDistinte}"/>
		<jsp:param name="campoGaraTelematica" value="TORN_GARTEL"/>
		<jsp:param name="datiModificabili" value="true"/>
	</jsp:include>
	
	<gene:gruppoCampi idProtezioni="ESPLGARA" visibile="${datiRiga.TORN_GARTEL eq 1}">
		<gene:campoScheda>
			<td colspan="2"><b>Sedute telematiche/espletamento gara</b></td>
		</gene:campoScheda>
		<gene:campoScheda entita="GARE1" campo="ESPLPORT" where="GARE1.NGARA = GARE.NGARA" />
		<gene:campoScheda entita="GARE1" campo="ESPLECO" where="GARE1.NGARA = GARE.NGARA" />
		<gene:campoScheda entita="GARE1" campo="ESPLBASE" where="GARE1.NGARA = GARE.NGARA" visibile="${datiRiga.TORN_OFFTEL eq 3}"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="SOPRALLUOGO" >
		<gene:campoScheda>
			<td colspan="2"><b>Gestione del sopralluogo</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false"/>
		<gene:campoScheda campo="SOPROBBL"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="SOPRDATA"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="SOPRORA"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="SOPRREF" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
	<c:if test="${isFascicoloDocumentaleAbilitato eq '1' && garaLottoUnico}">
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
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
			<c:if test="${tipoWSDM eq 'TITULUS' || tipoWSDM eq 'ENGINEERINGDOC'}">
				<c:choose>
					<c:when test="${tipoWSDM eq 'ENGINEERINGDOC'}">
						<c:set var="labelCoduff" value="U.O. di competenza"/>
					</c:when>
					<c:otherwise>
						<c:set var="labelCoduff" value="Ufficio"/>
					</c:otherwise>
				</c:choose>
				<c:if test="${!empty datiRiga.WSFASCICOLO_CODAOO and datiRiga.WSFASCICOLO_CODAOO !='' && tipoWSDM eq 'TITULUS'}" >
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
			<gene:campoScheda campo="ISPGD_FIT" campoFittizio="true" title="Riferimento al fascicolo" definizione="T40;"  value="${rifFascicolo}" modificabile="false"/>
			<gene:campoScheda campo="CODICE" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="ANNO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="NUMERO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="CLASSIFICA" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			
			<gene:campoScheda campo="CODAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="DESAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="CODUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="DESUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			
		</gene:gruppoCampi>
	</c:if>
	
	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="GARE"/>
	</jsp:include>
	
	
	
	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GARE_CODGAR1}"/>
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
						<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", datiRiga.GARE_NGARA,idconfi)}' scope="request"/>
						<c:if test="${esisteFascicoloAssociato eq 'true' }">
							<tr>
								<td class="vocemenulaterale" >
									<c:if test='${isNavigazioneDisattiva ne "1"}'>
										<a href="javascript:apriPopupModificaUOCompetenza('${datiRiga.GARE_NGARA}','GARE',${idconfi});" title="Modifica U.O. di competenza" >
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

	function calcolaGAROFF(percentuale) {
		if (percentuale == "")
			setValue("GARE_GAROFF", "");
		else {
		var importoAppalto = getValue("GARE_IMPAPP") == "" ? 0 : eval(getValue("GARE_IMPAPP"));
		var numeroDecimali = ${arrotondamentoGaroff};
		var importoCauzioneProvvisoria = round(importoAppalto * eval(percentuale) / 100., numeroDecimali);
		setValue("GARE_GAROFF", toMoney(importoCauzioneProvvisoria));
		}
	}

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
		
		archFunctionId += "_gare";
		
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

	function collegaFascicolo(ngara) {
	   var comando = "href=gare/commons/popup-associa-fascicolo.jsp";
	   comando = comando + "&ngara=" + ngara;
	   openPopUpCustom(comando, "collegafascicolo", 450, 350, "yes", "yes");
	}
	
	function checkPresenzaCenint(valore){
		if(valore==2){
			var cenint = getValue("TORN_CENINT");
			if(cenint=="" || cenint == null)
				return false;
		}
		return true;
	}
	
	function showCampiContatto(valore,nomeCampo,nomeCampoFit){
		if(valore == 2) {
			showObj("rowGARECONT_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, true);
			setValue("GARECONT_" + nomeCampo, "");
		}else if(valore == 3){
			showObj("rowGARECONT_" + nomeCampo, true);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("GARECONT_" + nomeCampoFit, "");
			setValue("NOMPUN_" + nomeCampoFit, "");
		}else{
			showObj("rowGARECONT_" + nomeCampo, false);
			showObj("rowNOMPUN_" + nomeCampoFit, false);
			setValue("GARECONT_" + nomeCampo, "");
			setValue("GARECONT_" + nomeCampoFit, "");
		}
	}
	
	function valorizzaCampiPresso(punto,luogo,campoPresso){
		var valore="";
		<c:choose>
			<c:when test="${modo eq 'VISUALIZZA'}">
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
		<c:if test="${campiModificabili eq 'false' }">
			$("#" + campoPresso).prop( "disabled", true );
		</c:if>
		
		
	}
			
	var stato = getValue("GARECONT_STATO");
	var punto = getValue("GARECONT_PCOESE");
	var luogo = getValue("GARECONT_LOCESE");
	valorizzaCampiPresso(punto,luogo,"PCOESE_FIT");
	
	showObj("rowPCOESE_FIT", true);
	if(punto != ""){
		showObj("rowNOMPUN_PCOESE", true);
		showObj("rowGARECONT_LOCESE", false);
		setValue("GARECONT_LOCESE", "");
	}else if(luogo!=""){
		showObj("rowGARECONT_LOCESE", true);
		showObj("rowNOMPUN_PCOESE", false);
		setValue("GARECONT_PCOESE", "");
		setValue("NOMPUN_PCOESE", "");
	}else{
		showObj("rowGARECONT_LOCESE", false);
		showObj("rowNOMPUN_PCOESE", false);
		setValue("GARECONT_PCOESE", "");
		setValue("GARECONT_LOCESE", "");
		setValue("NOMPUN_PCOESE", "");
	}
	
	var punto = getValue("GARECONT_PCOFAT");
	var luogo = getValue("GARECONT_LOCFAT");
	valorizzaCampiPresso(punto,luogo,"PCOFAT_FIT");
	
	showObj("rowPCOFAT_FIT", true);
	if(punto != ""){
		showObj("rowNOMPUN_PCOFAT", true);
		showObj("rowGARECONT_LOCFAT", false);
		setValue("GARECONT_LOCFAT", "");
	}else if(luogo!=""){
		showObj("rowGARECONT_LOCFAT", true);
		showObj("rowNOMPUN_PCOFAT", false);
		setValue("GARECONT_PCOFAT", "");
		setValue("NOMPUN_PCOFAT", "");
	}else{
		showObj("rowGARECONT_LOCFAT", false);
		showObj("rowNOMPUN_PCOFAT", false);
		setValue("GARECONT_PCOFAT", "");
		setValue("GARECONT_LOCFAT", "");
		setValue("NOMPUN_PCOFAT", "");
	}
	
	function archivioPunticon(codice,num){
		var href = ("href=gene/punticon/punticon-scheda-popup.jsp&key=PUNTICON.CODEIN=T:" + codice + ";PUNTICON.NUMPUN=N:" + num);
		openPopUp(href, "schedaPunticon");
	}
	
	function changeComune(provincia, nomeUnCampoInArchivio) {
		changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
		setValue("GARE_LOCLAV", "");
		setValue("GARE_LOCINT", "");
	}
	
	<c:if test='${modo eq "MODIFICA" }'>
		//Valorizzazione di IDIAUT
		var arrayScaglioni1 = new Array();
		<c:forEach items="${listaScaglioniA1z01}" var="scaglione" varStatus="indice" >
			arrayScaglioni1[${indice.index}] = new Array("${scaglione.tipoTabellato}", "${scaglione.descTabellato}");
		</c:forEach>
		function calcoloImportiAnac(importo){
			settaContributo(importo,arrayScaglioni1,"GARE_IDIAUT");
			<c:if test="${garaLottoUnico=='true' }">
				//Valorizzazione ISTAUT
				var arrayScaglioni2 = new Array();
				<c:forEach items="${listaScaglioniA1z02}" var="scaglione" varStatus="indice" >
					arrayScaglioni2[${indice.index}] = new Array("${scaglione.tipoTabellato}", "${scaglione.descTabellato}");
				</c:forEach>
				settaContributo(importo,arrayScaglioni2,"TORN_ISTAUT");
			</c:if>
		}
	</c:if>
	
	   function validazioneCup(campo){
			var valore = campo.value;
			if(valore!=null && valore.length!=15){
				alert('Il valore del codice CUP specificato non \u00e8 valido: deve avere una lunghezza di 15 caratteri');
				this.focus();
			}
	 	}
	 	
	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		bloccaDopoPubblicazione(bloccoModificatiDati,"");
	</c:if>
	
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