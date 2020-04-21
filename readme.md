# Car registry

### Launch service
```docker-compose up```

## How to use api

### Test
```curl -v http://localhost/api/v1/```

### CREATE:
```
curl -d '{"manufacturer": "Subaru","model": "Impreza","year" : 2005}' -H 'Content-Type: application/json' http://localhost/api/v1/cars/
curl -d '{"manufacturer": "Toyota","model": "Corolla","year" : 2010}' -H 'Content-Type: application/json' http://localhost/api/v1/cars/
curl -d '{"manufacturer": "Tesla","model": "Model S","year" : 2016}' -H 'Content-Type: application/json' http://localhost/api/v1/cars/
```

### READ:
(all)
```curl -v http://localhost/api/v1/cars/```

(id)
```curl -v http://localhost/api/v1/cars/1```

### UPDATE:
```curl -d '{"manufacturer": "Toyota","model": "Corolla","year" : 2011}' -H 'Content-Type: application/json' -X PUT http://localhost/api/v1/cars/2```

### DELETE:
```curl -X DELETE http://localhost/api/v1/cars/1```
