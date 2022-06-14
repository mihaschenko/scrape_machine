<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Update</title>
        <style>
            form > div {
                margin: 5px;
            }
            form label {
                font-weight: bold;
            }
            form input:not([type="submit"]), form select {
                margin: 3px 0 3px 20px;
                padding: 2px;
                border: 1px solid grey;
            }
            [type="submit"] {
                width: 80px;
                height: 25px;
                border: 1px solid grey;
                cursor: pointer;
            }
            [type="submit"]:hover {
                background-color: white;
            }
            input {
                border-radius: 4px;
            }
        </style>
    </head>
    <body>
        <h1>Create/Update config</h1>
        <ul>
            <li>
                <a href="/config">Back</a>
            </li>
        </ul>
        <form method="POST" action="${pageContext.request.contextPath}/config/update">
            <input type="text" name="id" value="<c:out value="${config.id}" />" hidden>
            <div>
                <label for="name">Name</label><br>
                <input type="text" id="name" name="name" value="<c:out value="${config.name}" />">
            </div>
            <div>
                <label for="baseUrl">Base url</label><br>
                <input type="text" id="baseUrl" name="baseUrl" value="<c:out value="${config.baseUrl}" />">
            </div>
            <div>
                <label for="connectWay">Connect way</label><br>
                <select name="connectWay" id="connectWay">
                    <option value="JSOUP" <c:if test="${config.connectWay.name().equals(\"JSOUP\")}">selected</c:if>>
                        Jsoup
                    </option>
                    <option value="SELENIUM" <c:if test="${config.connectWay.name().equals(\"SELENIUM\")}">selected</c:if>>
                        Selenium
                    </option>
                </select>
            </div>
            <div>
                <label for="categorySelector">Category selector</label><br>
                <input type="text" id="categorySelector" name="categorySelector" value="<c:out value="${config.categorySelector}" />">
            </div>
            <div>
                <label for="subcategorySelector">Subcategory selector</label><br>
                <input type="text" id="subcategorySelector" name="subcategorySelector" value="<c:out value="${config.subcategorySelector}" />">
            </div>
            <div>
                <label for="productSelector">Product selector</label><br>
                <input type="text" id="productSelector" name="productSelector" value="<c:out value="${config.productSelector}" />">
            </div>
            <div>
                <label for="nextPageSelector">Next page selector</label><br>
                <input type="text" id="nextPageSelector" name="nextPageSelector" value="<c:out value="${config.nextPageSelector}" />">
            </div>
            <input type="submit" value="Submit">
        </form>
    </body>
</html>