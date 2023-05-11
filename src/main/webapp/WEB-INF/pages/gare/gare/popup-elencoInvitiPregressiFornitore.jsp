
<%
	/*
	 * Created on 30-10-2018
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty param.codiceElenco}'>
		<c:set var="codiceElenco" value="${param.codiceElenco}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceElenco" value="${codiceElenco}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceDitta}'>
		<c:set var="codiceDitta" value="${param.codiceDitta}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceDitta" value="${codiceDitta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipoalgo}'>
		<c:set var="tipoalgo" value="${param.tipoalgo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoalgo" value="${tipoalgo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceElenco}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, codiceElenco, codiceGara, codiceDitta)}' />

${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codiceElenco, "SC", "20")}
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codiceDitta, "SC", "10")}
<c:set var="where" value="DITG.NGARA5 = GARE.NGARA AND GARE.ELENCOE = '${codiceElenco}' AND DITG.ACQUISIZIONE = 3"/> 

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Dettaglio inviti a procedure di gara mediante selezione da elenco per la ditta '${nomimo}'" />
	<gene:redefineInsert name="corpo">
			
		<table class="lista">
			<tr>
			<td> 
			<gene:formLista entita="GARE" where="${where} ${filtro}" pagesize="25" tableclass="datilista" gestisciProtezioni="false" sortColumn="-2" >

					<gene:campoLista campo="GENERE" ordinabile="false" visibile="false"/>
					<gene:campoLista campo="NGARA5" entita="DITG" where="GARE.NGARA = DITG.NGARA5 and DITTAO = '${codiceDitta}'" ordinabile="false"/>
					<gene:campoLista campo="CODCIG" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.GetCodcigInvitiPregressiFunction', pageContext, datiRiga.V_GARE_TORN_GENERE, datiRiga.DITG_NGARA5)}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCigListaGare" ordinabile="false"/>
					<gene:campoLista campo="OGGETTO" entita="V_GARE_TORN" where="V_GARE_TORN.CODGAR=GARE.CODGAR1" ordinabile="false"/>
					<gene:campoLista campo="INVGAR" entita="DITG" where="GARE.NGARA = DITG.NGARA5 and DITTAO = '${codiceDitta}'" ordinabile="false"/>
					<gene:campoLista campo="DITTAO" entita="DITG" where="GARE.NGARA = DITG.NGARA5 and DITTAO = '${codiceDitta}'" visibile="false"/>
					<gene:campoLista campo="RTOFFERTA" entita="DITG" where="GARE.NGARA = DITG.NGARA5 and DITTAO = '${codiceDitta}'" visibile="false"/>
					
					<gene:campoLista campo="CODGAR1" ordinabile="false" visibile="false"/>
					<gene:campoLista campo="DINVIT" title="Data invito" entita="TORN" where="torn.codgar = gare.codgar1" visibile="true" ordinabile="false"/>
					
					<gene:campoLista campo="INVOFF" title="Inviato offerta?" campoFittizio="true" definizione="A2;" visibile="true" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoInvoffDittaGara" />
					<gene:campoLista campo="AGGIUDICATARIA" visibile="true" title="Aggiudicataria ?" campoFittizio="true" definizione="N10;" value="" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoDittaAggiudicatariaGara"/>
					<gene:campoLista campo="GENERE" entita="V_GARE_TORN" where="V_GARE_TORN.CODICE=GARE.NGARA" visibile="true" title="Gara in lotti?" ordinabile="false"/>

				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
					&nbsp;
				</td>
			</tr>
		</table>

	</gene:redefineInsert>
	<gene:javaScript>
		
		$(function() {
		  $('.tooltipCig').tooltip({
			content: function(){
			  var element = $( this );
			  return element.attr('title')
			}
		  });
		});
		
		function chiudi(){
			window.close();
		}

	</gene:javaScript>

</gene:template>

