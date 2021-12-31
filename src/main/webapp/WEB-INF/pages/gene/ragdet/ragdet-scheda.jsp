<%
/*
 * Created on: 26/03/2015
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
				<tr id="rowRAGDET_NUMDIC_${param.contatore}">
			</gene:campoScheda>
		
		<gene:campoScheda addTr="false" campo="CODIMP_${param.contatore}" entita="RAGDET" campoFittizio="true" visibile="false" definizione="T10;1" value="${item[0]}" />
		<gene:campoScheda addTr="false" campo="CODDIC_${param.contatore}" entita="RAGDET" campoFittizio="true" hideTitle="true" definizione="T10;1" value="${item[1]}" href='javascript:archivioImpresa("${item[1]}");'/>
		<gene:campoScheda addTr="false" campo="NUMDIC_${param.contatore}" entita="RAGDET" campoFittizio="true"  visibile="false" definizione="N3;1" value="${item[2]}" />
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="RAGDET"  campoFittizio="true" visibile="false" definizione="T20" value="${item[3]}" />
		<gene:campoScheda addTr="false" campo="NOMEST_${param.contatore}" entita="IMPR" hideTitle="true" campoFittizio="true"   definizione="T2000;0;;NOTE;" value="${item[4]}" href='javascript:archivioImpresa("${item[1]}");'/>
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda addTr="false">
				<tr id="rowRAGDET_NUMDIC_${param.contatore}">
			</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="CODIMP_${param.contatore}" entita="RAGDET" campoFittizio="true" visibile="false" definizione="T10;1"  />
		<gene:campoScheda addTr="false" campo="CODDIC_${param.contatore}" entita="RAGDET" campoFittizio="true" hideTitle="true" definizione="T10;1"  />
		<gene:campoScheda addTr="false" campo="NUMDIC_${param.contatore}" entita="RAGDET" campoFittizio="true"  visibile="false" definizione="N3;1" />
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="RAGDET"  campoFittizio="true"  visibile="false" definizione="T20"  />
		<gene:campoScheda addTr="false" campo="NOMEST_${param.contatore}" entita="IMPR" hideTitle="true" campoFittizio="true"   definizione="T2000;0;;NOTE;"  />
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:otherwise>
</c:choose>

<gene:javaScript>
	<c:if test="${modo ne 'VISUALIZZA'}">
		//Si nasconde l'icona per l'eliminazione della riga
		$('#rowtitoloRAGDET_${param.contatore}').hide();
	</c:if>
</gene:javaScript>





