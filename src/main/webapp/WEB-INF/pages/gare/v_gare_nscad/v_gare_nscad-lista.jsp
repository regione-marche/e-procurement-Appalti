<%/*
   * Created on 10-apr-2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_NSCAD")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_NSCAD")}' />
<c:set var="filtro" value="${filtroLivelloUtente}" />



<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_NSCAD-lista">
	<gene:setString name="titoloMaschera" value="Lista gare in scadenza"/>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="V_GARE_NSCAD" where="${filtro}" pagesize="20" tableclass="datilista" sortColumn="3" >
 	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "PAGE.VIS.GARE.GARE-scheda.DATIGENPROT")}'/>

			<gene:campoLista title="Opzioni" width="50">
				
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProt(pageContext, "PAGE.VIS.GARE.GARE-scheda.DATIGENPROT")}' >
							<gene:PopUpItem title="Visualizza gara" href="visualizzaGara('${chiaveRigaJava}')" />
						</c:if>
					</gene:PopUp>
				
			</gene:campoLista>
			<c:set var="tipscad" value='${datiRiga.V_GARE_NSCAD_TIPSCAD}'/>
			<gene:campoLista title="" width="20">
				<IMG SRC="${contextPath}/img/tipscad${tipscad}.gif" >
			</gene:campoLista>
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';visualizzaGara('${chiaveRigaJava}');" />
			<gene:campoLista campo="TIPGEN" headerClass="sortable" />
			<gene:campoLista campo="NGARA" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista campo="OGGETTO" headerClass="sortable" />
			<gene:campoLista campo="TIPSCAD" headerClass="sortable" />
			<gene:campoLista campo="DATASCAD" title="Data termine presentazione" />
			<gene:campoLista campo="ORASCAD" title="Ora"/>
		</gene:formLista>
				</td>
			</tr>
			<tr>
				
			</tr>
		</table>
  </gene:redefineInsert>

	<gene:javaScript>
	// Visualizzazione del dettaglio
	function visualizzaGara(chiaveRiga){
		document.forms[0].entita.value = "GARE";
		listaVisualizza();
	}
	</gene:javaScript>
</gene:template>