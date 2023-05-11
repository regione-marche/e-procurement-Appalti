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
	<c:set var="tipoPubSitoIstituzionale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", tipoPubblicazioneSitoIstituzionale)}'/>
	<c:if test='${tipoPubSitoIstituzionale eq "2"}'>
		<c:set var="url" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsSitoIstituzionale)}'/>
		<c:if test='${not empty url }'>
			<c:set var="visualizzaFunzPubATC" value="true"/>
		</c:if>
	</c:if>
</c:if>
<c:set var="propertySimog" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlWsSimog)}' scope="request"/>
<c:if test="${! empty propertySimog}">
	<c:set var="isSimogAbilitato" value='1' scope="request"/>
</c:if>	
	
<script type="text/javascript">

	var linksetSubMenuGare = "";
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-gare")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_gare_torn/v_gare_torn-trova.jsp&deftrova=V_GARE_TORN", 1211, "Ricerca gare");
	</c:if>
	
	<c:if test='${isSimogAbilitato eq "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.RichiestaCIG.GestioneRUPCentriCosto") and !empty sessionScope.profiloUtente.codiceFiscale}' >
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=w3/commons/gestione-credenziali-simog.jsp&modo=VISUALIZZA", 1212, "Credenziali RUP per richiesta CIG");
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=w3/w3deleghe/w3deleghe-lista.jsp", 1213, "Collaborazioni RUP per richiesta CIG");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-elenchi")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_gare_eleditte/v_gare_eleditte-trova.jsp&deftrova=V_GARE_ELEDITTE", 1211, "Ricerca elenchi");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-avvisi")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gareavvisi/gareavvisi-trova.jsp&deftrova=GAREAVVISI", 1211, "Ricerca avvisi");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-cataloghi")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_gare_catalditte/v_gare_catalditte-trova.jsp&deftrova=V_GARE_CATALDITTE", 1211, "Ricerca cataloghi");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_NSCAD-lista.ApriGare")}' >
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_gare_nscad/v_gare_nscad-trova.jsp&deftrova=V_GARE_NSCAD", 1212, "Ricerca gare in scadenza");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_DITTE_PRIT-lista.ApriImprese")}' >
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_ditte_prit/v_ditte_prit-trova.jsp&deftrova=V_DITTE_PRIT", 1212, "Ricerca plichi da ritirare");
	</c:if>
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STRUMENTI.GareContrattiAdempimenti") && (abilitazioneGare eq "A"  or (abilitazioneGare eq "U" && !empty sessionScope.profiloUtente.codiceFiscale)) && fn:contains(listaOpzioniDisponibili, "OP130#")}'>
		linksetSubMenuStrumenti += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/anticor/anticor-trova.jsp&deftrova=ANTICOR", 1263, "Gare e contratti - adempimenti Legge 190/2012");
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

	<c:if test='${visualizzaFunzPubATC eq "true"}'>
		//linksetSubMenuStrumenti += creaVoceSubmenu("javascript:pubblicazioneATC();", 1266, "Pubblicazione massiva sul sito istituzionale ATC");
		linksetSubMenuStrumenti += creaVoceSubmenu("javascript:allineamentoDatiATC();", 1266, "Allinea dati su sito istituzionale ATC");
	</c:if>
		
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-profilo")}'>
		linksetSubMenuGare += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_gare_profilo/v_gare_profilo-trova.jsp&deftrova=V_GARE_PROFILO", 1265, "Ricerca gare e avvisi");
	</c:if>
	
	var linksetSubMenuRicerche = "";
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.RICERCHE.Trova-ricerche")}'>
		linksetSubMenuRicerche += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/meric/meric-trova.jsp&deftrova=MERIC", 1211, "Ricerca ricerche mercato");
	</c:if>
	
	var linksetSubMenuStipule = "";
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STIPULE.Trova-stipula")}'>
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_gare_stipula/v_gare_stipula-trova.jsp&deftrova=V_GARE_STIPULA", 1268, "Ricerca stipule contratti");
	</c:if>
	
	<c:if test='${isSimogAbilitato eq "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.RichiestaCIG.GestioneRUPCentriCosto") and !empty sessionScope.profiloUtente.codiceFiscale}' >
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=w3/commons/gestione-credenziali-simog.jsp&modo=VISUALIZZA", 1212, "Credenziali RUP per richiesta CIG");
		linksetSubMenuStipule += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=w3/w3deleghe/w3deleghe-lista.jsp", 1213, "Collaborazioni RUP per richiesta CIG");
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
	
	<c:if test='${visualizzaFunzPubATC eq "true"}'>
		function pubblicazioneATC(){
			var href = "href=gare/commons/popup-pubblicazioneMassivaATC.jsp&numeroPopUp=1";
			openPopUpCustom(href, "pubblicazioneATC", "800", "500", "no", "no");
		}
		
		function allineamentoDatiATC(){
			var href = "href=gare/commons/popup-allinementoDatiATC.jsp&numeroPopUp=1";
			openPopUpCustom(href, "allineamentoDatiATC", "800", "500", "no", "no");
		}
		
	</c:if>

</script>