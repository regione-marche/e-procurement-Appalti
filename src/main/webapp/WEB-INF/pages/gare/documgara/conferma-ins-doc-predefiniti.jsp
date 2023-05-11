<%
/*
 * Created on: 24-09-2021
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
		Finestra che visualizza la conferma per l'inserimento della documentazione predefinita
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:set var="tmp" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetWhereInsDatiDocumenti", pageContext)}' />

<c:choose>
	<c:when test='${not empty requestScope.documentiInseriti and requestScope.documentiInseriti eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Inserimento documenti predefiniti' />
<gene:redefineInsert name="corpo">
	
<c:set var="modo" value="MODIFICA" scope="request" />
	<table class="dettaglio-notab">
	
		<tr>
			<td colspan="2">
				Mediante questa funzione &egrave possibile inserire i documenti nella gara mediante selezione da un modello di documenti predefiniti.
				<br><br>
				Selezionare il modello da cui importare i documenti.
				<br>
	
	<gene:formLista entita="G1DOCUMOD" sortColumn="0" where="${requestScope.where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInsertDocumentiPredefiniti">
		<gene:campoLista title=" " width="50"	>
			<c:if test="${currentRow >= 0}">
				<input type="radio" name="modello" value="${chiaveRiga}" />
			</c:if>
		</gene:campoLista>
		<gene:campoLista campo="ID" visibile="false"/>
		<gene:campoLista campo="DESCRIZIONE" headerClass="sortable"/>
		<gene:campoLista campo="LIMINF" title="Da importo"/>
		<gene:campoLista campo="LIMSUP" title="A importo"/>
		<gene:campoLista title="N.doc." campo="N_DOC_MOD"
							entita="G1DOCUMOD" width="32" campoFittizio="true"
							definizione="N7;;;" value=""
							gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroDocumentiPerModello" />

		<input type="hidden" name="codgar" id="codgar" value="${param.codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${param.ngara}" />
		<input type="hidden" name="tiplav" id="tiplav" value="${param.tiplav}" />
		<input type="hidden" name="tipgarg" id="tipgarg" value="${param.tipgarg}" />
		<input type="hidden" name="lottodigara" id="lottodigara" value="${param.lottodigara}" />
		<input type="hidden" name="isoffertaunica" id="isoffertaunica" value="${param.isoffertaunica}" />
		<input type="hidden" name="importo" id="importo" value="${param.importo}" />
		<input type="hidden" name="critlic" id="critlic" value="${param.critlic}" />
		<input type="hidden" name="faseinvito" id="faseinvito" value="${param.faseinvito}" />
		<input type="hidden" name="tipologia" id="tipologia" value="${param.tipologia}" />
		<input type="hidden" name="busta" id="busta" value="${param.busta}" />
		<input type="hidden" name="gruppo" id="gruppo" value="${param.gruppo}" />
		<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${param.isProceduraTelematica}" />
		
		
	</gene:formLista>
			</td>
		</tr>
		
	</table>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
		var controllo = false;
		var modello = document.forms[0].modello;
		
		if (modello.checked) {
		controllo=true;
		}
		
		for(i=0; i < modello.length; i++) {
			if(modello[i].checked) {
				controllo=true;
			}
		}
		
		if(!controllo) {
				alert("Selezionare un modello nella lista");
			}
		
		if (controllo) {
				document.forms[0].jspPathTo.value="gare/documgara/conferma-ins-doc-predefiniti.jsp";
				listaConferma();
			}
		}
		
		function annulla(){
			window.close();
		}	
		
		function annulla(){
			window.close();
		}
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>