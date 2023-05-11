<%
/*
 * Created on: 10-nov-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori relativi alla ditta presenta nella lista delle
 * fasi di ricezione in analisi
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>



<c:choose>
	<c:when test='${not empty param.codiceDitta}'>
		<c:set var="codiceDitta" value="${param.codiceDitta}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceDitta" value="${codiceDitta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.numeroGara}'>
		<c:set var="numeroGara" value="${param.numeroGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroGara" value="${numeroGara}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

	
<div style="width:97%;">


<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DITG-scheda-popup-insrda">
	
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value='Aggiungi ditta da Rda' />
		<gene:formScheda entita="DITG" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitInsDittaRda">
		<c:choose>
			<c:when test='${requestScope.isFornitoreRda eq "false"}'>
			<gene:campoScheda>
				<td colspan="2">
					<br>
					${requestScope.msgFornitoreRda}
					<br>
					<br>
				</td>
				</gene:campoScheda>
				<gene:campoScheda>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">&nbsp;
					</td>
				</gene:campoScheda>
					
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="CODGAR5" visibile="false" />
				<gene:campoScheda campo="NGARA5"  visibile="false" />
				<gene:campoScheda campo="NPROGG" visibile="false" />
				<gene:campoScheda campo="DITTAO" visibile="false" defaultValue="${dittao}"/>
				<gene:campoScheda campo="NOMIMO" visibile="false" defaultValue="${nomimo}"/>
				<gene:campoScheda title="Codice ditta" campo="DITTAO" modificabile="false" campoFittizio="true" definizione="T10" defaultValue="${dittao}"/>
				<gene:campoScheda title="Ragione sociale" campo="NOMIMO" modificabile="false" campoFittizio="true" definizione="T61" defaultValue="${nomimo}"/>
				<gene:campoScheda title="Codice fiscale" campo="CFIMP" modificabile="false" campoFittizio="true" definizione="T16" defaultValue="${cfFornitore}"/>
				<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" modificabile="false" campoFittizio="true" definizione="T16" defaultValue="${pivaFornitore}"/>
				
				<gene:campoScheda campo="ACQUISIZIONE" defaultValue="" visibile="false"/>
				
				<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="" />
				
				<gene:campoScheda>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Importa" title="Importa" onclick="javascript:schedaConferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">&nbsp;
					</td>
				</gene:campoScheda>
			</c:otherwise>
		</c:choose>

		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	
	document.forms[0].jspPathTo.value="gare/ditg/ditg-schedaPopup-insertDaRda.jsp";
	var openerKeyParent = window.opener.document.forms[0].keyParent.value;
	setValue("DITG_NGARA5", openerKeyParent.substr(openerKeyParent.indexOf(":")+1));
	setValue("WIZARD_PAGINA_ATTIVA", window.opener.getValue("WIZARD_PAGINA_ATTIVA"));
	var winOpener = window.opener;
	setValue("DTEOFF", winOpener.getValue("DTEOFF"));
	setValue("OTEOFF", winOpener.getValue("OTEOFF"));
	setValue("DTEPAR", winOpener.getValue("DTEPAR"));
	setValue("OTEPAR", winOpener.getValue("OTEPAR"));

	<c:if test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		window.opener.document.forms[0].pgSort.value = "";
		window.opener.document.forms[0].pgLastSort.value = "";
		window.opener.document.forms[0].pgLastValori.value = "";
		window.opener.bloccaRichiesteServer();
		window.opener.listaVaiAPagina(0);
		window.close();
	</c:if>
	
	
	//<c:if test='${requestScope.isFornitoreRda eq "false"}'>
	//			var dim1=800;
	//			var dim2=300;
	//			window.resizeTo(dim1,dim2);	
	//</c:if>
	
	
	
	
	

	</gene:javaScript>
</gene:template>
</div>