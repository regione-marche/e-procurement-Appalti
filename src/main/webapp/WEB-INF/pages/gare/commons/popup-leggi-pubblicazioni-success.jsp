
<%
	/*
	 * Created on 03-lug-2018
	 *
	 * Copyright (c) Maggioli S.p.A.
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
<%@ page import="net.sf.json.JSONArray"%>
<%@ page import="net.sf.json.JSONObject"%>

<style TYPE="text/css">
.lista{
	 border-collapse:collapse;
	 padding-bottom: 10px;
}


</style>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<c:set var="codgar" value="${param.codgar}" />
	<gene:setString name="titoloMaschera"  value='Invio dei dati a Formulari Europei per la creazione dei formulari GUUE'  />


	<gene:redefineInsert name="corpo">
	
		<table class="lista" cellspacing ="0">
			<c:choose>
				<c:when test="${empty numeroPubblicazioni}">
					<p>Non è presente nessuna nuova pubblicazione per la gara</p>
				</c:when>
				<c:otherwise>
					<p>Sono presenti <b>${numeroPubblicazioni}</b> nuove pubblicazioni per la gara:</p>
				</c:otherwise>
			</c:choose>
			<c:set var="array" value="${pubblicazioni}"/>
			<c:set var="cnt" value="0" />
			<ul>
			<c:forEach items="${pubblicazioni}" var="pubblicazioni">
				<c:set var="cnt" value="${cnt + 1}" />
				<li>${pubblicazioni[1]} - numero '${pubblicazioni[2]}' - pubblicato in data ${pubblicazioni[0]}</li>
			</c:forEach>
			</ul>
			<tr style="height:10px;">
			
			</tr>
			<tr class="comandi-dettaglio">
				<td colspan=2>
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	
		function annulla(){
			window.close();
		}
	
	</gene:javaScript>	

</gene:template>

</div>