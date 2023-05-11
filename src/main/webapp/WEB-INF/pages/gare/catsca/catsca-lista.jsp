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
 /* Configurazione scadenze gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="CATSCA-lista" >
	<gene:setString name="titoloMaschera" value="Configurazione scadenze gara"/>

	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>

	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr><td >
			<gene:formLista entita="CATSCA" sortColumn="2;3;4;5" pagesize="30" tableclass="datilista"
			gestisciProtezioni="true" distinct="true" > 
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" >					
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATSCA-lista-dettaglio")}' >
								<gene:PopUpItem title="Visualizza dettaglio" href="ListaDettaglio('${datiRiga.CATSCA_TIPLAV}','${datiRiga.CATSCA_LIMINF}','${datiRiga.CATSCA_LIMSUP}','${datiRiga.CATSCA_CALCOLO}')"/>
							</c:if>
					</gene:PopUp>
				</gene:campoLista>
				
				<% // Campi veri e propri %>
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.CATSCA-lista-dettaglio")}'/>
				<c:set var="link" value="javascript:ListaDettaglio('${datiRiga.CATSCA_TIPLAV}','${datiRiga.CATSCA_LIMINF}','${datiRiga.CATSCA_LIMSUP}','${datiRiga.CATSCA_CALCOLO}')"/>
				<gene:campoLista campo="CALCOLO" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" />
				<gene:campoLista campo="TIPLAV" headerClass="sortable" />
				<gene:campoLista campo="LIMINF" headerClass="sortable" title="Da importo"/>
				<gene:campoLista campo="LIMSUP" headerClass="sortable" title="A importo"/>
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
   <gene:javaScript>
  function ListaDettaglio(tiplav,liminf,limsup,calcolo) {
	 var href = "href=gare/catsca/catsca-lista-dettaglio.jsp?TIPLAV="+tiplav+"&LIMINF="+liminf+"&LIMSUP="+limsup+"&CALCOLO="+calcolo;
	document.location.href="ApriPagina.do?"+csrfToken+"&"+href;
  }
  </gene:javaScript>
  
</gene:template>
