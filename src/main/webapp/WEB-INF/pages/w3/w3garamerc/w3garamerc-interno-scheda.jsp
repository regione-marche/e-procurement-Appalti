<%
	/*
	 * Created on 07-Ott-2010
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<c:set var="numgara" value="${gene:getValCampo(keyParent, 'NUMGARA')}" />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda title="Numero gara" entita="W3GARAMERC" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAMERC_NUMGARA" value="${item[0]}"/>
		<gene:campoScheda title="Numero categoria" entita="W3GARAMERC" campo="NUMMERC_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3GARAMERC_NUMMERC" value="${item[1]}"/>
		<gene:campoScheda title="Categoria merceologica" entita="W3GARAMERC" campo="CATEGORIA_${param.contatore}" campoFittizio="true" visibile="true" definizione="N7;0;W3031;;W3GARAMERC_CATEGORIA" value="${item[2]}" gestore="it.eldasoft.sil.w3.tags.gestori.decoratori.GestoreCategorieMerceologiche"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda title="Numero gara" entita="W3GARAMERC" campo="NUMGARA_${param.contatore}" campoFittizio="true" visibile="false" definizione="N10;1;;;W3GARAMERC_NUMGARA" value="${numgara}"/>
		<gene:campoScheda title="Numero categoria" entita="W3GARAMERC" campo="NUMMERC_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;W3GARAMERC_NUMMERC" />
		<gene:campoScheda title="Categoria merceologica" entita="W3GARAMERC" campo="CATEGORIA_${param.contatore}" campoFittizio="true" visibile="true" definizione="N7;0;W3031;;W3GARAMERC_CATEGORIA" gestore="it.eldasoft.sil.w3.tags.gestori.decoratori.GestoreCategorieMerceologiche"/>
	</c:otherwise>
</c:choose>

<gene:javaScript>
	modifySelectOption($("#W3GARAMERC_CATEGORIA_${param.contatore}"));
</gene:javaScript>


