
<%
	/*
	 * Created on 6-ago-2008
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


	<c:set var="ngara" value='${NGARA}' />
	<c:set var="numeroPrimeParimerito" value='${NUMEROPRIMEPARIMERITO}' />
	<c:set var="listaPrimeParimerito" value='${LISTAPRIMEPARIMERITO}' />
	
	
	<c:if test='${numeroPrimeParimerito > 1}'>
			<tr>
				<td colSpan="2">
					<br>&nbsp;<br>
					<b>Prima classificata: lista delle ditte pari merito</b>
					<br>
					<c:choose>
					<c:when test="${selpar ne 1 }">
						Il calcolo dell'aggiudicazione ha identificato come prima classificata più di una
						ditta. E' necessario sceglierne una sola dalla lista
						sottostante. 
					</c:when>
					<c:otherwise>
						Il calcolo dell'aggiudicazione ha identificato come prima classificata più di una
						ditta. Ne verrà selezionata una dalla lista sottostante mediante sorteggio.
					</c:otherwise>
					</c:choose>
					
				</td>
			</tr>

			<tr>
				<td colSpan="2">
					<gene:formLista entita="DITG"
						where="DITG.NGARA5 = '${ngara}' AND DITG.DITTAO IN (${listaPrimeParimerito})"
						tableclass="datilista" sortColumn="5;6" pagesize="0">
	
						<gene:campoLista title="Scegli" width="40" visibile="${selpar ne 1 }">
							<c:if test="${selpar ne 1  }"><center><input type="radio" name="prima" value="${datiRiga.DITG_DITTAO}" /></center></c:if>
						</gene:campoLista>
						
						<gene:campoLista campo="CODGAR5" visibile="false" />
						<gene:campoLista campo="DITTAO" visibile="false" ordinabile="false" />
						<gene:campoLista campo="NGARA5" visibile="false" />
						<gene:campoLista title="N." campo="NPROGG" visibile="true" />
						<gene:campoLista campo="NOMIMO" />
					</gene:formLista>
				</td>
			</tr>
			
		
		
	</c:if>
	
	
	
	