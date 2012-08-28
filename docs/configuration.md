# Configuration

[Back to Index.](./index.html)

This page explains how to configure Key2Gym. 

## Connections

Connections describe how the application should connect to the storage that is to be used. Connections
are stored as files in `etc/connections/`. A connection file's name has the following format: <codeName>.properties.
A connection file itself is a [Java-style properties file](http://en.wikipedia.org/wiki/.properties).

The following is the table of supported properties:

<table>

<thead>
<tr>
<td>Key</td>
<td>Description</td>
<td>Required</td>
</tr>
</thead>

<tbody>
<tr>
<td>type</td>
<td>Defines the type of this connection. Built-in types of connections: `MySQLEclipseLink`. Other connections can be supported by adding new classes at runtime. See `ConnectionsManager` class in the developer's documentation.</td>
<td>Yes</td>
</tr>

<tr>
<td>title</td>
<td>The string that is going to displayed in the application as the description of this connection. Any string. Note that you have escape the Unicode characters. See the [properties format description](http://en.wikipedia.org/wiki/.properties) for details. </td>
<td>Yes</td>
</tr>
</tbody>

</table>

All other keys are type-specific.

The following is the table of supported properties for connections of type `MySQLEclipseLink`:

<table>
<thead>
<tr>
<td>Key</td>
<td>Description</td>
<td>Required</td>
</tr>
</thead>
<tbody>
<tr>
<td>host</td>
<td>The server's host name. Host name usually is a FQDN or IP address of the server. The whole range of valid values is environment-specific.</td>
<td>Yes</td>
</tr>

<tr>
<td>port</td>
<td>The server's port number. Any number in the range of 1-65535. Default port for MySQL servers is 3306.</td>
<td>Yes</td>
</tr>

<tr>
<td>user</td>
<td>The MySQL user. The user must have permissions to create, alter and remove tables, as well as select, update and delete rows in the target database.</td>
<td>Yes</td>
</tr>

<tr>
<td>password</td>
<td>The MySQL user's password. The password has to be plain text.</td>
<td>Yes</td>
</tr>

<tr>
<td>database</td>
<td>The MySQL database to use. The database must already exist. See DDL properties to see what options are if the database does not have the propper schema.</td>
<td>Yes</td>
</tr>
</tbody>
</table>

[Back to Index.](./index.html)



