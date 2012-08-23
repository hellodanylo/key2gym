# Deployment

This page describes how to deploy a distribution of this software package.

## Java

This program is written in Java. Therefore, it requires Java Runtime Environment
to be available at the target computer. Go to [Java website](http://java.com) for instructions on
how to install Java Runtime Environment on your target OS.

## Database

Key2Gym uses external Relational Database Management System (RDBMS) to keep all the information.
As for now the only supported RDBMS is MySQL. The program has been intensively
tested with MySQL 5.1, however the later version should work as well. 
See the [MySQL documentation](http://dev.mysql.com/doc/), if you do not know how to do any of the steps below: 

1. Go to [MySQL website](http://mysql.com/) to obtain a distribution of MySQL server. If you are running
Linux/Unix/BSD, we suggest you to install MySQL server from the official repository.
2. After the installation is complete, you need to modify the `my.cnf` configuration file. 
Open it and place the following lines in the `mysqld` section:
	skip-character-set-client-handshake
	character-set-server=utf8
	collation_server=utf8_unicode_ci
3. Restart the server
4. Execute the `mysql/key2gym.sql` SQL script using the `mysql` client. 
Note that the script only contains tables structure, which means you have to `create` and `use` a database on your own before running the script.
	mysql> create database `key2gym`;
	mysql> use `key2gym`;
	mysql> source dist/mysql/schema_r4.sql;

TODO: Describe how to set up the database.

## Key2Gym

1. Unzip the distribution archive to the target folder.
2. You can now run the program with `java -jar key2gym.jar`.

You might want to read the [Configuration](./configuration.hmtl) to perform basic set up of Key2Gym.
