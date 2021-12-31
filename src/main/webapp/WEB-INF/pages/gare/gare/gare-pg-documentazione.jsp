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


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>

<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<c:set var="valtec" value='${gene:getValCampo(key, "GARE1.VALTEC")}'/>
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="tipoDoc"  value="${param.tipoDoc}" />
<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>
<c:set var="firmaRemota" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "firmaremota.auto.url")}'/>

<c:set var="numeroDocumentoWSDM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckAssociataRdaJIRIDEFunction", pageContext, codiceGara)}' scope="request"/>

<c:if test="${genereGara eq '10' or genereGara eq '20' }">
	<c:set var="garaElencoCatalogo" value='true'/>
</c:if>

<gene:formScheda entita="GARE" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDocumentazioneGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDocumentazioneGara">

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<gene:campoScheda campo="NGARA"  visibile="false" />
	<gene:campoScheda campo="CODGAR1"  visibile="false" />
	<gene:campoScheda campo="TIPGARG" visibile="false"/>
	<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="VALTEC" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
	<gene:campoScheda campo="IMPAPP" visibile="false"/>
	<gene:campoScheda campo="MODLICG" visibile="false"/>
	<gene:campoScheda campo="CRITLICG" visibile="false"/>
	
	<c:set var="condizioniBando" value='${gene:checkProt(pageContext,"SEZ.VIS.GARE.GARE-scheda.DOCUMGARA.DOCUMGARA")}'/>
		
	<c:set var="condizioniRequisiti" value='${gene:checkProt(pageContext,"SEZ.VIS.GARE.GARE-scheda.DOCUMGARA.DOCUMREQ") and itergaMacro !=3 }'/>
	<c:set var="condizioniDocumentazioneRichiesta" value='${gene:checkProt(pageContext,"SEZ.VIS.GARE.GARE-scheda.DOCUMGARA.DOCUMCONC")}'/>
		
	<gene:campoScheda>
			<td colspan="2">
				<c:if test='${condizioniBando}'>
					<input type="radio" value="1" name="filtroDocumentazione" id="atti" <c:if test='${tipoDoc ne 2 and tipoDoc ne 3}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione('1');" />
					 Documenti e atti
				</c:if>
				<c:if test='${condizioniBando }'>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:if>
				<c:if test='${condizioniRequisiti}'>
				<input type="radio" value="2" name="filtroDocumentazione" id="requisitiRichiesti" <c:if test='${tipoDoc eq 2}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione(2);" />
				<c:choose>
					<c:when test="${genereGara eq '10' or genereGara eq '20'}">
						Requisiti degli operatori
					</c:when>
					<c:otherwise>
						Requisiti dei concorrenti
					</c:otherwise>
				</c:choose>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:if>
				<c:if test='${condizioniDocumentazioneRichiesta }'>
				<input type="radio" value="3" name="filtroDocumentazione" id="documentazioneRichiesta" <c:if test='${tipoDoc eq 3}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione(3);" />
				<c:choose>
					<c:when test="${genereGara eq '10' or genereGara eq '20'}">
						Documenti richiesti agli operatori
					</c:when>
					<c:otherwise>
						Documenti richiesti ai concorrenti
					</c:otherwise>
				</c:choose>
				</c:if>
				<c:if test="${condizioniRequisiti || condizioniDocumentazioneRichiesta }">
					<br>
				</c:if>
			</td>
	</gene:campoScheda>	
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	

	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>

	<c:choose>
		<c:when test="${tipoDoc eq 3 and garaElencoCatalogo eq 'true'}">
			<jsp:include page="/WEB-INF/pages/gare/documgara/sezione-documenti.jsp">
				<jsp:param name="codiceGara" value="${codiceGara}"/>
				<jsp:param name="ngara" value="${numeroGara}"/>
				<jsp:param name="gruppo" value="${tipoDoc}"/>
				<jsp:param name="tipoDoc" value="${tipoDoc}"/>
				<jsp:param name="bustaLotti" value="${bustaLotti}"/>
				<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
				<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
				<jsp:param name="isProceduraTelematica" value="${isProceduraTelematica}"/>
			</jsp:include>
		</c:when>
		<c:when test="${tipoDoc ne 2}">
			<jsp:include page="/WEB-INF/pages/gare/documgara/elenco-documenti-albero.jsp">
				<jsp:param name="numeroGara" value="${numeroGara}"/>
				<jsp:param name="codiceGara" value="${codiceGara}"/>
				<jsp:param name="gruppo" value="${tipoDoc}"/>
				<jsp:param name="tipoDoc" value="${tipoDoc}"/>
				<jsp:param name="genere" value="${genereGara}"/>
				<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
			</jsp:include>
		</c:when>
		<c:otherwise>
			<jsp:include page="/WEB-INF/pages/gare/documgara/sezione-documenti.jsp">
				<jsp:param name="codiceGara" value="${codiceGara}"/>
				<jsp:param name="ngara" value="${numeroGara}"/>
				<jsp:param name="gruppo" value="${tipoDoc}"/>
				<jsp:param name="tipoDoc" value="${tipoDoc}"/>
				<jsp:param name="bustaLotti" value="${bustaLotti}"/>
				<jsp:param name="richiestaFirma" value="${richiestaFirma}"/>
				<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
				<jsp:param name="isProceduraTelematica" value="${isProceduraTelematica}"/>
			</jsp:include>
		</c:otherwise>
	</c:choose>
	

</gene:formScheda>

<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />

<gene:javaScript>

var idconfi = "${idconfi}";

document.forms[0].encoding="multipart/form-data";

function cambiaTipoDocumentazione(tipoDoc){
	document.forms[0].metodo.value="apri";
	document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
	bloccaRichiesteServer();
	document.forms[0].action += "&tipoDoc=" + tipoDoc;
	document.forms[0].submit();
	
}

function visualizzaDocumenti(ngara,tipoPubblicazione,gruppo){
	var href = contextPath + "/ApriPagina.do?href=gare/documgara/documenti-tipologia.jsp";
	formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + ngara;
	formVisualizzaDocumenti.tipologiaDoc.value = tipoPubblicazione;
	formVisualizzaDocumenti.gruppo.value = gruppo;
	formVisualizzaDocumenti.submit();
}

function visualizzaDocumentiConcorrenti(ngara,busta,titoloBusta){
	var href = contextPath + "/ApriPagina.do?href=gare/documgara/documenti-tipologia.jsp";
	formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + ngara;
	formVisualizzaDocumenti.busta.value = busta;
	formVisualizzaDocumenti.titoloBusta.value = titoloBusta;
	formVisualizzaDocumenti.gruppo.value = 3;
	formVisualizzaDocumenti.submit();
}
function pubblicaSuPortaleAppalti(){
	var href = "href=gare/commons/popup-pubblica-portale.jsp?codiceGara=${codiceGara}&ngara=${numeroGara}&gruppo=${gruppo}&tipologiaDoc=${tipoDoc}&valtec=${datiRiga.GARE1_VALTEC}&isProceduraTelematica=${isProceduraTelematica}&entita=GARE&genereGara=${genereGara}";
	if(idconfi){
		href = href + "&idconfi="+idconfi;
	}
	openPopUpCustom(href, "insDocumentiPredefiniti", 800, 650, "no", "yes");
}
	
function visAllegatiRda(codice){
	bloccaRichiesteServer();
	formAllegatiRda.codice.value = codice;
	formAllegatiRda.genere.value = "2";
	formAllegatiRda.submit();
}

</gene:javaScript>

<form name="formAllegatiRda" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/commons/lista-allegati-rda-scheda.jsp" /> 
	<input type="hidden" name="codice" id="codice" value="" />
	<input type="hidden" name="genere" id="genere" value="" />
</form> 

<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
	<input type="hidden" name="idprg" id="idprg" value="" />
	<input type="hidden" name="iddocdig" id="iddocdig" value="" />
</form>

<form name="formVisualizzaDocumenti" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/documgara/documenti-tipologia.jsp" /> 
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="codiceGara" value="${codiceGara}" />
	<input type="hidden" name="tipologiaDoc" value="" />
	<input type="hidden" name="gruppo" value="" />
	<input type="hidden" name="busta" value="" />
	<input type="hidden" name="titoloBusta" value="" />
	<input type="hidden" name="firstTimer" value="true" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />	
	<input type="hidden" name="isProceduraTelematica" value="${isProceduraTelematica}" />	
	<input type="hidden" name="idconfi" value="${idconfi}" />	
</form> 

<form name="formInviaAttiSCP" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/documgara/lista-atti-scp.jsp" />
	<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}" />
	<input type="hidden" name="ngara" value="${numeroGara}" />
	<input type="hidden" name="genere" value="${genereGara}" />
</form>