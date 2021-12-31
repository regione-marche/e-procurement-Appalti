
<%
	/*
	 * Created on 01-set-2008
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

	<gene:formScheda entita="GARE" where="GARE.NGARA = '${ngara}'">
		<gene:campoScheda campo="MODLICG" visibile="false"/>
		<gene:campoScheda>
				<td colspan="2"><b><br>&nbsp;<br>Prima classificata</b></td>
		</gene:campoScheda>	
		<gene:campoScheda entita="DITG" campo="DITTAO" where="DITG.NGARA5 = GARE.NGARA AND DITG.STAGGI = 4" />
		<gene:campoScheda entita="DITG" campo="NOMIMO" />
	
		<c:choose>
			<c:when test='${datiRiga.GARE_MODLICG eq 6}' >
				<gene:campoScheda title="Punteggio di aggiudicazione" campo="RIBPRO" modificabile="false" definizione="F13.9;0;;;G1RIBPRO" />
			</c:when>
			<c:when test='${datiRiga.GARE_MODLICG eq 17}' >
				<gene:campoScheda title="Rialzo di aggiudicazione" campo="RIBPRO" modificabile="false" definizione="F13.9;0;;PRC;G1RIBPRO" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda title="Ribasso di aggiudicazione" campo="RIBPRO" modificabile="false" definizione="F13.9;0;;PRC;G1RIBPRO" />
			</c:otherwise>
		</c:choose>
	
		<gene:campoScheda campo="IAGPRO" />
		<gene:campoScheda>
			<td class="comandi-dettaglio" colSpan="2">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
			</td>
		</gene:campoScheda>
	</gene:formScheda>

	




