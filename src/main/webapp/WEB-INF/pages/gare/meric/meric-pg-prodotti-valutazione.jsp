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
<c:set var="id" value='${gene:getValCampo(key, "MERIC.ID")}' />

<gene:redefineInsert name="head" >

	<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.prodotti.valutazione.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.formatCurrency-1.4.0.js"></script>

	<style type="text/css">
		
		TABLE.carrello {
			margin: 0;
			margin-top: 5px;
			margin-bottom: 5px;
			padding: 0px;
			width: 100%;
			font-size: 11px;
			border-collapse: collapse;
			border-left: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
		}

		TABLE.carrello TR.titolo {
			background-color: #EFEFEF;
			border-bottom: 1px solid #A0AABA;
		}
		
		TABLE.carrello TR.titolo TD, TABLE.carrello TR.titolo TH {
			padding: 8 2 8 2;
			text-align: left;
			font-weight: bold;
			height: 35px;
		}
		
		TABLE.carrello TR.intestazione {
			background-color: #EFEFEF;
			border-bottom: 1px solid #A0AABA;
		}
		
		TABLE.carrello TR.intestazione TH, TABLE.carrello TR.intestazione TD  {
			padding: 8 2 8 2;
			text-align: center;
			font-weight: bold;
			height: 35px;
		}

		TABLE.carrello TR.intestazione TH.articolo, TABLE.carrello TR.intestazione TD.articolo {
			width: 130px;
		}

		TABLE.carrello TR.intestazione TH.importo, TABLE.carrello TR.intestazione TD.importo {
			width: 110px;
			text-align: center;
		}

		TABLE.carrello TR.intestazione TH.impresa, TABLE.carrello TR.intestazione TD.impresa {
			width: 120px;
			text-align: center;
		}
		
		TABLE.carrello TR.intestazione TH.carrelloparagone, TABLE.carrello TR.intestazione TD.carrelloparagone {
			width: 130px;
			text-align: center;
		}
		
		TABLE.carrello TR.intestazione TH.dettaglioprodotto, TABLE.carrello TR.intestazione TD.dettaglioprodotto {
			width: 30px;
			text-align: center;
		}

		TABLE.carrello TR.intestazione TH.data, TABLE.carrello TR.intestazione TD.data {
			text-align: left;
		}

		TABLE.carrello TR.riepilogo {
			background-color: #EFEFEF;
			border-bottom: 1px solid #A0AABA;
		}
		
		TABLE.carrello TR.riepilogo TD {
			padding: 8 2 8 2;
			text-align: center;
			height: 25px;
		}
		
		TABLE.carrello TR.riepilogo TD.totale {
			padding: 8 4 8 4;
			text-align: right;
		}

		TABLE.carrello TR.acquistato TD {
			background-color: #D3E4FF;
		}

		img.img_aggiungi_prodotto_paragone, 
		img.img_aggiungi_prodotto_offerto, 
		img.img_elimina_prodotto_acquistato, 
		img.img_aggiungi_prodotto_da_lista,
		img.img_aggiungi_prodotto_da_scheda,
		img.img_variazione_articolo {
			padding-left: 8px;
			width: 18px;
			height: 18px;
			vertical-align: bottom;
			cursor: pointer;
		}

		img.img_documenti {
			width: 18px;
			height: 18px;
			vertical-align: bottom;
			cursor: pointer;
		}
		
		img.img_titolo {
			padding-left: 8px;
			padding-right: 8px;
			width: 18px;
			height: 18px;
			vertical-align: middle;
		}
		
		img.img_prodotto {
			cursor: pointer;
		}
		
		img.img_altriprodotti {
			text-align: center;
			width: 18px;
			height: 18px;
			cursor: pointer;
		}

		TABLE.carrello TR {
			background-color: #FFFFFF;
		}

		TABLE.carrello TR TH, TABLE.carrello TR TD{
			padding-left: 3px;
			padding-top: 3px;
			padding-bottom: 3px;
			padding-right: 3px;
			text-align: left;
			border-left: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-bottom: 1px solid #A0AABA;
			height: 35px;
			font: 11px Verdana, Arial, Helvetica, sans-serif;
		}

		TABLE.carrello TR TD.importo {
			width: 110px;
			text-align: right;
		}
		
		TABLE.carrello TR TD.impresa {
			width: 120px;
			text-align: left;
		}
		
		TABLE.carrello TR TD.nomecommerciale {
			width: 140px;
			text-align: left;
		}
		
		TABLE.carrello TR TD.iva {
			width: 30px;
			text-align: center;
		}		

		TABLE.carrello TR TD.altriprodotti, TABLE.carrello TR TD.centrato {
			text-align: center;
		}
		
		TABLE.carrello TR TD.dettaglioprodotto {
			text-align: center;
			width: 30px;
		}
		
		TABLE.carrello TR TD.coloreAI {
			background-color: #CCE0FF;
		}
		
		TABLE.carrello TR TD.coloreBI {
			background-color: #BFD8FF;
		}
		
		TABLE.carrello TR TD.coloreCI {
			background-color: #A5CAFF;
		}

		TABLE.carrello TR TD.coloreDI {
			background-color: #BFD8FF;
		}

		TABLE.carrello TR TD.coloreA {

		}
		
		TABLE.carrello TR TD.coloreB {

		}
		
		TABLE.carrello TR TD.coloreC {

		}		

		TABLE.carrello TR TD.coloreD {

		}	
		
		TABLE.scheda {
			margin: 0;
			margin-top: 5px;
			margin-bottom: 5px;
			padding: 0px;
			width: 100%;
			font-size: 11px;
			border-collapse: collapse;
			border-left: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
		}

		TABLE.scheda TR.intestazione {
			background-color: #EFEFEF;
			border-bottom: 1px solid #A0AABA;
		}
		
		TABLE.scheda TR.intestazione TD {
			padding: 8 2 8 2;
			text-align: left;
			font-weight: bold;
			border-left: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-bottom: 1px solid #A0AABA;
			height: 35px;
		}
	
		TABLE.scheda TR.sezione {
			background-color: #EFEFEF;
			border-bottom: 1px solid #A0AABA;
		}
		
		TABLE.scheda TR.sezione TD {
			padding: 8 2 8 8;
			text-align: left;
			font-weight: bold;
			height: 25px;
		}
	
		TABLE.scheda TR {
			background-color: #FFFFFF;
		}

		TABLE.scheda TR TD {
			padding-left: 3px;
			padding-top: 3px;
			padding-bottom: 3px;
			padding-right: 3px;
			text-align: left;
			border-left: 1px solid #A0AABA;
			border-right: 1px solid #A0AABA;
			border-top: 1px solid #A0AABA;
			border-bottom: 1px solid #A0AABA;
			height: 15px;
			font: 11px Verdana, Arial, Helvetica, sans-serif;
		}
		
		TABLE.scheda TR TD.etichetta {
			width: 150px;
			background-color: #EFEFEF;
			text-align: right;
		}


		TABLE.scheda TR TD.etichettacfr {
			width: 240px;
			background-color: #EFEFEF;
			text-align: right;
		}
				

		span.floatright{
			padding-right: 5px;
			padding-left: 5px;
			float: right;
			vertical-align: middle;
			cursor: pointer;
		}
				
	</style>
</gene:redefineInsert>


<gene:formScheda entita="MERIC" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMERICValutazione">
	<gene:campoScheda campo="ID" visibile="false" />
	<gene:campoScheda campo="CODCATA" visibile="false" />
	<gene:campoScheda campo="DATVAL" visibile="false" />
	<gene:campoScheda addTr="false" hideTitle="true" visibile="false" computed="true" nome="DATVAL_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','MERIC.DATVAL')}" modificabile="false" definizione="T20;0;;;G1DATVAL" />
	
	<gene:campoScheda title="Modo apertura" campo="MODOAPERTURA" campoFittizio="true" value="${modo}" definizione="T30;0" visibile="false" />
	
	<gene:campoScheda addTr="false">
		<tr>
			<td colspan="2">
				<table class="carrello" id="carrello">
					<tr class="intestazione">
						<td class="data" colspan="9">
							<img class="img_titolo" title="Data di attivazione della valutazione prodotti" alt="Data di attivazione della valutazione prodotti" src="img/orologio.png">
							Data di attivazione della valutazione prodotti:
							<span id="dataOraValutazioneProdotti"></span>
						</td>
					</tr>
					<tr class="intestazione">
						<td class="coloreAI articolo" rowspan="3">Articolo</td>
						<td class="coloreAI carrelloparagone" rowspan="2">Carrello di paragone</td>
						<td class="coloreBI" colspan="3">Prodotti e prezzi offerti per articolo</td>
						<td class="coloreCI" rowspan="2" colspan="3">Bozza ordine<br><br>(Prodotti selezionati: <span id="numero_prodotti_acquistati">0</span>)</td>
						<td rowspan="3" class="coloreDI importo">Differenza con miglior prezzo</td>
					</tr>
					<tr class="intestazione">
						<td class="coloreBI" colspan="2">Migliori prezzi per articolo&nbsp;&nbsp;
							<span id="numeroMiglioriPrezziContainer" style="vertical-align: top;"></span>
						</td>
						<td class="coloreBI" rowspan="2" title="Altri prodotti">Altri prodotti</td>
					</tr>
					<tr class="intestazione">
						<td class="coloreAI"><span id="impreseContainer"></span></td>
						<td class="coloreBI importo">Prezzo</td>
						<td class="coloreBI mpresa">Operatore</td>
						<td class="coloreCI importo">Prezzo</td>
						<td class="coloreCI iva">% IVA</td>
						<td class="coloreCI impresa">Operatore</td>
					</tr>
				</table>
			</td>
		</tr>
	</gene:campoScheda>
	
	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
			<jsp:param name="entita" value="MERIC"/>
			<jsp:param name="inputFiltro" value="ID=N:${id}"/>
			<jsp:param name="filtroCampoEntita" value="IDMERIC=${id}"/>
		</jsp:include>
		<c:if test='${autorizzatoModifiche eq "1"}'>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</c:if>
	</gene:campoScheda>

	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />	
	
	<c:if test='${autorizzatoModifiche eq "2"}'>
		<gene:redefineInsert name="schedaModifica" />
	</c:if>
	
</gene:formScheda>

<div id="mascheraListaProdottiArticolo" title="Articolo e lista prodotti" style="display: none;">

	<div id="schedaArticoloContainer" style="width: 950px;">
		<table class="scheda" id="schedaArticolo" style="width: 100%;">
			<tr id="schedaArticoloTitolo" style="cursor: pointer;" class="intestazione">
				<td colspan="6">
					<img class="img_titolo" title="Ricerca" alt="Ricerca" src="img/Content-17.png">Articolo
				</td>
			</tr>
			<tr style="display: none;">
				<td class="etichetta">Tipo</td>
				<td id="schedaArticoloTipo"></td>
				
				<td class="etichetta">Descrizione</td>
				<td id="schedaArticoloDescrizione" colspan="3"></td>
			</tr>
			<tr style="display: none;">
				<td class="etichetta">Descrizione estesa</td>
				<td id="schedaArticoloDescrizioneEstesa" colspan="5"></td>		
			</tr>
			<tr style="display: none;">
				<td class="etichetta">Colore</td>
				<td id="schedaArticoloColore"></td>
				
				<td class="etichetta">Modalit&agrave; di acquisto</td>
				<td id="schedaArticoloModalitaAcquisto"></td>
				
				
			</tr>
			<tr style="display: none;">
				<td class="etichetta">Quantit&agrave; richiesta</td>
				<td id="schedaArticoloQuantita"></td>
			</tr>
		</table>
	</div>

	<div id="pannelloRicercaListaProdottiContainer" style="width: 950px;">
		<table class="scheda" id="pannelloRicercaListaProdotti" style="width: 100%;">
			<tr id="pannelloRicercaListaProdottiTitolo" style="cursor: pointer;" class="intestazione">
				<td colspan="4">
					<img class="img_titolo" title="Ricerca" alt="Ricerca" src="img/Content-47.png" style="width: 20px; height: 20px;">Ricerca dei prodotti
				</td>
			</tr>
			<tr style="display: none;">
				<td class="etichetta">Nome commerciale</td>
				<td><input id="searchNomeCommerciale" type="text" size="40"></input></td>
				
				<td class="etichetta" ><span id="etichettaMarca">Marca</span></td>
				<td><input id="searchMarca" type="text" size="40"></input></td>
			</tr>		
			<tr style="display: none;" >
				<td class="etichetta">Operatore</td>
				<td colspan="3"><select id="searchOperatore"></select></td>
				
			</tr>	
			<tr style="display: none;">
				<td class="etichetta">Quantit&agrave; offerta</td>
				<td>
					da&nbsp;<input id="searchQuantitaOffertaMin" type="text" size="10"></input>
					a&nbsp;<input id="searchQuantitaOffertaMax" type="text" size="10"></input>
				</td>
				
				<td class="etichetta">Prezzo tot. offerto (esclusa IVA)</td>
				<td>
					da&nbsp;<input id="searchPrezzoTotaleOffertoMin" type="text" size="10"></input>
					a&nbsp;<input id="searchPrezzoTotaleOffertoMax" type="text" size="10"></input>
				</td>
			</tr>	
		</table>
	</div>
			
	<div id="listaProdottiArticoloContainer" style="width: 950px;"></div>
	
	<div id="confrontoProdottiContainer" style="width: 950px;"></div>	
	
</div>


<gene:redefineInsert name="addToAzioni" >
	<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC.valutazioneprodotti")}'>
		<c:if test='${autorizzatoModifiche eq "1" && isNavigazioneDisattiva ne "1"}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:valutazioneProdotti(${id},'AGGIORNA');" title="Aggiorna dati prodotti" tabindex="1515">Aggiorna dati prodotti</a>
				</td>
			</tr>
		</c:if>
	</c:if>
</gene:redefineInsert>


