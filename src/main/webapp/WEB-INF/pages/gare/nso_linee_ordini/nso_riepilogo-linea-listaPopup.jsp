<%/*
   * Created on 06-12-2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<style type="text/css">

	table.datilista tr.riepilogoTotale{
		background-color: #EFEFEF;
		font-size: 11px;
		font-weight: bold;
	}

	table.datilista td.riepilogoTotaleDesc{
		text-align: right !important;
		padding-top: 2px !important;
		padding-bottom: 2px !important;
	}

	table.datilista td.riepilogoTotaleImp {
		padding-top: 2px !important;
		padding-bottom: 2px !important;
	}

</style>

<c:choose>
	<c:when test='${not empty param.idOrdine}'>
		<c:set var="idOrdine" value="${param.idOrdine}" />
	</c:when>
	<c:otherwise>
		<c:set var="idOrdine" value="${idOrdine}" />
	</c:otherwise>
</c:choose>




<c:set var="idLineaOrdine" value='${gene:getValCampo(key,"ID")}' />
<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNsoDatiLineaOrdineFunction", pageContext, idOrdine,idLineaOrdine)}'/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="where" value="codice= '${requestScope.codiceLinea}' and exists(select ngara from nso_ordini where 
 NSO_ORDINI.VERSIONE=0 AND NSO_ORDINI.ID= NSO_LINEE_ORDINI.NSO_ORDINI_ID and NSO_ORDINI.NGARA='${requestScope.numeroGara}')" />

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="NSO_UTILIZZO_LINEA-lista">
	<gene:setString name="titoloMaschera" value="Elenco ordini che utilizzano la linea con codice ${codiceLinea}"/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
		
			<tr>
				<td>
					<table class="arealayout" id="riepilogoDisponibile">
							<tr colspan="2"/>
					</table>
				</td>
			</tr>
		
		
			<tr>
				<td>
				<gene:formLista entita="NSO_LINEE_ORDINI" pagesize="25" tableclass="datilista" gestisciProtezioni="false" sortColumn="1" where="${where}" >
					<gene:campoLista campo="CODICE"  visibile="false"/>
					<gene:campoLista campo="CODORD" entita="NSO_ORDINI" where="NSO_ORDINI.ID=NSO_LINEE_ORDINI.NSO_ORDINI_ID"  ordinabile="true" />  
					<gene:campoLista campo="STATO_ORDINE" entita="NSO_ORDINI" where="NSO_ORDINI.ID=NSO_LINEE_ORDINI.NSO_ORDINI_ID"  ordinabile="true" />  
					<gene:campoLista campo="QUANTITA" title="Quantita consumata" ordinabile="true"/>
					<gene:campoLista campo="PREZZO_UNITARIO" />
					<c:set var="calcolo" value="${datiRiga.NSO_LINEE_ORDINI_QUANTITA*datiRiga.NSO_LINEE_ORDINI_PREZZO_UNITARIO}" />
					<gene:campoLista title="Totale calcolato" campo="TOTALE_CALCOLATO" visibile="true" campoFittizio="true" definizione="F24.5;0;;MONEY5;NSO_LO_PU" value="${calcolo}" computed="true" />
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="window.close();">&nbsp;
					
				</td>
			</tr>			
		</table>
  </gene:redefineInsert>
  
	<gene:javaScript>
	
		aggiungiTotali();
		
		function aggiungiTotali() {
		
			elementoFMT= $('<fmt:formatNumber type="number" value="${qtaConsumata}" var="totFmt" minFractionDigits="2" maxFractionDigits="2" pattern="###,###,###,###,##0.00"></fmt:formatNumber>');
			var _tr = $("<tr/>",{"class": "riepilogoTotale"});
			var _td1 =  $("<td/>",{"class": "riepilogoTotaleDesc","colspan": "2"});
			_td1.append("<b> Totale quantità consumata:</b>");
			_tr.append(_td1);
			var _td1 = $("<td/>",{"class": "riepilogoTotaleImp"});
			_td1.append($(elementoFMT));
			_td1.append($("<span/>"),"${totFmt}");
			_td1.append($("<span/>"),"<c:if test='${!empty totFmt}'>&nbsp;</c:if>");
			_tr.append(_td1);
			
			elementoFMT= $('<fmt:formatNumber type="number" value="${prezzoConsumato}" var="totFmt" minFractionDigits="2" maxFractionDigits="2" pattern="###,###,###,###,##0.00"></fmt:formatNumber>');
			var _td2 =  $("<td/>",{"class": "riepilogoTotaleDesc","colspan": "1"});
			_td2.append("<b> Totale importo consumato:</b>");
			_tr.append(_td2);
			var _td2 = $("<td/>",{"class": "riepilogoTotaleImp"});
			_td2.append($(elementoFMT));
			_td2.append($("<span/>"),"${totFmt}");
			_td2.append($("<span/>"),"<c:if test='${!empty totFmt}'>&nbsp;&euro;</c:if>");
			_tr.append(_td2);
			
				
			$(".datilista").find('tbody').append(_tr);
			
			elementoFMT= $('<fmt:formatNumber type="number" value="${qtaDisponibile}" var="totFmt" minFractionDigits="2" maxFractionDigits="2" pattern="###,###,###,###,##0.00"></fmt:formatNumber>');
			var _tr = $("<tr/>",{"class": "riepilogoTotale"});
			var _td3 =  $("<td/>",{"class": "riepilogoTotaleDesc"});
			_td3.append("<b> Totale quantità disponibile:</b>");
			_tr.append(_td3);
			var _td3 = $("<td/>",{"class": "riepilogoTotaleImp"});
			_td3.append($(elementoFMT));
			_td3.append($("<span/>"),"${totFmt}");
			_td3.append($("<span/>"),"<c:if test='${!empty totFmt}'>&nbsp;</c:if>");
			_tr.append(_td3);

			$("#riepilogoDisponibile").find('tbody').append(_tr);
			
			elementoFMT= $('<fmt:formatNumber type="number" value="${prezzoDisponibile}" var="totFmt" minFractionDigits="2" maxFractionDigits="2" pattern="###,###,###,###,##0.00"></fmt:formatNumber>');
			var _tr = $("<tr/>",{"class": "riepilogoTotale"});
			var _td4 =  $("<td/>",{"class": "riepilogoTotaleDesc"});
			_td4.append("<b> Totale importo disponibile:</b>");
			_tr.append(_td4);
			var _td4 = $("<td/>",{"class": "riepilogoTotaleImp"});
			_td4.append($(elementoFMT));
			_td4.append($("<span/>"),"${totFmt}");
			_td4.append($("<span/>"),"<c:if test='${!empty totFmt}'>&nbsp;&euro;</c:if>");
			_tr.append(_td4);

			$("#riepilogoDisponibile").find('tbody').append(_tr);
			
			
			
			
		};		
	
	
	
	</gene:javaScript>
	
</gene:template>