
<%
	/*
	 * Created on 18-feb-2009
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

			
<tr>
	<td>
		<b>${titoloGenerico}</b>
	</td>
</tr>
<c:if test="${numeroErrori > 0 || numeroWarning > 0}">
	<tr>
		<td>		
			Durante il controllo dei dati sono stati rilevati alcuni errori/avvisi:
			<br>
			<br>
			&nbsp;&nbsp;
			<img width="20" height="20" title="Controllo bloccante: impedisce la validazione dei dati" alt="Controllo bloccante" src="${pageContext.request.contextPath}/img/controllo_e.gif"/>
			<i>&nbsp;numero totale degli errori bloccanti: ${numeroErrori}</i>
			<br>
			&nbsp;&nbsp;
			<img width="20" height="20" title="Avviso non bloccante: è una segnalazione che l'utente può anche ignorare" alt="Controllo non bloccante" src="${pageContext.request.contextPath}/img/controllo_w.gif"/>
			<i>&nbsp;numero totale degli avvisi non bloccanti: ${numeroWarning}</i>
			<br>&nbsp;
		</td>
	</tr>
</c:if>

<tr>
	<td>
		<table class="datilista">
			<thead>
				<tr>
					<th width="22px">
						&nbsp;
					</th>
					<th>
						Sezione
					</th>
					<th>
						Descrizione del campo e messaggio di controllo
					</th>				
				</tr>
			</thead>
			<tbody>
							
				<c:forEach items="${listaGenericaControlli}" step="1" var="controllo" varStatus="status" >
					<c:if test="${status.index%2 == 0}">
						<tr class="even">
					</c:if>
					<c:if test="${status.index%2 == 1}">
						<tr class="odd">
					</c:if>
						
						<c:choose>
							<c:when test="${controllo[0] == 'T'}">
								<td colspan="3">
									<i>${controllo[1]}</i>
								</td>
							</c:when>
							<c:otherwise>
								<td width="22px">
									<c:choose>
										<c:when test="${controllo[0] == 'E'}">
											<img width="20" height="20" title="Controllo bloccante, impedisce la validazione dei dati" alt="Controllo bloccante" src="${pageContext.request.contextPath}/img/controllo_e.gif"/>	
										</c:when>
										<c:when test="${controllo[0] == 'W'}">
											<img width="20" height="20" title="Controllo non bloccante, è una segnalazione che l'utente può anche ignorare" alt="Controllo non bloccante" src="${pageContext.request.contextPath}/img/controllo_w.gif"/>	
										</c:when>
										<c:when test="${controllo[0] == 'I'}">
											<img width="20" height="20" title="Informazione" alt="Informazione" src="${pageContext.request.contextPath}/img/controllo_i.gif"/>	
										</c:when>
									</c:choose>
									
								</td>
								
								<c:choose>
									<c:when test="${controllo_1_precedente ne controllo[1]}">
										<td>
											${controllo[1]}										
										</td>
									</c:when>
									<c:otherwise>
										<td>
											&nbsp;
										</td>
									</c:otherwise>
								</c:choose>
								
								<c:set var="controllo_1_precedente" value="${controllo[1]}" />
								
								<td>
									<b>${controllo[2]}</b>
									<br>${controllo[3]}
								</td>	
																	
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</td>
</tr>

			



