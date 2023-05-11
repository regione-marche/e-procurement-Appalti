
<%
	/*
	 * Created on 30-10-2018
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereV_GARE_TORNFunction" />

<c:set var="where" value="codgar in (select codgar from garuuid where (tipric='ATCPAT_esito' or tipric='ATCPAT_bando'))" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Selezione gara pubblicata su sito istituzionale ATC" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
			
		<table class="lista">
			<tr>
				<td><gene:formLista entita="V_GARE_TORN" pagesize="25" tableclass="datilista" gestisciProtezioni="false" sortColumn="-2" where="${where}" >
					<gene:campoLista title="Opzioni"	width="50">
						<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
						</gene:PopUp>
					</gene:campoLista>
					<gene:campoLista campo="CODICE"  ordinabile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs})" />
					<gene:campoLista campo="CODGAR"  visibile="false"/>
					<gene:campoLista campo="OGGETTO"  ordinabile="true"/>
					<gene:campoLista campo="GENERE"  visibile="false"/>
					<gene:campoLista campo="DATPUB"  entita="PUBBLI" where ="V_GARE_TORN.CODGAR=PUBBLI.CODGAR9 and PUBBLI.TIPPUB=11" title="Data pubbl. bando"/>
					<gene:campoLista campo="DINPUBG"  entita="PUBG" from="GARE" where ="V_GARE_TORN.CODGAR=GARE.CODGAR1 and GARE.NGARA=PUBG.NGARA and PUBG.TIPPUBG=12" title="Data pubbl. esito"/>
				</gene:formLista></td>
			</tr>
				
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
		
	</gene:javaScript>
</gene:template>

