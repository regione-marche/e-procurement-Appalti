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

<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<c:set var="dollaro" value="$"/>
<c:set var="codiceGara" value="${dollaro }${numeroGara }"/>

<c:set var="bloccoPubblicazionePortale" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO","false")}' />

<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>

<gene:formScheda entita="GAREAVVISI" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDocumentazioneGara">

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
	
	<jsp:include page="/WEB-INF/pages/gare/documgara/elenco-documenti-albero.jsp">
		<jsp:param name="numeroGara" value="${numeroGara}"/>
		<jsp:param name="codiceGara" value="${codiceGara}"/>
		<jsp:param name="genere" value="11"/>
		<jsp:param name="autorizzatoModifiche" value="${autorizzatoModifiche}"/>
		<jsp:param name="tipoDoc" value="1"/>
	</jsp:include>
	
	<input type="hidden" name="filtroDocumentazione" id="filtroDocumentazione" value="1"/>
	
</gene:formScheda>


<gene:javaScript>

document.forms[0].encoding="multipart/form-data";

var idconfi = "${idconfi}";

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
	document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
	schedaConferma();
}
 
function ConfermaAnnulla(){
	document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
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
