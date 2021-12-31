<%
/*
 * Created on: 13/01/2015
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
			<tr id="rowGARCOMPREQ_ID_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="ID_${param.contatore}" entita="GARCOMPREQ" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="GARCOMPREQ" campoFittizio="true" visibile="false" definizione="T20;0" value="${item[1]}" />
		<gene:campoScheda addTr="false" campo="DRICREQ_${param.contatore}" entita="GARCOMPREQ"  campoFittizio="true"  title="Data ricezione" definizione="D;0" value="${item[2]}" />
		<gene:campoScheda addTr="false" campo="NRICREQ_${param.contatore}" entita="GARCOMPREQ"  campoFittizio="true"  title="Numero protocollo" definizione="T20;0"  value="${item[3]}" />
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda addTr="false">
			<tr id="rowGARCOMPREQ_ID_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="ID_${param.contatore}" entita="GARCOMPREQ" campoFittizio="true" visibile="false" definizione="N12;1"  />
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="GARCOMPREQ" campoFittizio="true" visibile="false" definizione="T20;0"  value="${param.chiave }"/>
		<gene:campoScheda addTr="false" campo="DRICREQ_${param.contatore}" entita="GARCOMPREQ"  campoFittizio="true"  title="Data ricezione" definizione="D;0"  />
		<gene:campoScheda addTr="false" campo="NRICREQ_${param.contatore}" entita="GARCOMPREQ"  campoFittizio="true"  title="Numero protocollo" definizione="T20;0"   />
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:otherwise>
</c:choose>

<gene:javaScript>

	
<c:if test="${modo ne 'VISUALIZZA'}">
		$('#elimina_${param.contatore}').append($('#rowtitoloGARCOMPREQ_${param.contatore}'));
	</c:if>		

	
</gene:javaScript>



