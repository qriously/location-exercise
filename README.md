# Qriously Location Exercise

## Overview

Every day at Qriously we conduct large numbers of surveys all around the world. The primary way in which we find participants is through mobile advertising. 
This involves handling a lot of [RTB](https://en.wikipedia.org/wiki/Real-time_bidding) traffic (tens of thousands of requests per second) and one of the challenges we face is to ensure we can process this data efficiently.

From the traffic we have to decide whether to show a survey. One method we use to filter requests is using location information.
For each request we resolve the location (a [latitude/longitude coordinate](https://en.wikipedia.org/wiki/Geographic_coordinate_system#Latitude_and_longitude) into a country or specific region such as a US county.
We can then filter according to whether we have surveys in particular countries or regions.

We would like you to implement a way of resolving locations (coordinates) to US counties.
We have prepared a simple maven project and also a [shapefile](https://en.wikipedia.org/wiki/Shapefile) with some reference data.
You can trade-off between speed (time it took to resolve the location) and accuracy, however where we do resolve a location it has to be correct (the right county).

## Instructions

1. Create a copy of the public `location-exercise` repository. *Please do not fork the source repo directly as you cannot restrict access to a forked public repository*). 
 
    - [Create a new **private** repository](https://help.github.com/en/articles/creating-a-new-repository) called `location-exercise`
    - [Duplicate the source repository](https://help.github.com/en/articles/duplicating-a-repository) (remember to replace your `{exampleuser}` in the command below)
    ```
    $ git clone --bare git@github.com:qriously/location-exercise.git
    $ cd location-exercise.git
    $ git push --mirror git@github.com:{exampleuser}/location-exercise.git
    $ cd .. 
    $ rm -rf location-exercise.git
    ```
    - Clone the duplicated repo
    ```
    $ git clone git@github.com:{exampleuser}/location-exercise.git
    ```
    
1. Implement your solution.
    - You solution should extend the the `CountryResolver` class.

1. Include any instructions (or other useful comments) in a clearly marked text file.

1. Commit your changes locally then push to the remote repository.

1. Add the users **pc256** and **cw124** as [collaborators to the repository](https://help.github.com/en/articles/inviting-collaborators-to-a-personal-repository).

1. Drop us an email letting us know you've finished.


## Other Useful Information 

- You solution should extend the the `CountryResolver` class.
- We have included a `BasicCountyResolver` as an example.
- The project should compile using maven (i.e. `mvn clean install`)
- Feel free to use external libraries so long as your solution cleanly runs using maven.
- Document your progress and failures using the git commits.
- Use Java 8+
- Structured code in a clear manner.

### Happy Coding!
