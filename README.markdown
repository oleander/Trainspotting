# Trainspotting

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



### Start by installing the gem

    sudo gem install movie_searcher
Start `irb` and include the gem, `require 'movie_searcher'`

### Search for a movie by title

    $ MovieSearcher.find_by_title("The Dark Knight")
    => [#<ImdbParty::Movie:0x1012a5858 @imdb_id="tt0468569", @year="2008", @title="The Dark Knight" ... >, ...]

### Get a movie by its imdb id

    $ movie = MovieSearcher.find_movie_by_id("tt0468569")
    $ movie.title 
    => "The Dark Knight"
    $ movie.rating 
    => 8.9
    $ movie.certification 
    => "PG-13"

### Find the top 250 movies of all time

    $ MovieSearcher.top_250 
    => [#<ImdbParty::Movie:0x10178ef68 @imdb_id="tt0111161", @poster_url="http://ia.media-imdb.com/images/M/MV5BMTM2NjEyNzk2OF5BMl5BanBnXkFtZTcwNjcxNjUyMQ@@._V1_.jpg" ... >, ...]

### Get the currently popular tv shows

    $ MovieSearcher.popular_shows 
    => [#<ImdbParty::Movie:0x101ff2858 @imdb_id="tt1327801", @poster_url="http://ia.media-imdb.com/images/M/MV5BMTYxMjYxNjQxNl5BMl5BanBnXkFtZTcwNTU5Nzk4Mw@@._V1_.jpg", @year="2009", @title="Glee">, ... ]
    
### Search for a release name
    
    $ MovieSearcher.find_by_release_name("Heartbreaker 2010 LIMITED DVDRip XviD-SUBMERGE").imdb_id 
    => tt1465487

### Some configure alternatives
You can pass some options to the `find_by_release_name` method to specify how it should behave.

Here is an example.

    $ MovieSearcher.find_by_release_name("Heartbreaker 2010 LIMITED DVDRip XviD-SUBMERGE", :options => {:limit => 0.1, :details => true}) 

- ** :limit ** (Float) It defines how sensitive the parsing algorithm should be. Where 0.0 is super sensitive and 1.0 is don't care. The default value is 0.4 and workes in most cases. If you dont get any hits at all, try a large value.
- ** :details ** (Boolean) By default, the `find_by_release_name` only returns the most basic information about a movie, like *imdb_id*, *title* and *year*. If you set this to true, it will do another request to IMDB and get the casts, actors, rating and so on.

** The option param can't be passed to `find_by_title` **

** `find_movie_by_id` don't require that you pass the `:details` option to get all data **

## What is being returned?

The `find_by_title` method returns an `Array` of `ImdbParty::Movie` objects.

These are the accessors of `ImdbParty::Movie`

- **imdb_id** (String) The imdb id of the movie.
- **title** (String) The title of the movie.
- **directors** (Array) Related directors.
- **writers** (Array) Related writers.
- **tagline** (String) The movie tagline.
- **company** (String) Company who made the movie.
- **runtime** (String) The length of the movie, `120 min` for example.
- **rating** (Float) The movie rating, from 0 to 10.
- **poster_url** (String) Movie poster. **Beaware**, this image might expire. Use [tmdb_party](https://github.com/jduff/tmdb_party) if you want posters and images.
- **release_date** (Date) The release date of the movie.
- **certification** (String) The certification of the movie, *R* for example.
- **genres** (Array) The most relevant generes for the movie.
- **actors** (Array) Related actors.
- **trailers** (Hash) Related trailers. The key defines the quality of the trailer, like `H.264 480x360` and the value specify the url.

The `actors`, `writers` and `directors` accessors returns an `ImdbParty::Person` object that has the following accessors.

- **imdb_id** (String) The imdb id of the person.
- **role** (String) What role did the actor have in the movie. This is only set when working with an actor.
- **name** (String) The actual name of the actor.

## This sounds supr, how do I help?

- Start by copying the project or make your own branch.
- Navigate to the root path of the project and run `bundle`.
- Start by running all tests using rspec, `rspec spec/movie_searcher_spec.rb`.
- Implement your own code, write some tests, commit and do a pull request.

## Requirements

The gem is tested in OS X 10.6.6 using Ruby 1.8.7 and 1.9.2
