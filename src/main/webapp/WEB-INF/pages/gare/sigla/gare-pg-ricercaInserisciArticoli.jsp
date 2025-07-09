<%
	/*
	 * Created on 29-Set-2017
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>


<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script></script>
		<script type="text/javascript" src="${contextPath}/js/general.js"></script></script>
			
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value="Ricerca Articoli" />

	<html:form action="/TrovaArticolo" >
		<table class="ricerca">
	    <tr>
	      <td class="etichetta-dato" >Codice</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreCodice" value="${trovaArticoloForm.operatoreCodice}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="codice" styleId="codice" value="${trovaArticoloForm.codice}" size="50" maxlength="60"/>
	      </td>
		  <td class="operatore-trova">
			<html:radio property="codiceCondition" styleId="conditionAND" value="AND"/>&nbsp;AND
			<html:radio property="codiceCondition" styleId="conditionOR" value="OR"/>&nbsp;OR
		  </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Descrizione</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreDescrizione" value="${trovaArticoloForm.operatoreDescrizione}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="descrizione" styleId="descrizione" value="${trovaArticoloForm.descrizione}" size="50" maxlength="60"/>
	      </td>
		  <td class="operatore-trova">
			<html:radio property="descrizioneCondition" styleId="conditionAND" value="AND"/>&nbsp;AND
			<html:radio property="descrizioneCondition" styleId="conditionOR" value="OR"/>&nbsp;OR
		  </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Unita' di misura</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreUnitaDiMisura" value="${trovaArticoloForm.operatoreUnitaDiMisura}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="unitaDiMisura" styleId="unitaDiMisura" value="${trovaArticoloForm.unitaDiMisura}" size="50" maxlength="60"/>	      	
	      </td>
		  <td class="operatore-trova">
			<html:radio property="unitaDiMisuraCondition" styleId="conditionAND" value="AND"/>&nbsp;AND
			<html:radio property="unitaDiMisuraCondition" styleId="conditionOR" value="OR"/>&nbsp;OR
		  </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Gestione inventario</td>
	      <td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova">
			<html:select property="gestioneInventario">
	      		<html:options name="booleani" labelName="booleaniLabel" />
			</html:select>
	      </td>
		  <td class="operatore-trova">
			<html:radio property="gestioneInventarioCondition" styleId="conditionAND" value="AND"/>&nbsp;AND
			<html:radio property="gestioneInventarioCondition" styleId="conditionOR" value="OR"/>&nbsp;OR
		  </td>
	    </tr>
		<tr>
	      <td class="etichetta-dato" >Gestione magazzino</td>
	      <td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova"> 
			<html:select property="gestioneMagazzino">
	      		<html:options name="booleani" labelName="booleaniLabel" />
			</html:select>
	      </td>
		  <td class="operatore-trova">
			<html:radio property="gestioneMagazzinoCondition" styleId="conditionAND" value="AND"/>&nbsp;AND
			<html:radio property="gestioneMagazzinoCondition" styleId="conditionOR" value="OR"/>&nbsp;OR
		  </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Codice IVA</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreCodiceIva" value="${trovaArticoloForm.operatoreCodiceIva}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="codiceIva" styleId="codiceIva" value="${trovaArticoloForm.codiceIva}" size="50" maxlength="60"/>	      	
	      </td>
		  <td class="operatore-trova">
			<html:radio property="codiceIvaCondition" styleId="conditionAND" value="AND"/>&nbsp;AND
			<html:radio property="codiceIvaCondition" styleId="conditionOR" value="OR"/>&nbsp;OR
		  </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Tipo di gestione</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreTipoGestione" value="${trovaArticoloForm.operatoreTipoGestione}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="tipoGestione" styleId="tipoGestione" value="${trovaArticoloForm.tipoGestione}" size="50" maxlength="60"/>	      	
	      </td>
		  <td class="operatore-trova">
			<html:radio property="tipoGestioneCondition" styleId="conditionAND" value="AND"/>&nbsp;AND
			<html:radio property="tipoGestioneCondition" styleId="conditionOR" value="OR"/>&nbsp;OR
		  </td>
	    </tr>
		
		<jsp:include page="/WEB-INF/pages/gare/sigla/opzioniTrova.jsp" />

	    <tr>
	      <td class="comandi-dettaglio" colSpan="4">
	      	<INPUT type="button" class="bottone-azione" value="Trova" title="Trova articoli" onclick="javascript:avviaRicercaArticoli()">
	        <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reset dei campi di ricerca" onclick="javascript:nuovaRicerca()">
	        <input type="hidden" name="metodo" />
	        <input type="hidden" name="ngara" id="ngara" value="${param.ngara}" />
			<input type="hidden" name="codein" id="codein" value="${param.codein}" />
	        &nbsp;
	      </td>
	    </tr>
	</table>
	</html:form>	

	</gene:redefineInsert>

	<gene:javaScript>

	<c:choose>
		<c:when test="${trovaArticoloForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
		
		function nuovaRicerca(){
			document.trovaArticoloForm.metodo.value="apriNuovo";
			document.trovaArticoloForm.submit();
		}
		function avviaRicercaArticoli(){
			document.trovaArticoloForm.metodo.value="trova";
			document.trovaArticoloForm.submit();
		}
		function gestisciVisualizzazioneAvanzata() {
			var checkboxavanzate = document.getElementById("visualizzazioneAvanzata");
			var test = checkboxavanzate.checked;
			if (test == true) {
				trovaVisualizzazioneOperatori('visualizza');
			} else {
				nuovaRicerca();
			}
		}
		
		function trovaVisualizzazioneOperatori(vis) {
	
			var tablesricerca = [];
			
			var tables = document.getElementsByTagName('table');
			var ricercaclass = 'ricerca';
			var regularexp = new RegExp('\\b' + ricercaclass + '\\b');
			for (var i=0; i < tables.length; i++) {
				if (regularexp.test(tables[i].className)) {
					tablesricerca.push(tables[i]);
				}
			}
			
			if (tablesricerca.length > 0) {
				var rows = tablesricerca[0].getElementsByTagName('tr');			
			
				for (var row=0; row<rows.length; row++) {
					var cels = rows[row].getElementsByTagName('td');
					if (cels.length >=3) {
						if (vis == "visualizza") {
							cels[1].style.display='';
							cels[2].style.width=478;
							cels[3].style.display='';							
						} else {
							cels[1].style.display='none';
							cels[2].style.width=580;
							cels[3].style.display='none';
						}
					}
				}
			}
		}
		
	</gene:javaScript>

</gene:template>

