<%
	/*
	 * Created on 09-dic-2008
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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetImportiComplessiviFunction" parametro="${param.codiceLavoro}" />

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	<gene:setString name="key" value="TORN.CODGAR=T:${param.codiceLavoro}" />
	<gene:setString name="titoloMaschera" value="Verifica congruit&agrave; importo complessivo della gara" />
	<gene:redefineInsert name="corpo">

		<table class="dettaglio-notab">
			<tr>
				<td colspan="2" >
					<br>
					<c:choose>
						<c:when test='${requestScope.importoGara eq requestScope.totaleImportiGare}'>
							L'importo complessivo della gara &egrave; uguale alla somma degli importi a base di gara dei singoli lotti.
						</c:when>
						<c:otherwise>
							L'importo complessivo della gara &egrave; diverso dalla somma degli importi a base di gara dei singoli lotti.
						</c:otherwise>
					</c:choose>
					<br>&nbsp;
				</td>
			</tr>
		</table>
		<table class="dettaglio-notab">
			<tr>
				<td class="etichetta-dato">Importo complessivo</td>
				<td class="valore-dato">${requestScope.importoGara}
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Somma degli importi dei singoli lotti</td>
				<td class="valore-dato">${requestScope.totaleImportiGare}
				</td>
			</tr>
			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<input type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();" />&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
</gene:template>