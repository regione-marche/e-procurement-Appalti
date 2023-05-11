<%
/*
 * Created on: 23/03/2017
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda addTr="false">
			<tr id="rowTABPUB_CODTAB_${param.contatore}" >
		</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="CODTAB_${param.contatore}" entita="TABPUB" campoFittizio="true" visibile="false" definizione="N7;1" value="${item[0]}" />
		<gene:campoScheda addTr="false" campo="CODPUB_${param.contatore}" entita="TABPUB" campoFittizio="true" visibile="false" definizione="N7;1" value="${item[1]}" />
		<gene:campoScheda addTr="false" campo="TIPPUB_${param.contatore}" entita="TABPUB" campoFittizio="true" title="" definizione="N7;0;A1008;;TIPPUB_P" value="${item[2]}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPPUB" />
		<gene:campoScheda addTr="false">
			<c:if test="${modo ne 'VISUALIZZA'}">
					<td id="elimina_${param.contatore}"></td>
			</c:if>	
			</tr>
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda addTr="false">
			<tr id="rowTABPUB_CODTAB_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="CODTAB_${param.contatore}" entita="TABPUB" campoFittizio="true" visibile="false" definizione="N7;1" />
		<gene:campoScheda addTr="false" campo="CODPUB_${param.contatore}" entita="TABPUB" campoFittizio="true" visibile="false" definizione="N7;1" />
		<gene:campoScheda addTr="false" campo="TIPPUB_${param.contatore}" entita="TABPUB" campoFittizio="true" title="" definizione="N7;0;A1008;;TIPPUB_P" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPPUB" />
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:otherwise>
</c:choose>

<gene:javaScript>

	
	<c:if test="${modo ne 'VISUALIZZA'}">
		$('#elimina_${param.contatore}').append($('#rowtitoloTABPUB_${param.contatore}'));
		$('#elimina_${param.contatore}').css('paddingLeft','32%');
	</c:if>		

	
</gene:javaScript>

