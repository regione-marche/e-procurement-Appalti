
<%
	/*
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

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<gene:setString name="titoloMaschera" value='Pubblicazione della gara e dei lotti'  />
	<gene:redefineInsert name="corpo">
		<table class="lista">
		<c:choose>
		<c:when test="${garaGiaPubblicata ne 'true'}">	
			<tr>
				<br>
				La pubblicazione presso l'ANAC è avvenuta con successo.
				<br>
				<br>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<br>
				La gara in ANAC risulta <b>già perfezionata/pubblicata</b>.
				<br>
				<br>
				Sono state <b>aggiornate</b> le informazioni relative allo <b>stato della gara</b> e alla <b>data dell'operazione perfezionamento/pubblicazione</b>.
				<br>
				<br>
			</tr>
		</c:otherwise>
		</c:choose>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		window.opener.selezionaPagina(0);
		
		function chiudi(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

