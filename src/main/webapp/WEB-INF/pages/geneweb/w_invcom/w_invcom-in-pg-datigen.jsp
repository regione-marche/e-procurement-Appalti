
<%
	/*
	 * Created on 26-Lug-2015
	 *
	 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.storico.risposte.comunicazioni.js"></script>
<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.trasmettioperatori.js"></script>
<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsd.trasmettiOperatori.css" >

<c:choose>
	<c:when test='${not empty param.keyAdd}'>
		<c:set var="keyAdd" value="${param.keyAdd}"  />
	</c:when>
	<c:otherwise>
		<c:set var="keyAdd" value="${keyAdd}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.chiaveWSDM}'>
		<c:set var="chiaveWSDM" value="${param.chiaveWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveWSDM" value="${chiaveWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.entitaWSDM}'>
		<c:set var="entitaWSDM" value="${param.entitaWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="entitaWSDM" value="${entitaWSDM}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.genere}'>
		<c:set var="genere" value="${param.genere}"  />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${genere}" />
	</c:otherwise>
</c:choose>

<c:set var="idprg" value='${gene:getValCampo(key,"IDPRG")}'/>
<c:set var="idcom" value='${gene:getValCampo(key,"IDCOM")}'/>
<c:set var="comkey1" value='${gene:getValCampo(keyAdd, "COMKEY1")}'/>
<c:set var="autorizzatoModificaComunicazione" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.AutorizzatoModificaComunicazioneFunction",pageContext,idprg,idcom,"false")}' />

<c:set var="where" value="IDPRG='${idprg }' and IDCOM=${idcom }"/>
<c:set var="commodello" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "COMMODELLO","W_INVCOM",where)}' scope="request"/>
<c:set var="coment" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "COMENT","W_INVCOM",where)}' scope="request"/>	
<c:if test='${coment ne "HOMEPG"}' >
	<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
	<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codgar)}' scope="request"/>
	<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
	<c:set var="iterga" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select iterga from torn where codgar=?", codgar)}'/>
	<c:set var="isfineconc" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select isfineconc from torn where codgar=?", codgar)}'/>
	<c:if test="${integrazioneWSDM eq 1 }" >
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
	</c:if>		
</c:if>

<gene:formScheda entita="W_INVCOM" gestisciProtezioni="true"
	gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAggiornaCOMDATLET">

	<gene:campoScheda campo="IDPRG" visibile="false"/>
	<gene:campoScheda campo="IDCOM" visibile="true" />
	<gene:campoScheda campo="USERKEY1" entita="W_PUSER" where="w_puser.usernome = w_invcom.comkey1" visibile="false"/>
	<c:set var="link" value='javascript:archivioImpresa("${datiRiga.W_PUSER_USERKEY1}");' />
	<gene:campoScheda campo="COMMITT" visibile="false"/>
	<gene:campoScheda campo="COMMITT_fittizio" href='${gene:if(not empty datiRiga.W_PUSER_USERKEY1 && !((iterga eq "9" || iterga eq "10") and isfineconc ne "1"), link, "")}' campoFittizio='true' definizione='T161;;;;COMMITT' visibile="true" value="${(iterga ne '9' || isfineconc eq '1' || (not empty datiRiga.W_INVCOM_COMMITT and gene:matches(datiRiga.W_INVCOM_COMMITT, '^operatore \\\d{5}$', false)))?datiRiga.W_INVCOM_COMMITT:'Dato anonimizzato'}"/>

	<gene:campoScheda campo="COMMODELLO" visibile="${!empty datiRiga.W_INVCOM_COMMODELLO}"/>
	<gene:campoScheda campo="COMENT" visibile="false"/>
	<gene:campoScheda campo="IDCFG" visibile="false"/>
	<gene:campoScheda campo="COMDATINS" definizione="D;0;;TIMESTAMP;COMDATINS"  title="Data e ora invio" />
	<gene:campoScheda campo="COMDATLET" />
	<gene:campoScheda campo="COMSYSLET" visibile="false"/>
	<gene:campoScheda campo="SYSUTE" entita="USRSYS" title="Letto da" definizione="T" where="USRSYS.SYSCON = W_INVCOM.COMSYSLET" visibile="${!empty datiRiga.W_INVCOM_COMSYSLET}"/>
	<gene:campoScheda campo="COMMSGOGG" />
	<gene:campoScheda campo="COMMSGTES" />
	<gene:campoScheda campo="COMNUMPROT" visibile='${integrazioneWSDM =="1" and !empty datiRiga.W_INVCOM_COMNUMPROT}' modificabile="false"/>
  	<gene:campoScheda campo="COMDATPROT" visibile='${integrazioneWSDM =="1" and !empty datiRiga.W_INVCOM_COMDATPROT}' modificabile="false"/>
  	<gene:campoScheda campo="COMTIPMA" visibile="${false}"/>
  	<gene:campoScheda campo="COMKEY1" visibile="${false}"/>
  	<gene:campoScheda campo="COMKEY2" visibile="${false}"/>
	<gene:campoScheda campo="COMKEY3" visibile="${false}"/>
  	<gene:campoScheda campo="COMETICH" visibile="${!empty datiRiga.W_INVCOM_COMETICH}" modificabile="false"/>
	<gene:campoScheda>
	<tr id="rigaElencoRisposte">
	
		<td></td>
		<td class="valore-dato"><span style="float:right"><a  href="javascript:showElencoRisposte();" id="elencoRisposteLabel">Visualizza comunicazioni di risposta</a></span></td>
	</tr>
	<tr id="rigaTabellaStoricoRisposte">
		<td colspan="2">
			<table id="tabellaStoricoRisposte" class="griglia" >
			
			</table>
		</td>
	</tr>

	<tr id="rigaElencoInoltri">
		<td></td>
		<td class="valore-dato"><span style="float:right"><a  href="javascript:showElencoInoltri();" id="elencoInoltriLabel">Visualizza comunicazioni di inoltro</a></span></td>
	</tr>
	<tr id="rigaTabellaStoricoInoltri">
		<td colspan="2">
			<table id="tabellaStoricoInoltri" class="griglia" >
				
			</table>
		</td>
	</tr>
	</gene:campoScheda>
	
	<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
	<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
	<input type="hidden" name="genere" id="genere" value="${genere}"/>
	<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}"/>
	<c:if test='${param.entitaNoKey eq "HOMEPG"}' >
		<input type="hidden" name="entitaNoKey" value="HOMEPG" />
	</c:if>
	<gene:redefineInsert name="pulsanteModifica" />
	<gene:redefineInsert name="schedaModifica" />
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />

	<c:set var="isCommodello10or15" value="${datiRiga.W_INVCOM_COMMODELLO eq 10 || datiRiga.W_INVCOM_COMMODELLO eq 15}"/>
	<c:if test="${datiRiga.W_INVCOM_COMMODELLO >= 10 || datiRiga.W_INVCOM_COMMODELLO <= 18}">
		<c:set var="keyAdd" value="${keyAdd};W_INVCOM.COMKEY2=T:${datiRiga.W_INVCOM_COMKEY3}" />
	</c:if>
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">&nbsp;
		<c:if test='${((!gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")) || (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")))}'>
			<input type="hidden" name="keyAdd" value="${keyAdd}" />
			<input type="hidden" name="rispondi" value="1" />
			<input type="hidden" name="inoltra" id="attributoNascostoInoltra" value="2" />
			<c:if test="${autorizzatoModificaComunicazione eq 'true'}"> 
				<c:if test="${empty datiRiga.W_INVCOM_COMDATLET and !(isCommodello10or15) and (requestScope.isRettificaCodificata ne 'true')}">
					<INPUT type="button" class="bottone-azione" value='Segna come gi&agrave; letta' title='Segna come gi&agrave; letta' onclick="javascript:aggiornaDataLettura();" >
					&nbsp;
				</c:if>
				<c:if test="${genere ne '1' and !(isCommodello10or15)}">
				<INPUT type="button" class="bottone-azione" value='Rispondi' title='Rispondi' onclick="javascript:rispondiComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO}','${datiRiga.W_INVCOM_COMTIPMA}');" >
				&nbsp;
				</c:if>
				<c:if test='${ genere ne "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENEWEB.W_INVCOM-IN-scheda.inoltraComunicazione") and !((iterga eq "9" || iterga eq "10") and isfineconc ne "1") and !(isCommodello10or15) and (requestScope.isRettificaCodificata ne "true")}'>
					<INPUT type="button" class="bottone-azione" value='Inoltra' title='Inoltra' onclick="javascript:inoltraComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO}','${datiRiga.W_INVCOM_COMTIPMA}');" >
				</c:if>
				<c:if test='${isCommodello10or15}'>
					<INPUT id="okrettbtn" type="button" class="bottone-azione" value='Consenti rettifica' title='Consenti rettifica' onclick="javascript:rispondiComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO eq 10?"11":"16"}','${datiRiga.W_INVCOM_COMTIPMA}');" >
					&nbsp;
					<INPUT id="korettbtn" type="button" class="bottone-azione" value='Rifiuta rettifica' title='Rifiuta rettifica' onclick="javascript:rispondiComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO eq 10?"12":"17"}','${datiRiga.W_INVCOM_COMTIPMA}');" >
					&nbsp;
				</c:if>
			</c:if>
		</c:if>
		</td>
	</gene:campoScheda>
			

	<gene:redefineInsert name="addToAzioni" >
        <c:if test='${((!gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")) || (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")))}'>
		<tr>
			<c:if test="${autorizzatoModificaComunicazione eq 'true'}">
				<c:if test="${empty datiRiga.W_INVCOM_COMDATLET and !(isCommodello10or15) and (requestScope.isRettificaCodificata ne 'true')}">
					<tr>
						<td class="vocemenulaterale">
						<a href="javascript:aggiornaDataLettura();" title="Segna come gi&agrave; letta">Segna come gi&agrave; letta</a>
						</td>
					</tr>
				</c:if>
				<c:if test="${genere ne '1' and !(isCommodello10or15)}">
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:rispondiComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO}','${datiRiga.W_INVCOM_COMTIPMA}');" 
							title="Rispondi">Rispondi</a>
						</td>
					</tr>
				</c:if>
				<c:if test='${ genere ne "1" and  gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENEWEB.W_INVCOM-IN-scheda.inoltraComunicazione") and !((iterga eq "9" || iterga eq "10") and isfineconc ne "1") and !(isCommodello10or15) and (requestScope.isRettificaCodificata ne "true")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:inoltraComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO}','${datiRiga.W_INVCOM_COMTIPMA}');" 
							title="Inoltra">Inoltra</a>
						</td>
					</tr>
				</c:if>
				<c:if test='${isCommodello10or15}'>
					<tr>
						<td class="vocemenulaterale">
							<a id="okrettlink" href="javascript:rispondiComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO eq 10?"11":"16"}','${datiRiga.W_INVCOM_COMTIPMA}');"
							   title="Consenti rettifica">Consenti rettifica</a>
						</td>
					</tr>
					<tr>
						<td class="vocemenulaterale">
							<a id="korettlink" href="javascript:rispondiComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO eq 10?"12":"17"}','${datiRiga.W_INVCOM_COMTIPMA}');"
							   title="Rifiuta rettifica">Rifiuta rettifica</a>
						</td>
					</tr>
				</c:if>
				<c:if test="${tipoWSDM eq 'PALEO'  and not empty datiRiga.W_INVCOM_COMNUMPROT and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.TrasmettiAOperatoriInterni')}">
					<input type="hidden" id="_isRupOrDRP" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.CheckUserIsRupOrDRPFunction', pageContext , codgar )}"/>
					<input type="hidden" id="_superataFaseRicezione" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.CheckSuperataFaseRicezioneFunction', pageContext , codgar )}"/>
					<c:set var="abilitataFunzTrasmettiOpIntereni" value="true"/>
					<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:apriTrasmettiAOperatoriInterni();" title="Trasmetti a operatori interni" tabindex="1504">Trasmetti a operatori interni</a>
						</td>
					</tr>
				</c:if>
			</tr>
		</c:if>
	</tr>
	</c:if>
	</gene:redefineInsert>
</gene:formScheda>

<c:if test="${abilitataFunzTrasmettiOpIntereni eq 'true' }">
<div id="mascheraParametriWSDM" title='Trasmetti su documentale a operatori interni' style="display:none">
	<form id="richiestawslogin">
		<table class="dettaglio-notab">
			<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
			<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
			<input id="tiposistemaremoto" type="hidden" value="${tipoWSDM}" />
			<input id="tabellatiInDB" type="hidden" value="${tabellatiInDB}" />
			<input id="modoapertura" type="hidden" value="MODIFICA" /> 
			<tr id=sezioneMessaggi style="display: none;">
				<td colspan="2">
					<div  id="messagiTrasmissione"></div>
				</td>
			</tr>
			<tr id=sezioneErrori style="display: none;">
				<td colspan="2">
					<div  class="error" id="erroriTrasmissione"></div>
				</td>
			</tr>
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
				<jsp:param name="gestioneVisualizzazioneContratta" value="0"/>
				<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
			</jsp:include>		
		</table>
	</form>
	<br>
	<form id="formTrasmissione">
		<table class="dettaglio-notab">
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-datiTrasmissioneOperatori.jsp">
				<jsp:param name="tipoPagina" value="COM"/>
			</jsp:include>	
		</table>
	</form>
</div>
</c:if>

<gene:javaScript>

var contextPath = "${pageContext.request.contextPath}";
var idprg = "${idprg}";
var idcom = "${idcom}";
caricamentoStoricoRisposteComunicazioni(idprg,idcom,contextPath);
caricamentoStoricoInoltriComunicazioni(idprg,idcom,contextPath);

var isElencoRispoteShown = false;
$("#rigaTabellaStoricoRisposte").hide();
function showElencoRisposte(){
	if(isElencoRispoteShown){
		isElencoRispoteShown = false;
		$("#elencoRisposteLabel").html("Visualizza comunicazioni di risposta");
	}else{
		isElencoRispoteShown = true;
		$("#elencoRisposteLabel").html("Nascondi comunicazioni di risposta");
	}
	$("#rigaTabellaStoricoRisposte").toggle();
}

var isElencoInoltriShown = false;
$("#rigaTabellaStoricoInoltri").hide();
function showElencoInoltri(){
	if(isElencoInoltriShown){
		isElencoInoltriShown = false;
		$("#elencoInoltriLabel").html("Visualizza comunicazioni di inoltro");
	}else{
		isElencoInoltriShown = true;
		$("#elencoInoltriLabel").html("Nascondi comunicazioni di inoltro");
	}
	$("#rigaTabellaStoricoInoltri").toggle();
}

function aggiornaDataLettura() {
	document.forms[0].modo.value="MODIFICA";
	schedaConferma();
}

function rispondiComunicazione(idprg,idcom,commodello,comtipma) {
	<c:if test="${requestScope.hasRettificaRisposta eq 'true'}">
		alert("La comunicazione di risposta risulta già creata");
		return;
	</c:if>
	document.forms[0].modo.value="NUOVO";
	var href = "geneweb/w_invcom/w_invcom-scheda.jsp?idprgris="+idprg+"&idcomris="+idcom+"&commodello="+commodello+"&comtipma="+comtipma;
	var coment = "${datiRiga.W_INVCOM_COMENT}";
	var idcfg = "${datiRiga.W_INVCOM_IDCFG}";
	if(coment == 'HOMEPG'){
		href += "&entitaNoKey=HOMEPG&idcfg="+idcfg;
	}
	document.forms[0].jspPathTo.value=href;
	schedaNuovo();
}

function inoltraComunicazione(idprg,idcom,commodello,comtipma) {

	$('#attributoNascostoInoltra').val("1");
	document.forms[0].modo.value="NUOVO";
	var href = "geneweb/w_invcom/w_invcom-scheda.jsp?idprgino="+idprg+"&idcomino="+idcom+"&commodello="+commodello+"&comtipma="+comtipma;
	var coment = "${datiRiga.W_INVCOM_COMENT}";
	var idcfg = "${datiRiga.W_INVCOM_IDCFG}";
	if(coment == 'HOMEPG'){
		href += "&entitaNoKey=HOMEPG&idcfg="+idcfg;
	}
	document.forms[0].jspPathTo.value=href;
	schedaNuovo();

}

var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var keyAdd="${keyAdd }";
		var chiaveWSDM="${chiaveWSDM }";
		var entitaWSDM="${entitaWSDM }";
		var genere = "${genere }";
		document.pagineForm.action += "?keyAdd=" + keyAdd + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&genere=" + genere;
		selezionaPaginaDefault(pageNumber);
	}
	
function archivioImpresa(codiceImpresa){
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
}
</gene:javaScript>