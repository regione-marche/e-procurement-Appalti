<%
  /*
   * Created on 04-ott-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE SOTTOVOCI DEI MENU SPECIFICI DI UN'APPLICAZIONE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" scope="request"/>
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<c:if test='${fn:contains(opzioniUtenteAbilitate, "ou89#") }'>
	<c:set var="nonusato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsGestionePATFunction", pageContext, "")}'/>
	<c:set var="visualizzaFunzPubPAT" value="${isGestionePATISWEB eq 'true'}"/>
</c:if>
<c:set var="propertySimog" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsSimog)}' scope="request"/>
<c:if test="${! empty propertySimog}">
	<c:set var="isSimogAbilitato" value='1' scope="request"/>
</c:if>	
	
<script type="text/javascript">

	var linksetSubMenuGare = "";
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-gare")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/v_gare_torn/v_gare_torn-trova.jsp&deftrova=V_GARE_TORN", 1211, "Ricerca gare");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-elenchi")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/v_gare_eleditte/v_gare_eleditte-trova.jsp&deftrova=V_GARE_ELEDITTE", 1211, "Ricerca elenchi");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-avvisi")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/gareavvisi/gareavvisi-trova.jsp&deftrova=GAREAVVISI", 1211, "Ricerca avvisi");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-cataloghi")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/v_gare_catalditte/v_gare_catalditte-trova.jsp&deftrova=V_GARE_CATALDITTE", 1211, "Ricerca cataloghi");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_NSCAD-lista.ApriGare")}' >
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/v_gare_nscad/v_gare_nscad-trova.jsp&deftrova=V_GARE_NSCAD", 1212, "Ricerca gare in scadenza");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_DITTE_PRIT-lista.ApriImprese")}' >
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/v_ditte_prit/v_ditte_prit-trova.jsp&deftrova=V_DITTE_PRIT", 1212, "Ricerca plichi da ritirare");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STRUMENTI.GareContrattiAdempimenti") && (abilitazioneGare eq "A"  or (abilitazioneGare eq "U" && !empty sessionScope.profiloUtente.codiceFiscale)) && fn:contains(listaOpzioniDisponibili, "OP130#")}'>
		linksetSubMenuStrumenti += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/anticor/anticor-trova.jsp&deftrova=ANTICOR", 1263, "Gare e contratti - adempimenti Legge 190/2012");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STRUMENTI.ImportaAffidamentiDatiEsterni") && (abilitazioneGare eq "A")}'>
		linksetSubMenuStrumenti += creaVoceSubmenu("javascript:importaAffidamenti();", 1264, "Importa affidamenti da dati esterni");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STRUMENTI.ImportaDatiL190") && (abilitazioneGare eq "A")}'>
		linksetSubMenuStrumenti += creaVoceSubmenu("javascript:importaDatiL190();", 1267, "Importa dati per adempimenti L190");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STRUMENTI.ImportExcelANAC") && (abilitazioneGare eq "A")}'>
		linksetSubMenuStrumenti += creaVoceSubmenu("javascript:ImportExcelANAC();", 1268, "Importa CIG e smartCIG da excel ANAC");
	</c:if>

	<c:if test='${visualizzaFunzPubPAT eq "true" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.SitoIstituzionale.PubblicaMassiva")}'>
		linksetSubMenuStrumenti += creaVoceSubmenu("javascript:pubblicazionePAT();", 1266, "Pubblicazione massiva sul sito istituzionale");
	</c:if>

	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-profilo")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/v_gare_profilo/v_gare_profilo-trova.jsp&deftrova=V_GARE_PROFILO", 1265, "Ricerca gare e avvisi");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ModuloComunicazioniGeneriche") and fn:contains(listaOpzioniDisponibili, "OP114#")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=geneweb/w_invcom/w_invcom-in-lista.jsp&entitaNoKey=HOMEPG&resetFiltro=true", 1213, "Comunicazioni generiche ricevute");
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=geneweb/w_invcom/w_invcom-lista.jsp&entitaNoKey=HOMEPG&resetFiltro=true", 1214, "Comunicazioni generiche inviate");
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=geneweb/w_invcom/w_invcom-scheda.jsp&entitaNoKey=HOMEPG&modo=NUOVO", 1215, "Nuova comunicazione generica");
	</c:if>
	
	<c:if test='${isSimogAbilitato eq "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.RichiestaCIG.GestioneRUPCentriCosto") and !empty sessionScope.profiloUtente.codiceFiscale}' >
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=w3/commons/gestione-credenziali-simog.jsp&modo=VISUALIZZA", 1215, "Credenziali RUP per richiesta CIG");
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=w3/w3deleghe/w3deleghe-lista.jsp", 1216, "Collaborazioni RUP per richiesta CIG");
	</c:if>
	
	var linksetSubMenuRicerche = "";
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.RICERCHE.Trova-ricerche")}'>
		linksetSubMenuRicerche += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/meric/meric-trova.jsp&deftrova=MERIC", 1211, "Ricerca ricerche mercato");
	</c:if>
	
	var linksetSubMenuStipule = "";
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STIPULE.Trova-stipula")}'>
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=gare/v_gare_stipula/v_gare_stipula-trova.jsp&deftrova=V_GARE_STIPULA", 1268, "Ricerca stipule contratti");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ModuloComunicazioniGeneriche") and fn:contains(listaOpzioniDisponibili, "OP114#")}'>
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=geneweb/w_invcom/w_invcom-in-lista.jsp&entitaNoKey=HOMEPG&resetFiltro=true", 1269, "Comunicazioni generiche ricevute");
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=geneweb/w_invcom/w_invcom-lista.jsp&entitaNoKey=HOMEPG&resetFiltro=true", 1270, "Comunicazioni generiche inviate");
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=geneweb/w_invcom/w_invcom-scheda.jsp&entitaNoKey=HOMEPG&modo=NUOVO", 1271, "Nuova comunicazione generica");
	</c:if>
	
	<c:if test='${isSimogAbilitato eq "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.RichiestaCIG.GestioneRUPCentriCosto") and !empty sessionScope.profiloUtente.codiceFiscale}' >
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=w3/commons/gestione-credenziali-simog.jsp&modo=VISUALIZZA", 1271, "Credenziali RUP per richiesta CIG");
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?href=w3/w3deleghe/w3deleghe-lista.jsp", 1272, "Collaborazioni RUP per richiesta CIG");
	</c:if>

	function initPage(){
	}
	
	function importaAffidamenti(){
		var href = "href=gare/v_gare_datiesterni/popup-importa.jsp&numeroPopUp=1";
		openPopUpCustom(href, "importaAffidamenti", "700", "500", "yes", "yes");
	}
	
	function importaDatiL190(){
		var href = "href=gare/commons/popup-importaDatiL190.jsp&numeroPopUp=1";
		openPopUpCustom(href, "importaDatiL190", "700", "300", "yes", "yes");
	}
	
	function ImportExcelANAC(){
		var href = "href=gare/commons/popup-ImportExcelANAC.jsp&numeroPopUp=1";
		openPopUpCustom(href, "ImportExcelANAC", "700", "600", "yes", "yes");
	}
	
	<c:if test='${visualizzaFunzPubPAT eq "true"}'>
		function pubblicazionePAT(){
			var href = "href=gare/commons/popup-pubblicazioneMassivaPAT.jsp&numeroPopUp=1";
			openPopUpCustom(href, "pubblicazionePAT", "800", "500", "no", "no");
		}
	</c:if>

</script>