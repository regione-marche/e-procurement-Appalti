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
 /* Lista Modelli di Comunicazioni */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1CRIMOD-lista" >
	<gene:setString name="titoloMaschera" value="Modelli di criteri di valutazione per OEPV"/>

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr><td >
			<gene:formLista entita="G1CRIMOD" sortColumn="3" pagesize="20" tableclass="datilista"
			gestisciProtezioni="true" > 
				
				<!-- Se il nome del campo � vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">					
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.G1CRIMOD-scheda")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza"/>
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica"/>
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.elimina" />
							</c:if>							
					</gene:PopUp>
								
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
				<% // Campi veri e propri %>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="ID" visibile="false"/>
				<gene:campoLista campo="TITOLO" headerClass="sortable"  href="${link}"/>
				<gene:campoLista campo="DESCRI" />
			</gene:formLista>
		</td></tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<gene:insert name="pulsanteListaInserisci">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovo()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteListaEliminaSelezione">
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
					</c:if>
				</gene:insert>
			
				&nbsp;
			</td>
		</tr>
		</table>
  </gene:redefineInsert>
</gene:template>
