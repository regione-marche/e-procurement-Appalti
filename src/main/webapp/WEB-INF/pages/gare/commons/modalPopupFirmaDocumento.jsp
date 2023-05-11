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

<style type="text/css">
			.ui-dialog-titlebar {
				display: none;
			}
			#sottoTitolo{
				font:11px Verdana, Arial, Helvetica, sans-serif;
			}
			DIV.error {
				color: #E40000; 
				font-weight: bold;
				padding-left: 0px;
				padding-right: 0px;
				padding-top: 10px;
				padding-bottom: 10px;
			}
			input.readonly {
				border-width: 0px;
				background-color: #FFFFFF;
				color: #000000';
			}
</style>
		
<div id="mascheraParametriWSDM" title="Firma documento" style="display:none">

			<form id="parametriWSDM">
				<table class="dettaglio-notab">
					<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
					<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
					<input id="tiposistemaremoto" type="hidden" value="ITALPROT" />
					<input id="tabellatiInDB" type="hidden" value="" />
					<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
					<input type="hidden" name="gestioneFascicoloWSDM" id="gestioneFascicoloWSDM" value="" />
					<input id="modoapertura" type="hidden" value="MODIFICA" /> 
					<input id="entita" type="hidden" value="${param.entita}" /> 
					<input id="key1" type="hidden" value="${param.key1}" /> 
					<input id="key2" type="hidden" value="${param.key2}" />
					<input id="key3" type="hidden" value="${param.key3}" />
					<input id="key4" type="hidden" value="${param.key4}" />
					<input type="hidden" id="idconfi" name="idconfi" value="${idconfi}" />
					<input type="hidden" id="oggettoDocumento" name="oggettoDocumento" value="${param.oggettoDoc}" />
					<input type="hidden" id=codiceFascicolo name="codiceFascicolo">
					<input type="hidden" id="classificaFascicolo" name="classificaFascicolo">
					<input type="hidden" id="annoFascicolo" name="annoFascicolo">
					
					<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
					<tr id=sezioneErrori style="display: none;">
						<td colspan="2">
							<div class="error" id="erroriTrasmissione"></div>
						</td>
					</tr>		
					
					<tr id="sezionedatidocumentale">
						<td colspan="2">
							<br>
							<b>Dati dell'elemento documentale </b>
						</td>				
					</tr>
					<tr>
						<td class="etichetta-dato">Oggetto</td>
						<td class="valore-dato">
							<textarea id="oggetto" name="oggetto" title="Oggetto" class="testo" rows="4" cols="45"></textarea>
						</td>						
					</tr>
					<tr>
						<td class="etichetta-dato">Firmatario</td>
						<td class="valore-dato">
							<select id="firmatario" name="firmatario"></select>
						</td>						
					</tr>
					<tr>
						<td class="etichetta-dato">Ufficio firmatario </td>
						<td class="valore-dato">
							<select id="ufficiofirmatario" name="ufficiofirmatario"></select>
						</td>						
					</tr>
				</table>
			</form>		
				
		
	</div>

