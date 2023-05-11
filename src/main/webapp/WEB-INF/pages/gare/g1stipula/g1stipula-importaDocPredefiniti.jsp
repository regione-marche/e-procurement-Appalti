<%
/*
 * Created on: 10-10-2018
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="idStipula" value="${param.idStipula}" />

<c:set var="where" value='g1documod.gruppo=20'/>

<c:if test='${not empty requestScope.RISULTATO and requestScope.RISULTATO eq "OK"}' >
	<script type="text/javascript">
		opener.historyReload();
		window.close();
	</script>
</c:if>

<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Inserimento documenti predefiniti"/>
	<gene:redefineInsert name="corpo">
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	<table class="dettaglio-notab">
	
		<tr>
			<td colspan="2">
				Mediante questa funzione è possibile inserire i documenti nella stipula mediante selezione da un modello di documenti predefiniti.
				<br><br>
				Selezionare il modello da cui importare i documenti.
				<br>
				<gene:formLista entita="G1DOCUMOD" sortColumn="2" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreImportaDocumentiStipula">
					<gene:campoLista title=" " width="50"	>
						<c:if test="${currentRow >= 0}">
							<input type="radio" name="modello" value="${chiaveRiga}" />
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="ID" visibile="false"/>
					<gene:campoLista campo="DESCRIZIONE" headerClass="sortable"/>
					<gene:campoLista campo="ESCLUSO"  width="80"/>
					<gene:campoLista campo="LIMINF" title="Da importo"/>
					<gene:campoLista campo="LIMSUP" title="A importo"/>
					
					<input type="hidden" name="idStipula" id="idStipula" value="${idStipula}" />
					
				</gene:formLista>
			</td>
		</tr>
		
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:confermaImportaDocumenti();">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
			</td>
			
		</tr>
		
	</table>
	
	</gene:redefineInsert>
	
	
	<gene:javaScript>	
	

	function confermaImportaDocumenti(){
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
			listaConferma();
		}
	}
	
	function annulla(){
		window.close();
	}
		
	</gene:javaScript>
	
</gene:template>
