<%/*
   * Created on 22-05-2012
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

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_STIPULA")}' />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro=""/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" CENINT = '${sessionScope.uffint}'"/>
</c:if>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_STIPULA-lista" >

	<gene:setString name="titoloMaschera" value="Lista stipule contratti"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.G1STIPULA-scheda")}'/>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
			
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="V_GARE_STIPULA" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-3" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1STIPULA"
  		where="${filtroUffint}">
			<gene:campoLista campo="ID" visibile="false" />

			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItem title="Visualizza stipula" href="javascript:listaVisualizzaStipula('${chiaveRigaJava}')" />
					<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
						<gene:PopUpItem title="Modifica stipula" href="javascript:listaModificaStipula('${chiaveRigaJava}')" />
					</c:if>
					<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL") && (datiRiga.V_GARE_STIPULA_STATO < 3)}' >
						<gene:PopUpItem title="Elimina stipula" href="javascript:listaEliminaStipula()" />
					</c:if>
					<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA.Condividi-stipula")}'> 
						<gene:PopUpItem title="Condividi e proteggi stipula" href="javascript:apriGestionePermessiStipula('${datiRiga.V_GARE_STIPULA_ID}','${datiRiga.V_GARE_STIPULA_CODSTIPULA}',${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext, "ALT","Archivia-stipula")}'>
						<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && (datiRiga.V_GARE_STIPULA_ISARCHI ne "1") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItem title="Archivia stipula" href="archiviaStipula()" />
						</c:if>
					</c:if>
					
				</gene:PopUp>
		</gene:campoLista>
			<% // Campi veri e propri %>
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizzaStipula('${chiaveRigaJava}');" />
			
			<gene:campoLista campo="CODSTIPULA" title="Codice stipula" href="${gene:if(visualizzaLink, link, '')}" />
			<gene:campoLista campo="NGARA" title="Cod.gara o lotto" />
			<gene:campoLista campo="CODCIG" title="Codice CIG" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCigListaGare"/>
			<c:choose>
				<c:when test="${!empty (filtroLivelloUtente)}">
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.IDSTIPULA=V_GARE_STIPULA.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.IDSTIPULA=V_GARE_STIPULA.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
				</c:otherwise>
			</c:choose>
			<gene:campoLista campo="OGGETTO" />
			<gene:campoLista campo="DAATTO" />
			<gene:campoLista campo="CODIMP"  visibile="false"/>
			<gene:campoLista campo="NOMEST" title="Ditta aggiudicataria"/>
			<gene:campoLista campo="STATO" />
			<gene:campoLista campo="NOME_CONTRACT" />
			<gene:campoLista campo="CENINT" visibile="false" />
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
		
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStipula.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="id" id="id" value="" />
			<input type="hidden" name="codstipula" id="codstipula" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>		
		
  </gene:redefineInsert>

	<gene:javaScript>
	
	//Abilitazione alla gestione del codice html nei tooltip solo per il campo CIG e solo quando vi è
	//un elenco di codici CIG
	$(function() {
	  $('.tooltipCig').tooltip({
	    content: function(){
	      var element = $( this );
	      return element.attr('title')
	    }
	  });
	});
	
	function listaEliminaStipula(){
		chiaveRiga = chiaveRiga.replace("V_GARE_STIPULA", "G1STIPULA");
		var href = "href=gare/v_gare_stipula/eliminazione-stipula.jsp&chiaveRiga=" + chiaveRiga + "&numeroPopUp=1";
		win = openPopUpCustom(href, "confermaEliminaStipula", 500, 200, "no", "no");

		if(win!=null)
			win.focus();
	}
	
	function confermaDelete() {
		closePopUps();
		document.forms[0].entita.value = "G1STIPULA";
		document.forms[0].key.value = chiaveRiga;
		document.forms[0].metodo.value = "elimina";
		document.forms[0].submit();
	}
	
	
	
	// Visualizzazione del dettaglio
	function listaVisualizzaStipula(chiaveRiga){
		document.forms[0].keyParent.value = chiaveRiga;
		document.forms[0].entita.value = "G1STIPULA";
		listaVisualizza();
	}
	
	function listaModificaStipula(chiaveRiga){
		document.forms[0].keyParent.value = chiaveRiga;
		document.forms[0].entita.value = "G1STIPULA";
		listaModifica();
	}
	
	function listaNuovo(){
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1stipula/g1stipula_nuovaStipula.jsp";
	}
	

	function archiviaStipula(){
		chiaveRiga = chiaveRiga.replace("V_GARE_STIPULA", "G1STIPULA");
		href = "href=gare/v_gare_torn/v_gare_torn-popup-archivia-gara.jsp&key="+chiaveRiga;
		openPopUpCustom(href, "archiviaGara", 500, 350, "no", "yes");
	}
	
	function apriGestionePermessiStipula(id,codstipula,permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.id.value = id;
		formVisualizzaPermessiUtentiStandard.codstipula.value = codstipula;
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.submit();
	}
	
	
	</gene:javaScript>
</gene:template>