# Key2Gym

![Key2Gym logo](https://raw.github.com/equalsdanny/key2gym-img/master/logo-wide.png)

## Mission
The Key2Gym's mission is to create an open, excellent user experience providing, scalable, modern, platform-independent application for running small to medium-sized gyms.

Key2Gym is an open-source software licensed under Apache License Version 2.0.

## Features

* Register new clients with detailed profile information
* Control access to the gym with client subscriptions
* Check registered and casual clients in with support of personal locker keys
* Record purchases and payments
* Look up information about clients (profile information, money balance, history of attendances, etc)
* Check clients out
* See clients that are in the gym right now
* Track the items available for sale
* Track attendances and purchases by date in the application
* Generate financial and business reports in XML, HTML, etc to take away

![Screenshot](https://raw.github.com/equalsdanny/key2gym-img/master/screenshots/main-1.png)

## Requires

* Java 7 SE
* PostgreSQL 9.2+
* OpenEJB 4.5.0+

## Community

[Google Group](https://groups.google.com/forum/?hl=ru&fromgroups#!forum/key2gym)

## Building

You need:
* JDK 1.7+
* Ant 1.7.0+
* Scala 2.9.1+

1. Fork or download the repository.
2. `cd` to the repository's root folder.
3. Create `private/environment.properties` following the example [here](https://gist.github.com/4153536).
4. Run `ant build`.
5. See the documentation on the information about the required runtime environment to run the application. You have to deploy the whole environment to check out all the features.

## Documentation

The deployment documentation is avaiable in the distribution under the `docs` folder.
It's also soon will be available online at the project's website.

## Contributing

This project is currently maintained by 1 person. So all contributions are very welcome. If you would like to contribute, you might want to start with talking to other developers at the project's [Google Group](https://groups.google.com/forum/?hl=ru&fromgroups#!forum/key2gym).

Then feel free to:

1. Pick any issue from the `Issues` page
2. Fork the repository
3. Fix the issue
4. Issue a Pull Request
