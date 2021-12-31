<%/*
   * Created on 04-09-2013
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

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="id" value='${gene:getValCampo(key, "COMMALBO.ID")}'/>

<c:set var="whereAlbo" value="COMMNOMIN.IDALBO = ${id}"/>

<table class="dettaglio-tab-lista">
	<c:if test="${!empty filtroNominativi}">
	<tr>
		<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
		 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span>
			 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro();" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
			 <a class="link-generico" href="javascript:AnnullaFiltro();">Cancella filtro</a> ]
		</td>
	</tr>
	</c:if>

	<tr>
		<td>
			<gene:formLista entita="COMMNOMIN" where="${whereAlbo} ${filtroNominativi}" tableclass="datilista" sortColumn="2"
					gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCOMMNOMIN" pagesize="25" >
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />					
				
				<gene:redefineInsert name="addToAzioni">
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1503">
									Imposta filtro
								</a>
							</td>
						</tr>
				</gene:redefineInsert>
				
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0}">
					<c:set var="idNominativo" value='${gene:getValCampo(chiaveRigaJava, "COMMNOMIN.ID")}' />
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza nominativo"/>
							<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica nominativo" />
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina nominativo" />
							</c:if>
					</gene:PopUp>
				</c:if>
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
				</c:if>
								
			</gene:campoLista>				
				<gene:campoLista campo="ID" visibile="false" />
				<gene:campoLista campo="CODTEC" visibile="false" />
				<gene:campoLista campo="NUMORD" href="${link}" visibile="false"/>
				<gene:campoLista entita="TECNI" campo="NOMTEC" where="TECNI.CODTEC=COMMNOMIN.CODTEC" href="${link}" title="Nome" />
				<gene:campoLista entita="UFFINT" campo="NOMEIN" where="UFFINT.CODEIN=COMMNOMIN.CODEIN" title="Ufficio intestatario" />
				<gene:campoLista campo="LIVELLO"/>
				<gene:campoLista campo="DATAAB"/>
			</gene:formLista>
		</td>
	</tr>
		<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" /></tr>
</table>
<gene:javaScript>
		function impostaFiltro(){
			var comando = "href=gare/commons/popup-trova-filtroNominativi.jsp";
			var chiave="${key }";
			chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
			comando += "&idalbo=" + chiave;
			var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
			comando+="&nominativiPerPagina=" + risultatiPerPagina;
			openPopUpCustom(comando, "impostaFiltro", 850, 500, "yes", "yes");
		}

		function AnnullaFiltro(){
		 var comando = "href=gare/commons/popup-filtro.jsp&annulla=4";
		 openPopUpCustom(comando, "impostaFiltro", 10, 10, "no", "no");
		}	
		

</gene:javaScript>
