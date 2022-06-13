<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Config</title>
    <link rel="stylesheet" href="css/table.css">
</head>
<body>
<h1>Config</h1>
<h3>Config list size: ${configListSize}</h3>
<table>
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Base url</th>
        <th>User id</th>
        <th>Browser<br>automation</th>
        <th>Category</th>
        <th>Subcategory</th>
        <th>Product</th>
        <th>Next category<br>page</th>
        <th>Product data</th>
        <th></th>
        <th></th>
    </tr>
    <#list configList as value>
        <tr>
            <td>${value.id}</td>
            <td>${value.name}</td>
            <td>${value.baseUrl}</td>
            <td>${value.userId}</td>
            <td>${value.connectWay}</td>
            <td>${value.categorySelector!"NULL"}</td>
            <td>${value.subcategorySelector!"NULL"}</td>
            <td>${value.productSelector!"NULL"}</td>
            <td>${value.nextPageSelector!"NULL"}</td>
            <td>${value.productDataSelectors!"NULL"}</td>
            <td><a href="#">Update</a></td>
            <td><a href="/config/delete?id=${value.id}">Delete</a></td>
        </tr>
    </#list>
</table>
</body>
</html>