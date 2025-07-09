	<%/*
       * Created on 22-mag-2024
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

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="GARE" where="GARE.CODGAR1 = #TORN.CODGAR# AND GARE.NGARA <>#TORN.CODGAR#" sortColumn="2" tableclass="datilista" pagesize="25" gestisciProtezioni="true" >
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';aperturaLottoP31();" />
				<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza importo lotto" href="${link}"/>
						</gene:PopUp>
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" href="${link}"/>
				<gene:campoLista campo="NOT_GAR" title="Oggetto" headerClass="sortable"/>

				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
			</gene:formLista>
		<td>
	<tr>
</table>

	<gene:javaScript>
	function aperturaLottoP31(){
		visualizzaLottoP31.key.value=chiaveRiga;
		visualizzaLottoP31.submit();
	}
</gene:javaScript>

<form name="visualizzaLottoP31" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="/gare/g1schep31/g1schep31-importoLotti-scheda.jsp" /> 
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="autorizzatoModifiche" value="${param.autorizzatoModifiche}"/>
</form>
				