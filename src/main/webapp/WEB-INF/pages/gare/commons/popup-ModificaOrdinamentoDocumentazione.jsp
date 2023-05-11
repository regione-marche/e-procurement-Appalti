<%/*
   * Created on 10-05-2015
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:choose>
	<c:when test='${not empty requestScope.esitoAggiornaNumOrdineDocAction}'>
		<c:set var="esitoAggiornaNumOrdineDocAction" value="${requestScope.esitoAggiornaNumOrdineDocAction}" />
	</c:when>
	<c:otherwise>
		<c:set var="esitoAggiornaNumOrdineDocAction" value="${esitoAggiornaNumOrdineDocAction}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.tipoDoc}'>
		<c:set var="tipoDoc" value="${param.tipoDoc}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoDoc" value="${tipoDoc}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codgar1}'>
		<c:set var="codgar1" value="${param.codgar1}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar1" value="${codgar1}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.genereGara}">
		<c:set var="genereGara" value="${param.genereGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.isarchi}">
		<c:set var="isarchi" value="${param.isarchi}"/>
	</c:when>
	<c:otherwise>
		<c:set var="isarchi" value="${isarchi}"/>
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test="${not empty param.avviso}">
		<c:set var="avviso" value="${param.avviso}"/>
	</c:when>
	<c:otherwise>
		<c:set var="avviso" value="${avviso}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.isFaseInvito}">
		<c:set var="isFaseInvito" value="${param.isFaseInvito}"/>
	</c:when>
	<c:otherwise>
		<c:set var="isFaseInvito" value="${isFaseInvito}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.fasEle}">
		<c:set var="fasEle" value="${param.fasEle}"/>
	</c:when>
	<c:otherwise>
		<c:set var="fasEle" value="${fasEle}"/>
	</c:otherwise>
</c:choose>


<c:set var="ordinamento" value="5"/>


<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>

<c:set var="titoloForm" value="Modifica disposizione documenti"/>

<c:choose>
	<c:when test="${tipoDoc eq 1}">
		<c:set var="descTipoDoc" value="Documenti del bando/avviso"/>
	</c:when>
	<c:when test="${tipoDoc eq 2}">
		<c:choose>
			<c:when test="${genereGara eq 10 or genereGara eq 20}">
				<c:set var="descTipoDoc" value="Requisiti degli operatori"/>
			</c:when>
			<c:otherwise>
				<c:set var="descTipoDoc" value="Requisiti dei concorrenti"/>
			</c:otherwise>
		</c:choose>
		
	</c:when>
	<c:when test="${tipoDoc eq 3}">
		<c:choose>
			<c:when test="${genereGara eq 10 or genereGara eq 20}">
				<c:set var="descTipoDoc" value="Documenti richiesti agli operatori"/>
			</c:when>
			<c:otherwise>
				<c:set var="descTipoDoc" value="Documenti richiesti ai concorrenti"/>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${tipoDoc eq 4}">
		<c:set var="descTipoDoc" value="Documenti dell'esito"/>
	</c:when>
	<c:when test="${tipoDoc eq 5}">
		<c:set var="descTipoDoc" value="Documenti per la trasparenza"/>
	</c:when>
	<c:when test="${tipoDoc eq 6}">
		<c:set var="descTipoDoc" value="Documenti dell'invito"/>
	</c:when>
	<c:when test="${tipoDoc eq 10}">
		<c:set var="descTipoDoc" value="Atti e documenti (art.29 c.1 DLgs 50/2016)"/>
	</c:when>
	<c:when test="${tipoDoc eq 11}">
		<c:set var="descTipoDoc" value="Allegati all'ordine di acquisto"/>
	</c:when>
</c:choose>

< fmt:formatNumber type="number" value="${tipoDoc}" var="tipoDocumento"/>

<c:if test="${genereGara eq '3'}">
	<c:set var="varTemp" value="TORN.CODGAR=T:${codgar1 }"/>
	<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, varTemp)}' />
</c:if>


<c:choose>
	<c:when test="${tipoDoc ne 10}">
		<c:set var="where" value="DOCUMGARA.CODGAR='${codgar1 }' and DOCUMGARA.GRUPPO=${tipoDoc}"/>
	</c:when>
	<c:when test="${!empty ngara }">
		<c:set var="where" value="DOCUMGARA.CODGAR='${codgar1 }' and DOCUMGARA.NGARA='${ngara }' and DOCUMGARA.GRUPPO=${tipoDoc}"/>
	</c:when>
	<c:otherwise>
		<c:set var="where" value="DOCUMGARA.CODGAR='${codgar1 }' and DOCUMGARA.NGARA is null and DOCUMGARA.GRUPPO=${tipoDoc}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${isarchi eq 1 }">
		<c:set var="where" value="${where} and DOCUMGARA.ISARCHI='1'"/>
	</c:when>
	<c:otherwise>
		<c:set var="where" value="${where} and (DOCUMGARA.ISARCHI is null or DOCUMGARA.ISARCHI<>'1')"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${isFaseInvito eq 1 }">
		<c:set var="where" value="${where} and (DOCUMGARA.BUSTA is null or DOCUMGARA.BUSTA<>4)"/>
	</c:when>
</c:choose>

<c:if test="${not empty param.tipologia}">
	<c:set var="tipologia" value="${param.tipologia}"/>
	<c:set var="where" value="${where} and TIPOLOGIA = ${tipologia}"/>
</c:if>

<c:if test="${not empty param.busta}">
	<c:set var="busta" value="${param.busta}"/>
	<c:set var="where" value="${where} and BUSTA = ${busta}"/>
</c:if>

<c:if test="${not empty fasEle}">
	<c:choose>
		<c:when test="${fasEle eq iscrizione}">
			<c:set var="where" value="${where} and (FASELE = 1 or FASELE = 2)"/>
		</c:when>
		<c:otherwise>
			<c:set var="where" value="${where} and (FASELE = 2 or FASELE = 3)"/>
		</c:otherwise>
	</c:choose>
	
</c:if>

<c:if test="${not empty param.titolo}">
	<c:set var="descTipoDoc" value="${param.titolo}"/>
</c:if>

<c:if test='${esitoRicalcNumordDocGara ne "1"}'>
	<c:set var="verificaNumordNullo" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsNumordNulloFunction", pageContext, codgar1, tipoDoc)}' scope="request"/>
</c:if>

<c:choose>
	<c:when test='${verificaNumordNullo eq true and esitoRicalcNumordDocGara ne "1"}' >
		<c:set var="modo" value="MODIFICA" scope="request" />
		<gene:template file="popup-template.jsp" gestisciProtezioni="false" >

		<gene:redefineInsert name="corpo">
			
			<br/>
			E' in corso il riassegnamento del numero d'ordine ...
			<br/><br/>
				<gene:formLista entita="DOCUMGARA" where="${where }" pagesize="20" tableclass="datilista" sortColumn="1" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupModificaOrdinamentoDocumentazione">
				<gene:campoLista campo="CODGAR"  visibile="false" />
				<input type="hidden" name="numeroDocumenti" id="numeroDocumenti" value="" />
				<input type="hidden" name="codgar1" id="codgar1" value="${codgar1}" />
				<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
				<input type="hidden" name="tipoDoc" id="tipoDoc" value="${tipoDoc }" />
				<input type="hidden" name="tipologia" id="tipologia" value="${tipologia}" />
				<input type="hidden" name="busta" id="busta" value="${busta}" />
				<input type="hidden" name="titolo" id="titolo" value="${descTipoDoc}" />
				<input type="hidden" name="genereGara" id="genereGara" value="${genereGara }" />
				<input type="hidden" name="isarchi" id="isarchi" value="${isarchi }" />
				<input type="hidden" name="isFaseInvito" id="isFaseInvito" value="${isFaseInvito }" />
				<input type="hidden" name="fasEle" id="fasEle" value="${fasEle}" />
				<input type="hidden" name="esitoRicalcNumordDocGara" id="esitoRicalcNumordDocGara" value="${esitoRicalcNumordDocGara }" />
			</gene:formLista>
		</gene:redefineInsert>
		<gene:javaScript>
		 $("[id^=tabformLista]").hide();
		document.forms[0].pgSort.value = "";
		document.forms[0].pgLastSort.value = "";
		document.forms[0].pgLastValori.value = "";
		//Si blocca in modifica la pagina
		document.getElementById('bloccaScreen').style.visibility='visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
		listaConferma();		
		</gene:javaScript>
		</gene:template>
	</c:when>
	
	<c:otherwise>


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
	
	<c:choose>
		<c:when test="${ avviso eq 1}">
			<c:set var="tipoPagina" value="dell'avviso"/>
		</c:when>
		<c:otherwise>
			<c:set var="tipoPagina" value="di gara"/>
		</c:otherwise>
	</c:choose>
	Nella lista sotto sono riportati i documenti di gara della sezione '${descTipoDoc}'.<br>
	Per modificare l'ordine con cui questi documenti vengono visualizzati, trascinare le righe premendo con il mouse sulla prima colonna a sinistra.   
	
	<c:set var="numeroDocumentiDaArchiviare" value="0"/>
		
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="DOCUMGARA" where="${where }" pagesize="0" tableclass="datilista" sortColumn="${ordinamento }" gestisciProtezioni="false" >
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
											
					
					<gene:campoLista title="${titoloMenu }" width="${dim }" visibile="true" >
						<c:if test="${currentRow >= 0}">
							
								<img width="16" height="16" class="sort-handler" title="Trascina per cambiare l'ordine" alt="Trascina per cambiare l'ordine" src="${pageContext.request.contextPath}/img/cambia_ordine.png"/>
							
							
						</c:if>
					</gene:campoLista>
						
					
					<gene:campoLista campo="CODGAR"  visibile="false" />
					<gene:campoLista campo="NGARA"  visibile="false" />
					<gene:campoLista campo="NORDDOCG"  visibile="false"/>
					<gene:campoLista campo="NUMORD"  visibile="false"/>
					<c:choose>
						<c:when test="${tipoDoc eq 1 or tipoDoc eq 4 or tipoDoc eq 6 or tipoDoc eq 10 or tipoDoc eq 15 or tipoDoc eq 11}">
							<gene:campoLista campo="DESCRIZIONE"  />
							<gene:campoLista campo="DIGNOMDOC"  entita="W_DOCDIG" where="documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig"  href="javascript:visualizzaFileAllegato('${datiRiga.DOCUMGARA_IDPRG}','${datiRiga.DOCUMGARA_IDDOCDG}',${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)});"/>
							<gene:campoLista campo="URLDOC"  visibile='${gestioneUrl eq "true"}' />
							<gene:campoLista campo="ALLMAIL"  title="Allegare a comunicazione?" visibile='${tipoDoc eq 6}' />
						</c:when>
						<c:when test="${tipoDoc eq 5}">
							<gene:campoLista campo="DESCRIZIONE"  />
							<gene:campoLista campo="DIGNOMDOC"  entita="W_DOCDIG" where="documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig"  href="javascript:visualizzaFileAllegato('${datiRiga.DOCUMGARA_IDPRG}','${datiRiga.DOCUMGARA_IDDOCDG}',${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)});"/>
						</c:when>
						<c:when test="${tipoDoc eq 2 }">
							<gene:campoLista campo="DESCRIZIONE"  />
						</c:when>
						<c:when test="${tipoDoc eq 3 }">
							<gene:campoLista campo="TAB1NORD" entita= "TAB1" where="TAB1COD='A1013' and TAB1TIP=BUSTA" visibile="false"/>
							<gene:campoLista campo="BUSTA" visibile="${genereGara ne 10 and genereGara ne 20}"/>
							<gene:campoLista campo="FASELE"  visibile="${genereGara eq 10 or genereGara eq 20}" title="Fase"/>
							<gene:campoLista campo="DESCRIZIONE"  />
							<gene:campoLista campo="CONTESTOVAL"  />
							<gene:campoLista campo="OBBLIGATORIO" title="Obblig.?" />
							<gene:campoLista campo="MODFIRMA"  title="Formato"/>
							<gene:campoLista campo="DIGNOMDOC" title="Fac-simile" entita="W_DOCDIG" where="documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig" href="javascript:visualizzaFileAllegato('${datiRiga.DOCUMGARA_IDPRG}','${datiRiga.DOCUMGARA_IDDOCDG}',${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)});"/>
							<gene:campoLista campo="BUSTA_FIT" visibile="false" value="${datiRiga.DOCUMGARA_BUSTA}" edit="true"  campoFittizio="true" definizione="N7;0;A1013;;BUSTADG" title ="BUSTA_FIT"/>
							<gene:campoLista campo="FASELE_FIT" visibile="false" value="${datiRiga.DOCUMGARA_FASELE}" edit="true"  campoFittizio="true" definizione="N7;0;A1104;;FASELEDG" title ="FASELE_FIT"/>
						</c:when>
					</c:choose>
					<gene:campoLista campo="IDPRG"  visibile="false"/>
					<gene:campoLista campo="IDDOCDG"  visibile="false"/>
					<gene:campoLista campo="ISARCHI"  visibile="false" />
					<gene:campoLista campo="NORDDOCG_FIT" visibile="false" value="${datiRiga.DOCUMGARA_NORDDOCG}" edit="true"  campoFittizio="true" 
					definizione="N3" title ="NORDDOCG_FIT"/>
					<gene:campoLista campo="NUMORD_FIT" visibile="false" value="${datiRiga.DOCUMGARA_NUMORD}" edit="true"  campoFittizio="true" 
					definizione="N3" title ="NUMORD_FIT"/>
					
										
					<input type="hidden" name="numeroDocumenti" id="numeroDocumenti" value="" />
					<input type="hidden" name="codgar1" id="codgar1" value="${codgar1}" />
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
					<input type="hidden" name="tipoDoc" id="tipoDoc" value="${tipoDoc }" />
					<input type="hidden" name="genereGara" id="genereGara" value="${genereGara }" />
					<input type="hidden" name="tipologia" id="tipologia" value="${tipologia}" />
					<input type="hidden" name="busta" id="busta" value="${busta}" />
					<input type="hidden" name="titolo" id="titolo" value="${descTipoDoc}" />
					<input type="hidden" name="isarchi" id="isarchi" value="${isarchi }" />
					<input type="hidden" name="isFaseInvito" id="isFaseInvito" value="${isFaseInvito }" />
					
					<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti }" />
					<input type="hidden" name="esitoAggiornaNumOrdineDocAction" id="esitoAggiornaNumOrdineDocAction" value="${esitoAggiornaNumOrdineDocAction}" />
					
					<input type="hidden" name="esitoRicalcNumordDocGara" id="esitoRicalcNumordDocGara" value="${esitoRicalcNumordDocGara }" />
					<input type="hidden" name="fasEle" id="fasEle" value="${fasEle}" />
					
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
		
		<c:if test="${not empty esitoAggiornaNumOrdineDocAction and esitoAggiornaNumOrdineDocAction eq '1'}">
			opener.historyReload();
		</c:if>
		
		window.close();
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
				if(${tipoDoc eq 3 }){
					var busta_fasele_campo = "BUSTA_FIT_";
					var busta_fasele_msg = "busta";
					if(${genereGara eq 10 or genereGara eq 20}){
						busta_fasele_campo = "FASELE_FIT_";
						var busta_fasele_msg = "fase";
					}
					var old_index_for_getValue = old_index + 1;
					var busta_fasele_old = getValue(busta_fasele_campo + old_index_for_getValue);
					var new_index_for_getValue = new_index + 1;
					var busta_fasele_new = getValue(busta_fasele_campo + new_index_for_getValue);
					if(busta_fasele_old != busta_fasele_new){
						alert("Impossibile spostare le righe da una tipologia di "+ busta_fasele_msg + " ad un'altra");
						// Undo last drag
						RowSorter.revert("[id^=tabformLista]");
						return;
					}
				}
	            var ngara="${ngara }";
				var codgar1="${codgar1}";
				var tipoDoc="${tipoDoc}";
				var genereGara="${genereGara}";
				var isFaseInvito="${isFaseInvito}";
				var tipologia="${tipologia}";
				var busta="${busta}";
				var titoloTipologia="${descTipoDoc}";
				var esitoRicalcNumordDocGara="${esitoRicalcNumordDocGara}";
				var fasEle="${fasEle}";
				
	            var bloccoModifica ="${bloccoModifica }";
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
	            var listaNorddocg="";
	            var listaNumord="";
	            for(i = indicePartenza; i <= indiceArrivo; i++){
	            	if(listaNorddocg!="")
	            		listaNorddocg += ",";
	            	listaNorddocg += $("#NORDDOCG_FIT_" + i).val();
	            	if(listaNumord!="")
	            		listaNumord += ",";
	            	listaNumord += $("#NUMORD_FIT_" + i).val();
	            }
			   
	           	bloccaRichiesteServer();
				
				var href = "${contextPath }/pg/AggiornaNumOrdineDocumentazione.do" ;
				href+= "?"+csrfToken+"&listaNorddocg=" + listaNorddocg + "&listaNumord=" + listaNumord + "&ngara=" + ngara +  "&codgar1=" + codgar1 + "&direzione="  + direzione + "&tipoDoc=" + tipoDoc ;
				href+=  "&genereGara=" + genereGara + "&isFaseInvito=" + isFaseInvito + "&esitoRicalcNumordDocGara=" + esitoRicalcNumordDocGara + "&tipologia=" + tipologia + "&busta=" + busta + "&titolo=" + titoloTipologia;
				href+= "&fasEle=" + fasEle;
				//alert(href);
				document.location.href = href;
			}
    	  });
	
	
	</gene:javaScript>
	
	
</gene:template>

</c:otherwise>
</c:choose>