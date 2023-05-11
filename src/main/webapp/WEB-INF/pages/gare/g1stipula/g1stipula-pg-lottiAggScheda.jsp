
<%
/*
 * Created on: 19/05/2021
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<gene:redefineInsert name="head">	
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
			<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >
			
	</gene:redefineInsert>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetGenereGaraFunction", pageContext, codgar)}' />

<gene:redefineInsert name="schedaNuovo" />
<gene:redefineInsert name="pulsanteNuovo" />
<gene:redefineInsert name="documentiAssociati" />
<gene:redefineInsert name="noteAvvisi" />

<c:if test="${genere eq '2' }">
	<c:set var="chiaveGara" value="NGARA:${ngara }"/>
	<c:set var="tipgargString" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPGARGFunction", pageContext, chiaveGara)}' />
	<fmt:formatNumber type="number" value="${tipgargString}" var="tipgarg"/>
	<c:if test="${!(tipgarg >= 51 and tipgarg <= 89)}" >
		<c:set var="bloccoTipgarg" value="true"/>
	</c:if>
</c:if>

<c:if test="${genere ne '2' || bloccoTipgarg eq 'true'}">
	<gene:redefineInsert name="schedaModifica" />
	<gene:redefineInsert name="pulsanteModifica" />		
</c:if>


<c:set var="propertyCig" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsCig)}' scope="request"/>
<c:if test="${! empty propertyCig}">
	<c:set var="isCigAbilitato" value='1' scope="request"/>
</c:if>
<c:set var="propertySimog" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsSimog)}' scope="request"/>
<c:if test="${! empty propertySimog}">
	<c:set var="isSimogAbilitato" value='1' scope="request"/>
</c:if>	

<c:set var="integrazioneVigilanza" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.vigilanza.ws.url")}'/>
<c:if test='${!empty integrazioneVigilanza and integrazioneVigilanza != ""}'>
	<c:set var="nomeApplicativo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.vigilanza.nomeApplicativo")}'/>
</c:if>



<gene:formScheda entita="G1STIPULA" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreStipulaLotti">
	
	<c:set var="idStipula" value='${gene:getValCampo(key, "G1STIPULA.ID")}' />

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_STIPULA"/>
		<jsp:param name="inputFiltro" value="${key}"/>
		<jsp:param name="filtroCampoEntita" value="idstipula=${idStipula}"/>
	</jsp:include>
	
	<gene:redefineInsert name="addToAzioni" >
	<c:if test='${modo eq "VISUALIZZA"}'>
		<c:choose>
		<c:when test='${isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG")}'>
		</c:when>
		<c:otherwise>
			<c:if test='${isSimogAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG")}'>
					<tr>
						<td class="vocemenulaterale" >
							<c:if test='${isNavigazioneDisattiva ne "1"}'>
								<a href="javascript:apriFormRichiestaCig();" title='Richiesta CIG' tabindex="1508">
							</c:if>
								Richiesta CIG
							<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
						</td>
					</tr>
			</c:if>
		</c:otherwise>
		</c:choose>
		
		<c:if test='${modo eq "VISUALIZZA" and !empty integrazioneVigilanza and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaDatiVigilanza")}'>
	  <tr>    
	    <td class="vocemenulaterale">
		      	<c:set var="voceMenu" value="Invia dati a Vigilanza"/>
		      	<c:if test="${!empty nomeApplicativo and nomeApplicativo!=''}">
		      		<c:set var="voceMenu" value="Invia dati a ${nomeApplicativo }"/>
		      	</c:if>
		      	<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:popupInviaVigilanza('${codgar}');" title="${voceMenu }" tabindex="1512">
				</c:if>
				  ${voceMenu }
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
		</td>
	</tr>
		</c:if>
		
		
	</c:if>
	</gene:redefineInsert>

	<gene:campoScheda campo="ID" visibile="false" />
	<c:choose>
		<c:when test="${genere ne '2' || bloccoTipgarg eq 'true'}">
			<gene:callFunction
				obj="it.eldasoft.sil.pg.tags.funzioni.GestioneLottiStipulaFunction"
				parametro='${whereLottiAggiudicati}' />
			<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp">
				<jsp:param name="entita" value='GARE' />
				<jsp:param name="nomeAttributoLista" value='gare' />
				<jsp:param name="idProtezioni" value="GARE" />
				<jsp:param name="jspDettaglioSingolo"
					value="/WEB-INF/pages/gare/g1stipula/g1stipula-pg-stipula-dettaglio.jsp" />
				<jsp:param name="arrayCampi"
					value="'GARE_NUMGARA_','GARE_CODIGA_','TORN_NUMAVCP_','GARE_CODCIG_','GARE_NOTGAR_','GARE_IAGGIU_','GARE1_IMPRIN_','GARE1_IMPALTRO_'" />
				<jsp:param name="titoloSezione" value="Lotto" />
				<jsp:param name="usaContatoreLista" value="true" />
				<jsp:param name="descEntitaVociLink" value="Gara di stipula" />
			</jsp:include>
		</c:when>
		<c:otherwise>
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG") and not (isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG"))}'>
				<c:set var="codiceGaraSimog" value="${codgar}"/>
				<c:set var="esisteAnagraficaSimog" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteAnagraficaSimogFunction", pageContext, codiceGaraSimog)}'/>
			</c:if>
			<gene:campoScheda>
				<td colspan="2"><b>Lotto 1</b></td>
			</gene:campoScheda>
			<gene:campoScheda entita="GARE" campo="NGARA" where="GARE.NGARA='${ngara}'" title="Codice lotto" modificabile="false"/>
			<gene:campoScheda entita="TORN" campo="NUMAVCP" where="TORN.CODGAR='${codgar}'" modificabile="false"/>
			<gene:campoScheda entita="GARE" campo="CODCIG" where="GARE.NGARA='${ngara}'" modificabile="false"/>
			<gene:campoScheda campo="ESENTE_CIG" title="Esente CIG?" computed="true" campoFittizio="true" definizione="T10;;;SN;" value="2" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.CODCIG") }' modificabile="false"/>
			<gene:campoScheda campo="STATO_SIMOG" title="Effettuata richiesta CIG?"
				campoFittizio="true" definizione="T30;"
				value="${tipoSimogDesc} - ${statoSimog }" visibile="${esisteAnagraficaSimog eq 'true'}" modificabile="false"/>
			<gene:campoScheda entita="GARE" campo="NOT_GAR" where="GARE.NGARA='${ngara}'" modificabile="false"/>
			<gene:campoScheda entita="GARE" campo="IAGGIU" where="GARE.NGARA='${ngara}'" modificabile="false"/>
			<gene:campoScheda entita="GARE1" campo="IMPRIN" where="GARE1.NGARA='${ngara}'"  modificabile="false"/>
			<gene:campoScheda entita="GARE1" campo="IMPALTRO" where="GARE1.NGARA='${ngara}'"  modificabile="false"/>
			<jsp:include page="/WEB-INF/pages/gare/garcpv/codiciCPV-gara.jsp">
				<jsp:param name="datiModificabili" value="true"/>
				<jsp:param name="lottoStipula" value="true"/>
			</jsp:include> 
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
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2"}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
							</c:if>
						</gene:insert>
					</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
		</c:otherwise>
	</c:choose>
	
	
</gene:formScheda>
<gene:javaScript>
	<c:choose>
		<c:when test="${genere ne '2' }">
			$('[id^="rowtitoloGARE"] td').text("Dati di sintesi");
			$('[id^="rowtitoloGARE"]').css("font-weight","bold");
		</c:when>
		<c:otherwise>
			$("#IntestazioneCPV").hide();
			initEsenteCIG_CODCIG();
	
			initSMARTCIG();
		
			function initEsenteCIG_CODCIG() {
				var esenteCig = getValue("ESENTE_CIG");
				var codcig = getValue("GARE_CODCIG");
				//alert("esente CIG = " + esenteCig);
				//alert("Codice CIG = " + codcig);
				if ("" != codcig) {
					if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
						setValue("ESENTE_CIG", "Si", false);
						showObj("rowTORN_NUMAVCP", false);
					} else {
						setValue("ESENTE_CIG", "No", false);
						showObj("rowTORN_NUMAVCP", true);
					}
				} else {
					setValue("ESENTE_CIG", "No", false);
					showObj("rowTORN_NUMAVCP", true);
				}
			}
			
			function initSMARTCIG() {
				var codcig = getValue("GARE_CODCIG");
				if ("" != codcig) {
					if (codcig.indexOf("X") == 0 || codcig.indexOf("Y") == 0 || codcig.indexOf("Z") == 0) {
						showObj("rowTORN_NUMAVCP", false);
					}
				}
			}
		</c:otherwise>
	</c:choose>
	
	function apriFormRichiestaCig(){
		bloccaRichiesteServer();
		document.formRichiestaCig.submit();
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
	comando +="&genereGara=${genere}";
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&"+comando;
	}
</gene:javaScript>

<form name="formRichiestaCig" id="formRichiestaCig" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/commons/richiestaCig.jsp" />
	<input type="hidden" name="genereGara" value="${genere}" />
	<input type="hidden" name="codiceGara" value="${codgar}" />
</form>