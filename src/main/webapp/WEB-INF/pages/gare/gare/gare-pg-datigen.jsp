<%
/*
 * Created on: 13/11/2006
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%--
Viene sbiancata la variabile di sessione keyParentComunicazioni che viene inizializzata nella lista delle comunicazioni.
Se si crea una nuova comunicazione senza passare dalla lista delle comunicazioni la variabile altrimenti rimane valorizzata.
 --%>
<c:set var="keyParentComunicazioni" value="" scope="session"/>

<fmt:setBundle basename="AliceResources" />
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="isPersonalizzazioneGenovaAttiva" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsPersonalizzazioneGenovaAttivaFunction",pageContext)}' scope="request" />

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
</gene:redefineInsert>

<c:set var="filtroLivelloUtenteGare" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />

<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "PERI")}' />
<c:if test="${not empty filtroLivelloUtente}" >
	<c:set var="filtroLivelloUtente" value="and ${fn:replace(filtroLivelloUtente, 'PERI.CODLAV', 'APPA.CODLAV')}" />
</c:if>

<c:set var="isSimapAbilitato" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsSimapAbilitatoFunction",  pageContext)}' scope="request"/>
<c:set var="chiaveGara" value='${key}'/>

<c:if test='${modo ne "VISUALIZZA"}'>
	<c:set var="tabellatoA1z04" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTabellatoA1z0Function",  pageContext,"A1z04")}' scope="request"/>
	<c:set var="tabellatoA1z05" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTabellatoA1z0Function",  pageContext,"A1z05")}' scope="request"/>
</c:if>

<c:set var="integrazioneVigilanza" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.vigilanza.ws.url")}'/>
<c:if test='${!empty integrazioneVigilanza and integrazioneVigilanza != ""}'>
	<c:set var="nomeApplicativo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.vigilanza.nomeApplicativo")}'/>
</c:if>

<c:set var="garaLottoUnico" value='${garaLottoUnico}'/>

<% //si devono visualizzare gli atti autorizzativi solo se si è nella gara a lotto unico %>
<c:if test='${garaLottoUnico}'>
	<c:set var="ngaraPerGaratt" value=""/>
	<c:choose>
		<c:when test='${! empty gene:getValCampo(param.key, "CODGAR")}' >
			<c:set var="ngaraPerGaratt" value="${gene:getValCampo(param.key,'CODGAR')}"/>
		</c:when>
		<c:when test='${empty gene:getValCampo(param.key, "CODGAR") && ! empty gene:getValCampo(key, "NGARA")}' >
			<c:set var="ngaraPerGaratt" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>
		</c:when>
		<c:otherwise>
			<c:set var="ngaraPerGaratt" value="${gene:getValCampo(param.keyParent,'CODGAR')}"/>
		</c:otherwise>
	</c:choose>

	<c:set var="codgarPerGartecni" value="${ngaraPerGaratt}"/>
</c:if>

<c:set var="correttiviDefault" value="${gene:callFunction('it.eldasoft.sil.pg.tags.funzioni.GetCorrettiviDefaultFunction', pageContext)}" />
<c:set var="correttivoLavori" value="${fn:split(correttiviDefault, '#')[0]}"/>
<c:set var="correttivoFornitureServizi" value="${fn:split(correttiviDefault, '#')[1]}"/>

<c:set var="codGar" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="isVecchiaOepv" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVecchiaOEPVFunction", pageContext, codGar)}' />
<c:set var="isAccordoQuadro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAccordoQuadroFunction", pageContext, codGar)}'/>
<c:set var="log" value="${param.log}"/> 

<c:if test='${garaLottoUnico}'>
	<c:set var="soglieGara" value="${gene:callFunction('it.eldasoft.sil.pg.tags.funzioni.GetImportiSoglieGaraFunction', pageContext)}" />
	<c:set var="sogliaLavori" value="${fn:split(soglieGara, '#')[0]}"/>
	<c:set var="sogliaFornitureServizi" value="${fn:split(soglieGara, '#')[1]}"/>
</c:if>

<c:set var="esisteIntegrazioneLavori" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneLavoriFunction", pageContext)}' />

<c:choose>
	<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.PRECED") }'>
		<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "GARE", "PRECED")}'/>
	</c:when>
	<c:otherwise>
		<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "TORN", "CODGAR")}'/>
	</c:otherwise>
</c:choose>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="valoreTabellatoA1115" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "1")}'/>
<c:if test="${empty valoreTabellatoA1115 }">
	<c:set var="valoreTabellatoA1115" value="2"/>
</c:if>

<c:set var="valoreTabellatoA1115Compreq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "3")}'/>
<c:if test="${empty valoreTabellatoA1115Compreq }">
	<c:set var="valoreTabellatoA1115Compreq" value="2"/>
</c:if>

<c:set var="valoreTabellatoA1115Ricmano" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "5")}'/>
<c:if test="${empty valoreTabellatoA1115Ricmano }">
	<c:set var="valoreTabellatoA1115Ricmano" value="1"/>
</c:if>

<c:set var="valoreTabellatoA1115Settore" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoA1115Function", pageContext, "6")}'/>
<c:if test="${empty valoreTabellatoA1115Settore }">
	<c:set var="valoreTabellatoA1115Settore" value="O"/>
</c:if>

<c:set var="propertyCig" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsCig)}' scope="request"/>
<c:if test="${! empty propertyCig}">
	<c:set var="isCigAbilitato" value='1' scope="request"/>
</c:if>	
<c:set var="genere" value='1' scope="request"/>
<c:if test="${garaLottoUnico}">
	<c:set var="genere" value='2' scope="request"/>
</c:if>	

<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO"}'>
	<c:choose>
		<c:when test='${! empty gene:getValCampo(param.key, "CODGAR")}' >
			<c:set var="codiceGara" value="${gene:getValCampo(param.key,'CODGAR')}"/>
		</c:when>
		<c:otherwise>
			<c:set var="codiceGara" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>
		</c:otherwise>
	</c:choose>
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara,idconfi)}' />
	<c:set var="integrazioneWSDMDocumentale" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNDocumentaleFunction", pageContext, codiceGara,idconfi)}' />
	<c:if test='${integrazioneWSDM eq "1" or integrazioneWSDMDocumentale eq "1"}'>
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
		<c:if test='${garaLottoUnico }'>
		<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsFascicoloDocumentaleValidoFunction",  pageContext, codiceGara,idconfi)}' />
		</c:if>
		<c:if test="${integrazioneWSDMDocumentale eq '1' && tipoWSDM eq 'JIRIDE'}">
			<c:set var="isFascicoloDocumentaleCommessa" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", accessoFascicoloDocumentaleCommessa, idconfi)}'/>
		</c:if>
	</c:if>
</c:if>
<c:set var="integrazioneERPvsWSDM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneERPvsWSDMFunction", pageContext,idconfi)}'/>

<c:set var="tipoPubSitoIstituzionale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", tipoPubblicazioneSitoIstituzionale)}'/>
<c:if test="${!garaLottoUnico }">
	<c:set var="codiceGara" value="${gene:getValCampo(keyParent,'CODGAR')}"/>
</c:if>

<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO" && garaLottoUnico}'>
	${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoComunicazioniFS10_FS11_Stato9Function", pageContext, gene:getValCampo(key, 'NGARA'))}
</c:if>

<%/* Dati generali della gara */%>
<gene:formScheda entita="GARE" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreDatiGenerali" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE">




<%/* Viene riportato tipoGara, in modo tale che, in caso di errorie riapertura della pagina, 
     venga riaperta considerando il valore definito inizialmente per la prima apertura della pagina */%>
<input type="hidden" name="tipoGara" value="${param.tipoGara}" />
<input type="hidden" name="garaLottoUnico" value="${garaLottoUnico}" />
	<c:set var="numeroGara" value="${gene:getValCampo(key, 'NGARA') }" />
	<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codGar,"BANDO","false")}' />
	<c:choose>
		<c:when test='${garaLottoUnico}'>
			<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,numeroGara,"ESITO","false")}' />
		</c:when>
		<c:otherwise>
			<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codGar,"ESITO","true")}' />
		</c:otherwise>
	</c:choose>
	<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"2")}' />
	
	<c:if test="${!isProceduraTelematica }">
		<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' />
	</c:if>
	<c:set var="bloccoModificatiDati" value='${(isProceduraTelematica || applicareBloccoPubblicazioneGareNonTelematiche eq "1") && (bloccoPubblicazionePortaleEsito eq "TRUE" || bloccoPubblicazionePortaleBando eq "TRUE")}' />
	<gene:campoScheda visibile="${ bloccoModificatiDati eq 'true' and modo eq 'MODIFICA'}">
		<td colspan="2" style="color:#0000FF">
		<br><b>ATTENZIONE:</b>&nbsp;
		Parte dei dati sono in sola consultazione perché la gara è pubblicata su portale Appalti<br>&nbsp;
		</td>	
	</gene:campoScheda>
	<c:choose>
		<c:when test='${bloccoModificatiDati}'>
			<c:set var="modificaLabel" value='Modifica dopo pubblicazione' />
			<c:set var="bloccoModificaPubblicazione" value='TRUE' />
			<c:set var="bloccoModificatiDati" value='true' />
		</c:when>
		<c:otherwise>
			<c:set var="modificaLabel" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' />
			<c:set var="bloccoModificaPubblicazione" value='FALSE' />
			<c:set var="bloccoModificatiDati" value='false' />
		</c:otherwise>
	</c:choose>	

<c:if test="${garaLottoUnico}">
	<gene:redefineInsert name="schedaNuovo" >
	<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
	<tr>
		<td class="vocemenulaterale" >
			<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:schedaNuovaGara();" title="Inserisci" tabindex="1502"></c:if>
				${gene:resource("label.tags.template.lista.listaNuovo")}
			<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
		</td>
	</tr>
	</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="pulsanteNuovo">
			<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
				<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovaGara()" id="btnNuovo">
			</c:if>
	</gene:redefineInsert>
</c:if>	
<c:if test='${!garaLottoUnico}'>
<c:choose>
	<c:when test='${!(applicareBloccoPubblicazioneGareNonTelematiche eq "1" && (bloccoPubblicazionePortaleEsito eq "TRUE" || bloccoPubblicazionePortaleBando eq "TRUE"))}'>
		<gene:redefineInsert name="schedaNuovo" >
			<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:schedaNuovaGaraLotto();" title="Inserisci" tabindex="1502"></c:if>
						${gene:resource("label.tags.template.lista.listaNuovo")}
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
			</c:if>
			</gene:redefineInsert>
			<gene:redefineInsert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovaGaraLotto()" id="btnNuovo">
					</c:if>
			</gene:redefineInsert>
	</c:when>
	<c:otherwise>
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	</c:otherwise>
</c:choose>
</c:if>
<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
	<jsp:param name="entita" value="V_GARE_TORN"/>
	<jsp:param name="inputFiltro" value="${requestScope.inputFiltro}"/>
	<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
</jsp:include>

 <c:if test='${bloccoModificatiDati}'>
 	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
 </c:if>

<gene:redefineInsert name="addToAzioni" >
	<c:if test='${modo eq "VISUALIZZA" and bloccoModificatiDati and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2"}' >
	  <tr>
		  <td class="vocemenulaterale" >
				  <a href="javascript:schedaModifica();" title="${modificaLabel}">
			  ${modificaLabel}</a>
		  </td>
	  </tr>
   </c:if>	
	<c:if test='${modo eq "VISUALIZZA" and garaLottoUnico and datiRiga.TORN_GARTEL eq 1 && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_TORN-lista.Condividi-gara")}'>
	  <tr>    
	      <td class="vocemenulaterale">
	     		<c:choose>
	     			<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
	     				<a href="javascript:apriGestionePermessiGaraTelematica('${datiRiga.GARE_CODGAR1}', ${datiRiga.TORN_GARTEL});" title="Punto ordinante e istruttore" tabindex="1503">
	     			</c:when>
	     			<c:otherwise>
	     				<a href="javascript:apriGestionePermessiGaraTelematicaStandard('${datiRiga.GARE_CODGAR1}', ${datiRiga.TORN_GARTEL});" title="Punto ordinante e istruttore" tabindex="1503">
	     			</c:otherwise>
	     		</c:choose>
				  Punto ordinante e istruttore
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
		</tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.RettificaTermini" ) and 
		(datiRiga.TORN_ITERGA eq 1 or datiRiga.TORN_ITERGA eq 2 or datiRiga.TORN_ITERGA eq 4
		or ((datiRiga.TORN_ITERGA eq 3 or datiRiga.TORN_ITERGA eq 5 or datiRiga.TORN_ITERGA eq 6) and gene:checkProt(pageContext,"PAGE.VIS.GARE.GARE-scheda.DITTECONCORRENTI")))
		and garaLottoUnico and (datiRiga.TORN_GARTEL ne 1 or (datiRiga.TORN_GARTEL eq 1 and bloccoPubblicazionePortaleBando eq "TRUE"))}'>
	  	<c:set var="esisteFunzioneRettificaTermini" value="true" scope="request"/>
	  	<tr>    
	    	  <td class="vocemenulaterale">
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:popupRettificaTermini(${datiRiga.TORN_ITERGA },'${datiRiga.GARE_CODGAR1}','${datiRiga.TORN_GARTEL}','${datiRiga.GARE_NGARA}');" title="Rettifica termini di gara" tabindex="1504">
				</c:if>
				  Rettifica termini di gara
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
		</tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and bloccoModificatiDati and (empty datiRiga.GARE_CODCIG or empty datiRiga.TORN_NUMAVCP) and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.IntegraCodiceCig")}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:integraCodiceCig('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','No','${datiRiga.GARE_CODCIG}','${datiRiga.TORN_NUMAVCP}');" title='Integra codice CIG' tabindex="1505">
					Integra codice CIG
				</a>
			</td>
		</tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and gene:checkProtFunz(pageContext, "MOD","MOD") and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaGaraNonAggiud")}'>
	      <td class="vocemenulaterale">
		      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
						<a href="javascript:impostaGaraNonAggiudicata('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','${datiRiga.GARE_ESINEG}','${datiRiga.GARE_DATNEG}','${datiRiga.GARE1_NPANNREVAGG}');" title="Imposta gara non aggiudicata" tabindex="1506">
				</c:if>
				  Imposta gara non aggiudicata
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and (gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.exportXMLAntimafia"))}'>
		<tr>
			<td class="vocemenulaterale" >
				<c:if test='${isNavigazioneDisattiva ne "1"}'>
					<a href="javascript:esportaAntimafiaVerifica();" title="Esporta ditte partecipanti alla gara per verifica interdizione" tabindex="1507">
				</c:if>
					Esporta ditte per verifica interdizione
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
			</td>
		</tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AutovieIds") and garaLottoUnico}'>
	  	<tr>    
	    	  <td class="vocemenulaterale">
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:listaIds();" title="Collega Ids" tabindex="1508">
				</c:if>
				  Collega Ids
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
		</tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and isCigAbilitato eq "1" and garaLottoUnico and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG")}'>
		<tr>
			<td class="vocemenulaterale" >
				<c:if test='${isNavigazioneDisattiva ne "1"}'>
					<a href="javascript:popupInviaDatiCig('${datiRiga.GARE_CODGAR1}','${datiRiga.GARE_NGARA}','${genere}');" title="Invia dati per richiesta CIG" tabindex="1510">
				</c:if>
					Invia dati richiesta CIG
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
			</td>
		</tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and garaLottoUnico and isSimapAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaBandoAvviso")}'>
	   <tr>   
	      <td class="vocemenulaterale">
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:popupInviaBandoAvvisoSimap('${datiRiga.GARE_CODGAR1}','${datiRiga.TORN_ITERGA}','${datiRiga.TORN_SETTORE}');" title="Crea formulari GUUE" tabindex="1511">
				</c:if>
				  Crea formulari GUUE
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
	  </tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and garaLottoUnico and !empty integrazioneVigilanza and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaDatiVigilanza")}'>
	  <tr>    
	      <td class="vocemenulaterale">
		      	<c:set var="voceMenu" value="Invia dati a Vigilanza"/>
		      	<c:if test="${!empty nomeApplicativo and nomeApplicativo!=''}">
		      		<c:set var="voceMenu" value="Invia dati a ${nomeApplicativo }"/>
		      	</c:if>
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:popupInviaVigilanza('${datiRiga.GARE_CODGAR1}');" title="${voceMenu }" tabindex="1512">
				</c:if>
				  ${voceMenu }
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
		</tr>
	</c:if>
	<c:if test='${modo eq "VISUALIZZA" and garaLottoUnico and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.ControDatiL190")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:popupControlla190('${datiRiga.GARE_CODGAR1}');" title="Controllo dati ai fini di L.190/2012" tabindex="1513">
					</c:if>
						Controllo dati ai fini di L.190/2012
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
	</c:if>	
	
	<c:if test='${modo eq "VISUALIZZA" && integrazioneWSERP eq "1" && (tipoWSERP eq "AVM" || (tipoWSERP eq "UGOVPA" && datiRiga.TORN_ACCQUA ne "1" && empty datiRiga.TORN_NGARAAQ))}'>
	     <c:if test='${!bloccoModificatiDati }'>
			<c:if test='${autorizzatoModifiche ne "2" and (visListaLavForn eq "false" or datiRiga.TORN_ACCQUA eq "1")}'>
				<c:set var="presenzaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPPresenzaRdaFunction", pageContext, codiceGara, numeroGara, requestScope.tipoWSERP)}' />
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:leggiCarrelloRda('${datiRiga.GARE_CODGAR1}','${datiRiga.GARE_NGARA}');" title='Carica RdA' tabindex="1514">
							Carica RdA
						</a>
					</td>
				</tr>
			</c:if>
	     </c:if>
	</c:if>
	
	
	<c:if test='${modo eq "VISUALIZZA" and ((fn:contains(listaOpzioniDisponibili, "OP114#") && garaLottoUnico and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni")) || (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")))}'>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale" title='${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}'>
				${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}</td>
		</tr>
		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") && garaLottoUnico and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:leggiComunicazioni();" title="${gene:resource('label.tags.template.documenti.comunicazioniRicevute.tooltip')}" tabindex="1515">
					</c:if>
					${gene:resource('label.tags.template.documenti.comunicazioniRicevute')}
					<c:set var="numComunicazioniRicevuteNonLette" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniRicevuteDaLeggereFunction", pageContext, gene:getValCampo(key, "NGARA"))}' />
					<c:if test="${numComunicazioniRicevuteNonLette > 0}">(${numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:inviaComunicazioni();" title="${gene:resource('label.tags.template.documenti.inviaComunicazioni.tooltip')}" tabindex="1516">
					</c:if>
					${gene:resource('label.tags.template.documenti.inviaComunicazioni')}
					<c:set var="numComunicazioniBozza" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniBozzaFunction", pageContext, "GARE", gene:getValCampo(key, "NGARA"))}' />
					<c:if test="${numComunicazioniBozza > 0}">(${numComunicazioniBozza} ${gene:resource('label.tags.template.documenti.inviaComunicazioni.indicatore')})</c:if>
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
			<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:nuovaComunicazione();" title="Nuova comunicazione" tabindex="1517">
						</c:if>
						Nuova comunicazione
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
		</c:if>
	</c:if>
	
	<c:if test='${modo eq "VISUALIZZA" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.Discussioni")}'>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale" title='Messaggi interni'>Messaggi interni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale" >
				<c:if test='${isNavigazioneDisattiva ne "1"}'>
					<a href="javascript:listaDiscussioni();" title="Discussioni" tabindex="1516">
				</c:if>
				Conversazioni
				<c:set var="resultConteggioDiscussioni" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDiscussioniNumeroFunction", pageContext, "GARE", gene:getValCampo(key, "NGARA"))}' />
				<c:if test="${numeroDiscussioni > 0}">(${numeroDiscussioni})</c:if>
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
			</td>
		</tr>
	</c:if>
</gene:redefineInsert>

<c:set var="pathDocAssociatiPL" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathDocumentiAssociatiPL")}' />
<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>
<c:set var="exportDocumenti" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.pathArchivioDocumentiGara")}'/>
<c:set var="exportCOS" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "cos.sftp.url")}'/>

<c:choose>
	<c:when test="${documentiAssociatiDB eq '1' }">
		<c:set var="controlloPahDocAssociatiPl" value="true"/>
	</c:when>
	<c:otherwise>
		<c:set var="controlloPahDocAssociatiPl" value="${!empty pathDocAssociatiPL && pathDocAssociatiPL ne '' }"/>
	</c:otherwise>
</c:choose>
	<gene:redefineInsert name="addToDocumenti" >
		<c:if test='${modo eq "VISUALIZZA" && !empty datiRiga.GARE_NUMERA && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.VisualizzaDocAppaltoDaGare") 
			&& gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NUMERA") && controlloPahDocAssociatiPl}'>
			<tr>
				<td class="vocemenulaterale">
					<a href='javascript:visualizzaDocumentiAssociatiAppalto();' title="Documenti associati dell'appalto" tabindex="1522">
						Documenti associati dell'appalto
					</a>
				</td>
			</tr>
		</c:if>
		
		<c:if test='${modo eq "VISUALIZZA" and fn:contains(listaOpzioniDisponibili, "OP128#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.G_SCADENZ")}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href='javascript:apriScadenzario();' title="Scadenzario attività" tabindex="1513">
								Scadenzario attività
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Scadenzario attività
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
		
		<c:set var="creaFascicolo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", accessoCreaFascicolo,idconfi)}'/>
		<c:if test='${modo eq "VISUALIZZA"  and autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CreaFascicolo") and garaLottoUnico and integrazioneWSDM eq "1" and creaFascicolo eq "1"}'>
			<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", datiRiga.GARE_NGARA,idconfi)}' scope="request"/>
			
			<c:if test='${esisteFascicoloAssociato ne "true" and  (tipoWSDM eq "IRIDE" or tipoWSDM eq "JIRIDE" or tipoWSDM eq "ENGINEERING" 
				or tipoWSDM eq "ARCHIFLOW" or tipoWSDM eq "INFOR" or tipoWSDM eq "JPROTOCOL" or tipoWSDM eq "JDOC")}' >
			
				<tr>
					<c:choose>
						<c:when test='${isNavigazioneDisattiva ne "1"}'>
							<td class="vocemenulaterale">
								<a href="javascript:apriCreaFascicolo('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','${idconfi}',2);" title="Crea fascicolo" tabindex="1515">
									Crea fascicolo
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>
								Crea fascicolo
							</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:if>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA"  and garaLottoUnico and
                          ((integrazioneWSDMDocumentale eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciAlDocumentale"))
                          or (!empty exportCOS and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TrasferisciCos"))
                          or (!empty exportDocumenti and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ExportDocumenti")))}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:apriArchiviaDocumenti('${datiRiga.GARE_CODGAR1}');" title="Archivia documenti" tabindex="1516">
								Archivia documenti
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Archivia documenti
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and isFascicoloDocumentaleAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FascicoloDocumentale") and garaLottoUnico}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:document.formwsdm.submit();" title="Fascicolo documentale" tabindex="1517">
								Fascicolo documentale
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Fascicolo documentale
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and isFascicoloDocumentaleCommessa eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FascicoloDocumentaleCommessa") }'>
			<c:if test="${!empty datiRiga.GARE_CLAVOR }">
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:impostaDatiDocumentaleCommessa(1);" title="Fascicolo documentale commessa" tabindex="1518">
								Fascicolo documentale commessa
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Fascicolo documentale commessa
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
			</c:if>
			<c:if test="${!empty datiRiga.GARE_NUMERA }">
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href="javascript:impostaDatiDocumentaleCommessa(2);" title="Fascicolo documentale appalto" tabindex="1519">
								Fascicolo documentale appalto
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Fascicolo documentale appalto
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
			</c:if>
		</c:if>
	</gene:redefineInsert>
	
	<c:if test='${modoAperturaScheda eq "NUOVO" and !garaLottoUnico}'>
		<% // campi non presenti nella pagina e da includere solo nel caso di inserimento lotto di gara, in modo da inizializzarli nel DB %>
		<gene:campoScheda campo="NAVVIGG" visibile="false" defaultValue="${requestScope.initNAVVIG}"/>
		<gene:campoScheda campo="DAVVIGG" visibile="false" defaultValue="${requestScope.initDAVVIG}"/>
		<gene:campoScheda campo="DPUBAVG" visibile="false" defaultValue="${requestScope.initDPUBAV}"/>
		<gene:campoScheda campo="DFPUBAG" visibile="false" defaultValue="${requestScope.initDFPUBA}"/>
		<gene:campoScheda campo="DIBANDG" visibile="false" defaultValue="${requestScope.initDIBAND}"/>
	</c:if>
	
	<gene:campoScheda campo="PGAROF" visibile="false"/>

	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	

	<gene:gruppoCampi idProtezioni="GEN">
	<% // Campo fittizio che, se presente, indica che si sta inserendo una nuova
		 // gara associandola ad un appalto
	%>
		<c:if test='${modo eq "NUOVO" and not empty requestScope.initCLAVOR and not empty requestScope.initNUMERA}'>
			<gene:campoScheda campo="ISGARA_DA_APPALTO" campoFittizio="true" definizione="N1" value="1" visibile="false" />
		</c:if>
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<c:choose>
			<c:when test='${isCodificaAutomatica eq "false"}'>
				<c:choose>
					<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
						<gene:campoScheda campo="NGARA" title="${gene:if(garaLottoUnico, 'Codice gara', 'Codice lotto')}" modificabile="false" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="NGARA" title="${gene:if(garaLottoUnico, 'Codice gara', 'Codice lotto')}" obbligatorio="true" modificabile='${modoAperturaScheda eq "NUOVO"}' defaultValue="${requestScope.initNGARA}">
							<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="true" messaggio="${msgChiaveErrore}" />
						</gene:campoScheda>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="NGARA" modificabile='${modo eq "NUOVO"}' title="${gene:if(garaLottoUnico, 'Codice gara', 'Codice lotto')}"
					gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
			</c:otherwise>
		</c:choose>

		<gene:campoScheda campo="CODCOM"/>
		<gene:campoScheda campo="CODIGA" visibile="${not garaLottoUnico}" obbligatorio='${not gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie") && not garaLottoUnico}' modificabile ='${not gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}' />
		<gene:campoScheda campo="NUMAVCP" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}" defaultValue="${gene:if(garaLottoUnico, requestScope.initNUMAVCP,'')}" />
		<gene:campoScheda campo="CODCIG" defaultValue="${requestScope.initCODCIG}" />
	<c:choose>
		<c:when test='${fn:startsWith(datiRiga.GARE_CODCIG,"#") or fn:startsWith(datiRiga.GARE_CODCIG,"$") or fn:startsWith(datiRiga.GARE_CODCIG,"NOCIG")}'>
			<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="${datiRiga.GARE_CODCIG}" definizione="T10;;;;G1CODCIG" modificabile="false" />
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="" definizione="T10;;;;G1CODCIG" modificabile="false"  />
		</c:otherwise>
	</c:choose>
		
		<gene:campoScheda campo="ESENTE_CIG" campoFittizio="true" computed="true" title="Esente CIG?" definizione="T10;;;SN" defaultValue="${gene:if(!empty initIsStrumentale, initIsStrumentale,'2')}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoSenzaNull" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.CODCIG") }'/>
		<gene:fnJavaScriptScheda funzione="gestioneEsenteCIG()" elencocampi="ESENTE_CIG" esegui="false" />
		
		<gene:campoScheda campo="DACQCIG" defaultValue="${requestScope.initDACQCIG}"/>
		<gene:campoScheda campo="UREGA" entita="TORN" visibile='${garaLottoUnico and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CodiceUrega")}' modificabile='false'/>
		<gene:campoScheda campo="CODGARCLI" entita="TORN" visibile='${garaLottoUnico and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}' modificabile='false' />
		<gene:campoScheda campo="CODGAR1" visibile="false" defaultValue="${gene:getValCampo(param.keyParent, 'CODGAR')}"/>
		<c:choose>
			<c:when test='${modo eq "NUOVO" and not empty requestScope.initTIPGEN}'>
				<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='${garaLottoUnico}' 
					modificabile='${((!empty param.tipoAppalto) && param.tipoAppalto ne 1) || datiRiga.TORN_TIPGEN ne 1}' 
					defaultValue="${requestScope.initTIPGEN}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGEN" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='${garaLottoUnico}' 
					modificabile='${((!empty param.tipoAppalto) && param.tipoAppalto ne 1) || datiRiga.TORN_TIPGEN ne 1}' 
					defaultValue="${param.tipoAppalto}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGEN" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${!empty initSETTORE }">
				<c:set var="inizializzazioneSettore" value="${requestScope.initSETTORE }"/>
			</c:when>
			<c:otherwise>
				<c:set var="inizializzazioneSettore" value="${valoreTabellatoA1115Settore }"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="SETTORE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}" obbligatorio="true" defaultValue="${inizializzazioneSettore}" />
		<gene:campoScheda campo="TIPFORN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico && datiRiga.TORN_TIPFORN ne 98}"/>
		<gene:campoScheda campo="NOT_GAR" defaultValue="${requestScope.initNOT_GAR}"/>
		<gene:campoScheda campo="TIPGARG" obbligatorio="true" defaultValue="${gene:if(garaLottoUnico, requestScope.initTIPGARG, requestScope.initTIPGAR)}" visibile="${garaLottoUnico}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGAR"/>
		<gene:campoScheda campo="ITERGA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="${gene:if(garaLottoUnico, requestScope.initITERGAG, requestScope.initITERGA)}">
			<gene:calcoloCampoScheda 
			funzione='calcolaITERGA("#GARE_TIPGARG#")' elencocampi="GARE_TIPGARG" />
		</gene:campoScheda>
		<gene:campoScheda campo="TIPNEG" defaultValue="${gene:if(garaLottoUnico, '', requestScope.initTIPNEG)}" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="ALTNEG" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		
		<c:choose>
			<c:when test="${garaLottoUnico}">
				<gene:campoScheda campo="ACCQUA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="${gene:if(requestScope.initACCQUA ==null  || requestScope.initACCQUA eq '', '2', requestScope.initACCQUA)}"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="ACCQUA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
				<c:set var="valoreInizializzazioneAccqua" value="${requestScope.initACCQUATorn}"/>
			</c:otherwise>
		</c:choose>
		
		
		<gene:campoScheda campo="AQOPER" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" defaultValue="${gene:if(garaLottoUnico, '', requestScope.initAQOPER)}" obbligatorio="true" modificabile="${gene:if(garaLottoUnico=='true' or (garaLottoUnico=='false' and requestScope.initAQOPER ne '1') , 'true', 'false') }"/>
		<gene:campoScheda campo="AQNUMOPE" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" defaultValue="${gene:if(garaLottoUnico, '', requestScope.initAQNUMOPE)}">
			<gene:checkCampoScheda funzione='controlloCampoAqnumope("##")' obbligatorio="true" messaggio='Il valore specificato deve essere maggiore di 1.' onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="ISADESIONE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:archivio titolo="accordi quadro"
			obbligatorio="" 
			scollegabile="true"
			lista='gare/gare/gare-popup-lista-accordiQuadro.jsp' 
			scheda="" 
			schedaPopUp="" 
			campi="V_GARE_ACCORDIQUADRO.CODCIG;V_GARE_ACCORDIQUADRO.NGARA" 
			chiave="TORN.NGARAAQ"
			where=""
			formName="formArchivioAccordiQuadro">
			<gene:campoScheda campo="CODCIGAQ" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}">
			</gene:campoScheda>
			<gene:campoScheda campo="NGARAAQ" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		</gene:archivio>
		<gene:campoScheda campo="PRECED" defaultValue="${requestScope.initPRECED }" modificabile="false"/>	

		<gene:campoScheda campo="CRITLICG" obbligatorio="true" defaultValue="${gene:if(garaLottoUnico, requestScope.initCRITLICG, requestScope.initCRITLIC)}"/>
		
		<gene:campoScheda campo="DETLICG" obbligatorio="true" defaultValue="${gene:if(garaLottoUnico, requestScope.initDETLICG, requestScope.initDETLIC)}" />
		<c:choose>
			<c:when test="${!empty initRIBCAL }">
				<c:set var="inizializzazioneRibcal" value="${requestScope.initRIBCAL }"/>
			</c:when>
			<c:when test="${!garaLottoUnico && ((requestScope.initCRITLIC==1 && requestScope.initDETLIC==3 && param.tipoAppalto!=1) ||  requestScope.initDETLIC eq '4')}">
				<c:set var="inizializzazioneRibcal" value="2"/>
			</c:when>
			<c:otherwise>
				<c:set var="inizializzazioneRibcal" value="1"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="RIBCAL" obbligatorio="true" defaultValue="${inizializzazioneRibcal}"/>
		
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.CALCSOANG") }'>
				<c:set var="valoreInizializzazioneCalcsoang" value="${gene:if(garaLottoUnico, '1', requestScope.initCALCSOAN)}"/>
			</c:when>
			<c:otherwise>
				<c:set var="valoreInizializzazioneCalcsoang" value="2"/>
			</c:otherwise>
		</c:choose>
		
		<gene:campoScheda campo="CALCSOANG" obbligatorio="true" defaultValue="${valoreInizializzazioneCalcsoang }"/>
				
		<c:choose>
			<c:when test="${not empty initMODASTG}" >
				<c:set var="defaultMODASTG" value="${requestScope.initMODASTG}"/>
			</c:when>
			<c:otherwise>
				<c:set var="defaultMODASTG" value="2"/>
			</c:otherwise>
		</c:choose>
		
		<gene:campoScheda campo="MODASTG" defaultValue="${defaultMODASTG}" obbligatorio="true">
			<gene:checkCampoScheda funzione='(toVal("#GARE_MODASTG#") == 2 || toVal("#GARE_CALCSOANG#") == 1)' messaggio='Esclusione automatica delle offerte anomale: se non è previsto il calcolo della soglia di anomalia è possibile indicare esclusivamente il valore \"No\"' obbligatorio="true" onsubmit="true" />
		</gene:campoScheda>
		
		<gene:campoScheda campo="APPLEGREGG"  defaultValue="${gene:if(garaLottoUnico, '', requestScope.initAPPLEGREG)}"/>
		
		<c:set var="offaumDefault" value="2"/> 
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GarePrivateAcquisto")}'>
			<c:set var="garpriv" value="2"/> 
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GarePrivateVendita")}'>
			<c:set var="garpriv" value="1"/> 
			<c:set var="offaumDefault" value="1"/> 
		</c:if>
		
		<c:choose>
			<c:when test='${garaLottoUnico}'>
				<gene:campoScheda campo="OFFAUM" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="${offaumDefault}" visibile="true" modificabile="true"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="OFFAUM" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" value="2" visibile="false" modificabile="false"/>
			</c:otherwise>
		</c:choose>		
		
		<gene:campoScheda campo="SICINC" obbligatorio="true" defaultValue="1"/>
		
		<gene:campoScheda campo="MODLICG" visibile="false" defaultValue="${gene:if(garaLottoUnico, requestScope.initMODLICG, requestScope.initMODLIC)}" >
			<gene:calcoloCampoScheda 
			funzione='calcolaMODLICG("#GARE_CRITLICG#","#GARE_DETLICG#","#GARE_CALCSOANG#","#GARE_APPLEGREGG#")' 
			elencocampi="GARE_CRITLICG;GARE_DETLICG;GARE_CALCSOANG;GARE_APPLEGREGG" />
		</gene:campoScheda>
		
		<gene:fnJavaScriptScheda funzione='gestioneCriterioAggiudicazione("#GARE_CRITLICG#","#GARE_DETLICG#")' elencocampi='GARE_CRITLICG;GARE_DETLICG' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneCRITLICG("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneCOSTOFISSO_SEZIONITEC("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="true" />
		<c:if test="${not isVecchiaOepv}">	
			<gene:fnJavaScriptScheda funzione='gestioneOFFAUM("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="true" />
			<gene:fnJavaScriptScheda funzione='gestionePRERIB()' elencocampi='GARE_MODLICG' esegui="true" />
		</c:if>
		<gene:fnJavaScriptScheda funzione='gestioneDETLICG("#GARE_DETLICG#")' elencocampi='GARE_DETLICG' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneCALCSOANG("#GARE_CALCSOANG#","true")' elencocampi='GARE_CALCSOANG' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneFlagSicurezzaInclusa("#GARE_CRITLICG#","#GARE_DETLICG#")' elencocampi='GARE_CRITLICG;GARE_DETLICG' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneTipneg_Alteng("#TORN_ITERGA#","GARE")' elencocampi='TORN_ITERGA' esegui="true" />
		<c:if test="${garaLottoUnico && modo ne 'VISUALIZZA'}">
			<gene:fnJavaScriptScheda funzione='inizializzazioniDaRibcal("#GARE_RIBCAL#","#GARE_CRITLICG#")' elencocampi='GARE_RIBCAL' esegui="true" />
		</c:if>
				
		<c:if test="${modo ne 'VISUALIZZA'}">		
		<gene:fnJavaScriptScheda funzione='gestioneTabellatoDETLICG("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="true" />	
		</c:if>
			
		<c:choose>
			<c:when test="${garaLottoUnico}">
				<gene:fnJavaScriptScheda funzione='gestioneAQOPER("#TORN_ACCQUA#")' elencocampi='TORN_ACCQUA' esegui="true" />
			</c:when>
			<c:otherwise>
				<gene:fnJavaScriptScheda funzione='gestioneAccordoQuadroLotti("${modo }","${requestScope.initACCQUATorn }","${requestScope.initAQOPER }")' elencocampi='TORN_ACCQUA' esegui="true" />
			</c:otherwise>
		</c:choose>
		<gene:fnJavaScriptScheda funzione='gestioneAQNUMOPE("#GARE1_AQOPER#")' elencocampi='GARE1_AQOPER' esegui="true" />
		<gene:campoScheda campo="CORGAR" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR"  visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="CORGAR1" visibile="${!garaLottoUnico}" defaultValue="${gene:if(garaLottoUnico, '', requestScope.initCORGAR)}"/>
		<gene:campoScheda campo="GARTEL" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" modificabile="false" visibile='${garaLottoUnico && fn:contains(listaOpzioniDisponibili, "OP114#") && fn:contains(listaOpzioniDisponibili, "OP132#")}' defaultValue="${param.proceduraTelematica}"/>
		<gene:campoScheda campo="OFFTEL" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" modificabile="false" visibile='${garaLottoUnico && (param.proceduraTelematica eq 1 || isProceduraTelematica)}' defaultValue="${param.modalitaPresentazione}"/>
		<gene:campoScheda campo="FASGAR" modificabile="false"/>
		<gene:campoScheda title="Stato della gara" campo="STATOGARA" campoFittizio="true" definizione="T20" modificabile="false"/>
		<gene:campoScheda campo="ESINEG" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
		<gene:campoScheda campo="DATNEG" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
		<gene:campoScheda campo="NPANNREVAGG" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
		<gene:campoScheda campo="NOTNEG" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"  visibile="${!empty datiRiga.GARE_ESINEG }" modificabile="false"/>
		<gene:campoScheda campo="CATIGA" />
		<gene:campoScheda campo="DRICCAPTEC"/>
		
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioSoggettiAggregati") && modo eq "VISUALIZZA" and !garaLottoUnico}' >
			<gene:campoScheda addTr="false">
				<tr id="rowLinkDetSogAgg" style="display: none;">
					<td colspan="2" class="valore-dato" id="colonnaLinkDetSogAgg">
						<c:set var="titoloFunzione" value="Elenco soggetti per cui agisce la centrale di committenza"/>
						<c:set var="titoloFunzioneJs" value="Elenco soggetti per cui agisce la centrale di committenza"/>
						<c:if test="${datiRiga.TORN_ALTRISOG ne 3 or  empty datiRiga.TORN_ACCQUA}">
							<c:set var="titoloFunzione" value="Elenco soggetti qualificati a ricorrere all'accordo quadro"/>
							<c:set var="titoloFunzioneJs" value="Elenco soggetti qualificati a ricorrere all#accordo quadro"/>
						</c:if>
						<img width="16" height="16" title="Elenco soggetti" alt="Elenco soggetti" src="${pageContext.request.contextPath}/img/soggettiAggregati.png"/>
						<a href="javascript:dettaglioSoggAgg('${titoloFunzioneJs}');" title="${titoloFunzione}" >
							 ${titoloFunzione}
						</a>
				</tr>
			</gene:campoScheda>
		</c:if>	

		<gene:campoScheda campo="ISARCHI" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico && datiRiga.TORN_ISARCHI eq '1'}"/>
	</gene:gruppoCampi>

	<c:if test='${garaLottoUnico}'>
		<gene:fnJavaScriptScheda funzione="IniziazilizzazioneISADESIONE( '#GARE_TIPGARG#')" elencocampi="GARE_TIPGARG" esegui="false"/>
	</c:if>
	
        <c:if test='${modoAperturaScheda ne "VISUALIZZA"}'>
		<gene:fnJavaScriptScheda funzione='setTipoCategorie("#TORN_TIPGEN#")' elencocampi='TORN_TIPGEN' esegui="false"/>
		<gene:fnJavaScriptScheda funzione='showOGGCONT("#TORN_TIPGEN#",true)' elencocampi='TORN_TIPGEN' esegui="false"/>
	</c:if>
	<gene:fnJavaScriptScheda funzione="valorizzaStatoGara()" elencocampi="GARE_TIPGARG;TORN_DTEPAR;TORN_OTEPAR;TORN_DTEOFF;TORN_OTEOFF" esegui="true" />
	
	<c:if test='${garaLottoUnico}'>
		<gene:fnJavaScriptScheda funzione="visualizzaESettaCorrettivo('#GARE_MODLICG#', '#TORN_TIPGEN#', '#TORN_CORGAR#', 'TORN_CORGAR', ${garaLottoUnico})" elencocampi="GARE_MODLICG" esegui="true"/>
		<gene:fnJavaScriptScheda funzione="visualizzaTIPFORN( '#TORN_TIPGEN#')" elencocampi="TORN_TIPGEN" esegui="true"/>
		<gene:fnJavaScriptScheda funzione="visualizzaDaISADESIONE( '#TORN_ISADESIONE#')" elencocampi="TORN_ISADESIONE" esegui="true"/>
	</c:if>
	<c:if test="${!garaLottoUnico}">
		<gene:fnJavaScriptScheda funzione="visualizzaESettaCorrettivo('#GARE_MODLICG#', '#TORN_TIPGEN#', '#GARE_CORGAR1#', 'GARE_CORGAR1', ${garaLottoUnico})" elencocampi="GARE_MODLICG" esegui="true"/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="PRIMOATAU">
		<gene:campoScheda visibile="${garaLottoUnico}" addTr="false">
			<tr id="rowTITOLO_ATTO_AUTORIZZATIVO">
				<td colspan="2"><b>Atto autorizzativo</b></td>
			</tr>
		</gene:campoScheda>
		<c:choose>
			<c:when test="${(garaLottoUnico && initAttoDaAppa)}">
				<c:set var="initTATTOG" value="${requestScope.initTDAPPA}"/>
				<c:set var="initNATTOG" value="${requestScope.initNDAPPA }"/>
				<c:set var="initDATTOG" value="${requestScope.initDDAPPA }"/>
			</c:when>
			<c:when test="${garaLottoUnico && !initAttoDaAppa}">
				<c:set var="initTATTOG" value="${requestScope.initTDAPPR }"/>
				<c:set var="initNATTOG" value="${requestScope.initNDAPPR }"/>
				<c:set var="initDATTOG" value="${requestScope.initDDAPPR }"/>
			</c:when>
			<c:otherwise>
				<c:set var="initTATTOG" value="${requestScope.initTATTOT }"/>
				<c:set var="initNATTOG" value="${requestScope.initNATTOT }"/>
				<c:set var="initDATTOG" value="${requestScope.initDATTOT }"/>
			</c:otherwise>
		</c:choose>
		
		<gene:campoScheda campo="TATTOG" visibile="${garaLottoUnico}" defaultValue="${initTATTOG}"/>
		<gene:campoScheda campo="DATTOG" visibile="${garaLottoUnico}" defaultValue="${initDATTOG}"/> 
		<gene:campoScheda campo="NATTOG" visibile="${garaLottoUnico}" defaultValue="${initNATTOG}"/>
		<gene:campoScheda campo="DATRICT" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="NPROAG" visibile="${garaLottoUnico}" defaultValue="${gene:if(garaLottoUnico, '', requestScope.initNPROAT)}" />
		<gene:campoScheda campo="NOTEAT" visibile="${garaLottoUnico}" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR"/>
	</gene:gruppoCampi>
		
	<c:if test="${garaLottoUnico}">
		<c:set var="arrayCampiGaratt" value="'GARATT_CODGAR_', 'GARATT_NUMATT_', 'GARATT_TATTOT_', 'GARATT_DATTOT_', 'GARATT_NATTOT_', 'GARATT_NPROAT_', 'GARATT_DPROAA_','GARATT_NOTEAT_','GARATT_DATRICT_'"/>
		<c:if test='${modoAperturaScheda ne "NUOVO"}' >
			<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneAttiAutorizzativiFunction", pageContext, "GARATT", ngaraPerGaratt)}'/>
		</c:if>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='GARATT'/>
			<jsp:param name="chiave" value='${ngaraPerGaratt}'/>
			<jsp:param name="nomeAttributoLista" value='attiAutorizzativi' />
			<jsp:param name="idProtezioni" value="ATAU" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garatt/atto-autorizzativo.jsp"/>
			<jsp:param name="arrayCampi" value="${arrayCampiGaratt}"/>
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="titoloSezione" value="Altro atto autorizzativo" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo atto autorizzativo" />
			<jsp:param name="descEntitaVociLink" value="atto autorizzativo" />
			<jsp:param name="msgRaggiuntoMax" value="i atti autorizzativi"/>
		</jsp:include>
	
		<c:if test='${modoAperturaScheda ne "NUOVO"}' >
			<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneRichiesteAcquistoFunction", pageContext, ngaraPerGaratt, "")}'/>
		</c:if>
		
		<% // la sezione seguente viene utilizzata anche per AVM e dev'essere presente solo quando non si vede la lista delle lav/forn %>
		<c:set var="visGareRda" value="true" />
		<c:if test='${integrazioneWSERP eq "1" && (tipoWSERP eq "AVM" || tipoWSERP eq "UGOVPA") && (visListaLavForn eq "true" && isAccordoQuadro ne "1")}'>
			<c:set var="visGareRda" value="false" />
		</c:if>
		<c:set var="arrayCampiGarerda" value="'GARERDA_ID_', 'GARERDA_CODGAR_', 'GARERDA_CODCARR_', 'GARERDA_NUMRDA_', 'GARERDA_POSRDA_', 'GARERDA_DATCRE_', 'GARERDA_DATRIL_', 'GARERDA_DATACONS_', 'GARERDA_LUOGOCONS_', 'GARERDA_CODVOC_', 'GARERDA_VOCE_', 'GARERDA_CODCAT_', 'GARERDA_UNIMIS_', 'GARERDA_QUANTI_', 'GARERDA_PREZUN_', 'GARERDA_PERCIVA_', 'GARERDA_ESERCIZIO_', 'GARERDA_NGARA_'"/>
		<c:if test='${visGareRda ne "false"}' >
			<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
				<jsp:param name="entita" value='GARERDA'/>
				<jsp:param name="chiave" value='${ngaraPerGaratt}'/>
				<jsp:param name="nomeAttributoLista" value='listaRichiesteAcquisto' />
				<jsp:param name="idProtezioni" value="GARERDA" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garerda/richieste-acquisto.jsp"/>
				<jsp:param name="arrayCampi" value="'GARERDA_ID_', 'GARERDA_CODGAR_', 'GARERDA_CODCARR_', 'GARERDA_NUMRDA_', 'GARERDA_POSRDA_', 'GARERDA_DATCRE_', 'GARERDA_DATRIL_', 'GARERDA_DATACONS_', 'GARERDA_LUOGOCONS_', 'GARERDA_CODVOC_', 'GARERDA_VOCE_', 'GARERDA_CODCAT_', 'GARERDA_UNIMIS_', 'GARERDA_QUANTI_', 'GARERDA_PREZUN_', 'GARERDA_PERCIVA_', 'GARERDA_ESERCIZIO_', 'GARERDA_NGARA_'"/>
				<jsp:param name="sezioneListaVuota" value="true" />
				<jsp:param name="titoloSezione" value="Richiesta di acquisto" />
				<jsp:param name="titoloNuovaSezione" value="Nuova richiesta di acquisto" />
				<jsp:param name="descEntitaVociLink" value="richiesta di acquisto" />
				<jsp:param name="msgRaggiuntoMax" value="e richieste di acquisto"/>
				<jsp:param name="usaContatoreLista" value="true" />
			</jsp:include>
		</c:if>
	
		<c:if test='${modoAperturaScheda ne "NUOVO"}' >
			<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneImpegniDiSpesaFunction", pageContext, "GAREIDS", ngaraPerGaratt)}'/>
		</c:if>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='GAREIDS'/>
			<jsp:param name="chiave" value='${ngaraPerGaratt}'/>
			<jsp:param name="nomeAttributoLista" value='impegniDiSpesa' />
			<jsp:param name="idProtezioni" value="IDS" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gareids/impegno-di-spesa.jsp"/>
			<jsp:param name="arrayCampi" value="'GAREIDS_CODGAR_', 'GAREIDS_NUMIDS_', 'GAREIDS_DATEMISS_', 'GAREIDS_NPROT_', 'GAREIDS_DATRICEZ_', 'GAREIDS_IMPIDS_', 'GAREIDS_NOTEIDS_', 'GAREIDS_PROGIDS_'"/>
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="titoloSezione" value="Impegno di spesa" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo impegno di spesa" />
			<jsp:param name="descEntitaVociLink" value="impegno di spesa" />
			<jsp:param name="msgRaggiuntoMax" value="i impegni di spesa"/>
		</jsp:include>

		<gene:gruppoCampi idProtezioni="AVVESPL">
			<gene:campoScheda>
				<td colspan="2"><b>Avviso esplorativo</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="DPUBAVVISO" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
			<gene:campoScheda campo="DTPUBAVVISO" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
		</gene:gruppoCampi>
	</c:if>

	<c:if test='${esisteIntegrazioneLavori eq "TRUE"}'>

	<gene:gruppoCampi idProtezioni="RILA">
		<gene:campoScheda>
			<td colspan="2"><b>Riferimento all'appalto</b></td>
		</gene:campoScheda>

		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.AssociaGaraAppalto")}'>

				<c:choose>
					<c:when test='${modo eq "MODIFICA"}' >
						<c:set var="archivioWhereScheda" value=" (APPA.DAGG is null and APPA.DVOAGG is null) and (APPA.TIPLAVG = ${datiRiga.TORN_TIPGEN}) and not exists (select NGARA from GARE where APPA.CODLAV = GARE.CLAVOR and APPA.NAPPAL = GARE.NUMERA and GARE.NGARA != '${datiRiga.GARE_NGARA}') ${filtroLivelloUtente} " />
					</c:when>
					<c:otherwise>
						<c:set var="archivioWhereScheda" value=" (APPA.DAGG is null and APPA.DVOAGG is null) and (APPA.TIPLAVG = ${datiRiga.TORN_TIPGEN}) and not exists (select NGARA from GARE where APPA.CODLAV = GARE.CLAVOR and APPA.NAPPAL = GARE.NUMERA) ${filtroLivelloUtente} " />
					</c:otherwise>
				</c:choose>
	
				<gene:archivio titolo="Appalti"
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.CLAVOR"), "gare/gare/trovaAppalto/popup-lista-appalti.jsp","")}'
					scheda=""
					schedaPopUp=""
					campi="APPA.CODLAV;APPA.NAPPAL;APPA.CODCUA"
					chiave=""
					where="${archivioWhereScheda}"
					inseribile="false"
					formName="formArchivioAppalti" >
					<gene:campoScheda campo="CLAVOR" defaultValue="${requestScope.initCLAVOR}" modificabile="false"/>
					<gene:campoScheda campo="NUMERA"  defaultValue="${requestScope.initNUMERA}" modificabile="false"/>
					<gene:campoScheda campo="CODCUA" entita="APPA" where="GARE.CLAVOR = APPA.CODLAV and GARE.NUMERA = APPA.NAPPAL" defaultValue="${requestScope.initCODCUA}" modificabile="false"/>
				</gene:archivio>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="CLAVOR" />
				<gene:campoScheda campo="NUMERA" />
				<c:if test='${gene:checkProt(pageContext, "COLS.VIS.LAVO.APPA.CODCUA")}'>
					<gene:campoScheda campo="CODCUA" entita="APPA" where="GARE.CLAVOR = APPA.CODLAV and GARE.NUMERA = APPA.NAPPAL" modificabile="false"/>
				</c:if>
			</c:otherwise>
		</c:choose>
	</gene:gruppoCampi>
	</c:if>
	
	<c:if test='${modo eq "NUOVO" && (empty initCENINT || ! empty sessionScope.uffint) && garaLottoUnico}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ValorizzaStazioneAppaltanteFunction" parametro=""/>
	</c:if>
	
	<c:choose>
		<c:when test="${garaLottoUnico}">
			<c:set var="titoloSezioneRUP" value="Stazione appaltante e RUP"/>
		</c:when>
		<c:otherwise>
			<c:set var="titoloSezioneRUP" value="Soggetto per cui agisce la centrale di committenza"/>
		</c:otherwise>
	</c:choose>
	
	
	<gene:gruppoCampi idProtezioni="RUP" >
		<gene:campoScheda nome="RUP">
			<td colspan="2"><b>${titoloSezioneRUP }</b></td>
		</gene:campoScheda>
		
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN;UFFINT.ISCUC"
			 chiave="TORN_CENINT"
			 where="UFFINT.DATFIN IS NULL"
			 formName="formUFFINTGare">
				<gene:campoScheda campo="CENINT" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" obbligatorio="true" defaultValue="${requestScope.initCENINT}" modificabile="${empty sessionScope.uffint }" visibile="${garaLottoUnico}">
					<gene:checkCampoScheda funzione='checkPuntiContatto()' obbligatorio="true" messaggio="Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto nella gara" onsubmit="false"/>
				</gene:campoScheda>
		<c:choose>
			<c:when test='${modo eq "NUOVO"}'>
				<gene:campoScheda campo="NOMEIN" title="Denominazione" campoFittizio="true" definizione="T100;;;;NOMEIN" defaultValue="${requestScope.initNOMEIN}"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") && empty sessionScope.uffint}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT") && garaLottoUnico}' >
					<gene:checkCampoScheda funzione='checkPuntiContatto()' obbligatorio="true" messaggio="Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto nella gara" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="ISCUC" entita="UFFINT" from ="TORN" where="TORN.CENINT = UFFINT.CODEIN and TORN.CODGAR=GARE.CODGAR1" visibile="false" defaultValue="${requestScope.initISCUC}"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="NOMEIN" title="Denominazione" campoFittizio="true" definizione="T100;;;;NOMEIN" value="${requestScope.denominazione}"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") && empty sessionScope.uffint}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT") && garaLottoUnico}' >
					<gene:checkCampoScheda funzione='checkPuntiContatto()' obbligatorio="true" messaggio="Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto nella gara" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="ISCUC" entita="UFFINT" from ="TORN" where="TORN.CENINT = UFFINT.CODEIN and TORN.CODGAR=GARE.CODGAR1" visibile="false"/>
			</c:otherwise>
		</c:choose>
		</gene:archivio>
		
		<gene:campoScheda campo="UFFDET" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}" />
		
		<gene:campoScheda campo="LIVACQ" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 chiave="TORN_CODRUP"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="${requestScope.initCODRUP}" visibile="${garaLottoUnico }"/>
		<c:choose>
			<c:when test='${modo eq "NUOVO"}' >
				<gene:campoScheda campo="NOMTEC" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1" defaultValue="${requestScope.initNOMTEC1}"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP") && garaLottoUnico}'/>
			</c:when>
			<c:when test='${modo eq "MODIFICA" or modo eq "VISUALIZZA"}'>
				<gene:campoScheda campo="NOMTEC" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1" value="${requestScope.tecnico}"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP") && garaLottoUnico}'/>
			</c:when>
		</c:choose>
		</gene:archivio>
				
		<gene:campoScheda campo="ALTRISOG" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile ="${garaLottoUnico }" defaultValue="${requestScope.initALTRISOG}" obbligatorio="true"/>
				
		<c:if test='${modoAperturaScheda ne "NUOVO" and datiRiga.TORN_ALTRISOG eq "2"}' >
			<c:set var="tmp1" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneGaraltsogFunction", pageContext, datiRiga.GARE_NGARA)}'/>
		</c:if>
		
		<gene:archivio titolo="Uffici intestatari"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			campi="UFFINT.CODEIN;UFFINT.NOMEIN"
			 chiave="CENINT_GARALTSOG"
			 where="UFFINT.DATFIN IS NULL and (UFFINT.ISCUC is null or UFFINT.ISCUC <>'1')"
			 formName="formUFFINTGareAltriSogg">
			 <gene:campoScheda campo="CENINT_GARALTSOG" title="Codice stazione appaltante aderente" campoFittizio="true" definizione="T16;0;;;G1CENINTSOG" value="${initCenintGaraltsog }"
			 	modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARALTSOG.CENINT") }' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARALTSOG.CENINT")}' />
			 <gene:campoScheda campo="NOMEIN_GARALTSOG" title="Denominazione" campoFittizio="true" definizione="T254;0;;NOTE;NOMEIN" value="${initNomeinGaraltsog }"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT") }' 
				visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CENINT")}' />
		</gene:archivio>
		
		
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 chiave="CODRUP_GARALTSOG"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP_GARALTSOG" title="Codice resp.unico procedimento stazione appaltante aderente" campoFittizio="true" definizione="T10;0;;;G1CODRUPSOG" value="${requestScope.initCodrupGaraltsog}"
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARALTSOG.CODRUP") }' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARALTSOG.CODRUP")}' />
				<gene:campoScheda campo="NOMTEC_GARALTSOG" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1" value="${requestScope.initNomtecGaraltsog}"
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}'/>
		</gene:archivio>
		
		
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioSoggettiAggregati") && modo eq "VISUALIZZA" and garaLottoUnico}' >
			<gene:campoScheda addTr="false">
				<tr id="rowLinkDetSogAgg" style="display: none;">
					<td colspan="2" class="valore-dato" id="colonnaLinkDetSogAgg">
						<c:set var="titoloFunzione" value="Elenco soggetti per cui agisce la centrale di committenza"/>
						<c:set var="titoloFunzioneJs" value="Elenco soggetti per cui agisce la centrale di committenza"/>
						<c:if test="${datiRiga.TORN_ALTRISOG ne 3 or  empty datiRiga.TORN_ACCQUA}">
							<c:set var="titoloFunzione" value="Elenco soggetti qualificati a ricorrere all'accordo quadro"/>
							<c:set var="titoloFunzioneJs" value="Elenco soggetti qualificati a ricorrere all#accordo quadro"/>
						</c:if>
						<img width="16" height="16" title="Elenco soggetti" alt="Elenco soggetti" src="${pageContext.request.contextPath}/img/soggettiAggregati.png"/>
						<a href="javascript:dettaglioSoggAgg('${titoloFunzioneJs}');" title="${titoloFunzione}" >
							 ${titoloFunzione}
						</a>
				</tr>
			</gene:campoScheda>
		</c:if>	
		
		<gene:fnJavaScriptScheda funzione='aggiornaVisualizzazioneDaArchivioUffint("#UFFINT_ISCUC#")' elencocampi='UFFINT_ISCUC' esegui="true" />
		<gene:fnJavaScriptScheda funzione='sbiancaArchivioUffintAltriSogg("#TORN_CENINT#")' elencocampi='TORN_CENINT' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneArchivio("#TORN_ALTRISOG#")' elencocampi='TORN_ALTRISOG' esegui="true" />
		
		<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.UFFDET") and garaLottoUnico}'>
			<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneUffedt("#TORN_CENINT#","2")' elencocampi='TORN_CENINT' esegui="false" />
		</c:if>
		
		<c:if test="${garaLottoUnico && modo ne 'VISUALIZZA'}">
			<gene:fnJavaScriptScheda funzione='aggiornaFiltroArchivioAccordiQuadro("#GARE_TIPGARG#",true,null)' elencocampi='TORN_CENINT' esegui="false" />
		</c:if>
		
	</gene:gruppoCampi>
	
	
	<c:if test="${garaLottoUnico}">
		<c:set var="arrayCampiGartecni" value="'GARTECNI_CODGAR_', 'GARTECNI_NUMTEC_', 'GARTECNI_CODTEC_', 'GARTECNI_NOMTEC_', 'GARTECNI_INCTEC_'"/>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneUlterioriReferentiIncaricatiFunction" parametro='${codgarPerGartecni}' />
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='GARTECNI'/>
			<jsp:param name="chiave" value='${codgarPerGartecni}'/>
			<jsp:param name="nomeAttributoLista" value='ultReferentiIncaricati' />
			<jsp:param name="idProtezioni" value="ULTREFINC" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/gartecni/referente-incaricato.jsp"/>
			<jsp:param name="arrayCampi" value="'GARTECNI_CODGAR_', 'GARTECNI_NUMTEC_', 'GARTECNI_CODTEC_', 'GARTECNI_NOMTEC_', 'GARTECNI_INCTEC_'"/>		
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="titoloSezione" value="Ulteriore referente o incaricato" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo referente o incaricato" />
			<jsp:param name="descEntitaVociLink" value="referente o incaricato" />
			<jsp:param name="msgRaggiuntoMax" value="i referenti o incaricati"/>
		</jsp:include>
		<gene:gruppoCampi idProtezioni="DAQ">
			<gene:campoScheda nome="DAQ">
				<td colspan="2"><b>Durata dell'accordo quadro</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="AQDURATA"  entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR"/>
			<gene:campoScheda campo="AQTEMPO"  entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR"/>
		</gene:gruppoCampi>
	</c:if>
		
	<gene:gruppoCampi idProtezioni="TMP">
		<gene:campoScheda nome="TMP">
			<td colspan="2"><b>Durata del contratto</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DINLAVG"/>
		<gene:campoScheda campo="TEUTIL" defaultValue="${requestScope.initTEUTIL}" />
		<c:choose>
			<c:when test="${empty requestScope.initTEMESI}">
				<c:set var="TEMESIdefault" value="1" />
			</c:when>
			<c:otherwise>
				<c:set var="TEMESIdefault" value="${requestScope.initTEMESI}" />
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="TEMESI" modificabile="${datiRiga.TORN_TIPGEN == '2' || datiRiga.TORN_TIPGEN == '3'}" defaultValue="${TEMESIdefault}"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="CONSEGNA">
		<gene:campoScheda nome= "CONSEGNA">
			<td colspan="2"><b>Termini di consegna dei beni o di esecuzione dei servizi</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false"/>
		<gene:campoScheda campo="DTERMCON" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
		<gene:campoScheda campo="NGIOCON" entita="GARE1" where="GARE1.NGARA=GARE.NGARA"/>
	</gene:gruppoCampi>
		
	<jsp:include page="/WEB-INF/pages/gare/gare/gare-importoBaseAsta.jsp" >
		<jsp:param name="tipgen" value='${datiRiga.TORN_TIPGEN}'/>
		<jsp:param name="campiModificabili" value='true'/>
	</jsp:include>
	<gene:fnJavaScriptScheda funzione='aggiornaCategorieAppalto("GARE_IMPAPP")' elencocampi='GARE_IMPAPP' esegui="false" />
	<gene:fnJavaScriptScheda funzione='aggiornaArchiviPuntiContatto("#TORN_CENINT#")' elencocampi='TORN_CENINT' esegui="false" />

	<jsp:include page="/WEB-INF/pages/gare/commons/categorie-gara.jsp" >
		<jsp:param name="tipgen" value='${datiRiga.TORN_TIPGEN}'/>
	</jsp:include>

	<gene:gruppoCampi idProtezioni="ALTOFF">
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati relativi alla modalità di presentazione offerta e svolgimento della procedura</b></td>
		</gene:campoScheda>
   		<c:choose>
			<c:when test="${!empty initPrerib }">
				<c:set var="inizializzazionePrerib" value="${requestScope.initPrerib }"/>
			</c:when>
			<c:otherwise>
				<c:set var="inizializzazionePrerib" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1028","1","false")}'/>
			</c:otherwise>
		</c:choose>
   		<gene:campoScheda campo="PRERIB" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue='${inizializzazionePrerib }' visibile="true">
   			<gene:checkCampoScheda funzione='"##" < 9' obbligatorio="true" messaggio="Il valore specificato deve essere compreso tra 0 e 9" onsubmit="false"/>
   		</gene:campoScheda>
   		<c:choose>
   			<c:when test='${param.proceduraTelematica eq 1}'>
				<c:choose>
					<c:when test="${!empty initRICMANO }">
						<c:set var="inizializzazioneRicmano" value="${requestScope.initRICMANO }"/>
					</c:when>
					<c:otherwise>
						<c:set var="inizializzazioneRicmano" value="${valoreTabellatoA1115Ricmano }"/>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="RICMANO" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}" defaultValue="${inizializzazioneRicmano}"/>
			</c:when>
   			<c:otherwise>
				<gene:campoScheda campo="RICMANO" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}" defaultValue=""/>
   			</c:otherwise>
   		</c:choose>
   		<gene:fnJavaScriptScheda funzione="visualizzaMODMANO('#TORN_RICMANO#')" elencocampi="TORN_RICMANO" esegui="true"/>
   		<gene:campoScheda campo="MODMANO" entita="TORN" obbligatorio="true" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${modo ne 'VISUALIZZA' or datiRiga.TORN_RICMANO eq 1}" defaultValue="1"/>
		<gene:campoScheda campo="ULTDETLIC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" defaultValue="${gene:if(garaLottoUnico, '', requestScope.initULTDETLIC)}"/>
		<gene:campoScheda campo="SORTINV" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico && (param.proceduraTelematica eq 1  or datiRiga.TORN_GARTEL eq 1)}"/>
		<gene:campoScheda campo="NUMOPE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" obbligatorio="true" visibile="${garaLottoUnico && (param.proceduraTelematica eq 1  or datiRiga.TORN_GARTEL eq 1)}"/>
		<gene:campoScheda campo="INVERSA" entita="TORN" where="TORN.CODGAR = GARE.CODGAR1" visibile="${garaLottoUnico && (param.proceduraTelematica eq 1  or datiRiga.TORN_GARTEL eq 1)}" defaultValue="2"/>
		<c:choose>
			<c:when test="${!empty initCOMPREQ }">
				<c:set var="inizializzazioneCompreq" value="${requestScope.initCOMPREQ }"/>
			</c:when>
			<c:otherwise>
				<c:set var="inizializzazioneCompreq" value="${valoreTabellatoA1115Compreq }"/>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${(!empty initVALTEC &&  garaLottoUnico) || !garaLottoUnico}">
				<c:set var="inizializzazioneValtec" value="${requestScope.initVALTEC }"/>
			</c:when>
			<c:otherwise>
				<c:set var="inizializzazioneValtec" value="${valoreTabellatoA1115 }"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="COMPREQ" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}" defaultValue="${inizializzazioneCompreq }"/>
		<gene:campoScheda campo="VALTEC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" defaultValue="${inizializzazioneValtec}" />
		<gene:campoScheda campo="COSTOFISSO" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${isProceduraTelematica or param.proceduraTelematica eq '1'}"/>
		<gene:campoScheda campo="SEZIONITEC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="${isProceduraTelematica or param.proceduraTelematica eq '1'}"/>
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione='gestioneSortinv("#TORN_ITERGA#")' elencocampi='TORN_ITERGA' esegui="true" />
	<gene:fnJavaScriptScheda funzione='gestioneNumope("#TORN_SORTINV#")' elencocampi='TORN_SORTINV' esegui="true" />
	<gene:fnJavaScriptScheda funzione="aggiornaCompreq('#TORN_INVERSA#')" elencocampi="TORN_INVERSA" esegui="false" />
	
	<gene:gruppoCampi idProtezioni="ALT">
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati relativi alla procedura</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="OGGCONT" defaultValue="${gene:if(garaLottoUnico, '', requestScope.initOGGCONT)}" visibile='${not(modo eq "VISUALIZZA" and datiRiga.TORN_TIPGEN eq "3")}'/>
		<%/* Campi fittizzi per gestire i valori del tabellato A1031 da associare a OGGCONT in funzione di TIPGEN */%>
		<gene:campoScheda campo="OGGCONT_LAVORI" title="Oggetto contratto" entita="V_GARE_TORN"  campoFittizio="true"  definizione="N7;0;A1031;;G1OGGCONTG" value="${datiRiga.GARE_OGGCONT}" visibile='${modo ne "VISUALIZZA"}'/>
		<gene:campoScheda campo="OGGCONT_FORNITURE" title="Oggetto contratto" entita="V_GARE_TORN"  campoFittizio="true"  definizione="N7;0;A1031;;G1OGGCONTG" value="${datiRiga.GARE_OGGCONT}" visibile='${modo ne "VISUALIZZA"}'/>
		<gene:campoScheda campo="TIPLAV" visibile='${datiRiga.TORN_TIPGEN eq "1"}' defaultValue="${gene:if(garaLottoUnico, '', requestScope.initTIPLAV)}"/>
		<gene:campoScheda campo="URBASCO" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='${garaLottoUnico and datiRiga.TORN_TIPGEN eq "1"}'/>
		<gene:campoScheda campo="SOMMAUR" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='${garaLottoUnico}' defaultValue="${gene:if(garaLottoUnico, requestScope.initSOMMAUR, '')}"/>
		<gene:campoScheda campo="PROURG" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="MOTACC" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="PREINF" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="BANWEB" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="DOCWEB" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="TERRID" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="SEGRETA"/>
		<gene:campoScheda campo="SUBGAR"/>
		<gene:campoScheda campo="NORMA1" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="NORMA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}"/>
		<gene:campoScheda campo="TUS" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${garaLottoUnico}" defaultValue="${requestScope.initTUS}" />
		<gene:campoScheda campo="CONTOECO" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" defaultValue="${requestScope.initCONTECO}" />
		<gene:campoScheda campo="NOTEGA"/>
		<gene:campoScheda campo="IDCOMMALBO" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="${requestScope.initIDCOMMALBO}" visibile="false" />
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione='aggiornaOGGCONT("#V_GARE_TORN_OGGCONT_LAVORI#")' elencocampi='V_GARE_TORN_OGGCONT_LAVORI' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='aggiornaOGGCONT("#V_GARE_TORN_OGGCONT_FORNITURE#")' elencocampi='V_GARE_TORN_OGGCONT_FORNITURE' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='visualizzaMotacc("#TORN_PROURG#")' elencocampi='TORN_PROURG' esegui="true"/>
	<gene:fnJavaScriptScheda funzione='gestioneULTDETLIC("#GARE_CRITLICG#","#GARE_DETLICG#")' elencocampi='GARE_CRITLICG;GARE_DETLICG' esegui="true" />	
	
	<c:if test="${isProceduraTelematica or param.proceduraTelematica eq 1}">
		<gene:gruppoCampi idProtezioni="ASTA">
			<gene:campoScheda nome="ASTA">
				<td colspan="2"><b>Asta elettronica </b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="RICASTAE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
			<gene:campoScheda campo="AERIBMIN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" definizione="F13.9;0;;PRC;"/>
			<gene:campoScheda campo="AERIBMAX" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" definizione="F13.9;0;;PRC;"/>
			<gene:campoScheda campo="AEIMPMIN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
			<gene:campoScheda campo="AEIMPMAX" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
			<gene:campoScheda campo="AEMODVIS" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
			<gene:campoScheda campo="AENOTE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
		</gene:gruppoCampi>
		<gene:fnJavaScriptScheda funzione='gestioneCampiAstaEl_Critlicg("#GARE_CRITLICG#")' elencocampi='GARE_CRITLICG' esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneCampiAstaEl_Ricastae("#TORN_RICASTAE#")' elencocampi='TORN_RICASTAE' esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneCampiAstaEl_Ribcal("#GARE_RIBCAL#")' elencocampi='GARE_RIBCAL' esegui="false" />		
	</c:if>
	
	
	
	<c:choose>
		<c:when test="${garaLottoUnico}">
			<jsp:include page="/WEB-INF/pages/gare/torn/torn-sez-presentazione.jsp">
				<jsp:param name="campoTipoProcedura" value="GARE_TIPGARG"/>
				<jsp:param name="isGaraLottoUnico" value="true"/>
				<jsp:param name="isGaraOffertaUnica" value="false"/>
				<jsp:param name="valoreCodgar" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>
			</jsp:include>
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="DTEPAR" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
			<gene:campoScheda campo="OTEPAR" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
			<gene:campoScheda campo="DTEOFF" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
			<gene:campoScheda campo="OTEOFF" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false"/>
		</c:otherwise>
	</c:choose>
	
	<gene:campoScheda campo="DITTA" visibile="false"/>
	
	<c:if test='${garaLottoUnico}'>
		<gene:campoScheda campo="MODREA" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" visibile="false"/>
	</c:if>
	
	<c:if test='${modo eq "NUOVO" and garaLottoUnico}'>
		<gene:campoScheda campo="GARPRIV" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" visibile="false" value="${garpriv}"/>
	</c:if>
	
	<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = GARE.CODGAR1 AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>
	<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = GARE.CODGAR1 AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>

	<gene:campoScheda campo="AMMRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false" defaultValue="${requestScope.initAMMRIN}"/>
	<gene:campoScheda campo="DESRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="IMPRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false" defaultValue="${requestScope.initIMPRIN}"/>
	<gene:campoScheda campo="AMMOPZ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false" defaultValue="${requestScope.initAMMOPZ}"/>
	<gene:campoScheda campo="DESOPZ" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="IMPSERV" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="IMPPROR" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="IMPALTRO" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false" defaultValue="${requestScope.initIMPALTRO}"/>
			
	<c:if test="${garaLottoUnico}">
		<gene:campoScheda campo="VALMAX" entita="V_GARE_IMPORTI" where="V_GARE_IMPORTI.NGARA=GARE.NGARA" visibile="false"/>
		<gene:campoScheda campo="UUID" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" visibile="false"/>
		<gene:campoScheda campo="UUID" entita="TORN" where="TORN.CODGAR=GARE.GARE1" visibile="false"/>
	</c:if>
	
	<gene:campoScheda campo="INTEGRAZIONE_ERPvsWSDM" title="INTEGRAZIONE_ERPvsWSDM" campoFittizio="true" definizione="T1" defaultValue="${requestScope.initERPvsWSDM}" visibile="false" />
		
        <gene:campoScheda>
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
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
					</c:if>
				</gene:insert>
				<c:if test='${ garaLottoUnico and datiRiga.TORN_GARTEL eq 1 && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_TORN-lista.Condividi-gara")}'>
	     		<c:choose>
	     			<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
							<INPUT type="button"  class="bottone-azione" value='Punto ordinante e istruttore' title='Punto ordinante e istruttore' onclick="javascript:apriGestionePermessiGaraTelematica('${datiRiga.GARE_CODGAR1}', ${tipologiaGara});" >
	     			</c:when>
	     			<c:otherwise>
	     				<INPUT type="button"  class="bottone-azione" value='Punto ordinante e istruttore' title='Punto ordinante e istruttore' onclick="javascript:apriGestionePermessiGaraTelematicaStandard('${datiRiga.GARE_CODGAR1}', ${tipologiaGara});" >
	     			</c:otherwise>
	     		</c:choose>
				</c:if>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
<!--
 Parametri provenienti dalla pagina di associazione della gara all'appalto. Vengono salvati
 perche' in caso di errore in salvataggio della scheda sono necessari a ricaricare 
 correttamente la scheda
 -->
	<input type="hidden" name="tipoAppalto" id="tipoAppalto" value="${param.tipoAppalto}"/>
	<input type="hidden" name="tipoGara" id="tipoGara" value="${param.tipoGara}"/>
	<input type="hidden" name="chiaveRiga" id="chiaveRiga" value="${param.chiaveRiga}"/>
	<input type="hidden" name="modalitaPresentazione" id="modalitaPresentazione" value="${param.modalitaPresentazione}"/>
	<input type="hidden" name="lottoOfferteDistinte" id="lottoOfferteDistinte" value="${param.lottoOfferteDistinte}"/>
	<input type="hidden" name="codCPV" id="codCPV" value="${requestScope.initCODCPV}"/>
	<input type="hidden" name="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
	<input type="hidden" name="paginaDatiGenGare" value="si" />
	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${bloccoModificatiDati}"/>
</gene:formScheda>

		<form name="formVisualizzaPermessiUtenti" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtenti.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form> 
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStandard.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>

<gene:javaScript>

	var idconfi = "${idconfi}";
	
	<c:if test='${modoAperturaScheda eq "NUOVO" }'>
		function initGare1(){
			setOriginalValue("GARE1_VALTEC"," ");
			setOriginalValue("GARE1_CONTOECO","-1");
			
		}
		
		initGare1();
		
		<c:if test="${initGaratt && garaLottoUnico}">
			function initGaratt(){
				//Se lottoUnico, è stato selezionato un appalto, ci sono dati di APPR da riportare in GARATT ed è abilitato l'inserimento in GARATT
				showNextElementoSchedaMultipla('ATAU', new Array(${arrayCampiGaratt}), new Array());
				setValue("GARATT_TATTOT_1","${initTDAPPR}");
				setValue("GARATT_NATTOT_1","${initNDAPPR}");
				setValue("GARATT_DATTOT_1","${initDDAPPR}");
			}
			initGaratt();
		</c:if>
		
		
		<c:if test="${initGartecni && garaLottoUnico}">
			function initGartecni(){
				//Se lottoUnico-- 
				var contatore = 0;
				<c:if test="${!empty initCODTEC}">
					contatore =contatore+1;
					showNextElementoSchedaMultipla('ULTREFINC', new Array(${arrayCampiGartecni}), new Array());
					setValue("GARTECNI_CODTEC_"+contatore,"${initCODTEC}");
					setValue("GARTECNI_NOMTEC_"+contatore,"${initNOMTEC}");
					setValue("GARTECNI_INCTEC_"+contatore,"2");
				</c:if>
				<c:if test="${!empty initCODRPROGR}">
					contatore =contatore+1;
					showNextElementoSchedaMultipla('ULTREFINC', new Array(${arrayCampiGartecni}), new Array());
					setValue("GARTECNI_CODTEC_"+contatore,"${initCODRPROGR}");
					setValue("GARTECNI_NOMTEC_"+contatore,"${initNOMRPROGR}");
					setValue("GARTECNI_INCTEC_"+contatore,"14");
				</c:if>
				<c:if test="${!empty initCODDEC}">
					contatore =contatore+1;
					showNextElementoSchedaMultipla('ULTREFINC', new Array(${arrayCampiGartecni}), new Array());
					setValue("GARTECNI_CODTEC_"+contatore,"${initCODDEC}");
					setValue("GARTECNI_NOMTEC_"+contatore,"${initNOMDEC}");
					setValue("GARTECNI_INCTEC_"+contatore,"12");
				</c:if>
				<c:if test="${!empty initCODRO}">
					contatore =contatore+1;
					showNextElementoSchedaMultipla('ULTREFINC', new Array(${arrayCampiGartecni}), new Array());
					setValue("GARTECNI_CODTEC_"+contatore,"${initCODRO}");
					setValue("GARTECNI_NOMTEC_"+contatore,"${initNOMRO}");
					setValue("GARTECNI_INCTEC_"+contatore,"5");
				</c:if>
			}
			initGartecni();
		</c:if>
		
		<c:if test="${initGarerda && garaLottoUnico}">
			function initGarerda(){
				//Se lottoUnico, 
				//showNextElementoSchedaMultipla('GARERDA', new Array(${arrayCampiGarerda}), new Array());
				setValue("GARERDA_NUMRDA_1","${initNUMRDA}");
				setValue("GARERDA_CODCARR_1","${initCODCARR}");
				setValue("GARERDA_ESERCIZIO_1","${initESERCIZIO}");
			}
			initGarerda();
		</c:if>
		
		<c:if test="${initFromApparda && garaLottoUnico}">
			var numRdaList = "${initNUMRDA}";
			var voceRdaList = "${initVOCE}";
			var tipoRdaList = "${initTIPORDA}";
			var numRdaArray = numRdaList.split(';');
			var voceRdaArray = voceRdaList.split(';');
			var tipoRdaArray = tipoRdaList.split(';');
			for(var ind=0; ind < numRdaArray.length; ind++){
				if(ind>0){
					showNextElementoSchedaMultipla('GARERDA', new Array(${arrayCampiGarerda}), new Array());
				}
				var numRda =  numRdaArray[ind];
				var voceRda = voceRdaArray[ind];
				var tipoRda = tipoRdaArray[ind];
				var k =ind+1; 
				setValue("GARERDA_NUMRDA_"+k,numRda);
				setValue("GARERDA_VOCE_"+k,voceRda);
				setValue("GARERDA_ESERCIZIO_"+k,tipoRda);
			}
		</c:if>
	</c:if>
	
	function listaDiscussioni(){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_discuss_p/w_discuss_p-lista.jsp&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		document.location.href = href;
	}
	
	function visualizzaMODMANO(ricmano){
		if(ricmano == 1){
			showObj("rowTORN_MODMANO",true);
			if(getValue("TORN_MODMANO") == null || getValue("TORN_MODMANO")=="")
				setValue("TORN_MODMANO","1");
			
		}else{
			setValue("TORN_MODMANO","");
			showObj("rowTORN_MODMANO",false);
		}
	}
	
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioSoggettiAggregati") && modo eq "VISUALIZZA" }' >
		var accqua = getValue("TORN_ACCQUA");
		var altrisog = getValue("TORN_ALTRISOG");
		if((accqua==1 && (altrisog== 1 || altrisog== null || altrisog== "")) || altrisog== 3){
			$( '#rowLinkDetSogAgg' ).show( "fast" );
			//Per visualizzare il link allineato al margine sinistro.
			$("#colonnaLinkDetSogAgg").css("padding-left","0px");
		}
		
		function dettaglioSoggAgg(titolo){
			var ngara = getValue("GARE_NGARA");
			var codgar = getValue("GARE_CODGAR1");
			var comando = "href=gare/gare/popup-dettaglio-soggetti-aggregati.jsp";
		   	comando = comando + "&ngara=" + ngara + "&codgar=" + codgar;
		   	titolo = titolo.replace("#","'");
		   	comando += "&titolo=" + titolo;
		   	openPopUpCustom(comando, "dettaglioSoggAgg", 900, 550, "yes", "yes");
		}
	</c:if>
	
	showObj("rowTORN_ITERGA",false);
	showObj("rowTORN_ISADESIONE",false);
	
	function popupInviaBandoAvvisoSimap(codgar,iterga,settore) {
	   var comando = "href=gare/commons/popup-invia-bando-avviso-simap.jsp";
	   comando = comando + "&codgar=" + codgar + "&iterga=" + iterga + "&settore=" + settore;
	   openPopUpCustom(comando, "inviabandoavvisosimap", 550, 650, "yes", "yes");
	}
	
	function popupInviaVigilanza(codgar){
		var comando = "href=gare/commons/invia-vigilanza-pg.jsp";
	   	comando += "&codgar=" + codgar;
	   	<c:choose>
	   		<c:when test="${!empty nomeApplicativo and nomeApplicativo!=''}">
	   			var nomeApplicativo = "${nomeApplicativo}";
	   		</c:when>
	   		<c:otherwise>
	   			var nomeApplicativo = "Vigilanza";
	   		</c:otherwise>
	   	</c:choose>
	   	comando += "&nomeApplicativo=" +  nomeApplicativo ;
	   	comando +="&genereGara=2";
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&"+comando;
	}
	
	function schedaNuovaGara(){
		document.location.href = contextPath + "/pg/InitNuovaGara.do?" + csrfToken;
	}
	
	function schedaNuovaGaraLotto() {
		if(document.forms[0].keyParent.value==null || document.forms[0].keyParent.value == "")
			document.forms[0].keyParent.value = "TORN.CODGAR=T:" + getValue("GARE_CODGAR1");
		
<c:choose>
	<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.AssociaGaraAppalto") && esisteIntegrazioneLavori eq "TRUE"}' >
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/trovaAppalto/associaAppalto.jsp&modo=NUOVO&tipoAppalto="  + getValue("TORN_TIPGEN") + "&chiavePadre='"+getValue("keyParent")+"'";
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
	</c:when>
	<c:otherwise>
		document.forms[0].action += "&tipoAppalto=" + getValue("TORN_TIPGEN");
		schedaNuovo();
	</c:otherwise>
</c:choose>
	}
	
	activeForm.calcola("IMPMIS_RIB");
	activeForm.calcola("IMPCOR_RIB");
	activeForm.calcola("IMPAPP_RIB");
	
	function setTipoCategorie(tipoCategoria){
		if(tipoCategoria == "") tipoCategoria = "1";
		setTipoCategoriaPrevalentePerArchivio(tipoCategoria);
		setTipoCategoriaUlteriorePerArchivio(tipoCategoria);
		if(tipoCategoria == "1"){
			showObj("CATG_CATIGA_OBBL", true);
			showObj("CATG_CATIGA_NO_OBBL", false);
		} else {
			showObj("CATG_CATIGA_OBBL", false);
			showObj("CATG_CATIGA_NO_OBBL", true);
		}
	}
	
	// occorre eseguire una chiamata esplicita in questo modo e non mediante fnJavascriptScheda con esegui=true
	// in quanto maxIdUlterioreCategoriaVisualizzabile non è ancora definito
	setTipoCategorie(getValue("TORN_TIPGEN"));
	
	// Funzione per cambiare la condizione di where nell'apertura
	// dell'archivio delle categorie dell'appalto per la categoria prevalente
	function setTipoCategoriaPrevalentePerArchivio(tipoCategoria){
		if(document.forms[0].modo.value != "VISUALIZZA"){
			if(getValue("CATG_CATIGA") == "" || getValue("CAIS_TIPLAVG") == ""){
				document.formCategoriaPrevalenteGare.archWhereLista.value = "V_CAIS_TIT.TIPLAVG=" + tipoCategoria;
				setValue("CAIS_TIPLAVG", "" + tipoCategoria);
			} else {
				document.formCategoriaPrevalenteGare.archWhereLista.value = "V_CAIS_TIT.TIPLAVG=" + getValue("CAIS_TIPLAVG");
			}
		}
	}
	
	function setTipoCategoriaUlteriorePerArchivio(tipoCategoria){
		if(document.forms[0].modo.value != "VISUALIZZA"){
			for(var i=1; i <= maxIdUlterioreCategoriaVisualizzabile; i++){
				if(getValue("OPES_CATOFF_" + i) == ""){
					eval("document.formUlterioreCategoriaGare" + i + ".archWhereLista").value = "V_CAIS_TIT.TIPLAVG=" + tipoCategoria;
					//setValue("CAIS_TIPLAVG_" + i, "" + tipoCategoria);
				} else {
					eval("document.formUlterioreCategoriaGare" + i + ".archWhereLista").value = "V_CAIS_TIT.TIPLAVG=" + getValue("CAIS_TIPLAVG_" + i);
				}
			}
		}
	}
	
	// Funzione utilizzata solo per le gare a lotto unico, oppure gare a lotti non di tipo "Lavori"
	function visualizzaESettaCorrettivo(modAggiudicazione, tipoAppalto, correttivo, campoCorrettivo, garaLottoUnico) {
		var correttivoLavori = '${correttivoLavori}';
		var correttivoFornitureServizi = '${correttivoFornitureServizi}';
		var visualizza = false;
		
		// il commento della funzione si riferisce a questo if 
		if (garaLottoUnico || tipoAppalto != 1) {

			// il campo va visualizzato solo con modAggiudicazione=1 o 5
			if (modAggiudicazione == '1' || modAggiudicazione == '5') visualizza = true;
	
			// nel caso di spegnimento va resettato il suo valore, altrimenti se 
			// va visualizzato ed il dato è vuoto, va inizializzato con il valore di default
			if (!visualizza) setValue(campoCorrettivo, "");
			else {
				if (correttivo == "") {
					if (tipoAppalto == '1')
						setValue(campoCorrettivo, correttivoLavori);
					else
						setValue(campoCorrettivo, correttivoFornitureServizi);
				}
			} 
		}
		showObj("row"+campoCorrettivo, visualizza);
	}
	
	
	<c:if test='${esisteIntegrazioneLavori eq "TRUE"}'>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.AssociaGaraAppalto")}' >
			var livelloProgettazioneSettato = false;
			var whereArchivioAppalti = document.formArchivioAppalti.archWhereLista.value;
		</c:if>
	</c:if>
	
	<c:if test='${modo ne "VISUALIZZA"}'>
		
		//In base al valore di tipgen viene gestita la visualizzazione
		//dei campi fittizzi OGGCONT_LAVORI e OGGCONT_FORNITURE
		function showOGGCONT(tipgen, sbianca) {
			showObj("rowGARE_OGGCONT", false);
			if (tipgen == "3") {
				showObj("rowV_GARE_TORN_OGGCONT_LAVORI", false);
				showObj("rowV_GARE_TORN_OGGCONT_FORNITURE", false);
				if (sbianca == true) {
					setValue("V_GARE_TORN_OGGCONT_LAVORI", "" );
					setValue("V_GARE_TORN_OGGCONT_FORNITURE", "" );
					setValue("GARE_OGGCONT", "" );
				}
			} else if(tipgen == "2") {
				showObj("rowV_GARE_TORN_OGGCONT_LAVORI", false);
				showObj("rowV_GARE_TORN_OGGCONT_FORNITURE", true);
				if (sbianca == true) {
					setValue("V_GARE_TORN_OGGCONT_LAVORI", "" );
					setValue("V_GARE_TORN_OGGCONT_FORNITURE", "" );
					setValue("GARE_OGGCONT", "" );
				}
			} else {
				showObj("rowV_GARE_TORN_OGGCONT_LAVORI", true);
				showObj("rowV_GARE_TORN_OGGCONT_FORNITURE", false);
				if (sbianca == true) {
					setValue("V_GARE_TORN_OGGCONT_LAVORI", "" );
					setValue("V_GARE_TORN_OGGCONT_FORNITURE", "" );
					setValue("GARE_OGGCONT", "" );
				}
			}
		}
		
		//La funzione filtra i valori del tabellato A1031,
		//lasciando quelli con tab1tip <10
		function TABA1031perLavori() {
			if (document.getElementById("V_GARE_TORN_OGGCONT_LAVORI") && (document.getElementById("rowGARE_OGGCONT"))) {
				var num_option=eval(document.getElementById('V_GARE_TORN_OGGCONT_LAVORI').options.length);
				for (a=num_option - 1; a>=0; a--) {
					var value = eval(document.getElementById('V_GARE_TORN_OGGCONT_LAVORI').options[a].value);
					if (value != null && parseInt(value) >=10) {
						document.getElementById('V_GARE_TORN_OGGCONT_LAVORI').options[a]=null;
					}
				}
			}
		}
		
		//La funzione filtra i valori del tabellato A1031,
		//lasciando quelli con tab1tip >=10
		function TABA1031perForniture() {
			if (document.getElementById("V_GARE_TORN_OGGCONT_FORNITURE")&& (document.getElementById("rowGARE_OGGCONT"))) {
				var num_option=eval(document.getElementById('V_GARE_TORN_OGGCONT_FORNITURE').options.length);
				for (a=num_option -1 ;a>=0;a--) {
					var value = eval(document.getElementById('V_GARE_TORN_OGGCONT_FORNITURE').options[a].value);
					if (value != null && parseInt(value) < 10) {
						document.getElementById('V_GARE_TORN_OGGCONT_FORNITURE').options[a]=null;
					}
				}
			}	
		}
		
		<c:if test='${gene:checkProt(pageContext, "COLS.MOD.GARE.GARE.OGGCONT") && gene:checkProt(pageContext, "SEZ.MOD.GARE.GARE-scheda.DATIGEN.ALT")}'>
			TABA1031perLavori();
			TABA1031perForniture();
		</c:if>
		
	showOGGCONT(getValue("TORN_TIPGEN"),false);
		
	function aggiornaOGGCONT(valore){
		setValue("GARE_OGGCONT", valore );
	}
	
	$("#GARE_CODCIG").css({'text-transform': 'uppercase' });
	$("#TORN_CODCIGAQ").css({'text-transform': 'uppercase' });
	
	$(function() {
	    $('#GARE_CODCIG').change(function() {
				if (!controllaCIG("GARE_CODCIG")) {
					alert("Codice CIG non valido")
					this.focus();
				}
	    });
	});

	</c:if>

	function initEsenteCIG_CODCIG() {
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("GARE_CODCIG");
		//alert("esente CIG = " + esenteCig);
		//alert("Codice CIG = " + codcig);
<c:choose>
	<c:when test='${modo ne "VISUALIZZA"}'>
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG", "1", false);
				setOriginalValue("ESENTE_CIG", "1", false);
				showObj("rowCODCIG_FIT", true);
				showObj("rowGARE_CODCIG", false);
				setValue("GARE_CODCIG", "", false);
				//setOriginalValue("GARE_CODCIG", "", false);
			} else {
				setValue("ESENTE_CIG", "2", false);
				setOriginalValue("ESENTE_CIG", "2", false);
				showObj("rowCODCIG_FIT", false);
				showObj("rowGARE_CODCIG", true);
			}
		} else {
			setValue("ESENTE_CIG", "2", false);
			setOriginalValue("ESENTE_CIG", "2", false);
			showObj("rowCODCIG_FIT", false);
			showObj("rowGARE_CODCIG", true);
		}
	</c:when>
	<c:otherwise>
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG", "Si", false);
				showObj("rowCODCIG_FIT", true);
				showObj("rowGARE_CODCIG", false);
			} else {
				setValue("ESENTE_CIG", "No", false);
				showObj("rowCODCIG_FIT", false);
				showObj("rowGARE_CODCIG", true);
			}
		} else {
			setValue("ESENTE_CIG", "No", false);
			showObj("rowCODCIG_FIT", false);
			showObj("rowGARE_CODCIG", true);
		}
	</c:otherwise>
</c:choose>
	}

	function gestioneEsenteCIG() {
	<c:if test='${modo ne "VISUALIZZA"}'>
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("GARE_CODCIG");
		if ("1" == esenteCig) {
			showObj("rowGARE_CODCIG", false);
			//setValue("GARE_CODCIG", "", false);
			if (getOriginalValue("CODCIG_FIT") == getValue("CODCIG_FIT")) {
				setValue("CODCIG_FIT", "", false);
			} else {
				setValue("CODCIG_FIT", getOriginalValue("CODCIG_FIT"), false);
			}
			<c:if test='${gene:checkProt(pageContext, "COLS.MAN.GARE.GARE.CODCIG")}'>
			if(getValue("CODCIG_FIT")==null || getValue("CODCIG_FIT")=="" )
				setValue("CODCIG_FIT", " ", false);
			</c:if>
			showObj("rowCODCIG_FIT", true);
		} else {
			showObj("rowGARE_CODCIG", true);
			showObj("rowCODCIG_FIT", false);
			setValue("CODCIG_FIT", "", false);
		}
	</c:if>
	}

	function gestioneCriterioAggiudicazione(critlicg,detlicg) {
		if (critlicg == 1 || critlicg == 3) {
			showObj("rowGARE_DETLICG", true);
		} else {
			showObj("rowGARE_DETLICG", false);
		}
		
		if (critlicg == 1 && detlicg == 3) {
			showObj("rowGARE_RIBCAL", true);
		} else {
			showObj("rowGARE_RIBCAL", false);
		}
		
		if(critlicg == 3){
			showObj("rowGARE_CALCSOANG", false);
			showObj("rowTORN_OFFAUM", false);
		}else{
			showObj("rowGARE_CALCSOANG", true);
			showObj("rowTORN_OFFAUM", true);
		}
	}
	
	function gestioneCRITLICG(critlicg) {
		var calcsoan = getValue("GARE_CALCSOANG");
		document.forms[0].GARE_DETLICG.value='';
		document.forms[0].GARE_APPLEGREGG.value='';
		
		if (critlicg == 2  || critlicg == 3 || calcsoan == '2') {
			document.forms[0].GARE_MODASTG.value='2';
			showObj("rowGARE_MODASTG", false);
		} else {
			document.forms[0].GARE_MODASTG.value='';
			showObj("rowGARE_MODASTG", true);
		}
	}
	
	function gestioneCOSTOFISSO_SEZIONITEC(critlicg) {
		var gartel = getValue("TORN_GARTEL");
		if(gartel && gartel == 1 && critlicg == 2){
			showObj("rowGARE1_COSTOFISSO", true);
			showObj("rowGARE1_SEZIONITEC", true);
		}else{
			document.forms[0].GARE1_SEZIONITEC.value='';
			showObj("rowGARE1_COSTOFISSO", false);
			showObj("rowGARE1_SEZIONITEC", false);
		}
	}
	
	<c:if test="${not isVecchiaOepv}">	
	function gestioneOFFAUM(critlicg) {
		var offtel = getValue("TORN_OFFTEL");
		var modlicg = getValue("GARE_MODLICG");
		if(offtel && offtel == 1 && modlicg == 6){
			showObj("rowTORN_OFFAUM", false);
			setValue("TORN_OFFAUM",2);
		} else if(critlicg != 3){
			showObj("rowTORN_OFFAUM", true);
		}
	}
	
	function gestionePRERIB() {
		var offtel = getValue("TORN_OFFTEL");
		var modlicg = getValue("GARE_MODLICG");
		if(offtel && offtel == 1 && modlicg == 6){
			showObj("rowTORN_PRERIB", false);
			
		} else {
			showObj("rowTORN_PRERIB", true);
		}
	}
	</c:if>
	
	function gestioneDETLICG(detlicg) {
		var critlicg = getValue("GARE_CRITLICG");
		var tipgen = getValue("TORN_TIPGEN");
		
		document.forms[0].GARE_APPLEGREGG.value='';
		//Solo se offerta prezzi unitari, imposta RIBCAL in base al tipo di gara (lavori, forniture, servizi)
		var ribcal=2;
		if ((critlicg == 1 && detlicg == 3 && tipgen != 1) || detlicg == 4) {
			setValue("GARE_RIBCAL",2);
		} else {
			setValue("GARE_RIBCAL",1);
			ribcal=1;
		} 
		
		
		<c:if test="${(isProceduraTelematica or param.proceduraTelematica eq 1) and modo ne 'VISUALIZZA'}">
			/*
			if (critlicg == 1) {
				var ricastae = getValue("TORN_RICASTAE"); 
				if (ricastae=='1') {
					visualizzazioneCampiScartoAstaEl(ribcal,true);
				} 
			}
			*/
		</c:if>
	}


	function gestioneCALCSOANG(calcsoang,impostaValori) {
		
		var critlicg = getValue("GARE_CRITLICG");
		if (impostaValori == 'true'){
			//document.forms[0].GARE_MODASTG.value='';
			if (critlicg == 2) {
				document.forms[0].GARE_MODASTG.value='2';
			} else {
				document.forms[0].GARE_MODASTG.value='';
			}
			
			document.forms[0].GARE_APPLEGREGG.value='';
		}	
		if (calcsoang == 2) {
			if (impostaValori == 'true')
				setValue("GARE_MODASTG","2");
			showObj("rowGARE_MODASTG", false);
		} else {
			if (critlicg == 2) {
				showObj("rowGARE_MODASTG", false);
			} else {
				showObj("rowGARE_MODASTG", true);
			}
		}
	}
	
	function gestioneFlagSicurezzaInclusa(critlicg,detlicg) {
		if (detlicg == 3 || critlicg == 2 || detlicg ==4) {
			showObj("rowGARE_SICINC", true);
		} else {
			showObj("rowGARE_SICINC", false);
			setValue("GARE_SICINC",1);
		}
	}

	function calcolaMODLICG(critlicg,detlicg,calcsoang,applegregg) {
		var ret = "";
		if (critlicg!="") {
			if (critlicg == 1 && detlicg !="" && calcsoang !="") {
				if (detlicg == 1 && calcsoang == '1') ret = 13;
				if (detlicg == 1 && calcsoang == '2') ret = 1;
				if (detlicg == 2 && calcsoang == '1') ret = 13;
				if (detlicg == 2 && calcsoang == '2') ret = 1;
				if (detlicg == 3 && calcsoang == '1') ret = 14;
				if (detlicg == 3 && calcsoang == '2') ret = 5;
				if (detlicg == 4 && calcsoang == '1') ret = 13;
				if (detlicg == 5 && calcsoang == '1') ret = 13;
				if (detlicg == 4 && calcsoang == '2') ret = 1;
				if (detlicg == 5 && calcsoang == '2') ret = 1;
				//Regione Sicilia
				if (calcsoang == '1') {
					if (detlicg == 1 && applegregg == '1') ret = 15;
					if (detlicg == 2 && applegregg == '1') ret = 15;
					if (detlicg == 3 && applegregg == '2') ret = 16;
				}
			}
			if (critlicg == 2) {
				ret = 6;
			}else if(critlicg == 3){
				ret = 17;
			}
		}
		
		return ret;
	}
	<c:if test='${garaLottoUnico}'>
		function visualizzaTIPFORN(tipgen){
			if (tipgen == "2") {
				showObj("rowTORN_TIPFORN", true);
			} else {
				showObj("rowTORN_TIPFORN", false);
				setValue("TORN_TIPFORN", "");
			}
		}
	</c:if>	
	
	function leggiComunicazioni() {
		var href = contextPath + "/ApriPagina.do?href=geneweb/w_invcom/w_invcom-in-lista.jsp&genere=${genere}&chiave=" + document.forms[0].key.value;
		<c:choose>
			<c:when test='${garaLottoUnico}'>
				var entitaWSDM="GARE";
				var chiaveWSDM=getValue("GARE_NGARA");
			</c:when>
			<c:otherwise>
				var entitaWSDM="TORN";
				var chiaveWSDM=getValue("GARE_CODGAR1");
			</c:otherwise>
		</c:choose>
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM + "&" + csrfToken;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}

	function inviaComunicazioni() {
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-lista.jsp&genere=${genere}&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		<c:choose>
			<c:when test='${garaLottoUnico}'>
				var entitaWSDM="GARE";
				var chiaveWSDM=getValue("GARE_NGARA");
			</c:when>
			<c:otherwise>
				var entitaWSDM="TORN";
				var chiaveWSDM=getValue("GARE_CODGAR1");
			</c:otherwise>
		</c:choose>
		href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href;
	}

	function esportaAntimafiaVerifica() {
		var href="href=gare/gare/gare-popup-exportAntimafia.jsp&codiceGara=${datiRiga.GARE_NGARA}&tipoRichiesta=VERIFICA&islottoGara=${lottoDiGara}";
		openPopUpCustom(href, "exportXMLantimafia", 550, 300, "yes", "yes");
	}
	
	var calcsoang = "${datiRiga.GARE_CALCSOANG}"
	gestioneCALCSOANG(calcsoang,"false");
	
		
	function valorizzaStatoGara() {
		//var tipgarg = getValue("GARE_TIPGARG");
		var iterga = getValue("TORN_ITERGA");
		var esineg = getValue("GARE_ESINEG");
		var ditta = getValue("GARE_DITTA");
		var dataOdierna = new Date();
		var dataPartecipazione;
		var oraPartecipazione;
		if (iterga==2 || iterga==4) {
			dataPartecipazione = getValue("TORN_DTEPAR");
			oraPartecipazione = getValue("TORN_OTEPAR");
		} else { 
			dataPartecipazione = getValue("TORN_DTEOFF");
			oraPartecipazione = getValue("TORN_OTEOFF");
		}
		
		if(oraPartecipazione=="")
			oraPartecipazione ="23:59:59";
			
		if (ditta!="" || esineg!="") {
			setValue("STATOGARA","Conclusa");
		} else if(dataPartecipazione!="") {
			var dataTmp = dataPartecipazione.split("/");
			dataPartecipazione = dataTmp[1]+"/"+dataTmp[0]+"/"+dataTmp[2];
			dataPartecipazione = new Date(dataPartecipazione + " " + oraPartecipazione);
			
			var dattaOggi = dataOdierna.getTime();
			var dataPArt = dataPartecipazione.getTime();
			if (dataPArt>=dattaOggi && ditta=="" && esineg=="") {
				setValue("STATOGARA","In corso");
			} else if(dataPArt < dattaOggi && ditta=="" && esineg=="") {
				setValue("STATOGARA","In aggiudicazione");
			}
				
		} else if(dataPartecipazione=="" && ditta=="" && esineg=="" ) {
			setValue("STATOGARA","");
		}
	}
	
	function impostaGaraNonAggiudicata(ngara,codgar1,esineg,datneg,npannrevagg) {
		var href="href=gare/commons/popup-ImpostaGaraNonAggiudicata.jsp&ngara=" + ngara + "&codgar1=" + codgar1 + "&esineg=" + esineg + "&datneg=" + datneg + "&npannrevagg=" + npannrevagg;
		href+="&isOffertaUnica=No";
		<c:if test='${!garaLottoUnico}'>
			href+="&isLottoOffDistinte=Si";
		</c:if>
		openPopUpCustom(href, "impostaGaraNonAggiudicata", 700, 400, "yes", "yes");
	}
	
	<c:if test='${modo ne "VISUALIZZA"}'>
	//Si redefinisce la funzione archivioLista per sbiancare il valore di archValueChiave
	function archivioListaCustom(nomeArchivio) {
		//document.formCategoriaElenco.archValueChiave.value = "";
		if (nomeArchivio.indexOf("formUlterioreCategoriaGare") >= 0 || nomeArchivio == "formCategoriaPrevalenteGare")
			eval("document." + nomeArchivio + ".archValueChiave").value = "";
		if (nomeArchivio == "formUFFINTGare") {
      if (!checkPuntiContatto()) {
        alert("Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto nella gara");
        return;
      }
    }
		archivioListaDefault(nomeArchivio);
	 	
	 }

	var archivioListaDefault = archivioLista;
	var archivioLista = archivioListaCustom;
	
	//Gestione onchange campi archivio categoria prevalente
	if(document.getElementById("CATG_CATIGA")!= null)
		document.getElementById("CATG_CATIGA").onchange = modificaCampoArchivio;
	if(document.getElementById("CAIS_DESCAT")!= null)
		document.getElementById("CAIS_DESCAT").onchange = modificaCampoArchivio;
	
	//Gestione onchange campi archivio ulteriori categorie
	for(var i=1; i <= maxIdUlterioreCategoriaVisualizzabile; i++) {
		if(document.getElementById("OPES_CATOFF_" + i)!= null)
			document.getElementById("OPES_CATOFF_" + i).onchange = modificaCampoArchivio;
		if(document.getElementById("CAIS_DESCAT_" + i)!= null)
			document.getElementById("CAIS_DESCAT_" + i).onchange = modificaCampoArchivio;
	}
	
	//Viene ridefinito l'onchange dei campi collegati all'archivio di modo che il valore inserito
	//nel campo viene impostato nel campo "archValueChiave", mentre viene sbiancato il campo archCampoChanged
	//in modo che nella popup dell'archivio delle categorie posso impostare manualmente la where senza che 
	//venga inserita in automatico la condizione sul campo valorizzato. 
	function modificaCampoArchivio() {
		var campo =this.id;
		var valore = this.value;
						
		//Valorizzazione della variabile globale activeArchivioForm			
		for(var s=0; s < document.forms.length; s++) {
			try {
				if(eval("document." + document.forms[s].name + ".archCampi.value.indexOf(new String('" + campo + "')) >= 0")){
					activeArchivioForm = document.forms[s].name;
					break;
				}
			} catch(err) {
				// Oggetto inesistente nell's-esimo form
			}
		}
		
		if(valore!= null && valore !="")
			valore = valore.toUpperCase();
		else {
			getArchivio(activeArchivioForm).sbiancaCampi(0);
			return;
		}
		
		eval("document." + activeArchivioForm +".archValueChiave").value = valore;
		
		this.value="";
		//Apertura dell'archivio 
		eval("document." + activeArchivioForm +".metodo").value = "lista";
		getArchivio(activeArchivioForm).submit(true);
	}
	
		arrayTabellatoA1z04= new Array();
        <c:forEach items="${listaValoriA1z04}" var="punteggio" varStatus="indice" >
        	arrayTabellatoA1z04[${indice.index}] = ${punteggio};
        </c:forEach>
		
		arrayTabellatoA1z05= new Array();
        <c:forEach items="${listaValoriA1z05}" var="valTabellato" varStatus="indice" >
        	arrayTabellatoA1z05[${indice.index}] = ${valTabellato};
        </c:forEach>
        
		function calcolaITERGA(tipogara) {
			var valoreTrovato = false;
			var ret = "";
      		for (i=0; i < arrayTabellatoA1z04.length; i++) {
				var valori = arrayTabellatoA1z04[i];
				var valoreTipogara= valori[1];
				if (tipogara == valoreTipogara) {
					ret = valori[0];
					valoreTrovato = true;
					break;
				}
			}
			if(valoreTrovato==false)
				ret="2";
			return ret;
		}
		
		function IniziazilizzazioneISADESIONE(tipogara){
			var ret = "2";
			var garaLottoUnico = "${garaLottoUnico}";
			if (garaLottoUnico == "true") {
				for (i=0; i < arrayTabellatoA1z05.length;i++) {
					var valori = arrayTabellatoA1z05[i];
					var valoreTipogara = valori[1];
					if (tipogara == valoreTipogara) {
						if (valori[0]== 76 || valori[0] ==77) {
							aggiornaFiltroArchivioAccordiQuadro(tipogara, false,valori[0]);
							ret = 1;
							break;
						}
					}
				}
			}
			setValue("TORN_ISADESIONE",ret);
			if (ret=="1") {
				setValue("TORN_ACCQUA","2");
				showObj("rowTORN_ACCQUA",false);
			} else {
				showObj("rowTORN_ACCQUA",true);
			}
		}
		
		
	</c:if>
	
	function visualizzaDaISADESIONE(valore) {
		var visualizza = true;
		if (valore != 1)
			visualizza = false;
		
		showObj("rowTORN_CODCIGAQ",visualizza);
		showObj("rowTORN_NGARAAQ",visualizza);
		showObj("rowTORN_ACCQUA",!visualizza);
		if (!visualizza) {
			setValue("TORN_CODCIGAQ","");
			setValue("TORN_NGARAAQ","");
		}
	}
	
	<c:if test='${modo eq "VISUALIZZA" and fn:contains(listaOpzioniDisponibili, "OP128#")}'>
	function apriScadenzario() {
	<c:choose>
		<c:when test="${garaLottoUnico}">
		var entita = "TORN";
		var chiave = "TORN.CODGAR=T:$" + "${gene:getValCampo(key, 'NGARA')}";
		</c:when>
		<c:otherwise>
		var entita = "GARE";
		var chiave = document.forms[0].key.value;
		</c:otherwise>
	</c:choose>
		scadenzario(entita, chiave, ${sessionScope.entitaPrincipaleModificabile}, "BANDO");
	}
	</c:if>
	
	function popupInviaDatiCig(codgar,numeroLotto,genere) {
	 	var comando = "href=gare/commons/popup-invia-dati-richiesta-cig.jsp";
	 	comando = comando + "&codgar=" + codgar+ "&numeroLotto=" + numeroLotto+ "&genere=" + genere;
	 	<c:if test="${garaLottoUnico}">
	 		var importoSottoSoglia=false;
	 		var importo = getValue("V_GARE_IMPORTI_VALMAX");
	 		if(importo== null || importo == "")
	 			importo=0;
	 		importo = parseFloat(importo);
	 		if(importo < 40000)
	 			importoSottoSoglia=true;
	 		comando+="&importoSottoSoglia=" + importoSottoSoglia;
	 		var uuidTorn= getValue("TORN_UUID");
	 		var uuidGare1= getValue("GARE1_UUID");
	 		comando+="&uuidTorn=" + uuidTorn + "&uuidGare1="+uuidGare1;
	 	</c:if>
	 	openPopUpCustom(comando, "inviadaticig", 550, 650, "yes", "yes");
 	}
 	

 	var schedaConfermaDefault = schedaConferma;

	function schedaConfermaCustom() {
		clearMsg();
		setValue("GARE_CODCIG", getValue("GARE_CODCIG").toUpperCase(), false);
		
		<c:if test='${modo eq "NUOVO"}'>
			setValue("GARE_NGARA", getValue("GARE_NGARA").trim(), false);
		</c:if>
		
		var esenteCig = getValue("ESENTE_CIG");
		if (esenteCig == "2" && getValue("GARE_CODCIG") != "") {
			if (!controllaCIG("GARE_CODCIG")) {
				outMsg("Codice CIG non valido", "ERR");
				onOffMsg();
				return;
			}
		} else {
			setValue("GARE_CODCIG", getValue("CODCIG_FIT").toUpperCase());
		}
		
		if (isObjShow("rowTORN_CODCIGAQ")) {
			var codiceCigAQ = getValue("TORN_CODCIGAQ");
			if (codiceCigAQ != "") {
				setValue("TORN_CODCIGAQ", codiceCigAQ.toUpperCase());
				if (!controllaCIG("TORN_CODCIGAQ")) {
					outMsg("Codice CIG accordo quadro non valido", "ERR");
					onOffMsg();
					return;
				}
			}
		}
	
		var tipoAppalto=getValue("TORN_TIPGEN");
		var tipoCategoria = getValue("CAIS_TIPLAVG");
		if (tipoCategoria == null || tipoCategoria == "")
			tipoCategoria=tipoAppalto;
		
		var catiga = getValue("CATG_CATIGA");
		if (catiga!=null && catiga!="") {
			var valoreClassifica = "";
			var classificaDisabilitata;
			var classificaVisualizzata;
			if (tipoCategoria==1) {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_LAVORI");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_LAVORI").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_LAVORI");
			} else if(tipoCategoria=="2") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_FORNITURE");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_FORNITURE").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_FORNITURE");	
			} else if(tipoCategoria=="3") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_SERVIZI");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_SERVIZI").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_SERVIZI");	
			} else if(tipoCategoria=="4") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_LAVORI150");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_LAVORI150").disabled;
				 classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_LAVORI150");	
			} else if(tipoCategoria=="5") {
				valoreClassifica = getValue("CATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI");
				classificaDisabilitata = document.getElementById("CATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI").disabled;
				classificaVisualizzata = isObjShow("rowCATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI");	
			}
			
			if (!classificaDisabilitata && (valoreClassifica==null || valoreClassifica=="") && classificaVisualizzata) {
				clearMsg();
				var msg="Il campo 'Classifica' della"; 
				if (tipoAppalto == 1)
					msg+=" Categoria prevalente";
				else
					msg+=" Prestazione principale";
				msg+=" e' obbligatorio"; 
				
				outMsg(msg, "ERR");
				onOffMsg();
				return;
			}
		}
		
		//Controllo classifiche delle ulteriori categorie
		if (controlloClassificaSezioniDinamiche(tipoAppalto) != "true") {
			return;
		}
		<c:if test="${isProceduraTelematica or param.proceduraTelematica eq 1}">
			var ricastae = getValue("TORN_RICASTAE");
			if (ricastae == 1) {
				var aeribmin = getValue("TORN_AERIBMIN");
				var aeribmax = getValue("TORN_AERIBMAX");
				if (aeribmin!=null && aeribmin!="" && aeribmax != null && aeribmax !="") {
					aeribmin = parseFloat(aeribmin);
					aeribmax = parseFloat(aeribmax);
					if (aeribmin >= aeribmax) {
						outMsg("Lo scarto minimo del ribasso di rilancio per l'asta elettronica deve essere inferiore allo scarto massimo.", "ERR");
						onOffMsg();
						return;
					}
				}
				var aeimpmin = getValue("TORN_AEIMPMIN");
				var aeimpmax = getValue("TORN_AEIMPMAX");
				if (aeimpmin!=null && aeimpmin!="" && aeimpmax != null && aeimpmax !="") {
					aeimpmin = parseFloat(aeimpmin);
					aeimpmax = parseFloat(aeimpmax);
					if (aeimpmin >= aeimpmax) {
						outMsg("Lo scarto minimo dell'importo di rilancio per l'asta elettronica deve essere inferiore allo scarto massimo.", "ERR");
						onOffMsg();
						return;
					}
				}
				var offaum = getValue("TORN_OFFAUM");
				if (offaum==1) {
					outMsg("Non è possibile ammettere offerte in aumento nel caso di ricorso all'asta elettronica.", "ERR");
					onOffMsg();
					return;
				}
				
				var ribcal = getValue("GARE_RIBCAL");
				if (ribcal==3) {
					outMsg("Non è disponibile il ricorso all'asta elettronica nel caso di offerta prezzi espressa mediante somma sconti pesati.", "ERR");
					onOffMsg();
					return;
				}
			}
		</c:if>
		schedaConfermaDefault();
	}
	
	var schedaConferma = schedaConfermaCustom;

	function controlloClassificaSezioniDinamiche(tipoAppalto) {
		var valoreClassifica="";
		var classificaDisabilitata;
		var classificaVisualizzata;
		for (var i=1; i <= idUltimaUlterioreCategoriaVisualizzata; i++) {
			var tipoCategoria = getValue("CAIS_TIPLAVG_" + i);
			var catoff = getValue("OPES_CATOFF_" + i);
			if (catoff!=null && catoff!="") {
				if (tipoCategoria==1) {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_LAVORI_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_LAVORI_" + i).disabled;
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_LAVORI_" + i);
				} else if(tipoCategoria=="2") {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_FORNITURE_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_FORNITURE_" + i).disabled;
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_FORNITURE_" + i);	
				} else if(tipoCategoria=="3") {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_SERVIZI_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_SERVIZI_" + i).disabled;
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_SERVIZI_" + i);	
				} else if(tipoCategoria=="4"){
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_LAVORI150_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_LAVORI150_" + i).disabled;	
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_LAVORI150_" + i);
				} else if(tipoCategoria=="5") {
					valoreClassifica = getValue("OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_" + i);
					classificaDisabilitata = document.getElementById("OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_" + i).disabled;	
					classificaVisualizzata = isObjShow("rowOPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_" + i);
				}
				if (!classificaDisabilitata && (valoreClassifica==null || valoreClassifica=="") && classificaVisualizzata) {
					clearMsg();
					var msg="Il campo 'Classifica' delle"; 
					if(tipoAppalto==1)
						msg+=" Categorie ulteriori";
					else
						msg+=" Prestazioni secondarie";
					msg+=" e' obbligatorio"; 
					
					outMsg(msg, "ERR");
					onOffMsg();
					return "false";
				}
			}
		}
		return "true";	
			
	}
	<c:if test='${modo eq "VISUALIZZA" && !empty datiRiga.GARE_NUMERA && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.VisualizzaDocAppaltoDaGare") 
			&& gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NUMERA") && controlloPahDocAssociatiPl}'>
		function visualizzaDocumentiAssociatiAppalto() {
			var tipgen = getValue("TORN_TIPGEN");
			var clavor = getValue("GARE_CLAVOR");
			var numera = getValue("GARE_NUMERA");
			
			var comando = "href=gare/gare/gare-popup-visualizzaDocAppalto.jsp";
		 	comando = comando + "&tipgen=" + tipgen+ "&clavor=" + clavor+ "&numera=" + numera;
		 	openPopUpCustom(comando, "visualizzaDocAppalto", 700, 450, "yes", "yes");
		}
		
		
	</c:if>
	
	function checkPuntiContatto() {
		var puntoContatto = getValue("TORN_PCOPRE");
		if (puntoContatto!= null && puntoContatto!="") {
			return false;
		}
		puntoContatto = getValue("TORN_PCODOC");
		if (puntoContatto!= null && puntoContatto!="") {
			return false;
		}
		puntoContatto = getValue("TORN_PCOOFF");
		if (puntoContatto!= null && puntoContatto!="") {
			return false;
		}
		puntoContatto = getValue("TORN_PCOGAR");
		if (puntoContatto!= null && puntoContatto!="") {
			return false;
		}
		return true;
	} 
	
	function apriGestionePermessiGaraTelematica(codgar, genereGara) {
		bloccaRichiesteServer();
<c:choose>
	<c:when test="${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'}" >
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'true';
	</c:when>
	<c:otherwise>
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'false';
	</c:otherwise>
</c:choose>
		formVisualizzaPermessiUtentiStandard.action = "${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiGaraTelematica.do";
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara; 
		formVisualizzaPermessiUtentiStandard.submit();
	}

	function apriGestionePermessiGaraTelematicaStandard(codgar, genereGara) {
		bloccaRichiesteServer();
<c:choose>
	<c:when test="${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'}" >
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'true';
	</c:when>
	<c:otherwise>
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'false';
	</c:otherwise>
</c:choose>
		formVisualizzaPermessiUtentiStandard.action = "${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiGaraTelematicaStandard.do";
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtentiStandard.submit();
	}
	
	function visualizzaSezioneTerminiConsegna() {
		var tipgen = getValue("TORN_TIPGEN");
		var accqua = getValue("TORN_ACCQUA");
		var visibile=true;
		if (tipgen==1 || accqua=="1") {
			visibile=false;
		}
		showObj("rowCONSEGNA",visibile);
		showObj("rowGARE1_DTERMCON", visibile);
		showObj("rowGARE1_NGIOCON", visibile);
	}
	visualizzaSezioneTerminiConsegna();
	
	function visualizzaMotacc(prourg) {
		var visibile= false;
		if (prourg == 1) {
			visibile=true;
		} else {
			setValue("TORN_MOTACC","");
		}	
		showObj("rowTORN_MOTACC",visibile);	
	}
	
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>
	
	function listaIds() {
		var comando = "href=gare/v_lista_ids/popup-lista-ids.jsp&codiceGara=" + "${ngaraPerGaratt}" + "&genereGara=" + "2";
	 	openPopUpCustom(comando, "visualizzaListaIds", 900, 450, "yes", "yes");
	}
	
	function popupRettificaTermini(iterga,codgar,gartel,ngara) {
		var comando = "href=gare/commons/popup-rettificaTermini.jsp&codgar=" + codgar +"&ngara=" + ngara + "&iterga=" + iterga +"&pagina=Datigen" + "&tipoGara=2" + "&gartel="+gartel;
	 	openPopUpCustom(comando, "rettificaTermini", 700, 350, "yes", "yes");
	}
	
	function apriExportDocumenti(codgar) {
		var comando = "href=gare/commons/popup-richiesta-export-documenti.jsp?codgar=" + codgar + "&genere=${genere}";
	 	openPopUpCustom(comando, "exportDocumenti", 700, 350, "yes", "yes");
	}
	
	function popupControlla190(codgar){
		var comando = "href=gare/commons/popup-controllo190.jsp?codgar=" + codgar + "&genere=${datiRiga.GARE_GENERE}";
		openPopUpCustom(comando, "Controlla190", 900, 450, "yes", "yes");
	}
	
	function apriArchiviaDocumenti(codgar) {
		bloccaRichiesteServer();
		<c:choose>
		<c:when test="${garaLottoUnico}">
			<c:set var="codice" value="${numeroGara}"/>
		</c:when>
		<c:otherwise>
			<c:set var="codice" value="${codgar}"/>
		</c:otherwise>
		</c:choose>

		formVisualizzaDocumenti.codice.value = getValue("GARE_NGARA");
		formVisualizzaDocumenti.codgar.value = codgar;
		formVisualizzaDocumenti.genere.value = "2";
		formVisualizzaDocumenti.entita.value = "GARE";
		formVisualizzaDocumenti.key1.value = "${codice}";
		formVisualizzaDocumenti.chiaveOriginale.value = getValue("GARE_NGARA");
		formVisualizzaDocumenti.gartel.value = getValue("TORN_GARTEL");
		formVisualizzaDocumenti.submit();
	}

	function nuovaComunicazione() {
		var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
		var tipo = 2;
		var numeroGara = getValue("GARE_NGARA");
		var keyAdd = "W_INVCOM.COMKEY1=T:" + numeroGara;
		var keyParent = "GARE.NGARA=T:" + numeroGara;
		<c:choose>
			<c:when test='${garaLottoUnico}'>
				var entitaWSDM="GARE";
				var chiaveWSDM=getValue("GARE_NGARA");
			</c:when>
			<c:otherwise>
				var entitaWSDM="TORN";
				var chiaveWSDM=getValue("GARE_CODGAR1");
			</c:otherwise>
		</c:choose>
		var href = "";
		if (IsW_CONFCOMPopolata == "true") {
			href = contextPath + "/pg/InitNuovaComunicazione.do?genere=" + tipo + "&keyAdd=" + keyAdd+"&keyParent=" + keyParent + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		} else {
			href = contextPath + "/Lista.do?numModello=0&keyAdd=" + keyAdd ;
			href += "&keyParent=" + keyParent + "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
		}
		if(idconfi){
			href = href + "&idconfi="+idconfi;
		}
		document.location.href = href + "&" + csrfToken;;
	}
			
	function gestioneAQOPER(accqua) {
		if (accqua == '1') {
			showObj("rowGARE1_AQOPER",true);
			showObj("rowDAQ",true);
			showObj("rowTORN_AQDURATA",true);
			showObj("rowTORN_AQTEMPO",true);
			
			showObj("rowTMP",false);
			showObj("rowGARE_DINLAVG",false);
			showObj("rowGARE_TEUTIL",false);
			showObj("rowGARE_TEMESI",false);
			
			showObj("rowCONSEGNA",false);
			showObj("rowGARE1_DTERMCON",false);
			showObj("rowGARE1_NGIOCON",false);
						
			<c:if test='${modo ne "VISUALIZZA"}'>
				if(getValue("GARE1_AQOPER")==null || getValue("GARE1_AQOPER")=="")
					setValue("GARE1_AQOPER","1");
				//$("#TORN_AQOPER").attr("disabled", "disabled");
				var aqtempo = getValue("TORN_AQTEMPO");
				if (aqtempo == "" || aqtempo == null)
					setValue("TORN_AQTEMPO","2");
			</c:if>
		} else {
			showObj("rowGARE1_AQOPER",false);
			showObj("rowDAQ",false);
			showObj("rowTORN_AQDURATA",false);
			showObj("rowTORN_AQTEMPO",false);
			
			showObj("rowTMP",true);
			showObj("rowGARE_DINLAVG",true);
			showObj("rowGARE_TEUTIL",true);
			showObj("rowGARE_TEMESI",true);
			
			var tipgen = getValue("TORN_TIPGEN");
			var visibile=true;
			if (tipgen==2 || tipgen==3) {
				showObj("rowCONSEGNA",true);
				showObj("rowGARE1_DTERMCON",true);
				showObj("rowGARE1_NGIOCON",true);
			}
			
			<c:if test='${modo ne "VISUALIZZA"}'>
				setValue("GARE1_AQOPER","");
				setValue("TORN_AQDURATA","");
				setValue("TORN_AQTEMPO","");
				gestioneAQNUMOPE("");
			</c:if>
		}
	}
	
	/*
	function gestioneAQNUMOPE(aqoper) {
		if(aqoper == '2') {
			showObj("rowGARE1_AQNUMOPE",true);
		} else {
			showObj("rowGARE1_AQNUMOPE",false);
			<c:if test='${modo ne "VISUALIZZA"}'>
				setValue("GARE1_AQNUMOPE","");
			</c:if>
		}
	}
	
	
	
	function gestioneAccordoQuadroLotti(modo,initAQOPER) {
		var accqua;
		<c:choose>
			<c:when test='${modo eq "NUOVO"}'>
			accqua="${requestScope.initACCQUATorn}";
			</c:when>
			<c:otherwise>
			accqua=getValue("TORN_ACCQUA");
			</c:otherwise>
		</c:choose>
		if (accqua == '1') {
			showObj("rowTMP",false);
			showObj("rowGARE_DINLAVG",false);
			showObj("rowGARE_TEUTIL",false);
			showObj("rowGARE_TEMESI",false);
			var initAQOPER = "${requestScope.initAQOPER }";
			if(initAQOPER!="2")
				showObj("rowGARE1_AQNUMOPE",false);
		}else{
			showObj("rowGARE1_AQOPER",false);
			showObj("rowGARE1_AQNUMOPE",false);
		}
	}
	*/
	
	function aggiornaVisualizzazioneDaArchivioUffint(iscuc) {
		if (iscuc==1) {
			showObj("rowTORN_ALTRISOG",true);
		} else {
			showObj("rowTORN_ALTRISOG",false);
		}
	}
	
	function sbiancaArchivioUffintAltriSogg() {
		setValue("TORN_ALTRISOG", "");
		setValue("CENINT_GARALTSOG", "");
		setValue("NOMEIN_GARALTSOG", "");
	}
	
	function gestioneVisualizzazioneArchivio(altrisog) {
		if (altrisog=='2') {
			showObj("rowCENINT_GARALTSOG",true);
			showObj("rowNOMEIN_GARALTSOG",true);
			showObj("rowCODRUP_GARALTSOG",true);
			showObj("rowNOMTEC_GARALTSOG",true);
		} else {
			<c:if test="${!garaLottoUnico}">
				showObj("rowRUP", false);
			</c:if>
			showObj("rowCENINT_GARALTSOG",false);
			showObj("rowNOMEIN_GARALTSOG",false);
			showObj("rowCODRUP_GARALTSOG",false);
			showObj("rowNOMTEC_GARALTSOG",false);
			setValue("CENINT_GARALTSOG", "");
			setValue("NOMEIN_GARALTSOG", "");
			setValue("CODRUP_GARALTSOG", "");
			setValue("NOMTEC_GARALTSOG", "");
		}
	}
	
	function gestioneULTDETLIC(critlic,detlic) {
		if ((critlic == 1 && (detlic == 3 || detlic == 4)) || critlic == 2) {
			showObj("rowGARE1_ULTDETLIC", true);
		} else {
			setValue("GARE1_ULTDETLIC","");
			showObj("rowGARE1_ULTDETLIC", false);
		}
	}
	
	
	
	
	<c:if test="${garaLottoUnico && modo ne 'VISUALIZZA'}">
	function aggiornaFiltroArchivioAccordiQuadro(tipgarg, eseguireRicercaTipgarg,valoreCorrispondenzaTrovato) {
		var tipgen = getValue("TORN_TIPGEN");
		<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />
		
		if(document.formArchivioAccordiQuadro !=null) {
			document.formArchivioAccordiQuadro.archWhereLista.value = "V_GARE_ACCORDIQUADRO.TIPGEN = " + tipgen + " and (V_GARE_ACCORDIQUADRO.ISARCHI <> '1' or V_GARE_ACCORDIQUADRO.ISARCHI is null)";
			
			var cenint = getValue("TORN_CENINT");
			if (cenint!=null && cenint!="") {
				document.formArchivioAccordiQuadro.archWhereLista.value += " and (not exists(select id from garaltsog g where g.ngara=V_GARE_ACCORDIQUADRO.NGARA) or " + 
				" exists (select id from garaltsog g where g.ngara=V_GARE_ACCORDIQUADRO.NGARA and g.cenint='" + cenint + "'))";
			}
		}
		
		
		
		if (eseguireRicercaTipgarg) {
			for (i=0; i< arrayTabellatoA1z05.length;i++) {
				var valori = arrayTabellatoA1z05[i];
				var valoreTipogara= valori[1];
				if (tipgarg == valoreTipogara) {
					if (valori[0] ==77 || valori[0] ==76) {
						valoreCorrispondenzaTrovato = valori[0];
						break;
					}
				}
			}
		}
		
		if (valoreCorrispondenzaTrovato != null && valoreCorrispondenzaTrovato != "") {
			if (document.formArchivioAccordiQuadro !=null ) {
				if (valoreCorrispondenzaTrovato == 77) {
					document.formArchivioAccordiQuadro.archWhereLista.value += " and V_GARE_ACCORDIQUADRO.AQOPER = 2";
				} else {
					var tabellatoA1138 = "${tabellatoA1138}";
					if (tabellatoA1138=="1")
						document.formArchivioAccordiQuadro.archWhereLista.value += " and V_GARE_ACCORDIQUADRO.AQOPER = 1";
				}
			}
		}
	}
	
	var tipgarg = getValue("GARE_TIPGARG");
	aggiornaFiltroArchivioAccordiQuadro(tipgarg,true,null); 
	
	</c:if>
	
	<c:if test="${modo ne 'VISUALIZZA'}">
		var gartel = getValue("TORN_GARTEL");
		var offtel = getValue("TORN_OFFTEL");
		
		if(gartel!=1 || (gartel==1 && offtel==2)){
			$("#GARE_RIBCAL option[value='3']").remove();
		}
	</c:if>
	
	
	
	
	<c:if test="${isProceduraTelematica or param.proceduraTelematica eq 1}">
		<c:choose>
			<c:when test="${modo ne 'VISUALIZZA'}">
				function gestioneCampiAstaEl_Critlicg(critlicg) {
					if (critlicg==1) {
						showObj("rowASTA",true);
						showObj("rowTORN_RICASTAE",true);
						var ricastae = getValue("TORN_RICASTAE");
						if (ricastae==null || ricastae=="") {
							setValue("TORN_RICASTAE","2"); 
							nascondiCampiAstaEl(true);
						} else {
							if (ricastae=='1') {
								showObj("rowTORN_RICASTAE",true);
								showObj("rowTORN_AEMODVIS",true);
								showObj("rowTORN_AENOTE",true);
								var ribcal = getValue("GARE_RIBCAL"); 
								visualizzazioneCampiScartoAstaEl(ribcal,true);
							} else {
								nascondiCampiAstaEl(true);
							}
						}
					} else {
						showObj("rowASTA",false);
						showObj("rowTORN_RICASTAE",false);
						setValue("TORN_RICASTAE","");
						nascondiCampiAstaEl(true);
					}
				} 	
			</c:when>
			<c:otherwise>
				function gestioneCampiAstaEl_Critlicg(critlicg) {
					if (critlicg==1) {
						showObj("rowASTA",true);
						var ricastae = getValue("TORN_RICASTAE"); 
						if (ricastae=='1') {
							showObj("rowTORN_RICASTAE",true);
							showObj("rowTORN_AEMODVIS",true);
							showObj("rowTORN_AENOTE",true);
							var ribcal = getValue("GARE_RIBCAL"); 
							visualizzazioneCampiScartoAstaEl(ribcal,false);
						} else {
							nascondiCampiAstaEl(false);
						}
					} else {
						showObj("rowASTA",false);
						showObj("rowTORN_RICASTAE",false);
						nascondiCampiAstaEl(false);
					}
				}
			</c:otherwise>
		</c:choose>
		
		function visualizzazioneCampiScartoAstaEl(ribcal,modifica) {
			if (ribcal==1) {
				showObj("rowTORN_AERIBMIN",true);
				showObj("rowTORN_AERIBMAX",true);
				showObj("rowTORN_AEIMPMIN",false);
				showObj("rowTORN_AEIMPMAX",false);
				if (modifica) {
					setValue("TORN_AEIMPMIN","");
					setValue("TORN_AEIMPMAX","");
				}
			} else if (ribcal==2) {
				showObj("rowTORN_AEIMPMIN",true);
				showObj("rowTORN_AEIMPMAX",true);
				showObj("rowTORN_AERIBMIN",false);
				showObj("rowTORN_AERIBMAX",false);
				if (modifica) {
					setValue("TORN_AERIBMIN","");
					setValue("TORN_AERIBMAX","");
				}
			}
		}
		
		function nascondiCampiAstaEl(modifica){
			showObj("rowTORN_AEMODVIS",false);
			showObj("rowTORN_AENOTE",false);
			showObj("rowTORN_AERIBMIN",false);
			showObj("rowTORN_AERIBMAX",false);
			showObj("rowTORN_AEIMPMIN",false);
			showObj("rowTORN_AEIMPMAX",false);
			if(modifica){
				setValue("TORN_AEMODVIS","");
				setValue("TORN_AENOTE","");
				setValue("TORN_AERIBMIN","");
				setValue("TORN_AERIBMAX","");
				setValue("TORN_AEIMPMIN","");
				setValue("TORN_AEIMPMAX","");
			}
		}
		
		function gestioneCampiAstaEl_Ricastae(ricastae) {
			if (ricastae=='1') {
				showObj("rowTORN_RICASTAE",true);
				showObj("rowTORN_AEMODVIS",true);
				showObj("rowTORN_AENOTE",true);
				var ribcal = getValue("GARE_RIBCAL"); 
				visualizzazioneCampiScartoAstaEl(ribcal,true);
			} else {
				nascondiCampiAstaEl(true);
			}
		}
		
		function gestioneCampiAstaEl_Ribcal(ribcal){
			var critlicg = getValue("GARE_CRITLICG");
			
			if (critlicg == 1) {
				var ricastae = getValue("TORN_RICASTAE"); 
				if (ricastae=='1') {
					visualizzazioneCampiScartoAstaEl(ribcal,true);
				} 
			}
			
		
		}
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.UFFDET") and garaLottoUnico}'>
		function gestioneVisualizzazioneUffedt(cenint,settaValore){
			if (cenint==null || cenint=="") {
				setValue("TORN_UFFDET","");
				showObj("rowTORN_UFFDET",false);
			} else {
				showObj("rowTORN_UFFDET",true);
				$.ajax({
					type: "GET",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if (x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "${pageContext.request.contextPath}/pg/ListavaloriSettori.do",
					data: "codein=" + cenint,
					success: function(data){
						if (data==null || (data!=null  && data.length==0)) {
							showObj("rowTORN_UFFDET",false);
							setValue("TORN_UFFDET","");
						} else {
				<c:if test='${modo ne "VISUALIZZA"}'>
					var valoreIniziale=getValue("TORN_UFFDET");
					//Si sbiancano tutti i valori del campo e poi si inseriscono solo quelli della tabella UFFSET
					$('#TORN_UFFDET').empty();
					var option = new Option("", "");
					$('#TORN_UFFDET').append($(option));
					$.map( data, function( item ) {
						$("#TORN_UFFDET").append($("<option/>", {value: item[0], text: item[1] }));
					});
					if (settaValore=="1")
						setValue("TORN_UFFDET",valoreIniziale);
					else
						setValue("TORN_UFFDET","");		
				</c:if>
							}
					},
					error: function(e){
						alert("Errore nell'estrazione dei settori attivi per la stazione appaltante:" + cenint);
					}
				});
			}
		}
		
		var cenint = getValue("TORN_CENINT")
		gestioneVisualizzazioneUffedt(cenint,"1");
	</c:if>

	initEsenteCIG_CODCIG();
	
	<c:if test='${integrazioneWSERP eq "1"}'>
	 <c:choose>
		<c:when test='${tipoWSERP eq "SMEUP" || tipoWSERP eq "AVM" || tipoWSERP eq "UGOVPA"}'>
			function leggiCarrelloRda(codgar,ngara){
				<c:choose>
				<c:when test='${!empty presenzaRda && presenzaRda eq "2"}'>
					alert("Tale funzione non risulta disponibile in quanto esistono rda collegate alla lista delle lavorazioni e forniture!");
				</c:when>
				<c:otherwise>
					bloccaRichiesteServer();
					formListaRda.href.value = "gare/commons/lista-rda-scheda.jsp";
					formListaRda.codgar.value = codgar;
					formListaRda.codice.value = ngara;
					formListaRda.genere.value = "2";
					formListaRda.bustalotti.value = "${bustalotti}";
					formListaRda.linkrda.value = "1";
					formListaRda.submit();
				</c:otherwise>
	 			</c:choose>
			}
		</c:when>
		<c:otherwise>
		</c:otherwise>
	 </c:choose>
	</c:if>
	
	
	function visMetaDati(numeroRda,esercizio,tipoWSERP){
		var ngara = getValue("GARE_NGARA");
		var href="href=gare/garerda/popup-rda-metadati.jsp&ngara=" + ngara + "&numeroRda=" + numeroRda  + "&esercizio=" + esercizio + "&tipoWSERP=" + tipoWSERP + "&idconfi=" + idconfi;
		openPopUpCustom(href, "metadatiRda", 800, 600, "yes","yes");
	}
	
	<c:if test='${integrazioneERPvsWSDM eq "1"}'>
			$("#rowtitoloGARERDA_1").text("Richiesta di acquisto");
			$("#rowtitoloGARERDA_1").css("font-weight","bold");
	</c:if>
	<c:if test='${integrazioneWSERP eq "1" && tipoWSERP eq "FNM"}'>
			$("#rowtitoloGARERDA_1").text("Procedimento");
			$("#rowtitoloGARERDA_1").css("font-weight","bold");
	</c:if>
	
	function gestioneSortinv(iterga){
		if(iterga==2 || iterga==4){
			showObj("rowTORN_SORTINV",true);
		}else{
			showObj("rowTORN_SORTINV",false);
			setValue("TORN_SORTINV","");
		}
		sortinv = getValue("TORN_SORTINV");
		gestioneNumope(sortinv);
	}	
	
	function gestioneNumope(sortinv){
		if(sortinv==1){
			showObj("rowTORN_NUMOPE",true);
		}else{
			showObj("rowTORN_NUMOPE",false);
			setValue("TORN_NUMOPE","");
		}
	}	
	
	function impostaDatiDocumentaleCommessa(tipo){
		if(tipo==1){
			document.formwsdm.entita.value="PERI";
			document.formwsdm.key1.value="${datiRiga.GARE_CLAVOR}";
			document.formwsdm.key2.value=null;
			document.formwsdm.genereGara.value="-1";
		}else{
			document.formwsdm.entita.value="APPA";
			document.formwsdm.key1.value="${datiRiga.GARE_CLAVOR}";
			document.formwsdm.key2.value="${datiRiga.GARE_NUMERA}";
			document.formwsdm.genereGara.value="-2";
		}
		document.formwsdm.autorizzatoModifiche.value = "2";
		document.formwsdm.submit();
	}	
		
	
	var options;
	function gestioneTabellatoDETLICG(critlicg){
		var detlicg = getValue("GARE_DETLICG");
		var form = $("#GARE_DETLICG");
		if(options){
			options.each(function(){
			$(this).removeClass("removed");
			});
			options.appendTo(form);
		}
		if(critlicg == 3){
			$("#GARE_DETLICG").find("option[value='1']").addClass("removed");
			$("#GARE_DETLICG").find("option[value='2']").addClass("removed");
			$("#GARE_DETLICG").find("option[value='3']").addClass("removed");
			$("#GARE_DETLICG").find("option[value='5']").addClass("removed");
			$("#rowGARE_CALCSOANG select").val("2");
			$("#rowTORN_OFFAUM select").val("1");
			showObj("rowGARE_MODASTG", false);
		}else{
			var tipgen = getValue("TORN_TIPGEN");
			$("#GARE_DETLICG").find("option[value='6']").addClass("removed");
			if(tipgen == 1){
				$("#GARE_DETLICG").find("option[value='5']").addClass("removed");
			}else{
				$("#GARE_DETLICG").find("option[value='1']").addClass("removed");
				$("#GARE_DETLICG").find("option[value='2']").addClass("removed");
			}
		}
		options = $("#GARE_DETLICG").children().detach();
		options.not(".removed").appendTo(form);
		$("#rowGARE_DETLICG select").val(detlicg);
	}
	
	<c:if test='${modo eq "MODIFICA"}'>
		var bloccoModificatiDati = $("#bloccoModificatiDati").val();
		bloccaDopoPubblicazione(bloccoModificatiDati);
	</c:if>		
</gene:javaScript>

<form name="formVisualizzaDocumenti" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/commons/archivia-documenti-scheda.jsp" /> 
	<input type="hidden" name="codice" id="codice" value="" />
	<input type="hidden" name="codgar" id="codgar" value="" />
	<input type="hidden" name="genere" id="genere" value="" />
	<input type="hidden" name="entita" value="" />
	<input type="hidden" name="key1" value="" />
	<input type="hidden" name="chiaveOriginale" value="" />
	<input type="hidden" name="gartel" value="" />
	<input type="hidden" name="idconfi" value="${idconfi}" />
</form> 
		
<form name="formwsdm" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-scheda.jsp" /> 
	<input type="hidden" name="entita" value="GARE" />
	<input type="hidden" name="key1" value="${datiRiga.GARE_NGARA}" />
	<input type="hidden" name="key2" value="" /> 
	<input type="hidden" name="key3" value="" />
	<input type="hidden" name="key4" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="genereGara" value="2" />
	<input type="hidden" name="idconfi" value="${idconfi}" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
	
</form>

<form name="formListaRda" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="" /> 
	<input type="hidden" name="codgar" id="codgar" value="" />
	<input type="hidden" name="codice" id="codice" value="" />
	<input type="hidden" name="genere" id="genere" value="" />
	<input type="hidden" name="bustalotti" id="bustalotti" value="" />
	<input type="hidden" name="linkrda" id="linkrda" value="" />
	<input type="hidden" name="uffint" id="uffint" value="${sessionScope.uffint}" />
</form> 
