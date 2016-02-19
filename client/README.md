Cette application permet de synchroniser les répertoires. Elle répond à certains de mes besoins de sauvegarde ou synchronisation.

Elle m'a permis d'essayer Java FX en tant que client lourd.

Grâce à la librairie [https://github.com/cathive/fx-guice](https://github.com/cathive/fx-guice), j'ai pu facilement mettre en place l'injection de dépendance entre Java FX et google guice.

fx-guice n'étant pas encore disponible sous maven, il faut d'abord récupérer cette librairie et la recompiler en local :

	<dependency>
        <groupId>com.cathive.fx</groupId>
        <artifactId>fx-guice</artifactId>
        <version>1.0.1-SNAPSHOT</version>
   	</dependency>

SyncFiles a été compilé avec le jdk 1.7.à_07. JavaFX y est inclus.

Pour compiler fx-guice, il suffit d'activer le profile javafx-bundled-with-jdk et de modifier la dépendance suivante en mettant la version 2.2 :

	<dependency>
    	<groupId>com.oracle</groupId>
        <artifactId>javafx-runtime</artifactId>
        <version>2.2</version>
        <scope>system</scope>
        <systemPath>${java.home}/lib/jfxrt.jar</systemPath>
	</dependency>
 
Pour la tester sous un windows 64 bits, vous pouvez directement [dézipper l'archive](http://www.afondlesport.com/~fensminger/SyncFiles/SyncFiles.zip) et lancer l'exécutable. Le JRE 1.7.0_07 y est inclus.
