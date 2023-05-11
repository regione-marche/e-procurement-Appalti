
<%
  /*
			 * Created on: 03-04-2014
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
			 Finestra per l'attivazione della funzione 'Pubblica su Amministrazione trasparente'
			 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when
		test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}'>
		<script type="text/javascript">
			opener.historyReload();
			window.close();
		</script>
	</c:when>
	<c:otherwise>

		<div style="width: 97%;">
			<gene:template file="popup-message-template.jsp">
				<gene:setString name="titoloMaschera"
					value='Pubblica su portale Appalti' />

				<c:set var="modo" value="NUOVO" scope="request" />

				<gene:redefineInsert name="corpo">
					<gene:formScheda entita="TORN" gestisciProtezioni="false"
						plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePubblicaSuAmministrazioneTrasparente"
						gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaSuAmministrazioneTrasparente">

						<gene:campoScheda>
							<td colSpan="2"><br>
							<c:choose>
							<c:when	test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'>
								${requestScope.msg }
							</c:when>
							<c:otherwise>
								${requestScope.MsgConferma}
							<c:if	test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "WARNING"}'>
								${requestScope.msg }
							</c:if>
							</c:otherwise>
							</c:choose> <br>&nbsp; <br>&nbsp;</td>
						</gene:campoScheda>
						<gene:campoScheda campo="CODGAR" campoFittizio="true"	defaultValue="${param.codgar}" visibile="false"	definizione="T21;0" />
						<gene:campoScheda campo="NGARA" campoFittizio="true"	defaultValue="${param.ngara}" visibile="false"	definizione="T20;0" />
						<gene:campoScheda campo="GENERE" campoFittizio="true"	defaultValue="${param.genere}" visibile="false"	definizione="T20;0" />
						<c:if test='${requestScope.controlloSuperato ne "NO"}'>
							<gene:campoScheda campo="DATPUB" campoFittizio="true"	definizione="D;0;;;DATPUB" obbligatorio="true" />
						</c:if>
					</gene:formScheda>
				</gene:redefineInsert>

				<c:if	test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'>
					<gene:redefineInsert name="buttons">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla()">&nbsp;
					</gene:redefineInsert>
				</c:if>


				<gene:javaScript>
				
		var $datepicker = $('#DATPUB');
		$datepicker.datepicker();
		$datepicker.datepicker( "option", "dateFormat", "dd/mm/yy" );
		$datepicker.datepicker('setDate', new Date());
		

				
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupPubblicaSuPortale.jsp";
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