<%
/*
 * Created on: 07/03/2012
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
		Finestra per la funzione 'Copia ditta in altri lotti'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty chiave}'>
		<c:set var="chiave" value='${chiave}' />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${param.chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty numeroFaseAttiva}'>
		<c:set var="numeroFaseAttiva" value='${numeroFaseAttiva}' />
	</c:when>
	<c:otherwise>
		<c:set var="numeroFaseAttiva" value="${param.numeroFaseAttiva}" />
	</c:otherwise>
</c:choose>

<c:set var="codiceGara" value='${gene:getValCampo(chiave, "CODGAR5")}'/>
<c:set var="numeroGara" value='${gene:getValCampo(chiave, "NGARA5")}'/>
<c:set var="codiceDitta" value='${gene:getValCampo(chiave, "DITTAO")}'/>

<c:set var="ragioneSociale" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara,codiceGara,codiceDitta)}' />

<div style="width:97%;">

<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp">
		
	<gene:setString name="titoloMaschera" value='Copia ditta negli altri lotti della gara' />
		
	<gene:redefineInsert name="corpo">
				
		<table class="dettaglio-notab">
			<tr>
				<td colspan="2">
					<c:choose>
						<c:when test="${!empty RISULTATO and RISULTATO eq 'COPIAESEGUITA'}">
							<br>
							La ditta &egrave; stata copiata correttamente nei lotti selezionati.
							<br>
				  			<br>
						</c:when>
						<c:otherwise>
							Mediante questa funzione è possibile copiare la ditta "${ragioneSociale }" del lotto <b>${numeroGara}</b>
							negli altri lotti della gara.
							<br><br>
							Scegliere i lotti in cui copiare la ditta.
							<br>
							<b>Attenzione: se la ditta risulta già inserita nei lotti selezionati, verr&agrave; cancellata</b>
							<br>				
							<gene:formLista entita="GARE" sortColumn="3" where="GARE.CODGAR1 = '${codiceGara}' AND (GARE.FASGAR < 7 or GARE.FASGAR IS NULL) AND GARE.NGARA <> '${numeroGara}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCopiaDitta" >
								<gene:set name="titoloMenu">
									<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
								</gene:set>
								<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" >
									<input type="checkbox" name="keys" value="${datiRiga.GARE_NGARA}"  />
								</gene:campoLista>
								<gene:campoLista campo="CODGAR1" visibile="false" edit="true" ordinabile="false"/>
								<gene:campoLista campo="NGARA" title="Codice lotto" ordinabile="false" width="110"/>	
								<gene:campoLista campo="CODIGA" title="Lotto" width="65" ordinabile="false"/>
								<gene:campoLista campo="NOT_GAR" ordinabile="false"/>
								<input type="hidden" name="chiave" id="chiave" value="${chiave }"/>
								<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara }"/>
								<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta }"/>
								<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara }"/>
								<input type="hidden" name="numeroFaseAttiva" id="numeroFaseAttiva" value="${numeroFaseAttiva }" />
							</gene:formLista>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test="${empty RISULTATO}" >
						<INPUT type="button" class="bottone-azione" value="Copia ditta" title="Copia ditta" onclick="javascript:confermaCopiaDitta();">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
				
			</tr>
		</table>

  	</gene:redefineInsert>

	<gene:javaScript>

		document.forms[0].jspPathTo.value="gare/gare/gare-popup-copia-ditta.jsp";
		
		function confermaCopiaDitta(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno un lotto dalla lista");
	      	} else {
	      		listaConferma();
 			}
		}

		
		function annulla(){
			window.close();
		}

	</gene:javaScript>
	
	</gene:template>
</div>
