<%
	/*
	 * Created on 17-ott-2007
   	 *
   	 * Copyright (c) EldaSoft S.p.A.
   	 * Tutti i diritti sono riservati.
   	 *
   	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   	 * aver prima formalizzato un accordo specifico con EldaSoft.
   	 */

	// PAGINA CHE CONTIENE LA HOMEPAGE DI PG
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />
<c:set var="fnucase" value='${gene:callFunction("it.eldasoft.gene.tags.utils.functions.GetUpperCaseDBFunction", "")}' />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' scope="request"/>
<c:set var="filtroLivelloUtenteElencoOperatori" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_ELEDITTE")}' scope="request"/>
<c:set var="filtroLivelloUtenteCataloghi" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_CATALDITTE")}' scope="request"/>
<c:set var="filtroLivelloUtenteAvvisi" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "GAREAVVISI")}' scope="request"/>
<c:set var="filtroPermessiUtente"	value=''/>
<c:set var="filtroLivelloUtenteService" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_DITTE_PRIT")}' scope="request"/>
<c:set var="filtroLivelloRicercheMercato" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "MERIC")}' scope="request"/>
<c:set var="filtroLivelloUtenteStipule" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_STIPULA")}' scope="request"/>
<c:set var="filtroLivelloUtenteProfiloRicerca" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_PROFILO")}' scope="request"/>
<c:set var="filtroLivelloUtenteProtocollo" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_NSCAD")}' scope="request"/>
<c:set var="filtroTipoGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction", pageContext)}' scope="request"/>
<c:set var="uffintAbilitata" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata")}'/>
<c:set var="visualizzazioneGareALotti" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti") }' scope="request"/>
<c:set var="visualizzazioneGareLottiOffUnica" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica") }' scope="request"/>
<c:set var="visualizzazioneGareALottoUnico" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALottoUnico") }' scope="request"/>
<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}' >
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro="homeElenchi"/>
</c:if>
<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}' >
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro="homeCataloghi"/>
</c:if>

<gene:setIdPagina schema="GARE" maschera="HOMEPG" />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetListaTipologiaDocFunction" />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetPaginazioneListaFunction" />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" /> 
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#")}' >
	<c:set var="amministratore" value="true"/>
</c:if>

<c:choose>
	<c:when test='${fn:contains(listaOpzioniDisponibili, "OP114#")}' >
		<c:set var="isIntegrazionePortaleAlice" value="true" scope="session"/>
	</c:when>
	<c:otherwise>
		<c:set var="isIntegrazionePortaleAlice" value="false" scope="session"/>
	</c:otherwise>
</c:choose>
<c:if test='${isIntegrazionePortaleAlice eq "true"}' >
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisiciRegistrazioniPortale") or 
					gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisisciAggiornamentiPortale") or
					(gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisiciIscrizioniElencoPortale") and 
					(gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare") or 
					gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")))}' >
		<c:set var="msgRichiesteArray" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroComunicazioniFunction", pageContext,filtroLivelloUtenteElencoOperatori,sessionScope.uffint)}' />
		<c:set var="msgRichieste" value="${fn:split(msgRichiesteArray, '#')[0]}"/>
		<c:set var="msgRegistrazioni" value="${fn:split(msgRichiesteArray, '#')[1]}"/>
		<c:set var="msgAggiornamenti" value="${fn:split(msgRichiesteArray, '#')[2]}"/>
	</c:if>
</c:if>
<c:choose>
	<c:when test='${fn:contains(listaOpzioniDisponibili, "OP129#")}' >
		<c:set var="isAmministrazioneTrasparente" value="true" scope="session"/>
	</c:when>
	<c:otherwise>
		<c:set var="isAmministrazioneTrasparente" value="false" scope="session"/>
	</c:otherwise>
</c:choose>

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<c:set var="abilitazioneCollaboratori" value="${!empty sessionScope.profiloUtente.codiceFiscale}" />

<gene:historyClear />

<html>
	<head>
		<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
		<script type="text/javascript">
			<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
		
			// al click nel documento si chiudono popup e menu
			if (ie4||ns6) document.onclick=hideSovrapposizioni;
			
			function hideSovrapposizioni() {
			  //hideSubmenuNavbar();
			  hideMenuPopup();
			  hideSubmenuNavbar();
			}
		</script>
		<jsp:include page="/WEB-INF/pages/commons/jsSubMenuComune.jsp" />
		<jsp:include page="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp" />
		<jsp:include page="/WEB-INF/pages/commons/vuoto.jsp" />
		<script type="text/javascript">
	  		function formFindButton(){
				bloccaRichiesteServer();
				formfind.submit();
			}
	  		
			function trovaGaraAvanzata(){
				apriPagina.href.value="gare/v_gare_torn/v_gare_torn-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovaGara(){
				document.location.href = "${contextPath}/pg/InitNuovaGara.do?" + csrfToken;
			}
			
			function trovaGaraAvanzataRicerca(){
				apriPagina.href.value="gare/v_gare_profilo/v_gare_profilo-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
	
			function trovaGaraAvanzataElencoDitte(){
				apriPagina.href.value="gare/v_gare_eleditte/v_gare_eleditte-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovaGaraElencoDitte(){
				listaNuovo.action += "&tipoGara=garaLottoUnico&garaPerElenco=1";
				listaNuovo.entita.value= "GARE";
				listaNuovo.jspPath.value="/WEB-INF/pages/gare/gare-scheda.jsp";
				bloccaRichiesteServer();
				listaNuovo.submit();
			}
			
			function trovaGaraAvanzataCataloghi(){
				apriPagina.href.value="gare/v_gare_catalditte/v_gare_catalditte-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovaGaraCatalogo(){
				listaNuovo.action += "&tipoGara=garaLottoUnico&garaPerCatalogo=1";
				listaNuovo.entita.value= "GARE";
				listaNuovo.jspPath.value="/WEB-INF/pages/gare/gare-scheda.jsp";
				bloccaRichiesteServer();
				listaNuovo.submit();
			}
	
			function trovaAvanzataRicercheMercato(){
				apriPagina.href.value="gare/meric/meric-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovaRicercaMercato(){
				listaNuovo.entita.value= "MERIC";
				listaNuovo.jspPath.value="/WEB-INF/pages/gare/meric/meric-scheda.jsp";
				bloccaRichiesteServer();
				listaNuovo.submit();
			}
	
			function trovaAvvisoAvanzato(){
				apriPagina.href.value="gare/gareavvisi/gareavvisi-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovoAvviso(){
				listaNuovo.entita.value= "GAREAVVISI";
				listaNuovo.jspPath.value="/WEB-INF/pages/gare/gareavvisi/gareavvisi-scheda.jsp";
				bloccaRichiesteServer();
				listaNuovo.submit();
			}
			
			function trovaOrdineNsoAvanzata(){
				apriPagina.href.value="gare/nso_ordini/nso_ordini-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
		
			function nuovoOrdineNso(){
				document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/nso_ordini/nso_associaCig.jsp&tipoGara=garaLottoUnico&modo=NUOVO";
			}
			
			function trovaStipulaAvanzata(){
				apriPagina.href.value="gare/v_gare_stipula/v_gare_stipula-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovaStipula(){
				document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1stipula/g1stipula_nuovaStipula.jsp";
			}
	
			function listaComunicazioni(tipo){
				var pagina = "w_invcom-lista-Da-Portale.jsp";
				if(tipo==4)
					pagina = "w_invcom-lista-ComunicazioniFS12-Da-Portale.jsp";
				apriPagina.href.value="geneweb/w_invcom/" +pagina + "?tipo="+tipo + "&param=${sessionScope.uffint}";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function trovaGaraAvanzataProtocollo(){
				apriPagina.href.value="gare/v_gare_nscad/v_gare_nscad-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function trovaImpreseAvanzata(){
				apriPagina.href.value="gare/v_ditte_prit/v_ditte_prit-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function trovaDittaAntimafiaAvanzata(){
				apriPagina.href.value="gene/impr/impr-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovaDittaAntimafia(){
				listaNuovo.entita.value= "IMPR";
				listaNuovo.jspPath.value="/WEB-INF/pages/gene/impr-scheda.jsp";
				bloccaRichiesteServer();
				listaNuovo.submit();
			}
			
			function importaAntimafia(){
				apriPagina.href.value="gare/gare/gare-importAntimafia.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function isNumber(n) {
			  return !isNaN(parseFloat(n)) && isFinite(n);
			}
			
			function apriAdempimenti(){
				var abilitazione="${abilitazioneGare}";
				if (abilitazione == "A" || (abilitazione == "U" && ${not empty sessionScope.profiloUtente.codiceFiscale}))
					document.location.href = "${contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/anticor/anticor-trova.jsp";
				else
					alert("Funzione non abilitata per l'utente corrente");
			}
	
			function apriListaIds(){
				apriPagina.href.value="gare/v_lista_ids/v_lista_ids-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function visualizzaCatalogo(catalogo){
				var chiave = "V_GARE_CATALDITTE.CODGAR=T:$" + catalogo;
				var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-scheda.jsp";
				href += "&key=" + chiave + "&" + csrfToken;
				document.location.href = href;
			}
			
			function visualizzaElenco(elenco){
				var chiave = "V_GARE_ELEDITTE.CODGAR=T:$" + elenco;
				var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-scheda.jsp";
				href += "&key=" + chiave;
				document.location.href = href;
			}
			
			function alboComponentiCommisione(modo){
				if(modo == 1){
					//creazione albo
					location.href = '${pageContext.request.contextPath}/ApriPagina.do?'+csrfToken+'&href=gare/commalbo/commalbo-scheda.jsp&modo=NUOVO';
				}else{
					//accedi ad allbo
					var chiave = "COMMALBO.ID=N:1";
					location.href = '${pageContext.request.contextPath}/ApriPagina.do?'+csrfToken+'&href=gare/commalbo/commalbo-scheda.jsp&key='+chiave;
				}
				
			}
			
			function trovaGaraLottoAvanzata(){
				apriPagina.href.value="w3/v_w3gare/v_w3gare-trova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function nuovaAnagraficaGara(){
				apriPagina.href.value="w3/w3gara/w3gara-nuova.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function apriListaCollaboratori() {
				apriPagina.href.value="w3/w3deleghe/w3deleghe-lista.jsp";
				bloccaRichiesteServer();
				apriPagina.submit();
			}
			
			function gestisciCredenzialiSIMOG(modo){
				var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=w3/commons/gestione-credenziali-simog.jsp";
				href += "&modo=VISUALIZZA" ;	
				document.location.href = href;
			}
			
			function apriListaRdaRdi(){
				var href = contextPath + "/pg/CollegaScollegaRda.do?"+csrfToken;
				document.location.href = href;
			}
			
		</script>
	</head>

	<body onload="setVariables();checkLocation();initPage();checkBrowser();">
		<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
		<table class="arealayout">
			<!-- questa definizione dei gruppi di colonne serve a fissare la dimensione
			     dei td in modo da vincolare la posizione iniziale del men� di navigazione
			     sopra l'area lavoro appena al termine del men� contestuale -->
			<colgroup width="150px"></colgroup>
			<colgroup width="*"></colgroup>
			<tbody>
				<tr class="testata">
					<td colspan="2">
						<jsp:include page="/WEB-INF/pages/commons/testata.jsp" />
					</td>
				</tr>
				<tr class="menuprincipale">
					<td></td>
					<td>
						<table class="contenitore-navbar">
							<tbody>
								<tr>
									<jsp:include page="/WEB-INF/pages/commons/menuSpecifico.jsp" />
									<jsp:include page="/WEB-INF/pages/commons/menuComune.jsp" />
								</tr>
							</tbody>
						</table>
		
						<!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
						<iframe id="iframesubnavmenu" class="gene"></iframe>
						<div id="subnavmenu" class="subnavbarmenuskin"
							onMouseover="highlightSubmenuNavbar(event,'on');"
							onMouseout="highlightSubmenuNavbar(event,'off');"></div>
					</td>
				</tr>
				<tr>
					<td class="menuazioni" valign="top">
						<div id="menulaterale"></div>
					</td>
					<td class="arealavoro">
						<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />
		
						<div class="contenitore-arealavoro">
							<c:set var="metodo"	value="trovaGara"/>

							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}' >
								<c:set var="metodo"	value="trovaGaraElencoDitte"/>
							</c:if>
						
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}' >
								<c:set var="metodo"	value="trovaGaraCataloghi"/>
							</c:if>
						
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GAREAVVISI-lista.ApriAvviso")}' >
								<c:set var="metodo"	value="trovaAvvisi"/>
							</c:if>
							
							<c:if test='${gene:checkProtFunz(pageContext,"ALT","ListaDitteAntimafia")}'>
								<c:set var="metodo"	value="trovaDittaAntimafia"/>
							</c:if>
		
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_NSCAD-lista.ApriGare")}' >
								<c:set var="metodo"	value="trovaGaraProtocollo"/>
							</c:if>
						
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}' >
								<c:set var="metodo"	value="trovaRicercaMercato"/>
							</c:if>
						
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")}' >
								<c:set var="metodo"	value="trovaTutteLeGare"/>
							</c:if>
						
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")}' >
								<c:set var="metodo"	value="trovaOrdiniNso"/>
								<c:set var="functionId" value="skip" />
							</c:if>
						
							<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GestioneStipulaContratti")}' >
								<c:set var="metodo"	value="trovaStipula"/>
							</c:if>
						
							<c:if test='${gene:checkProt(pageContext,"MENU.VIS.SIMOG-CIG")}' >
								<c:set var="metodo"	value="trovaGaraLotto"/>
							</c:if>
		
							<form name="formfind" action="${contextPath}/pg/ProcessHome.do" method="post">
								<input type="hidden" name="metodo" value="${metodo}" />
								<input type="hidden" name="archFunctionId" value='${functionId}' />
								<table align="left" class="arealayout" style="width: 500">
		
									<jsp:include page="/WEB-INF/pages/commons/browserSupportati.jsp" >
										<jsp:param name="colspan" value="2" />
									</jsp:include>
								
									<tr>
										<td>
											<div class="margin0px titolomaschera">${sessionScope.nomeProfiloAttivo}</div>
											<p>${sessionScope.descProfiloAttivo}</p>
										</td>
									</tr>
									
									<c:if test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista")}' >
										<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni') }">
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "1")}' />
										</c:if>
										
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca in gare</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();">
													<img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" alt="Trova gara" align="top">
												</a>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"MENU.VIS.GARE") and gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-gare")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:trovaGaraAvanzata()" 
														title="Vai alla ricerca avanzata gare">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<c:choose>
														<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AttivaDicituraAffidamenti") }'>
															<c:set var="titoloCrea" value="Crea nuovo affidamento"/>
														</c:when>
														<c:otherwise>
															<c:set var="titoloCrea" value="Crea nuova gara"/>
														</c:otherwise>
													</c:choose>
													<b><a class="link-generico" href="javascript:nuovaGara();"
														title="${titoloCrea}">${titoloCrea}</a></b>
												</td>
											</tr>
										</c:if>
							
										<!-- Scadenzario -->
										<c:if test='${fn:contains(listaOpzioniDisponibili, "OP128#") && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.G_SCADENZ.Calendario")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:calendarioScadenzario('TORN;GARE')"
														title="Scadenzario gare">Scadenzario gare</a></b>
												</td>
											</tr>
										</c:if>
									</c:if>
		
									<!-- Adempimenti L190 -->
									<c:if test='${fn:contains(listaOpzioniDisponibili, "OP130#") && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.L190Adempimenti")}' >
										<tr>
											<td>
												<br/>
												<br/>
													<b><a class="link-generico" href="javascript:apriAdempimenti()"
														title="Gare e contratti - adempimenti Legge 190/2012">Adempimenti Legge 190/2012</a></b>
											</td>
										</tr>
									</c:if>
									
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AutovieIds")}' >
										<c:set var="numIdsUtente" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumeroIdsUtenteFunction", pageContext, idUtente)}' />
							
										<tr>
											<td>
												<br/>
												<br/>
													<b><a class="link-generico" href="javascript:apriListaIds()"
														title=${msgConsultaIds}>${msgConsultaIds}</a></b>
											</td>
										</tr>
									</c:if>
						
		
									<!-- INIZIO - Trova gara per elenco operatori economici (ex Albo fornitori) -->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}' >
										<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni') }">
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "2")}' />
										</c:if>
										
										<tr>
											<td valign="middle">
												<br/>
												<br/>
													<div class="titoloInputGoogle">
														ricerca negli elenchi &nbsp;&nbsp;
														<input type="radio" name="tipoRicerca" value="1" />
														per operatore &nbsp;&nbsp;
														<input type="radio" name="tipoRicerca" value="2" checked="CHECKED" />
														per elenco
													</div>
													<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
													<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
														alt="Trova elenco" align="top"></a>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"MENU.VIS.GARE") and gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-elenchi")}' >
											<tr>
												<td>
													<br/>
													<br/>
														<b><a class="link-generico" href="javascript:trovaGaraAvanzataElencoDitte();"
															title="Vai alla ricerca avanzata elenchi">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
														<b><a class="link-generico" href="javascript:nuovaGaraElencoDitte();"
															title="Crea nuovo elenco operatori economici">Crea nuovo elenco operatori economici</a></b>
												</td>
											</tr>
										</c:if>
									</c:if>
									<!-- FINE - Trova gara per elenco operatori economici (ex Albo fornitori) -->
						
									<!-- INIZIO - Trova Catalogo elettronico -->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}' >
										<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni') }">
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "3")}' />
										</c:if>
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">
													ricerca nei cataloghi &nbsp;&nbsp;
													<input type="radio" name="tipoRicerca" value="1" />
													per operatore &nbsp;&nbsp;
													<input type="radio" name="tipoRicerca" value="2" checked="CHECKED" />
													per catalogo
												</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova catalogo" align="top"></a>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"MENU.VIS.GARE") and gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-cataloghi")}' >
											<tr>
												<td>
													<br/>
													<br/>
														<b><a class="link-generico" href="javascript:trovaGaraAvanzataCataloghi();"
															title="Vai alla ricerca avanzata cataloghi">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
														<b><a class="link-generico" href="javascript:nuovaGaraCatalogo();"
															title="Crea nuovo elenco catalogo elettronico">Crea nuovo catalogo elettronico</a></b>
												</td>
											</tr>
										</c:if>
									</c:if>
									<!-- FINE - Trova Catalogo elettronico -->
						
									<!-- INIZIO - Trova Ricerca di mercato e ODA -->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}' >
										<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni') }">
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "4")}' />
										</c:if>
							
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">
													Filtra per:&nbsp;&nbsp;
													<input type="radio" name="tipoRicerca" id="tipoRicerca1" value="1" />
													<label for="tipoRicerca1">ordine di acquisto</label> &nbsp;&nbsp;
													<input type="radio" name="tipoRicerca" value="2" id="tipoRicerca2" checked="CHECKED" />
													<label for="tipoRicerca2">ricerca di mercato</label>
												</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova ricerca di mercato" align="top"></a>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"MENU.VIS.RICERCHE") and gene:checkProt(pageContext,"SUBMENU.VIS.RICERCHE.Trova-ricerche")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:trovaAvanzataRicercheMercato();"
														title="Vai alla ricerca avanzata ricerche di mercato">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:nuovaRicercaMercato();"
														title="Crea nuova ricerca di mercato">Crea nuova ricerca di mercato</a></b>
												</td>
											</tr>
										</c:if>
									</c:if>
									<!-- FINE - Trova Ricerca di mercato e ODA -->
						
									<!-- INIZIO - Trova AVVISI -->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GAREAVVISI-lista.ApriAvviso")}' >
										<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni') }">
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "5")}' />
										</c:if>
										
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca avvisi</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova avviso" align="top"></a>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"MENU.VIS.GARE") and gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-avvisi")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:trovaAvvisoAvanzato();"
														title="Vai alla ricerca avanzata avvisi">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:nuovoAvviso();"
														title="Crea nuovo avviso">Crea nuovo avviso</a></b>
												</td>
											</tr>
										</c:if>
									</c:if>
									<!-- FINE - Trova AVVISI -->
						
									<!-- INIZIO - Ordini NSO -->
									<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")}' >
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca in ordini &nbsp;&nbsp;</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova ordine" align="top"></a>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-ordiniNso")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:trovaOrdineNsoAvanzata();"
														title="Vai alla ricerca avanzata degli ordini">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:nuovoOrdineNso();"
														title="Crea nuovo ordine">Crea nuovo ordine</a></b>
												</td>
											</tr>
								
											<%-- Conteggio Comunicazioni Non lette per gli Ordini --%>
											<c:set var="profiloRicerca" value="true" scope="request"/> 
											<c:set var="filtroUffint" value="${sessionScope.uffint}" scope="request"/>
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "9")}' />
											<%-- Conteggio Comunicazioni Non lette per gli Ordini FINE--%>
										</c:if>
									</c:if>
									<!-- FINE - Trova ordini -->
									
									<!-- INIZIO - Gestione Contratti -->
									<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GestioneStipulaContratti")}' >
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca in stipule &nbsp;&nbsp;</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton()"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova stipula" align="top"></a>
											</td>
										</tr>

										<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.STIPULE.Trova-stipula")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:trovaStipulaAvanzata();"
														title="Vai alla ricerca avanzata delle stipule">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:nuovaStipula();"
														title="Crea nuova stipula">Crea nuova stipula</a></b>
												</td>
											</tr>
											
											<%-- Conteggio Comunicazioni Non lette per le Stipule --%>
											<c:set var="profiloRicerca" value="true" scope="request"/> 
											<c:set var="filtroUffint" value="${sessionScope.uffint}" scope="request"/>
											<!-- Per la lettura delle comunicazioni l'utilizzo della view  V_GARE_STIPULA crea rallentamenti, per ovviare si e' deciso di usare direttamente l'entita G1STIPULA-->
											<c:set var="filtroLivelloUtenteStipuleFS12" value='${fn:replace(filtroLivelloUtenteStipule,"V_GARE_STIPULA","G1STIPULA")}' scope="request"/>
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "10")}' />
											<%-- Conteggio Comunicazioni Non lette per le Stipule FINE--%>
										</c:if>
									</c:if>
									<!-- FINE - Gestione Contratti -->
						
									<!-- INIZIO - ricerca di tutte le gare -->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")}' >
										<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni') }">
											<c:set var="profiloRicerca" value="true" scope="request"/> 
											<c:set var="filtroProfilo" value=" AND V_GARE_PROFILO.CODPROFILO IN (SELECT COD_PROFILO FROM W_ACCPRO WHERE ID_ACCOUNT = ${idUtente})" scope="request"/>
											<c:set var="filtroLivelloUtente" value="${filtroLivelloUtenteProfiloRicerca}" scope="request"/> 
											<c:if test="${uffintAbilitata eq 1 and !amministratore}">
												<c:set var="filtroUffint" value=" AND CENINT IN (select codein from usr_ein where syscon = ${idUtente} )" scope="request"/>
											</c:if>
											<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "count", "8")}' />
										</c:if>
							
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca in gare e avvisi &nbsp;&nbsp;</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova elenco" align="top"></a>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-profilo")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:trovaGaraAvanzataRicerca();"
														title="Vai alla ricerca avanzata gare e avvisi">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
									</c:if>
									<!-- FINE - ricerca di tutte le gare -->
						
									<!-- Personalizzazione ASPI - profilo Protocollo-->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_NSCAD-lista.ApriGare")}' >
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca in gare</div>
												<input type="text" name="findstrProtocollo" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova gara" align="top"></a>
											</td>
										</tr>

										<tr>
											<td>
												<br/>
												<br/>
												<b><a class="link-generico" href="javascript:trovaGaraAvanzataProtocollo()"
													title="Vai alla ricerca avanzata gare">Ricerca avanzata</a></b>
											</td>
										</tr>
									</c:if>
									<!-- Fine Personalizzazione ASPI - profilo Protocollo-->
						
									<!-- Personalizzazione ASPI - profilo Service Affidamenti-->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_DITTE_PRIT-lista.ApriImprese")}' >
										<tr>
											<td valign="middle"><br>
												<select name="tipoDoc" id="tipoDoc" onchange="javascript:formFindButton();" >
							      					<option value=""/>
							      					<c:forEach items="${listaTipologiaDoc}" var="tipologia">
						    			  				<option value="${tipologia.tipoTabellato}">${tipologia.descTabellato}</option>
						    			  			</c:forEach>
						    					</select>
						    					<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
						    					alt="Trova impresa" align="top" ></a>
											</td>
										</tr>
										
										<tr>
											<td>
												<br/>
												<br/>
												<b><a class="link-generico" href="javascript:trovaImpreseAvanzata()"
													title="Vai alla ricerca avanzata gare">Ricerca avanzata</a></b>
											</td>
										</tr>
									</c:if>
									<!-- Fine Personalizzazione ASPI - Service Affidamenti-->
						
									<!-- Personalizzazione prefettura Caserta - accesso alla lista imprese per accertamenti antimafia -->
									<c:if test='${gene:checkProtFunz(pageContext,"ALT","ListaDitteAntimafia")}'>
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca in ditte</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();"><img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" 
													alt="Trova Ditta" align="top"></a>
											</td>
										</tr>
										
										<tr>
											<td>
												<br/>
												<br/>
												<b><a class="link-generico" href="javascript:trovaDittaAntimafiaAvanzata()"
													title="Vai alla ricerca avanzata ditte">Ricerca avanzata</a></b>
											</td>
										</tr>
										
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.INS.GENE.ImprLista.LISTANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:nuovaDittaAntimafia();"
														title="Crea nuova ditta">Crea nuova ditta</a></b>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.importXMLAntimafia")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:importaAntimafia();"
														title="Importazione dati per verifica interdizione ditte o accertamento antimafia">
														Importazione dati per verifica interdizione o accertamento antimafia
													</a></b>
												</td>
											</tr>
										</c:if>
									</c:if>
									<!-- Fine personalizzazione prefettura Caserta -->
						
									<c:if test='${isIntegrazionePortaleAlice eq "true"}' >
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisiciRegistrazioniPortale")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<c:if test='${not fn:contains(msgRegistrazioni, "Non ci sono richieste")}'>
														<b><a class="link-generico" href="javascript:listaComunicazioni(2);" title="${msgRegistrazioni }">
													</c:if>
													${msgRegistrazioni}
													<c:if test='${not fn:contains(msgRegistrazioni, "Non ci sono richieste")}'>
														</a>
													</c:if>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisisciAggiornamentiPortale")}' >
											<tr>
												<td>
													<br/>
													<br/>
													<c:if test='${not fn:contains(msgAggiornamenti, "Non ci sono richieste")}'>
														<b><a class="link-generico" href="javascript:listaComunicazioni(3);" title="${msgAggiornamenti }">
													</c:if>
													${msgAggiornamenti}
													<c:if test='${not fn:contains(msgAggiornamenti, "Non ci sono richieste")}'>
														</a></b>
													</c:if>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisiciIscrizioniElencoPortale")
												and (gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")
												or gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare"))}' >
											<tr>
												<td>
													<br/>
													<br/>
													<c:if test='${not fn:contains(msgRichieste, "Non ci sono richieste")}'>
														<b><a class="link-generico" href="javascript:listaComunicazioni(1);" title="${msgRichieste }">
													</c:if>
													${msgRichieste}
													<c:if test='${not fn:contains(msgRichieste, "Non ci sono richieste")}'>
														</a></b>
													</c:if>
												</td>
											</tr>
										</c:if>
									</c:if>
						
									<c:if test="${not empty conteggioComunicazioniDaLeggere and isIntegrazionePortaleAlice and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni')}">
										<tr>
											<td>
												<br/>
												<br/>
												<c:if test="${conteggioComunicazioniDaLeggere ne 0}">
													<b><a class="link-generico" href="javascript:listaComunicazioni(4);" title="Vai alla lista delle comunicazioni ricevute non lette">
												</c:if>
													<c:choose>
														<c:when test="${conteggioComunicazioniDaLeggere eq 1 }">
															C'� ${conteggioComunicazioniDaLeggere} comunicazione non letta	
														</c:when>
														<c:when test="${conteggioComunicazioniDaLeggere > 1 }">
															Ci sono ${conteggioComunicazioniDaLeggere} comunicazioni non lette	
														</c:when>
														<c:when test="${conteggioComunicazioniDaLeggere eq 0 }">
															Non ci sono comunicazioni non lette	
													</c:when>
												</c:choose>
												<c:if test="${conteggioComunicazioniDaLeggere ne 0}">
													</a></b>
												</c:if>
											</td>
										</tr>
									</c:if>
						
									<!-- Personalizzazione Elenchi-->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare") and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.NotificaRinnoviIscrizioneElenco")}' >
										<c:set var="tmp1" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNumRinnoviVerificareFunction", pageContext, filtroLivelloUtenteElencoOperatori,sessionScope.uffint,"V_GARE_ELEDITTE")}' />
									
										<c:if test="${!empty listaRichiesteRinnovi }">	
											<tr>
												<td>
													<table class="arealayout">
														<br/>
														<br/>
														<tr>
															<td>
																<img src="${pageContext.request.contextPath}/img/rinnovi.png"/>&nbsp;
															</td>
															
															<td>
																Sono presenti operatori per cui deve essere verificata la richiesta di rinnovo:
															</td>
														</tr>
														
														<tr style="padding: 5px">
															<td colspan="2" style="padding: 5px">
																<ul>
																	<c:forEach items="${listaRichiesteRinnovi}" var="richiestaRinnovo" varStatus="status" >
																		<b><li style="margin-bottom: 5px">		
																			<a class="link-generico" href="javascript:visualizzaElenco('${richiestaRinnovo[1] }');" 
																					title="${richiestaRinnovo[0] }">${richiestaRinnovo[0] }</a>
																		</li></b>
																	</c:forEach>
																</ul>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</c:if>
									</c:if>
						
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare") and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.NotificaAggiornamentiCategorieElenco")}' >
										<c:set var="tmp2" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNumModificheInAttesaFunction", pageContext, filtroLivelloUtenteElencoOperatori,sessionScope.uffint,"V_GARE_ELEDITTE")}' />
								
										<c:if test="${!empty listaModificheInAttesa }">	
											<tr>
												<td>
													<table class="arealayout">
														<br/>
														<br/>
														<tr>
															<td>
																<img src="${pageContext.request.contextPath}/img/notificaAggCategorie.png"/>&nbsp;
															</td>
											
															<td>
																Sono presenti operatori con richieste di modifica alle categorie d'iscrizione da confermare:
															</td>
														</tr>
														
														<tr>
															<td colspan="2" style="padding: 5px">
																<ul>
																	<c:forEach items="${listaModificheInAttesa}" var="modificaInAttesa" varStatus="status" >
																		<b><li style="margin-bottom: 5px">	
																			<a class="link-generico" href="javascript:visualizzaElenco('${modificaInAttesa[1] }');" 
																					title="${modificaInAttesa[0] }">${modificaInAttesa[0] }</a>
																		</li></b>
																	</c:forEach>
																</ul>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</c:if>
									</c:if>
						
									<!-- Personalizzazione Cataloghi elettronici-->
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}' >
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.NotificaVerificaProdottiCatalogo")}' >
											<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumProdottiVerificareFunction", pageContext, filtroLivelloUtenteCataloghi,sessionScope.uffint)}' />
								
											<c:if test="${!empty listaRichiesteProdotti }">	
												<tr>
													<td>
														<table class="arealayout">
															<br/>
															<br/>
															<tr>
																<td>
																	<img src="${pageContext.request.contextPath}/img/notificaAggCategorie.png"/>&nbsp;
																</td>
													
																<td>
																	Sono presenti prodotti in attesa di verifica conformit�:
																</td>
															</tr>
															
															<tr>
																<td colspan="2" style="padding: 5px">
																	<ul>
																		<c:forEach items="${listaRichiesteProdotti}" var="richiestaProdotto" varStatus="status" >
																			<b><li style="margin-bottom: 5px">	
																				<a class="link-generico" href="javascript:visualizzaCatalogo('${richiestaProdotto[1] }');" 
																						title="${richiestaProdotto[0] }">${richiestaProdotto[0] }</a>
																			</li></b>
																		</c:forEach>
																	</ul>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</c:if>
										</c:if>
										
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.NotificaRinnoviIscrizioneElenco")}' >
											<c:set var="tmp1" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNumRinnoviVerificareFunction", pageContext, filtroLivelloUtenteCataloghi,sessionScope.uffint,"V_GARE_CATALDITTE")}' />
								
											<c:if test="${!empty listaRichiesteRinnovi }">	
												<tr>
													<td>
														<table class="arealayout">
															<br/>
															<br/>
															<tr>
																<td>
																	<img src="${pageContext.request.contextPath}/img/rinnovi.png"/>&nbsp;
																</td>
																
																<td>
																	Sono presenti operatori per cui deve essere verificata la richiesta di rinnovo:
																</td>
															</tr>
															
															<tr style="padding: 5px">
																<td colspan="2" style="padding: 5px">
																	<ul>
																		<c:forEach items="${listaRichiesteRinnovi}" var="richiestaRinnovo" varStatus="status" >
																			<b><li style="margin-bottom: 5px">		
																				<a class="link-generico" href="javascript:visualizzaCatalogo('${richiestaRinnovo[1] }');" 
																						title="${richiestaRinnovo[0] }">${richiestaRinnovo[0] }</a>
																			</li></b>
																		</c:forEach>
																	</ul>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</c:if>
										</c:if>
										
										<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.NotificaAggiornamentiCategorieElenco")}' >
											<c:set var="tmp2" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNumModificheInAttesaFunction", pageContext, filtroLivelloUtenteCataloghi,sessionScope.uffint,"V_GARE_CATALDITTE")}' />
								
											<c:if test="${!empty listaModificheInAttesa }">	
												<tr>
													<td>
														<table class="arealayout">
															<br/>
															<br/>
															<tr>
																<td>
																	<img src="${pageContext.request.contextPath}/img/notificaAggCategorie.png"/>&nbsp;
																</td>
															
																<td>
																	Sono presenti operatori per cui ci sono aggiornamenti alle categorie d'iscrizione da confermare:
																</td>
															</tr>
														
															<tr>
																<td colspan="2" style="padding: 5px">
																	<ul>
																		<c:forEach items="${listaModificheInAttesa}" var="modificaInAttesa" varStatus="status" >
																			<b><li style="margin-bottom: 5px">	
																				<a class="link-generico" href="javascript:visualizzaCatalogo('${modificaInAttesa[1] }');" 
																						title="${modificaInAttesa[0] }">${modificaInAttesa[0] }</a>
																			</li></b>
																		</c:forEach>
																	</ul>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</c:if>
										</c:if>
									</c:if>
									
									<c:set var="integrazioneProgrammazione" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneProgrammazioneFunction", pageContext)}'/>
									
									<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.IntegrazioneProgrammazione') && integrazioneProgrammazione eq '1' && gene:checkProt(pageContext,'MENU.VIS.GARE') && gene:checkProt(pageContext,'SUBMENU.VIS.GARE.Trova-gare')}">
									<c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "codiceApplicativoNonCollegato", null,null,null)}' />
									
										<tr>
											<td>
												<br/>
												<br/>
												<c:if test="${conteggioRDARDI > 0}">
													<b><a class="link-generico" href="javascript:apriListaRdaRdi();" title="Vai alla lista delle RDA/RDI da prendere in carico">
												</c:if>
													<c:choose>
														<c:when test="${conteggioRDARDI eq 1 }">
															� presente n. ${conteggioRDARDI} RdA/RdI da prendere in carico
														</c:when>
														<c:when test="${conteggioRDARDI > 1 }">
															Sono presenti n. ${conteggioRDARDI} RdA/RdI da prendere in carico
														</c:when>
														<c:when test="${conteggioRDARDI eq 0 }">
															Non sono presenti RdA/RdI da prendere in carico
													</c:when>
													<c:when test="${conteggioRDARDI eq -1}">
															<b>ATTENZIONE:</b> integrazione programmazione RdA/RdI non disponibile
															<c:if test="${not empty errorMsgRda}"><br>(${errorMsgRda})</c:if>
													</c:when>
												</c:choose>
												<c:if test="${conteggioRDARDI ne 0}">
													</a></b>
												</c:if>
											</td>
										</tr>
									</c:if>
						
									<!-- Personalizzazione AMA -->
									<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GestioneAlboComponentiCommissione")}'>
										<c:set var="numAlbiComponentiCommissione" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetNumAlbiComponentiCommissioneFunction", pageContext)}' />
								
										<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro="homeAlboCommissione"/>
										<c:choose>
											<c:when test='${empty numAlbiComponentiCommissione || numAlbiComponentiCommissione eq "0"}'>
												<tr>
													<td>
														<br/>
														<br/>
														<b><a class="link-generico" href="javascript:alboComponentiCommisione(1)"
																title="Crea elenco componenti commissione">Crea elenco componenti commissione</a></b>
													</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td>
														<br/>
														<br/>
														<b><a class="link-generico" href="javascript:alboComponentiCommisione(2)"
																title="Accedi ad elenco componenti commissione">Accedi ad elenco componenti commissione</a></b>
													</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</c:if>
									<!-- Fine personalizzazione AMA -->
						
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.Homepage-EseguiReport")}' >
										<tr>
											<td>
												<br/>
												<br/>
												<b><a class="link-generico" href="${contextPath}/geneGenric/ListaRicerchePredefinite.do"
														title="Vai alla lista dei report predefiniti">Esegui un report</a></b>
											</td>
										</tr>
									</c:if>
						
									<!--  Home page richiesta CIG -->
									<c:if test='${gene:checkProt(pageContext,"MENU.VIS.SIMOG-CIG")}'>
										<tr>
											<td valign="middle">
												<br/>
												<br/>
												<div class="titoloInputGoogle">ricerca richieste CIG e smartCIG</div>
												<input type="text" name="findstr" size="50" class="testo-cerca-oggetto" align="top" />
												<a href="javascript:formFindButton();">
													<img src="${contextPath}/img/${applicationScope.pathImg}trova_oggetto.png" alt="Ricerca richieste CIG e smartCIG" align="top">
												</a>
											</td>
										</tr>
									
										<tr>
											<td>
												<span style="color: #707070;">
													<i>* Questa ricerca rapida considera solamente le richieste non ancora pubblicate</i>
												</span>
											</td>
										</tr>
									
										<c:if test='${gene:checkProt(pageContext, "SUBMENU.VIS.SIMOG-CIG.Trova-gare-lotti")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:trovaGaraLottoAvanzata()"
															title="Ricerca avanzata delle richieste CIG e smartCIG">Ricerca avanzata</a></b>
												</td>
											</tr>
										</c:if>
											
										<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
											<tr>
												<td>
													<br/>
													<br/>
													<b><a class="link-generico" href="javascript:nuovaAnagraficaGara();"
															title="Crea nuova anagrafica gara">Crea nuova anagrafica gara</a></b>
												</td>
											</tr>
										</c:if>
										<c:if test='${abilitazioneCollaboratori}'>
										<tr>
											<td>
												<br/>
												<br/>
												<b><a class="link-generico" href="javascript:gestisciCredenzialiSIMOG(1)"
														title="Imposta credenziali RUP">Imposta credenziali RUP</a></b>
											</td>
										</tr>
										<tr>
											<td>
												<br/>
												<br/>
												<b><a class="link-generico" href="javascript:apriListaCollaboratori()"
														title="Gestione collaborazioni RUP">Gestione collaborazioni RUP</a></b>
											</td>
										</tr>
										</c:if>
									</c:if>
						
									<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.Riferimenti-Informazioni")}' >
										<tr>
											<td align="center">
												<br/>
												<br/>
												<br/>
												<br/>
												<p style="color: #707070;">
													<jsp:include page="/WEB-INF/pages/gene/login/infoCustom.jsp"/>					
												</p>
											</td>
										</tr>
									</c:if>
								</table>
							</form>
					
							<c:if test='${!gene:checkProt(pageContext,"MENU.VIS.SIMOG-CIG")}'>
								<form name="listaNuovo" action="${contextPath}/Lista.do" method="post">
									<input type="hidden" name="jspPath" value="/WEB-INF/pages/gare/v_gare_torn/v_gare_torn-lista.jsp" /> 
									<input type="hidden" name="jspPathTo" value="" /> 
									<input type="hidden" name="activePage" value="" /> 
									<input type="hidden" name="isPopUp" value="0" /> 
									<input type="hidden" name="numeroPopUp" value="" /> 
									<input type="hidden" name="metodo" value="nuovo" /> 
									<input type="hidden" name="entita" value="PERI" /> 
									<input type="hidden" name="gestisciProtezioni" value="1" />
								</form>
							
								<form name="apriPagina" action="${contextPath}/ApriPagina.do" method="post">
									<input type="hidden" name="href" value="gare/v_gare_torn/v_gare_torn-lista.jsp" />
								</form>
							</c:if>
					
							<c:if test='${gene:checkProt(pageContext,"MENU.VIS.SIMOG-CIG")}'>
								<form name="listaNuovo" action="${contextPath}/Lista.do" method="post">
									<input type="hidden" name="jspPath" value="/WEB-INF/pages/w3/w3gara/w3gara-lista.jsp" /> 
									<input type="hidden" name="jspPathTo" value="" /> 
									<input type="hidden" name="activePage" value="" /> 
									<input type="hidden" name="isPopUp" value="0" /> 
									<input type="hidden" name="numeroPopUp" value="" /> 
									<input type="hidden" name="metodo" value="nuovo" /> 
									<input type="hidden" name="entita" value="W3GARA" /> 
									<input type="hidden" name="gestisciProtezioni" value="1" />
								</form>
						
								<form name="apriPagina" action="${contextPath}/ApriPagina.do" method="post">
									<input type="hidden" name="href" value="w3/w3gara/w3gara-lista.jsp" />
								</form>		
							</c:if>
						</div>
		
						<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
						<iframe class="gene" id="iframepopmenu" ></iframe>
						<div id="popmenu" class="popupmenuskin"
							onMouseover="highlightMenuPopup(event,'on');"
							onMouseout="highlightMenuPopup(event,'off');"></div>
					</td>
				</tr>
		
				<tr>
					<td COLSPAN="2">
						<div id="footer">
							<jsp:include page="/WEB-INF/pages/commons/footer.jsp" />
						</div>
					</td>
				</tr>
				
				<c:choose>
					<c:when test='${gene:checkProt(pageContext,"MENU.VIS.SIMOG-CIG")}'>
						<c:set var="fromGare" value="0" scope="session"/>
					</c:when>
					<c:otherwise>
						<c:set var="fromGare" value="1" scope="session"/>
					</c:otherwise>
				</c:choose>
				
		</TD>
	</TR>

			</tbody>
		</table>
		
		<div id="contenitorefeedrss" style="position:absolute; left:660px; top:110px;" >
			<jsp:include page="/WEB-INF/pages/commons/feedRss.jsp" />
		</div>
	</body>
</html>