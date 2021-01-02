//Churches

CREATE CONSTRAINT ON (c:Church) ASSERT c.id IS UNIQUE;

LOAD CSV WITH HEADERS FROM 'file:///churches.csv' AS row 
WITH toFloat(row.lat) AS lat, toFloat(row.lng) AS lng, toBoolean(row.cathoric) AS cathoric 
RETURN lat, lng, cathoric
LIMIT 3


LOAD CSV WITH HEADERS FROM 'file:///churches.csv' AS row 
WITH toFloat(row.lat) AS lat, toFloat(row.lng) AS lng, toBoolean(row.cathoric) AS cathoric, row.url AS url, row.address AS address, row.name AS name, apoc.util.sha1([row.url]) AS id
MERGE (c:Church {id:id})
	SET c.url = url, c.lat = lat, c.lng = lng, c.cathoric = cathoric, c.address = address, c.name = name
RETURN count(c)

CREATE CONSTRAINT ON (s:Station) ASSERT s.id IS UNIQUE;

LOAD CSV WITH HEADERS FROM 'file:///stations.csv' AS row 
WITH row.id AS id, toFloat(row.lat) AS lat, toFloat(row.lng) AS lng, row.name AS name, toInteger(row.passengers2017) AS passengers
RETURN id, lat, lng, name, passengers
LIMIT 3

MATCH (c:Church {name:"Tokyo Sophia International Church"}) RETURN c;

MATCH (c:Church { id: '9bf2d6e1182dbd1490d3074d0888d4fe68677aa6' }) RETURN c;

MATCH (c:Church) DELETE c;

//Stations

LOAD CSV WITH HEADERS FROM 'file:///stations.csv' AS row 
WITH row.id AS id, toFloat(row.lat) AS lat, toFloat(row.lng) AS lng, row.name AS name, row.company AS company, row.line AS line, toInteger(row.passengers2017) AS passengers
MERGE (s:Station {id:id})
	SET s.lat = lat, s.lng = lng, s.name = name, s.company = company, s.line = line, s.passengers = passengers
RETURN count(s)


WITH point({ longitude: 12.78, latitude: 56.7, height: 100 }) AS p1, point({ latitude: 56.71, longitude: 12.79, height: 100 }) AS p2
RETURN distance(p1,p2) AS dist


MATCH (t:TrainStation)-[:TRAVEL_ROUTE]->(o:Office)
WITH point({ longitude: t.longitude, latitude: t.latitude }) AS trainPoint, point({ longitude: o.longitude, latitude: o.latitude }) AS officePoint
RETURN round(distance(trainPoint, officePoint)) AS travelDistance

MATCH (s:Station) DELETE s;


//Relationship

MATCH (c:Church),(s:Station)
WHERE s.name = 'お台場海浜公園' AND c.name = '日本キリスト教団秋田高陽教会'
RETURN c, s;

MATCH (c:Church),(s:Station)
WHERE s.name = 'お台場海浜公園' AND c.name = '日本キリスト教団秋田高陽教会'
CREATE (s)-[r:ROUTE {distance: 456307} ]->(c)
RETURN type(r)

MATCH (c:Church)-[r:ROUTE]-(s) 
WHERE r.distance > 400000 RETURN c, s;

// create relationships
LOAD CSV WITH HEADERS FROM 'file:///routes.csv' AS row
MATCH (s:Station {id: row.stationId})
MATCH (c:Church {id: row.churchId})
MERGE (s)-[r:ROUTE {distance: toFloat(row.distance)}]->(c)
RETURN count(r);

MATCH (s { name: 'お台場海浜公園' })-[r:ROUTE]->() DELETE r

MATCH (s:Station)-[r:ROUTE]->(c:Church) WHERE r.distance < 100 RETURN c,s LIMIT 100;
