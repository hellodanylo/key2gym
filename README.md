# Census

## About
Census is a free sofware for running and maintaing gyms and other membership-based clubs or enterprises.

It helps you to:
* Keep track of clients and their memberships
* Record attendances
* Record purchases, payments
* Keep track of clients' debts and their collection.

## Building

You need:
* JDK (with `java` and `javac` on the class path)
* Ant (with `ant` on the class path)
* Perl (with `perl` on the class path)

1. Fork or download the repository.
2. `cd` to the repository's root folder and run `ant -buildfile nb-build.xml release`.
3. You can now run Census as `cd dist && java -jar census.jar`.

Note that you have to deploy the whole environment to check out all the features.

## Deploying

The deployment information is avaiable in the distribution: `docs/deployment.html`.

## Contributing

This project is currently maintained by 1 person. So all contributions are very welcome.
Feel free to:

1. Pick any issue from the `Issues` page
2. Fork the repository
3. Fix the issue
4. Issue a Pull Request
