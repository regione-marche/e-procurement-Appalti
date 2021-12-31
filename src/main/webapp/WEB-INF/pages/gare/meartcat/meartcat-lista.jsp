
<%
	/*
	 * Created on 02-Dec-2013
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

<c:set var="codiceGara" value="$ ${param.opes_ngara}" />
<c:set var="codiceGara" value='${fn:replace(codiceGara," ", "")}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="MEARTCAT-lista" schema="GARE">
	<gene:setString name="titoloMaschera" value="${param.cais_caisim} - ${param.cais_descat} - Lista articoli" />
	<gene:setString name="entita" value="MEARTCAT" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="MEARTCAT" pagesize="20" sortColumn="6" tableclass="datilista"
						gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMEARTCAT">
						
						
						<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
							<jsp:param name="entita" value="V_GARE_TORN"/>
							<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
							<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
						</jsp:include>
						
						<input type="hidden" name="opes_ngara" value="${param.opes_ngara}" />
						<input type="hidden" name="opes_nopega" value="${param.opes_nopega}" />
						<input type="hidden" name="cais_caisim" value="${param.cais_caisim}" />
						<input type="hidden" name="cais_descat" value="${param.cais_descat}" />						
						<input type="hidden" name="listachiamante" value="meartcat-lista"/>
						
						<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
							<c:set var="resultdiritti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDirittiMEARTCATFunction",pageContext,datiRiga.MEARTCAT_ID)}'/>
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItemResource	resource="popupmenu.tags.lista.visualizza" title="Visualizza" />
									<c:if test="${isMEARTCATCancellabile eq 'true'}">
										<c:if test='${autorizzatoModifiche ne "2" and gene:checkProtFunz(pageContext, "MOD","MOD")}'>
											<gene:PopUpItemResource	resource="popupmenu.tags.lista.modifica" title="Modifica" />
										</c:if>
										<c:if test='${autorizzatoModifiche ne "2" and gene:checkProtFunz(pageContext, "DEL","DEL")}'>
											<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"	title="Elimina" />
										</c:if>
									</c:if>
								</gene:PopUp>
								<c:if test='${ autorizzatoModifiche ne "2" and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
									<c:if test="${isMEARTCATCancellabile eq 'true'}">
										<input type="checkbox" name="keys" value="${chiaveRiga}" />
									</c:if>
								</c:if>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="ID" visibile="false"/>
						<gene:campoLista campo="NGARA" visibile="false" />
						<gene:campoLista campo="NOPEGA" visibile="false" />
						<gene:campoLista campo="TIPO" />
						<gene:campoLista campo="DESCR" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
						<gene:campoLista campo="COD" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
						<gene:campoLista campo="STATO"/>
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
	</gene:redefineInsert>
</gene:template>