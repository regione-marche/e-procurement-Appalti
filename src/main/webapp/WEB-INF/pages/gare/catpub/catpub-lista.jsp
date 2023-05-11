<%
/*
 * Created on: 23-mar-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Pubblicazioni bando ed esito di gara */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATPUB-scheda")}'/>
<c:set var="tipoSelezionato" value="${gene:if(empty param.TIPO,1,param.TIPO)}"/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="CATPUB-lista" >
	
	<gene:setString name="titoloMaschera"  value="Pubblicazioni bando ed esito di gara"/>
	
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr>
			<td>
				<c:forEach var="i" begin="1" end="2">
					<input type="radio" name="tipologia" value="${i}" onclick="cambiaTipoPubblicazione('${i}');" <c:if test="${tipoSelezionato == i}">checked="checked"</c:if>>
					<c:choose>
					    <c:when test="${i == 1}">
					       <c:out value="Pubblicazioni del bando"></c:out>
					       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    </c:when>
					    <c:otherwise>
					        <c:out value="Pubblicazioni dell'esito"></c:out>
					    </c:otherwise>
					</c:choose>
					
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td>
			<gene:formLista entita="CATPUB" sortColumn="2" pagesize="20" tableclass="datilista"
			gestisciProtezioni="true" where="CATPUB.TIPCLA=${tipoSelezionato}"
			gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCATPUB"> 
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">					
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATPUB-scheda")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza"/>
							</c:if>
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATPUB-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
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
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATPUB-scheda")}'/>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="CODTAB" visibile="false"/>
				<gene:campoLista campo="TIPLAV" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" />
				<gene:campoLista campo="TIPGAR" headerClass="sortable" />
				<gene:campoLista campo="LIMINF" headerClass="sortable" title='Da importo'/>
				<gene:campoLista campo="LIMSUP" headerClass="sortable" title='A importo'/>
				<input type="hidden" id="TIPO" name="TIPO" value="${tipoSelezionato}"/>
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

	<gene:javaScript>
	function cambiaTipoPubblicazione(tipo) {
		document.location.href="ApriPagina.do?"+csrfToken+"&href=gare/catpub/catpub-lista.jsp?TIPO="+tipo;
	}
  </gene:javaScript>
</gene:template>
