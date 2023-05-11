<%
/*
 * Created on: 02-04-2020
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>


<tr>
	<td colspan="2">
		<b>Selezione operatore</b>
		<br>Impostare un filtro sul cognome operatore, quindi cliccare su 'Ricerca operatori' per ottenere la lista degli operatori. Selezionare l'operatore a cui fare la trasmissione.
	</td>
</tr>
<tr id="rigaFiltro">
	<td class="etichetta-dato">Filtro su cognome operatore (*)</td>
	<td class="valore-dato">
		<input id="filtroCognome" name="filtroCognome" title="Filtro su cognome operatore" class="testo" type="text" size="24" value="" maxlength="100"> &nbsp;<a href="javascript:caricamentoTabellatoOperatori();" id="linklCaricaOperatori" >Ricerca operatori</a>
	</td>
</tr>
<tr id="rigaOperatori">
	<td colspan="2">
	<div style="display: none;" class="error" id="operatorimessaggio"></div>
	<br>
	<div id="operatoricontainer"></div>
	<br>
	<br>
	</td>
</tr>
<tr id="rigaTipiTrasmissione">
	<td class="etichetta-dato">Tipo trasmissione (*)</td>
	<td class="valore-dato">
		<select id="tipiTrasmissione" name="tipiTrasmissione"></select>
		
	</td>
</tr>
<tr id="rigaPagina" style="display: none;">
	<td class="etichetta-dato">Pagina</td>
	<td class="valore-dato">
		<input id="pagina" name="pagina" title="pagina" class="testo" type="text" size="24" value="${param.tipoPagina }" maxlength="100">
	</td>
</tr>