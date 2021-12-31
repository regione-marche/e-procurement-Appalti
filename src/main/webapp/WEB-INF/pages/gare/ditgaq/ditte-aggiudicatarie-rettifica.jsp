<%
/*
 * Created on: 19/12/2013
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


<c:set var="ngara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="tmp" value='${fn:substringAfter(param.chiave, ";")}' />
<c:set var="modlicg" value='${fn:substringBefore(tmp, ":")}' />
<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">		
		<gene:campoScheda campo="ID_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDQ" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="T20;0;;;G1NGARADQ" value="${item[1]}" />
		<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="T10;0;;;G1DITTAODQ" value="${item[2]}" />
		<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="IMPR" campoFittizio="true"  modificabile="false" visibile="true" definizione="T60;0;;;G_NOMIMP" value="${item[3]}" />
		<gene:campoScheda campo="RIBAGGINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F13.9;0;;PRC;G1RIBINIDQ" value="${gene:if(empty item[16] or item[16] eq '', item[4], item[16])}" title="Ribasso di aggiudicazione iniziale"/>
		<gene:campoScheda campo="IAGGIUINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGINIDQ" value="${gene:if(empty item[17] or item[17] eq '', item[6], item[17])}" title="Importo di aggiudicazione iniziale"/>
		<gene:campoScheda campo="RIBAGG_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="F13.9;0;;PRC;G1RIBAGGDQ" value="${item[4]}" title="Nuovo ribasso di aggiudicazione (*)" />
		<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="F15;0;;MONEY;G1IAGGIUDQ" value="${item[6]}" title='Nuovo importo di aggiudicazione (*)'/>
		<gene:campoScheda campo="RIDISO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T2;0;;SN;G1RIDISODQ" value="${item[11]}" modificabile="flase" visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.DITGAQ.RIDISO')}"/>
		<gene:campoScheda campo="IMPGAR_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPGARDQ" value="${item[12]}" visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.DITGAQ.IMPGAR')}"/>
		<gene:campoScheda campo="INIZ_RIB_${param.contatore}" campoFittizio="true" definizione="T1" value="${gene:if(empty item[16] or item[16] eq '', '1', '2')}" visibile="false"/>		
		<gene:campoScheda campo="INIZ_IMP_${param.contatore}" campoFittizio="true" definizione="T1" value="${gene:if(empty item[17] or item[17] eq '', '1', '2')}" visibile="false"/>	
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDQ"  />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="T20;0;;;G1NGARADQ"  />
		<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="T10;0;;;G1DITTAODQ"  />
		<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="IMPR" campoFittizio="true"  modificabile="false" visibile="true" definizione="T60;0;;;G_NOMIMP" />
		<gene:campoScheda campo="RIBAGGINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F13.9;0;;PRC;G1RIBINIDQ"  title="Ribasso di aggiudicazione iniziale"/>
		<gene:campoScheda campo="IAGGIUINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGINIDQ"  title="Importo di aggiudicazione iniziale"/>
		<gene:campoScheda campo="RIBAGG_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="F13.9;0;;PRC;G1RIBAGGDQ"  title="Nuovo ribasso di aggiudicazione" />
		<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="F15;0;;MONEY;G1IAGGIUDQ"  title='Nuovo importo di aggiudicazione'/>
		<gene:campoScheda campo="RIDISO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T2;0;;SN;G1RIDISODQ"  modificabile="flase" visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.DITGAQ.RIDISO')}"/>
		<gene:campoScheda campo="IMPGAR_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPGARDQ"  visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.DITGAQ.IMPGAR')}"/>
		<gene:campoScheda campo="INIZ_RIB_${param.contatore}" campoFittizio="true" definizione="T1" visibile="false"/>		
		<gene:campoScheda campo="INIZ_IMP_${param.contatore}" campoFittizio="true" definizione="T1"  visibile="false"/>	
	</c:otherwise>
</c:choose>



