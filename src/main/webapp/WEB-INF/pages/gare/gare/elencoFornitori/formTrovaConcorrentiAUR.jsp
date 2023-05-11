<%
/*
 * Created on 18-lug-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI TROVA CONCORRENTI AUR
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="contenitore-popup">
<html:form action="/ElencoFornitori" >
	<table class="ricerca">
	    <tr>
	      <td class="etichetta-dato">Ragione sociale</td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="i_NAME1" styleId="i_NAME1" value="" size="50" maxlength="140"/>
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato">Codice fiscale</td>
	      <td class="valore-dato-trova">
	      	<html:text property="i_STCD1" styleId="i_STCD1" value="" size="25" maxlength="16"/>
	      </td>
	    </tr>

	    <tr>
	      <td class="etichetta-dato">Partita IVA</td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="i_STCD2" styleId="i_STCD2" value="" size="25" maxlength="11"/>
	      </td>
	    </tr>

			<!--tr>
	    	<td class="flag-sensitive" colspan="3">
	    		<!--html-:-select property="risPerPagina"-->
		      	<!--html-:-options name="listaRisPerPagina" labelName="listaRisPerPagina" /-->
					<!--/html-:-select--><!--&nbsp;Risultati per pagina-->
				<!--/td>
			</tr-->
	    <tr>
	      <td class="comandi-dettaglio" colSpan="3">
	      	<INPUT type="button" class="bottone-azione" value="Trova" title="Trova utenti" onclick="javascript:avviaRicercaConcorrentiAUR()">&nbsp;
	        <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reset dei campi di ricerca" onclick="javascript:nuovaRicerca()">&nbsp;
	        <INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi finestra" onclick="javascript:window.close();">&nbsp;
	        <INPUT type="hidden" name="metodo" value="trovaDitta">
	        <INPUT type="hidden" name="ngara" value="${ngara}">
	        <INPUT type="hidden" name="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}">
	        <INPUT type="hidden" name="numeroFaseAttiva" value="${numeroFaseAttiva}">
	        <INPUT type="hidden" name="codStazioneAppaltante" value="${codStazioneAppaltante}">
	        &nbsp;
	      </td>
	    </tr>
	</table>
</html:form>
</div>