<%
/*
 * Created on: 29-mar-2016
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Dettagli motivo esclusione ditte in gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DETMOT-lista" >
	<gene:setString name="titoloMaschera" value="Dettagli motivo esclusione ditte in gara"/>

	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>

	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr><td >
			<gene:formLista entita="TAB1" sortColumn="5;3" pagesize="20" tableclass="datilista"
			gestisciProtezioni="true"  where="TAB1.TAB1COD='A2054' AND (TAB1.TAB1TIP<>98 AND TAB1.TAB1TIP<>99 AND TAB1.TAB1TIP<>100 AND TAB1.TAB1TIP<>101)" 
			pathScheda="gare/detmot/detmot-scheda.jsp" > 
				
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">					
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.DETMOT-scheda")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza"/>
							</c:if>
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.DETMOT-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica"/>
							</c:if>
					</gene:PopUp>
				</gene:campoLista>
				
				<% // Campi veri e propri %>
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.DETMOT-scheda")}'/>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="TAB1COD" visibile="false"/>
				<gene:campoLista campo="TAB1TIP" visibile="false"/>
				<gene:campoLista campo="TAB1DESC" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" title="Motivo esclusione"/>
				<gene:campoLista campo="TAB1NORD" headerClass="sortable" visibile="false"/>
				<gene:campoLista campo="ANNOFF" entita="DETMOT" where="TAB1.TAB1TIP = MOTIES" title="Dettaglio motivo esclusione"/>
			</gene:formLista>
		</td></tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<gene:insert name="pulsanteListaInserisci"/>

				&nbsp;
			</td>
		</tr>
		</table>
  </gene:redefineInsert>
</gene:template>
