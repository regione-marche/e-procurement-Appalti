<%
/*
 * Created on 16-12-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA ELECO FORNITORI 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:if test='${not empty RISULTATO and RISULTATO eq "OK"}' >
	<script type="text/javascript">
	
		
		
				window.opener.document.forms[0].pgSort.value = "";
				window.opener.document.forms[0].pgLastSort.value = "";
				window.opener.document.forms[0].pgLastValori.value = "";
				window.opener.bloccaRichiesteServer();
				window.opener.listaVaiAPagina(0);
				window.close();
		
	 
	</script>
</c:if>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="apice" value="\\'" />

<div class="contenitore-popup">
	<table class="ricerca">
		<tr>
			<td>
				<display:table name="listaElencoFornitori" id="Fornitore" class="datilista" pagesize="25" sort="list" size="${numeroTotaleFornitoriEstratti}" >
					<display:column title="Codice fornitore" >
						<a href="${contextPath}/pg/InserimentoConcorrenti.do?codice=${Fornitore.codiceFornitore}&ngara=${param.ngara }&garaLottiConOffertaUnica=${param.garaLottiConOffertaUnica}&numeroFaseAttiva=${param.numeroFaseAttiva}&codStazioneAppaltante=${param.codStazioneAppaltante }" Title='Seleziona fornitore'>
							${Fornitore.codiceFornitore}
						</a>
					</display:column>
					<display:column title="Ragione sociale">
						<a href="${contextPath}/pg/InserimentoConcorrenti.do?codice=${Fornitore.codiceFornitore}&ngara=${param.ngara }&garaLottiConOffertaUnica=${param.garaLottiConOffertaUnica}&numeroFaseAttiva=${param.numeroFaseAttiva}&codStazioneAppaltante=${param.codStazioneAppaltante }" Title='Seleziona fornitore'>
							${Fornitore.ragSoc}
						</a>
					</display:column>
					<display:column title="Indirizzo">${Fornitore.indirizzo}</display:column>
					<display:column title="Comune">${Fornitore.comune}</display:column>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();" >
	        &nbsp;
	     </td>
	  </tr>
	</table>
</div>
