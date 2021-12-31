
<%
  /*
			 * Created on: 15-07-2019
			 *
			 * Copyright (c) EldaSoft S.p.A.
			 * Tutti i diritti sono riservati.
			 *
			 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
			 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
			 * aver prima formalizzato un accordo specifico con EldaSoft.
			 */

			/*
			 Descrizione:
			 Finestra per l'importazione dei dati della L.190
			 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:choose>
	<c:when
		test='${"1" eq "2"}'>
		<script type="text/javascript">
			opener.historyReload();
			window.close();
		</script>
	</c:when>
	<c:otherwise>

		<div style="width: 97%;">
			<gene:template file="popup-message-template.jsp">
				<gene:setString name="titoloMaschera" value='Importa Dati per adempimenti L190' />

				<c:set var="modo" value="NUOVO" scope="request" />

				<gene:redefineInsert name="corpo">
					<gene:formScheda entita="TORN" gestisciProtezioni="false"
						gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupImportaDatiL190">
						
						<gene:campoScheda>
							<td colSpan="2"><br>
							<c:choose>
							<c:when	test='${!empty requestScope.esito}'>
								<td colSpan="2">
									<textarea cols="70" rows="12" readonly="readonly">
										${requestScope.msg}
									<c:if test='${!empty requestScope.numeroCigImportati}'>
										Numero CIG inseriti: ${requestScope.numeroCigImportati}
									</c:if>
									<c:if test='${!empty requestScope.numeroCigAggiornati}'>
										Numero CIG aggiornati: ${requestScope.numeroCigAggiornati}
									</c:if>	
									</textarea>
								</td>
							</c:when>
							<c:otherwise>
							</c:otherwise>
							</c:choose>
						</gene:campoScheda>

						<gene:campoScheda campo="CODGAR" campoFittizio="true"	defaultValue="${param.codgar}" visibile="false"	definizione="T21;0" />
						<gene:campoScheda campo="NGARA" campoFittizio="true"	defaultValue="${param.ngara}" visibile="false"	definizione="T20;0" />
						<gene:campoScheda campo="DATPUB" campoFittizio="true" visibile="${empty requestScope.esito}" title="Data di riferimento" definizione="D;0;;;DATPUB" obbligatorio="true" />
						
					</gene:formScheda>
				</gene:redefineInsert>

				<c:if	test='${requestScope.esito eq "ERRORE"}'>
					<gene:redefineInsert name="buttons">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla()">&nbsp;
					</gene:redefineInsert>
				</c:if>
				<c:if	test='${requestScope.esito eq "OK"}'>
					<gene:redefineInsert name="buttons">
						<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla()">&nbsp;
					</gene:redefineInsert>
				</c:if>


				<gene:javaScript>
				
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popup-importaDatiL190.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
	

	</gene:javaScript>
			</gene:template>
		</div>

	</c:otherwise>
</c:choose>