<%
	/*
	 * Created on 10-09-2012
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetImportiComplessiviFunction" parametro="${param.codiceGara}" />

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	<gene:setString name="key" value="TORN.CODGAR=T:${param.codiceGara}" />
	<gene:setString name="titoloMaschera" value="Modifica importo complessivo della gara ${param.codiceGara}" />
	<gene:redefineInsert name="corpo">

		<table class="dettaglio-notab">
			<tr>
				<td colspan="2" >
					<br>
					Mediante questa funzione è possibile modificare l'importo complessivo della gara quando questo non coincide con la somma degli importi dei singoli lotti.
					<br><br><b>ATTENZIONE: L'eventuale successiva modifica dell'importo dei lotti comporta la sovrascrittura dell'importo modificato</b>
					<br>&nbsp;
				</td>
			</tr>
		</table>
		<table class="dettaglio-notab">
			<tr>
				<td class="etichetta-dato">Importo complessivo</td>
				<td class="valore-dato"><input typpe="text" value="${param.importo}" size="20" class="importo" maxlength="17" id="importo" onchange="checkImporto(this.value);"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Somma degli importi dei singoli lotti</td>
				<td class="valore-dato">${requestScope.totaleImportiGare}
				</td>
			</tr>
			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<input type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:conferma();" />&nbsp;
					<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();" />&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		function conferma() {
			var importo=document.getElementById("importo").value;
			window.opener.setValue("TORN_IMPTOR", importo);
			window.opener.setValue("IMPTOR_FIT", importo);
			window.close();
		}
		
		var defaultImporto = "${param.importo}";
		function checkImporto(value){
			var msg; 
			if(!isNumericStr(value,true)) {
				msg = "Attenzione:\nSi e' inserito un valore di campo non consentito ! \nI caratteri ammessi sono le cifre, il punto come\nseparatore decimale ed eventualmente il segno";
				alert(msg);
				$("#importo").val(defaultImporto);
				return;
			}
			value=String(round(parseFloat(value), 2));
			if(value){
				value=String(round(parseFloat(value), 2));
			}
			
		}
		
	</gene:javaScript>
</gene:template>
