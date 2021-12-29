<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Config</title>
</head>
<body>
<h1>Config</h1>
<h2>Config list size: ${configListSize}</h2>
<table>
    <tr>
        <td>Id</td>
        <td>Name</td>
        <td>Base url</td>
        <td>User id</td>
    </tr>
    <#list configList as config>
        <tr>
            <td>${config.id}</td>
            <td>${config.name}</td>
            <td>${config.base_url}</td>
            <td>${config.user_id}</td>
        </tr>
    </#list>
</table>
</body>
</html>