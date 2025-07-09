<%
/*
 * Created on: 24/05/2012
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
<c:set var="dollaro" value="$"/>
<c:set var="codiceGara" value="${dollaro }${numeroGara }"/>
<c:set var="tipoDoc"  value="${param.tipoDoc}" />

<c:set var="bloccoPubblicazionePortale" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO","false")}' />

<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>

<c:set var="isPaginaDocumentazione" value='true'/>

<gene:formScheda entita="GAREAVVISI" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDocumentazioneGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDocumentazioneGara">

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	
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
	
	<gene:campoScheda campo="NGARA"  visibile="false" />
	<gene:campoScheda campo="CODGAR"  visibile="false" />
	<gene:campoScheda campo="CODGAR1" entita="GARE" campoFittizio="true" definizione="T21" visibile="false" value="${datiRiga.GAREAVVISI_CODGAR}"/>
	<gene:campoScheda campo="ITERGA" entita="TORN" where="GAREAVVISI.CODGAR = TORN.CODGAR" visibile="false"/>
	<gene:campoScheda campo="MODLICG" entita="GARE" where="GAREAVVISI.CODGAR = GARE.CODGAR1 and GAREAVVISI.NGARA = GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="VALTEC" entita="GARE1" where="GAREAVVISI.CODGAR = GARE1.CODGAR1 and GAREAVVISI.NGARA = GARE1.NGARA" visibile="false"/>
	
	<c:choose>
	<c:when test="${not empty vernpa and gene:callFunctionN('it.eldasoft.sil.pg.tags.funzioni.GetSeAttivoModuloANACFormFunction', pageContext, datiRiga.GAREAVVISI_CODGAR) eq 'true'}">
		<gene:campoScheda>
		<td colspan="2">
			<input type="radio" value="0" name="filtroDocumentazione" id="anacFormPCP" <c:if test='${tipoDoc eq 0}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione(0);" />
			 Schede ANAC
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
			<input type="radio" value="1" name="filtroDocumentazione" id="atti" <c:if test='${tipoDoc ne 2 and tipoDoc ne 3 and tipoDoc ne 0}'>checked="checked"</c:if> onclick="javascript:cambiaTipoDocumentazione('1');" />
			 Documenti e atti
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
		</gene:campoScheda>
	</c:when>

	<c:otherwise>
		<input type="hidden" name="filtroDocumentazione" id="filtroDocumentazione" value="1"/>
	</c:otherwise>
	</c:choose>
	
	<jsp:include page="/WEB-INF/pages/gare/documgara/elenco-documenti-albero.jsp">
		<jsp:param name="numeroGara" value="${numeroGara}"/>
		<jsp:param name="codiceGara" value="${codiceGara}"/>
		<jsp:param name="genere" value="11"/>
		<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
		<jsp:param name="tipoDoc" value="${tipoDoc}"/>
		<jsp:param name="gruppo" value="${tipoDoc}"/>
	</jsp:include>
	
</gene:formScheda>


<gene:javaScript>

document.forms[0].encoding="multipart/form-data";

function cambiaTipoDocumentazione(tipoDoc){
	document.forms[0].metodo.value="apri";
	document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
	bloccaRichiesteServer();
	document.forms[0].action += "?tipoDoc=" + tipoDoc;
	document.forms[0].encoding="";
	document.forms[0].submit();
}

var idconfi = "${idconfi}";

function visualizzaFormNPA(idForm,islotti,modpres) {
	formVisualizzaFormNPA.key.value = "G1NPAFORM.ID=N:" + idForm;
	formVisualizzaFormNPA.idForm.value = idForm;
	formVisualizzaFormNPA.islotti.value = islotti;
	formVisualizzaFormNPA.modpres.value = modpres;
	formVisualizzaFormNPA.submit();
}

function visualizzaDocumenti(ngara,tipoPubblicazione,gruppo){
	var href = contextPath + "/ApriPagina.do?href=gare/documgara/documenti-tipologia.jsp";
	formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + ngara;
	formVisualizzaDocumenti.tipologiaDoc.value = tipoPubblicazione;
	formVisualizzaDocumenti.gruppo.value = gruppo;
	formVisualizzaDocumenti.submit();
}
function pubblicaSuPortaleAppalti(){
	var href = "href=gare/commons/popup-pubblica-portale.jsp?codiceGara=${codiceGara}&ngara=${numeroGara}&gruppo=${gruppo}&tipologiaDoc=${tipoDoc}&valtec=${datiRiga.GARE1_VALTEC}&isProceduraTelematica=${isProceduraTelematica}&entita=GARE&genereGara=11";
	if(idconfi){
		href = href + "&idconfi="+idconfi;
	}
	href = href + "&garavviso=1";
	openPopUpCustom(href, "insDocumentiPredefiniti", 800, 650, "no", "yes");
}

function ConfermaModifica(){
	document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do";
	schedaConferma();
}
 
function ConfermaAnnulla(){
	document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do";
	schedaAnnulla();
}
 
function scegliFile(indice) {
	var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
	var lunghezza_stringa=selezioneFile.length;
	var posizione_barra=selezioneFile.lastIndexOf("\\");
	var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
	if(nome.length>100){
		alert("Il nome del file non può superare i 100 caratteri!");
		document.getElementById("selFile[" + indice + "]").value="";
		setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
	}else{
		setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
		$("#spanRichiestaFirma_" + indice).show();
	}
	
}



<c:if test='${(modo eq "MODIFICA" or modo eq "NUOVO") and gestioneUrl eq "true" }'>
		
	var showNextElementoSchedaMultipla_Default = showNextElementoSchedaMultipla;
	function showNextElementoSchedaMultipla_Custom(tipo, campi,visibilitaCampi){
		showNextElementoSchedaMultipla_Default(tipo, campi,visibilitaCampi);
		var indice = eval("lastId" + tipo + "Visualizzata");
		$("#rowDOCUMGARA_URLDOC_" + indice).hide();
		
	}
	showNextElementoSchedaMultipla =   showNextElementoSchedaMultipla_Custom;
</c:if>

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

<form name="formVisualizzaFormNPA" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/g1npaform/g1npaform-scheda.jsp" /> 
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="codiceGara" value="${codiceGara}" />
	<input type="hidden" name="idForm" value="" />
	<input type="hidden" name="islotti" value="" />
	<input type="hidden" name="modpres" value="" />
</form>

<form name="formAggiungiAnacForm" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/g1npaform/aggiungiAnacForm.jsp" /> 
	<input type="hidden" name="codiceGara" value="${codiceGara}" />
	<input type="hidden" name="ngara" value="${ngara}" />
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
</form> 
