<%
/*
 * Created on: 13-07-2017
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
		Popup per annulare il calcolo dei punteggi tecnici o economici
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<gene:setString name="titoloMaschera" value="Scelta modalita' inserimento documenti" />


<c:set var="modo" value="NUOVO" scope="request" />
	<c:choose>
		<c:when test="${param.stepWizard eq -4 }">
			<c:set var="obbligoBusta" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.QuestionariQForm.obbligoBusta.preq")}'  />
		</c:when>
		<c:otherwise>
			<c:set var="obbligoBusta" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.QuestionariQForm.obbligoBusta.amm")}'  />
		</c:otherwise>
	</c:choose>	
	
	
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" gestisciProtezioni="false" >
		
			<gene:campoScheda>
				<td>
				<br>
					Scegliere la modalità con cui inserire la documentazione della ditta<br><br>
				</td>
			</gene:campoScheda>
			<gene:campoScheda>
				<td>
					<input type="radio" value="1" name="tipoInserimento" id="questionario" <c:if test="${obbligoBusta eq 'true'}">checked="checked"</c:if> />Configurazione guidata dei documenti mediante q-form
					<br>
					<input type="radio" value="2" name="tipoInserimento" id="normale" <c:if test="${obbligoBusta ne 'true'}">checked="checked"</c:if> />Inserimento diretto dei documenti richiesti
					<br>
					<br> 
				</td>
			</gene:campoScheda>
		</gene:formScheda>
		
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		
	</gene:redefineInsert>

	<gene:javaScript>
	
		<c:if test="${obbligoBusta eq 'true'}">
			document.getElementById("normale").disabled = true; 
		</c:if> 
		
		function conferma() {
			var chiaveRiga = "${param.chiaveRiga}";
			var tipo = "${param.tipo}";
			var comunicazioniVis = "${param.comunicazioniVis}";
			var documentiElenco = "${param.documentiElenco}";
			var autorizzatoModifiche = "${param.autorizzatoModifiche}";
			var modalitaQFORM="";
			if($("input[name='tipoInserimento']:checked").val() == 1)
				modalitaQFORM="INSERIMENTOQFORM";
			window.opener.bloccaRichiesteServer();
			window.opener.verificaDocumentiRichiesti(chiaveRiga,tipo,comunicazioniVis,documentiElenco,autorizzatoModifiche,modalitaQFORM);
			window.close();
		}
		
		function annulla(){
			window.close();
		}
	
	
	</gene:javaScript>
</gene:template>
</div>

