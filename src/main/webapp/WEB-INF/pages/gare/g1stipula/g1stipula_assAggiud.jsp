<%/*
   * Created on 02-10-2018
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:template file="scheda-template.jsp">
<gene:setString name="titoloMaschera" value="Crea nuova stipula"/>

	<c:set var="modo" value="NUOVO" />
	<gene:redefineInsert name="documentiAzioni" />
	<gene:redefineInsert name="schedaConferma" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaStipula();" title="Avanti" tabindex="1502">
					Avanti &gt;
				</a>
			</td>
		</tr>
	</gene:redefineInsert>
	<gene:redefineInsert name="schedaAnnulla" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annullaCreazione();" title="Annulla" tabindex="1504">
					Annulla
				</a>
			</td>
		</tr>	
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="G1STIPULA" gestisciProtezioni="true" >
			<gene:campoScheda>
				<td colspan="2">
					<br><b>Selezione della gara oggetto di stipula</b>
					<br><br>
				</td>
			</gene:campoScheda>
			<gene:archivio titolo="gare o lotti oggetto di stipula"
				obbligatorio="true" 
				scollegabile="false"
				inseribile="false"
				lista='gare/g1stipula/g1stipula-popup-selezionaAggiudicatario.jsp' 
				scheda="" 
				schedaPopUp="" 
				campi="V_AGGIUDICATARI_STIPULA.CODICE;V_AGGIUDICATARI_STIPULA.NGARA;V_AGGIUDICATARI_STIPULA.NCONT;GARE.CODIGA;V_AGGIUDICATARI_STIPULA.CODCIG;V_AGGIUDICATARI_STIPULA.OGGETTO;V_AGGIUDICATARI_STIPULA.CODIMP;V_AGGIUDICATARI_STIPULA.NOMEST" 
				chiave=""
				functionId="default"
				formName="formArchivioAggiudStipula">	
				<gene:campoScheda campo="CODICE" title="Codice gara" entita="V_AGGIUDICATARI_STIPULA" where="G1STIPULA.NGARA=V_AGGIUDICATARI_STIPULA.NGARA" />
				<gene:campoScheda campo="NGARA" title="numero gara" visibile="false" />
				<gene:campoScheda campo="NCONT" visibile="false"/>
				<gene:campoScheda campo="CODIGA" entita="GARE" where="V_AGGIUDICATARI_STIPULA.NGARA=GARE.NGARA" visibile="false"/>
				<gene:campoScheda campo="CODCIG" entita="V_AGGIUDICATARI_STIPULA" where="G1STIPULA.NGARA=V_AGGIUDICATARI_STIPULA.NGARA" />
				<gene:campoScheda campo="OGGETTO" />
				<gene:campoScheda campo="CODIMP"  entita="V_AGGIUDICATARI_STIPULA" where="G1STIPULA.NGARA=V_AGGIUDICATARI_STIPULA.NGARA" />
				<gene:campoScheda campo="NOMEST"  entita="V_AGGIUDICATARI_STIPULA" where="G1STIPULA.NGARA=V_AGGIUDICATARI_STIPULA.NGARA" />
			</gene:archivio>	
			
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
			<c:choose>
		   	<c:when test='${not empty param.modScheda}' >
		      <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:associaAppalto();">&nbsp;
				</c:when>
				<c:otherwise>
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaStipula();">&nbsp;
				</c:otherwise>
			</c:choose>
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>

<gene:javaScript>
	
	$('#G1STIPULA_NGARA').keypress(function(event) {
	    if (event.keyCode == 13) {
	        event.preventDefault();
	    }
	});
	
		function annullaCreazione(){
			bloccaRichiesteServer();
			historyBack();
		}

		function indietro(){
			bloccaRichiesteServer();
			historyBack();
		}

		function creaNuovaStipula(){
			clearMsg();
			var ngara = getValue("G1STIPULA_NGARA");
			//alert(ngara);
			var codiga = getValue("GARE_CODIGA");
			var ncont = getValue("G1STIPULA_NCONT");
			//alert(ncont);
			if(ngara==null || ngara==""){
				outMsg('Il campo "Codice gara oggetto della stipula" è obbligatorio ', "ERR");
				onOffMsg();
				return;
			}else{
				document.forms[0].activePage.value = 0;
				document.forms[0].jspPath.value="/WEB-INF/pages/gare/g1stipula/g1stipula-scheda.jsp";
				document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/g1stipula/g1stipula-scheda.jsp";
				document.forms[0].action = document.forms[0].action + "&ngara=" + ngara + "&ncont=" + ncont+ "&codiga=" + codiga;
				bloccaRichiesteServer();
				document.forms[0].submit();
			}
		}
		

	<c:if test='${not empty param.chiavePadre}'>
		document.forms[0].keyParent.value = ${param.chiavePadre};
	</c:if>

	</gene:javaScript>
</gene:template>