<%/*
   * Created on 12-01-2012
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

<c:set var="codiceGara" value='${gene:getValCampo(key, "CODGAR")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "CODIMP")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<c:set var="esistonoGareSenzaCat" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoGareConElencoSenzaCategoriaFunction",pageContext,numeroGara)}'/>

<c:set var="colspan" value="8"/>
<c:set var="where" value=" V_ISCRIZCAT_CLASSI.NGARA='${numeroGara}' and V_ISCRIZCAT_CLASSI.CODGAR='${codiceGara}' and V_ISCRIZCAT_CLASSI.CODIMP='${codiceDitta}'"/>
<c:if test="${esistonoGareSenzaCat eq 'FALSE'}">
	<c:set var="where" value="${where} and V_ISCRIZCAT_CLASSI.CAISIM <> '0'"/>
</c:if>

<c:set var="tipoalgo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPOALGOFunction", pageContext, numeroGara)}'/>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, numeroGara, codiceGara, codiceDitta)}' />

<c:set var="indiceRiga" value="-1"/>
<c:set var="numCambi" value="0"/>

<c:choose>
	<c:when test='${not empty param.modifica}'>
		<c:set var="modifica" value="${param.modifica}" />
	</c:when>
	<c:otherwise>
		<c:set var="modifica" value="${modifica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.paginaAttivaWizard}'>
		<c:set var="paginaAttivaWizard" value="${param.paginaAttivaWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="paginaAttivaWizard" value="${paginaAttivaWizard}" />
	</c:otherwise>
</c:choose>

<c:set var="tipoclass" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoClassificaElencoFunction", pageContext, numeroGara)}'/>


<gene:template file="scheda-template.jsp" gestisciProtezioni="false"  >
	<gene:setString name="titoloMaschera" value="Elenco categorie d'iscrizione con dettaglio classifiche per l'operatore economico ${nomimo}"/>
	
			
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
		
	
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="V_ISCRIZCAT_CLASSI" where='${where}' pagesize="200" tableclass="datilista" sortColumn="3;4" gestisciProtezioni="false" >
 					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />
										
					<c:set var="oldTiplavg" value="${newTiplavg}"/>
					<%/* Nel caso di categorie 0, non fa variare il titolo al cambio della Tipologia e non ne riporta il valore nel titolo */%>
					<c:choose>
						<c:when test="${datiRiga.V_ISCRIZCAT_CLASSI_CAISIM eq '0'}">
							<c:set var="newTiplavg" value=""/>
						</c:when>
						<c:otherwise>
							<c:set var="newTiplavg" value="${datiRiga.V_ISCRIZCAT_CLASSI_TIPLAVG }"/>
						</c:otherwise>
					</c:choose>
					
					<c:set var="oldTitolo" value="${newTitolo}"/>
					<c:set var="newTitolo" value="${datiRiga.V_ISCRIZCAT_CLASSI_TITCAT }"/>
					
					<gene:campoLista campoFittizio="true" visibile="false">
						<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
						<c:if test="${oldTitolo != newTitolo || newTiplavg != oldTiplavg}">
							<td colspan="${colspan }">
								<b>${datiRiga.TAB1_TAB1DESC }</b> <c:if test="${not empty datiRiga.TAB1_TAB1DESC and not empty datiRiga.TAB5_TAB5DESC}"> - </c:if> <b>${datiRiga.TAB5_TAB5DESC }</b>
							</td>
						</tr>
											
						<tr class="odd">
						<c:set var="numCambi" value="${numCambi + 1}"/>
						</c:if>
					</gene:campoLista>
														
					<c:set var="codcat" value="${datiRiga.V_ISCRIZCAT_CLASSI_CAISIM}"/>
					<c:set var="oldCodiceCategoria" value="${newCodiceCategoria}"/>
					<c:set var="newCodiceCategoria" value="${datiRiga.V_ISCRIZCAT_CLASSI_CAISIM }"/>				
					<c:if test="${oldCodiceCategoria == newCodiceCategoria && datiRiga.V_ISCRIZCAT_CLASSI_NUMCLASS ne -100}">
						<c:set var="codcat" value=""/>
					</c:if>
					
					<gene:campoLista title="" width="22" >
						<c:choose>
							<c:when test="${datiRiga.V_ISCRIZCAT_CLASSI_NUMLIV > '1' and codcat ne ''}">
								<img width="22" height="16" title="Categoria di livello ${datiRiga.V_ISCRIZCAT_CLASSI_NUMLIV}" alt="Categoria di livello ${datiRiga.V_ISCRIZCAT_CLASSI_NUMLIV}" src="${pageContext.request.contextPath}/img/livelloCategoria${datiRiga.V_ISCRIZCAT_CLASSI_NUMLIV}.gif"/>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
					
					<gene:campoLista campo="NUMORD"  visibile="false"/>
					<gene:campoLista campo="NUMCLASS"  visibile="false" edit="true" />
					<gene:campoLista campo="TIPLAVG" edit="${updateLista eq 1}"  visibile = "false"/>	
					
					<gene:campoLista campo="CAISIM"  value="${codcat}" ordinabile="false"/>
					<gene:campoLista campo="CODGAR" visibile="false" />
					<gene:campoLista campo="NGARA"  visibile="false" />
					<gene:campoLista campo="CODIMP" visibile="false" />
					<gene:campoLista campo="DESCAT" title="Descrizione" ordinabile="false" />
					<gene:campoLista campo="DESCAT_FIT" visibile="false" campoFittizio="true" definizione="T2000;" value="${datiRiga.V_ISCRIZCAT_CLASSI_DESCAT}" edit="true"/>
					
					<gene:campoLista campo="INFNUMCLASS" title="Da classifica" ordinabile="false" value='${gene:if(datiRiga.V_ISCRIZCAT_CLASSI_NUMCLASS eq -100,datiRiga.V_ISCRIZCAT_CLASSI_INFNUMCLASS,"")}' gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoClassificaCategoria" visibile="${ tipoclass eq 1}"/>
					<gene:campoLista campo="SUPNUMCLASS" title='${gene:if(tipoclass eq 1,"A classifica","Classifica")}' ordinabile="false" value='${gene:if(datiRiga.V_ISCRIZCAT_CLASSI_NUMCLASS eq -100,datiRiga.V_ISCRIZCAT_CLASSI_SUPNUMCLASS,"")}' gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoClassificaCategoria"/>					
					
					<gene:campoLista campo="INVREA" width="80" ordinabile="false" visibile="${tipoalgo eq 1 or tipoalgo eq 3 or tipoalgo eq 4 or tipoalgo eq 5 or tipoalgo eq 15}"/>
					<gene:campoLista campo="INVPEN" width="80" ordinabile="false" visibile="${tipoalgo eq 1 or tipoalgo eq 5}" />
					<gene:campoLista campo="AGGREA" width="80" ordinabile="false" visibile="${tipoalgo eq 12 or tipoalgo eq 15}" title="N.aggiudic."/>
					<gene:campoLista campo="ALTPEN"  width="80" ordinabile="false"/>					
					
					<gene:campoLista campo="TITCAT"  visibile = "false"/>
					<gene:campoLista campo="TAB5DESC" entita = "TAB5" where ="TAB5.TAB5COD = 'G_j05' and TAB5.TAB5TIP =V_ISCRIZCAT_CLASSI.TITCAT" visibile="false" />
					<gene:campoLista campo="TAB1DESC" entita = "TAB1" where ="TAB1.TAB1COD = 'G_038' and TAB1.TAB1TIP = V_ISCRIZCAT_CLASSI.TIPLAVG and V_ISCRIZCAT_CLASSI.CAISIM <> '0'" visibile="false" />
					
					<gene:campoLista title="Dett. penalità" width="20" campoFittizio="true" definizione="T20" campo="COLONNA_PEN">
						<c:choose>
							<c:when test="${datiRiga.V_ISCRIZCAT_CLASSI_ISFOGLIA eq '1' and empty codcat}">
								<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriPopup(${currentRow}+1,'${chiaveRigaJava}','1');" title="Dettaglio penalità" >
									<img width="16" height="16" title="Dettaglio penalità" alt="Dettaglio penalità" src="${pageContext.request.contextPath}/img/penalita.png"/>
								</a>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
					<c:if test="${gene:checkProt(pageContext,'MASC.VIS.GARE.ISCRIZCAT-scheda') }">
						<gene:campoLista title="" width="20" >
							<c:choose>
								<c:when test="${datiRiga.V_ISCRIZCAT_CLASSI_ISFOGLIA eq '1' && datiRiga.V_ISCRIZCAT_CLASSI_CAISIM ne '0' && datiRiga.V_ISCRIZCAT_CLASSI_NUMCLASS eq -100 }">
									<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriPopup(${currentRow}+1,'${chiaveRigaJava}','2');" title="Ulteriori informazioni" >
										<img width="16" height="16" title="Ulteriori informazioni" alt="Ulteriori informazioni" src="${pageContext.request.contextPath}/img/opzioniUlteriori.png"/>
									</a>
								</c:when>
								<c:otherwise>
									&nbsp;
								</c:otherwise>
							</c:choose>
						</gene:campoLista>
					</c:if>
					<gene:campoLista campo="ISFOGLIA"  visibile="false"/>
					<gene:campoLista campo="NUMLIV"  visibile="false"/>
								
					<c:set var="indiceRiga" value="${indiceRiga + 1}"/>
			
					<%/* Questa parte di codice setta lo stile della riga in base che sia un titolo oppure una riga di dati */%>
					<gene:campoLista visibile="false" >
						<th style="display:none">
							<c:if test="${oldTitolo != newTitolo || newTiplavg != oldTiplavg}"><script type="text/javascript">
								var nomeForm = document.forms[0].name;
								var indice = ${indiceRiga};
								document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className =document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi }  ) - 1].className;
								document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } ) - 1].className = "white";
							</script></c:if>
						</th>
					</gene:campoLista>
					
					<gene:campoLista visibile="false">
			             <th style="display:none">
					         <c:if test="${datiRiga.V_ISCRIZCAT_CLASSI_ISFOGLIA eq '2'}">
					         <c:set var="numliv" value="${datiRiga.V_ISCRIZCAT_CLASSI_NUMLIV}"/>
				                 <script type="text/javascript">
					                 var nomeForm = document.forms[0].name;
		 							 var indice = ${indiceRiga};
		 							
		 							 document.getElementById("tab" + nomeForm).rows[indice + (${numCambi } ) ].className = "livello"+${numliv};
				                 </script>
				             </c:if>
			             </th>
				     </gene:campoLista>
							
					<input type="hidden" name="numeroCategorie" id="numeroCategorie" value="" />
					<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta }" />
					<input type="hidden" name="modifica" id="modifica" value="${modifica }" />
					<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara }" />
					<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara }" />
                    <input type="hidden" name="paginaAttivaWizard" id="paginaAttivaWizard" value="${paginaAttivaWizard }" />                  															
				</gene:formLista>
				</td>
			</tr>
						
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					
					<INPUT type="button"  class="bottone-azione" value='Torna a elenco categorie' title='Torna a elenco categorie' onclick="javascript:historyVaiIndietroDi(1);">
					&nbsp;
							
						
					&nbsp;
				</td>
			</tr>
			
					
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
				
		function apriPopup( indiceRiga,chiaveRiga,tipo){
			var codiceGara="${codiceGara }";
			var numeroGara="${numeroGara}";
			var codiceDitta="${codiceDitta }";
			var chiavi = chiaveRiga.split(";");
			var codact=chiavi[0].substring(chiavi[0].indexOf(":") + 1, chiavi[0].length);
			var tipcat=chiavi[1].substring(chiavi[1].indexOf(":") + 1, chiavi[1].length);
			var numclass = getValue("V_ISCRIZCAT_CLASSI_NUMCLASS_" + indiceRiga);
			var entita;
			if(numclass!=-100){
				var key="ISCRIZCLASSI.CODGAR=T:" + codiceGara + ";ISCRIZCLASSI.CODIMP=T:" + codiceDitta + ";ISCRIZCLASSI.NGARA=T:" + numeroGara;
				key += ";ISCRIZCLASSI.CODCAT=T:" + codact + ";ISCRIZCLASSI.TIPCAT=N:" + tipcat + ";ISCRIZCLASSI.NUMCLASS=N:" + numclass;
				entita="ISCRIZCLASSI";
			}else{
				var key="ISCRIZCAT.CODGAR=T:" + codiceGara + ";ISCRIZCAT.CODIMP=T:" + codiceDitta + ";ISCRIZCAT.NGARA=T:" + numeroGara;
				key += ";ISCRIZCAT.CODCAT=T:" + codact + ";ISCRIZCAT.TIPCAT=N:" + tipcat;
				entita="ISCRIZCAT";
			}
						
			var href = "href=gare/iscrizcat/iscrizcat-schedaPopup-ulterioriCampi.jsp";
			href += "&key=" + key;
			href += "&entita=" + entita;					
			if(entita=="ISCRIZCLASSI"){
				var descat = getValue("DESCAT_FIT_" + indiceRiga);
				href += "&descat=" + descat;
			}
			
			href += "&modificabile=true";
			href += "&tipo=" + tipo;
			var autorizzatoModifiche = "${autorizzatoModifiche }";
			href += "&autorizzatoModifiche=" + autorizzatoModifiche;
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
			
		}
		
		<c:if test='${updateLista ne 1}'>
			//Si centra l'immagine delle penalità
			var numeroCategorie = ${currentRow}+1;
			for(i=1;i<=numeroCategorie;i++){
	           $("#colCOLONNA_PEN_" + i).parent().css( "text-align", "center" );
	        }
        </c:if>
	</gene:javaScript>
		
</gene:template>