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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="CAIS-lista">
	
	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.jstree.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.jstree.gene.cais.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.highlight.js"></script>
		
		<style type="text/css">
			.highlight {
			    background-color: #FFDB05;
			    -moz-box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4); /* FF3.5+ */
			    -webkit-box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4); /* Saf3.0+, Chrome */
			    box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4); /* Opera 10.5+, IE 9.0 */
			}
			
			.ui-dialog-titlebar {
				display: none;
			}
			
			.ui-widget-overlay { 
				background: rgb(128, 128, 128); opacity: .20; filter:Alpha(Opacity=20);
			}
			
			.ui-corner-all, .ui-corner-top, .ui-corner-left, .ui-corner-tl { -moz-border-radius-topleft: 0px; -webkit-border-top-left-radius: 0px; -khtml-border-top-left-radius: 0px; border-top-left-radius: 0px; }
			.ui-corner-all, .ui-corner-top, .ui-corner-right, .ui-corner-tr { -moz-border-radius-topright: 0px; -webkit-border-top-right-radius: 0px; -khtml-border-top-right-radius: 0px; border-top-right-radius: 0px; }
			.ui-corner-all, .ui-corner-bottom, .ui-corner-left, .ui-corner-bl { -moz-border-radius-bottomleft: 0px; -webkit-border-bottom-left-radius: 0px; -khtml-border-bottom-left-radius: 0px; border-bottom-left-radius: 0px; }
			.ui-corner-all, .ui-corner-bottom, .ui-corner-right, .ui-corner-br { -moz-border-radius-bottomright: 0px; -webkit-border-bottom-right-radius: 0px; -khtml-border-bottom-right-radius: 0px; border-bottom-right-radius: 0px; }
			
						
		</style>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Categorie d'iscrizione"/>
	<gene:redefineInsert name="azioniContesto"></gene:redefineInsert>
	<gene:redefineInsert name="corpo">
	
		<c:set var="isAddItemEnabled" value="false"/>
		<c:set var="isVisItemEnabled" value="false"/>
		<c:set var="isUpdItemEnabled" value="false"/>
		<c:set var="isDelItemEnabled" value="false"/>
	
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
			<c:set var="isAddItemEnabled" value="true"/>	
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.CAIS-scheda")}' >
			<c:set var="isVisItemEnabled" value="true"/>
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.CAIS-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
			<c:set var="isUpdItemEnabled" value="true"/>
		</c:if>
		<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}' >
			<c:set var="isDelItemEnabled" value="true"/>
		</c:if>
	
		<input type="hidden" id="isAddItemEnabled" value="${isAddItemEnabled}"/>
		<input type="hidden" id="isVisItemEnabled" value="${isVisItemEnabled}"/>
		<input type="hidden" id="isUpdItemEnabled" value="${isUpdItemEnabled}"/>
		<input type="hidden" id="isDelItemEnabled" value="${isDelItemEnabled}"/>
	
		<table class="dettaglio-notab">
			<tr>
				<td style="border-top: 0px;" class="etichetta-dato">Legenda</td>
				<td class="valore-dato-trova">
					<img title="Categoria" alt="Categoria" src="img/categoria_verde.gif">&nbsp;Categoria&nbsp;
					<img title="Categoria associata" alt="Categoria associata" src="img/categoria_rosso.gif">&nbsp;Categoria associata&nbsp;
					<img title="Categoria archiviata" alt="Categoria archiviata" src="img/categoria_grigio.gif">&nbsp;Categoria archiviata&nbsp;
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">
					Ricerca
				</td>
				<td class="valore-dato-trova">
					<input class="testo" style="vertical-align: middle;" type="text" size="40" id="textsearch" title="${titletextsearch}"/>
					<span class="link-generico" id="deletesearch"><img title="Elimina ricerca" alt="Elimina ricerca" src="img/cancellaFiltro.gif"></span>
					&nbsp;
					<span style="vertical-align: middle;" id="messaggioricerca"></span>
				</td>
			</tr>
			<tr>
				<td style="border-bottom: 0px;" colspan="2" class="valore-dato-trova">
					<img alt="Categorie" src="img/open_folder.gif">
					<span style="vertical-align: middle;">
						<span style="display: none;" id="attesa" >
							<img title="Attesa" alt="Attesa" src="${contextPath}/css/jquery/jstree/themes/classic/throbber.gif">
						</span>
						Categorie
					</span>
					<span id="expandall" style="padding-left: 10px;"><img title="Espandi tutto" alt="Espandi tutto" src="img/expand.gif">&nbsp;</span>
					<span id="collapseall"><img title="Chiudi tutto" alt="Chiudi tutto" src="img/collapse.gif">&nbsp;</span>
					<div id="categorietree" style="width:780px; min-height: 250px; padding-left: 0px; margin-left: 0px;"></div>
				</td>
			</tr>
		</table>
	
		<form name="formCategoria" action="${contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="gene/cais/cais-scheda-albero.jsp" />
			<input type="hidden" name="metodo" value="apri" />
			<input type="hidden" name="modo" value="">
			<input type="hidden" name="entita" value="CAIS" />
			<input type="hidden" name="key" value="" />
			<input type="hidden" name="keyparent" value="" />
			<input type="hidden" name="activePage" value="0" />
			<input type="hidden" name="tiplavg" value="" />
			<input type="hidden" name="titcat" value="" />
			<input type="hidden" name="codliv1" value="" />
			<input type="hidden" name="codliv2" value="" />
			<input type="hidden" name="codliv3" value="" />
			<input type="hidden" name="codliv4" value="" />
		</form>	
		
		<div id="dialog-delCais" title="Elimina categoria" style="display: none;">
			<br>
			<br>
			Eliminare la categoria selezionata e tutte le sue categorie figlie ?
		</div>
	
	</gene:redefineInsert>
</gene:template>

