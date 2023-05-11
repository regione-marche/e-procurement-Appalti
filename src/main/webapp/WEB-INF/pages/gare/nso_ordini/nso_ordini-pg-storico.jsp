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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereNSO_ORDINIFunction" />

<c:set var="id" value='${gene:getValCampo(key, "ID")}'/>
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "NSO_ORDINI")}' />
<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=""/>
</c:if>
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneDatiNsoFunction", pageContext, id)}'/>

<c:set var="where" value="NSO_ORDINI.ID_ORIGINARIO = ${requestScope.idOriginarioOrdine} AND NSO_ORDINI.VERSIONE > 0" />


	<gene:setString name="titoloMaschera" value="Storia ordine"/>
	<gene:redefineInsert name="listaNuovo" >
	</gene:redefineInsert>
	<gene:redefineInsert name="listaEliminaSelezione">
	</gene:redefineInsert>									
	
 
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="NSO_ORDINI" where='${where}' tableclass="datilista" sortColumn="3"
					gestisciProtezioni="true"  pagesize="25" >
  	
			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza ordine" href="javascript:listaVisualizza()" />
					</gene:PopUp>
			</gene:campoLista>
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="ID" visibile="false" />
			<gene:campoLista campo="VERSIONE" visibile="false" />
			<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNsoDatiListaOrdiniFunction", pageContext, datiRiga.NSO_ORDINI_ID)}'/>
			<gene:campoLista campo="CODORD" title="Codice ordine" href="${gene:if(visualizzaLink, link, '')}" />
			<gene:campoLista campo="NGARA" title="Gara/Lotto di riferimento" />
			<gene:campoLista campo="OGGETTO" title="Oggetto" />
			<gene:campoLista campo="CUP" title="Codice CUP" />
			<gene:campoLista campo="CIG" title="Codice CIG" />
			<gene:campoLista title="Codice ordine collegato" campo="CODORD_COLLEGATO" entita="NSO_ORDINI" campoFittizio="true" value="${requestScope.codiceOrdineCollegato}" visibile="true"/>
			<gene:campoLista campo="DATA_ORDINE" title="Data ordine" />
			<gene:campoLista campo="DATA_SCADENZA" title="Data scadenza" />
			<gene:campoLista campo="IS_REVISIONE" title="Ordine in revisione?" />
			<gene:campoLista campo="STATO_ORDINE" title="Stato ordine" />
			<gene:campoLista title="Autore operazione" entita = "USRSYS" campo="SYSUTE" where="USRSYS.SYSCON=NSO_ORDINI.SYSCON" visibile="true"/>
			<gene:campoLista campo="CODEIN" visibile="false" />
		
		</gene:formLista>
				</td>
			</tr>
		</table>

	<gene:javaScript>
	
	</gene:javaScript>
