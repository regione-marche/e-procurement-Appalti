<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript" src="${contextPath}/js/general.js"></script>
</HEAD>

<BODY>

	<table style="border: thin; position: absolute; top: 25%; left: 5%;"
		align="left" width="90%">
		<tr>
			<td align="center">
				<div class="titolomaschera">Componi verbale seduta di gara</div>
				<div class="contenitore-dettaglio">
					<table class="lista">

						<tr>
							<td><br> <span>Errore nella composizione del
									verbale seduta di gara</span> <br> <br></td>
						</tr>
						<br>
						<tr>
							<td><jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
								<br>
							<br></td>
						</tr>

						<tr>
							<td class="comandi-dettaglio"><INPUT type="button"
								class="bottone-azione" value="Esci" title="Esci"
								onclick="javascript:window.close();">&nbsp;
							<td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>

</BODY>
</HTML>