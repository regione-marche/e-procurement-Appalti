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
</gene:redefineInsert>
	
<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>
<c:set var="codiceGara" value="${gene:concat('$',gene:getValCampo(key, 'NGARA'))}"/>
<c:set var="isFascicoloDocumentaleAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsApplicaFascicolazioneValidaFunction",  pageContext, codiceGara, idconfi)}' />
	
<%/* Dati generali della gara */%>
<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreElencoDitte">
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo"/>
			
	<gene:gruppoCampi idProtezioni="GEN" >
		<gene:campoScheda>
			<td colspan="2"><b>Criterio di rotazione</b></td>
		</gene:campoScheda>

		<gene:campoScheda campo="NGARA" visibile="false" />
		<gene:campoScheda campo="CODGAR1" visibile="false" />

		<gene:campoScheda campo="CODGAR" entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false" />
		<gene:campoScheda campo="TIPOLOGIA"  entita="GAREALBO" where="GARE.CODGAR1=GAREALBO.CODGAR and GARE.NGARA=GAREALBO.NGARA" visibile="false"/>
		
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
	
	
	<c:if test="${isFascicoloDocumentaleAbilitato eq '1'}">
		<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO", idconfi)}' />
		<gene:gruppoCampi idProtezioni="ISPGD">
			<gene:campoScheda>
				<td colspan="2"><b>Integrazione con sistema di protocollazione e gestione documentale</b></td>
			</gene:campoScheda>
			<c:if test="${!empty datiRiga.WSFASCICOLO_CODICE and datiRiga.WSFASCICOLO_CODICE !=''}">
				<c:set var="rifFascicolo" value="Cod.: ${datiRiga.WSFASCICOLO_CODICE}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_ANNO and datiRiga.WSFASCICOLO_ANNO !=''}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Anno: ${datiRiga.WSFASCICOLO_ANNO}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_NUMERO and datiRiga.WSFASCICOLO_NUMERO !=''}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Num.: ${datiRiga.WSFASCICOLO_NUMERO}"/>
			</c:if>
			<c:if test="${!empty datiRiga.WSFASCICOLO_CLASSIFICA and datiRiga.WSFASCICOLO_CLASSIFICA !='' && tipoWSDM ne 'JIRIDE'}">
				<c:if test="${!empty rifFascicolo && rifFascicolo !=''}">
					<c:set var="rifFascicolo" value="${rifFascicolo } -"/>
				</c:if>
				<c:set var="rifFascicolo" value="${rifFascicolo } Classifica: ${datiRiga.WSFASCICOLO_CLASSIFICA}"/>
			</c:if>
			<c:if test="${tipoWSDM eq 'TITULUS' || tipoWSDM eq 'ENGINEERINGDOC'}">
				<c:choose>
					<c:when test="${tipoWSDM eq 'ENGINEERINGDOC'}">
						<c:set var="labelCoduff" value="U.O. di competenza"/>
					</c:when>
					<c:otherwise>
						<c:set var="labelCoduff" value="Ufficio"/>
					</c:otherwise>
				</c:choose>
				<c:if test="${!empty datiRiga.WSFASCICOLO_CODAOO and datiRiga.WSFASCICOLO_CODAOO !=''}" >
					<c:set var="rifFascicolo" value="${rifFascicolo } - AOO: ${datiRiga.WSFASCICOLO_CODAOO}"/>
					<c:if test="${!empty datiRiga.WSFASCICOLO_DESAOO and datiRiga.WSFASCICOLO_DESAOO !=''}" >
						<c:set var="rifFascicolo" value="${rifFascicolo } ${datiRiga.WSFASCICOLO_DESAOO}"/>
					</c:if>
				</c:if>
				<c:if test="${!empty datiRiga.WSFASCICOLO_CODUFF and datiRiga.WSFASCICOLO_CODUFF !=''}" >
					<c:set var="rifFascicolo" value="${rifFascicolo } - ${labelCoduff}: ${datiRiga.WSFASCICOLO_CODUFF}"/>
					<c:if test="${!empty datiRiga.WSFASCICOLO_DESUFF and datiRiga.WSFASCICOLO_DESUFF !=''}" >
						<c:set var="rifFascicolo" value="${rifFascicolo } ${datiRiga.WSFASCICOLO_DESUFF}"/>
					</c:if>
				</c:if>
			</c:if>
			<gene:campoScheda campo="ISPGD_FIT" campoFittizio="true" title="Riferimento al fascicolo" definizione="T40;"  value="${rifFascicolo}" modificabile="false"/>
			<gene:campoScheda campo="CODICE" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="ANNO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="NUMERO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="CLASSIFICA" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			
			<gene:campoScheda campo="CODAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="DESAOO" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="CODUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
			<gene:campoScheda campo="DESUFF" entita="WSFASCICOLO" where="WSFASCICOLO.KEY1=GARE.NGARA and WSFASCICOLO.ENTITA='GARE'" visibile="false"/>
		</gene:gruppoCampi>
	</c:if>
	
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

</gene:javaScript>