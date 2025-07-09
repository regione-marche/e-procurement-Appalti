<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereGAREAVVISIFunction" />

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione dell'avviso di preinformazione"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="V_GARE_AVVISI" sortColumn="-2" inserisciDaArchivio="true" gestisciProtezioni="true" >
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="archivioSeleziona(${datiArchivioArrayJs},${currentRow});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="NGARA" title="Codice" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs},${currentRow});" />
			<gene:campoLista campo="TIPOAPP" entita="GAREAVVISI" where="GAREAVVISI.CODGAR = V_GARE_AVVISI.CODGAR" headerClass="sortable"/>
			<gene:campoLista campo="TIPOAVV" entita="GAREAVVISI" where="GAREAVVISI.CODGAR = V_GARE_AVVISI.CODGAR" headerClass="sortable"/>
			<gene:campoLista campo="OGGETTO" entita="GAREAVVISI" headerClass="sortable"/>
			<gene:campoLista campo="IDPIANINPA" headerClass="sortable"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
