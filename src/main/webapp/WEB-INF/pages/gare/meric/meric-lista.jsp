<%/*
   * Created on 20-05-2014
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
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "MERIC")}' />

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" CENINT = '${sessionScope.uffint}'"/>
</c:if>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MERIC-lista" >
	<gene:setString name="titoloMaschera" value="Lista ricerche di mercato"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.MERIC-scheda")}'/>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
			
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
				
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="MERIC" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="3" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMERIC"
  		where="${filtroUffint}">
  	

  	<c:set var='visualizzaPopUp' value='${visualizzaLink || gene:checkProtFunz(pageContext, "MOD","MOD") || gene:checkProtFunz(pageContext, "DEL","DEL")}'/>
  	
			<gene:campoLista title="Opzioni" width="50">
				<c:if test='${visualizzaPopUp}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GARE.MERIC-scheda")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza ricerca di mercato"/>
						</c:if>
						<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtObj(pageContext, "MASC.VIS", "GARE.MERIC-scheda") and gene:checkProtFunz(pageContext, "MOD", "MOD")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica" title="Modifica ricerca di mercato" />
						</c:if>
						<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtFunz(pageContext, "DEL", "DEL")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina ricerca di mercato" />
						</c:if>
						<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_TORN-lista.Condividi-gara")}'> 
							<gene:PopUpItem title="Punto ordinante e istruttore" href="javascript:apriGestionePermessiRicercaMercatoStandard('${datiRiga.MERIC_ID}','${datiRiga.MERIC_CODRIC}',${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Archivia-ricercaMercato")}'>
							<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && (datiRiga.MERIC_ISARCHI ne "1") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItem title="Archivia ricerca di mercato" href="archiviaRicercaMercato()" />
							</c:if>
						</c:if>			
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi veri e propri %>
			
			<gene:campoLista campo="ID" visibile="false"/>
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="CODRIC" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista campo="OGGETTO"  headerClass="sortable"  />
			<gene:campoLista campo="DATDEF" headerClass="sortable" />
			<gene:campoLista campo="ISARCHI" visibile="false" />
						
			<c:choose>
				<c:when test="${!empty (filtroLivelloUtente)}">
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.IDMERIC=MERIC.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.IDMERIC=MERIC.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
				</c:otherwise>
			</c:choose>
					
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
	
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiRicercaMercato.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="id" id="id" value="" />
			<input type="hidden" name="codric" id="codric" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>
		
  </gene:redefineInsert>

	<gene:javaScript>
	
	
	function archiviaRicercaMercato(){
		href = "href=gare/v_gare_torn/v_gare_torn-popup-archivia-gara.jsp&key="+chiaveRiga;
		openPopUpCustom(href, "archiviaAvviso", 500, 350, "no", "yes");
	}

	function apriGestionePermessiRicercaMercatoStandard(id,codric,permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.id.value = id;
		formVisualizzaPermessiUtentiStandard.codric.value = codric;
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.submit();
	}
	
	</gene:javaScript>
</gene:template>