<%
/*
 * Created on: 04/06/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>	
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
	
	<c:set var="idprg" value="${param.idprg}" />
	<c:set var="idcom" value="${param.idcom}" />
	<c:set var="idconfi" value="${param.idconfi}" />
	<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,idprg,idcom)}' />
	<c:set var="compub" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select compub from W_INVCOM where idprg=? and idcom=?", idprg, idcom)}'/>
	
	<c:set var="chiaveGara" value="GARE.NGARA=T:${comkey1}" />
	<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,chiaveGara)}' />
	
	<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />
		
	<c:if test="${genereGara eq '10' || genereGara eq '20'}">
		<c:set var="isVisibleDataScadenzaIscriz" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVisibleDataScadenzaIscrizFunction",pageContext,comkey1)}' />
		<c:set var="valiscr" value='${valiscr}' />
	</c:if>
	
	<c:choose>
		<c:when test="${genereGara eq '10'}">
		<c:set var="testoTipoGara" value=" in elenco" />
		</c:when>
		<c:when test="${genereGara eq '20'}">
			<c:set var="testoTipoGara" value="in catalogo" />
		</c:when>
		<c:otherwise>
			<c:set var="testoTipoGara" value="in gara" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Ricerca destinatari da ditte ${testoTipoGara }" />
		
	<gene:redefineInsert name="corpo">
		<gene:formTrova entita="DITG"  gestisciProtezioni="true"
			lista="geneweb/w_invcomdes/w_invcomdes-lista.jsp"
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori.RicercaDitteW_INVCOMDESGestoreTrova">
			<gene:campoTrova campo="NOMIMO" />
			<gene:campoTrova campo="CFIMP" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP"/>
			<gene:campoTrova campo="PIVIMP" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP"/>
			<c:choose>
				<c:when test="${genereGara ne '10' and genereGara ne '20'}">
					<gene:campoTrova campo="INVGAR" />
					<gene:campoTrova campo="INVOFF" />
					<gene:campoTrova title="Ditta esclusa ?" campo="AMMGAR" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoInvertito"/>
					<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.AMMGAR") && gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.FASGAR")}'>
						<gene:fnJavaScriptTrova funzione="gestioneAMMGAR('#Campo5#')" elencocampi="Campo5" esegui="true" />
					</c:if>
					
					<gene:campoTrova campo="FASGAR" />
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="CAISIM" title="Codice categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  "/>
					<gene:campoTrova campo="DESCAT1" title="Descrizione categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  "/>
					<gene:campoTrova campo="TIPLAVG" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  "/>
					<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.SUPNUMCLASS")}'>
 						<gene:campoTrova campo="NUMCLASS" title="Classifica categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  " gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoClassificaCategoriaRicerca"/>
						<gene:fnJavaScriptTrova funzione="gestioneNumcla('#Campo6#')" elencocampi="Campo6" esegui="true" />
 					</c:if>
					<gene:campoTrova campo="ABILITAZ" />
					<gene:campoTrova campo="DRICIND" title="Data domanda iscrizione"/>
					<c:set var="chiaveTmp" value="GARE.NGARA=T:${comkey1 }" />
					<c:set var="tipologia" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGarealboFunction", pageContext, chiaveTmp)}' scope="request"/>
					<c:if test="${tipologia ne 3 }">
						<gene:campoTrova campo="DSCAD" />
						<gene:campoTrova campo="STRIN" />
					</c:if>
					<c:if test='${isVisibleDataScadenzaIscriz eq "true"}'>
						<c:choose>
							<c:when test='${dbms eq "POS"}'>
								<gene:campoTrova title="Data scadenza iscrizione" computed="true" campo="${gene:getDBFunction(pageContext,'isnull','DSCAD;DRICIND')}::date + ${valiscr } - 1" definizione="D;0;;DATA_ELDA"/>
							</c:when>
							<c:otherwise>
								<gene:campoTrova title="Data scadenza iscrizione" computed="true" campo="${gene:getDBFunction(pageContext,'isnull','DSCAD;DRICIND')} + ${valiscr } - 1" definizione="D;0;;DATA_ELDA"/>
							</c:otherwise>
						</c:choose>
					</c:if>
					<gene:campoTrova campo="ESTIMP" title="Sorteggiata per verifica documenti?"/>
					<gene:campoTrova campo="DSORTEV" title="Data sorteggio"/>
				</c:otherwise>
			</c:choose>
			

			<tr>
				<td class="etichetta-dato"/>
				<td class="operatore-trova"/>
				<td class="valore-dato-trova"/>
			</tr>
			<tr>
				<td class="etichetta-dato">Visualizza solo ditte non ancora inserite tra i destinatari?</td>
				<td class="operatore-trova"/>
				<td class="valore-dato-trova">
					<select id="CampoFitt" name="CampoFitt" title="Visualizza ditte non inserite tra i destinatari">
						<option value="1" selected="selected">Si</option>
						<option value="2" >No</option>
					</select>
				</td>
			</tr>
			
			<input type="hidden" name="idconfi" value="${idconfi}" />
			<input type="hidden" name="idprg" value="${idprg}" />
			<input type="hidden" name="idcom" value="${idcom}" />
			<input type="hidden" name="comkey1" value="${comkey1}" />
			<input type="hidden" name="genereGara" value="${genereGara}" />
			<input type="hidden" name="compub" value="${compub}" />
			
				
		</gene:formTrova>
		
		<c:if test="${genereGara eq '10'}">
			<span id="LinkSelezioneCategoria" ><a id="aLinkSelezioneCategoria" href="javascript:ApriAlberoCategorie();" class="link-generico">Selezione categoria da albero</a></span>
			<div id="mascheraAlberoCategorie" title="Selezione Categoria" style="display: none;">
			<c:set var='tipoele' value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPOELEFunction", pageContext, comkey1)}' />
			
			<input type="hidden" id="MODOAPERTURA" name="MODOAPERTURA" value="VISUALIZZA" />
			<input type="hidden" id="GAREGENERE" name="GAREGENERE" value="10" />
			<input type="hidden" id="GAREALBO_TIPOELE" name="GAREALBO_TIPOELE" value="${tipoele}" />
			<input type="hidden" id="GARE_NGARA" name="GARE_NGARA" value="${comkey1}" />
			<input type="hidden" id="menuSelezioneArchivio" name="menuSelezioneArchivio" value="true" />
			<input type="hidden" id="campoCodiceCategoria" name="campoCodiceCategoria" value="Campo3" />
			<input type="hidden" id="campoDescrizioneCategoria" name="campoDescrizioneCategoria" value="Campo4" />
			
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
			<c:if test="${genereGara eq '10' or genereGara eq '20'}">
				showObj("rowCampo5", false);
			</c:if>
			
			<c:if test="${compub eq '3'}">
				showObj("rowCampo1", false);
				showObj("rowCampo2", false);
			</c:if>

			function gestioneAMMGAR(ammgar){
				document.getElementById("rowCampo6").style.display = (ammgar=='2' ? '':'none');
			}

			function gestioneNumcla(numcla){
				var index = document.getElementById("Campo6").selectedIndex;
				var tipoAppalto = document.getElementById("Campo6").options[index].text.substr(0,1);
	
				setValue("Campo5",tipoAppalto);
			}
			
			//ATTENZIONE: Se si inseriscono campi prima del campo CAISIM , si deve controllare nel codice sorgente della pagina
			//da browser qual è il nuovo indice assegnato al campo e modificare di conseguenza il codice seguente
			<c:if test="${genereGara eq '10'}">
				$("#LinkSelezioneCategoria").appendTo($("#"+$("#campoCodiceCategoria").val()).parent());
								
				function ApriAlberoCategorie(){
					var ngara="${chiaveGara}";
					$("#mascheraAlberoCategorie").dialog( "option", { ngara: ngara } );	
		   			
		   			$("#mascheraAlberoCategorie").dialog("open");
					$("#mascheraAlberoCategorie").height(300);
				
				}
			</c:if>
		</gene:javaScript>
  	</gene:redefineInsert>
</gene:template>
</div>


