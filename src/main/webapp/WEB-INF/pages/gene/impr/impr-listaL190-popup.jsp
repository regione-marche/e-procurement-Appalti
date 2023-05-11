<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione del tecnico */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereIMPRFunction" />

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 
<c:set var="nomeContainerFiltri" value="deftrovaIMPR-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
<c:if test="${!fn:contains(sessionScope[nomeContainerFiltri].trovaAddWhere, 'CGENIMP') && ! empty sessionScope.uffint && fn:contains(archiviFiltrati,'IMPR')}">
	<c:set var="filtroUffint" value="CGENIMP = '${sessionScope.uffint}'"/>
</c:if>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="lista-imprese-popup">
	<gene:setString name="titoloMaschera" value="Selezione dell'impresa"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="IMPR" sortColumn="3" gestisciProtezioni="true" inserisciDaArchivio='${gene:checkProtFunz(pageContext,"INS","nuovo")}' where="${filtroUffint}">
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:seleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>

<c:set var="hrefDettaglio" value=""/>
<c:if test='${!gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CODIMP")}'>
	<c:set var="hrefDettaglio" value="javascript:seleziona(${datiArchivioArrayJs});"/> 
</c:if>
			
			<gene:campoLista campo="CODIMP" headerClass="sortable" width="90" href="javascript:seleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="NOMEST" headerClass="sortable" href="${hrefDettaglio}"/>
			<gene:campoLista campo="CFIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="PIVIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="NAZIMP" headerClass="sortable" width="120" visibile="false"/>
			
		</gene:formLista>
  </gene:redefineInsert>
  
  <gene:javaScript>
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// IMPORTANTE: I nomi delle form dell'archivio nel caso di sezioni dinamiche devono avere una struttura del tipo nome + "_" +indice 
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//
	//ArrayValori[0]:nomest
	//ArrayValori[1]:cfimp
	//ArrayValori[2]:pvimp
	//ArrayValori[3]:nazimp
	
	function seleziona(arrayValori){
		var element;
		var close=true;
		try{
			parentForm=eval('window.opener.activeForm');
		}catch(e){
			outMsg(e.message);
			close=false;
		}
		var parentFormName = eval('window.opener.activeArchivioForm');
		
		var campoCodFisc = "ANTICORDITTE_CODFISC";
		var campoCodFiscEst = "ANTICORDITTE_IDFISCEST";
		var camporagSoc= "ANTICORDITTE_RAGSOC"; 
		
		var pos = parentFormName.indexOf("_");
		if(pos > 0){
			//L'archivio è stato richiamato da una scheda multipla, quindi i campi hanno una numerazione
			var indice = parentFormName.substring(pos + 1);
			campoCodFisc += "_" + indice ;
			campoCodFiscEst += "_" + indice ;
			camporagSoc += "_" + indice ;
		}
		//Si deve valorrizare il campo CODFISC della finestra chiamente se il valore di NAZIMP è 1 oppure nullo, altrimenti o IDFISCEST
		var piva=arrayValori[2];
		if(piva==null || piva =="")
			piva= arrayValori[1];
		if(piva !=null && piva != ""){	
			var nazimp = arrayValori[3];
			if(nazimp==null || nazimp=="")
		 		nazimp=1;
			if(nazimp==1){
				parentForm.setValue(campoCodFisc,piva);
				parentForm.setValue(campoCodFiscEst,"");
			}else{
				parentForm.setValue(campoCodFiscEst,piva);
				parentForm.setValue(campoCodFisc,"");
			} 
		}
		parentForm.setValue(camporagSoc,arrayValori[0]); 
		if(close)
			window.close();
		
	}
	</gene:javaScript>
  
</gene:template>


