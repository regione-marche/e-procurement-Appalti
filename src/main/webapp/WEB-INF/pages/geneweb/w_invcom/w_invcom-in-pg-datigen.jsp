
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

<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codgar)}' scope="request"/>
<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>

<gene:formScheda entita="W_INVCOM" gestisciProtezioni="true"
	gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAggiornaCOMDATLET">

	<gene:campoScheda campo="IDPRG" visibile="false"/>
	<gene:campoScheda campo="IDCOM" visibile="true" />
	<gene:campoScheda campo="USERKEY1" entita="W_PUSER" where="w_puser.usernome = w_invcom.comkey1" visibile="false"/>
	<c:set var="link" value='javascript:archivioImpresa("${datiRiga.W_PUSER_USERKEY1}");' />
	<gene:campoScheda campo="COMMITT" href='${gene:if(not empty datiRiga.W_PUSER_USERKEY1, link, "")}'/>
	<gene:campoScheda campo="COMMODELLO" visibile="${!empty datiRiga.W_INVCOM_COMMODELLO}"/>
	<gene:campoScheda campo="COMDATINS" definizione="D;0;;TIMESTAMP;COMDATINS"  title="Data e ora invio" />
	<gene:campoScheda campo="COMDATLET" />
	<gene:campoScheda campo="COMMSGOGG" />
	<gene:campoScheda campo="COMMSGTES" />
	<gene:campoScheda campo="COMNUMPROT" visibile='${integrazioneWSDM =="1" and !empty datiRiga.W_INVCOM_COMNUMPROT}' modificabile="false"/>
  	<gene:campoScheda campo="COMDATPROT" visibile='${integrazioneWSDM =="1" and !empty datiRiga.W_INVCOM_COMDATPROT}' modificabile="false"/>
  	<gene:campoScheda campo="COMTIPMA" visibile="${false}"/>
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
	</gene:campoScheda>
	
	<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
	<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
	<input type="hidden" name="genere" id="genere" value="${genere}"/>
	<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}"/>
	<gene:redefineInsert name="pulsanteModifica" />
	<gene:redefineInsert name="schedaModifica" />
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />
	
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">&nbsp;
		<c:if test='${((!gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")) || (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")))}'>
			<input type="hidden" name="keyAdd" value="${keyAdd}" />
			<input type="hidden" name="rispondi" value="1" />
			<c:if test="${autorizzatoModificaComunicazione eq 'true'}"> 
				<c:if test="${empty datiRiga.W_INVCOM_COMDATLET}"> 
					<INPUT type="button" class="bottone-azione" value='Segna come gi&agrave; letta' title='Segna come gi&agrave; letta' onclick="javascript:aggiornaDataLettura();" >
					&nbsp;
				</c:if>
				<c:if test="${genere ne '1'}"> 
				<INPUT type="button" class="bottone-azione" value='Rispondi' title='Rispondi' onclick="javascript:rispondiComunicazione('${datiRiga.W_INVCOM_IDPRG}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMMODELLO}','${datiRiga.W_INVCOM_COMTIPMA}');" >
				&nbsp;
				</c:if>
			</c:if>
		</c:if>
		</td>
	</gene:campoScheda>
			

</gene:formScheda>

<gene:javaScript>

var contextPath = "${pageContext.request.contextPath}";
var idprg = "${idprg}";
var idcom = "${idcom}";
caricamentoStoricoRisposteComunicazioni(idprg,idcom,contextPath);

var isElencoRispoteShown = false;
$("#tabellaStoricoRisposte").hide();
function showElencoRisposte(){
	if(isElencoRispoteShown){
		isElencoRispoteShown = false;
		$("#elencoRisposteLabel").html("Visualizza comunicazioni di risposta");
	}else{
		isElencoRispoteShown = true;
		$("#elencoRisposteLabel").html("Nascondi comunicazioni di risposta");
	}
	$("#tabellaStoricoRisposte").toggle();
}

function aggiornaDataLettura() {
	document.forms[0].modo.value="MODIFICA";
	schedaConferma();
}

function rispondiComunicazione(idprg,idcom,commodello,comtipma) {
	document.forms[0].modo.value="NUOVO";
	document.forms[0].jspPathTo.value="geneweb/w_invcom/w_invcom-scheda.jsp?idprgris="+idprg+"&idcomris="+idcom+"&commodello="+commodello+"&comtipma="+comtipma;
	schedaNuovo();
}

var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var keyAdd="${keyAdd }";
		var chiaveWSDM="${chiaveWSDM }";
		var entitaWSDM="${entitaWSDM }";
		var genere = "${genere }";
		document.pagineForm.action += "&keyAdd=" + keyAdd + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&genere=" + genere;
		selezionaPaginaDefault(pageNumber);
	}
	
function archivioImpresa(codiceImpresa){
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
}
</gene:javaScript>