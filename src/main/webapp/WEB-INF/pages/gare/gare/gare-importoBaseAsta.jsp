<%
/*
 * Created on 21-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione: Importo a base di gara della gara
	Creato da:   Marcello Caminiti
 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<fmt:setBundle basename="AliceResources" />

<c:choose>
	<c:when test='${param.tipgen eq "1"}'>
		<c:set var="CampoMsgImportoTotale" value="GARE.IMPAPP" />
	</c:when>
	<c:otherwise>
		<c:set var="CampoMsgImportoTotale" value="GARE.IMPAPP_NO_ONPRGE" />
	</c:otherwise>
</c:choose>

<gene:gruppoCampi idProtezioni="IMP">
		<gene:campoScheda>
			<td colspan="2"><c:if test='${param.tipgen eq "1"}'>${gene:callFunction3("it.eldasoft.gene.tags.functions.ExpandCollapseAllFunction",pageContext,"IMP","GARE.IMPMIS;GARE.IMPCOR;GARE.IMPAPP")}</c:if><b>Importo a base di gara, IVA esclusa</b></td>
		</gene:campoScheda>
		
		<gene:campoScheda campo="IMPMIS" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IMPMIS","IMPMIS_RIB;GARE.IMPNRM;GARE.IMPSMI;GARE.NOTMIS")}' defaultValue="${requestScope.initIMPMIS}" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
		<gene:campoScheda 
			title='di cui soggetto a ribasso' campo="IMPMIS_RIB" campoFittizio="true" modificabile="false" definizione="F15;0;;MONEY;" 
		    visibile='${(gene:checkProt( pageContext,"COLS.VIS.GARE.GARE.IMPNRM") or gene:checkProt( pageContext,"COLS.VIS.GARE.GARE.IMPSMI")) and param.tipgen eq "1"}'>
			<gene:calcoloCampoScheda funzione='toMoney(toVal("#GARE_IMPMIS#") - toVal("#GARE_IMPNRM#") - toVal("#GARE_IMPSMI#"))' 
			elencocampi="GARE_IMPMIS;GARE_IMPNRM;GARE_IMPSMI" />
			<gene:checkCampoScheda funzione='toVal("##")>=0' 
			messaggio='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.MsgImportoTotaleBaseAstaFunction",pageContext,"GARE.IMPMIS")}'
			obbligatorio="true" onsubmit="true" />
		</gene:campoScheda>
		<gene:campoScheda campo="IMPNRM" defaultValue="${requestScope.initIMPNRM}" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
		<gene:campoScheda campo="IMPSMI" defaultValue="${requestScope.initIMPSMI}" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
		<gene:fnJavaScriptScheda funzione="sbiancaCampiSeNonValorizzato('#GARE_IMPMIS#', 'GARE_IMPNRM;GARE_IMPSMI')" elencocampi="GARE_IMPMIS" esegui="false"/> 
		<gene:campoScheda campo="NOTMIS" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
			
		<gene:campoScheda campo="IMPCOR" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IMPCOR","IMPCOR_RIB;GARE.IMPSCO;GARE.IMPNRC;GARE.NOTCOR")}' defaultValue="${requestScope.initIMPCOR}" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
		<gene:campoScheda
			title='di cui soggetto a ribasso' campo="IMPCOR_RIB" campoFittizio="true" modificabile="false" definizione="F15;0;;MONEY;"
		    visibile='${(gene:checkProt( pageContext,"COLS.VIS.GARE.GARE.IMPNRC") or gene:checkProt( pageContext,"COLS.VIS.GARE.GARE.IMPSCO")) and param.tipgen eq "1"}'>
			<gene:calcoloCampoScheda funzione='toMoney(toVal("#GARE_IMPCOR#") - toVal("#GARE_IMPNRC#") - toVal("#GARE_IMPSCO#"))' 
			elencocampi="GARE_IMPCOR;GARE_IMPNRC;GARE_IMPSCO" />
			<gene:checkCampoScheda funzione='toVal("##")>=0' 
			messaggio='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.MsgImportoTotaleBaseAstaFunction",pageContext,"GARE.IMPCOR")}'
			obbligatorio="true" onsubmit="true" />
		</gene:campoScheda>
		<gene:campoScheda campo="IMPNRC" defaultValue="${requestScope.initIMPNRC}" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
		<gene:campoScheda campo="IMPSCO" defaultValue="${requestScope.initIMPSCO}" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
		<gene:fnJavaScriptScheda funzione="sbiancaCampiSeNonValorizzato('#GARE_IMPCOR#', 'GARE_IMPNRC;GARE_IMPSCO')" elencocampi="GARE_IMPCOR" esegui="false"/>
		<gene:campoScheda campo="NOTCOR" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
		
		<gene:campoScheda campo="ONPRGE" defaultValue="${requestScope.initONPRGE}" visibile='${param.tipgen eq "1"}' modificabile="${param.campiModificabili}"/>
					
		<gene:campoScheda campo="IMPAPP" title='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseFunction",pageContext,"GARE.IMPAPP","IMPAPP_RIB;GARE.IMPNRL;GARE.IMPSIC")}'
		  defaultValue="${requestScope.initIMPAPP}" modificabile="${param.campiModificabili}">
			<gene:calcoloCampoScheda funzione='toMoney(toVal("#GARE_IMPMIS#") + toVal("#GARE_IMPCOR#") + toVal("#GARE_ONPRGE#"))' 
			elencocampi="GARE_IMPMIS;GARE_IMPCOR;GARE_ONPRGE" />
		</gene:campoScheda>
		<gene:campoScheda title="di cui soggetto a ribasso" campo="IMPAPP_RIB" campoFittizio="true" modificabile="false"
		  definizione="F15;0;;MONEY;" visibile='${gene:checkProt( pageContext,"COLS.VIS.GARE.GARE.IMPNRL") or gene:checkProt( pageContext,"COLS.VIS.GARE.GARE.IMPSIC")}'>
			<gene:calcoloCampoScheda funzione='toMoney(toVal("#GARE_IMPAPP#") - toVal("#GARE_IMPNRL#") - toVal("#GARE_IMPSIC#") - toVal("#GARE_ONPRGE#"))' elencocampi="GARE_IMPAPP;GARE_IMPNRL;GARE_IMPSIC;GARE_ONPRGE" />
			<gene:checkCampoScheda funzione='toVal("##")>=0' 
			messaggio='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.MsgImportoTotaleBaseAstaFunction",pageContext,CampoMsgImportoTotale)}'
			obbligatorio="true" onsubmit="true" />
		</gene:campoScheda>
		<gene:campoScheda campo="IMPNRL" defaultValue="${requestScope.initIMPNRL}" modificabile="${param.campiModificabili}">
			<gene:calcoloCampoScheda funzione='toMoney(toVal("#GARE_IMPNRM#") + toVal("#GARE_IMPNRC#"))' 
			elencocampi="GARE_IMPNRM;GARE_IMPNRC" />
		</gene:campoScheda>
		<gene:campoScheda campo="IMPSIC" defaultValue="${requestScope.initIMPSIC}" modificabile="${param.campiModificabili}">
			<gene:calcoloCampoScheda funzione='toMoney(toVal("#GARE_IMPSMI#") + toVal("#GARE_IMPSCO#"))' 
			elencocampi="GARE_IMPSMI;GARE_IMPSCO" />
		</gene:campoScheda>
		<c:if test="${param.lottoOffertaUnica eq 'true'}">
			<gene:campoScheda campo="MODMANO" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='false' defaultValue="${requestScope.initMODMANO}"/>
		</c:if>
		<gene:fnJavaScriptScheda funzione="sbiancaCampiSeNonValorizzato('#GARE_IMPAPP#', 'GARE_IMPNRL;GARE_IMPSIC;GARE1_IMPMANO')" elencocampi="GARE_IMPAPP" esegui="false"/>
		<c:if test="${modo ne 'VISUALIZZA'}">
			<gene:fnJavaScriptScheda funzione="calcoloImportoDaPercentualeManodopera('#GARE_IMPAPP#', '#IMPPER#','#GARE_IMPNRL#','#GARE_IMPSIC#','#GARE_ONPRGE#')" elencocampi="GARE_IMPAPP;IMPPER;GARE_IMPNRL;GARE_IMPSIC" esegui="false"/>
		</c:if>
		<gene:fnJavaScriptScheda funzione="calcoloPercentualeManodopera('#GARE_IMPAPP#', '#GARE1_IMPMANO#','#GARE_IMPNRL#','#GARE_IMPSIC#','#GARE_ONPRGE#')" elencocampi="GARE1_IMPMANO" esegui="true"/>
		<gene:campoScheda campo="IMPMANO" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" modificabile="${param.campiModificabili}"/>
		<c:set var="IMPPERmodificabile" value="${param.campiModificabili and gene:checkProt(pageContext,'COLS.MOD.GARE.GARE1.IMPMANO')}" />
		<gene:campoScheda title='Costi manodopera in percentuale' campo="IMPPER" visibile="${gene:checkProt( pageContext,'COLS.VIS.GARE.TORN.MODMANO') and gene:checkProt( pageContext,'COLS.VIS.GARE.GARE1.IMPMANO')}" campoFittizio="true" modificabile="${IMPPERmodificabile}"  definizione="F13.5;0;;PRC;" />
		<gene:campoScheda campo="ONSOGRIB" visibile='${param.tipgen eq "1"}' defaultValue="${gene:if(requestScope.initONSOGRIBDaAppa eq 'true', requestScope.initONSOGRIB, '1')}" modificabile="${param.campiModificabili}"/>
		<gene:campoScheda campo="IVALAV" modificabile="${param.campiModificabili}"/>
	</gene:gruppoCampi>
	<gene:javaScript>
	
	<c:if test="${modo eq 'VISUALIZZA'}">
		var view = $("#IMPPERview");
		var htmlString = view.html();
		if(view.html()){view.html(htmlString + " %");}
	</c:if>
	
	function calcoloPercentualeManodopera(impapp, impmano,impnrl, impsic, imprge){
		impapp = impapp - impnrl - impsic - imprge;
		if(!impapp || impapp == 0 || !impmano ){
			setValue("IMPPER", "", false);
			return;
			}
		var percentuale = ((impmano / impapp) * 100);  
		percentuale = round(eval(percentuale), 5);
		<c:if test="${!IMPPERmodificabile and modo ne 'VISUALIZZA'}">
			percentuale = percentuale + " %";
		</c:if>
		setValue("IMPPER", percentuale, false);
	}
	
	
	function calcoloImportoDaPercentualeManodopera(impapp, impper,impnrl, impsic, imprge){
		if(!impapp || impapp == 0){
			setValue("GARE1_IMPMANO", "", false);
			return;
			}
		if(!impper){
			setValue("GARE1_IMPMANO", "", false);
			return;
		}
		impapp = impapp - impnrl - impsic - imprge;
		var importo = (impapp / 100) * impper;  
		importo = round(eval(importo), 2);
		setValue("GARE1_IMPMANO", importo, false);
	}
	
	</gene:javaScript>