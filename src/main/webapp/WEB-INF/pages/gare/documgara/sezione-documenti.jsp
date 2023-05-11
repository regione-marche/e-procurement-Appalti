
<%
/*
 * Created on: 14/07/2010
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

<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>	

<c:set var="bustalotti" value='${param.bustalotti}'/>
<c:set var="tipologia" value='${param.tipologiaDoc}'/>
<c:set var="busta" value='${param.busta}'/>
<c:set var="titoloBusta" value='${param.titoloBusta}'/>
<c:set var="codiceGara" value='${param.codiceGara}'/>
<c:set var="ngara" value='${param.ngara}'/>
<c:set var="richiestaFirma" value='${param.richiestaFirma}'/>
<c:set var="gruppo" value='${param.gruppo}'/>
<c:set var="autorizzatoModifiche" value='${param.autorizzatoModifiche}'/>
<c:set var="lottoFaseInvito" value='${param.lottoFaseInvito}'/>
<c:set var="isProceduraTelematica" value='${param.isProceduraTelematica}'/>
<c:set var="insDocDaProtocollo" value='${param.insDocDaProtocollo}'/>
<c:set var="sso" value='${param.sso}'/>
<c:set var="fasEle" value='${param.fasEle}'/>

<c:choose>
	<c:when test='${not empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}" />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>
	
<c:if test="${lottoFaseInvito}">
	<c:set var="ngara" value=''/>
</c:if>
<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:if test='${firmaProvider eq 2}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:set var="gestioneERP" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.gestioneERP",idconfi)}'/>
<c:set var="numeroDocumentoWSDM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckAssociataRdaJIRIDEFunction", pageContext, codiceGara)}' scope="request"/>
<c:if test='${integrazioneWSERP eq "1" and tipoWSERP eq "AMIU"}'>
	<c:set var="presenzaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPPresenzaRdaFunction", pageContext, codiceGara, null, requestScope.tipoWSERP)}' scope="request" />
</c:if>

<c:choose> 
	<c:when test="${gruppo eq 3 && not empty busta && busta != ''}" >
		<c:set var="parametroFiltro" value='${busta}'/>
	</c:when>
	<c:when test="${gruppo eq 3 && not empty fasEle && fasEle != ''}" >
		<c:set var="parametroFiltro" value='${fasEle}'/>
	</c:when>
	<c:otherwise>
		<c:set var="parametroFiltro" value='${tipologia}'/>
	</c:otherwise>
</c:choose>
<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GestioneDocumentazioneTipologiaFunction", pageContext,codiceGara,parametroFiltro,gruppo)}' />
<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>

<c:choose>
	<c:when test="${not empty busta  }">
		<c:set var="parametro" value="${busta}"/>
	</c:when>
	<c:otherwise>
		<c:set var="parametro" value="${fasEle}"/>
	</c:otherwise>
</c:choose>
<c:set var="condizioneFunzArchivia" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.CheckDocumentiPubblicatiTipologiaFunction", pageContext, codiceGara, tipologia,parametro, gruppo)}' scope="request"/>

<c:if test="${genereGara eq '10' or genereGara eq '20' }">
	<c:set var="garaElencoCatalogo" value='true'/>
	<c:set var="tipologiaGareAlbo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGarealboFunction", pageContext, key)}' scope="request"/>
</c:if>

<c:if test="${genereGara ne '10' and genereGara ne '20' and genereGara ne '11' }">
<c:set var="integrazioneMDgueUrl" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazioneMDgue.url")}'/>
</c:if>



<gene:redefineInsert name="schedaConferma">
	<tr>
		<td class="vocemenulaterale">
			<a href="javascript:ConfermaModifica();" title="Salva modifiche" tabindex="1501">
				${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
	</tr>
</gene:redefineInsert>

<gene:redefineInsert name="schedaAnnulla">
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:ConfermaAnnulla();" title="Annulla modifiche" tabindex="1502">
					${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a></td>
			</tr>
</gene:redefineInsert>

<c:if test="${gruppo eq 4 }">
	<c:set var="gruppo15Visibile" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsGruppoDocumentazioneVisibileFunction", pageContext, codiceGara, "15")}' scope="request"/>
</c:if>

<input type="hidden" name="filtroDocumentazione" value="${gruppo}"/>

	<c:if test='${gruppo eq 1}'>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DOCUMGARA'/>
			<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='documentiGara' />
			<jsp:param name="idProtezioni" value="DOCUMGARA" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/bando-documenti-gara.jsp"/>
			<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_', 'DOCUMGARA_DESCRIZIONE_', 'DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','DOCUMGARA_URLDOC_','DOCUMGARA_TIPOLOGIA_','W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','tipoAllegato_','W_DOCDIG_DIGFIRMA_','DOCUMGARA_IDSTAMPA'"/>		
			<jsp:param name="titoloSezione" value="Documento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo documento" />
			<jsp:param name="descEntitaVociLink" value="documento" />
			<jsp:param name="msgRaggiuntoMax" value="i documenti"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
			<jsp:param name="firmaRemota" value="${firmaRemota}"/>
			<jsp:param name="firmaDocumento" value="${firmaDocumento}"/>
			<jsp:param name="tipologia" value="${tipologia}"/>
			<jsp:param name="funzEliminazione" value="delDocumento"/>
			<jsp:param name="numeroDocumentoWSDM" value="${numeroDocumentoWSDM}"/>
			<jsp:param name="gestioneERP" value="${gestioneERP}"/>
			<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
			<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}"/>
		</jsp:include>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.DOCUMGARA.DOCUMGARA")}'/>
	</c:if>
	
	<c:if test='${gruppo eq 2 }'>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DOCUMGARA'/>
			<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='requisitiConcorrenti' />
			<jsp:param name="idProtezioni" value="DOCUMREQ" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/requisiti-concorrenti.jsp"/>
			<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_'"/>		
			<jsp:param name="titoloSezione" value="Requisito" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo requisito" />
			<jsp:param name="descEntitaVociLink" value="requisito" />
			<jsp:param name="msgRaggiuntoMax" value="i requisiti"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="tipologia" value="${tipologia}"/>
			<jsp:param name="firmaRemota" value="${firmaRemota}"/>
			<jsp:param name="firmaDocumento" value="${firmaDocumento}"/>
			<jsp:param name="funzEliminazione" value="delDocumento"/>
			<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
			<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}"/>
		</jsp:include>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.DOCUMGARA.DOCUMREQ")}'/>
	</c:if>
	
	<c:if test='${gruppo eq 3 }'>
		<c:if test="${isProceduraTelematica eq 'true' and busta eq '2'}">
			<c:set var="whereSezTec" value="NGARA='${ngara }'"/>
			<c:set var="sezionitec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "SEZIONITEC","GARE1", whereSezTec)}'/>
		</c:if>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DOCUMGARA'/>
			<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='documentazioneConcorrenti' />
			<jsp:param name="idProtezioni" value="DOCUMCONC" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/documentazione-concorrenti.jsp"/>
			<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_','DOCUMGARA_BUSTA_','DOCUMGARA_FASELE_','DOCUMGARA_REQCAP_','DOCUMGARA_TIPODOC_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_CONTESTOVAL_','DOCUMGARA_OBBLIGATORIO_','DOCUMGARA_STATODOC_','DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_VALENZA_','DOCUMGARA_MODFIRMA_','W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','DOCUMGARA_GENTEL_','DOCUMGARA_SEZTEC_','DOCUMGARA_IDSTAMPA_'"/>		
			<jsp:param name="titoloSezione" value="Documento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo documento" />
			<jsp:param name="descEntitaVociLink" value="documento" />
			<jsp:param name="msgRaggiuntoMax" value="i documenti"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="funzEliminazione" value="delDocumentazioneConcorrenti"/>
			<jsp:param name="busta" value="${busta}"/>
			<jsp:param name="firmaRemota" value="${firmaRemota}"/>
			<jsp:param name="firmaDocumento" value="${firmaDocumento}"/>
			<jsp:param name="funzEliminazione" value="delDocumento"/>
			<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
			<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}"/>
			<jsp:param name="sezionitec" value="${sezionitec}"/>
			<jsp:param name="fasEle" value="${fasEle}"/>
			
		</jsp:include>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.DOCUMGARA.DOCUMCONC")}'/>
	</c:if>
	
	<c:if test='${gruppo eq 4}'>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DOCUMGARA'/>
			<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='documentiEsito' />
			<jsp:param name="idProtezioni" value="DOCUMESITO" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/documenti-esito.jsp"/>
			<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','DOCUMGARA_URLDOC_','DOCUMGARA_TIPOLOGIA_','W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','tipoAllegato_','W_DOCDIG_DIGFIRMA_', 'DOCUMGARA_DATAPROV_', 'DOCUMGARA_NUMPROV_'"/>		
			<jsp:param name="titoloSezione" value="Documento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo documento" />
			<jsp:param name="descEntitaVociLink" value="documento" />
			<jsp:param name="msgRaggiuntoMax" value="i documenti"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
			<jsp:param name="tipologia" value="${tipologia}"/>
			<jsp:param name="firmaRemota" value="${firmaRemota}"/>
			<jsp:param name="firmaDocumento" value="${firmaDocumento}"/>
			<jsp:param name="funzEliminazione" value="delDocumento"/>
			<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
			<jsp:param name="numeroDocumentoWSDM" value="${numeroDocumentoWSDM}"/>
			<jsp:param name="gestioneERP" value="${gestioneERP}"/>
			<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}"/>
		</jsp:include>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.DOCUMGARA.DOCUMGARA")}'/>
	</c:if>
	
	<c:if test='${gruppo eq 5}'>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DOCUMGARA'/>
			<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='documentiTrasparenza' />
			<jsp:param name="idProtezioni" value="DOCUMTRASP" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/documenti-trasparenza.jsp"/>
			<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','W_DOCDIG_IDPRG_','DOCUMGARA_TIPOLOGIA_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','DOCUMGARA_DITTAAGG_','IMPR_NOMIMP_','W_DOCDIG_DIGFIRMA_'"/>		
			<jsp:param name="titoloSezione" value="Documento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo documento" />
			<jsp:param name="descEntitaVociLink" value="documento" />
			<jsp:param name="msgRaggiuntoMax" value="i documenti"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
			<jsp:param name="tipologia" value="${tipologia}"/>
			<jsp:param name="firmaRemota" value="${firmaRemota}"/>
			<jsp:param name="firmaDocumento" value="${firmaDocumento}"/>
			<jsp:param name="funzEliminazione" value="delDocumento"/>
			<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
			<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}"/>
		</jsp:include>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.DOCUMGARA.DOCUMGARA")}'/>
	</c:if>
	<c:if test='${gruppo eq 6}'>
	<c:set var="condizioneBloccoAllmail" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsInvitoPubblicatoFunction", pageContext,codiceGara)}'/>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DOCUMGARA'/>
			<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='documentiInvito' />
			<jsp:param name="idProtezioni" value="DOCUMINVITI" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/documenti-invito.jsp"/>
			<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','W_DOCDIG_IDPRG_','DOCUMGARA_TIPOLOGIA_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','DOCUMGARA_IDSTAMPA','NOMEDOCGEN_','DOCUMGARA_ALLMAIL_','DOCUMGARA_URLDOC_','tipoAllegato_','W_DOCDIG_DIGFIRMA_'"/>		
			<jsp:param name="titoloSezione" value="Documento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo documento" />
			<jsp:param name="descEntitaVociLink" value="documento" />
			<jsp:param name="msgRaggiuntoMax" value="i documenti"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
			<jsp:param name="tipologia" value="${tipologia}"/>
			<jsp:param name="firmaRemota" value="${firmaRemota}"/>
			<jsp:param name="firmaDocumento" value="${firmaDocumento}"/>
			<jsp:param name="funzEliminazione" value="delDocumento"/>
			<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
			<jsp:param name="condizioneBloccoAllmail" value="${condizioneBloccoAllmail}"/>
			<jsp:param name="isProceduraTelematica" value="${isProceduraTelematica}"/>
			<jsp:param name="numeroDocumentoWSDM" value="${numeroDocumentoWSDM}"/>
			<jsp:param name="gestioneERP" value="${gestioneERP}"/>
			<jsp:param name="lottoFaseInvito" value="${lottoFaseInvito}"/>
		</jsp:include>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.DOCUMGARA.DOCUMGARA")}'/>
	</c:if>
	
	<c:if test='${gruppo eq 10 or gruppo eq 15}'>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='DOCUMGARA'/>
			<jsp:param name="chiave" value='${codiceGara};${ngara}'/>
			<jsp:param name="nomeAttributoLista" value='atti' />
			<jsp:param name="idProtezioni" value="ATTI" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/documgara/atti-documenti.jsp"/>
			<jsp:param name="arrayCampi" value="'DOCUMGARA_CODGAR_', 'DOCUMGARA_NGARA_', 'DOCUMGARA_NORDDOCG_','DOCUMGARA_GRUPPO_', 'DOCUMGARA_FASGAR_', 'DOCUMGARA_IDPRG_', 'DOCUMGARA_IDDOCDG_','DOCUMGARA_DESCRIZIONE_','DOCUMGARA_STATODOC_','DOCUMGARA_VALENZA_','DOCUMGARA_URLDOC_','DOCUMGARA_TIPOLOGIA_','W_DOCDIG_IDPRG_','W_DOCDIG_IDDOCDIG_','W_DOCDIG_DIGDESDOC_','W_DOCDIG_DIGNOMDOC_','selezioneFile_','tipoAllegato_','W_DOCDIG_DIGFIRMA_','DOCUMGARA_DATARILASCIO_', 'DOCUMGARA_DATAPROV_', 'DOCUMGARA_NUMPROV_'"/>		
			<jsp:param name="titoloSezione" value="Documento" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo documento" />
			<jsp:param name="descEntitaVociLink" value="documento" />
			<jsp:param name="msgRaggiuntoMax" value="i documenti"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
			<jsp:param name="gruppo" value="${gruppo}"/>
			<jsp:param name="tipologia" value="${tipologia}"/>
			<jsp:param name="firmaRemota" value="${firmaRemota}"/>
			<jsp:param name="firmaDocumento" value="${firmaDocumento}"/>
			<jsp:param name="funzEliminazione" value="delDocumento"/>
			<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
			<jsp:param name="numeroDocumentoWSDM" value="${numeroDocumentoWSDM}"/>
			<jsp:param name="gestioneERP" value="${gestioneERP}"/>
		</jsp:include>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.DOCUMGARA.DOCUMGARA")}'/>
	</c:if>

	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.TORN_CODGAR}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
	
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:ConfermaModifica();">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:ConfermaAnnulla();">
				</gene:insert>
		
			</c:when>
			<c:otherwise>
				<c:if test='${numDocAttesaFirma > 0}'>
					<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:historyReload();">
				</c:if>
				<gene:insert name="pulsanteModifica">
					<c:if test='${autorizzatoModifiche ne "2" and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and condizioneModificaSezioneProfilo eq true}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<c:if test="${autorizzatoModifiche ne '2' and isIntegrazionePortaleAlice eq 'true' and gruppo eq 2 and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.PubblicaSuPortale')}">
						<INPUT type="button"  class="bottone-azione" value='Pubblica su portale Appalti' title='Pubblica su portale Appalti' onclick="javascript:pubblicaSuPortaleAppalti();">
				</c:if>
				<gene:insert name="pulsanteIndietro">
					<c:if test='${gruppo ne 2 and gruppo ne 3}'>
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco atti' title='Torna a elenco atti' onclick="javascript:historyVaiIndietroDi(1);">
					</c:if>
					<c:if test='${gruppo eq 3 and empty garaElencoCatalogo}'>
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco buste' title='Torna a elenco buste' onclick="javascript:historyVaiIndietroDi(1);">
					</c:if>
					<c:if test='${gruppo eq 3 and garaElencoCatalogo}'>
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco tipologie' title='Torna a elenco tipologie' onclick="javascript:historyVaiIndietroDi(1);">
					</c:if>
				</gene:insert>
				
			</c:otherwise>
			</c:choose>
		</td>
	
	</gene:campoScheda>
	<gene:redefineInsert name="addToAzioni" >
		<c:choose>
			<c:when test='${modo eq "VISUALIZZA" and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and condizioneModificaSezioneProfilo eq true and autorizzatoModifiche ne "2" and gruppo eq 2}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:schedaModifica()" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1505">
						</c:if>
							${gene:resource("label.tags.template.dettaglio.schedaModifica")}
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<c:if test="${autorizzatoModifiche eq '2' or !condizioneModificaSezioneProfilo}">
						<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
				</c:if>
			</c:otherwise>
		</c:choose>	
		<c:if test='${empty garaElencoCatalogo and genereGara ne 11 and gruppo ne 10 and gruppo ne 15 and gruppo ne 5 and autorizzatoModifiche ne "2" and !condizioneFunzArchivia and modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.InsertPredefiniti")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:apriPopupInsertPredefiniti()" title="Inserisci documenti predefiniti" tabindex="1505">
					</c:if>
						Inserisci documenti predefiniti
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>	
		<c:if test='${gruppo eq 4 and autorizzatoModifiche ne "2" and !condizioneFunzArchivia and modoAperturaScheda eq "VISUALIZZA" and gruppo15Visibile eq "true" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.InsertDelibereAContrare")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:apriPopupInsertGruppo15('${codiceGara}','${tipologia}', '${ngara }');" title="Inserisci documenti da delibera a contrarre" tabindex="1506">
					</c:if>
						Inserisci documenti da delibera a contrarre
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>	
		<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ArchiviaDocumenti") and condizioneFunzArchivia and modoAperturaScheda eq "VISUALIZZA"}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:set var="titoloArchivia" value="Archivia documenti"/>
					<c:if test="${gruppo eq 2}">
						<c:set var="titoloArchivia" value="Archivia requisiti"/>
					</c:if>
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:archiviaDocumenti()" title="${titoloArchivia}" tabindex="1507">
					</c:if>
						${titoloArchivia}
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ModificaOrdinamentoDocumentazione")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:modificaOrdinam()" title='Modifica disposizione documenti' tabindex="1509">
					</c:if>
						Modifica disposizione documenti
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${numDocAttesaFirma > 0 and modoAperturaScheda eq "VISUALIZZA"}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:rileggiDati()" title='Rileggi dati' tabindex="1510">
					</c:if>
						Rileggi dati
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>	
		<c:if test='${autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and isIntegrazionePortaleAlice eq "true" and gruppo eq 2 
						and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PubblicaSuPortale")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:pubblicaSuPortaleAppalti();" title='Pubblica su portale Appalti' tabindex="1500">
					</c:if>
						Pubblica su portale Appalti
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${insDocDaProtocollo eq  "1" and autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.InserisciDocDaProtocollo")}'>
			
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:apriInsDocDaProtocollo();" title='Inserisci documenti da protocollo' tabindex="1511">
					</c:if>
						Inserisci documenti da protocollo
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
			
		</c:if>
		<c:if test="${not empty integrazioneMDgueUrl and integrazioneMDgueUrl ne '' and (gruppo eq 1 or gruppo eq 6) and modoAperturaScheda eq 'VISUALIZZA'  and autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.InsertDocDGUE')}">
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:insDocumentoMDGUE('${ngara}','${codiceGara}','${gruppo}','${isProceduraTelematica}','${tipologia}');" title='Inserisci documento con M-DGUE' tabindex="1512">
					</c:if>
						Inserisci documento con M-DGUE
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="modelliPredisposti" />
	
	<c:if test='${gruppo ne 2 and !(gruppo eq 3 and garaElencoCatalogo)}'>
		<gene:redefineInsert name="documentiAssociati" />
		<gene:redefineInsert name="noteAvvisi" />
	</c:if>
	
<gene:javaScript>

document.forms[0].encoding="multipart/form-data";

//Apertura popup per l'inserimento dei documenti predefiniti
function apriPopupInsertPredefiniti() {
	var lottoDiGara = 0;
	
	<c:if test="${lottoDiGara}">
		lottoDiGara = 1;
	</c:if>
	var tipgen=getValue("TORN_TIPGEN");
	var tipgarg=getValue("GARE_TIPGARG");
	var importo=getValue("GARE_IMPAPP");
	var critlic=getValue("GARE_CRITLICG");
	var tipologia = "${tipologia }";
	var busta = "${busta }";
	var gruppo = "${gruppo}";
	var isProceduraTelematica ="${isProceduraTelematica }";
	var href = "href=gare/documgara/conferma-ins-doc-predefiniti.jsp?codgar="+getValue("GARE_CODGAR1")+"&ngara="+getValue("GARE_NGARA")+"&lottoDiGara=" + lottoDiGara;
	href += "&tiplav=" +  tipgen + "&tipgarg=" +tipgarg + "&importo=" + importo + "&critlic=" + critlic+ "&isProceduraTelematica=" + isProceduraTelematica + "&gruppo=" + gruppo + "&tipologia=" + tipologia;
	if(busta){
		href = href + "&busta=" + busta;
	}
	openPopUpCustom(href, "insDocumentiPredefiniti", 700, 450, "no", "yes");
}

function ConfermaModifica(){
	document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
	var gruppo = "${gruppo}";
	<c:if test='${gruppo eq 6}'>
		$('[id^="DOCUMGARA_ALLMAIL_"]').attr('disabled',false);
	</c:if>
	schedaConferma();
	
}
 
function ConfermaAnnulla(){
	document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
	schedaAnnulla();
}
 
function scegliFile(indice) {
	var firmaDocumento="${firmaDocumento}";
	if(firmaDocumento=='1'){
		var digfirma = getValue("W_DOCDIG_DIGFIRMA_" + indice);
		if(digfirma==1){
			alert("Il documento è in attesa di firma, non è possibile modificare il file");
			document.getElementById("selFile[" + indice + "]").value = "";
			return;
		}
	}
	var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
	var lunghezza_stringa=selezioneFile.length;
	var posizione_barra=selezioneFile.lastIndexOf("\\");
	var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
	var gruppo="${gruppo}";
	var idstampa="";
	if(document.getElementById("DOCUMGARA_IDSTAMPA_" + indice ))
		idstampa = document.getElementById("DOCUMGARA_IDSTAMPA_" + indice ).value;
	if(idstampa=="DGUE" && (gruppo=="6" || gruppo=="1")){
		if(!controlloTipoFile(nome,"xml")){
			alert("Il formato del file selezionato non è valido.\nE' consentito solo il formato xml");
			document.getElementById("selFile[" + indice + "]").value="";
			setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
			return;
		}
	} else if(gruppo=="6"){
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
	var gruppo="${gruppo}";
	if(gruppo=="6"){
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

function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
	var key = '${ngara}';
	var gruppo = '${gruppo}';
	var busta = '${busta}';
	var tipologia = '${tipologia}';
	var vet = dignomdoc.split(".");
	var ext = vet[vet.length-1];
	ext = ext.toUpperCase();
	
	<c:choose>
		<c:when test="${digitalSignatureWsCheck eq 0}">
			if(ext=='P7M' || ext=='TSD'){
				document.formVisFirmaDigitale.idprg.value = idprg;
				document.formVisFirmaDigitale.iddocdig.value = iddocdig;
				tracciamentoDownloadDocgara(idprg, iddocdig,key,gruppo,busta,tipologia);
				document.formVisFirmaDigitale.submit();
			}else{
				tracciamentoDownloadDocgara(idprg, iddocdig,key,gruppo,busta,tipologia);
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
		</c:when>
		<c:otherwise>
			if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
				document.formVisFirmaDigitale.idprg.value = idprg;
				document.formVisFirmaDigitale.iddocdig.value = iddocdig;
				tracciamentoDownloadDocgara(idprg, iddocdig,key,gruppo,busta,tipologia);
				document.formVisFirmaDigitale.submit();
			}else{
				tracciamentoDownloadDocgara(idprg, iddocdig,key,gruppo,busta,tipologia);
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
			
		</c:otherwise>
	</c:choose>
	
	
	
}

function visualizzaFileGenerato(nomeFile) {
	var href = "${pageContext.request.contextPath}/DownloadTempFile.do";
	document.location.href = href + "?"+csrfToken+"&nomeTempFile=" + nomeFile;
}

<c:if test='${modo eq "MODIFICA" and gruppo eq 2}'>
	if(document.getElementById("bandoDocumenti")!=null)
		document.getElementById("bandoDocumenti").disabled=true;
	if(document.getElementById("documentazioneRichiesta")!=null)
		document.getElementById("documentazioneRichiesta").disabled=true;
	<c:if test='${genereGara eq "3"}'>
		if(document.getElementById("documentiEsito")!=null)
			document.getElementById("documentiEsito").disabled=true;
		if(document.getElementById("documentiTrasparenza")!=null)
			document.getElementById("documentiTrasparenza").disabled=true;
		if(document.getElementById("documentiInvio")!=null)
			document.getElementById("documentiInvio").disabled=true;
	</c:if>
	if(document.getElementById("atti")!=null)
		document.getElementById("atti").disabled=true;
</c:if> 

<c:if test='${modo eq "MODIFICA" and gruppo eq 1}'>
	if(document.getElementById("documentazioneRichiesta")!=null)
		document.getElementById("documentazioneRichiesta").disabled=true;
	if(document.getElementById("requisitiRichiesti")!=null)
		document.getElementById("requisitiRichiesti").disabled=true;
	<c:if test='${genereGara eq "3"}'>
		if(document.getElementById("documentiEsito")!=null)
			document.getElementById("documentiEsito").disabled=true;
		if(document.getElementById("documentiTrasparenza")!=null)
			document.getElementById("documentiTrasparenza").disabled=true;
		if(document.getElementById("documentiInvio")!=null)
			document.getElementById("documentiInvio").disabled=true;
	</c:if>
	if(document.getElementById("atti")!=null)
		document.getElementById("atti").disabled=true;
</c:if> 

<c:if test='${modo eq "MODIFICA" and gruppo eq 3}'>
	if(document.getElementById("requisitiRichiesti")!=null)
		document.getElementById("requisitiRichiesti").disabled=true;
	if(document.getElementById("bandoDocumenti")!=null)
		document.getElementById("bandoDocumenti").disabled=true;
	<c:if test='${genereGara eq "3"}'>
		if(document.getElementById("documentiEsito")!=null)
			document.getElementById("documentiEsito").disabled=true;
		if(document.getElementById("documentiTrasparenza")!=null)
			document.getElementById("documentiTrasparenza").disabled=true;
		if(document.getElementById("documentiInvio")!=null)
			document.getElementById("documentiInvio").disabled=true;
	</c:if>
	if(document.getElementById("atti")!=null)
		document.getElementById("atti").disabled=true;
</c:if> 

<c:if test='${modo eq "MODIFICA" and gruppo eq 4 and genereGara eq "3"}'>
	if(document.getElementById("requisitiRichiesti")!=null)
		document.getElementById("requisitiRichiesti").disabled=true;
	if(document.getElementById("bandoDocumenti")!=null)
		document.getElementById("bandoDocumenti").disabled=true;
	if(document.getElementById("documentazioneRichiesta")!=null)
		document.getElementById("documentazioneRichiesta").disabled=true;
	if(document.getElementById("documentiTrasparenza")!=null)
		document.getElementById("documentiTrasparenza").disabled=true;
	if(document.getElementById("documentiInvio")!=null)
		document.getElementById("documentiInvio").disabled=true;
	if(document.getElementById("atti")!=null)
		document.getElementById("atti").disabled=true;
</c:if> 

<c:if test='${modo eq "MODIFICA" and gruppo eq 5 and genereGara eq "3"}'>
	if(document.getElementById("bandoDocumenti")!=null)
		document.getElementById("bandoDocumenti").disabled=true;
	if(document.getElementById("requisitiRichiesti")!=null)
		document.getElementById("requisitiRichiesti").disabled=true;
	if(document.getElementById("documentiEsito")!=null)
		document.getElementById("documentiEsito").disabled=true;
	if(document.getElementById("documentazioneRichiesta")!=null)
		document.getElementById("documentazioneRichiesta").disabled=true;
	if(document.getElementById("documentiInvio")!=null)
		document.getElementById("documentiInvio").disabled=true;
	if(document.getElementById("atti")!=null)
		document.getElementById("atti").disabled=true;
</c:if> 

<c:if test='${modo eq "MODIFICA" and gruppo eq 6 and genereGara eq "3"}'>
	if(document.getElementById("bandoDocumenti")!=null)
		document.getElementById("bandoDocumenti").disabled=true;
	if(document.getElementById("requisitiRichiesti")!=null)
		document.getElementById("requisitiRichiesti").disabled=true;
	if(document.getElementById("documentiEsito")!=null)
		document.getElementById("documentiEsito").disabled=true;
	if(document.getElementById("documentazioneRichiesta")!=null)
		document.getElementById("documentazioneRichiesta").disabled=true;
	if(document.getElementById("documentiTrasparenza")!=null)
		document.getElementById("documentiTrasparenza").disabled=true;
	if(document.getElementById("atti")!=null)
		document.getElementById("atti").disabled=true;	
</c:if>

<c:if test='${modo eq "MODIFICA" and (gruppo eq 10 or gruppo eq 15)}'>
	if(document.getElementById("bandoDocumenti")!=null)
		document.getElementById("bandoDocumenti").disabled=true;
	if(document.getElementById("requisitiRichiesti")!=null)
		document.getElementById("requisitiRichiesti").disabled=true;
	if(document.getElementById("documentiEsito")!=null)
		document.getElementById("documentiEsito").disabled=true;
	if(document.getElementById("documentazioneRichiesta")!=null)
		document.getElementById("documentazioneRichiesta").disabled=true;
	if(document.getElementById("documentiTrasparenza")!=null)
		document.getElementById("documentiTrasparenza").disabled=true;
	if(document.getElementById("documentiInvio")!=null)
		document.getElementById("documentiInvio").disabled=true;	
</c:if>

	<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and gestioneUrl eq "true" and (gruppo eq 1 or gruppo eq 4 or gruppo eq 6 or gruppo eq 10 or gruppo eq 15)}'>
		
	var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
	function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
		showNextElementoSchedaMultipla_Default(tipo, campi,visibilitaCampi);
		var indice = eval("lastId" + tipo + "Visualizzata");
		$("#rowDOCUMGARA_URLDOC_" + indice).hide();
		$('#DOCUMGARA_ALLMAIL_' + indice).attr('disabled',false);
	}
	showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
</c:if>
	
	function archiviaDocumenti(){
		var titoloTipologia = $("#G1CF_PUBB_NOME").val();
		var titoloBusta = "${titoloBusta}";
		var gruppo = "${gruppo }";
		var codgar1 = getValue("GARE_CODGAR1");
		var ngara = getValue("GARE_NGARA");
		var genereGara = "${genereGara }";
		var iterga = getValue("TORN_ITERGA");
		var modlic = getValue("GARE_MODLICG");
		var lottoDiGara = 0;
		var tipologia = "${tipologia}";
		var busta = "${busta}";
		var fasEle = "${fasEle}";
	<c:if test="${lottoDiGara}">
		lottoDiGara = 1;
	</c:if>
		var isProceduraTelematica ="${isProceduraTelematica }";
		var href = "href=gare/commons/popup-ArchiviaDocumentazione.jsp?tipoDoc=" + gruppo + "&codgar1=" + codgar1 + "&ngara=" + ngara + "&genereGara=" + genereGara;
		href += "&isarchi=1&lottoDiGara=" + lottoDiGara + "&iterga=" + iterga + "&modlic=" + modlic + "&isProceduraTelematica=" + isProceduraTelematica + "&tipologia=" + tipologia;
		if(tipologia){
			href += "&titolo=" + titoloTipologia;	
		}
		if(busta){
			href = href + "&busta=" + busta + "&titolo=" + titoloBusta;
		}
		if(fasEle){
			href = href + "&fasEle=" + fasEle + "&titolo=" + titoloBusta;
		}		
		href += "&valtec=" + getValue("GARE1_VALTEC");
		var sezionitec = "${sezionitec}";
		if (sezionitec != "" )
			href += "&sezionitec=" + sezionitec;
		openPopUpCustom(href, "archiviazioneDocumenti", 900, 500, "no", "yes");
	}
	
	function modificaOrdinam(){
		var gruppo = "${gruppo }";
		var codgar1 = getValue("GARE_CODGAR1");
		var ngara = getValue("GARE_NGARA");
		var genereGara = "${genereGara }";
		var tipologia = "${tipologia}";
		var busta = "${busta}";
		var titoloTipologia = $("#G1CF_PUBB_NOME").val();
		var titoloBusta = "${titoloBusta}";
		var fasEle = "${fasEle}";
		var href = "href=gare/commons/popup-ModificaOrdinamentoDocumentazione.jsp";
		href+="?tipoDoc=" + gruppo + "&codgar1=" + codgar1 + "&ngara=" + ngara + "&genereGara=" + genereGara + "&tipologia=" + tipologia;
		if(tipologia){
			href = href + "&titolo=" + titoloTipologia;
		}
		if(busta){
			href = href + "&busta=" + busta + "&titolo=" + titoloBusta;
		}
		if(fasEle){
			href = href + "&fasEle=" + fasEle + "&titolo=" + titoloBusta;
		}
		//alert(href);
		openPopUpCustom(href, "modificaOrdinamDocumgara", 850, 550, "yes", "yes");		
		//modificaOrdinamDocumgara(gruppo, codgar1, ngara, genereGara, lottoDiGara, iterga, modlic, isProceduraTelematica);
	}
	

	function delDocumento(id, label, tipo, campi){
		var tippub = getValue("DOCUMGARA_STATODOC_" + id);
		if(tippub == 5)
			alert("Non è possibile eliminare un documento già pubblicato")
		else
			delElementoSchedaMultipla(id,label,tipo,campi);
	}
	
	function validURL(str) {
		if(str==""){
			return true;
		}else{
			var res = /^(((http|HTTP|https|HTTPS|ftp|FPT|ftps|FTPS|sftp|SFTP):\/\/)|((w|W){3}(\d)?\.))[\w\?!\./:;\-_=#+*%@&quot;\(\)&amp;]+/.test(str);
			return res;
		}
	}
	
	<c:if test='${numDocAttesaFirma > 0}'>
		function rileggiDati(){
			document.forms[0].append('<input type="hidden" name="firstTimer" value="true" />');
			historyReload();
		}
	</c:if>	
	
	
		
</gene:javaScript>


