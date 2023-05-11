<%
/*
 * Created on 16-12-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA CARRELLI FABBISOGNI 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<form action="" name="formRadioBut">
	<table class="dettaglio-notab">
	  <tr>
	  	<td>
	  		<table class="dettaglio-notab" id="Modello">
	  			<tr><br><b>Selezionare il modello da cui creare la nuova comunicazione:</b></tr>
				<tr>
					<br>&nbsp;<input type="radio" name="numModello" value="0" checked="CHECKED"/>&nbsp;
					Comunicazione vuota<br><br>
					
				</tr>	
				<c:forEach items="${listaModelliComunicazioni}" var="Modello" varStatus="stato">
					<tr>
						&nbsp;<input type="radio" name="numModello" value="${Modello[0]}" />&nbsp;
						${Modello[1]}<br>
						&nbsp;&nbsp;<i>${Modello[2]}</i><br><br>
					</tr>	
				</c:forEach>
						
			</table>
		</td>
	   </tr>	
	    <td class="comandi-dettaglio" colSpan="2">
		      	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();" >&nbsp;&nbsp;&nbsp;&nbsp;
		      	<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaComunicazione();">&nbsp;
	    </td>
	  </tr>
	</table>	
</form>

<form name="listaNuovo" action="${contextPath}/Lista.do" method="post">
	<input type="hidden" name="jspPath" value="" /> 
	<input type="hidden" name="jspPathTo" value="" /> 
	<input type="hidden" name="activePage" value="0" /> 
	<input type="hidden" name="isPopUp" value="0" /> 
	<input type="hidden" name="numeroPopUp" value="0" /> 
	<input type="hidden" name="metodo" value="nuovo" /> 
	<input type="hidden" name="entita" value="" /> 
	<input type="hidden" name="gestisciProtezioni" value="1" />
</form>