
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
	<c:when test='${not empty param.tipgen}'>
		<c:set var="tipgen" value="${param.tipgen}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipgen" value="${tipgen}" />
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereV_AGGIUDICATARI_STIPULAFunction" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Selezione della gara oggetto di stipula" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
	
			<br/>
			Nella lista sottostante sono riportate le gare o lotti di gara aggiudicati, per cui si puo' procedere alla stipula del contratto.  
			<br/>
			Selezionare la gara o il lotto di gara oggetto di stipula.
			<br/>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="V_AGGIUDICATARI_STIPULA" pagesize="25" tableclass="datilista" gestisciProtezioni="false" sortColumn="-2;6" where="${where}" >
					<gene:campoLista title="Opzioni"	width="50">
						<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
						</gene:PopUp>
					</gene:campoLista>
					<gene:campoLista campo="CODICE" title="Codice gara" ordinabile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs})"/>
					<gene:campoLista campo="CODGAR" visibile="false"/>
					<gene:campoLista campo="NGARA"  visibile="false" />
					<gene:campoLista campo="NCONT"  visibile="false"/>
					<gene:campoLista campo="CODIGA" title="N.lotto" entita="GARE" where="V_AGGIUDICATARI_STIPULA.CODLOTTO=GARE.NGARA" href="javascript:archivioSeleziona(${datiArchivioArrayJs})"/>
					<gene:campoLista campo="CODCIG"   gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCigListaGare"/>
					<gene:campoLista campo="OGGETTO"  />
					<gene:campoLista campo="CODIMP"  />
					<gene:campoLista campo="NOMEST"  />
					<input type="hidden" name="tipgen" id="tipgen" value="${tipgen}" />
					
				</gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="window.close();">&nbsp;
					
				</td>
			</tr>			
		</table>
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
		
		
	</gene:javaScript>
</gene:template>

