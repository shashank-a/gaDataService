<c:if test="${empty agentData}"> 
	<div>NO Data Found</div>
</c:if>

<c:if test="${!empty agentData}">
<div id="${accNo}"></div>
<div id="rowData">
		<c:forEach items="${agentData}" var="agentRow">
			<div><c:out value="${agentRow.eventLabel}"></c:out><div>
			<div><c:out value="${agentRow.sendCount}"></c:out><div>
			<div><c:out value="${agentRow.callConclusion}"></c:out><div>
		</c:forEach>
</div>
</c:if>