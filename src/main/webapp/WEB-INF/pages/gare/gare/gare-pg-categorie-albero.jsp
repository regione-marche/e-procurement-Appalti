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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />

<c:set var="codiceGara" value="$ ${ngara}" />
<c:set var="codiceGara" value='${fn:replace(codiceGara," ", "")}' />

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


<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARECategorieAlbero">

	<gene:campoScheda campo="NGARA" visibile="false" />
	<gene:campoScheda campo="CODGAR1" visibile="false" />
	<gene:campoScheda campo="GENERE" visibile="false" />
	<gene:campoScheda entita="GAREALBO" campo="TIPOELE" where="GAREALBO.NGARA = GARE.NGARA" visibile="false"/>

	<gene:campoScheda title="Modo apertura" campo="MODOAPERTURA" campoFittizio="true" value="${modo}" definizione="T30;0" visibile="false" />
	<gene:campoScheda title="Genere" campo="GAREGENERE" campoFittizio="true" value="${datiRiga.GARE_GENERE}" definizione="T30;0" visibile="false" />
	<gene:campoScheda title="Checked" campo="CHECKED" campoFittizio="true" definizione="T2000;0" visibile="false" />
	<gene:campoScheda title="Unchecked" campo="UNCHECKED" campoFittizio="true" definizione="T2000;0" visibile="false" />
	<gene:campoScheda title="Undefined" campo="UNDETERMINED" campoFittizio="true" definizione="T2000;0" visibile="false" />
	<gene:campoScheda title="Abilita ordine minimo" campo="ABILITAORDINEMINIMO" campoFittizio="true" value="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE-scheda.CATEGORIEGARA.CATEG-ImpostaOrdineMin')}" definizione="T2000;0" visibile="false" />

	<gene:campoScheda visibile="${datiRiga.GARE_GENERE eq '10'}">
		<td class="etichetta-dato">Legenda</td>
		<td class="valore-dato">
			<table class="griglia" style="border: 0px; color: #404040;">
				<tr>
					<td class="no-border" style="padding-left: 5px;">
						<img title="Categoria" alt="Categoria" src="img/categoria_blu.gif">&nbsp;Categoria&nbsp;
						<img title="Categoria archiviata" alt="Categoria archiviata" src="img/categoria_grigio.gif">&nbsp;Categoria archiviata&nbsp;
						<c:if test="${modo ne 'VISUALIZZA'}">
							&nbsp;
							<img id="expandlegendack" title="Espandi legenda" alt="Espandi legenda" src="img/expand.gif">
							<img id="collapselegendack" style="display: none;" title="Chiudi legenda" alt="Chiudi legenda" src="img/collapse.gif">
						</c:if>
						<c:if test="${modo ne 'VISUALIZZA'}">
							<div id="legendack" style="display: none;">
								<img title="Gruppo o categoria non selezionata" alt="Gruppo o categoria non selezionata" src="${contextPath}/css/jquery/jstree/themes/classic/d_unchecked.gif">
								<img title="Gruppo o categoria selezionata" alt="Gruppo o categoria selezionata" src="${contextPath}/css/jquery/jstree/themes/classic/d_checked.gif">
								<img title="Gruppo o categoria indefinita" alt="Gruppo o categoria indefinita" src="${contextPath}/css/jquery/jstree/themes/classic/d_undetermined.gif">
								Gruppo o categoria non selezionata/selezionata/indefinita
								<br>
								<img title="Categoria archiviata selezionata" alt="Categoria archiviata selezionata" src="${contextPath}/css/jquery/jstree/themes/classic/d_g_checked.gif">
								Categoria archiviata selezionata
							</div>
						</c:if>
					</td>
				</tr>
			</table>
		</td>
	</gene:campoScheda>



	<gene:campoScheda visibile="${datiRiga.GARE_GENERE eq '20'}">
		<td class="etichetta-dato">Legenda</td>
		<td class="valore-dato">
			<table class="griglia" style="border: 0px; color: #404040;">
				<tr>
					<td class="no-border" style="padding-left: 5px;">
						<img title="Categoria senza articoli" alt="Categoria senza articoli" src="img/categoria_blu.gif">&nbsp;Cat. senza articoli&nbsp;
						<img title="Categoria archiviata senza articoli" alt="Categoria archiviata senza articoli" src="img/categoria_grigio.gif">&nbsp;Cat. archiviata senza articoli&nbsp;
						<img title="Categoria con articoli" alt="Categoria con articoli" src="img/categoria_arancione.gif">&nbsp;Cat. con articoli&nbsp;
						<img title="Articolo"alt="Articolo" src="img/documentazione.gif">&nbsp;Articolo&nbsp;
						<c:if test="${modo ne 'VISUALIZZA'}">
							&nbsp;
							<img id="expandlegendack" title="Espandi legenda" alt="Espandi legenda" src="img/expand.gif">
							<img id="collapselegendack" style="display: none;" title="Chiudi legenda" alt="Chiudi legenda" src="img/collapse.gif">
						</c:if>
						<c:if test="${modo ne 'VISUALIZZA'}">
							<div id="legendack" style="display: none;">
								<img title="Gruppo o categoria non selezionata" alt="Gruppo o categoria non selezionata" src="${contextPath}/css/jquery/jstree/themes/classic/d_unchecked.gif">
								Gruppo o categoria non selezionata
								<br>
								<img title="Gruppo o categoria senza articoli selezionata" alt="Gruppo o categoria senza articoli selezionata" src="${contextPath}/css/jquery/jstree/themes/classic/d_checked.gif">
								<img title="Gruppo o categoria senza articoli indefinita" alt="Gruppo o categoria senza articoli indefinita" src="${contextPath}/css/jquery/jstree/themes/classic/d_undetermined.gif">
								Gruppo o categoria, senza articoli, selezionata/indefinita
								<br>
								<img title="Categoria archiviata selezionata" alt="Categoria archiviata selezionata" src="${contextPath}/css/jquery/jstree/themes/classic/d_g_checked.gif">
								Categoria archiviata, senza articoli, selezionata
								<br>
								<img title="Gruppo o categoria con articoli selezionata" alt="Gruppo o categoria con articoli selezionata" src="${contextPath}/css/jquery/jstree/themes/classic/d_r_checked.gif">
								<img title="Gruppo o categoria con articoli indefinita" alt="Gruppo o categoria con articoli indefinita" src="${contextPath}/css/jquery/jstree/themes/classic/d_r_undetermined.gif">
								Gruppo o categoria, con articoli, selezionata/indefinita
							</div>
						</c:if>	
											
					</td>
				</tr>
			</table>
		</td>
	</gene:campoScheda>
	
	
	<gene:campoScheda>
		<c:choose>
			<c:when test="${datiRiga.GARE_GENERE eq '10'}">
				<c:set var="titlealbero" value="Categorie" />
				<c:set var="titletextsearch" value="Ricerca per codice categoria, descrizione categoria" />
			</c:when>
			<c:otherwise>
				<c:set var="titlealbero" value="Categorie ed articoli" />
				<c:set var="titletextsearch" value="Ricerca per codice categoria, descrizione categoria, codice articolo, descrizione articolo e colore articolo" />
			</c:otherwise>
		</c:choose>
		<td class="etichetta-dato">Ricerca</td>
		<td class="valore-dato">
			<div id="categoriemenu" style="padding-left: 5px; color: #404040;">
				<input class="testo" style="vertical-align: middle;" type="text" size="40" id="textsearch" title="${titletextsearch}"/>
				<span class="link-generico" id="deletesearch"><img title="Elimina ricerca" alt="Elimina ricerca" src="img/cancellaFiltro.gif"></span>
				&nbsp;
				<span style="vertical-align: middle;" id="messaggioricerca"></span>
			</div>	
		</td>
	</gene:campoScheda>	
	
		
	<gene:campoScheda>
		<td colspan="2" style="padding-top:5px; padding-bottom:5px;">
			<img alt="Categorie" src="img/open_folder.gif">
			<span style="vertical-align: middle;">
				<span style="display: none;" id="attesa" >
					<img title="Attesa" alt="Attesa" src="${contextPath}/css/jquery/jstree/themes/classic/throbber.gif">
				</span>
				${titlealbero}
			</span>
			<div id="messaggiodatinontrovati" style="display: none">
				<br>
				Nessun elemento estratto
				<br><br>
			</div>
			<div id="categorietree" style="min-height: 250px; padding-left: 0px; margin-left: 0px;"></div>
		</td>
	</gene:campoScheda>

	<gene:campoScheda>	
		<c:if test="${datiRiga.GARE_GENERE eq '10'}">
			<c:set var="entita" value="V_GARE_ELEDITTE"/>
		</c:if>
		<c:if test="${datiRiga.GARE_GENERE eq '20'}">
			<c:set var="entita" value="V_GARE_CATALDITTE"/>
		</c:if>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="${entita}"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GARE_CODGAR1}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
	</gene:campoScheda>
	
	<gene:campoScheda title="Autorizzato modifiche" campo="AUTORIZZATOMODIFICHE" campoFittizio="true" value="${autorizzatoModifiche}" definizione="T1;0" visibile="false" />	
	
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />
		
	<gene:redefineInsert name="schedaModifica">
		<tr>
			<td class="vocemenulaterale">
				<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.modassociazionecategorie") && autorizzatoModifiche eq "1"}'>
					<a href="javascript:schedaModifica();" title="Modifica associazione categorie" tabindex="1501">Modifica associazione categorie</a>
				</c:if>
			</td>
		</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="pulsanteModifica">
		<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.modassociazionecategorie") && autorizzatoModifiche eq "1"}'>
			<input type="button" class="bottone-azione" value="Modifica associazione categorie" title="Modifica associazione categorie" onclick="javascript:schedaModifica()">
		</c:if>
	</gene:redefineInsert>

	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>


</gene:formScheda>

<div id="mascheraImportoOrdineMinimo">
	<form name="formImportoOrdineMinimo" id="formImportoOrdineMinimo">
		<table class="lista">
			<tr>
				<td>
					<b>Importo minimo ordine</b><br><br>
					Importo minimo garantito agli operatori economici per gli articoli appartenenti, direttamente o meno, alla categoria selezionata<br><br>
				</td>
			</tr>
			<tr>
				<td>
					<input id="ORDMIN" name="ORDMIN" title="Importo ordine minimo" class="importo" type="text" size="20" value="" maxlength="18">&nbsp;&euro;
					<br><br><span id="errorMessage"></span>
				</td>
			</tr>
		</table>
	</form>
</div>

<div id="mascheraNoImportoOrdineMinimo">
	<table class="lista">
		<tr>
			<td colspan="2">
				<b>Importo minimo ordine</b><br><br>
			</td>
		</tr>
		<tr>
			<td>
				<img title="Attenzione" alt="Attenzione" src="img/attenzione.gif">&nbsp;
			</td>
			<td>
				L'importo minimo dell'ordine non &egrave; modificabile in quanto esistono gi&agrave; una categoria padre 
				o delle categorie figlie con l'importo valorizzato.			
			</td>
		</tr>
	</table>
</div>

<form name="formListaArticoli" action="${contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/meartcat/meartcat-lista.jsp" />
	<input type="hidden" name="entita" value="MEARTCAT" />
	<input type="hidden" name="trovaAddWhere" value="" />
	<input type="hidden" name="trovaParameter" value="" /> 
	<input type="hidden" name="risultatiPerPagina" value="20" />
	<input type="hidden" name="opes_ngara" value="" />
	<input type="hidden" name="opes_nopega" value="" />
	<input type="hidden" name="cais_caisim" value="" />
	<input type="hidden" name="cais_descat" value="" />
</form>

<form name="formSchedaArticolo" action="${contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/meartcat/meartcat-scheda.jsp" />
	<input type="hidden" name="entita" value="MEARTCAT" />
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="opes_ngara" value="" />
	<input type="hidden" name="opes_nopega" value="" />
	<input type="hidden" name="cais_caisim" value="" />
	<input type="hidden" name="cais_descat" value="" />
</form>	

