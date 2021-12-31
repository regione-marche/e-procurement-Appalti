<%
/*
 * Created on: 05-04-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form di ricerca dei tecnici */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:choose>
	<c:when test='${not empty param.isGaraElenco}'>
		<c:set var="isGaraElenco" value="${param.isGaraElenco}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraElenco" value="${isGaraElenco}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isGaraCatalogo}'>
		<c:set var="isGaraCatalogo" value="${param.isGaraCatalogo}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraCatalogo" value="${isGaraCatalogo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.dittePerPagina}'>
		<c:set var="dittePerPagina" value="${param.dittePerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="dittePerPagina" value="${dittePerPagina}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipologia}'>
		<c:set var="tipologia" value="${param.tipologia}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipologia" value="${tipologia}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipoele}'>
		<c:set var="tipoele" value="${param.tipoele}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoele" value="${tipoele}" />
	</c:otherwise>
</c:choose>

<c:if test="${isGaraElenco eq 1 or isGaraCatalogo eq 1}">
		<c:set var="isVisibleDataScadenzaIscriz" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVisibleDataScadenzaIscrizFunction",pageContext,ngara)}' />
		<c:set var="valiscr" value='${valiscr}' />
	</c:if>


<c:if test="${isGaraElenco ne 1 and isGaraCatalogo ne 1}">
	<c:set var="compreq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCompreqFunction", pageContext, codiceGara)}' />
</c:if>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<c:set var="urlWs80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>
<c:if test='${not empty urlWs80 and urlWs80 ne "" and not empty uffint and uffint ne ""}'>
	<c:set var="gateway" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", gatewayArt80)}'/>
</c:if>

<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	
	
	<gene:redefineInsert name="head" >

		<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.jstree.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.jstree.categorie.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.highlight.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.formatCurrency-1.4.0.js"></script>
	
		<style type="text/css">
			
			.highlight {
			    background-color: #FFDB05;
			    -moz-box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4); /* FF3.5+ */
			    -webkit-box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4); /* Saf3.0+, Chrome */
			    box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4); /* Opera 10.5+, IE 9.0 */
			}
		
		    li[tiponodo='CA'].jstree-checked > a .jstree-checkbox, li[tiponodo='CAISARCHI'].jstree-checked > a .jstree-checkbox, li[tiponodo='PA'].jstree-checked > a .jstree-checkbox
		    {
		        background:url("${contextPath}/css/jquery/jstree/themes/classic/d_r.gif") -38px -19px no-repeat !important;
		    }
		 
		    li[tiponodo='CA'].jstree-unchecked > a .jstree-checkbox, li[tiponodo='CAISARCHI'].jstree-unchecked > a .jstree-checkbox, li[tiponodo='PA'].jstree-unchecked > a .jstree-checkbox
		    {
		        background:url("${contextPath}/css/jquery/jstree/themes/classic/d_r.gif") -2px -19px no-repeat !important;
		    }
		
		    li[tiponodo='CA'].jstree-undetermined > a .jstree-checkbox, li[tiponodo='CAISARCHI'].jstree-undetermined > a .jstree-checkbox, li[tiponodo='PA'].jstree-undetermined > a .jstree-checkbox
		    {
		        background:url("${contextPath}/css/jquery/jstree/themes/classic/d_r.gif") -20px -19px no-repeat !important;
		    }
		    
		    li[tiponodo='CISARCHI'].jstree-checked > a .jstree-checkbox
		    {
		        background:url("${contextPath}/css/jquery/jstree/themes/classic/d_g.gif") -38px -19px no-repeat !important;
		    }
		 
		    li[tiponodo='CISARCHI'].jstree-unchecked > a .jstree-checkbox
		    {
		        background:url("${contextPath}/css/jquery/jstree/themes/classic/d_g.gif") -2px -19px no-repeat !important;
		    }
		
		    li[tiponodo='CISARCHI'].jstree-undetermined > a .jstree-checkbox
		    {
		        background:url("${contextPath}/css/jquery/jstree/themes/classic/d_g.gif") -20px -19px no-repeat !important;
		    }
			
			label.error {
				margin-left: 0px;
				margin-right: 0px;
				color: red;
				font-size: 9px;
			}
			
			.error {
				color: red;
			}
			
		</style>
	</gene:redefineInsert>
	
	<%-- Variabile che serve da indice per definire l'id dei campi fittizi, visto che nella pagina di trova tutti i campi in automatico hanno un nome del tipo Campo + progressivo 
		IMPORTANTE: aggiungendo campi nella maschera, incrementare la variabile 
	--%>
	<c:set var="numCampi" value="-1"/>
	
	<gene:redefineInsert name="corpo">
  		<gene:formTrova entita="DITG"  >
  			
  			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroCodimp and param.resetCampi ne '1'}">
					<gene:campoTrova campo="CODIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroCodimp}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="CODIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" />
					<c:set var="numCampi" value="${numCampi + 1 }"/>		
				</c:otherwise>
			</c:choose>  			
			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1 ) and sessionScope.filtroDitteLocale ne 'si' and fn:contains(sessionScope.campoFiltroHome,'NOMIMO') and param.resetCampi ne '1'}">
					<gene:campoTrova campo="NOMIMO" defaultValue="${ sessionScope.valoreFiltroHome}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroNomimo and param.resetCampi ne '1'}">
					<gene:campoTrova campo="NOMIMO" defaultValue="${ sessionScope.valoreFiltroNomimo}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="NOMIMO" />		
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroCF and param.resetCampi ne '1'}">
					<gene:campoTrova campo="CFIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroCF}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="CFIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" />
					<c:set var="numCampi" value="${numCampi + 1 }"/>		
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and fn:contains(sessionScope.campoFiltroHome,'PIVIMP') and param.resetCampi ne '1'}">
					<gene:campoTrova campo="PIVIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroHome}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroPIVA and param.resetCampi ne '1'}">
					<gene:campoTrova campo="PIVIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroPIVA}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="PIVIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>		
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroTipimp and param.resetCampi ne '1'}">
					<gene:campoTrova campo="TIPIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroTipimp}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="TIPIMP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" />
					<c:set var="numCampi" value="${numCampi + 1 }"/>		
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroIsmpmi and param.resetCampi ne '1'}">
					<gene:campoTrova campo="ISMPMI" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroIsmpmi}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="ISMPMI" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" />
					<c:set var="numCampi" value="${numCampi + 1 }"/>		
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroEmail and param.resetCampi ne '1'}">
					<gene:campoTrova campo="EMAIIP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroEmail}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="EMAIIP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" />
					<c:set var="numCampi" value="${numCampi + 1 }"/>		
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroPec and param.resetCampi ne '1'}">
					<gene:campoTrova campo="EMAI2IP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" defaultValue="${ sessionScope.valoreFiltroPec}"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="EMAI2IP" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" />
					<c:set var="numCampi" value="${numCampi + 1 }"/>		
				</c:otherwise>
			</c:choose>
			
			
			<input type="hidden" name="azione" id="azione" value="" />
			<c:if test="${isGaraElenco eq 1 or isGaraCatalogo eq 1}">
				<c:choose>
					<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroCodCat and param.resetCampi ne '1'}">
						<gene:campoTrova campo="CAISIM" title="Codice categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP " defaultValue="${ sessionScope.valoreFiltroCodCat}"/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:when>
					<c:otherwise>
						<gene:campoTrova campo="CAISIM" title="Codice categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP "/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>	
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroDescCat and param.resetCampi ne '1'}">
						<gene:campoTrova campo="DESCAT1" title="Descrizione categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP " defaultValue="${ sessionScope.valoreFiltroDescCat}"/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:when>
					<c:otherwise>
						<gene:campoTrova campo="DESCAT1" title="Descrizione categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP "/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>	
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroTipcat and param.resetCampi ne '1'}">
						<gene:campoTrova campo="TIPLAVG" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP " defaultValue="${ sessionScope.valoreFiltroTipcat}"/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:when>
					<c:otherwise>
						<gene:campoTrova campo="TIPLAVG" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP "/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>	
					</c:otherwise>
				</c:choose>
				<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.SUPNUMCLASS")}'>
					<c:choose>
						<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroNumclass and param.resetCampi ne '1'}">
							<gene:campoTrova campo="NUMCLASS" title="Classifica categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP " gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoClassificaCategoriaRicerca" defaultValue="${ sessionScope.valoreFiltroNumclass}"/>
							<c:set var="numCampi" value="${numCampi + 1 }"/>
						</c:when>
						<c:otherwise>
							<gene:campoTrova campo="NUMCLASS" title="Classifica categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP " gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoClassificaCategoriaRicerca"/>
							<c:set var="numCampi" value="${numCampi + 1 }"/>	
						</c:otherwise>
					</c:choose>
					<gene:fnJavaScriptTrova funzione="gestioneNumcla('#Campo11#')" elencocampi="Campo11" esegui="true" />
				</c:if>
				
				<c:choose>
					<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroAbilitaz and param.resetCampi ne '1'}">
						<gene:campoTrova campo="ABILITAZ" defaultValue="${ sessionScope.valoreFiltroAbilitaz}"/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:when>
					<c:otherwise>
						<gene:campoTrova campo="ABILITAZ" />		
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroDricind and param.resetCampi ne '1'}">
						<gene:campoTrova campo="DRICIND" title="Data domanda iscrizione" defaultValue="${ sessionScope.valoreFiltroDricind}"/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:when>
					<c:otherwise>
						<gene:campoTrova campo="DRICIND" title="Data domanda iscrizione"/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>		
					</c:otherwise>
				</c:choose>
				
				<c:if test="${tipologia ne 3 }">
					<c:set var="chiaveTmp" value="GARE.NGARA=T:${param.ngara }" />
					<c:set var="tipologia" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGarealboFunction", pageContext, chiaveTmp)}' scope="request"/>
					<c:if test="${tipologia ne 3 }">
						<c:choose>
							<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroDscad and param.resetCampi ne '1'}">
								<gene:campoTrova campo="DSCAD" defaultValue="${ sessionScope.valoreFiltroDscad}"/>
								<c:set var="numCampi" value="${numCampi + 1 }"/>
							</c:when>
							<c:otherwise>
								<gene:campoTrova campo="DSCAD" />		
								<c:set var="numCampi" value="${numCampi + 1 }"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroStrin and param.resetCampi ne '1'}">
								<gene:campoTrova campo="STRIN" title="Stato verifica rinnovo" defaultValue="${ sessionScope.valoreFiltroStrin}"/>
								<c:set var="numCampi" value="${numCampi + 1 }"/>
							</c:when>
							<c:otherwise>
								<gene:campoTrova campo="STRIN" title="Stato verifica rinnovo" />		
								<c:set var="numCampi" value="${numCampi + 1 }"/>
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:choose>
						<c:when test='${dbms eq "POS"}'>
							<gene:campoTrova title="Data scadenza iscrizione" computed="true" campo="${gene:getDBFunction(pageContext,'isnull','DSCAD;DRICIND')}::date + ${valiscr } - 1" definizione="D;0;;DATA_ELDA"/>
							<c:set var="numCampi" value="${numCampi + 1 }"/>
						</c:when>
						<c:otherwise>
							<gene:campoTrova title="Data scadenza iscrizione" computed="true" campo="${gene:getDBFunction(pageContext,'isnull','DSCAD;DRICIND')} + ${valiscr } - 1" definizione="D;0;;DATA_ELDA"/>
							<c:set var="numCampi" value="${numCampi + 1 }"/>
						</c:otherwise>
					</c:choose>
					
				</c:if>
					
				<gene:campoTrova campo="SITUAZDOCI" entita="IMPRDOCG" where="DITG.DITTAO = IMPRDOCG.CODIMP AND DITG.NGARA5 = IMPRDOCG.NGARA" title="Stato verifica documenti richiesti"/>
				<c:set var="numCampi" value="${numCampi + 1 }"/>
			
				<gene:campoTrova campo="DATASCADENZA" entita="IMPRDOCG" where="DITG.DITTAO = IMPRDOCG.CODIMP AND DITG.NGARA5 = IMPRDOCG.NGARA" title="Data scadenza documenti richiesti"/>
				<c:set var="numCampi" value="${numCampi + 1 }"/>
			
			</c:if>

			<c:if test="${isGaraCatalogo eq 1}">
				<gene:campoTrova campo="STATO"  entita="MEISCRIZPROD" where="DITG.NGARA5 = MEISCRIZPROD.NGARA and DITG.DITTAO = MEISCRIZPROD.CODIMP and DITG.CODGAR5 = MEISCRIZPROD.CODGAR" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoStatoProdottiME"/>
				<c:set var="numCampi" value="${numCampi + 1 }"/>
			</c:if>
			
			<c:if test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and fn:contains(listaOpzioniDisponibili, 'OP114#')}">
				<c:choose>
					<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroCoordsic and param.resetCampi ne '1'}">
						<gene:campoTrova campo="COORDSIC" defaultValue="${sessionScope.valoreFiltroCoordsic}"/>
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:when>
					<c:otherwise>
						<gene:campoTrova campo="COORDSIC" />
						<c:set var="numCampi" value="${numCampi + 1 }"/>
					</c:otherwise>
				</c:choose>
			</c:if>
			
			<c:choose>
				<c:when test="${sessionScope.filtroDitteLocale ne 'si' and !empty sessionScope.valoreFiltroAltnot and param.resetCampi ne '1'}">
					<gene:campoTrova campo="ALTNOT" computed="true" title="Note operatore" defaultValue="${sessionScope.valoreFiltroAltnot}" definizione="T60;0;;;G1ALTNOT"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="ALTNOT" computed="true" title="Note operatore" definizione="T60;0;;;G1ALTNOT"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:otherwise>
			</c:choose>
			
			<gene:campoTrova campo="DURC_REG"  entita="V_IMPR_VERIFICA" from="IMPR" where="IMPR.CODIMP =DITG.DITTAO AND V_IMPR_VERIFICA.CODIMP=IMPR.CODIMP" />
			<c:set var="numCampi" value="${numCampi + 1 }"/>
			
			<gene:campoTrova campo="WL_REG"  entita="V_IMPR_VERIFICA" from="IMPR" where="IMPR.CODIMP =DITG.DITTAO AND V_IMPR_VERIFICA.CODIMP=IMPR.CODIMP" />
			<c:set var="numCampi" value="${numCampi + 1 }"/>

			
			<c:choose>
				<c:when test="${gateway eq '1' }">
					<gene:campoTrova campo="STATO" id="ART80_STATO" entita="ART80" where="ART80.CODIMP=DITG.DITTAO AND ART80.CODEIN='${uffint }'" title="Esito verifica requisiti art.80"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
				<c:when test='${not empty urlWs80 and urlWs80 ne ""}'>
					<gene:campoTrova campo="ART80_STATO" id="ART80_STATO" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" title="Esito verifica requisiti art.80"/>
					<c:set var="numCampi" value="${numCampi + 1 }"/>
				</c:when>
			</c:choose>
			
			<c:if test="${isGaraElenco eq 1 or isGaraCatalogo eq 1  or compreq eq '1'}">
				<gene:campoTrova campo="ESTIMP" title="Sorteggiata per verifica ${gene:if(compreq eq '1', 'requisiti', 'documenti')}?"/>
				<c:set var="numCampi" value="${numCampi + 1 }"/>
			</c:if>
			
			<c:if test="${isGaraElenco eq 1 or isGaraCatalogo eq 1 }">
				<gene:campoTrova campo="DSORTEV" title="Data sorteggio"/>
				<c:set var="numCampi" value="${numCampi + 1 }"/>
			</c:if>
			
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}'>
				
				<c:set var="idCampoFittizioAvv" value="${numCampi + 1}" />
				<c:set var="numCampi" value="${numCampi + 1 }"/>
				
				<tr id="rowCampo${idCampoFittizioAvv}">
					<td class="etichetta-dato">Ricorso ad avvalimento?</td>
					<td class="operatore-trova">
						<input type="hidden" name="Campo${idCampoFittizioAvv}_where" value="" />
						<input type="hidden" name="Campo${idCampoFittizioAvv}_computed" value="false" />
						<input type="hidden" name="Campo${idCampoFittizioAvv}_from" value="" />
						<input type="hidden" name="Campo${idCampoFittizioAvv}_conf" value="=" />
						<input type="hidden" name="defCampo${idCampoFittizioAvv}" value="" />
						&nbsp;
					</td>
					<td class="valore-dato-trova">
						<select id="Campo${idCampoFittizioAvv}" name="Campo${idCampoFittizioAvv}" title="Ricorso ad avvalimento?" onchange="javascript:valorizzaArchivi(this.value);"> 
							<option value=""></option>
							<option value="1" >Si</option>
							<option value="2" >No</option>
							
						</select>
						<input type="hidden" name="visualizzaArchivi" id="visualizzaArchivi" value="">
					</td>
				</tr>
			</c:if>
			
			<c:if test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and fn:contains(listaOpzioniDisponibili, 'OP114#')}">
				<c:set var="idCampoFittizioModConf" value="${numCampi + 1 }" />	
				
				
				<tr id="rowCampo${idCampoFittizioModConf}">
					<td class="etichetta-dato">Presente richiesta modifiche categorie da confermare?</td>
					<td class="operatore-trova">
						<input type="hidden" name="Campo${idCampoFittizioModConf}_where" value="" />
						<input type="hidden" name="Campo${idCampoFittizioModConf}_computed" value="false" />
						<input type="hidden" name="Campo${idCampoFittizioModConf}_from" value="" />
						<input type="hidden" name="Campo${idCampoFittizioModConf}_conf" value="=" />
						<input type="hidden" name="defCampo${idCampoFittizioModConf}" value="" />
						&nbsp;
					</td>
					<td class="valore-dato-trova">
						<select id="Campo${idCampoFittizioModConf}" name="Campo${idCampoFittizioModConf}" title="Presente richiesta modifiche categorie da confermare?" onchange="javascript:valorizzaAggCat(this.value);"> 
							<option value=""></option>
							<option value="1" >Si</option>
							<option value="2" >No</option>
							
						</select>
						
					</td>
				</tr>
				
			</c:if>
			
			
			
			<input type="hidden" id="isGaraElenco" name="isGaraElenco" value="${isGaraElenco}" />
			<input type="hidden" id="isGaraCatalogo" name="isGaraCatalogo" value="${isGaraCatalogo}" />
			<input type="hidden" id="ngara" name="ngara" value="${ngara}" />
			<input type="hidden" name="dittePerPagina" value="${dittePerPagina}" />
			<input type="hidden" name="resetCampi" value="" />
			<input type="hidden" id="MODOAPERTURA" name="MODOAPERTURA" value="VISUALIZZA" />
			<input type="hidden" id="GAREGENERE" name="GAREGENERE" value="10" />
			<input type="hidden" id="GAREALBO_TIPOELE" name="GAREALBO_TIPOELE" value="${tipoele}" />
			<input type="hidden" id="tipoele" name="tipoele" value="${tipoele}" />
			<input type="hidden" id="GARE_NGARA" name="GARE_NGARA" value="${ngara}" />
			<input type="hidden" id="menuSelezioneArchivio" name="menuSelezioneArchivio" value="true" />
			<input type="hidden" id="codiceGara" name="codiceGara" value="${codiceGara}" />
		</gene:formTrova>
		
		<c:if test="${isGaraElenco eq 1 or isGaraCatalogo eq 1}">
			<span id="LinkSelezioneCategoria" ><a id="aLinkSelezioneCategoria" href="javascript:ApriAlberoCategorie();" class="link-generico">Selezione categoria da albero</a></span>
			<div id="mascheraAlberoCategorie" title="Selezione Categoria" style="display: none;">
			
				
					<table class=dettaglio-tab id="pannelloRicercaCategorie">
						<tr>
							<td class="etichetta-dato">Legenda</td>
							<td class="valore-dato">
								<table class="griglia" style="border: 0px; color: #404040;">
									<tr>
										<td class="no-border" style="padding-left: 5px;">
											<img title="Categoria" alt="Categoria" src="img/categoria_blu.gif">&nbsp;Categoria&nbsp;
											<img title="Categoria archiviata" alt="Categoria archiviata" src="img/categoria_grigio.gif">&nbsp;Categoria archiviata&nbsp;
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<c:set var="titletextsearch" value="Ricerca per codice categoria, descrizione categoria" />
							<td class="etichetta-dato">Ricerca</td>
							<td class="valore-dato">
								<div id="categoriemenu" style="padding-left: 5px; color: #404040;">
									<input class="testo" style="vertical-align: middle;" type="text" size="40" id="textsearch" title="${titletextsearch}"/>
									<span class="link-generico" id="deletesearch"><img title="Elimina ricerca" alt="Elimina ricerca" src="img/cancellaFiltro.gif"></span>
									&nbsp;
									<span style="vertical-align: middle;" id="messaggioricerca"></span>
								</div>	
							</td>
						</tr>
						<tr>
							<td colspan="2" style="padding-top:5px; padding-bottom:5px;">
								<img alt="Categorie" src="img/open_folder.gif">
								<span style="vertical-align: middle;">
									<span style="display: none;" id="attesa" >
										<img title="Attesa" alt="Attesa" src="${contextPath}/css/jquery/jstree/themes/classic/throbber.gif">
									</span>
									Categorie
								</span>
								<div id="messaggiodatinontrovati" style="display: none">
									<br>
									Nessun elemento estratto
									<br><br>
								</div>
								<div id="categorietree" style="min-height: 250px; padding-left: 0px; margin-left: 0px;"></div>
							</td>
						</tr>		
					</table>
				
			</div>
		</c:if>
		
		
		
		<gene:javaScript>
		
			document.forms[0].campiCount.value+=2;
				
			document.forms[0].jspPathTo.value="gare/commons/popup-filtro.jsp";
			document.forms[0].action+= "?tipo=Ditte";
			
			var dittePerPAgina="${dittePerPagina}";
			var indiceSelezionato =  document.getElementById('risultatiPerPagina').selectedIndex;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].value = dittePerPAgina;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].innerHTML = dittePerPAgina;
			document.getElementById("risultatiPerPagina").disabled=true

			<c:if test="${isGaraElenco eq 1}">
				showObj("rowCampo10", false);
			</c:if>
			
			
			function gestioneNumcla(numcla){
				var index = document.getElementById("Campo11").selectedIndex;
				var tipoAppalto = document.getElementById("Campo11").options[index].text.substr(0,1);
	
				setValue("Campo10",tipoAppalto);
			}
			
			var trovaNuova_Default = trovaNuova;
		
			function trovaNuova_Custom(){
				document.forms[0].resetCampi.value='1';
				trovaNuova_Default();
			}
			
			trovaNuova = trovaNuova_Custom;
			
			function trovaVisualizzazioneAvanzata() {
				var checkboxavanzate = document.getElementById("visualizzazioneAvanzata");
				var test = checkboxavanzate.checked;
				if (test == true) {
					trovaVisualizzazioneOperatori('visualizza');
				} else {
					trovaNuova_Default();
				}
			}			
			//ATTENZIONE: Se si inseriscono campi prima del campo CAISIM , si deve controllare nel codice sorgente della pagina
			//da browser qual'è il nuovo indice assegnato al campo e modificare di conseguenza il codice seguente
			<c:if test="${isGaraElenco eq 1 or isGaraCatalogo eq 1}">
				$("#LinkSelezioneCategoria").appendTo($("#Campo8").parent());
								
				function ApriAlberoCategorie(){
					var ngara="${ngara }";
					$("#mascheraAlberoCategorie").dialog( "option", { ngara: ngara } );	
		   			
		   			$("#mascheraAlberoCategorie").dialog("open");
					$("#mascheraAlberoCategorie").height(300);
				
				}
			</c:if>
			
			/* Funzione che ricava il valore in sessione per i campi fittizi a partire dall'indice del campo.
			*  In sessione i campi sono del tipo Campo + progressivo.
			*  Se l'indice del campo fittizio dovessere essere maggiore di 27 aggiornare la funzione!!! 
			*/
			function getValoreDaIndiceCampo(indice){
				var valore;
				switch (parseInt(indice)) {
					case 10:
					    valore ="${sessionScope.trovaDITG.Campo10}";
					    break;
					case 11:
					    valore ="${sessionScope.trovaDITG.Campo11}";
					    break;
					case 12:
					    valore ="${sessionScope.trovaDITG.Campo12}";
					    break;
					case 13:
					    valore ="${sessionScope.trovaDITG.Campo13}";
					    break;
					case 14:
					    valore ="${sessionScope.trovaDITG.Campo14}";
					    break;
					case 15:
					    valore ="${sessionScope.trovaDITG.Campo15}";
					    break;
					case 16:
					    valore ="${sessionScope.trovaDITG.Campo16}";
					    break;
					case 17:
					    valore ="${sessionScope.trovaDITG.Campo17}";
					    break;
					case 18:
					    valore ="${sessionScope.trovaDITG.Campo18}";
					    break;
					case 19:
					    valore ="${sessionScope.trovaDITG.Campo19}";
					    break;
					case 20:
					    valore ="${sessionScope.trovaDITG.Campo20}";
					    break;
					case 21:
					    valore ="${sessionScope.trovaDITG.Campo21}";
					    break;
					case 22:
					    valore ="${sessionScope.trovaDITG.Campo22}";
					    break;
					case 23:
					    valore ="${sessionScope.trovaDITG.Campo23}";
					    break;
					case 24:
					    valore ="${sessionScope.trovaDITG.Campo24}";
					    break;
					case 25:
					    valore ="${sessionScope.trovaDITG.Campo25}";
					    break;
					case 26:
					    valore ="${sessionScope.trovaDITG.Campo26}";
					    break;
					case 27:
					    valore ="${sessionScope.trovaDITG.Campo27}";
					    break;
				}
				return valore;
			}
			
			function initVisualizzaArchivi(){
				var valore = getValoreDaIndiceCampo(${idCampoFittizioAvv});
				
				var filtroOld = document.forms[0].filtro.value;
				if(filtroOld!=null && filtroOld!="")
					filtroOld += " and ";
				else
					filtroOld += "";
				
				if (valore == 1) {
					document.getElementById('Campo${idCampoFittizioAvv}').selectedIndex = 1;
					document.forms[0].filtro.value = filtroOld + "exists(select dittao from ditgavval where ditgavval.dittao = ditg.dittao and ditgavval.ngara = ditg.ngara5)";	
				} else if(valore == 2) {
					document.getElementById('Campo${idCampoFittizioAvv}').selectedIndex = 2;
					document.forms[0].filtro.value = filtroOld + "not exists(select dittao from ditgavval where ditgavval.dittao = ditg.dittao and ditgavval.ngara = ditg.ngara5)";
				} 
				document.getElementById('visualizzaArchivi').value = valore;
			}
 		
			
			function valorizzaArchivi(valore) {
				var msgExists = "exists(select dittao from ditgavval where ditgavval.dittao = ditg.dittao and ditgavval.ngara = ditg.ngara5)";
				var msgNotExists = "not exists(select dittao from ditgavval where ditgavval.dittao = ditg.dittao and ditgavval.ngara = ditg.ngara5)";
				/*
				document.getElementById('visualizzaArchivi').value = valore;
				var filtroOld = document.forms[0].filtro.value;
				if(filtroOld!=null && filtroOld!="")
					filtroOld += " and ";
				else
					filtroOld += "";
				if (valore == 1) {
					document.forms[0].filtro.value = filtroOld + "exists(select dittao from ditgavval where ditgavval.dittao = ditg.dittao and ditgavval.ngara = ditg.ngara5)";	
				} else if(valore == 2) {
					document.forms[0].filtro.value = filtroOld + "not exists(select dittao from ditgavval where ditgavval.dittao = ditg.dittao and ditgavval.ngara = ditg.ngara5)";	
				}
				*/
				costruzioneFiltro(msgExists,msgNotExists, valore);
								
			}

			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}'>
				initVisualizzaArchivi();
			</c:if>
			
			
			<c:if test="${(isGaraElenco eq 1 or isGaraCatalogo eq 1) and fn:contains(listaOpzioniDisponibili, 'OP114#')}">
				initVisualizzaCampoFittizioCatDaConfermare();
				
				function initVisualizzaCampoFittizioCatDaConfermare(){
					var valore = getValoreDaIndiceCampo(${idCampoFittizioModConf});
									
					
					var filtroOld = document.forms[0].filtro.value;
					if(filtroOld!=null && filtroOld!="")
						filtroOld += " and ";
					else
						filtroOld += "";
					
					if (valore == 1) {
						document.getElementById('Campo${idCampoFittizioModConf}').selectedIndex = 1;
						document.forms[0].filtro.value = filtroOld + "exists(select codimp from garacquisiz where garacquisiz.codimp = ditg.dittao and garacquisiz.ngara = ditg.ngara5 and garacquisiz.stato=1)";	
					} else if(valore == 2) {
						document.getElementById('Campo${idCampoFittizioModConf}').selectedIndex = 2;
						document.forms[0].filtro.value = filtroOld + "not exists(select codimp from garacquisiz where garacquisiz.codimp = ditg.dittao and garacquisiz.ngara = ditg.ngara5 and garacquisiz.stato=1)";
					}
					
				}
			</c:if>
			
			function valorizzaAggCat(valore) {
				var msgExists = "exists(select codimp from garacquisiz where garacquisiz.codimp = ditg.dittao and garacquisiz.ngara = ditg.ngara5 and garacquisiz.stato=1)";
				var msgNotExists = "not exists(select codimp from garacquisiz where garacquisiz.codimp = ditg.dittao and garacquisiz.ngara = ditg.ngara5 and garacquisiz.stato=1)";
				costruzioneFiltro(msgExists,msgNotExists, valore);		
			}

			/*
			Per i campi fittizi si deve costruire a mano il filtro, da riportare in "document.forms[0].filtro.value". Si deve tenere conto che 
			la modifica di un campo deve mantenere intatta la condizione già presente per gli altri campi. 
			*/
			function costruzioneFiltro(msgExists, msgNotExists,valore){
				var filtroOld = document.forms[0].filtro.value;
							
				if(filtroOld!=null && filtroOld!="" && (filtroOld.indexOf(msgNotExists)>-1 || filtroOld.indexOf(msgExists)>-1)){
					if (valore == 1) {
						filtroOld = filtroOld.replace(msgNotExists,msgExists);
					}else if(valore == 2){
						filtroOld = filtroOld.replace(msgExists,msgNotExists);
					}else{
						filtroOld = filtroOld.replace(" and " + msgNotExists,"");
						filtroOld = filtroOld.replace(msgNotExists,"");
						filtroOld = filtroOld.replace(" and " + msgExists,"");
						filtroOld = filtroOld.replace(msgExists,"");
					}
					document.forms[0].filtro.value = filtroOld;
				}else{
					if(filtroOld!=null && filtroOld!="")
						filtroOld += " and ";
					else
						filtroOld += "";
						
					if (valore == 1) {
						document.forms[0].filtro.value = filtroOld + msgExists;	
					} else if(valore == 2) {
						document.forms[0].filtro.value = filtroOld + msgNotExists;	
					}		
				}
				document.forms[0].filtro.value = document.forms[0].filtro.value.trim();
                if(document.forms[0].filtro.value.startsWith("and")){
                	document.forms[0].filtro.value = document.forms[0].filtro.value.substring(4);
                }
			}
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
