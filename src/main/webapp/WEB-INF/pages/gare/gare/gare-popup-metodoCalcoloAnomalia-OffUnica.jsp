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


<c:set var="contextPath" value="${pageContext.request.contextPath}" />

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

<c:choose>
	<c:when test='${not empty param.paginazione}'>
		<c:set var="paginazione" value="${param.paginazione}" />
	</c:when>
	<c:otherwise>
		<c:set var="paginazione" value="${paginazione}" />
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${ngara }" />

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetMetodoCalcoloSogliaFunction", pageContext, ngara, "true")}

<gene:template file="popup-template.jsp">
<gene:setString name="titoloMaschera" value='Metodo di calcolo soglia anomalia della gara ${ngara }' />
	
	<gene:redefineInsert name="gestioneHistory" />	
	<gene:redefineInsert name="addHistory" />	
	<gene:redefineInsert name="corpo">
		<br>
		Ai lotti della gara corrente sono stati assegnati i seguenti metodi di calcolo della soglia di anomalia. 
		<c:if test="${blocco ne 'true' and updateLista ne 1}">
		<br>
		E' possibile modificare i valori assegnati premendo il tasto modifica.
		</c:if>
	
		<br>
		<c:set var="where" value="GARE.CODGAR1 ='${ngara}' and GARE.NGARA!=GARE.CODGAR1 and (GARE.MODLICG=13 OR GARE.MODLICG=14)" />
		
		
		<c:choose>
			<c:when test="${isGaraDLGS2017 }">
				<c:set var="tipoLegge" value="DLGS2017"/>
			</c:when>
			<c:otherwise>
				<c:set var="tipoLegge" value="DLGS2016"/>
			</c:otherwise>
		</c:choose>
		${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriCampoMETCOEFFFunction", pageContext, tipoLegge)}
		
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
		
					<gene:formLista pagesize="${paginazione }" tableclass="datilista" entita="GARE" sortColumn="1" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreImpostaMetodoCalcoloSoglia">
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
						
						<gene:campoLista campo="NGARA" />
						<gene:campoLista campo="CODCIG" />
						<gene:campoLista campo="METSOGLIA" title="Metodo di calcolo" entita="GARE1" where="GARE.NGARA=GARE1.NGARA" edit="${updateLista eq 1 }"/>
						<gene:campoLista campo="METCOEFF"  entita="GARE1" where="GARE.NGARA=GARE1.NGARA" edit="${updateLista eq 1 }" visibile="false"/>
											
						
						<gene:campoLista title="Coeff. metodo E">
							<c:choose>
								<c:when test="${updateLista eq 1 }">
									<select id="METCOEFF_${currentRow +1 }" name="METCOEFF_${currentRow +1}" title="Coeffic.per calcolo soglia anomalia metodo E"  >
										<option value="" title="&nbsp;" >&nbsp;</option>
										<c:if test='${not empty listaValoriTabellatoDLGS}'>
											<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
												<option value="${valoreTabellato[0]}" title="${valoreTabellato[1]}" <c:if test="${valoreTabellato[0] eq  datiRiga.GARE1_METCOEFF}">selected="selected"</c:if>>${valoreTabellato[1]}</option>
											</c:forEach>
										</c:if>
									</select>
								</c:when>
								<c:otherwise>
									<c:if test='${datiRiga.GARE1_METSOGLIA eq 5 and not empty listaValoriTabellatoDLGS}'>
										<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
												<c:if test="${valoreTabellato[0] eq  datiRiga.GARE1_METCOEFF}">${valoreTabellato[1]}</c:if>
										</c:forEach>
									</c:if>
								</c:otherwise>
							</c:choose>
							
						</gene:campoLista>
						<gene:campoLista campo="NGARA"  entita="GARE1" where="GARE.NGARA=GARE1.NGARA" edit="${updateLista eq 1 }" visibile="false"/>
							
						<input type="hidden" name="ngara" id="ngara" value="${ngara }" />
						<input type="hidden" name="isGaraDLGS2017" id="isGaraDLGS2017" value="${isGaraDLGS2017 }" />
						<input type="hidden" name="blocco" id="blocco" value="${blocco }" />
						<input type="hidden" name="paginazione" id="paginazione" value="${paginazione }" />
						<input type="hidden" name="numeroLottiPagina" id="numeroLottiPagina" value="" />
					</gene:formLista>
	  			</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test="${updateLista eq 0 }">
							<c:if test="${blocco ne true }">
								<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:listaApriInModifica();">
							</c:if>
							<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
						</c:when>
						<c:otherwise>
							<c:if test="${requestScope.RISULTATO ne 'ERRORI'}">
								<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:salvaLista();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
			
		</table>
		
		
		
		<br>
		<br>
					
  </gene:redefineInsert>
	 
	
	<gene:javaScript>
		function chiudi(){
			window.close();
		}
			
		
		function annulla(){
			//document.getElementById("modalita").value = 'vis';
			document.forms[0].pgLastSort.value="GARE.NGARA";
			listaAnnullaModifica();
		}
		
		<c:if test="${updateLista eq 1 }">
			//In modifica si deve nascondere la select relativa al campo METOCOEFF quando METSOGLIA <> 5
			document.getElementById("numeroLottiPagina").value = ${currentRow}+1;
			var numeroLottiPagina = ${currentRow}+1;
			for(var z=1; z <= numeroLottiPagina; z++){
				document.getElementById("GARE1_METSOGLIA_" + z).onchange = changeSoglia;
				var metsoglia=getValue("GARE1_METSOGLIA_" + z);
				if(metsoglia!=5){
					$("#METCOEFF_"+z).hide();
				}
			
			}
				
			function changeSoglia(){
				var objId = this.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
				
				if(this.value == "5"){
					$("#METCOEFF_"+numeroRiga).show();
				}else{
					$("#METCOEFF_"+numeroRiga).hide();
					$("#METCOEFF_"+numeroRiga).val('');
				}
			}
			
			function salvaLista(){
				var contatore = ${currentRow}+1;
				var msg="Si deve valorizzare il coefficente per tutti i lotti per cui si è impostato il metodo di calcolo 'Metodo E (art.97 c.2/e DLgs 50/2016)'";
				for(var t=1; t <= contatore; t++){
					var metsoglia=getValue("GARE1_METSOGLIA_" + t);
					if(metsoglia==null || metsoglia ==""){
						clearMsg();
						outMsg("Il campo \"Metodo calcolo soglia anomalia \" è obbligatorio","ERR");
						onOffMsgFlag(true);
						return;
					}
					if(metsoglia==5){
						var metcoeff=getValue("METCOEFF_" + t);
						if(metcoeff==null || metcoeff == ""){
							clearMsg();
							outMsg("Si deve valorizzare il coefficente per tutti i lotti per cui si è impostato il metodo di calcolo \"Metodo E (art.97 c.2/e DLgs 50/2016)\"","ERR");
							onOffMsgFlag(true);
							return;
						}
					}
				}
				for(var t=1; t <= contatore; t++){
					//var metsoglia=getValue("METCOEFF_" + t);
					var metcoeff=$("#METCOEFF_"+ t).val();
					setValue("GARE1_METCOEFF_" + t,metcoeff);
				}
				
				listaConferma();
			}
			
		</c:if>
	</gene:javaScript>
</gene:template>