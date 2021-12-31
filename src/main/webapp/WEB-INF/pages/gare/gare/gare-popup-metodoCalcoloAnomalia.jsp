<%
/*
 * Created on: 06-02-2019
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


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.isGaraDLGS2017}'>
		<c:set var="isGaraDLGS2017" value="${param.isGaraDLGS2017}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraDLGS2017" value="${isGaraDLGS2017}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.blocco}'>
		<c:set var="blocco" value="${param.blocco}" />
	</c:when>
	<c:otherwise>
		<c:set var="blocco" value="${blocco}" />
	</c:otherwise>
</c:choose>

${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetMetodoCalcoloSogliaFunction", pageContext, ngara, "false")}

<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Metodo di calcolo soglia anomalia della gara ${ngara }' />

	<gene:redefineInsert name="corpo">
		<br>
		Alla gara corrente è stato assegnato il seguente metodo di calcolo della soglia di anomalia. 
		<br><br>
		<table class="dettaglio-notab">
		<form action="" name="soglia">
		<tr id="rowMETSOGLIA" >
		${gene:callFunction3("it.eldasoft.gene.tags.functions.GetListaValoriTabellatoFunction", pageContext, "A1126", "tipiMetodi")}
		<c:choose>
			<c:when test="${blocco ne 'true'}">
				<td class="etichetta-dato" width="30%">Metodo calcolo soglia anomalia (*)</td>
				<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid; " width="40%">
				<select id="METSOGLIA" name="METSOGLIA" title="Metodo calcolo soglia anomalia" >
				<option value="" title="&nbsp;" selected="selected" >&nbsp;</option>
				<c:forEach var="metodo" items="${requestScope.tipiMetodi}">
					<option value="${metodo.tipoTabellato }" title="${metodo.descTabellato }" <c:if test="${metodo.tipoTabellato eq  metsoglia}">selected="selected"</c:if>>${metodo.descTabellato }</option>
				</c:forEach>
				</select>
				</td>
				<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid; " width="30%">
				
				<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/DLgs50-2016_calcoloSogliaAnomalia.pdf');" title="Consulta manuale" style="color:#002E82;">
					<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
				</a>
				
				</td>
			</c:when>
			<c:otherwise>
				<td class="etichetta-dato">Metodo calcolo soglia anomalia</td>
				<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid; ">
				<c:forEach var="metodo" items="${requestScope.tipiMetodi}">
					<c:if test="${metodo.tipoTabellato eq  metsoglia}">${metodo.descTabellato }&nbsp;
					<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/DLgs50-2016_calcoloSogliaAnomalia.pdf');" title="Consulta manuale" style="color:#002E82;">
						<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
					</a>
					</c:if>
				</c:forEach>
				
				</td>
			</c:otherwise>
		</c:choose>
		
		</tr>
		<c:choose>
			<c:when test="${isGaraDLGS2017 }">
				<c:set var="tipoLegge" value="DLGS2017"/>
			</c:when>
			<c:otherwise>
				<c:set var="tipoLegge" value="DLGS2016"/>
			</c:otherwise>
		</c:choose>
		${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriCampoMETCOEFFFunction", pageContext, tipoLegge)}
		<tr id="rowMETCOEFF" <c:if test="${blocco ne 'true'}">style="display:none;"</c:if>>
		<c:choose>
			<c:when test="${blocco ne 'true'}">
				<td class="etichetta-dato">Coefficiente (*)</td>
				<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid;" colspan="2">
				<select id="METCOEFF" name="METCOEFF" title="Coefficiente"  >
				<c:if test='${not empty listaValoriTabellatoDLGS}'>
					<option value="" title="&nbsp;" selected="selected" >&nbsp;</option>
					<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
						<option value="${valoreTabellato[0]}" title="${valoreTabellato[1]}" <c:if test="${valoreTabellato[0] eq  metcoeff}">selected="selected"</c:if>>${valoreTabellato[1]}</option>
					</c:forEach>
				</c:if>
				</select>
				</td>
			</c:when>
			<c:when test="${blocco eq 'true' and metsoglia eq 5}">
				<td class="etichetta-dato">Coefficiente</td>
				<td style="PADDING-LEFT: 10px; TEXT-ALIGN: left; BORDER-TOP: #A1BAA9 1px solid;">
				<c:if test='${not empty listaValoriTabellatoDLGS}'>
					<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
						<c:if test="${valoreTabellato[0] eq  metcoeff}">${valoreTabellato[1]}</c:if>
					</c:forEach>
				</c:if>
				</td>
			</c:when>
		</c:choose>
		</tr>
		<input type="hidden" name="ngara" id="ngara" value="${ngara }">
		<input type="hidden" name="isGaraDLGS2017" id="isGaraDLGS2017" value="${isGaraDLGS2017 }">
		<input type="hidden" name="blocco"  id="blocco" value="${blocco }">
		</form>
		</table>
		<br>
		<br>
					
  </gene:redefineInsert>
	 <c:if test='${ blocco eq "true"}' >
	  	<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
		</gene:redefineInsert>
	  </c:if>
	
	<gene:javaScript>
		<c:if test="${blocco ne 'true'}">
			$('#METSOGLIA').change(function() {
					 metosoglia = $( this ).val();
					 if(metosoglia==5)
					 	$("#rowMETCOEFF").show();
					 else{
					 	$("#rowMETCOEFF").hide();
					 	$("#METCOEFF").val('');
					 }
			    });
			    
			metsogliaIni = $("#METSOGLIA").val();
			metcoeffIni = $("#METCOEFF").val();
			if(metsogliaIni==5)
				$("#rowMETCOEFF").show();
		</c:if>
		
		
	
		function conferma(){
			<c:if test="${blocco ne 'true'}">
				var metsoglia = $("#METSOGLIA").val();
				if(metsoglia==null || metsoglia ==""){
					clearMsg();
					outMsg("Il campo \"Metodo calcolo soglia anomalia \" è obbligatorio","ERR");
					onOffMsgFlag(true);
					return;
				}
				var metcoff = $("#METCOEFF").val();
				if(metsoglia==5 && (metcoff==null || metcoff =="")){
					clearMsg();
					outMsg("Il campo \"Coefficiente per calcolo metodo E\" è obbligatorio","ERR");
					onOffMsgFlag(true);
					return;
				}
				if(metsogliaIni==metsoglia && metcoff==metcoeffIni){
					window.close();
				}
				
			</c:if>
			_wait();
			var ngara="${ngara}";
			var isGaraDLGS2017 = "${isGaraDLGS2017}";
			$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/ModificaMetodoCalcoloSoglia.do",   
					data:{
						ngara: ngara,
						isGaraDLGS2017: isGaraDLGS2017,
						metsoglia : $("#METSOGLIA").val(),
						metcoeff : $("#METCOEFF").val()
					},
					success: function(res){
						esito= res.esito;
						
						
					},
					error: function(e){
						//alert("Errore generico durante l'operazione");
					},
					complete: function(jqXHR, textStatus) {
						_nowait();
						if(textStatus=="error" || textStatus=="parsererror" ||esito=="nok" ){
							outMsg("Si è verificato un errore durante l'aggiornamento","ERR");
							onOffMsgFlag(true);
						}else{
							window.close();
						}
					}
				});
			
		}

		function annulla(){
			window.close();
		}
		
		/*
		 * Funzione di attesa
		 */
		function _wait() {
			document.getElementById('bloccaScreen').style.visibility='visible';
			$('#bloccaScreen').css("width",$(document).width());
			$('#bloccaScreen').css("height",$(document).height());
			document.getElementById('wait').style.visibility='visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
		}
		
		
		/*
		 * Nasconde l'immagine di attesa
		 */
		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility='hidden';
			document.getElementById('wait').style.visibility='hidden';
		}

	</gene:javaScript>
</gene:template>