<%
/*
 * Created on: 28/10/2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDOCSOC" value="${item[0]}" />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="T2;1;;;G1IDPRGDS" value="${item[1]}" />
		<gene:campoScheda campo="IDCOM_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDCOMDS" value="${item[2]}" />
		<gene:campoScheda campo="NUMORD_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="N3;0;;;G1NUMORDDS" value="${item[3]}" />
		<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" definizione="T2000;0;;;G1DESCRIDS" value="${item[4]}" />
		<gene:campoScheda campo="OBBLIGATORIO_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" definizione="T2;0;;SN;G1OBBLIDS" value="${item[5]}" />
		<gene:campoScheda campo="FORMATO_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" definizione="N7;0;A1105;;G1FORMATODS" value="${item[6]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDOCSOC"  />
		<gene:campoScheda campo="IDPRG_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="T2;1;;;G1IDPRGDS" value="${param.chiave1}"/>
		<gene:campoScheda campo="IDCOM_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDCOMDS" value="${param.chiave2}"/>
		<gene:campoScheda campo="NUMORD_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" visibile="false" definizione="N3;0;;;G1NUMORDDS" /> 
		<gene:campoScheda campo="DESCRIZIONE_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" definizione="T2000;0;;;G1DESCRIDS" />
		<gene:campoScheda campo="OBBLIGATORIO_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" definizione="T2;0;;SN;G1OBBLIDS" />
		<gene:campoScheda campo="FORMATO_${param.contatore}" entita="G1DOCSOC" campoFittizio="true" definizione="N7;0;A1105;;G1FORMATODS" />
	</c:otherwise>
</c:choose>