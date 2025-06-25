# Module306-Backend

````shell
docker run -p 5432:5432 --name postgres --env POSTGRES_USER=user --env POSTGRES_PASSWORD=password postgres
````

After creation of the database you have to create the database "trackify"
````sql
CREATE DATABASE trackify
````

````shell
docker run -p 9000:9000 -p 9001:9001 --name minio -v ~/minio/data:/data -e "MINIO_ROOT_USER=minioadmin" -e "MINIO_ROOT_PASSWORD=minioadmin" quay.io/minio/minio server /data --console-address ":9001"
````

````shell
docker run -p 8080:8081 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.2.5 start
````

