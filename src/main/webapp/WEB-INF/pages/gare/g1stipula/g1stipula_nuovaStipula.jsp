<%
/*
 * Created on 28-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI
 // SELEZIONE DEL TIPO DI GARA (IN FASE DI CREAZIONE)
%>


<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:set var="Tab1tipA1174" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1174","1","true")}'/>

<gene:template file="scheda-template.jsp">
<gene:redefineInsert name="addHistory" />
<gene:setString name="titoloMaschera" value="Creazione nuova stipula" />
<gene:redefineInsert name="corpo">
<gene:redefineInsert name="documentiAssociati" />
<gene:redefineInsert name="noteAvvisi" />

<gene:redefineInsert name="addToAzioni" >
	<tr>
		<td class="vocemenulaterale">
			<a href="javascript:procediNuovaStipula();" title="Avanti" tabindex="1501">
				Avanti &gt;
			</a>
		</td>
	</tr>
	<tr>
		<td class="vocemenulaterale">
			<a href="javascript:annullaProcedi();" title="Annulla" tabindex="1502">
				Annulla
			</a>
		</td>
	</tr>
</gene:redefineInsert>

<form action="" name="formRadioButStipula">
	<table class="dettaglio-notab">
		<tr>
		 	<td>
		 		<br>
				&nbsp;<input type="radio" name="modstip" id="radiomodstip1" value="1" checked="checked" />&nbsp;<b>Stipula un contratto collegato ad una gara aggiudicata con la piattaforma</b>
				<br>
				<div style="padding-left: 29px;">
					<i>Scegliere questa opzione per procedere con l'iter di stipula di un contratto collegato ad una gara a lotto unico o a pi&ugrave; lotti i cui dati sono gi&agrave; presenti nella piattaforma</i>
				</div>
				
				<br>
				&nbsp;<input type="radio" name="modstip" id="radiomodstip2" value="2" />&nbsp;<b>Stipula un contratto inserendo contestualmente i dati di aggiudicazione</b>
				<br>
				<div style="padding-left: 29px;">
					<i>Scegliere questa opzione per poter inserire rapidamente i dati di aggiudicazione quando non si &egrave; gestita la gara o l'affidamento in piattaforma e procedere con l'iter di stipula del relativo contratto; verr&agrave; automaticamente inserita anche la gara collegata, accessibile dal profilo abbinato al tipo di procedura scelto.</i>
				</div>
		 	    <br>
				<c:if test="${Tab1tipA1174 eq 1}">
					&nbsp;<input type="radio" name="modstip" id="radiomodstip3" value="3" />&nbsp;<b>Crea atto aggiuntivo/variante a contratto esistente</b>
					<br>
					<div style="padding-left: 29px;">
						<i>Scegliere questa opzione per poter creare un atto aggiuntivo/variante a contratto esistente</i>
					</div>
					<br>
				</c:if>	
			</td>
		</tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaProcedi();">&nbsp;&nbsp;&nbsp;&nbsp;
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:procediNuovaStipula();">&nbsp;
			</td>
		</tr>
	</table>
</form>



</gene:redefineInsert>
<gene:javaScript>
	$(document).ready(function() {

	$("#alinkIndietro").parent().remove();
	})

	function procediNuovaStipula(){
		if($("#radiomodstip1").prop('checked')){
			document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1stipula/g1stipula_assAggiud.jsp&modo=NUOVO";
		}else{
			if($("#radiomodstip2").prop('checked')){
				document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1stipula/g1stipula_creaAggiud.jsp&modo=NUOVO";
			}
			else{
			   if($("#radiomodstip3").prop('checked')){
				document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1stipula/g1stipula_collAggiud.jsp&modo=NUOVO";
			}
		}
				
		}
		
		
	}
	
	function annullaProcedi(){
		bloccaRichiesteServer();
		historyBack();
	}
	
</gene:javaScript>	
</gene:template>