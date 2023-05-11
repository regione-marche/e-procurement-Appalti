
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
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<style type="text/css">
	
	.nascondi {
		display: none;
	}
	

</style>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Elenco Ivr per l'impresa ${codiceDitta}" />
	<gene:redefineInsert name="corpo">
	<input type="hidden" name="idCalcoloRow" id="idCalcoloRow" value="${idCalcoloRow}"/>
		<table class="lista">
			<tr>
				<td>
					<display:table name="listaIVR" id="IvrForm" class="datilista" pagesize="25" sort="list" >
						
						<display:column title="Id calcolo row" style="display:none;" headerClass="nascondi">
								${IvrForm[0]}
						</display:column>
						<display:column title="Data inserimento" >
								${IvrForm[1]}
						</display:column>
						<display:column title="Ivr">
							<c:choose>
								<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.VisualizzaDettaglioVendorRating")}' >
									<a href="javascript:apriDettaglioIvrDaIdCodiceRow(${IvrForm[0]},${IvrForm[2]})" Title='Apri Dettaglio'>
										${IvrForm[2]}
									</a>
								</c:when>
								<c:otherwise>
									${IvrForm[2]}
								</c:otherwise>
							</c:choose>
						</display:column>
						<display:column title="Data inizio validit&agrave;">
								${IvrForm[3]}
						</display:column>
						<display:column title="Data fine validit&agrave;" >
								${IvrForm[4]}
						</display:column>
						<display:column title="Data inizio sospensione">
								${IvrForm[5]}
						</display:column>
						<display:column title="Data fine sospensione">
								${IvrForm[6]}
						</display:column>
						<display:column title="Sospensione revocata?" decorator="it.eldasoft.sil.pg.tags.gestori.decoratori.BooleanDecorator">
								${IvrForm[7]}
						</display:column>
					</display:table>
				</td>
			</tr>
			<tr>
			    <td class="comandi-dettaglio" colSpan="2">
			    	<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();" >&nbsp;
				</td>
		  </tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	
		function apriDettaglioIvrDaIdCodiceRow(idCodiceRow,ivr){
			var par = "idCodiceRow=" + idCodiceRow;
			par += "&ivr=" + ivr;
			openPopUpActionCustom(contextPath + "/pg/GetWSDettaglioIvr.do", par, "dettaglioIVR", 600, 450, 1, 1);
		}
	
		window.addEventListener("load", 
			function associaFunzioniEventoOnchange(){
				 var idCalcoloRow = document.getElementById('idCalcoloRow').value;
				 var righe = document.getElementById('IvrForm').getElementsByTagName('tr');
				 for(var j=1; j<righe.length; j++){
				 	if(righe[j].cells[0].innerHTML.trim() === idCalcoloRow)
					{
						var els = righe[j].getElementsByTagName('td');

						for(var i=0;i<els.length;i++){
						  els[i].style.background = "yellow";
						}
					}
				 }
			}
		);
	</gene:javaScript>
</gene:template>

