<%
/*
 * Created on: 20/04/2020
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


<c:set var="ngara" value='${gene:getValCampo(key,"NGARA") }'/>
<c:set var="ncont" value='${gene:getValCampo(key,"NCONT") }'/>

<c:choose>
	<c:when test='${!empty param.cenint}'>
		<c:set var="cenint" value='${param.cenint}' />
	</c:when>
	<c:otherwise>
		<c:set var="cenint" value="${cenint}" />
	</c:otherwise>
</c:choose>

<c:set var="codimp" value='${gene:getValCampo(key,"CODIMP") }'/>
<c:set var="ngaral" value='${gene:getValCampo(key,"NGARAL") }'/>
<c:set var="where" value="G1AQSPESA.NGARA = '${ngara }' and G1AQSPESA.NCONT = ${ncont } and G1AQSPESA.CENINT = '${cenint }'"/>
<c:set var="nomein" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNOMEINFunction", pageContext, cenint)}'/>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />



<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1AQSPESA-lista" >
	<gene:setString name="titoloMaschera" value="Prenotazioni di spesa dell'ente ${nomein }"/>

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr><td >
			<gene:formLista entita="G1AQSPESA" where="${where }" sortColumn="6;2" pagesize="20" tableclass="datilista"
			gestisciProtezioni="true" > 
				
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				
				<!-- Se il nome del campo ? vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">					
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.G1AQSPESA-scheda")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza"/>
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD") && autorizzatoModifiche ne "2"}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica"/>
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") && autorizzatoModifiche ne "2"}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.elimina" />
							</c:if>							
					</gene:PopUp>
								
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
				<% // Campi veri e propri %>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.G1AQSPESA-scheda")}'/>
				
				<gene:campoLista campo="ID" visibile="false"/>
				<gene:campoLista campo="NGARA" visibile="false"/>
				<gene:campoLista campo="NCONT" visibile="false"/>
				<gene:campoLista campo="CENINT" visibile="false"/>
				<gene:campoLista campo="DATRIC" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
				<gene:campoLista campo="IMPRIC" headerClass="sortable"/>
				<gene:campoLista campo="DATAUT" headerClass="sortable"/>
				<gene:campoLista campo="IMPAUT" headerClass="sortable"/>
				<input type="hidden" name="cenint" id="cenint" value="${cenint}"/>
			</gene:formLista>
		</td></tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<gene:insert name="pulsanteListaInserisci">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && autorizzatoModifiche ne "2"}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovo()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteListaEliminaSelezione">
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && autorizzatoModifiche ne "2"}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
					</c:if>
				</gene:insert>
			
				&nbsp;
			</td>
		</tr>
		</table>
  </gene:redefineInsert>
  <gene:javaScript>
	
	function listaVisualizzaCustom() {
		document.forms[0].action=document.forms[0].action+"&cenint=${cenint}&ngara=${ngara}&ncont=${ncont}&codimp=${codimp}&ngaral=${ngaral}";
		listaVisualizzaDefault();
	}
	
	var listaVisualizzaDefault = listaVisualizza;
	var listaVisualizza = listaVisualizzaCustom;
	
	function listaModificaCustom() {
		document.forms[0].action=document.forms[0].action+"&cenint=${cenint}&ngara=${ngara}&ncont=${ncont}&codimp=${codimp}&ngaral=${ngaral}";
		listaModificaDefault();
	}
	
	var listaModificaDefault = listaModifica;
	var listaModifica = listaModificaCustom;
	
	function listaNuovoCustom() {
		document.forms[0].action=document.forms[0].action+"&cenint=${cenint}&ngara=${ngara}&ncont=${ncont}&codimp=${codimp}&ngaral=${ngaral}";
		listaNuovoDefault();
	}
	
	var listaNuovoDefault = listaNuovo;
	var listaNuovo = listaNuovoCustom;
 	
</gene:javaScript>
</gene:template>

