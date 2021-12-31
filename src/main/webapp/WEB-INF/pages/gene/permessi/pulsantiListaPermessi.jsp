<%
/*
 * Created on 11-07-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
  //PAGINA CHE CONTIENE I PULSANTI DELLA LISTA DEI PERMESSI E CHE IN CASO DI PERSONALIZZAZIONE
  //DOVRAì ESSERE RIDEFINITA NEI PROGETTI.
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="modificaAbilitata" value="true" />
<c:if test='${fn:containsIgnoreCase(campoChiave, "IDMERIC") || fn:containsIgnoreCase(campoChiave, "CODGAR")}'>
	<c:set var="autotizzatoModifica" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetAutorizzatoModificaFunction", pageContext, campoChiave, valoreChiave,"1")}'/>
	<c:if test="${autotizzatoModifica ne 'true' }">
		<c:set var="modificaAbilitata" value="false" />
	</c:if>	
</c:if>
<c:if test="${modificaAbilitata eq 'true' }">
<tr>
	<td class="comandi-dettaglio" colSpan="2">
    	<INPUT type="button" class="bottone-azione" value="Modifica condivisione" title="Modifica condivisione lavoro" onclick="javascript:modificaCondivisioneLavoro();">
   		&nbsp;
	</td>
</tr>
</c:if>
