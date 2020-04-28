# Car registry

### Launch service
```docker-compose up```

## How to use api

### Test
```curl -v http://localhost/api/v1/```

### CREATE:
```
curl -d '{
    "manufacturer": "Ford",
    "model": "Sierra",
    "year": 1995,
    "surname": "Pavardenis",
    "name": "Vardenis",
    "number": "+3706123456",
    "email": "vardeniux@mail.com"
}' -H 'Content-Type: application/json' http://localhost/api/v1/ownedcars/
```
### READ:
(all)
```curl -v http://localhost/api/v1/ownedcars/```

(id)
```curl -v http://localhost/api/v1/ownedcars/1```

### UPDATE:
```curl -d '{
    "manufacturer": "Ford",
    "model": "Sierra",
    "year": 1995,
    "surname": "Pavardenis",
    "name": "Vardenis",
    "number": "+3706123456",
    "email": "vardeniux@mail.com"
}' -H 'Content-Type: application/json' -X PUT http://localhost/api/v1/ownedcars/2```
```
### DELETE:
```curl -X DELETE http://localhost/api/v1/ownedcars/1```
