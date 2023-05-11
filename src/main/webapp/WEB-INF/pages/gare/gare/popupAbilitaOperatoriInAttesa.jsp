<%
/*
 * Created on: 30-04-2018
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
		Finestra per la valorizzazione dei campi CODCIG, DACQCIG 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty genere}'>
		<c:set var="genere" value='${genere}' />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${param.genere}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<gene:setString name="titoloMaschera" value='Abilitazione operatori in attesa verifica' />	

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAbilitaOperatoriInAttesa">
		
		<c:choose>
			<c:when test="${empty esito or esito ne '1' }">
				<gene:campoScheda>
					<c:choose>
						<c:when test="${genere eq 10 }">
							<c:set var="msgGenere" value="elenco"/>
						</c:when>
						<c:otherwise>
							<c:set var="msgGenere" value="catalogo"/>
						</c:otherwise>
					</c:choose>
					<td colSpan="2">Mediante questa funzione si procede all'abilitazione degli operatori in ${msgGenere } che sono in stato 'Attesa verifica domanda' e hanno presentato domanda d'iscrizione 
					prima del termine specificato di seguito.
					<br><br><b>ATTENZIONE:</b> L'operazione non comporta l'invio di alcuna comunicazione agli operatori abilitati.<br><br>
					</td>
				</gene:campoScheda>
				<gene:campoScheda>
					<td colSpan="2"><br></td>
				</gene:campoScheda>
				<gene:campoScheda campo="DATLIMITE" title="Data entro cui considerare le domande d'iscrizione"  campoFittizio="true" definizione="D;0" obbligatorio="true" >
					<gene:checkCampoScheda funzione='confrontoDate("#DATABILITAZ#", "##")' obbligatorio="true" messaggio="La data di abilitazione deve essere successiva o uguale della data entro cui considerare le domande d'iscrizione." onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="DATABILITAZ" title="Data abilitazione"  campoFittizio="true" definizione="D;0" obbligatorio="true" >
					<gene:checkCampoScheda funzione='confrontoDate("##","#DATLIMITE#")' obbligatorio="true" messaggio="La data di abilitazione deve essere successiva o uguale della data entro cui considerare le domande d'iscrizione." onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda>
					<td colSpan="2"><br></td>
				</gene:campoScheda>
			</c:when>
			<c:otherwise>
				<gene:campoScheda>
					<td colSpan="2">Operazione completata.
					<br><br>Sono stati abilitati ${operatoriInAttesa } operatori.<br><br></td>
				</gene:campoScheda>
			</c:otherwise>
		</c:choose>
		
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="genere" id="genere" value="${genere }"/>
	</gene:formScheda>
  </gene:redefineInsert>
<c:if test='${not empty requestScope.esito and requestScope.esito eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>	
	
	<gene:javaScript>
		
		<c:if test='${not empty requestScope.esito and requestScope.esito eq "1" and operatoriInAttesa >0}' >
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
		</c:if>
				
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/popupAbilitaOperatoriInAttesa.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		// La funzione verifica se la data di abilitazione è maggiore o uguale della 
		//data limite
		function confrontoDate(dataAbilitazione,dataLimite){
			var esito=true;
			if(dataAbilitazione != null && dataLimite!=null && dataAbilitazione != "" && dataLimite != ""){
				var dataSplittata=dataAbilitazione.split("/");
			    var d1 = new Date(dataSplittata[1] + "/" + dataSplittata[0] + "/" + dataSplittata[2]);

				dataSplittata=dataLimite.split("/");
			    var d2 = new Date(dataSplittata[1] + "/" + dataSplittata[0] + "/" + dataSplittata[2]);

				if(d1 < d2) 
					esito=false;
			}
			return esito;
		}	
		
	</gene:javaScript>
</gene:template>
</div>


