<%
/*
 * Created on: 11-07-2018
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
		Finestra che visualizza la conferma per la copia degli attributi aggiuntivi nei lotti
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<div style="width:97%;">

<gene:template file="popup-template.jsp">
	
	<c:choose>
		<c:when test='${!empty RISULTATO}'>
			<c:set var="lottoSorgente" value='${lottoSorgente}' />
			<c:set var="codiceGara" value='${codiceGara}' />
		</c:when>
		<c:otherwise>
			<c:set var="lottoSorgente" value="${param.lottoSorgente}" />
			<c:set var="codiceGara" value='${param.codiceGara}' />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.bloccoModifica}'>
			<c:set var="bloccoModifica" value="${param.bloccoModifica}" />
		</c:when>
		<c:otherwise>
			<c:set var="bloccoModifica" value="${bloccoModifica}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="esistonoAttributi" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ConteggioAttributiAggiuntiviFunction",pageContext,lottoSorgente,"XDPRE")}' />
	
	<gene:setString name="titoloMaschera" value='Copia attributi aggiuntivi negli altri lotti della gara' />
		
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<c:set var="modo" value="MODIFICA" scope="request" />
	
		<table class="dettaglio-notab">
			
			<c:choose>
				<c:when test="${esistonoAttributi eq 'FALSE'}">
					<tr>
						<td colspan="2">
							<br>
							La lista degli attributi aggiuntivi delle lavorazioni e forniture del lotto <b>${lottoSorgente}</b> è vuota.
							<br>
							Non è possibile procedere con la copia.
							<br>
							<br>
						</td>
					</tr>
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</tr>
				</c:when>
			
				<c:otherwise>
					<tr>
						<td colspan="2">
							<c:choose>
								<c:when test="${!empty RISULTATO and RISULTATO eq 'COPIAESEGUITA'}">
									<br>
									Gli attributi aggiuntivi delle lavorazioni e forniture del lotto sono stati copiati correttamente.
									<br>
						  			<br>
								</c:when>
								<c:otherwise>
									Mediante questa funzione è possibile copiare gli attributi aggiuntivi delle lavorazioni e forniture del lotto <b>${lottoSorgente}</b>
									negli altri lotti della gara.
									<br><br>
									Scegliere i lotti in cui copiare gli attributi aggiuntivi.
									<br>
									<b>Attenzione: la lista originale degli attributi aggiuntivi dei lotti selezionati verr&agrave; cancellata</b>
									<br>
									
									<c:set var="where" value="GARE.CODGAR1 = '${codiceGara}' AND (GARE.FASGAR IS NULL or GARE.FASGAR <=1) AND (GARE.MODLICG=5 or GARE.MODLICG=14 or (GARE.MODLICG=6 and GARE.NGARA IN (select G1CRIDEF.NGARA from G1CRIDEF where G1CRIDEF.NGARA=GARE.NGARA and G1CRIDEF.FORMATO=52)))  AND GARE.NGARA NOT IN ('${lottoSorgente}') AND GARE.CODGAR1 <> GARE.NGARA"/>
													
									<gene:formLista entita="GARE" sortColumn="3" where="${where }" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCopiaAttributiAggiuntiviXDPRE" >
										<c:if test="${bloccoModifica ne 'true' }">
										<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
											<c:if test="${currentRow >= 0 }">
												<input type="checkbox" name="keys" value="${chiaveRiga}" />
											</c:if>
										</gene:campoLista>
										</c:if>
										<gene:campoLista campo="CODGAR1" visibile="false" ordinabile="false"/>
										<gene:campoLista campo="NGARA" title="Codice lotto" ordinabile="false" width="110"/>	
										<gene:campoLista campo="CODIGA" title="Lotto" width="65" ordinabile="false"/>
										<gene:campoLista campo="NOT_GAR" ordinabile="false"/>
										
										<input type="hidden" name="lottoSorgente" id="lottoSorgente" value="${lottoSorgente}" />
										<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}" />
										<input type="hidden" name="bloccoModifica" id="bloccoModifica" value="${bloccoModifica}" />
									</gene:formLista>
									
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<c:if test="${empty RISULTATO and bloccoModifica ne true}" >
								<INPUT type="button" class="bottone-azione" value="Copia attributi aggiuntivi" title="Copia attributi aggiuntivi" onclick="javascript:confermaCopiaAttributi();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
						
					</tr>
				</c:otherwise>			
			</c:choose>
			
		</table>

  	</gene:redefineInsert>

	<gene:javaScript>

		document.forms[0].jspPathTo.value="gare/gare/gare-popup-copia-attributiAggiuntiviXDPRE.jsp";
		
		function confermaCopiaAttributi(){
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
