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

<c:set var="ngara" value="${param.ngara}" />
<c:set var="codgar" value="${param.codgar}" />
<c:set var="offtel" value="${param.offtel}" />

<c:choose>
	<c:when test='${offtel eq 1}'>
		<c:set var="where" value=''/>
	</c:when>
	<c:otherwise>
		<c:set var="where" value='g1crimod.id not in (select goevmod.idcrimod from goevmod,g1cridef where g1cridef.idgoevmod = goevmod.id and g1cridef.formato != 100)'/>
	</c:otherwise>
</c:choose>

<c:if test='${not empty requestScope.RISULTATO and requestScope.RISULTATO eq "OK"}' >
	<script type="text/javascript">
		opener.historyReload();
		window.close();
	</script>
</c:if>

<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Importa criteri di valutazione da modello"/>
	<gene:redefineInsert name="corpo">
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	<table class="dettaglio-notab">
	
		<tr>
			<td colspan="2">
				Mediante questa funzione è possibile importare nella gara corrente i criteri di valutazione di un modello.
				<br><br>
				Scegliere il modello da cui importare i criteri di valutazione.
				<br>
				<c:if test="${!empty where}">
					<b>Attenzione:</b> Date le caratteristiche della gara, la lista sottostante riporta solo i modelli che hanno tutti i criteri senza formato.  
					<br>
				</c:if>
				<br>
				<b>Attenzione: la lista originale dei criteri di valutazione della gara verr&agrave; cancellata</b>
				<br><br>		
				<gene:formLista entita="G1CRIMOD" sortColumn="2" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreImportaCriteriModello">
					<gene:campoLista title=" " width="50"	>
						<c:if test="${currentRow >= 0}">
							<input type="radio" name="modello" value="${chiaveRiga}" />
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="ID" visibile="false"/>
					<gene:campoLista campo="TITOLO" headerClass="sortable" width="1000"/>
					<gene:campoLista campo="DESCRI" width="1000" />
					
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
					<input type="hidden" name="codiceGara" id="codiceGara" value="${codgar}" />
					
				</gene:formLista>
			</td>
		</tr>
		
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<INPUT type="button" class="bottone-azione" value="Importa criteri" title="Importa criteri" onclick="javascript:confermaImportaCriteri();">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
			</td>
			
		</tr>
		
	</table>
	
	</gene:redefineInsert>
	
	
	<gene:javaScript>	
	
	console.log("offtel = ${offtel}");
	
	function confermaImportaCriteri(){
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
