<%
/*
 * Created on: 21/09/2023
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
		Finestra pop-up per fare l'export in formato XML delle ditte per gli accertamenti antimafia
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<c:set var="ngara" value='${param.ngara}' />
	<c:set var="sql" value="select 1 from torn, gare where codgar=codgar1 and ngara= ? 
			and ((tipoconc=1 and not (dittap is null and exists(select 1 from ditg where ngara5=ngara and (ammgar is null or ammgar='1')))) 
				or 
				(tipoconc=2 and exists(select 1 from gare g where g.ngarapa=gare.ngara and g.isriconclusa ='1')))"/>
	
	<c:set var="controlliSuperati" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, ngara)}'/>
	
	<gene:setString name="titoloMaschera" value="Rimuovi anonimato" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
	
		<c:set var="modo" value="NUOVO" scope="request" />
		
		<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRimuoviAnonimatoConcorso">
		
			<gene:campoScheda>
				<td colspan=2>
				<br>
				<c:choose>
					<c:when test="${empty controlliSuperati && empty operazioneEseguita}">
						<c:choose>
							<c:when test="${param.tipoconc eq '1'}">
								Non è possibile procedere perché non è stato completato il calcolo dell'aggiudicazione.
								<br>
							</c:when>
							<c:otherwise>
								Non è possibile procedere perché il secondo grado del concorso di progettazione non risulta ancora concluso.
								<br>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${not empty operazioneEseguita}">
						Rimozione dell'anonimato eseguita<c:if test="${operazioneEseguita eq '1'}"> parzialmente</c:if>.
						<br><br>
						Numero operatori elaborati 1° grado:  ${successi1g}.
						<br>
						Numero operatori elaborati con errori 1° grado: ${errori1g}
						<br>
						<c:if test="${param.tipoconc eq '2'}">
							Numero operatori elaborati 2° grado:  ${successi2g}.
							<br>
							Numero operatori elaborati con errori 2° grado: ${errori2g}
							<br>
						</c:if>
						<br>
						<c:if test="${operazioneEseguita eq '2'}">
							È ora possibile procedere all'apertura della documentazione amministrativa e all'aggiudicazione
							<br>
						</c:if>
					</c:when>
					<c:otherwise>
						Mediante questa funzione si procede alla rimozione dell'anonimato delle ditte partecipanti al concorso di progettazione.
						<br><br><b>ATTENZIONE: L'operazione è irreversibile.</b> 
						Una volta tolto l'anonimato non è più possibile modificare la valutazione tecnica e rifare il calcolo graduatoria.
						<br><br>
						Confermi l'operazione ?
						<br><br>
					</c:otherwise>
				</c:choose>
				<br>
				</td>
			</gene:campoScheda >
		

			<c:if test="${not empty controlliSuperati && empty operazioneEseguita}">
				<gene:campoScheda campo="GARA" title="Codice gara" obbligatorio="true" campoFittizio="true" definizione="T21;0"/>
			</c:if>
		
		
			<input type="hidden" name="tipo" value="${tipo}">
			<input type="hidden" name="ngara" value="${ngara}">
			<input type="hidden" name="iterga" value="${iterga}">
			<input type="hidden" name="codgar" value="${codgar}">
			<input type="hidden" name="genere" value="${genere}">
			<input type="hidden" name="tipoconc" value="${param.tipoconc}">
		</gene:formScheda>
			
		<c:if test="${empty controlliSuperati}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
		<c:if test="${not empty operazioneEseguita}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
  </gene:redefineInsert>
  	
  	<gene:javaScript>
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-rimuoviAnonimato.jsp";
		
		function conferma() {
			var gara = getValue("GARA");
			if(gara==null || gara ==""){
				alert("Compilare il campo 'Codice gara' con il codice della gara corrente");
				return;
			}
			var ngara= "${ngara }";
			if (gara != ngara){
				alert("Il codice gara specificato non è quello della gara corrente");
				return;
			}
			
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		<c:if test='${not empty operazioneEseguita}'>
			window.opener.listaVaiAPagina(0);
		</c:if>
	
	
	</gene:javaScript>
</gene:template>

</div>
