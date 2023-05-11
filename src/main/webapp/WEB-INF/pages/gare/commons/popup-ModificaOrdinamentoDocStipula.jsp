<%/*
   * Created on 08-02-2022
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty param.idStipula}'>
		<c:set var="idStipula" value="${param.idStipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="idStipula" value="${idStipula}" />
	</c:otherwise>
</c:choose>


<c:set var="titoloForm" value="Modifica disposizione documenti"/>

<c:set var="where" value="g1docstipula.idstipula='${idStipula}'"/>

<gene:template file="popup-template.jsp" gestisciProtezioni="false" >

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.rowsorter-2.1.1.js"></script>
	</gene:redefineInsert>	

	<gene:setString name="titoloMaschera" value="${titoloForm}" />
	

	<c:choose>
		<c:when test='${isarchi eq 1}'>
			<c:set var="modo" value="APRI" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="modo" value="MODIFICA" scope="request" />
		</c:otherwise>
	</c:choose>
	
			
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	
	<br>
	
	Nella lista sotto sono riportati i documenti della stipula.<br>
	Per modificare l'ordine con cui questi documenti vengono visualizzati, trascinare le righe premendo con il mouse sulla prima colonna a sinistra.   
		
  	<%// Creo la lista %>
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="G1DOCSTIPULA" where="${where }" pagesize="0" tableclass="datilista" sortColumn="4;5" gestisciProtezioni="false" >
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					
					<gene:campoLista title="${titoloMenu }" width="${dim }" visibile="true" >
						<c:if test="${currentRow >= 0}">		
								<img width="16" height="16" class="sort-handler" title="Trascina per cambiare l'ordine" alt="Trascina per cambiare l'ordine" src="${pageContext.request.contextPath}/img/cambia_ordine.png"/>
						</c:if>
					</gene:campoLista>
						
					<gene:campoLista campo="ID"  visibile="false" />
					<gene:campoLista campo="IDSTIPULA"  visibile="false" />
					<gene:campoLista campo="FASE"  visibile="true"/>
					<gene:campoLista campo="NUMORD"  visibile="false"/>
					<gene:campoLista campo="TITOLO"  />
					<gene:campoLista campo="DESCRIZIONE"  />
					<gene:campoLista campo="VISIBILITA"  />
					<gene:campoLista campo="IDPRG" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				    <gene:campoLista campo="IDDOCDIG" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				    <gene:campoLista campo="DIGNOMDOC" visibile="true" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" ordinabile="false"
				    href="javascript: 	visualizzaFileAllegato('${datiRiga.V_GARE_DOCSTIPULA_IDPRG}','${datiRiga.V_GARE_DOCSTIPULA_IDDOCDIG}', '${datiRiga.V_GARE_DOCSTIPULA_DIGNOMDOC}');" />
					<gene:campoLista campo="ID_FIT" visibile="false" value="${datiRiga.G1DOCSTIPULA_ID}" edit="true"  campoFittizio="true" 
					definizione="N12" title ="ID_FIT"/>
					<gene:campoLista campo="NUMORD_FIT" visibile="false" value="${datiRiga.G1DOCSTIPULA_NUMORD}" edit="true"  campoFittizio="true" 
					definizione="N3" title ="NUMORD_FIT"/>
					<gene:campoLista campo="FASE_FIT" visibile="false" value="${datiRiga.G1DOCSTIPULA_FASE}" edit="true"  campoFittizio="true" definizione="N2" title ="FASE_FIT"/>
						
					<input type="hidden" name="idStipula" id="idStipula" value="${idStipula}" />				
					
					
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					
							<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
							
					&nbsp;
				</td>
			</tr>
			
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
	
	function chiudi(){
		
		opener.historyReload();
	}
	
	
	
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		
		var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
		document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
	}
	
	
	
	 $("[id^=tabformLista]").rowSorter({
		  	handler: "img.sort-handler",
	        onDragStart: function(tbody, row, old_index) {
	        	$('.sort-handler').css('cursor','pointer');
	        },
	        onDrop: function(tbody, row, new_index, old_index) {	
			
				var busta_fasele_campo = "FASE_FIT_";
				var busta_fasele_msg = "fase";
				var old_index_for_getValue = old_index + 1;
				var busta_fasele_old = getValue(busta_fasele_campo + old_index_for_getValue);
				var new_index_for_getValue = new_index + 1;
				var busta_fasele_new = getValue(busta_fasele_campo + new_index_for_getValue);
				if(busta_fasele_old != busta_fasele_new){
					alert("Impossibile spostare le righe da una tipologia di fase ad un'altra");
					// Undo last drag
					RowSorter.revert("[id^=tabformLista]");
					return;
				}
					
	            var idStipula="${idStipula}";			
	            var indicePartenza=1;
	            var indiceArrivo=1;
	            var direzione="asc";
	            if(new_index > old_index){
	            	indicePartenza += old_index;
	            	indiceArrivo += new_index;
	            }else if(new_index < old_index){
	            	indicePartenza += new_index;
	            	indiceArrivo += old_index;
	            	direzione = "desc"
	            }else{
	            	return;
	            }
	            var listaId="";
	            var listaNumord="";
	            for(i = indicePartenza; i <= indiceArrivo; i++){
	            	if(listaId!="")
	            		listaId += ",";
	            	listaId += $("#ID_FIT_" + i).val();
	            	if(listaNumord!="")
	            		listaNumord += ",";
	            	listaNumord += $("#NUMORD_FIT_" + i).val();
	            }
			   
	           	bloccaRichiesteServer();
				
				var href = "${contextPath }/pg/AggiornaNumOrdineDocStipula.do" ;
				href+= "?"+csrfToken+"&listaId=" + listaId + "&listaNumord=" + listaNumord + "&idStipula=" + idStipula + "&direzione="  + direzione;
				document.location.href = href;
			}
    	  });
	
	
	</gene:javaScript>
	
	
</gene:template>