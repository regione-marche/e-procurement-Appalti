<%
/*
 * Created on: 15-mar-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Configurazione scadenze gara Lista-Dettaglio*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:set var="tipolavoro" value="${gene:if(!empty param.TIPLAV,param.TIPLAV,TIPLAV)}"/>
<c:set var="limiteinferiore" value="${gene:if(!empty param.LIMINF,param.LIMINF,LIMINF)}"/>
<c:set var="limitesuperiore" value="${gene:if(!empty param.LIMSUP,param.LIMSUP,LIMSUP)}"/>
<c:set var="tipocalcolo" value="${gene:if(!empty param.CALCOLO,param.CALCOLO,CALCOLO)}"/>

<c:set var="tipolavoroDescrizione" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1007", tipolavoro, "false")}'/>
<c:set var="limiteinferioreVisualizzato" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetMoneyVisuaFunction",limiteinferiore)}' />
<c:set var="limitesuperioreVisualizzato" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetMoneyVisuaFunction",limitesuperiore)}' />
<c:set var="tipocalcoloDescrizione" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1032", tipocalcolo, "false")}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="CATSCA-lista-dettaglio" >
	<gene:setString name="titoloMaschera" value="Configurazione scadenze gara"/>

	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>

	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.lista.titolo"))}' id="listadettaglio" />		
	</gene:redefineInsert>

	<gene:redefineInsert name="corpo">
	<br><br>
	<b>Tipo di calcolo:</b> ${tipocalcoloDescrizione}
	<br><br>
	<b>Tipo di appalto:</b> ${tipolavoroDescrizione}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Da importo:</b> ${limiteinferioreVisualizzato}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>A importo:</b> ${limitesuperioreVisualizzato}
	<br><br>
	
		<table class="lista">
		<tr><td >
			<gene:formLista entita="CATSCA" sortColumn="2;3;4;5;6" pagesize="20" tableclass="datilista"
			gestisciProtezioni="true" where="CATSCA.TIPLAV = ${tipolavoro} and CATSCA.LIMINF = ${limiteinferiore} and CATSCA.LIMSUP = ${limitesuperiore} and CATSCA.CALCOLO = ${tipocalcolo}" > 
				
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">					
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATSCA-scheda")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza"/>
							</c:if>
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATSCA-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica"/>
							</c:if>
					</gene:PopUp>
				</gene:campoLista>
				
				<% // Campi veri e propri %>
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATSCA-scheda")}'/>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="TIPGAR" headerClass="sortable" width="300" href="${gene:if(visualizzaLink, link, '')}"/>
				<gene:campoLista campo="PROURG" headerClass="sortable" />
				<gene:campoLista campo="TERRID" headerClass="sortable" />
				<gene:campoLista campo="DOCWEB" headerClass="sortable" />
				<gene:campoLista campo="NUMGIO" headerClass="sortable" />
				<input type="hidden" id="TIPLAV" name="TIPLAV" value="${tipolavoro}"/>
				<input type="hidden" id="LIMINF" name="LIMINF" value="${limiteinferiore}"/>
				<input type="hidden" id="LIMSUP" name="LIMSUP" value="${limitesuperiore}"/>
				<input type="hidden" id="CALCOLO" name="CALCOLO" value="${tipocalcolo}"/>
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
