# Trainspotting

## READ FIRST

Please consider reading this README-file at our github repo, where it is
formatted in a more pleasent way.

[Click here](https://github.com/oleander/Trainspotting) 

## Authors

Arash Rouhani, cid rarash

Linus Oleander, cid oleander

## Background

We've implemented trainspotting generally. That means we have not hardcoded anything.
Consequently, the program must read the train-file, and it must be passed as an argument.

Our solution can handle almost any map, as long the sensors are sensibly placed and the
max velocities are reasonable.

## Sensors

We first specify how our general solution expects the sensor placement, then we
continue to discuss the sensor placement for the particular map (that was the actual assignment)

### Placements of Sensors

Placing of the sensors must be sensible, formally:

- Before each crossing, in all four directions.
- Before each switch, in all three directions.
- Before each turn-around point.

One single sensor can safely act as both a Crossing-sensor and Switch-sensor.
Sensors not abiding the bullets above will be ignored in that diretion that
they are not abiding a rule. (Which is of course very common, used as a margin)

### Sensors in the particular map

The placement has been done to abide the rules above in one direction, which leads to using many sensors. 
One could conversely let each Sensor act as a bi-directional Sensor, that would lead to less sensors.

We choose many sensors which effectivly means good train flow, rather than few
sensors which would end in bad train flow, but high maximum velocity.

## How the solution uses semaphores

We've realized by studying the general case that each sensor could correspond
to three actions:

- If near the edge, turn around
- If near a crossing, grab a crossing-semaphore and then drive
- If about to switch segment, *safely* switch current segment-semaphore to the new one the train is about to enter.

Hence this description clearly indicates how we use semaphores. Put short: For each crossing and for each segment.

By segment we mean any region which is bound by switches or endpoints. (There are 8 in the original map)

Totally the solution uses up to 9 semaphores for the given railmap. (upto because they are created dynamically)

## Maximum train speed

We have already discussed that the submitted railway has many sensors which ultimately lead to lower maximum train speed
but better trainflow.

We do not try to temporairly slow down or halfen the speed at any time to increase the maximum train speed, as that would
make the already complicated general solution more complicated.

We confidently say that the maximum speed is 14, however, it could easily be increased by using the more sparing
sensor placenment, an example of that is highlighed in the file **origfast**.

## Command line usage

Out implementation must read the file as input, therfor it must be passed to the program.

Here is an example of starting the simulation: 

		$ 2 "tsim bana -s 3" "java Lab1 bana 15 7"
		=> Starting java Lab1 bana 15 7
		=> Starting tsim bana -s 3
		=> Parse complete!
		=> ...

## How our code works

We have a class that contains all the data about the railmap, **Railmap**.
It is also responsible for parsing the railway-map.

The train class contains information about the train and useful methods.

The sensor class contains almost no information, it's primarly the place for
sensor-related code.

The other classes are of a lower level and are not mentioned.

## Included maps

Since we have a general solution, we couldn't resist to try out our solution
on more "fun" maps


## Screenshots


### Bloopers

![The train](https://github.com/oleander/Trainspotting/blob/master/Screenshot-tsim.png)
The four trains in the top left have "stucked" eachother, they have
taken semaphores in a cyclic way.


