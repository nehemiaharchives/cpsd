# cpsd
cpsd is a database to develop Church planting strategy. 
It is originally created for mission organizations around the world to plant Churches in Japan - the couontry with world's [second largest](https://joshuaproject.net/unreached/1?s=Population&o=desc) unreached people group.

## Background
I converted from atheist physics fanboy to evangelical Christian when I was a student of [ICU](https://www.icu.ac.jp/en/) (International Christian University). 
Changed my major from physics to computer sciencce as I felt CS is pragmatically more usuful for world evangelism. 
At that time I also started an internship at [Christian Today Japan](https://www.christiantoday.co.jp/english.htm) as Full Stack Developer and reporter. 
I moved from Japan to US to earn Master of Arts in Information Technology from [OIT](https://oit.olivetuniversity.edu/) (Olivet Institute of Technology) while majoring Missiology and Software Engineering together. 
Primary interest was to leverage tech for world mission strategy all the way from micro strategy like locating Church absent cities to macro strategy like global interindustrial logistics.
After serving as a minister of [G&IT](https://gnit.org/) (Gospel & Information Techcnology) in United States, I moved back to Japan to work as editor in chief of Christian Today Japan.
Intersection of Christianity, Journalism and Information Technology has been my field. I was always interested in the advancement of [Data Journalism](https://datajournalism.com/).
Now that I spent 1 year in the world of online Christian Journalim and [FaithTech](https://faithtech.com/), came to know almost nobody practice Data Journalism in our field. 
That was the moment I felt God called me to be the first one to do [that](https://raw.githubusercontent.com/nehemiaharchives/cpsd/master/Christian_Data_Journalism.svg). First topic I chose was meant for something useful for World Mission Strategy.

## Current Project
Now at the starting point, cpsd is limited to for the usage in mission strategy in Japan in English. 
First dataset is location of Churches and train stations with passenger count and distance.

## Technical Architecture
cpsd is a combination of [Kotlin](https://kotlinlang.org/) functions meant to be executed in [integrated development environment](https://en.wikipedia.org/wiki/Integrated_development_environment) (IDE) with running local [Neo4j](https://neo4j.com/) instance at localhost.

## Setup and Run
1. Download Neo4j Desktop, create a database with your choce of password, and [install APOC](https://neo4j.com/labs/apoc/4.2/installation/).
2. Prepare ```churches.csv``` with header ```url,catholic,lat,lng,address,name```.
3. Prepare ```stations.csv``` with header ```id,lat,lng,name,company,line,passengers2017```.
4. In Neo4j Desktop, Your Project-> Click "⋯" Dropdown->Open folder->Import copy and paste ```churches.csv``` and ```stations.csv``` in ```Import``` directory.
5. Open Neo4j Browser and run Query 1-4 in [Cypher file](src/main/resources/church-station.cql) to import csv into Neo4j.
6. Import this repo into your IDE and create ```db.properties``` with 2 lines: ```username = neo4j``` and ```password = NEO4J_PASSWORD_YOU_CREATED_IN_STEP_1``` in [resources](src/main/resources)
7. Run ```main()``` in [Route.kt](src/main/kotlin/org/gnit/cpsd/Route.kt) this will generate ```routes.csv``` in [resources](src/main/resources)
8. Copy and paste ```routes.csv``` in ```Import``` directory and run Query 5 in Neo4j Browser. 
9. In your IDE, run ```main()``` in [StationWithChurch.kt](src/main/kotlin/org/gnit/cpsd/StationWithChurch.kt) to generate GeoJson files of stations with Churches.
10. In your IDE, run ```main()``` in [StationWithoutChurch.kt](src/main/kotlin/org/gnit/cpsd/StationWithoutChurch.kt) to generate GeoJson files of stations without Churches.
11. In your IDE, run ```main()``` in [Churches.kt](src/main/kotlin/org/gnit/cpsd/Churches.kt) to generate GeoJson files of all Churches.

## Resulted files
GeoJson files are separated by GeoJson ```FeatureCollection```, Churches and Stations are ```Point```, Stations are also ```Polygon```, Route from Station to Church is ```LineString```. 
Files are also separated into 6 distance segments from 0 to 500, 1000, 1500, 2000, 2500 or 3000 meters. 
Following are the expected files to be generated:

```
500-church.json
500-route.json
500-station-point-with-church.json
500-station-point-without-church.json
500-station-polygon-with-church.json
500-station-polygon-without-church.json

1000-church.json
1000-route.json
1000-station-point-with-church.json
1000-station-point-without-church.json
1000-station-polygon-with-church.json
1000-station-polygon-without-church.json

1500-church.json
1500-route.json
1500-station-point-with-church.json
1500-station-point-without-church.json
1500-station-polygon-with-church.json
1500-station-polygon-without-church.json

2000-church.json
2000-route.json
2000-station-point-with-church.json
2000-station-point-without-church.json
2000-station-polygon-with-church.json
2000-station-polygon-without-church.json

2500-church.json
2500-route.json
2500-station-point-with-church.json
2500-station-point-without-church.json
2500-station-polygon-with-church.json
2500-station-polygon-without-church.json

3000-church.json
3000-route.json
3000-station-point-with-church.json
3000-station-point-without-church.json
3000-station-polygon-with-church.json
3000-station-polygon-without-church.json
```
Data for stations and Churches in Japan are located at [data/japan](data/japan) directory in this repo.

## Visualization
You can import those GeoJson files into [GIS](https://en.wikipedia.org/wiki/Geographic_information_system) software or services like [MapBox Studio](https://www.mapbox.com/mapbox-studio). For example, [Church & Station](https://github.com/nehemiaharchives/church-station) project at [Christian Today Japan](https://www.linkedin.com/company/christian-today-japan/).

## Future
Hoping to apply for other countries, accomodate other type of data and languages.

## Code of Conduct
Matthew 28:19-20 "Therefore go and make disciples of all nations, baptizing them in the name of the Father and of the Son and of the Holy Spirit, and teaching them to obey everything I have commanded you. " - Jesus Christ
