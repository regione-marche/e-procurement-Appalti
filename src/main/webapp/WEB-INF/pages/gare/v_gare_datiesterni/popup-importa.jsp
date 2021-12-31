
<%
  /*
			 * Created on 18-12-2014
			 *
			 * Copyright (c) EldaSoft S.p.A.
			 * Tutti i diritti sono riservati.
			 *
			 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
			 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
			 * aver prima formalizzato un accordo specifico con EldaSoft.
			 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Importa affidamenti da dati esterni' />
	
	<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupImportaAffidamentiEsterni">
			<table class="lista">
				<c:choose>
					<c:when test='${empty requestScope.esito or requestScope.esito eq ""}'>
						<tr>
							<td class="valore-dato" colspan="2">
								<br>
								Confermi l'avvio dell'importazione di affidamenti da dati esterni?<br>
								<br>
								<br>
							</td>
						</tr>
					</c:when>
					<c:when test='${requestScope.esito eq "ERRORE"}'>
						<tr>
							<td class="valore-dato" colspan="2">
								<br>
								Operazione di importazione interrotta<br>
								<br>
								<br>
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td class="valore-dato" colspan="2">
								<br>
								Operazione di importazione terminata
								<br>
							</td>
						</tr>
						<tr>
							<td class="valore-dato" colspan="2">
								<br>
								Numero CIG inseriti: ${requestScope.numeroCigImportati }
								<br>
							</td>
						</tr>
						<tr>
							<td class="valore-dato" colspan="2">
								Numero CIG aggiornati: ${requestScope.numeroCigAggiornati }
								<br>
							</td>
						</tr>
						<tr>
							<td class="valore-dato" colspan="2">
								Numero CIG non importati in seguito a errori: ${requestScope.numeroCigConErrori }
								<br><br>
							</td>
						</tr>
						<c:if test='${not empty requestScope.messaggiErrore && requestScope.messaggiErrore ne ""}'>
							<tr>
								<td colSpan="2">
									<textarea cols="77" rows="14" readonly="readonly">${requestScope.messaggiErrore}</textarea>
								</td>
							</tr>
						</c:if>
					</c:otherwise>
				</c:choose>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<c:if test='${requestScope.esito ne "ERRORE" and requestScope.esito ne "OK"}' >
							<INPUT type="button" class="bottone-azione" value="Importa" title="Importa"	onclick="javascript:importa();">
						</c:if>
						<c:choose>
							<c:when test='${requestScope.esito eq "OK"}'>
								<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
							</c:when>
							<c:otherwise>
								<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</gene:formScheda>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		
		function importa() {
			document.forms[0].jspPathTo.value="gare/v_gare_datiesterni/popup-importa.jsp";
			schedaConferma();
			
		}
		
	</gene:javaScript>
</gene:template></div>

