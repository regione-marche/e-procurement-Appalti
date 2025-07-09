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
<c:set var="elencoeVisibile" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaElencoOperatoriFunction', pageContext, gene:getValCampo(param.key, 'NGARA'),'GARE')}" />
<c:set var="catiga" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.GetCatigaFunction', pageContext, gene:getValCampo(param.key, 'NGARA'))}" />
<c:set var="isIntegrazioneOLIAMM" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteViewOLIAMMFunction",  pageContext)}' />
<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,gene:getValCampo(param.key, "NGARA"))}' />
<c:set var="integrazioneptendpoint" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazionePT.url")}'/>

<c:if test='${modo eq "MODIFICA" }'>
	<c:set var="bloccoGcap" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoGcapFunction",  pageContext,gene:getValCampo(param.key, "NGARA"),"LOTTO_INICO")}' />
</c:if>
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="genereGara" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select genere from v_gare_genere where codgar=? and genere < 100", codiceGara)}'/>
<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.CaricaDescPuntiContattoFunction",  pageContext, codiceGara, "GARECONT", "")}'/>

<c:set var="sql" value="select vernpa from torn where codgar=?"/>
<c:set var="verNPA" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, codiceGara)}'/>

<c:if test='${modo ne "NUOVO"}'>
	<c:set var="bloccoModificaPCP" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.npa.GetBloccoModificaPCP", pageContext, codiceGara, "true")}'/>
</c:if>

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

<c:if test="${bloccoModificaPCP}">
	<c:set var="bloccoModificatiDati" value='true' />
</c:if>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />

<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE">
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
	<input type="hidden" name="bloccoModificaPCP" value="${bloccoModificaPCP}" />
	<input type="hidden" name="paginaAltriDatiTorn" value="no" />
	
	<c:if test="${isFascicoloDocumentaleAbilitato eq '1' && garaLottoUnico}">
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
	</c:if>
		
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
	<gene:campoScheda campo="NGARA" visibile="false" />		
	<gene:campoScheda campo="CODGAR1" visibile="false" />
	<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="CENINT" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="IMPAPP" visibile="false" />
	<gene:campoScheda campo="GARTEL" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="NGARAAQ" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="OFFTEL" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="ISPNRR" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="SETTORE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="ISADESIONE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
	<gene:campoScheda campo="ISCONCES" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false"/>
	
	<gene:campoScheda campo="CATIGA_FIT" campoFittizio="true" definizione="T30" value="${catiga}" visibile="false" />
			
	<c:choose>
		<c:when test="${!(isProceduraTelematica eq 'true' and datiRiga.TORN_ITERGA == 6)}">
			<c:set var="titoloSezioneLuogo" value="Luogo principale di esecuzione o consegna"/>
		</c:when>
		<c:otherwise>
			<c:set var="titoloSezioneLuogo" value="Luogo"/>
		</c:otherwise>
	</c:choose>
	<gene:gruppoCampi idProtezioni="LOC" >
		<gene:campoScheda>
			<td colspan="2"><b>${titoloSezioneLuogo}</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="LOCNAZ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${campiModificabili}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoLOCNAZ"/>
			<gene:fnJavaScriptScheda funzione='changeNazione("#GARE1_LOCNAZ#")' elencocampi='GARE1_LOCNAZ' esegui="true"/>
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
		<gene:campoScheda campo="LOCINT_FITT" campoFittizio="true" definizione="T9;;;;G1LOCINT" />
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

	<gene:fnJavaScriptScheda funzione="settaCauzione('#VALMAX_FIT#', '#GARE_PGAROF#')" elencocampi="VALMAX_FIT;GARE_PGAROF" esegui="false"/>
	<gene:fnJavaScriptScheda funzione="calcoloImportiAnac('#VALMAX_FIT#')" elencocampi="VALMAX_FIT;GARE_IMPAPP" esegui="false"/>
	<gene:campoScheda campo="VALMAX" entita="V_GARE_IMPORTI" where="V_GARE_IMPORTI.NGARA=GARE.NGARA" visibile="false" />
	<gene:campoScheda campo="VALMAX_FIT" campoFittizio="true" title="Valore massimo stimato" definizione="F14.5;;;MONEY;G1IMPMAX_IM" value="${datiRiga.V_GARE_IMPORTI_VALMAX }" visibile="false"/>

	<gene:gruppoCampi idProtezioni="CAU">
		<gene:campoScheda>
			<td colspan="2"><b>Garanzia provvisoria</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PGAROF" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="GAROFF" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="MODCAU" modificabile="${campiModificabili}"/>
	</gene:gruppoCampi>
	
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
		<jsp:param name="datiModificabili" value="${not bloccoModificaPCP}"/>
		<jsp:param name="bloccoModificaPCP" value="${bloccoModificaPCP}"/>
	</jsp:include> 
	
	<gene:gruppoCampi idProtezioni="CUP" >
		<gene:campoScheda>
			<td colspan="2"><b>Codice CUP</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CUPPRG" />
		<gene:campoScheda campo="CUPMST"  modificabile="${campiModificabili}"/>
		
		<c:if test='${modoAperturaScheda ne "NUOVO" && gene:checkProt(pageContext, "COLS.VIS.GARE.GARECUP.CUP")}' >
			<c:set var="ngaraCup" value='${gene:getValCampo(param.key, "NGARA")}'/>
			<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaCupFunction", pageContext, ngaraCup)}'/>
			<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
				<jsp:param name="entita" value='GARECUP'/>
				<jsp:param name="chiave" value='${ngaraCup}'/>
				<jsp:param name="nomeAttributoLista" value='listaNCup' />
				<jsp:param name="idProtezioni" value="GARECUP" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garecup/garecup-scheda.jsp"/>
				<jsp:param name="arrayCampi" value="'GARECUP_ID_', 'GARECUP_NGARA_', 'GARECUP_CUP_'"/>
				<jsp:param name="sezioneListaVuota" value="false" />
				<jsp:param name="titoloSezione" value="Altro codice CUP" />
				<jsp:param name="titoloNuovaSezione" value="Nuovo codice CUP" />
				<jsp:param name="descEntitaVociLink" value="ulteriore codice CUP" />
				<jsp:param name="msgRaggiuntoMax" value="i codici CUP"/>
				<jsp:param name="usaContatoreLista" value="true" />
			</jsp:include>
		</c:if>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CUI" >
		<gene:campoScheda>
			<td colspan="2"><b>Programmazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CODCUI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="ANNINT" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
	</gene:gruppoCampi>

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
		<jsp:param name="verNPA" value="${verNPA}"/>
	</jsp:include>
	
	<c:set var="ngaraDFina" value='${gene:getValCampo(param.key, "NGARA")}'/>
	<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaDFinaFunction", pageContext, ngaraDFina)}'/>
	<c:if test="${not empty verNPA}">
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='G1DFINA'/>
			<jsp:param name="chiave" value='${ngaraDFina}'/>
			<jsp:param name="nomeAttributoLista" value='listaDFina' />
			<jsp:param name="idProtezioni" value="G1DFINA" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1dfina/g1dfina-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'G1DFINA_ID_', 'G1DFINA_NGARA_', 'G1DFINA_TIFINA_', 'G1DFINA_IMPOFI_'"/>
			<jsp:param name="titoloSezione" value="Finanziamento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo finanziamento" />
			<jsp:param name="descEntitaVociLink" value="finanziamento" />
			<jsp:param name="msgRaggiuntoMax" value="i finanziamenti"/>
			<jsp:param name="usaContatoreLista" value="true" />
		</jsp:include>
	</c:if>

	<gene:gruppoCampi idProtezioni="ULTDATI" visibile="${not empty verNPA}">
		<gene:campoScheda>
			<td colspan="2"><b>Ulteriori dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CATMERC" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="DISPOPART" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="SERVILOC" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="ISSTRUMEL" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="CODCNEL" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseSkipProtFunction",pageContext,"GARE1.CODCNEL","CODCNEL_DESC")}'/>
		<gene:campoScheda id="CODCNEL_DESC">
				<td class="etichetta-dato"></td>
				<td class="valore-dato"><div style="background-color: #EFEFEF; border: 1px solid;padding:5px">Inserire il codice CNEL di 4 caratteri corrispondente al contratto di settore utilizzato come reperibile in <a href="https://www.cnel.it/Archivio-Contratti">https://www.cnel.it/Archivio-Contratti</a>, punto a), scaricando il file excel di competenza (colonna CCNL id), oppure calcolandolo come indicato nel punto b) dal sito web al link <a href="https://www.cnel.it/Archivio-Contratti/Contrattazione-Nazionale/Ricerca-CCNL" >https://www.cnel.it/Archivio-Contratti/Contrattazione-Nazionale/Ricerca-CCNL</a> (primi 4 caratteri del campo "Categoria" dopo aver selezionato il campo "Settore").
				Nel caso in cui il codice non risulti applicabile, indicare "n.a." nel valore del campo.</div>
				</td>
		</gene:campoScheda>
		<gene:campoScheda campo="ACCESSI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="AFFIRIS" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="ESECRIS" entita="GARE1" />
		<gene:campoScheda campo="MOTCOLL" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
		<gene:campoScheda campo="CIGCOLL" obbligatorio="true" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${(not empty datiRiga.GARE1_MOTCOLL and datiRiga.GARE1_MOTCOLL ne '10' and modo eq 'VISUALIZZA') or modo ne 'VISUALIZZA'}"/>
	</gene:gruppoCampi>
	
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
			<gene:campoScheda campo="CODPROMO"  entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
			<gene:campoScheda campo="NOMEST" entita="IMPR" from="TORN" where="TORN.CODPROMO=IMPR.CODIMP and GARE.CODGAR1=TORN.CODGAR" />
		</gene:archivio>

		<gene:campoScheda campo="ISPRELPROM"  entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
		<gene:campoScheda campo="ISSOCSCOPO"  entita="TORN" where="GARE.CODGAR1=TORN.CODGAR"/>
	</gene:gruppoCampi>
		

	<gene:gruppoCampi idProtezioni="PARIOPP" visibile="${datiRiga.TORN_ISPNRR eq '1'}">
		<gene:campoScheda>
			<td colspan="2"><b>Tutela pari opportunità generazionali e di genere</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DEROGAAQ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${datiRiga.TORN_ISADESIONE eq '1'}" />
		<gene:campoScheda campo="PREVQUOTA" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${ (not(modo eq 'VISUALIZZA' and datiRiga.GARE1_DEROGAAQ eq '1')) or datiRiga.TORN_ISADESIONE ne '1'}" />
		<gene:campoScheda campo="QUOFEMMI" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${(not(modo eq 'VISUALIZZA' and datiRiga.GARE1_DEROGAAQ eq '1')) or datiRiga.TORN_ISADESIONE ne '1'}"/>
		<gene:campoScheda campo="QUOGIOVA" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"
			visibile="${(not(modo eq 'VISUALIZZA' and datiRiga.GARE1_DEROGAAQ eq '1')) or datiRiga.TORN_ISADESIONE ne '1'}"/>

		<c:set var="ngaraDer" value='${gene:getValCampo(param.key, "NGARA")}'/>
		<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaQuotaDerFunction", pageContext, ngaraDer)}'/>
		<c:if test="${not empty listaNDer or modo ne 'VISUALIZZA'}">
			<gene:campoScheda>
				<td colspan="2" id="titoloMotiviDeroga"><b>Motivi deroga</b></td>
			</gene:campoScheda>
		</c:if>
		<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
			<jsp:param name="entita" value='G1QUOTADER'/>
			<jsp:param name="chiave" value='${ngaraDer}'/>
			<jsp:param name="nomeAttributoLista" value='listaNDer' />
			<jsp:param name="idProtezioni" value="G1QUOTADER" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1quotader/g1quotader-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'G1QUOTADER_ID_', 'G1QUOTADER_NGARA_', 'G1QUOTADER_MOTDEROGA_'"/>
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="titoloSezione" value="Motivo deroga quota occupazione femminile/giovanile" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo motivo deroga" />
			<jsp:param name="descEntitaVociLink" value="motivo deroga" />
			<jsp:param name="msgRaggiuntoMax" value="i motivi deroga"/>
			<jsp:param name="usaContatoreLista" value="true" />
		</jsp:include>

		<c:set var="ngaraMisprem" value='${gene:getValCampo(param.key, "NGARA")}'/>
		<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneMultiplaMisPremialiFunction", pageContext, ngaraMisprem)}'/>
		<gene:campoScheda>
			<td colspan="2" id="titoloMisPremiali"><b>Misure premiali</b></td>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-sezione-multipla-singola-riga.jsp" >
			<jsp:param name="entita" value='G1MISPREMIALI'/>
			<jsp:param name="chiave" value='${ngaraMisprem}'/>
			<jsp:param name="nomeAttributoLista" value='listaNMisprem' />
			<jsp:param name="idProtezioni" value="G1MISPREMIALI" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1mispremiali/g1mispremiali-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'G1MISPREMIALI_ID_', 'G1MISPREMIALI_NGARA_', 'G1MISPREMIALI_MISURA_'"/>
			<jsp:param name="sezioneListaVuota" value="true" />
			<jsp:param name="titoloSezione" value="Misure premiali" />
			<jsp:param name="titoloNuovaSezione" value="Nuova misura" />
			<jsp:param name="descEntitaVociLink" value="misura premiale" />
			<jsp:param name="msgRaggiuntoMax" value="e misure premiali"/>
			<jsp:param name="usaContatoreLista" value="true" />
		</jsp:include>
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

	//Funzione per aggiornare il campo GAROFF
	function settaCauzione(importoAppalto, percentualeCauzione) {
				
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
		setValue("GARE_GAROFF",  round(eval(importoCauzioneProvvisoria), 2));
		 
		 
	}

	<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
		var arrayScaglioni = new Array();
		<c:forEach items="${listaScaglioniA1z01}" var="scaglione" varStatus="indice" >
			arrayScaglioni[${indice.index}] = new Array("${scaglione.tipoTabellato}", "${scaglione.descTabellato}");
		</c:forEach>
		function calcoloImportiAnac(importo){
			settaContributo(importo,arrayScaglioni,"GARE_IDIAUT");
		}
	</c:if>

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
	
	$("#GARE_CUPPRG").on('change', function() {
		 	validazioneCup(this);
	});
	
	   function validazioneCup(campo){
			var valore = campo.value;
			var reg = new RegExp("^[A-Za-z0-9]{15}$", "g");
			if(valore!=null && valore.length>0 && !reg.test(valore)){
				alert('Il valore del codice CUP specificato non \u00e8 valido: deve avere una lunghezza di 15 caratteri e deve contenere solo caratteri alfanumerici (lettere e numeri)');
				campo.value=campo.defaultValue;
				this.focus();
			}else{
				campo.defaultValue=valore;
			}
	 	}
	 	
	$("#GARE1_CODCUI").on('change', function() {
		 	validazioneCUI(this);
	});
	
	function validazioneCUI(campo){
			var valore = campo.value;
			var reg = new RegExp("^[A-Za-z0-9]{20,22}$", "g");
			if(valore!=null && valore.length>0 && !reg.test(valore)){
				alert('Il valore del codice CUI specificato non \u00e8 valido: deve avere una lunghezza di compresa tra 20 e 22 caratteri e deve contenere solo caratteri alfanumerici (lettere e numeri)');
				campo.value=campo.defaultValue;
				this.focus();
			}else{
				campo.defaultValue=valore;
			}
	 	}
		
	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		var bloccoModificaPCP = "${bloccoModificaPCP}";
		bloccaDopoPubblicazione(bloccoModificatiDati,"","",bloccoModificaPCP,"${impoOpzRinnModificabili}","${bloccoModificaPubblicazione}");
	</c:if>
	
	function gestisciCIGCOLL() {
		if (${modo ne 'VISUALIZZA'}){
			if ($('#GARE1_MOTCOLL').val() != '' && $('#GARE1_MOTCOLL').val() != 10) {
				$('#rowGARE1_CIGCOLL').show();
			} else {
				$('#GARE1_CIGCOLL').val('');
				$('#rowGARE1_CIGCOLL').hide();
			}
		}
	}

	function delElementoSchedaMultiplaNoConfirm(id, label, tipo, campi){
		hideElementoSchedaMultipla(id, tipo, campi, false);
		setValue(label + id, "1");
	}

	function gestisciDEROGAAQ() {
		if (${modo ne 'VISUALIZZA'}){
			if ($('#GARE1_DEROGAAQ').val() != 1 || '${datiRiga.TORN_ISADESIONE}' != '1') {
				$('#rowGARE1_PREVQUOTA').show(); 
				$('#titoloMisPremiali').show(); 
				$('#rowLinkAddG1MISPREMIALI').show();
			} else {
				for (let i = 1; i < $('[id^=rowG1MISPREMIALI_MISURA_]').length + 1; i++) { 
					if ($('#rowG1MISPREMIALI_MISURA_' + i).is(":visible")){
						delElementoSchedaMultiplaNoConfirm(i, 'G1MISPREMIALI_DEL_G1MISPREMIALI_', 'G1MISPREMIALI', new Array('G1MISPREMIALI_ID_', 'G1MISPREMIALI_NGARA_', 'G1MISPREMIALI_MISURA_'));
					}
				}
				$('#GARE1_PREVQUOTA').val('');
				gestisciPREVQUOTA();
				$('#rowGARE1_PREVQUOTA').hide(); 
				$('#titoloMisPremiali').hide(); 
				$('#rowLinkAddG1MISPREMIALI').hide();
			}
		}
	}

	function gestisciPREVQUOTA() {
		if ($('#GARE1_PREVQUOTA').val() == 3 || $('#GARE1_PREVQUOTA').val() == 1) {
			$('#titoloMotiviDeroga').show(); 
			$('#rowLinkAddG1QUOTADER').show();
		} else {
			for (let i = 1; i < $('[id^=rowG1QUOTADER_MOTDEROGA_]').length + 1; i++) {
				if ($('#rowG1QUOTADER_MOTDEROGA_' + i).is(":visible")){
					delElementoSchedaMultiplaNoConfirm(i, 'G1QUOTADER_DEL_G1QUOTADER_', 'G1QUOTADER', new Array('G1QUOTADER_ID_', 'G1QUOTADER_NGARA_', 'G1QUOTADER_MOTDEROGA_'));
				}
			}
			$('#titoloMotiviDeroga').hide(); 
			$('#rowLinkAddG1QUOTADER').hide();
		}

		if ($('#GARE1_PREVQUOTA').val() == 3) {
			$('#rowGARE1_QUOFEMMI').show();
			$('#rowGARE1_QUOGIOVA').show();
		} else {
			$('#GARE1_QUOFEMMI').val('');
			$('#GARE1_QUOGIOVA').val('');
			$('#rowGARE1_QUOFEMMI').hide();
			$('#rowGARE1_QUOGIOVA').hide();
		}
	}

	$(document).ready(function() {
		gestisciCIGCOLL();
		gestisciDEROGAAQ();
		gestisciPREVQUOTA();
		$('#GARE1_MOTCOLL').change(function(){
			gestisciCIGCOLL();
		});
		$('#GARE1_DEROGAAQ').change(function(){
			gestisciDEROGAAQ();
		});
		$('#GARE1_PREVQUOTA').change(function(){
			gestisciPREVQUOTA();
		});

		$('#GARE1_CIGCOLL').change(function() {
			if (!controllaCIG("GARE1_CIGCOLL")) {
				alert("Codice CIG non valido")
				this.focus();
			}
	    });
	    if (${bloccoModificaPCP eq "true" and modo eq "MODIFICA"}) {
	    	$('#rowLinkAddG1MISPREMIALI').hide();
			$('#rowLinkAddG1QUOTADER').hide();
			$("[id^='rowtitoloG1MISPREMIALI_']").each( function() {
				$(this).hide();
			});
			$("[id^='rowtitoloG1QUOTADER_']").each( function() {
				$(this).hide();
			});

			var nascondiTitoloMISPREMIALI = true;
			$("[id^='rowG1MISPREMIALI_MISURA_']").each( function() {
				if ($(this).is(":visible")){
					nascondiTitoloMISPREMIALI = false;
				}
			});
			if (nascondiTitoloMISPREMIALI) {
				$('#titoloMisPremiali').hide();
			}

			var nascondiTitoloQUOTADER = true;
			$("[id^='rowG1QUOTADER_MOTDEROGA_']").each( function() {
				if ($(this).is(":visible")){
					nascondiTitoloQUOTADER = false;
				}
			});
			if (nascondiTitoloQUOTADER) {
				$('#titoloMotiviDeroga').hide();
			}
	    }
	    if (${datiRiga.TORN_ISPNRR ne '1'}) {
			for (let i = 1; i < $('[id^=rowG1MISPREMIALI_MISURA_]').length + 1; i++) {
				$('#rowG1MISPREMIALI_MISURA_' + i).hide();
			}
		}
	});
	
	function visualizzaG1SCHEP31(){
		formVisualizzaG1SCHEP31.submit();
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
	
	<form name="formVisualizzaG1SCHEP31" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/g1schep31/g1schep31-pagine-scheda.jsp" /> 
	<input type="hidden" name="key" value="TORN.CODGAR=T:${codiceGara};" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
</form>