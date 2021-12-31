<%/*
   * Created on 17-ott-2007
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


<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<c:choose>
		<c:when test='${not empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.codiceGara}'>
			<c:set var="codiceGara" value="${param.codiceGara}" />
		</c:when>
		<c:otherwise>
			<c:set var="codiceGara" value="${codiceGara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.codiceDitta}'>
			<c:set var="codiceDitta" value="${param.codiceDitta}" />
		</c:when>
		<c:otherwise>
			<c:set var="codiceDitta" value="${codiceDitta}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.codiceDitta}'>
			<c:set var="codiceDitta" value="${param.codiceDitta}" />
		</c:when>
		<c:otherwise>
			<c:set var="codiceDitta" value="${codiceDitta}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.ngaraaq}'>
			<c:set var="ngaraaq" value="${param.ngaraaq}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngaraaq" value="${ngaraaq}" />
		</c:otherwise>
	</c:choose>
		
	<gene:setString name="titoloMaschera" value='Selezione lavorazioni da accordo quadro'/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<gene:set name="titoloMenu">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
	
	
	
	<c:set var="where" value="V_GCAP_DPRE.NGARA = '${ngara}' AND V_GCAP_DPRE.CODGAR = '${codiceGara}' AND V_GCAP_DPRE.COD_DITTA = '${codiceDitta}' AND V_GCAP_DPRE.CONTAF NOT IN (SELECT CONTAF FROM GCAP WHERE GCAP.NGARA ='${ngaraaq}' AND GCAP.DITTAO is null)" />
	  	
		
  				<gene:formLista entita="V_GCAP_DPRE" where='${where}' pagesize="20" tableclass="datilista" sortColumn="4;3" gestisciProtezioni="true"
  					gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreV_GCAP_DPRE">
  					
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					
  					
  					<gene:redefineInsert name="addToAzioni" >
						<c:when test='${updateLista eq 1 }'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
										${gene:resource("label.tags.template.dettaglio.schedaConferma")}
									</a>
								</td>
							</tr>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
										${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
									</a>
								</td>
							</tr>
						</c:when>
					
  					</gene:redefineInsert>
  					 					
  					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
						<c:if test="${currentRow >= 0 and (updateLista eq 0)}">
							<input type="checkbox" name="keys" value="${chiaveRiga}"  />
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="CODGAR" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="CONTAF" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="NORVOC" title="N." headerClass="sortable" width="30" visibile="false"/>
					<gene:campoLista campo="COD_DITTA" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="NGARA" visibile="false" title='Codice lotto' edit = "false"/>
					<gene:campoLista campo="CODVOC" title="Voce" headerClass="sortable" width="80" edit = "false"/>
					<gene:campoLista campo="VOCE" headerClass="sortable" edit = "false"/>
					<gene:campoLista title="Um" campo="UNIMIS" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" width="55" headerClass="sortable" edit = "false"/>
					<gene:campoLista campo="QUANTIEFF" headerClass="sortable" width="80" edit = "${updateLista eq 1}"/>
					<gene:campoLista campo="PREOFF" headerClass="sortable" edit = "false"/>
					
				</gene:formLista>
				
  </gene:redefineInsert>
  <gene:javaScript>
  	document.getElementById("numeroProdotti").value = ${currentRow}+1;
  
	</gene:javaScript>
</gene:template>