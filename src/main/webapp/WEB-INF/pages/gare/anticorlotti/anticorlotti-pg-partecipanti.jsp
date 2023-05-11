<%/*
   * Created on 04-09-2013
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

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="id" value='${gene:getValCampo(key, "ANTICORLOTTI.ID")}'/>

<c:set var="completato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCompletatoFunction", pageContext, key)}' />

<c:set var="where" value="ANTICORPARTECIP.IDANTICORLOTTI = ${id }"/>

<c:choose>
	<c:when test='${not empty param.paginaAppalti}'>
		<c:set var="paginaAppalti" value="${param.paginaAppalti}" />
	</c:when>
	<c:when test='${not empty requestScope.pagina}'>
		<c:set var="paginaAppalti" value="${requestScope.paginaAppalti}" />
	</c:when>
	<c:otherwise>
		<c:set var="paginaAppalti" value="${paginaAppalti}" />
	</c:otherwise>
</c:choose>


<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="ANTICORPARTECIP" where="${where }" tableclass="datilista" sortColumn="3"
					gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAnticorAppalti" pagesize="25" >
				
				
				<gene:redefineInsert name="listaNuovo" >
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && paginaAppalti eq 1 and completato ne 1}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:listaNuovo();" title="Inserisci" tabindex="1501">
									${gene:resource("label.tags.template.lista.listaNuovo")}</a></td>
						</tr>
						</c:if>
				</gene:redefineInsert>
				<gene:redefineInsert name="listaEliminaSelezione">
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && paginaAppalti eq 1 and completato ne 1}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1502">
									${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
							</td>
						</tr>
					</c:if>
				</gene:redefineInsert>									
				
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza partecipante" href="visualizzaPartecipante('${chiaveRigaJava}',${datiRiga.ANTICORPARTECIP_TIPO },'${paginaAppalti}')" />
							<c:if test='${paginaAppalti eq 1 and completato ne 1}'>
								<gene:PopUpItem title="Modifica partecipante" href="modificaPartecipante('${chiaveRigaJava}','${datiRiga.ANTICORPARTECIP_TIPO }')" />
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL") && paginaAppalti eq 1  and completato ne 1}' >
								<gene:PopUpItem title="Elimina partecipante" href="javascript:listaElimina()" />
							</c:if>
						</gene:PopUp>
						<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && paginaAppalti eq 1 and completato ne 1}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}"  />
						</c:if>	
					</c:if>
				</gene:campoLista>
				
				<gene:campoLista campo="ID" visibile="false" />
				<gene:campoLista campo="RAGSOC" href="javascript:visualizzaPartecipante('${chiaveRigaJava}',${datiRiga.ANTICORPARTECIP_TIPO },'${paginaAppalti }');" />
				<%-- gene:campoLista title="Cod. fisc." campo="CODFISC_FIT" headerClass="sortable" campoFittizio="true" definizione="T16" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCodFiscAdempimenti"/ --%>
				<gene:campoLista campo="TIPO"/>
				<gene:campoLista campo="AGGIUDICATARIA" />
				<gene:campoLista campo="DAANNOPREC" entita="ANTICORLOTTI" where="ANTICORLOTTI.ID=ANTICORPARTECIP.IDANTICORLOTTI" visibile="false"/>
				<gene:campoLista campo="IDANTICORLOTTI" visibile="false"/>
				<input type="hidden" name="paginaAppalti" id="paginaAppalti" value="${paginaAppalti}"/>
			</gene:formLista>
		</td>
	</tr>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<gene:insert name="pulsanteListaInserisci">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && paginaAppalti eq 1 and completato ne 1}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovo()">
				</c:if>
			</gene:insert>
			<gene:insert name="pulsanteListaEliminaSelezione">
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && paginaAppalti eq 1 and completato ne 1}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
				</c:if>
			</gene:insert>
		
			&nbsp;
		</td>
	</tr>
</table>
<gene:javaScript>
	function visualizzaPartecipante(chiaveRiga,tipo,paginaAppalti){
		var bloccoModifica=true;
		if(paginaAppalti =="1")
			bloccoModifica=false
		document.forms[0].action += "&tipo="+tipo+ "&bloccoModifica=" + bloccoModifica;
		document.forms[0].keyParent.value=chiaveRiga;
		document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="apri";
		document.forms[0].activePage.value="0";
		document.forms[0].submit();
		
		//listaVisualizza();
	}
	
	function modificaPartecipante(chiaveRiga,tipo){
		document.forms[0].action += "&tipo="+tipo;
		document.forms[0].keyParent.value=chiaveRiga;
		document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="modifica";
		document.forms[0].activePage.value="0";
		document.forms[0].submit();
	}
	
	var selezionaPaginaDefault = selezionaPagina;
		var selezionaPagina = selezionaPaginaCustom;
		function selezionaPaginaCustom(pageNumber){
			var paginaAppalti="${paginaAppalti }"
			document.pagineForm.action += "&paginaAppalti=" + paginaAppalti;
			selezionaPaginaDefault(pageNumber);
		}

</gene:javaScript>
