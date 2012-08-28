# Deployment

[Back to Index.](./index.html)

This page explains how to deploy a distribution of Key2Gym.

## Java

This program is written in Java. Therefore, it requires Java Runtime Environment
to be available at the target computer. Go to [Java website](http://java.com) for instructions on
how to install Java Runtime Environment on your target OS.

## Database

Key2Gym uses external Relational Database Management System (RDBMS) to keep all the information.
As for now the only supported RDBMS is MySQL. The program has been intensively
tested with MySQL 5.1, however the later version should work as well. 
See the [MySQL documentation](http://dev.mysql.com/doc/), if you do not know how to do any of the steps below:

Go to [MySQL website](http://mysql.com/) to obtain a distribution of MySQL server. If you are running
Linux/Unix/BSD, we suggest you to install the MySQL server from the official repository.

Restart the server

Execute the `mysql/key2gym.sql` SQL script using the `mysql` client. 
Note that the script only contains tables structure, which means you have to `create` and `use` a database on your own before running the script.
	
	mysql> create database `key2gym;
	mysql> use `key2gym`;
	mysql> source dist/mysql/key2gym.sql;

TODO: Describe how to set up the database.

## Key2Gym

1. Unzip the distribution archive to the target folder.

2. Configure the application as explained on the [Configuration](./configuration.html) page.

2. You can now run the program with `java -jar key2gym.jar`.



[Back to Index.](./index.html)
