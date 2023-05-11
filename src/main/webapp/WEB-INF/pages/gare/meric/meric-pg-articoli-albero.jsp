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

<c:if test="${fn:contains(header['user-agent'], 'MSIE')}">
	<gene:redefineInsert name="doctype">
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
	</gene:redefineInsert>
</c:if>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="id" value='${gene:getValCampo(key, "MERIC.ID")}' />

<gene:redefineInsert name="head" >

	<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.jstree.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.jstree.articoli.js"></script>
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
		
		label.error {
			margin-left: 0px;
			margin-right: 0px;
			color: red;
			font-weight: bold;
			font-size: 11px;
		}
		
		.error {
			color: red;
		}
		
		#vakata-contextmenu {		
			width: 280px;
		}
		
		#vakata-contextmenu li ins {
			margin-top: 2px;
			margin-left: 2px;
		}
		
	</style>
</gene:redefineInsert>


<gene:formScheda entita="MERIC" gestisciProtezioni="true">
				
	<gene:campoScheda campo="ID" visibile="false" />
	<gene:campoScheda campo="CODCATA" visibile="false" />
	<gene:campoScheda campo="DATVAL" visibile="false" />

	<gene:campoScheda>
		<td class="etichetta-dato">Legenda</td>
		<td class="valore-dato">
			<table class="griglia" style="border: 0px; color: #404040;">
				<tr>
					<td class="no-border" style="padding-left: 5px;">
						<img title="Categoria" alt="Categoria" src="img/categoria_arancione.gif">&nbsp;Categoria&nbsp;
						<img title="Articolo" alt="Articolo" src="img/articolo.gif">&nbsp;Articolo&nbsp;
						<img title="Articolo inserito nel carrello" alt="Articolo inserito nel carrello" src="img/articolocarrello.gif">&nbsp;Articolo inserito nel carrello&nbsp;
					</td>
				</tr>
			</table>
		</td>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
			<jsp:param name="entita" value="MERIC"/>
			<jsp:param name="inputFiltro" value="ID=N:${id}"/>
			<jsp:param name="filtroCampoEntita" value="IDMERIC=${id}"/>
		</jsp:include>
	</gene:campoScheda>
	
	<gene:campoScheda>
		<td class="etichetta-dato">Ricerca</td>
		<td class="valore-dato">
			<div id="articolimenu" style="padding-left: 5px; color: #404040;">
				<input class="testo" style="vertical-align: middle;" type="text" size="40" id="textsearch" title="Ricerca per codice categoria, descrizione categoria, codice articolo, descrizione articolo e colore articolo"/>
				<span class="link-generico" id="deletesearch"><img title="Elimina ricerca" alt="Elimina ricerca" src="img/cancellaFiltro.gif"></span>
				&nbsp;
				<span style="vertical-align: middle;" id="messaggioricerca"></span>
			</div>	
		</td>
	</gene:campoScheda>	
		
	<gene:campoScheda>
		<td colspan="2" style="padding-top:5px; padding-bottom:5px;">
			<img alt="Categorie ed articoli" src="img/open_folder.gif">
			<span style="vertical-align: middle;">
				<span style="display: none;" id="attesa" >
					<img title="Attesa" alt="Attesa" src="${contextPath}/css/jquery/jstree/themes/classic/throbber.gif">
				</span>
				Categorie ed articoli
			</span>
			<div id="messaggiodatinontrovati" style="display: none">
				<br>
				Nessun elemento estratto
				<br><br>
			</div>
			<div id="articolitree" style="min-height: 250px; padding-left: 0px; margin-left: 0px;"></div>
		</td>
	</gene:campoScheda>


	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />	
	<gene:redefineInsert name="schedaModifica" />
	<gene:redefineInsert name="pulsanteModifica" />
	<gene:campoScheda title="Autorizzato modifiche" campo="AUTORIZZATOMODIFICHE" campoFittizio="true" value="${autorizzatoModifiche}" definizione="T1;0" visibile="false" />

</gene:formScheda>

<div id="mascheraAggiungiArticolo">
	<form name="formAggiungiArticolo" id="formAggiungiArticolo">
		<table class="lista">
			<tr>
				<td>
					<b>Aggiungi al carrello</b>
					<br>
					<br>
				</td>
			</tr>
			<tr>
				<td style="height: 55px; vertical-align: top;">
					<span style="vertical-align: middle;">Quantit&agrave;&nbsp;(*)&nbsp;&nbsp;</span>
					<input type="text" id="quantita_articolo" name="quantita_articolo" size="9" title="Quantit&agrave; articolo" style="vertical-align: middle; text-align: right;">
					<span style="padding-top: 5px;" id="errorMessage"></span>
				</td>
			</tr>
			<tr>
				<td>
					Confermi l'aggiunta dell'articolo al carrello ?
				</td>
			</tr>
		</table>
	</form>
</div>

<div id="mascheraAggiungiArticoloQuantitaUM">
	<form name="formAggiungiArticoloQuantitaUM" id="formAggiungiArticoloQuantitaUM">
		<table class="lista">
			<tr>
				<td colspan="2">
					<b>Aggiungi al carrello</b>
					<br>
					<br>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					Per questa tipologia di articolo la quantit&agrave; richiesta viene espressa dettagliando le sue due componenti.
					<br>Indicare, oltre alla quantit&agrave;, anche una descrizione per ognuna delle due componenti.
					<br>(Es.<i>Unit&agrave; di misura su cui &egrave; espresso il prezzo = 'Camera per Notte': 
					riportare nella prima componente 'Camera' e nella seconda 'Notte'</i>)
					<br><br>
				</td>
			</tr>
			<tr>
				<td colspan="2" style="height: 40px; vertical-align: top;">
					<span style="vertical-align: middle;">Unit&agrave; di misura su cui &egrave; espresso il prezzo:&nbsp;</span>
					<span style="vertical-align: middle;" id="unimis"></span>
				</td>
			</tr>
			<tr>
				<td style="height: 50px; vertical-align: top;">
					<span style="vertical-align: middle;">Prima voce che compone l'unit&agrave; di misura:<br>Descrizione<br></span>
					<input type="text" id="desdet1" name="desdet1" maxlength="30" size="40" title="Prima componente quantit&agrave;" style="vertical-align: middle; text-align: left;" enabled>&nbsp;*<br>
					<span style="padding-top: 5px;" id="errorMessageDesc1"></span>
				</td>
				<td style="height: 50px; vertical-align: top;">
					<span style="vertical-align: middle;"><br>Quantit&agrave;<br></span>
					<input type="text" id="quadet1" name="quadet1" size="9" title="Quantit&agrave; 1" style="vertical-align: middle; text-align: right;">&nbsp;* <br>
					<span style="padding-top: 5px;" id="errorMessageQuant1"></span>
				</td>
			</tr>
			<tr>
				<td style="height: 50px; vertical-align: top;">
					<span style="vertical-align: middle;">Seconda voce che compone l'unit&agrave; di misura:<br>Descrizione<br></span>
					<input type="text" id="desdet2" name="desdet2" maxlength="30" size="40" title="Seconda componente quantit&agrave;" style="vertical-align: middle; text-align: left;">&nbsp;*<br>
					<span style="padding-top: 5px;" id="errorMessageDesc2"></span>
				</td>
				<td style="height: 50px; vertical-align: top;">
					<span style="vertical-align: middle;"><br>Quantit&agrave;<br></span>
					<input type="text" id="quadet2" name="quadet2" size="9" title="Quantit&agrave; 2" style="vertical-align: middle; text-align: right;">&nbsp;*<br> 
					<span style="padding-top: 5px;" id="errorMessageQuant2"></span>
				</td>
			</tr>
			<tr>
				<td style="height: 50px; vertical-align: top;">
					<span style="vertical-align: middle;"><br>Quantit&agrave;&nbsp;totale<br></span>
				</td>
				<td style="height: 50px; vertical-align: top;">
					<span style="vertical-align: middle;"><br></span>
					<input type="text" id="quantita_Fit" name="quantita_Fit" size="9" title="Quantit&agrave; articolo" style="vertical-align: middle; text-align: right;background-color: #ECECEC;" disabled>
					<input type="hidden" id="quantita_Tot" name="quantita_Tot" >
				</td>
			</tr>
			<tr>
				<td>
					Confermi l'aggiunta dell'articolo al carrello ?
				</td>
			</tr>
		</table>
	</form>
</div>

<div id="mascheraNoAggiungiArticolo">
	<table class="lista">
		<tr>
			<td colspan="2">
				<b>Aggiungi al carrello</b>
				<br>
				<br>
			</td>
		</tr>
		<tr>
			<td>
				<img title="Attenzione" alt="Attenzione" src="img/attenzione.gif">&nbsp;
			</td>
			<td>
				Non &egrave; possibile aggiungere l'articolo al carrello in quanto non ci sono prodotti associati.			
			</td>
		</tr>
	</table>
</div>

<div id="mascheraEliminaArticolo">
	<table class="lista">
		<tr>
			<td colspan="2">
				<b>Rimuovi dal carrello</b>
				<br>
				<br>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				Confermi la rimozione dell'articolo selezionato dal carrello ?<br><br>
			</td>
		</tr>
	</table>
</div>



<form name="formSchedaArticolo" action="${contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/meartcat/meartcat-scheda.jsp" />
	<input type="hidden" name="entita" value="MEARTCAT" />
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="listachiamante" value="meric-pg-articoli-albero.jsp"/>
</form>	

