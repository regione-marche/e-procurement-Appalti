
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:template file="popup-template.jsp" gestisciProtezioni="false">
	<gene:setString name="titoloMaschera" value="Selezione criterio di rotazione"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="ALGORITMI" sortColumn="6;4" gestisciProtezioni="false" inserisciDaArchivio='false' where="ALGORITMI.CODAPP='PG'">
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>
			
			<c:set var="hrefDettaglio" value=""/>
			<c:if test='${!gene:checkProt(pageContext, "COLS.VIS.GARE.ALGORITMI.CODTEC")}'>
				<c:set var="hrefDettaglio" value="javascript:archivioSeleziona(${datiArchivioArrayJs});"/> 
			</c:if>
			<gene:campoLista campo="TAB1TIP" entita="TAB1" where="ALGORITMI.CODALGO=TAB1.TAB1COD and  ALGORITMI.TIPOALGO=TAB1.TAB1TIP" visibile="false"/>
			<gene:campoLista campo="TAB1DESC" entita="TAB1" where="ALGORITMI.CODALGO=TAB1.TAB1COD and  ALGORITMI.TIPOALGO=TAB1.TAB1TIP" visibile="false"/>
			<gene:campoLista campo="TIPOALGO"  href="javascript:archivioSeleziona(${datiArchivioArrayJs});" ordinabile="false"/>
			<gene:campoLista campo="DESCALGO" ordinabile="false"/>
			<gene:campoLista campo="TAB1NORD" entita="TAB1" where="ALGORITMI.CODALGO=TAB1.TAB1COD and  ALGORITMI.TIPOALGO=TAB1.TAB1TIP" visibile="false"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
