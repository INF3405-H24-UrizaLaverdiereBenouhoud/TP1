## Compilation

JDK : Eclipse Temurin Java 8
Devrait fonctionner avec n'importe quelle version de Java 8 ou supérieure.

### Apache Maven - Génération des exécutables

On peut générer les exécutables .jar avec Maven.

```bash
mvn -B clean package --file pom.xml
```

Les artéfacts se situe à deux endroits, pour serveur et client

1. `Server/target/INF3405-Server.jar`
2. `Client/target/INF3405-Client.jar`

## Utilisation

### Serveur

Pour lancer le serveur, il suffit de lancer le .jar avec la commande suivante:

(Pensez à bien préciser le chemin vers un fichier comptes et messages JSON)

```bash
java -jar INF3405-Server.jar <chemin vers fichier comptes> <chemin vers fichier messages>
```

### Client

Pour lancer le client, il suffit de lancer le .jar avec la commande suivante:

```bash
java -jar INF3405-Client.jar
```