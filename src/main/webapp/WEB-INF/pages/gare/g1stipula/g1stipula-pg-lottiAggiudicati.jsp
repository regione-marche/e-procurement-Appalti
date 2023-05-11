<%
/*
 * Created on: 02/12/2009
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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereGAREFunction" />

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="GARE" where='${whereLottiAggiudicati}' tableclass="datilista" sortColumn="1" pagesize="25" >
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
				<gene:campoLista campo="NGARA" title="Codice lotto" width="100"/>
				<gene:campoLista campo="CODIGA" title="Numero lotto"/>
				<gene:campoLista campo="NUMAVCP" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" />
				<gene:campoLista campo="CODCIG" />
				<gene:campoLista campo="IAGGIU" />
				<gene:campoLista campo="IMPRIN" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
				<gene:campoLista campo="IMPALTRO" entita="GARE1" where="GARE1.NGARA=GARE.NGARA" />
			</gene:formLista >
		</td>
	</tr>
</table>
<gene:javaScript>

</gene:javaScript>