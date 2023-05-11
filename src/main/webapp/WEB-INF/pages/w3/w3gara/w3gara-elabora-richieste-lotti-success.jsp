
<%
	/*
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

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<gene:setString name="titoloMaschera" value='Elaborazione di tutte le richieste'  />
	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr>
				<br>
				<c:choose>
					<c:when test="${numeroRichiesteElaborate == 0}">
						<b>Non è stato possibile elaborare alcuna richiesta.</b>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${numeroRichiesteDaElaborare - numeroRichiesteElaborate > 0}">
								<b>Non &egrave; stato possibile elaborare tutte le richieste.</b>
							</c:when>
							<c:otherwise>
								<b>Tutte le richieste sono state elaborate con successo.</b>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				<br>
				<br>
				Richieste di elaborazione: ${numeroRichiesteDaElaborare}
				<br>
				Richieste elaborate: ${numeroRichiesteElaborate}
				<br>
				<br>
			</tr>
			
			<c:if test="${numeroRichiesteDaElaborare - numeroRichiesteElaborate > 0}">
				<tr>
					<td>
						<b>Lista delle richieste non elaborate</b>
						<br>
						<table class="datilista">
							<thead>
								<tr>
									<th>
										N°
									</th>
									<th>
										Oggetto
									</th>				
								</tr>
							</thead>
							<tbody>
											
								<c:forEach items="${listaStatoElaborazioneRichieste}" step="1" var="statoElaborazione" varStatus="status" >
									<c:if test="${status.index%2 == 0}">
										<tr class="even">
									</c:if>
									
									<c:if test="${status.index%2 == 1}">
										<tr class="odd">
									</c:if>
										<td>
											${statoElaborazione[0]}	
										</td>
										<td>
											${statoElaborazione[1]}	
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		window.opener.selezionaPagina(1);
		
		function chiudi(){
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

