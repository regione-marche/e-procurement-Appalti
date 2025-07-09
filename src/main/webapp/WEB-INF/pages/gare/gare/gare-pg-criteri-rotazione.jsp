<%
/*
 * Created on: 21/07/2010
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

<fmt:setBundle basename="AliceResources" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.nuts.mod.js"></script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >
</gene:redefineInsert>
	
<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>
<c:set var="codiceGara" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>
<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO","false")}' />
<c:set var="bloccoModificatiDati" value='${bloccoPubblicazionePortaleBando eq "TRUE" && modo eq "MODIFICA"}'/>

<%/* Dati generali della gara */%>
<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreElencoDitte">
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo"/>

	<gene:campoScheda visibile="${bloccoModificatiDati eq 'true'}">
		<td colspan="2" style="color:#0000FF">
			<br><b>ATTENZIONE:</b>&nbsp;
			Parte dei dati sono in sola consultazione perché l'elenco è pubblicato su portale Appalti.
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<gene:gruppoCampi idProtezioni="GEN" >
		<gene:campoScheda>
			<td colspan="2"><b>Criterio di rotazione</b></td>
		</gene:campoScheda>

		<gene:campoScheda campo="NGARA" visibile="false" />
		<gene:campoScheda campo="CODGAR1" visibile="false" />

		<gene:campoScheda campo="CODGAR" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false" />
		<gene:campoScheda campo="TIPOLOGIA"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false"/>
		<gene:campoScheda campo="TIPOELE"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false"/>

		<gene:campoScheda campo="ISELPREM"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA"/>
		<gene:campoScheda campo="ISELPREMOB" entita="GAREALBO" obbligatorio="true" />
	    <gene:archivio titolo="Criteri rotazione"
		    lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GAREALBO.TIPOALGO"),"gare/algoritmi/algoritmi-lista-popup.jsp","")}'
		    scheda=''
		    schedaPopUp=''
		    campi="ALGORITMI.TIPOALGO;TAB1.TAB1DESC;TAB1.TAB1DESC;ALGORITMI.DESCALGO"
		    chiave="GAREALBO_TIPOALGO"
		    inseribile="false">
		    <gene:campoScheda campo="TIPOALGO" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false"/>
		    <gene:campoScheda campo="TAB1DESC" entita="TAB1" from="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA and GAREALBO.TIPOALGO=TAB1.TAB1TIP and TAB1.TAB1COD='A1073'" visibile="false"/>
		    <gene:campoScheda campo="TIPOALGO_DESC" obbligatorio="true" campoFittizio="true" definizione="T100;0;;;TIPOALGOGA" modificabile="true" value="${datiRiga.TAB1_TAB1DESC}" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GAREALBO.TIPOALGO","ALGORITMI.DESCALGO")}' tooltip="Criterio di rotazione">
		    <c:if test='${modo eq "VISUALIZZA"}'>
		    <span style="float: right;">
		    <a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/criteriRotazioneSelezioneDaElenco.pdf');" title="Consulta manuale" style="color:#002E82;">
		     <img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
		    </a>
		    </span>
		    </c:if>
		   </gene:campoScheda>
		   <gene:campoScheda campo="DESCALGO" entita="ALGORITMI" from="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA and ALGORITMI.CODAPP='PG' and ALGORITMI.TIPOALGO=GAREALBO.TIPOALGO" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GAREALBO.TIPOALGO")}' modificabile="false"/>
	   </gene:archivio>
   		 
		<gene:campoScheda campo="AGGNUMORD"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />	
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="MAN" visibile="${datiRiga.GAREALBO_TIPOLOGIA ne 3 }">
		<gene:campoScheda>
			<td colspan="2"><b>Rinnovo iscrizione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="VALISCR" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA"  visibile="${datiRiga.GAREALBO_TIPOLOGIA ne 3 }"/>
		<gene:campoScheda campo="RIFISCR" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA"  visibile="${datiRiga.GAREALBO_TIPOLOGIA ne 3 }"/>
		<gene:campoScheda campo="GPREAVRIN" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA"  visibile="${datiRiga.GAREALBO_TIPOLOGIA ne 3 }" />
		<gene:campoScheda campo="APPRIN" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA"  visibile='${datiRiga.GAREALBO_TIPOLOGIA ne 3 and fn:contains(listaOpzioniDisponibili, "OP114#")}' />
		
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="ALTRIDATI" >
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="TIPOCLASS"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" obbligatorio="true"/>
		<gene:campoScheda campo="ISCRIRT" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA"  obbligatorio="true"/>
		<gene:campoScheda campo="AGGRTCOMP" entita="GAREALBO" />
		<gene:campoScheda campo="COORDSIC" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="REQTORRE" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="PUBOPE" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile='${fn:contains(listaOpzioniDisponibili, "OP114#")}'/>  
		<gene:campoScheda campo="CTRLAGGIU" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="CTRLIMP" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" obbligatorio="true">
			<gene:checkCampoScheda funzione='checkValorePositivo("##")' obbligatorio="true" messaggio="Deve essere inserito un valore maggiore di zero" onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="CTRLGG" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" obbligatorio="true">
			<gene:checkCampoScheda funzione='checkValorePositivo("##")' obbligatorio="true" messaggio="Deve essere inserito un valore maggiore di zero" onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="CTRLELE" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="CTRLPROV" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="CTRLIMPGA" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
		<gene:campoScheda campo="ISOPEAUTO" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" />
	</gene:gruppoCampi>

	<gene:fnJavaScriptScheda funzione='gestioneCTRLAGGIU("#GAREALBO_CTRLAGGIU#")' elencocampi='GAREALBO_CTRLAGGIU' esegui="true" />
	<gene:fnJavaScriptScheda funzione='gestioneAGGRTCOMP("#GAREALBO_TIPOALGO#","#GAREALBO_ISCRIRT#")' elencocampi='GAREALBO_TIPOALGO;GAREALBO_ISCRIRT' esegui="true" />
	<gene:fnJavaScriptScheda funzione='gestioneISELPREM("#GAREALBO_ISELPREM#")' elencocampi='GAREALBO_ISELPREM' esegui="true" />

	<jsp:include page="/WEB-INF/pages/gare/garcpv/codiciCPV-gara.jsp">
		<jsp:param name="datiModificabili" value="${not bloccoModificaPCP}"/>
		<jsp:param name="lottoOffertaUnica" value="true"/>
		<jsp:param name="initCODCPV" value="${initCODCPV }"/>
		<jsp:param name="bloccoModificaPCP" value="${bloccoModificaPCP}"/>
	</jsp:include>

	<gene:gruppoCampi idProtezioni="LUOGOESEC" >
		<gene:campoScheda>
			<td colspan="2"><b>Luogo di esecuzione</b></td>
		</gene:campoScheda>
		<c:choose>
			<c:when test="${not bloccoModificaPCP}">
				<gene:campoScheda campo="CODNUTS" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" href="#" speciale="true" >
					<gene:popupCampo titolo="Dettaglio codice NUTS" href="#" />
				</gene:campoScheda>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="CODNUTS" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" />
			</c:otherwise>
		</c:choose>
	</gene:gruppoCampi>



	<input type="hidden" name="genere" value="${genere}" />
	
	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GARE_CODGAR1}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	<c:if test='${!(modo eq "MODIFICA" or modo eq "NUOVO")}'>
		<gene:redefineInsert name="addToAzioni">
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
			<c:if test='${datiRiga.GAREALBO_ISELPREM eq "1"}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:accediElementiPremiali('${datiRiga.GARE_NGARA}');" title="Accedi ad elementi premiali">
								Accedi ad elementi premiali
							</a>
						</td>
					</tr>
			</c:if>
		</gene:redefineInsert>
	</c:if>

</gene:formScheda>

<gene:javaScript>

	
	function gestioneCTRLAGGIU(ctrlaggiu){
		if(ctrlaggiu==1 || ctrlaggiu==2){
			showObj("rowGAREALBO_CTRLIMP",true);
			showObj("rowGAREALBO_CTRLGG",true);
			showObj("rowGAREALBO_CTRLELE",true);
			showObj("rowGAREALBO_CTRLIMPGA",true);
			showObj("rowGAREALBO_CTRLPROV",true);
			<c:if test='${modo ne "VISUALIZZA"}'>
				if(getValue("GAREALBO_CTRLELE")==null || getValue("GAREALBO_CTRLELE")=="")
					setValue("GAREALBO_CTRLELE", "1");
				if(getValue("GAREALBO_CTRLIMPGA")==null || getValue("GAREALBO_CTRLIMPGA")=="")
					setValue("GAREALBO_CTRLIMPGA", "2");
				if(getValue("GAREALBO_CTRLPROV")==null || getValue("GAREALBO_CTRLPROV")=="")
					setValue("GAREALBO_CTRLPROV", "2");
			</c:if>
		}else{
			showObj("rowGAREALBO_CTRLIMP",false);
			showObj("rowGAREALBO_CTRLGG",false);
			showObj("rowGAREALBO_CTRLELE",false);
			showObj("rowGAREALBO_CTRLIMPGA",false);
			showObj("rowGAREALBO_CTRLPROV",false);
			<c:if test='${modo ne "VISUALIZZA"}'>
				setValue("GAREALBO_CTRLIMP", "");
				setValue("GAREALBO_CTRLGG", "");
				setValue("GAREALBO_CTRLELE", "");
				setValue("GAREALBO_CTRLIMPGA", "");
				setValue("GAREALBO_CTRLPROV", "");
			</c:if>
		}
	}

	function gestioneAGGRTCOMP(tipoalgo,iscrirt) {
		var tipologie = [10,13];
		if(!(tipologie.includes(parseInt(tipoalgo))) || iscrirt!=2){
			setValue("GAREALBO_AGGRTCOMP", "");
			showObj("rowGAREALBO_AGGRTCOMP",false);
		} else {
			showObj("rowGAREALBO_AGGRTCOMP",true);
		}
	}

	function checkValorePositivo(valore) {
		if (valore!=null && valore!="") {
			if (toVal(valore)>0)
				return true;
			else
				return false;
		} else {
			return true;
		}
	}

	function accediElementiPremiali(ngara){
		formVisElementiPremiali.ngara.value=ngara;
		formVisElementiPremiali.submit();
	}

	function gestioneISELPREM(iselprem) {
		if(iselprem != null && iselprem === '1'){
			showObj("rowGAREALBO_ISELPREMOB",true);
			<c:if test='${modo ne "VISUALIZZA"}'>
				if ($('#GAREALBO_ISELPREMOB').val() === '')
					setValue("GAREALBO_ISELPREMOB", "1");
			</c:if>
		} else {
			setValue("GAREALBO_ISELPREMOB", "");
			<c:if test='${modo ne "VISUALIZZA"}'>
			showObj("rowGAREALBO_ISELPREMOB",false);
			</c:if>
		}
	}

	$(document).ready(function(){
		var bloccoModificaDati = "${bloccoModificatiDati}";
		if(bloccoModificaDati==='true'){
			$('#GAREALBO_ISELPREM, #GAREALBO_TIPOCLASS, #GAREALBO_ISCRIRT').on('mousedown', function(e) {
				e.preventDefault();
				this.blur();
				window.focus();
			}).css('background-color','#EFEFEF');
		}
		_creaFinestraAlberoNUTS();
		_creaLinkAlberoNUTS($("#TORN_CODNUTS").parent(), "${modo}", $("#TORN_CODNUTS"), $("#TORN_CODNUTSview") );

		$("input[name^='TORN_CODNUTS']").attr('readonly','readonly');
		$("input[name^='TORN_CODNUTS']").attr('tabindex','-1');
		$("input[name^='TORN_CODNUTS']").css('border-width','1px');
		$("input[name^='TORN_CODNUTS']").css('background-color','#E0E0E0');

	});

</gene:javaScript>

<form name="formVisElementiPremiali" id="formVisElementiPremiali" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/elpremalbo/elpremalbo-lista.jsp" />
	<input type="hidden" name="ngara" id="ngara" value="" />
</form>
