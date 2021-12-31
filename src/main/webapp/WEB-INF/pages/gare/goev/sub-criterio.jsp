<%
/*
 * Created on: 26/05/2009
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

<c:set var="numeroGara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="numeroCriterio" value='${fn:substringAfter(param.chiave, ";")}' />

<c:choose>
<c:when test="${param.tipoDettaglio eq 1}">			
			<gene:campoScheda campo="NGARA_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="T20;1;;;G1_NGARAE" value="${item[0]}" />
			<gene:campoScheda campo="NECVAN_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N4;1;;;G1_NECVAE" value="${item[1]}" />
			<gene:campoScheda campo="NORPAR1_${param.contatore}" entita="GOEV" campoFittizio="true" definizione="F8.3;0;;;G1_NORPAE1" value="${item[2]}" />
			<gene:campoScheda campo="DESPAR_${param.contatore}" entita="GOEV" campoFittizio="true" definizione="T2000;0;;NOTE;G1_DESPAE" value="${item[3]}" obbligatorio="true"/> 
			<gene:campoScheda campo="MAXPUN_${param.contatore}" entita="GOEV" campoFittizio="true" definizione="F8.3;0;;;G1_MAXPUE" value="${item[4]}" obbligatorio="true"/>
			<gene:campoScheda campo="NECVAN1_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N7;0;;;G1_NECVAN1" value="${item[5]}" />
			<gene:campoScheda campo="LIVPAR_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N7;0;;;G1_LIVPAE" value="2" />
			<gene:campoScheda campo="NORPAR_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="F8.3;0;;;G1_NORPAE"  />
			<gene:campoScheda campo="TIPPAR_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N2" value="${param.tipoCriterioPadre}"  /> 
</c:when>
<c:otherwise>
			<gene:campoScheda campo="NGARA_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="T20;1;;;G1_NGARAE" value="${numeroGara}" />
			<gene:campoScheda campo="NECVAN_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N4;1;;;G1_NECVAE" />
			<gene:campoScheda campo="NORPAR1_${param.contatore}" entita="GOEV" campoFittizio="true" definizione="F8.3;0;;;G1_NORPAE1" />
			<gene:campoScheda campo="DESPAR_${param.contatore}" entita="GOEV" campoFittizio="true" definizione="T2000;0;;NOTE;G1_DESPAE" obbligatorio="true"/> 
			<gene:campoScheda campo="MAXPUN_${param.contatore}" entita="GOEV" campoFittizio="true" definizione="F8.3;0;;;G1_MAXPUE" obbligatorio="true"/>
			<gene:campoScheda campo="NECVAN1_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N7;0;;;G1_NECVAN1" />
			<gene:campoScheda campo="LIVPAR_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N7;0;;;G1_LIVPAE" />
			<gene:campoScheda campo="NORPAR_${param.contatore}" entita="GOEV" campoFittizio="true"   visibile="false" definizione="F8.3;0;;;G1_NORPAE"/>
			<gene:campoScheda campo="TIPPAR_${param.contatore}" entita="GOEV" campoFittizio="true" visibile="false" definizione="N2" value="${param.tipoCriterioPadre}"  />  
</c:otherwise>
</c:choose>

<gene:fnJavaScriptScheda funzione='aggiornaNorparSubcriteri("#GOEV_NORPAR#")' elencocampi='GOEV_NORPAR' esegui="false" />
<gene:fnJavaScriptScheda funzione='aggiornaMaxpunPadre()' elencocampi='GOEV_MAXPUN_${param.contatore}' esegui="false" />
