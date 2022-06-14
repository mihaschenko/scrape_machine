<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Config</title>
        <link rel="stylesheet" href="../../../css/table.css">
    </head>
    <body>
        <h1>Config</h1>
        <h3>Config list size: ${configListSize}</h3>
        <ul>
            <li>
                <a href="config/create">Create new config</a>
            </li>
            <li>
                <a href="/">Back</a>
            </li>
        </ul>
        <table>
            <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Base url</th>
                <th>User id</th>
                <th>Connect<br>way</th>
                <th>Category</th>
                <th>Subcategory</th>
                <th>Product</th>
                <th>Next category<br>page</th>
                <th>Product data</th>
                <th></th>
                <th></th>
            </tr>
            <c:forEach items="${configList}" var="item">
                <tr>
                    <td><c:out value="${item.id}"/></td>
                    <td><c:out value="${item.name}"/></td>
                    <td><c:out value="${item.baseUrl}"/></td>
                    <td><c:out value="${item.userId}"/></td>
                    <td><c:out value="${item.connectWay}"/></td>
                    <td><c:out value="${item.categorySelector}"/></td>
                    <td><c:out value="${item.subcategorySelector}"/></td>
                    <td><c:out value="${item.productSelector}"/></td>
                    <td><c:out value="${item.nextPageSelector}"/></td>
                    <td><c:out value="${item.productDataSelectors}"/></td>
                    <td><a href="config/update?id=<c:out value="${item.id}"/>">Update</a></td>
                    <td><a href="config/delete?id=<c:out value="${item.id}"/>">Delete</a></td>
                </tr>
            </c:forEach>
        </table>
    </body>
</html>