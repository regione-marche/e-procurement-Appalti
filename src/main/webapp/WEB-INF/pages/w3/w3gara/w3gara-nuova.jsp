<%
/*
 * Created on: 03/09/2012
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
<gene:template file="scheda-template.jsp">

<gene:setString name="titoloMaschera" value='Creazione di una nuova anagrafica gara' />
<gene:redefineInsert name="addHistory" />
<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>

<gene:redefineInsert name="addToAzioni" >
	<tr>
		<td class="vocemenulaterale" >
			<a href="javascript:nuova();" title="Avanti" tabindex="1503">Avanti</a>
		</td>
	</tr>
</gene:redefineInsert>

<gene:redefineInsert name="corpo">

	<table class="dettaglio-notab">
		<tr>
			<td class="valore-dato" colspan="2"><input type="radio" name="tipo" id="GARA" value="1" onclick=""> <b>finalizzata alla richiesta CIG</b></td>
		</tr>
		<tr>
			<td class="valore-dato" colspan="2"><input type="radio" name="tipo" id="SCIG" value="1" onclick=""> <b>finalizzata alla richiesta SMARTCIG</b></td>
		</tr>
		<tr class="comandi-dettaglio">
			<td class="comandi-dettaglio" colspan="2">
				<input type="button" value="Indietro" title="Indietro" class="bottone-azione" onclick="javascript:historyVaiIndietroDi(1);"/>&nbsp;&nbsp;
				<input type="button" value="Avanti" title="Avanti" class="bottone-azione" onclick="javascript:nuova();"/>&nbsp;
			</td>
		</tr>
	</table>

</gene:redefineInsert>

<gene:javaScript>

	function nuova(){
		if (document.getElementById("GARA").checked) {
			location.href = '${pageContext.request.contextPath}/ApriPagina.do?' + csrfToken + '&href=w3/w3gara/w3gara-scheda.jsp&modo=NUOVO';
		} else if (document.getElementById("SCIG").checked) {
			location.href = '${pageContext.request.contextPath}/ApriPagina.do?' + csrfToken + '&href=w3/w3smartcig/w3smartcig-scheda.jsp&modo=NUOVO';
		}
	}
	
	function Annulla(){
		document.location.href = '${pageContext.request.contextPath}/Home.do' + csrfToken;
	}

</gene:javaScript>

</gene:template>

